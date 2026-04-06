package com.adopt.apigw.modules.subscriber.service;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.AutoRenewalPreferenceMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.*;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.repository.CustomRepository;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.devCode.ToolService;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.mapper.postpaid.CustQuotaDtlsMapper;
import com.adopt.apigw.mapper.postpaid.CustomerLedgerMapper;
import com.adopt.apigw.mapper.postpaid.StaffUserMapper;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Alert.smsScheduler.service.SmsSchedulerService;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.Area.repository.AreaRepository;
import com.adopt.apigw.modules.Area.service.AreaService;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.Branch.repository.BranchRepository;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.BusinessUnit.repository.BusinessUnitRepository;
import com.adopt.apigw.modules.CommonList.model.CommonListDTO;
import com.adopt.apigw.modules.CommonList.repository.CommonListRepository;
import com.adopt.apigw.modules.CommonList.service.CommonListService;
import com.adopt.apigw.modules.CommonList.utils.TypeConstants;
import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;
import com.adopt.apigw.modules.Communication.Helper.CommunicationHelper;
import com.adopt.apigw.modules.CustomerDBR.repository.CustomerDBRRepository;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingService;
import com.adopt.apigw.modules.Matrix.domain.Matrix;
import com.adopt.apigw.modules.Matrix.domain.TatMatrixWorkFlowDetails;
import com.adopt.apigw.modules.Matrix.repository.MatrixRepository;
import com.adopt.apigw.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.NetworkDevices.mapper.NetworkDeviceMapper;
import com.adopt.apigw.modules.NetworkDevices.repository.NetworkDeviceRepository;
import com.adopt.apigw.modules.NetworkDevices.service.SlotService.OLTSlotService;
import com.adopt.apigw.modules.NetworkDevices.service.SlotService.OltPortService;
import com.adopt.apigw.modules.Notification.model.NotificationDTO;
import com.adopt.apigw.modules.Notification.service.NotificationService;
import com.adopt.apigw.modules.PartnerLedger.repository.PartnerLedgerDetailsRepository;
import com.adopt.apigw.modules.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.Pincode.repository.PincodeRepository;
import com.adopt.apigw.modules.Pincode.service.PincodeService;
import com.adopt.apigw.modules.PlanQosMapping.PlanQosMappingPojo;
import com.adopt.apigw.modules.RvenueClient.RevenueClient;
import com.adopt.apigw.modules.ServiceArea.SubscriberMapper;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.mapper.ServiceAreaMapper;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.SubscriberUpdates.Utils.SubscriberUpdateUtils;
import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateConstant;
import com.adopt.apigw.modules.SubscriberUpdates.model.SubscriberUpdateDTO;
import com.adopt.apigw.modules.SubscriberUpdates.service.SubscriberUpdateService;
import com.adopt.apigw.modules.Teams.domain.Hierarchy;
import com.adopt.apigw.modules.Teams.repository.HierarchyRepository;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.TimeBasePolicy.domain.TimeBasePolicy;
import com.adopt.apigw.modules.TimeBasePolicy.repository.TimeBasePolicyRepository;
import com.adopt.apigw.modules.custAccountProfile.CustAccountProfile;
import com.adopt.apigw.modules.custAccountProfile.CustAccountProfileRepository;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
import com.adopt.apigw.modules.integrations.NexgeVoice.service.NexgeVoiceProvisionService;
import com.adopt.apigw.modules.ippool.model.IPAllocationDTO;
import com.adopt.apigw.modules.ippool.model.IPPoolDTO;
import com.adopt.apigw.modules.ippool.model.IPPoolDtlsDTO;
import com.adopt.apigw.modules.ippool.service.IPAllocationService;
import com.adopt.apigw.modules.ippool.service.IPPoolDtlsService;
import com.adopt.apigw.modules.ippool.service.IPPoolService;
import com.adopt.apigw.modules.ippool.utils.IpConfigConstant;
import com.adopt.apigw.modules.payments.repository.ServiceAuditRepository;
import com.adopt.apigw.modules.linkacceptance.model.LinkAcceptanceDTO;
import com.adopt.apigw.modules.planUpdate.repository.CustomerPackageRepository;
import com.adopt.apigw.modules.planUpdate.service.QuotaDtlsService;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;
import com.adopt.apigw.modules.qosPolicy.model.QOSPolicyDTO;
import com.adopt.apigw.modules.qosPolicy.repository.QOSPolicyRepository;
import com.adopt.apigw.modules.qosPolicy.service.QOSPolicyService;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.modules.subscriber.Domain.QServiceAudit;
import com.adopt.apigw.modules.subscriber.Domain.ServiceAudit;
import com.adopt.apigw.modules.subscriber.controller.CustPlanMappingDropdownPojo;
import com.adopt.apigw.modules.subscriber.mapper.ServiceAuditMapper;
import com.adopt.apigw.modules.subscriber.model.*;
import com.adopt.apigw.modules.subscriber.model.Constants;
import com.adopt.apigw.modules.subscriber.queryScript.PlanQueryScript;
import com.adopt.apigw.nepaliCalendarUtils.model.EnglishDateDTO;
import com.adopt.apigw.nepaliCalendarUtils.model.NepaliDateDTO;
import com.adopt.apigw.nepaliCalendarUtils.service.DateConverterService;
import com.adopt.apigw.pojo.ClientServicePojo;
import com.adopt.apigw.pojo.ExtendPlanValidity;
import com.adopt.apigw.pojo.FlagDTO;
import com.adopt.apigw.pojo.NewCustPojos.NewCustPlanMappingPojo;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.pojo.customer.plans.ExtendPlanValidityInBulk;
import com.adopt.apigw.pojo.customer.plans.PromiseToPayPojo;
import com.adopt.apigw.pojo.customer.plans.PromiseToPayPojoInBulk;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.*;
import com.adopt.apigw.rabbitMq.message.ServiceTerminationMessage;
import com.adopt.apigw.repository.common.*;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.CacheService;
import com.adopt.apigw.service.common.*;
import com.adopt.apigw.service.postpaid.*;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.*;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static org.apache.commons.collections.CollectionUtils.collect;

@Service
public class SubscriberService extends AbstractService<Customers, CustomersPojo, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberService.class);

    private static final String MODULE = " [SubscriberService()] ";
    private final CommonListRepository commonListRepository;
    private final CustChargeDetailsRepository custChargeDetailsRepository;
    private final CustPlanMapppingRepository custPlanMapppingRepository;
    private final StaffUserRepository staffUserRepository;
    @Autowired
    PartnerLedgerDetailsRepository ledgerDetailsRepository;
    @Autowired
    PartnerCommissionService commissionService;
    @Autowired
    PlanGroupService planGroupService;
    @Autowired
    NotificationTemplateRepository templateRepository;
    @Autowired
    CustomerServiceMappingRepository customerServiceMappingRepository;
    @Autowired
    CustPlanMappingRepository custPlanMappingRepo;
    @Autowired
    DiscountAuditRepocitory discountAuditRepocitory;
    @Autowired
    CustAccountProfileRepository custAccountProfileRepository;
    @Autowired
    ServiceAuditMapper serviceAuditMapper;
    @Lazy
    @Autowired
    CustomerInventoryMappingService customerInventoryMappingService;
    @Autowired
    ClientServiceSrv clientService;
    @Autowired
    ChargeRepository chargeRepository;
    @Autowired
    TatUtils tatUtils;
    @Autowired
    HierarchyService hierarchyService;
    @Autowired
    HierarchyRepository hierarchyRepository;
    @Autowired
    MatrixRepository matrixRepository;
    @Autowired
    WorkflowAuditService workflowAuditService;
    @Autowired
    StaffUserService staffUserService;
    @Autowired
    BusinessUnitRepository businessUnitRepository;
    @Autowired
    StaffUserMapper staffUserMapper;
    @Autowired
    PartnerService partnerService;
    @Autowired
    PlanGroupMappingRepository planGroupMappingService;
    @Autowired
    QOSPolicyRepository qosPolicyRepository;
    @Autowired
    TimeBasePolicyRepository timeBasePolicyRepository;
    @Autowired
    PostpaidPlanService postpaidPlanService;
    @Autowired
    BranchRepository branchRepository;
    @Autowired
    PartnerRepository partnerRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    private CustomerDBRRepository customerDBRRepository;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private CustomersService customersService;
    @Autowired
    private QOSPolicyService qosPolicyService;
    @Autowired
    private CustPlanMappingService custPlanMappingService;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomRepository<CustomerPlansModel> customRepository;
    @Autowired
    private CustomRepository<CaseCountModel> caseCountCustomRepository;
    @Autowired
    private CustomRepository<CustomCustChargeDetailsPojo> chargeQueryRepository;
    @Autowired
    private SubscriberMapper subscriberMapper;
    @Autowired
    private CustQuotaService custQuotaService;
    @Autowired
    private CommonListService commonListService;
    @Autowired
    private CustomerLedgerService customerLedgerService;
    @Autowired
    private CustomerLedgerDtlsService customerLedgerDtlsService;
    @Autowired
    private CustChargeService custChargeService;
    @Autowired
    private CustMacMapppingService custMacMapppingService;
    @Autowired
    private ServiceAreaService serviceAreaService;
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;
    @Autowired
    private CustomerAddressService customerAddressService;
    @Autowired
    private CityService cityService;
    @Autowired
    private StateService stateService;
    @Autowired
    private CountryService countryService;
    @Autowired
    private CustChargeDetailMapper custChargeDetailMapper;
    @Autowired
    private IPPoolService ipPoolService;
    @Autowired
    private SubscriberUpdateService subscriberUpdateService;
    @Autowired
    private CreditDocService creditDocService;
    @Autowired
    private NetworkDeviceMapper networkDeviceMapper;
    @Autowired
    private OLTSlotService oltSlotService;
    @Autowired
    private OltPortService oltPortService;
    @Autowired
    private NetworkDeviceRepository networkDeviceRepository;
    @Autowired
    private ServiceAreaRepository serviceAreaRepository;
    @Autowired
    private ChargeService chargeService;
    @Autowired
    private PostpaidPlanService planService;
    @Autowired
    private PostpaidPlanMapper postpaidPlanMapper;
    @Autowired
    private IPPoolDtlsService ipPoolDtlsService;
    @Autowired
    private IPAllocationService ipAllocationService;
    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    private NexgeVoiceProvisionService nexgeVoiceProvisionService;
    @Autowired
    private TaxService taxService;
    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    private BillRunService billRunService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private PincodeService pincodeService;
    @Autowired
    private ClientServiceSrv clientServiceSrv;
    @Autowired
    private PurchasedHistoryMapper purchasedHistoryMapper;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private CustQuotaDtlsMapper custQuotaDtlsMapper;
    @Autowired
    private DateConverterService dateConverterService;
    @Autowired
    private PlanServiceService planServiceService;
    @Autowired
    private DbrService dbrService;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private DebitDocRepository debitDocRepository;
    @Autowired
    private PlanServiceRepository planServiceRepository;
    @Autowired
    private PlanGroupRepository planGroupRepository;
    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;
    @Autowired
    private ClientServiceRepository clientServiceRepository;
    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;
    @Autowired
    private CreditDocRepository creditDocRepository;
    @Autowired
    private DebitDocService debitDocService;
    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;
    @Autowired
    private CustQuotaRepository custQuotaRepository;
    @Autowired
    private EzBillServiceUtility ezBillServiceUtility;
    @Autowired
    private PlanCasMappingRepository planCasMappingRepocitory;
    @Autowired
    private ToolService toolService;
    @Autowired
    private ServiceAuditRepository serviceAuditRepository;
    @Autowired
    private AcctNumCreationService acctNumCreationService;
    @Autowired
    private TrialDebitDocRepository trialDebitDocumentRepository;
    @Autowired
    private CreateDataSharedService createDataSharedService;
    @Autowired
    private NumberSequenceUtil numberSequenceUtil;
    @Autowired
    private CustPlanExtendValidityMappingRepository custPlanExtendValidityMappingRepository;
    @Autowired
    private SmsSchedulerService smsSchedulerService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CustomerLedgerMapper customerLedgerMapper;
    @Autowired
    private CustomerLedgerService custLegerService;
    @Autowired
    private CustomerDocDetailsService customerDocDetailsService;
    @Autowired
    private TatMatrixWorkFlowDetailsRepo tatMatrixWorkFlowDetailsRepo;
    @Autowired
    private MessagesPropertyConfig messagesProperty;
    @Autowired
    private PincodeRepository pincodeRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private ShorterRepository shorterRepository;

    @Autowired
    private RevenueClient revenueClient;

    @Autowired
    private CustomerPackageRepository customerPackageRepository;

    @Autowired
    private VasPlanRepository vasPlanRepository;

    @Autowired
    private CacheService cacheService;
    @Value("${auto-assign-username: superadmin}")
    private String autoAssignUsername;

    @Value("${auto-assign-password: superadmin@2021@SUPERADMIN}")
    private String autoAssignPasswod;
    @Autowired
    private VasPlanService vasPlanService;

    public SubscriberService(CommonListRepository commonListRepository, CustChargeDetailsRepository custChargeDetailsRepository, CustPlanMapppingRepository custPlanMapppingRepository, StaffUserRepository staffUserRepository) {
        sortColMap.put("id", "custid");
        this.commonListRepository = commonListRepository;
        this.custChargeDetailsRepository = custChargeDetailsRepository;
        this.staffUserRepository = staffUserRepository;
        this.custPlanMapppingRepository = custPlanMapppingRepository;
    }

    public static void main(String[] args) {
        LocalDateTime todaysDate = LocalDateTime.now();
        LocalDateTime minusOneDay = LocalDateTime.now().minusDays(0);

    }

    @Override
    protected JpaRepository<Customers, Integer> getRepository() {
        return customersRepository;
    }

    public List<PurchasedHistoryDTO> getByPurchaseHistoryCustId(Integer custId) {
        List<CustPlanMapppingPojo> custPlanMapppingPojoList = custPlanMappingService.findAllByCustomersId(custId);
        if (null != custPlanMapppingPojoList && 0 < custPlanMapppingPojoList.size()) {
            List<CustPlanMappping> planMapppingList = custPlanMapppingPojoList.stream().map(pojo -> customerMapper.mapCustPlanMapPojoToCustPlanMap(pojo, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            return planMapppingList.stream().map(data -> purchasedHistoryMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public CustomersBasicDetailsPojo getBasicDetailsOfSubscriber(Customers customers) throws NoSuchFieldException {
        String SUBMODULE = MODULE + " [getBasicDetailsOfSubscriber()] ";
        try {
            return subscriberMapper.domainToDTO(customers, new CycleAvoidingMappingContext());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public QosPolicyDetailsModel getQosPolicyDetails(Integer custId) throws Exception {
        String SUBMODULE = MODULE + " [getQosPolicyDetails()] ";
        QosPolicyDetailsModel detailsModel = new QosPolicyDetailsModel();
        try {
            List<CustomerPlansModel> plansModelList = getActivePlanList(custId, false);
            if (null != plansModelList && 0 < plansModelList.size()) {
                detailsModel.setPlanList(plansModelList.stream().filter(dto -> null != dto.getService() && dto.getService().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA)).collect(Collectors.toList()));
            }
            detailsModel.setQosPolicyList(qosPolicyService.getAllEntities(getMvnoIdFromCurrentStaff(custId)));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return detailsModel;
    }

    public CustomerLocationDTO getLocationDetail(Customers customers) throws Exception {
        CustomerLocationDTO customerLocationDTO = new CustomerLocationDTO();
        customerLocationDTO.setCustId(customers.getId());
        customerLocationDTO.setLatitude(customers.getLatitude());
        customerLocationDTO.setLongitude(customers.getLongitude());
        customerLocationDTO.setUrl(customers.getUrl());
        customerLocationDTO.setGis_code(customers.getGis_code());
        return customerLocationDTO;
    }

    public CustomersBasicDetailsPojo updateLocationDetail(CustomerLocationDTO dto, Customers customers) throws Exception {
        String oldValue = SubscriberUpdateUtils.customString(Arrays.asList(customers.getLatitude(), customers.getLongitude(), customers.getUrl(), customers.getGis_code()));
        customers.setId(dto.getCustId());
        customers.setLatitude(dto.getLatitude());
        customers.setLongitude(dto.getLongitude());
        customers.setUrl(dto.getUrl());
        customers.setGis_code(dto.getGis_code());
        Customers customers1 = customersService.update(customers);
        String newValue = SubscriberUpdateUtils.customString(Arrays.asList(customers1.getLatitude(), customers1.getLongitude(), customers1.getUrl(), customers1.getGis_code()));
        SubscriberUpdateUtils.updateSubscriber(oldValue, newValue, UpdateConstant.UPDATE_LOCATION_DETAIL, customers1, dto.getRemarks(), null);
        return getBasicDetailsOfSubscriber(customers1);
    }

    public CustomersBasicDetailsPojo changeQos(ChangeQosRequestDTO requestDTO, Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [changeQos()] ";
        try {
            if (requestDTO.getPlanId() != null) {
                CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(requestDTO.getCustPackRelId());
                PostpaidPlan postpaidPlan = planService.getPostpaidPlanForEdit(custPlanMappping.getPlanId(), customers.getMvnoId());
                CustPlanMapppingPojo custPlanMapppingPojo = customerMapper.mapCustPlanMapToCustPlanMapPojo(custPlanMappping, new CycleAvoidingMappingContext());
                if (custPlanMapppingPojo != null) {
                    QOSPolicyDTO oldQosPolicyDTO = qosPolicyService.getEntityById(custPlanMapppingPojo.getQospolicyId(), customers.getMvnoId());
                    custPlanMapppingPojo.setQospolicyId(requestDTO.getQosPolicyId());
                    custPlanMapppingPojo.setCustid(requestDTO.getCustId());
                    custPlanMapppingPojo.setPlanId(requestDTO.getPlanId());
                    custPlanMapppingPojo.setCustomer(customerMapper.domainToDTO(customersRepository.findById(requestDTO.getCustId()).get(), new CycleAvoidingMappingContext()));
                    custPlanMappingService.update(customerMapper.mapCustPlanMapPojoToCustPlanMap(custPlanMapppingPojo, new CycleAvoidingMappingContext()), "");

                    //Subscriber Update
                    QOSPolicyDTO newQosPolicyDTO = qosPolicyService.getEntityById(requestDTO.getQosPolicyId(), customers.getMvnoId());
                    String oldVal = null != oldQosPolicyDTO ? oldQosPolicyDTO.getName() : "-";
                    String newVal = null != newQosPolicyDTO ? newQosPolicyDTO.getName() : "-";
                    SubscriberUpdateUtils.updateSubscriber(oldVal, newVal, UpdateConstant.UPDATE_QOS, customers, requestDTO.getRemark(), postpaidPlan.getName());
                }
//                List<CustPlanMapppingPojo> planMapppingPojoList = custPlanMappingService.findAllByCustomersId(requestDTO.getCustId());
//                if (null != planMapppingPojoList && 0 < planMapppingPojoList.size()) {
//                    planMapppingPojoList = planMapppingPojoList.stream().filter(dto -> null != dto.getPlanId()
//                            && requestDTO.getPlanId().equals(dto.getPlanId())).collect(Collectors.toList());
//                    if (null != planMapppingPojoList && 0 < planMapppingPojoList.size()) {
//                        planMapppingPojoList.forEach(dto -> {
//
//                        /*try {
//                            this.subscriberUpdateService.updateSubscriber("Subscriber","ChangeQos",requestDTO,dto,customerMapper.dtoToDomain(dto.getCustomer(),new CycleAvoidingMappingContext()));
//                        } catch (JsonProcessingException e) {
//                            e.printStackTrace();
//                        }*/
//                        });
//                    }
//                }
                return getBasicDetailsOfSubscriber(customersRepository.findById(requestDTO.getCustId()).get());
            } else {
                throw new RuntimeException("Plan id is required");
            }
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        }
    }

    public CustomersBasicDetailsPojo changeVoiceDetails(CustomerVoiceDetailsDTO pojo) throws Exception {
        String SUBMODULE = MODULE + " [ChangeVoiceDetails] ";
        try {
            Customers customers = customersRepository.findById(pojo.getId()).get();
            Customers oldCustomerData = new Customers(customers);
            String oldVal = SubscriberUpdateUtils.customString(Arrays.asList(customers.getDidno(), customers.getIntercomno(), customers.getChilddidno()));
            customers.setId(pojo.getId());
            customers.setVoicesrvtype(pojo.getVoicesrvtype());
            if (pojo.getVoicesrvtype().equals(SubscriberConstants.PHONE_LINE)) {
                customers.setDidno(pojo.getDidno());
                customers.setIntercomno(null);
                customers.setIntercomgrp(null);
                customers.setChilddidno(null);
            }
            if (pojo.getVoicesrvtype().equals(SubscriberConstants.SHIP_TRUNK)) {
                customers.setDidno(pojo.getDidno());
                customers.setChilddidno(pojo.getChilddidno());
                customers.setIntercomno(null);
                customers.setIntercomgrp(null);
            }
            if (pojo.getVoicesrvtype().equals(SubscriberConstants.INTERCOM)) {
                customers.setIntercomno(pojo.getIntercomno());
                customers.setIntercomgrp(pojo.getIntercomgrp());
                customers.setDidno(null);
                customers.setChilddidno(null);
            }
            customers = customersService.update(customers);
            NexgeVoiceProvisionService nexgeVoiceHandlerService = SpringContext.getBean(NexgeVoiceProvisionService.class);
            if (!customers.getVoiceprovision()) {
                Boolean result = nexgeVoiceHandlerService.performCustomerProvision(customers);
                customers.setVoiceprovision(result);
                customers = customersService.update(customers);
            } else {
                oldCustomerData.setId(customers.getId());
                oldCustomerData.setAcctno(customers.getAcctno());
                nexgeVoiceHandlerService.performUpdate(pojo, oldCustomerData);
            }
            nexgeVoiceHandlerService.performUpdate(pojo, oldCustomerData);

            //Subscriber Update
            String newVal = SubscriberUpdateUtils.customString(Arrays.asList(customers.getDidno(), customers.getIntercomno(), customers.getChilddidno()));
            SubscriberUpdateUtils.updateSubscriber(oldVal, newVal, UpdateConstant.UPDATE_VOICE_DETAILS, customers, pojo.getRemarks(), "");
            nexgeVoiceHandlerService.performUpdate(pojo, customers);
            return getBasicDetailsOfSubscriber(customers);
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        }
    }

    public CustomerVoiceDetailsDTO getVoiceDetailById(Integer id) throws Exception {
        CustomerVoiceDetailsDTO detailsDTO = new CustomerVoiceDetailsDTO();
        String SUBMODULE = MODULE + " [VoiceDetailsGetById] ";
        try {
            Customers customers = customersRepository.findById(id).get();
            if (customers == null) {
                return null;
            } else {
                detailsDTO.setId(customers.getId());
                detailsDTO.setVoicesrvtype(customers.getVoicesrvtype());
                detailsDTO.setDidno(customers.getDidno());
                detailsDTO.setChilddidno(customers.getChilddidno());
                detailsDTO.setIntercomno(customers.getIntercomno());
                detailsDTO.setIntercomgrp(customers.getIntercomgrp());
                detailsDTO.setCommonListVoiceService(commonListService.getCommonListByType(TypeConstants.VOICE_SERVICE));
                detailsDTO.setCommonListInercomGroup(commonListService.getCommonListByType(TypeConstants.INTERCOM_GROUP));
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            e.printStackTrace();
            throw e;
        }
        return detailsDTO;
    }

    public VoiceProvisionReqDTO getVoiceProvision(Integer custId) throws Exception {
        VoiceProvisionReqDTO reqDTO = new VoiceProvisionReqDTO();
        String SUBMODULE = MODULE + " [getVoiceProvision] ";
        try {
            Customers customers = customersRepository.findById(custId).get();
            if (customers == null) {
                return null;
            } else {
                reqDTO.setCustId(customers.getId());
                reqDTO.setVoiceProvision(customers.getVoiceprovision() ? "Yes" : "No");
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            e.printStackTrace();
            throw e;
        }
        return reqDTO;
    }

    public CustomersBasicDetailsPojo changeVoiceProvision(VoiceProvisionReqDTO pojo) throws Exception {
        String SUBMODULE = MODULE + " [changeVoiceProvision] ";
        try {
            Customers customers = customersRepository.findById(pojo.getCustId()).get();
            String oldVal = SubscriberUpdateUtils.customString(Collections.singletonList(customers.getVoiceprovision() ? "Yes" : "No"));
            customers.setVoiceprovision(pojo.getVoiceProvisionFlag());
            NexgeVoiceProvisionService nexgeVoiceHandlerService = SpringContext.getBean(NexgeVoiceProvisionService.class);
            if (pojo.getVoiceProvisionFlag()) {
                Boolean result = nexgeVoiceHandlerService.performCustomerProvision(customers);
                customers.setVoiceprovision(result);
                customers = customersService.update(customers);
            }
            //Subscriber Update
            String newVal = SubscriberUpdateUtils.customString(Collections.singletonList(customers.getVoiceprovision() ? "Yes" : "No"));
            SubscriberUpdateUtils.updateSubscriber(oldVal, newVal, UpdateConstant.UPDATE_PROVISION_DETAILS, customers, pojo.getRemark(), "");
            return getBasicDetailsOfSubscriber(customers);
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        }
    }

    public SubscriberPopupDTO getSubscriberOtherDetails(Integer id) {
        SubscriberPopupDTO subscriberPopupDTO = new SubscriberPopupDTO();
        String SUBMODULE = MODULE + " [getSubscriberOtherDetails()] ";
        try {
            Customers customers = new Customers();
            customers = customersRepository.getOne(id);

            subscriberPopupDTO.setOnlinerenewalflag(customers.getOnlinerenewalflag());
            subscriberPopupDTO.setVoipenableflag(customers.getVoipenableflag());
            subscriberPopupDTO.setCustId(customers.getId());
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            e.printStackTrace();
        }
        return subscriberPopupDTO;
    }

    public CustomersBasicDetailsPojo updateSubscriberOtherDetails(SubscriberPopupDTO subscriberPopupDTO) throws Exception {
        Customers customers;
        try {
            customers = customersRepository.findById(subscriberPopupDTO.getCustId()).get();
            //Old Value
            String oldVal = SubscriberUpdateUtils.customString(Arrays.asList(customers.getOnlinerenewalflag().toString(), customers.getVoipenableflag().toString()));
            customers.setOnlinerenewalflag(subscriberPopupDTO.getOnlinerenewalflag());
            customers.setVoipenableflag(subscriberPopupDTO.getVoipenableflag());
            customers = update(customers);

            //Subscriber Update
            String newVal = SubscriberUpdateUtils.customString(Arrays.asList(customers.getOnlinerenewalflag().toString(), customers.getVoipenableflag().toString()));
            SubscriberUpdateUtils.updateSubscriber(oldVal, newVal, UpdateConstant.UPDATE_OTHER_DETAILS, customers, subscriberPopupDTO.getRemarks(), "");
        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage(), e);
            throw e;
        }
        return getBasicDetailsOfSubscriber(customers);
    }

    public List<CustomerPlansModel> getActivePlanList(Integer custId, Boolean isNotChangePlan) {
//        Customers customers = customersRepository.findById(custId).orElse(null);
        Customers customers = customersRepository.findById(custId).get();
        //List<CustomerPlansModel> customerPlansModelList = getCustomerPlanList(custId, isNotChangePlan);
        List<CustomerPlansModel> customerPlansModelList = getCustomerPlanList(customers, isNotChangePlan);
        if (null != customerPlansModelList && 0 < customerPlansModelList.size()) {
            List<CustomerPlansModel> list = customerPlansModelList.stream().filter(dto -> null != dto.getPlanstage() && (dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.SUSPEND) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.INGRACE)) && dto.getServiceEndDate() != null && dto.getServiceEndDate().isAfter(LocalDateTime.now())).collect(Collectors.toList());
            list.stream().forEach(data -> {
                java.sql.Date startDateTmp = data.getStartDate();
                java.sql.Date endDateTmp = data.getEndDate();

                Long validity = ChronoUnit.DAYS.between(startDateTmp.toLocalDate(), endDateTmp.toLocalDate());
                if (validity == 0L) validity = 1L;

                PostpaidPlan plan = postpaidPlanService.get(data.getPlanId(), customers.getMvnoId());
                if (plan != null) {
                    if (plan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
                        Timestamp startDateTime = new Timestamp(data.getStartDate().getTime());
                        Timestamp endDateTime = new Timestamp(data.getEndDate().getTime());
                        Long prorate_validity = ChronoUnit.HOURS.between(startDateTime.toLocalDateTime(), endDateTime.toLocalDateTime());
                        Double prorate_validity1 = Math.ceil(prorate_validity / 24.0);
                        validity = prorate_validity1.longValue();

                    }
                }
                data.setChildValidity(validity);
                data.setValidity(Double.valueOf(plan.getValidity()));
                if (data.getPromiseToPayStartDate() != null && data.getPromiseToPayEndDate() != null && data.getPromiseToPayStartDate().before(new Date()) && data.getPromiseToPayEndDate().after(new Date())) {
                    data.setCustPlanStatus(CommonConstants.INGRACE_STATUS);
                }
                data.setDbStartDate(Instant.ofEpochMilli(data.getStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                data.setDbEndDate(Instant.ofEpochMilli(data.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                data.setDbExpiryDate(Instant.ofEpochMilli(data.getExpiryDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                Long remainingDays = 0L;
                if (data.getDbExpiryDate() != null) {
                    remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), data.getDbExpiryDate());
                }
                data.setRemainingDays(remainingDays);
                data.setCustomerInventorySerialnumberDtos(customerInventoryMappingService.getActiveSerialnumberByConnectionNo(data.getConnection_no(), data.getCustId()));
            });
            return list;
        }
        return new ArrayList<>();
    }

    public List<CustomerPlansModel> getActivePlanList(Customers customers, Boolean isNotChangePlan) {
        List<CustomerPlansModel> customerPlansModelList = getCustomerPlanList(customers, isNotChangePlan);
        if (null != customerPlansModelList && 0 < customerPlansModelList.size()) {
            List<CustomerPlansModel> list = customerPlansModelList.stream().filter(dto -> null != dto.getPlanstage() && (dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.SUSPEND) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.INGRACE)) && dto.getServiceEndDate().isAfter(LocalDateTime.now())).collect(Collectors.toList());
            list.stream().forEach(data -> {
                java.sql.Date startDateTmp = data.getStartDate();
                java.sql.Date endDateTmp = data.getEndDate();

                Long validity = ChronoUnit.DAYS.between(startDateTmp.toLocalDate(), endDateTmp.toLocalDate());
                if (validity == 0L) validity = 1L;

                Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(data.getPlanId());
                if (plan.isPresent()) {
                    if (plan.get().getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
                        Timestamp startDateTime = new Timestamp(data.getStartDate().getTime());
                        Timestamp endDateTime = new Timestamp(data.getEndDate().getTime());
                        Long prorate_validity = ChronoUnit.HOURS.between(startDateTime.toLocalDateTime(), endDateTime.toLocalDateTime());
                        Double prorate_validity1 = Math.ceil(prorate_validity / 24.0);
                        validity = prorate_validity1.longValue();

                    }
                }
                data.setChildValidity(validity);
                data.setValidity(Double.parseDouble(validity.toString()));
                if (data.getPromiseToPayStartDate() != null && data.getPromiseToPayEndDate() != null && data.getPromiseToPayStartDate().before(new Date()) && data.getPromiseToPayEndDate().after(new Date())) {
                    data.setCustPlanStatus(CommonConstants.INGRACE_STATUS);
                }
                data.setDbStartDate(Instant.ofEpochMilli(data.getStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                data.setDbEndDate(Instant.ofEpochMilli(data.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                data.setDbExpiryDate(Instant.ofEpochMilli(data.getExpiryDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                Long remainingDays = 0L;
                if (data.getDbExpiryDate() != null) {
                    remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), data.getDbExpiryDate());
                }
                data.setRemainingDays(remainingDays);
                data.setCustomerInventorySerialnumberDtos(customerInventoryMappingService.getActiveSerialnumberByConnectionNo(data.getConnection_no(), data.getCustId()));
            });
            return list;
        }
        return new ArrayList<>();
    }

    public List<CustomerPlansModel> getTrialPlanList(Integer custId, Boolean isChangePlan) {
        List<CustomerPlansModel> customerPlansModelList = getCustomerPlanList(custId, isChangePlan);
        List<CustomerPlansModel> list = customerPlansModelList.stream().filter(customerPlansModel -> (customerPlansModel.getIstrialplan() != null) && customerPlansModel.getIstrialplan()).collect(Collectors.toList());
        list.stream().forEach(data -> {
            java.sql.Date startDateTmp = data.getStartDate();
            java.sql.Date endDateTmp = data.getEndDate();

            Long validity = ChronoUnit.DAYS.between(startDateTmp.toLocalDate(), endDateTmp.toLocalDate());
            if (validity == 0L) validity = 1L;
            Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(data.getPlanId());
            if (plan.isPresent()) {
                if (plan.get().getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
                    Timestamp startDateTime = new Timestamp(data.getStartDate().getTime());
                    Timestamp endDateTime = new Timestamp(data.getEndDate().getTime());
                    Long prorate_validity = ChronoUnit.HOURS.between(startDateTime.toLocalDateTime(), endDateTime.toLocalDateTime());
                    Double prorate_validity1 = Math.ceil(prorate_validity / 24.0);
                    validity = prorate_validity1.longValue();

                }
            }
            data.setChildValidity(validity);
            data.setValidity(Double.parseDouble(validity.toString()));
            data.setDbStartDate(Instant.ofEpochMilli(data.getStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
            data.setDbEndDate(Instant.ofEpochMilli(data.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
            data.setDbExpiryDate(Instant.ofEpochMilli(data.getExpiryDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
        });
        return list;
    }

    public List<CustomerPlansModel> getFuturePlanList(Integer custId, Boolean isChangePlan) {
//        Customers customers = customersRepository.getOne(custId);
        Customers customers = customersRepository.findById(custId).get();
        //List<CustomerPlansModel> customerPlansModelList = getCustomerPlanList(custId, isChangePlan);
        List<CustomerPlansModel> customerPlansModelList = getCustomerPlanList(customers, isChangePlan);
        if (null != customerPlansModelList && 0 < customerPlansModelList.size()) {
            List<CustomerPlansModel> list = customerPlansModelList.stream().filter(dto -> null != dto.getPlanstage() && dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.FUTURE) && (dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.FUTURE))).collect(Collectors.toList());
            list.stream().forEach(data -> {
                java.sql.Date startDateTmp = data.getStartDate();
                java.sql.Date endDateTmp = data.getEndDate();

                Long validity = ChronoUnit.DAYS.between(startDateTmp.toLocalDate(), endDateTmp.toLocalDate());
                if (validity == 0L) validity = 1L;

                PostpaidPlan plan = postpaidPlanService.get(data.getPlanId(), getMvnoIdFromCurrentStaff(custId));
                if (plan != null) {
                    if (plan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
                        Timestamp startDateTime = new Timestamp(data.getStartDate().getTime());
                        Timestamp endDateTime = new Timestamp(data.getEndDate().getTime());
                        Long prorate_validity = ChronoUnit.HOURS.between(startDateTime.toLocalDateTime(), endDateTime.toLocalDateTime());
                        Double prorate_validity1 = Math.ceil(prorate_validity / 24.0);
                        validity = prorate_validity1.longValue();

                    }
                }
                data.setChildValidity(validity);
                data.setValidity(Double.parseDouble(validity.toString()));
                data.setDbStartDate(Instant.ofEpochMilli(data.getStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                data.setDbEndDate(Instant.ofEpochMilli(data.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                data.setDbExpiryDate(Instant.ofEpochMilli(data.getExpiryDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
            });
            return list;
        }
        return new ArrayList<>();
    }

    public List<CustomerPlansModel> getFuturePlanList(Customers customers, Boolean isChangePlan) {
        List<CustomerPlansModel> customerPlansModelList = getCustomerPlanList(customers, isChangePlan);
        if (null != customerPlansModelList && 0 < customerPlansModelList.size()) {
            List<CustomerPlansModel> list = customerPlansModelList.stream().filter(dto -> null != dto.getPlanstage() && dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.FUTURE) && (dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.FUTURE))).collect(Collectors.toList());
            list.stream().forEach(data -> {
                java.sql.Date startDateTmp = data.getStartDate();
                java.sql.Date endDateTmp = data.getEndDate();

                Long validity = ChronoUnit.DAYS.between(startDateTmp.toLocalDate(), endDateTmp.toLocalDate());
                if (validity == 0L) validity = 1L;

                Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(data.getPlanId());
                if (plan.isPresent()) {
                    if (plan.get().getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
                        Timestamp startDateTime = new Timestamp(data.getStartDate().getTime());
                        Timestamp endDateTime = new Timestamp(data.getEndDate().getTime());
                        Long prorate_validity = ChronoUnit.HOURS.between(startDateTime.toLocalDateTime(), endDateTime.toLocalDateTime());
                        Double prorate_validity1 = Math.ceil(prorate_validity / 24.0);
                        validity = prorate_validity1.longValue();

                    }
                }
                data.setChildValidity(validity);
                data.setValidity(Double.parseDouble(validity.toString()));
                data.setDbStartDate(Instant.ofEpochMilli(data.getStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                data.setDbEndDate(Instant.ofEpochMilli(data.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                data.setDbExpiryDate(Instant.ofEpochMilli(data.getExpiryDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
            });
            return list;
        }
        return new ArrayList<>();
    }

    public List<CustomerPlansModel> getExpiredPlanList(Integer custId, Boolean isChangePlan) {
        Customers customers = customersRepository.getOne(custId);
        //List<CustomerPlansModel> customerPlansModelList = getCustomerPlanList(custId, isChangePlan);
        List<CustomerPlansModel> customerPlansModelList = getCustomerPlanList(customers, isChangePlan);
        if (null != customerPlansModelList && 0 < customerPlansModelList.size()) {
            List<CustomerPlansModel> list = customerPlansModelList.stream().filter(dto -> null != dto.getPlanstage() && (dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.INAVCTIVE) || (dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.EXPIRED) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.SUSPEND) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE) || dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.INGRACE) || (dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE) || (dto.getPlanstage().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD)))) && dto.getExpiryDate().before(new Date()))).collect(Collectors.toList());
            list.stream().forEach(data -> {
                java.sql.Date startDateTmp = data.getStartDate();
                java.sql.Date endDateTmp = data.getEndDate();

                Long validity = ChronoUnit.DAYS.between(startDateTmp.toLocalDate(), endDateTmp.toLocalDate());
                if (validity == 0L) validity = 1L;
                Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(data.getPlanId());
                if (plan.isPresent()) {
                    if (plan.get().getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
                        Timestamp startDateTime = new Timestamp(data.getStartDate().getTime());
                        Timestamp endDateTime = new Timestamp(data.getEndDate().getTime());
                        Long prorate_validity = ChronoUnit.HOURS.between(startDateTime.toLocalDateTime(), endDateTime.toLocalDateTime());
                        Double prorate_validity1 = Math.ceil(prorate_validity / 24.0);
                        validity = prorate_validity1.longValue();

                    }
                }
                if ((data.getCustPlanStatus() != null && (!data.getCustPlanStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.TERMINATE) && !data.getCustPlanStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.SUSPEND)) && !data.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE)))
                    data.setCustPlanStatus("Expired");
                data.setChildValidity(validity);
                data.setValidity(Double.parseDouble(validity.toString()));
                data.setDbStartDate(Instant.ofEpochMilli(data.getStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                data.setDbEndDate(Instant.ofEpochMilli(data.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                data.setDbExpiryDate(Instant.ofEpochMilli(data.getExpiryDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
            });
            if (!CollectionUtils.isEmpty(list)) {
                List<Integer> customerPlansModelActive = customerPlansModelList.stream().filter(i -> i.getCustPlanStatus().equalsIgnoreCase("Active")).map(CustomerPlansModel::getServiceId).collect(Collectors.toList());

                list.forEach(planModel -> {
                    Integer serviceId = planModel.getServiceId();
                    planModel.setIsPromiseTopay(!customerPlansModelActive.contains(serviceId));
                });
            }

            return list;

        }
        return new ArrayList<>();
    }

    /**
     * This Method is to use for calculate plan validity
     *
     * @param startDate represents the start date of plan
     * @param endDate   represents the end date of plan
     * @param planId    represents the specific plan id
     * @return Long total duration between start and end date of plan
     */
    private Long getPlanValidity(java.sql.Date startDate, java.sql.Date endDate, Integer planId, Integer mvnoId) {
        java.sql.Date startDateTmp = startDate;
        java.sql.Date endDateTmp = endDate;

        Long validity = ChronoUnit.DAYS.between(startDateTmp.toLocalDate(), endDateTmp.toLocalDate());
        if (validity == 0L) validity = 1L;
//        Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(planId);
        Optional<PostpaidPlan> plan = Optional.ofNullable(postpaidPlanService.get(planId, mvnoId));
        if (plan.isPresent()) {
            if (plan.get().getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
                Timestamp startDateTime = new Timestamp(startDate.getTime());
                Timestamp endDateTime = new Timestamp(endDate.getTime());
                Long prorate_validity = ChronoUnit.HOURS.between(startDateTime.toLocalDateTime(), endDateTime.toLocalDateTime());
                Double prorate_validity1 = Math.ceil(prorate_validity / 24.0);
                validity = prorate_validity1.longValue();

            }
        }
        return validity;
    }

    public List<CustomerPlansModel> getCustomerPlanList(Integer custId, Boolean isNotChangePlan) {
//        Customers customers = customersRepository.getOne(custId);
        Customers customers = customersRepository.findById(custId).get();
        List<CustomerPlansModel> customerPlansModelList = getResultOfQuery(customers);
        if (isNotChangePlan) {
            List<Integer> singleChildCustIds = customersService.getSingleChildCustomerIds(custId, CommonConstants.INVOICE_TYPE_GROUP);
            if (singleChildCustIds != null && singleChildCustIds.size() > 0) {
                for (Integer childCustId : singleChildCustIds) {
                    Customers childCustomers = customersRepository.getOne(childCustId);
                    customerPlansModelList.addAll(getResultOfQuery(childCustomers));
                }
            }

        }
        if (null != customerPlansModelList && 0 < customerPlansModelList.size()) {
            List<Integer> childCustIds = new ArrayList<>();
//            Customers customer = customersRepository.findById(custId).get();
            List<Customers> childCustomersList = customersRepository.findAllByParentCustomers(customers);
            if (!CollectionUtils.isEmpty(childCustomersList)) {
                childCustIds = childCustomersList.stream().map(Customers::getId).collect(Collectors.toList());
            }
            for (CustomerPlansModel customerPlansModel : customerPlansModelList) {
                Long systemPromiseToPayCount = Long.valueOf(clientServiceSrv.getValueByName(ClientServiceConstant.PROMISETOPAY_COUNT, customers.getMvnoId()));
                boolean isPromiseTopayTaken = customerPlansModel.getPromiseToPayCount() != null && customerPlansModel.getPromiseToPayCount() >= systemPromiseToPayCount;
                customerPlansModel.setIsPromiseToPayTaken(isPromiseTopayTaken);
                if (!CollectionUtils.isEmpty(childCustIds)) {
                    customerPlansModel.setIsChildExists(customerServiceMappingRepository.existsByCustIdInAndServiceIdInAndInvoiceType(childCustIds, Collections.singletonList(Long.valueOf(customerPlansModel.getServiceId())), CommonConstants.INVOICE_TYPE_GROUP));
                } else {
                    customerPlansModel.setIsChildExists(false);
                }
                if (customerPlansModel.getPlangroupid() != null) {
                    // TODO: pass mvnoID manually 6/5/2025
                    PlanGroup planGroup = planGroupService.findPlanGroupById(customerPlansModel.getPlangroupid(), customers.getMvnoId());
                    if (Objects.nonNull(planGroup)) {
                        customerPlansModel.setPlanGroupName(planGroup.getPlanGroupName());
                    }
                }
                if (customerPlansModel.getCustomerServiceMappingId() != null) {
                    QCustomerServiceMapping qCustomerServiceMapping = QCustomerServiceMapping.customerServiceMapping;
                    BooleanExpression booleanCustServiceMapping = qCustomerServiceMapping.isNotNull().and(qCustomerServiceMapping.id.in(customerPlansModel.getCustomerServiceMappingId())).and(qCustomerServiceMapping.isDeleted.eq(false));
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findOne(booleanCustServiceMapping).get();
//                    CustPlanMappping custPlanMappping = custPlanMappingRepo.findById(customerPlansModel.getPlanmapid());
                    CustPlanMappping custPlanMappping = custPlanMappingService.getEntityById(customerPlansModel.getPlanmapid());
                    if (customerServiceMapping != null) {
                        customerPlansModel.setConnection_no(customerServiceMapping.getConnectionNo());
                        customerPlansModel.setNickname(customerServiceMapping.getNickName());
                        if (custPlanMappping.getStartServiceDate() != null) {
                            customerPlansModel.setServiceStartDate(custPlanMappping.getStartServiceDate());
                        }
                        customerPlansModel.setDiscountFlowInProcess(customerServiceMapping.getDiscountFlowInProcess());
//                        PlanService planService1 = planServiceRepository.findById(customerServiceMapping.getServiceId().intValue()).get();
                        PlanService planService1 = planServiceService.get(customerServiceMapping.getServiceId().intValue(), customers.getMvnoId());
                        if (planService1.getIsServiceThroughLead() != null && planService1.getIsServiceThroughLead()) {
                            customerPlansModel.setIsServiceThroughLead(true);
                        }
                        if (customerServiceMapping.getServiceHoldDate() != null)
                            customerPlansModel.setServiceHoldDate(customerServiceMapping.getServiceHoldDate());
                        if (customerServiceMapping.getServiceHoldBy() != null)
                            customerPlansModel.setServiceHoldBy(customerServiceMapping.getServiceHoldBy());
                        if (customerServiceMapping.getServiceHoldRemarks() != null)
                            customerPlansModel.setServiceHoldRemarks(customerServiceMapping.getServiceHoldRemarks());

                        if (customerServiceMapping.getServiceResumeDate() != null)
                            customerPlansModel.setServiceResumeDate(customerServiceMapping.getServiceResumeDate());
                        if (customerServiceMapping.getServiceHoldDate() != null && customerServiceMapping.getServiceResumeDate() != null) {
//                            long daysBetween = ChronoUnit.DAYS.between(customerServiceMapping.getServiceHoldDate(), customerServiceMapping.getServiceResumeDate());
                            customerPlansModel.setRemainingPauseDays((int) customerPlansModel.getMaxHoldDurationDays() - customerServiceMapping.getActualHoldDurationDays());
                        } else {
                            customerPlansModel.setRemainingPauseDays(customerPlansModel.getMaxHoldDurationDays());
                        }
                        if (customerServiceMapping.getServiceHoldAttempts() != null && customerPlansModel.getMaxHoldAttempts() != null) {
                            int diff = customerPlansModel.getMaxHoldAttempts() - customerServiceMapping.getServiceHoldAttempts();
                            customerPlansModel.setRemainingHoldAttempts(Math.max(diff, 0));
                        }


//                        if(customerServiceMapping.getServiceHoldAttempts()!=null)
//                            customerPlansModel.setMaxHoldAttempts(customerServiceMapping.getServiceHoldAttempts());
                        if (customerServiceMapping.getServiceResumeBy() != null)
                            customerPlansModel.setServiceResumeBy(customerServiceMapping.getServiceHoldBy());
                        if (customerServiceMapping.getServiceResumeRemarks() != null)
                            customerPlansModel.setServiceResumeRemarks(customerServiceMapping.getServiceResumeRemarks());

                        if (custPlanMappping.getEndDate() != null) {
                            customerPlansModel.setServiceEndDate(custPlanMappping.getEndDate());
                        }
                        customerPlansModel.setChildValidity(getPlanValidity(customerPlansModel.getStartDate(), customerPlansModel.getEndDate(), customerPlansModel.getPlanId(), customers.getMvnoId()));
                        customerPlansModel.setValidity(Double.valueOf(getPlanValidity(customerPlansModel.getStartDate(), customerPlansModel.getEndDate(), customerPlansModel.getPlanId(), customers.getMvnoId())));
                        if (custPlanMappping.getRemarks() != null)
                            customerPlansModel.setRemarks(custPlanMappping.getRemarks());
                        if (custPlanMappping.getBillTo() != null)
                            customerPlansModel.setBillTo(custPlanMappping.getBillTo());
                        if (customerServiceMapping.getDiscount() != null)
                            customerPlansModel.setDiscount(customerServiceMapping.getDiscount());
                        if (customerServiceMapping.getDiscountType() != null)
                            customerPlansModel.setDiscountType(customerServiceMapping.getDiscountType());
                        if (customerServiceMapping.getDiscountExpiryDate() != null) {
                            customerPlansModel.setDiscountExpiryDate(customerServiceMapping.getDiscountExpiryDate());
                        }
                        if (custPlanMappping.getPlanGroup() != null) {
                            customerPlansModel.setCustPlanCategory("Plan Group");
                        } else {
                            customerPlansModel.setCustPlanCategory("Individual");
                        }
                        if (customerServiceMapping.getInvoiceType() != null) {
                            customerPlansModel.setInvoiceType(customerServiceMapping.getInvoiceType());
                        }
                        if (customerServiceMapping.getNextStaff() != null) {
                            customerPlansModel.setNextStaff(customerServiceMapping.getNextStaff());
                        }
                        if (customerServiceMapping.getNextTeamHierarchyMappingId() != null) {
                            customerPlansModel.setNextTeamHierarchyMappingId(customerServiceMapping.getNextTeamHierarchyMappingId());
                        }
                        if (customerServiceMapping.getId() != null) {
                            customerPlansModel.setCustomerServiceMappingId(customerServiceMapping.getId());
                        }
                        if (customerServiceMapping.getStatus() != null) {
                            customerPlansModel.setCustServMappingStatus(customerServiceMapping.getStatus());
                        }
                        if (Objects.isNull(customerServiceMapping.getStatus())) {
                            customerServiceMapping.setStatus(Constants.ACTIVE);
                        }
                        if (Objects.isNull(customerServiceMapping.getStatus())) {
                            customerPlansModel.setCustServMappingStatus(Constants.ACTIVE);
                        }if(Objects.isNull(customerPlansModel.getCustServMappingStatus())){
                            customerPlansModel.setCustServMappingStatus(customerServiceMapping.getStatus());
                        }
                        if (custPlanMappping.getIsVoid() != null)
                            customerPlansModel.setIsVoid(custPlanMappping.getIsVoid());
                        else customerPlansModel.setIsVoid(false);
                        if (custPlanMappping.getExtendValidityremarks() != null)
                            customerPlansModel.setExtendValidityremarks(custPlanMappping.getExtendValidityremarks());
                        customerPlansModel.setCustomerInventorySerialnumberDtos(customerInventoryMappingService.getActiveSerialnumberByConnectionNo(customerPlansModel.getConnection_no(), customerPlansModel.getCustId()));
                        if (custPlanMappping.getCreatedate() != null) {
                            customerPlansModel.setCreatedate(custPlanMappping.getCreatedate());
                        }
                        if (custPlanMappping.getPromise_to_pay_remarks() != null) {
                            customerPlansModel.setPromiseToPayRemarks(custPlanMappping.getPromise_to_pay_remarks());
                        }
                    }
                    customerPlansModel.setDbStartDate(Instant.ofEpochMilli(customerPlansModel.getStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                    customerPlansModel.setDbEndDate(Instant.ofEpochMilli(customerPlansModel.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                    customerPlansModel.setDbExpiryDate(Instant.ofEpochMilli(customerPlansModel.getExpiryDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                    if (customerPlansModel.getPromiseToPayStartDate() != null && customerPlansModel.getPromiseToPayEndDate() != null && customerPlansModel.getPromiseToPayStartDate().before(new Date()) && customerPlansModel.getPromiseToPayEndDate().after(new Date())) {
                        customerPlansModel.setCustPlanStatus(CommonConstants.INGRACE_STATUS);
                    }
                }
            }
            return customerPlansModelList;//.stream().filter(CommonUtils.distinctByKey(CustomerPlansModel::getCustPlanMapppingId)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<CustomerPlansModel> getCustomerPlanList(Customers customers, Boolean isNotChangePlan) {
        //List<CustomerPlansModel> customerPlansModelList = customRepository.getResultOfQuery(PlanQueryScript.getActivePlans(custId), CustomerPlansModel.class);
        List<CustomerPlansModel> customerPlansModelList = getResultOfQuery(customers);
        if (isNotChangePlan) {
            List<Integer> singleChildCustIds = customersService.getSingleChildCustomerIds(customers.getId(), CommonConstants.INVOICE_TYPE_GROUP);
            if (singleChildCustIds != null && singleChildCustIds.size() > 0) {
                for (Integer childCustId : singleChildCustIds) {
                    Customers childCustomers = customersRepository.findById(childCustId).orElse(null);
                    customerPlansModelList.addAll(getResultOfQuery(childCustomers));
                }
            }

        }
        if (null != customerPlansModelList && 0 < customerPlansModelList.size()) {
            List<Integer> childCustIds = new ArrayList<>();
            //Customers customer = customersRepository.findById(customers.getId()).get();
            List<Customers> childCustomersList = customersRepository.findAllByParentCustomers(customers);
            if (!CollectionUtils.isEmpty(childCustomersList)) {
                childCustIds = childCustomersList.stream().map(Customers::getId).collect(Collectors.toList());
            }
            for (CustomerPlansModel customerPlansModel : customerPlansModelList) {
//                Long systemPromiseToPayCount = Long.valueOf(clientServiceSrv.getValueByNameAndmvnoId(ClientServiceConstant.PROMISETOPAY_COUNT, customers.getMvnoId()));
                Long systemPromiseToPayCount = Long.valueOf(clientServiceSrv.getValueByNameAndmvnoId(ClientServiceConstant.PROMISETOPAY_COUNT, customers.getMvnoId()));
                boolean isPromiseTopayTaken = customerPlansModel.getPromiseToPayCount() != null && customerPlansModel.getPromiseToPayCount() >= systemPromiseToPayCount;
                customerPlansModel.setIsPromiseToPayTaken(isPromiseTopayTaken);
                if (!CollectionUtils.isEmpty(childCustIds)) {
                    customerPlansModel.setIsChildExists(customerServiceMappingRepository.existsByCustIdInAndServiceIdInAndInvoiceType(childCustIds, Collections.singletonList(Long.valueOf(customerPlansModel.getServiceId())), CommonConstants.INVOICE_TYPE_GROUP));
                } else {
                    customerPlansModel.setIsChildExists(false);
                }
                if (customerPlansModel.getPlangroupid() != null) {
                    // TODO: pass mvnoID manually 6/5/2025
                    PlanGroup planGroup = planGroupService.findPlanGroupById(customerPlansModel.getPlangroupid(), getMvnoIdFromCurrentStaff(null));
                    if (Objects.nonNull(planGroup)) {
                        customerPlansModel.setPlanGroupName(planGroup.getPlanGroupName());
                    }
                }
                if (customerPlansModel.getCustomerServiceMappingId() != null) {
                    QCustomerServiceMapping qCustomerServiceMapping = QCustomerServiceMapping.customerServiceMapping;
                    BooleanExpression booleanCustServiceMapping = qCustomerServiceMapping.isNotNull().and(qCustomerServiceMapping.id.in(customerPlansModel.getCustomerServiceMappingId()));
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findOne(booleanCustServiceMapping).get();
                    CustPlanMappping custPlanMappping = custPlanMappingRepo.findById(customerPlansModel.getPlanmapid());
                    if (customerServiceMapping != null) {
                        customerPlansModel.setConnection_no(customerServiceMapping.getConnectionNo());
                        customerPlansModel.setNickname(customerServiceMapping.getNickName());
                        if (custPlanMappping.getStartServiceDate() != null) {
                            customerPlansModel.setServiceStartDate(custPlanMappping.getStartServiceDate());
                        }
                        PlanService planService1 = planServiceRepository.findById(customerServiceMapping.getServiceId().intValue()).get();
                        if (planService1.getIsServiceThroughLead() != null && planService1.getIsServiceThroughLead()) {
                            customerPlansModel.setIsServiceThroughLead(true);
                        }
                        if (customerServiceMapping.getServiceHoldDate() != null)
                            customerPlansModel.setServiceHoldDate(customerServiceMapping.getServiceHoldDate());
                        if (customerServiceMapping.getServiceHoldBy() != null)
                            customerPlansModel.setServiceHoldBy(customerServiceMapping.getServiceHoldBy());
                        if (customerServiceMapping.getServiceHoldRemarks() != null)
                            customerPlansModel.setServiceHoldRemarks(customerServiceMapping.getServiceHoldRemarks());

                        if (customerServiceMapping.getServiceResumeDate() != null)
                            customerPlansModel.setServiceResumeDate(customerServiceMapping.getServiceResumeDate());
                        if (customerServiceMapping.getServiceResumeBy() != null)
                            customerPlansModel.setServiceResumeBy(customerServiceMapping.getServiceHoldBy());
                        if (customerServiceMapping.getServiceResumeRemarks() != null)
                            customerPlansModel.setServiceResumeRemarks(customerServiceMapping.getServiceResumeRemarks());

                        if (custPlanMappping.getEndDate() != null) {
                            customerPlansModel.setServiceEndDate(custPlanMappping.getEndDate());
                        }
                        customerPlansModel.setChildValidity(getPlanValidity(customerPlansModel.getStartDate(), customerPlansModel.getEndDate(), customerPlansModel.getPlanId(), customers.getMvnoId()));
//                        customerPlansModel.setValidity(Double.valueOf(getPlanValidity(customerPlansModel.getStartDate(), customerPlansModel.getEndDate(), customerPlansModel.getPlanId(), customers.getMvnoId())));
                        customerPlansModel.setValidity(Double.valueOf(custPlanMappping.getPlanValidityDays()));
                        if (custPlanMappping.getRemarks() != null)
                            customerPlansModel.setRemarks(custPlanMappping.getRemarks());
                        if (custPlanMappping.getBillTo() != null)
                            customerPlansModel.setBillTo(custPlanMappping.getBillTo());
                        if (customerServiceMapping.getDiscount() != null)
                            customerPlansModel.setDiscount(customerServiceMapping.getDiscount());
                        if (customerServiceMapping.getDiscountType() != null)
                            customerPlansModel.setDiscountType(customerServiceMapping.getDiscountType());
                        if (customerServiceMapping.getDiscountExpiryDate() != null) {
                            customerPlansModel.setDiscountExpiryDate(customerServiceMapping.getDiscountExpiryDate());
                        }
                        if (custPlanMappping.getPlanGroup() != null) {
                            customerPlansModel.setCustPlanCategory("Plan Group");
                        } else {
                            customerPlansModel.setCustPlanCategory("Individual");
                        }
                        if (customerServiceMapping.getInvoiceType() != null) {
                            customerPlansModel.setInvoiceType(customerServiceMapping.getInvoiceType());
                        }
                        if (customerServiceMapping.getNextStaff() != null) {
                            customerPlansModel.setNextStaff(customerServiceMapping.getNextStaff());
                        }
                        if (customerServiceMapping.getNextTeamHierarchyMappingId() != null) {
                            customerPlansModel.setNextTeamHierarchyMappingId(customerServiceMapping.getNextTeamHierarchyMappingId());
                        }
                        if (customerServiceMapping.getId() != null) {
                            customerPlansModel.setCustomerServiceMappingId(customerServiceMapping.getId());
                        }
                        if (customerServiceMapping.getStatus() != null) {
                            customerPlansModel.setCustServMappingStatus(customerServiceMapping.getStatus());
                        }
                        if (Objects.isNull(customerServiceMapping.getStatus())) {
                            customerServiceMapping.setStatus(Constants.ACTIVE);
                        }
                        if (Objects.isNull(customerServiceMapping.getStatus())) {
                            customerPlansModel.setCustServMappingStatus(Constants.ACTIVE);
                        }
                        if (custPlanMappping.getIsVoid() != null)
                            customerPlansModel.setIsVoid(custPlanMappping.getIsVoid());
                        else customerPlansModel.setIsVoid(false);
                        if (custPlanMappping.getExtendValidityremarks() != null)
                            customerPlansModel.setExtendValidityremarks(custPlanMappping.getExtendValidityremarks());
                        customerPlansModel.setCustomerInventorySerialnumberDtos(customerInventoryMappingService.getActiveSerialnumberByConnectionNo(customerPlansModel.getConnection_no(), customerPlansModel.getCustId()));
                        if (custPlanMappping.getCreatedate() != null) {
                            customerPlansModel.setCreatedate(custPlanMappping.getCreatedate());
                        }
                        if (custPlanMappping.getPromise_to_pay_remarks() != null) {
                            customerPlansModel.setPromiseToPayRemarks(custPlanMappping.getPromise_to_pay_remarks());
                        }
                    }
                    customerPlansModel.setDbStartDate(Instant.ofEpochMilli(customerPlansModel.getStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                    customerPlansModel.setDbEndDate(Instant.ofEpochMilli(customerPlansModel.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                    customerPlansModel.setDbExpiryDate(Instant.ofEpochMilli(customerPlansModel.getExpiryDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                    if (customerPlansModel.getPromiseToPayStartDate() != null && customerPlansModel.getPromiseToPayEndDate() != null && customerPlansModel.getPromiseToPayStartDate().before(new Date()) && customerPlansModel.getPromiseToPayEndDate().after(new Date())) {
                        customerPlansModel.setCustPlanStatus(CommonConstants.INGRACE_STATUS);
                    }
                }
            }
            return customerPlansModelList;//.stream().filter(CommonUtils.distinctByKey(CustomerPlansModel::getCustPlanMapppingId)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<CaseCountModel> getCaseCountByCustomer(Integer custId) {
        List<CaseCountModel> caseCountList = caseCountCustomRepository.getResultOfQuery(PlanQueryScript.getCaseCount(custId), CaseCountModel.class);
        if (null != caseCountList && 0 < caseCountList.size()) {
            return caseCountList;
        }
        return new ArrayList<>();
    }

    public List<CustomerPlansModel> getQuota(Integer customerId) throws Exception {
        String SUBMODULE = MODULE + " [getQuota()] ";
        try {
            List<CustomerPlansModel> dataPlanList = getActivePlanList(customerId, false);
            if (null != dataPlanList && 0 < dataPlanList.size()) {
                return dataPlanList.stream().filter(dto -> null != dto.getService() && dto.getService().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA)).collect(Collectors.toList());
            }
            return new ArrayList<>();
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    //    @Deprecated
//    public StatusModel getStatus(Integer customerId) throws Exception {
//        String SUBMODULE = MODULE + " [getStatus()] ";
//        StatusModel statusModel = new StatusModel();
//        try {
//            statusModel.setCustId(customerId);

    public CustomersBasicDetailsPojo changeQuota(QuotaDtlsModel requestDTO, Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [changeQuota()] ";
        try {
            CustQuotaDetails dto = custQuotaService.getByCustPackId(requestDTO.getPlanmapid());
            String oldVal = "";
            String newVal = "";
            if (dto.getTotalQuota() == -1) {
                oldVal = dto.getTotalQuota().toString();
                dto.setTotalQuota(dto.getTotalQuota());
                newVal = dto.getTotalQuota().toString();
            } else {
                oldVal = dto.getTotalQuota().toString();
                if (requestDTO.getOperationDataQuota() != null) {
                    if (requestDTO.getOperationDataQuota().equalsIgnoreCase(Constants.ADD)) {
                        Double valQuotaAdd = QuotaDtlsService.quotaUnitConvertToKBUnit(requestDTO.getValueDataQuota(), requestDTO.getUnitDataQuota());
                        Double OldQuota = QuotaDtlsService.quotaUnitConvertToKBUnit(dto.getTotalQuota(), dto.getQuotaUnit());
                        Double totalQuota = valQuotaAdd + OldQuota;
                        dto.setTotalQuotaKB(totalQuota);
                        dto.setTotalQuota(Double.parseDouble(new DecimalFormat("##.######").format(QuotaDtlsService.quotaUnitConvertKbToUnit(totalQuota, dto.getQuotaUnit()))));
                        newVal = dto.getTotalQuota().toString();
                    } else if (requestDTO.getOperationDataQuota().equalsIgnoreCase(Constants.REPLACE)) {
                        dto.setTotalQuota(requestDTO.getValueDataQuota());
                        dto.setQuotaUnit(requestDTO.getUnitDataQuota());
                        dto.setTotalQuotaKB(QuotaDtlsService.quotaUnitConvertToKBUnit(requestDTO.getValueDataQuota(), requestDTO.getUnitDataQuota()));
                        newVal = dto.getTotalQuota().toString();
                    }
                }
            }
            if (dto.getTimeTotalQuota() == -1) {
                oldVal = dto.getTimeTotalQuota().toString();
                dto.setTimeTotalQuota(dto.getTimeTotalQuota());
            } else {
                oldVal = dto.getTimeTotalQuota().toString();
                if (requestDTO.getOperationTimeQuota() != null) {
                    if (requestDTO.getOperationTimeQuota().equalsIgnoreCase(Constants.ADD)) {
                        Double newAddVal = QuotaDtlsService.quotaUnitConvertToSecond(requestDTO.getValueTimeQuota(), requestDTO.getUnitTimeQuota());
                        Double oldTimeVal = QuotaDtlsService.quotaUnitConvertToSecond(dto.getTimeTotalQuota(), dto.getTimeQuotaUnit());
                        Double totalTimeQuota = newAddVal + oldTimeVal;
                        dto.setTimeTotalQuotaSec(totalTimeQuota);
                        dto.setTimeTotalQuota(Double.parseDouble(new DecimalFormat("##.######").format(QuotaDtlsService.quotaUnitSecondConvertToUnit(totalTimeQuota, dto.getTimeQuotaUnit()))));
                        newVal = dto.getTimeTotalQuota().toString();
                    } else if (requestDTO.getOperationTimeQuota().equalsIgnoreCase(Constants.REPLACE)) {
                        dto.setTimeTotalQuota(requestDTO.getValueTimeQuota());
                        dto.setTimeQuotaUnit(requestDTO.getUnitTimeQuota());
                        dto.setTimeTotalQuotaSec(QuotaDtlsService.quotaUnitConvertToSecond(requestDTO.getValueTimeQuota(), requestDTO.getUnitTimeQuota()));
                        newVal = dto.getTimeTotalQuota().toString();
                    }
                }
            }
            dto.setCustomer(customersRepository.findById(requestDTO.getCustId()).get());
            dto = custQuotaService.update(dto);

            //Subscriber Update
            SubscriberUpdateUtils.updateSubscriber(oldVal, newVal, UpdateConstant.UPDATE_QUOTA, customers, requestDTO.getRemark(), null);
            return getBasicDetailsOfSubscriber(dto.getCustomer());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    /// /            statusModel.setCurrentStatus(customersRepository.findById(customerId).getStatus());
//            statusModel.setChangedStatus(commonListService.getCommonListByType(TypeConstants.STATUS));
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return statusModel;
//    }
    public CustomersBasicDetailsPojo changeStatus(StatusDTO requestDTO) throws Exception {
        String SUBMODULE = MODULE + " [changeStatus()] ";
        Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
        //Old value
        String oldValue = customers.getStatus();
        Customers tempCustomers = new Customers(customers);
        try {
            if (requestDTO.getChangedStatus().equalsIgnoreCase(Constants.ACTIVE)) {
                customers.setStatus(Constants.ACTIVE);

                // Voice Provisioning Integration
                try {
                    if (null != customers.getVoicesrvtype() && (customers.getVoicesrvtype().equalsIgnoreCase(SubscriberConstants.INTERCOM) || customers.getVoicesrvtype().equalsIgnoreCase(SubscriberConstants.PHONE_LINE) || customers.getVoicesrvtype().equalsIgnoreCase(SubscriberConstants.SHIP_TRUNK))) {
                        //Call Provisioning service if voice customer

                        NexgeVoiceProvisionService voiceProvisionService = SpringContext.getBean(NexgeVoiceProvisionService.class);
                        Boolean provisionResult = voiceProvisionService.performCustomerProvision(customers);
                        customers.setVoiceprovision(provisionResult);
                    }

                } catch (Exception e) {
                    ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
                    e.printStackTrace();
                }
                CommunicationHelper communicationHelper = new CommunicationHelper();
                Map<String, String> map = new HashMap<>();
                map.put(CommunicationConstant.DESTINATION, customers.getMobile());
                map.put(CommunicationConstant.EMAIL, customers.getEmail());
                communicationHelper.generateCommunicationDetails(CommunicationConstant.INTERNET_ENABLED, Collections.singletonList(map));
            } else if (requestDTO.getChangedStatus().equalsIgnoreCase(Constants.INACTIVE)) {

                customers.setStatus(Constants.INACTIVE);
                CommunicationHelper communicationHelper = new CommunicationHelper();
                Map<String, String> map = new HashMap<>();
                map.put(CommunicationConstant.DESTINATION, customers.getMobile());
                map.put(CommunicationConstant.EMAIL, customers.getEmail());
                communicationHelper.generateCommunicationDetails(CommunicationConstant.INTERNET_DISABLED, Collections.singletonList(map));
            } else if (requestDTO.getChangedStatus().equalsIgnoreCase(Constants.SUSPEND)) {
                customers.setStatus(Constants.SUSPEND);
                CommunicationHelper communicationHelper = new CommunicationHelper();
                Map<String, String> map = new HashMap<>();
                map.put(CommunicationConstant.USERNAME, customers.getUsername());
                map.put(CommunicationConstant.DESTINATION, customers.getMobile());
                map.put(CommunicationConstant.EMAIL, customers.getEmail());
                communicationHelper.generateCommunicationDetails(CommunicationConstant.ACC_SUSPENDED, Collections.singletonList(map));
            } else if (requestDTO.getChangedStatus().equalsIgnoreCase(Constants.TERMINATE)) {
                customers.setStatus(Constants.TERMINATE);
                CommunicationHelper communicationHelper = new CommunicationHelper();
                Map<String, String> map = new HashMap<>();
                map.put(CommunicationConstant.DESTINATION, customers.getMobile());
                map.put(CommunicationConstant.EMAIL, customers.getEmail());
                Map<String, Object> map1 = hierarchyService.getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, CommonConstants.HIERARCHY_TYPE, false, true, customers);
                Hierarchy hierarchy = null;
//                HashMap<String, String> tatMapDetails = new HashMap<>();
//                Optional<StaffUser> staffUser=staffUserRepository.findById(getLoggedInUserId());
//                Long tat_id= Long.valueOf(String.valueOf(map.get("tat_id")));
//                Optional<Matrix> matrix=matrixRepository.findById(tat_id);
//                if(matrix.isPresent()) {
//                    if(getBUIdsFromCurrentStaff()!=null){
//                        hierarchy= hierarchyRepository.findByBuIdIsNullAndMvnoIdAndEventNameAndIsDeleted(customers.getMvnoId(),CommonConstants.WORKFLOW_EVENT_NAME.CAF,false);
//                    }else {
//                        hierarchy = hierarchyRepository.findByMvnoIdAndBuIdInAndEventNameAndIsDeleted(customers.getMvnoId(), getBUIdsFromCurrentStaff(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, false);
//                    }
//                    if(hierarchy!=null){
//                        if(matrix.isPresent()) {
//                            Matrix matrix1=matrix.get();
//                            customers.setSlaUnit(matrix1.getRunit());
//                            if(matrix1.getRunit().equalsIgnoreCase("MIN")) {
//                                customers.setNextfollowupdate(LocalDate.now());
//                                customers.setNextfollowuptime(LocalTime.now().plusMinutes(matrix1.getRtime()));
//                            }else if(matrix1.getRunit().equalsIgnoreCase("HOUR")) {
//                                customers.setNextfollowupdate(LocalDate.now());
//                                customers.setNextfollowuptime(LocalTime.now().plusHours(matrix1.getRtime()));
//                            }else {
//                                customers.setNextfollowupdate(LocalDate.now().plusDays(matrix1.getRtime()));
//                            }
//                            if (staffUser.get().getStaffUserparent() != null && !CollectionUtils.isEmpty(map) && customers != null && !map.get("eventId").equals("0") && !map.get("eventId").equals(null)) {
//                                tatMapDetails.put("workFlowId", map.get("workFlowId").toString());
//                                tatMapDetails.put("eventId", map.get("eventId").toString());
//                                tatMapDetails.put("eventName", map.get("eventName").toString());
//                                tatMapDetails.put("tat_id", map.get("tat_id").toString());
//                                tatMapDetails.put("orderNo", map.get("orderNo").toString());
//                                tatUtils.saveOrUpdateDataForTatMatrix( tatMapDetails, staffUser.get(),customers.getId(),null);
//                            }
//
//                        }
//                    }
//                }
                communicationHelper.generateCommunicationDetails(CommunicationConstant.ACC_TERMINATED, Collections.singletonList(map));
            } else if (requestDTO.getChangedStatus().equalsIgnoreCase(Constants.NEW_ACTIVE)) {
                if (customers.getStatus().equalsIgnoreCase(requestDTO.getChangedStatus())) {
                    customers.setStatus(Constants.NEW_ACTIVE);
                    CommunicationHelper communicationHelper = new CommunicationHelper();
                    Map<String, String> map = new HashMap<>();
                    map.put(CommunicationConstant.DESTINATION, customers.getMobile());
                    map.put(CommunicationConstant.EMAIL, customers.getEmail());
                    communicationHelper.generateCommunicationDetails(CommunicationConstant.ACC_TERMINATED, Collections.singletonList(map));
                } else if (customers.getStatus().equalsIgnoreCase(Constants.ACTIVE) || customers.getStatus().equalsIgnoreCase(Constants.INACTIVE) || customers.getStatus().equalsIgnoreCase(Constants.TERMINATE) || customers.getStatus().equalsIgnoreCase(Constants.SUSPEND) || customers.getStatus().equalsIgnoreCase(Constants.NEW_ACTIVE)) {
                    if (customers.getStatus().equalsIgnoreCase(Constants.ACTIVE) && requestDTO.getChangedStatus().equalsIgnoreCase(Constants.NEW_ACTIVE)) {
                        throw new RuntimeException("Customer status is Active so it can not be an NewActivation");
                    } else if (customers.getStatus().equalsIgnoreCase(Constants.INACTIVE) && requestDTO.getChangedStatus().equalsIgnoreCase(Constants.NEW_ACTIVE)) {
                        throw new RuntimeException("Customer status is InActive so it can not be an NewActivation");
                    } else if (customers.getStatus().equalsIgnoreCase(Constants.TERMINATE) && requestDTO.getChangedStatus().equalsIgnoreCase(Constants.NEW_ACTIVE)) {
                        throw new RuntimeException("Customer status is Terminate so it can not be an NewActivation");
                    } else if (customers.getStatus().equalsIgnoreCase(Constants.SUSPEND) && requestDTO.getChangedStatus().equalsIgnoreCase(Constants.NEW_ACTIVE)) {
                        throw new RuntimeException("Customer status is Suspend so it can not be an NewActivation");
                    } else {
                        throw new RuntimeException("Change Status is not Proper.");
                    }
                }
            } else if (customers.getStatus().equalsIgnoreCase(Constants.ACTIVE) || customers.getStatus().equalsIgnoreCase(Constants.INACTIVE) || customers.getStatus().equalsIgnoreCase(Constants.TERMINATE) || customers.getStatus().equalsIgnoreCase(Constants.SUSPEND) || customers.getStatus().equalsIgnoreCase(Constants.NEW_ACTIVE)) {

                if (customers.getStatus().equalsIgnoreCase(Constants.ACTIVE) && requestDTO.getChangedStatus().equalsIgnoreCase(Constants.NEW_ACTIVE)) {
                    throw new RuntimeException("Customer status is Active so it can not be an NewActivation");
                } else if (customers.getStatus().equalsIgnoreCase(Constants.INACTIVE) && requestDTO.getChangedStatus().equalsIgnoreCase(Constants.NEW_ACTIVE)) {
                    throw new RuntimeException("Customer status is InActive so it can not be an NewActivation");
                } else if (customers.getStatus().equalsIgnoreCase(Constants.TERMINATE) && requestDTO.getChangedStatus().equalsIgnoreCase(Constants.NEW_ACTIVE)) {
                    throw new RuntimeException("Customer status is Terminate so it can not be an NewActivation");
                } else if (customers.getStatus().equalsIgnoreCase(Constants.SUSPEND) && requestDTO.getChangedStatus().equalsIgnoreCase(Constants.ACTIVE)) {
                    throw new RuntimeException("Customer status is Suspend so it can not be an NewActivation");
                } else {
                    throw new RuntimeException("Change Status is not Proper.");
                }
            } else {
                throw new RuntimeException("Change Status is not Proper.");
            }
            customers = customersService.update(customers);

            //Subscriber Update
            String newValue = customers.getStatus();
            SubscriberUpdateUtils.updateSubscriber(oldValue, newValue, UpdateConstant.UPDATE_STATUS, customers, requestDTO.getRemarks(), null);
            if (customers.getStatus().equalsIgnoreCase("Terminate")) {
                customersService.checkCustomerTerminationFlow(customers.getId());
            }

            return getBasicDetailsOfSubscriber(customers);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<CustomersBasicDetailsPojo> searchCustomer(String searchText) {
        String SUBMODULE = MODULE + " [searchCustomersCustom()] ";
        try {
            QCustomers customer = QCustomers.customers;
            Predicate builder = null;
            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                builder = customer.isNotNull().andAnyOf(customer.firstname.startsWithIgnoreCase(searchText), customer.lastname.startsWithIgnoreCase(searchText), customer.phone.startsWith(searchText), customer.email.startsWith(searchText), customer.username.startsWith(searchText)).and(customer.isDeleted.isFalse());
            }
            if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                builder = customer.isNotNull().andAnyOf(customer.firstname.startsWithIgnoreCase(searchText), customer.lastname.startsWithIgnoreCase(searchText), customer.phone.startsWith(searchText), customer.email.startsWith(searchText), customer.username.startsWith(searchText)).and(customer.isDeleted.isFalse()).and(customer.partner.id.eq(getLoggedInUserPartnerId()));
            }
            List<Customers> customerList = (List<Customers>) customersRepository.findAll(builder);
            return customerList.stream().map(data -> {
                try {
                    return subscriberMapper.domainToDTO(data, new CycleAvoidingMappingContext());
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<CustomerPlansModel> getExpiry(Integer customerId) {
        String SUBMODULE = MODULE + " [getExpiry()] ";
        try {
            List<CustomerPlansModel> dataPlanList = getActivePlanList(customerId, false);
            if (null != dataPlanList && 0 < dataPlanList.size()) {
                return dataPlanList.stream().filter(dto -> null != dto.getService() && dto.getService().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA)).collect(Collectors.toList());
            }
            return new ArrayList<>();
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public CustomersBasicDetailsPojo changeExpiry(ExpiryDTO requestDTO) throws Exception {
        String SUBMODULE = MODULE + " [changeExpiry()] ";
        Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
        try {
            if (requestDTO.getPlanId() != null) {
                CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(requestDTO.getPlanmapid());

                if (requestDTO.getPlanId().equals(custPlanMappping.getPlanId())) {
                    LocalDateTime revisedExpiry = LocalDateTime.of(requestDTO.getRevisedExpiryDate(), LocalTime.of(23, 59, 59));
                    custPlanMappping.setExpiryDate(revisedExpiry);
                }
                custPlanMappingService.update(custPlanMappping, "");

                //Subscriber Update
                SubscriberUpdateUtils.updateSubscriber(requestDTO.getCurrentExpiryDate(), requestDTO.getRevisedExpiryDate().toString(), UpdateConstant.UPDATE_EXPIRY, customers, requestDTO.getRemarks(), null);
                return getBasicDetailsOfSubscriber(customersRepository.findById(requestDTO.getCustId()).get());
            } else {
                throw new RuntimeException("Plan id is required");
            }
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * @return List<DocumentTypeModel>
     * This method return all the documentType with it's subType.
     */
    public List<CommonListDTO> getDocumentList(String docType) {
        String SUBMODULE = MODULE + " [getDocumentList()] ";
        try {
            List<CommonListDTO> docTypeCommonList = commonListService.getCommonListByType(docType);
            if (null != docTypeCommonList && 0 < docTypeCommonList.size()) {
                for (CommonListDTO commonDTO : docTypeCommonList) {
                    List<CommonListDTO> subTypeList = commonListService.getCommonListByType(commonDTO.getValue());
                    if (null != subTypeList && 0 < subTypeList.size()) {
                        commonDTO.setSubTypeList(subTypeList);
                    }
                }
                return docTypeCommonList;
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return new ArrayList<>();
    }

    @Transactional
    public ApplyChargeResponseDTO applyCharge(ApplyChargeRequestDTO requestDTO, Customers customers, Charge charge) throws Exception {
        String SUBMODULE = MODULE + " [changeCharge()] ";
        ApplyChargeResponseDTO applyChargeResponseDTO = new ApplyChargeResponseDTO();
        try {
            CustChargeDetails custChargeDetails = new CustChargeDetails();
            custChargeDetails.setChargeid(charge.getId());
            custChargeDetails.setChargetype(charge.getChargetype());
            custChargeDetails.setCustomer(customers);
            custChargeDetails.setActualprice(Double.parseDouble(new DecimalFormat("##.##").format(charge.getActualprice())));
            custChargeDetails.setValidity(0.0);
            custChargeDetails.setIsUsed(false);
            custChargeDetails.setPrice(Double.parseDouble(new DecimalFormat("##.##").format(charge.getPrice())));
            custChargeDetails.setIs_reversed(false);
            custChargeDetails.setRemarks(requestDTO.getRemarks());

            TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO(null, null, customers.getId(), charge.getId());
            if (taxDetailCountReqDTO != null) {
                Double taxAmount = taxService.taxCalculationByCharge(taxDetailCountReqDTO);
                custChargeDetails.setTaxamount(Double.parseDouble(new DecimalFormat("##.##").format(taxAmount)));
            } else {
                custChargeDetails.setTaxamount(0.0);
            }

            LocalDateTime chargeDate = null;
            if (requestDTO.getCharge_date() != null) {
                chargeDate = LocalDateTime.of(requestDTO.getCharge_date(), LocalTime.now());
                custChargeDetails.setCharge_date(chargeDate);
            }

            //Set Start Date of charge
            if (requestDTO.getStartdate() == null) {
                if (chargeDate != null) {
                    custChargeDetails.setStartdate(chargeDate);
                } else {
                    throw new RuntimeException("Charge date can not be null");
                }
            } else {
                LocalDateTime startDate = LocalDateTime.of(requestDTO.getStartdate(), LocalTime.now());
                custChargeDetails.setStartdate(startDate);
                custChargeDetails.setCharge_date(startDate);
            }

            //Set End Date of charge
            if (requestDTO.getEnddate() == null && requestDTO.getCharge_date() == null && requestDTO.getIsUnlimited() != null) {
                if (requestDTO.getIsUnlimited()) {
                    LocalDateTime endDate = LocalDateTime.of(LocalDate.of(1999, 12, 31), LocalTime.of(00, 00, 00));
                    custChargeDetails.setEnddate(endDate);
                } else {
                    throw new RuntimeException("Please select End Date or set as unlimited");
                }
            }
            if (requestDTO.getEnddate() != null) {
                LocalDateTime endDate = LocalDateTime.of(requestDTO.getEnddate(), LocalTime.of(23, 59, 59));
                custChargeDetails.setEnddate(endDate);
            }
            custChargeDetails = this.custChargeService.save(custChargeDetails);

            //Update Customer ledger
            String desc = "Charge Apply For " + charge.getName();
            CustomerLedgerDtls customerLedgerDtls = new CustomerLedgerDtls();
            customerLedgerDtls.setCustomer(customers);
            customerLedgerDtls.setAmount(custChargeDetails.getPrice() + custChargeDetails.getTaxamount());
            customerLedgerDtls.setCreditdocid(null);
            customerLedgerDtls.setDebitdocid(null);
            customerLedgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_INVOICE);
            customerLedgerDtls.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
            customerLedgerDtls.setDescription(desc);
            customerLedgerDtlsService.save(customerLedgerDtls);

            CustomerLedger customerLedger = customerLedgerService.getCustomerLeger(customers).get(0);
            Double totalDue = customerLedger.getTotaldue() + (custChargeDetails.getPrice() + custChargeDetails.getTaxamount());
            customerLedger.setTotaldue(Double.parseDouble(new DecimalFormat("##.##").format(totalDue)));
            customerLedgerService.save(customerLedger);
            requestDTO.setCharge_name(charge.getName());
            requestDTO.setCustChargeId(custChargeDetails.getId());
            applyChargeResponseDTO.setBasicChargeDetails(requestDTO);
            applyChargeResponseDTO.setCustomersBasicDetails(this.getBasicDetailsOfSubscriber(customers));

            //Send communication to customer
            CommunicationHelper communicationHelper = new CommunicationHelper();
            Map<String, String> map = new HashMap<>();
            map.put(CommunicationConstant.CHARGE_NAME, charge.getName());
            map.put(CommunicationConstant.AMOUNT, String.valueOf(Double.parseDouble(new DecimalFormat("##.##").format(custChargeDetails.getPrice() + custChargeDetails.getTaxamount()))));
            map.put(CommunicationConstant.DESTINATION, customers.getMobile());
            map.put(CommunicationConstant.EMAIL, customers.getEmail());
            communicationHelper.generateCommunicationDetails(CommunicationConstant.CHARGE_RECIEVED, Collections.singletonList(map));

            //Subscriber Update
            String newValue = SubscriberUpdateUtils.customString(Arrays.asList(charge.getName(), String.valueOf((custChargeDetails.getPrice() + custChargeDetails.getTaxamount()))));
            SubscriberUpdateUtils.updateSubscriber(null, newValue, UpdateConstant.APPLY_CHARGE, customers, requestDTO.getRemarks(), null);
        } catch (RuntimeException e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return applyChargeResponseDTO;
    }

    public ReverableChargePojo getReversibleCharge(Customers customers) throws Exception {
        ReverableChargePojo reverableChargePojo = new ReverableChargePojo();
        String SUBMODULE = MODULE + " [getReversibleCharge()] ";
        try {
            List<CommonListDTO> commonListDTOList = commonListService.getCommonListByType(TypeConstants.REVERSABLE_CHARGE_TYPE);
            List<CustChargeDetails> custChargeDetails = this.custChargeService.findByCustomerId(customers.getId());
            List<CustChargeDetailsPojo> custChargeDetailsPojoList = null;
            if (custChargeDetails != null && custChargeDetails.size() > 0) {
                custChargeDetailsPojoList = custChargeDetails.stream().map(data -> custChargeDetailMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            }
            if (custChargeDetailsPojoList != null && commonListDTOList != null) {
                reverableChargePojo.setCustChargeDetailsList(custChargeDetailsPojoList);
                reverableChargePojo.setReversalTypeCommonList(commonListDTOList);
                return reverableChargePojo;
            } else throw new RuntimeException("Charges not found");
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public ReversableChargeResponseDTO reverseCharge(ReverseChargeRequestDTO requestDTO, Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [reverseCharge()] ";
        ReversableChargeResponseDTO reversableChargeResponseDTO = new ReversableChargeResponseDTO();
        try {
            CustChargeDetails custChargeDetails = this.custChargeService.findByChargeByid(requestDTO.getCharge_id());
            Charge charge = null;
            CustChargeDetails tempCustChargeDetails = new CustChargeDetails(custChargeDetails);
            if (!custChargeDetails.getIs_reversed()) {
                custChargeDetails.setRev_amt(Double.parseDouble(new DecimalFormat("##.##").format(requestDTO.getRev_amt())));
                custChargeDetails.setRev_date(requestDTO.getRev_date());
                custChargeDetails.setRev_remarks(requestDTO.getRev_remarks());
                custChargeDetails.setIs_reversed(true);

                this.custChargeService.save(custChargeDetails);
                charge = this.chargeService.get(custChargeDetails.getChargeid(), customers.getMvnoId());
                String desc = "Reverse Charge Against " + charge.getName();
                CustomerLedgerDtls customerLedgerDtls = new CustomerLedgerDtls();
                customerLedgerDtls.setCustomer(customers);
                customerLedgerDtls.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(requestDTO.getRev_amt())));
                customerLedgerDtls.setCreditdocid(null);
                customerLedgerDtls.setDebitdocid(null);
                customerLedgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_PAYMENT);
                customerLedgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                customerLedgerDtls.setDescription(desc);
                customerLedgerDtlsService.save(customerLedgerDtls);

                CustomerLedger customerLedger = customerLedgerService.getCustomerLeger(customers).get(0);
                if (customerLedger != null) {
                    Double totalpaid = customerLedger.getTotalpaid() + requestDTO.getRev_amt();
                    customerLedger.setTotalpaid(Double.parseDouble(new DecimalFormat("##.##").format(totalpaid)));
                    customerLedgerService.save(customerLedger);
                } else {
                    throw new RuntimeException("Customer leger not found");
                }
            } else {
                throw new RuntimeException("Given charge is already reversed");
            }
            reversableChargeResponseDTO.setBasicReverseCharge(requestDTO);
            reversableChargeResponseDTO.setCustomersBasicDetails(this.getBasicDetailsOfSubscriber(customers));

            //Subscriber Update
            String oldValue = SubscriberUpdateUtils.customString(Arrays.asList(charge.getName(), String.valueOf((custChargeDetails.getPrice() + custChargeDetails.getTaxamount()))));
            SubscriberUpdateUtils.updateSubscriber(oldValue, null, UpdateConstant.REVERSE_CHARGE, customers, requestDTO.getRev_remarks(), null);
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        }
        return reversableChargeResponseDTO;
    }

    public CalculateChargePojo calculateReverseCharge(CustChargeDetails custChargeDetails) throws Exception {
        LocalDateTime startDate = custChargeDetails.getStartdate();
        LocalDateTime endDate = custChargeDetails.getEnddate();
        LocalDateTime chargeDate = custChargeDetails.getCharge_date();
        Double totalamount = custChargeDetails.getPrice();
        Double refundAmount = totalamount;
        CalculateChargePojo calculateChargePojo = new CalculateChargePojo();

        if (startDate == null) {
            startDate = chargeDate;
        }

        if (null != startDate && null != endDate) {
            long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), endDate);
            long totalServicedDays = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
            if (remainingDays > totalServicedDays) {
                totalServicedDays = 0;
            }
            final Double oneDayAmount = totalamount / totalServicedDays;
            final long usedDays = totalServicedDays - remainingDays;
            if (totalServicedDays > 0) {
                refundAmount = (totalamount - (oneDayAmount * usedDays));
            } else if (totalServicedDays == 0) {
                refundAmount = totalamount;
            } else {
                refundAmount = 0.0;
            }
        }

        calculateChargePojo.setCustChargeId(custChargeDetails.getId());
        calculateChargePojo.setFullAmount(Double.parseDouble(new DecimalFormat("##.##").format(totalamount)));
        calculateChargePojo.setProratedAmount(Double.parseDouble(new DecimalFormat("##.##").format(refundAmount)));
        return calculateChargePojo;
    }

    public MacUpdateModel getMacDetails(Integer customerId) {
        String SUBMODULE = MODULE + " [getMacDetails()] ";
        MacUpdateModel macDetailsModel = new MacUpdateModel();
        try {
            macDetailsModel.setMacTelFlag(customersRepository.findById(customerId).get().getMactelflag());
            List<CustMacMappping> macAddresses = custMacMapppingService.findMacAddressByCustomerId(customerId);
            List<MacUpdateDetailsModel> macUpdateDetailsModelList = new ArrayList<>();
            for (CustMacMappping custMacMappping : macAddresses) {
                MacUpdateDetailsModel macUpdateDetailsModel = new MacUpdateDetailsModel();
                macUpdateDetailsModel.setId(custMacMappping.getId());
                macUpdateDetailsModel.setMacAddress(custMacMappping.getMacAddress());
                macUpdateDetailsModel.setIsDeleted(custMacMappping.getIsDeleted());
                macUpdateDetailsModelList.add(macUpdateDetailsModel);
            }
            macDetailsModel.setMacAddresses(macUpdateDetailsModelList);
            macDetailsModel.setCustId(customerId);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return macDetailsModel;
    }

    public CustomersBasicDetailsPojo updateMacDetails(MacUpdateModel requestDTO, Integer mvnoId) throws Exception {
        String SUBMODULE = MODULE + " [updateMacDetails()] ";
        List<CustMacMappping> newCustMacMapppingList = new ArrayList<>();
        List<CustMacMappping> oldCustMacMapppingList = new ArrayList<>();
        try {
            if (requestDTO.getMacTelFlag()) {
                requestDTO.getMacAddresses().stream().forEach(dto -> {
                    CustMacMappping custMacMappping = dto.getId() == null ? new CustMacMappping() : custMacMapppingService.get(dto.getId(), mvnoId);
                    oldCustMacMapppingList.add(new CustMacMappping(custMacMappping));
                    custMacMappping.setCustomer(customersRepository.findById(requestDTO.getCustId()).get());
                    custMacMappping.setMacAddress(dto.getMacAddress());
                    custMacMappping.setIsDeleted(dto.getIsDeleted() != null && dto.getIsDeleted());
                    newCustMacMapppingList.add(custMacMapppingService.update(custMacMappping));
                });
            } else resetMacDetails(requestDTO.getCustId(), requestDTO.getRemarks());
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            customers.setMactelflag(requestDTO.getMacTelFlag());
            customers = customersService.update(customers);

            //Subscriber Update
            String oldValue = SubscriberUpdateUtils.customString(oldCustMacMapppingList.stream().map(CustMacMappping::getMacAddress).collect(Collectors.toList()));
            String newValue = SubscriberUpdateUtils.customString(newCustMacMapppingList.stream().map(CustMacMappping::getMacAddress).collect(Collectors.toList()));
            SubscriberUpdateUtils.updateSubscriber(oldValue, newValue, UpdateConstant.UPDATE_MAC, customers, requestDTO.getRemarks(), null);
            return getBasicDetailsOfSubscriber(customers);

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public CustomersBasicDetailsPojo resetMacDetails(Integer customerId, String remarks) throws Exception {
        String SUBMODULE = MODULE + " [resetMacDetails()] ";
        try {
            custMacMapppingService.deleteByCustomerId(customerId);
            Customers customers = customersRepository.findById(customerId).get();
            //Subscriber Update
            SubscriberUpdateUtils.updateSubscriber(null, null, UpdateConstant.RESET_MAC, customers, remarks, null);
            return getBasicDetailsOfSubscriber(customersRepository.findById(customerId).get());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public ContactDetailsDTO getContactDetails(CustomersPojo pojo) {
        String SUBMODULE = MODULE + " [getContactDetails()] ";
        ContactDetailsDTO contactDetailsDTO = new ContactDetailsDTO();
        try {
            contactDetailsDTO.setMobile(pojo.getMobile());
            contactDetailsDTO.setAltMobile(pojo.getAltmobile());
            contactDetailsDTO.setEmail(pojo.getEmail());
            contactDetailsDTO.setAltEmail(pojo.getAltemail());
            contactDetailsDTO.setPhone(pojo.getPhone());
            contactDetailsDTO.setAltPhone(pojo.getAltphone());
            contactDetailsDTO.setFax(pojo.getFax());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return contactDetailsDTO;
    }

    public CustomersBasicDetailsPojo updateContactDetails(ContactDetailsDTO contactDetailsDTO) throws Exception {
        String SUBMODULE = MODULE + " [updateContactDetails()] ";
        Customers customers = customersRepository.findById(contactDetailsDTO.getCustId()).get();
        String oldValue = SubscriberUpdateUtils.customString(Arrays.asList(customers.getMobile(), customers.getAltmobile(), customers.getEmail(), customers.getAltemail(), customers.getPhone(), customers.getAltphone(), customers.getFax()));
        Customers tempCustomers = new Customers(customers);
        try {
            customers.setMobile(contactDetailsDTO.getMobile());
            customers.setAltmobile(contactDetailsDTO.getAltMobile());
            customers.setEmail(contactDetailsDTO.getEmail());
            customers.setAltemail(contactDetailsDTO.getAltEmail());
            customers.setPhone(contactDetailsDTO.getPhone());
            customers.setAltphone(contactDetailsDTO.getAltPhone());
            customers.setFax(contactDetailsDTO.getFax());
            customers = customersService.update(customers);

            //Subscriber Update
            String newValue = SubscriberUpdateUtils.customString(Arrays.asList(customers.getMobile(), customers.getAltmobile(), customers.getEmail(), customers.getAltemail(), customers.getPhone(), customers.getAltphone(), customers.getFax()));
            SubscriberUpdateUtils.updateSubscriber(oldValue, newValue, UpdateConstant.UPDATE_CONTACT_DETAILS, customers, contactDetailsDTO.getRemarks(), null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return getBasicDetailsOfSubscriber(customersRepository.findById(contactDetailsDTO.getCustId()).get());
    }

    public BasicDetailsModel getBasicDetails(CustomersPojo pojo) {
        String SUBMODULE = MODULE + " [getBasicDetails()] ";
        BasicDetailsModel basicDetailsModel = new BasicDetailsModel();
        try {
            basicDetailsModel.setTitleList(commonListService.getCommonListByType(TypeConstants.TITLE));
            basicDetailsModel.setTitle(pojo.getTitle());
            basicDetailsModel.setName(pojo.getFirstname());
            basicDetailsModel.setAadharNumber(pojo.getAadhar());
            basicDetailsModel.setContactPerson(pojo.getContactperson());
            basicDetailsModel.setGst(pojo.getGst());
            basicDetailsModel.setPan(pojo.getPan());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return basicDetailsModel;
    }

    public CustomersBasicDetailsPojo updateBasicDetails(BasicDetailsDTO basicDetailsDTO) throws Exception {
        String SUBMODULE = MODULE + " [updateBasicDetails()] ";
        Customers customers = customersRepository.findById(basicDetailsDTO.getCustId()).get();
        String oldValue = SubscriberUpdateUtils.customString(Arrays.asList(customers.getTitle(), customers.getFirstname(), customers.getAadhar(), customers.getContactperson(), customers.getGst(), customers.getPan()));
        Customers tempCustomers = new Customers(customers);
        try {
            customers.setTitle(basicDetailsDTO.getTitle());
            customers.setFirstname(basicDetailsDTO.getName());
            customers.setAadhar(basicDetailsDTO.getAadharNumber());
            customers.setContactperson(basicDetailsDTO.getContactPerson());
            customers.setGst(basicDetailsDTO.getGst());
            customers.setPan(basicDetailsDTO.getPan());
            customers = customersService.update(customers);
            //Subscriber Update
            String newValue = SubscriberUpdateUtils.customString(Arrays.asList(customers.getTitle(), customers.getFirstname(), customers.getAadhar(), customers.getContactperson(), customers.getGst(), customers.getPan()));
            SubscriberUpdateUtils.updateSubscriber(oldValue, newValue, UpdateConstant.UPDATE_Basic_DETAILS, customers, basicDetailsDTO.getRemarks(), null);

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return getBasicDetailsOfSubscriber(customersRepository.findById(basicDetailsDTO.getCustId()).get());
    }

    public NetworkDetailsModel getNetworkDetails(Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [getNetworkDetails()] ";
        NetworkDetailsModel networkDetailsModel = new NetworkDetailsModel();
        try {
            if (commonListService.getCommonListByType(TypeConstants.NETWORK) != null) {
                networkDetailsModel.setNetworkType(commonListService.getCommonListByType(TypeConstants.NETWORK));
            }
            if (ipPoolService.getAllEntities(customers.getMvnoId()) != null && ipPoolService.getAllEntities(customers.getMvnoId()).size() > 0) {
                networkDetailsModel.setDefaultPool(ipPoolService.getAllEntities(customers.getMvnoId()));
            }
            if (serviceAreaService.getAllEntities(customers.getMvnoId()) != null && serviceAreaService.getAllEntities(customers.getMvnoId()).size() > 0) {
                networkDetailsModel.setServiceArea(serviceAreaService.getAllEntities(customers.getMvnoId()));
            }
            if (commonListService.getCommonListByType(TypeConstants.SERVICE_TYPE) != null) {
                networkDetailsModel.setServiceType(commonListService.getCommonListByType(TypeConstants.SERVICE_TYPE));
            }
            if (customers.getDefaultpoolid() != null) {
                if (ipPoolService.getEntityById(customers.getDefaultpoolid(), customers.getMvnoId()) != null) {
                    networkDetailsModel.setSelectedDefaultIpPool(ipPoolService.getEntityById(customers.getDefaultpoolid(), customers.getMvnoId()));
                }
            }

            if (serviceAreaMapper.domainToDTO(customers.getServicearea(), new CycleAvoidingMappingContext()) != null) {
                networkDetailsModel.setSelectedServiceArea(serviceAreaMapper.domainToDTO(customers.getServicearea(), new CycleAvoidingMappingContext()));
            }
            if (networkDeviceMapper.domainToDTO(customers.getNetworkdevices(), new CycleAvoidingMappingContext()) != null) {
                networkDetailsModel.setSelectedNetworkDeviceDTO(networkDeviceMapper.domainToDTO(customers.getNetworkdevices(), new CycleAvoidingMappingContext()));
            }
            if (customers.getOltslotid() != null) {
                if (oltSlotService.getEntityById(customers.getOltslotid(), customers.getMvnoId()) != null) {
                    networkDetailsModel.setSelectedOltSlotDetailDTO(oltSlotService.getEntityById(customers.getOltslotid(), customers.getMvnoId()));
                }
            }
            if (customers.getOltportid() != null) {
                if (oltPortService.getEntityById(customers.getOltportid(), customers.getMvnoId()) != null) {
                    networkDetailsModel.setSelectedOltPortDetailsDTO(oltPortService.getEntityById(customers.getOltportid(), customers.getMvnoId()));
                }
            }
            if (customers.getNetworktype() != null) {
                networkDetailsModel.setSelectedNetworkType(customers.getNetworktype());
            }
            if (customers.getOnuid() != null) {
                networkDetailsModel.setSelectedOnuId(customers.getOnuid());
            }
            if (customers.getStrconntype() != null) {
                networkDetailsModel.setSelectedConnectionType(customers.getStrconntype());
            }
            if (customers.getServicetype() != null && customers.getServicetype().trim().length() > 0) {
                networkDetailsModel.setSelectedServiceType(customers.getServicetype());
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return networkDetailsModel;
    }

    public CustomersBasicDetailsPojo updateNetworkDetails(NetworkDetailsDTO networkDetailsDTO) throws Exception {
        String SUBMODULE = MODULE + " [updateNetworkDetails()] ";
        Customers customers = customersRepository.findById(networkDetailsDTO.getCustId()).get();
        String oldValue = SubscriberUpdateUtils.customString(Arrays.asList(null != customers.getNetworktype() ? customers.getNetworktype() : "-", null != customers.getNetworkdevices() ? customers.getNetworkdevices().getName() : "-", null != customers.getOltportid() ? customers.getOltportid().toString() : "-", null != customers.getOltslotid() ? customers.getOltslotid().toString() : "-", null != customers.getOnuid() ? customers.getOnuid() : "-", null != customers.getServicearea() ? customers.getServicearea().getName() : "-", null != customers.getStrconntype() ? customers.getStrconntype() : "-", null != customers.getServicetype() ? customers.getServicetype() : "-"));

        Customers tempCustomers = new Customers(customers);
        try {
            if (networkDetailsDTO.getNetworkType() != null) {
                customers.setNetworktype(networkDetailsDTO.getNetworkType());
            }
            if (networkDetailsDTO.getDefaultPool() != null) {
                customers.setDefaultpoolid(networkDetailsDTO.getDefaultPool());
            }
            if (networkDetailsDTO.getOlt() != null) {
                customers.setNetworkdevices(networkDeviceRepository.findById(networkDetailsDTO.getOlt()).orElse(null));
            }
            if (networkDetailsDTO.getOltPort() != null) {
                customers.setOltportid(networkDetailsDTO.getOltPort());
            }
            if (networkDetailsDTO.getOltSlot() != null) {
                customers.setOltslotid(networkDetailsDTO.getOltSlot());
            }
            if (networkDetailsDTO.getOnuId() != null) {
                customers.setOnuid(networkDetailsDTO.getOnuId());
            }
            if (networkDetailsDTO.getServiceArea() != null) {
                customers.setServicearea(serviceAreaRepository.findById(networkDetailsDTO.getServiceArea()).orElse(null));
            }
            if (networkDetailsDTO.getConnectionType() != null) {
                customers.setStrconntype(networkDetailsDTO.getConnectionType());
            }
            if (networkDetailsDTO.getServiceType() != null) {
                customers.setServicetype(networkDetailsDTO.getServiceType());
            }
            customers = customersService.update(customers);

            //Subscriber Update
            String newValue = SubscriberUpdateUtils.customString(Arrays.asList(null != customers.getNetworktype() ? customers.getNetworktype() : null, null != customers.getNetworkdevices() ? customers.getNetworkdevices().getName() : null, null != customers.getOltportid() ? customers.getOltportid().toString() : null, null != customers.getOltslotid() ? customers.getOltslotid().toString() : null, null != customers.getOnuid() ? customers.getOnuid() : null, null != customers.getServicearea() ? customers.getServicearea().getName() : null, null != customers.getStrconntype() ? customers.getStrconntype() : null, null != customers.getServicetype() ? customers.getServicetype() : null));
            SubscriberUpdateUtils.updateSubscriber(oldValue, newValue, UpdateConstant.UPDATE_NETWORK_DETAILS, customers, networkDetailsDTO.getRemarks(), null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return getBasicDetailsOfSubscriber(customersRepository.findById(networkDetailsDTO.getCustId()).get());
    }

    public AddressDetailDTO getAddressDetails(Integer customerId) {
        String SUBMODULE = MODULE + " [getAddressDetails()] ";
        AddressDetailDTO addressDetailDTO = new AddressDetailDTO();
        Customers customers = customersRepository.findById(customerId).get();
        try {
            CustomerAddress presentAddress = customerAddressService.findByAddressTypeAndCustomer(SubscriberConstants.CUST_ADDRESS_PRESENT, customers);
            CustomerAddress permanentAddress = customerAddressService.findByAddressTypeAndCustomer(SubscriberConstants.CUST_ADDRESS_PERMANENT, customers);
            CustomerAddress paymentAddress = customerAddressService.findByAddressTypeAndCustomer(SubscriberConstants.CUST_ADDRESS_PAYMENT, customers);
            if (presentAddress != null) {
                AddressDetailsModel addressDetailsModel = new AddressDetailsModel();
                addressDetailsModel.setAddress1(presentAddress.getAddress1());
                addressDetailsModel.setAddress2(presentAddress.getAddress2());
                addressDetailsModel.setArea(null != presentAddress.getArea() ? presentAddress.getArea().getName() : "-");
                addressDetailsModel.setLandmark(presentAddress.getLandmark());
                addressDetailsModel.setPincode(null != presentAddress.getPincode() ? presentAddress.getPincode().getPincode() : "-");
                addressDetailsModel.setCountry(countryService.get(presentAddress.getCountryId(), getMvnoIdFromCurrentStaff(customerId)).getName());
                addressDetailsModel.setState(stateService.get(presentAddress.getStateId(), getMvnoIdFromCurrentStaff(customerId)).getName());
                addressDetailsModel.setCity(cityService.get(presentAddress.getCityId(), getMvnoIdFromCurrentStaff(customerId)).getName());
                addressDetailDTO.setPresent(addressDetailsModel);
            }
            if (permanentAddress != null) {
                AddressDetailsModel addressDetailsModel = new AddressDetailsModel();
                addressDetailsModel.setAddress1(permanentAddress.getAddress1());
                addressDetailsModel.setAddress2(permanentAddress.getAddress2());
                addressDetailsModel.setArea(null != permanentAddress.getArea() ? permanentAddress.getArea().getName() : "-");
                addressDetailsModel.setLandmark(permanentAddress.getLandmark());
                addressDetailsModel.setPincode(null != permanentAddress.getPincode() ? permanentAddress.getPincode().getPincode() : "-");
                addressDetailsModel.setCountry(countryService.get(permanentAddress.getCountryId(), getMvnoIdFromCurrentStaff(customerId)).getName());
                addressDetailsModel.setState(stateService.get(permanentAddress.getStateId(), getMvnoIdFromCurrentStaff(customerId)).getName());
                addressDetailsModel.setCity(cityService.get(permanentAddress.getCityId(), getMvnoIdFromCurrentStaff(customerId)).getName());
                addressDetailDTO.setPermanent(addressDetailsModel);
            }
            if (paymentAddress != null) {
                AddressDetailsModel addressDetailsModel = new AddressDetailsModel();
                addressDetailsModel.setAddress1(paymentAddress.getAddress1());
                addressDetailsModel.setAddress2(paymentAddress.getAddress2());
                addressDetailsModel.setArea(null != paymentAddress.getArea() ? paymentAddress.getArea().getName() : "-");
                addressDetailsModel.setLandmark(paymentAddress.getLandmark());
                addressDetailsModel.setPincode(null != paymentAddress.getPincode() ? paymentAddress.getPincode().getPincode() : "-");
                addressDetailsModel.setCountry(countryService.get(paymentAddress.getCountryId(), getMvnoIdFromCurrentStaff(customerId)).getName());
                addressDetailsModel.setState(stateService.get(paymentAddress.getStateId(), getMvnoIdFromCurrentStaff(customerId)).getName());
                addressDetailsModel.setCity(cityService.get(paymentAddress.getCityId(), getMvnoIdFromCurrentStaff(customerId)).getName());
                addressDetailDTO.setPayment(addressDetailsModel);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return addressDetailDTO;
    }

    public CustomersBasicDetailsPojo updateAddressDetails(AddressUpdateDTO addressUpdateDTO, Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [updateAddressDetails()] ";
        String oldValue;
        CustomerAddress customerAddress = customerAddressService.findByAddressTypeAndCustomer(addressUpdateDTO.getAddressType(), customersRepository.findById(addressUpdateDTO.getCustId()).get());

        try {
            if (null == customerAddress) {
                // Save a new entry
                oldValue = "";
                customerAddress = new CustomerAddress();
                customerAddress.setAddress1(addressUpdateDTO.getAddress().getAddress1());
                customerAddress.setAddress2(addressUpdateDTO.getAddress().getAddress2());
                customerAddress.setLandmark(addressUpdateDTO.getAddress().getLandmark());
                customerAddress.setArea(areaService.getMapper().dtoToDomain(areaService.getEntityById(addressUpdateDTO.getAddress().getAreaId().longValue(), customers.getMvnoId()), new CycleAvoidingMappingContext()));
                customerAddress.setPincode(pincodeService.getMapper().dtoToDomain(pincodeService.getEntityById(addressUpdateDTO.getAddress().getPincodeId().longValue(), customers.getMvnoId()), new CycleAvoidingMappingContext()));
                customerAddress.setCountry(countryService.get(addressUpdateDTO.getAddress().getCountryId(), customers.getMvnoId()));
                customerAddress.setState(stateService.get(addressUpdateDTO.getAddress().getStateId(), customers.getMvnoId()));
                customerAddress.setCity(cityService.get(addressUpdateDTO.getAddress().getCityId(), customers.getMvnoId()));
                customerAddressService.save(customerAddress);

            } else {
                oldValue = SubscriberUpdateUtils.customString(Arrays.asList(null != customerAddress.getAddress1() ? customerAddress.getAddress1() : "-", null != customerAddress.getLandmark() ? customerAddress.getLandmark() : "-", null != customerAddress.getArea() ? customerAddress.getArea().getName() : "-", null != customerAddress.getPincode() ? customerAddress.getPincode().getPincode() : "-", null != customerAddress.getCountry() ? customerAddress.getCountry().getName() : "-", null != customerAddress.getState() ? customerAddress.getState().getName() : "-", null != customerAddress.getCity() ? customerAddress.getCity().getName() : "-"));


                customerAddress.setAddress1(addressUpdateDTO.getAddress().getAddress1());
                customerAddress.setAddress2(addressUpdateDTO.getAddress().getAddress2());
                customerAddress.setLandmark(addressUpdateDTO.getAddress().getLandmark());
                customerAddress.setArea(areaService.getMapper().dtoToDomain(areaService.getEntityById(addressUpdateDTO.getAddress().getAreaId().longValue(), customers.getMvnoId()), new CycleAvoidingMappingContext()));
                customerAddress.setPincode(pincodeService.getMapper().dtoToDomain(pincodeService.getEntityById(addressUpdateDTO.getAddress().getPincodeId().longValue(), customers.getMvnoId()), new CycleAvoidingMappingContext()));
                customerAddress.setCountry(countryService.get(addressUpdateDTO.getAddress().getCountryId(), customers.getMvnoId()));
                customerAddress.setState(stateService.get(addressUpdateDTO.getAddress().getStateId(), customers.getMvnoId()));
                customerAddress.setCity(cityService.get(addressUpdateDTO.getAddress().getCityId(), customers.getMvnoId()));
                customerAddressService.update(customerAddress);
            }
            //Subscriber Update
            String newValue = SubscriberUpdateUtils.customString(Arrays.asList(null != customerAddress.getAddress1() ? customerAddress.getAddress1() : "-", null != customerAddress.getLandmark() ? customerAddress.getLandmark() : "-", null != customerAddress.getArea() ? customerAddress.getArea().getName() : "-", null != customerAddress.getPincode() ? customerAddress.getPincode().getPincode() : "-", null != customerAddress.getCountry() ? customerAddress.getCountry().getName() : "-", null != customerAddress.getState() ? customerAddress.getState().getName() : "-", null != customerAddress.getCity() ? customerAddress.getCity().getName() : "-"));
            SubscriberUpdateUtils.updateSubscriber(oldValue, newValue, UpdateConstant.UPDATE_ADDRESS_DETAILS, customers, addressUpdateDTO.getRemarks(), null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return getBasicDetailsOfSubscriber(customersRepository.findById(addressUpdateDTO.getCustId()).get());
    }

    @Transactional
    public RecordpaymentResponseDTO recordPayment(RecordPaymentRequestDTO requestDTO, Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [recordPayment()] ";
        RecordpaymentResponseDTO dto = new RecordpaymentResponseDTO();
        try {
            CreditDocument creditDocument = new CreditDocument();
            creditDocument.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(requestDTO.getPaymentAmount())));
            creditDocument.setCustomer(customers);
            //creditDocument.setChequedate(requestDTO.getChequeDate());
            creditDocument.setIs_reversed(false);
            creditDocument.setPaymentdate(requestDTO.getPaymentDate());
            creditDocument.setIsDelete(false);
            creditDocument.setRemarks(requestDTO.getRemarks());
            creditDocument.setPaymode(requestDTO.getPaymentMode());
            creditDocument.setTds_received_date(creditDocument.getTds_received_date());
            creditDocument.setStatus(UtilsCommon.PAYMENT_STATUS_APPROVED);
            if (getLoggedInUser() != null) {
                creditDocument.setApproverid(getLoggedInUser().getUserId());
            } else {
                creditDocument.setApproverid(2);
            }
            creditDocument.setCreatedate(LocalDateTime.now());
            creditDocument.setReferenceno(String.valueOf(UtilsCommon.getUniqueNumber()));
            creditDocument.setXmldocument(this.creditDocService.assemblePaymentXML(creditDocument, UtilsCommon.ADDR_TYPE_PRESENT, customers.getMvnoId()));
            if (UtilsCommon.PAYMENT_MODE_CHEQUE.equalsIgnoreCase(requestDTO.getPaymentMode())) {
                creditDocument.setPaydetails1(requestDTO.getBankName());
                creditDocument.setPaydetails2(requestDTO.getBranch());
                creditDocument.setPaydetails3(requestDTO.getChequeNo());
                if (requestDTO.getChequeDate() != null) {
                    creditDocument.setChequedate(requestDTO.getChequeDate());
                    creditDocument.setPaydetails4(requestDTO.getChequeDate().toString());
                }
            } else {
                creditDocument.setPaydetails4(requestDTO.getReferenceNo());
            }
            if (requestDTO.getIsTdsDeducted()) {
                creditDocument.setTdsflag(true);
                creditDocument.setTdsamount(Double.parseDouble(new DecimalFormat("##.##").format(requestDTO.getTdsAmount())));
                creditDocument.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(requestDTO.getPaymentAmount() - requestDTO.getTdsAmount())));
            } else {
                creditDocument.setTds_received(false);
                creditDocument.setTdsflag(false);
            }
            creditDocument.setMvnoId(customers.getMvnoId());
            creditDocument.setPaytype("Payment");
            creditDocument = this.creditDocService.save(creditDocument);
            dto.setCreditDocument(Collections.singletonList(creditDocument));
            String desc = "Payment received from" + ", " + requestDTO.getPaymentMode() + ", ref: " + requestDTO.getReferenceNo();
            CustomerLedgerDtls customerLedgerDtls = new CustomerLedgerDtls();
            customerLedgerDtls.setCreditdocid(creditDocument.getId());
            customerLedgerDtls.setCustomer(customers);
            customerLedgerDtls.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(requestDTO.getPaymentAmount())));
            customerLedgerDtls.setDebitdocid(null);
            customerLedgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_PAYMENT);
            customerLedgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
            customerLedgerDtls.setDescription(desc);
            customerLedgerDtlsService.save(customerLedgerDtls);

            List<CustomerLedger> customerLedgerList = customerLedgerService.getCustomerLeger(customers);
            if (!customerLedgerList.isEmpty()) {
                CustomerLedger customerLedger = customerLedgerList.get(0);
                customerLedger.setTotalpaid(customerLedger.getTotalpaid() + requestDTO.getPaymentAmount());
                customerLedgerService.save(customerLedger);
            }

            if (requestDTO.getPaymentMode().equalsIgnoreCase(SubscriberConstants.PAYMENT_TYPE_TDS)) {
                CreditDocument originaltxn = this.creditDocService.get(requestDTO.getCredit_doc_id(), customers.getMvnoId());
                creditDocument.setTds_received(true);
                if (!originaltxn.getTds_received()) {
                    if (!originaltxn.getTdsflag()) {
                        originaltxn.setTds_received(true);
                        originaltxn.setTds_credit_doc_id(creditDocument.getId());
                        originaltxn.setTdsamount(requestDTO.getPaymentAmount());
                        originaltxn.setTds_received_date(requestDTO.getPaymentDate());
                        originaltxn = this.creditDocService.save(originaltxn);
                        if (dto.getCreditDocument().size() > 0) {
                            List<CreditDocument> creditDocumentList = new ArrayList<>();
                            creditDocumentList.addAll(dto.getCreditDocument());
                            creditDocumentList.add(originaltxn);
                            dto.setCreditDocument(creditDocumentList);
                        } else {
                            dto.setCreditDocument(Collections.singletonList(originaltxn));
                        }
                    } else {
                        throw new RuntimeException("Payment is already TDS");
                    }
                } else {
                    throw new RuntimeException("Given payment is already tds deducted");
                }
            }
            dto.setRecordPaymentRequestDTO(requestDTO);
            // Subscriber Update
            String newValue = SubscriberUpdateUtils.customString(Arrays.asList(requestDTO.getPaymentMode(), String.valueOf((requestDTO.getPaymentAmount())), requestDTO.getReferenceNo()));
            SubscriberUpdateUtils.updateSubscriber(null, newValue, UpdateConstant.RECORD_PAYMENT, customers, requestDTO.getRemarks(), null);
            dto.setCustomersBasicDetails(this.getBasicDetailsOfSubscriber(customers));
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return dto;
    }

    public ReversablePaymentPojo getReversiblePayment(Customers customers) throws Exception {
        ReversablePaymentPojo reversablePaymentPojo = new ReversablePaymentPojo();
        String SUBMODULE = MODULE + " [getReversiblePayment] ";
        try {
            List<CommonListDTO> commonListDTOList = commonListService.getCommonListByType(TypeConstants.PAYMENT_MODE);
            List<CreditDocument> creditDocumentList = this.creditDocService.getCreditDocByCustomer(customers, false);
            List<CreditDocumentPojo> creditDocumentPojoList = creditDocumentList.stream().map(data -> {
                try {
                    return this.creditDocService.convertCreditDocumentModelToCreditDocumentPojo(data);
                } catch (Exception e) {
                    return null;
                }
            }).collect(Collectors.toList());
            reversablePaymentPojo.setCreditDocumentPojo(creditDocumentPojoList);
            reversablePaymentPojo.setPaymentModeCommonList(commonListDTOList);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return reversablePaymentPojo;
    }

    public ReversePaymentResponseDTO reversePayment(ReversePaymentRequestDTO requestDTO, Customers customers) throws Exception {
        ReversePaymentResponseDTO responseDTO = new ReversePaymentResponseDTO();
        String SUBMODULE = MODULE + " [reversePayment()] ";
        try {
            CreditDocument creditDocument = this.creditDocService.get(requestDTO.getPayment_id(), customers.getMvnoId());
            //  DebitDocument debitDocument = new DebitDocument();
            LocalDateTime localDateTime = LocalDateTime.now();
            if (!creditDocument.getIs_reversed()) {
                /*   debitDocument.setTotalamount(creditDocument.getAmount());
                debitDocument.setBilldate(localDateTime);
                debitDocument.setIs_credit_reversal(true);
                debitDocument.setIsDelete(false);
                debitDocument.setCredit_doc_id(creditDocument.getId());
                debitDocument.setDocnumber(String.valueOf(CommonUtils.getUniqueNumber()));
                debitDocument = this.debitDocService.save(debitDocument);*/
                creditDocument.setResevrsed_date(localDateTime.toLocalDate());
                // creditDocument.setResverse_debitdoc_id(debitDocument.getId());
                creditDocument.setIs_reversed(true);
                creditDocument = this.creditDocService.save(creditDocument);
                String desc = "Reverse Payment of " + creditDocument.getAmount();
                CustomerLedgerDtls customerLedgerDtls = new CustomerLedgerDtls();
                customerLedgerDtls.setCustomer(customers);
                customerLedgerDtls.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(creditDocument.getAmount())));
                customerLedgerDtls.setCreditdocid(creditDocument.getId());
                //  customerLedgerDtls.setDebitdocid(debitDocument.getId());
                customerLedgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_PAYMENT);
                customerLedgerDtls.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
                customerLedgerDtls.setDescription(desc);
                customerLedgerDtlsService.save(customerLedgerDtls);

                CustomerLedger customerLedger = customerLedgerService.getCustomerLeger(customers).get(0);
                if (customerLedger != null) {
                    Double totaldue = customerLedger.getTotaldue() + creditDocument.getAmount();
                    customerLedger.setTotaldue(Double.parseDouble(new DecimalFormat("##.##").format(totaldue)));
                    customerLedgerService.save(customerLedger);
                } else {
                    throw new RuntimeException("Customer leger not found");
                }
            } else {
                throw new RuntimeException("Given charge is already reversed");
            }
            // Subscriber Update
            SubscriberUpdateUtils.updateSubscriber(null, String.valueOf(requestDTO.getRev_amt()), UpdateConstant.REVERSE_PAYMENT, customers, requestDTO.getRev_remarks(), null);
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        responseDTO.setCustomersBasicDetails(this.getBasicDetailsOfSubscriber(customers));
        responseDTO.setBasicReversePayment(requestDTO);
        return responseDTO;
    }

    @Transactional
    public RecordpaymentResponseDTO updatePayment(RecordPaymentRequestDTO requestDTO, CreditDocument creditDocument, Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [updatePayment()] ";
        RecordpaymentResponseDTO dto = new RecordpaymentResponseDTO();

        //Old VALUE
        String oldValue = SubscriberUpdateUtils.customString(Arrays.asList(null != creditDocument.getPaymentdate() ? creditDocument.getPaymentdate().toString() : "-", null != creditDocument.getPaymode() ? creditDocument.getPaymode() : "-", null != creditDocument.getReferenceno() ? creditDocument.getReferenceno() : "-"));

        try {
            creditDocument.setCustomer(customers);
            creditDocument.setChequedate(requestDTO.getChequeDate());
            creditDocument.setIs_reversed(false);
            creditDocument.setPaymentdate(requestDTO.getPaymentDate());
            creditDocument.setIsDelete(false);
            creditDocument.setRemarks(requestDTO.getRemarks());
            creditDocument.setPaymode(requestDTO.getPaymentMode());
            creditDocument.setTds_received_date(creditDocument.getTds_received_date());
            creditDocument.setStatus(UtilsCommon.PAYMENT_STATUS_APPROVED);
            creditDocument.setApproverid(getLoggedInUserId());
            creditDocument.setRemarks(UtilsCommon.PAYMENT_STATUS_APPROVED);
            creditDocument.setCreatedate(LocalDateTime.now());
            creditDocument.setReferenceno(requestDTO.getReferenceNo());
            creditDocument.setXmldocument(this.creditDocService.assemblePaymentXML(creditDocument, UtilsCommon.ADDR_TYPE_PRESENT, customers.getMvnoId()));
            if (UtilsCommon.PAYMENT_MODE_CHEQUE.equalsIgnoreCase(requestDTO.getPaymentMode())) {
                creditDocument.setPaydetails1(requestDTO.getBankName());
                creditDocument.setPaydetails2(requestDTO.getBranch());
                creditDocument.setPaydetails3(requestDTO.getChequeNo());
                if (requestDTO.getChequeDate() != null) {
                    creditDocument.setChequedate(requestDTO.getChequeDate());
                    creditDocument.setPaydetails4(requestDTO.getChequeDate().toString());
                }
            } else {
                creditDocument.setPaydetails1(null);
                creditDocument.setPaydetails2(null);
                creditDocument.setPaydetails3(null);
                creditDocument.setChequedate(null);
                creditDocument.setPaydetails4(requestDTO.getReferenceNo());
                creditDocument.setReferenceno(requestDTO.getReferenceNo());
            }
            if (requestDTO.getIsTdsDeducted()) {
                creditDocument.setTdsflag(true);
                creditDocument.setTdsamount(requestDTO.getTdsAmount());
                creditDocument.setAmount(requestDTO.getPaymentAmount() - requestDTO.getTdsAmount());
            } else {
                creditDocument.setTds_received(false);
                creditDocument.setTdsflag(false);
            }

            creditDocument = this.creditDocService.save(creditDocument);

            //Old VALUE
            String newValue = SubscriberUpdateUtils.customString(Arrays.asList(null != creditDocument.getPaymentdate() ? creditDocument.getPaymentdate().toString() : "-", null != creditDocument.getPaymode() ? creditDocument.getPaymode() : "-", null != creditDocument.getReferenceno() ? creditDocument.getReferenceno() : "-"));

            if (requestDTO.getPaymentMode().equalsIgnoreCase(SubscriberConstants.PAYMENT_TYPE_TDS)) {
                CreditDocument originaltxn = this.creditDocService.get(requestDTO.getCredit_doc_id(), customers.getMvnoId());
                creditDocument.setTds_received(true);
                if (!originaltxn.getTds_received()) {
                    if (!originaltxn.getTdsflag()) {
                        originaltxn.setTds_received(true);
                        originaltxn.setTds_credit_doc_id(creditDocument.getId());
                        originaltxn.setTdsamount(requestDTO.getPaymentAmount());
                        originaltxn.setTds_received_date(requestDTO.getPaymentDate());
                        this.creditDocService.save(originaltxn);
                    } else {
                        throw new RuntimeException("Payment is already TDS");
                    }
                } else {
                    throw new RuntimeException("Given payment is already tds deducted");
                }
            }
            // Subscriber Update
            SubscriberUpdateUtils.updateSubscriber(oldValue, newValue, UpdateConstant.UPDATE_PAYMENT, customers, requestDTO.getRemarks(), null);
            dto.setCustomersBasicDetails(this.getBasicDetailsOfSubscriber(customers));
            dto.setRecordPaymentRequestDTO(requestDTO);
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return dto;
    }

    public List<CreditDocumentPojo> getTdsPendingPayment(Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [getTdsPendingPayment] ";
        List<CreditDocumentPojo> creditDocumentPojoList = null;
        try {
            List<CreditDocument> creditDocumentList = this.creditDocService.getTdspendingCreditDocByCustomer(customers);
            creditDocumentPojoList = creditDocumentList.stream().map(data -> {
                try {
                    return this.creditDocService.convertCreditDocumentModelToCreditDocumentPojo(data);
                } catch (Exception e) {
                    ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
                    return null;
                }
            }).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return creditDocumentPojoList;
    }

    public List<CreditDocumentPojo> getAllPayment(Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [getAllPayment] ";
        List<CreditDocumentPojo> creditDocumentPojoList = null;
        try {
            List<CreditDocument> creditDocumentList = this.creditDocService.getAllPaymentByCustomer(customers);
            creditDocumentPojoList = creditDocumentList.stream().map(data -> {
                try {
                    return this.creditDocService.convertCreditDocumentModelToCreditDocumentPojo(data);
                } catch (Exception e) {
                    ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
                    return null;
                }
            }).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return creditDocumentPojoList;
    }

    public StatusModel getCustomersStatus(Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [getStatus()] ";
        StatusModel statusModel = new StatusModel();
        try {
            List<CommonListDTO> commonListDTOS;
            List<CommonListDTO> responseList = new ArrayList<>();
            commonListDTOS = commonListService.getCommonListByType(TypeConstants.STATUS);
            Optional<CommonListDTO> currentStatusDTO = commonListDTOS.stream().filter(data -> data.getValue().equalsIgnoreCase(customers.getStatus())).findAny();
            String custStatus = currentStatusDTO.get().getValue();
            if (SubscriberConstants.NEW_ACTIVATION.equalsIgnoreCase(custStatus)) {
                for (int i = 0; i < commonListDTOS.size(); i++) {
                    if ((SubscriberConstants.ACTIVE).equalsIgnoreCase(commonListDTOS.get(i).getValue())) {
                        responseList.add(commonListDTOS.get(i));
                    }
                }
            }
            if (SubscriberConstants.ACTIVE.equalsIgnoreCase(custStatus)) {
                for (int i = 0; i < commonListDTOS.size(); i++) {
                    if (commonListDTOS.get(i).getValue().equalsIgnoreCase(SubscriberConstants.IN_ACTIVE) || commonListDTOS.get(i).getValue().equals(SubscriberConstants.SUSPEND)) {
                        responseList.add(commonListDTOS.get(i));
                    }
                }
            }
            if (SubscriberConstants.SUSPEND.equalsIgnoreCase(custStatus)) {
                for (int i = 0; i < commonListDTOS.size(); i++) {
                    if (commonListDTOS.get(i).getValue().equalsIgnoreCase(SubscriberConstants.ACTIVE) || commonListDTOS.get(i).getValue().equals(SubscriberConstants.TERMINATE)) {
                        responseList.add(commonListDTOS.get(i));
                    }
                }
            }
            if (SubscriberConstants.IN_ACTIVE.equalsIgnoreCase(custStatus)) {
                for (int i = 0; i < commonListDTOS.size(); i++) {
                    if (commonListDTOS.get(i).getValue().equalsIgnoreCase(SubscriberConstants.ACTIVE) || commonListDTOS.get(i).getValue().equals(SubscriberConstants.SUSPEND)) {
                        responseList.add(commonListDTOS.get(i));
                    }
                }
            }
            statusModel.setCustId(customers.getId());
            statusModel.setCurrentStatus(currentStatusDTO.orElse(null));
            statusModel.setChangedStatus(responseList);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return statusModel;
    }

    public boolean checkCustomerUniqueMobile(String mobileNo, Integer custId) {
        if (custId != null) {
            Customers cust = customersRepository.getOne(custId);
            if (cust != null && !cust.getIsDeleted() && cust.getId().equals(custId)) {
                if (null != cust.getMobile() && !"".equals(cust.getMobile()) && mobileNo.equalsIgnoreCase(cust.getMobile())) {
                    return false;
                }
            }
        }
        List<Customers> customersList = customersRepository.findAllByMobileAndIsDeletedIsFalseOrderByIdDesc(mobileNo);
        return null != customersList && 0 < customersList.size();
    }

    public boolean checkCustomerUniqueUsername(String userName) {
        List<Customers> customersList = customersRepository.findAllByUsernameAndIsDeletedIsFalseOrderByIdDesc(userName);
        return null != customersList && 0 < customersList.size();
    }

    public boolean checkCustomerUniqueCafno(String cafNo) {
        List<Customers> customersList = customersRepository.findAllByCafnoAndIsDeletedIsFalseOrderByIdDesc(cafNo);
        return null != customersList && 0 < customersList.size();
    }

    public boolean checkCustomerUniqueEmail(String email, Integer custId) {
        if (custId != null) {
            Customers cust = customersRepository.getOne(custId);
            if (cust != null && !cust.getIsDeleted() && cust.getId().equals(custId)) {
                if (null != cust.getEmail() && !"".equals(cust.getEmail()) && email.equalsIgnoreCase(cust.getEmail())) {
                    return false;
                }
            }
        }
        List<Customers> customersList = customersRepository.findAllByEmailAndIsDeletedIsFalseOrderByIdDesc(email);
        return null != customersList && 0 < customersList.size();
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Subscribers");
        List<CustomersPojo> customersPojoList = customersService.convertResponseModelIntoPojo(customersRepository.findAll());
        createExcel(workbook, sheet, CustomersPojo.class, customersPojoList, getFields());
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<CustomersPojo> customersPojoList = customersService.convertResponseModelIntoPojo(customersRepository.findAll());
        createPDF(doc, CustomersPojo.class, customersPojoList, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{CustomersPojo.class.getDeclaredField("id"), CustomersPojo.class.getDeclaredField("fullName"), CustomersPojo.class.getDeclaredField("username"), CustomersPojo.class.getDeclaredField("email"), CustomersPojo.class.getDeclaredField("mobile"), CustomersPojo.class.getDeclaredField("acctno"), CustomersPojo.class.getDeclaredField("outstanding"), CustomersPojo.class.getDeclaredField("status")};
    }

    public ChangePlanDTO getSubscriberCurrentPlan(Customers customers) throws Exception {

        List<ClientServicePojo> clientServicePojo = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CONVERT_VOL_BOOST_TOPUP);

        ChangePlanDTO changePlanDTO = new ChangePlanDTO();
        CustomerPlansModel customerPlansModel = this.getActivePlanList(customers.getId(), false).stream().filter(data -> data.getService().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA)).findAny().orElse(null);
        if (customerPlansModel != null) {
            PostpaidPlan postpaidPlan = this.planService.get(customerPlansModel.getPlanId(), customers.getMvnoId());
            CurrentPlanDTO currentPlanDTO = new CurrentPlanDTO();
            currentPlanDTO.setUserName(customers.getUsername());
            currentPlanDTO.setCurrentOutstanding(Double.parseDouble(new DecimalFormat("##.##").format(customers.getOutstanding())));
            currentPlanDTO.setCurrentPlanName(customerPlansModel.getPlanName());
            currentPlanDTO.setCustCurrentPlanId(customerPlansModel.getPlanId());
            if (customerPlansModel.getVolTotalQuota().equalsIgnoreCase("Unlimited")) {
                currentPlanDTO.setTotalDataQuota("Unlimited");
            } else {
                currentPlanDTO.setTotalDataQuota(Double.valueOf(customerPlansModel.getVolTotalQuota()).toString());
            }
            currentPlanDTO.setUsedDataQuota(Double.valueOf(customerPlansModel.getVolUsedQuota()));
            currentPlanDTO.setQuotaUnit(customerPlansModel.getVolQuotaUnit());
            currentPlanDTO.setQuotaunittime(customerPlansModel.getTimeQuotaUnit());
            final long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), customerPlansModel.getExpiryDate().toLocalDate());
            currentPlanDTO.setDaysToExpiry(remainingDays);
            if (postpaidPlan != null) {
                if (postpaidPlan.getValidity() > 0) {
                    final long totalServicedDays = ChronoUnit.DAYS.between(customerPlansModel.getStartDate().toLocalDate(), customerPlansModel.getExpiryDate().toLocalDate());
                    final Double oneDayAmount = (postpaidPlan.getOfferprice() / totalServicedDays);
                    final Double refundAmount = (remainingDays * oneDayAmount);
                    currentPlanDTO.setRefundableAmount((Math.round(refundAmount * 100) / 100.00));
                } else {
                    throw new RuntimeException("Validity can not be zero");
                }
            }
            changePlanDTO.setCurrentPlanDTO(currentPlanDTO);
        }
        PlanByPartnerReqDTO planByPartnerReqDTO = new PlanByPartnerReqDTO(customers.getPartner().getId(), CommonConstants.DATA_SERVICE_ID, CommonConstants.PLAN_GROUP_RENEW);
        List<PostpaidPlanPojo> postpaidPlans = this.planService.getPlanByPartnerId(planByPartnerReqDTO);
        List<CustomPlanDto> customPlanDtoList = new ArrayList<>();
        postpaidPlans.forEach(data -> {
            CustomPlanDto customPlanDto = new CustomPlanDto();
            customPlanDto.setPlanId(data.getId());
            customPlanDto.setPlanName(data.getDisplayName());
            customPlanDto.setQuotaType(data.getQuotatype());
            customPlanDto.setDataQuota(data.getQuota());
            customPlanDto.setTimeQuota(data.getQuotatime());
            customPlanDto.setQuotaUnit(data.getQuotaUnit());
            customPlanDto.setQuotaunittime(data.getQuotaunittime());
            customPlanDto.setValidity(data.getValidity());
            customPlanDto.setPrice(data.getOfferprice());
            customPlanDto.setActivationDate(LocalDateTime.now());


            customPlanDto.setExpiryDate(LocalDateTime.of(LocalDateTime.now().plusDays(data.getValidity().longValue()).toLocalDate(), LocalTime.of(00, 00, 00)));
            if (customerPlansModel != null) {
                LocalDateTime tempActivationDate = LocalDateTime.of(customerPlansModel.getExpiryDate().toLocalDate().plusDays(1L), LocalTime.of(00, 00, 00));
                LocalDateTime tempExpiryDate = tempActivationDate.plusDays(data.getValidity().longValue());
                if (customerPlansModel.getExpiryDate() != null) {
                    customPlanDto.setRenewalActivationDate(tempActivationDate);
                    customPlanDto.setRenewalExpiryDate(tempExpiryDate);
                }
            } else {
                customPlanDto.setRenewalActivationDate(null);
                customPlanDto.setRenewalExpiryDate(null);
            }
            customPlanDtoList.add(customPlanDto);
        });
        changePlanDTO.setCustomPlanDtoList(customPlanDtoList);
        return changePlanDTO;
    }

    @Transactional
    public CustomChangePlanDTO changePlan(ChangePlanRequestDTO requestDTO, Customers customers, Boolean onlinePurchase, Double onlinePaidAmount, String requestFrom, Double maxValidity) throws Exception {
        String SUBMODULE = MODULE + " [changePlan()] ";
        CustomChangePlanDTO customChangePlanDTO = new CustomChangePlanDTO();
        QuotaDtlsModel quotaDtlsModel = null;
        Boolean addQuotaInExistingPlan = false;
        Integer parentCustomerId = null;
        try {
            if (Objects.nonNull(customers.getParentCustomers())) {
                if (customers.getInvoiceType().equalsIgnoreCase("Group")) {
                    parentCustomerId = customers.getParentCustomers().getId();
                }
            }
//        	List<CustomerPlansModel> futurePlanList = this.subscriberService.getFuturePlanList(customers.getId());
            CustomersPojo customersPojo = customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext());
            PostpaidPlan postpaidPlan = planService.get(requestDTO.getPlanId(), customers.getMvnoId());
            List<CustomerPlansModel> activeCustPlanModelList;
            CustomerPlansModel activeCustPlanModel = new CustomerPlansModel();
            if (parentCustomerId != null) {
                activeCustPlanModelList = this.subscriberService.getActivePlanList(parentCustomerId, false);
            } else {
                activeCustPlanModelList = this.subscriberService.getActivePlanList(customers.getId(), false);
            }

            if (activeCustPlanModelList.size() > 0) {
                if (parentCustomerId != null) {
                    List<CustomerPlansModel> customerPlansModels = this.getActivePlanList(parentCustomerId, false).stream().filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                    if (customerPlansModels.size() > 0) {
                        activeCustPlanModel = customerPlansModels.get(0);
                    }
                } else {
                    List<CustomerPlansModel> customerPlansModels = this.getActivePlanList(customers.getId(), false).stream().filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                    if (customerPlansModels.size() > 0) {
                        activeCustPlanModel = customerPlansModels.get(0);
                    }
                }
            } else {
                ApplicationLogger.logger.error(SUBMODULE + "No Active Plan");
                activeCustPlanModel = null;
            }

            CustPlanMappping custPlanMappping = null;
            LocalDateTime startDate = null;
            LocalDateTime expiryDate = null;
            LocalDateTime todaysDate = LocalDateTime.now();
            Double taxAmount = 0.0;
            LocalDateTime endDate = null;
            Integer planValidityDays = 0;

            TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO(requestDTO.getPlanId(), null, customers.getId(), null);
            if (taxDetailCountReqDTO != null) {
                taxAmount = taxService.taxCalculationByPlan(taxDetailCountReqDTO, postpaidPlan.getChargeList());
            }
            if (!requestDTO.getIsAdvRenewal() && requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW)) {
                //This is in use
                PostpaidPlan tempPostpaidPlan = null;
                if (activeCustPlanModel != null) {
                    tempPostpaidPlan = this.planService.get(activeCustPlanModel.getPlanId(), customers.getMvnoId());
                    if (parentCustomerId != null) {
                        CustomerPlansModel finalActiveCustPlanModel1 = activeCustPlanModel;
                        custPlanMappping = customers.getParentCustomers().getPlanMappingList().stream().filter(data -> data.getId().intValue() == finalActiveCustPlanModel1.getPlanmapid().intValue()).findAny().orElse(null);
                    } else {
                        CustomerPlansModel finalActiveCustPlanModel = activeCustPlanModel;
                        custPlanMappping = customers.getPlanMappingList().stream().filter(data -> data.getId().intValue() == finalActiveCustPlanModel.getPlanmapid().intValue()).findAny().orElse(null);
                    }
                }
                if (custPlanMappping != null) {
                    //new logic

//                    if (requestDTO.getAddonStartDate() != null) {
//                        startDate = LocalDateTime.of(requestDTO.getAddonStartDate(), LocalTime.now());
//                    } else if (todaysDate.isBefore(custPlanMappping.getExpiryDate()))
//                        startDate = custPlanMappping.getExpiryDate().plusMinutes(1);
//                    else {
//                        startDate = todaysDate;
//                    }
                    if (parentCustomerId != null) {
                        startDate = custPlanMappping.getStartDate();
                        expiryDate = custPlanMappping.getExpiryDate();
                        endDate = expiryDate;
                    } else {
                        if (requestDTO.getAddonStartDate() != null) {
                            startDate = requestDTO.getAddonStartDate();
                        } else if (todaysDate.isBefore(custPlanMappping.getExpiryDate()) && requestDTO.getOnlinePurType() != null && !requestDTO.getOnlinePurType().equalsIgnoreCase(SubscriberConstants.PURCHASE_FROM_FLUTTERWAVE))
                            startDate = custPlanMappping.getExpiryDate().plusMinutes(1);
                        else if (requestDTO.getOnlinePurType() != null && requestDTO.getOnlinePurType().equalsIgnoreCase(SubscriberConstants.PURCHASE_FROM_FLUTTERWAVE))
                            startDate = todaysDate;
                        else {
                            startDate = todaysDate;
                        }
                        //new logic with neplai cal..
                        expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                        endDate = expiryDate;
                    }
                    List<CustomerPlansModel> getFuturePlanList = new ArrayList<>();
                    if (parentCustomerId != null) {
                        getFuturePlanList = this.getFuturePlanList(parentCustomerId, false).stream().filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                    } else {
                        getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                    }
                    if (parentCustomerId != null) {
                        if (getFuturePlanList.size() > 0) {
                            startDate = LocalDateTime.of(getFuturePlanList.get(0).getStartDate().toLocalDate(), LocalTime.now());
                            expiryDate = LocalDateTime.of(getFuturePlanList.get(0).getEndDate().toLocalDate(), LocalTime.now());
                            endDate = expiryDate;
                        }
                    } else {
                        if (requestDTO.getAddonStartDate() != null) {
                            startDate = requestDTO.getAddonStartDate();
                        } else if (requestDTO.getOnlinePurType() != null && requestDTO.getOnlinePurType().equalsIgnoreCase(SubscriberConstants.PURCHASE_FROM_FLUTTERWAVE)) {
                            startDate = LocalDateTime.now();
                        } else if (getFuturePlanList.size() > 0) {
                            startDate = LocalDateTime.of(getFuturePlanList.get(0).getEndDate().toLocalDate(), LocalTime.from(startDate));
                            startDate = startDate.plusMinutes(1L);
//                            expiryDate = LocalDateTime.of(startDate.plusDays(postpaidPlan.getValidity().longValue()).toLocalDate(), LocalTime.from(startDate));
                            if (maxValidity != null && maxValidity != 0.0) {
                                if (requestDTO.getPlanList() != null && requestDTO.getPlanList().get(0).getId() != requestDTO.getPlanId())
                                    startDate = LocalDateTime.of(getFuturePlanList.get(0).getStartDate().toLocalDate(), LocalTime.from(startDate));
//                                expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);//LocalDateTime.of(startDate.plusDays(maxValidity.longValue()).toLocalDate(), LocalTime.from(startDate));
                            }
                            expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                            endDate = expiryDate;
                        }
                    }
                } else {
                    if (requestDTO.getAddonStartDate() != null) {
                        startDate = requestDTO.getAddonStartDate();
                    } else {
                        startDate = todaysDate;
                    }
                    expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                    endDate = expiryDate;
                }

                if (requestDTO.getIsRefund()) {
                    final long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now().toLocalDate(), activeCustPlanModel.getEndDate().toLocalDate());
                    Double oneDayAmount;
                    Double refundAmount;
                    Double roundFigureAmount = 0.0;
                    if (tempPostpaidPlan != null) {
                        if (tempPostpaidPlan.getValidity() > 0) {
                            final long totalServicedDays = ChronoUnit.DAYS.between(activeCustPlanModel.getStartDate().toLocalDate(), activeCustPlanModel.getEndDate().toLocalDate());
                            oneDayAmount = (tempPostpaidPlan.getOfferprice() / totalServicedDays);
                            refundAmount = (remainingDays * oneDayAmount);
                            roundFigureAmount = (Math.round(refundAmount * 100) / 100.00);
                        } else throw new RuntimeException("Validity can not be zero");
                    }
                    CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
                    ledgerDtls.setCustomer(customers);
                    ledgerDtls.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(roundFigureAmount)));
                    ledgerDtls.setDescription("Refund against change plan");
                    ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                    ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_REFUND);
                    customerLedgerDtlsService.save(ledgerDtls);

                    //Update customer ledger
                    CustomerLedger customerLedger = this.customerLedgerService.getCustomerLeger(customers).get(0);
                    customerLedger.setTotalpaid(Double.parseDouble(new DecimalFormat("##.##").format(customerLedger.getTotalpaid() + roundFigureAmount)));
                    customerLedger.setCustomer(customers);
                    customerLedger.setUpdatedate(LocalDateTime.now());
                    customerLedgerService.save(customerLedger);
                }

                Long renewCustCount = null;
                if (customers.getPartner().getName() != null && !customers.getPartner().getName().equalsIgnoreCase(CommonConstants.DEFAULT_PARTNER)) {
                    commissionService.setPartnerCommission(requestDTO, customers, customers.getPartner(), requestFrom);
                }
            }

            if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON)) {
                //This is in use
                startDate = requestDTO.getAddonStartDate();

                expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                endDate = expiryDate;

                Boolean isFuture = null;
                Boolean isActive = null;

                if (startDate.toLocalDate().equals(LocalDate.now())) {
                    isFuture = false;
                    isActive = true;
                } else {
                    if (startDate.toLocalDate().isAfter(LocalDate.now())) {
                        isFuture = true;
                        isActive = false;
                    } else throw new RuntimeException("Please select today's date or future date");
                }

                if (isFuture) {
                    List<CustomerPlansModel> getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).collect(Collectors.toList());
                    if (getFuturePlanList.size() > 0) {
                        LocalDateTime finalStartDate = startDate;
                        getFuturePlanList.forEach(data -> {
                            if (data.getExpiryDate().toLocalDate().isAfter(finalStartDate.toLocalDate())) {
                                throw new RuntimeException("Plan overlapping for selected plan, Please select another plan");
                            }
                        });
                    }
                }

                if (isActive) {
                    List<CustomerPlansModel> getActivePlanList = this.getActivePlanList(customers.getId(), false).stream().filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).collect(Collectors.toList());
                    if (getActivePlanList.size() > 0) {
                        LocalDateTime finalStartDate = startDate;
                        getActivePlanList.forEach(data -> {
                            if (data.getExpiryDate().toLocalDate().isAfter(finalStartDate.toLocalDate())) {
                                throw new RuntimeException("Plan overlapping for selected plan, Please select another plan");
                            }
                        });
                    }
                }

                if (customers.getPartner().getName() != null && !customers.getPartner().getName().equalsIgnoreCase(CommonConstants.DEFAULT_PARTNER)) {
                    commissionService.setPartnerCommission(requestDTO, customers, customers.getPartner(), requestFrom);
                }
            } else if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_UPGRADE)) {
                //This is in not use
                List<CustPlanMappping> tempCustPlanMapList = customers.getPlanMappingList();
                CustomerPlansModel finalActiveCustPlanModel2 = activeCustPlanModel;
                custPlanMappping = tempCustPlanMapList.stream().filter(data -> data.getId().intValue() == finalActiveCustPlanModel2.getPlanmapid().intValue()).findAny().orElse(null);
                custPlanMappping.setEndDate(LocalDateTime.now());
                custPlanMappingService.save(custPlanMappping, "");
                startDate = LocalDateTime.now();
                expiryDate = LocalDateTime.of(startDate.plusDays(postpaidPlan.getValidity().longValue()).toLocalDate(), LocalTime.of(23, 59, 59));

                List<CustomerPlansModel> getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).collect(Collectors.toList());
                if (getFuturePlanList.size() > 0) {
                    LocalDateTime finalExpiryDate = expiryDate;
                    getFuturePlanList.forEach(data -> {
                        if (data.getStartDate().toLocalDate().isBefore(finalExpiryDate.toLocalDate())) {
                            throw new RuntimeException("Plan overlapping for selected plan, Please select another plan");
                        }
                    });
                }
            } else if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW) && requestDTO.getIsAdvRenewal()) {
                //This is in not use
                PostpaidPlan tempPostpaidPlan = this.planService.get(requestDTO.getPlanId(), customers.getMvnoId());
                List<LocalDate> expiryDateList = new ArrayList<>();
                List<CustomerPlansModel> getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> !data.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_ADDON)).collect(Collectors.toList());
                if (getFuturePlanList.size() > 0) {
                    getFuturePlanList.forEach(data -> {
                        expiryDateList.add(data.getExpiryDate().toLocalDate());
                    });
                    if (expiryDateList.size() > 0) {
                        LocalDate maxDate = expiryDateList.stream().max(LocalDate::compareTo).get();
                        startDate = LocalDateTime.of(LocalDate.parse(maxDate.toString()).plusDays(1), LocalTime.of(00, 00, 00));
                        expiryDate = LocalDateTime.of(startDate.plusDays(tempPostpaidPlan.getValidity().longValue()).toLocalDate(), LocalTime.of(23, 59, 59));
                    }
                } else {
                    if (activeCustPlanModel == null) {
                        startDate = LocalDateTime.of(LocalDate.now(), LocalTime.now().plusSeconds(1));
                    } else {
                        startDate = LocalDateTime.of(activeCustPlanModel.getEndDate().toLocalDate().plusDays(1L), LocalTime.of(00, 00, 00));
                    }
                    expiryDate = LocalDateTime.of(startDate.plusDays(tempPostpaidPlan.getValidity().longValue()).toLocalDate(), LocalTime.of(23, 59, 59));
                }
            }
            Services service = serviceRepository.findById(postpaidPlan.getServiceId().longValue()).get();

            planValidityDays = customersService.calculatePlanValidityDays(customersPojo, postpaidPlan, postpaidPlan.getValidity().longValue(), startDate);
            Long qospolicyId = null;
            if (postpaidPlan.getQospolicy() != null) {
                qospolicyId = postpaidPlan.getQospolicy().getId();
            }
            CustPlanMapppingPojo planMapppingPojo1 = new CustPlanMapppingPojo(requestDTO.getPlanId(), customers.getId(), customersPojo, startDate, expiryDate, qospolicyId, postpaidPlan.getUploadQOS(), postpaidPlan.getDownloadQOS(), postpaidPlan.getUploadTs(), postpaidPlan.getDownloadTs(), service.getServiceName(), postpaidPlan.getOfferprice(), taxAmount, endDate, postpaidPlan.getValidity(), planValidityDays, requestDTO.getPlanGroupId(), null, customers.getUsername());
            if (!CollectionUtils.isEmpty(requestDTO.getPlanMappingList())) {
                Optional<CustPlanMapppingPojo> planMappingPojo = requestDTO.getPlanMappingList().stream().filter(custPlanMapppingPojo -> custPlanMapppingPojo.getPlanId().equals(planMapppingPojo1.getPlanId())).findAny();
                if (planMappingPojo.isPresent()) {
                    planMapppingPojo1.setNewAmount(planMappingPojo.get().getNewAmount());

                    if (planMappingPojo.get().getBillTo() != null)
                        planMapppingPojo1.setBillTo(planMappingPojo.get().getBillTo());
                    else planMapppingPojo1.setBillTo(com.adopt.apigw.constants.Constants.CUSTOMER);

                    if (planMappingPojo.get().getIsInvoiceToOrg() != null)
                        planMapppingPojo1.setIsInvoiceToOrg(planMappingPojo.get().getIsInvoiceToOrg());
                    else planMapppingPojo1.setIsInvoiceToOrg(false);

//                    planMapppingPojo1.setBillTo(planMappingPojo.get().getBillTo());
//                    planMapppingPojo1.setIsInvoiceToOrg(planMappingPojo.get().getIsInvoiceToOrg());
                }
            }
            //Entry in quotadtls
            for (CustPlanMapppingPojo planMapppingPojo : Collections.singleton(planMapppingPojo1)) {
                PostpaidPlanPojo plan = postpaidPlanMapper.domainToDTO(planService.get(planMapppingPojo.getPlanId(), customers.getMvnoId()), new CycleAvoidingMappingContext());
                if (null != plan && null != plan.getQuotatype()) {
                    Double totalQuotaForSeconds = 0.0;
                    Double totalQuotaForKB = 0.0;
                    if (null != plan.getQuotaunittime() && plan.getQuotaunittime().equalsIgnoreCase(com.adopt.apigw.constants.Constants.MINUTE)) {
                        totalQuotaForSeconds = plan.getQuotatime() * 60;
                    }
                    if (null != plan.getQuotaunittime() && plan.getQuotaunittime().equalsIgnoreCase(com.adopt.apigw.constants.Constants.HOUR)) {
                        totalQuotaForSeconds = plan.getQuotatime() * 60 * 60;
                    }
                    if (null != plan.getQuotaUnit() && plan.getQuotaUnit().equalsIgnoreCase(com.adopt.apigw.constants.Constants.MB)) {
                        totalQuotaForKB = (double) plan.getQuota() * 1024;
                    }
                    if (null != plan.getQuotaUnit() && plan.getQuotaUnit().equalsIgnoreCase(com.adopt.apigw.constants.Constants.GB)) {
                        totalQuotaForKB = (double) plan.getQuota() * 1024 * 1024;
                    }
                    CustQuotaDtlsPojo quotaDetails = null;
                    if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.TIME_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), 0.0, 0.0, null, plan.getQuotatime(), 0.0, plan.getQuotaunittime(), 0.0, 0.0, 0.0, totalQuotaForSeconds, customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()), plan.getName(), plan.getPlanGroup());
                    }
                    if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.DATA_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), (double) plan.getQuota(), 0.0, plan.getQuotaUnit(), 0.0, 0.0, null, totalQuotaForKB, 0.0, 0.0, 0.0, customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()), plan.getName(), plan.getPlanGroup());
                    }
                    if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.BOTH_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), (double) plan.getQuota(), 0.0, plan.getQuotaUnit(), plan.getQuotatime(), 0.0, plan.getQuotaunittime(), totalQuotaForKB, 0.0, 0.0, totalQuotaForSeconds, customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()), plan.getName(), plan.getPlanGroup());
                    }

                    planMapppingPojo.setQuotaList(Collections.singletonList(quotaDetails));
                }
                planMapppingPojo1.setQuotaList(planMapppingPojo.getQuotaList());
            }
            try {
                if (null != customers.getVoicesrvtype() && customers.getVoiceprovision() && null != postpaidPlan.getParam1()) {
                    nexgeVoiceProvisionService.performServicePlanUpdate(customers.getId().toString(), customers.getAcctno(), postpaidPlan.getParam1());
                }
            } catch (Exception ex) {
                ApplicationLogger.logger.error(SUBMODULE + " Change Plan ", ex.getMessage(), ex);
                throw ex;
            }

            if (requestDTO.getIsPaymentReceived() && !onlinePurchase) {
                RecordpaymentResponseDTO recordpaymentResponseDTO = recordPayment(requestDTO.getRecordPaymentDTO(), customers);
                if (null != recordpaymentResponseDTO && null != recordpaymentResponseDTO.getCreditDocument() && 0 < recordpaymentResponseDTO.getCreditDocument().size()) {
                    CreditDocument creditDocument = recordpaymentResponseDTO.getCreditDocument().get(0);
                    planMapppingPojo1.setCreditdocid(creditDocument.getId().longValue());
                }
                customChangePlanDTO.setRecordpaymentResponseDTO(recordpaymentResponseDTO);
            }
            if (!requestDTO.getIsPaymentReceived() && onlinePurchase) {
                //Payment record
                RecordPaymentRequestDTO recordPaymentDto = new RecordPaymentRequestDTO(UtilsCommon.PAYMENT_MODE_ONLINE, LocalDateTime.now().toLocalDate(), onlinePaidAmount, false, "", customers.getId());
                RecordpaymentResponseDTO recordpaymentResponseDTO = subscriberService.recordPayment(recordPaymentDto, customers);

                //Invoke Receipt therad
                if (null != customers) {
                    List<CreditDocument> creditDocumentList = recordpaymentResponseDTO.getCreditDocument();
                    Runnable receiptRunnable = new ReceiptThread(billRunService, creditDocumentList);
                    Thread receiptThread = new Thread(receiptRunnable);
                    receiptThread.start();
                }

                //Set creditDoc id in custpack
                if (null != recordpaymentResponseDTO && null != recordpaymentResponseDTO.getCreditDocument() && 0 < recordpaymentResponseDTO.getCreditDocument().size()) {
                    CreditDocument creditDocument = recordpaymentResponseDTO.getCreditDocument().get(0);
                    planMapppingPojo1.setCreditdocid(creditDocument.getId().longValue());
                }

            }

            if (requestDTO.getWalletBalUsed() != null) {
                planMapppingPojo1.setWalletBalUsed(requestDTO.getWalletBalUsed());
            } else {
                planMapppingPojo1.setWalletBalUsed(0.0);
            }

            if (requestDTO.getPurchaseId() != null) {
                planMapppingPojo1.setOnlinePurchaseId(requestDTO.getPurchaseId());
            }

            if (requestDTO.getOnlinePurType() != null) {
                planMapppingPojo1.setPurchaseType(requestDTO.getPurchaseType());
            }

            if (requestDTO.getPurchaseFrom() != null) {
                planMapppingPojo1.setPurchaseFrom(requestDTO.getPurchaseFrom());
            } else {
                if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                    planMapppingPojo1.setPurchaseFrom(SubscriberConstants.PURCHASE_FROM_ADMIN);
                } else {
                    planMapppingPojo1.setPurchaseFrom(SubscriberConstants.PURCHASE_FROM_PARTNER);
                }
            }

            planMapppingPojo1.setValidity(postpaidPlan.getValidity());
            if (requestDTO.getDiscount() != null && requestDTO.getDiscount() != 0) {
                planMapppingPojo1.setDiscount(requestDTO.getDiscount());
            } else if (customersPojo.getDiscount() != 0) planMapppingPojo1.setDiscount(customersPojo.getDiscount());
            else if (custPlanMappping != null) {
                if (custPlanMappping.getDiscount() != null)
                    planMapppingPojo1.setDiscount(custPlanMappping.getDiscount());
                else planMapppingPojo1.setDiscount(0d);

            } else {
                planMapppingPojo1.setDiscount(0d);
            }
            Optional<PlanService> services = planServiceRepository.findById(postpaidPlan.getServiceId());
            if (services.isPresent()) {
                if (services.get().getExpiry() != null) {
                    if (services.get().getExpiry().equalsIgnoreCase(com.adopt.apigw.constants.Constants.AT_MIDNIGHT)) {
                        planMapppingPojo1.setExpiryDate(planMapppingPojo1.getExpiryDate().toLocalDate().atTime(LocalTime.MAX).minusSeconds(1));
                        planMapppingPojo1.setEndDate(planMapppingPojo1.getEndDate().toLocalDate().atTime(LocalTime.MAX).minusSeconds(1));
                    }
                }
            }
            QCustomerServiceMapping qCustomerServiceMapping = QCustomerServiceMapping.customerServiceMapping;
            BooleanExpression bCustSerMap = qCustomerServiceMapping.isNotNull().and(qCustomerServiceMapping.custId.in(planMapppingPojo1.getCustid())).and(qCustomerServiceMapping.serviceId.in(postpaidPlan.getServiceId()));
            List<CustomerServiceMapping> customerServiceMappings = new ArrayList<>();
            customerServiceMappings = (List<CustomerServiceMapping>) customerServiceMappingRepository.findAll(bCustSerMap);
            if (!customerServiceMappings.isEmpty()) {
                CustomerServiceMapping customerServiceMapp = customerServiceMappings.get(0);
                if (Objects.isNull(customerServiceMapp.getConnectionNo())) {
//                    customerServiceMapp = customersService.generateConnectionNumber(customerServiceMapp);
                    Boolean isLCO = customers.getLcoId() != null;
                    String connectionNo = numberSequenceUtil.getConnectionNumber(isLCO, customers.getLcoId(), customers.getMvnoId());
                    customerServiceMapp.setConnectionNo(connectionNo);
                }
                Integer custServiceMappingId = customerServiceMapp.getId();
                planMapppingPojo1.setCustServiceMappingId(custServiceMappingId);
                planMapppingPojo1.setInvoiceType(customerServiceMapp.getInvoiceType());
            }
            if (customerServiceMappings.isEmpty()) {
                CustomerServiceMapping mapping = new CustomerServiceMapping();
                mapping.setServiceId(Long.valueOf(postpaidPlan.getServiceId()));
                mapping.setCustId(requestDTO.getCustId());
//                mapping = customersService.generateConnectionNumber(mapping);
                Boolean isLCO = customers.getLcoId() != null;
                String connectionNo = numberSequenceUtil.getConnectionNumber(isLCO, customers.getLcoId(), customers.getMvnoId());
                mapping.setConnectionNo(connectionNo);
                CustomerServiceMapping savedCustomerServiceMapping = new CustomerServiceMapping();
                savedCustomerServiceMapping = customerServiceMappingRepository.save(mapping);
                planMapppingPojo1.setCustServiceMappingId(savedCustomerServiceMapping.getId());
            }
            //PostpaidPlanPojo planPojo = postpaidPlanMapper.domainToDTO(postpaidPlan, new CycleAvoidingMappingContext());
            CustPlanMappping mapping = null;
            if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW)) {
                //operation renew
                mapping = custPlanMappingService.save(customerMapper.mapCustPlanMapPojoToCustPlanMap(planMapppingPojo1, new CycleAvoidingMappingContext()), CommonConstants.EVENTCONSTANTS.RENEW_PLAN);
            } else if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON)) {
                //operation addOn
                if (planMapppingPojo1.getPurchaseType().equalsIgnoreCase("Bandwidthbooster")) {
                    mapping = custPlanMappingService.save(customerMapper.mapCustPlanMapPojoToCustPlanMap(planMapppingPojo1, new CycleAvoidingMappingContext()), CommonConstants.EVENTCONSTANTS.NEW_BANDWIDTH_BOOSTER);
                } else if (planMapppingPojo1.getPurchaseType().equalsIgnoreCase("Volume Booster")) {
                    mapping = custPlanMappingService.save(customerMapper.mapCustPlanMapPojoToCustPlanMap(planMapppingPojo1, new CycleAvoidingMappingContext()), CommonConstants.EVENTCONSTANTS.NEW_VOLUME_BOOSTER);
                }
            }
            PostpaidPlanPojo planPojo = postpaidPlanMapper.domainToDTO(postpaidPlan, new CycleAvoidingMappingContext());
            customersService.saveCustomerChargeHistory(postpaidPlan, customersPojo, mapping, requestDTO.getPlanGroupId(), false, null, null);
            if (planMapppingPojo1.getBillTo().equalsIgnoreCase(com.adopt.apigw.constants.Constants.ORGANIZATION) && planMapppingPojo1.getIsInvoiceToOrg()) {
                planMapppingPojo1.setCustRefId(customersPojo.getId());
                CustomersPojo subscriber = new CustomersPojo();
                subscriber.setPlanMappingList(Collections.singletonList(planMapppingPojo1));
                subscriber.setId(planMapppingPojo1.getCustid());
                customersService.saveDataForOrganizationCustomer(subscriber, planMapppingPojo1.getDiscount(), planMapppingPojo1.getValidity().longValue(), null, customersPojo.getCusttype());
            }
            if (activeCustPlanModel != null) {
                // Subscriber Update
                SubscriberUpdateUtils.updateSubscriber(activeCustPlanModel.getPlanName(), postpaidPlan.getName(), UpdateConstant.UPDATE_PLAN, customers, requestDTO.getRemarks(), null);
            } else {
                // Subscriber Update
                SubscriberUpdateUtils.updateSubscriber(null, postpaidPlan.getName(), UpdateConstant.UPDATE_PLAN, customers, requestDTO.getRemarks(), null);
            }

            customers.setNextBillDate(LocalDate.now());
            LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers, customers.getNextBillDate());
            if (nextQuotaReset != null) {
                customers.setNextQuotaResetDate(nextQuotaReset);
            } else {
                customers.setNextQuotaResetDate(LocalDate.now());
            }
            customersService.update(customers);
            sendRenewRechargeMessage(customers.getUsername(), customers.getCountryCode(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), postpaidPlan.getName(), requestDTO.getPurchaseType(), (long) customersService.getLoggedInStaffId());


            //Update subscriber ledger
            /*
            CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
            ledgerDtls.setCustomer(customers);
            ledgerDtls.setDebitdocid(null);
            ledgerDtls.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(planMapppingPojo1.getOfferPrice()
                    + planMapppingPojo1.getTaxAmount())));
            ledgerDtls.setDescription("Invoice generated for change plan : " + planService.get(planMapppingPojo1.getPlanId()).getDisplayName());
            ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
            ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_INVOICE);
            customerLedgerDtlsService.save(ledgerDtls);

            CustomerLedger customerLedger = this.customerLedgerService.getCustomerLeger(customers).get(0);
            customerLedger.setTotaldue(customerLedger.getTotaldue() + (planMapppingPojo1.getOfferPrice() + planMapppingPojo1.getTaxAmount()));
            customerLedger.setUpdatedate(LocalDateTime.now());
            customerLedgerService.save(customerLedger);
             */
            if (quotaDtlsModel != null && addQuotaInExistingPlan) {
                this.changeQuota(quotaDtlsModel, customers);
            }

            //communication for change plan
            CommunicationHelper communicationHelper = new CommunicationHelper();
            Map<String, String> map = new HashMap<>();
            map.put(CommunicationConstant.PLAN_NAME, postpaidPlan.getDisplayName());
            map.put(CommunicationConstant.DATE, LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            map.put(CommunicationConstant.DESTINATION, customersPojo.getMobile());
            map.put(CommunicationConstant.EMAIL, customersPojo.getEmail());
            communicationHelper.generateCommunicationDetails(CommunicationConstant.PACKGE_RENEWED_1, Collections.singletonList(map));

            customChangePlanDTO.setCustomersBasicDetailsPojo(getBasicDetailsOfSubscriber(customers));
            if (mapping != null) customChangePlanDTO.setCustpackagerelid(mapping.getId());

            return customChangePlanDTO;
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Transactional
    public CustomChangePlanDTO renewCustomer(ChangePlanRequestDTO requestDTO, Customers customers, Boolean onlinePurchase, Double onlinePaidAmount, String requestFrom, Double maxValidity, String number, DateOverrideDto dateOverrideDto, ChangePlanRequestDTOList requestDTOs) throws Exception {
        String SUBMODULE = MODULE + " [changePlan()] ";
        CustomChangePlanDTO customChangePlanDTO = new CustomChangePlanDTO();
        QuotaDtlsModel quotaDtlsModel = null;
        Boolean addQuotaInExistingPlan = false;
        Integer parentCustomerId = null;
        try {
//            Optional<CustomerServiceMapping> customerServiceMapping = customerServiceMappingRepository.findById(requestDTO.getCustServiceMappingId());
            Optional<CustomerServiceMapping> customerServiceMapping = Optional.ofNullable(getCustomerServiceMapping(requestDTO.getCustServiceMappingId()));
            if (Objects.nonNull(customers.getParentCustomers())) {
                if (customerServiceMapping != null) {
                    if (customerServiceMapping.get().getInvoiceType().equalsIgnoreCase("Group"))
                        parentCustomerId = customers.getParentCustomers().getId();
                }
            }
            CustomersPojo customersPojo = customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext());
            PostpaidPlan postpaidPlan = planService.get(requestDTO.getPlanId(), customers.getMvnoId());
//            Services service = serviceRepository.findById(postpaidPlan.getServiceId().longValue()).get();
            Services service = planServiceService.getServices(postpaidPlan.getServiceId(), customersPojo.getMvnoId());
            CustomerPlansModel activeCustPlanModel = null;
            List<CustomerPlansModel> parentActiveCustPlanModelList = null;
            List<CustomerPlansModel> activeCustPlanModelList = this.subscriberService.getActivePlanList(customers.getId(), false);
            if (parentCustomerId != null) {
                parentActiveCustPlanModelList = this.subscriberService.getActivePlanList(parentCustomerId, false);
            }
            List<CustomerPlansModel> tempActivePlanList = this.subscriberService.getActivePlanList(customers.getId(), false);
            activeCustPlanModelList.removeIf(custPlanMapp -> custPlanMapp.getCustPlanStatus().equalsIgnoreCase("STOP") && custPlanMapp.getStopServiceDate() == null);
            tempActivePlanList.removeIf(custPlanMapp -> custPlanMapp.getCustPlanStatus().equalsIgnoreCase("STOP") && custPlanMapp.getStopServiceDate() == null);
//            List<CustomerPlansModel> activeCustPlanModelList = this.subscriberService.getCustomerPlanList(customers.getId());
            activeCustPlanModelList.removeIf(custPlanMapp -> custPlanMapp.getServiceId().longValue() != service.getId());
            tempActivePlanList.removeIf(custPlanMapp -> custPlanMapp.getServiceId().longValue() != service.getId());

            if (activeCustPlanModelList.size() > 0) {
                if (requestDTO.getBindWithOldPlanId() != null) {
                    activeCustPlanModelList = this.subscriberService.getActivePlanList(customers.getId(), false).stream().filter(data -> data.getPlanId().toString().equalsIgnoreCase(requestDTO.getBindWithOldPlanId())).filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_ONLY))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                    if (activeCustPlanModelList.size() > 0) activeCustPlanModel = activeCustPlanModelList.get(0);
                } else if (requestDTO.getNewPlanList() != null) {
                    if (requestDTO.getCustServiceMappingId() != null)
                        activeCustPlanModelList = this.getActivePlanList(customers.getId(), false).stream().filter(dto -> null != dto.getPlanstage() && dto.getCustomerServiceMappingId().equals(requestDTO.getCustServiceMappingId()) && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_ONLY))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                    else
                        activeCustPlanModelList = this.getActivePlanList(customers.getId(), false).stream().filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW)) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_ONLY)).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                    if (activeCustPlanModelList.size() > 0) activeCustPlanModel = activeCustPlanModelList.get(0);
                } else {
                    if (requestDTO.getCustServiceMappingId() != null)
                        activeCustPlanModelList = this.getActivePlanList(customers.getId(), false).stream().filter(dto -> null != dto.getPlanstage() && dto.getCustomerServiceMappingId().equals(requestDTO.getCustServiceMappingId()) && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_ONLY))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                    else
                        activeCustPlanModelList = this.getActivePlanList(customers.getId(), false).stream().filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_ONLY))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                    if (activeCustPlanModelList.size() > 0) activeCustPlanModel = activeCustPlanModelList.get(0);
                }
            }

            CustPlanMappping custPlanMappping = null;
            LocalDateTime startDate = null;
            LocalDateTime expiryDate = null;
            Integer validity = null;
            LocalDateTime todaysDate = LocalDateTime.now();
            Double taxAmount = 0.0;
            LocalDateTime endDate = null;
            Integer planValidityDays = 0;

            TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO(requestDTO.getPlanId(), null, customers.getId(), null);
            if (taxDetailCountReqDTO != null) {
                taxAmount = taxService.taxCalculationByPlan(taxDetailCountReqDTO, postpaidPlan.getChargeList());
            }
            if (!requestDTO.getIsAdvRenewal() && requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW)) {
                if (!customerServiceMapping.get().getStatus().equalsIgnoreCase("Hold")) {
                    if (activeCustPlanModel != null) {
                        if (parentCustomerId != null) {
                            startDate = Instant.ofEpochMilli(activeCustPlanModel.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(1L);
                            if (activeCustPlanModel.getCustPlanStatus().equalsIgnoreCase(CommonConstants.STOP_STATUS))
                                startDate = LocalDateTime.now();
                            expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                            endDate = expiryDate;
                            PostpaidPlan plan = this.planService.get(requestDTO.getPlanId(), customers.getMvnoId());
                            Optional<PlanService> services = planServiceRepository.findById(plan.getServiceId());
                            if (services.isPresent()) {
                                if (services.get().getExpiry() != null) {
                                    if (services.get().getExpiry().equalsIgnoreCase(com.adopt.apigw.constants.Constants.AT_MIDNIGHT)) {
                                        expiryDate = LocalDateTime.of(expiryDate.toLocalDate(), LocalTime.of(23, 59, 59));
                                        endDate = expiryDate;
                                    }
                                }
                            }
                            Customers parentCustomer = this.subscriberService.get(parentCustomerId, customers.getMvnoId());
                            LocalDateTime parentMaxExpiryDate = getParentMaxExpiryDateByService(plan.getServiceId(), parentCustomer);

                            /* if parent and child have same service */
                            if (parentActiveCustPlanModelList.get(0).getServiceId().equals(activeCustPlanModel.getServiceId())) {
                                if (parentMaxExpiryDate != null) {
                                    /*  checking start date of childs plan with parents end date of a plan  */
                                    if (startDate.toLocalDate().isBefore(parentMaxExpiryDate.toLocalDate())) {
                                        if (parentMaxExpiryDate.isAfter(expiryDate)) {
                                            expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                                            endDate = expiryDate;
                                        } else if (!parentMaxExpiryDate.isAfter(expiryDate)) {
                                            expiryDate = parentMaxExpiryDate;
                                            endDate = expiryDate;
                                        }
                                    } else {
                                    /*  this exception is thrown when childs start date is more than parents expire date ,condition:-when service of both parent and child
                                     is same */
                                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "No Active plan with Parent for " + activeCustPlanModel.getService() + "service so kindly renew Parent first.", null);
                                    }
                                }
                            }
                        } else {
                            if (dateOverrideDto != null) {
                                if (dateOverrideDto.isDateOverrideFlag()) {
                                    startDate = dateOverrideDto.getChangePlanStartDate();
                                    expiryDate = dateOverrideDto.getChangePlanEndDate();
                                    endDate = expiryDate;
                                }
                            } else {
                                if (requestDTO.getAddonStartDate() != null && requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON))
                                    startDate = requestDTO.getAddonStartDate();
                                else if (requestDTO.getAddonStartDate() != null && requestDTO.getOnlinePurType().equalsIgnoreCase(SubscriberConstants.PURCHASE_FROM_FLUTTERWAVE) || requestDTO.getOnlinePurType().equalsIgnoreCase(SubscriberConstants.PURCHASE_FROM_MTN)) {
                                    startDate = requestDTO.getAddonStartDate();
                                } else {
                                    startDate = Instant.ofEpochMilli(activeCustPlanModel.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(1);
                                    if (activeCustPlanModel.getCustPlanStatus().equalsIgnoreCase(CommonConstants.STOP_STATUS))
                                        startDate = LocalDateTime.now();
                                }
                                expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                                endDate = expiryDate;
                            }
                            PostpaidPlan plan = this.planService.get(requestDTO.getPlanId(), customers.getMvnoId());
//                            Optional<PlanService> services = planServiceRepository.findById(plan.getServiceId());
                            Optional<PlanService> services = Optional.ofNullable(planServiceService.get(plan.getServiceId(), customers.getMvnoId()));
                            if (services.isPresent()) {
                                if (services.get().getExpiry() != null) {
                                    if (services.get().getExpiry().equalsIgnoreCase(com.adopt.apigw.constants.Constants.AT_MIDNIGHT)) {
                                        expiryDate = LocalDateTime.of(expiryDate.toLocalDate(), LocalTime.of(23, 59, 59));
                                        endDate = expiryDate;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Renewal can not  be done as Service connection is on Hold !!", null);
                }
                List<CustomerPlansModel> getFuturePlanList = new ArrayList<>();

                if (requestDTO.getBindWithOldPlanId() != null)
                    getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> data.getPlanId().toString().equalsIgnoreCase(requestDTO.getBindWithOldPlanId())).filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                else if (requestDTO.getNewPlanList() != null)
                    getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> data.getPlanId().toString().equalsIgnoreCase(requestDTO.getPlanId().toString())).filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                else
                    getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());

                Integer allowrenewalnumberoftime = Integer.valueOf(clientServiceSrv.getValueByName("allowrenewalnumberoftime"));

                List<Integer> featureIds = getFuturePlanList.stream().map(x -> x.getServiceId()).collect(Collectors.toList());
                PostpaidPlan plan1 = this.planService.get(requestDTO.getPlanId(), customers.getMvnoId());
                Integer serviceIds = plan1.getServiceId();
                List<CustomerPlansModel> getFuturePlanListForRenew = getFuturePlanList.stream().filter(x -> x.getServiceId() == serviceIds).collect(Collectors.toList());
                if (getFuturePlanListForRenew.size() >= allowrenewalnumberoftime && allowrenewalnumberoftime != 0) {
                    throw new RuntimeException("Renewal not allowed as Future Plan is already added to this Service");
                }

                getFuturePlanList.removeIf(custPlanMap -> custPlanMap.getCustPlanStatus().equalsIgnoreCase("STOP") && custPlanMap.getStopServiceDate() == null);
                getFuturePlanList.removeIf(custPlanMapp -> custPlanMapp.getServiceId().longValue() != service.getId());

                if (getFuturePlanList.size() > 0) {
                    if (parentCustomerId != null) {
                        startDate = Instant.ofEpochMilli(getFuturePlanList.get(0).getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(1L);
                        expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                        endDate = expiryDate;
                        PostpaidPlan plan = this.planService.get(requestDTO.getPlanId(), customers.getMvnoId());
                        Optional<PlanService> services = planServiceRepository.findById(plan.getServiceId());
                        if (services.isPresent()) {
                            if (services.get().getExpiry() != null) {
                                if (services.get().getExpiry().equalsIgnoreCase(com.adopt.apigw.constants.Constants.AT_MIDNIGHT)) {
                                    expiryDate = LocalDateTime.of(expiryDate.toLocalDate(), LocalTime.of(23, 59, 59));
                                    endDate = expiryDate;
                                }
                            }
                        }
                        Customers parentCustomer = this.subscriberService.get(parentCustomerId, customers.getMvnoId());
                        LocalDateTime parentMaxExpiryDate = getParentMaxExpiryDateByService(plan.getServiceId(), parentCustomer);
                        if (parentMaxExpiryDate != null) {
                            if (parentMaxExpiryDate.isAfter(expiryDate)) {
                                expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                                endDate = expiryDate;
                            } else if (parentMaxExpiryDate.isBefore(expiryDate)) {
                                expiryDate = parentMaxExpiryDate;
                                endDate = expiryDate;
                            }
                            if (startDate.isAfter(expiryDate)) {
                                throw new RuntimeException("Kindly renew Parent first.");
                            }
                        }
                    } else {
                        if (requestDTO.getAddonStartDate() != null && requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON))
                            startDate = requestDTO.getAddonStartDate();
                        else if (getFuturePlanList.size() > 0) {
                            if (requestDTO.getOnlinePurType().equalsIgnoreCase(SubscriberConstants.PURCHASE_FROM_FLUTTERWAVE)) {
                                System.out.println("Payment from flutterwave");
                            } else {
                                if (dateOverrideDto != null) {
                                    if (dateOverrideDto.isDateOverrideFlag()) {
                                        startDate = dateOverrideDto.getChangePlanStartDate();
                                        expiryDate = dateOverrideDto.getChangePlanEndDate();
                                        endDate = expiryDate;
                                    }
                                } else {
                                    startDate = Instant.ofEpochMilli(getFuturePlanList.get(0).getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(1L);
                                    expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                                    endDate = expiryDate;
                                }
                                PostpaidPlan plan = this.planService.get(requestDTO.getPlanId(), customers.getMvnoId());
                                Optional<PlanService> services = planServiceRepository.findById(plan.getServiceId());
                                if (services.isPresent()) {
                                    if (services.get().getExpiry() != null) {
                                        if (services.get().getExpiry().equalsIgnoreCase(com.adopt.apigw.constants.Constants.AT_MIDNIGHT)) {
                                            expiryDate = LocalDateTime.of(expiryDate.toLocalDate(), LocalTime.of(23, 59, 59));
                                            endDate = expiryDate;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (activeCustPlanModel == null && (getFuturePlanList == null || (getFuturePlanList != null && getFuturePlanList.size() == 0))) {
                    if (requestDTO.getAddonStartDate() != null && requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON)) {
                        startDate = requestDTO.getAddonStartDate();
                    } else {
                        startDate = todaysDate;
                    }
                    expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                    endDate = expiryDate;
                }

                if (requestDTO.getIsRefund()) {
                    final long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now().toLocalDate(), activeCustPlanModel.getEndDate().toLocalDate());
                    Double oneDayAmount;
                    Double refundAmount;
                    Double roundFigureAmount = 0.0;
                    if (postpaidPlan != null) {
                        if (postpaidPlan.getValidity() > 0) {
                            final long totalServicedDays = ChronoUnit.DAYS.between(activeCustPlanModel.getStartDate().toLocalDate(), activeCustPlanModel.getEndDate().toLocalDate());
                            oneDayAmount = (postpaidPlan.getOfferprice() / totalServicedDays);
                            refundAmount = (remainingDays * oneDayAmount);
                            roundFigureAmount = (Math.round(refundAmount * 100) / 100.00);
                        } else throw new RuntimeException("Validity can not be zero");
                    }
                    CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
                    ledgerDtls.setCustomer(customers);
                    ledgerDtls.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(roundFigureAmount)));
                    ledgerDtls.setDescription("Refund against change plan");
                    ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                    ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_REFUND);
                    customerLedgerDtlsService.save(ledgerDtls);

                    //Update customer ledger
                    CustomerLedger customerLedger = this.customerLedgerService.getCustomerLeger(customers).get(0);
                    customerLedger.setTotalpaid(Double.parseDouble(new DecimalFormat("##.##").format(customerLedger.getTotalpaid() + roundFigureAmount)));
                    customerLedger.setCustomer(customers);
                    customerLedger.setUpdatedate(LocalDateTime.now());
                    customerLedgerService.save(customerLedger);
                }

                customers.setNextBillDate(expiryDate.toLocalDate());
                customers.setLastBillDate(startDate.toLocalDate());
                LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers, customers.getNextBillDate());
                if (nextQuotaReset != null) {
                    customers.setNextQuotaResetDate(nextQuotaReset);
                } else {
                    customers.setNextQuotaResetDate(LocalDate.now());
                }
                if (!customers.getStatus().equalsIgnoreCase(SubscriberConstants.TERMINATE)) {
                    customers.setStatus(SubscriberConstants.ACTIVE);
                }
                save(customers);
                CustomerUpdateMessage message = new CustomerUpdateMessage(customers);
                kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));

                if (customers.getPartner().getName() != null && !customers.getPartner().getName().equalsIgnoreCase(CommonConstants.DEFAULT_PARTNER)) {
                    commissionService.setPartnerCommission(requestDTO, customers, customers.getPartner(), requestFrom);
                }
            }

            if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON)) {
                //This is in use
                if (requestDTO.getAddonStartDate() != null) startDate = requestDTO.getAddonStartDate();
                else startDate = LocalDateTime.now();

                if (requestDTO.getAddonEndDate() != null) {
                    expiryDate = requestDTO.getAddonEndDate();
                } else {
                    expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                }
                //if(customers!=null && customers.getCusttype()!=null && customers.getCusttype().equalsIgnoreCase("Postpaid") && customers.getNextBillDate()!=null && customers.getNextBillDate().isBefore(expiryDate.toLocalDate()))
                //expiryDate = customers.getNextBillDate().atTime(expiryDate.getHour(),expiryDate.getMinute(),expiryDate.getSecond());

                endDate = expiryDate;
                CustPlanMappping mappping = null;
                if (activeCustPlanModel != null) {
//                    mappping = custPlanMappingRepository.findById(activeCustPlanModel.getPlanmapid());
                    mappping = custPlanMappingService.getEntityById(activeCustPlanModel.getPlanmapid());
                } else {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "No Active Service with User currently so kindly renew a Service first.", null);
                }
                String planGroup = postpaidPlan.getPlanGroup();
                if (planGroup != null && planGroup.equalsIgnoreCase("Bandwidthbooster") && requestDTO.getAddonEndDate() == null) {
                    if (mappping != null && mappping.getExpiryDate().isBefore(expiryDate)) {
                        expiryDate = mappping.getExpiryDate();
                        endDate = expiryDate;
                    }
                }


                if (startDate.isAfter(expiryDate)) {
                    throw new RuntimeException("Expiry date can not be less than start date!");
                }


                Boolean isFuture = null;
                Boolean isActive = null;

                if (startDate.toLocalDate().equals(LocalDate.now())) {
                    isFuture = false;
                    isActive = true;
                } else {
                    if (startDate.toLocalDate().isAfter(LocalDate.now())) {
                        isFuture = true;
                        isActive = false;
                    } else throw new RuntimeException("Please select today's date or future date");
                }

                if (isFuture) {
                    List<CustomerPlansModel> getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).collect(Collectors.toList());
                    if (getFuturePlanList.size() > 0) {
                        LocalDateTime finalStartDate = startDate;
                        getFuturePlanList.forEach(data -> {
                            if (data.getPlangroup().equalsIgnoreCase("Bandwidthbooster")) {
                                if (data.getCustPlanStatus().equalsIgnoreCase("Active") && data.getExpiryDate().toLocalDate().isAfter(finalStartDate.toLocalDate())) {
                                    throw new RuntimeException("Plan overlapping for selected plan, Please select another plan");
                                }
                            }
                        });
                    }
                }
                //  for resolve jira -10075, add multiple Bandwidth Booster its shows an Error

             /*   if (isActive == true) {
                    List<CustomerPlansModel> getActivePlanList = this.getActivePlanList(customers.getId(), false).stream().filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup()))
                            .filter(data ->data.getCustPlanMapppingId().equals(requestDTO.getCustServiceMappingId())).collect(Collectors.toList());
                    if (getActivePlanList.size() > 0) {
                        LocalDateTime finalStartDate = startDate;
                        getActivePlanList.forEach(data -> {
                            if (data.getPlangroup().equalsIgnoreCase("Bandwidthbooster")) {
                                if (data.getCustPlanStatus().equalsIgnoreCase("Active") && data.getExpiryDate().toLocalDate().isAfter(finalStartDate.toLocalDate())) {
                                    throw new RuntimeException("Plan overlapping for selected plan, Please select another plan");
                                }
                            }
                        });
                    }
                }*/

                if (!customers.getCusttype().equalsIgnoreCase("Postpaid")) {
                    customers.setNextBillDate(expiryDate.toLocalDate());
                    customers.setLastBillDate(startDate.toLocalDate());
                    LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers, customers.getNextBillDate());
                    if (nextQuotaReset != null) {
                        customers.setNextQuotaResetDate(nextQuotaReset);
                    } else {
                        customers.setNextQuotaResetDate(LocalDate.now());
                    }
                    save(customers);
                }


                if (customers.getPartner().getName() != null && !customers.getPartner().getName().equalsIgnoreCase(CommonConstants.DEFAULT_PARTNER)) {
                    commissionService.setPartnerCommission(requestDTO, customers, customers.getPartner(), requestFrom);
                }
            } else if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_UPGRADE)) {
                //This is in not use
                List<CustPlanMappping> tempCustPlanMapList = customers.getPlanMappingList();
                Integer planMapId = activeCustPlanModel.getPlanmapid();
                custPlanMappping = tempCustPlanMapList.stream().filter(data -> data.getId().intValue() == planMapId.intValue()).findAny().orElse(null);
                custPlanMappping.setEndDate(LocalDateTime.now());
                if (customers.getIsinvoicestop()) custPlanMappping.setIsInvoiceCreated(false);
                custPlanMappingService.save(custPlanMappping, SubscriberConstants.PLAN_PURCHASE_UPGRADE, requestDTO.getIsTriggerCoaDm());
                startDate = LocalDateTime.now();
                expiryDate = LocalDateTime.of(startDate.plusDays(postpaidPlan.getValidity().longValue()).toLocalDate(), LocalTime.of(23, 59, 59));

                List<CustomerPlansModel> getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).collect(Collectors.toList());
                if (getFuturePlanList.size() > 0) {
                    LocalDateTime finalExpiryDate = expiryDate;
                    getFuturePlanList.forEach(data -> {
                        if (data.getStartDate().toLocalDate().isBefore(finalExpiryDate.toLocalDate())) {
                            throw new RuntimeException("Plan overlapping for selected plan, Please select another plan");
                        }
                    });
                }

                customers.setNextBillDate(expiryDate.toLocalDate());
                customers.setLastBillDate(startDate.toLocalDate());
                LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers, customers.getNextBillDate());
                if (nextQuotaReset != null) {
                    customers.setNextQuotaResetDate(nextQuotaReset);
                } else {
                    customers.setNextQuotaResetDate(LocalDate.now());
                }
                save(customers);
            } else if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW) && requestDTO.getIsAdvRenewal()) {
                //This is in not use
                PostpaidPlan tempPostpaidPlan = this.planService.get(requestDTO.getPlanId(), customers.getMvnoId());
                List<LocalDate> expiryDateList = new ArrayList<>();
                List<CustomerPlansModel> getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> !data.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_ADDON)).collect(Collectors.toList());
                if (getFuturePlanList.size() > 0) {
                    getFuturePlanList.forEach(data -> {
                        expiryDateList.add(data.getExpiryDate().toLocalDate());
                    });
                    if (expiryDateList.size() > 0) {
                        LocalDate maxDate = expiryDateList.stream().max(LocalDate::compareTo).get();
                        startDate = LocalDateTime.of(LocalDate.parse(maxDate.toString()).plusDays(1), LocalTime.of(00, 00, 00));
                        expiryDate = LocalDateTime.of(startDate.plusDays(tempPostpaidPlan.getValidity().longValue()).toLocalDate(), LocalTime.of(23, 59, 59));
                    }
                } else {
                    if (activeCustPlanModel == null) {
                        startDate = LocalDateTime.of(LocalDate.now(), LocalTime.now().plusSeconds(1));
                    } else {
                        startDate = LocalDateTime.of(activeCustPlanModel.getEndDate().toLocalDate().plusDays(1L), LocalTime.of(00, 00, 00));
                    }
                    expiryDate = LocalDateTime.of(startDate.plusDays(tempPostpaidPlan.getValidity().longValue()).toLocalDate(), LocalTime.of(23, 59, 59));
                }

                customers.setNextBillDate(expiryDate.toLocalDate());
                customers.setLastBillDate(startDate.toLocalDate());
                LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers, customers.getNextBillDate());
                if (nextQuotaReset != null) {
                    customers.setNextQuotaResetDate(nextQuotaReset);
                } else {
                    customers.setNextQuotaResetDate(LocalDate.now());
                }
                save(customers);
            }
//            Services service = serviceRepository.findById(postpaidPlan.getServiceId().longValue()).get();

            if (dateOverrideDto != null) {
                if (dateOverrideDto.isDateOverrideFlag()) {
                    planValidityDays = Math.toIntExact(ChronoUnit.DAYS.between(startDate, expiryDate));
                }
            } else {
                planValidityDays = customersService.calculatePlanValidityDays(customersPojo, postpaidPlan, postpaidPlan.getValidity().longValue(), startDate);
            }

            Long qospolicyId = null;
            if (postpaidPlan.getQospolicy() != null) {
                qospolicyId = postpaidPlan.getQospolicy().getId();
            }
            CustPlanMapppingPojo planMapppingPojo1 = new CustPlanMapppingPojo(requestDTO.getPlanId(), customers.getId(), customersPojo, startDate, expiryDate, qospolicyId, postpaidPlan.getUploadQOS(), postpaidPlan.getDownloadQOS(), postpaidPlan.getUploadTs(), postpaidPlan.getDownloadTs(), service.getServiceName(), postpaidPlan.getOfferprice(), taxAmount, endDate, postpaidPlan.getValidity(), planValidityDays, requestDTO.getPlanGroupId(), Integer.valueOf(number), customers.getUsername());
            if (activeCustPlanModel != null) {
                if (activeCustPlanModel.getPromiseToPayDays() != null && activeCustPlanModel.getPromiseToPayDays() > 0) {
                    planMapppingPojo1.setCprIdForPromiseToPay(activeCustPlanModel.getPlanmapid());
                    LocalDateTime currentTime = LocalDateTime.now().plusMinutes(1);
                    if ((LocalDate.now().equals(activeCustPlanModel.getPromiseToPayStartDate().toLocalDate()) || LocalDate.now().isAfter(activeCustPlanModel.getPromiseToPayStartDate().toLocalDate())) && (LocalDate.now().isBefore(activeCustPlanModel.getPromiseToPayEndDate().toLocalDate()) || (LocalDate.now().equals(activeCustPlanModel.getPromiseToPayEndDate().toLocalDate())))) {
                        CustPlanMappping mappping = custPlanMappingRepository.findById(activeCustPlanModel.getPlanmapid());
                        if (mappping != null) {
                            mappping.setStartDate(currentTime);
                            mappping.setEndDate(currentTime);
                            mappping.setExpiryDate(currentTime);
                            mappping.setStatus(CommonConstants.STOP_STATUS);
                            mappping.setCustPlanStatus(CommonConstants.STOP_STATUS);
                            custPlanMappingRepository.save(mappping);
                        }
                        Long daysDiff = ChronoUnit.DAYS.between(mappping.getPromise_to_pay_startdate().toLocalDate(), LocalDate.now());
                        planMapppingPojo1.setStartDate(currentTime);
                        planMapppingPojo1.setEndDate(currentTime.plusDays(planMapppingPojo1.getPlanValidityDays()).minusDays(daysDiff));
                        planMapppingPojo1.setExpiryDate(currentTime.plusDays(planMapppingPojo1.getPlanValidityDays()));
                    }
                }
            } else {
                List<CustomerPlansModel> expirePlanList = this.subscriberService.getExpiredPlanList(customers.getId(), false);
                if (expirePlanList != null && !expirePlanList.isEmpty()) {
                    expirePlanList = expirePlanList.stream().filter(x -> x.getCustomerServiceMappingId().equals(requestDTO.getCustServiceMappingId())).collect(Collectors.toList());
                    expirePlanList = expirePlanList.stream().filter(x -> x.getIsPromiseToPayTaken()).collect(Collectors.toList());
                    if (expirePlanList != null && !expirePlanList.isEmpty()) {
                        CustomerPlansModel model = expirePlanList.get(0);
                        if (model.getPromiseToPayDays() != null && model.getPromiseToPayDays() > 0) {
                            planMapppingPojo1.setCprIdForPromiseToPay(model.getPlanmapid());
                            LocalDateTime currentTime = LocalDateTime.now();
                            if (LocalDate.now().isAfter(model.getPromiseToPayEndDate().toLocalDate())) {
                                planMapppingPojo1.setStartDate(currentTime);
                                planMapppingPojo1.setEndDate(currentTime.plusDays(planMapppingPojo1.getPlanValidityDays()).minusDays(model.getPromiseToPayDays()));
                                planMapppingPojo1.setExpiryDate(currentTime.plusDays(planMapppingPojo1.getPlanValidityDays()));
                            }
                        }
                    }
                }
            }

            planMapppingPojo1.setBillableCustomerId(requestDTO.getBillableCustomerId());

            if (!CollectionUtils.isEmpty(requestDTO.getPlanMappingList())) {
                Optional<CustPlanMapppingPojo> planMappingPojo = requestDTO.getPlanMappingList().stream().filter(custPlanMapppingPojo -> custPlanMapppingPojo.getPlanId().equals(planMapppingPojo1.getPlanId())).findAny();
                if (planMappingPojo.isPresent()) {
                    planMapppingPojo1.setNewAmount(planMappingPojo.get().getNewAmount());
//                    planMapppingPojo1.setBillTo(planMappingPojo.get().getBillTo());
//                    planMapppingPojo1.setIsInvoiceToOrg(planMappingPojo.get().getIsInvoiceToOrg());

                    if (planMappingPojo.get().getBillTo() != null)
                        planMapppingPojo1.setBillTo(planMappingPojo.get().getBillTo());
                    else planMapppingPojo1.setBillTo(com.adopt.apigw.constants.Constants.CUSTOMER);

                    if (planMappingPojo.get().getIsInvoiceToOrg() != null)
                        planMapppingPojo1.setIsInvoiceToOrg(planMappingPojo.get().getIsInvoiceToOrg());
                    else planMapppingPojo1.setIsInvoiceToOrg(false);
                }
            }
            //Entry in quotadtls
            for (CustPlanMapppingPojo planMapppingPojo : Collections.singleton(planMapppingPojo1)) {
                PostpaidPlanPojo plan = postpaidPlanMapper.domainToDTO(planService.get(planMapppingPojo.getPlanId(), customers.getMvnoId()), new CycleAvoidingMappingContext());
                if (null != plan && null != plan.getQuotatype()) {
                    Double totalQuotaForSeconds = 0.0;
                    Double totalQuotaForKB = 0.0;
                    if (null != plan.getQuotaunittime() && plan.getQuotaunittime().equalsIgnoreCase(com.adopt.apigw.constants.Constants.MINUTE)) {
                        totalQuotaForSeconds = plan.getQuotatime() * 60;
                    }
                    if (null != plan.getQuotaunittime() && plan.getQuotaunittime().equalsIgnoreCase(com.adopt.apigw.constants.Constants.HOUR)) {
                        totalQuotaForSeconds = plan.getQuotatime() * 60 * 60;
                    }
                    if (null != plan.getQuotaUnit() && plan.getQuotaUnit().equalsIgnoreCase(com.adopt.apigw.constants.Constants.MB)) {
                        totalQuotaForKB = (double) plan.getQuota() * 1024;
                    }
                    if (null != plan.getQuotaUnit() && plan.getQuotaUnit().equalsIgnoreCase(com.adopt.apigw.constants.Constants.GB)) {
                        totalQuotaForKB = (double) plan.getQuota() * 1024 * 1024;
                    }
                    CustQuotaDtlsPojo quotaDetails = null;
                    if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.TIME_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), 0.0, 0.0, null, plan.getQuotatime(), 0.0, plan.getQuotaunittime(), 0.0, 0.0, 0.0, totalQuotaForSeconds, customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()), plan.getName(), plan.getPlanGroup());
                    }
                    if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.DATA_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), (double) plan.getQuota(), 0.0, plan.getQuotaUnit(), 0.0, 0.0, null, totalQuotaForKB, 0.0, 0.0, 0.0, customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()), plan.getName(), plan.getPlanGroup());
                    }
                    if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.BOTH_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), (double) plan.getQuota(), 0.0, plan.getQuotaUnit(), plan.getQuotatime(), 0.0, plan.getQuotaunittime(), totalQuotaForKB, 0.0, 0.0, totalQuotaForSeconds, customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()), plan.getName(), plan.getPlanGroup());
                    }
                    if (plan.isUseQuota()) {
                        quotaDetails.setChunkAvailable(plan.isUseQuota());
                    } else {
                        quotaDetails.setChunkAvailable(false);
                    }
                    if (plan.getChunk() != null) {
                        quotaDetails.setReservedQuotaInPer(plan.getChunk());
                    } else {
                        quotaDetails.setReservedQuotaInPer(0.0);
                    }

                    quotaDetails.setTotalReservedQuota(0.0000);
                    String parentQuotaType = customersService.getParentQuotaByCustomerId(customers.getId());
                    if (parentQuotaType != null) {
                        quotaDetails.setParentQuotaType(parentQuotaType);
                    }
                    quotaDetails.setLastQuotaReset(LocalDateTime.now());
                    if (plan.getUsageQuotaType() != null) {
                        quotaDetails.setUsageQuotaType(plan.getUsageQuotaType());
                    } else {
                        quotaDetails.setUsageQuotaType("TOTAL");
                    }
                    //quotaDetails.setLastQuotaReset(LocalDateTime.now());

                    planMapppingPojo.setQuotaList(Collections.singletonList(quotaDetails));
                }
                planMapppingPojo1.setQuotaList(planMapppingPojo.getQuotaList());
            }
            try {
                if (null != customers.getVoicesrvtype() && customers.getVoiceprovision() && null != postpaidPlan.getParam1()) {
                    nexgeVoiceProvisionService.performServicePlanUpdate(customers.getId().toString(), customers.getAcctno(), postpaidPlan.getParam1());
                }
            } catch (Exception ex) {
                ApplicationLogger.logger.error(SUBMODULE + " Change Plan ", ex.getMessage(), ex);
                throw ex;
            }

//            if (requestDTO.getIsPaymentReceived() && !onlinePurchase) {
//                RecordpaymentResponseDTO recordpaymentResponseDTO = recordPayment(requestDTO.getRecordPaymentDTO(), customers);
//                if (null != recordpaymentResponseDTO && null != recordpaymentResponseDTO.getCreditDocument() && 0 < recordpaymentResponseDTO.getCreditDocument().size()) {
//                    CreditDocument creditDocument = recordpaymentResponseDTO.getCreditDocument().get(0);
//                    planMapppingPojo1.setCreditdocid(creditDocument.getId().longValue());
//                }
//                customChangePlanDTO.setRecordpaymentResponseDTO(recordpaymentResponseDTO);
//            }
//            if (!requestDTO.getIsPaymentReceived() && onlinePurchase) {
//                //Payment record
//                RecordPaymentRequestDTO recordPaymentDto = new RecordPaymentRequestDTO(CommonUtils.PAYMENT_MODE_ONLINE, LocalDateTime.now().toLocalDate(), onlinePaidAmount, false, "", customers.getId());
//                RecordpaymentResponseDTO recordpaymentResponseDTO = subscriberService.recordPayment(recordPaymentDto, customers);
//
//                //Invoke Receipt therad
//                if (null != customers) {
//                    List<CreditDocument> creditDocumentList = recordpaymentResponseDTO.getCreditDocument();
//                    Runnable receiptRunnable = new ReceiptThread(billRunService, creditDocumentList);
//                    Thread receiptThread = new Thread(receiptRunnable);
//                    receiptThread.start();
//                }
//
//                //Set creditDoc id in custpack
//                if (null != recordpaymentResponseDTO && null != recordpaymentResponseDTO.getCreditDocument() && 0 < recordpaymentResponseDTO.getCreditDocument().size()) {
//                    CreditDocument creditDocument = recordpaymentResponseDTO.getCreditDocument().get(0);
//                    planMapppingPojo1.setCreditdocid(creditDocument.getId().longValue());
//                }
//
//            }

            if (requestDTO.getWalletBalUsed() != null) {
                planMapppingPojo1.setWalletBalUsed(requestDTO.getWalletBalUsed());
            } else {
                planMapppingPojo1.setWalletBalUsed(0.0);
            }

            if (requestDTO.getPurchaseId() != null) {
                planMapppingPojo1.setOnlinePurchaseId(requestDTO.getPurchaseId());
            }

            if (postpaidPlan.getPlanGroup() != null) {
                planMapppingPojo1.setPurchaseType(postpaidPlan.getPlanGroup());
            }

            if (requestDTO.getPurchaseFrom() != null) {
                planMapppingPojo1.setPurchaseFrom(requestDTO.getPurchaseFrom());
            } else {
                if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                    planMapppingPojo1.setPurchaseFrom(SubscriberConstants.PURCHASE_FROM_ADMIN);
                } else {
                    planMapppingPojo1.setPurchaseFrom(SubscriberConstants.PURCHASE_FROM_PARTNER);
                }
            }

            planMapppingPojo1.setValidity(postpaidPlan.getValidity());
            if (requestDTO.getDiscount() != null && requestDTO.getDiscount() != 0) {
                planMapppingPojo1.setDiscount(requestDTO.getDiscount());
            } else if (customersPojo.getDiscount() != 0) planMapppingPojo1.setDiscount(customersPojo.getDiscount());
            else if (custPlanMappping != null) {
                if (custPlanMappping.getDiscount() != null)
                    planMapppingPojo1.setDiscount(custPlanMappping.getDiscount());
                else planMapppingPojo1.setDiscount(0d);

            } else if (requestDTO.getPlanBindWithOldPlans() != null && requestDTO.getPlanBindWithOldPlans().size() > 0 && requestDTO.getDiscount() == null) {
                for (NewPlanBindWithOldPlan newPlanBindWithOldPlan : requestDTO.getPlanBindWithOldPlans()) {
                    planMapppingPojo1.setDiscount(newPlanBindWithOldPlan.getDiscount());

                }
            } else {
                planMapppingPojo1.setDiscount(0d);
            }
//            Optional<PlanService> services = planServiceRepository.findById(postpaidPlan.getServiceId());
            Optional<PlanService> services = Optional.ofNullable(planServiceService.get(postpaidPlan.getServiceId(), customers.getMvnoId()));

            if (services.isPresent()) {
                if (services.get().getExpiry() != null) {
                    if (services.get().getExpiry().equalsIgnoreCase(com.adopt.apigw.constants.Constants.AT_MIDNIGHT)
                            && !(postpaidPlan.getPlanGroup() != null && (postpaidPlan.getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_BANDWIDTH_BOOSTER) || postpaidPlan.getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER)))) {
                        planMapppingPojo1.setExpiryDate(LocalDateTime.of(planMapppingPojo1.getExpiryDate().toLocalDate(), LocalTime.of(23, 59, 59)));
                        planMapppingPojo1.setEndDate(LocalDateTime.of(planMapppingPojo1.getEndDate().toLocalDate(), LocalTime.of(23, 59, 59)));
                    }
                }
            }
            if (customers.getIsinvoicestop()) {
                planMapppingPojo1.setIsInvoiceCreated(false);
                planMapppingPojo1.setIsinvoicestop(true);
            }
            if (requestDTO.getPlanGroupId() == null && requestDTO.getCustServiceMappingId() != null) {
                planMapppingPojo1.setCustServiceMappingId(requestDTO.getCustServiceMappingId());
            } else {
                QCustomerServiceMapping qCustomerServiceMapping = QCustomerServiceMapping.customerServiceMapping;
                BooleanExpression bCustSerMap = qCustomerServiceMapping.isNotNull().and(qCustomerServiceMapping.custId.in(planMapppingPojo1.getCustid())).and(qCustomerServiceMapping.serviceId.in(postpaidPlan.getServiceId()));
                List<CustomerServiceMapping> customerServiceMappings = new ArrayList<>();
                customerServiceMappings = (List<CustomerServiceMapping>) customerServiceMappingRepository.findAll(bCustSerMap);
                if (!customerServiceMappings.isEmpty()) {
                    CustomerServiceMapping customerServiceMapp = customerServiceMappings.get(0);
                    if (Objects.isNull(customerServiceMapp.getConnectionNo())) {
//                        customerServiceMapp = customersService.generateConnectionNumber(customerServiceMapp);
                        Boolean isLCO = customers.getLcoId() != null;
                        String connectionNo = numberSequenceUtil.getConnectionNumber(isLCO, customers.getLcoId(), customers.getMvnoId());
                        customerServiceMapp.setConnectionNo(connectionNo);
                    }
                    Integer custServiceMappingId = customerServiceMapp.getId();
                    planMapppingPojo1.setCustServiceMappingId(custServiceMappingId);
                    planMapppingPojo1.setInvoiceType(customerServiceMapp.getInvoiceType());
                }
                if (customerServiceMappings.isEmpty()) {
                    CustomerServiceMapping mapping = new CustomerServiceMapping();
                    mapping.setServiceId(Long.valueOf(postpaidPlan.getServiceId()));
                    mapping.setCustId(requestDTO.getCustId());
//                    mapping = customersService.generateConnectionNumber(mapping);
                    Boolean isLCO = customers.getLcoId() != null;
                    String connectionNo = numberSequenceUtil.getConnectionNumber(isLCO, customers.getLcoId(), customers.getMvnoId());
                    mapping.setConnectionNo(connectionNo);
                    CustomerServiceMapping savedCustomerServiceMapping = new CustomerServiceMapping();
                    savedCustomerServiceMapping = customerServiceMappingRepository.save(mapping);
                    planMapppingPojo1.setCustServiceMappingId(savedCustomerServiceMapping.getId());
                } else {
                    if (customerServiceMapping.isPresent()) {
                        customerServiceMapping.get().setStatus(CommonConstants.ACTIVE_STATUS);
                        customerServiceMapping.get().setCustId(planMapppingPojo1.getCustid());
                        planMapppingPojo1.setCustServiceMappingId(customerServiceMapping.get().getId());
                        customerServiceMappingRepository.save(customerServiceMapping.get());
                    }
                }
            }
            List<CustPlanMappping> planMapppingList = custPlanMappingRepository.findAllByCustServiceMappingId(requestDTO.getCustServiceMappingId());//TODO
            if (!CollectionUtils.isEmpty(planMapppingList)) {
                planMapppingPojo1.setInvoiceType(planMapppingList.get(0).getInvoiceType());
                if (requestDTOs != null) {
                    planMapppingPojo1.setRenewalForBooster(requestDTOs.getRenewalForBooster());
                } else {
                    planMapppingPojo1.setRenewalForBooster(false);
                }
            } else {
                planMapppingPojo1.setInvoiceType(com.adopt.apigw.constants.Constants.INDEPENDENT_INVOICE_TYPE);
            }
            if (requestDTO.getVoucherId() != null) {
                planMapppingPojo1.setVoucherId(requestDTO.getVoucherId());
            }
            CustPlanMappping mapping;
            if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW)) {
                //operation renew
                mapping = custPlanMappingService.save(customerMapper.mapCustPlanMapPojoToCustPlanMap(planMapppingPojo1, new CycleAvoidingMappingContext()), CommonConstants.EVENTCONSTANTS.RENEW_PLAN);
            } else if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON)) {
                //operation addOn
                if (planMapppingPojo1.getPurchaseType().equalsIgnoreCase("Bandwidthbooster")) {
                    mapping = custPlanMappingService.save(customerMapper.mapCustPlanMapPojoToCustPlanMap(planMapppingPojo1, new CycleAvoidingMappingContext()), CommonConstants.EVENTCONSTANTS.NEW_BANDWIDTH_BOOSTER, requestDTO.getIsTriggerCoaDm());
                } else if (planMapppingPojo1.getPurchaseType().equalsIgnoreCase("Volume Booster")) {
                    mapping = custPlanMappingService.save(customerMapper.mapCustPlanMapPojoToCustPlanMap(planMapppingPojo1, new CycleAvoidingMappingContext()), CommonConstants.EVENTCONSTANTS.NEW_VOLUME_BOOSTER, requestDTO.getIsTriggerCoaDm());
                } else {
                    mapping = null;
                }
            } else {
                mapping = null;
            }

            AutoRenewalPreferenceMessage autoRenewalPreferenceMessage = new AutoRenewalPreferenceMessage(com.adopt.apigw.constants.Constants.AUTO_RENEWAL_PREFERANCE, customers.getUsername(), customers.getId(), customers.getMvnoId(), customers.getEmail(), customers.getBuId(), mapping.getRenewalForBooster(), customers.getPhone());
            Gson gson = new Gson();
            gson.toJson(autoRenewalPreferenceMessage);
            kafkaMessageSender.send(new KafkaMessageData(autoRenewalPreferenceMessage, AutoRenewalPreferenceMessage.class.getSimpleName()));

            PostpaidPlanPojo planPojo = postpaidPlanMapper.domainToDTO(postpaidPlan, new CycleAvoidingMappingContext());
            customersService.saveCustomerChargeHistory(postpaidPlan, customersPojo, mapping, requestDTO.getPlanGroupId(), false, requestDTO.getPurchaseType(), null);
            if (planMapppingPojo1.getBillTo().equalsIgnoreCase(com.adopt.apigw.constants.Constants.ORGANIZATION)) {// && planMapppingPojo1.getIsInvoiceToOrg()) {
                planMapppingPojo1.setCustRefId(customersPojo.getId());
                planMapppingPojo1.setCustRefName(customersPojo.getUsername());
                CustomersPojo subscriber = new CustomersPojo();
                planMapppingPojo1.setIsInvoiceToOrg(true);
                planMapppingPojo1.setId(mapping.getId());
                subscriber.setPlanMappingList(Collections.singletonList(planMapppingPojo1));
                subscriber.setId(planMapppingPojo1.getCustid());
                subscriber.setUsername(customersPojo.getUsername());
                subscriber.setFirstname(customersPojo.getFirstname());
                customersService.saveDataForOrganizationCustomer(subscriber, planMapppingPojo1.getDiscount(), planMapppingPojo1.getValidity().longValue(), null, customersPojo.getCusttype());
            }
            if (activeCustPlanModel != null) {
                // Subscriber Update
                SubscriberUpdateUtils.updateSubscriber(activeCustPlanModel.getPlanName(), postpaidPlan.getName(), UpdateConstant.UPDATE_PLAN, customers, requestDTO.getRemarks(), null);
            } else {
                // Subscriber Update
                SubscriberUpdateUtils.updateSubscriber(null, postpaidPlan.getName(), UpdateConstant.UPDATE_PLAN, customers, requestDTO.getRemarks(), null);
            }

            //customers.setNextBillDate(LocalDate.now());
            customersService.update(customers);
            sendRenewRechargeMessage(customers.getUsername(), customers.getCountryCode(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), postpaidPlan.getName(), requestDTO.getPurchaseType(), (long) customersService.getLoggedInStaffId());

            if (quotaDtlsModel != null && addQuotaInExistingPlan) {
                this.changeQuota(quotaDtlsModel, customers);
            }

            //communication for change plan
            //commented as no use
//            CommunicationHelper communicationHelper = new CommunicationHelper();
//            Map<String, String> map = new HashMap<>();
//            map.put(CommunicationConstant.PLAN_NAME, postpaidPlan.getDisplayName());
//            map.put(CommunicationConstant.DATE, LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
//            map.put(CommunicationConstant.DESTINATION, customersPojo.getMobile());
//            map.put(CommunicationConstant.EMAIL, customersPojo.getEmail());
//            communicationHelper.generateCommunicationDetails(CommunicationConstant.PACKGE_RENEWED_1, Collections.singletonList(map));

            customChangePlanDTO.setCustomersBasicDetailsPojo(getBasicDetailsOfSubscriber(customers));
            //dbrService.addDbrForPrepaidCustomerForChangePlan(mapping,customers);
            if (mapping != null) customChangePlanDTO.setCustpackagerelid(mapping.getId());
//            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(requestDTO.getCustServiceMappingId()).orElse(null);
            if (customerServiceMapping != null) {
                CustPlanMappping finalMapping = mapping;
                tempActivePlanList.forEach((customerPlansModel) -> {
                    if (customerPlansModel.getCustomerServiceMappingId() == customerServiceMapping.get().getId()) {
                        ezBillServiceUtility.renewPlanInEzBill(finalMapping, customerPlansModel.getPlanmapid(), requestDTO.getPurchaseType());
                    }
                });
            }
            if ((mapping.getStartDate().equals(LocalDateTime.now()) || mapping.getStartDate().isBefore(LocalDateTime.now())) && CollectionUtils.isEmpty(tempActivePlanList)) {
                //Ezbill call for renew plan
                ezBillServiceUtility.renewPlanInEzBill(mapping, mapping.getId(), requestDTO.getPurchaseType());
            }
            //Required for create invoice for renew
            customChangePlanDTO.setRenewalId(mapping.getRenewalId());
            Instant instant = mapping.getStartDate().atZone(ZoneId.systemDefault()).toInstant();
            Instant instant1 = mapping.getEndDate().atZone(ZoneId.systemDefault()).toInstant();
            customChangePlanDTO.setStartdate(Date.from(instant));
            customChangePlanDTO.setEnddate(Date.from(instant1));
            return customChangePlanDTO;
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    private LocalDateTime getParentMaxExpiryDateByService(Integer serviceId, Customers customers) {
        String serviceName = planServiceService.get(serviceId, customers.getMvnoId()).getName();
        LocalDateTime localDateTime = null;
        List<CustomerPlansModel> activePlanList = this.getActivePlanList(customers.getId(), false).stream().filter(data -> data.getService().equalsIgnoreCase(serviceName)).filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
        if (activePlanList.size() > 0)
            localDateTime = Instant.ofEpochMilli(activePlanList.get(0).getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();

        List<CustomerPlansModel> futurePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> data.getService().equalsIgnoreCase(serviceName)).filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
        if (futurePlanList.size() > 0)
            localDateTime = Instant.ofEpochMilli(futurePlanList.get(0).getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime;
    }

    public List<CustChargeDetailsPojo> getSubscriberPurchasedCharge(Customers customers) {
        return custChargeService.findCustChargeByChargeCategory(customers, ChargeConstants.CHARGE_CATEGORY_IP);
    }

    public List<CustChargeDetailsPojo> getSubscriberPurchasedChargeForIpRollback(Customers customers) {
        return custChargeService.findCustChargeForRollback(customers, ChargeConstants.CHARGE_CATEGORY_IP);
    }

    @Transactional
    public CustomersBasicDetailsPojo allocateIp(Customers customers, AllocateIpDTO requestDTO, Boolean is_replace) throws Exception {
        String SUBMODULE = MODULE + " [allocateIp()] ";
        try {
            CustChargeDetailsPojo chargeDetailsPojo = custChargeDetailMapper.domainToDTO(custChargeService.get(requestDTO.getChargeId(), customers.getMvnoId()), new CycleAvoidingMappingContext());
            //OLD VALUE
            String newValue = null;
            if (chargeDetailsPojo != null) {
                ChargePojo chargePojo = chargeService.convertChargeModelToChargePojo(chargeService.get(chargeDetailsPojo.getChargeid(), customers.getMvnoId()));
                IPPoolDtlsDTO ipPoolDtlsDTO = ipPoolDtlsService.getEntityById(requestDTO.getIpPoolDtlsId(), customers.getMvnoId());
                newValue = ipPoolDtlsDTO.getIpAddress();
                if (is_replace) {
                    chargeDetailsPojo.setIsUsed(false);
                }
                if (ipPoolDtlsDTO.getStatus().equalsIgnoreCase(IpConfigConstant.IP_STATUS_BLOCK) && ipPoolDtlsDTO.getBlockByCustId() == customers.getId().longValue()) {
                    if (!chargeDetailsPojo.getIsUsed() && chargePojo.getChargecategory().equalsIgnoreCase(ChargeConstants.CHARGE_CATEGORY_IP)) {
                        IPAllocationDTO allocationDTO = new IPAllocationDTO();
                        allocationDTO.setCustId(customers.getId().longValue());
                        allocationDTO = ipAllocationService.saveEntity(allocationDTO);

                        if (allocationDTO != null && allocationDTO.getId() != null) {
                            ipPoolDtlsDTO.setStatus(IpConfigConstant.IP_STATUS_ALLOCATED);
                            ipPoolDtlsDTO.setAllocatedId(allocationDTO.getId());
                            ipPoolDtlsDTO.setUnblockTime(null);
                            ipPoolDtlsDTO.setBlockByCustId(null);
                            ipPoolDtlsDTO = ipPoolDtlsService.saveEntity(ipPoolDtlsDTO);

                            allocationDTO.setPoolDetailsId(ipPoolDtlsDTO.getPoolDetailsId());
                            allocationDTO = ipAllocationService.saveEntity(allocationDTO);
                            chargeDetailsPojo.setIppooldtlsid(ipPoolDtlsDTO.getPoolDetailsId());
                            chargeDetailsPojo.setPurchaseEntityId(allocationDTO.getId());
                            chargeDetailsPojo.setIsUsed(true);
                            custChargeService.save(custChargeDetailMapper.dtoToDomain(chargeDetailsPojo, new CycleAvoidingMappingContext()));
                        }
                    } else {
                        throw new RuntimeException("Given ip charge is already used or charge category is not IP");
                    }
                } else {
                    throw new RuntimeException("Given ip is already allocated or not blocked for this subscriber");
                }
                // Subscriber Update
                SubscriberUpdateUtils.updateSubscriber(null, newValue, UpdateConstant.ALLOCATE_NEW_IP, customers, requestDTO.getRemarks(), null);
                return getBasicDetailsOfSubscriber(customers);
            } else {
                throw new RuntimeException("Applied charges for ip allocation is not found");
            }
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<SubscriberAllocatedIP> getSubscriberAllocatedIp(Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [getSubscriberAllocatedIp()] ";
        try {
            List<CustChargeDetailsPojo> custChargeDetailsPojoList = custChargeService.findCustChargeByChargeCategoryUsed(customers, ChargeConstants.CHARGE_CATEGORY_IP);
            if (custChargeDetailsPojoList.size() > 0) {
                List<SubscriberAllocatedIP> allocatedIPList = new ArrayList<>();
                custChargeDetailsPojoList.forEach(data -> {
                    try {
                        SubscriberAllocatedIP allocatedIP = null;
//                        IPAllocationDTO ipAllocationDTO = ipAllocationService.getEntityById(data.getPurchaseEntityId());
                        IPPoolDtlsDTO ipPoolDtlsDTO = ipPoolDtlsService.findByAllocatedIp(data.getPurchaseEntityId());
                        if (ipPoolDtlsDTO != null && ipPoolDtlsDTO.getAllocatedId() != null) {
                            IPPoolDTO ipPoolDTO = ipPoolService.getEntityById(ipPoolDtlsDTO.getPoolId(), customers.getMvnoId());
                            if (ipPoolDTO != null) {
                                allocatedIP = new SubscriberAllocatedIP(ipPoolDtlsDTO.getPoolDetailsId(), ipPoolDTO.getPoolId(), data.getChargeName(), data.getPrice(), ipPoolDtlsDTO.getIpAddress(), ipPoolDtlsDTO.getAllocatedId(), data.getStartdate(), data.getEnddate(), ipPoolDTO.getPoolName(), data.getId());
                            }
                        }
                        if (allocatedIP != null) {
                            allocatedIPList.add(allocatedIP);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return allocatedIPList;
            } else {
                throw new RuntimeException("No any charges found");
            }
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            e.printStackTrace();
            throw e;
        }
    }

    public IPPoolDtlsDTO releaseIp(IPPoolDtlsDTO ipPoolDtlsDTO, Customers customers, String reason) throws Exception {
        String SUBMODULE = MODULE + " [releaseIp()] ";
        try {
            if (ipPoolDtlsDTO != null) {
                IPPoolDtlsDTO tempIpPoolDtlsDTO = ipPoolDtlsService.getEntityById(ipPoolDtlsDTO.getPoolDetailsId(), customers.getMvnoId());
                if (!tempIpPoolDtlsDTO.getStatus().equalsIgnoreCase(IpConfigConstant.IP_STATUS_FREE)) {
                    tempIpPoolDtlsDTO.setStatus(IpConfigConstant.IP_STATUS_FREE);
                    tempIpPoolDtlsDTO.setAllocatedId(null);
                    tempIpPoolDtlsDTO = ipPoolDtlsService.saveEntity(tempIpPoolDtlsDTO);

                    IPAllocationDTO ipAllocationDTO = ipAllocationService.getEntityById(ipPoolDtlsDTO.getAllocatedId(), customers.getMvnoId());
                    if (ipAllocationDTO.getCustId() == customers.getId().longValue()) {
                        ipAllocationDTO.setTerminatedDate(LocalDateTime.now());
                        ipAllocationDTO.setTerminationReason(reason);
                        ipAllocationDTO.setIsSystemUpdated(false);
                        ipAllocationDTO = ipAllocationService.saveEntity(ipAllocationDTO);
                    } else {
                        throw new RuntimeException("Provided customer is not matched");
                    }
                } else {
                    throw new RuntimeException("Given IP Already Free");
                }
                // Subscriber Update
                SubscriberUpdateUtils.updateSubscriber(null, tempIpPoolDtlsDTO.getIpAddress(), UpdateConstant.RELEASE_IP, customers, ipPoolDtlsDTO.getRemarks(), null);
            }
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            e.printStackTrace();
            throw e;
        }
        return ipPoolDtlsDTO;
    }

    @Transactional
    public CustomersBasicDetailsPojo replaceIp(ReplaceIPDTO requestDTO, Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [replaceIp()] ";
        try {
            //Release Ip
            IPPoolDtlsDTO ipPoolDtlsDTO = new IPPoolDtlsDTO();
            ipPoolDtlsDTO.setPoolDetailsId(requestDTO.getCurrentPoolDetailsId());
            ipPoolDtlsDTO.setAllocatedId(requestDTO.getCurrentAllocatedId());
            ipPoolDtlsDTO.setRemarks(requestDTO.getRemarks());
            ipPoolDtlsDTO = this.releaseIp(ipPoolDtlsDTO, customers, IpConfigConstant.IP_TERMINATION_RELEASE);

            //Allocate New Ip
            AllocateIpDTO allocateIpDTO = new AllocateIpDTO();
            allocateIpDTO.setChargeId(requestDTO.getCurrentChargeId());
            allocateIpDTO.setIpPoolDtlsId(requestDTO.getNewPoolDetailsId());
            allocateIpDTO.setRemarks(requestDTO.getRemarks());
            return this.allocateIp(customers, allocateIpDTO, true);
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public ReverseChargeRequestDTO rollBackIp(ReverseChargeRequestDTO requestDTO, Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [rollBackIp()] ";
        try {
            //Reverse charge
            ReversableChargeResponseDTO responseDTO = this.reverseCharge(requestDTO, customers);

            //Release charge
            CustChargeDetails custChargeDetails = this.custChargeService.findByChargeByid(requestDTO.getCharge_id());
            IPPoolDtlsDTO ipPoolDtlsDTO = ipPoolDtlsService.findByAllocatedIp(custChargeDetails.getPurchaseEntityId());
            if (ipPoolDtlsDTO != null) {
                ipPoolDtlsDTO = this.releaseIp(ipPoolDtlsDTO, customers, IpConfigConstant.IP_TERMINATION_ROLLBACK);
            }

            // Subscriber Update
            String oldVal = null;
            if (ipPoolDtlsDTO != null) {
                oldVal = ipPoolDtlsDTO.getIpAddress();
            } else {
                oldVal = "Reverse Ip charge";
            }
            SubscriberUpdateUtils.updateSubscriber(oldVal, "", UpdateConstant.REVERSE_IP, customers, requestDTO.getRev_remarks(), null);
            return requestDTO;
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public CustChargeDetailsPojo changeIPExpiry(ChangeIPExpiryDTO requestDTO, Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [changeIPExpiry()] ";
        try {
            CustChargeDetailsPojo chargeDetailsPojo = custChargeDetailMapper.domainToDTO(custChargeService.get(requestDTO.getCurrentChargeId(), customers.getMvnoId()), new CycleAvoidingMappingContext());
            String oldVal = chargeDetailsPojo.getEnddate().toString();
            CustChargeDetails custChargeDetails = null;
            if (chargeDetailsPojo != null) {
                if (!requestDTO.getRevisedExpiryDate().before(chargeDetailsPojo.getEnddate())) {
                    chargeDetailsPojo.setEnddate(requestDTO.getRevisedExpiryDate());
                    custChargeDetails = custChargeService.save(custChargeDetailMapper.dtoToDomain(chargeDetailsPojo, new CycleAvoidingMappingContext()));
                    chargeDetailsPojo = custChargeDetailMapper.domainToDTO(custChargeDetails, new CycleAvoidingMappingContext());
                } else {
                    throw new RuntimeException("Revised date can not be less than end date");
                }
                // Subscriber Update
                SubscriberUpdateUtils.updateSubscriber(oldVal, custChargeDetails.getEnddate().toString(), UpdateConstant.CHANGE_IP_EXPIRY, customers, customers.getRemarks(), null);
                return chargeDetailsPojo;
            } else {
                throw new RuntimeException("Charge not found");
            }
        } catch (RuntimeException e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Transactional
    public CustomAdjustPayModel adjustPayment(AdjustPaymentDTO requestDTO, Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [adjustPayment()] ";
        CustomAdjustPayModel customAdjustPayModel = new CustomAdjustPayModel();
        try {
            CustomerLedger custLedger = this.customerLedgerService.getCustomerLeger(customers).get(0);
            CustomerLedgerDtls custLedgerDtls = new CustomerLedgerDtls();
            final String desc = "Adjust Payment";
            custLedgerDtls.setCustomer(customers);
            custLedgerDtls.setAmount(requestDTO.getAmount());
            custLedgerDtls.setCreditdocid(null);
            custLedgerDtls.setDebitdocid(null);
            custLedgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_PAYMENT);
            custLedgerDtls.setDescription(desc);

            if (requestDTO.getPaymentType().equalsIgnoreCase(SubscriberConstants.PAYMENT_TYPE_CREDIT)) {
//                custLedgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
//                customerLedgerDtlsService.save(custLedgerDtls);
//                Double totalPaid = custLedger.getTotalpaid() + requestDTO.getAmount();
//                custLedger.setTotalpaid(Double.parseDouble(new DecimalFormat("##.##").format(totalPaid)));
//                customerLedgerService.save(custLedger);

                RecordPaymentRequestDTO recordPaymentRequestDTO = new RecordPaymentRequestDTO(UtilsCommon.PAYMENT_MODE_ADJUST, LocalDate.now(), requestDTO.getAmount(), false, requestDTO.getRemarks(), customers.getId());
                customAdjustPayModel.setRecordpaymentResponseDTO(this.recordPayment(recordPaymentRequestDTO, customers));

            } else if (requestDTO.getPaymentType().equalsIgnoreCase(SubscriberConstants.PAYMENT_TYPE_DEBIT)) {
                custLedgerDtls.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
                customerLedgerDtlsService.save(custLedgerDtls);
                Double totalDue = custLedger.getTotaldue() + requestDTO.getAmount();
                custLedger.setTotaldue(Double.parseDouble(new DecimalFormat("##.##").format(totalDue)));
                customerLedgerService.save(custLedger);
            } else {
                throw new RuntimeException("Please Select Proper Payment Type");
            }
            // Subscriber Update
            SubscriberUpdateUtils.updateSubscriber("", requestDTO.getPaymentType() + " " + requestDTO.getAmount(), UpdateConstant.ADJUST_PAYMENT, customers, requestDTO.getRemarks(), null);

            customAdjustPayModel.setBasicDetailsPojo(getBasicDetailsOfSubscriber(customers));
            return customAdjustPayModel;
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        }
    }

    public List<Customers> getSubscriberFromUsername(String username) {
        return customersRepository.findByUsername(username);
    }

    public List<Customers> getActiveSubscriberFromUsername(String username) {
        return customersRepository.findByUsernameAndIsDeletedIsFalse(username).stream().filter(cust -> cust != null && !cust.getStatus().equalsIgnoreCase("Terminate")).collect(Collectors.toList());
    }

    public SubscriberPlanDetailsDTO getSubscriberPlanDetails(CustPlanMapppingPojo customerPlansModel) throws Exception {
        ReverableChargePojo reverableChargePojo = new ReverableChargePojo();
        String SUBMODULE = MODULE + " [getSubscriberPLanDetails] ";
        try {
            SubscriberPlanDetailsDTO subscriberPlanDetailsDTO = new SubscriberPlanDetailsDTO();
            PostpaidPlanPojo postpaidPlanPojo = postpaidPlanMapper.domainToDTO(planService.get(customerPlansModel.getPlanId(), customerPlansModel.getCustomer().getMvnoId()), new CycleAvoidingMappingContext());
            if (postpaidPlanPojo != null) {
                subscriberPlanDetailsDTO.setPlanId(customerPlansModel.getId().longValue());
                subscriberPlanDetailsDTO.setPlanName(postpaidPlanPojo.getName());
                subscriberPlanDetailsDTO.setActivationDate(customerPlansModel.getStartDate().toLocalDate());
                subscriberPlanDetailsDTO.setExpiryDate(customerPlansModel.getExpiryDate().toLocalDate());
                subscriberPlanDetailsDTO.setUserName(customerPlansModel.getCustomer().getUsername());
                subscriberPlanDetailsDTO.setFullName(customerPlansModel.getCustomer().getFirstname() + " " + customerPlansModel.getCustomer().getFirstname());
                subscriberPlanDetailsDTO.setOutStanding(Double.parseDouble(new DecimalFormat("##.##").format(customerPlansModel.getCustomer().getOutstanding())));
                if (customerPlansModel.getStartDate().toLocalDate().isAfter(LocalDate.now())) {
                    //That's means starting date is future
                    subscriberPlanDetailsDTO.setUsedDays(0L);
                    subscriberPlanDetailsDTO.setFullAmount(customerPlansModel.getOfferPrice() + customerPlansModel.getTaxAmount());
                    subscriberPlanDetailsDTO.setProratedAmount(customerPlansModel.getOfferPrice() + customerPlansModel.getTaxAmount());
                } else {
                    long usedDays = ChronoUnit.DAYS.between(customerPlansModel.getStartDate().toLocalDate(), LocalDate.now());
                    if (usedDays > 0) {
                        subscriberPlanDetailsDTO.setUsedDays(usedDays);
                        final long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now().toLocalDate(), customerPlansModel.getExpiryDate().toLocalDate());
                        final long totalServicedDays = ChronoUnit.DAYS.between(customerPlansModel.getStartDate().toLocalDate(), customerPlansModel.getExpiryDate().toLocalDate());
                        final Double oneDayAmount = ((customerPlansModel.getOfferPrice() + customerPlansModel.getTaxAmount()) / totalServicedDays);
                        final Double refundAmount = (remainingDays * oneDayAmount);
                        subscriberPlanDetailsDTO.setFullAmount(customerPlansModel.getOfferPrice() + customerPlansModel.getTaxAmount());
                        subscriberPlanDetailsDTO.setProratedAmount((Math.round(refundAmount * 100) / 100.00));
                    } else if (usedDays == 0) {
                        subscriberPlanDetailsDTO.setUsedDays(0L);
                        subscriberPlanDetailsDTO.setFullAmount(customerPlansModel.getOfferPrice() + customerPlansModel.getTaxAmount());
                        subscriberPlanDetailsDTO.setProratedAmount(customerPlansModel.getOfferPrice() + customerPlansModel.getTaxAmount());
                    } else {
                        throw new RuntimeException("Something went wrong");
                    }
                }
            } else {
                throw new RuntimeException("Postpaid plan not found!!");
            }

            return subscriberPlanDetailsDTO;
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public CustomersBasicDetailsPojo cancelPlan(CancelPlanRequestDTO requestDTO, Customers customers, CustPlanMapppingPojo customerPlansModel) throws Exception {
        String SUBMODULE = MODULE + " [cancelPlan()] ";
        try {
            BillRunService billRunService = SpringContext.getBean(BillRunService.class);
            CustomersPojo customersPojo = customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext());
            PostpaidPlanPojo postpaidPlanPojo = postpaidPlanMapper.domainToDTO(planService.get(customerPlansModel.getPlanId(), customers.getMvnoId()), new CycleAvoidingMappingContext());

            if (postpaidPlanPojo == null) {
                throw new RuntimeException("Mapped plan is doesn't exist");
            }
            LocalDateTime todaysDate = LocalDateTime.now();
            Double finalAmount = 0.0;

            if (requestDTO.getIsActive() && !requestDTO.getIsFuture()) {
                finalAmount = customerPlansModel.getOfferPrice();
            }
            if (!requestDTO.getIsActive() && requestDTO.getIsFuture()) {
                finalAmount = customerPlansModel.getOfferPrice() + customerPlansModel.getTaxAmount();
            }
            if (requestDTO.getIsProrated() && !requestDTO.getIsFullRefund()) {
                long usedDays = ChronoUnit.DAYS.between(customerPlansModel.getStartDate().toLocalDate(), LocalDate.now());
                if (usedDays > 0) {
                    final long remainingDays = ChronoUnit.DAYS.between(todaysDate.toLocalDate(), customerPlansModel.getExpiryDate().toLocalDate());
                    final long totalServicedDays = ChronoUnit.DAYS.between(customerPlansModel.getStartDate().toLocalDate(), customerPlansModel.getExpiryDate().toLocalDate());
                    final Double oneDayAmount = (finalAmount / totalServicedDays);
                    final Double refundAmount = (remainingDays * oneDayAmount);
                    finalAmount = (Math.round(refundAmount * 100) / 100.00);
                } else if (usedDays == 0) {
                    finalAmount = finalAmount;
                } else {
                    throw new RuntimeException("Something went wrong");
                }
            }
            if (!requestDTO.getIsFullRefund() && !requestDTO.getIsProrated()) {
                finalAmount = 0.0;
            }

            //Set End Date

            if (customerPlansModel != null) {
                customerPlansModel.setEndDate(todaysDate);
                custPlanMappingService.save(customerMapper.mapCustPlanMapPojoToCustPlanMap(customerPlansModel, new CycleAvoidingMappingContext()), "");
            }

            if (finalAmount > 0.0) {
                //Save new credit document
               /* PaymentHistoryDTO creditDocumentPojo = new PaymentHistoryDTO();
                creditDocumentPojo.setAmount(finalAmount);
                creditDocumentPojo.setPaymentdate(LocalDate.now());
                creditDocumentPojo.setCustId(customers.getId());
                creditDocumentPojo.setRemarks("Refund of cancel plan");
                creditDocumentPojo.setStatus(SubscriberConstants.PAYMENT_STATUS_APPROVED);
                CreditDocument creditDocument = this.creditDocService.save(creditDocumentMapper.dtoToDomain(creditDocumentPojo, new CycleAvoidingMappingContext()));*/

                //Save Customer ledger details
                CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
                ledgerDtls.setCustomer(customers);
                ledgerDtls.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(finalAmount)));
                ledgerDtls.setDescription("Refund against cancel plan");
                ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_REFUND);
                // ledgerDtls.setCreditdocid(creditDocument.getId());
                customerLedgerDtlsService.save(ledgerDtls);

                //Update customer ledger
                CustomerLedger customerLedger = this.customerLedgerService.getCustomerLeger(customers).get(0);
                customerLedger.setTotalpaid(Double.parseDouble(new DecimalFormat("##.##").format(customerLedger.getTotalpaid() + finalAmount)));
                customerLedger.setCustomer(customers);
                customerLedger.setUpdatedate(LocalDateTime.now());
                customerLedgerService.save(customerLedger);
            }

            // Subscriber Update
            SubscriberUpdateUtils.updateSubscriber("", postpaidPlanPojo.getName(), UpdateConstant.CANCEL_PLAN, customers, requestDTO.getRemarks(), null);
            return getBasicDetailsOfSubscriber(customers);
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<CustomCustChargeDetailsPojo> getSubscriberCharges(Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [getSubscriberCharges] ";
        try {
            List<CustomCustChargeDetailsPojo> resultList = chargeQueryRepository.getResultOfQuery(PlanQueryScript.getChargeDetailsByCustomer(customers.getId()), CustomCustChargeDetailsPojo.class);
            if (null != resultList && 0 < resultList.size()) {
                return resultList;
            }
            return new ArrayList<>();
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<SubscriberUpdateDTO> getSubscriberUpdates(Customers customers) throws Exception {
        String SUBMODULE = MODULE + " [getSubscriberUpdates] ";
        try {
            return subscriberUpdateService.getAllByCustomer(customers.getId());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Transactional
    public CustomersBasicDetailsPojo activatePlan(ActivatePlanReqModel requestDTO, Customers customers, CustPlanMapppingPojo customerPlansModel) throws Exception {
        String SUBMODULE = MODULE + " [activatePlan()] ";
        try {
            CustPlanMapppingPojo selectedFuturePlan = new CustPlanMapppingPojo(customerPlansModel.getStartDate(), customerPlansModel.getExpiryDate());

            List<CustomerPlansModel> currentPlanList = this.subscriberService.getActivePlanList(customers.getId(), false).stream().filter(data -> !data.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER)).collect(Collectors.toList());
            CustomersPojo customersPojo = customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext());
            LocalDateTime todaysDate = LocalDateTime.now();
            //End all active plans
            if (currentPlanList.size() > 0) {
                currentPlanList.forEach(data -> {
                    CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(data.getPlanmapid());
                    CustPlanMapppingPojo tempCustomerPlansModel = customerMapper.mapCustPlanMapToCustPlanMapPojo(custPlanMappping, new CycleAvoidingMappingContext());
                    tempCustomerPlansModel.setEndDate(todaysDate);
                    custPlanMappingService.save(customerMapper.mapCustPlanMapPojoToCustPlanMap(tempCustomerPlansModel, new CycleAvoidingMappingContext()), "");
                });
            }

            //Set requested plan as active plan
            Long setActiveValidty = ChronoUnit.DAYS.between(customerPlansModel.getStartDate().toLocalDate(), customerPlansModel.getExpiryDate().toLocalDate());
            customerPlansModel.setStartDate(LocalDateTime.now().plusSeconds(1));
            customerPlansModel.setExpiryDate(LocalDateTime.of(LocalDate.now().plusDays(setActiveValidty), LocalTime.of(00, 00, 00)));
            CustPlanMappping custPlanMappping = custPlanMappingService.save(customerMapper.mapCustPlanMapPojoToCustPlanMap(customerPlansModel, new CycleAvoidingMappingContext()), "");

            //Swip future plan activation and expiry dates
            List<CustomerPlansModel> futurePlanList = this.subscriberService.getFuturePlanList(customers.getId(), false).stream().filter(data -> !data.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER)).collect(Collectors.toList());

            List<CustomerPlansModel> upperFuturePlanList = new ArrayList<>();
            List<CustomerPlansModel> lowerFuturePlanList = new ArrayList<>();
            if (futurePlanList.size() > 0) {
                futurePlanList.forEach(data -> {
                    if (data.getExpiryDate().toLocalDate().isBefore(selectedFuturePlan.getStartDate().toLocalDate())) {
                        upperFuturePlanList.add(data);
                    } else {
                        lowerFuturePlanList.add(data);
                    }
                });
            }

            if (upperFuturePlanList != null) {
                upperFuturePlanList.forEach(data -> {
                    CancelPlanRequestDTO cancelPlanRequestDTO = new CancelPlanRequestDTO(data.getCustId(), true, false, "Refund against cancel plan", false, true);
                    CustPlanMappping tempCustPlanMappping = custPlanMappingRepository.findById(data.getPlanmapid());
                    try {
                        this.cancelPlan(cancelPlanRequestDTO, customers, customerMapper.mapCustPlanMapToCustPlanMapPojo(tempCustPlanMappping, new CycleAvoidingMappingContext()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            if (lowerFuturePlanList.size() > 0) {
                final LocalDateTime[] maxExpiryDate = {custPlanMappping.getExpiryDate()};
                lowerFuturePlanList.forEach(data -> {
                    CustPlanMappping futureCustPlanMappping = custPlanMappingRepository.findById(data.getPlanmapid());
                    CustPlanMapppingPojo tempCustomerPlansModel = customerMapper.mapCustPlanMapToCustPlanMapPojo(futureCustPlanMappping, new CycleAvoidingMappingContext());
                    Long setFutureValidty = ChronoUnit.DAYS.between(tempCustomerPlansModel.getStartDate().toLocalDate(), tempCustomerPlansModel.getExpiryDate().toLocalDate());
                    tempCustomerPlansModel.setStartDate(maxExpiryDate[0]);
                    tempCustomerPlansModel.setExpiryDate(tempCustomerPlansModel.getStartDate().plusDays(setFutureValidty));
                    maxExpiryDate[0] = tempCustomerPlansModel.getExpiryDate();
                    custPlanMappingService.save(customerMapper.mapCustPlanMapPojoToCustPlanMap(tempCustomerPlansModel, new CycleAvoidingMappingContext()), "");
                });
            }

            //Subscriber Update
            PostpaidPlan tempPostpaidPlan = this.planService.get(custPlanMappping.getPlanId(), customers.getMvnoId());
            SubscriberUpdateUtils.updateSubscriber("", tempPostpaidPlan.getName(), UpdateConstant.Active_PLAN, customers, requestDTO.getRemarks(), null);

            //communication for change plan
            CommunicationHelper communicationHelper = new CommunicationHelper();
            Map<String, String> map = new HashMap<>();
            map.put(CommunicationConstant.PLAN_NAME, tempPostpaidPlan.getDisplayName());
            map.put(CommunicationConstant.DATE, LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            map.put(CommunicationConstant.DESTINATION, customersPojo.getMobile());
            map.put(CommunicationConstant.EMAIL, customersPojo.getEmail());
            communicationHelper.generateCommunicationDetails(CommunicationConstant.PACKGE_RENEWED_1, Collections.singletonList(map));
            return getBasicDetailsOfSubscriber(customers);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public Boolean validatePurchaseAddon(ChangePlanRequestDTO requestDTO) {
        String SUBMODULE = MODULE + " [validate Addon Request()] ";
        try {
            ClientServicePojo clientServicePojo = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CONVERT_VOL_BOOST_TOPUP).get(0);
            if (clientServicePojo != null && clientServicePojo.getValue().equalsIgnoreCase("1")) {
                PostpaidPlan postpaidPlan = planService.get(requestDTO.getPlanId(), clientServicePojo.getMvnoId());
                if (postpaidPlan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_ADDON)) {
                    List<CustomerPlansModel> currentPlanList = this.subscriberService.getActivePlanList(requestDTO.getCustId(), false).stream().filter(data -> data.getService().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA) && data.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW) && !data.getVolTotalQuota().equalsIgnoreCase(SubscriberConstants.UNLIMITED_QUOTA) && (data.getQuotaType().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA) || data.getQuotaType().equalsIgnoreCase(CommonConstants.BOTH_QUOTA_TYPE))).collect(Collectors.toList());

                    if (currentPlanList == null || currentPlanList.size() <= 0) {
                        throw new RuntimeException("Something went wrong.");
                    }
                } else {
                    throw new RuntimeException("Selected plan is not for add on.");
                }
            } else {
                throw new RuntimeException("Client service for convert addon can't be null");
            }
            return false;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public AddonEligibilityRequestDTO checkEligibilityAddon(Customers customers) {
        String SUBMODULE = MODULE + " [validate Addon Request()] ";
        AddonEligibilityRequestDTO addonEligibilityRequestDTO = new AddonEligibilityRequestDTO(false, false);
        try {
            ClientServicePojo clientServicePojo = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CONVERT_VOL_BOOST_TOPUP).get(0);
            if (clientServicePojo != null && clientServicePojo.getValue().equalsIgnoreCase("1")) {
                List<CustomerPlansModel> currentPlanList = this.subscriberService.getActivePlanList(customers.getId(), false).stream().filter(data -> data.getService().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA) && data.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW) && !data.getVolTotalQuota().equalsIgnoreCase(SubscriberConstants.UNLIMITED_QUOTA) && (data.getQuotaType().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA) || data.getQuotaType().equalsIgnoreCase(CommonConstants.BOTH_QUOTA_TYPE))).collect(Collectors.toList());

                addonEligibilityRequestDTO.setIsValidActivePlan(currentPlanList != null && currentPlanList.size() > 0);
                addonEligibilityRequestDTO.setIsConvertToAddon(true);
            } else {
                addonEligibilityRequestDTO.setIsConvertToAddon(false);
            }
            return addonEligibilityRequestDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public void sendRenewRechargeMessage(String username, String countryCode, String mobileNumber, String emailId, Integer mvnoId, String plan, String purchaseType, Long staffId) {
        try {
            if (purchaseType.equals("Renew")) {
                Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_RENEW);
                if (optionalTemplate.isPresent()) {
                    if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                        // Set message in queue to send notification after opt generated successfully.
                        Long buId = null;
                        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                            buId = getBUIdsFromCurrentStaff().get(0);
                        }
                        CustomerRenewalMessage custRenewMessage = new CustomerRenewalMessage(username, countryCode, mobileNumber, emailId, mvnoId, RabbitMqConstants.CUSTOMER_RENEW, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, plan, purchaseType, buId, staffId);
                        Gson gson = new Gson();
                        gson.toJson(custRenewMessage);
                        kafkaMessageSender.send(new KafkaMessageData(custRenewMessage, CustomerRenewalMessage.class.getSimpleName()));
//                        messageSender.send(custRenewMessage, RabbitMqConstants.QUEUE_BSS_CUSTOMER_RENEWAL_SUCCESS);
                    }
                }
            } else {
                Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_RECHARGE);
                if (optionalTemplate.isPresent()) {
                    if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                        Long buId = null;
                        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                            buId = getBUIdsFromCurrentStaff().get(0);
                        }
                        CustomerRechargeMessage customerRechargeMessage = new CustomerRechargeMessage(username, countryCode, mobileNumber, emailId, mvnoId, RabbitMqConstants.CUSTOMER_RECHARGE, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, plan, purchaseType, buId, staffId);
                        Gson gson = new Gson();
                        gson.toJson(customerRechargeMessage);
                        kafkaMessageSender.send(new KafkaMessageData(customerRechargeMessage, CustomerRechargeMessage.class.getSimpleName()));
//                        messageSender.send(customerRechargeMessage, RabbitMqConstants.QUEUE_BSS_CUSTOMER_RECHARGE_SUCCESS);
                    }
                }

            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public LocalDateTime calculateExpiryDate(CustomersPojo subscriber, PostpaidPlan postpaidPlan, LocalDateTime startDate) {
        LocalDateTime expDate = null;
        if (postpaidPlan.getUnitsOfValidity() != null && !"".equals(postpaidPlan.getUnitsOfValidity())) {
            if (subscriber.getCalendarType() != null && subscriber.getCalendarType().equalsIgnoreCase(CommonConstants.CAL_TYPE_NEPALI)) {
                //String currentDateAndTime = dateConverterService.LocalDateTimeToString(LocalDateTime.now());
                String currentDateAndTime = startDate.getDayOfMonth() + "-" + startDate.getMonthValue() + "-" + startDate.getYear() + " " + startDate.getHour() + ":" + startDate.getMinute() + ":" + startDate.getSecond();
                NepaliDateDTO nepaliCurrentDateDTO = dateConverterService.getNepaliDateFromEnglishDate(currentDateAndTime);
                NepaliDateDTO nepaliEndDateDTO = null;
                long plusDays = postpaidPlan.getValidity().longValue();
                if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_DAYS)) {
                    LocalDateTime endDateForday = startDate.plusDays(plusDays);
                    nepaliEndDateDTO = dateConverterService.getNepaliDateFromEnglishDate(endDateForday.getDayOfMonth() + "-" + endDateForday.getMonthValue() + "-" + endDateForday.getYear() + " " + endDateForday.getHour() + ":" + endDateForday.getMinute() + ":" + endDateForday.getSecond());
//                      nepaliEndDateDTO = dateConverterService.calculateEndDateNepaliByDay(nepaliCurrentDateDTO, (int) plusDays);
                } else if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_MONTHS)) {
                    nepaliEndDateDTO = dateConverterService.calculateEndDateNepaliByMonth(nepaliCurrentDateDTO, (int) plusDays);
                } else if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_YEARS)) {
                    nepaliEndDateDTO = dateConverterService.calculateEndDateNepaliByYear(nepaliCurrentDateDTO, (int) plusDays);
                }

                EnglishDateDTO englishEndDateDTO = dateConverterService.getEnglishDateDTOFromNepaliDate(nepaliEndDateDTO.toString());

                expDate = LocalDateTime.of(englishEndDateDTO.getYear(), englishEndDateDTO.getMonth(), englishEndDateDTO.getDate(), englishEndDateDTO.getHour(), englishEndDateDTO.getMin(), englishEndDateDTO.getSec());
            }
            if (subscriber.getCalendarType() != null && subscriber.getCalendarType().equalsIgnoreCase(CommonConstants.CAL_TYPE_ENGLISH)) {
                if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_DAYS)) {
                    expDate = LocalDateTime.of(startDate.plusDays(postpaidPlan.getValidity().longValue()).toLocalDate(), LocalTime.from(startDate));
                    /*if(postpaidPlan.getValidity() == 1){
                        expDate = expDate.minusDays(1);
                    }*/
                } else if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_MONTHS)) {
                    expDate = startDate.plusDays(UtilsCommon.getDaysForExpiryDateByMonth(postpaidPlan.getValidity(), startDate.toLocalDate()));
                } else if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_YEARS)) {
                    expDate = startDate.plusDays(UtilsCommon.getDaysForExpiryDateByYear(postpaidPlan.getValidity(), startDate.toLocalDate()));
                } else if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
                    Long hours = postpaidPlan.getValidity().longValue();
                    expDate = startDate.plusHours(hours);
                }
            }
        }
        return expDate;
    }

    public List<ServicePlan> getServiceWisePlansForRenewalTime(Integer custId) throws Exception {
        List<ServicePlan> servicePlans = new ArrayList<>();
        Map<Integer, List<PlanList>> map = null;
        if (custId != null) {
            List<Integer> list = getEligiblePlanIds(custId);
            if (list != null && list.size() > 0) {
                map = new HashMap<>();
                for (int i = 0; i < list.size(); i++) {
                    PostpaidPlan plan = planService.get(list.get(i), getMvnoIdFromCurrentStaff(custId));
                    Integer serviceId = plan.getServiceId();
                    Integer planId = plan.getId();
                    String planName = plan.getName();
                    if (map.containsKey(serviceId)) {
                        List<PlanList> plans = map.get(serviceId);
                        PlanList p = new PlanList(planId, planName);
                        plans.add(p);
                        map.put(serviceId, plans);
                    } else {
                        List<PlanList> plans = new ArrayList<>();
                        PlanList p = new PlanList(planId, planName);
                        plans.add(p);
                        map.put(serviceId, plans);
                    }
                }
            }
            if (map != null) {
                for (Map.Entry<Integer, List<PlanList>> entry : map.entrySet()) {
                    ServicePlan plan = new ServicePlan();
                    plan.setServiceId(entry.getKey());
                    plan.setPlanList((ArrayList<PlanList>) entry.getValue());
                    servicePlans.add(plan);
                }
            } else throw new Exception("No Active or Future -Plan Exist!");
        } else throw new Exception("Customer Id Requried!");
        return servicePlans;
    }

    private List<Integer> getEligiblePlanIds(Integer custId) {
        List<Integer> list = null;
        if (custId != null) {
            Customers customers = customersRepository.findById(custId).get();
            if (customers == null) throw new RuntimeException("Customer Does not Exist for given Id " + custId);
            List<CustomerPlansModel> activePlansModels = this.getActivePlanList(custId, false).stream().filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW))).collect(Collectors.toList());
            List<CustomerPlansModel> futurePlansModels = this.getFuturePlanList(custId, false).stream().filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW))).collect(Collectors.toList());

            if (activePlansModels != null && activePlansModels.size() > 0) {
                Integer planGroup = activePlansModels.get(activePlansModels.size() - 1).getPlangroupid();
                list = new ArrayList<>();
                Boolean flag = true;
                for (int i = activePlansModels.size() - 1; i >= 0; i--) {
                    if (activePlansModels.get(i).getPlangroupid().toString().equalsIgnoreCase(planGroup.toString()) && !list.contains(activePlansModels.get(i).getPlanId()) && flag)
                        list.add(activePlansModels.get(i).getPlanId());
                    else flag = false;
                }
            }

            if (futurePlansModels != null && futurePlansModels.size() > 0) {
                Integer planGroup = futurePlansModels.get(futurePlansModels.size() - 1).getPlangroupid();
                list = new ArrayList<>();
                Boolean flag = true;
                for (int i = futurePlansModels.size() - 1; i >= 0; i--) {
                    if (futurePlansModels.get(i).getPlangroupid().toString().equalsIgnoreCase(planGroup.toString()) && !list.contains(futurePlansModels.get(i).getPlanId()) && flag)
                        list.add(futurePlansModels.get(i).getPlanId());
                    else flag = false;
                }
            }
        }
        return list;
    }

    public String getLastRenewalPlanGroupId(Integer custId) throws Exception {
        String planGroupId = null;
        if (custId != null) {
            List<CustomerPlansModel> activePlansModels = this.getActivePlanList(custId, false).stream().filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
            if (activePlansModels != null && activePlansModels.size() > 0)
                planGroupId = String.valueOf(activePlansModels.get(0).getPlangroupid());

            List<CustomerPlansModel> futurePlansModels = this.getFuturePlanList(custId, false).stream().filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
            if (futurePlansModels != null && futurePlansModels.size() > 0)
                planGroupId = String.valueOf(futurePlansModels.get(0).getPlangroupid());
        } else throw new Exception("Customer Id Required!");
        return planGroupId;
    }

    public boolean businessPromotionPaymentFLow(Integer invoiceId, boolean isApproved) {

        try {
            Optional<DebitDocument> debitDocument = debitDocRepository.findById(invoiceId);
            if (debitDocument.get().getId() != null) {
                if (isApproved) {
                    RecordPaymentPojo pojo = new RecordPaymentPojo();
                    if (debitDocument.isPresent()) {
                        Double totalAmount = debitDocument.get().getTotalamount();
                        LocalDateTime startDate = debitDocument.get().getStartdate();
                        LocalDateTime endDate = debitDocument.get().getEndate();
                        LocalDateTime todayDate = LocalDateTime.now();
                        Long invoiceDays = ChronoUnit.DAYS.between(startDate, endDate);
                        Long usedDays = ChronoUnit.DAYS.between(startDate, todayDate);

                        pojo.setPaytype("invoice");
                        List<Integer> invoiceIds = new ArrayList<>();
                        invoiceIds.add(debitDocument.get().getId());
                        pojo.setInvoiceId(invoiceIds);
                        pojo.setAmount(totalAmount);
                        pojo.setCustomerid(debitDocument.get().getCustomer().getId());
                        pojo.setPaymentdate(LocalDate.now());
                        pojo.setPaymode("Online");
                        pojo.setReferenceno(debitDocument.get().getId().toString());
                        pojo.setRemark("credit-note for Approval!");
                        pojo.setType("creditnote");
                        creditDocService.save(pojo, false, false, false, null);

                    }
                } else {
                    DebitDocument debitDoc = debitDocRepository.findById(invoiceId.intValue()).orElse(null);
                    CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(debitDoc.getCustpackrelid());
                    CustPlanMappping actualCustomerPlanMapping = custPlanMappingRepository.findById(custPlanMappping.getCustRefId());
                    debitDoc.setStatus(CommonConstants.CUSTOMER_STATUS_REJECTED);
                    Customers customers = actualCustomerPlanMapping.getCustomer();
                    customers.setStatus(SubscriberConstants.IN_ACTIVE);
                    customersRepository.save(customers);
                }

            } else {
                throw new CustomValidationException(404, "Invoice not found", null);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error in Payment Approval", e);
        }

        return isApproved;
    }

    public HashMap<String, Object> getStartEndAndExpirydate(ChangePlanRequestDTO requestDTO, String requestFrom) throws Exception {
        LocalDateTime startDate = null;
        LocalDateTime expiryDate = null;
        LocalDateTime endDate = null;
        LocalDateTime todaysDate = LocalDateTime.now();
        Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
        CustomersPojo customersPojo = customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext());
        PostpaidPlan postpaidPlan = planService.get(requestDTO.getPlanId(), customers.getMvnoId());

        CustomerPlansModel activeCustPlanModel = null;

        List<CustomerPlansModel> activeCustPlanModelList = this.subscriberService.getActivePlanList(customers.getId(), false);
//            List<CustomerPlansModel> activeCustPlanModelList = this.subscriberService.getCustomerPlanList(customers.getId());

        if (activeCustPlanModelList.size() > 0) {
            if (requestDTO.getBindWithOldPlanId() != null) {
                activeCustPlanModelList = this.subscriberService.getActivePlanList(customers.getId(), false).stream().filter(data -> data.getPlanId().toString().equalsIgnoreCase(requestDTO.getBindWithOldPlanId())).filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_ONLY))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                if (activeCustPlanModelList.size() > 0) activeCustPlanModel = activeCustPlanModelList.get(0);
            } else if (requestDTO.getNewPlanList() != null) {
                activeCustPlanModelList = this.getActivePlanList(customers.getId(), false).stream().filter(data -> data.getPlanId().toString().equalsIgnoreCase(requestDTO.getPlanId().toString())).filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                if (activeCustPlanModelList.size() > 0) activeCustPlanModel = activeCustPlanModelList.get(0);
            } else {
                activeCustPlanModelList = this.getActivePlanList(customers.getId(), false).stream().filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_ONLY))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
                if (activeCustPlanModelList.size() > 0) activeCustPlanModel = activeCustPlanModelList.get(0);
            }
        }

        if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON)) {
            //This is in use
            startDate = requestDTO.getAddonStartDate();
            expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
            endDate = expiryDate;
            LocalDate localDate = endDate.toLocalDate();
            Date date = java.sql.Date.valueOf(localDate);

            if (date.after(activeCustPlanModel.getEndDate())) {
                java.sql.Date localDateTime = activeCustPlanModel.getEndDate();
//                LocalDateTime localDateTime = localDate1.atStartOfDay();

                long epochMilli = localDateTime.getTime();
                LocalDateTime dateTime = Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDateTime();
                endDate = dateTime;
            }
            //Conver java.util.date to sql date
            //  LocalDateTime expiryLocalDateTime =  LocalDateTime.ofInstant(activeCustPlanModel.getEndDate().toInstant(), ZoneId.systemDefault());

            Boolean isFuture = null;
            Boolean isActive = null;

            if (startDate.toLocalDate().equals(LocalDate.now())) {
                isFuture = false;
                isActive = true;
            } else {
                if (startDate.toLocalDate().isAfter(LocalDate.now())) {
                    isFuture = true;
                    isActive = false;
                } else throw new RuntimeException("Please select today's date or future date");
            }

            if (isFuture) {
                List<CustomerPlansModel> getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).collect(Collectors.toList());
                if (getFuturePlanList.size() > 0) {
                    LocalDateTime finalStartDate = startDate;
                    getFuturePlanList.forEach(data -> {
                        if (data.getExpiryDate().toLocalDate().isAfter(finalStartDate.toLocalDate())) {
                            throw new RuntimeException("Plan overlapping for selected plan, Please select another plan");
                        }
                    });
                }
            }

            if (isActive) {
                List<CustomerPlansModel> getActivePlanList = this.getActivePlanList(customers.getId(), false).stream().filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).collect(Collectors.toList());
                if (getActivePlanList.size() > 0) {
                    LocalDateTime finalStartDate = startDate;
                    getActivePlanList.forEach(data -> {
                        if (data.getExpiryDate().toLocalDate().isAfter(finalStartDate.toLocalDate())) {
                            throw new RuntimeException("Plan overlapping for selected plan, Please select another plan");
                        }
                    });
                }
            }
        } else if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_UPGRADE)) {
            startDate = LocalDateTime.now();
            expiryDate = LocalDateTime.of(startDate.plusDays(postpaidPlan.getValidity().longValue()).toLocalDate(), LocalTime.of(23, 59, 59));
            List<CustomerPlansModel> getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).collect(Collectors.toList());
            if (getFuturePlanList.size() > 0) {
                LocalDateTime finalExpiryDate = expiryDate;
                getFuturePlanList.forEach(data -> {
                    if (data.getStartDate().toLocalDate().isBefore(finalExpiryDate.toLocalDate())) {
                        throw new RuntimeException("Plan overlapping for selected plan, Please select another plan");
                    }
                });
            }
        } else if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW) && requestDTO.getIsAdvRenewal()) {
            //This is in not use
            PostpaidPlan tempPostpaidPlan = this.planService.get(requestDTO.getPlanId(), customers.getMvnoId());
            List<LocalDate> expiryDateList = new ArrayList<>();
            List<CustomerPlansModel> getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> !data.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_ADDON)).collect(Collectors.toList());
            if (getFuturePlanList.size() > 0) {
                getFuturePlanList.forEach(data -> {
                    expiryDateList.add(data.getExpiryDate().toLocalDate());
                });
                if (expiryDateList.size() > 0) {
                    LocalDate maxDate = expiryDateList.stream().max(LocalDate::compareTo).get();
                    startDate = LocalDateTime.of(LocalDate.parse(maxDate.toString()).plusDays(1), LocalTime.of(00, 00, 00));
                    expiryDate = LocalDateTime.of(startDate.plusDays(tempPostpaidPlan.getValidity().longValue()).toLocalDate(), LocalTime.of(23, 59, 59));
                }
            } else {
                if (activeCustPlanModel == null) {
                    startDate = LocalDateTime.of(LocalDate.now(), LocalTime.now().plusSeconds(1));
                } else {
                    startDate = LocalDateTime.of(activeCustPlanModel.getEndDate().toLocalDate().plusDays(1L), LocalTime.of(00, 00, 00));
                }
                expiryDate = LocalDateTime.of(startDate.plusDays(tempPostpaidPlan.getValidity().longValue()).toLocalDate(), LocalTime.of(23, 59, 59));
            }
        } else if (!requestDTO.getIsAdvRenewal() && requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW)) {
            Integer parentCustomerId = null;
            if (Objects.nonNull(customers.getParentCustomers())) {
                if (customers.getInvoiceType().equalsIgnoreCase("Group")) {
                    parentCustomerId = customers.getParentCustomers().getId();
                }
            }
            if (activeCustPlanModel != null) {
                if (parentCustomerId != null) {
                    startDate = Instant.ofEpochMilli(activeCustPlanModel.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(1L);
                    expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                    endDate = expiryDate;
                    PostpaidPlan plan = this.planService.get(requestDTO.getPlanId(), customers.getMvnoId());
                    Customers parentCustomer = this.subscriberService.get(parentCustomerId, customers.getMvnoId());
                    LocalDateTime parentMaxExpiryDate = getParentMaxExpiryDateByService(plan.getServiceId(), parentCustomer);
                    if (parentMaxExpiryDate != null) {
                        if (parentMaxExpiryDate.isAfter(expiryDate)) {
                            expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                            endDate = expiryDate;
                        } else if (!parentMaxExpiryDate.isAfter(expiryDate)) {
                            expiryDate = parentMaxExpiryDate;
                            endDate = expiryDate;
                        }
                    }
                } else {
                    if (requestDTO.getAddonStartDate() != null) startDate = requestDTO.getAddonStartDate();
                    else
                        startDate = Instant.ofEpochMilli(activeCustPlanModel.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(1);
                    expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                    endDate = expiryDate;
                }
            }
            List<CustomerPlansModel> getFuturePlanList = new ArrayList<>();

            if (requestDTO.getBindWithOldPlanId() != null)
                getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> data.getPlanId().toString().equalsIgnoreCase(requestDTO.getBindWithOldPlanId())).filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
            else if (requestDTO.getNewPlanList() != null)
                getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> data.getPlanId().toString().equalsIgnoreCase(requestDTO.getPlanId().toString())).filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
            else
                getFuturePlanList = this.getFuturePlanList(customers.getId(), false).stream().filter(data -> data.getPlangroup().equalsIgnoreCase(postpaidPlan.getPlanGroup())).filter(data -> !data.getCustPlanStatus().equalsIgnoreCase("Stop")).filter(dto -> null != dto.getPlanstage() && (dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL) || dto.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW))).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());

            if (getFuturePlanList.size() > 0) {
                if (parentCustomerId != null) {
                    startDate = Instant.ofEpochMilli(getFuturePlanList.get(0).getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(1L);
                    expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                    endDate = expiryDate;
                    PostpaidPlan plan = this.planService.get(requestDTO.getPlanId(), customers.getMvnoId());
                    Customers parentCustomer = this.subscriberService.get(parentCustomerId, customers.getMvnoId());
                    LocalDateTime parentMaxExpiryDate = getParentMaxExpiryDateByService(plan.getServiceId(), parentCustomer);
                    if (parentMaxExpiryDate != null) {
                        if (parentMaxExpiryDate.isAfter(expiryDate)) {
                            expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                            endDate = expiryDate;
                        } else if (!parentMaxExpiryDate.isAfter(expiryDate)) {
                            expiryDate = parentMaxExpiryDate;
                            endDate = expiryDate;
                        }
                    }
                } else {
                    if (requestDTO.getAddonStartDate() != null) startDate = requestDTO.getAddonStartDate();
                    else if (getFuturePlanList.size() > 0) {
                        startDate = Instant.ofEpochMilli(getFuturePlanList.get(0).getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(1L);
                        expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                        endDate = expiryDate;
                    }
                }
            }

            if (activeCustPlanModel == null && (getFuturePlanList == null || (getFuturePlanList != null && getFuturePlanList.size() == 0))) {
                if (requestDTO.getAddonStartDate() != null) {
                    startDate = requestDTO.getAddonStartDate();
                } else {
                    startDate = todaysDate;
                }
                expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
                endDate = expiryDate;
            }
        } else {
            startDate = LocalDateTime.now();
            expiryDate = calculateExpiryDate(customersPojo, postpaidPlan, startDate);
            endDate = expiryDate;
        }
        Optional<PlanService> services = planServiceRepository.findById(postpaidPlan.getServiceId());
        if (services.isPresent()) {
            if (services.get().getExpiry() != null) {
                if (services.get().getExpiry().equalsIgnoreCase(com.adopt.apigw.constants.Constants.AT_MIDNIGHT)) {
                    startDate = startDate.toLocalDate().atTime(LocalTime.MIN);
                    expiryDate = LocalDateTime.of(expiryDate.toLocalDate(), LocalTime.of(23, 59, 59));
                    endDate = LocalDateTime.of(endDate.toLocalDate(), LocalTime.of(23, 59, 59));
                }
            }

        }
        Double refundableAmount = 0d;
        if (requestDTO.getCustServiceMappingId() != null) {
            Optional<CustomerServiceMapping> customerServiceMapping = customerServiceMappingRepository.findById(requestDTO.getCustServiceMappingId());
            if (customerServiceMapping.isPresent())
                refundableAmount = creditDocService.getRefundableAmountByService(Collections.singletonList(customerServiceMapping.get().getId().longValue()));
        }
        HashMap<String, Object> response = new HashMap<>();
        response.put("startDate", startDate);
        response.put("expiryDate", expiryDate);
        response.put("endDate", endDate);
        response.put("refundableAmount", refundableAmount);
        return response;
    }

    @Transactional
    public DeactivatePlanReqDTOList deActivatePlanInList(DeactivatePlanReqDTOList requestDTOs) throws NoSuchFieldException {
        DeactivatePlanReqDTOList deactivatePlanReqDTOList = new DeactivatePlanReqDTOList();
        List<DeactivatePlanReqDTO> result = new ArrayList<>();
        boolean changePlanDate = false;
        DateOverrideDto dateOverrideDto = new DateOverrideDto();
        try {
//            if (requestDTOs.getDateOverrideDtos() == null) {
//                PostpaidPlan plan = planService.findById(requestDTOs.getDeactivatePlanReqDTOS().get(0).getDeactivatePlanReqModels().get(0).getNewPlanId());
//                dateOverrideDto.setChangePlanStartDate(plan.getStartDate().atStartOfDay());
//                dateOverrideDto.setChangePlanEndDate(plan.getEndDate().atStartOfDay());
//                dateOverrideDto.setDateOverrideFlag(true);
//                requestDTOs.setDateOverrideDtos(dateOverrideDto);
//            }
            Random rnd = new Random();
            int renewalId = rnd.nextInt(999999);

//            String pendingRevenue = clientService.getValueByName("CHECK_PENDING_REVENUE_CHANGEPLAN");
//            if (pendingRevenue.equalsIgnoreCase("1")) {
////                5.  plan group to 2 plan  change plan
//                if (isRevenuePendings(requestDTOs)) {
//                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Change plan not allowed as Revenue is Pending !", null);
//                }
//            }

            if (requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate() != null && requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate().equalsIgnoreCase("Next Bill Date")) {
                changePlanDate = true;
            }
            for (DeactivatePlanReqDTO reqDTO : requestDTOs.getDeactivatePlanReqDTOS()) {
                if (requestDTOs.getSkipQuotaUpdate() == null) {
                    requestDTOs.setSkipQuotaUpdate(requestDTOs.getDeactivatePlanReqDTOS().get(0).isSkipQuotaUpdate());
                } else reqDTO.setSkipQuotaUpdate(requestDTOs.getSkipQuotaUpdate());
                result.add(deActivatePlan(reqDTO, renewalId, changePlanDate, requestDTOs.getDateOverrideDtos()));
            }
            deactivatePlanReqDTOList.setDeactivatePlanReqDTOS(result);
        } catch (CustomValidationException cbe) {
            cbe.printStackTrace();
            throw new CustomValidationException(cbe.getErrCode(), cbe.getMessage(), null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return deactivatePlanReqDTOList;
    }

    @Transactional
    public DeactivatePlanReqDTO deActivatePlan(DeactivatePlanReqDTO requestDTO, Integer renewalId, boolean changePlanDate, DateOverrideDto dateOverrideDtos) throws NoSuchFieldException {
        if (requestDTO.getCustId() == null) {
            throw new RuntimeException("custId is mandatory");
        }
        if (CollectionUtils.isEmpty(requestDTO.getDeactivatePlanReqModels())) {
            throw new RuntimeException("plan details is mandatory");
        }
//        if (isPayementPendingSent(requestDTO)) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Change plan not allowed as Payment is Pending for Approval!", null);
//        }
        List<Integer> custServiceMappingIdList = requestDTO.getDeactivatePlanReqModels().stream().map(DeactivatePlanReqModel::getCustServiceMappingId).collect(Collectors.toList());
        if (isCustomerValidToStopService(requestDTO.getCustId(), custServiceMappingIdList)) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Change plan not allowed as Future date plan is available with customer!", null);
        }

//        Optional<Customers> customers = customersRepository.findById(requestDTO.getCustId());
        Optional<Customers> customers = Optional.ofNullable(customersRepository.findById(requestDTO.getCustId()).get());
        if (!customers.isPresent()) {
            throw new RuntimeException("Customer not available!");
        }

        if (requestDTO.getPaymentOwner() == null) {
            requestDTO.setPaymentOwner("");
        }

        if (requestDTO.getPaymentOwnerId() == null) {
            requestDTO.setPaymentOwnerId(-1);
        }
        List<CustomerPlansModel> activeCustPlanModelList = this.subscriberService.getActivePlanList(customers.get().getId(), false);

        if (customers.isPresent()) {
            customers.get().setLastBillDate(LocalDate.now());
            Integer debitDocId = null;//new HashSet<>();
            HashSet<Integer> debitdocIds = new HashSet<>();
            Boolean flag = true;
            List<DeactivatePlanReqModel> reqModels = requestDTO.getDeactivatePlanReqModels();
            reqModels.removeIf(deactivatePlaReqModel -> deactivatePlaReqModel.getNewPlanId() == null);
            for (DeactivatePlanReqModel model : reqModels) {
                model.setSkipQuotaUpdate(requestDTO.isSkipQuotaUpdate());
                if (requestDTO.isPlanGroupFullyChanged()) {
                    Optional<CustomerPlansModel> plansModel = activeCustPlanModelList.stream().filter(customerPlansModel -> customerPlansModel.getCustomerServiceMappingId().equals(requestDTO.getDeactivatePlanReqModels().get(0).getCustServiceMappingId())).findFirst();
                    if (plansModel.isPresent()) {
                        if (requestDTO.getDeactivatePlanReqModels().get(0).getNewPlanGroupId().equals(plansModel.get().getPlangroupid())) {
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "The current Active planGroup is the same as the selected change planGroup, Please select a different planGroup to perform the Change plan.", null);
                        }
                    }
                    Optional<PlanGroup> newPlanGroup = planGroupRepository.findById(requestDTO.getDeactivatePlanReqModels().get(0).getNewPlanGroupId());
                    if (newPlanGroup.isPresent()) customers.get().setPlangroup(newPlanGroup.get());
                }
                List<CustomerPlansModel> list = new LinkedList<>();
                if (model.getCustServiceMappingId() != null) {
                    DeactivatePlanReqModel finalModel = model;
                    list = activeCustPlanModelList.stream().filter(customerPlansModel -> finalModel.getCustServiceMappingId().equals(customerPlansModel.getCustomerServiceMappingId()) && !customerPlansModel.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER) && !customerPlansModel.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_DTV_ADDON) && !customerPlansModel.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_BANDWIDTH_BOOSTER) && !customerPlansModel.isIsdeleteforVoid()).collect(Collectors.toList());
                } else {
                    list = activeCustPlanModelList.stream().filter(customerPlansModel -> !customerPlansModel.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER) && !customerPlansModel.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_DTV_ADDON) && !customerPlansModel.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_BANDWIDTH_BOOSTER) && !customerPlansModel.isIsdeleteforVoid()).collect(Collectors.toList());
                }
                List<CustPlanMappping> planMapppingList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(list)) {
                    for (CustomerPlansModel planMappping : list) {
                        CustPlanMappping custPlanMappping = custPlanMappingRepository.findByIdAndCustPlanStatusNotAndIsDeleteFalse(planMappping.getPlanmapid(), "STOP");
                        if (custPlanMappping != null) planMapppingList.add(custPlanMappping);
                    }
                }
                if (CollectionUtils.isEmpty(planMapppingList)) {
                    if (flag) {
                        for (DeactivatePlanReqModel deactivatePlanReqModel : reqModels) {
                            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(deactivatePlanReqModel.getNewPlanId());

                            CustomersPojo customersPojo = customerMapper.domainToDTO(customers.get(), new CycleAvoidingMappingContext());
                            customersPojo.setBillableCustomerId(requestDTO.getBillableCustomerId());

                            if (customers.get().getPlangroup() != null)
                                saveCustPlanMapping(customerMapper.domainToDTO(customers.get(), new CycleAvoidingMappingContext()), postpaidPlan.get(), customers.get().getPlangroup().getPlanGroupId(), model, customers.get(), deactivatePlanReqModel.getCustServiceMappingId(), renewalId, changePlanDate, CommonConstants.EVENTCONSTANTS.CHANGE_PLAN, dateOverrideDtos);
                            else
                                saveCustPlanMapping(customerMapper.domainToDTO(customers.get(), new CycleAvoidingMappingContext()), postpaidPlan.get(), null, model, customers.get(), deactivatePlanReqModel.getCustServiceMappingId(), renewalId, changePlanDate, CommonConstants.EVENTCONSTANTS.CHANGE_PLAN, dateOverrideDtos);
                        }
                        flag = false;
                    }
//                  if (!requestDTO.isPlanGroupChange())
//                      debitDocService.createInvoice(customers.get(), null, 200, null, "", requestDTO.getPaymentOwner(), requestDTO.getPaymentOwnerId(),false);

                } else {
                    for (CustPlanMappping planMappping : planMapppingList) {

//                        planMappping.setBillableCustomerId(planMappping.getCustServiceMappingId());
                        if (planMappping != null) {
                            if (planMappping != null && planMappping.getStopServiceDate() != null && planMappping.getCustPlanStatus().equalsIgnoreCase("STOP"))
                                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Change plan not allowed as service is STOP!", null);
                            if (requestDTO.isPlanGroupChange()) {
                                if (planMappping != null) {
                                    if (planMappping.getPlanGroup() == null) {
                                        planMappping.setPlanGroup(customers.get().getPlangroup());
                                    }
                                    if (planMappping.getDebitdocid() != null) {
                                        debitDocId = planMappping.getDebitdocid().intValue();//.add();
                                        debitdocIds.add(debitDocId);
                                    }
                                }
                                planMappping.setBillableCustomerId(requestDTO.getBillableCustomerId());
                                model = deactivateCustPlanMapping(planMappping, model.getNewPlanId(), customers.get().getPlangroup().getPlanGroupId(), model, customers.get(), requestDTO.getPaymentOwner(), requestDTO.getPaymentOwnerId(), renewalId, changePlanDate, dateOverrideDtos);
                            } else {
                                boolean dateoverrideFlag = false;
                                if (dateOverrideDtos != null) {
                                    dateoverrideFlag = dateOverrideDtos.isDateOverrideFlag();
                                }
                             /*   if (planMappping != null) {
                                    if(!dateoverrideFlag) {
                                        if (model.getNewPlanId().equals(planMappping.getPlanId())) {
                                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "The current Active plan is the same as the selected change plan.Please select a different plan to perform the Change plan.", null);
                                        }
                                    }
                                }*/
                                planMappping.setBillableCustomerId(requestDTO.getBillableCustomerId());
                                model = deactivateCustPlanMapping(planMappping, model.getNewPlanId(), null, model, customers.get(), requestDTO.getPaymentOwner(), requestDTO.getPaymentOwnerId(), renewalId, changePlanDate, dateOverrideDtos);
                            }
                        }
                    }
                }

            }
//            if (requestDTO.isPlanGroupChange() && debitDocId != null) {
//                debitDocService.createInvoice(customers.get(), null, 200, debitdocIds, "", requestDTO.getPaymentOwner(), requestDTO.getPaymentOwnerId(),false);
//            } else if (requestDTO.isPlanGroupChange() && activeCustPlanModelList.size() == 0) {
//                debitDocService.createInvoice(customers.get(), null, 200, debitdocIds, "", requestDTO.getPaymentOwner(), requestDTO.getPaymentOwnerId(),false);
//            }
            CustomersPojo subscriber = new CustomersPojo();
            subscriber = customerMapper.domainToDTO(customers.get(), new CycleAvoidingMappingContext());
        }
        return requestDTO;
    }

    private boolean isPayementPendingSent(DeactivatePlanReqDTO requestDTO) {
        try {
            QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
            BooleanExpression exp = qCustPlanMappping.isNotNull().and(qCustPlanMappping.customer.id.eq(requestDTO.getCustId())).and(qCustPlanMappping.custServiceMappingId.eq(requestDTO.getDeactivatePlanReqModels().get(0).getCustServiceMappingId()));
            List<CustPlanMappping> custPlanMappping = (List<CustPlanMappping>) custPlanMappingRepo.findAll(exp);
            List<Integer> debitDocId = custPlanMappping.stream().filter(i -> i.getDebitdocid() != null).map(i -> i.getDebitdocid().intValue()).collect(Collectors.toList());
            List<Integer> creditDocids = new ArrayList<>();
            if (debitDocId.size() > 0) {
                QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
                BooleanExpression exp1 = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.debtDocId.in(debitDocId));
                List<CreditDebitDocMapping> creditDebitDocMappings = (List<CreditDebitDocMapping>) creditDebtMappingRepository.findAll(exp1);
                creditDocids = creditDebitDocMappings.stream().filter(i -> i.getCreditDocId() != null).map(i -> i.getCreditDocId()).collect(Collectors.toList());
                QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
                BooleanExpression exp2 = qCreditDocument.isNotNull().and(qCreditDocument.id.in(creditDocids)).and(qCreditDocument.status.equalsIgnoreCase("pending"));
                return creditDocRepository.exists(exp2);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private boolean isRevenuePending(DeactivatePlanReqDTO requestDTO, Customers customers) {
        try {
            Boolean isRevenuePending = false;
            Double pendingAmount = 0.0d;
            Double newPlanGroupPrice = 0.00;
            List<DeactivatePlanReqModel> deactivatePlanReqModels = requestDTO.getDeactivatePlanReqModels();
            List<Integer> custServiceMappingIds = requestDTO.getDeactivatePlanReqModels().stream().map(DeactivatePlanReqModel::getCustServiceMappingId).collect(Collectors.toList());
            List<Long> convertedIds = custServiceMappingIds.stream().map(Integer::longValue).collect(Collectors.toList());

            for (DeactivatePlanReqModel model : deactivatePlanReqModels) {
                if (model.getPlanId() != null) {
                    Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(model.getPlanId());
                    if (postpaidPlan.isPresent()) {
//                        1.plan to plan change normal
                        if (postpaidPlan.get().getCategory().equalsIgnoreCase("Normal")) {
                            Optional<PostpaidPlan> newPlan = postpaidPlanRepo.findById(model.getNewPlanId());
                            Double newPlanOfferPrice = newPlan.get().getOfferprice();

                            pendingAmount = pendingAmount(model);

                            if (newPlanOfferPrice < pendingAmount) {
                                isRevenuePending = true;
                            }
                        } else {
//                           2. plan to plan Business Promotion change plan
                            Optional<PostpaidPlan> newPlan = postpaidPlanRepo.findById(model.getNewPlanId());

                            Double newPlanOfferPrice = newPlan.get().getNewOfferPrice();

                            pendingAmount = pendingAmount(model);

                            if (newPlanOfferPrice < pendingAmount) {
                                isRevenuePending = true;
                            }
                        }
                    }
                } else {

                    if (model.getNewPlanGroupId() != null) {
                        PlanGroup planGroup = planGroupRepository.findById(model.getNewPlanGroupId()).get();
                        newPlanGroupPrice = planGroup.getOfferprice();


                        if (customers.getPlangroup() != null) {
                            QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
                            BooleanExpression exp = qCustPlanMappping.isNotNull().and(qCustPlanMappping.custServiceMappingId.eq(model.getCustServiceMappingId()));
                            exp = exp.and(qCustPlanMappping.planGroup.planGroupId.eq(customers.getPlangroup().getPlanGroupId())).and(qCustPlanMappping.custPlanStatus.equalsIgnoreCase("Active"));
                            List<CustPlanMappping> custPlanMappping = (List<CustPlanMappping>) custPlanMapppingRepository.findAll(exp);
                            DebitDocument debitDocument = null;
                            if (!CollectionUtils.isEmpty(custPlanMappping) && custPlanMappping.get(0).getDebitdocid() != null)
                                debitDocument = debitDocRepository.findById(custPlanMappping.get(0).getDebitdocid().intValue()).get();

                            List<Long> custServiceMappingId = new ArrayList<>();
                            custServiceMappingId.add(model.getCustServiceMappingId().longValue());
                            pendingAmount = creditDocService.getRefundableAmountByService(custServiceMappingId);
                            if (pendingAmount <= 0)
                                pendingAmount = debitDocService.getPendingRevenueWithTaxAtCurrentDate(debitDocument);

                            if (newPlanGroupPrice < pendingAmount) {
                                isRevenuePending = true;
                            }
                        } else if (convertedIds.size() > 0) {
                            List<Long> custServiceMappingId = new ArrayList<>();
                            custServiceMappingId.add(model.getCustServiceMappingId().longValue());
                            pendingAmount = creditDocService.getRefundableAmountByService(convertedIds);
//                        if (pendingAmount <= 0)
//                            pendingAmount = debitDocService.getPendingRevenueWithTaxAtCurrentDate(pendingAmount);
//
                            if (newPlanGroupPrice < pendingAmount) {
                                isRevenuePending = true;
                            }
                        }
                    }

                }
            }
            return isRevenuePending;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Double pendingAmount(DeactivatePlanReqModel model) {
        try {
            Double pendingAmount = 0.0d;
            QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
            BooleanExpression exp = qCustPlanMappping.isNotNull().and(qCustPlanMappping.custServiceMappingId.eq(model.getCustServiceMappingId()));
            exp.and(qCustPlanMappping.planId.eq(model.getPlanId()));
            List<CustPlanMappping> custPlanMappping = (List<CustPlanMappping>) custPlanMapppingRepository.findAll(exp);
            DebitDocument debitDocument = debitDocRepository.findById(custPlanMappping.get(0).getDebitdocid().intValue()).get();

            List<Long> custServiceMappingId = new ArrayList<>();
            custServiceMappingId.add(model.getCustServiceMappingId().longValue());
            pendingAmount = creditDocService.getRefundableAmountByService(custServiceMappingId);
            if (pendingAmount <= 0)
                pendingAmount = debitDocService.getPendingRevenueWithTaxAtCurrentDate(debitDocument);

            return pendingAmount;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    private boolean isRevenuePendings(DeactivatePlanReqDTOList requestDTOs) {
        try {
            List<Long> custServiceMappingId = new ArrayList<>();
            Double pendingRevenue = 0.00;
            Double newPrice = 0.00;
            Boolean isPendingRevenue = false;
            if (requestDTOs.getDeactivatePlanReqDTOS().size() > 1) {
                for (DeactivatePlanReqDTO reqDTO : requestDTOs.getDeactivatePlanReqDTOS()) {
                    Integer planGroupId = reqDTO.getDeactivatePlanReqModels().get(0).getPlanGroupId();
                    if (planGroupId != null) {
                        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
                        BooleanExpression exp = qCustPlanMappping.isNotNull().and(qCustPlanMappping.custServiceMappingId.eq(reqDTO.getDeactivatePlanReqModels().get(0).getCustServiceMappingId()));
                        exp.and(qCustPlanMappping.planId.eq(planGroupId));
                        List<CustPlanMappping> custPlanMappping = (List<CustPlanMappping>) custPlanMapppingRepository.findAll(exp);
                        DebitDocument debitDocument = debitDocRepository.findById(custPlanMappping.get(0).getDebitdocid().intValue()).get();


//                custServiceMappingId.add(reqDTO.getDeactivatePlanReqModels().get(0).getCustServiceMappingId().longValue());
//                pendingRevenue = creditDocService.getRefundableAmountByService(custServiceMappingId);
//                if(pendingRevenue <= 0)
                        pendingRevenue = debitDocService.getPendingRevenueWithTaxAtCurrentDate(debitDocument);


                        PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(reqDTO.getDeactivatePlanReqModels().get(0).getNewPlanId()).get();
                        newPrice = newPrice + postpaidPlan.getOfferprice();
                    }
                }
            }
            if (pendingRevenue > newPrice) {
                isPendingRevenue = true;
            }
            return isPendingRevenue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public void orgCustInvoiceForChangePlan(CustomersPojo subscriber, DeactivatePlanReqDTO requestDTO) {
        Integer planValidityDays = 0;
        LocalDateTime startDate = null;
        try {
            String value = null;
            Integer custId = null;

            if (subscriber.getId() != null) {
                custId = subscriber.getId();
            }
            if (subscriber.getCusttype().equalsIgnoreCase("Prepaid")) {
                value = clientServiceRepository.findValueByNameandMvnoId("ORGANIZATION", getLoggedInMvnoId(custId));
            } else {
                value = clientServiceRepository.findValueByNameandMvnoId("ORGANIZATIONPOST", getLoggedInMvnoId(custId));
            }
            List<Customers> customers = customersRepository.findByUsername(value);
            List<CustPlanMappping> planMapppingList = custPlanMappingRepo.findAllByCustomerIdAndCustPlanStatus(subscriber.getId(), "Active");
            List<CustPlanMapppingPojo> planMapppingPojos = new ArrayList<>();
            for (CustPlanMappping custPlanMappping : planMapppingList) {
                PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).get();
                startDate = LocalDateTime.now();
                planValidityDays = customersService.calculatePlanValidityDays(subscriber, postpaidPlan, postpaidPlan.getValidity().longValue(), startDate);
                CustPlanMapppingPojo custPlanMapppingPojo = custPlanMappingService.convertDomainToDto(custPlanMappping);
                custPlanMapppingPojo.setBillTo(com.adopt.apigw.constants.Constants.ORGANIZATION);
                custPlanMapppingPojo.setPlanValidityDays(planValidityDays);
                custPlanMapppingPojo.setIsInvoiceToOrg(requestDTO.getDeactivatePlanReqModels().get(0).isBillToOrg());
                planMapppingPojos.add(custPlanMapppingPojo);
            }
            subscriber.setPlanMappingList(planMapppingPojos);
            customersService.saveDataForOrganizationCustomer(subscriber, 0.00, 0L, requestDTO.getDeactivatePlanReqModels().get(0).getPlanGroupId(), subscriber.getCusttype());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public DeactivatePlanReqModel deactivateCustPlanMapping(CustPlanMappping custPlanMappping, Integer newPlanId, Integer planGroupId, DeactivatePlanReqModel model, Customers customers, String paymentOwner, Integer paymentOwnerId, Integer renewalId, boolean changePlanDate, DateOverrideDto dateOverrideDtos) {
        try {
            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(newPlanId);//planService.get(newPlanId);
            if (!postpaidPlan.isPresent()) {
                throw new RuntimeException("New Plan not available!");
            }
            Optional<DebitDocument> debitDocument = Optional.empty();
            Integer custServiceMappingId = model.getCustServiceMappingId();
            Optional<CustomerServiceMapping> customerServiceMapping = customerServiceMappingRepository.findById(custServiceMappingId);
            Boolean isTrialPlan = null;
            LocalDateTime expDate = null;
            LocalDateTime endDate = null;

            //if condition is used to store old plan expiry date ,so that it can be used to set trial plans expiry date
            if (custPlanMappping.getIstrialplan() != null) {
                if (custPlanMappping.getIstrialplan()) {
                    isTrialPlan = custPlanMappping.getIstrialplan();
                    expDate = custPlanMappping.getExpiryDate();
                    endDate = custPlanMappping.getEndDate();
                }
            }

            if (custPlanMappping != null) {
                QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
                BooleanExpression expression = qDebitDocument.isNotNull();
                if (custPlanMappping.getDebitdocid() != null) {
                    expression = expression.and(qDebitDocument.id.eq(custPlanMappping.getDebitdocid().intValue()));
                    expression = expression.and(qDebitDocument.billrunstatus.notIn(com.adopt.apigw.constants.Constants.VOID, com.adopt.apigw.constants.Constants.CANCELLED));
                    debitDocument = debitDocRepository.findOne(expression);
                }
                //change CPR

                if (custPlanMappping.getStartDate().isAfter(custPlanMappping.getEndDate())) {
                    custPlanMappping.setStartDate(LocalDateTime.now());
                    custPlanMappping.setEndDate(custPlanMappping.getStartDate().plusSeconds(1));
                    custPlanMappping.setExpiryDate(custPlanMappping.getStartDate().plusSeconds(1));
                }

                //this for postpaid next bill date
                final LocalDate endDateForDbr = custPlanMappping.getEndDate().toLocalDate();
                if (changePlanDate) {
                    LocalDateTime enddate = customers.getNextBillDate().atStartOfDay().minusSeconds(1);
                    custPlanMappping.setEndDate(enddate);
                    custPlanMappping.setExpiryDate(enddate);
                } else {
                    custPlanMappping.setEndDate(LocalDateTime.now());
                    custPlanMappping.setExpiryDate(LocalDateTime.now());
                    custPlanMappping.setIsVoid(Boolean.TRUE);
                    custPlanMappping.setCustPlanStatus("STOP");
                }

                if (customers.getIstrialplan()) {
                    custPlanMappping.setIstrialplan(false);
                }
//                QCustomerServiceMapping qCustomerServiceMapping = QCustomerServiceMapping.customerServiceMapping;
                custPlanMappping.setCustServiceMappingId(custServiceMappingId);
                custPlanMappingRepository.save(custPlanMappping);
                if (changePlanDate)
                    custPlanMappingService.updateCustPlanEndDateInRadius(custPlanMappping, CommonConstants.EVENTCONSTANTS.RENEW_PLAN);
                else
                    custPlanMappingService.updateCustPlanEndDateInRadius(custPlanMappping, CommonConstants.EVENTCONSTANTS.CHANGE_PLAN);

            }

            /*Integer cprIdForPromiseToPay=null;
            if(custPlanMappping.getGraceDays()!=null && custPlanMappping.getGraceDays()>0)
                cprIdForPromiseToPay=custPlanMappping.getId();*/

            //CustPlanMappping response = saveCustPlanMapping(customerMapper.domainToDTO(custPlanMappping.getCustomer(), new CycleAvoidingMappingContext()), postpaidPlan.get(), planGroupId, model.getDiscount(), custPlanMappping.getCustomer());
            CustomersPojo customersPojo = customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext());
            customersPojo.setBillableCustomerId(custPlanMappping.getBillableCustomerId());
            CustPlanMappping response = saveCustPlanMapping(customersPojo, postpaidPlan.get(), planGroupId, model, customers, custServiceMappingId, renewalId, changePlanDate, CommonConstants.EVENTCONSTANTS.CHANGE_PLAN, dateOverrideDtos);
            //response.setCprIdForPromiseToPay(cprIdForPromiseToPay);
            response.setBillableCustomerId(custPlanMappping.getBillableCustomerId());
            custPlanMappingRepository.save(response);
            if (isTrialPlan != null) {
                if (isTrialPlan) {
                    response.setIstrialplan(true);
                    response.setExpiryDate(expDate);
                    response.setEndDate(endDate);
                    custPlanMappingRepo.save(response);
                }
            }
            model.setCprId(response.getId());
            if (debitDocument.isPresent()) {
                List<Integer> debidDocIds = new ArrayList<>();
                debidDocIds.add(debitDocument.get().getId());

                if (!CollectionUtils.isEmpty(model.getDebitDocIds())) {
                    debidDocIds.addAll(model.getDebitDocIds());
                }
                model.setDebitDocIds(debidDocIds);
                debitDocument.get().setBillrunstatus("Cancelled");
                debitDocRepository.save(debitDocument.get());
            }
            if (custPlanMappping != null) ezBillServiceUtility.changePlan(custPlanMappping, response);
            if (customerServiceMapping.isPresent()) {
                customerServiceMapping.get().setCustId(custPlanMappping.getCustomer().getId());
                customerServiceMappingRepository.save(customerServiceMapping.get());
            }
            return model;
        } catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }

    }

    @Transactional
    private CustPlanMappping saveCustPlanMapping(CustomersPojo subscriber, PostpaidPlan postpaidPlanObj, Integer plangroupId, DeactivatePlanReqModel model, Customers customers, Integer custServiceMappingId, Integer renewalId, boolean changePlanDate, String event, DateOverrideDto dateOverrideDtos) {
        try {
            CustPlanMappping custPlanMappping = null;

            CustPlanMapppingPojo planMapppingPojo = new CustPlanMapppingPojo();
            Integer validity = customersService.calculatePlanValidityDays(subscriber, postpaidPlanObj, null, LocalDateTime.now());
            LocalDateTime startDate = null;
            LocalDateTime endDate = null;
            LocalDateTime expDate = null;

            PostpaidPlanPojo plan = postpaidPlanMapper.domainToDTO(postpaidPlanObj, new CycleAvoidingMappingContext());
            planMapppingPojo.setCustomer(subscriber);
            planMapppingPojo.setCustid(subscriber.getId());
            planMapppingPojo.setDiscount(model.getDiscount());

            if (subscriber.getAddressList().isEmpty()) {
                List<CustomerAddressPojo> customerAddresses = new ArrayList<>();
                CustomerAddressPojo customerAddress1 = new CustomerAddressPojo();
                customerAddress1.setAddressType("Present");
                customerAddress1.setStateId(1);
                customerAddress1.setCustomer(subscriber);
                customerAddresses.add(customerAddress1);
                subscriber.setAddressList(customerAddresses);


            }

            planMapppingPojo.setBillableCustomerId(subscriber.getBillableCustomerId());

            subscriber.setLastBillDate(LocalDate.now());
            subscriber.setNextBillDate(LocalDate.now().plusDays(validity));
            LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers, subscriber.getNextBillDate());
            if (nextQuotaReset != null) {
                subscriber.setNextQuotaResetDate(nextQuotaReset);
            } else {
                subscriber.setNextQuotaResetDate(LocalDate.now());
            }

            // Set Expiry Date of Plan
            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                planMapppingPojo.setPurchaseFrom(SubscriberConstants.PURCHASE_FROM_ADMIN);
            } else {
                planMapppingPojo.setPurchaseFrom(SubscriberConstants.PURCHASE_FROM_PARTNER);
            }
            planMapppingPojo.setPurchaseType(SubscriberConstants.PURCHASE_TYPE_NEW);
            String planName = postpaidPlanObj.getName();
            TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO(postpaidPlanObj.getId(), subscriber.getAddressList().stream().filter(data -> data.getAddressType().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PRESENT)).findAny().orElse(null).getStateId(), null, null);
            Double taxAmount = taxService.taxCalculationByPlan(taxDetailCountReqDTO, postpaidPlanObj.getChargeList());


            if (dateOverrideDtos != null) {
                boolean dateOverrideFlag = dateOverrideDtos.isDateOverrideFlag();
                if (dateOverrideFlag) {
                    startDate = dateOverrideDtos.getChangePlanStartDate();
                    expDate = dateOverrideDtos.getChangePlanEndDate();
                    validity = Math.toIntExact(ChronoUnit.DAYS.between(startDate, expDate));

                }
            } else {
                startDate = LocalDateTime.now();
                expDate = customersService.calculateExpiryDate(subscriber, null, postpaidPlanObj, Long.valueOf(validity));
            }

            planMapppingPojo.setPlanValidityDays(validity);
//            Optional<CustomerServiceMapping> customerServiceMapping = customerServiceMappingRepository.findById(custServiceMappingId);
            Optional<CustomerServiceMapping> customerServiceMapping = Optional.ofNullable(getCustomerServiceMapping(custServiceMappingId));
            if (null != postpaidPlanObj && planMapppingPojo.getExpiryDate() == null) {
                planMapppingPojo.setStartDate(startDate);
                if (changePlanDate) {
                    planMapppingPojo.setStartDate(customers.getNextBillDate().atStartOfDay());
                }
//                expDate = customersService.calculateExpiryDate(subscriber, null, postpaidPlanObj, Long.valueOf(validity));

                Integer parentCustomerId = null;
                if (Objects.nonNull(customers.getParentCustomers())) {
                    if (customerServiceMapping.isPresent()) {
                        if (customerServiceMapping.get().getInvoiceType().equalsIgnoreCase("Group"))
                            parentCustomerId = customers.getParentCustomers().getId();
                    }
                }
                List<CustomerPlansModel> parentActiveCustPlanModelList = null;
                List<CustomerPlansModel> activeCustPlanModelList = this.subscriberService.getActivePlanList(customers.getId(), false);
                if (parentCustomerId != null) {
                    parentActiveCustPlanModelList = this.subscriberService.getActivePlanList(parentCustomerId, false);
                    //parent customer expiray date
                    Customers parentCustomer = this.subscriberService.get(parentCustomerId, customers.getMvnoId());
                    LocalDateTime parentMaxExpiryDate = getParentMaxExpiryDateByService(plan.getServiceId(), parentCustomer);
                    startDate = planMapppingPojo.getStartDate();
                    endDate = planMapppingPojo.getEndDate();
                    if (changePlanDate) {
                        startDate = customers.getNextBillDate().atStartOfDay().minusSeconds(1L);
                    }
                    /* if parent and child have same service */
                    if (parentActiveCustPlanModelList.size() > 0) {
                        if (parentActiveCustPlanModelList.get(0).getServiceId().equals(postpaidPlanObj.getServiceId())) {
                            if (parentMaxExpiryDate != null) {
                                /*  checking start date of childs plan with parents end date of a plan  */
                                if (startDate.isBefore(parentMaxExpiryDate)) {
                                    if (parentMaxExpiryDate.isAfter(expDate)) {
                                        expDate = calculateExpiryDate(subscriber, postpaidPlanObj, startDate);
                                    } else if (!parentMaxExpiryDate.isAfter(expDate)) {
                                        expDate = parentMaxExpiryDate;
                                    }
                                }
                            }
                        }
                    }
                }
                if (!postpaidPlanObj.getUnitsOfValidity().equalsIgnoreCase("Hours")) {
                    expDate = LocalDateTime.of(expDate.toLocalDate(), LocalTime.now());
                }
                planMapppingPojo.setEndDate(expDate);
                planMapppingPojo.setExpiryDate(expDate);
                subscriber.setLastBillDate(LocalDate.now());
                subscriber.setNextBillDate(expDate.toLocalDate());
                nextQuotaReset = customersService.getNextQuotaResetDate(customers, subscriber.getNextBillDate());
                if (nextQuotaReset != null) {
                    subscriber.setNextQuotaResetDate(nextQuotaReset);
                } else {
                    subscriber.setNextQuotaResetDate(LocalDate.now());
                }

                Long prorate_validity = ChronoUnit.DAYS.between(planMapppingPojo.getStartDate(), planMapppingPojo.getEndDate());
                planMapppingPojo.setValidity(prorate_validity.doubleValue());
                if (subscriber.getInvoiceType() != null)
                    planMapppingPojo.setOfferPrice(subscriber.getInvoiceType().equals(CommonConstants.INVOICE_TYPE_GROUP) ? (postpaidPlanObj.getOfferprice() / planMapppingPojo.getPlanValidityDays()) * prorate_validity : postpaidPlanObj.getOfferprice());
                else planMapppingPojo.setOfferPrice(postpaidPlanObj.getOfferprice());

                planMapppingPojo.setTaxAmount(taxAmount);
                if (null != postpaidPlanObj.getQospolicy()) {
                    planMapppingPojo.setQospolicyId(postpaidPlanObj.getQospolicy().getId());
                }
            }
//            LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers, customers.getNextBillDate());
//            if(nextQuotaReset != null) {
//                customers.setNextQuotaResetDate(nextQuotaReset);
//            } else {
//                customers.setNextQuotaResetDate(LocalDate.now());
//            }
//            save(customers);
            if (model.isBillToOrg() && model.getNewAmount() != null) {
                planMapppingPojo.setNewAmount(model.getNewAmount());
                planMapppingPojo.setIsInvoiceToOrg(model.isBillToOrg());
                planMapppingPojo.setBillTo(com.adopt.apigw.constants.Constants.ORGANIZATION);
            }

//            Optional<PlanService> services = planServiceRepository.findById(plan.getServiceId());
            Optional<PlanService> services = Optional.ofNullable(planServiceService.get(plan.getServiceId(), customers.getMvnoId()));

            if (services.isPresent()) {
                if (services.get().getExpiry() != null) {
                    if (services.get().getExpiry().equalsIgnoreCase(com.adopt.apigw.constants.Constants.AT_MIDNIGHT)) {
                        planMapppingPojo.setExpiryDate(planMapppingPojo.getExpiryDate().toLocalDate().atTime(LocalTime.MAX).minusSeconds(1));
                        planMapppingPojo.setEndDate(planMapppingPojo.getEndDate().toLocalDate().atTime(LocalTime.MAX).minusSeconds(1));
                    }
                }
                planMapppingPojo.setService(services.get().getName());
            }
            QOSPolicyDTO qosPolicyDTO = null;
            if (null != plan && null != plan.getQuotatype()) {

                if (!plan.getQuotatype().equalsIgnoreCase(CommonConstants.DID_QUOTA_TYPE) && !plan.getQuotatype().equalsIgnoreCase(CommonConstants.INTERCOM_QUOTA_TYPE) && !plan.getQuotatype().equalsIgnoreCase(CommonConstants.VOICE__BOTH_QUOTA_TYPE)) {
                    Double totalQuotaForSeconds = 0.0;
                    Double totalQuotaForKB = 0.0;
                    if (null != plan.getQuotaunittime() && plan.getQuotaunittime().equalsIgnoreCase(com.adopt.apigw.constants.Constants.MINUTE)) {
                        totalQuotaForSeconds = plan.getQuotatime() * 60;
                    }
                    if (null != plan.getQuotaunittime() && plan.getQuotaunittime().equalsIgnoreCase(com.adopt.apigw.constants.Constants.HOUR)) {
                        totalQuotaForSeconds = plan.getQuotatime() * 60 * 60;
                    }
                    if (null != plan.getQuotaUnit() && plan.getQuotaUnit().equalsIgnoreCase(com.adopt.apigw.constants.Constants.MB)) {
                        totalQuotaForKB = (double) plan.getQuota() * 1024;
                    }
                    if (null != plan.getQuotaUnit() && plan.getQuotaUnit().equalsIgnoreCase(com.adopt.apigw.constants.Constants.GB)) {
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
                    if (plan.getQospolicyid() != null) {
//                        QOSPolicyDTO qosPolicy = qosPolicyService.getEntityForUpdateAndDelete(plan.getQospolicyid());
                        if (Objects.nonNull(qosPolicyDTO) && qosPolicyDTO.getDownstreamprofileuid() != null) {
                            quotaDetails.setDownstreamprofileuid(qosPolicyDTO.getDownstreamprofileuid());
                            quotaDetails.setUpstreamprofileuid(qosPolicyDTO.getUpstreamprofileuid());
                        }
                    }
                    quotaDetails.setPlanId(postpaidPlanObj.getId());
                    quotaDetails.setLastQuotaReset(LocalDateTime.now());
                    if (plan.isUseQuota()) {
                        quotaDetails.setChunkAvailable(plan.isUseQuota());
                    } else {
                        quotaDetails.setChunkAvailable(false);
                    }
                    if (plan.getChunk() != null) {
                        quotaDetails.setReservedQuotaInPer(plan.getChunk());
                    } else {
                        quotaDetails.setReservedQuotaInPer(0.0);
                    }
                    if (postpaidPlanObj.getUsageQuotaType() != null) {
                        quotaDetails.setUsageQuotaType(postpaidPlanObj.getUsageQuotaType());
                    } else {
                        quotaDetails.setUsageQuotaType("TOTAL");
                    }
                    quotaDetails.setSkipQuotaUpdate(model.isSkipQuotaUpdate());
                    quotaDetails.setUsedQuota(model.getUpdatedUsedQuota());
                    planMapppingPojo.setQuotaList(Collections.singletonList(quotaDetails));
//                    planMapppingPojo.setPlanValidityDays(customersService.calculatePlanValidityDays(subscriber, postpaidPlanObj, Long.valueOf(validity), LocalDateTime.now()));
                    planMapppingPojo.setPlangroupid(plangroupId);
                    //planMapppingPojo.setService(postpaidPlanObj.getServiceName());
                    planMapppingPojo.setPlanId(plan.getId());
                    if (subscriber.getIsinvoicestop()) {
                        planMapppingPojo.setIsInvoiceCreated(false);
                        planMapppingPojo.setIsinvoicestop(true);
                    }
                    if (custServiceMappingId == null) {
                        CustomerServiceMapping mapping = new CustomerServiceMapping();
                        mapping.setServiceId(Long.valueOf(postpaidPlanObj.getServiceId()));
                        mapping.setCustId(subscriber.getId());
//                        mapping = customersService.generateConnectionNumber(mapping);
                        Boolean isLCO = customers.getLcoId() != null;
                        String connectionNo = numberSequenceUtil.getConnectionNumber(isLCO, customers.getLcoId(), customers.getMvnoId());
                        mapping.setConnectionNo(connectionNo);
                        planMapppingPojo.setCustServiceMappingId(customerServiceMapping.get().getId());
                    } else {
                        if (customerServiceMapping.isPresent()) {
                            if (customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION)) {
                                customerServiceMapping.get().setStatus(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION);
                                planMapppingPojo.setCustPlanStatus(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION);
                            } else {
                                customerServiceMapping.get().setStatus(CommonConstants.ACTIVE_STATUS);
                            }
                            customerServiceMapping.get().setCustId(planMapppingPojo.getCustid());
                            if(model.getDiscount() != null){
                                customerServiceMapping.get().setDiscount(model.getDiscount());
                                customerServiceMapping.get().setNewDiscount(model.getDiscount());
                            }
                            planMapppingPojo.setCustServiceMappingId(customerServiceMapping.get().getId());
                            customerServiceMappingRepository.save(customerServiceMapping.get());
                        }
                    }
//                    if (Objects.isNull(plangroupId)) {
//                        if (model.getPlanId() != null) {
//                            String invoiceType = customerServiceMappingRepository.findInvoiceTypeByCustServiceId(model.getCustServiceMappingId());
//                            if (invoiceType != null) {
//                                planMapppingPojo.setInvoiceType(invoiceType);
//                            }
//                        }
//                    } else {
//                        if (custPlanMappingRepository.findAllByCustServiceMappingId(custServiceMappingId).get(0).getInvoiceType() != null) {
//                            planMapppingPojo.setInvoiceType(custPlanMappingRepository.findAllByCustServiceMappingId(custServiceMappingId).get(0).getInvoiceType());
//                        }
//                    }
                    String invoiceType = customerServiceMappingRepository.findInvoiceTypeByCustServiceId(model.getCustServiceMappingId());
                    if (invoiceType != null) {
                        planMapppingPojo.setInvoiceType(invoiceType);
                    }
                    if (null != plan && plan.getQospolicyid() != null) {
                        qosPolicyDTO = qosPolicyService.getEntityForUpdateAndDelete(plan.getQospolicyid(), subscriber.getMvnoId());
                        if (Objects.nonNull(qosPolicyDTO) && qosPolicyDTO.getDownstreamprofileuid() != null) {
                            quotaDetails.setDownstreamprofileuid(qosPolicyDTO.getDownstreamprofileuid());
                            quotaDetails.setUpstreamprofileuid(qosPolicyDTO.getUpstreamprofileuid());
                        }
                    }
                    planMapppingPojo.setRenewalId(renewalId);
                    if (changePlanDate) event = CommonConstants.EVENTCONSTANTS.RENEW_PLAN;
                    custPlanMappping = custPlanMappingService.save(custPlanMappingService.convertDTOToDomain(planMapppingPojo), event, true, subscriber);
                    planMapppingPojo.setId(custPlanMappping.getId());
                    subscriber.setCustPackageId(custPlanMappping.getId());
                    customersService.saveCustomerChargeHistory(postpaidPlanObj, subscriber, custPlanMappping, plangroupId, false, changePlanDate, null, null);

                } else {
                    CustQuotaDtlsPojo quotaDetails = null;
                    if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.DID_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), plan.getQuotadid(), 0.0, 0.0, 0.0, subscriber, plan.getQuotaunitdid(), null);
                    } else if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.INTERCOM_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), 0.0, 0.0, plan.getQuotaintercom(), 0.0, subscriber, null, plan.getQuotaunitintercom());
                    } else if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.VOICE__BOTH_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), plan.getQuotadid(), 0.0, plan.getQuotaintercom(), 0.0, subscriber, plan.getQuotaunitdid(), plan.getQuotaunitintercom());
                    }
                    quotaDetails.setPlanId(postpaidPlanObj.getId());
                    quotaDetails.setLastQuotaReset(LocalDateTime.now());
                    if (postpaidPlanObj.getUsageQuotaType() != null) {
                        quotaDetails.setUsageQuotaType(postpaidPlanObj.getUsageQuotaType());
                    } else {
                        quotaDetails.setUsageQuotaType("TOTAL");
                    }
                    quotaDetails.setSkipQuotaUpdate(model.isSkipQuotaUpdate());
                    quotaDetails.setUsedQuota(model.getUpdatedUsedQuota());
                    planMapppingPojo.setQuotaList(Collections.singletonList(quotaDetails));
//                    planMapppingPojo.setPlanValidityDays(customersService.calculatePlanValidityDays(subscriber, postpaidPlanObj, Long.valueOf(validity), LocalDateTime.now()));
                    planMapppingPojo.setPlangroupid(plangroupId);
                    planMapppingPojo.setCustServiceMappingId(custServiceMappingId);
                    planMapppingPojo.setRenewalId(renewalId);
                    if (changePlanDate) event = CommonConstants.EVENTCONSTANTS.RENEW_PLAN;
                    custPlanMappping = custPlanMappingService.save(custPlanMappingService.convertDTOToDomain(planMapppingPojo), event, true, subscriber);
                    planMapppingPojo.setId(custPlanMappping.getId());
                    subscriber.setCustPackageId(custPlanMappping.getId());
                    custPlanMappping.setStatus("1");
                    customersService.saveCustomerChargeHistory(postpaidPlanObj, subscriber, custPlanMappping, plangroupId, false, changePlanDate, null, null);
                }
            }
            if (!CollectionUtils.isEmpty(custPlanMappping.getQuotaList())) {
                List<CustQuotaDetails> list = custPlanMappping.getQuotaList();
                if (!CollectionUtils.isEmpty(list)) {
                    CustPlanMappping finalCustPlanMappping = custPlanMappping;
                    list.forEach(x -> x.setLastQuotaReset(LocalDateTime.now()));
                    list.forEach(custQuotaDetails -> custQuotaDetails.setCustPlanMappping(finalCustPlanMappping));
                    custQuotaRepository.saveAll(list);
                }
            }
            if (!CollectionUtils.isEmpty(custPlanMappping.getQuotaList())) {
                List<CustQuotaDetails> list = custPlanMappping.getQuotaList();
                if (!CollectionUtils.isEmpty(list)) {
                    CustPlanMappping finalCustPlanMappping = custPlanMappping;
                    list.forEach(x -> x.setLastQuotaReset(LocalDateTime.now()));
                    list.forEach(custQuotaDetails -> custQuotaDetails.setCustPlanMappping(finalCustPlanMappping));
                    custQuotaRepository.saveAll(list);
                }
            }
            customerServiceMapping.get().setCustId(custPlanMappping.getCustomer().getId());
            customerServiceMappingRepository.save(customerServiceMapping.get());
            return custPlanMappping;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (Exception ex) {
            throw new RuntimeException("Exception on change plan: " + ex.getMessage());
        }
    }


    @Transactional
    public CustomChangePlanDTO cancelTrialPlan(TrialPlanDTO requestDTO, HttpServletRequest request) throws NoSuchFieldException {
        CustomChangePlanDTO customChangePlanDTO = new CustomChangePlanDTO();
        try {
            QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
            BooleanExpression expression = qCustPlanMappping.isNotNull().and(qCustPlanMappping.customer.id.eq(requestDTO.getCustId())).and(qCustPlanMappping.startDate.before(LocalDateTime.now())).and(qCustPlanMappping.endDate.after(LocalDateTime.now())).and(qCustPlanMappping.istrialplan.eq(false));
            boolean isAvailable = custPlanMappingRepository.exists(expression);
            if (!isAvailable) {
                //Terminate customer
                Optional<Customers> customers = customersRepository.findById(requestDTO.getCustId());
                if (customers.isPresent()) {
                    customersService.changeStatus(customers.get().getId(), "Terminate", request, " ");
                } else {
                    throw new RuntimeException("Given customer not available!");
                }
            }
            // add stop in cpr flag = new ArrayList<>();
            List<CustPlanMappping> custPlanMapppingList = fetchTrialPlanList(requestDTO.getCustId(), requestDTO.getPlanId(), requestDTO.getCprId());  //changed plangrpId to planId
            if (!CollectionUtils.isEmpty(custPlanMapppingList)) {
                custPlanMapppingList.forEach(planMappping -> {
                    planMappping.setCustPlanStatus("STOP");
                    custPlanMappingService.update(planMappping, "");
                });
                custPlanMappingRepository.saveAll(custPlanMapppingList);
            } else {
                throw new RuntimeException("Given Plan and customer mapping not available!");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Exception At cancel Trial plan: " + ex.getMessage());
        }
        customChangePlanDTO.setCustomersBasicDetailsPojo(getBasicDetailsOfSubscriber(customersRepository.findById(requestDTO.getCustId()).get()));
        return customChangePlanDTO;
    }

    @Transactional
    public CustomChangePlanDTO extendTrailPlan(TrialPlanDTO requestDTO, HttpServletRequest request) throws NoSuchFieldException {
        CustomChangePlanDTO customChangePlanDTO = new CustomChangePlanDTO();
        try {
            List<CustPlanMappping> custPlanMapppingList = fetchTrialPlanList(requestDTO.getCustId(), requestDTO.getPlanGroupId(), requestDTO.getCprId());
            if (!CollectionUtils.isEmpty(custPlanMapppingList)) {
                for (CustPlanMappping planMappping : custPlanMapppingList) {
                    planMappping.setExpiryDate(planMappping.getExpiryDate().plusDays(requestDTO.getExtendDays()));
                    planMappping.setEndDate(planMappping.getEndDate().plusDays(requestDTO.getExtendDays()));
                }
                custPlanMappingRepository.saveAll(custPlanMapppingList);
            } else {
                throw new RuntimeException("Given Plan and customer mapping not available!");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Exception At extends Trial plan: " + ex.getMessage());
        }
        customChangePlanDTO.setCustomersBasicDetailsPojo(getBasicDetailsOfSubscriber(customersRepository.findById(requestDTO.getCustId()).get()));
        return customChangePlanDTO;
    }

    @Transactional
    public CustomChangePlanDTO covertTrailPlanToNormal(TrialPlanDTO requestDTO, HttpServletRequest request) throws NoSuchFieldException {
        CustomChangePlanDTO customChangePlanDTO = new CustomChangePlanDTO();
        try {
            Customers customers = customersService.getById(requestDTO.getCustId());
            if (customers == null) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Customer not found!", null);
            }
            List<CustPlanMappping> custPlanMapppingList = fetchTrialPlanList(requestDTO.getCustId(), requestDTO.getPlanGroupId(), requestDTO.getCprId());
            if (!CollectionUtils.isEmpty(custPlanMapppingList)) {
                custPlanMapppingList.forEach(planMappping -> {
                    if (requestDTO.getBillingStartFrom().equalsIgnoreCase("CURRENTDATE")) {
                        planMappping.setExpiryDate(LocalDateTime.now().plusDays(planMappping.getPlanValidityDays()));
                        planMappping.setEndDate(LocalDateTime.now().plusDays(planMappping.getPlanValidityDays()));
                        planMappping.setStartDate(LocalDateTime.now());
                        planMappping.setTrailPlanFromToday(true);
                        planMappping.setTrailPlanFromTrailDay(false);
                    } else {
                        Long dateDiff = ChronoUnit.DAYS.between(planMappping.getExpiryDate().toLocalDate(), LocalDateTime.now().toLocalDate());
                        if (dateDiff > 0) {
                            if(customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_PREPAID)) {
                                planMappping.setStartDate(LocalDateTime.now().minusDays(dateDiff));
                            }
                            custPlanMappingRepository.save(planMappping);
                            planMappping.setEndDate(planMappping.getStartDate().plusDays(planMappping.getPlanValidityDays()));
                            planMappping.setExpiryDate(planMappping.getStartDate().plusDays(planMappping.getPlanValidityDays()));
                        } else {
                            planMappping.setExpiryDate(planMappping.getStartDate().plusDays(planMappping.getPlanValidityDays()));
                            planMappping.setEndDate(planMappping.getStartDate().plusDays(planMappping.getPlanValidityDays()));
                        }
                        planMappping.setTrailPlanFromTrailDay(true);
                        planMappping.setTrailPlanFromToday(false);
                    }
                    if (planMappping.getPlanId() != null) {
                        PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(planMappping.getPlanId()).get();
                        Optional<PlanService> services = planServiceRepository.findById(postpaidPlan.getServiceId());
                        if (services.isPresent()) {
                            if (services.get().getExpiry() != null) {
                                if (services.get().getExpiry().equalsIgnoreCase(com.adopt.apigw.constants.Constants.AT_MIDNIGHT)) {
                                    planMappping.setExpiryDate(LocalDateTime.of(planMappping.getExpiryDate().toLocalDate(), LocalTime.of(23, 59, 59)));
                                    planMappping.setEndDate(LocalDateTime.of(planMappping.getExpiryDate().toLocalDate(), LocalTime.of(23, 59, 59)));
                                }
                            }
                        }
                    }
                    planMappping.setCustPlanStatus("Active");
                    planMappping.setIstrialplan(false);
                    planMappping.setIsinvoicestop(false);
                    planMappping.setRemarks(requestDTO.getRemarks());
                });
                custPlanMappingRepository.saveAll(custPlanMapppingList);
                customers.setIsinvoicestop(false);
                customers.setIstrialplan(false);
                customersRepository.save(customers);
            } else {
                throw new RuntimeException("Given Plan and customer mapping not available!");
            }
            createDataSharedService.updateCustomerCPREntityForAllMicroServce(customers, custPlanMapppingList);
            sendcustomerRadiusUpdateStatus(requestDTO.getCustId());
            customChangePlanDTO.setCustomersBasicDetailsPojo(getBasicDetailsOfSubscriber(customersRepository.findById(requestDTO.getCustId()).get()));
            customChangePlanDTO.setCustpackagerelid(requestDTO.getCprId());
            customChangePlanDTO.setRemarks(requestDTO.getRemarks());
        } catch (Exception ex) {
            throw new RuntimeException("Exception At cancel Trial plan: " + ex.getMessage());
        }
        return customChangePlanDTO;
    }

    public List<CustPlanMappping> fetchTrialPlanList(Integer custId, Integer planGroupId, Integer cprId) {
        QCustPlanMappping custPlanMappping = QCustPlanMappping.custPlanMappping;
        BooleanExpression booleanExpression = custPlanMappping.isNotNull().and(custPlanMappping.customer.id.eq(custId)).and(custPlanMappping.istrialplan.eq(true));

        if (planGroupId != null) {
            booleanExpression = booleanExpression.and(custPlanMappping.planId.eq(planGroupId)); //plangrpid as a planid
        }
        return (List<CustPlanMappping>) custPlanMappingRepository.findAll(booleanExpression);
    }

    /*
    update customer to radius on termination
    trial plan cancel
    trial plan extends
     */
    public void sendcustomerRadiusUpdateStatus(Integer custId) {
        Optional<Customers> customers = customersRepository.findById(custId);
        CustomerUpdateMessage customerMessage = new CustomerUpdateMessage(customers.get());
//        messageSender.send(customerMessage, RabbitMqConstants.QUEUE_RADIUS_CUSTOMER_UPDATE_STATUS);
        kafkaMessageSender.send(new KafkaMessageData(customerMessage, CustomerUpdateMessage.class.getSimpleName()));
    }

    @Transactional
    public List<CustServiceMappingDTO> addPromiseToPayInBulk(PromiseToPayPojoInBulk promiseToPayPojoInBulk) {
        List<Integer> custPlanIds = promiseToPayPojoInBulk.getPromiseToPay().stream().map(PromiseToPayPojo::getCustPlanMapping).collect(Collectors.toList());
        List<Integer> custSerIds = custPlanMappingRepository.getAllByCustServiceMappingIdInCprIds(custPlanIds);
        List<CustomerServiceMapping> serviceMappings = custPlanMappingService.changeStatusOfCustServices(custSerIds, StatusConstants.CUSTOMER_SERVICE_STATUS.INGRACE, promiseToPayPojoInBulk.getPromise_to_pay_remarks(), Boolean.FALSE);
        return serviceMappings.stream().map(customerServiceMapping -> new CustServiceMappingDTO(customerServiceMapping)).collect(Collectors.toList());
        //        List<CustPlanMappping> planMapppingList = custPlanMappingRepository.findAllByIdIn(custPlanIds);
//        if (CollectionUtils.isEmpty(planMapppingList)) {
//            throw new RuntimeException("No Plan Available for P2P!");
//        }
//        Integer gracePeriod = Integer.valueOf(clientServiceRepository.findValueByName("graceperiod"));
//        Integer graceDays = promiseToPayPojoInBulk.getGraceDays();
//        if (null == graceDays) {
//            graceDays = gracePeriod;
//        }
//        if (gracePeriod < graceDays) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Promise to pay days must not be more than " + gracePeriod, null);
//        }
//        Set<Integer> postpaidPlanIds = planMapppingList.stream().map(CustPlanMappping::getPlanId).collect(Collectors.toSet());
//        if (!CollectionUtils.isEmpty(postpaidPlanIds)) {
//            List<Long> serviceIds = postpaidPlanRepo.findServiceIdByPlanId(postpaidPlanIds);
//            List<CustPlanMappping> custPlanMapppingList = getChilCustomerPlans(planMapppingList.get(0).getCustomer().getId(), serviceIds, Boolean.TRUE);
//            if (!CollectionUtils.isEmpty(custPlanMapppingList))
//                planMapppingList.addAll(custPlanMapppingList);
//        }
//        return promiseToPanMultipleCpr(planMapppingList, graceDays, promiseToPayPojoInBulk.getPromise_to_pay_remarks());
    }


    @Transactional
    public Customers addPromiseToPay(Integer custId, Integer graceDays, String promise_to_pay_remarks) {
        Integer gracePeriod = Integer.valueOf(clientServiceRepository.findValueByNameandMvnoId("graceperiod", getLoggedInMvnoId(custId)));
        if (null == graceDays) {
            graceDays = gracePeriod;
        }
        if (gracePeriod < graceDays) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Promise to pay days must not be more than " + gracePeriod, null);
        }

        Customers customers = customersRepository.findById(custId).get();
        if (null == customers) {
            throw new RuntimeException("Unable to fetch basic Details of customer id " + custId);
        }
        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
        BooleanExpression expression = qCustPlanMappping.isNotNull().and(qCustPlanMappping.customer.id.eq(custId));
        List<CustPlanMappping> custPlanMapppings = (List<CustPlanMappping>) custPlanMappingRepository.findAll(expression);
        if (CollectionUtils.isEmpty(custPlanMapppings)) {
            throw new RuntimeException("No Active Plan Available for P2P!");
        }
        Set<Integer> postpaidPlanIds = custPlanMapppings.stream().map(CustPlanMappping::getPlanId).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(postpaidPlanIds)) {
            List<Long> serviceIds = postpaidPlanRepo.findServiceIdByPlanId(postpaidPlanIds);
            List<CustPlanMappping> custPlanMapppingList = getChilCustomerPlans(custPlanMapppings.get(0).getCustomer().getId(), serviceIds, Boolean.TRUE);
            if (!CollectionUtils.isEmpty(custPlanMapppingList)) custPlanMapppings.addAll(custPlanMapppingList);
        }
        promiseToPanMultipleCpr(custPlanMapppings, graceDays, promise_to_pay_remarks);
        return customers;
    }

    @Transactional
    public List<CustPlanMapppingPojo> promiseToPanMultipleCpr(List<CustPlanMappping> custPlanMapppings, Integer graceDays, String promise_to_pay_remarks) {
        List<CustPlanMapppingPojo> custPlanMapppingPojoList = new ArrayList<>();
        try {
            custPlanMapppings.removeIf(custPlanMappping -> custPlanMappping.getIsHold() && custPlanMappping.getIsVoid() != null);
            List<CustPlanMappping> custPlanMapppingList = custPlanMapppings.stream().filter(custPlan -> custPlan.getEndDate().isBefore(LocalDateTime.now())).collect(Collectors.toList());
            List<String> services = custPlanMapppingList.stream().map(CustPlanMappping::getService).collect(Collectors.toList());
            Optional<CustPlanMappping> sameCustPlanMapping = custPlanMapppings.stream().filter(custPlan -> !services.contains(custPlan.getService()) && custPlan.getStartDate().isAfter(LocalDateTime.now())).findAny();
            Optional<CustPlanMappping> sameactiveCustPlanMapping = custPlanMapppings.stream().filter(custPlan -> !services.contains(custPlan.getService()) && custPlan.getStartDate().isBefore(LocalDateTime.now()) && custPlan.getEndDate().isAfter(LocalDateTime.now())).findAny();

            if (sameCustPlanMapping.isPresent() || sameactiveCustPlanMapping.isPresent()) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Promise to pay not allowed as Future/Present date plan with same service is available with customer!", null);
            }
            Customers customer = null;//custPlanMapppingList.get(0).getCustomer();
            for (CustPlanMappping custPlanMappping : custPlanMapppingList) {
                Long count = null;
                if (custPlanMappping.getPromisetopay_renew_count() == null) {
                    count = 0L;
                } else {
                    count = custPlanMappping.getPromisetopay_renew_count();
                }
                Long systemPromiseToPayCount = Long.valueOf(clientServiceSrv.getValueByName(ClientServiceConstant.PROMISETOPAY_COUNT));
                if (count < systemPromiseToPayCount) {


                    count = count + 1;
                    LocalDateTime endDate = LocalDateTime.now();//custPlanMappping.get().getEndDate();
                    custPlanMappping.setEndDate(endDate.plusDays(graceDays));
                    custPlanMappping.setExpiryDate(endDate.plusDays(graceDays));
                    custPlanMappping.setGraceDays(graceDays);
                    custPlanMappping.setPromise_to_pay_remarks(promise_to_pay_remarks);
                    custPlanMappping.setGraceDateTime(custPlanMappping.getEndDate().plusDays(graceDays));
                    custPlanMappping.setPromisetopay_renew_count(count);
                    custPlanMappping.setPromise_to_pay_startdate(LocalDateTime.now());
                    LocalDateTime promiseToPayEndDate = LocalDateTime.now().plusDays(graceDays);
                    custPlanMappping.setPromise_to_pay_enddate(promiseToPayEndDate);
                    custPlanMappping.setCustPlanStatus(CommonConstants.ACTIVE_STATUS);
                    custPlanMappingRepository.save(custPlanMappping);
                    custPlanMapppingPojoList.add(custPlanMappingService.convertDomainToDto(custPlanMappping));
                    custPlanMappingService.updateCustPlanEndDateInRadius(custPlanMappping, "P2P");
                    ezBillServiceUtility.extendExpiryDateInEZBill(custPlanMappping, custPlanMappping.getEndDate());
                    customer = custPlanMappping.getCustomer();
                } else {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Promise to pay has already been used.", null);
                }
                if (customer != null) {
                    List<CustomerPlansModel> list = getFuturePlanList(customer.getId(), false);
                    if (CollectionUtils.isEmpty(list)) {
                        customer.setStatus(CommonConstants.INGRACE_STATUS);
                        customersRepository.save(customer);
                    }
                }
            }

        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        } catch (Exception ex) {
            throw new RuntimeException("Exception when adding promise to pay: " + ex.getMessage());
        }
        return custPlanMapppingPojoList;
    }

    @Transactional
    public List<CustPlanMapppingPojo> holdServiceInBulk(DeactivatePlanReqDTOList reqDTOList) {
        List<CustPlanMapppingPojo> result = new ArrayList<>();
        List<Integer> custServiceIds = reqDTOList.getDeactivatePlanReqModels().stream().map(DeactivatePlanReqModel::getCustServiceMappingId).collect(Collectors.toList());
        if (reqDTOList.isServiceStopBulkFlag()) {
            List<PlanGroup> planGroups = new ArrayList<>();
            List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingIdIn(custServiceIds);
            if (!CollectionUtils.isEmpty(custPlanMapppingList)) {
                planGroups = custPlanMapppingList.stream().map(CustPlanMappping::getPlanGroup).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(planGroups)) {
                    List<CustPlanMappping> plangroupCustPlans = custPlanMappingService.getCustPlanMappingByPlanGroup(custPlanMapppingList.get(0).getCustomer(), planGroups, true);
                    if (!CollectionUtils.isEmpty(plangroupCustPlans)) {
                        List<Integer> custSerIds = plangroupCustPlans.stream().map(CustPlanMappping::getCustServiceMappingId).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(custSerIds)) {
                            List<CustomerServiceMapping> serviceMappings = customerServiceMappingRepository.findAllByIdIn(custSerIds);
                            if (!CollectionUtils.isEmpty(serviceMappings)) {
                                custServiceIds.addAll(serviceMappings.stream().map(CustomerServiceMapping::getId).collect(Collectors.toList()));
                            }
                        }
                    }
                }

            }

        }
        custServiceIds = custServiceIds.stream().distinct().collect(Collectors.toList());
        List<CustomerServiceMapping> customerServiceMappings = customerServiceMappingRepository.findAllByIdIn(custServiceIds);
        customerServiceMappings.stream().forEach(customerServiceMapping -> {
            customerServiceMapping.setGenerateCreditDoc(reqDTOList.getGenerateCreditDoc());
            reqDTOList.setServiceResumeDate(LocalDate.now().plusDays(reqDTOList.getHoldDays() + 1));
            List<CustPlanMappping> custPlanMapppings = custPlanMappingRepo.findAllByCustServiceMappingId(customerServiceMapping.getId());
            custPlanMapppings = custPlanMapppings.stream().filter(custPlanMappping -> custPlanMappping.getExpiryDate().isAfter(LocalDateTime.now())).collect(Collectors.toList());
            for (CustPlanMappping mappping : custPlanMapppings) {
                if (mappping.getExpiryDate().isBefore(LocalDate.now().plusDays(reqDTOList.getHoldDays()).atStartOfDay())) {
                    throw new IllegalStateException("Service hold days exceed expiry date");
                }

            }
            checkValidity(customerServiceMapping.getCustId(),customerServiceMapping,reqDTOList);
//            PostpaidPlan postpaidPlan= postpaidPlanRepo.getPlanDetailsById(custPlanMapppings.get(0).getPlanId()).orElse(null);
//            if ( Objects.nonNull(postpaidPlan.getMaxHoldAttempts()) && Objects.nonNull(postpaidPlan.getMaxHoldDurationDays()) && postpaidPlan.getMaxHoldAttempts() > 0 && postpaidPlan.getMaxHoldDurationDays() > 0) {
//                boolean exceededAttempts = customerServiceMapping.getServiceHoldAttempts() >= postpaidPlan.getMaxHoldAttempts();
//                boolean exceededDuration =
//                        (customerServiceMapping.getActualHoldDurationDays() +
//                                ChronoUnit.DAYS.between(LocalDate.now().plusDays(1), reqDTOList.getServiceResumeDate()))
//                                > postpaidPlan.getMaxHoldDurationDays();
//
//                if (exceededAttempts || exceededDuration) {
//                    throw new IllegalStateException("Hold limit exceeded: " +
//                            (exceededAttempts ? "Max hold attempts reached. " : "") +
//                            (exceededDuration ? "Max hold duration exceeded." : ""));
//                }
//            }
        });
        if (!CollectionUtils.isEmpty(custServiceIds)) {
            custPlanMappingService.changeStatusOfCustServices(custServiceIds, CommonConstants.WORKFLOW_AUDIT_STATUS.INPROGRESS, reqDTOList.getDeactivatePlanReqModels().get(0).getRemarks(), false);
        } else {
            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), "Customer service id is mandatory for resume service!", null);
        }
        List<CustPlanMappping> custPlanMapppings = custPlanMappingRepo.findAllByCustServiceMappingIdIn(custServiceIds);
        custPlanMapppings.removeIf(custPlanMappping ->
                StatusConstants.CUSTOMER_SERVICE_STATUS.STOP.equalsIgnoreCase(custPlanMappping.getCustPlanStatus())
        );
        custPlanMapppings.forEach(custPlanMappping -> {
            custPlanMappping.setStatus(CommonConstants.WORKFLOW_AUDIT_STATUS.INPROGRESS);
            custPlanMappping.setCustPlanStatus(CommonConstants.WORKFLOW_AUDIT_STATUS.INPROGRESS);
            result.add(custPlanMappingService.convertDomainToDto(custPlanMappping));
            DeactivatePlanReqModel pojo = new DeactivatePlanReqModel();
            pojo.setCprId(custPlanMappping.getId());
            pojo.setRemarks(reqDTOList.getDeactivatePlanReqModels().get(0).getRemarks());
            pojo.setReasonId(reqDTOList.getDeactivatePlanReqModels().get(0).getReasonId());
            saveserviceAudit(custPlanMappping, pojo, CommonConstants.WORKFLOW_AUDIT_STATUS.INPROGRESS);
        });
        custPlanMappingRepo.saveAll(custPlanMapppings);
//        List<CustomerServiceMapping> customerServiceMappingList = customerServiceMappingRepository.findAllByIdIn(custServiceIds);
        customerServiceMappings.stream().forEach(i -> {
            i.setServiceHoldDate(LocalDateTime.now());
            i.setServiceHoldBy(getLoggedInUser().getUsername());
            i.setRemarks("Service Hold In progress");
            i.setServiceResumeDate(reqDTOList.serviceResumeDate.atStartOfDay());
            i.setServiceHoldAttempts(i.getServiceHoldAttempts() + 1);
            i.setActualHoldDurationDays((i.getActualHoldDurationDays() + Math.toIntExact(ChronoUnit.DAYS.between(LocalDate.now().plusDays(1), reqDTOList.getServiceResumeDate()))));
        });
        customerServiceMappingRepository.saveAll(customerServiceMappings);
        return result;
    }

    public void saveserviceAudit(CustPlanMappping custPlanMappping, DeactivatePlanReqModel pojo, String custPlanStatus) {
        try {
            ServiceAudit serviceAudit = new ServiceAudit();
            serviceAudit.setAction(custPlanStatus);
            if (custPlanMappping.getServiceHoldDate() != null)
                serviceAudit.setServiceStopTime(custPlanMappping.getServiceHoldDate());
            if (custPlanMappping.getStartServiceDate() != null)
                serviceAudit.setServicestarttime(custPlanMappping.getStartServiceDate());
            serviceAudit.setCprid(Long.valueOf(pojo.getCprId()));
            serviceAudit.setRemarks(pojo.getRemarks());
            if (pojo.getReasonId() != -1) {
                serviceAudit.setReasonId(String.valueOf(pojo.getReasonId()));
            }
            String reason = commonListRepository.findByReasonId(pojo.getReasonId());
            // CommonList commonList = commonListRepository.findCategoryReasonById(pojo.getReasonId());
            serviceAudit.setReason(reason);
            //serviceAudit.setReason(commonList.getText());
            Long userId = (long) getLoggedInUserId();
            if (userId != null && userId != 0 && userId != -1) {
                serviceAudit.setStaffId(userId);
            } else {
                serviceAudit.setStaffId(Long.valueOf(custPlanMappping.getCreatedById()));
            }
            StaffUser staffUser = staffUserRepository.findById(Math.toIntExact(serviceAudit.getStaffId())).get();
            serviceAudit.setStaffName(staffUser.getUsername());
            serviceAudit.setCustServiceMappingId(custPlanMappping.getCustServiceMappingId());
            serviceAuditRepository.save(serviceAudit);

        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }

    public CustPlanMappping updateEndAndStartDate(CustPlanMappping custPlanMappping, LocalDateTime updatedDateTime, boolean updateToRadius) {
        custPlanMappping.setEndDate(updatedDateTime);
        custPlanMappping.setExpiryDate(updatedDateTime);
        custPlanMappping.setGraceDays(0);
        if (updateToRadius) custPlanMappingService.updateCustPlanEndDateInRadius(custPlanMappping, "");
        return custPlanMappping;
    }

    @Transactional
    public List<CustPlanMapppingPojo> resumeServiceInBulk(DeactivatePlanReqDTOList planReqDTOList) {
        List<CustPlanMapppingPojo> result = new ArrayList<>();
        List<Integer> custServiceIds = planReqDTOList.getDeactivatePlanReqModels().stream().map(DeactivatePlanReqModel::getCustServiceMappingId).collect(Collectors.toList());
        if (planReqDTOList.isServiceStopBulkFlag()) {
            List<PlanGroup> planGroups = new ArrayList<>();
            List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingIdIn(custServiceIds);
            if (!CollectionUtils.isEmpty(custPlanMapppingList)) {
                planGroups = custPlanMapppingList.stream().map(CustPlanMappping::getPlanGroup).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(planGroups)) {
                    List<CustPlanMappping> plangroupCustPlans = custPlanMappingService.getCustPlanMappingByPlanGroup(custPlanMapppingList.get(0).getCustomer(), planGroups, true);
                    if (!CollectionUtils.isEmpty(plangroupCustPlans)) {
                        List<Integer> custSerIds = plangroupCustPlans.stream().map(CustPlanMappping::getCustServiceMappingId).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(custSerIds)) {
                            List<CustomerServiceMapping> serviceMappings = customerServiceMappingRepository.findAllByIdIn(custSerIds);
                            if (!CollectionUtils.isEmpty(serviceMappings)) {
                                custServiceIds.addAll(serviceMappings.stream().map(CustomerServiceMapping::getId).collect(Collectors.toList()));
                            }
                        }
                    }
                }

            }
        }
        custServiceIds = custServiceIds.stream().distinct().collect(Collectors.toList());
//        if (!CollectionUtils.isEmpty(custServiceIds)) {
//            custPlanMappingService.changeStatusOfCustServices(custServiceIds, StatusConstants.CUSTOMER_SERVICE_STATUS.RESUME, planReqDTOList.getDeactivatePlanReqModels().get(0).getRemarks(), false);
//        } else {
//            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), "Customer service id is mandatory for resume service!", null);
//        }
//        List<CustPlanMappping> custPlanMapppings = custPlanMappingRepo.findAllByCustServiceMappingIdIn(custServiceIds);
//        custPlanMapppings.forEach(custPlanMappping -> {
//            result.add(custPlanMappingService.convertDomainToDto(custPlanMappping));
//            DeactivatePlanReqModel pojo = new DeactivatePlanReqModel();
//            pojo.setCprId(custPlanMappping.getId());
//            pojo.setRemarks(planReqDTOList.getDeactivatePlanReqModels().get(0).getRemarks());
//            pojo.setReasonId(planReqDTOList.getDeactivatePlanReqModels().get(0).getReasonId());
//            saveserviceAudit(custPlanMappping, pojo, "Resume");
//        });
        if (!CollectionUtils.isEmpty(custServiceIds)) {
            List<CustomerServiceMapping> customerServiceMappings = customerServiceMappingRepository.findAllByIdIn(custServiceIds);
            customerServiceMappings.stream().forEach(i -> {
                i.setServiceResumeBy(getLoggedInUser().getUsername());
                i.setRemarks("Service Resume In progress");
                i.setServiceResumeDate(LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay());
                long daysElapsed = ChronoUnit.DAYS.between(i.getServiceHoldDate(), LocalDateTime.now());
                int updatedHold = i.getActualHoldDurationDays() - Math.toIntExact(daysElapsed);
                i.setActualHoldDurationDays(Math.max(updatedHold, 0));
            });
            customerServiceMappingRepository.saveAll(customerServiceMappings);
        } else {
            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), "Customer service id is mandatory for resume service!", null);
        }
        return result;
    }

    private void startChildService(CustPlanMappping custPlanMappping) {
        try {
            Integer parentCustid = custPlanMappping.getCustomer().getId();
            String parentService = custPlanMappping.getService();

            //child cust details extraction from customers table
            QCustomers qCustomers = QCustomers.customers;
            BooleanExpression exp = qCustomers.isNotNull().and(qCustomers.parentCustomers.id.eq(parentCustid));
            exp = exp.and(qCustomers.status.equalsIgnoreCase("Active"));
            List<Customers> childCust = (List<Customers>) customersRepository.findAll(exp);
            List<Integer> childID = childCust.stream().map(childId -> childId.getId()).collect(Collectors.toList());

            //child data in cpr table
            if (childID.size() > 0) {
                QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
                BooleanExpression exp2 = qCustPlanMappping.isNotNull();
                exp2 = exp2.and(qCustPlanMappping.customer.id.in(childID)).and(qCustPlanMappping.custPlanStatus.equalsIgnoreCase("STOP")).and(qCustPlanMappping.service.equalsIgnoreCase(parentService));
                exp2 = exp2.and(qCustPlanMappping.stopServiceDate.isNotNull());
                List<CustPlanMappping> childCpr = (List<CustPlanMappping>) custPlanMappingRepository.findAll(exp2);
                boolean isFutureAvailab = childCpr.stream().filter(CustPlanMappping -> CustPlanMappping.getStartDate().isAfter(LocalDateTime.now())).findAny().isPresent();
                List<Integer> childCprId = childCpr.stream().map(i -> i.getCustomer().getId()).collect(Collectors.toList());

                if (childCprId.size() > 0) {
                    for (CustPlanMappping cpr : childCpr) {
                        LocalDate stopDate = cpr.getStopServiceDate();
                        Long daysDiff = ChronoUnit.DAYS.between(stopDate, LocalDate.now());
                        cpr.setStopServiceDate(null);
                        cpr.setCustPlanStatus("Active");
                        cpr.setIsHold(Boolean.FALSE);
                        cpr.setServiceStartBy(getLoggedInUser().getFirstName() + " " + getLoggedInUser().getLastName());
                        cpr.setServiceStartRemarks(custPlanMappping.getServiceStartRemarks());
                        cpr.setStartServiceDate(LocalDateTime.now());
                        final LocalDateTime endDate = cpr.getEndDate();
                        LocalDateTime updatedDays = cpr.getEndDate();
                        updateEndAndStartDate(cpr, updatedDays.plusDays(daysDiff), true);

                        if (isFutureAvailab && custPlanMappping.getStartDate().isAfter(LocalDateTime.now()))
                            cpr.setEndDate(endDate.plusDays(daysDiff));
                        else if (!isFutureAvailab) cpr.setEndDate(endDate.plusDays(daysDiff));
                        else cpr.setEndDate(endDate);

                        if (cpr.getTotalHoldDays() != null) daysDiff = daysDiff + cpr.getTotalHoldDays();
                        cpr.setTotalHoldDays(daysDiff.intValue());
                    }
                    custPlanMappingRepository.saveAll(childCpr);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CustPlanMappping getCustPlanMappingByAddUnitValiditiy(CustPlanMappping custPlanMappping, Integer validity, String unit) {
        Integer response = 0;
        try {
            switch (unit) {
                case Constants.MINUTES:
                    custPlanMappping.getEndDate().plusMinutes(validity);
                    custPlanMappping.getExpiryDate().plusMinutes(validity);
                    break;
                case Constants.HOUR:
                    custPlanMappping.getEndDate().plusHours(validity);
                    custPlanMappping.getExpiryDate().plusHours(validity);
                    break;
                case Constants.DAYS:
                    custPlanMappping.getEndDate().plusDays(validity);
                    custPlanMappping.getExpiryDate().plusDays(validity);
                    break;
                case Constants.MONTH:
                    custPlanMappping.getEndDate().plusMonths(validity);
                    custPlanMappping.getExpiryDate().plusMonths(validity);
                    break;
                case Constants.YEAR:
                    custPlanMappping.getEndDate().plusYears(validity);
                    custPlanMappping.getExpiryDate().plusYears(validity);
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            logger.error("Exception to convert days from unit");
        }
        return custPlanMappping;
    }

    public Boolean isCustomerValidToStopService(Integer custId, List<Integer> custServiceMappingIdList) {
        List<CustomerPlansModel> futurePladPlansModels = getFuturePlanList(custId, false);
        if (!CollectionUtils.isEmpty(futurePladPlansModels)) {
            return futurePladPlansModels.stream().filter(customerPlansModel -> customerPlansModel.getCustomerServiceMappingId() != null && !customerPlansModel.getPlangroup().equalsIgnoreCase("Volume Booster") && !customerPlansModel.getPlangroup().equalsIgnoreCase("Bandwidthbooster")).filter(customerPlansModel -> custServiceMappingIdList.contains(customerPlansModel.getCustomerServiceMappingId())).findAny().isPresent();
        }

        return false;//custPlanMappingRepository.exists(expression);
    }

    public List<CustomerPlansModel> getActivePlanListForServiceId(List<CustomerPlansModel> plansList, Integer serviceId) {
        List<CustomerPlansModel> removeData = new ArrayList<>();
        for (CustomerPlansModel lists : plansList) {
            Integer planids = lists.getPlanId();
            List<Integer> ids = new ArrayList<>();
            ids.add(planids);
            if (!postpaidPlanRepo.existsByIdInAndServiceId(ids, serviceId)) {
                removeData.add(lists);
            }
        }
        if (!CollectionUtils.isEmpty(removeData)) plansList.removeAll(removeData);
        return plansList;
    }

    public void validateTransaction(String transactionId, Long orderId) {
        List<CustomerPayment> customerPaymentList = customerPaymentRepository.findAllByPgTransactionId(transactionId);
        if (!customerPaymentList.isEmpty()) {
            throw new RuntimeException("Already Payment happen with this link");
        }
        List<CustomerPayment> customerPaymentList1 = customerPaymentRepository.findAllByOrderIdAndStatusContainingIgnoreCase(orderId, "Successful");
        if (!customerPaymentList1.isEmpty()) {
            throw new RuntimeException("Already Payment happen with this link(transref)");
        }
    }

    public CustomerServiceMapping saveNickName(Integer id, String name) {
        CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(id).get();
        customerServiceMapping.setNickName(name);
        customerServiceMappingRepository.save(customerServiceMapping);
        return customerServiceMapping;
    }


    //@Transactional
    public DeactivatePlanReqDTO deActivatePlanForCAFCustomer(DeactivatePlanReqDTO requestDTO) throws NoSuchFieldException {
        if (requestDTO.getBillableCustomerId() == null) {
            requestDTO.setBillableCustomerId(requestDTO.getCustId());
        }
        if (requestDTO.getCustId() == null) {
            throw new RuntimeException("custId is mandatory");
        }
        Optional<Customers> customers = customersRepository.findById(requestDTO.getCustId());
        if (CollectionUtils.isEmpty(requestDTO.getDeactivatePlanReqModels())) {
            throw new RuntimeException("plan details is mandatory");
        }
//        if (isCustomerValidToStopService(requestDTO.getCustId())) {
//            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Change plan not allowed as Future date plan is available with customer!", null);
//        }

        if (!customers.isPresent()) {
            throw new RuntimeException("Customer not available!");
        }

        List<CustomerPlansModel> activeCustPlanModelList = this.subscriberService.getActivePlanList(customers.get().getId(), false);
//        activeCustPlanModelList.removeIf(customerPlansModel -> (customerPlansModel.getCustPlanStatus().equalsIgnoreCase("STOP") && customerPlansModel.getStopServiceDate() == null));
        if(activeCustPlanModelList.isEmpty()){
            activeCustPlanModelList = this.subscriberService.getExpiredPlanList(customers.get().getId(), false);
        }
        if (customers.isPresent()) {
            customers.get().setLastBillDate(LocalDate.now());
            Integer debitDocId = null;//new HashSet<>();
            HashSet<Integer> debitdocIds = new HashSet<>();
            Random rnd = new Random();
            int renewalId = rnd.nextInt(999999);
            List<Integer> oldDebitdocIds = new ArrayList<>();
            for (DeactivatePlanReqModel model : requestDTO.getDeactivatePlanReqModels()) {
                if (requestDTO.isPlanGroupFullyChanged()) {
                    Optional<PlanGroup> newPlanGroup = planGroupRepository.findById(requestDTO.getDeactivatePlanReqModels().get(0).getNewPlanGroupId());
                    if (newPlanGroup.isPresent()) customers.get().setPlangroup(newPlanGroup.get());
                }
                List<CustomerPlansModel> list = new LinkedList<>();
                if (model.getCustServiceMappingId() != null) {
                    list = activeCustPlanModelList.stream().filter(customerPlansModel -> model.getCustServiceMappingId().equals(customerPlansModel.getCustomerServiceMappingId()) && !customerPlansModel.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER) && !customerPlansModel.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_BANDWIDTH_BOOSTER)).collect(Collectors.toList());
                } else {
                    list = activeCustPlanModelList.stream().filter(customerPlansModel -> !customerPlansModel.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER) && !customerPlansModel.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_BANDWIDTH_BOOSTER)).collect(Collectors.toList());
                }
                List<CustPlanMappping> planMapppingList = new ArrayList<>();
                CustPlanMappping custPlanMappping = null;
                if (!CollectionUtils.isEmpty(list)) {
                    for (CustomerPlansModel planMappping : list)
                        planMapppingList.add(custPlanMappingRepository.findById(planMappping.getPlanmapid()));
                    custPlanMappping = custPlanMappingRepository.findById(list.get(0).getPlanmapid());
                }
                for (CustPlanMappping planMappping : planMapppingList) {

                    planMappping.setBillableCustomerId(requestDTO.getBillableCustomerId());

                    if (planMappping.getStopServiceDate() != null && planMappping.getCustPlanStatus().equalsIgnoreCase("STOP"))
                        throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Change plan not allowed as service is STOP!", null);
                    if (requestDTO.isPlanGroupChange()) {
                        if (planMappping != null) {
                            if (planMappping.getPlanGroup() == null) {
                                planMappping.setPlanGroup(customers.get().getPlangroup());
                            }
                            if (planMappping.getTraildebitdocid() != null) {
                                debitDocId = planMappping.getTraildebitdocid().intValue();//.add();
                                debitdocIds.add(debitDocId);
                            }
                        }
                        oldDebitdocIds = debitdocIds.stream().collect(Collectors.toList());
                        deactivateCustPlanMappingForCaf(planMappping, model.getNewPlanId(), customers.get().getPlangroup().getPlanGroupId(), model, customers.get(), requestDTO.getPaymentOwner(), oldDebitdocIds, requestDTO.getPaymentOwnerId(), renewalId);
                    } else {
                        if (planMappping != null) {
                            if (model.getNewPlanId().equals(planMappping.getPlanId())) {
                                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "The current Active plan is the same as the selected change plan. " + "Please select a different plan to perform the Change plan.", null);
                            }
                        }
                        if (planMappping.getTraildebitdocid() != null)
                            oldDebitdocIds.add(planMappping.getTraildebitdocid().intValue());
                        deactivateCustPlanMappingForCaf(planMappping, model.getNewPlanId(), null, model, customers.get(), requestDTO.getPaymentOwner(), oldDebitdocIds, requestDTO.getPaymentOwnerId(), renewalId);
                    }
                }
                if (requestDTO.isPlanGroupChange() && debitDocId != null) {
                    debitDocService.createInvoice(customers.get(), null, 200, debitdocIds, "", requestDTO.getPaymentOwner(), requestDTO.getPaymentOwnerId(), false, CommonConstants.INVOICE_TYPE.CHANGE_PLAN);
                } else if (requestDTO.isPlanGroupChange() && activeCustPlanModelList.size() == 0) {
                    debitDocService.createInvoice(customers.get(), null, 200, debitdocIds, "", requestDTO.getPaymentOwner(), requestDTO.getPaymentOwnerId(), false, CommonConstants.INVOICE_TYPE.CHANGE_PLAN);
                }
            }
            //Update invoice status
                if (!CollectionUtils.isEmpty(oldDebitdocIds)) {
                List<TrialDebitDocument> trialDebitDocuments = trialDebitDocumentRepository.findAllByIdIn(oldDebitdocIds);
                trialDebitDocuments = trialDebitDocuments.stream().peek(debitDoc -> debitDoc.setBillrunstatus(com.adopt.apigw.constants.Constants.CANCELLED)).collect(Collectors.toList());
                trialDebitDocumentRepository.saveAll(trialDebitDocuments);
            }

        }
        return requestDTO;
    }

    public DeactivatePlanReqModel deactivateCustPlanMappingForCaf(CustPlanMappping custPlanMappping, Integer newPlanId, Integer planGroupId, DeactivatePlanReqModel model, Customers customers, String paymentOwner, List<Integer> oldDebitdocIds, Integer paymentOwnerId, Integer renewalId) {
        try {
            if (Objects.isNull(paymentOwner)) {
                paymentOwner = "";
            }
            if (paymentOwner == null) {
                paymentOwnerId = -1;
            }
            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(newPlanId);//planService.get(newPlanId);
            if (!postpaidPlan.isPresent()) {
                throw new RuntimeException("New Plan not available!");
            }
            Optional<DebitDocument> debitDocument = Optional.empty();
            Integer custServiceMappingId = null;
            if (custPlanMappping != null) {
                //change CPR
                final LocalDate endDateForDbr = custPlanMappping.getEndDate().toLocalDate();
                custPlanMappping.setEndDate(LocalDateTime.now());
                custPlanMappping.setExpiryDate(LocalDateTime.now());
                custPlanMappping.setCustPlanStatus("STOP");
                if (!oldDebitdocIds.isEmpty()) {
                    custPlanMappping.setTraildebitdocid(oldDebitdocIds.get(0).longValue());
                }
                QCustomerServiceMapping qCustomerServiceMapping = QCustomerServiceMapping.customerServiceMapping;
                custServiceMappingId = custPlanMappping.getCustServiceMappingId();
                custPlanMappping.setCustServiceMappingId(custServiceMappingId);
                custPlanMappingRepository.save(custPlanMappping);
                custPlanMappingService.updateCustPlanEndDateInRadius(custPlanMappping, CommonConstants.EVENTCONSTANTS.CHANGE_PLAN);

            }

            //CustPlanMappping response = saveCustPlanMapping(customerMapper.domainToDTO(custPlanMappping.getCustomer(), new CycleAvoidingMappingContext()), postpaidPlan.get(), planGroupId, model.getDiscount(), custPlanMappping.getCustomer());
            CustPlanMappping response = saveCustPlanMapping(customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()), postpaidPlan.get(), planGroupId, model, customers, custServiceMappingId, renewalId, false, CommonConstants.EVENTCONSTANTS.CHANGE_PLAN, null);

            response.setBillableCustomerId(custPlanMappping.getBillableCustomerId());
            custPlanMappingRepository.save(response);

            model.setCprId(response.getId());
            if (custPlanMappping != null && planGroupId == null) {
                if (Objects.isNull(custPlanMappping.getTraildebitdocid())) {
                    FlagDTO flagDTO = new FlagDTO();
                    Double discount = model.getDiscount();
                    if (discount == null ){
                        flagDTO.setDiscount(false);
                    }else {
                        flagDTO.setDiscount(true);
                    }
                    debitDocService.createInvoice(customers, CommonConstants.INVOICE_TYPE.CHANGE_PLAN, "", null, null, null, false, false, null, null,flagDTO);
                }
            }
            if (custPlanMappping == null) {
                if (planGroupId == null)
                    debitDocService.createInvoice(customers, null, 200, null, "", paymentOwner, paymentOwnerId, false, CommonConstants.INVOICE_TYPE.CHANGE_PLAN);
            } else if (custPlanMappping.getPlanGroup() == null && planGroupId == null) {
                if (model.getIsFromFlutterWave() != null && model.getIsFromFlutterWave().length() != 0 && model.getIsFromFlutterWave().equalsIgnoreCase("true")) {
                    debitDocService.createInvoice(customers, null, 200, null, model.getCreditDocumentId(), paymentOwner, paymentOwnerId, false, CommonConstants.INVOICE_TYPE.CHANGE_PLAN);
                } else {
                    HashSet<Integer> debitDocIds = new HashSet<>();
                    if (debitDocument.isPresent()) {
                        debitDocIds.add(debitDocument.get().getId());
                        debitDocService.createInvoice(customers, null, 200, debitDocIds, null, paymentOwner, paymentOwnerId, false, CommonConstants.INVOICE_TYPE.CHANGE_PLAN);
                    } else {
                        debitDocService.createInvoice(customers, null, 200, null, null, paymentOwner, paymentOwnerId, false, CommonConstants.INVOICE_TYPE.CHANGE_PLAN);
                    }
                }
            }

            return model;
        } catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }

    }

    @Transactional
    public List<CustPlanMapppingPojo> extendServiceValidityInBulk(ExtendPlanValidityInBulk extendPlanValidityInBulk) {
        try {
            List<CustPlanMapppingPojo> custPlanMapppingPojoList = new ArrayList<>();
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            List<ExtendPlanValidity> extendPlanValidities = extendPlanValidityInBulk.getExtendPlanValidity();
            extendPlanValidities = extendPlanValidities.stream().filter(UtilsCommon.distinctByKey(ExtendPlanValidity::getCustPlanMapppingId)).collect(Collectors.toList());
            for (ExtendPlanValidity extendPlanValidity : extendPlanValidityInBulk.getExtendPlanValidity()) {
                Optional<CustomerServiceMapping> customerServiceMapping = customerServiceMappingRepository.findById(extendPlanValidity.getCustPlanMapppingId());
                QCustomers qCustomers = QCustomers.customers;
                BooleanExpression booleanExpression = qCustomers.isDeleted.eq(false).and(qCustomers.parentCustomers.id.eq(customerServiceMapping.get().getCustId()));
                if (extendPlanValidity.isExtentionforChild()) {
                    if (getBUIdsFromCurrentStaff().size() != 0)
                        // TODO: pass mvnoID manually 6/5/2025
                        booleanExpression = booleanExpression.and(qCustomers.mvnoId.eq(1).or(qCustomers.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qCustomers.buId.in(getBUIdsFromCurrentStaff()))));
                    List<Customers> customers = IterableUtils.toList(customersRepository.findAll(booleanExpression));
                    for (Customers custPlanMappping : customers) {
                        genericDataDTO = extendServiceValidity(custPlanMappping.getCustomerServiceMappingList().get(0), extendPlanValidity);
                    }

                }
                if (customerServiceMapping.isPresent()) {
                    try {
                        genericDataDTO = extendServiceValidity(customerServiceMapping.get(), extendPlanValidity);
                        custPlanMapppingPojoList.addAll((Collection<? extends CustPlanMapppingPojo>) genericDataDTO.getData());
                    } catch (CustomValidationException ce) {
                        //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
                        ce.printStackTrace();
                        throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), ce.getMessage(), null);
                    } catch (Exception ex) {
                        throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
                    }
                }
            }
            return custPlanMapppingPojoList;
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), ce.getMessage(), null);
        } catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    /**
     * @param extendStartDate
     * @param extendEndDate
     * @param custPlanExtendValidityMappings
     * @return boolean
     * @detail check extend validity is valid or not
     */
    public boolean checkExtendValidityisValid(LocalDate extendStartDate, LocalDate extendEndDate, List<CustPlanExtendValidityMapping> custPlanExtendValidityMappings) {
        for (CustPlanExtendValidityMapping custPlanMappping : custPlanExtendValidityMappings) {
            if (custPlanMappping.getDownTimeStartDate().equals(extendStartDate) || custPlanMappping.getDownTimeStartDate().equals(extendEndDate) || custPlanMappping.getDownTimeExpiryDate().equals(extendStartDate) || custPlanMappping.getDownTimeExpiryDate().equals(extendEndDate)) {
                return true;
            } else if ((custPlanMappping.getDownTimeStartDate().isBefore(extendStartDate) || custPlanMappping.getDownTimeStartDate().equals(extendStartDate)) && (custPlanMappping.getDownTimeExpiryDate().isAfter(extendEndDate) || custPlanMappping.getDownTimeExpiryDate().equals(extendEndDate))) {
                return true;
            }
        }
        return false;

    }

    @Transactional
    public GenericDataDTO extendServiceValidity(CustomerServiceMapping customerServiceMapping, ExtendPlanValidity extendPlanValidity) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (extendPlanValidity.getDownStartDate().isAfter(extendPlanValidity.getDownEndDate())) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Down End Time can not be less than Down Start Time!", null);
            }
            List<CustPlanMappping> planMapppingList = custPlanMappingRepository.findAllByCustServiceMappingId(customerServiceMapping.getId());
            planMapppingList = planMapppingList.stream().filter(i -> i.getCustPlanStatus().equalsIgnoreCase("Active") || i.getCustPlanStatus().equalsIgnoreCase("InGrace")).sorted(comparing(CustPlanMappping::getStartDate).reversed()).filter(custPlanMappping -> !custPlanMappping.getPurchaseType().equals(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER) && !custPlanMappping.getPurchaseType().equals(CommonConstants.PLAN_GROUP_BANDWIDTH_BOOSTER)).filter(UtilsCommon.distinctByKey(CustPlanMappping::getCustServiceMappingId)).collect(Collectors.toList());
            Integer custId = planMapppingList.get(0).getCustomer().getId();
            Integer planGroupId;
            Set<PlanGroup> planGroups = planMapppingList.stream().filter(custPlanMapp -> custPlanMapp.getPlanGroup() != null).map(CustPlanMappping::getPlanGroup).collect(Collectors.toSet());
            //PlanGroup
            if (!CollectionUtils.isEmpty(planGroups)) {
                Set<Integer> planGroupIds = planGroups.stream().map(PlanGroup::getPlanGroupId).collect(Collectors.toSet());
                planGroupId = extendPlanValidity.getPlanGroupId();
                QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
                BooleanExpression expression = qCustPlanMappping.isNotNull().and(qCustPlanMappping.planGroup.planGroupId.in(planGroupIds)).and(qCustPlanMappping.customer.id.eq(custId));
                planMapppingList = (List<CustPlanMappping>) custPlanMappingRepository.findAll(expression);
            } else if (planMapppingList.get(0).getPlanGroup() != null && custId != null) {
                planGroupId = planMapppingList.get(0).getPlanGroup().getPlanGroupId();
//                LocalDate endDate = planMapppingList.get(0).getEndDate().toLocalDate();
                QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
                BooleanExpression expression = qCustPlanMappping.isNotNull().and(qCustPlanMappping.planGroup.planGroupId.eq(planGroupId)).and(qCustPlanMappping.customer.id.eq(custId));
                planMapppingList = (List<CustPlanMappping>) custPlanMappingRepository.findAll(expression);
            }

            if (CollectionUtils.isEmpty(planMapppingList)) {
                throw new RuntimeException("No Service available to extend!");
            }
            //Child
            if (!CollectionUtils.isEmpty(planMapppingList)) {
                List<CustPlanMappping> custPlanMapppingList = getChilCustomerPlans(planMapppingList.get(0).getCustomer().getId(), Collections.singletonList(customerServiceMapping.getServiceId()), Boolean.TRUE);
                if (!CollectionUtils.isEmpty(planMapppingList)) {
                    planMapppingList.addAll(custPlanMapppingList);
                }
            }
            planMapppingList.removeIf(custPlanMappping -> custPlanMappping.getPurchaseType().equals(CommonConstants.PLAN_GROUP_BANDWIDTH_BOOSTER) || custPlanMappping.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.STOP));
            planMapppingList.removeIf(custPlanMappping -> (custPlanMappping.getEndDate().isBefore(LocalDateTime.now()) && custPlanMappping.getPurchaseType().equals(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER)));

            List<CustPlanMappping> volumeBoosterPlans = planMapppingList.stream().filter(custPlanMappping -> custPlanMappping.getPurchaseType().equals(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER)).collect(Collectors.toList());
            planMapppingList.stream().filter(custPlanMappping -> !custPlanMappping.getPurchaseType().equals(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER)).sorted(comparing(CustPlanMappping::getStartDate).reversed());
            List<CustPlanMappping> custPlanDistinctByService = planMapppingList.stream().sorted(comparing(CustPlanMappping::getStartDate).reversed()).filter(custPlanMappping -> !custPlanMappping.getPurchaseType().equals(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER)).filter(UtilsCommon.distinctByKey(CustPlanMappping::getCustServiceMappingId)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(volumeBoosterPlans)) {
                custPlanDistinctByService.addAll(volumeBoosterPlans);
            }
            Long validityDays = ChronoUnit.DAYS.between(extendPlanValidity.getDownStartDate(), extendPlanValidity.getDownEndDate()) + 1;
            List<CustPlanExtendValidityMapping> custPlanExtendValidityMappings = custPlanExtendValidityMappingRepository.findAllByCustPlanMapppingId(custPlanDistinctByService.get(0).getId());
            if (!custPlanExtendValidityMappings.isEmpty() && custPlanExtendValidityMappings.size() > 0) {
                if (checkExtendValidityisValid(extendPlanValidity.getDownStartDate(), extendPlanValidity.getDownEndDate(), custPlanExtendValidityMappings)) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Down time already used!", null);
                }
            }

            custPlanDistinctByService = custPlanDistinctByService.stream().peek(custPlanMappping -> {
                if (custPlanMappping.getEndDate().isBefore(LocalDateTime.now()))
                    custPlanMappping.setEndDate(LocalDateTime.now().plusDays(validityDays));
                else custPlanMappping.setEndDate(custPlanMappping.getEndDate().plusDays(validityDays));
                CustPlanExtendValidityMapping custPlanExtendValidityMapping = new CustPlanExtendValidityMapping();
                custPlanExtendValidityMapping.setDownTimeStartDate(extendPlanValidity.getDownStartDate());
                custPlanExtendValidityMapping.setDownTimeExpiryDate(extendPlanValidity.getDownEndDate());
                custPlanExtendValidityMapping.setExtendValidityremarks(extendPlanValidity.getExtend_validity_remarks());
                custPlanExtendValidityMapping.setCustPlanMappping(custPlanMappping);
                custPlanExtendValidityMapping.setCustServiceMappingId(customerServiceMapping.getId());
                custPlanExtendValidityMappingRepository.save(custPlanExtendValidityMapping);
                if (custPlanMappping.getEndDate().isAfter(LocalDateTime.now()) || custPlanMappping.getPromisetopay_renew_count() > 0)
                    custPlanMappping.setCustPlanStatus(CommonConstants.ACTIVE_STATUS);
                if (custPlanMappping.getPromise_to_pay_enddate() != null)
                    custPlanMappping.setPromise_to_pay_enddate(LocalDateTime.now());
            }).collect(Collectors.toList());
            //custPlanDistinctByService.stream().forEach(x->{
            //dbrService.extendsValidityForDBR(validityDays,x.getPlanId(),x.getDebitdocid().intValue(),x.getCustomer().getId());
            //});

            if (customerServiceMapping.getInvoiceType() != null && customerServiceMapping.getInvoiceType().equalsIgnoreCase("group")) {
                if (isChildEligibleToExtend(customerServiceMapping, custPlanDistinctByService)) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Plan extention not allowed, extention days are greater than parent plan!", null);
                }
            }
            custPlanMappingRepository.saveAll(custPlanDistinctByService);
            List<CustPlanMapppingPojo> custPlanMapppingPojoList = new ArrayList<>();
            custPlanDistinctByService.forEach(custPlanMappping -> {
                custPlanMappingService.updateCustPlanEndDateInRadius(custPlanMappping, "P2P");
                ezBillServiceUtility.serviceExtensionResponse(custPlanMappping, custPlanMappping.getEndDate());

            });
            List<CustPlanMapppingPojo> customerPlansModel = customerMapper.mapCustPlanMapListToCustPlanMapPojoList(custPlanDistinctByService, new CycleAvoidingMappingContext());
            genericDataDTO.setData(customerPlansModel);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            return genericDataDTO;
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), ce.getMessage(), null);
        } catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    private boolean isChildEligibleToExtend(CustomerServiceMapping customerServiceMapping, List<CustPlanMappping> custPlanDistinctByService) {
        Customers customers = customersRepository.findById(customerServiceMapping.getCustId()).get();
        Integer parentId = customers.getParentCustomers().getId();

        //for future plans
        if (parentId != null) {
            if (custPlanDistinctByService.get(0).getStartDate().isAfter(LocalDateTime.now())) {
                List<CustomerPlansModel> futureplansOfParent = getFuturePlanList(parentId, false);
                if (futureplansOfParent.stream().filter(customerPlansModel -> customerPlansModel.getServiceId() == customerServiceMapping.getServiceId().longValue()).findAny().isPresent()) {
                    List<CustomerPlansModel> parentPlans = futureplansOfParent.stream().filter(i -> i.getServiceId() == (customerServiceMapping.getServiceId().longValue())).collect(Collectors.toList());
//                CustomerPlansModel filteredParentPlan =  parentPlans.stream().sorted((o2, o1) -> o1.getEndDate().compareTo(o2.getEndDate())).findFirst().get();
                    parentPlans.sort(Comparator.comparing(CustomerPlansModel::getEndDate).reversed());
                    if (parentPlans.get(0).getEndDate().toLocalDate().isAfter(custPlanDistinctByService.get(0).getEndDate().toLocalDate()) || parentPlans.get(0).getEndDate().toLocalDate().equals(custPlanDistinctByService.get(0).getEndDate().toLocalDate())) {
                        return false;
                    }
                }
            }

            //for current plans
            List<CustomerServiceMapping> customerServiceMappingParent = customerServiceMappingRepository.findByCustId(parentId);
            Services serviceName = serviceRepository.findById(customerServiceMapping.getServiceId()).get();
            String servicenameChild = serviceName.getServiceName();
            if (customerServiceMappingParent.stream().filter(custServiceMap -> custServiceMap.getServiceId().equals(customerServiceMapping.getServiceId())).findAny().isPresent()) {

                List<CustPlanMappping> custPlanMappping = custPlanMappingRepo.findAllByCustomerId(parentId);
                List<CustomerPlansModel> activePlanListOfParent = getActivePlanList(parentId, false);
                if (activePlanListOfParent.stream().filter(customerPlansModel -> customerPlansModel.getServiceId() == customerServiceMapping.getServiceId().longValue()).findAny().isPresent()) {
                    List<CustomerPlansModel> parentPlans = activePlanListOfParent.stream().filter(customerPlansModel -> customerPlansModel.getServiceId() == customerServiceMapping.getServiceId().longValue()).collect(Collectors.toList());
                    parentPlans.sort(Comparator.comparing(CustomerPlansModel::getEndDate).reversed());
                    return !parentPlans.get(0).getEndDate().toLocalDate().isAfter(custPlanDistinctByService.get(0).getEndDate().toLocalDate()) && !parentPlans.get(0).getEndDate().toLocalDate().isBefore(custPlanDistinctByService.get(0).getEndDate().toLocalDate());
                }
                return true;
            }
        }
        return false;
    }

    @Transactional
    public List<CustPlanMappping> extendChildValidity(CustomerServiceMapping customerServiceMapping) {
        try {
            Customers parentCustomers = customersRepository.findById(customerServiceMapping.getCustId()).get();
            List<Customers> customers = customersRepository.findAllByParentCustomers(parentCustomers);
            List<CustPlanMappping> custPlanMapppingList1 = new ArrayList<>();
//            customers.removeIf(i->i.getInvoiceType().equalsIgnoreCase(CommonConstants.INVOICE_TYPE_INDEPENDENT));
//            if (customers.stream().anyMatch(i -> i.getInvoiceType().equalsIgnoreCase(CommonConstants.INVOICE_TYPE_GROUP))) {
            if (customers.size() > 0) {
                List<Integer> custIds = customers.stream().map(i -> i.getId()).collect(Collectors.toList());
                QCustomerServiceMapping qCustomerServiceMapping = QCustomerServiceMapping.customerServiceMapping;
                BooleanExpression expression = qCustomerServiceMapping.isNotNull().and(qCustomerServiceMapping.custId.in(custIds)).and(qCustomerServiceMapping.serviceId.eq(customerServiceMapping.getServiceId()).and(qCustomerServiceMapping.invoiceType.equalsIgnoreCase(CommonConstants.INVOICE_TYPE_GROUP)));
                List<CustomerServiceMapping> customerServiceMappingList = (List<CustomerServiceMapping>) customerServiceMappingRepository.findAll(expression);
                for (CustomerServiceMapping customerServiceMapping1 : customerServiceMappingList) {
                    List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingId(customerServiceMapping1.getId());
                    custPlanMapppingList.removeIf(custPlanMappping -> custPlanMappping.getCustPlanStatus().equalsIgnoreCase("STOP"));
                    if (!CollectionUtils.isEmpty(custPlanMapppingList)) {
                        custPlanMapppingList.sort(Comparator.comparing(CustPlanMappping::getStartDate).reversed());
                        Set<Integer> serviceIdSet = new HashSet<>();
                        List<CustPlanMappping> custPlanDistinctByService = custPlanMapppingList.stream().filter(e -> serviceIdSet.add(e.getCustServiceMappingId())).collect(Collectors.toList());
                        custPlanMapppingList1.addAll(custPlanDistinctByService);
                    }
                }
            }
            custPlanMapppingList1.removeIf(custPlanMappping -> custPlanMappping.getPurchaseType().equals(CommonConstants.PLAN_GROUP_BANDWIDTH_BOOSTER));
            custPlanMapppingList1.removeIf(custPlanMappping -> (custPlanMappping.getEndDate().isBefore(LocalDateTime.now()) && custPlanMappping.getPurchaseType().equals(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER)));

            List<CustPlanMappping> volumeBoosterPlans = custPlanMapppingList1.stream().filter(custPlanMappping -> custPlanMappping.getPurchaseType().equals(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER)).collect(Collectors.toList());
            custPlanMapppingList1.stream().filter(custPlanMappping -> !custPlanMappping.getPurchaseType().equals(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER)).sorted(comparing(CustPlanMappping::getStartDate).reversed());
            List<CustPlanMappping> custPlanDistinctByService = custPlanMapppingList1.stream().filter(UtilsCommon.distinctByKey(CustPlanMappping::getCustServiceMappingId)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(volumeBoosterPlans)) {
                custPlanDistinctByService.addAll(volumeBoosterPlans);
            }
            return custPlanMapppingList1;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public List getdiscoutAudit(Integer custpackid) {
        List<discountAudit> discountAudits = new ArrayList<>();
        if (custpackid != null) {
            QdiscountAudit qdiscountAudit = QdiscountAudit.discountAudit;
            BooleanExpression booleanExpression = qdiscountAudit.isNotNull().and(qdiscountAudit.custpackgeid.eq(custpackid));
            discountAudits = IterableUtils.toList(discountAuditRepocitory.findAll(booleanExpression));
        } else {
            discountAudits = null;
        }
        return discountAudits;
    }

    public List<CustomerPlansModel> getTotalPlanList(Integer customerId) {
        List<CustomerPlansModel> totalPlanList = new ArrayList<>();
        List<CustomerPlansModel> activePlanList = getActivePlanList(customerId, false);
        List<CustomerPlansModel> expiredPlanList = getExpiredPlanList(customerId, false);
        activePlanList = activePlanList.stream().filter(customerPlansModel -> customerPlansModel.getPlanId() != 14).collect(Collectors.toList());
        expiredPlanList = expiredPlanList.stream().filter(customerPlansModel -> customerPlansModel.getPlanId() != 14).collect(Collectors.toList());
        activePlanList = activePlanList.stream().filter(customerPlansModel -> !customerPlansModel.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_ONLY)).collect(Collectors.toList());
        expiredPlanList = expiredPlanList.stream().filter(customerPlansModel -> !customerPlansModel.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_ONLY)).collect(Collectors.toList());
        if (!activePlanList.isEmpty()) {
            totalPlanList.addAll(activePlanList);
        } else {
            totalPlanList.addAll(expiredPlanList);
        }
        totalPlanList = totalPlanList.stream().filter(toolService.distinctByKey(customerPlansModel -> customerPlansModel.getPlanName())).collect(Collectors.toList());
        return totalPlanList;
    }

    public GenericDataDTO getCircuitDetailsByCustServiceMapId(Integer custServiceMapId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        LinkAcceptanceDTO linkAcceptanceDTO = new LinkAcceptanceDTO();
        try {
            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(custServiceMapId).get();
            if (customerServiceMapping != null) {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        linkAcceptanceDTO.setLeaseCircuitId(customerServiceMapping.getId());
                linkAcceptanceDTO.setCircuitName(customerServiceMapping.getLeaseCircuitName());
                linkAcceptanceDTO.setCircuitStatus(customerServiceMapping.getCircuitStatus());
                linkAcceptanceDTO.setCafNo(customerServiceMapping.getCafNo());
                linkAcceptanceDTO.setUploadCAF(customerServiceMapping.getUploadCAF());
                linkAcceptanceDTO.setCustomerName(customerServiceMapping.getCustomerName());
                linkAcceptanceDTO.setAccountNumber(customerServiceMapping.getAccountNumber());
                linkAcceptanceDTO.setTypeOfLink(customerServiceMapping.getTypeOfLink());
                linkAcceptanceDTO.setLinkInstallationDate(customerServiceMapping.getLinkInstallationDate());
                linkAcceptanceDTO.setLinkAcceptanceDate(customerServiceMapping.getLinkAcceptanceDate());
                linkAcceptanceDTO.setPurchaseOrderDate(customerServiceMapping.getPurchaseOrderDate());
//linkAcceptanceDTO.setCircleName(customerServiceMapping.getCircleName());
//linkAcceptanceDTO.setOrderLoginDate(customerServiceMapping.getOrderLoginDate());
                linkAcceptanceDTO.setPartner(customerServiceMapping.getPartner());
                linkAcceptanceDTO.setExpiryDate(customerServiceMapping.getExpiryDate());
                linkAcceptanceDTO.setDistance(customerServiceMapping.getDistance());
                linkAcceptanceDTO.setDistanceUnit(customerServiceMapping.getDistanceUnit());
                linkAcceptanceDTO.setBandwidth(customerServiceMapping.getBandwidth());
                linkAcceptanceDTO.setUploadQOS(customerServiceMapping.getUploadQOS());
                linkAcceptanceDTO.setDownloadQOS(customerServiceMapping.getDownloadQOS());
                linkAcceptanceDTO.setLinkRouterLocation(customerServiceMapping.getLinkRouterLocation());
                linkAcceptanceDTO.setLinkPortType(customerServiceMapping.getLinkPortType());
                linkAcceptanceDTO.setLinkRouterIP(customerServiceMapping.getLinkRouterIp());
                linkAcceptanceDTO.setLinkPortOnRouter(customerServiceMapping.getLinkPortOnRouter());
                linkAcceptanceDTO.setVLANId(customerServiceMapping.getVLANId());
                linkAcceptanceDTO.setBandwidthType(customerServiceMapping.getBandwidthType());
                linkAcceptanceDTO.setLinkRouterName(customerServiceMapping.getLinkRouterName());
                linkAcceptanceDTO.setCircuitBillingId(customerServiceMapping.getCircuitBillingId());
                linkAcceptanceDTO.setPop(customerServiceMapping.getPop());
                linkAcceptanceDTO.setAssociatedLevel(customerServiceMapping.getAssociatedLevel());
                linkAcceptanceDTO.setLocationLevel1(customerServiceMapping.getLocationLevel1());
                linkAcceptanceDTO.setLocationLevel2(customerServiceMapping.getLocationLevel2());
                linkAcceptanceDTO.setLocationLevel3(customerServiceMapping.getLocationLevel3());
                linkAcceptanceDTO.setLocationLevel4(customerServiceMapping.getLocationLevel4());
                linkAcceptanceDTO.setBaseStationId1(customerServiceMapping.getBaseStationId1());
                linkAcceptanceDTO.setBaseStationId2(customerServiceMapping.getBaseStationId2());
//linkAcceptanceDTO.setOrganisationCircle(customerServiceMapping.getOrganisationCircle());
//linkAcceptanceDTO.setTerminationCircle(customerServiceMapping.getTerminationCircle());
                linkAcceptanceDTO.setTerminationAddress(customerServiceMapping.getTerminationAddress());
//linkAcceptanceDTO.setOrganisationAddress2(customerServiceMapping.getOrganisationAddress2());
//linkAcceptanceDTO.setTerminationAddress2(customerServiceMapping.getTerminationAddress2());
                linkAcceptanceDTO.setNote(customerServiceMapping.getNote());
                linkAcceptanceDTO.setContactPerson(customerServiceMapping.getContactPerson());
                linkAcceptanceDTO.setContactPerson1(customerServiceMapping.getContactPerson1());
//linkAcceptanceDTO.setContactPerson2(customerServiceMapping.getContactPerson2());
                linkAcceptanceDTO.setMobileNumber(customerServiceMapping.getMobileNumber());
                linkAcceptanceDTO.setMobileNumber1(customerServiceMapping.getMobileNumber1());
//linkAcceptanceDTO.setMobileNumber2(customerServiceMapping.getMobileNumber2());
                linkAcceptanceDTO.setLandLineNumber(customerServiceMapping.getLandlineNumber());
                linkAcceptanceDTO.setLandLineNumber1(customerServiceMapping.getLandlineNumber1());
//linkAcceptanceDTO.setLandlineNumber2(customerServiceMapping.getLandLineNumber2());
                linkAcceptanceDTO.setEmailId(customerServiceMapping.getEmailId());
                linkAcceptanceDTO.setEmailId1(customerServiceMapping.getEmailId1());
//linkAcceptanceDTO.setEmailId2(customerServiceMapping.getEmailId2());
//linkAcceptanceDTO.setTraiRate(customerServiceMapping.getTraiRate());
                linkAcceptanceDTO.setOtcChargesFile(customerServiceMapping.getOtcChargesFile());
                linkAcceptanceDTO.setServiceChargerFile(customerServiceMapping.getServiceChargerFile());
                linkAcceptanceDTO.setStaticOrPooledIP(customerServiceMapping.getStaticOrPooledIP());
                linkAcceptanceDTO.setChargeTypeFile(customerServiceMapping.getChargeTypeFile());
                linkAcceptanceDTO.setBillingCycle(customerServiceMapping.getBillingCycle());
                linkAcceptanceDTO.setBillingType(customerServiceMapping.getBillingType());
                linkAcceptanceDTO.setBillable(customerServiceMapping.getBillable());
                linkAcceptanceDTO.setBillingGroup(customerServiceMapping.getBillingGroup());
                linkAcceptanceDTO.setPayable(customerServiceMapping.getPayable());
                linkAcceptanceDTO.setEnableProcessing(customerServiceMapping.getEnableProcessing());
                linkAcceptanceDTO.setDeposite(customerServiceMapping.getDeposite());
                linkAcceptanceDTO.setPoNumber(customerServiceMapping.getPoNumber());
                linkAcceptanceDTO.setBillRemark(customerServiceMapping.getBillRemark());
                linkAcceptanceDTO.setFullName(customerServiceMapping.getFullName());
                linkAcceptanceDTO.setOrganisation(customerServiceMapping.getOrganisation());
                linkAcceptanceDTO.setAddress1(customerServiceMapping.getAddress1());
                linkAcceptanceDTO.setAddress2(customerServiceMapping.getAddress2());
                linkAcceptanceDTO.setCity(customerServiceMapping.getCity());
                linkAcceptanceDTO.setZipCode(customerServiceMapping.getZipcode());
                linkAcceptanceDTO.setState(customerServiceMapping.getState());
                linkAcceptanceDTO.setCountry(customerServiceMapping.getCountry());
                linkAcceptanceDTO.setStatus(customerServiceMapping.getStatus());
                linkAcceptanceDTO.setMvnoId(customerServiceMapping.getMvnoId());
                linkAcceptanceDTO.setBuId(customerServiceMapping.getBuId());
                linkAcceptanceDTO.setServiceAreaType(customerServiceMapping.getServiceAreaType());
                linkAcceptanceDTO.setBranch(customerServiceMapping.getBranch());
                linkAcceptanceDTO.setPlanService(customerServiceMapping.getServiceId());
                linkAcceptanceDTO.setConnectionType(customerServiceMapping.getConnectionType());
                genericDataDTO.setData(linkAcceptanceDTO);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("No Records..");
            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    public Page<ServiceAuditDTO> getserviceStatusAudit(Integer custpackid, List<GenericSearchModel> filters, PaginationRequestDTO paginationRequestDTO) {
        List<ServiceAudit> serviceAudit = null;
        PageRequest pageRequest = PageRequest.of(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
        QServiceAudit qServiceAudit = QServiceAudit.serviceAudit;
        List<ServiceAuditDTO> ServiceAuditDTOlist = new ArrayList<>();

        serviceAudit = serviceAuditRepository.findResonIdByCpr(custpackid);
        for (ServiceAudit serviceAudit1 : serviceAudit) {
            ServiceAuditDTO serviceAuditDTO = new ServiceAuditDTO();
            serviceAuditDTO.setId(serviceAudit1.getId());
            serviceAuditDTO.setCustServiceMappingId(serviceAudit1.getCustServiceMappingId());
            serviceAuditDTO.setAction(serviceAudit1.getAction());
            serviceAuditDTO.setRemarks(serviceAudit1.getRemarks());
            serviceAuditDTO.setCprid(serviceAudit1.getCprid());
            serviceAuditDTO.setReasonCategory(serviceAudit1.getReasonCategory());
            serviceAuditDTO.setStaffName(serviceAudit1.getStaffName());
            serviceAuditDTO.setReason(serviceAudit1.getReason());
            serviceAuditDTO.setServicestarttime(serviceAudit1.getServicestarttime());
            serviceAuditDTO.setServiceStopTime(serviceAudit1.getServiceStopTime());
            serviceAuditDTO.setAuditDate(serviceAudit1.getAuditDate());
            ServiceAuditDTOlist.add(serviceAuditDTO);
        }
        return new PageImpl<>(ServiceAuditDTOlist, pageRequest, ServiceAuditDTOlist.size());
    }


    public boolean canChildApprove(Customers childCustomer, Long serviceId) {
        Optional<Customers> parentCustomer = Optional.ofNullable(childCustomer.getParentCustomers());//customersRepository.findById(childCustomer.getParentCustomersId());
        if (parentCustomer.isPresent()) {
            LocalDateTime parentExpirayDate = getParentExpiredDate(parentCustomer.get(), serviceId);
            return !LocalDateTime.now().isAfter(parentExpirayDate);
        }
        return true;
    }

    public LocalDateTime getParentExpiredDate(Customers parentCustomer, Long serviceId) {
        LocalDateTime expiredDate = LocalDateTime.now();

        List<CustomerPlansModel> futureList = subscriberService.getFuturePlanList(parentCustomer.getId(), false);//.stream().filter(x -> x.getService().equals(serviceName)).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(futureList)) {
            futureList = futureList.stream().sorted(Comparator.comparing(CustomerPlansModel::getEndDate).reversed()).filter(customerPlansModel -> customerPlansModel.getServiceId().equals(serviceId)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(futureList)) {
                return UtilsCommon.convertToLocalDateTimeViaSqlTimestamp(futureList.get(0).getEndDate());
            }
        }
        List<CustomerPlansModel> activeList = subscriberService.getActivePlanList(parentCustomer.getId(), false);//.stream().filter(x -> x.getService().equals(serviceName)).sorted((o1, o2) -> o2.getExpiryDate().compareTo(o1.getExpiryDate())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(activeList)) {
            activeList = activeList.stream().sorted(Comparator.comparing(CustomerPlansModel::getEndDate).reversed()).filter(customerPlansModel -> customerPlansModel.getServiceId().intValue() == (serviceId)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(activeList)) {
                return UtilsCommon.convertToLocalDateTimeViaSqlTimestamp(activeList.get(0).getEndDate());
            }
        }

        return expiredDate;
    }

    @Transactional
    public List<CustPlanMapppingPojo> stopServiceInBulk(DeactivatePlanReqDTOList reqDTOList) {
        List<CustPlanMapppingPojo> result = new ArrayList<>();
        List<Integer> custServiceMappIds = reqDTOList.getDeactivatePlanReqModels().stream().map(DeactivatePlanReqModel::getCustServiceMappingId).collect(Collectors.toList());
        if (reqDTOList.isServiceStopBulkFlag() || !reqDTOList.isServiceStopBulkFlag()) {
            List<PlanGroup> planGroups = new ArrayList<>();
            List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingIdIn(custServiceMappIds);
            if (!CollectionUtils.isEmpty(custPlanMapppingList)) {
                planGroups = custPlanMapppingList.stream().map(CustPlanMappping::getPlanGroup).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(planGroups)) {
                    List<CustPlanMappping> plangroupCustPlans = custPlanMappingService.getCustPlanMappingByPlanGroup(custPlanMapppingList.get(0).getCustomer(), planGroups, false);
                    if (!CollectionUtils.isEmpty(plangroupCustPlans)) {
                        List<Integer> custSerIds = plangroupCustPlans.stream().map(CustPlanMappping::getCustServiceMappingId).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(custSerIds)) {
                            List<CustomerServiceMapping> serviceMappings = customerServiceMappingRepository.findAllByIdIn(custSerIds);
                            if (!CollectionUtils.isEmpty(serviceMappings)) {
                                custServiceMappIds.addAll(serviceMappings.stream().map(CustomerServiceMapping::getId).collect(Collectors.toList()));
                            }
                        }
                    }
                }

            }

        }
        List<CustomerServiceMapping> custServiceMappingDTOS = custPlanMappingService.changeStatusOfCustServices(custServiceMappIds, StatusConstants.CUSTOMER_SERVICE_STATUS.STOP, reqDTOList.getDeactivatePlanReqModels().get(0).getRemarks(), false);
        if (!CollectionUtils.isEmpty(custServiceMappingDTOS)) {
            List<Integer> serviceIds = custServiceMappingDTOS.stream().map(CustomerServiceMapping::getId).collect(Collectors.toList());
            ServiceTerminationMessage message = new ServiceTerminationMessage(serviceIds, StatusConstants.CUSTOMER_SERVICE_STATUS.STOP, reqDTOList.getDeactivatePlanReqModels().get(0).getRemarks(), true);
//            messageSender.send(message,RabbitMqConstants.QUEUE_SERVICE_TERMINATION_REVENUE);
            kafkaMessageSender.send(new KafkaMessageData(message, ServiceTerminationMessage.class.getSimpleName()));
        }
        return result;
    }

    public List<CustPlanMappping> getChilCustomerPlans(Integer parentCutId, List<Long> serviceId, boolean isExpReq) {
        List<CustPlanMappping> planMapppingList = new ArrayList<>();
        List<Integer> childCustomers = customersRepository.findAllByParentId(parentCutId);
        List<CustomerServiceMapping> customerServiceMappings = new ArrayList<>();
        if (!CollectionUtils.isEmpty(childCustomers)) {
            List<CustomerServiceMapping> childServiceMappings = customerServiceMappingRepository.findAllByCustIdInAndServiceIdInAndInvoiceType(childCustomers, serviceId, CommonConstants.INVOICE_TYPE_GROUP);
            if (!CollectionUtils.isEmpty(childServiceMappings)) {
                customerServiceMappings.addAll(childServiceMappings);
            }
        }
        if (!CollectionUtils.isEmpty(customerServiceMappings)) {
            List<Integer> csmIds = customerServiceMappings.stream().map(CustomerServiceMapping::getId).collect(Collectors.toList());
            List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
            if (isExpReq)
                custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingIdInAndIsHoldAndIsVoid(csmIds, Boolean.FALSE, Boolean.FALSE);
            else
                custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingIdInAndCustPlanStatusAndEndDateIsAfter(csmIds, CommonConstants.ACTIVE_STATUS, LocalDateTime.now());

            if (!CollectionUtils.isEmpty(custPlanMapppingList)) {
                planMapppingList.addAll(custPlanMapppingList);
            }
        }
        return planMapppingList;
    }

    public List<CustPlanMappingDropdownPojo> getSerialNumber(Integer custId, List<Integer> serviceIds) {
        List<CustPlanMappping> serialNumberList = custPlanMappingRepository.findAllByCustomerIdAndService(custId, serviceIds);

        return custPlanMappingDropdownPojo(serialNumberList);
    }

    public List<CustPlanMappingDropdownPojo> custPlanMappingDropdownPojo(List<CustPlanMappping> custPlanMapppingList) {
        List<CustPlanMappingDropdownPojo> custPlanMappingDropdownPojos = new ArrayList<>();
        CustPlanMappingDropdownPojo custPlanMappingDropdownPojo = new CustPlanMappingDropdownPojo();
        for (CustPlanMappping custPlanMappping : custPlanMapppingList) {
            CustPlanMappingDropdownPojo custPlanMappingDropdownPojo1 = new CustPlanMappingDropdownPojo();
            custPlanMappingDropdownPojo1.setId(custPlanMappping.getId());
            custPlanMappingDropdownPojo1.setSerialNumber(custPlanMappping.getSerialNumber());
            custPlanMappingDropdownPojos.add(custPlanMappingDropdownPojo1);
        }
        return custPlanMappingDropdownPojos;
    }


    public List<CustomerPlansModel> getResultOfQuery(Customers customers) {
        List<CustomerPlansModel> customerPlansModels = new ArrayList<>();
        try {
            List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepo.findAllByCustomerIdAndIsDeleteIsFalse(customers.getId());
            List<CustPlanMappping> custPlanMapppingsWithoutVasList = custPlanMapppingList.stream().filter(custPlanMappping -> custPlanMappping.getVasId() == null).collect(Collectors.toList());
            for (CustPlanMappping mappping : custPlanMapppingsWithoutVasList) {
                CustomerPlansModel customerPlansModel = new CustomerPlansModel();
//                PostpaidPlan plan = postpaidPlanRepo.findById(mappping.getPlanId()).orElse(null);
//                PlanService services = planServiceRepository.findById(plan.getServiceId()).orElse(null);
                PlanService services = null;
                PostpaidPlan plan = null;
                if (mappping.getPlanId() != null) {
                    plan = postpaidPlanService.get(mappping.getPlanId(), customers.getMvnoId());
                }
                if (plan != null) {
                    services = planServiceService.get(plan.getServiceId(), customers.getMvnoId());
                }
                customerPlansModel.setCustId(customers.getId());
                customerPlansModel.setRenewalId(mappping.getRenewalId());
                customerPlansModel.setPlanId(plan != null ? plan.getId() : null);
                customerPlansModel.setPlanName(plan != null ? plan.getName() : null);
                customerPlansModel.setServiceId(plan != null ? plan.getServiceId() : null);
                customerPlansModel.setService(services != null ? services.getName() : "");
                customerPlansModel.setPlangroup(plan != null ? plan.getPlanGroup() : null);
                customerPlansModel.setPlanmapid(mappping.getId());
                customerPlansModel.setRenewalForBooster(mappping.getRenewalForBooster());
                if (plan != null && plan.getOfferprice() != null) {
                    customerPlansModel.setOfferPrice(plan != null ? plan.getOfferprice() : null);
                }


                customerPlansModel = setMaxHoldDayAndAttempt(customers.getId(), customerPlansModel, customers.getMvnoId());/**Set new cust hold days and attempt**/


                customerPlansModel.setIsHold(mappping.getIsHold());
                customerPlansModel.setIsVoid(mappping.getIsVoid());
                customerPlansModel.setDebitdocid(mappping.getDebitdocid());
                customerPlansModel.setCustomerServiceMappingId(mappping.getCustServiceMappingId());
                customerPlansModel.setIsdeleteforVoid(mappping.getIsDelete());
                customerPlansModel.setCustPlanMapppingId(mappping.getId());

                customerPlansModel.setValidity(plan != null ? plan.getValidity() : null);
                customerPlansModel.setStartDate(new java.sql.Date(Timestamp.valueOf(mappping.getStartDate()).getTime()));
                customerPlansModel.setStartDateString(new java.sql.Date(Timestamp.valueOf(mappping.getStartDate()).getTime()));
                customerPlansModel.setExpiryDate(new java.sql.Date(Timestamp.valueOf(mappping.getExpiryDate()).getTime()));
                customerPlansModel.setExpiryDateString(new java.sql.Date(Timestamp.valueOf(mappping.getExpiryDate()).getTime()));
                if (mappping.getEndDate() != null)
                    customerPlansModel.setEndDate(new java.sql.Date(Timestamp.valueOf(mappping.getEndDate()).getTime()));
                customerPlansModel.setCustPlanStatus(mappping.getCustPlanStatus());
                customerPlansModel.setIstrialplan(mappping.getIstrialplan());
                customerPlansModel.setPromiseToPayDays(mappping.getGraceDays());
                if (mappping.getPromise_to_pay_startdate() != null)
                    customerPlansModel.setPromiseToPayStartDate(new java.sql.Date(Timestamp.valueOf(mappping.getPromise_to_pay_startdate()).getTime()));
                if (mappping.getPromise_to_pay_enddate() != null)
                    customerPlansModel.setPromiseToPayEndDate(new java.sql.Date(Timestamp.valueOf(mappping.getPromise_to_pay_enddate()).getTime()));
                customerPlansModel.setPromiseToPayCount(mappping.getPromisetopay_renew_count());
                customerPlansModel.setServiceStartRemarks(mappping.getServiceStartRemarks());
                customerPlansModel.setServiceHoldRemarks(mappping.getServiceHoldRemarks());
                customerPlansModel.setServiceStartBy(mappping.getServiceStartBy());
                customerPlansModel.setServiceHoldBy(mappping.getServiceHoldBy());
                customerPlansModel.setIsinvoicestop(customers.getIsinvoicestop());
                if (mappping.getPlanGroup() != null)
                    customerPlansModel.setPlangroupid(mappping.getPlanGroup().getPlanGroupId());
                if (mappping.getStopServiceDate() != null)
                    customerPlansModel.setStopServiceDate(new java.sql.Date(Timestamp.valueOf(mappping.getStopServiceDate().atStartOfDay()).getTime()));
                if (mappping.getStartDate().isBefore(LocalDateTime.now()) && mappping.getEndDate().isAfter(LocalDateTime.now()) || mappping.getCustPlanStatus().equalsIgnoreCase("INGRACE"))
                    customerPlansModel.setPlanstage("ACTIVE");
                if ((mappping.getStartDate().isBefore(LocalDateTime.now()) && mappping.getEndDate().isBefore(LocalDateTime.now())) || (mappping.getCustPlanStatus().equalsIgnoreCase("Suspend") || mappping.getCustPlanStatus().equalsIgnoreCase("Terminate")  || mappping.getCustPlanStatus().equalsIgnoreCase("STOP")))
                    customerPlansModel.setPlanstage("EXPIRED");
                if (mappping.getStartDate().isAfter(LocalDateTime.now()) && mappping.getEndDate().isAfter(LocalDateTime.now()))
                    customerPlansModel.setPlanstage("FUTURE");
                if (customerPlansModel.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD)) {
                    customerPlansModel.setPlanstage(customerPlansModel.getCustPlanStatus());
                }


                if (mappping.getQospolicy() != null) {
                    customerPlansModel.setQosPolicyName(mappping.getQospolicy().getName());
                    customerPlansModel.setQosPolicyId(mappping.getQospolicy().getId());
                    customerPlansModel.setQosSpeed(mappping.getQospolicy().getQosspeed());
                }
//                PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(mappping.getPlanId()).orElse(null);
                customerPlansModel.setIsAllowOverUsage(plan != null ? plan.getAllowOverUsage() : null);


                if (mappping.getQuotaList() != null && !mappping.getQuotaList().isEmpty()) {
                    if (mappping.getQuotaList().get(0).getQuotaType() != null)
                        customerPlansModel.setQuotaType(mappping.getQuotaList().get(0).getQuotaType());
                    if (mappping.getQuotaList().get(0).getUsedQuota() != null)
                        customerPlansModel.setVolUsedQuota(mappping.getQuotaList().get(0).getUsedQuota().toString());
                    if (mappping.getQuotaList().get(0).getTotalQuota() != null)
                        customerPlansModel.setVolTotalQuota(mappping.getQuotaList().get(0).getTotalQuota().toString());
                    if (mappping.getQuotaList().get(0).getQuotaUnit() != null)
                        customerPlansModel.setVolQuotaUnit(mappping.getQuotaList().get(0).getQuotaUnit());
                    if (mappping.getQuotaList().get(0).getTimeQuotaUsed() != null)
                        customerPlansModel.setTimeUsedQuota(mappping.getQuotaList().get(0).getTimeQuotaUsed().toString());
                    if (mappping.getQuotaList().get(0).getTimeQuotaUsed() != null)
                        customerPlansModel.setTimeTotalQuota(mappping.getQuotaList().get(0).getTimeTotalQuota().toString());
                    if (mappping.getQuotaList().get(0).getTimeQuotaUnit() != null)
                        customerPlansModel.setTimeQuotaUnit(mappping.getQuotaList().get(0).getTimeQuotaUnit());
                    if (mappping.getQuotaList().get(0).getTotalReservedQuota() != null) {
                        customerPlansModel.setTotalReserve(mappping.getQuotaList().get(0).getTotalReservedQuota());
                    } else {
                        customerPlansModel.setTotalReserve(0.0);
                    }
                    if (mappping.getQuotaList().get(0).getIsChunkAvailable() != null && mappping.getQuotaList().get(0).getIsChunkAvailable()) {
                        customerPlansModel.setIsChunkAvailable(mappping.getQuotaList().get(0).getIsChunkAvailable());
                    } else {
                        customerPlansModel.setIsChunkAvailable(false);
                        customerPlansModel.setTotalReserve(0.0);
                    }
                }
                customerPlansModels.add(customerPlansModel);
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            throw ex;
        }
        return customerPlansModels;
    }

    public List<CustomerPlansModel> getActivePlanQuota(Integer custId) {
        List<CustomerPlansModel> customerPlansModels = new ArrayList<>();
        try {
            CustomerPlansModel customerPlansModel = new CustomerPlansModel();
            List<Object[]> activePlanObjectList = custPlanMappingRepository.getActivePlanQuotaByCustId(custId);
            for (Object[] activePlan : activePlanObjectList) {
                Integer planId = (Integer) activePlan[0];
                Double totalQuota = (Double) activePlan[1];
                String quotaType = (String) activePlan[2];
                Double usedQuota = (Double) activePlan[3];
                Double totalReserveQuota = (Double) activePlan[4];
                Boolean isChunkAvailable = (Boolean) activePlan[5];
                LocalDateTime endDate = (LocalDateTime) activePlan[6];
                LocalDateTime startDate = (LocalDateTime) activePlan[7];
                Double timeTotalQuota = (Double) activePlan[8];
                Double timeUsedQuota = (Double) activePlan[9];
                PostpaidPlan plan = postpaidPlanRepo.findById(planId).orElse(null);
                if (plan != null) {
                    customerPlansModel.setIsAllowOverUsage(plan.getAllowOverUsage());
                }
                if (isChunkAvailable) {
                    customerPlansModel.setIsChunkAvailable(isChunkAvailable);
                    customerPlansModel.setTotalReserve(totalReserveQuota);
                } else {
                    customerPlansModel.setIsChunkAvailable(false);
                    customerPlansModel.setTotalReserve(0.0);
                }
                customerPlansModel.setVolTotalQuota(totalQuota.toString());
                customerPlansModel.setVolUsedQuota(usedQuota.toString());
                customerPlansModel.setQuotaType(quotaType);
                customerPlansModel.setTimeUsedQuota(timeUsedQuota.toString());
                customerPlansModel.setTimeTotalQuota(timeTotalQuota.toString());
                if (endDate.isAfter(LocalDateTime.now())) {
                    if (startDate.isBefore(LocalDateTime.now())) {
                        customerPlansModel.setPlanstage("ACTIVE");
                    } else {
                        customerPlansModel.setPlanstage("FUTURE");
                    }
                } else {
                    customerPlansModel.setPlanstage("EXPIRED");
                }
                customerPlansModels.add(customerPlansModel);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            throw ex;
        }
        return customerPlansModels;
    }


    public CustAccountProfile fetchSubscriberAccountProfileDetail(Long profileId) {
        CustAccountProfile custAccountProfile = custAccountProfileRepository.findByProfileId(profileId).orElse(null);
        return custAccountProfile;
    }


    @Transactional
    public CustomersPojo saveSubscriberFromForm(CustomersPojo subscriber, String requestFrom, boolean isCustomerUpdate) throws Exception {
        String SUBMODULE = MODULE + " [saveSubscriber()] ";
        LocalDateTime currentTime = LocalDateTime.now();
        final CustomersPojo tempCustomerPojo = subscriber;
        //subscriber=getLocationmapping(subscriber);
        String planName = null;
        Long validity = null;
        Customers parentCustomer = null;
        double discount = subscriber.getDiscount();
        Double trialPlanPeirodConstant;
        ClientService trialClientService = clientServiceSrv.getByNameAndMvnoIdEquals(com.adopt.apigw.constants.Constants.TRIAL_PLAN_PERIOD_THRESHOLD, subscriber.getMvnoId());
        if (trialClientService != null) {
            trialPlanPeirodConstant = Double.valueOf(trialClientService.getValue());
        } else {
            trialPlanPeirodConstant = 0d;
        }
        Integer tpValidityCount;
        ClientService noOfTimeTrialclientService = clientServiceSrv.getByNameAndMvnoIdEquals(com.adopt.apigw.constants.Constants.ALLOW_NUMBER_OF_TIME_TRAIL, subscriber.getMvnoId());
        if (noOfTimeTrialclientService != null) {
            tpValidityCount = Integer.valueOf(noOfTimeTrialclientService.getValue());
        } else {
            tpValidityCount = 0;
        }
        BusinessUnit businessUnit = new BusinessUnit();
        if (subscriber.getBuId() != null) {
            businessUnit = businessUnitRepository.findById(subscriber.getBuId()).get();
        } else if (subscriber.getBuId() == null) {
            businessUnit.setPlanBindingType("");
        }
        if (subscriber.getBuId() == null) {

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

        HashMap<Integer, Boolean> mapforTrialPlan = new HashMap<Integer, Boolean>();
        try {
            if (getBUIdsFromCurrentStaff().size() == 1) subscriber.setBuId(subscriber.getBuId());
            Integer plangroupId = subscriber.getPlangroupid();
            boolean isDirectChargeApplied = false;
            if (null != subscriber && subscriber.getId() == null) {
                if (subscriber.getIsCustCaf() != null && !"".equals(subscriber.getIsCustCaf())) {
                    if (subscriber.getIsCustCaf().equalsIgnoreCase("yes")) {
                        subscriber.setStatus(SubscriberConstants.NEW_ACTIVATION);
                    } else subscriber.setStatus(subscriber.getStatus());
                }
//                if( subscriber.getMvnoId()==getMvnoIdFromCurrentStaff()) {
//                    if (getMvnoIdFromCurrentStaff() != null)
//                        subscriber.setMvnoId(getMvnoIdFromCurrentStaff());
//                }

                if (subscriber.getParentCustomerId() != null)
                    parentCustomer = customersRepository.getOne(subscriber.getParentCustomerId());
                Long profileId = mvnoRepository.findProfileIdByMvnoId(Long.valueOf(subscriber.getMvnoId())).orElse(null);
                if (profileId != null) {
                    CustAccountProfile custAccountProfile = fetchSubscriberAccountProfileDetail(profileId);
                    if (subscriber.getAcctno() == null || subscriber.getAcctno().trim().isEmpty()) {
                        // TODO: pass mvnoID manually 6/5/2025
                        subscriber.setAcctno(acctNumCreationService.getNewCustomerAccountNo(custAccountProfile, subscriber.getMvnoId()));
                    }
                }
                if (Boolean.TRUE.equals(subscriber.getIsCredentialMatchWithAccountNo())) {
                    subscriber.setUsername(subscriber.getAcctno());
                    subscriber.setPassword(subscriber.getAcctno());
                    subscriber.setLoginUsername(subscriber.getAcctno());
                } else {
                    subscriber.setPassword(subscriber.getPassword());
                }
                if (subscriber.getIsPasswordAutoGenerated() != null && Boolean.TRUE.equals(subscriber.getIsPasswordAutoGenerated())) {
                    subscriber.setPassword(toolService.generateRandomPassword());  /**Random password set here **/
                    subscriber.setLoginPassword(toolService.generateRandomPassword());
                }
                subscriber.setSelfcarepwd(subscriber.getPassword());
                subscriber.setCusttype(subscriber.getCusttype());
                subscriber.setPopid(subscriber.getPopid());
                subscriber.setStaffId(subscriber.getStaffId());

                if (subscriber.getBranch() != null) subscriber.setBranch(subscriber.getBranch());

                List<CreditDocumentPojo> creditDocumentList = new ArrayList<>();
                List<CustPlanMapppingPojo> planMappingList = new ArrayList<>();
//                if (subscriber.getLeadId() != null) {
//                    LeadMaster leadMaster = leadMasterRepository.findById(subscriber.getLeadId()).orElse(null);
//                    if (leadMaster != null && leadMaster.getLeadSource() != null && leadMaster.getLeadSource().getLeadSourceName() != null)
//                        subscriber.setLeadSource(leadMaster.getLeadSource().getLeadSourceName());
//                }
                if (plangroupId != null && plangroupId != 0) {
                    PlanGroup planGroup = planGroupService.findPlanGroupById(plangroupId, subscriber.getMvnoId());
                    if (planGroup == null) {
                        throw new IllegalArgumentException("No record found for Plan Group with id : '" + plangroupId + "'. Or you are not authorised to update/delete this record.");
                    }
                    List<PlanGroupMapping> planGroupMappingList = planGroupMappingService.findPlanGroupMappingByPlanGroupId(plangroupId, subscriber.getMvnoId());
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
                                    else planMapping.setBillTo(com.adopt.apigw.constants.Constants.CUSTOMER);

                                    if (custPlanMapppingPojo.get().getIsInvoiceToOrg() != null)
                                        planMapping.setIsInvoiceToOrg(custPlanMapppingPojo.get().getIsInvoiceToOrg());
                                    else planMapping.setIsInvoiceToOrg(false);

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
                    Customers customers = customerMapper.dtoToDomain(subscriber, new CycleAvoidingMappingContext());
                    LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers, customers.getNextBillDate());
                    if (nextQuotaReset != null) {
                        customers.setNextQuotaResetDate(nextQuotaReset);
                    } else {
                        customers.setNextQuotaResetDate(LocalDate.now());
                    }
                    CustPlanMapppingPojo mapping;
                    LocalDateTime parentExpiryDate = null;
                    if (null != subscriber || subscriber.getCustlabel().equalsIgnoreCase("organization")) {
                        if (subscriber.getPlanMappingList() != null && subscriber.getPlanMappingList().size() > 0) {
                            for (int i = 0; i < subscriber.getPlanMappingList().size(); i++) {
                                parentExpiryDate = null;
                                if (subscriber.getPlanMappingList().size() > 1)
                                    mapping = customersService.getSinglePlan(subscriber, i);
                                else mapping = subscriber.getPlanMappingList().get(i);

                                if (mapping.getBillTo() == null) {
                                    mapping.setBillTo(com.adopt.apigw.constants.Constants.CUSTOMER);
                                    mapping.setIsInvoiceToOrg(false);
                                }

                                if (mapping.getIstrialplan() != null && mapping.getIstrialplan()) {
                                    mapping.setIsinvoicestop(true);
                                    subscriber.setIsinvoicestop(true);
                                    subscriber.setIstrialplan(true);
//                                    if (businessUnit.getPlanBindingType().equalsIgnoreCase("Predefined") || Objects.isNull(businessUnit.getPlanBindingType()) || (businessUnit.getPlanBindingType().equalsIgnoreCase("On-Demand"))) {
                                    subscriber.getPlanMappingList().forEach(custPlanMapppingPojo -> custPlanMapppingPojo.setIsTrialValidityDays(trialPlanPeirodConstant));
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
                                    PostpaidPlan plan = planService.get(mapping.getPlanId(), customers.getMvnoId());
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
                                        if (prorate_validity == 0) prorate_validity = 1L;
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
                                    paymentPojo.setMvnoId(subscriber.getMvnoId());
                                }
                            }
                            if (paymentPojo.getPaymode() != null && paymentPojo.getAmount() > 0) {
                                paymentPojo.setAmount(paymentPojo.getAmount());
                                paymentPojo.setPaymentdate(LocalDate.now());
                                Partner partner = partnerService.get(subscriber.getPartnerid(), subscriber.getMvnoId());
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
                                else creditDocument.setLcoId(null);

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
                                    creditDocument.setMvnoId(subscriber.getMvnoId());
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
                        Customers customers = customerMapper.dtoToDomain(subscriber, new CycleAvoidingMappingContext());
                        if (parentCustomer != null) {
                            LocalDate childMinNextBillDate = customersService.getNextBillDate(customers);
                            if (childMinNextBillDate.isAfter(parentCustomer.getNextBillDate()))
                                subscriber.setNextBillDate(parentCustomer.getNextBillDate());
                            else subscriber.setNextBillDate(childMinNextBillDate);
                        } else subscriber.setNextBillDate(customersService.getNextBillDate(customers));

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
                                PostpaidPlan plan = planService.get(mapping.getPlanId(), subscriber.getMvnoId());
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
                // Set Activation By name
                if (null != subscriber.getStatus() && subscriber.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS) && subscriber.getActivationByName() == null) {
                    StaffUser loggedStaffUser = staffUserService.get(getLoggedInUserId(), subscriber.getMvnoId());
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
                if (Objects.isNull(subscriber.getIsDunningEnable()) || isCustomerUpdate) {
                    subscriber.setIsDunningEnable(true);
                }
                if (Objects.isNull(subscriber.getIsNotificationEnable()) || isCustomerUpdate) {
                    subscriber.setIsNotificationEnable(true);
                }
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
                                StaffUser staffUser = staffUserService.get(staffId, subscriber.getMvnoId());
                                assignedUser = staffUser;
                                savedCustomer.setNextTeamHierarchyMapping(Integer.valueOf(map.get("nextTatMappingId")));
                                savedCustomer.setCurrentAssigneeId(staffId);
                                if (savedCustomer != null) {
                                    String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER + " with username : " + " ' " + subscriber.getUsername() + " '";
                                    hierarchyService.sendWorkflowAssignActionMessage(staffUser.getCountryCode(), staffUser.getPhone(), staffUser.getEmail(), staffUser.getMvnoId(), staffUser.getFullName(), action, staffUser.getId().longValue());
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
                                StaffUser currentStaff = staffUserService.get(getLoggedInUserId(), subscriber.getMvnoId());
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
                            if (!CollectionUtils.isEmpty(map) && map.containsKey("tat_id") && !map.get("tat_id").equals("null")) {
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
                                                minutes = matrix1.getRtime();

                                                LocalTime currentTimenow = LocalTime.now();
                                                LocalTime nextFollowupTime = currentTimenow.plusMinutes(minutes);
                                                if (nextFollowupTime.getHour() < currentTimenow.getHour() || (nextFollowupTime.getHour() == currentTimenow.getHour() && nextFollowupTime.getMinute() < currentTimenow.getMinute())) {
                                                    LocalDate nextFollowupDate = LocalDate.now().plusDays(1);

                                                    subscriber.setNextfollowupdate(nextFollowupDate);
                                                    subscriber.setNextfollowuptime(nextFollowupTime);
                                                } else {
                                                    subscriber.setNextfollowupdate(LocalDate.now());
                                                    subscriber.setNextfollowuptime(nextFollowupTime);
                                                }
                                            } else if (matrix1.getRunit().equalsIgnoreCase("HOUR")) {
                                                hours = matrix1.getRtime();
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
                                                subscriber.setNextfollowupdate(LocalDate.now().plusDays(matrix1.getRtime()));
                                            }

                                            if (staffUser.get().getStaffUserparent() != null && !CollectionUtils.isEmpty(map) && subscriber != null && !map.get("eventId").equals("0") && !map.get("eventId").equals(null)) {
                                                tatMapDetails.put("workFlowId", map.get("workFlowId").toString());
                                                tatMapDetails.put("eventId", map.get("eventId").toString());
                                                tatMapDetails.put("eventName", map.get("eventName").toString());
                                                tatMapDetails.put("tat_id", map.get("tat_id").toString());
                                                tatMapDetails.put("orderNo", map.get("orderNo").toString());

                                                TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails = new TatMatrixWorkFlowDetails(Long.valueOf("1"), "Level 1", staffUser.get().getId(), Long.valueOf(tatMapDetails.get("workFlowId")), Long.valueOf(tatMapDetails.get("tat_id")), (staffUser != null && staffUser.get().getStaffUserparent() != null) ? staffUser.get().getStaffUserparent().getId() : null, LocalDateTime.now(), matrix1.getRtime().toString(), matrix1.getRunit(), "Notification", true, tatMapDetails.get("nextTatMappingId") != null ? Integer.valueOf(tatMapDetails.get("nextTatMappingId")) : null, subscriber.getId(), tatMapDetails.get("eventName"), tatMapDetails.get("eventId") != null ? Integer.valueOf(tatMapDetails.get("eventId")) : null, CommonConstants.NOTIFICATION_TYPE_STAFF, null, true);
//                                               tatUtils.saveOrUpdateDataForTatMatrix( tatMapDetails, staffUser.get(),subscriber.getId(),null);
                                                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                                            }

                                        }
                                    }
                                }
                            }
                            subscriber.setId(savedCustomer.getId());
                            subscriber.setCurrentAssigneeId(getLoggedInUserId());
                            StaffUser currentStaff = staffUserService.get(getLoggedInUserId(), subscriber.getMvnoId());
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
                LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(savedCustomer, savedCustomer.getNextBillDate());
                if (nextQuotaReset != null) {
                    savedCustomer.setNextQuotaResetDate(nextQuotaReset);
                } else {
                    savedCustomer.setNextQuotaResetDate(LocalDate.now());
                }
                save(savedCustomer);
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
                        PostpaidPlan postpaidPlanObj = planService.get(planMapppingPojo.getPlanId(), subscriber.getMvnoId());
                        PostpaidPlanPojo plan = postpaidPlanMapper.domainToDTO(postpaidPlanObj, new CycleAvoidingMappingContext());
                        Optional<PlanService> services = planServiceRepository.findById(plan.getServiceId());
                        if (services.isPresent()) {
                            if (services.get().getExpiry() != null) {
                                if (services.get().getExpiry().equalsIgnoreCase(com.adopt.apigw.constants.Constants.AT_MIDNIGHT)) {
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
                                qosPolicy = qosPolicyService.getEntityForUpdateAndDelete(plan.getQospolicyid(), subscriber.getMvnoId());
                            }
                            if (!plan.getQuotatype().equalsIgnoreCase(CommonConstants.DID_QUOTA_TYPE) && !plan.getQuotatype().equalsIgnoreCase(CommonConstants.INTERCOM_QUOTA_TYPE) && !plan.getQuotatype().equalsIgnoreCase(CommonConstants.VOICE__BOTH_QUOTA_TYPE)) {
                                Double totalQuotaForSeconds = 0.0;
                                Double totalQuotaForKB = 0.0;
                                if (null != plan.getQuotaunittime() && plan.getQuotaunittime().equalsIgnoreCase(com.adopt.apigw.constants.Constants.MINUTE)) {
                                    totalQuotaForSeconds = plan.getQuotatime() * 60;
                                }
                                if (null != plan.getQuotaunittime() && plan.getQuotaunittime().equalsIgnoreCase(com.adopt.apigw.constants.Constants.HOUR)) {
                                    totalQuotaForSeconds = plan.getQuotatime() * 60 * 60;
                                }
                                if (null != plan.getQuotaUnit() && plan.getQuotaUnit().equalsIgnoreCase(com.adopt.apigw.constants.Constants.MB)) {
                                    totalQuotaForKB = (double) plan.getQuota() * 1024;
                                }
                                if (null != plan.getQuotaUnit() && plan.getQuotaUnit().equalsIgnoreCase(com.adopt.apigw.constants.Constants.GB)) {
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
                                planMapppingPojo.setQuotaList(Collections.singletonList(quotaDetails));
                                planMapppingPojo.setPlanValidityDays(customersService.calculatePlanValidityDays(subscriber, postpaidPlanObj, validity, LocalDateTime.now()));
                                planMapppingPojo.setPlangroupid(plangroupId);
                                CustomerServiceMapping mapping = new CustomerServiceMapping(planMapppingPojo);

                                mapping.setServiceId(Long.valueOf(postpaidPlanObj.getServiceId()));
                                mapping.setCustId(subscriber.getId());
//                                mapping = generateConnectionNumber(mapping);
                                Boolean isLCO = subscriber.getLcoId() != null;
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
                                // fix for provicinal portal caf creationn issue
                                if (savedCustomer != null) {
                                    mapping.setCustomer(savedCustomer);
                                    mapping.setMvnoId(savedCustomer.getMvnoId());
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
                                                assignedStaff = staffUserService.get(staffId, subscriber.getMvnoId());
                                                mapping.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                                mapping.setNextStaff(staffId);
                                                if (mapping != null) {
                                                    String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER_SERVICE_ADD + " with for customer : " + " ' " + savedCustomer.getUsername() + " ' ";
                                                    hierarchyService.sendWorkflowAssignActionMessage(assignedStaff.getCountryCode(), assignedStaff.getPhone(), assignedStaff.getEmail(), assignedStaff.getMvnoId(), assignedStaff.getFullName(), action, assignedStaff.getId().longValue());

                                                }
                                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, mapping.getId(), mapping.getConnectionNo(), staffId, assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                                                if (assignedStaff.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                                                    if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                                                        map.put("tat_id", map.get("current_tat_id"));
                                                    tatUtils.saveOrUpdateDataForTatMatrix(map, assignedStaff, mapping.getId(), null);
                                                }
                                            } else {
                                                StaffUser currentStaff = staffUserService.get(getLoggedInUserId(), subscriber.getMvnoId());
                                                assignedStaff = currentStaff;
                                                mapping.setNextTeamHierarchyMappingId(null);
                                                mapping.setNextStaff(currentStaff.getId());
                                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, mapping.getId(), mapping.getConnectionNo(), staffId, currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                            }
                                        } else {
                                            StaffUser currentStaff = staffUserService.get(getLoggedInUserId(), subscriber.getMvnoId());
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
                                customersService.saveCustomerChargeHistory(postpaidPlanObj, subscriber, custPlanMappping, plangroupId, true, null, null);

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
                                Boolean isLCO = subscriber.getLcoId() != null;
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
                                                assignedStaff = staffUserService.get(staffId, subscriber.getMvnoId());
                                                mapping.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                                mapping.setNextStaff(staffId);
                                                if (mapping != null) {
                                                    String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER_SERVICE_ADD + " with for customer : " + " ' " + savedCustomer.getUsername() + " ' ";
                                                    hierarchyService.sendWorkflowAssignActionMessage(assignedStaff.getCountryCode(), assignedStaff.getPhone(), assignedStaff.getEmail(), assignedStaff.getMvnoId(), assignedStaff.getFullName(), action, assignedStaff.getId().longValue());

                                                }
                                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, mapping.getId(), mapping.getConnectionNo(), staffId, assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                                                if (assignedStaff.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                                                    if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                                                        map.put("tat_id", map.get("current_tat_id"));
                                                    tatUtils.saveOrUpdateDataForTatMatrix(map, assignedStaff, mapping.getId(), null);
                                                }
                                            } else {
                                                StaffUser currentStaff = staffUserService.get(getLoggedInUserId(), subscriber.getMvnoId());
                                                assignedStaff = currentStaff;
                                                mapping.setNextTeamHierarchyMappingId(null);
                                                mapping.setNextStaff(currentStaff.getId());
                                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, mapping.getId(), mapping.getConnectionNo(), staffId, currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                            }
                                        } else {
                                            StaffUser currentStaff = staffUserService.get(getLoggedInUserId(), subscriber.getMvnoId());
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
                                customersService.saveCustomerChargeHistory(postpaidPlanObj, subscriber, custPlanMappping, plangroupId, true, null, null);
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

            } else {
                Customers customers = get(subscriber.getId(), subscriber.getMvnoId());

                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) != null) {
                    // TODO: pass mvnoID manually 6/5/2025
                    subscriber.setMvnoId(getMvnoIdFromCurrentStaff(null));
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
                subscriber.setNextQuotaResetDate(customers.getNextQuotaResetDate());
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
                            mapping.setPlanValidityDays(customersService.calculatePlanValidityDays(subscriber, planService.get(entityById.getPlanId(), subscriber.getMvnoId()), validity, LocalDateTime.now()));

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
                                PostpaidPlan plan = planService.get(mapping.getPlanId(), subscriber.getMvnoId());
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
                    subscriber.setIs_from_pwc(requestFrom.equalsIgnoreCase("pw"));

                    StaffUser assignedUser;
                    if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
                        Map<String, String> map1 = hierarchyService.getTeamForNextApproveForAuto(subscriber.getMvnoId(), subscriber.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, CommonConstants.HIERARCHY_TYPE, false, true, subscriber);
                        int staffId = 0;
                        if (map1.containsKey("staffId") && map1.containsKey("nextTatMappingId")) {
                            staffId = Integer.parseInt(map1.get("staffId"));
                            StaffUser staffUser = staffUserService.get(staffId, subscriber.getMvnoId());
                            assignedUser = staffUser;
                            subscriber.setNextTeamHierarchyMapping(Integer.valueOf(map1.get("nextTatMappingId")));
                            subscriber.setCurrentAssigneeId(staffId);
                            if (subscriber != null) {
                                String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER + " with username : " + " ' " + subscriber.getUsername() + " '";
                                hierarchyService.sendWorkflowAssignActionMessage(staffUser.getCountryCode(), staffUser.getPhone(), staffUser.getEmail(), staffUser.getMvnoId(), staffUser.getFullName(), action, assignedUser.getId().longValue());
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
                            StaffUser currentStaff = staffUserService.get(getLoggedInUserId(), subscriber.getMvnoId());
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
                        StaffUser currentStaff = staffUserService.get(getLoggedInUserId(), subscriber.getMvnoId());
                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CAF, subscriber.getId(), subscriber.getUsername(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                    }
                }
            }

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

            LocalDateTime localDateTime = LocalDateTime.now();
            StringBuilder planNamesBuilder = new StringBuilder();
            if (customersPojo.getPlanMappingList() != null) {
                for (CustPlanMapppingPojo mappping : customersPojo.getPlanMappingList()) {
                    String planname = postpaidPlanRepo.findNameById(mappping.getPlanId());
                    if (!planNamesBuilder.toString().contains(planname)) {
                        planNamesBuilder.append(planname).append(", ");
                    }
                }
            }
            customersService.sendCustApprovalRegistrationMessage(customersPojo.getId(), localDateTime.toString(), planNamesBuilder.toString(), customersPojo.getUsername(), customersPojo.getPassword(), customersPojo.getCountryCode(), customersPojo.getMobile(), customersPojo.getEmail(), customersPojo.getMvnoId(), customersPojo.getStatus(), subscriber.getAcctno(), (long) customersService.getLoggedInStaffId(), customersPojo.getLoginUsername(), customersPojo.getLoginPassword());
            //send message
            if (customersPojo.getStatus() != null && !customersPojo.getStatus().equalsIgnoreCase("NewActivation")) {
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
                kafkaMessageSender.send(new KafkaMessageData(customerMessage, CustomerMessage.class.getSimpleName()));
//                this.messageSender.send(customerMessage, RabbitMqConstants.QUEUE_APIGW_SEND_CUSTOMER);
            }

            if (subscriber.getOverChargeList() != null && !subscriber.getOverChargeList().isEmpty() && subscriber.getOverChargeList().get(0).getBillTo().equalsIgnoreCase(com.adopt.apigw.constants.Constants.ORGANIZATION) && subscriber.getOverChargeList().get(0).getIsInvoiceToOrg()) {
                customersService.saveDirectChargeDataForOrganizationCustomer(subscriber, subscriber.getCusttype());
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

    public List<CustomerPlansModel> getActiveAddonPlanList(Integer custId, Boolean isNotChangePlan) {
        Customers customers = customersRepository.findById(custId).orElse(null);
        //List<CustomerPlansModel> customerPlansModelList = getCustomerPlanList(custId, isNotChangePlan);
        List<CustomerPlansModel> customerPlansModelList = getCustomerPlanList(customers, isNotChangePlan);
        if (null != customerPlansModelList && 0 < customerPlansModelList.size()) {
            List<CustomerPlansModel> list = customerPlansModelList.stream().filter(dto -> null != dto.getPlangroup() && (dto.getPlangroup().equalsIgnoreCase(StatusConstants.ADDON_PLAN.ADD_ON) || dto.getPlangroup().equalsIgnoreCase((StatusConstants.ADDON_PLAN.BANDWIDTHBOOSTER))) || dto.getPlangroup().equalsIgnoreCase(StatusConstants.ADDON_PLAN.VOLUME_BOOSTER)).collect(Collectors.toList());

            return list;
        }
        return new ArrayList<>();
    }

    public Map<String, String> customerCreateFromXLS(List<CustomersMigrationPojo> pojos, Integer mvnoId, HttpServletRequest req) throws IOException {
        Map<String, String> map = new HashMap<>();
        List<Integer> mvnoIdList = new ArrayList<>();
        // TODO: pass mvnoID manually 6/5/2025
        if (getLoggedInMvnoId() != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            mvnoIdList.add(getLoggedInMvnoId());
        }
        mvnoIdList.add(1);
        try {

            for (CustomersMigrationPojo custpojo : pojos) {
                if (!custpojo.getUsername().trim().isEmpty()) {
                    Customers cust = new Customers();
                    cust.setParentExperience(null);
                    cust.setCusttype(custpojo.getCusttype());
                    cust.setTitle(custpojo.getTitle());
                    cust.setFirstname(custpojo.getFirstname());
                    cust.setLastname(custpojo.getLastname());
                    cust.setUsername(custpojo.getUsername());
                    cust.setPassword(custpojo.getPassword());
                    cust.setCountryCode(custpojo.getCountryCode());
                    cust.setMobile(custpojo.getPrimaryMobile());
                    cust.setAltmobile(custpojo.getSecondaryMobile());
                    cust.setFax(custpojo.getFax());
                    cust.setPhone(custpojo.getTelephone());
                    cust.setEmail(custpojo.getEmail());
                    cust.setPan(custpojo.getPan());
                    cust.setContactperson(custpojo.getContactperson());
                    cust.setCalendarType(custpojo.getCalendarType());
                    cust.setCustcategory(custpojo.getCustomerCategory());
                    cust.setMvnoId(custpojo.getMvnoId());
                    if (custpojo.getParentExperience() != null) {
                        cust.setParentExperience(custpojo.getParentExperience());
                    } else {
                        cust.setParentExperience(null);
                    }
//                cust.setCDCustomerType(custpojo);
//                cust.setCDCustomerSubType(custpojo);
                    cust.setCustomerSector(custpojo.getCustomerSector());
                    cust.setCustomerSubSector(custpojo.getCustomerSubSector());
                    cust.setCafno(custpojo.getCafno());
                    cust.setBirthDate(parseDateTime(custpojo.getDOB()));
                    cust.setBillday(custpojo.getBillday());
                    cust.setStatus(custpojo.getStatus());
//                cust.setDedicatedStaffUserName(custpojo);
                    List<CustomerAddress> addressList = new ArrayList<>();
                    CustomerAddress customerAddress = new CustomerAddress();
                    customerAddress.setCustomer(cust);
                    customerAddress.setAddressType("new");
                    customerAddress.setVersion("New");
                    customerAddress.setAddress1(custpojo.getAddress());
                    customerAddress.setLandmark(custpojo.getLandmark());
                    if (custpojo.getParentCustomer().length() > 0) {
                        try {
                            // TODO: pass mvnoID manually 6/5/2025
                            Optional<Customers> customers = customersRepository.findByUsernameAndMvnoId(custpojo.getParentCustomer(), getMvnoIdFromCurrentStaff(null));
                            cust.setParentCustomers(customers.get());
                            cust.setParentCustomersId(customers.get().getId());
                        } catch (Exception e) {
                            map.put(cust.getUsername(), e.getMessage());
                            continue;
                        }
                    }

                    cust.setCustomerType(custpojo.getCustomerType());
                    cust.setSalesremark(custpojo.getSalesremark());
                    if (!custpojo.getParentExperience().trim().isEmpty()) {
                        cust.setParentExperience(custpojo.getParentExperience());
                    } else {
                        cust.setParentExperience(null);
                    }
//                cust.setParentExperience(custpojo.getParentExperience());
                    try {
                        Optional<ServiceArea> serviceArea = serviceAreaRepository.findAllByNameAndMvnoIdIn(custpojo.getServiceArea(), mvnoIdList);
                        cust.setServicearea(serviceArea.get());
                        customerAddress.setCityId(serviceArea.get().getCityid().intValue());
                        Optional<City> city = cityRepository.findById(serviceArea.get().getCityid().intValue());
                        Optional<Country> country = countryRepository.findById(city.get().getCountryId());
                        customerAddress.setCity(city.get());
                        customerAddress.setCityId(city.get().getId());
                        customerAddress.setCountry(country.get());
                        customerAddress.setCountryId(country.get().getId());
                        customerAddress.setState(city.get().getState());
                        customerAddress.setAddressType(SubscriberConstants.CUST_ADDRESS_PRESENT);
                        customerAddress.setIsDelete(false);

                        Optional<Pincode> pincode = pincodeRepository.findByPincodeAndMvnoIdIn(custpojo.getMunicipality(), mvnoIdList);
                        customerAddress.setPincodeId(pincode.get().getId().intValue());
                        customerAddress.setPincode(pincode.get());
                        cust.setPincode(pincode.get().getId().intValue());

                        if (custpojo.getAreaName() != null) {
                            try {
                                Optional<Area> area = areaRepository.findAllByNameEqualsIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(custpojo.getWard(), mvnoIdList);
                                customerAddress.setArea(area.get());
                                customerAddress.setAreaId(area.get().getId().intValue());
                            } catch (Exception e) {
                                map.put(cust.getUsername(), e.getMessage());
                                continue;
                            }

                        } else {
                            List<Area> area = areaRepository.findAreasByPincode(pincode.get().getId());
                            customerAddress.setArea(area.get(0));
                            customerAddress.setAreaId(area.get(0).getId().intValue());
                        }


                    } catch (Exception e) {
                        map.put(cust.getUsername(), e.getMessage());
                        continue;
                    }
                    if (custpojo.getBranchName().length() > 0) {
                        try {
                            Optional<Branch> branch = branchRepository.getAllByNameEqualsAndMvnoIdIn(custpojo.getBranchName(), mvnoIdList);

                            if (branch.isPresent()) {
                                cust.setBranch(branch.get().getId());
                            } else if (custpojo.getPartner() !=null &&!custpojo.getPartner().trim().isEmpty() ) {
                                Optional<Partner> partner = partnerRepository.findByNameAndMvnoIdIn(custpojo.getPartner(), mvnoIdList);
                                if (partner.isPresent()) {
                                    cust.setPartner(partner.get());
                                    cust.setPartnerName(partner.get().getName());

                                }
                            }
                        } catch (Exception e) {
                            map.put(cust.getUsername(), e.getMessage());
                            continue;
                        }
                    }


                    addressList.add(customerAddress);

                    cust.setAddressList(addressList);
                    cust.setValleyType(custpojo.getValleyType());
                    cust.setLatitude(custpojo.getLatitude());
                    cust.setLongitude(custpojo.getLongitude());
//                    if (custpojo.getPOP().length() > 0) {
//                        cust.setPopid(Long.parseLong(custpojo.getPOP()));
//                    }
//                    if (custpojo.getOLT().length() > 0) {
//                        cust.setOltid(Long.parseLong(custpojo.getOLT()));
//                    }
//                    if (custpojo.getSplitterDB().length() > 0) {
//                        cust.setSplitterid(Long.parseLong(custpojo.getSplitterDB()));
//                    }
//                    cust.setMasterdbid(custpojo.getMasterdbid());

//                cust.setIpv6(custpojo);
                    cust.setNasIpAddress(custpojo.getNasIpAddress());
                    cust.setNasPort(custpojo.getNasPort());
                    cust.setIpPoolNameBind(custpojo.getIpPoolNameBind());
                    if (custpojo.getEarlybillday() != null) {
                        cust.setEarlybillday(custpojo.getEarlybillday());
                    }
                    if (custpojo.getPlanGroupName().length() > 0) {
                        try {
                            PlanGroup planGroup = planGroupRepository.findByPlanGroupNameEqualsAndMvnoIdInAndIsDelete(custpojo.getPlanGroupName(), mvnoIdList, false);
                            cust.setPlangroup(planGroup);
                        } catch (Exception e) {
                            map.put(cust.getUsername(), e.getMessage());
                            continue;
                        }
                    }
                    List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
                    cust.setInvoiceType(custpojo.getInvoiceType());

                    for (String name : custpojo.getPlannameList()) {
                        CustPlanMappping custPlanMappping = new CustPlanMappping();
                        custPlanMappping.setCustomer(cust);
                        custPlanMappping.setBillTo(custpojo.getBillTo());
                        Optional<PostpaidPlan> plan = postpaidPlanRepo.findAllByNameEqualsAndMvnoIdIn(name, mvnoIdList);
                        custPlanMappping.setPlanId(plan.get().getId());
                        custPlanMappping.setPostpaidPlan(plan.get());
//                    custPlanMappping.setBillableTo(custpojo.getBillableTo());
                        custPlanMappping.setStatus("Active");
                        custPlanMappping.setIsDelete(false);
                        custPlanMappping.setBillableCustomerId(custpojo.getBillableCustomerId());
                        if (custpojo.getInvoiceToOrganization().length() > 0) {
                            custPlanMappping.setIsInvoiceToOrg(Boolean.parseBoolean(custpojo.getInvoiceToOrganization()));
                        }
                        custPlanMappping.setCustPlanStatus(CommonConstants.ACTIVE_STATUS);

                        custPlanMappping.setDiscountType(custpojo.getDiscountType());
                        custPlanMappping.setDiscount(custpojo.getDiscount());
                        custPlanMappping.setDiscountExpiryDate(parseDate(custpojo.getDExpiryDate()));
//                    customersMigrationPojo.setPlanName(dataFormatter.formatCellValue(row.getCell(51)));
                        if (custpojo.getNewPriceWithDiscount().length() > 0) {
                            custPlanMappping.setNewAmount(Double.parseDouble(custpojo.getNewPriceWithDiscount()));
                        }

                        custPlanMappping.setService(custpojo.getServiceName());

                        cust.setPlanName(name);
                        custPlanMapppingList.add(custPlanMappping);
                    }
                    cust.setPlanMappingList(custPlanMapppingList);
                    List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
                    CustomerServiceMapping customerServiceMapping = new CustomerServiceMapping();
                    try {
                        PlanService planService = planServiceRepository.findAllByNameEqualsAndMvnoIdIn(custpojo.getServiceName(), mvnoIdList);
                        customerServiceMapping.setServiceId(planService.getId().longValue());

                        customerServiceMapping.setStatus("Active");
                        customerServiceMapping.setDiscountExpiryDate(parseDate(custpojo.getDExpiryDate()));
                        customerServiceMapping.setBillingCycle(custpojo.getBillday().toString());
                        customerServiceMapping.setDiscount(custpojo.getDiscount());
                        customerServiceMapping.setCustomerName(custpojo.getUsername());
                        customerServiceMappingList.add(customerServiceMapping);
                    } catch (Exception e) {
                        map.put(cust.getUsername(), e.getMessage());
                        continue;
                    }
//                    cust.setCustomerServiceMappingList(customerServiceMappingList);

                    CustomersPojo pojo = customerMapper.domainToDTO(cust, new CycleAvoidingMappingContext());
                    RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
                    recordPaymentPojo.setAmount(0D);
                    recordPaymentPojo.setPaytype("");
                    recordPaymentPojo.setReferenceno("");
                    pojo.setPaymentDetails(recordPaymentPojo);
                    Boolean duplicateUser = customersRepository.customerUsernameIsAlreadyExist(pojo.getUsername(), Arrays.asList(pojo.getMvnoId().longValue(), 1L));
                    if(duplicateUser){

                        logger.info("duplicate user found : "+ pojo.getUsername());
                        continue;
                    }

                    pojo = customersService.save(pojo, "bss", false);


                    if (!pojo.getStatus().equalsIgnoreCase("NewActivation") && pojo.getPlanMappingList().size() > 0 && pojo.getPlanMappingList().get(0).getBillTo().equals(com.adopt.apigw.constants.Constants.ORGANIZATION) && pojo.getPlanMappingList().get(0).getIsInvoiceToOrg()) {
                        customersService.saveDataForOrganizationCustomer(pojo, pojo.getDiscount(), 0L, pojo.getPlanMappingList().get(0).getPlangroupid(), pojo.getCusttype());
                    }
                    //Save customer detail with time base policy
                    customersService.CustTimeBasePolicyDetailsSend(pojo);
                    pojo.setCustomerCreated(true);
                    pojo.setIstrialplan(false);
                    pojo.setIsinvoicestop(false);
                    customersService.sharedCustomerData(pojo, false);
                    try {
                        NotificationDTO notificationDTO = notificationService.findByName("Registration");
                        if (null != notificationDTO)
                            smsSchedulerService.sendSMS(Collections.singletonList(pojo), notificationDTO.getId());
                    } catch (Exception e) {
//                    log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    }
                    boolean isInvoiceCreated = false;
                    try {
                        if ((pojo.getCusttype() != null & !"".equals(pojo.getCusttype()) && pojo.getCusttype().equalsIgnoreCase("Prepaid")) && (recordPaymentPojo == null || (recordPaymentPojo != null && recordPaymentPojo.getAmount() <= 0))) {
                            //   log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"create customers"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                        } else {
                            // Generate Invoice
                            if (null != pojo.getPlanMappingList() && 0 < pojo.getPlanMappingList().size() && !pojo.getIstrialplan() && !pojo.getIsinvoicestop()) {
                                //Integer custPackRel = pojo.getPlanMappingList().get(0).getId();
                                //Customers customer = customerMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext());
                                //customer.setBillRunCustPackageRelId(custPackRel);
//                        Runnable invoiceRunnable = new InvoiceCreationThread(pojo, customersService, null, false, null, CommonConstants.INVOICE_TYPE.CREATE_CUSTOMER);
//                        Thread invoiceThread = new Thread(invoiceRunnable);
//                        invoiceThread.start();
                                isInvoiceCreated = true;
                            }
                            // Generate Receipt
                            Customers customers = customersService.savePaymentXMLDocument(customerMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext()));
                            if (null != customers) {
                                Runnable receiptRunnable = new ReceiptThread(billRunService, customers.getCreditDocuments());
                                Thread receiptThread = new Thread(receiptRunnable);
                                receiptThread.start();
                            }

                            customers = customerMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext());
//                        getLocationmapping(pojo);
                            // Generate Charge Invoice
                            if (null != customers && null != customers.getOverChargeList() && 0 < customers.getOverChargeList().size() && !pojo.getIstrialplan() && !pojo.getIsinvoicestop()) {
                                List<Integer> custChargeIdList = new ArrayList<>();
                                customers.getOverChargeList().forEach(data -> custChargeIdList.add(data.getId()));
                                Runnable chargeRunnable = new ChargeThread(customers.getId(), custChargeIdList, customersService, 0L, "", null);
                                Thread billchargeThread = new Thread(chargeRunnable);
                                billchargeThread.start();
//                            RESP_CODE = APIConstants.SUCCESS;
//                            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                            }
                        }
                    } catch (Exception e) {
//                    log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    }
                    try {
                        boolean invoice = false;
                        List<CustPlanMapppingPojo> custPlanList = pojo.getPlanMappingList();
                        for (int i = 0; i < custPlanList.size(); i++) {
                            CustPlanMapppingPojo custPlan = custPlanList.get(i);
                            if (custPlan.getOfferPrice() > 0) {
                                invoice = true;
                                break;
                            }
                        }

                        if (invoice && !isInvoiceCreated && !pojo.getIstrialplan() && !pojo.getIsinvoicestop()) {
                            Customers subscribe = customerMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext());
                            if (subscribe.getParentCustomers() != null && subscribe.getStatus().equalsIgnoreCase("Active")) {
                                List<CustomerServiceMapping> mappings = customerServiceMappingRepository.findByCustId(subscribe.getId());
                                if (mappings != null && !mappings.isEmpty()) {
                                    Boolean isGroup = mappings.stream().filter(x -> x.getInvoiceType().equals(CommonConstants.INVOICE_TYPE_GROUP)).collect(Collectors.toList()).size() > 0;
                                    if (isGroup) {
//                                Runnable chargeRunnable1 = new InvoiceCreationThread(pojo, customersService, CommonConstants.INVOICE_TYPE_GROUP, false, null, CommonConstants.INVOICE_TYPE.CREATE_CUSTOMER);
//                                Thread billchargeThread1 = new Thread(chargeRunnable1);
//                                billchargeThread1.start();
                                    }

                                    Boolean isIndependent = mappings.stream().filter(x -> x.getInvoiceType().equals(CommonConstants.INVOICE_TYPE_INDEPENDENT)).collect(Collectors.toList()).size() > 0;
                                    if (isIndependent) {
//                                Runnable chargeRunnable1 = new InvoiceCreationThread(pojo, customersService, CommonConstants.INVOICE_TYPE_INDEPENDENT, false, null, CommonConstants.INVOICE_TYPE.CREATE_CUSTOMER);
//                                Thread billchargeThread1 = new Thread(chargeRunnable1);
//                                billchargeThread1.start();
                                    }
                                }
                            } else {
//                        Runnable chargeRunnable1 = new InvoiceCreationThread(pojo, customersService, null, false, null, CommonConstants.INVOICE_TYPE.CREATE_CUSTOMER);
//                        Thread billchargeThread1 = new Thread(chargeRunnable1);
//                        billchargeThread1.start();
                            }
                        }
                    } catch (Exception e) {
//                    log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    }


                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return map;

    }

    public Map<String, String> postpaidPlanCreateFromXLS(List<PostpaidPlanMigrationPojo> pojos, Integer mvnoId) throws IOException {
        Map<String, String> map = new HashMap<>();
        List<Integer> mvnoIdList = new ArrayList<>();
        // TODO: pass mvnoID manually 6/5/2025
        if (getLoggedInMvnoId(null) != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            mvnoIdList.add(getLoggedInMvnoId(null));
        }
        mvnoIdList.add(1);
        try {
            for (PostpaidPlanMigrationPojo planpojos : pojos) {
                if (!Objects.isNull(planpojos)) {
                    PostpaidPlan plan = new PostpaidPlan();
                    plan.setName(planpojos.getName());
                    plan.setDisplayName(planpojos.getDisplayName());
                    plan.setCode(planpojos.getCode());
                    plan.setPlantype(planpojos.getPlantype());
                    plan.setCategory(planpojos.getCategory());
                    plan.setMode(planpojos.getMode());
                    plan.setPlanGroup(planpojos.getPlanGroup());
                    List<Long> serviceAreaIds = new ArrayList<>();
                    List<ServiceArea> serviceAreaList = new ArrayList<>();
                    List<ServiceAreaDTO> serviceAreaDTOSList = new ArrayList<>();
                    try {
                        String cleanedInput = planpojos.getServiceAreas().toString().replaceAll("[\\[\\]]", "");
                        String[] parts = cleanedInput.split(",");
                        ArrayList<String> list = new ArrayList<>();
                        for (String part : parts) {
                            list.add(part.trim());
                        }
                        serviceAreaList = serviceAreaRepository.findAllByNameInAndMvnoIdIn(list, mvnoIdList);
                        if (serviceAreaList.size() > 0) {
                            for (ServiceArea area : serviceAreaList) {
                                serviceAreaIds.add(area.getId());
                                serviceAreaDTOSList.add(serviceAreaMapper.domainToDTO(area, new CycleAvoidingMappingContext()));
                            }
                            plan.setServiceAreaNameList(serviceAreaList);
                        }


                    } catch (Exception e) {
                        map.put(plan.getName(), e.getMessage());
                        continue;
                    }
                    List<PlanService> servicesList = new ArrayList<>();
                    for (String name : planpojos.getServiceNames()) {
                        try {
                            servicesList.add(planServiceRepository.findAllByNameEqualsAndMvnoIdIn(name, mvnoIdList));

                        } catch (Exception e) {

                            map.put(plan.getName(), e.getMessage());
                        }
                    }
                    plan.setServiceName(servicesList.get(0).getName());
                    plan.setServiceId(servicesList.get(0).getId());
                    plan.setAccessibility(planpojos.getAccessibility());
                    plan.setValidity(planpojos.getValidity());
                    plan.setUnitsOfValidity(planpojos.getUnitsOfValidity());
                    plan.setAllowdiscount(planpojos.getAllowdiscount());
                    plan.setPlanStatus(planpojos.getPlanStatus());
                    plan.setInvoiceToOrg(planpojos.getInvoiceToOrg());
                    plan.setRequiredApproval(planpojos.getRequiredApproval());
                    plan.setAllowdiscount(plan.isAllowdiscount());
                    plan.setMaxconcurrentsession(planpojos.getMaxconcurrentsession());
                    plan.setDesc(planpojos.getDesc());
                    plan.setQuotatype(planpojos.getQuotatype());
                    plan.setQuotaunittime(planpojos.getQuotaunittime());
                    plan.setQuotatime(planpojos.getQuotatime());
                    plan.setQuota(planpojos.getQuota());
                    plan.setQuotaUnit(planpojos.getQuotaUnit());
                    plan.setQuotaResetInterval(planpojos.getQuotaResetInterval());
                    plan.setSaccode(planpojos.getSaccode());
                    Double newPlanOfferPrice = 0D;
                    if (plan.getNewOfferPrice() != null) {
                        plan.setNewOfferPrice(planpojos.getNewOfferPrice());
                    }
                    List<PlanQosMappingPojo> qosMappingPojoList = new ArrayList<>();
                    if (Objects.nonNull(planpojos.getQospolicyName())) {
                        try {
                            Optional<QOSPolicy> qosPolicy = qosPolicyRepository.findAllByNameAndMvnoIdIn(planpojos.getQospolicyName(), mvnoIdList);
                            plan.setQospolicy(qosPolicy.get());
                            PlanQosMappingPojo planQosMappingPojo = new PlanQosMappingPojo();
                            planQosMappingPojo.setIsdelete(false);
                            planQosMappingPojo.setQosid(qosPolicy.get().getId());
                            planQosMappingPojo.setQosPolicyName(qosPolicy.get().getName());
                            qosMappingPojoList.add(planQosMappingPojo);

                        } catch (Exception e) {
                            map.put(plan.getName(), e.getMessage());
                            continue;
                        }
                    }
                    plan.setPlanQosMappingEntityList(qosMappingPojoList);
                    if (Objects.isNull(planpojos.getTimebasepolicyName().trim()) && !planpojos.getTimebasepolicyName().equals("")) {
                        try {
                            Optional<TimeBasePolicy> timeBasePolicy = timeBasePolicyRepository.findAllByNameEqualsIgnoreCaseAndMvnoIdIn(planpojos.getTimebasepolicyName(), mvnoIdList);
                            if (timeBasePolicy.isPresent()) {
                                plan.setTimebasepolicyName(timeBasePolicy.get().getName());
                            } else {
                                map.put(plan.getName(), "Invalid Tmebase policy");
                                continue;
                            }

                        } catch (Exception e) {
                            map.put(plan.getName(), e.getMessage());
                            continue;
                        }
                    }
                    plan.setParam1(planpojos.getParam1());
                    plan.setParam2(planpojos.getParam2());
                    plan.setParam3(planpojos.getParam3());
                    List<PostpaidPlanCharge> postpaidPlanChargeList = new ArrayList<>();
                    Double price = 0D;
                    String cleanedInput = planpojos.getChargenameList().toString().replaceAll("[\\[\\]]", "");
                    String[] parts = cleanedInput.split(",");
                    ArrayList<String> list = new ArrayList<>();
                    for (String part : parts) {
                        list.add(part.trim());
                    }
                    for (String chargeName : list) {
                        try {
                            Optional<Charge> charge = chargeRepository.findAllByNameEqualsAndMvnoIdIn(chargeName, mvnoIdList);
                            if (charge.isPresent()) {
                                PostpaidPlanCharge postpaidPlanCharge = new PostpaidPlanCharge();
                                postpaidPlanCharge.setPlan(plan);
                                postpaidPlanCharge.setBillingCycle(planpojos.getBillCycle());
                                postpaidPlanCharge.setChargeprice(charge.get().getActualprice());
                                postpaidPlanChargeList.add(postpaidPlanCharge);
                                price += charge.get().getPrice();
                                postpaidPlanCharge.setCharge(charge.get());
                                postpaidPlanCharge.setChargeId(charge.get().getId());
                            } else {
                                map.put(plan.getName(), "Invalid Charge ");
                                continue;
                            }
                        } catch (Exception e) {
                            map.put(plan.getName(), e.getMessage());
                            continue;
                        }
                    }
                    plan.setChargeList(postpaidPlanChargeList);
                    if (planpojos.getStartDateString() != null) {
                        plan.setStartDate(LocalDate.parse(planpojos.getStartDateString()));
                    }
                    if (planpojos.getEndDateString() != null) {
                        plan.setEndDate(LocalDate.parse(planpojos.getEndDateString()));
                    }

                    plan.setStatus(planpojos.getPlanStatus());
                    plan.setPlanStatus(planpojos.getPlanStatus());
                    plan.setOfferprice(price);
                    PostpaidPlanPojo pojo = postpaidPlanMapper.domainToDTO(plan, new CycleAvoidingMappingContext());
                    pojo.setServiceAreaIds(serviceAreaIds);
                    pojo.setServiceAreaNameList(serviceAreaDTOSList);
                    try {
                        boolean flag = postpaidPlanService.duplicateVerifyAtSave(plan.getName());
                        if (flag) {
                            pojo = postpaidPlanService.saveFromMigration(pojo);
                            postpaidPlanService.sendCreateDataShared(pojo, CommonConstants.OPERATION_ADD, false);
                            //		workflowAuditService.workFlowAuditPlan(pojo, getLoggedInUserId());
                            //     createDataSharedService.updateEntityDataForAllMicroService(plan);
                            map.put(plan.getName(), "Success");
                        } else {
                            map.put(plan.getName(), "Plan name Already Exists");
                            continue;
                        }
                    } catch (Exception e) {
                        map.put(plan.getName(), e.getMessage());
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
//            map.put(plan.getName(), e.getMessage());
        }


        return map;

    }

    private LocalDateTime parseDateTime(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MMM/yyyy", Locale.ENGLISH);
        try {
            if (value == null || value.isEmpty()) {
                return null;
            }
            LocalDateTime date = LocalDateTime.parse(value, formatter);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate parseDate(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy", Locale.ENGLISH);
        try {
            if (value == null || value.isEmpty()) {
                return null;
            }
            LocalDate date = LocalDate.parse(value, formatter);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    public void skipQuotaUpdate(Integer custId, Boolean skipQuotaUpdate) {
        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustomerId(custId);
        custPlanMapppingList.sort(Comparator.comparingInt(CustPlanMappping::getId).reversed());
        Integer renewalId = custPlanMapppingList.get(0).getRenewalId();
        List<CustPlanMappping> newCustPlanMapppingList = custPlanMapppingList.stream().filter(custPlanMappping -> {
            Integer planRenewalId = custPlanMappping.getRenewalId();
            return planRenewalId != null && planRenewalId.equals(renewalId);
        }).collect(Collectors.toList());
        for (CustPlanMappping planMappping : newCustPlanMapppingList) {
            for (CustQuotaDetails quotaDetails : planMappping.getQuotaList()) {
                quotaDetails.setSkipQuotaUpdate(skipQuotaUpdate);
            }
        }
        custPlanMappingRepository.saveAll(newCustPlanMapppingList);
    }

    public CustomerServiceMapping getCustomerServiceMapping(Integer custServiceMappingId) {
        String cacheKey = cacheKeys.CUSTOMER_SERVICE_MAPPING + custServiceMappingId;

        try {
            CustomerServiceMapping customerServiceMapping = (CustomerServiceMapping) cacheService.getFromCache(cacheKey, CustomerServiceMapping.class);

            if (customerServiceMapping == null) {
                customerServiceMapping = customerServiceMappingRepository.findById(custServiceMappingId).orElse(null);

                if (customerServiceMapping != null) {
                    cacheService.putInCacheWithExpire(cacheKey, customerServiceMapping);
                }
            }

            return customerServiceMapping;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public void autoRenewOrAddonPlan(ChangePlanRequestDTOList requestDTOs) {
        try {
            if (requestDTOs != null) {
                String number = String.valueOf(UtilsCommon.gen());
                List<ChangePlanRequestDTO> changePlanRequestDTOS = requestDTOs.getChangePlanRequestDTOList();
                changePlanRequestDTOS.removeIf(changePlanRequestDTO -> changePlanRequestDTO.getPlanId() == null);
                Integer custId = null;
                Optional<Integer> custIdOptional = requestDTOs.getChangePlanRequestDTOList().stream().filter(ChangePlanRequestDTO::getIsParent).map(ChangePlanRequestDTO::getCustId).findFirst();
                Set<Integer> custIdsWithoutDuplicates = requestDTOs.getChangePlanRequestDTOList().stream().filter(i -> !i.getIsParent()).map(ChangePlanRequestDTO::getCustId).collect(Collectors.toSet());
                List<Integer> custIds = new ArrayList<>();
                custIds.addAll(custIdsWithoutDuplicates);
                Set<Customers> customersforInvoice = new HashSet<>();

                if (custIds != null && custIds.size() > 0)
                    customersforInvoice.addAll(customersRepository.findAllById(custIds));

                if (custIdOptional.isPresent()) custId = custIdOptional.get();
                else {
                    custIdOptional = requestDTOs.getChangePlanRequestDTOList().stream().map(ChangePlanRequestDTO::getCustId).findFirst();
                    if (custIdOptional.isPresent()) custId = custIdOptional.get();
                }

                Customers parentCustomers = customersRepository.findById(custId).get();
                customersforInvoice.add(parentCustomers);
                List<CustChargeOverrideDTO> custChargeDetailsList = requestDTOs.getCustChargeDetailsList();
                List<CustChargeOverrideDTO> custChargeOverrideDTOS = new ArrayList<>();
                for (ChangePlanRequestDTO requestDTO : requestDTOs.getChangePlanRequestDTOList()) {
                    if (null == requestDTO.getCustId()) {
                        logger.error(LogConstants.REQUEST_FROM + "Bss" + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customersRepository.findById(custId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + 404);
                        return;
                    }

                    if (requestDTO.getPaymentOwner() == null) requestDTO.setPaymentOwner("");

                    Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
                    if (null == customers) {
                        logger.info(LogConstants.REQUEST_FROM + "Bss" + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + 404);
                        return;
                    }

                    if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON)) {
                        List<CustomerPlansModel> currentPlanList = this.subscriberService.getActivePlanList(customers.getId(), false);
                        if (currentPlanList.size() <= 0) {
                            logger.info(LogConstants.REQUEST_FROM + "Bss" + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + 404);
                            return;
                        }
                    }

                    if (subscriberService.getCustomerPlanList(customers.getId(), false).size() <= 0) {
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_NEW);
                    } else {
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_RENEW);
                    }

                    List<CustomersBasicDetailsPojo> custBasicDetailsPojoList = new ArrayList<CustomersBasicDetailsPojo>();
                    CustomersBasicDetailsPojo basicDetailsPojo = null;
                    if (requestDTO.getPlanGroupId() != null && requestDTO.getPlanGroupId() != 0 && requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW)) {
                        if (CollectionUtils.isEmpty(requestDTO.getNewPlanList())) {
                            List<Integer> newPlanList = requestDTO.getPlanBindWithOldPlans().stream().filter(newPlanBindWithOldPlan -> newPlanBindWithOldPlan.getNewPlanId() != null).map(NewPlanBindWithOldPlan::getNewPlanId).collect(Collectors.toList());
                            requestDTO.setNewPlanList(newPlanList);
                        }
                        CustomChangePlanDTO customChangePlanDTO = null;
                        for (NewPlanBindWithOldPlan newPlanBindWithOldPlan : requestDTO.getPlanBindWithOldPlans()) {
                            if (newPlanBindWithOldPlan.getNewPlanId() != null) {
                                requestDTO.setPlanId(newPlanBindWithOldPlan.getNewPlanId());
                                requestDTO.setCustServiceMappingId(newPlanBindWithOldPlan.getCustServiceMappingId());
                                requestDTO.setIsTriggerCoaDm(requestDTOs.getIsTriggerCoaDm());
                                customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers, false, 0.0, "bss", null, number, requestDTOs.getDateOverrideDtos(), null);
                                Thread.sleep(1000);
                                if (customChangePlanDTO.getRecordpaymentResponseDTO() != null) {
                                    if (customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument().size() > 0 && !customers.getIsinvoicestop()) {
                                        List<CreditDocument> creditDocumentList = customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument();
                                        Runnable receiptRunnable = new ReceiptThread(billRunService, creditDocumentList);
                                        Thread receiptThread = new Thread(receiptRunnable);
                                        receiptThread.start();
                                    }
                                }
                                basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                                if (!CollectionUtils.isEmpty(custChargeDetailsList)) {
                                    List<CustChargeOverrideDTO> custChargeOverrideDTOs = custChargeDetailsList.stream().filter(custCharge -> custCharge.getParentId().equals(customers.getId())).collect(Collectors.toList());

                                    for (CustChargeOverrideDTO custChargeOverrideDTO : custChargeOverrideDTOs) {
                                        custChargeOverrideDTO.setIsRenew(true);
                                        CustChargeOverrideDTO chargeOverrideDTO = custChargeService.createCustChargeOverride(custChargeOverrideDTO);
                                        basicDetailsPojo.setCustChargeOverride(chargeOverrideDTO);
                                        custChargeOverrideDTOS.add(chargeOverrideDTO);
                                    }
                                }
                                custBasicDetailsPojoList.add(basicDetailsPojo);
                            }
                        }

                    } else {
                        if (Objects.nonNull(requestDTOs.getIsTriggerCoaDm())) {
                            requestDTO.setIsTriggerCoaDm(requestDTOs.getIsTriggerCoaDm());
                        }
                        CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers, false, 0.0, "bss", null, number, requestDTOs.getDateOverrideDtos(), null);
                        if (customChangePlanDTO.getRecordpaymentResponseDTO() != null) {
                            if (customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument().size() > 0 && !customers.getIsinvoicestop()) {
                                List<CreditDocument> creditDocumentList = customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument();
                                Runnable receiptRunnable = new ReceiptThread(billRunService, creditDocumentList);
                                Thread receiptThread = new Thread(receiptRunnable);
                                receiptThread.start();
                            }
                        }
                        basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                        if (!CollectionUtils.isEmpty(custChargeDetailsList)) {
                            for (CustChargeOverrideDTO custChargeOverrideDTO : custChargeDetailsList) {
                                if (custChargeOverrideDTO.getCustid().equals(customers.getId())) {
                                    List<CustChargeDetailsPojo> custChargeDetailsPojoList = custChargeOverrideDTO.getCustChargeDetailsPojoList().stream().peek(custChargeDetailsPojo -> {
                                        custChargeDetailsPojo.setStartdate(customChangePlanDTO.getStartdate());
                                        custChargeDetailsPojo.setEnddate(customChangePlanDTO.getEnddate());
                                        custChargeDetailsPojo.setExpiry(customChangePlanDTO.getEnddate());
                                    }).collect(Collectors.toList());
                                    custChargeOverrideDTO.setCustChargeDetailsPojoList(custChargeDetailsPojoList);
                                    custChargeOverrideDTO.setIsRenew(true);
                                    custChargeOverrideDTO.setCustid(customers.getId());
                                    CustChargeOverrideDTO chargeOverrideDTO = custChargeService.createCustChargeOverride(custChargeOverrideDTO);
                                    basicDetailsPojo.setCustChargeOverride(chargeOverrideDTO);
                                    custChargeOverrideDTOS.add(chargeOverrideDTO);
                                }
                            }
                        }
                    }
                }

                try {
                    if (requestDTOs.getChangePlanRequestDTOList().get(0).getPurchaseType().equalsIgnoreCase("Addon")) {
                        if (customersforInvoice.size() > 1) {
                            Integer parentId = custIdOptional.get();
                            custIds.removeIf(i -> i.equals(parentId));
                            List<Integer> childIds = custIds;
                            debitDocService.createInvoice(customersforInvoice, com.adopt.apigw.constants.Constants.ADD_ON, parentId, childIds, requestDTOs.getRecordPayment(), null, false, requestDTOs.getChangePlanRequestDTOList().get(0).getIsAutoPaymentRequired(), requestDTOs.getChangePlanRequestDTOList().get(0).getCreditDocumentPaymentPojoList(), null);
                        } else {
                            debitDocService.createInvoice(
                                    parentCustomers, com.adopt.apigw.constants.Constants.ADD_ON, "", requestDTOs.getRecordPayment(), null, null, false, requestDTOs.getChangePlanRequestDTOList().get(0).getIsAutoPaymentRequired(), requestDTOs.getChangePlanRequestDTOList().get(0).getCreditDocumentPaymentPojoList(), null,null);
                        }
                    } else {
                        if (customersforInvoice.size() > 1) {
                            Integer parentId = custIdOptional.get();
                            custIds.removeIf(i -> i.equals(parentId));
                            List<Integer> childIds = custIds;
                            debitDocService.createInvoice(customersforInvoice, com.adopt.apigw.constants.Constants.RENEW, parentId, childIds, requestDTOs.getRecordPayment(), null, false, requestDTOs.getChangePlanRequestDTOList().get(0).getIsAutoPaymentRequired(), requestDTOs.getChangePlanRequestDTOList().get(0).getCreditDocumentPaymentPojoList(), null);
                        } else {
                            debitDocService.createInvoice(parentCustomers, com.adopt.apigw.constants.Constants.RENEW, "", requestDTOs.getRecordPayment(), null, null, false, requestDTOs.getChangePlanRequestDTOList().get(0).getIsAutoPaymentRequired(), requestDTOs.getChangePlanRequestDTOList().get(0).getCreditDocumentPaymentPojoList(), null,null);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public ChangePlanRequestDTOList convert(AutoRenewOrAddonPlanRequestDto dto) {
        ChangePlanRequestDTOList requestDTOList = new ChangePlanRequestDTOList();
        requestDTOList.setChangePlanRequestDTOList(new ArrayList<>());
        ChangePlanRequestDTO requestDTO = new ChangePlanRequestDTO();
        requestDTO.setPurchaseType(dto.getPurchaseType());
        requestDTO.setPlanId(dto.getPlanId());
        requestDTO.setDiscount(dto.getDiscount());
        requestDTO.setCustId(dto.getCustomerId());
        requestDTO.setCustServiceMappingId(dto.getCustomerServiceMappingId());
        requestDTO.setIsParent(dto.getIsParent());
        requestDTO.setIsAdvRenewal(false);
        requestDTO.setRemarks(dto.getRemarks());
        requestDTO.setIsPaymentReceived(false);
        requestDTO.setBillableCustomerId(dto.getCustomerId());
        requestDTO.setPaymentOwnerId(dto.getPaymentOwnerId());
        requestDTO.setIsRefund(dto.getIsAutoRefund());
        requestDTO.setIsAutoPaymentRequired(dto.getIsAutoPaymentRequired());
        requestDTO.setCreditDocumentPaymentPojoList(dto.getCreditDocumentPaymentPojoList());
        if (dto.getPurchaseType() != null && dto.getPurchaseType().equalsIgnoreCase("Addon")) {
            requestDTO.setAddonStartDate(dto.getAddonStartDate());
            requestDTO.setAddonEndDate(dto.getAddonEndDate());
            requestDTOList.setIsTriggerCoaDm(true);
        }
        requestDTOList.getChangePlanRequestDTOList().add(requestDTO);
        requestDTOList.setRecordPayment(null);
        requestDTOList.setCustChargeDetailsList(null);
        return requestDTOList;
    }


    public void autoRenewOrAddonPlan(AutoRenewOrAddonPlanRequestDto requestDto) {
        generateTokenForAutoAssignRenewal();
        ChangePlanRequestDTOList requestDTOList = convert(requestDto);
        autoRenewOrAddonPlan(requestDTOList);
    }

    public void autoRenewOrAddonPlan(List<AutoRenewOrAddonPlanRequestDto> requestDto1) {
        for (AutoRenewOrAddonPlanRequestDto requestDto : requestDto1) {
            generateTokenForAutoAssignRenewal();
            ChangePlanRequestDTOList requestDTOList = convert(requestDto);
            autoRenewOrAddonPlan(requestDTOList);
        }
    }


    public void generateTokenForAutoAssignRenewal() {
        List<GrantedAuthority> role_name = new ArrayList<>();
        role_name.add(new SimpleGrantedAuthority("ADMIN"));
        LoggedInUser user = new LoggedInUser(autoAssignUsername, autoAssignPasswod, true, true, true, true, role_name, "superadmin", "superadmin", LocalDateTime.now(), 1, 1, "ADMIN", null, 1, null, 1, new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(), "superadmin", null, null, null);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public String generatePaymentLink(Integer custId, String token) {
        String hash = null;
        try {
            Optional<Customers> optionalCustomers = customersRepository.findById(custId);
            if (optionalCustomers.isPresent()) {
                Customers customers = optionalCustomers.get();
//                List<Object[]> activePlans = getActivePlanListByCustomerId(custId);
//                Object[] latestPlan = activePlans.get(0);

                if (customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVE) || customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.INAVCTIVE) || customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.SUSPEND)) {
//                    List<DebitDocument> debitDocuments = revenueClient.getDebitDocumentByCustId(custId, token);
//                    DebitDocument debitDocument = getLatestDebitDocument(debitDocuments);
//                    if (debitDocument != null) {
                    Shorter shorter = new Shorter();
                    CodeGenerator codeGenerator = new CodeGenerator();
                    hash = codeGenerator.generate(12);
                    shorter.setCustId(custId);
//                        Double totalAmount = Optional.ofNullable(debitDocument.getTotalamount()).orElse(0.0);
//                        Double adjustedAmount = Optional.ofNullable(debitDocument.getAdjustedAmount()).orElse(0.0);
//                        double difference = totalAmount - adjustedAmount;
//                        shorter.setAmount(Math.abs(difference) < 0.1 ? 0.0 : difference);
                    shorter.setCustomerUsername(customers.getUsername());
                    shorter.setMvnoId(customers.getMvnoId());
//                        shorter.setInvoiceId(debitDocument.getId());
                    shorter.setHash(hash);
//                        shorter.setPlanName(String.valueOf(latestPlan[1]));
//                        shorter.setPlanDueDate(((Timestamp) latestPlan[2]).toLocalDateTime());
                    shorter.setToken(token);
                    shorter.setIshashused(false);
                    shorter.setAccountNumber(getAccountNoForShorter(custId));
                    shorter.setLinkType(CommonConstants.CURRENT_PAYMENT);
                    shorterRepository.save(shorter);
//                    }
                } else if (customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.NEW_ACTIVATION) || customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVATION_PENDING)) {
//                    List<TrialDebitDocument> trialDebitDocuments = revenueClient.getTrailDebitDocumentByCustId(custId, token);
//                    TrialDebitDocument trialDebitDocument = getLatestTrailDebitDocument(trialDebitDocuments);
//                    if (trialDebitDocument != null) {
                    Shorter shorter = new Shorter();
                    CodeGenerator codeGenerator = new CodeGenerator();
                    hash = codeGenerator.generate(12);
                    shorter.setCustId(custId);
//                        Double totalAmount = Optional.ofNullable(trialDebitDocument.getTotalamount()).orElse(0.0);
//                        Double adjustedAmount = Optional.ofNullable(trialDebitDocument.getAdjustedAmount()).orElse(0.0);
//                        double difference = totalAmount - adjustedAmount;
//                        shorter.setAmount(Math.abs(difference) < 0.1 ? 0.0 : difference);
                    shorter.setCustomerUsername(customers.getUsername());
                    shorter.setMvnoId(customers.getMvnoId());
//                        shorter.setInvoiceId(trialDebitDocument.getId());
                    shorter.setHash(hash);
//                        shorter.setPlanName(String.valueOf(latestPlan[1]));
//                        shorter.setPlanDueDate(((Timestamp) latestPlan[2]).toLocalDateTime());
                    shorter.setToken(token);
                    shorter.setIshashused(false);
                    shorter.setAccountNumber(getAccountNoForShorter(custId));
                    shorter.setLinkType(CommonConstants.CURRENT_PAYMENT);
                    shorterRepository.save(shorter);
//                    }
                }
            }
        } catch (Exception e) {
            logger.error("getting error while generatePaymentLink by custId {} : ", custId, e.getMessage());
        }
        return hash;
    }

    public String generatePaymentLinkForRenew(Integer custId, String token) {
        String hash = null;
        CustomerLedgerDtlsPojo pojo = new CustomerLedgerDtlsPojo();
        pojo.setCustId(custId);
        ResponseEntity<Map<String, Object>> walletPojo = revenueClient.getWalletAmount(pojo, token);
        if (walletPojo.getStatusCode().is2xxSuccessful() && walletPojo.getBody() != null) {
            Map<String, Object> responseBody = walletPojo.getBody();
            if (responseBody.containsKey("customerWalletDetails")) {
                Double customerWalletDetails = Double.parseDouble(responseBody.get("customerWalletDetails").toString());
                if (customerWalletDetails < 0) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please settle the Pending Invoice First", null);
                }
            } else {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Failed to fetch customer wallet details", null);
            }
        } else {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Failed to fetch customer wallet details", null);
        }

        Optional<Customers> optionalCustomers = customersRepository.findById(custId);
        if (optionalCustomers.isPresent()) {
            Customers customers = optionalCustomers.get();
//                List<Object[]> renewPlans = getRenewPlanListByCustomerId(custId);
//                Object[] latestPlan = renewPlans.get(0);

            if (customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVE) || customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.INAVCTIVE) || customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.SUSPEND)) {
                Shorter shorter = new Shorter();
                CodeGenerator codeGenerator = new CodeGenerator();
                hash = codeGenerator.generate(12);
                shorter.setCustId(custId);
//                        BigDecimal value = (BigDecimal) latestPlan[3];  // Extract BigDecimal
//                        Double amount = value != null ? value.doubleValue() : null;
//                        shorter.setAmount(amount);
                shorter.setCustomerUsername(customers.getUsername());
                shorter.setMvnoId(customers.getMvnoId());
//                        BigInteger planId = (BigInteger)latestPlan[0];
//                        shorter.setPlanId(planId.intValue());
                shorter.setHash(hash);
//                        shorter.setPlanName(String.valueOf(latestPlan[1]));
//                        shorter.setPlanDueDate(((Timestamp) latestPlan[2]).toLocalDateTime());
                shorter.setToken(token);
                shorter.setIshashused(false);
                shorter.setAccountNumber(getAccountNoForShorter(custId));
                shorter.setLinkType(CommonConstants.RENEW_PAYMENT);
                shorterRepository.save(shorter);
            } else {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Customer status is currently under approval or has been terminated.", null);
            }
            return hash;
        } else {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Customer is not found by given Id", null);
        }
    }


    public String generatePaymentLinkForRenewShedular(Integer custId, String token) {
        String hash = null;
        CustomerLedgerDtlsPojo pojo = new CustomerLedgerDtlsPojo();
        pojo.setCustId(custId);
        ResponseEntity<Map<String, Object>> walletPojo = revenueClient.getWalletAmount(pojo, token);
        if (walletPojo.getStatusCode().is2xxSuccessful() && walletPojo.getBody() != null) {
            Map<String, Object> responseBody = walletPojo.getBody();
            if (responseBody.containsKey("customerWalletDetails")) {
                Double customerWalletDetails = Double.parseDouble(responseBody.get("customerWalletDetails").toString());
                if (customerWalletDetails < 0) {
                    logger.error("Link will not be generated for custId: " + custId + " because wallet is not settled");
                    return null;
                }
            } else {
                logger.error("Failed to fetch customer wallet details");
                return null;
            }
        } else {
            logger.error("Failed to fetch customer wallet details");
            return null;
        }

        Optional<Customers> optionalCustomers = customersRepository.findById(custId);
        if (optionalCustomers.isPresent()) {
            Customers customers = optionalCustomers.get();
//            List<Object[]> renewPlans = getRenewPlanListByCustomerId(custId);
//            Object[] latestPlan = renewPlans.get(0);

            if (customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVE) || customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.INAVCTIVE) || customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.SUSPEND)) {
                Shorter shorter = new Shorter();
                CodeGenerator codeGenerator = new CodeGenerator();
                hash = codeGenerator.generate(12);
                shorter.setCustId(custId);
//                BigDecimal value = (BigDecimal) latestPlan[3];  // Extract BigDecimal
//                Double amount = value != null ? value.doubleValue() : null;
//                shorter.setAmount(amount);
                shorter.setCustomerUsername(customers.getUsername());
                shorter.setMvnoId(customers.getMvnoId());
//                BigInteger planId = (BigInteger)latestPlan[0];
//                shorter.setPlanId(planId.intValue());
                shorter.setHash(hash);
//                shorter.setPlanName(String.valueOf(latestPlan[1]));
//                shorter.setPlanDueDate(((Timestamp) latestPlan[2]).toLocalDateTime());
                shorter.setToken(token);
                shorter.setIshashused(false);
                shorter.setAccountNumber(getAccountNoForShorter(custId));
                shorter.setLinkType(CommonConstants.RENEW_PAYMENT);
                shorterRepository.save(shorter);
            } else {
                logger.error("Customer status is currently under approval or has been terminated.");
            }
            return hash;
        } else {
            logger.error("Customer is not found by given Id");
            return null;
        }
    }


    public List<Object[]> getActivePlanListByCustomerId(Integer custId) {
        List<Object[]> result = custPlanMapppingRepository.findActivePlanDetails(custId,
                Arrays.asList(SubscriberConstants.PLAN_PURCHASE_RENEW,
                        SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL,
                        SubscriberConstants.PLAN_PURCHASE_NEW));
        return result;
    }

    public DebitDocument getLatestDebitDocument(List<DebitDocument> debitDocuments) {
        return debitDocuments.stream()
                .max(Comparator.comparing(DebitDocument::getId))
                .orElse(null);
    }

    public TrialDebitDocument getLatestTrailDebitDocument(List<TrialDebitDocument> trialDebitDocuments) {
        return trialDebitDocuments.stream()
                .max(Comparator.comparing(TrialDebitDocument::getId))
                .orElse(null);
    }

    public List<Object[]> getLatestActivePlanListByCustomerId(Integer custId) {
        List<Object[]> result = custPlanMapppingRepository.findActivePlanDetailsLatest(custId,
                Arrays.asList(SubscriberConstants.PLAN_PURCHASE_RENEW,
                        SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL,
                        SubscriberConstants.PLAN_PURCHASE_NEW));
        return result;
    }

    public List<Object[]> getLatestFuturePlanListByCustomerId(Integer custId) {
        List<Object[]> result = custPlanMapppingRepository.findFuturePlanDetails(custId,
                Arrays.asList(SubscriberConstants.PLAN_PURCHASE_RENEW,
                        SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL,
                        SubscriberConstants.PLAN_PURCHASE_NEW));
        return result;
    }

    public List<Object[]> getLatestExpiredListByCustomerId(Integer custId) {
        List<Object[]> result = custPlanMapppingRepository.findExpiredPlanDetails(custId,
                Arrays.asList(SubscriberConstants.PLAN_PURCHASE_RENEW,
                        SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL,
                        SubscriberConstants.PLAN_PURCHASE_NEW));
        return result;
    }

    public List<Object[]> getRenewPlanListByCustomerId(Integer custId) {
        List<Object[]> result = new ArrayList<>();
        List<Object[]> futurePlanList = getLatestFuturePlanListByCustomerId(custId);
        if (!futurePlanList.isEmpty()) {
            return futurePlanList;
        }
        List<Object[]> activePlanList = getLatestActivePlanListByCustomerId(custId);
        if (!activePlanList.isEmpty()) {
            return activePlanList;
        }
        List<Object[]> expiredPlanList = getLatestExpiredListByCustomerId(custId);
        if (!expiredPlanList.isEmpty()) {
            return expiredPlanList;
        } else {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Customer has no plan.", null);
        }
    }

    public DeactivatePlanReqDTO upgradePlanByAccountNoAndPlanName(String accountNo, String packageName) {
        DeactivatePlanReqDTO deactivatePlanReqDTO = new DeactivatePlanReqDTO();
        List<Integer> planIds = postpaidPlanRepo.findActivePlansWithQosPolicy(packageName, CommonConstants.CUST_PLAN_STATUS_ACTIVE);
        if (planIds.isEmpty()) {
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Plan not found", null);
        }
        if (planIds.size() > 1) {
            throw new CustomValidationException(HttpStatus.CONFLICT.value(), "Duplicate plans found for package: " + packageName, null);
        }
        Customers customer = customersRepository.findCustomerIdByAccNo(accountNo, getLoggedInUser().getMvnoId());
        if (customer == null) {
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Account not found", null);
        }
        Integer newPlanId = planIds.get(0);
        Integer planId = 0;
        Double planDiscount = 0.0;
        deactivatePlanReqDTO.setCustId(customer.getId());
        deactivatePlanReqDTO.setBillableCustomerId(customer.getId());
        deactivatePlanReqDTO.setPlanGroupChange(false);
        deactivatePlanReqDTO.setPlanGroupFullyChanged(false);
        deactivatePlanReqDTO.setPaymentOwner(getLoggedInUser().getUsername());
        deactivatePlanReqDTO.setPaymentOwnerId(getLoggedInUserId());
        List<DeactivatePlanReqModel> deactivatePlanReqModels = new ArrayList<>();
        DeactivatePlanReqModel deactivatePlanReqModel = new DeactivatePlanReqModel();
        deactivatePlanReqModel.setNewPlanId(newPlanId);
        List<CustomerPlansModel> customerPlansModels = new ArrayList<>();
        if (customer.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVE)) {
            customerPlansModels = customersService.getPlanByCustServiceList(customer.getId(), null, true, false);
        } else if (customer.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVATION_PENDING) || customer.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.NEW_ACTIVATION)) {
            customerPlansModels = customersService.getPlanByCustServiceList(customer.getId(), customer.getStatus(), false, false);
        }
        CustomerPlansModel customerPlansModel = customerPlansModels.get(0);
        deactivatePlanReqModel.setPlanId(customerPlansModel.getPlanId());
        deactivatePlanReqModel.setDiscount(customerPlansModel.getDiscount());
        deactivatePlanReqModel.setCustServiceMappingId(customerPlansModel.getCustomerServiceMappingId());
        deactivatePlanReqModels.add(deactivatePlanReqModel);
        deactivatePlanReqDTO.setDeactivatePlanReqModels(deactivatePlanReqModels);
        return deactivatePlanReqDTO;
    }

    public Object[] findPlanPriceAndEndDateByPlanId(Integer planId, Integer custId) {
        Object[] planDetails = custPlanMappingRepo.findPlanPriceAndEndDateByPlanId(planId, custId);
        return planDetails;
    }

    public CustomerPaymentDto generateAirtelRequestByAccountNumber(String accountNo, Double amount, String token) {
        CustomerPaymentDto customerPaymentDto = new CustomerPaymentDto();
        try {
            // TODO: pass mvnoID manually 6/5/2025
            List<Customers> customers = customersRepository.findByAcctnoAndMvnoId(accountNo, getMvnoIdFromCurrentStaff(null));
            if (customers.isEmpty()) {
                customerPaymentDto.setMobileNumber("Customer not found");
                return customerPaymentDto;
            }
            if (customers.size() > 1) {
                customerPaymentDto.setMobileNumber("Duplicate Account Number found for Account Number:");
                return customerPaymentDto;
            }
            Customers customer = customers.get(0);
            List<Object[]> renewPlans = getRenewPlanListByCustomerId(customer.getId());
            Object[] latestPlan = renewPlans.get(0);

            BigInteger planId = (BigInteger) latestPlan[0];
//                Double planPrice = latestPlan[3] != null ? ((BigDecimal) latestPlan[3]).doubleValue() : null;
//                if(!Objects.equals(amount, planPrice)){
//                    customerPaymentDto.setMobileNumber("Please enter same amount as Package price");
//                    return customerPaymentDto;
//                }

            if (customer.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVE)) {
                List<DebitDocument> debitDocuments = revenueClient.getDebitDocumentByCustId(customer.getId(), token);
                DebitDocument debitDocument = getLatestDebitDocument(debitDocuments);
                if (debitDocument != null) {
                    customerPaymentDto.setCustomerId(customer.getId());
                    Double totalAmount = Optional.ofNullable(debitDocument.getTotalamount()).orElse(0.0);
                    Double adjustedAmount = Optional.ofNullable(debitDocument.getAdjustedAmount()).orElse(0.0);
                    double difference = totalAmount - adjustedAmount;
                    customerPaymentDto.setAmount(String.valueOf(Math.abs(difference) < 0.1 ? 0.0 : difference));
                    customerPaymentDto.setIsFromCaptive(false);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setMerchantName(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL_USSD_PUSH);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setMvnoId(customer.getMvnoId());
                    customerPaymentDto.setInvoiceId(debitDocument.getId());
                    customerPaymentDto.setMobileNumber(customer.getMobile());
                } else {
                    customerPaymentDto.setCustomerId(customer.getId());
                    customerPaymentDto.setIsFromCaptive(false);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setMerchantName(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL_USSD_PUSH);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setMvnoId(customer.getMvnoId());
                    customerPaymentDto.setMobileNumber(customer.getMobile());
                }
            } else if (customer.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.NEW_ACTIVATION) || customer.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVATION_PENDING)) {
                List<TrialDebitDocument> trialDebitDocuments = revenueClient.getTrailDebitDocumentByCustId(customer.getId(), token);
                TrialDebitDocument trialDebitDocument = getLatestTrailDebitDocument(trialDebitDocuments);
                if (trialDebitDocument != null) {
                    customerPaymentDto.setCustomerId(customer.getId());
                    Double totalAmount = Optional.ofNullable(trialDebitDocument.getTotalamount()).orElse(0.0);
                    Double adjustedAmount = Optional.ofNullable(trialDebitDocument.getAdjustedAmount()).orElse(0.0);
                    double difference = totalAmount - adjustedAmount;
                    customerPaymentDto.setAmount(String.valueOf(Math.abs(difference) < 0.1 ? 0.0 : difference));
                    customerPaymentDto.setIsFromCaptive(false);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setMerchantName(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL_USSD_PUSH);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setMvnoId(customer.getMvnoId());
                    customerPaymentDto.setInvoiceId(trialDebitDocument.getId());
                    customerPaymentDto.setMobileNumber(customer.getMobile());
                } else {
                    customerPaymentDto.setCustomerId(customer.getId());
                    customerPaymentDto.setIsFromCaptive(false);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setMerchantName(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL_USSD_PUSH);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setMvnoId(customer.getMvnoId());
                    customerPaymentDto.setMobileNumber(customer.getMobile());
                }
            }
        } catch (Exception e) {
            logger.error("getting error while generateAirtelRequestByCustId by accountNo {} : ", accountNo, e.getMessage());
        }
        return customerPaymentDto;
    }

    public CustomerPaymentDto generateMoMoPayRequestByAccountNumber(String accountNo, Double amount, String token) {
        CustomerPaymentDto customerPaymentDto = new CustomerPaymentDto();
        try {
            // TODO: pass mvnoID manually 6/5/2025
            List<Customers> customers = customersRepository.findByAcctnoAndMvnoId(accountNo, getMvnoIdFromCurrentStaff(null));
            if (customers.isEmpty()) {
                customerPaymentDto.setMobileNumber("Customer not found");
                return customerPaymentDto;
            }
            if (customers.size() > 1) {
                customerPaymentDto.setMobileNumber("Duplicate Account Number found for Account Number:" + accountNo);
                return customerPaymentDto;
            }
            Customers customer = customers.get(0);
            List<Object[]> renewPlans = getRenewPlanListByCustomerId(customer.getId());
            Object[] latestPlan = renewPlans.get(0);

            BigInteger planId = (BigInteger) latestPlan[0];
//            Double planPrice = latestPlan[3] != null ? ((BigDecimal) latestPlan[3]).doubleValue() : null;
//            if(!Objects.equals(amount, planPrice)){
//                customerPaymentDto.setMobileNumber("Please enter same amount as Package price");
//                return customerPaymentDto;
//            }

            if (customer.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVE)) {
                List<DebitDocument> debitDocuments = revenueClient.getDebitDocumentByCustId(customer.getId(), token);
                DebitDocument debitDocument = getLatestDebitDocument(debitDocuments);
                if (debitDocument != null) {
                    customerPaymentDto.setCustomerId(customer.getId());
                    Double totalAmount = Optional.ofNullable(debitDocument.getTotalamount()).orElse(0.0);
                    Double adjustedAmount = Optional.ofNullable(debitDocument.getAdjustedAmount()).orElse(0.0);
                    double difference = totalAmount - adjustedAmount;
                    customerPaymentDto.setAmount(String.valueOf(Math.abs(difference) < 0.1 ? 0.0 : difference));
                    customerPaymentDto.setIsFromCaptive(false);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setMerchantName(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY_USSD_PUSH);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setCustomerUUID(UUID.randomUUID().toString());
                    customerPaymentDto.setMvnoId(customer.getMvnoId());
                    customerPaymentDto.setInvoiceId(debitDocument.getId());
                    customerPaymentDto.setMobileNumber(customer.getCountryCode().replace("+", "") + customer.getMobile());
                } else {
                    customerPaymentDto.setCustomerId(customer.getId());
                    customerPaymentDto.setIsFromCaptive(false);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setMerchantName(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY_USSD_PUSH);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setCustomerUUID(UUID.randomUUID().toString());
                    customerPaymentDto.setMvnoId(customer.getMvnoId());
                    customerPaymentDto.setInvoiceId(null);
                    customerPaymentDto.setPlanId(planId.longValue());
                    customerPaymentDto.setMobileNumber(customer.getCountryCode().replace("+", "") + customer.getMobile());
                }
            } else if (customer.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.NEW_ACTIVATION) || customer.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVATION_PENDING)) {
                List<TrialDebitDocument> trialDebitDocuments = revenueClient.getTrailDebitDocumentByCustId(customer.getId(), token);
                TrialDebitDocument trialDebitDocument = getLatestTrailDebitDocument(trialDebitDocuments);
                if (trialDebitDocument != null) {
                    customerPaymentDto.setCustomerId(customer.getId());
                    Double totalAmount = Optional.ofNullable(trialDebitDocument.getTotalamount()).orElse(0.0);
                    Double adjustedAmount = Optional.ofNullable(trialDebitDocument.getAdjustedAmount()).orElse(0.0);
                    double difference = totalAmount - adjustedAmount;
                    customerPaymentDto.setAmount(String.valueOf(Math.abs(difference) < 0.1 ? 0.0 : difference));
                    customerPaymentDto.setIsFromCaptive(false);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setMerchantName(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY_USSD_PUSH);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setCustomerUUID(UUID.randomUUID().toString());
                    customerPaymentDto.setMvnoId(customer.getMvnoId());
                    customerPaymentDto.setInvoiceId(trialDebitDocument.getId());
                    customerPaymentDto.setMobileNumber(customer.getCountryCode().replace("+", "") + customer.getMobile());
                } else {
                    customerPaymentDto.setCustomerId(customer.getId());
                    customerPaymentDto.setIsFromCaptive(false);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setMerchantName(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY_USSD_PUSH);
                    customerPaymentDto.setCustomerUsername(customer.getUsername());
                    customerPaymentDto.setCustomerUUID(UUID.randomUUID().toString());
                    customerPaymentDto.setMvnoId(customer.getMvnoId());
                    customerPaymentDto.setInvoiceId(null);
                    customerPaymentDto.setPlanId(planId.longValue());
                    customerPaymentDto.setMobileNumber(customer.getCountryCode().replace("+", "") + customer.getMobile());
                }
            }
        } catch (Exception e) {
            logger.error("getting error while generateAirtelRequestByCustId by accountNo {} : ", accountNo, e.getMessage());
        }
        return customerPaymentDto;
    }

    public String getAccountNoForShorter(Integer custId) {
        String accountNumber = null;
        Integer mvnoId = 0;
        Object[] result = customersService.getAccountNoByCustId(custId);
        if (result == null || result.length == 0) {
            throw new CustomValidationException(417, "Customer not Found: " + custId, null);
        }
        if (result != null && result.length > 0 && result[0] instanceof Object[]) {
            Object[] innerArray = (Object[]) result[0];
            if (innerArray.length >= 2) {
                accountNumber = innerArray[0] != null ? ((String) innerArray[0]) : null;
                mvnoId = innerArray[1] != null ? ((Integer) innerArray[1]) : 0;
            }
        }
        return accountNumber;
    }


    public Integer getLatestPlanByCustId(Integer custId) {
        List<Object[]> renewPlans = getRenewPlanListByCustomerId(custId);
        Object[] latestPlan = renewPlans.get(0);
        BigInteger planId = (BigInteger) latestPlan[0];
        return planId.intValue();
    }

    public boolean isChangePlanBuyWithin24Hour(Integer custId) {
        boolean isAllow = false;
        Timestamp startDateObj = customerPackageRepository.findStartDateByCustomerId(custId);
        if (startDateObj != null) {
            LocalDateTime startDateTime = startDateObj.toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(startDateTime, now);
            if (duration.toMinutes() < 1440) {  // 1440 minutes = 24 hours
                isAllow = true;
            }
        }
        logger.info("********* isChangePlanBuyWithin24Hour isAllow : " + isAllow + "*********");
        return isAllow;
    }

    public void extendGraceDays(CustomersGracedaysPojo pojo) {
        try {
            customersRepository.updateGracedays(pojo.getCustId(), pojo.getGraceDays());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Map<String, Object> updateAllRenewalStatuses(UpdateRenewalStatusRequest plan) {
        Map<String, Object> response = new HashMap<>();
        try {
            int updated = custPlanMapppingRepository.updateRenewalForBoosterById(
                    plan.getRenewalForBooster(),
                    plan.getCustPlanMapppingId()
            );

            if (updated == 0) {
                logger.warn("No record found to update for custPlanMappingId={}", plan.getCustPlanMapppingId());
                throw new RuntimeException("No plan found with ID: " + plan.getCustPlanMapppingId());
            }
            AutoRenewalBoosterPlanMessage message = new AutoRenewalBoosterPlanMessage(plan.getRenewalForBooster(), plan.getCustPlanMapppingId());
            kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));

            CustomerDetailsDTO customerDetailsDTO = custPlanMapppingRepository.getCustomerDetailsByCprId(plan.getCustPlanMapppingId());
            AutoRenewalPreferenceMessage autoRenewalPreferenceMessage = new AutoRenewalPreferenceMessage(com.adopt.apigw.constants.Constants.AUTO_RENEWAL_PREFERANCE, customerDetailsDTO.getUsername(), customerDetailsDTO.getCustId(), customerDetailsDTO.getMvnoId(), customerDetailsDTO.getEmail(), customerDetailsDTO.getBuId(), customerDetailsDTO.getRenewalForBooster(), customerDetailsDTO.getPhone());
            Gson gson = new Gson();
            gson.toJson(autoRenewalPreferenceMessage);
            kafkaMessageSender.send(new KafkaMessageData(autoRenewalPreferenceMessage, AutoRenewalPreferenceMessage.class.getSimpleName()));

            logger.info("Successfully updated renewalForBooster for custPlanMappingId={}", plan.getCustPlanMapppingId());
            response.put("status", "SUCCESS");
            response.put("message", "Plan updated successfully");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error occurred while updating renewalForBooster for custPlanMappingId={}: {}",
                    plan.getCustPlanMapppingId(), e.getMessage(), e);
            response.put("status", "FAILED");
            response.put("message", "Error updating plan: " + e.getMessage());
            return response;
        }
    }

    public Page<Customers> getListFromCustomer(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Integer mvnoId) {
        pageRequest = PageRequest.of(pageNumber - 1, customPageSize, Sort.by(sortBy).descending());
        QCustomers qCustomers = QCustomers.customers;
        BooleanExpression exp = qCustomers.isNotNull();
        if (mvnoId != 1) {
            exp = exp.and(qCustomers.mvnoId.eq(mvnoId));
        }
        List<String> statusList = new ArrayList<>();
        statusList.add(CommonConstants.CUSTOMER_STATUS.ACTIVE);
        statusList.add(CommonConstants.CUSTOMER_STATUS.NEW_ACTIVATION);
        statusList.add(CommonConstants.CUSTOMER_STATUS.INAVCTIVE);
        statusList.add(CommonConstants.CUSTOMER_STATUS.SUSPEND);
        exp = exp.and(qCustomers.status.in(statusList));
        List<Long> ids = customersService.getServiceAreaIdsList();
        if (getLoggedInUserId() != 1 && !ids.isEmpty()) {
            exp = exp.and(qCustomers.servicearea.id.in(ids));
        }
        if (null == filterList || 0 == filterList.size()) {
            return customersRepository.findAll(exp, pageRequest);
        } else {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Filter pass in list api", null);
        }
    }

    public CustomerPlansModel setMaxHoldDayAndAttempt(Integer custId, CustomerPlansModel customerPlansModel, Integer mvnoId) {
        List<NewCustPlanMappingPojo> custPlanMappingPojos = custPlanMappingRepo.findActivePlansWithVasFilter(custId);
        if (custPlanMappingPojos.isEmpty()) {
            List<VasPlan> vasPlanList = vasPlanRepository.findDefaultVasPlansByMvnoIdIn(Arrays.asList(mvnoId, 1));
            if (!vasPlanList.isEmpty()) {
                VasPlan selectedPlan = vasPlanList.stream().filter(p -> p.getMvnoId().equals(mvnoId)).findFirst().orElseGet(() -> vasPlanList.stream().filter(p -> p.getMvnoId().equals(1)).findFirst().orElse(null));
                if (selectedPlan != null) {
                    customerPlansModel.setMaxHoldAttempts(selectedPlan.getPauseTimeLimit());
                    customerPlansModel.setMaxHoldDurationDays(selectedPlan.getPauseDaysLimit());
                }
            }
            return customerPlansModel;
        } else {
            VasPlan vasPlan = vasPlanRepository.findById(custPlanMappingPojos.get(0).getVasPackId()).orElse(null);
            if (vasPlan != null) {
                customerPlansModel.setMaxHoldAttempts(vasPlan.getPauseTimeLimit());
                customerPlansModel.setMaxHoldDurationDays(vasPlan.getPauseDaysLimit());
            }
            return customerPlansModel;
        }
    }


    public void checkValidity(Integer custId,CustomerServiceMapping customerServiceMapping,DeactivatePlanReqDTOList reqDTOList) {
        try {
            List<NewCustPlanMappingPojo> planList = custPlanMappingRepository.findAllCurrentPlansByCustId(custId);
            Integer mvnoId = customersRepository.getCustomerMvnoIdByCustId(custId);
            List<VasPlan> vasPlanList = vasPlanRepository.findDefaultVasPlansByMvnoIdIn(Arrays.asList(mvnoId, 1));
            VasPlan vasPlan=null;
            if (planList.isEmpty()) {
                 vasPlan = vasPlanList.get(0);
            }
            Optional<Integer> vasPackId = planList.stream()
                    .map(NewCustPlanMappingPojo::getVasPackId)
                    .filter(Objects::nonNull)
                    .findFirst();
             vasPlan = vasPlanRepository.findById(vasPackId.get()).get();

            if ( Objects.nonNull(vasPlan.getPauseTimeLimit()) && Objects.nonNull(vasPlan.getPauseDaysLimit()) && vasPlan.getPauseTimeLimit() > 0 && vasPlan.getPauseTimeLimit() > 0) {
                boolean exceededAttempts = customerServiceMapping.getServiceHoldAttempts() >= vasPlan.getPauseTimeLimit();
                boolean exceededDuration =
                        (customerServiceMapping.getActualHoldDurationDays() +
                                ChronoUnit.DAYS.between(LocalDate.now().plusDays(1), reqDTOList.getServiceResumeDate()))
                                > vasPlan.getPauseDaysLimit();

                if (exceededAttempts || exceededDuration) {
                    throw new IllegalStateException("Hold limit exceeded: " +
                            (exceededAttempts ? "Max hold attempts reached. " : "") +
                            (exceededDuration ? "Max hold duration exceeded." : ""));
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
