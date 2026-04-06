package com.adopt.apigw.service.customerServices;

import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.DocumentConstants;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.repository.CustomRepository;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.devCode.ToolService;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.mapper.postpaid.CustomerLedgerMapper;
import com.adopt.apigw.mapper.postpaid.StaffUserMapper;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.lead.LeadMaster;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Alert.smsScheduler.service.SmsSchedulerService;
import com.adopt.apigw.modules.Area.service.AreaService;
import com.adopt.apigw.modules.Branch.repository.BranchRepository;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.BusinessUnit.repository.BusinessUnitRepository;
import com.adopt.apigw.modules.CafFollowUp.repository.CafFollowUpAuditRepository;
import com.adopt.apigw.modules.CommonList.repository.CommonListRepository;
import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;
import com.adopt.apigw.modules.Communication.Helper.CommunicationHelper;
import com.adopt.apigw.modules.CustomerDBR.repository.CustomerDBRRepository;
import com.adopt.apigw.modules.CustomerDBR.service.CustomerDBRService;
import com.adopt.apigw.modules.CustomerMacMgmt.Service.CustMacMgmtService;
import com.adopt.apigw.modules.Customers.CustomersController;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.adopt.apigw.modules.InventoryManagement.item.ItemRepository;
import com.adopt.apigw.modules.LocationMaster.repository.CustomerLocationRepository;
import com.adopt.apigw.modules.LocationMaster.repository.LocationMasterRepository;
import com.adopt.apigw.modules.Matrix.domain.Matrix;
import com.adopt.apigw.modules.Matrix.domain.TatMatrixWorkFlowDetails;
import com.adopt.apigw.modules.Matrix.repository.MatrixRepository;
import com.adopt.apigw.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.NetworkDevices.mapper.NetworkDeviceMapper;
import com.adopt.apigw.modules.NetworkDevices.service.NetworkDeviceService;
import com.adopt.apigw.modules.Notification.model.NotificationDTO;
import com.adopt.apigw.modules.Notification.service.NotificationService;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerLedgerDetailsService;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerLedgerService;
import com.adopt.apigw.modules.Pincode.service.PincodeService;
import com.adopt.apigw.modules.ServiceArea.SubscriberMapper;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.Teams.domain.Hierarchy;
import com.adopt.apigw.modules.Teams.repository.HierarchyRepository;
import com.adopt.apigw.modules.Teams.repository.TeamHierarchyMappingRepo;
import com.adopt.apigw.modules.Teams.repository.TeamsRepository;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.Teams.service.TeamsService;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.TimeBasePolicy.repository.CustomerTimeBasePolicyDetailsRepository;
import com.adopt.apigw.modules.custAccountProfile.CustAccountProfile;
import com.adopt.apigw.modules.custAccountProfile.CustAccountProfileRepository;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.modules.customerDocDetails.repository.CustomerDocDetailsRepository;
import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
import com.adopt.apigw.modules.linkacceptance.mapper.LinkAcceptanceMapper;
import com.adopt.apigw.modules.linkacceptance.model.LinkAcceptanceDTO;
import com.adopt.apigw.modules.partnerdocDetails.repository.PartnerDocdetailsRepository;
import com.adopt.apigw.modules.planUpdate.repository.CustomerPackageRepository;
import com.adopt.apigw.modules.planUpdate.service.CustomerPackageService;
import com.adopt.apigw.modules.qosPolicy.model.QOSPolicyDTO;
import com.adopt.apigw.modules.qosPolicy.service.QOSPolicyService;
import com.adopt.apigw.modules.role.service.RoleService;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.modules.subscriber.model.CustomerPlansModel;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.modules.tickets.repository.CaseRepository;
import com.adopt.apigw.modules.workflow.service.WorkflowAssignStaffMappingService;
import com.adopt.apigw.nepaliCalendarUtils.service.DateConverterService;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.pojo.customer.NextBillDatinfo;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.CustServiceMappingMessage;
import com.adopt.apigw.rabbitMq.message.CustomerMessage;
import com.adopt.apigw.rabbitMq.message.LeadMasterPojoMessage;
import com.adopt.apigw.repository.LeadMasterRepository;
import com.adopt.apigw.repository.common.*;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.*;
import com.adopt.apigw.service.postpaid.*;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerCreation extends AbstractService<Customers, CustomersPojo, Integer> {

    public static final String MODULE = " [CustomersService] ";
    private static final Logger logger = LoggerFactory.getLogger(CustomersService.class);

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CustomersService customersService;

    @Autowired
    TeamsService teamsService;
    @Autowired
    StaffUserRepository staffUserRepository;
    @Autowired
    MessageSender messageSender;
    @Autowired
    NotificationTemplateRepository templateRepository;
    @Autowired
    ClientServiceRepository clientServiceRepository;
    @Autowired
    CustomerApproveRepo customerApproveRepo;
    @Autowired
    TeamsRepository teamsRepository;
    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;
    @Autowired
    CustomerCafAssignmentService customerCafAssignmentService;
    @Autowired
    CustomerPackageRepository customerPackageRepository;
    @Autowired
    WorkflowAuditService workflowAuditService;
    @Autowired
    WorkflowAuditRepository workflowAuditRepository;
    @Autowired
    WorkFlowQueryUtils workFlowQueryUtils;
    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;
    @Autowired
    PartnerDocdetailsRepository partnerDocdetailsRepository;
    @Autowired
    DbrService dbrService;
    @Autowired
    WorkflowAssignStaffMappingService workflowAssignStaffMappingService;
    private String bulkcsv;
    //    @PersistenceContext
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private CustomerDBRRepository customerDBRRepository;
    @Autowired
    private MessagesPropertyConfig messagesProperty;
    @Autowired
    private CountryService countryService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private PincodeService pincodeService;
    @Autowired
    private StateService stateService;
    @Autowired
    private CityService cityService;
    @Autowired
    private PartnerLedgerService partnerLedgerService;
    @Autowired
    private PartnerCommissionService partnerCommService;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private CustomerAddressService custAddressService;
    @Autowired
    private CustomerLedgerService custLegerService;
    @Autowired
    private BillRunService billRunService;
    @Autowired
    private PostpaidPlanService planService;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private PostpaidPlanMapper postpaidPlanMapper;
    @Autowired
    private CreditDocService creditDocService;
    @Autowired
    private PartnerLedgerDetailsService partnerLedgerDetailsService;
    @Autowired
    private ServiceAreaService serviceAreaService;
    @Autowired
    private NetworkDeviceService networkDeviceService;
    //    @Autowired
//    private CaseService caseService;
//    @Autowired
//    private CaseUpdateService caseUpdateService;
    @Autowired
    private TaxService taxService;
    @Autowired
    private ChargeService chargeService;
    @Autowired
    private SubscriberMapper subscriberMapper;
    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    private ClientServiceSrv clientServiceSrv;
    @Autowired
    private CustPlanMappingService custPlanMappingService;
    @Autowired
    private CustomerLedgerDtlsService customerLedgerDtlsService;
    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private CaseRepository caseRepository;
    @Autowired
    private CustomerPaymentRepository paymentRepository;
    @Autowired
    private CustomerDocDetailsService customerDocDetailsService;
    @Autowired
    private PostpaidPlanService postpaidPlanService;
    @Autowired
    private TaxTypeSlabService taxTypeSlabService;
    @Autowired
    private TaxTypeTierService typeTierService;
    @Autowired
    private NetworkDeviceMapper networkDeviceMapper;
    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;
    @Autowired
    private CustomerLedgerMapper customerLedgerMapper;
    @Autowired
    private CustQuotaRepository custQuotaRepository;
    @Autowired
    private CustQuotaService custQuotaService;

    //    @Autowired
//    private HierarchyRepository hierarchyRepository;
    @Autowired
    private CustMacMapppingService custMacMapppingService;
    @Autowired
    private CustPlanMappingService planMappingService;
    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    private PlanGroupMappingService planGroupMappingService;
    @Autowired
    private PlanGroupMappingRepository planGroupMappingRepository;
    @Autowired
    private PlanGroupService planGroupService;
    @Autowired
    private CustomerChargeHistoryRepo customerChargeHistoryRepo;
    @Autowired
    private CustomerPackageService customerPackageService;
    @Autowired
    private DateConverterService dateConverterService;
    @Autowired
    private CustomerTimeBasePolicyDetailsRepository customerTimeBasePolicyDetailsRepository;
    @Autowired
    private HierarchyService hierarchyService;
    @Autowired
    private TeamHierarchyMappingRepo teamHierarchyMappingRepo;
    @Autowired
    private CustomerLedgerService customerLedgerService;
    @Autowired
    private CommonListRepository commonListRepository;
    @Autowired
    private HierarchyRepository hierarchyRepository;
    @Autowired
    private PostpaidPlanChargeRepo planChargeRepo;
    @Autowired
    private SmsSchedulerService smsSchedulerService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private OTPService otpService;
    @Autowired
    private CustChargeService custChargeService;
    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;
    @Autowired
    private DebitDocRepository debitDocRepository;
    @Autowired
    private CustomerDocDetailsRepository customerDocDetailsRepository;
    @Autowired
    private DebitDocService debitDocService;
    @Autowired
    private CustomerDBRService customerDBRService;
    @Autowired
    private MatrixRepository matrixRepository;
    @Autowired
    private TatMatrixWorkFlowDetailsRepo tatMatrixWorkFlowDetailsRepo;
    @Autowired
    private TatUtils tatUtils;
    @Autowired
    private PlanServiceRepository planServiceRepository;
    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;
    @Autowired
    private EzBillServiceUtility ezBillServiceUtility;
    @Autowired
    private CustChargeDetailsRepository custChargeDetailsRepository;

    @Autowired
    private BusinessUnitRepository businessUnitRepository;
    @Autowired
    private LinkAcceptanceMapper linkAcceptanceMapper;

    @Autowired
    ServiceRepository serviceRepository;
    @Autowired
    DiscountAuditRepocitory discountAuditRepocitory;

    @Autowired
    private CreditDocRepository creditDocRepository;
    @Autowired
    private CafFollowUpAuditRepository cafFollowUpAuditRepository;

    @Autowired
    private CustMacMapppingRepository custMacMapppingRepository;

    @Autowired
    private LeadMasterRepository leadMasterRepository;

    @Autowired
    private TrialDebitDocRepository trialDebitDocRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private PlanGroupRepository planGroupRepository;

    @Autowired
    private ToolService toolService;

    @Autowired
    CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    private PlanGroupMappingChargeRelRepo planGroupMappingChargeRelRepo;

    @Autowired
    CustomersController customersController;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    private WifiService wifiService;

    @Autowired
    private LocationMasterRepository locationMasterRepository;

    @Autowired
    private CustomerLocationRepository customerLocationRepository;

    @Autowired
    private CustomersUtil customersUtil;

    @Autowired
    private NumberSequenceUtil numberSequenceUtil;

    @Autowired
    private CustomerLocationMapper customerLocationMapper;

    @Autowired
    private CmsClientUtil cmsClientUtil;
    @Autowired
    QOSPolicyService qosPolicyService;

    @Autowired
    StaffUserMapper staffUserMapper;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    CustomRepository customRepository;

    @Autowired
    CustIpMappingRepo custIpMappingRepo;

    @Autowired
    CustMacMgmtService custMacMgmtService;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private CustAccountProfileRepository custAccountProfileRepository;

    @Autowired
    private AcctNumCreationService acctNumCreationService;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Transactional
    public CustomersPojo save(CustomersPojo pojo, String requestFrom, boolean isCustomerUpdate) throws Exception {
        Map<String, String> map = new HashMap<>();
        // TODO: pass mvnoID manually 6/5/2025
        if (pojo.getMvnoId() == null || pojo.getMvnoId() == getMvnoIdFromCurrentStaff(null)) {
            pojo.setMvnoId(getMvnoIdFromCurrentStaff(null));        // TODO: pass mvnoID manually 6/5/2025
        }
        if (isCustomerUpdate) {
            customersService.updateUsername(pojo.getUsername(), pojo.getId());
        }
        // pojo=getLocationmapping(pojo);
        if (pojo != null && (pojo.getIsCustCaf() != null && pojo.getIsCustCaf().equalsIgnoreCase("yes")) && (pojo.getCustlabel() != null && pojo.getCustlabel().equalsIgnoreCase("organization")) && (pojo.getPlanMappingList() != null && pojo.getPlanMappingList().size() > 0)) {
            List<CustomersPojo> custCafPojoSavedList = new ArrayList<>();
            Map<String, Object> cafList = customersService.deriveChildCustomersFromOrgCust(pojo.getCustlabel(), pojo);
            CustomersPojo parentCustomer = null;
            if (cafList != null && cafList.size() > 0) {
                parentCustomer = (CustomersPojo) cafList.get("orgCaf");
                List<CustomersPojo> childCustPojoList = (List<CustomersPojo>) cafList.get("childCustList");

                if (parentCustomer != null) {
                    parentCustomer = saveSubscriber(parentCustomer, requestFrom, isCustomerUpdate);
                    custCafPojoSavedList.add(parentCustomer);
                    if (childCustPojoList != null && childCustPojoList.size() > 0) {
                        for (CustomersPojo childCust : childCustPojoList) {
                            String parentCustFullName = null;
                            if (parentCustomer.getParentCustomers() != null) {
                                childCust.setParentCustomers(parentCustomer.getParentCustomers());

                                if (parentCustomer.getParentCustomers().getFirstname() != null && parentCustomer.getParentCustomers().getLastname() != null) {
                                    parentCustFullName = parentCustomer.getParentCustomers().getFirstname() + " " + parentCustomer.getParentCustomers().getLastname();
                                } else {
                                    parentCustFullName = "";
                                    if (parentCustomer.getParentCustomers().getFirstname() != null)
                                        parentCustFullName += parentCustomer.getParentCustomers().getFirstname();
                                    if (parentCustomer.getParentCustomers().getLastname() != null)
                                        parentCustFullName += parentCustomer.getParentCustomers().getLastname();
                                }
                            } else {
                                childCust.setParentCustomers(parentCustomer);
                                parentCustFullName = "";
                                if (parentCustomer.getFirstname() != null && parentCustomer.getLastname() != null) {
                                    parentCustFullName += parentCustomer.getFirstname() + " " + parentCustomer.getLastname();
                                } else {
                                    if (parentCustomer.getFirstname() != null)
                                        parentCustFullName += parentCustomer.getFirstname();

                                    if (parentCustomer.getLastname() != null)
                                        parentCustFullName += parentCustomer.getLastname();
                                }
                            }
                            childCust.setPartnerid(parentCustomer.getPartnerid());
                            childCust.setParentCustomerName(parentCustFullName);
                            if (parentCustomer.getParentCustomerId() != null) {
                                childCust.setParentCustomerId(parentCustomer.getParentCustomerId());
                            } else {
                                if (parentCustomer.getParentCustomers() != null && parentCustomer.getParentCustomers().getId() != null)
                                    childCust.setParentCustomerId(parentCustomer.getParentCustomers().getId());
                                else
                                    childCust.setParentCustomerId(parentCustomer.getId());
                            }
                            childCust = saveSubscriber(childCust, requestFrom, isCustomerUpdate);
                            custCafPojoSavedList.add(childCust);
                        }
                    }
                }
            }
            return parentCustomer;
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (pojo.getMvnoId() != getMvnoIdFromCurrentStaff(null)) {
            return subscriberService.saveSubscriberFromForm(pojo, requestFrom, isCustomerUpdate);
        }

        return saveSubscriber(pojo, requestFrom, isCustomerUpdate);
    }


    public CustomersPojo saveSubscriber(CustomersPojo subscriber, String requestFrom, boolean isCustomerUpdate) throws Exception {
        String SUBMODULE = MODULE + " [saveSubscriber()] ";
        LocalDateTime currentTime = LocalDateTime.now();
        final CustomersPojo tempCustomerPojo = subscriber;
        String planName = null;
        Long validity = null;
        Customers parentCustomer = null;
        List<CustIpMapping> custIpMappings = new ArrayList<>();
        double discount = subscriber.getDiscount();
        //check customer process as per BU
        BusinessUnit businessUnit = new BusinessUnit();
        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        } else if (getBUIdsFromCurrentStaff().size() == 0) {
            businessUnit.setPlanBindingType("");
        }
        subscriber = updateCustomerPojoAsPerBU(subscriber, businessUnit);

        try {
            if (getBUIdsFromCurrentStaff().size() == 1)
                subscriber.setBuId(getBUIdsFromCurrentStaff().get(0));

            boolean isDirectChargeApplied = false;
            //create New Customer
            if(null != subscriber && subscriber.getId() == null) {
                subscriber = createNewCustomer(subscriber, currentTime,requestFrom,businessUnit);
            }
            //Update existing customer
            else {
                subscriber.setIs_from_pwc(requestFrom.equalsIgnoreCase("pw"));
                subscriber = updateCustomerCreations(subscriber, parentCustomer, currentTime, validity);
            }

            CustomersPojo customersPojo = subscriber;
            if (isCustomerUpdate) {
                List<CustPlanMapppingPojo> planMappingList = custPlanMappingService.findAllByCustomersId(subscriber.getId());
                if (!CollectionUtils.isEmpty(planMappingList)) {
                    subscriber.setPlanMappingList(planMappingList);
                }
                subscriber.setAadhar(customersService.updatedAadhar(subscriber.getId(), subscriber.getAadhar()));
                subscriber.setPan(customersService.updatedPan(subscriber.getId(), subscriber.getPan()));
                subscriber.setGst(customersService.updatedGst(subscriber.getId(), subscriber.getGst()));
                customersPojo = customerMapper.domainToDTO(update(customerMapper.dtoToDomain(subscriber, new CycleAvoidingMappingContext())), new CycleAvoidingMappingContext());
            }

            customersPojo.setPartnerLedgerMappingId(subscriber.getPartnerLedgerMappingId());
            if (customersPojo != null && customersPojo.getId() != null && isDirectChargeApplied) {
                custChargeService.mapCustomerPlanPackIdToDirectCharge(customersPojo.getId());
            }
            if (customersPojo.getParentCustomers() != null) {
                Integer id = customersPojo.getParentCustomers().getId();
                customersPojo.setParentCustomerId(id);
            }

            //send message
            if (customersPojo.getStatus() != null && !customersPojo.getStatus().equalsIgnoreCase("NewActivation")) {
                syncCustomerDataWithOtherServices(customersPojo);
            }

            if (subscriber.getOverChargeList() != null && !subscriber.getOverChargeList().isEmpty() && subscriber.getOverChargeList().get(0).getBillTo().equalsIgnoreCase(Constants.ORGANIZATION) && subscriber.getOverChargeList().get(0).getIsInvoiceToOrg()) {
                customersService.saveDirectChargeDataForOrganizationCustomer(subscriber, subscriber.getCusttype());
            }

            //Addidng IpMapping to customer
            if (!custIpMappings.isEmpty()) {
               customersPojo = saveOrUpdateCustomerIpMapping(custIpMappings, subscriber);
            }
            return customersPojo;
        } catch (CustomValidationException ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            throw new CustomValidationException(ex.getErrCode(), ex.getMessage(), null);
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error("Unable to send customer Approval", APIConstants.ERROR_TAG, ex.getStackTrace());
            throw ex;
        }
    }

    public CustomersPojo updateCustomerPojoAsPerBU(CustomersPojo subscriber, BusinessUnit businessUnit) {

        if (getBUIdsFromCurrentStaff().size() != 0) {

            if (businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.PREDEFINED)) {
                if (subscriber.getPan() != null && !subscriber.getPan().equalsIgnoreCase("")) {
                    List<String> aLong = customersRepository.getpanforretail(subscriber.getPan());
                    for (int i = 0; i < aLong.size(); i++) {
                        if (!aLong.get(i).equalsIgnoreCase("")) {
                            if (aLong.get(i) == subscriber.getPan()) {
                                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("pan no must not be same"), null);
                            }
                        }
                    }
                }
                subscriber.setBusinessType(CommonConstants.RETAIL);
            } else if (businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND)) {
                subscriber.setBusinessType(CommonConstants.ENTERPRISE);
                if (subscriber.getPan() != null && !subscriber.getPan().equalsIgnoreCase("")) {
                    List<String> aLong = customersRepository.getpanforenterprise(subscriber.getPan());
                    for (int i = 0; i < aLong.size(); i++) {
                        if (!aLong.get(i).equalsIgnoreCase("")) {
                            if (aLong.get(i) == subscriber.getPan()) {
                                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("pan no must not be same"), null);
                            }
                        }
                    }
                }
            }
        }
        return subscriber;
    }

    public void syncCustomerDataWithOtherServices(CustomersPojo customersPojo) {
        StringBuilder planNamesBuilder = new StringBuilder();
        if (customersPojo.getPlanMappingList() != null) {
            for (CustPlanMapppingPojo mappping : customersPojo.getPlanMappingList()) {
                String planname = postpaidPlanRepo.findNameById(mappping.getPlanId());
                if (!planNamesBuilder.toString().contains(planname)) {
                    planNamesBuilder.append(planname).append(", ");
                }
            }
        }

        customersService.sendCustApprovalRegistrationMessage(customersPojo.getId(),LocalDateTime.now().toString(), planNamesBuilder.toString(), customersPojo.getUsername(), customersPojo.getPassword(), customersPojo.getCountryCode(), customersPojo.getMobile(), customersPojo.getEmail(), customersPojo.getMvnoId(), customersPojo.getStatus(), customersPojo.getAcctno(),(long) customersService.getLoggedInStaffId(),customersPojo.getLoginUsername(),customersPojo.getLoginPassword());

        CustomerMessage customerMessage = new CustomerMessage();
        customerMessage.setId(customersPojo.getId());
        customerMessage.setTitle(customersPojo.getTitle());
        customerMessage.setUsername(customersPojo.getUsername());
        customerMessage.setPassword(customersPojo.getPassword());
        customerMessage.setFirstname(customersPojo.getFirstname());
        customerMessage.setLastname(customersPojo.getLastname());
        customerMessage.setStatus(customersPojo.getStatus());
        customerMessage.setIsDeleted(customersPojo.getIsDeleted());
        customerMessage.setMvnoId(customersPojo.getMvnoId());
        if (customersPojo.getBuId() != null) {
            customerMessage.setBuId(customersPojo.getBuId().intValue());
        }
//        this.messageSender.send(customerMessage, RabbitMqConstants.QUEUE_APIGW_SEND_CUSTOMER);
        kafkaMessageSender.send(new KafkaMessageData(customerMessage, CustomerMessage.class.getSimpleName()));

        if (customersPojo.getLeadId() != null) {
            LeadMaster leadMaster = leadMasterRepository.findById(customersPojo.getLeadId()).orElse(null);
            LeadMasterPojoMessage leadMasterPojoMessage = new LeadMasterPojoMessage(leadMaster);
            leadMasterPojoMessage.setCurrentLoggedInStaffId(getLoggedInUserId());
            kafkaMessageSender.send(new KafkaMessageData(leadMasterPojoMessage, LeadMasterPojoMessage.class.getSimpleName()));
//            this.messageSender.send(leadMasterPojoMessage, RabbitMqConstants.QUEUE_LEAD_CAF_CONVERTION);
        }
    }

    public CustomersPojo saveOrUpdateCustomerIpMapping(List<CustIpMapping> custIpMappings, CustomersPojo subscriber) {
        for (CustIpMapping custIpMapping : custIpMappings) {
            custIpMapping.setCustid(subscriber.getId());
        }
        if (!customersService.ifIpForCustomerExit(custIpMappings)) {
            custIpMappingRepo.saveAll(custIpMappings);
            subscriber.setCustIpMappingList(custIpMappings);
        } else {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Ip is already is exit, Please try with diffrent Ip", null);
        }
        return subscriber;
    }

    public void generateCommunicationHelper(CustomersPojo subscriber, String planName) throws Exception {
        CommunicationHelper communicationHelper = new CommunicationHelper();
        Map<String, String> map = new HashMap<>();
        map.put(CommunicationConstant.USERNAME, subscriber.getUsername());
        map.put(CommunicationConstant.PASSWORD, subscriber.getPassword());
        map.put(CommunicationConstant.DESTINATION, subscriber.getMobile());
        map.put(CommunicationConstant.EMAIL, subscriber.getEmail());

        if (planName != null) {
            map.put(CommunicationConstant.PLAN_NAME, planName);
            map.put(CommunicationConstant.REGISTRATION_DATE, LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            communicationHelper.generateCommunicationDetails(CommunicationConstant.ACC_REGISTERED, Collections.singletonList(map));
        } else {
            communicationHelper.generateCommunicationDetails(CommunicationConstant.REGISTRATION, Collections.singletonList(map));
        }
    }

    public CustAccountProfile fetchSubscriberAccountProfileDetail(Long profileId){
        CustAccountProfile custAccountProfile = custAccountProfileRepository.findByMvnoId(Long.valueOf(profileId)).orElse(null);
        return custAccountProfile;
    }

    public CustomersPojo createNewCustomer(CustomersPojo subscriber, LocalDateTime currentTime, String requestFrom, BusinessUnit businessUnit) throws Exception {
        final CustomersPojo tempCustomerPojo = subscriber;
        //subscriber=getLocationmapping(subscriber);
        String planName = null;
        Long validity = null;
        Customers parentCustomer = null;
        List<CustIpMapping> custIpMappings = new ArrayList<>();
        double discount = subscriber.getDiscount();
        Double trialPlanPeirodConstant;
// TODO: pass mvnoID manually 6/5/2025
        ClientService trialClientService = clientServiceSrv.getByNameAndMvnoIdEquals(Constants.TRIAL_PLAN_PERIOD_THRESHOLD, getLoggedInMvnoId(null));
        if (trialClientService != null) {
            trialPlanPeirodConstant = Double.valueOf(trialClientService.getValue());
        } else {
            trialPlanPeirodConstant = 0d;
        }
        Integer tpValidityCount;
        // TODO: pass mvnoID manually 6/5/2025
        ClientService noOfTimeTrialclientService = clientServiceSrv.getByNameAndMvnoIdEquals(Constants.ALLOW_NUMBER_OF_TIME_TRAIL, getLoggedInMvnoId(null));
        if (noOfTimeTrialclientService != null) {
            tpValidityCount = Integer.valueOf(noOfTimeTrialclientService.getValue());
        } else {
            tpValidityCount = 0;
        }
        if (subscriber.getCustIpMappingList() != null) {
            custIpMappings = subscriber.getCustIpMappingList();
            subscriber.setCustIpMappingList(null);
        }
        Integer plangroupId = subscriber.getPlangroupid();
        HashMap<Integer, Boolean> mapforTrialPlan = new HashMap<Integer, Boolean>();
        if (subscriber.getIsCustCaf() != null && !"".equals(subscriber.getIsCustCaf())) {
            if (subscriber.getIsCustCaf().equalsIgnoreCase("yes")) {
                subscriber.setStatus(SubscriberConstants.NEW_ACTIVATION);
            } else
                subscriber.setStatus(subscriber.getStatus());
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (subscriber.getMvnoId() == getMvnoIdFromCurrentStaff(null)) {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != null)
                // TODO: pass mvnoID manually 6/5/2025
                subscriber.setMvnoId(getMvnoIdFromCurrentStaff(null));
        }

        if (subscriber.getParentCustomerId() != null)
            parentCustomer = customersRepository.getOne(subscriber.getParentCustomerId());
        Long profileId = mvnoRepository.findProfileIdByMvnoId(Long.valueOf(subscriber.getMvnoId())).orElse(null);
        if(profileId!=null) {
            CustAccountProfile custAccountProfile = fetchSubscriberAccountProfileDetail(profileId);
            if (subscriber.getAcctno() == null || subscriber.getAcctno().trim().isEmpty()) {
                // TODO: pass mvnoID manually 6/5/2025
                subscriber.setAcctno(acctNumCreationService.getNewCustomerAccountNo(custAccountProfile, getMvnoIdFromCurrentStaff(null)));
            }
        }
        if(Boolean.TRUE.equals(subscriber.getIsCredentialMatchWithAccountNo())){
            subscriber.setUsername(subscriber.getAcctno());
            subscriber.setPassword(subscriber.getAcctno());
            subscriber.setLoginUsername(subscriber.getAcctno());
        }else {
            subscriber.setPassword(subscriber.getPassword());
        }
        if(subscriber.getIsPasswordAutoGenerated() != null && Boolean.FALSE.equals(subscriber.getIsPasswordAutoGenerated())){
            subscriber.setPassword(toolService.generateRandomPassword());
            subscriber.setLoginPassword(toolService.generateRandomPassword());
        }
        subscriber.setSelfcarepwd(subscriber.getPassword());
        subscriber.setCusttype(subscriber.getCusttype());
        subscriber.setPopid(subscriber.getPopid());
        subscriber.setStaffId(subscriber.getStaffId());

        if (subscriber.getBranch() != null)
            subscriber.setBranch(subscriber.getBranch());

        List<CreditDocumentPojo> creditDocumentList = new ArrayList<>();
        List<CustPlanMapppingPojo> planMappingList = new ArrayList<>();
        if (subscriber.getLeadId() != null) {
            LeadMaster leadMaster = leadMasterRepository.findById(subscriber.getLeadId()).orElse(null);
            if (leadMaster != null && leadMaster.getLeadSource() != null && leadMaster.getLeadSource().getLeadSourceName() != null)
                subscriber.setLeadSource(leadMaster.getLeadSource().getLeadSourceName());
        }
        if (plangroupId != null && plangroupId != 0) {
            // TODO: pass mvnoID manually 6/5/2025
            PlanGroup planGroup = planGroupService.findPlanGroupById(plangroupId, getMvnoIdFromCurrentStaff(null));
            if (planGroup == null) {
                throw new IllegalArgumentException("No record found for Plan Group with id : '" + plangroupId + "'. Or you are not authorised to update/delete this record.");
            }
            // TODO: pass mvnoID manually 6/5/2025
            List<PlanGroupMapping> planGroupMappingList = planGroupMappingService.findPlanGroupMappingByPlanGroupId(plangroupId, getMvnoIdFromCurrentStaff(null));
            CustPlanMapppingPojo planMapping;
            List<CustPlanMapppingPojo> planMappingBySubscriber = subscriber.getPlanMappingList();
            Random rnd = new Random();
            int renewalId = rnd.nextInt(999999);
            if (planGroupMappingList != null) {
                for (PlanGroupMapping planGroupMapping : planGroupMappingList) {
                    planMapping = new CustPlanMapppingPojo();
                    planMapping.setDiscountType(tempCustomerPojo.getDiscountType());
                    planMapping.setDiscountExpiryDate(tempCustomerPojo.getDiscountExpiryDate());
                    planMapping.setPlanId(planGroupMapping.getPlan().getId());
                    planMapping.setPlangroupid(plangroupId);
                    planMapping.setService(planGroupMapping.getService());
                    planMapping.setRenewalId(renewalId);

                    if (!CollectionUtils.isEmpty(planMappingBySubscriber)) {
                        Optional<CustPlanMapppingPojo> custPlanMapppingPojo = planMappingBySubscriber.stream().filter(pm -> pm.getPlanId().equals(planGroupMapping.getPlan().getId())).findAny();
                        if (custPlanMapppingPojo.isPresent()) {
                            if (custPlanMapppingPojo.get().getBillTo() != null)
                                planMapping.setBillTo(custPlanMapppingPojo.get().getBillTo());
                            else
                                planMapping.setBillTo(Constants.CUSTOMER);

                            if (custPlanMapppingPojo.get().getIsInvoiceToOrg() != null)
                                planMapping.setIsInvoiceToOrg(custPlanMapppingPojo.get().getIsInvoiceToOrg());
                            else
                                planMapping.setIsInvoiceToOrg(false);

                            planMapping.setNewAmount(custPlanMapppingPojo.get().getNewAmount());
                        }
                    }
                    if (subscriber.getIstrialplan()) {
                        mapforTrialPlan.put(planMapping.getPlanId(), true);
                        planMapping.setIstrialplan(true);
                        planMapping.setIsinvoicestop(true);
                    } else {
                        mapforTrialPlan.put(planMapping.getPlanId(), false);
                        planMapping.setIstrialplan(false);
                        planMapping.setIsinvoicestop(false);
                    }
                    if (subscriber.getParentCustomerId() != null) {
                        if (subscriber.getInvoiceType() != null) {
                            planMapping.setInvoiceType(subscriber.getInvoiceType());
                        }
                    }
                    planMappingList.add(planMapping);
                }
                subscriber.setPlanMappingList(planMappingList);
            }
        }

        if (null != subscriber.getCusttype() && subscriber.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_PREPAID)) {
            //Prepaid
            subscriber.setNextBillDate(LocalDate.now());
            CustPlanMapppingPojo mapping;
            LocalDateTime parentExpiryDate = null;
            if (null != subscriber || subscriber.getCustlabel().equalsIgnoreCase("organization")) {
                if (subscriber.getPlanMappingList() != null && subscriber.getPlanMappingList().size() > 0) {
                    for (int i = 0; i < subscriber.getPlanMappingList().size(); i++) {
                        parentExpiryDate = null;
                        if (subscriber.getPlanMappingList().size() > 1)
                            mapping = customersService.getSinglePlan(subscriber, i);
                        else
                            mapping = subscriber.getPlanMappingList().get(i);

                        if (mapping.getBillTo() == null) {
                            mapping.setBillTo(Constants.CUSTOMER);
                            mapping.setIsInvoiceToOrg(false);
                        }

                        if (mapping.getIstrialplan() != null && mapping.getIstrialplan()) {
                            mapping.setIsinvoicestop(true);
                            subscriber.setIsinvoicestop(true);
                            subscriber.setIstrialplan(true);
                            subscriber.getPlanMappingList().forEach(custPlanMapppingPojo -> custPlanMapppingPojo.setIsTrialValidityDays(Double.valueOf(trialPlanPeirodConstant)));
                            subscriber.getPlanMappingList().forEach(custPlanMapppingPojo -> custPlanMapppingPojo.setTrialPlanValidityCount(0));
//                                    }
                            mapforTrialPlan.put(mapping.getPlanId(), true);
                        } else {
                            mapforTrialPlan.put(mapping.getPlanId(), false);
                            mapping.setIstrialplan(false);
                            mapping.setIsinvoicestop(false);
                            mapping.setIsTrialValidityDays(0.0);
                            mapping.setTrialPlanValidityCount(0);
                        }
                        if (subscriber.getParentCustomerId() != null) {
                            if (subscriber.getPlanMappingList().get(i).getInvoiceType() != null) {
                                mapping.setInvoiceType(subscriber.getPlanMappingList().get(i).getInvoiceType());
                            }
                            if (subscriber.getPlanMappingList().get(i).getInvoiceType() == null) {
                                mapping.setInvoiceType(subscriber.getInvoiceType());
                            }
                        }
                        String serviceName = mapping.getService();

                        if (parentCustomer != null) {
                            List<CustomerPlansModel> activeList = subscriberService.getActivePlanList(parentCustomer.getId(), false).stream().filter(x -> x.getService().equals(serviceName)).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                            if (activeList != null && activeList.size() > 0)
                                parentExpiryDate = Instant.ofEpochMilli(activeList.get(0).getExpiryDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                            List<CustomerPlansModel> futureList = subscriberService.getFuturePlanList(parentCustomer.getId(), false).stream().filter(x -> x.getService().equals(serviceName)).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                            if (futureList != null && futureList.size() > 0)
                                parentExpiryDate = Instant.ofEpochMilli(futureList.get(0).getExpiryDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                        }
                        if (subscriber.getBillableCustomerId() != null) {
                            mapping.setBillableCustomerId(subscriber.getBillableCustomerId());
                        }

                        // Set Expiry Date of Plan
                        if (mapping.getPlanId() != null) {
                            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                                mapping.setPurchaseFrom(SubscriberConstants.PURCHASE_FROM_ADMIN);
                            } else {
                                mapping.setPurchaseFrom(SubscriberConstants.PURCHASE_FROM_PARTNER);
                            }
                            mapping.setPurchaseType(SubscriberConstants.PURCHASE_TYPE_NEW);
                            PostpaidPlan plan = planService.get(mapping.getPlanId(),subscriber.getMvnoId());
                            if (plan != null) {
                                planName = plan.getName();
                            }
                            Double taxAmount = 0d;
                            if (!CollectionUtils.isEmpty(subscriber.getAddressList())) {
                                TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO(plan.getId(), subscriber.getAddressList().stream().filter(data -> data.getAddressType().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PRESENT)).findAny().orElse(null).getStateId(), null, null);
                                taxAmount = taxService.taxCalculationByPlan(taxDetailCountReqDTO, plan.getChargeList());
                            }
                            mapping.setPlanValidityDays(customersService.calculatePlanValidityDays(subscriber, plan, validity, LocalDateTime.now()));
                            if (null != plan && mapping.getExpiryDate() == null) {
                                mapping.setStartDate(currentTime);
                                LocalDateTime expDate = customersService.calculateExpiryDate(subscriber, null, plan, validity);
                                if (!plan.getUnitsOfValidity().equalsIgnoreCase("Hours")) {
                                    expDate = LocalDateTime.of(expDate.toLocalDate(), LocalTime.from(currentTime));
                                }
                                if (parentCustomer != null && parentExpiryDate != null && mapping.getInvoiceType() != null && mapping.getInvoiceType().equalsIgnoreCase(CommonConstants.INVOICE_TYPE_GROUP)) {
                                    if (expDate.isAfter(parentExpiryDate)) expDate = parentExpiryDate;
                                } else if (parentCustomer != null && parentExpiryDate == null && mapping.getInvoiceType().equalsIgnoreCase(CommonConstants.INVOICE_TYPE_GROUP)) {
                                    if (subscriber.getIsCustCaf() != null) {
                                        if (!subscriber.getIsCustCaf().equalsIgnoreCase("yes"))
                                            throw new RuntimeException("Parent don't have " + serviceName + " Service");
                                    }
                                }

                                mapping.setEndDate(expDate);
                                mapping.setExpiryDate(expDate);
                                LocalDateTime startDateTmp = mapping.getStartDate().withHour(0).withMinute(0).withSecond(0);
                                LocalDateTime endDateTmp = mapping.getEndDate().withHour(0).withMinute(0).withSecond(0);
                                Long prorate_validity = ChronoUnit.DAYS.between(startDateTmp, endDateTmp);
                                if (prorate_validity == 0)
                                    prorate_validity = 1l;
                                mapping.setValidity(prorate_validity.doubleValue());
                                mapping.setOfferPrice(parentCustomer != null && mapping.getInvoiceType() != null && mapping.getInvoiceType().equals(CommonConstants.INVOICE_TYPE_GROUP) ? (plan.getOfferprice() / mapping.getPlanValidityDays()) * prorate_validity : plan.getOfferprice());
                                mapping.setTaxAmount(taxAmount);
                                if (null != plan.getQospolicy()) {
                                    mapping.setQospolicyId(plan.getQospolicy().getId());
                                }
                            }
                        }
                    }
                }

                // Create Credit Document
                if (null != subscriber.getPaymentDetails()) {
                    RecordPaymentPojo paymentPojo = subscriber.getPaymentDetails();
                    if (requestFrom.equalsIgnoreCase("pw")) {
                        if (paymentPojo.getPaytype() == null) {
                            paymentPojo.setPaytype(com.adopt.apigw.modules.subscriber.model.Constants.ADVANCE);
                        }
                        if (paymentPojo.getType() == null) {
                            paymentPojo.setType(UtilsCommon.PAYMENT_TYPE);
                        }
                        if (paymentPojo.getMvnoId() == null) {
                            // TODO: pass mvnoID manually 6/5/2025
                            paymentPojo.setMvnoId(getMvnoIdFromCurrentStaff(null));
                        }
                    }
                    if (paymentPojo.getPaymode() != null && paymentPojo.getAmount() > 0) {
                        paymentPojo.setAmount(paymentPojo.getAmount());
                        paymentPojo.setPaymentdate(LocalDate.now());
                        Partner partner = partnerService.get(subscriber.getPartnerid(),subscriber.getMvnoId());
                        if (partner != null)
                            paymentPojo.setPaymentreferenceno("Received By Partner : " + partner.getName());
                    }

                    CreditDocumentPojo creditDocument = new CreditDocumentPojo();
                    if (null != paymentPojo.getPaymode()) {

                        if (paymentPojo.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE_TYPE_CHEQUE)) {
                            creditDocument.setPaydetails1(paymentPojo.getBank());
                            creditDocument.setPaydetails2(paymentPojo.getBranch());
                            creditDocument.setPaydetails3(paymentPojo.getChequeno());
                            creditDocument.setChequedate(paymentPojo.getChequedate());
                        }

                        if (getLoggedInUser().getLco())
                            creditDocument.setLcoId(getLoggedInUser().getPartnerId());
                        else
                            creditDocument.setLcoId(null);

                        creditDocument.setPaymode(paymentPojo.getPaymode());
                        creditDocument.setPaydetails4(paymentPojo.getPaymentreferenceno());
                        creditDocument.setAmount(paymentPojo.getAmount());
                        creditDocument.setPaymentdate(paymentPojo.getPaymentdate());
                        creditDocument.setCustomer(subscriber);
                        creditDocument.setCreatedById(subscriber.getCreatedById());
                        creditDocument.setRemarks(paymentPojo.getRemark());
                        creditDocument.setApproverid(getLoggedInUserId());
                        creditDocument.setReferenceno(String.valueOf(UtilsCommon.getUniqueNumber()));
                        creditDocument.setStatus(UtilsCommon.PAYMENT_STATUS_PENDING);
                        if (requestFrom.equalsIgnoreCase("pw")) {
                            creditDocument.setStatus(UtilsCommon.PAYMENT_STATUS_APPROVED);
                            creditDocument.setPaytype(paymentPojo.getPaytype());
                            creditDocument.setType(paymentPojo.getType());
                            // TODO: pass mvnoID manually 6/5/2025
                            creditDocument.setMvnoId(getMvnoIdFromCurrentStaff(null));
                        }
                        creditDocument.setAdjustedAmount(Double.valueOf(UtilsCommon.INITIAL_PAYMENT_ADJUST));
                        creditDocumentList.add(creditDocument);
                    }
                    if (requestFrom.equals("pw") || (paymentPojo.getPaymode() != null && paymentPojo.getAmount() > 0))
                        subscriber.setCreditDocuments(creditDocumentList);
                }
            }
        } else {
            //Postpaid
            // Set NextBillDate For Postpaid Customer.
            if (null != subscriber.getCusttype() && subscriber.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {

                if (parentCustomer != null) {
                    NextBillDatinfo childMinNextBillDate = customersService.getNextBillDateChargeType(customerMapper.dtoToDomain(subscriber, new CycleAvoidingMappingContext()));
                    if (childMinNextBillDate.getNextBilldate().isAfter(parentCustomer.getNextBillDate()) && childMinNextBillDate.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_RECURRING))
                        subscriber.setNextBillDate(parentCustomer.getNextBillDate());
                    else subscriber.setNextBillDate(childMinNextBillDate.getNextBilldate());
                } else
                    subscriber.setNextBillDate(customersService.getNextBillDate(customerMapper.dtoToDomain(subscriber, new CycleAvoidingMappingContext())));
            }

            if (null != subscriber && null != subscriber.getPlanMappingList() && 0 < subscriber.getPlanMappingList().size()) {
                for (CustPlanMapppingPojo mapping : subscriber.getPlanMappingList()) {
                    if (mapping.getPlanId() != null) {
                        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                            mapping.setPurchaseFrom(SubscriberConstants.PURCHASE_FROM_ADMIN);
                        } else {
                            mapping.setPurchaseFrom(SubscriberConstants.PURCHASE_FROM_PARTNER);
                        }
                        mapping.setCustomer(subscriber);
                        if (subscriber.getInvoiceType() == null && mapping.getInvoiceType() != null) {
                            subscriber.setInvoiceType(mapping.getInvoiceType());
                        }
                        //check trial plan
                        if (mapping.getIstrialplan() != null && mapping.getIstrialplan()) {
                            mapping.setIsinvoicestop(true);
                            subscriber.setIsinvoicestop(true);
                            subscriber.setIstrialplan(true);
                            if (businessUnit.getPlanBindingType().equalsIgnoreCase("Predefined") || Objects.isNull(businessUnit.getPlanBindingType()) || (businessUnit.getPlanBindingType().equalsIgnoreCase("On-Demand"))) {
                                subscriber.getPlanMappingList().forEach(custPlanMapppingPojo -> custPlanMapppingPojo.setIsTrialValidityDays(Double.valueOf(tpValidityCount)));
                            }
                            mapforTrialPlan.put(mapping.getPlanId(), true);
                        } else {
                            mapforTrialPlan.put(mapping.getPlanId(), false);
                            mapping.setIstrialplan(false);
                            mapping.setIsinvoicestop(false);
                        }
                        mapping.setPlangroupid(plangroupId);
                        mapping.setPurchaseType(SubscriberConstants.PURCHASE_TYPE_NEW);
                        PostpaidPlan plan = planService.get(mapping.getPlanId(),subscriber.getMvnoId());
                        planName = plan.getName();
                        Integer locationId = null;

                        if (!CollectionUtils.isEmpty(subscriber.getAddressList()))
                            locationId = subscriber.getAddressList().stream().filter(data -> data.getAddressType().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PRESENT)).findAny().orElse(null).getStateId();

                        TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO(plan.getId(), locationId, null, null);
                        Double taxAmount = taxService.taxCalculationByPlan(taxDetailCountReqDTO, plan.getChargeList());
                        mapping.setPlanValidityDays(customersService.calculatePlanValidityDays(subscriber, plan, validity, LocalDateTime.now()));
                        if (null != plan && mapping.getExpiryDate() == null) {
                            mapping.setStartDate(currentTime);

                            LocalDateTime expDate = customersService.calculateExpiryDate(subscriber, null, plan, validity);
                            if (!plan.getUnitsOfValidity().equalsIgnoreCase("Hours"))
                                expDate = LocalDateTime.of(expDate.toLocalDate(), LocalTime.from(currentTime));

                            if (expDate.toLocalDate().isBefore(subscriber.getNextBillDate())) {
                                mapping.setExpiryDate(expDate);
                                mapping.setEndDate(expDate);
                            } else {
                                mapping.setExpiryDate(LocalDateTime.of(subscriber.getNextBillDate(), LocalTime.from(currentTime)));
                                mapping.setEndDate(LocalDateTime.of(subscriber.getNextBillDate(), LocalTime.from(currentTime)));
                            }

                            Long prorate_validity = ChronoUnit.DAYS.between(mapping.getStartDate(), mapping.getEndDate());
                            mapping.setOfferPrice(parentCustomer != null && subscriber.getInvoiceType().equals(CommonConstants.INVOICE_TYPE_GROUP) ? (plan.getOfferprice() / mapping.getPlanValidityDays()) * prorate_validity : plan.getOfferprice());
                            mapping.setTaxAmount(taxAmount);
                            mapping.setTaxAmount(taxAmount);
                            if (null != plan.getQospolicy()) {
                                mapping.setQospolicyId(plan.getQospolicy().getId());
                            }
                        }
                    }
                }
            }
        }

        // Set FirstActivation Date
        if (null != subscriber.getStatus() && subscriber.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS) && subscriber.getFirstActivationDate() == null) {
            subscriber.setFirstActivationDate(LocalDateTime.now());
        }
        // Set Activation By Name
        if (null != subscriber.getStatus() && subscriber.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS) && subscriber.getActivationByName() == null) {
            StaffUser loggedStaffUser = staffUserRepository.findById(getLoggedInUserId()).get();
            subscriber.setActivationByName(loggedStaffUser.getFirstname() + " " + loggedStaffUser.getLastname());
        }

        // Set ParentCustomer
        if (subscriber.getParentCustomerId() != null) {
            parentCustomer = customersRepository.getOne(subscriber.getParentCustomerId());
            if (parentCustomer != null) {
                subscriber.setParentCustomers(customerMapper.domainToDTO(parentCustomer, new CycleAvoidingMappingContext()));
            }
        }

        subscriber.setCustname(subscriber.getFirstname());

        // Set Partner
        if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
            subscriber.setPartnerid(getLoggedInUserPartnerId());
        }

        // ApplyCharge With tax
        if (null != subscriber.getOverChargeList() && subscriber.getOverChargeList().size() > 0) {
            ChargePojo chargePojo = new ChargePojo();
            List<CustChargeDetailsPojo> list = new ArrayList<>();
            if (!businessUnit.getPlanBindingType().trim().equalsIgnoreCase("")) {
                if (businessUnit.getPlanBindingType().equalsIgnoreCase("On-Demand")) {
                    for (CustChargeDetailsPojo custChargeDetailsPojo : subscriber.getOverChargeList()) {
                        if (custChargeDetailsPojo.getChargePojo() != null) {
                            chargePojo = chargeService.save(custChargeDetailsPojo.getChargePojo());
                            custChargeDetailsPojo.setChargePojo(chargePojo);
                            custChargeDetailsPojo.setChargeid(chargePojo.getId());
                            list.add(custChargeDetailsPojo);
                        }
                        subscriber.setOverChargeList((list));
                    }
                }
            }
            List<CustChargeDetailsPojo> custChargeDetailsPojoList = custChargeService.calDirectCharge(subscriber);
            if (custChargeDetailsPojoList.size() > 0) {
                subscriber.setOverChargeList(custChargeDetailsPojoList);
            }
        }
//        if (Objects.isNull(subscriber.getIsDunningEnable()) || isCustomerUpdate) {
//            subscriber.setIsDunningEnable(true);
//        }
//        if (Objects.isNull(subscriber.getIsNotificationEnable()) || isCustomerUpdate) {
//            subscriber.setIsNotificationEnable(true);
//        }
        Map<Integer, StaffUserPojo> staffByParentStaffId = new HashMap<>();
        Customers savedCustomer = null;
        if (subscriber.getStatus() != null && !"".equals(subscriber.getStatus())) {
            if (subscriber.getStatus().equalsIgnoreCase("NewActivation")) {
                if (getLoggedInUser().getLco() != null && getLoggedInUser().getLco()) {
                    subscriber.setLcoId(getLoggedInUser().getPartnerId());
                } else {
                    subscriber.setLcoId(null);
                }
                subscriber.setIs_from_pwc(requestFrom.equalsIgnoreCase("pw"));
                savedCustomer = save(customerMapper.dtoToDomain(subscriber, new CycleAvoidingMappingContext()));
                subscriber.setId(savedCustomer.getId());
                StaffUser assignedUser;
                if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
                    Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(subscriber.getMvnoId(), subscriber.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, CommonConstants.HIERARCHY_TYPE, false, true, subscriber);
                    int staffId = 0;
                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                        staffId = Integer.parseInt(map.get("staffId"));
                        StaffUser staffUser = staffUserRepository.findById(staffId).get();
                        assignedUser = staffUser;
                        savedCustomer.setNextTeamHierarchyMapping(Integer.valueOf(map.get("nextTatMappingId")));
                        savedCustomer.setCurrentAssigneeId(staffId);
                        if (savedCustomer != null) {
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER + " with username : " + " ' " + subscriber.getUsername() + " '";
                            hierarchyService.sendWorkflowAssignActionMessage(staffUser.getCountryCode(), staffUser.getPhone(), staffUser.getEmail(), staffUser.getMvnoId(), staffUser.getFullName(), action,staffUser.getId().longValue());
                        }
                        try {
                            NotificationDTO notificationDTO = notificationService.findByName("Registration");
                            if (null != notificationDTO)
                                smsSchedulerService.sendSMS(Collections.singletonList(subscriber), notificationDTO.getId());
                        } catch (Exception e) {
                            ApplicationLogger.logger.error("Unable to Save Subscriber rsponse:{}exception{}", APIConstants.FAIL, e.getStackTrace());
                            e.printStackTrace();
                        }
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CAF, savedCustomer.getId(), savedCustomer.getUsername(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());

                    } else {
                        StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                        assignedUser = currentStaff;
                        subscriber.setCurrentAssigneeId(currentStaff.getId());
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CAF, savedCustomer.getId(), savedCustomer.getUsername(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                    }
                    if (staffByParentStaffId != null && !staffByParentStaffId.isEmpty()) {
                        for (Map.Entry<Integer, StaffUserPojo> staffMap : staffByParentStaffId.entrySet()) {
                            StaffUser staffUser = staffUserMapper.dtoToDomain(staffMap.getValue(), new CycleAvoidingMappingContext());
                            tatUtils.changeTatAssignee(subscriber, staffUser, true, false);
                        }
                    }
                    //TAT functionality
                    if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                        if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                            map.put("tat_id", map.get("current_tat_id"));
                        tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, savedCustomer.getId(), null);
                        Hierarchy hierarchy = null;
                        if (getBUIdsFromCurrentStaff().size() == 0) {
                            hierarchy = hierarchyRepository.findByBuIdIsNullAndMvnoIdAndEventNameAndIsDeleted(subscriber.getMvnoId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, false);
                        } else {
                            hierarchy = hierarchyRepository.findByMvnoIdAndBuIdInAndEventNameAndIsDeleted(subscriber.getMvnoId(), getBUIdsFromCurrentStaff(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, false);
                        }
                        if (hierarchy != null) {
                            Optional<Matrix> matrix = matrixRepository.findById(Long.valueOf(hierarchy.getTeamHierarchyMappingList().get(0).getTat_id()));
                            if (matrix.isPresent()) {
                                Matrix matrix1 = matrix.get();
                                subscriber.setSlaTime(matrix1.getSlaTime().intValue());
                                subscriber.setSlaUnit(matrix1.getSlaUnit());
                            }

                        }
                    }

                } else {
                    Map<String, Object> map = hierarchyService.getTeamForNextApprove(subscriber.getMvnoId(), subscriber.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, CommonConstants.HIERARCHY_TYPE, false, true, subscriber);
                    Hierarchy hierarchy = null;
                    HashMap<String, String> tatMapDetails = new HashMap<>();
                    Optional<StaffUser> staffUser = staffUserRepository.findById(getLoggedInUserId());
                    if (!CollectionUtils.isEmpty(map) && map.get("tat_id") != null && !map.get("tat_id").equals("null")) {
                        Long tat_id = Long.valueOf(String.valueOf(map.get("tat_id")));
                        Optional<Matrix> matrix = matrixRepository.findById(tat_id);
                        if (matrix.isPresent()) {
                            if (getBUIdsFromCurrentStaff().size() == 0) {
                                hierarchy = hierarchyRepository.findByBuIdIsNullAndMvnoIdAndEventNameAndIsDeleted(subscriber.getMvnoId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, false);
                            } else {
                                hierarchy = hierarchyRepository.findByMvnoIdAndBuIdInAndEventNameAndIsDeleted(subscriber.getMvnoId(), getBUIdsFromCurrentStaff(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, false);
                            }
                            if (hierarchy != null) {
                                if (matrix.isPresent()) {
                                    Matrix matrix1 = matrix.get();
                                    subscriber.setSlaUnit(matrix1.getRunit());
                                    long hours = 0;
                                    long minutes = 0;
                                    if (matrix1.getRunit().equalsIgnoreCase("MIN")) {
                                        minutes = Long.valueOf(matrix1.getRtime());

                                        LocalTime currentTimenow = LocalTime.now();
                                        LocalTime nextFollowupTime = currentTimenow.plusMinutes(minutes);
                                        if (nextFollowupTime.getHour() < currentTimenow.getHour() ||
                                                (nextFollowupTime.getHour() == currentTimenow.getHour() && nextFollowupTime.getMinute() < currentTimenow.getMinute())) {
                                            LocalDate nextFollowupDate = LocalDate.now().plusDays(1);

                                            subscriber.setNextfollowupdate(nextFollowupDate);
                                            subscriber.setNextfollowuptime(nextFollowupTime);
                                        } else {
                                            subscriber.setNextfollowupdate(LocalDate.now());
                                            subscriber.setNextfollowuptime(nextFollowupTime);
                                        }
                                    } else if (matrix1.getRunit().equalsIgnoreCase("HOUR")) {
                                        hours = Long.valueOf(matrix1.getRtime());
                                        LocalTime currentTimenow = LocalTime.now();
                                        long total_hours = currentTimenow.getHour() + hours;
                                        if (total_hours >= 24) {
                                            long days = total_hours / 24;
                                            long remainingHours = total_hours % 24;
                                            long remainingMinutes = currentTimenow.getMinute();
                                            subscriber.setNextfollowupdate(LocalDate.now().plusDays(days));
                                            subscriber.setNextfollowuptime(LocalTime.of((int) remainingHours, (int) remainingMinutes));
                                        } else {
                                            subscriber.setNextfollowupdate(LocalDate.now());
                                            subscriber.setNextfollowuptime(currentTimenow.plusHours(hours));
                                        }
                                    } else {
                                        subscriber.setNextfollowupdate(LocalDate.now().plusDays(Long.valueOf(matrix1.getRtime())));
                                    }

                                    if (staffUser.get().getStaffUserparent() != null && !CollectionUtils.isEmpty(map) && subscriber != null && !map.get("eventId").equals("0") && !map.get("eventId").equals(null)) {
                                        tatMapDetails.put("workFlowId", map.get("workFlowId").toString());
                                        tatMapDetails.put("eventId", map.get("eventId").toString());
                                        tatMapDetails.put("eventName", map.get("eventName").toString());
                                        tatMapDetails.put("tat_id", map.get("tat_id").toString());
                                        tatMapDetails.put("orderNo", map.get("orderNo").toString());

                                        TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
                                                new TatMatrixWorkFlowDetails(new Long("1"), "Level 1", staffUser.get().getId(),
                                                        Long.valueOf(tatMapDetails.get("workFlowId")), Long.valueOf(tatMapDetails.get("tat_id")),
                                                        staffUser.get().getStaffUserparent().getId(), LocalDateTime.now(),
                                                        matrix1.getRtime().toString(), matrix1.getRunit(), "Notification", true, tatMapDetails.get("nextTatMappingId") != null ? Integer.valueOf(tatMapDetails.get("nextTatMappingId")) : null,
                                                        subscriber.getId(), tatMapDetails.get("eventName"), tatMapDetails.get("eventId") != null ? Integer.valueOf(tatMapDetails.get("eventId")) : null, CommonConstants.NOTIFICATION_TYPE_STAFF, null, true);
//                                               tatUtils.saveOrUpdateDataForTatMatrix( tatMapDetails, staffUser.get(),subscriber.getId(),null);
                                        tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                                    }

                                }
                            }
                        }
                    }
                    subscriber.setId(savedCustomer.getId());
                    subscriber.setCurrentAssigneeId(getLoggedInUserId());
                    StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                    workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CAF, savedCustomer.getId(), savedCustomer.getUsername(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                }
            } else {
                if (getLoggedInUser().getLco() != null && getLoggedInUser().getLco()) {
                    subscriber.setLcoId(getLoggedInUser().getPartnerId());
                } else {
                    subscriber.setLcoId(null);
                }
                subscriber.setIs_from_pwc(requestFrom.equalsIgnoreCase("pw"));
            }
        }
        if (subscriber.getAltmobile() != null && subscriber.getAltmobile().equalsIgnoreCase(""))
            subscriber.setAltmobile(null);

        if (subscriber.getStatus().equalsIgnoreCase(SubscriberConstants.ACTIVE))
            subscriber.setCreditDocuments(null);

        if (!CollectionUtils.isEmpty(subscriber.getCustomerLocations())) {
            List<CustomerLocationMappingDto> customerLocationMappings = customersService.saveCustomerLocationMapping(subscriber, subscriber.getCustomerLocations());
            if (!CollectionUtils.isEmpty(customerLocationMappings)) {
                subscriber.setCustomerLocations(customerLocationMappings);
            }
        }
        String parentQuotaType = subscriber.getParentQuotaType();
        savedCustomer = save(customerMapper.dtoToDomain(subscriber, new CycleAvoidingMappingContext()));
        if (savedCustomer.getCafno() == null) {
            savedCustomer.setCafno(savedCustomer.getId().toString());
        }

        subscriber = customerMapper.domainToDTO(savedCustomer, new CycleAvoidingMappingContext());
        subscriber.setDiscount(discount);
        if (parentQuotaType != null) {
            subscriber.setParentQuotaType(parentQuotaType);
        }
        // Add CreditDocId In CustPlanMapping If Payment Is Collected At The Time Of customer Creation
        if (null != subscriber.getCreditDocuments() && 0 < subscriber.getCreditDocuments().size() && null != subscriber.getPlanMappingList() && 0 < subscriber.getPlanMappingList().size()) {
            CreditDocumentPojo creditDocumentPojo = subscriber.getCreditDocuments().get(0);
            if (creditDocumentPojo.getId() != null) {
                subscriber.getPlanMappingList().get(0).setCreditdocid(creditDocumentPojo.getId().longValue());
            }
        }

        // Set ledger Details if payment is received while creating subscriber
        if (null != subscriber.getCreditDocuments() && 0 < subscriber.getCreditDocuments().size()) {
            String desc = "";
            List<CustLedgerDtlsPojo> ledgerDtlsList = new ArrayList<>();
            for (CreditDocumentPojo creditDocumentPojo : subscriber.getCreditDocuments()) {
                CustLedgerDtlsPojo ledgerDtls = new CustLedgerDtlsPojo();
                ledgerDtls.setCustomer(subscriber);
                ledgerDtls.setCreditdocid(creditDocumentPojo.getId());
                ledgerDtls.setAmount(creditDocumentPojo.getAmount());
                if (null != creditDocumentPojo.getChequeNo()) {
                    desc = creditDocumentPojo.getPaymode() + " " + creditDocumentPojo.getChequeNo();
                } else {
                    desc = creditDocumentPojo.getPaymode() + " " + creditDocumentPojo.getReferenceno();
                }
                ledgerDtls.setDescription(desc);
                ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_PAYMENT);
                ledgerDtlsList.add(ledgerDtls);
            }
            subscriber.setLedgerDtls(ledgerDtlsList);
        }

        // Set Ledger
        if (null != subscriber.getLedgerDtls() && 0 < subscriber.getLedgerDtls().size()) {
            CustomersPojo finalSubscriber = subscriber;
            List<CustLedgerDtlsPojo> creditList = subscriber.getLedgerDtls().stream().filter(dto -> null != dto.getCustomer() && null != dto.getCreditdocid() && finalSubscriber.getId().equals(dto.getCustomer().getId())).collect(Collectors.toList());

            if (null != creditList && 0 < creditList.size()) {
                CustomerLedgerPojo customerLedger = new CustomerLedgerPojo();
                customerLedger.setCustomer(subscriber);
                customerLedger.setTotaldue(-creditList.stream().mapToDouble(CustLedgerDtlsPojo::getAmount).sum());
                customerLedger.setTotalpaid(creditList.stream().mapToDouble(CustLedgerDtlsPojo::getAmount).sum());
                subscriber.setCustLeger(customerLedger);
            }
        } else {
            CustomerLedgerPojo customerLedger = new CustomerLedgerPojo();
            customerLedger.setCustomer(subscriber);
            customerLedger.setTotaldue(0.0);
            customerLedger.setTotalpaid(0.0);
            subscriber.setCustLeger(customerLedger);
        }

        // Set QuotaDtls
        if (null != subscriber.getPlanMappingList() && 0 < subscriber.getPlanMappingList().size()) {
            List<Long> LCIds = subscriber.getLinkAcceptanceList().stream().map(LinkAcceptanceDTO::getId).collect(Collectors.toList());
            for (int i = 0; i < subscriber.getPlanMappingList().size(); i++) {

                CustPlanMapppingPojo tempPlanMapppingPojo = tempCustomerPojo.getPlanMappingList().get(i);
                CustPlanMapppingPojo planMapppingPojo = subscriber.getPlanMappingList().get(i);
                if (plangroupId != null) {
                    planMapppingPojo.setDiscount(discount);
                    subscriber.setDiscount(discount);
                }
                PostpaidPlan postpaidPlanObj = planService.get(planMapppingPojo.getPlanId(),subscriber.getMvnoId());
                PostpaidPlanPojo plan = postpaidPlanMapper.domainToDTO(postpaidPlanObj, new CycleAvoidingMappingContext());
                Optional<PlanService> services = planServiceRepository.findById(plan.getServiceId());
                if (services.isPresent()) {
                    if (services.get().getExpiry() != null) {
                        if (services.get().getExpiry().equalsIgnoreCase(Constants.AT_MIDNIGHT)) {
//                                    planMapppingPojo.setStartDate(planMapppingPojo.getStartDate().toLocalDate().atTime(LocalTime.MIN).plusSeconds(1));
                            planMapppingPojo.setExpiryDate(planMapppingPojo.getExpiryDate().toLocalDate().atTime(LocalTime.MAX).minusSeconds(1));
                            planMapppingPojo.setEndDate(planMapppingPojo.getEndDate().toLocalDate().atTime(LocalTime.MAX).minusSeconds(1));
                        }
                    }
                }

                //for Trial plan
                if (!CollectionUtils.isEmpty(mapforTrialPlan)) {
                    if (mapforTrialPlan.containsKey(planMapppingPojo.getPlanId())) {
                        planMapppingPojo.setIsinvoicestop(mapforTrialPlan.get(planMapppingPojo.getPlanId()));
                        planMapppingPojo.setIstrialplan(mapforTrialPlan.get(planMapppingPojo.getPlanId()));
                        if (planMapppingPojo.getIsTrialValidityDays() != null && planMapppingPojo.getIsTrialValidityDays() <= trialPlanPeirodConstant && planMapppingPojo.getIstrialplan()) {
                            Integer trialPlanValidity = (planMapppingPojo.getIsTrialValidityDays()).intValue();
                            planMapppingPojo.setEndDate(planMapppingPojo.getStartDate().plusDays(trialPlanValidity - 1));
                            planMapppingPojo.setExpiryDate(planMapppingPojo.getStartDate().plusDays(trialPlanValidity - 1));
                        }
                    }
                }
                QOSPolicyDTO qosPolicy = null;
                if (null != plan && null != plan.getQuotatype()) {
                    if (plan.getQospolicyid() != null) {
                        qosPolicy = qosPolicyService.getEntityForUpdateAndDelete(plan.getQospolicyid(),subscriber.getMvnoId());
                    }
                    if (!plan.getQuotatype().equalsIgnoreCase(CommonConstants.DID_QUOTA_TYPE) && !plan.getQuotatype().equalsIgnoreCase(CommonConstants.INTERCOM_QUOTA_TYPE) && !plan.getQuotatype().equalsIgnoreCase(CommonConstants.VOICE__BOTH_QUOTA_TYPE)) {
                        Double totalQuotaForSeconds = 0.0;
                        Double totalQuotaForKB = 0.0;
                        if (null != plan.getQuotaunittime() && plan.getQuotaunittime().equalsIgnoreCase(Constants.MINUTE)) {
                            totalQuotaForSeconds = plan.getQuotatime() * 60;
                        }
                        if (null != plan.getQuotaunittime() && plan.getQuotaunittime().equalsIgnoreCase(Constants.HOUR)) {
                            totalQuotaForSeconds = plan.getQuotatime() * 60 * 60;
                        }
                        if (null != plan.getQuotaUnit() && plan.getQuotaUnit().equalsIgnoreCase(Constants.MB)) {
                            totalQuotaForKB = (double) plan.getQuota() * 1024;
                        }
                        if (null != plan.getQuotaUnit() && plan.getQuotaUnit().equalsIgnoreCase(Constants.GB)) {
                            totalQuotaForKB = (double) plan.getQuota() * 1024 * 1024;
                        }
                        CustQuotaDtlsPojo quotaDetails = null;
                        if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.TIME_QUOTA_TYPE)) {
                            quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), 0.0, 0.0, null, plan.getQuotatime(), 0.0, plan.getQuotaunittime(), 0.0, 0.0, 0.0, totalQuotaForSeconds, subscriber, plan.getName(), plan.getPlanGroup());
                        }
                        if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.DATA_QUOTA_TYPE)) {
                            quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), (double) plan.getQuota(), 0.0, plan.getQuotaUnit(), 0.0, 0.0, null, totalQuotaForKB, 0.0, 0.0, 0.0, subscriber, plan.getName(), plan.getPlanGroup());
                        }
                        if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.BOTH_QUOTA_TYPE)) {
                            quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), (double) plan.getQuota(), 0.0, plan.getQuotaUnit(), plan.getQuotatime(), 0.0, plan.getQuotaunittime(), totalQuotaForKB, 0.0, 0.0, totalQuotaForSeconds, subscriber, plan.getName(), plan.getPlanGroup());
                        }

                        quotaDetails.setLastQuotaReset(LocalDateTime.now());
                        quotaDetails.setCustQuotaType(CommonConstants.CUST_QUOTA_TYPE.INDIVIDUAL);
                        if (Objects.nonNull(qosPolicy) && qosPolicy.getUpstreamprofileuid() != null) {
                            quotaDetails.setUpstreamprofileuid(qosPolicy.getUpstreamprofileuid());
                            quotaDetails.setDownstreamprofileuid(qosPolicy.getDownstreamprofileuid());
                        }
                        if (subscriber.getParentQuotaType() != null) {
                            quotaDetails.setParentQuotaType(subscriber.getParentQuotaType());
                        }
                        quotaDetails.setLastQuotaReset(LocalDateTime.now());
                        if (plan.isUseQuota()) {
                            quotaDetails.setChunkAvailable(plan.isUseQuota());
                            quotaDetails.setReservedQuotaInPer(plan.getChunk());
                            quotaDetails.setTotalReservedQuota(0.0);
                        }
                        if (plan.getQospolicyid() != null) {
                            qosPolicy = qosPolicyService.getEntityForUpdateAndDelete(plan.getQospolicyid(),subscriber.getMvnoId());
                            if (Objects.nonNull(qosPolicy) && qosPolicy.getDownstreamprofileuid() != null) {
                                quotaDetails.setDownstreamprofileuid(qosPolicy.getDownstreamprofileuid());
                                quotaDetails.setUpstreamprofileuid(qosPolicy.getUpstreamprofileuid());
                            }
                        }
                        planMapppingPojo.setQuotaList(Collections.singletonList(quotaDetails));
                        planMapppingPojo.setPlanValidityDays(customersService.calculatePlanValidityDays(subscriber, postpaidPlanObj, validity, LocalDateTime.now()));
                        planMapppingPojo.setPlangroupid(plangroupId);
                        CustomerServiceMapping mapping = new CustomerServiceMapping(planMapppingPojo);

                        mapping.setServiceId(Long.valueOf(postpaidPlanObj.getServiceId()));
                        mapping.setCustId(subscriber.getId());
//                                mapping = generateConnectionNumber(mapping);
                        Boolean isLCO = subscriber.getLcoId() != null ? true : false;
                        String connectionNo = numberSequenceUtil.getConnectionNumber(isLCO, subscriber.getLcoId(), subscriber.getMvnoId());
                        mapping.setConnectionNo(connectionNo);
                        mapping.setInvoiceType(subscriber.getPlanMappingList().get(i).getInvoiceType());

                        mapping.setDiscountType(tempPlanMapppingPojo.getDiscountType());
                        mapping.setDiscountExpiryDate(tempPlanMapppingPojo.getDiscountExpiryDate());
                        mapping.setDiscount(planMapppingPojo.getDiscount());
                        mapping.setNewDiscountType(tempPlanMapppingPojo.getDiscountType());
                        mapping.setNewDiscountExpiryDate(tempPlanMapppingPojo.getDiscountExpiryDate());
                        mapping.setNewDiscount(planMapppingPojo.getDiscount());

                        //Lease Circuit is hold for this sprint so commented
//                                mapping.setLeaseCircuitId(LCIds.get(i));
                        CustomerServiceMapping savedCustomerServiceMapping = new CustomerServiceMapping(planMapppingPojo);
                        mapping = customersService.generateCircuitNameFormat(mapping);
                        //mapping.setStatus(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION);
                        if (subscriber.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION)) {
                            mapping.setStatus(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION);
                            planMapppingPojo.setCustPlanStatus(CommonConstants.ACTIVE_STATUS);
                                    /*List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingId(mapping.getId());
                                    if(!CollectionUtils.isEmpty(custPlanMapppingList)){
                                        custPlanMapppingList.stream().peek(custPlanMappping -> custPlanMappping.setCustPlanStatus(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION));
                                        custPlanMappingRepository.saveAll(custPlanMapppingList);
                                    }*/
                        } else {
                            mapping.setStatus(CommonConstants.ACTIVE_STATUS);
                            planMapppingPojo.setCustPlanStatus(CommonConstants.ACTIVE_STATUS);
                                    /*List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingId(mapping.getId());
                                    if(!CollectionUtils.isEmpty(custPlanMapppingList)){
                                        custPlanMapppingList.stream().peek(custPlanMappping -> custPlanMappping.setCustPlanStatus(CommonConstants.ACTIVE_STATUS));
                                        custPlanMappingRepository.saveAll(custPlanMapppingList);
                                    }*/
                        }
                        savedCustomerServiceMapping = customerServiceMappingRepository.save(mapping);

                        mapping.setId(savedCustomerServiceMapping.getId());
                        CustServiceMappingMessage message = new CustServiceMappingMessage(mapping);
                        //messageSender.send(message, RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_MAPPING_FOR_INTEGRATION);
                        //messageSender.send(message, RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_MAPPING_KPI);
                        //Customers customers = customersRepository.findById(savedCustomerServiceMapping.getCustId()).orElse(null);

                        // Adding workflow for the customer service add
                        if (subscriber.getStatus().equalsIgnoreCase("NewActivation")) {
                            //  mapping.setStatus("NewActivation");
                            if (mapping != null && mapping.getNextStaff() == null) {
                                StaffUser assignedStaff = new StaffUser();
                                if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
                                    Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(savedCustomer.getMvnoId(), savedCustomer.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, CommonConstants.HIERARCHY_TYPE, false, true, mapping);
                                    int staffId = 0;
                                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                                        staffId = Integer.parseInt(map.get("staffId"));
                                        assignedStaff = staffUserRepository.findById(staffId).get();
                                        mapping.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                        mapping.setNextStaff(staffId);
                                        if (mapping != null) {
                                            String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER_SERVICE_ADD + " with for customer : " + " ' " + savedCustomer.getUsername() + " ' ";
                                            hierarchyService.sendWorkflowAssignActionMessage(assignedStaff.getCountryCode(), assignedStaff.getPhone(), assignedStaff.getEmail(), assignedStaff.getMvnoId(), assignedStaff.getFullName(), action,assignedStaff.getId().longValue());

                                        }
                                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, mapping.getId(), mapping.getConnectionNo(), staffId, assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                                        if (assignedStaff.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                                            if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                                                map.put("tat_id", map.get("current_tat_id"));
                                            tatUtils.saveOrUpdateDataForTatMatrix(map, assignedStaff, mapping.getId(), null);
                                        }
                                    } else {
                                        StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                                        assignedStaff = currentStaff;
                                        mapping.setNextTeamHierarchyMappingId(null);
                                        mapping.setNextStaff(currentStaff.getId());
                                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, mapping.getId(), mapping.getConnectionNo(), staffId, currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                    }
                                } else {
                                    StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                                    assignedStaff = currentStaff;
                                    mapping.setNextTeamHierarchyMappingId(null);
                                    //mapping.setNextStaff(currentStaff.getId());
                                    mapping.setNextStaff(null);
                                    workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, mapping.getId(), mapping.getConnectionNo(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                }
                                savedCustomerServiceMapping = customerServiceMappingRepository.save(mapping);
                            } else {
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Service can not be terminated as it is in change discount approval process.", null);
                            }
                        } else {
                            mapping.setStatus("Active");
                            planMapppingPojo.setCustPlanStatus(CommonConstants.ACTIVE_STATUS);
                                    /*List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingId(mapping.getId());
                                    if(!CollectionUtils.isEmpty(custPlanMapppingList)){
                                        custPlanMapppingList.stream().peek(custPlanMappping -> custPlanMappping.setCustPlanStatus(CommonConstants.ACTIVE_STATUS));
                                        custPlanMappingRepository.saveAll(custPlanMapppingList);
                                    }*/
                            savedCustomerServiceMapping = customerServiceMappingRepository.save(mapping);
                        }
                        planMapppingPojo.setCustServiceMappingId(savedCustomerServiceMapping.getId());
                        if (savedCustomerServiceMapping.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION)) {
                            planMapppingPojo.setIsinvoicestop(false);
                        }

                        CustPlanMappping custPlanMappping = custPlanMappingService.saveCustPlan(custPlanMappingService.convertDTOToDomain(planMapppingPojo), "");
                        /**Enterning cprid in custquotadetaillist**/
                        List<CustQuotaDetails> custQuotaDetailsList = custQuotaRepository.findOnlyByCustId(custPlanMappping.getCustomer().getId());
                        if (!custQuotaDetailsList.isEmpty()) {
                            custQuotaDetailsList.forEach(x -> x.setLastQuotaReset(LocalDateTime.now()));
                            custQuotaDetailsList = custQuotaDetailsList.stream().peek(custQuotaDetails -> custQuotaDetails.setCustPlanMappping(custPlanMappping)).collect(Collectors.toList());
                            custQuotaRepository.saveAll(custQuotaDetailsList);
                        }
                        customersService.saveCustomerChargeHistory(postpaidPlanObj, subscriber, custPlanMappping, plangroupId, true,null, null);

                    } else {
                        CustQuotaDtlsPojo quotaDetails = null;
                        if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.DID_QUOTA_TYPE)) {
                            quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), plan.getQuotadid(), 0.0, 0.0, 0.0, subscriber, plan.getQuotaunitdid(), null);
                        } else if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.INTERCOM_QUOTA_TYPE)) {
                            quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), 0.0, 0.0, plan.getQuotaintercom(), 0.0, subscriber, null, plan.getQuotaunitintercom());
                        } else if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.VOICE__BOTH_QUOTA_TYPE)) {
                            quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), plan.getQuotadid(), 0.0, plan.getQuotaintercom(), 0.0, subscriber, plan.getQuotaunitdid(), plan.getQuotaunitintercom());
                        }

                        quotaDetails.setLastQuotaReset(LocalDateTime.now());
                        quotaDetails.setCustQuotaType(CommonConstants.CUST_QUOTA_TYPE.INDIVIDUAL);
                        if (subscriber.getParentQuotaType() != null) {
                            quotaDetails.setParentQuotaType(subscriber.getParentQuotaType());
                        }
                        if (plan.isUseQuota()) {
                            quotaDetails.setChunkAvailable(plan.isUseQuota());
                            quotaDetails.setReservedQuotaInPer(plan.getChunk());
                            quotaDetails.setTotalReservedQuota(0.0);
                        }

                        planMapppingPojo.setQuotaList(Collections.singletonList(quotaDetails));
                        planMapppingPojo.setPlanValidityDays(customersService.calculatePlanValidityDays(subscriber, postpaidPlanObj, validity, LocalDateTime.now()));
                        planMapppingPojo.setPlangroupid(plangroupId);
                        CustomerServiceMapping mapping = new CustomerServiceMapping(planMapppingPojo);

                        mapping.setDiscountType(tempPlanMapppingPojo.getDiscountType());
                        mapping.setDiscountExpiryDate(tempPlanMapppingPojo.getDiscountExpiryDate());
                        mapping.setDiscount(planMapppingPojo.getDiscount());
                        mapping.setNewDiscountType(tempPlanMapppingPojo.getDiscountType());
                        mapping.setNewDiscountExpiryDate(tempPlanMapppingPojo.getDiscountExpiryDate());
                        mapping.setNewDiscount(planMapppingPojo.getDiscount());

                        mapping.setServiceId(Long.valueOf(postpaidPlanObj.getServiceId()));
                        mapping.setCustId(subscriber.getId());
                        mapping.setInvoiceType(subscriber.getPlanMappingList().get(i).getInvoiceType());
//                                mapping = generateConnectionNumber(mapping);
                        Boolean isLCO = subscriber.getLcoId() != null ? true : false;
                        String connectionNo = numberSequenceUtil.getConnectionNumber(isLCO, subscriber.getLcoId(), subscriber.getMvnoId());
                        mapping.setConnectionNo(connectionNo);
                        CustomerServiceMapping savedCustomerServiceMapping = new CustomerServiceMapping(planMapppingPojo);
                        mapping = customersService.generateCircuitNameFormat(mapping);
                        savedCustomerServiceMapping = customerServiceMappingRepository.save(mapping);
                        mapping = savedCustomerServiceMapping;
                        mapping.setId(savedCustomerServiceMapping.getId());

                        //Customers customers = customersRepository.findById(mapping.getCustId()).orElse(null);
                        // Adding workflow for the customer service add
                        if (subscriber.getStatus().equalsIgnoreCase("NewActivation")) {
                            mapping.setStatus("NewActivation");
                            if (mapping != null && mapping.getNextStaff() == null) {
                                StaffUser assignedStaff = new StaffUser();
                                if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
                                    Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(savedCustomer.getMvnoId(), savedCustomer.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, CommonConstants.HIERARCHY_TYPE, false, true, mapping);
                                    int staffId = 0;
                                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                                        staffId = Integer.parseInt(map.get("staffId"));
                                        assignedStaff = staffUserRepository.findById(staffId).get();
                                        mapping.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                        mapping.setNextStaff(staffId);
                                        if (mapping != null) {
                                            String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER_SERVICE_ADD + " with for customer : " + " ' " + savedCustomer.getUsername() + " ' ";
                                            hierarchyService.sendWorkflowAssignActionMessage(assignedStaff.getCountryCode(), assignedStaff.getPhone(), assignedStaff.getEmail(), assignedStaff.getMvnoId(), assignedStaff.getFullName(), action,assignedStaff.getId().longValue());

                                        }
                                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, mapping.getId(), mapping.getConnectionNo(), staffId, assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                                        if (assignedStaff.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                                            if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                                                map.put("tat_id", map.get("current_tat_id"));
                                            tatUtils.saveOrUpdateDataForTatMatrix(map, assignedStaff, mapping.getId(), null);
                                        }
                                    } else {
                                        StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                                        assignedStaff = currentStaff;
                                        mapping.setNextTeamHierarchyMappingId(null);
                                        mapping.setNextStaff(currentStaff.getId());
                                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, mapping.getId(), mapping.getConnectionNo(), staffId, currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                    }
                                } else {
                                    StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                                    assignedStaff = currentStaff;
                                    mapping.setNextTeamHierarchyMappingId(null);
                                    mapping.setNextStaff(currentStaff.getId());
                                    workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, mapping.getId(), mapping.getConnectionNo(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                }
                                savedCustomerServiceMapping = customerServiceMappingRepository.save(mapping);
                            } else {
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Service can not be terminated as it is in change discount approval process.", null);
                            }
                        } else {
                            mapping.setStatus("Active");
                            planMapppingPojo.setCustPlanStatus(CommonConstants.ACTIVE_STATUS);
                                    /*List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingId(mapping.getId());
                                    if(!CollectionUtils.isEmpty(custPlanMapppingList)){
                                        custPlanMapppingList.stream().peek(custPlanMappping -> custPlanMappping.setCustPlanStatus(CommonConstants.ACTIVE_STATUS));
                                        custPlanMappingRepository.saveAll(custPlanMapppingList);
                                    }*/
                            savedCustomerServiceMapping = customerServiceMappingRepository.save(mapping);
                        }


                        planMapppingPojo.setCustServiceMappingId(savedCustomerServiceMapping.getId());
                        if (savedCustomerServiceMapping.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION)) {
                            planMapppingPojo.setIsinvoicestop(true);
                        }
                        CustPlanMappping custPlanMappping = custPlanMappingService.saveCustPlan(custPlanMappingService.convertDTOToDomain(planMapppingPojo), "");
                        custPlanMappping.setStatus("1");
                        customersService.saveCustomerChargeHistory(postpaidPlanObj, subscriber, custPlanMappping, plangroupId, true,null, null);
                    }
                }
            }
        }
        if (ValidateCrudTransactionData.validateStringTypeFieldValue(subscriber.getAadhar())) {
            CustomerDocDetailsDTO docDetailsDTO = new CustomerDocDetailsDTO();
            docDetailsDTO.setCustomer(subscriber);
            docDetailsDTO.setCustId(subscriber.getId());
            docDetailsDTO.setDocType(DocumentConstants.PROOF_OF_ADDRESS);
            docDetailsDTO.setDocStatus(DocumentConstants.VERIFICATION_PENDING);
            docDetailsDTO.setMode(DocumentConstants.ONLINE);
            docDetailsDTO.setDocSubType(DocumentConstants.AADHAR_CARD);
            customerDocDetailsService.saveEntity(docDetailsDTO);
        }
        if (ValidateCrudTransactionData.validateStringTypeFieldValue(subscriber.getPan())) {
            CustomerDocDetailsDTO docDetailsDTO = new CustomerDocDetailsDTO();
            docDetailsDTO.setCustomer(subscriber);
            docDetailsDTO.setCustId(subscriber.getId());
            docDetailsDTO.setDocType(DocumentConstants.PROOF_OF_IDENTITY);
            docDetailsDTO.setDocStatus(DocumentConstants.VERIFICATION_PENDING);
            docDetailsDTO.setMode(DocumentConstants.ONLINE);
            docDetailsDTO.setDocSubType(DocumentConstants.PAN_CARD);
            customerDocDetailsService.saveEntity(docDetailsDTO);
        }
        if (ValidateCrudTransactionData.validateStringTypeFieldValue(subscriber.getGst())) {
            CustomerDocDetailsDTO docDetailsDTO = new CustomerDocDetailsDTO();
            docDetailsDTO.setCustomer(subscriber);
            docDetailsDTO.setCustId(subscriber.getId());
            docDetailsDTO.setDocType(DocumentConstants.PROOF_OF_IDENTITY);
            docDetailsDTO.setDocStatus(DocumentConstants.VERIFICATION_PENDING);
            docDetailsDTO.setMode(DocumentConstants.ONLINE);
            docDetailsDTO.setDocSubType(DocumentConstants.GST_NUMBER);
            customerDocDetailsService.saveEntity(docDetailsDTO);
        }
        generateCommunicationHelper(subscriber, planName);
        return subscriber;
    }

    public CustomersPojo updateCustomerCreations(CustomersPojo subscriber, Customers parentCustomer, LocalDateTime currentTime, Long validity) throws Exception {

        Customers customers = customersRepository.findById(subscriber.getId()).get();
        String planName = "";
        // TODO: pass mvnoID manually 6/5/2025
        if (subscriber.getMvnoId() != null) {
            // TODO: pass mvnoID manually 6/5/2025
            subscriber.setMvnoId(subscriber.getMvnoId());
        }
        if (subscriber.getParentCustomerId() != null) {
            parentCustomer = customersRepository.getOne(subscriber.getParentCustomerId());
            if (parentCustomer != null) {
                subscriber.setParentCustomers(customerMapper.domainToDTO(parentCustomer, new CycleAvoidingMappingContext()));
            }
        }
        subscriber.setNextTeamHierarchyMapping(customers.getNextTeamHierarchyMapping());
        subscriber.setNextBillDate(customers.getNextBillDate());
        subscriber.setLastBillDate(customers.getLastBillDate());
        // ApplyCharge With tax
        if (null != subscriber.getOverChargeList() && subscriber.getOverChargeList().size() > 0) {
            List<CustChargeDetailsPojo> custChargeDetailsPojoList = custChargeService.calDirectCharge(subscriber);
            if (custChargeDetailsPojoList.size() > 0) {
                subscriber.setOverChargeList(custChargeDetailsPojoList);
            }
        }

        CustomerLedger ledger = custLegerService.getCustomerLeger(subscriber.getId());
        CustomerLedgerPojo ledgerPojo = customerLedgerMapper.domainToDTO(ledger, new CycleAvoidingMappingContext());
        subscriber.setCustLeger(ledgerPojo);
        List<CreditDocumentPojo> creditDocumentList = new ArrayList<>();
        List<CreditDocument> creditDoclist = creditDocService.getAllByCustomer_IdOrderByIdDesc(subscriber.getId());
        for (CreditDocument c : creditDoclist) {
            CreditDocumentPojo creditDocumentPojo = new CreditDocumentPojo();
            creditDocumentPojo.setId(c.getId());
            creditDocumentPojo.setAmount(c.getAmount());
            creditDocumentPojo.setPaymentdate(c.getPaymentdate());
            creditDocumentPojo.setCustomer(subscriber);
            creditDocumentPojo.setPaymode(c.getPaymode());
            creditDocumentPojo.setStatus(c.getStatus());
            creditDocumentPojo.setApproverid(c.getApproverid());
            creditDocumentPojo.setCreatedate(c.getCreatedate());
            creditDocumentPojo.setCreatedById(c.getCreatedById());
            creditDocumentPojo.setLastModifiedById(c.getLastModifiedById());
            creditDocumentPojo.setReferenceno(c.getReferenceno());
            creditDocumentPojo.setXmldocument(c.getXmldocument());
            creditDocumentPojo.setTdsflag(c.getTdsflag());
            creditDocumentPojo.setCreatedByName(c.getCreatedByName());
            creditDocumentPojo.setLcoId(c.getLcoid());
            creditDocumentList.add(creditDocumentPojo);
        }
        subscriber.setCreditDocuments(creditDocumentList);
        subscriber.setCustname(subscriber.getFirstname());
        if (null != subscriber && null != subscriber.getPlanMappingList() && 0 < subscriber.getPlanMappingList().size()) {
            for (int i = 0; i < subscriber.getPlanMappingList().size(); i++) {
                CustPlanMapppingPojo mapping = subscriber.getPlanMappingList().get(i);
                if (mapping.getId() != null) {
                    CustPlanMappping entityById = custPlanMappingService.getEntityById(mapping.getId());
                    mapping.setId(entityById.getId());
                    mapping.setPurchaseFrom(entityById.getPurchaseFrom());
                    mapping.setPurchaseType(entityById.getPurchaseType());
                    mapping.setStartDate(entityById.getStartDate());
                    mapping.setExpiryDate(entityById.getExpiryDate());
                    mapping.setOfferPrice(entityById.getOfferPrice());
                    mapping.setEndDate(entityById.getEndDate());
                    mapping.setStatus(entityById.getStatus());
                    mapping.setPlanValidityDays(customersService.calculatePlanValidityDays(subscriber, planService.get(entityById.getPlanId(),subscriber.getMvnoId()), validity, LocalDateTime.now()));

                } else {
                    mapping = customersService.getSinglePlan(subscriber, i);
                    // Set Expiry Date of Plan
                    if (mapping.getPlanId() != null) {
                        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                            mapping.setPurchaseFrom(SubscriberConstants.PURCHASE_FROM_ADMIN);
                        } else {
                            mapping.setPurchaseFrom(SubscriberConstants.PURCHASE_FROM_PARTNER);
                        }
                        mapping.setPurchaseType(SubscriberConstants.PURCHASE_TYPE_NEW);
                        PostpaidPlan plan = planService.get(mapping.getPlanId(),subscriber.getMvnoId());
                        planName = plan.getName();
                        TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO(plan.getId(), subscriber.getAddressList().stream().filter(data -> data.getAddressType().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PRESENT)).findAny().orElse(null).getStateId(), null, null);
                        Double taxAmount = taxService.taxCalculationByPlan(taxDetailCountReqDTO, plan.getChargeList());
                        if (null != plan && mapping.getExpiryDate() == null) {
                            mapping.setStartDate(currentTime);
                            LocalDateTime expDate = LocalDateTime.now().plusDays(plan.getValidity().longValue());
                            if (!plan.getUnitsOfValidity().equalsIgnoreCase("Hours")) {
                                expDate = LocalDateTime.of(expDate.toLocalDate(), LocalTime.of(23, 59, 59));
                            }
                            mapping.setExpiryDate(expDate);
                            mapping.setOfferPrice(plan.getOfferprice());
                            mapping.setPlanValidityDays(customersService.calculatePlanValidityDays(subscriber, plan, validity, LocalDateTime.now()));
                            mapping.setTaxAmount(taxAmount);
                            if (null != plan.getQospolicy()) {
                                mapping.setQospolicyId(plan.getQospolicy().getId());
                            }
                        }
                    }
                }
            }
        }
        if (subscriber.getStatus().equalsIgnoreCase("NewActivation")) {
            if (getLoggedInUser().getLco() != null && getLoggedInUser().getLco()) {
                subscriber.setLcoId(getLoggedInUser().getPartnerId());
            } else {
                subscriber.setLcoId(null);
            }

            StaffUser assignedUser;
            if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
                Map<String, String> map1 = hierarchyService.getTeamForNextApproveForAuto(subscriber.getMvnoId(), subscriber.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, CommonConstants.HIERARCHY_TYPE, false, true, subscriber);
                int staffId = 0;
                if (map1.containsKey("staffId") && map1.containsKey("nextTatMappingId")) {
                    staffId = Integer.parseInt(map1.get("staffId"));
                    StaffUser staffUser = staffUserRepository.findById(staffId).get();
                    assignedUser = staffUser;
                    subscriber.setNextTeamHierarchyMapping(Integer.valueOf(map1.get("nextTatMappingId")));
                    subscriber.setCurrentAssigneeId(staffId);
                    if (subscriber != null) {
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER + " with username : " + " ' " + subscriber.getUsername() + " '";
                        hierarchyService.sendWorkflowAssignActionMessage(staffUser.getCountryCode(), staffUser.getPhone(), staffUser.getEmail(), staffUser.getMvnoId(), staffUser.getFullName(), action,staffUser.getId().longValue());
                    }
                    try {
                        NotificationDTO notificationDTO = notificationService.findByName("Registration");
                        if (null != notificationDTO)
                            smsSchedulerService.sendSMS(Collections.singletonList(subscriber), notificationDTO.getId());
                    } catch (Exception e) {
                        ApplicationLogger.logger.error("Unable to Save Subscriber rsponse:{}exception{}", APIConstants.FAIL, e.getStackTrace());
                        e.printStackTrace();
                    }
                    workflowAuditService.saveAudit(map1.containsKey("eventId") ? Integer.parseInt(map1.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CAF, subscriber.getId(), subscriber.getUsername(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());

                } else {
                    StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                    assignedUser = currentStaff;
                    subscriber.setCurrentAssigneeId(currentStaff.getId());
                    workflowAuditService.saveAudit(map1.containsKey("eventId") ? Integer.parseInt(map1.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CAF, subscriber.getId(), subscriber.getUsername(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                }
                //TAT functionality
                if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map1)) {
                    if (map1.get("current_tat_id") != null && map1.get("current_tat_id") != "null")
                        map1.put("tat_id", map1.get("current_tat_id"));
                    tatUtils.saveOrUpdateDataForTatMatrix(map1, assignedUser, subscriber.getId(), null);
                }

            } else {
                subscriber.setId(subscriber.getId());
                subscriber.setCurrentAssigneeId(getLoggedInUserId());
                StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CAF, subscriber.getId(), subscriber.getUsername(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
            }
        }
        generateCommunicationHelper(subscriber, planName);
        return subscriber;
    }

    @Override
    protected JpaRepository<Customers, Integer> getRepository() {
        return customersRepository;
    }
}
