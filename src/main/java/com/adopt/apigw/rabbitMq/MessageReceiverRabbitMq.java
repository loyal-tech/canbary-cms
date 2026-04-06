package com.adopt.apigw.rabbitMq;

import org.springframework.stereotype.Component;

//
//import com.adopt.apigw.MicroSeviceDataShare.PartnerAmountMessage;
//import com.adopt.apigw.MicroSeviceDataShare.SharedDataConstants.SharedDataConstants;
//import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.*;
//import com.adopt.apigw.core.dto.GenericDataDTO;
//import com.adopt.apigw.kafka.KafkaMessageData;
//import com.adopt.apigw.kafka.KafkaMessageSender;
//import com.adopt.apigw.model.common.CustomerPayment;
//import com.adopt.apigw.model.common.Customers;
//import com.adopt.apigw.model.common.StaffUser;
//import com.adopt.apigw.model.postpaid.*;
//import com.adopt.apigw.modules.Area.service.AreaService;
//import com.adopt.apigw.modules.BankManagement.service.BankManagementService;
//import com.adopt.apigw.modules.Branch.service.BranchService;
//import com.adopt.apigw.modules.BusinessUnit.service.BusinessUnitService;
//import com.adopt.apigw.modules.BusinessVerticals.Service.BusinessVerticalsService;
//import com.adopt.apigw.modules.CaseCustomerDetails.service.CaseCustometDetailsService;
//import com.adopt.apigw.modules.ChangePlanDTOs.CreditDebitDocMessage;
//import com.adopt.apigw.modules.FlutterWaveHelper.FlutterWaveService;
//import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustInvParamsService;
//import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
//import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
//import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingService;
//import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapingDTO;
//import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
//import com.adopt.apigw.modules.InventoryManagement.VendorManagment.VendorRepo;
//import com.adopt.apigw.modules.InventoryManagement.VendorManagment.VendorService;
//import com.adopt.apigw.modules.InventoryManagement.inward.InwardDto;
//import com.adopt.apigw.modules.InventoryManagement.inward.InwardServiceImpl;
//import com.adopt.apigw.modules.InventoryManagement.item.ItemDto;
//import com.adopt.apigw.modules.InventoryManagement.item.ItemServiceImpl;
//import com.adopt.apigw.modules.InventoryManagement.product.ProductController;
//import com.adopt.apigw.modules.InventoryManagement.product.ProductDto;
//import com.adopt.apigw.modules.InventoryManagement.product.ProductServiceImpl;
//import com.adopt.apigw.modules.InvestmentCode.service.InvestmentCodeService;
//import com.adopt.apigw.modules.Mvno.service.MvnoService;
//import com.adopt.apigw.modules.PaymentConfig.service.PaymentConfigService;
//import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaLocationMappingRepository;
//import com.adopt.apigw.modules.mvnoDocDetails.model.MvnoDocDetailsDTO;
//import com.adopt.apigw.modules.mvnoDocDetails.service.DocDetailsService;
//import com.adopt.apigw.modules.planUpdate.domain.CustomerPackage;
//import com.adopt.apigw.modules.planUpdate.repository.CustomerPackageRepository;
//import com.adopt.apigw.modules.role.service.RoleService;
//import com.adopt.apigw.modules.Pincode.service.PincodeService;
//import com.adopt.apigw.modules.Region.service.RegionService;
//import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
//import com.adopt.apigw.modules.SubBusinessUnit.Service.SubBusinessUnitService;
//import com.adopt.apigw.modules.SubBusinessVertical.Service.SubBusinessVerticalService;
////import com.adopt.apigw.modules.tickets.service.CaseService;
//import com.adopt.apigw.pojo.CustMilestoneDetailsPojo;
//import com.adopt.apigw.pojo.QuickInvoiceCreationPojo;
//import com.adopt.apigw.pojo.api.StaffUserPojo;
//import com.adopt.apigw.repository.postpaid.*;
//import com.adopt.apigw.repository.radius.CustomersRepository;
//import com.adopt.apigw.service.CustomerPaymentService;
//import com.adopt.apigw.service.common.*;
//import com.adopt.apigw.service.tacacs.Impl.AccessLevelGroupTacacsServiceImpl;
//import com.adopt.apigw.service.postpaid.*;
//import com.adopt.apigw.utils.CommonConstants;
//import com.adopt.apigw.utils.DateTimeUtil;
//import com.adopt.apigw.utils.StatusConstants;
//import com.netflix.discovery.converters.Auto;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.slf4j.MDC;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.adopt.apigw.core.utillity.log.ApplicationLogger;
//import com.adopt.apigw.model.lead.LeadMasterPojo;
//import com.adopt.apigw.modules.Teams.service.HierarchyService;
//import com.adopt.apigw.modules.Teams.service.TeamsService;
//import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
//import com.adopt.apigw.pojo.SendLeadDocConvertPojo;
//import com.adopt.apigw.pojo.api.CustomersPojo;
//import com.adopt.apigw.rabbitMq.message.*;
//import com.adopt.apigw.rabbitMq.message.LeadMasterPojoMessage;
//import com.adopt.apigw.service.LeadMasterService;
//import com.adopt.apigw.utils.APIConstants;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.util.CollectionUtils;
//
//import javax.transaction.Transactional;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.stream.Collectors;
//
@Component
public class MessageReceiverRabbitMq {
//    private static Log log = LogFactory.getLog(MessageReceiverRabbitMq.class);
//
//    @Autowired
//    CustomerPaymentService customerPaymentService;
////
////    @Autowired
////    RoleRepository roleRepository;
////
////    @Autowired
////    StaffRepository staffRepository;
////
////    @Autowired
////    RoleService roleService;
////
////    @Autowired
////    MvnoService mvnoService;
//
////    @Autowired
////    RoleScreensRepository roleScreensRepository;
//
//    @Autowired
//    ProductServiceImpl productService;
//
//    @Autowired
//    CustomersService customerService;
//
//    @Autowired
//    StaffUserService staffUserService;
//
//    @Autowired
//    PartnerService partnerService;
//
//    @Autowired
//    CustomerPackageRepository customerPackageRepository;
//
//    @Autowired
//    private DebitDocRepository debitDocRepository;
//
//    @Autowired
//    private CreditDocRepository creditDocRepository;
//
//    @Autowired
//    PostpaidPlanRepo postpaidPlanRepo;
//    @Autowired
//    HierarchyService hierarchyService;
//
//    @Autowired
//    CustomerCafAssignmentService customerCafAssignmentService;
//
//    @Autowired
//    TeamsService teamsService;
//
//    @Autowired
//    DbrService dbrService;
//
//    @Autowired
//    CustomerDocDetailsService customerDocDetailsService;
//    @Autowired
//    ClientServiceSrv clientServiceSrv;
//
//    @Autowired
//    CreditDocService creditDocService;
//
//    @Autowired
//    DebitDocService debitDocService;
//
//    @Autowired
//    private LeadMasterService leadMasterService;
//
//    @Autowired
//    PartnerCommissionService partnerCommissionService;
//
//    @Autowired
//    FlutterWaveService flutterWaveService;
//
//    @Autowired
//    VendorService vendorService;
//
////    @Autowired
////    CaseService caseService;
//
//    @Autowired
//    CustomerInventoryMappingRepo customerInventoryMappingRepo;
//
//    @Autowired
//    ItemServiceImpl itemService;
//
//    @Autowired
//    private CustMilestoneDetailsService custMilestoneDetailsService;
//
//    @Autowired
//    private ProductController productController;
//
//    @Autowired
//    private InwardServiceImpl inwardService;
//
//    @Autowired
//    private InOutWardMACService inOutWardMACService;
//
//    public static String user = null;
//
//    @Autowired
//    private MessageSender messageSender;
//    @Autowired
//    private KafkaMessageSender kafkaMessageSender;
//
//    @Autowired
//    private AccessLevelGroupTacacsServiceImpl accessLevelGroupTacacsService;
//
//    @Autowired
//    CaseCustometDetailsService caseCustometDetailsService;
//    @Autowired
//    MvnoService mvnoService;
//    @Autowired
//    RoleService roleService;
//
//    @Autowired
//    CustPlanMappingService custPlanMappingService;
//
//
//
//    @Autowired
//    CountryService countryService;
//
//    @Autowired
//    StateService stateService;
//
//    @Autowired
//    CityService cityService;
//
//    @Autowired
//    PincodeService pincodeService;
//
//    @Autowired
//    AreaService areaService;
//
//    @Autowired
//    ServiceAreaService serviceAreaService;
//
//    @Autowired
//    BusinessUnitService businessUnitService;
//
//    @Autowired
//    BranchService branchService;
//
//    @Autowired
//    RegionService regionService;
//
//    @Autowired
//    BusinessVerticalsService businessVerticalsService;
//
//    @Autowired
//    BankManagementService bankManagementService;
//
//    @Autowired
//    SubBusinessUnitService subBusinessUnitService;
//
//    @Autowired
//    SubBusinessVerticalService subBusinessVerticalService;
//
//    @Autowired
//    DepartmentService departmentService;
//
//    @Autowired
//    InvestmentCodeService investmentCodeService;
//
//    @Autowired
//    ChargeService chargeService;
//    @Autowired
//    DebitDocDetailRepository debitDocDetailRepository;
//
//    @Autowired
//    CustPlanMappingRepository custPlanMappingRepository;
//
//    @Autowired
//    CustomersRepository customersRepository;
//
//    @Autowired
//    private CustQuotaService custQuotaService;
//    @Autowired
//    CustomerInventoryMappingService customerInventoryMappingService;
//
//    @Autowired
//    private CustChargeDetailsRepository custChargeDetailsRepository;
//
//    @Autowired
//    private CustMacMapppingService custMacMapppingService;
//    @Autowired
//    private TrialDebitDocService trialDebitDocService;
//
//    @Autowired
//    private PaymentConfigService paymentConfigService;
//
//    @Autowired
//    private CustInvParamsService custInvParamsService;
//
//    @Autowired
//    private DocDetailsService docDetailsService;
//
//    @Autowired
//    private OTPManagmentService otpManagmentService;
//    @Autowired
//    private ServiceAreaLocationMappingRepository serviceAreaLocationMappingRepository;
//
//
//
//
////	@RabbitListener(queues = RabbitMqConstants.QUEUE_LOGIN_SUCCESS_WIFI)
////	public void receiveLoginSuccessMessage(LoginMessage message) {
////        setUserProperties(message.getTraceId(),message.getSpanId());
////		log.info("Received Message From RabbitMq : <" + message + ">");
////		user = message.getLoginData().get("userName").toString();
////		// emailService.sendEmailNotification(RabbitMqConstants.QUEUE_LOGIN_SUCCESS,
////		// message.getMessage(),
////		// message.getLoginData(), message.getSourceName());
////	}
//
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_STAFF_SUCCESS_WIFI)
////    public void receiveStaffList(StaffMessage message) {
////        setUserProperties(message.getTraceId(),message.getSpanId());
////        log.info("Received Message From RabbitMq : <" + message + ">");
////        staffService.saveRoleAndStaff(RabbitMqConstants.QUEUE_STAFF_SUCCESS_WIFI, message.getStaff(),
////                message.getRoleScreenList(), message.isUpdate(), message.isDelete(), message.getActualName());
////    }
//
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_ROLE_SUCCESS_WIFI)
////    public void receiveRoleList(RoleMessage message) {
////        setUserProperties(message.getTraceId(),message.getSpanId());
////        log.info("Received Message From RabbitMq : <" + message + ">");
////        roleService.saveRole(RabbitMqConstants.QUEUE_ROLE_SUCCESS_WIFI, message.getRole(), message.getRoleScreenList(),
////                message.isUpdate(), message.isDelete(), message.getRoleName());
////    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_UPDATE_QUOTA)
//    public void updateCustomerUsedQuota(CustomerQuotaInfo quotaInfo) {
//        log.info("Received Message From RabbitMq : <" + quotaInfo + ">");
//        customerService.updateCustomerPlanDetail(quotaInfo);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_RADIUS_CUST_MAC_ADD)
//    public void receiveMessageCustomerMAcApigw(CustMacMessage message) {
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            customerService.addMacFromRadius(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_DELETE_CUST_FROM_RADIUS)
////
////    public void receiveMessageToDeleteCustomer(CustomerMessage customerMessage) {
////        setUserProperties(customerMessage.getTraceId(),customerMessage.getSpanId());
////        log.info("Received Message From RabbitMq : <" + customerMessage + ">");
////        customerService.deleteCustomerFromRadius(customerMessage);
////    }
//
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_UPDATE_CUST_FROM_RADIUS)
////    public void receiveMessageToUpdateCustomer(CustomMessage message) {
////        setUserProperties(message.getTraceId(),message.getSpanId());
////        log.info("Received Message From RabbitMq : <" + message + ">");
////        customerService.updateCustomerFromRadius(message);
////    }
//
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_CHANGE_STATUS_WIFI)
////    public void receiveMessageToChangeStatus(CustomMessage message) {
////        setUserProperties(message.getTraceId(),message.getSpanId());
////        log.info("Received Message From RabbitMq : <" + message + ">");
////        customerService.updateRadiusCustomerStatus(message);
////    }
//
//    public void setUserProperties(String traceId, String spanId) {
////		if(message.getCurrentUser() != null)
////			MDC.put("userName", message.getCurrentUser());
//        if (traceId != null)
//            MDC.put("traceId", traceId);
//        if (spanId != null)
//            MDC.put("spanId", spanId);
//    }
//
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_COUNT_FOR_STAFF)
////    public void receiveCountfortheStaff(SendCountMessage message) {
////        this.staffUserService.geCountFromMessage(message);
////    }
//
////	@RabbitListener(queues = RabbitMqConstants.QUEUE_LEAD_MGMT_INIT_DATA)
////	public void receiveLeadDatafromCrm(SendSaveLeadData message) {
////		this.hierarchyService.LeadManagementWorkFlow(message.getLeadMgmtWfDTO());
////	}
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_APPROVER_UPDATE_DETAIL)
////    public void receiveLeadUpdateDatafromCrm(SendUpdateLeadData message) {
////        this.customerCafAssignmentService.updateCustomerLeadAssignment(message.getLeadFlowApproverUpdatedData());
////    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_LEAD_MGMT_INIT_DATA)
//    public void receiveLeadDataFromCRM(SendSaveLeadData message) {
//        this.hierarchyService.leadManagementWorkflowRequest(message.getLeadMgmtWfDTO());
//    }
//
//
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_LEAD_STATUS_INFO)
////    public void receiveLeadStatusInfoFromCrm(SendLeadStatusReq message) {
////        this.teamsService.getLeadTeamHierarchy(message.getLeadMgmtWfDTO());
////    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CUSTOMER_CAF_POJO)
//    public void receiveCustomerPojofromSalesCrms(CustomersPojo message) throws Exception {
//        try {
//            // receive lead Document
//            CustomersPojo customerPojo = this.customerService.save(message, "rf", false);
//            // get lead doc list
//            // get customer username
//            // craete new folder with username in "customerdoc" folder
//            // write loop for lead doc list and copy one by one doc in new folder.
//            // save customer caf doc list
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error("Unable to save Customer Caf rsponse:{}exception{}", APIConstants.FAIL,
//                    ex.getStackTrace());
//            throw ex;
//        }
//    }
//
////	@RabbitListener(queues = RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION)
////	public void receivePrepaidCustomerInvoiceChargesDetail(PrepaidInvoiceCharges message) {
////		ApplicationLogger.logger.info("RabbitMq receive Start receivePrepaidCustomerInvoiceChargesDetail() ",
////				APIConstants.SUCCESS, message);
////		try {
////			String orgainzationCustomerUsername = clientServiceSrv.getValueByName("ORGANIZATION");
////			if (message.getCustomerUsername().equalsIgnoreCase(orgainzationCustomerUsername)) {
////				debitDocService.sendOrganizatonBillForApprovAL(message.getInvoiceId());
////				message.setOrgCust(true);
////			}
////			if (message.isOrgCust())
////				dbrService.addDbrForOrgCustomerPrepaid(message);
////			else
////				dbrService.addDbrForPrepaidCustomer(message);
////		} catch (Exception e) {
////			ApplicationLogger.logger.error("RabbitMq receive Error receivePrepaidCustomerInvoiceChargesDetail() ",
////					APIConstants.FAIL, e.getStackTrace());
////		}
////
////		ApplicationLogger.logger.info("RabbitMq receive End receivePrepaidCustomerInvoiceChargesDetail() ",
////				APIConstants.SUCCESS, message);
////	}
//
////	@RabbitListener(queues = RabbitMqConstants.QUEUE_POSTPAID_CUSTOMER_INVOICE_CREATION)
////	public void receivePostpaidCustomerInvoiceChargesDetail(PostpaidInvoiceCharges message) {
////		ApplicationLogger.logger.info("RabbitMq receive Start receivePostpaidCustomerInvoiceChargesDetail() ",
////				APIConstants.SUCCESS, message);
////
////		try {
////			String orgainzationCustomerUsername = clientServiceSrv.getValueByName("ORGANIZATION");
////			if (message.getCustomerUsername().equalsIgnoreCase(orgainzationCustomerUsername)) {
////				debitDocService.sendOrganizatonBillForApprovAL(message.getInvoiceId());
////				message.setOrgCust(true);
////			}
////			if (message.isOrgCust())
////				dbrService.addDbrForOrgCustomerPostpaid(message);
////			else
////				dbrService.addDbrForPostpaidCustomer(message);
////		} catch (Exception e) {
////			ApplicationLogger.logger.error("RabbitMq receive Error receivePostpaidCustomerInvoiceChargesDetail() ",
////					APIConstants.FAIL, e.getStackTrace());
////		}
////
////		ApplicationLogger.logger.info("RabbitMq receive End receivePostpaidCustomerInvoiceChargesDetail() ",
////				APIConstants.SUCCESS, message);
////	}
//
////	@RabbitListener(queues = RabbitMqConstants.QUEUE_POSTPAID_CUSTOMER_INVOICE_DIRECT_CHARGE)
////	public void receivePostpaidCustomerInvoiceDirectChargeDetails(PostpaidInvoiceCharges message) {
////		ApplicationLogger.logger.info("RabbitMq receive Start receivePostpaidCustomerInvoiceDirectChargeDetails() ",
////				APIConstants.SUCCESS, message);
////
////		try {
////			dbrService.addDbrForPostpaidCustomerDirectCharge(message);
////		} catch (Exception e) {
////			ApplicationLogger.logger.error(
////					"RabbitMq receive Error receivePostpaidCustomerInvoiceDirectChargeDetails() ", APIConstants.FAIL,
////					e.getStackTrace());
////		}
////
////		ApplicationLogger.logger.info("RabbitMq receive End receivePostpaidCustomerInvoiceDirectChargeDetails() ",
////				APIConstants.SUCCESS, message);
////	}
////
////	@RabbitListener(queues = RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_DIRECT_CHARGE)
////	public void receivePrepaidCustomerInvoiceDirectChargeDetails(PrepaidInvoiceCharges message) {
////		ApplicationLogger.logger.info("RabbitMq receive Start receivePrepaidCustomerInvoiceDirectChargeDetails() ",
////				APIConstants.SUCCESS, message);
////
////		try {
////			dbrService.addDbrForPrepaidCustomerDirectCharge(message);
////		} catch (Exception e) {
////			ApplicationLogger.logger.error("RabbitMq receive Error receivePrepaidCustomerInvoiceDirectChargeDetails() ",
////					APIConstants.FAIL, e.getStackTrace());
////		}
////
////		ApplicationLogger.logger.info("RabbitMq receive End receivePrepaidCustomerInvoiceDirectChargeDetails() ",
////				APIConstants.SUCCESS, message);
////	}
//
////	@RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_INVOICE_INVENTORY_CHARGE)
////	public void receiveCustomerInvoiceInventoryChargeDetails(PrepaidInvoiceCharges message) {
////		ApplicationLogger.logger.info("RabbitMq receive Start receiveCustomerInvoiceInventoryChargeDetail() ",
////				APIConstants.SUCCESS, message);
////
////		try {
////			dbrService.addDbrForCustomerInventoryCharge(message);
////		} catch (Exception e) {
////			ApplicationLogger.logger.error("RabbitMq receive Error receiveCustomerInvoiceInventoryChargeDetail() ",
////					APIConstants.FAIL, e.getStackTrace());
////		}
////
////		ApplicationLogger.logger.info("RabbitMq receive End receiveCustomerInvoiceInventoryChargeDetail() ",
////				APIConstants.SUCCESS, message);
////	}
//
////	@RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_SEND_LEAD_DOC_CONVERT)
////	public void receiveLeadDocDetails(SendLeadDocConvertPojo message) {
////		ApplicationLogger.logger.info("RabbitMq receive Start receiveLeadDocDetails() ", APIConstants.SUCCESS, message);
////
////		try {
////			if (message != null && message.getCustomerDocDetailsDTOList() != null
////					&& message.getCustomerDocDetailsDTOList().size() > 0) {
////				customerDocDetailsService.saveCustDocFromLeadDoc(message.getCustomerDocDetailsDTOList());
////			}
////		} catch (Exception e) {
////			ApplicationLogger.logger.error("RabbitMq receive Error receiveLeadDocDetails() ", APIConstants.FAIL,
////					e.getStackTrace());
////		}
////
////		ApplicationLogger.logger.info("RabbitMq receive End receiveLeadDocDetails() ", APIConstants.SUCCESS, message);
////	}
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_SEND_LEAD_MASTER)
//    public void receiveLeadMaster(LeadMasterPojoMessage message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receiveLeadMaster() ", APIConstants.SUCCESS, message);
//        try {
//            this.leadMasterService.save(new LeadMasterPojo(message));
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receiveLeadMaster() ", APIConstants.FAIL,
//                    e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receiveLeadMaster() ", APIConstants.SUCCESS, message);
//    }
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CUSTOMER_CAF_POJO)
////    public void receiveCustomerPojofromSalesCrms(CustomersPojo message) throws Exception {
////        try {
////            //receive lead Document
////            CustomersPojo customerPojo = this.customerService.save(message, "rf", false);
////            // get lead doc list
////            // get customer username
////            // craete new folder with username in "customerdoc" folder
////            // write loop for lead doc list and copy one by one doc in new folder.
////            // save customer caf doc list
////
////        } catch (Exception ex) {
////            ApplicationLogger.logger.error("Unable to save Customer Caf rsponse:{}exception{}", APIConstants.FAIL,
////                    ex.getStackTrace());
////            throw ex;
////        }
////    }
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION)
//    public void receivePrepaidCustomerInvoiceChargesDetail(PrepaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receivePrepaidCustomerInvoiceChargesDetail() ", APIConstants.SUCCESS, message);
//        try {
//            DebitDocument debitDocument= null;
//            if(message.getIsPaymentApproved()==false)
//            {
//                Customers customers=customersRepository.findById(message.getCustId()).get();
//                customers.setWalletbalance(message.getWalletBalance());
//                if (message.getNextBilldate()!=null && !message.getNextBilldate().isEmpty()){
//                    LocalDate nextBilldate = LocalDate.parse(message.getNextBilldate());
//                    customers.setNextBillDate(nextBilldate);
//                    if (message.getChildIdNextBillDatePair()!=null && message.getChildIdNextBillDatePair().size()>0){
//                        List<Customers> childCusts = new ArrayList<>();
//                        for (Map.Entry<Integer, String> childIdAndDate : message.getChildIdNextBillDatePair()) {
//                                    LocalDateTime nextBillDate = DateTimeUtil.getLocaldateTimefromString(childIdAndDate.getValue());
//                                    Customers childCustomer = customersRepository.findById(childIdAndDate.getKey()).get();
//                                    childCustomer.setNextBillDate(nextBillDate.toLocalDate());
//                                    childCusts.add(childCustomer);
//                        }
//                        customersRepository.saveAll(childCusts);
//                    }
//                }
//                debitDocService.sendBillGenData(Math.toIntExact(message.getInvoiceId()),false);
//                String orgainzationCustomerUsername = clientServiceSrv.getValueByName("ORGANIZATION");
//                if (message.getIsCaf() == null) {
//                    message.setIsCaf("false");
//                }
//                if (message.getCustomerUsername() != null || message.getCustomerName() != null) {
//                    debitDocument=this.getdebitDocument(message.getDebitDocument());
//                    debitDocument.setCreatedById(message.getLoggedInUserId());
//                    debitDocument.setNextStaff(message.getLoggedInUserId());
//                    debitDocument.setLastModifiedById(message.getLoggedInUserId());
//                    debitDocument.setDebitDocumentTAXRels(null);
//                    debitDocument.setCustomer(customers);
//                    debitDocument.setPaymentStatus(message.getPaymentStatus());
//                    debitDocument.setBillrunid(message.getBillRunId());
//                    debitDocument.setCreatedByName(message.getCreatedByName());
//                    debitDocument.setIsDirectChargeInvoice(message.getIsDirectChargeInvoice());
//                    debitDocument.setRemarks(message.getDebitDocument().getRemarks());
//                    Double totaaldays=0.0;
//
//                    if(customers!=null)
//                        debitDocument.setBuId(customers.getBuId());
//                    if (debitDocument.getCustpackrelid()!=null  ) {
//                        CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(debitDocument.getCustpackrelid());
//                        if(custPlanMappping!=null && Objects.nonNull(custPlanMappping)) {
//                            custPlanMappping.setDebitdocid(debitDocument.getId().longValue());
//                            custPlanMappingRepository.save(custPlanMappping);
//                            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
//                            totaaldays = postpaidPlan.getValidity();
//                            debitDocument.setPostpaidPlan(postpaidPlan);
//                        }
//                    }
//
//                    if(debitDocument.getCreatedById()==null)
//                    {
//                        debitDocument.setCreatedById(customers.getCreatedById());
//                        debitDocument.setLastModifiedById(customers.getLastModifiedById());
//                    }
//                    debitDocument  = debitDocRepository.save(debitDocument);
//                    List<DebitDocDetails> updatedList = new ArrayList<>();
//                    if(message.getDebitDocument().getDebitDocDetailsList()!=null){
//                        updatedList = this.getDebitdocDetails(message.getDebitDocument().getDebitDocDetailsList());
//                        debitDocDetailRepository.saveAll(updatedList);
//                    }
//
//                    //               debitDocDetailRepository.saveAll(debitDocument.getDebitDocDetailsList());
//                    if (message.getCustomerUsername().equalsIgnoreCase(orgainzationCustomerUsername) || message.getCustomerName().equalsIgnoreCase(orgainzationCustomerUsername)) {
//                        debitDocService.sendOrganizatonBillForApprovAL(message.getInvoiceId(), message.getLoggedInUserId(),debitDocument);
//                        message.setOrgCust(true);
//                    }
//                    if (message.getDebitDocument()!=null && message.getDebitDocument().getUpdateDebitDpcDetailsIds()!=null){
//                        updateDebitDocDetailsForMvnoInoivce(message.getDebitDocument().getUpdateDebitDpcDetailsIds(), debitDocument.getId());
//
//                    }
//                }
//
//                if (message.getOldDebitDocumentId() != null) {
//                    if (message.getOldDebitDocumentId().size() > 0 && !message.getIsCaf().equals("true")) {
//                        debitDocService.adjustOldDebitDocument(message.getInvoiceId(), message.getOldDebitDocumentId());
//                    }
//                }
//
//                if (message.getInventoryMappingId() != null) {
//                    Optional<CustomerInventoryMapping> customerInventoryMapping = customerInventoryMappingRepo.findById(message.getInventoryMappingId());
//                    if (customerInventoryMapping.isPresent()) {
//                        Optional<DebitDocument> debitDocuments = debitDocRepository.findById(Math.toIntExact(message.getInvoiceId().intValue()));
//                        Long itemId = customerInventoryMapping.get().getItemId();
//                        //itemService.changeItemWarrantyStatus(message.getInventoryMappingId(), itemId, debitDocuments.get().getBilldate());
//                    }
//                }
//                customersRepository.save(customers);
//                List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
//                if (message.getCustPackAndDebitDocIdPair().size()>0) {
//                    for (Map.Entry<Integer, Long> custPackAndDebitDocIdPair : message.getCustPackAndDebitDocIdPair()) {
//                        CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(custPackAndDebitDocIdPair.getKey().intValue());
//                        if(custPackAndDebitDocIdPair.getValue()!=null)
//                        {
//                            custPlanMappping.setDebitdocid(custPackAndDebitDocIdPair.getValue());
//                        }else{
//                            custPlanMappping.setDebitdocid(debitDocument.getId().longValue());
//                        }
//                        custPlanMapppingList.add(custPlanMappping);
//                    }
//
//                    if (message.getCustPackAndEndDatePair().size()>0){
//                        for (Map.Entry<Integer, String> custPackAndEndDate : message.getCustPackAndEndDatePair()) {
//                            for (CustPlanMappping custPlanMappping : custPlanMapppingList){
//                                if(custPlanMappping.getId().equals(custPackAndEndDate.getKey())){
//                                    LocalDateTime endDate = DateTimeUtil.getLocaldateTimefromString(custPackAndEndDate.getValue());
//                                    custPlanMappping.setEndDate(endDate);
//                                    custPlanMappping.setExpiryDate(endDate);
//                                }
//                            }
//                        }
//                    }
//                    custPlanMappingRepository.saveAll(custPlanMapppingList);
//                }
//            }
//            if(debitDocument == null) {
//                debitDocument=debitDocRepository.findById(message.getDebitDocument().getId()).orElse(null);
//            }
//            if(debitDocument!=null && message.getDebitDocument().getBillrunstatus().equalsIgnoreCase("Cancelled")){
//                List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllByDebitdocid(Long.valueOf(debitDocument.getId()));
//                if(!CollectionUtils.isEmpty(custChargeDetailsList)) {
//                    for (CustChargeDetails custChargeDetails: custChargeDetailsList) {
//                        custChargeDetails.setEnddate(debitDocument.getCreatedate().minusMinutes(1));
//                        custChargeDetails.setIsDeleted(true);
//                        custChargeDetailsRepository.save(custChargeDetails);
//                    }
//                }
//            }
//            if(debitDocument!=null &&  message.getPaymentStatus()!=null && message.getPaymentStatus().equalsIgnoreCase("Cancelled")){
//                List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllByDebitdocid(Long.valueOf(debitDocument.getId()));
//                if(!CollectionUtils.isEmpty(custChargeDetailsList)) {
//                    for (CustChargeDetails custChargeDetails: custChargeDetailsList) {
//                        custChargeDetails.setEnddate(debitDocument.getCreatedate().minusMinutes(1));
//                        custChargeDetails.setIsDeleted(true);
//                        custChargeDetailsRepository.save(custChargeDetails);
//                    }
//                }
//            }
//            if(debitDocument != null && message.getIsVoid() != null && message.getIsVoid()) {
//                List<CustPlanMappping> mapppings = custPlanMappingRepository.findAllByDebitdocumentid(debitDocument.getId().longValue());
//                debitDocument.setBillrunstatus("VOID");
//                debitDocRepository.save(debitDocument);
//                if (!CollectionUtils.isEmpty(mapppings)) {
//                    for (CustPlanMappping mapping : mapppings) {
////                            mapping.setIsDelete(true);
//                        mapping.setIsVoid(Boolean.TRUE);
//                        mapping.setEndDate(LocalDateTime.now());
//                        mapping.setExpiryDate(LocalDateTime.now());
//                        if (mapping.getStartDate().isAfter(mapping.getEndDate())) {
//                            mapping.setStartDate(LocalDateTime.now());
//                            mapping.setEndDate(mapping.getStartDate().plusSeconds(1));
//                            mapping.setExpiryDate(mapping.getStartDate().plusSeconds(1));
//                        }
//                        mapping.setCustPlanStatus(CommonConstants.STOP_STATUS);
//                        custPlanMappingService.save(mapping, CommonConstants.CHANGE_PLAN_MSG);
//                    }
//                }
//            }
//
//            else
//            {
//                Customers customers=customersRepository.findById(message.getCustId()).get();
//                customers.setWalletbalance(message.getWalletBalance());
//                if(message.getDebitDocument()!=null && message.getDebitDocument().getId()!=null)
//                {
//                     DebitDocument document=debitDocRepository.findById(message.getDebitDocument().getId()).orElse(null);
//                    document.setAdjustedAmount(message.getAdjustedAmount());
//                    document.setBillrunstatus(message.getBillRunStatus());
//                    document.setPaymentStatus(message.getPaymentStatus());
//                    if(document.getPaymentStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID)) {
//                        document.setStatus(CommonConstants.DEBIT_DOC_STATUS.APPROVED);
//                    }
//                    document.setRemarks(message.getDebitDocument().getRemarks());
//                    debitDocRepository.save(document);
//
//
//                    if(document.getPaymentStatus().equalsIgnoreCase("Fully Paid") ){
//                        List<CustPlanMappping> custPlanMapppingList1=custPlanMappingRepository.findAllByDebitdocid(document.getId());
//                        List<Integer> cprIds = custPlanMapppingList1.stream().filter(list->list.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE )).map(mappping -> mappping.getId() ).collect(Collectors.toList());
//                        customerDocDetailsService.changeStatusDisableToActive(cprIds);
//                    }
//
//                }
//            }
//            if(!CollectionUtils.isEmpty(message.getChargeIds()) && debitDocument != null) {
//                List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllById(message.getChargeIds());
//                if(!CollectionUtils.isEmpty(custChargeDetailsList)) {
//                    DebitDocument finalDebitDocument = debitDocument;
//                    custChargeDetailsList = custChargeDetailsList.stream().peek(custChargeDetails -> custChargeDetails.setDebitdocid(Long.valueOf(finalDebitDocument.getId()))).collect(Collectors.toList());
//                    custChargeDetailsRepository.saveAll(custChargeDetailsList);
//                }
//            }
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receivePrepaidCustomerInvoiceChargesDetail() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receivePrepaidCustomerInvoiceChargesDetail() ", APIConstants.SUCCESS, message);
//    }
//
//    public void updateDebitDocDetailsForMvnoInoivce(List<Integer> debitDocDetailsIds, Integer mvnoDocId) {
//        try {
//            List<DebitDocDetails> debitDocDetails = debitDocDetailRepository.findAllByDebitdocdetailidIn(debitDocDetailsIds);
//            debitDocDetails= debitDocDetails.stream().peek(i->i.setMvnodebitdocumentid(mvnoDocId)).collect(Collectors.toList());
//            debitDocDetailRepository.saveAll(debitDocDetails);
//        } catch (Exception ex) {
//            log.error("Exception to update mvnoDocId in customer invoices: "+ex.getMessage());
//        }
//    }
//
//    private DebitDocument getdebitDocument(DebitDocument debitDocument) {
//        DebitDocument debitDocument1=new DebitDocument();
//        debitDocument1.setId(debitDocument.getId());
//        debitDocument1.setDocument(debitDocument.getDocument());
//        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        debitDocument1.setLocalenddate( debitDocument.getLocalenddate());
//        debitDocument1.setLocalstartdate(debitDocument.getLocalstartdate());
//        debitDocument1.setStartdate(LocalDateTime.parse(debitDocument.getLocalstartdate(),formatter));
//
////        debitDocument1.setLocalenddate(debitDocument.getEndate().format(formatter).toString());
//        debitDocument1.setDuedate(LocalDateTime.parse(debitDocument.getDuedateString(),formatter));
//      //  debitDocument1.setLatepaymentdateString(debitDocument.getLatepaymentdate().format(formatter).toString());
//        debitDocument1.setSubtotal(debitDocument.getSubtotal());
//        debitDocument1.setTax(debitDocument.getTax());
//        debitDocument1.setDiscount(debitDocument.getDiscount());
//        debitDocument1.setTotalamount(debitDocument.getTotalamount());
//        debitDocument1.setPreviousbalance(debitDocument.getPreviousbalance());
//        debitDocument1.setLatepaymentfee(debitDocument.getLatepaymentfee());
//        debitDocument1.setCurrentpayment(debitDocument.getCurrentpayment());
//        debitDocument1.setCurrentdebit(debitDocument.getCurrentdebit());
//        debitDocument1.setCurrentcredit(debitDocument.getCurrentcredit());
//        debitDocument1.setTotaldue(debitDocument.getTotaldue());
//        //       debitDocument.setTotalamountinwords(debitDocument.getAmountinwords());
//        // debitDocument.dueinwords=debitDocument.getDueinwords();
//        debitDocument1.setBillrunid(debitDocument.getBillrunid());
//        debitDocument1.setBillrunstatus(debitDocument.getBillrunstatus());
//        debitDocument1.setStatus(debitDocument.getStatus());
//        debitDocument1.setIsDelete(debitDocument.getIsDelete());
//        debitDocument1.setCstchargeid(debitDocument.getCstchargeid());
//        debitDocument1.setPaymentowner(debitDocument.getPaymentowner());
//        debitDocument1.setDebitDocumentTAXRels(debitDocument.getDebitDocumentTAXRels());
//
////        debitDocument1.setDebitDocDetailsList(updatedList);
//        debitDocument1.setDocnumber(debitDocument.getDocnumber());
//        //debitDocument1.setTotalCustomerDiscount(debitDocument.getCustomer().getId().doubleValue());
//        debitDocument1.setDocnumber(debitDocument.getDocnumber());
//        debitDocument1.setCustRefName(debitDocument.getCustRefName());
//        debitDocument1.setCustpackrelid(debitDocument.getCustpackrelid());
//        debitDocument1.setEndate(LocalDateTime.parse(debitDocument.getLocalenddate(),formatter));
//        debitDocument1.setRemarks(debitDocument.getRemarks());
//        debitDocument.setUpdateDebitDpcDetailsIds(debitDocument.getUpdateDebitDpcDetailsIds());
//        //  debitDocument.inventoryMappingId=debitDocument.getInventoryMappingId();
//        return debitDocument1;
//    }
//
//    private List<DebitDocDetails> getDebitdocDetails(List<DebitDocDetails> debitDocDetailsList) {
//        List<DebitDocDetails> updatedList = new ArrayList<>();
//        for(DebitDocDetails debitDocDetail : debitDocDetailsList) {
//            debitDocDetail.setStartdate(null);
//            debitDocDetail.setEnddate(null);
//            updatedList.add(debitDocDetail);
//        }
//        return updatedList;
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_POSTPAID_CUSTOMER_INVOICE_CREATION)
//    public void receivePostpaidCustomerInvoiceChargesDetail(PostpaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receivePostpaidCustomerInvoiceChargesDetail() ", APIConstants.SUCCESS, message);
//        ApplicationLogger.logger.info("RabbitMq receive Start receivePostpaidCustomerInvoiceChargesDetail() ", APIConstants.SUCCESS, message);
//
//        try {
//            String orgainzationCustomerUsername = clientServiceSrv.getValueByName("ORGANIZATION");
//            if (message.getCustomerUsername().equalsIgnoreCase(orgainzationCustomerUsername)) {
//                debitDocService.sendOrganizatonBillForApprovAL(message.getInvoiceId(), message.getLoggedInUserId());
//                message.setOrgCust(true);
//            }
//            if (message.isOrgCust())
//                dbrService.addDbrForOrgCustomerPostpaid(message);
//            else
//                dbrService.addDbrForPostpaidCustomer(message);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receivePostpaidCustomerInvoiceChargesDetail() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receivePostpaidCustomerInvoiceChargesDetail() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_POSTPAID_CUSTOMER_INVOICE_DIRECT_CHARGE)
//    public void receivePostpaidCustomerInvoiceDirectChargeDetails(PostpaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receivePostpaidCustomerInvoiceDirectChargeDetails() ", APIConstants.SUCCESS, message);
//
//        try {
//            dbrService.addDbrForPostpaidCustomerDirectCharge(message);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receivePostpaidCustomerInvoiceDirectChargeDetails() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receivePostpaidCustomerInvoiceDirectChargeDetails() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_DIRECT_CHARGE)
//    public void receivePrepaidCustomerInvoiceDirectChargeDetails(PrepaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receivePrepaidCustomerInvoiceDirectChargeDetails() ", APIConstants.SUCCESS, message);
//
//        try {
//            String orgainzationCustomerUsername = clientServiceSrv.getValueByName("ORGANIZATION");
//            if (message.getCustomerName() != null && message.getCustomerName().equalsIgnoreCase(orgainzationCustomerUsername)) {
//                debitDocService.sendOrganizatonBillForApprovAL(message.getInvoiceId(), message.getLoggedInUserId());
//                message.setOrgCust(true);
//            }
//
//            if (message.isOrgCust())
//                dbrService.addDbrForOrgCustomerDirectChargeForPrepaid(message);
//            else
//                dbrService.addDbrForPrepaidCustomerDirectCharge(message);
//
//            if (message.getOldDebitDocumentId().size() > 0) {
//                debitDocService.adjustOldDebitDocument(message.getInvoiceId(), message.getOldDebitDocumentId());
//            }
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receivePrepaidCustomerInvoiceDirectChargeDetails() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receivePrepaidCustomerInvoiceDirectChargeDetails() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_INVOICE_INVENTORY_CHARGE)
//    public void receiveCustomerInvoiceInventoryChargeDetails(PrepaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receiveCustomerInvoiceInventoryChargeDetail() ", APIConstants.SUCCESS, message);
//
//        try {
//            dbrService.addDbrForCustomerInventoryCharge(message);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receiveCustomerInvoiceInventoryChargeDetail() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receiveCustomerInvoiceInventoryChargeDetail() ", APIConstants.SUCCESS, message);
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_SEND_LEAD_DOC_CONVERT)
//    public void receiveLeadDocDetails(SendLeadDocConvertPojo message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receiveLeadDocDetails() ", APIConstants.SUCCESS, message);
//
//        try {
//            if (message != null && message.getCustomerDocDetailsDTOList() != null && message.getCustomerDocDetailsDTOList().size() > 0) {
//                customerDocDetailsService.saveCustDocFromLeadDoc(message.getCustomerDocDetailsDTOList());
//            }
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receiveLeadDocDetails() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receiveLeadDocDetails() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_INVOICE)
//    public void receivePartnerInvoiceCreation(PrepaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receiveLeadMaster() ", APIConstants.SUCCESS, message);
//        try {
//            partnerCommissionService.adjustInvoiceAmount(message.getTotalInvoiceAmount(), message.getInvoiceId());
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receiveLeadMaster() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receiveLeadMaster() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_NASUPDATE)
//    public void receiveNasupdate(NasUpdateMessage message) {
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION)
//    public void receivePrepaidCustomerInvoiceChargesDetail(PrepaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receivePrepaidCustomerInvoiceChargesDetail() ", APIConstants.SUCCESS, message);
//        try {
//            DebitDocument debitDocument= null;
//
//            if(message.getIsPaymentApproved()==false)
//            {
//                Customers customers=customersRepository.findById(message.getCustId()).get();
//                customers.setWalletbalance(message.getWalletBalance());
//                if (message.getNextBilldate()!=null && !message.getNextBilldate().isEmpty())
//                {
//                    LocalDate nextBilldate = LocalDate.parse(message.getNextBilldate());
//                    customers.setNextBillDate(nextBilldate);
//                    if (message.getChildIdNextBillDatePair()!=null && message.getChildIdNextBillDatePair().size()>0)
//                    {
//                        List<Customers> childCusts = new ArrayList<>();
//                        for (Map.Entry<Integer, String> childIdAndDate : message.getChildIdNextBillDatePair()) {
//                                    LocalDateTime nextBillDate = DateTimeUtil.getLocaldateTimefromString(childIdAndDate.getValue());
//                                    Customers childCustomer = customersRepository.findById(childIdAndDate.getKey()).get();
//                                    childCustomer.setNextBillDate(nextBillDate.toLocalDate());
//                                    childCusts.add(childCustomer);
//                        }
//                        customersRepository.saveAll(childCusts);
//                    }
//                }
//
//                debitDocService.sendBillGenData(Math.toIntExact(message.getInvoiceId()),false);
//                String orgainzationCustomerUsername = clientServiceSrv.getValueByNameAndmvnoId("ORGANIZATION",customers.getMvnoId());
//
//                if (message.getIsCaf() == null)
//                    message.setIsCaf("false");
//
//                if (message.getCustomerUsername() != null || message.getCustomerName() != null)
//                {
//                    debitDocument=this.getdebitDocument(message.getDebitDocument());
//                    debitDocument.setCreatedById(message.getLoggedInUserId());
//                    debitDocument.setNextStaff(message.getLoggedInUserId());
//                    debitDocument.setLastModifiedById(message.getLoggedInUserId());
//                    debitDocument.setDebitDocumentTAXRels(null);
//                    debitDocument.setCustomer(customers);
//                    debitDocument.setPaymentStatus(message.getPaymentStatus());
//                    debitDocument.setBillrunid(message.getBillRunId());
//                    debitDocument.setCreatedByName(message.getCreatedByName());
//                    debitDocument.setIsDirectChargeInvoice(message.getIsDirectChargeInvoice());
//                    debitDocument.setRemarks(message.getDebitDocument().getRemarks());
//                    debitDocument.setBillrunstatus(message.getBillRunStatus());
//
//                    if(customers!=null)
//                        debitDocument.setBuId(customers.getBuId());
//                    if (debitDocument.getCustpackrelid()!=null  ) {
//                        CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(debitDocument.getCustpackrelid());
//                        if(custPlanMappping!=null && Objects.nonNull(custPlanMappping)) {
//                            custPlanMappping.setDebitdocid(debitDocument.getId().longValue());
//                            custPlanMappingRepository.save(custPlanMappping);
//                            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
//                            debitDocument.setPostpaidPlan(postpaidPlan);
//                        }
//                    }
//
//                    if(debitDocument.getCreatedById()==null)
//                    {
//                        debitDocument.setCreatedById(customers.getCreatedById());
//                        debitDocument.setLastModifiedById(customers.getLastModifiedById());
//                    }
//
//                    debitDocument  = debitDocRepository.save(debitDocument);
//                    List<DebitDocDetails> updatedList = new ArrayList<>();
//                    if(message.getDebitDocument().getDebitDocDetailsList()!=null){
//                        updatedList = this.getDebitdocDetails(message.getDebitDocument().getDebitDocDetailsList());
//                        debitDocDetailRepository.saveAll(updatedList);
//                    }
//
//                    if (message.getCustomerUsername().equalsIgnoreCase(orgainzationCustomerUsername) || message.getCustomerName().equalsIgnoreCase(orgainzationCustomerUsername)) {
//                        debitDocService.sendOrganizatonBillForApprovAL(message.getInvoiceId(), message.getLoggedInUserId(),debitDocument);
//                        message.setOrgCust(true);
//                    }
//
//                    if (message.getDebitDocument()!=null && message.getDebitDocument().getUpdateDebitDpcDetailsIds()!=null)
//                        updateDebitDocDetailsForMvnoInoivce(message.getDebitDocument().getUpdateDebitDpcDetailsIds(), debitDocument.getId());
//                }
//
//                if (message.getOldDebitDocumentId() != null) {
//                    if (message.getOldDebitDocumentId().size() > 0 && !message.getIsCaf().equals("true")) {
//                        debitDocService.adjustOldDebitDocument(message.getInvoiceId(), message.getOldDebitDocumentId());
//                    }
//                }
//
//                customersRepository.save(customers);
//
//                List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
//                if (message.getCustPackAndDebitDocIdPair().size()>0)
//                {
//                    for (Map.Entry<Integer, Long> custPackAndDebitDocIdPair : message.getCustPackAndDebitDocIdPair()) {
//                        CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(custPackAndDebitDocIdPair.getKey().intValue());
//                        if(custPackAndDebitDocIdPair.getValue()!=null)
//                        {
//                            custPlanMappping.setDebitdocid(custPackAndDebitDocIdPair.getValue());
//                        }else{
//                            custPlanMappping.setDebitdocid(debitDocument.getId().longValue());
//                        }
//                        custPlanMapppingList.add(custPlanMappping);
//                    }
//
//                    if (message.getCustPackAndEndDatePair().size()>0){
//                        for (Map.Entry<Integer, String> custPackAndEndDate : message.getCustPackAndEndDatePair()) {
//                            for (CustPlanMappping custPlanMappping : custPlanMapppingList){
//                                if(custPlanMappping.getId().equals(custPackAndEndDate.getKey())){
//                                    LocalDateTime endDate = DateTimeUtil.getLocaldateTimefromString(custPackAndEndDate.getValue());
//                                    custPlanMappping.setEndDate(endDate);
//                                    custPlanMappping.setExpiryDate(endDate);
//                                }
//                            }
//                        }
//                    }
//                    custPlanMappingRepository.saveAll(custPlanMapppingList);
//                }
//            }
//
//            if(debitDocument == null)
//                debitDocument=debitDocRepository.findById(message.getDebitDocument().getId()).orElse(null);
//
//            if(debitDocument!=null && message.getDebitDocument().getBillrunstatus().equalsIgnoreCase("Cancelled")){
//                List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllByDebitdocid(Long.valueOf(debitDocument.getId()));
//                if(!CollectionUtils.isEmpty(custChargeDetailsList)) {
//                    for (CustChargeDetails custChargeDetails: custChargeDetailsList) {
//                        custChargeDetails.setEnddate(debitDocument.getCreatedate().minusMinutes(1));
//                        custChargeDetails.setIsDeleted(true);
//                        custChargeDetailsRepository.save(custChargeDetails);
//                    }
//                }
//            }
//
//            if(debitDocument!=null &&  message.getPaymentStatus()!=null && message.getPaymentStatus().equalsIgnoreCase("Cancelled")){
//                List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllByDebitdocid(Long.valueOf(debitDocument.getId()));
//                if(!CollectionUtils.isEmpty(custChargeDetailsList)) {
//                    for (CustChargeDetails custChargeDetails: custChargeDetailsList) {
//                        custChargeDetails.setEnddate(debitDocument.getCreatedate().minusMinutes(1));
//                        custChargeDetails.setIsDeleted(true);
//                        custChargeDetailsRepository.save(custChargeDetails);
//                    }
//                }
//            }
//
//            if(debitDocument != null && message.getIsVoid() != null && message.getIsVoid())
//            {
//                List<CustPlanMappping> mapppings = custPlanMappingRepository.findAllByDebitdocumentid(debitDocument.getId().longValue());
//                debitDocument.setBillrunstatus("VOID");
//                debitDocRepository.save(debitDocument);
//                if (!CollectionUtils.isEmpty(mapppings)) {
//                    for (CustPlanMappping mapping : mapppings) {
//                        mapping.setIsVoid(Boolean.TRUE);
//                        mapping.setEndDate(LocalDateTime.now());
//                        mapping.setExpiryDate(LocalDateTime.now());
//                        if (mapping.getStartDate().isAfter(mapping.getEndDate())) {
//                            mapping.setStartDate(LocalDateTime.now());
//                            mapping.setEndDate(mapping.getStartDate().plusSeconds(1));
//                            mapping.setExpiryDate(mapping.getStartDate().plusSeconds(1));
//                        }
//                        mapping.setCustPlanStatus(CommonConstants.STOP_STATUS);
//                        custPlanMappingService.save(mapping, CommonConstants.CHANGE_PLAN_MSG);
//                    }
//                }
//            }
//            else
//            {
//                if(message.getDebitDocument()!=null && message.getDebitDocument().getId()!=null) {
//                    DebitDocument document=debitDocRepository.findById(message.getDebitDocument().getId()).orElse(null);
//                    if(message.getIsPaymentApproved()!=false) {
//                        document.setAdjustedAmount(message.getAdjustedAmount());
//                        document.setBillrunstatus(message.getBillRunStatus());
//                        document.setPaymentStatus(message.getPaymentStatus());
//                        if (document.getPaymentStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID)) {
//                            document.setStatus(CommonConstants.DEBIT_DOC_STATUS.APPROVED);
//                        }
//                        document.setRemarks(message.getDebitDocument().getRemarks());
//                        debitDocRepository.save(document);
//                    }
//
//                    if(document.getPaymentStatus().equalsIgnoreCase("Fully Paid") ){
//                        List<CustPlanMappping> custPlanMapppingList1=custPlanMappingRepository.findAllByDebitdocid(document.getId());
//                        List<Integer> cprIds = custPlanMapppingList1.stream().filter(list->list.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE )).map(mappping -> mappping.getId() ).collect(Collectors.toList());
//                        customerDocDetailsService.changeStatusDisableToActive(cprIds);
//                    }
//                }
//            }
//
//            if(!CollectionUtils.isEmpty(message.getChargeIds()) && debitDocument != null) {
//                List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllById(message.getChargeIds());
//                if(!CollectionUtils.isEmpty(custChargeDetailsList)) {
//                    DebitDocument finalDebitDocument = debitDocument;
//                    custChargeDetailsList = custChargeDetailsList.stream().peek(custChargeDetails -> custChargeDetails.setDebitdocid(Long.valueOf(finalDebitDocument.getId()))).collect(Collectors.toList());
//                    custChargeDetailsRepository.saveAll(custChargeDetailsList);
//                }
//            }
//
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receivePrepaidCustomerInvoiceChargesDetail() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receivePrepaidCustomerInvoiceChargesDetail() ", APIConstants.SUCCESS, message);
//    }
//
//    public void updateDebitDocDetailsForMvnoInoivce(List<Integer> debitDocDetailsIds, Integer mvnoDocId) {
//        try {
//            List<DebitDocDetails> debitDocDetails = debitDocDetailRepository.findAllByDebitdocdetailidIn(debitDocDetailsIds);
//            debitDocDetails= debitDocDetails.stream().peek(i->i.setMvnodebitdocumentid(mvnoDocId)).collect(Collectors.toList());
//            debitDocDetailRepository.saveAll(debitDocDetails);
//        } catch (Exception ex) {
//            log.error("Exception to update mvnoDocId in customer invoices: "+ex.getMessage());
//        }
//    }
//
//    private DebitDocument getdebitDocument(DebitDocument debitDocument) {
//        DebitDocument debitDocument1=new DebitDocument();
//        debitDocument1.setId(debitDocument.getId());
//        debitDocument1.setDocument(debitDocument.getDocument());
//        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        debitDocument1.setLocalenddate( debitDocument.getLocalenddate());
//        debitDocument1.setLocalstartdate(debitDocument.getLocalstartdate());
//        debitDocument1.setStartdate(LocalDateTime.parse(debitDocument.getLocalstartdate(),formatter));
//
////        debitDocument1.setLocalenddate(debitDocument.getEndate().format(formatter).toString());
//        debitDocument1.setDuedate(LocalDateTime.parse(debitDocument.getDuedateString(),formatter));
//      //  debitDocument1.setLatepaymentdateString(debitDocument.getLatepaymentdate().format(formatter).toString());
//        debitDocument1.setSubtotal(debitDocument.getSubtotal());
//        debitDocument1.setTax(debitDocument.getTax());
//        debitDocument1.setDiscount(debitDocument.getDiscount());
//        debitDocument1.setTotalamount(debitDocument.getTotalamount());
//        debitDocument1.setPreviousbalance(debitDocument.getPreviousbalance());
//        debitDocument1.setLatepaymentfee(debitDocument.getLatepaymentfee());
//        debitDocument1.setCurrentpayment(debitDocument.getCurrentpayment());
//        debitDocument1.setCurrentdebit(debitDocument.getCurrentdebit());
//        debitDocument1.setCurrentcredit(debitDocument.getCurrentcredit());
//        debitDocument1.setTotaldue(debitDocument.getTotaldue());
//        //       debitDocument.setTotalamountinwords(debitDocument.getAmountinwords());
//        // debitDocument.dueinwords=debitDocument.getDueinwords();
//        debitDocument1.setBillrunid(debitDocument.getBillrunid());
//        debitDocument1.setBillrunstatus(debitDocument.getBillrunstatus());
//        debitDocument1.setStatus(debitDocument.getStatus());
//        debitDocument1.setIsDelete(debitDocument.getIsDelete());
//        debitDocument1.setCstchargeid(debitDocument.getCstchargeid());
//        debitDocument1.setPaymentowner(debitDocument.getPaymentowner());
//        debitDocument1.setDebitDocumentTAXRels(debitDocument.getDebitDocumentTAXRels());
//
////        debitDocument1.setDebitDocDetailsList(updatedList);
//        debitDocument1.setDocnumber(debitDocument.getDocnumber());
//        //debitDocument1.setTotalCustomerDiscount(debitDocument.getCustomer().getId().doubleValue());
//        debitDocument1.setDocnumber(debitDocument.getDocnumber());
//        debitDocument1.setCustRefName(debitDocument.getCustRefName());
//        debitDocument1.setCustpackrelid(debitDocument.getCustpackrelid());
//        debitDocument1.setEndate(LocalDateTime.parse(debitDocument.getLocalenddate(),formatter));
//        debitDocument1.setRemarks(debitDocument.getRemarks());
//        debitDocument.setUpdateDebitDpcDetailsIds(debitDocument.getUpdateDebitDpcDetailsIds());
//        //  debitDocument.inventoryMappingId=debitDocument.getInventoryMappingId();
//        return debitDocument1;
//    }
//
//    private List<DebitDocDetails> getDebitdocDetails(List<DebitDocDetails> debitDocDetailsList) {
//        List<DebitDocDetails> updatedList = new ArrayList<>();
//        for(DebitDocDetails debitDocDetail : debitDocDetailsList) {
//            debitDocDetail.setStartdate(null);
//            debitDocDetail.setEnddate(null);
//            updatedList.add(debitDocDetail);
//        }
//        return updatedList;
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_POSTPAID_CUSTOMER_INVOICE_CREATION)
//    public void receivePostpaidCustomerInvoiceChargesDetail(PostpaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receivePostpaidCustomerInvoiceChargesDetail() ", APIConstants.SUCCESS, message);
//        ApplicationLogger.logger.info("RabbitMq receive Start receivePostpaidCustomerInvoiceChargesDetail() ", APIConstants.SUCCESS, message);
//
//        try {
//            String orgainzationCustomerUsername = clientServiceSrv.getValueByName("ORGANIZATION");
//            if (message.getCustomerUsername().equalsIgnoreCase(orgainzationCustomerUsername)) {
//                debitDocService.sendOrganizatonBillForApprovAL(message.getInvoiceId(), message.getLoggedInUserId());
//                message.setOrgCust(true);
//            }
//            if (message.isOrgCust())
//                dbrService.addDbrForOrgCustomerPostpaid(message);
//            else
//                dbrService.addDbrForPostpaidCustomer(message);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receivePostpaidCustomerInvoiceChargesDetail() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receivePostpaidCustomerInvoiceChargesDetail() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_POSTPAID_CUSTOMER_INVOICE_DIRECT_CHARGE)
//    public void receivePostpaidCustomerInvoiceDirectChargeDetails(PostpaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receivePostpaidCustomerInvoiceDirectChargeDetails() ", APIConstants.SUCCESS, message);
//
//        try {
//            dbrService.addDbrForPostpaidCustomerDirectCharge(message);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receivePostpaidCustomerInvoiceDirectChargeDetails() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receivePostpaidCustomerInvoiceDirectChargeDetails() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_DIRECT_CHARGE)
//    public void receivePrepaidCustomerInvoiceDirectChargeDetails(PrepaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receivePrepaidCustomerInvoiceDirectChargeDetails() ", APIConstants.SUCCESS, message);
//
//        try {
//            String orgainzationCustomerUsername = clientServiceSrv.getValueByName("ORGANIZATION");
//            if (message.getCustomerName() != null && message.getCustomerName().equalsIgnoreCase(orgainzationCustomerUsername)) {
//                debitDocService.sendOrganizatonBillForApprovAL(message.getInvoiceId(), message.getLoggedInUserId());
//                message.setOrgCust(true);
//            }
//
//            if (message.isOrgCust())
//                dbrService.addDbrForOrgCustomerDirectChargeForPrepaid(message);
//            else
//                dbrService.addDbrForPrepaidCustomerDirectCharge(message);
//
//            if (message.getOldDebitDocumentId().size() > 0) {
//                debitDocService.adjustOldDebitDocument(message.getInvoiceId(), message.getOldDebitDocumentId());
//            }
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receivePrepaidCustomerInvoiceDirectChargeDetails() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receivePrepaidCustomerInvoiceDirectChargeDetails() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_INVOICE_INVENTORY_CHARGE)
//    public void receiveCustomerInvoiceInventoryChargeDetails(PrepaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receiveCustomerInvoiceInventoryChargeDetail() ", APIConstants.SUCCESS, message);
//
//        try {
//            dbrService.addDbrForCustomerInventoryCharge(message);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receiveCustomerInvoiceInventoryChargeDetail() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receiveCustomerInvoiceInventoryChargeDetail() ", APIConstants.SUCCESS, message);
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_SEND_LEAD_DOC_CONVERT)
//    public void receiveLeadDocDetails(SendLeadDocConvertPojo message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receiveLeadDocDetails() ", APIConstants.SUCCESS, message);
//
//        try {
//            if (message != null && message.getCustomerDocDetailsDTOList() != null && message.getCustomerDocDetailsDTOList().size() > 0) {
//                customerDocDetailsService.saveCustDocFromLeadDoc(message.getCustomerDocDetailsDTOList());
//            }
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receiveLeadDocDetails() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receiveLeadDocDetails() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_INVOICE)
//    public void receivePartnerInvoiceCreation(PrepaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receiveLeadMaster() ", APIConstants.SUCCESS, message);
//        try {
//            partnerCommissionService.adjustInvoiceAmount(message.getTotalInvoiceAmount(), message.getInvoiceId());
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receiveLeadMaster() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receiveLeadMaster() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_NASUPDATE)
//    public void receiveNasupdate(NasUpdateMessage message) {
//
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            customerService.nasUpdate(message.getCustomerId(), message.getNasPort(), message.getFramedIp());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION)
//    public void receivePrepaidCustomerInvoiceChargesDetail(PrepaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq ReceivePrepaidCustomerInvoiceChargesDetail START:- "+LocalDateTime.now());
//        try {
//            DebitDocument debitDocument= null;
//
//            if(message.getIsPaymentApproved()==false)
//            {
//                Customers customers=customersRepository.findById1(message.getCustId());
//                customers.setWalletbalance(message.getWalletBalance());
//                if (message.getNextBilldate()!=null && !message.getNextBilldate().isEmpty())
//                {
//                    LocalDate nextBilldate = LocalDate.parse(message.getNextBilldate());
//                    customers.setNextBillDate(nextBilldate);
//                    if (message.getChildIdNextBillDatePair()!=null && message.getChildIdNextBillDatePair().size()>0)
//                    {
//                        //List<Customers> childCusts = new ArrayList<>();
//                        for (Map.Entry<Integer, String> childIdAndDate : message.getChildIdNextBillDatePair()) {
//                                    LocalDateTime nextBillDate = DateTimeUtil.getLocaldateTimefromString(childIdAndDate.getValue());
//                                    Customers childCustomer = customersRepository.findById(childIdAndDate.getKey()).get();
//                                    childCustomer.setNextBillDate(nextBillDate.toLocalDate());
//                                    customersRepository.updateNextBillDate(customers.getId(),customers.getNextBillDate());
//                            //childCusts.add(childCustomer);
//                        }
//                        //customersRepository.saveAll(childCusts);
//                    }
//                }
//
//                customersRepository.updateWalletAndNextBillDate(customers.getId(),customers.getWalletbalance(),customers.getNextBillDate());
//                debitDocService.sendBillGenData(Math.toIntExact(message.getInvoiceId()),false);
//                String orgainzationCustomerUsername = clientServiceSrv.getValueByNameAndmvnoId("ORGANIZATION",customers.getMvnoId());
//
//                if (message.getIsCaf() == null)
//                    message.setIsCaf("false");
//
//                if (message.getCustomerUsername() != null || message.getCustomerName() != null)
//                {
//                    debitDocument=this.getdebitDocument(message.getDebitDocument());
//                    debitDocument.setCreatedById(message.getLoggedInUserId());
//                    debitDocument.setNextStaff(message.getLoggedInUserId());
//                    debitDocument.setLastModifiedById(message.getLoggedInUserId());
//                    debitDocument.setDebitDocumentTAXRels(null);
//                    debitDocument.setCustomer(null);
//                    debitDocument.setPaymentStatus(message.getPaymentStatus());
//                    debitDocument.setBillrunid(message.getBillRunId());
//                    debitDocument.setCreatedByName(message.getCreatedByName());
//                    debitDocument.setIsDirectChargeInvoice(message.getIsDirectChargeInvoice());
//                    debitDocument.setRemarks(message.getDebitDocument().getRemarks());
//                    debitDocument.setBillrunstatus(message.getBillRunStatus());
//
//                    Integer planId=null;
//                    if(customers!=null)
//                        debitDocument.setBuId(customers.getBuId());
//                    if (debitDocument.getCustpackrelid()!=null  ) {
//                        CustPlanMappping custPlanMappping = custPlanMappingRepository.findById1(debitDocument.getCustpackrelid());
//                        if(custPlanMappping!=null && Objects.nonNull(custPlanMappping)) {
//                            custPlanMappingRepository.updateCustPlanMapping(custPlanMappping.getId(),debitDocument.getId().longValue());
//                            //PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
//                            //debitDocument.setPostpaidPlan(postpaidPlan);
//                            planId=custPlanMappping.getPlanId();
//                        }
//                    }
//
////                    if(debitDocument.getCreatedById()==null)
////                    {
////                        debitDocument.setCreatedById(customers.getCreatedById());
////                        debitDocument.setLastModifiedById(customers.getLastModifiedById());
////                    }
//
//                    debitDocument  = debitDocRepository.save(debitDocument);
//                    if(planId!=null)
//                        debitDocRepository.updateCustomer(debitDocument.getId(),customers.getId(),planId);
//                    else
//                        debitDocRepository.updateCustomer(debitDocument.getId(),customers.getId());
//
//                    List<DebitDocDetails> updatedList = new ArrayList<>();
//                    if(message.getDebitDocument().getDebitDocDetailsList()!=null){
//                        updatedList = this.getDebitdocDetails(message.getDebitDocument().getDebitDocDetailsList());
//                        debitDocDetailRepository.saveAll(updatedList);
//                    }
//
//                    if (message.getCustomerUsername().equalsIgnoreCase(orgainzationCustomerUsername) || message.getCustomerName().equalsIgnoreCase(orgainzationCustomerUsername)) {
//                        debitDocService.sendOrganizatonBillForApprovAL(message.getInvoiceId(), message.getLoggedInUserId(),debitDocument);
//                        message.setOrgCust(true);
//                    }
//
//                    if (message.getDebitDocument()!=null && message.getDebitDocument().getUpdateDebitDpcDetailsIds()!=null)
//                        updateDebitDocDetailsForMvnoInoivce(message.getDebitDocument().getUpdateDebitDpcDetailsIds(), debitDocument.getId());
//                }
//
//                if (message.getOldDebitDocumentId() != null) {
//                    if (message.getOldDebitDocumentId().size() > 0 && !message.getIsCaf().equals("true")) {
//                        debitDocService.adjustOldDebitDocument(message.getInvoiceId(), message.getOldDebitDocumentId());
//                    }
//                }
//
//                //customersRepository.save(customers);
//
//                List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
//                if (message.getCustPackAndDebitDocIdPair().size()>0)
//                {
//                    for (Map.Entry<Integer, Long> custPackAndDebitDocIdPair : message.getCustPackAndDebitDocIdPair()) {
//                        CustPlanMappping custPlanMappping = custPlanMappingRepository.findById2(custPackAndDebitDocIdPair.getKey().intValue());
//                        if(custPackAndDebitDocIdPair.getValue()!=null)
//                        {
//                            custPlanMappping.setDebitdocid(custPackAndDebitDocIdPair.getValue());
//                        }else{
//                            custPlanMappping.setDebitdocid(debitDocument.getId().longValue());
//                        }
//                        custPlanMapppingList.add(custPlanMappping);
//                    }
//
//                    if (message.getCustPackAndEndDatePair().size()>0){
//                        for (Map.Entry<Integer, String> custPackAndEndDate : message.getCustPackAndEndDatePair()) {
//                            for (CustPlanMappping custPlanMappping : custPlanMapppingList){
//                                if(custPlanMappping.getId().equals(custPackAndEndDate.getKey())){
//                                    LocalDateTime endDate = DateTimeUtil.getLocaldateTimefromString(custPackAndEndDate.getValue());
//                                    custPlanMappping.setEndDate(endDate);
//                                    custPlanMappping.setExpiryDate(endDate);
//                                }
//                            }
//                        }
//                    }
//
//                    if(custPlanMapppingList!=null && !custPlanMapppingList.isEmpty())
//                    {
//                        custPlanMapppingList.stream().forEach(custPlanMappping -> {
//                            custPlanMappingRepository.updateCustPlanMapping(custPlanMappping.getId(),custPlanMappping.getDebitdocid(),custPlanMappping.getEndDate(),custPlanMappping.getExpiryDate());
//                        });
//                    }
//                }
//            }
//
//            if(debitDocument == null)
//                debitDocument=debitDocRepository.findById(message.getDebitDocument().getId()).orElse(null);
//
//            if(debitDocument!=null && message.getDebitDocument().getBillrunstatus().equalsIgnoreCase("Cancelled")){
//                List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllByDebitdocid(Long.valueOf(debitDocument.getId()));
//                if(!CollectionUtils.isEmpty(custChargeDetailsList)) {
//                    for (CustChargeDetails custChargeDetails: custChargeDetailsList) {
//                        custChargeDetails.setEnddate(debitDocument.getCreatedate().minusMinutes(1));
//                        custChargeDetails.setIsDeleted(true);
//                        custChargeDetailsRepository.save(custChargeDetails);
//                    }
//                }
//            }
//
//            if(debitDocument!=null &&  message.getPaymentStatus()!=null && message.getPaymentStatus().equalsIgnoreCase("Cancelled")){
//                List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllByDebitdocid(Long.valueOf(debitDocument.getId()));
//                if(!CollectionUtils.isEmpty(custChargeDetailsList)) {
//                    for (CustChargeDetails custChargeDetails: custChargeDetailsList) {
//                        custChargeDetails.setEnddate(debitDocument.getCreatedate().minusMinutes(1));
//                        custChargeDetails.setIsDeleted(true);
//                        custChargeDetailsRepository.save(custChargeDetails);
//                    }
//                }
//            }
//
//            if(debitDocument != null && message.getIsVoid() != null && message.getIsVoid())
//            {
//                List<CustPlanMappping> mapppings = custPlanMappingRepository.findAllByDebitdocumentid(debitDocument.getId().longValue());
//                debitDocument.setBillrunstatus("VOID");
//                debitDocRepository.save(debitDocument);
//                if (!CollectionUtils.isEmpty(mapppings)) {
//                    for (CustPlanMappping mapping : mapppings) {
//                        mapping.setIsVoid(Boolean.TRUE);
//                        mapping.setEndDate(LocalDateTime.now());
//                        mapping.setExpiryDate(LocalDateTime.now());
//                        if (mapping.getStartDate().isAfter(mapping.getEndDate())) {
//                            mapping.setStartDate(LocalDateTime.now());
//                            mapping.setEndDate(mapping.getStartDate().plusSeconds(1));
//                            mapping.setExpiryDate(mapping.getStartDate().plusSeconds(1));
//                        }
//                        mapping.setCustPlanStatus(CommonConstants.STOP_STATUS);
//                        custPlanMappingService.save(mapping, CommonConstants.CHANGE_PLAN_MSG);
//                    }
//                }
//            }
//            else
//            {
//                if(message.getDebitDocument()!=null && message.getDebitDocument().getId()!=null) {
//                    DebitDocument document=debitDocRepository.findById(message.getDebitDocument().getId()).orElse(null);
//                    if(message.getIsPaymentApproved()!=false) {
//                        document.setAdjustedAmount(message.getAdjustedAmount());
//                        document.setBillrunstatus(message.getBillRunStatus());
//                        document.setPaymentStatus(message.getPaymentStatus());
//                        if (document.getPaymentStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID)) {
//                            document.setStatus(CommonConstants.DEBIT_DOC_STATUS.APPROVED);
//                        }
//                        document.setRemarks(message.getDebitDocument().getRemarks());
//                        debitDocRepository.save(document);
//                    }
//
//                    if(document.getPaymentStatus().equalsIgnoreCase("Fully Paid") ){
//                        List<CustPlanMappping> custPlanMapppingList1=custPlanMappingRepository.findAllByDebitdocid(document.getId());
//                        List<Integer> cprIds = custPlanMapppingList1.stream().filter(list->list.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE )).map(mappping -> mappping.getId() ).collect(Collectors.toList());
//                        customerDocDetailsService.changeStatusDisableToActive(cprIds);
//                    }
//                }
//            }
//
//            if(!CollectionUtils.isEmpty(message.getChargeIds()) && debitDocument != null) {
//                List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllById(message.getChargeIds());
//                if(!CollectionUtils.isEmpty(custChargeDetailsList)) {
//                    DebitDocument finalDebitDocument = debitDocument;
//                    custChargeDetailsList = custChargeDetailsList.stream().peek(custChargeDetails -> custChargeDetails.setDebitdocid(Long.valueOf(finalDebitDocument.getId()))).collect(Collectors.toList());
//                    custChargeDetailsRepository.saveAll(custChargeDetailsList);
//                }
//            }
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receivePrepaidCustomerInvoiceChargesDetail() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receivePrepaidCustomerInvoiceChargesDetail END:- "+LocalDateTime.now());
//    }
//
//    public void updateDebitDocDetailsForMvnoInoivce(List<Integer> debitDocDetailsIds, Integer mvnoDocId) {
//        try {
//            List<DebitDocDetails> debitDocDetails = debitDocDetailRepository.findAllByDebitdocdetailidIn(debitDocDetailsIds);
//            debitDocDetails= debitDocDetails.stream().peek(i->i.setMvnodebitdocumentid(mvnoDocId)).collect(Collectors.toList());
//            debitDocDetailRepository.saveAll(debitDocDetails);
//        } catch (Exception ex) {
//            log.error("Exception to update mvnoDocId in customer invoices: "+ex.getMessage());
//        }
//    }
//
//    private DebitDocument getdebitDocument(DebitDocument debitDocument) {
//        DebitDocument debitDocument1=new DebitDocument();
//        debitDocument1.setId(debitDocument.getId());
//        debitDocument1.setDocument(debitDocument.getDocument());
//        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        debitDocument1.setLocalenddate( debitDocument.getLocalenddate());
//        debitDocument1.setLocalstartdate(debitDocument.getLocalstartdate());
//        debitDocument1.setStartdate(LocalDateTime.parse(debitDocument.getLocalstartdate(),formatter));
//
////        debitDocument1.setLocalenddate(debitDocument.getEndate().format(formatter).toString());
//        debitDocument1.setDuedate(LocalDateTime.parse(debitDocument.getDuedateString(),formatter));
//      //  debitDocument1.setLatepaymentdateString(debitDocument.getLatepaymentdate().format(formatter).toString());
//        debitDocument1.setSubtotal(debitDocument.getSubtotal());
//        debitDocument1.setTax(debitDocument.getTax());
//        debitDocument1.setDiscount(debitDocument.getDiscount());
//        debitDocument1.setTotalamount(debitDocument.getTotalamount());
//        debitDocument1.setPreviousbalance(debitDocument.getPreviousbalance());
//        debitDocument1.setLatepaymentfee(debitDocument.getLatepaymentfee());
//        debitDocument1.setCurrentpayment(debitDocument.getCurrentpayment());
//        debitDocument1.setCurrentdebit(debitDocument.getCurrentdebit());
//        debitDocument1.setCurrentcredit(debitDocument.getCurrentcredit());
//        debitDocument1.setTotaldue(debitDocument.getTotaldue());
//        //       debitDocument.setTotalamountinwords(debitDocument.getAmountinwords());
//        // debitDocument.dueinwords=debitDocument.getDueinwords();
//        debitDocument1.setBillrunid(debitDocument.getBillrunid());
//        debitDocument1.setBillrunstatus(debitDocument.getBillrunstatus());
//        debitDocument1.setStatus(debitDocument.getStatus());
//        debitDocument1.setIsDelete(debitDocument.getIsDelete());
//        debitDocument1.setCstchargeid(debitDocument.getCstchargeid());
//        debitDocument1.setPaymentowner(debitDocument.getPaymentowner());
//        debitDocument1.setDebitDocumentTAXRels(debitDocument.getDebitDocumentTAXRels());
//
////        debitDocument1.setDebitDocDetailsList(updatedList);
//        debitDocument1.setDocnumber(debitDocument.getDocnumber());
//        //debitDocument1.setTotalCustomerDiscount(debitDocument.getCustomer().getId().doubleValue());
//        debitDocument1.setDocnumber(debitDocument.getDocnumber());
//        debitDocument1.setCustRefName(debitDocument.getCustRefName());
//        debitDocument1.setCustpackrelid(debitDocument.getCustpackrelid());
//        debitDocument1.setEndate(LocalDateTime.parse(debitDocument.getLocalenddate(),formatter));
//        debitDocument1.setRemarks(debitDocument.getRemarks());
//        debitDocument.setUpdateDebitDpcDetailsIds(debitDocument.getUpdateDebitDpcDetailsIds());
//        //  debitDocument.inventoryMappingId=debitDocument.getInventoryMappingId();
//        return debitDocument1;
//    }
//
//    private List<DebitDocDetails> getDebitdocDetails(List<DebitDocDetails> debitDocDetailsList) {
//        List<DebitDocDetails> updatedList = new ArrayList<>();
//        for(DebitDocDetails debitDocDetail : debitDocDetailsList) {
//            debitDocDetail.setStartdate(null);
//            debitDocDetail.setEnddate(null);
//            updatedList.add(debitDocDetail);
//        }
//        return updatedList;
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_POSTPAID_CUSTOMER_INVOICE_CREATION)
//    public void receivePostpaidCustomerInvoiceChargesDetail(PostpaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receivePostpaidCustomerInvoiceChargesDetail() ", APIConstants.SUCCESS, message);
//        ApplicationLogger.logger.info("RabbitMq receive Start receivePostpaidCustomerInvoiceChargesDetail() ", APIConstants.SUCCESS, message);
//
//        try {
//            String orgainzationCustomerUsername = clientServiceSrv.getValueByName("ORGANIZATION");
//            if (message.getCustomerUsername().equalsIgnoreCase(orgainzationCustomerUsername)) {
//                debitDocService.sendOrganizatonBillForApprovAL(message.getInvoiceId(), message.getLoggedInUserId());
//                message.setOrgCust(true);
//            }
//            if (message.isOrgCust())
//                dbrService.addDbrForOrgCustomerPostpaid(message);
//            else
//                dbrService.addDbrForPostpaidCustomer(message);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receivePostpaidCustomerInvoiceChargesDetail() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receivePostpaidCustomerInvoiceChargesDetail() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_POSTPAID_CUSTOMER_INVOICE_DIRECT_CHARGE)
//    public void receivePostpaidCustomerInvoiceDirectChargeDetails(PostpaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receivePostpaidCustomerInvoiceDirectChargeDetails() ", APIConstants.SUCCESS, message);
//
//        try {
//            dbrService.addDbrForPostpaidCustomerDirectCharge(message);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receivePostpaidCustomerInvoiceDirectChargeDetails() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receivePostpaidCustomerInvoiceDirectChargeDetails() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_DIRECT_CHARGE)
//    public void receivePrepaidCustomerInvoiceDirectChargeDetails(PrepaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receivePrepaidCustomerInvoiceDirectChargeDetails() ", APIConstants.SUCCESS, message);
//
//        try {
//            String orgainzationCustomerUsername = clientServiceSrv.getValueByName("ORGANIZATION");
//            if (message.getCustomerName() != null && message.getCustomerName().equalsIgnoreCase(orgainzationCustomerUsername)) {
//                debitDocService.sendOrganizatonBillForApprovAL(message.getInvoiceId(), message.getLoggedInUserId());
//                message.setOrgCust(true);
//            }
//
//            if (message.isOrgCust())
//                dbrService.addDbrForOrgCustomerDirectChargeForPrepaid(message);
//            else
//                dbrService.addDbrForPrepaidCustomerDirectCharge(message);
//
//            if (message.getOldDebitDocumentId().size() > 0) {
//                debitDocService.adjustOldDebitDocument(message.getInvoiceId(), message.getOldDebitDocumentId());
//            }
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receivePrepaidCustomerInvoiceDirectChargeDetails() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receivePrepaidCustomerInvoiceDirectChargeDetails() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_INVOICE_INVENTORY_CHARGE)
//    public void receiveCustomerInvoiceInventoryChargeDetails(PrepaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receiveCustomerInvoiceInventoryChargeDetail() ", APIConstants.SUCCESS, message);
//
//        try {
//            dbrService.addDbrForCustomerInventoryCharge(message);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receiveCustomerInvoiceInventoryChargeDetail() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receiveCustomerInvoiceInventoryChargeDetail() ", APIConstants.SUCCESS, message);
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_SEND_LEAD_DOC_CONVERT)
//    public void receiveLeadDocDetails(SendLeadDocConvertPojo message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receiveLeadDocDetails() ", APIConstants.SUCCESS, message);
//
//        try {
//            if (message != null && message.getCustomerDocDetailsDTOList() != null && message.getCustomerDocDetailsDTOList().size() > 0) {
//                customerDocDetailsService.saveCustDocFromLeadDoc(message.getCustomerDocDetailsDTOList());
//            }
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receiveLeadDocDetails() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receiveLeadDocDetails() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_INVOICE)
//    public void receivePartnerInvoiceCreation(PrepaidInvoiceCharges message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receiveLeadMaster() ", APIConstants.SUCCESS, message);
//        try {
//            partnerCommissionService.adjustInvoiceAmount(message.getTotalInvoiceAmount(), message.getInvoiceId());
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("RabbitMq receive Error receiveLeadMaster() ", APIConstants.FAIL, e.getStackTrace());
//        }
//
//        ApplicationLogger.logger.info("RabbitMq receive End receiveLeadMaster() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_NASUPDATE)
//    public void receiveNasupdate(NasUpdateMessage message) {
//
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            customerService.nasUpdate(message.getCustomerId(), message.getNasPort(), message.getFramedIp());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    @RabbitListener(queues = RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT)
//    public void receiveFinalTicketETRAudits(TicketETRAuditMessage message) {
//
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            customerService.nasUpdate(message.getCustomerId(), message.getNasPort(), message.getFramedIp());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT)
////    public void receiveFinalTicketETRAudits(TicketETRAuditMessage message) {
////
////        log.info("Received Message From RabbitMq : <" + message + ">");
////        try {
////            caseService.saveETRAudit(message.getCustomerData());
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
////  these method not use in anywhere that's why commented
//
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_EMAIL_DOC_AUDIT)
////    public void receiveFinalCustDocETRAudits(TicketETRAuditMessage message) {
////
////        log.info("Received Message From RabbitMq : <" + message + ">");
////        try {
////            caseService.saveEnterpriseETRAudit(message.getCustomerData());
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_LEAD_QUOTATION_WF)
//    public void receiveLeadDataFromCRM(SendLeadQuotationMessage message) {
//        this.hierarchyService.leadQuotationWorkflowRequest(message.getLeadQuotationWfDTO());
//    }
//
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_NASUPDATE)
////    public void receiveNasupdate(NasUpdateMessage message) {
////
////        log.info("Received Message From RabbitMq : <" + message + ">");
////        try {
////            customerService.nasUpdate(message.getCustomerId(),message.getNasPort(),message.getFramedIp());
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_LEAD_MILESTONES_MAPPING)
//    public void receiveCustMilestoneDetailsFromSalaesCRMS(QuickInvoicePojoMessage message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receiveCustMilestoneDetails() ", APIConstants.SUCCESS, message);
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            QuickInvoiceCreationPojo pojo = new QuickInvoiceCreationPojo(message);
//            List<CustMilestoneDetailsPojo> list= custMilestoneDetailsService.saveCustomerMileStoneWithLead(pojo);
//            System.out.println("List of saved cust milestone details of specific lead"+list);
//        } catch (Exception e) {
//            ApplicationLogger.logger.info("RabbitMq receive End receiveCustMilestoneDetails() ", APIConstants.FAIL, message);
//        }
//        ApplicationLogger.logger.info("RabbitMq receive End receiveCustMilestoneDetails() ", APIConstants.SUCCESS, message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_REQUEST_GATEWAY_FOR_STAFFUSER)
//    public void receiveStaffRequestFromKPI(String message) throws Exception {
//        ApplicationLogger.logger.info("RabbitMq receive Start receiveStaffRequestFromKPI() ", APIConstants.SUCCESS, message);
//        try {
//            List<StaffUser> list = new ArrayList<StaffUser>();
//            if(message != null && !"".equals(message)){
//                if("0".equals(message)){
//                    list =  staffUserService.getAllUsers();
//                }else{
//                    list  = staffUserService.findAllByParentStaffId(Integer.valueOf(message));
//                }
//                if(list != null && list.size() > 0){
//                    List<StaffUserPojo> pojoList = staffUserService.convertResponseModelIntoPojo(list);
//                    if(pojoList != null && pojoList.size() > 0){
//                        List<UserMessage> messageList = new ArrayList<UserMessage>();
//                        for (StaffUserPojo pojo : pojoList) {
//                            messageList.add(new UserMessage(pojo));
//                        }
////                        messageSender.send(messageList,RabbitMqConstants.QUEUE_RESPONSE_GATEWAY_FOR_STAFFUSER);
//                        kafkaMessageSender.send(new KafkaMessageData(messageList,UserMessage.class.getSimpleName()));
//                    }
//                }
//                System.out.println("List of StaffUserPojo"+list);
//            }
//        } catch (Exception e) {
//            ApplicationLogger.logger.info("RabbitMq receive End receiveStaffRequestFromKPI() ", APIConstants.FAIL, message);
//        }
//        ApplicationLogger.logger.info("RabbitMq receive End receiveStaffRequestFromKPI() ", APIConstants.SUCCESS, message);
//    }
//
//
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_TICKET_TAT_AUDIT)
////    public void receiveFinalTicketTATAudits(TicketETRAuditMessage message) {
////
////        log.info("Received Message From RabbitMq : <" + message + ">");
////        try {
////            caseService.saveTATAudit(message.getCustomerData());
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
////*******this rebbitmq call commit becase this call send in SELFCARE microservice*************
//
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_INTEGRATION_CREATE_SELFCARE_TICKET)
////    public void receiveSelfCareCreateTicketIntegration(TicketMessageIntegration message) {
////
////        log.info("Received Message From RabbitMq : <" + message + ">");
////        try {
////            caseService.saveSelfCareTicket(message);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_PRODUCT_FROM_RMS)
//    public void productFromRms(ProductDto message){
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            productService.saveEntityFromRms(message);
//            log.info("Product Created Successfully From Rms");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_INWARD_RMS_INTEGRATOIN)
//    public void inwardFromRms(InwardDto message){
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            inwardService.saveEntityFromRms(message,false,false);
//            log.info("Inward Created Successfully From Rms");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERIALIZED_ITEM_FROM_RMS_INTEGRATOIN)
//    public void itemFromRms(ItemDto message){
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            itemService.saveEntity(message);
//            log.info("Inward Created Successfully From Rms");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERIALIZED_ITEM_HISTORY_RMS_INTEGRATOIN)
//    public void itemHistoryFromRms(InOutWardMACMapingDTO message){
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            inOutWardMACService.saveEntityFromIntegrationRms(message);
//            log.info("Inward Created Successfully From Rms");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_ALG_SAVE_SEND)
//    public void receiveMessageACLFromTACACS(ReceiveAccessLevelGroupTacacsMessage message) {
//        log.info("Received Tacacs Access Level Group Message From RabbitMq is : <" + message + ">");
//        try {
//            accessLevelGroupTacacsService.addAccessLevelGroup(message);
//        } catch (Exception e) {
//            log.info("receiveMessageACLtacacs Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_ALG_UPDATE_SEND)
//    public void receiveupdateMessageAccessLevelGroupFromTACACS(ReceiveAccessLevelGroupTacacsMessage message) {
//        log.info("Received Tacacs Access Level Group Message From RabbitMq is : <" + message + ">");
//        try {
//            accessLevelGroupTacacsService.updateAccessLevelGroup(message.getId(),message);
//        } catch (Exception e) {
//            log.info("receiveMessageACLtacacs Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_ALG_DELETE_SEND)
//    public void receivedeleteMessageAccessLevelGroupFromTACACS(ReceiveAccessLevelGroupTacacsMessage message) {
//        log.info("Received Tacacs Access Level Group Message From RabbitMq is : <" + message + ">");
//        try {
//            accessLevelGroupTacacsService.deleteAccessLevelGroupById(message.getId());
//        } catch (Exception e) {
//            log.info("receiveMessageAccessLevelGrouptacacs Failed :" + e.getMessage());
//        }
//    }
//
//
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_TICKET_DATA_TO_APIGW)
//    public void saveTicketDataFromMicroService(CloseTicketCheckMessage message){
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            caseCustometDetailsService.saveCaseCustomerDetails(message);
//            log.info("Close Ticket Data fetch successfully");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDATED_TICKET_DATA_TO_APIGW)
//    public void updateTicketDataFromMicroService(CloseTicketCheckMessage message){
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            caseCustometDetailsService.updateCaseCustomerDetails(message);
//            log.info("Close Ticket Data fetch successfully");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Create MVNO from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_CMS)
//    public void receiveMessageCreateMVNO(SaveMvnoSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            mvnoService.saveMVNOEntity(message);
//            log.info("MVNO Created Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageCreateMVNO Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Update MVNO from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS)
//    public void receiveMessageUpdateMVNO(UpdateMvnoSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            mvnoService.updateMVNOEntity(message);
//            log.info("MVNO Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageUpdateMVNO Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Create Role from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CREATE_ROLE_COMMON_APIGW_TO_CMS)
//    public void receiveMessageCreateRole(SaveRoleSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            roleService.saveRoleEntity(message);
//            log.info("Role Created Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageCreateRole Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Update Role from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDATE_ROLE_COMMON_APIGW_TO_CMS)
//    public void receiveMessageUpdateRole(UpdateRoleSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            roleService.updateRoleEntity(message);
//            log.info("Role Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageUpdateRole Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Create Staff User from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CREATE_STAFFUSER_COMMON_APIGW_TO_CMS)
//    public void receiveMessageCreateStaffUser(SaveStaffUserSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            staffUserService.saveStaffUserEntity(message);
//            log.info("Staff user Created Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageCreateStaffUser Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Update Staff User from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDATE_STAFFUSER_COMMON_APIGW_TO_CMS)
//    public void receiveMessageUpdateStaffUser(UpdateStaffUserSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            staffUserService.updatetaffUserEntity(message);
//            log.info("Staff user Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageUpdateStaffUser Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Create Teams from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CREATE_TEAM_COMMON_APIGW_TO_CMS)
//    public void receiveMessageCreateTeam(SaveTeamsSharedSharedData message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            teamsService.saveTeams(message);
//            log.info("Teams Created Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageCreateTeam Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Update Team from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDATE_TEAM_COMMON_APIGW_TO_CMS)
//    public void receiveMessageUpdateTeam(UpdateTeamsSharedData message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            teamsService.updateTeams(message);
//            log.info("Team Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageUpdateTeam Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Create Client Service from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_CMS)
//    public void receiveMessageCreateClientService(SaveClientServMessge message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            clientServiceSrv.saveSharedClientService(message);
//            log.info("Client Service Created Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageCreateClientService Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Update Client Service from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_CMS)
//    public void receiveMessageUpdateClientService(UpdateClientServMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            clientServiceSrv.updateSharedClientService(message);
//            log.info("Client Service Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
////Country
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForCountryCreation(SaveCountrySharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Country Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            countryService.saveCountry(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Country Creation :"+e.getMessage());
//        }
//
//    }
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForCountryUpdation(UpdateCountrySharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Country Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            countryService.updateCountry(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Country Creation :"+e.getMessage());
//        }
//
//    }
//
//
//    //State
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForStateCreation(SaveStateSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For State Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            stateService.saveStateEntity(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for State Creation :"+e.getMessage());
//        }
//
//    }
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForStateUpdation(UpdateStateSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For State Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            stateService.updateStateEntity(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for State Update :"+e.getMessage());
//        }
//
//    }
//
//
//    //City
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForCityCreation(SaveCitySharedDataMessage message) {
//        log.info("Received Message From RabbitMq For City Save receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            cityService.saveCityEntity(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for City Save :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForCityUpdation(UpdateCitySharedDataMessage message) {
//        log.info("Received Message From RabbitMq For City Update receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            cityService.updateCityEntity(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for City Update :"+e.getMessage());
//        }
//
//    }
//
//
//    //Pincode
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForPincodeCreate(SavePincodeSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Pincode Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            pincodeService.savePincode(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Pincode Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForPincodeUpdate(UpdatePincodeSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Pincode Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            pincodeService.updatePincode(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Pincode Update :"+e.getMessage());
//        }
//
//    }
//
//
//    //Area
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForAreaCreate(SaveAreaSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For area Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            areaService.saveAreaEntity(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for area Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForAreaUpdate(UpdateAreaSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For area Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            areaService.updateAreaEntity(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for area Update :"+e.getMessage());
//        }
//
//    }
//
//
//    //ServiceArea
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForServiceAreaCreate(SaveServiceAreaSharedDataMessge message) {
//        log.info("Received Message From RabbitMq For service area Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            serviceAreaService.saveServiceArea(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for service area Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForServiceAreaUpdate(UpdateServiceAreaSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For service area  Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            serviceAreaService.updateServiceArea(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for service area Update :"+e.getMessage());
//        }
//
//    }
//
//    //Business Unit
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_CMS)
//    public void receiveMessageForBusinessUnitCreate(SaveBusinessUnitSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For business unit Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            businessUnitService.saveBusineeUnit(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for business unit Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_CMS)
//    public void receiveMessageForBusinessUnitUpdate(UpdateBusinessUnitSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For business unit Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            businessUnitService.updateBusinessUnit(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for business unit Update :"+e.getMessage());
//        }
//
//    }
//
//
//    //Branch
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForBranchCreate(SaveBranchSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For branch Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            branchService.saveBranch(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for branch Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForBranchUpdate(UpdateBranchSharedData message) {
//        log.info("Received Message From RabbitMq For branch Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            branchService.updateBranch(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for branch Update :"+e.getMessage());
//        }
//
//    }
//
//
//    //Region
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_REGION_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForRegionCreate(SaveRegionSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Region Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            regionService.saveRegion(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Region Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForRegionUpdate(UpdateRegionSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Region Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            regionService.updateRegion(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Region Update :"+e.getMessage());
//        }
//
//    }
//
//    // BUSINESS VERTICALS
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BUSINESS_VERTICALS_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForBusinessVerticalsCreate(SaveBusinessVerticalSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Business vertical Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            businessVerticalsService.saveBusinessVertical(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Business vertical Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForBusinessVerticalsUpdate(UpdateBusinessVerticalSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Business vertical Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            businessVerticalsService.updateBusinessVertical(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Business vertical Update :"+e.getMessage());
//        }
//
//    }
//
//    //BANK
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BANK_MANAGEMENT_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForBankManagementCreate(SaveBankManagementSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Bank Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            bankManagementService.saveBank(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Bank Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BANK_MANAGEMENT_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForBankManagementUpdate(UpdateBankManagementSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Bank Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            bankManagementService.updateBank(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Bank Update :"+e.getMessage());
//        }
//
//    }
//
//
//    //SUBBUSINESS UNIT
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForSubBUCreate(SaveSubBusinessUnitSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For SubBu Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            subBusinessUnitService.saveSubBU(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for SubBU Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForSubBUUpdate(UpdateSubBusinessUnitSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For SubBU Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            subBusinessUnitService.updateSubBU(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for SuBU Update :"+e.getMessage());
//        }
//
//    }
//    //SUBBUSINESS VERTICAL
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SUB_BUSINESS_VERTICALS_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForSubBVerticalCreate(SaveSubBusinessVerticalsSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For SubBV Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            subBusinessVerticalService.saveSubBuVertical(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for SubBV Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SUB_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForSubBVerticalUpdate(UpdateSubBusinessVerticalsSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For SubBV Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            subBusinessVerticalService.updateSubBuVertical(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for SuBV Update :"+e.getMessage());
//        }
//
//    }
//
//    //DEPARTMENT
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_DEPARTMENT_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForDepartmentCreation(SaveDepartmentSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Department Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            departmentService.saveDepartment(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Department Creation :"+e.getMessage());
//        }
//
//    }
//
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_DEPARTMENT_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForDepartmentUpdation(UpdateDepartmentSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Department Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            departmentService.updateDepartment(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Department Creation :"+e.getMessage());
//        }
//
//    }
//
//
//
//
//    //INVESTMENT CODE
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_INVESTMENT_CODE_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForInvestmentCodeCreation(SaveInvestmentCodeSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For ICCODE Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            investmentCodeService.saveInvestMentCode(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for ICCCODE Creation :"+e.getMessage());
//        }
//
//    }
//
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_INVESTMENT_CODE_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForInvestmentCodeUpdation(UpdateInvestmentCodeSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For ICCODE Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            investmentCodeService.updateInvestMentCode(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for ICCODE Creation :"+e.getMessage());
//        }
//    }
//
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_INVENTORY_SEND_CREATE_NEW_CHARGE_TO_CMS)
//    public void receivedMessageForCreateNewProCharge(InventoryChargeMessage message) {
//        log.info("Received Message From RabbitMq For State Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            chargeService.saveNewProductCharge(message);
//        }
//        catch(Exception e) {
//            log.info("receivedMessageForCreateNewProCharge Failed for New Charge Creation :"+e.getMessage());
//        }
//    }
//
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_INVENTORY_SEND_UPDATE_NEW_CHARGE_TO_CMS)
//    public void receivedMessageForUpdateNewProCharge(InventoryChargeMessage message) {
//        log.info("Received Message From RabbitMq For State Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            chargeService.updateNewProductCharge(message);
//        }
//        catch(Exception e) {
//            log.info("receivedMessageForUpdateNewProCharge Failed for New Charge Updation :"+e.getMessage());
//        }
//    }
//
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_INVENTORY_SEND_CREATE_REF_CHARGE_TO_CMS)
//    public void receivedMessageForCreateRefProCharge(InventoryChargeMessage message) {
//        log.info("Received Message From RabbitMq For State Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            chargeService.saveRefProductCharge(message);
//        }
//        catch(Exception e) {
//            log.info("receivedMessageForCreateRefProCharge Failed for Refurbuished Charge Creation :"+e.getMessage());
//        }
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS)
//    public void receiveMessageforcreditDocFromRevenue(CreditDocMessageList message) {
//        log.info("Received Message From RabbitMq For creditdoc Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            creditDocService.savecreditDoc(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for credit doc Creation :"+e.getMessage());
//        }
//
//    }
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_INVENTORY_SEND_UPDATE_REF_CHARGE_TO_CMS)
//    public void receivedMessageForUpdateRefProCharge(InventoryChargeMessage message) {
//        log.info("Received Message From RabbitMq For State Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            chargeService.updateRefProductCharge(message);
//        }
//        catch(Exception e) {
//            log.info("receivedMessageForUpdateRefProCharge Failed for Refurbuished Charge Updation :"+e.getMessage());
//        }
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_UPDATE_VOID_INVOICE_STATUS)
//    public void receiveMessageForVoidInvoiceFromRevenue(VoidInvoiceMessage message) {
//        log.info("Received Message From RabbitMq For creditdoc Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            creditDocService.updateCPR(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for credit doc Creation :"+e.getMessage());
//        }
//
//    }
//    @RabbitListener(queues = SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API)
//    public void receiveMessagePartnerBalanceFromRevenue(PartnerAmountMessage message) {
//        log.info("Received Message From RabbitMq For creditdoc Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            partnerService.updateAmount(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for credit doc Creation :"+e.getMessage());
//        }
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_CMS)
//    public void receiveUpadatePartner(UpdatePartnerSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            partnerService.updatePartnerData(message);
//            log.info("Partner Created Successfully From pms");
//        } catch (Exception e) {
//            log.error("receiveMessageUpdatePartner Failed :" + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_CMS)
//    public void receiveMessageCreatePartner(SavePartnerSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            partnerService.savePartnerEntiry(message);
//            log.info("Partner Created Successfully From pms");
//        } catch (Exception e) {
//            log.error("receiveMessageCreatePartner Failed :" + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_QUOTA_FROM_RADIUS)
//    public void receivecustomerQuotamessage(SendQuotaMsg message) {
//        log.info("Received Quota data  Message From RabbitMq is : <" + message + ">");
//        try {
//            custQuotaService.sendNotificationOfQuota(message.getQuotaData());
//        } catch (Exception e) {
//            log.info("processing quota data message  Failed :" + e.getMessage());
//            }
//    }
//    @RabbitListener(queues = SharedDataConstants.QUEUE_REJECT_ORG_INVOICE)
//    public void receiveMessageBillToOrg(OrganizationInvoiceRejectMesssage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            custPlanMappingService.updateCustPlanStatus(message);
//            log.info("Partner Created Successfully From pms");
//        } catch (Exception e) {
//            log.error("Receive Message for Reject Org Invoice Failed :" + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CREATE_DATA_ROLE_CMS)
//    public void receiveMessageRoleCreateFromCMS(CommonRoleMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            roleService.saveRole(message);
//            log.info("Client Service Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessage Role create Message :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//
//    }
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_DELETE_DATA_ROLE_CMS)
//    public void receiveMessageRoleDeleteFromCMS(CommonRoleMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            roleService.deleteRole(message);
//            log.info("Client Service Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessage Role Update/DELETE Message Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_QUOTA_INTRIM_FROM_RADIUS)
//    public void receivecustquotaintrimmessage(SendQuotaIntrimMsg message) {
//        log.info("Received Quota data  Message From RabbitMq is : <" + message + ">");
//        try {
//            custQuotaService.saveCustQuotaIntrim(message.getQuotaData());
//        } catch (Exception e) {
//            log.info("processing quota data message  Failed :" + e.getMessage());
//        }
//    }
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS)
//    public void receiveItemSerialDatamessage(InventorySerialNumberMessage message) {
//        log.info("Received Item Serial Data Message From RabbitMq is : <" + message + ">");
//        try {
//            customerInventoryMappingService.saveInventoryData(message);
//        } catch (Exception e) {
//            log.info("receiveItemSerialDatamessage Failed :" + e.getMessage());
//        }
//    }
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_DELETE_MAC_FROM_RADIUS)
//    public void receiveMessageToDeleteMAc(MacAddressMappingMessage message)
//    {
//        try {
//            log.info("Received Message From RabbitMq : <" + message + ">");
//            custMacMapppingService.deleteMacByMacId(message);
//        }
//        catch (Exception e){
//            log.info("deleteMacByMacId is failed"+e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CUSTOMER_ENDDATE_FROMRADIUS)
//    public void receiveMessageToUpdateEnddate(CustomerEndDateUpdateMessage message)
//    {
//        try {
//            log.info("Received Message From RabbitMq : <" + message + ">");
//            customerService.recieveCustomerEnddateMessage(message);
//        } catch (Exception e) {
//            log.info("recieveCustomerEnddateMessage Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_UPDATE_CONCURRENCY_FROM_RADIUS)
//    public void receiveMessageToUpdateConcurrency(CustomerUpdateMessage message)
//    {
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        custMacMapppingService.updateCustomerConcurrency(message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CHANGE_PLAN_STATUS_CMS)
//    public void receiveMessageToExpirePlanOnCreditNoteApprove(Integer message){
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        custPlanMappingService.expirePlanByCprId(message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_POSTPAID_TRIAL_INVOICE_FROM_REVENUE)
//    public void postpiadTrailInvoiceRevenueToCMSQueue(CustomerBillingMessage message)
//    {
//        try {
//            log.info("Received Message From RabbitMq : <" + message + ">");
//            trialDebitDocService.saveTrialDebitDoc(message);
//        } catch (Exception e) {
//            log.info("recieveCustomerEnddateMessage Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_RESERVED_QUOTA_RADIUS)
//    public void receiveUpdateCustomerReserveQuotamessage(SendQuotaMsg message) {
//        log.info("Received Quota data  Message From RabbitMq is : <" + message + ">");
//        try {
//            custQuotaService.setCustomerChunkQuota(message);
//        } catch (Exception e) {
//            log.info("processing quota data message  Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CUSTPLANMAPPINGS_REVENUE_TO_CMS_P2P)
//    public void receiveUpdateCustPlanMappingForP2Pmessage(UpdateCustplanMappingMessage message) {
//        log.info("Received Quota data  Message From RabbitMq is : <" + message + ">");
//        try {
//                custPlanMappingService.updateCustPlanMapping(message);
//        } catch (Exception e) {
//            log.info("processing quota data message  Failed :" + e.getMessage());
//        }
//    }
//
//    /**receive Payment configuration message from common started**/
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_PAYMENT_CONFIGURTION_TO_CMS)
//    public void receivePaymentConfigMessage(PaymentConfigMessage message) {
//        log.info("Received Payment Config  Message From RabbitMq is : <" + message + ">");
//        try {
//            paymentConfigService.handleRecievePaymentConfig(message);
//        } catch (Exception e) {
//            log.info("processing Payment Config message  Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UUID_DATA_TO_CMS)
//    public void receiveUuidDataFromIntegrationMessage(UuidDataDTO message) {
//        log.info("Received Payment Config  Message From RabbitMq is : <" + message + ">");
//        try {
//            customerService.saveUuidToCustomerServiceMapping(message);
//        } catch (Exception e) {
//            log.info("processing Payment Config message  Failed :" + e.getMessage());
//        }
//    }
//
//    /**receive Payment configuration message from common ended**/
//
//
//    @RabbitListener(queues = SharedDataConstants.QUEUE_CREDIT_DOC_IDS_TO_CMS)
//    public void receiveCreditDocIdsMessage(CreditDocIdsMessages message) {
//        log.info("Received CreditDocIds  Message From RabbitMq is : <" + message + ">");
//        try {
//            creditDocService.processCreditDocIds(message);
//        } catch (Exception e) {
//            log.info("processing CreditDocIds  Message  Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = SharedDataConstants.QUEUE_CREDIT_DOC_DETAILS_TO_CMS)
//    public void receiveCreditDocMessage(ListOfCreditDocForBatch message) {
//        log.info("Received CreditDoc  Message From RabbitMq is : <" + message + ">");
//        try {
//            creditDocService.processCreditDoc(message);
//        } catch (Exception e) {
//            log.info("processing CreditDoc  Message  Failed :" + e.getMessage());
//        }
//    }
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CUST_INV_DETAIL_TO_CMS)
//    public void receiveCustInvParamsMessage(CustInvParamsMessage message) {
//        log.info("Received Cust Inventory params  Message From RabbitMq is : <" + message + ">");
//        try {
//            if(!message.getIsUpdate())
//                custInvParamsService.saveCustInvParams(message);
//            else
//                custInvParamsService.updateCustInvParams(message);
//        } catch (Exception e) {
//            log.info("processing CreditDoc  Message  Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CREDIT_DEBIT_DOC_TO_CMS)
//    public void receiveCreditDebitDocMessage(CreditDebitDocMessage message) {
//        log.info("Received Cust Inventory params  Message From RabbitMq is : <" + message + ">");
//        try {
//            if (!message.getCreditDebitDocMappingList().isEmpty())
//                    creditDocService.savecreditDebitDocMappings(message.getCreditDebitDocMappingList());
//        } catch (Exception e) {
//            log.info("processing CreditDoc  Message  Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_MVNO_DOC_SAVE_FROM_COMMON)
//    public void receiveMvnoDocSaveMessage(MvnoDocDetailsDTO message) {
//        log.info("Received Mvno Doc save  Message From RabbitMq is : <" + message + ">");
//        try {
//            docDetailsService.saveEntity(message);
//        } catch (Exception e) {
//            log.info("processing Mvno Doc save  Message  Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_MVNO_DOC_UPDATE_FROM_COMMON)
//    public void receiveMvnoDocUpdateMessage(MvnoDocDetailsDTO message) {
//        log.info("Received Mvno Doc save  Message From RabbitMq is : <" + message + ">");
//        try {
//            docDetailsService.updateEntity(message);
//        } catch (Exception e) {
//            log.info("processing Mvno Doc save  Message  Failed :" + e.getMessage());
//        }
//    }
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS_ISP)
//    public void receiveMessageMvnoUpdateIsp(UpdateMvnoData message) {
//
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            mvnoService.updateMvnoIdIsptoIsp(message.getOldmvnoId(), message.getNewmvnoId());
//        } catch (Exception e) {
//            log.error("Error message : "+e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CUST_PLAN_DETAIL_FROM_RADIUS)
//    public void updateCustomerPlanDetailsQuota(CustomerPackageRelMessage quotaInfo) {
//        log.info("Received Message From RabbitMq : <" + quotaInfo + ">");
//        customerService.updateCustPlanStatusFromRadius(quotaInfo);
//    }
//
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_BUD_PAYMENT_CREDIT_TO_REVENUE)
//    public void receiveBudPayPaymentDetailsMessage(BudPayPaymentMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            creditDocService.updateBudPaymentData(message);
//        } catch (Exception e) {
//            log.error("receiveMessageUpdateTeam Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING_CMS)
//    public void receiveMessageCustomerMAcApigw(CustMacMappingMessage message) {
//        log.info("Received Message From RabbitMq receiveMessageCustomerMAcApigw: <" + message + ">");
//        try {
//            boolean delete = (boolean) message.getData().get("isDelete");
//            if (delete) {
//                customerService.deleteCustomerMACFromApigateway(message);
//            } else {
//                customerService.updateCustomerMacFromApiGTW(message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_SAVE_VENDOR_QUEUE)
//    public void receiveMessageForSaveVendor(SaveUpdateVendorMessage message) {
//        log.info("Received Message From RabbitMq receiveMessageCustomerMAcApigw: <" + message + ">");
//        try {
//            vendorService.saveVendorEntity(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UPDATE_VENDOR_QUEUE)
//    public void receiveMessageCustomerMAcApigw(SaveUpdateVendorMessage message) {
//        log.info("Received Message From RabbitMq receiveMessageCustomerMAcApigw: <" + message + ">");
//        try {
//            vendorService.UpdateVendorEntity(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_PAYMENT_AUDIT_TO_CMS)
//    public void receiveApymentAuditMessageFromIntegration(CustPayDTOMessage message) {
//        log.info("Received Message From RabbitMq receiveMessageCustomerMAcApigw: <" + message + ">");
//        try {
//            if(message!=null){
//                if(message.getId()==null){
//                    customerPaymentService.saveCustomerPayment(message);
//                }else{
//                    customerPaymentService.updateCustomerPayment(message);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @RabbitListener(queues = SharedDataConstants.QUEUE_PLAN_MAPPING_STATUS_UPDATE_CMS)
//    public void receiveCustPlanMappingStatusUpdateFromRevenue(CustPlanMappingStatusMessage message) {
//        log.info("Received Message From RabbitMq receiveCustPlanMappingStatusUpdateFromRevenue: <" + message + ">");
//        try {
//            if(message!=null && message.getCustPlanMappings()!=null && !message.getCustPlanMappings().isEmpty())
//            {
//                List<CustPlanMappingMessage> mappingMessages=message.getCustPlanMappings();
//                mappingMessages.stream().forEach(mapping->{
//                    CustPlanMappping custPlanMappping=custPlanMappingRepository.findById(mapping.getId());
//                    if(custPlanMappping!=null)
//                    {
//                        custPlanMappping.setStatus(mapping.getStatus());
//                        custPlanMappping.setCustPlanStatus(mapping.getCustPlanStatus());
//                        custPlanMappping.setExpiryDate(LocalDateTime.now());
//                        custPlanMappping.setEndDate(LocalDateTime.now());
//                        custPlanMappingRepository.save(custPlanMappping);
//                    }
//                });
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA_LOCATION_MAPPING)
//    public void receiveServiceAreaLocationMapping(List<LocationServiceareaMappingMessage> message) {
//        log.info("Received Message From RabbitMq receiveMessageServiceAreaLocationMapping: <" + message + ">");
//        try {
//            if (message != null && !message.isEmpty()) {
//                    serviceAreaService.saveServiceAreaLocationMapping(message);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_OTP_PROFILE_TO_CMS)
//    public void receiveMessageCustomerDeactiovationWhenMvnoIsInActive(OTPProfileMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            otpManagmentService.saveOtpProfileFromRabbitMq(message.getOtpManagement());
//            log.info("Defoult Deprovision Successfull");
//        } catch (Exception e) {
//            log.error("receiveMessage defoult update Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//

//    @RabbitListener(queues = RabbitMqConstants.QUEUE_PRODUCT_FROM_RMS)
//    public void productFromRms(ProductDto message){
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            productService.saveEntityFromRms(message);
//            log.info("Product Created Successfully From Rms");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_INWARD_RMS_INTEGRATOIN)
//    public void inwardFromRms(InwardDto message){
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            inwardService.saveEntityFromRms(message,false,false);
//            log.info("Inward Created Successfully From Rms");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERIALIZED_ITEM_FROM_RMS_INTEGRATOIN)
//    public void itemFromRms(ItemDto message){
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            itemService.saveEntity(message);
//            log.info("Inward Created Successfully From Rms");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERIALIZED_ITEM_HISTORY_RMS_INTEGRATOIN)
//    public void itemHistoryFromRms(InOutWardMACMapingDTO message){
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            inOutWardMACService.saveEntityFromIntegrationRms(message);
//            log.info("Inward Created Successfully From Rms");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_ALG_SAVE_SEND)
//    public void receiveMessageACLFromTACACS(ReceiveAccessLevelGroupTacacsMessage message) {
//        log.info("Received Tacacs Access Level Group Message From RabbitMq is : <" + message + ">");
//        try {
//            accessLevelGroupTacacsService.addAccessLevelGroup(message);
//        } catch (Exception e) {
//            log.info("receiveMessageACLtacacs Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_ALG_UPDATE_SEND)
//    public void receiveupdateMessageAccessLevelGroupFromTACACS(ReceiveAccessLevelGroupTacacsMessage message) {
//        log.info("Received Tacacs Access Level Group Message From RabbitMq is : <" + message + ">");
//        try {
//            accessLevelGroupTacacsService.updateAccessLevelGroup(message.getId(),message);
//        } catch (Exception e) {
//            log.info("receiveMessageACLtacacs Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_ALG_DELETE_SEND)
//    public void receivedeleteMessageAccessLevelGroupFromTACACS(ReceiveAccessLevelGroupTacacsMessage message) {
//        log.info("Received Tacacs Access Level Group Message From RabbitMq is : <" + message + ">");
//        try {
//            accessLevelGroupTacacsService.deleteAccessLevelGroupById(message.getId());
//        } catch (Exception e) {
//            log.info("receiveMessageAccessLevelGrouptacacs Failed :" + e.getMessage());
//        }
//    }
//
//
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_TICKET_DATA_TO_APIGW)
//    public void saveTicketDataFromMicroService(CloseTicketCheckMessage message){
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            caseCustometDetailsService.saveCaseCustomerDetails(message);
//            log.info("Close Ticket Data fetch successfully");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDATED_TICKET_DATA_TO_APIGW)
//    public void updateTicketDataFromMicroService(CloseTicketCheckMessage message){
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            caseCustometDetailsService.updateCaseCustomerDetails(message);
//            log.info("Close Ticket Data fetch successfully");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Create MVNO from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_CMS)
//    public void receiveMessageCreateMVNO(SaveMvnoSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            mvnoService.saveMVNOEntity(message);
//            log.info("MVNO Created Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageCreateMVNO Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Update MVNO from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS)
//    public void receiveMessageUpdateMVNO(UpdateMvnoSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            mvnoService.updateMVNOEntity(message);
//            log.info("MVNO Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageUpdateMVNO Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Create Role from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CREATE_ROLE_COMMON_APIGW_TO_CMS)
//    public void receiveMessageCreateRole(SaveRoleSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            roleService.saveRoleEntity(message);
//            log.info("Role Created Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageCreateRole Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Update Role from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDATE_ROLE_COMMON_APIGW_TO_CMS)
//    public void receiveMessageUpdateRole(UpdateRoleSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            roleService.updateRoleEntity(message);
//            log.info("Role Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageUpdateRole Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Create Staff User from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CREATE_STAFFUSER_COMMON_APIGW_TO_CMS)
//    public void receiveMessageCreateStaffUser(SaveStaffUserSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            staffUserService.saveStaffUserEntity(message);
//            log.info("Staff user Created Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageCreateStaffUser Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Update Staff User from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDATE_STAFFUSER_COMMON_APIGW_TO_CMS)
//    public void receiveMessageUpdateStaffUser(UpdateStaffUserSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            staffUserService.updatetaffUserEntity(message);
//            log.info("Staff user Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageUpdateStaffUser Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Create Teams from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CREATE_TEAM_COMMON_APIGW_TO_CMS)
//    public void receiveMessageCreateTeam(SaveTeamsSharedSharedData message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            teamsService.saveTeams(message);
//            log.info("Teams Created Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageCreateTeam Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Update Team from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDATE_TEAM_COMMON_APIGW_TO_CMS)
//    public void receiveMessageUpdateTeam(UpdateTeamsSharedData message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            teamsService.updateTeams(message);
//            log.info("Team Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageUpdateTeam Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Create Client Service from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_CMS)
//    public void receiveMessageCreateClientService(SaveClientServMessge message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            clientServiceSrv.saveSharedClientService(message);
//            log.info("Client Service Created Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageCreateClientService Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    //Update Client Service from RabbitMQ
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_CMS)
//    public void receiveMessageUpdateClientService(UpdateClientServMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            clientServiceSrv.updateSharedClientService(message);
//            log.info("Client Service Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
////Country
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForCountryCreation(SaveCountrySharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Country Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            countryService.saveCountry(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Country Creation :"+e.getMessage());
//        }
//
//    }
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForCountryUpdation(UpdateCountrySharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Country Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            countryService.updateCountry(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Country Creation :"+e.getMessage());
//        }
//
//    }
//
//
//    //State
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForStateCreation(SaveStateSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For State Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            stateService.saveStateEntity(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for State Creation :"+e.getMessage());
//        }
//
//    }
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForStateUpdation(UpdateStateSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For State Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            stateService.updateStateEntity(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for State Update :"+e.getMessage());
//        }
//
//    }
//
//
//    //City
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForCityCreation(SaveCitySharedDataMessage message) {
//        log.info("Received Message From RabbitMq For City Save receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            cityService.saveCityEntity(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for City Save :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForCityUpdation(UpdateCitySharedDataMessage message) {
//        log.info("Received Message From RabbitMq For City Update receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            cityService.updateCityEntity(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for City Update :"+e.getMessage());
//        }
//
//    }
//
//
//    //Pincode
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForPincodeCreate(SavePincodeSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Pincode Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            pincodeService.savePincode(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Pincode Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForPincodeUpdate(UpdatePincodeSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Pincode Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            pincodeService.updatePincode(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Pincode Update :"+e.getMessage());
//        }
//
//    }
//
//
//    //Area
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForAreaCreate(SaveAreaSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For area Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            areaService.saveAreaEntity(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for area Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForAreaUpdate(UpdateAreaSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For area Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            areaService.updateAreaEntity(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for area Update :"+e.getMessage());
//        }
//
//    }
//
//
//    //ServiceArea
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForServiceAreaCreate(SaveServiceAreaSharedDataMessge message) {
//        log.info("Received Message From RabbitMq For service area Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            serviceAreaService.saveServiceArea(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for service area Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForServiceAreaUpdate(UpdateServiceAreaSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For service area  Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            serviceAreaService.updateServiceArea(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for service area Update :"+e.getMessage());
//        }
//
//    }
//
//    //Business Unit
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_CMS)
//    public void receiveMessageForBusinessUnitCreate(SaveBusinessUnitSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For business unit Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            businessUnitService.saveBusineeUnit(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for business unit Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_CMS)
//    public void receiveMessageForBusinessUnitUpdate(UpdateBusinessUnitSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For business unit Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            businessUnitService.updateBusinessUnit(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for business unit Update :"+e.getMessage());
//        }
//
//    }
//
//
//    //Branch
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForBranchCreate(SaveBranchSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For branch Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            branchService.saveBranch(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for branch Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForBranchUpdate(UpdateBranchSharedData message) {
//        log.info("Received Message From RabbitMq For branch Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            branchService.updateBranch(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for branch Update :"+e.getMessage());
//        }
//
//    }
//
//
//    //Region
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_REGION_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForRegionCreate(SaveRegionSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Region Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            regionService.saveRegion(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Region Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForRegionUpdate(UpdateRegionSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Region Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            regionService.updateRegion(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Region Update :"+e.getMessage());
//        }
//
//    }
//
//    // BUSINESS VERTICALS
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BUSINESS_VERTICALS_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForBusinessVerticalsCreate(SaveBusinessVerticalSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Business vertical Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            businessVerticalsService.saveBusinessVertical(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Business vertical Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForBusinessVerticalsUpdate(UpdateBusinessVerticalSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Business vertical Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            businessVerticalsService.updateBusinessVertical(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Business vertical Update :"+e.getMessage());
//        }
//
//    }
//
//    //BANK
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BANK_MANAGEMENT_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForBankManagementCreate(SaveBankManagementSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Bank Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            bankManagementService.saveBank(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Bank Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BANK_MANAGEMENT_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForBankManagementUpdate(UpdateBankManagementSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Bank Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            bankManagementService.updateBank(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Bank Update :"+e.getMessage());
//        }
//
//    }
//
//
//    //SUBBUSINESS UNIT
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForSubBUCreate(SaveSubBusinessUnitSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For SubBu Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            subBusinessUnitService.saveSubBU(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for SubBU Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SUB_BUSINESS_UNIT_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForSubBUUpdate(UpdateSubBusinessUnitSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For SubBU Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            subBusinessUnitService.updateSubBU(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for SuBU Update :"+e.getMessage());
//        }
//
//    }
//    //SUBBUSINESS VERTICAL
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SUB_BUSINESS_VERTICALS_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForSubBVerticalCreate(SaveSubBusinessVerticalsSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For SubBV Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            subBusinessVerticalService.saveSubBuVertical(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for SubBV Creation :"+e.getMessage());
//        }
//
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SUB_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForSubBVerticalUpdate(UpdateSubBusinessVerticalsSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For SubBV Update, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            subBusinessVerticalService.updateSubBuVertical(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for SuBV Update :"+e.getMessage());
//        }
//
//    }
//
//    //DEPARTMENT
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_DEPARTMENT_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForDepartmentCreation(SaveDepartmentSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Department Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            departmentService.saveDepartment(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Department Creation :"+e.getMessage());
//        }
//
//    }
//
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_DEPARTMENT_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForDepartmentUpdation(UpdateDepartmentSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Department Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            departmentService.updateDepartment(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Department Creation :"+e.getMessage());
//        }
//
//    }
//
//
//
//
//    //INVESTMENT CODE
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_INVESTMENT_CODE_CREATE_DATA_SHARE_CPM)
//    public void receiveMessageForInvestmentCodeCreation(SaveInvestmentCodeSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For ICCODE Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            investmentCodeService.saveInvestMentCode(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for ICCCODE Creation :"+e.getMessage());
//        }
//
//    }
//
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_INVESTMENT_CODE_UPDATE_DATA_SHARE_CPM)
//    public void receiveMessageForInvestmentCodeUpdation(UpdateInvestmentCodeSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For ICCODE Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            investmentCodeService.updateInvestMentCode(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for ICCODE Creation :"+e.getMessage());
//        }
//    }
//
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_INVENTORY_SEND_CREATE_NEW_CHARGE_TO_CMS)
//    public void receivedMessageForCreateNewProCharge(InventoryChargeMessage message) {
//        log.info("Received Message From RabbitMq For State Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            chargeService.saveNewProductCharge(message);
//        }
//        catch(Exception e) {
//            log.info("receivedMessageForCreateNewProCharge Failed for New Charge Creation :"+e.getMessage());
//        }
//    }
//
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_INVENTORY_SEND_UPDATE_NEW_CHARGE_TO_CMS)
//    public void receivedMessageForUpdateNewProCharge(InventoryChargeMessage message) {
//        log.info("Received Message From RabbitMq For State Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            chargeService.updateNewProductCharge(message);
//        }
//        catch(Exception e) {
//            log.info("receivedMessageForUpdateNewProCharge Failed for New Charge Updation :"+e.getMessage());
//        }
//    }
//
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_INVENTORY_SEND_CREATE_REF_CHARGE_TO_CMS)
//    public void receivedMessageForCreateRefProCharge(InventoryChargeMessage message) {
//        log.info("Received Message From RabbitMq For State Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            chargeService.saveRefProductCharge(message);
//        }
//        catch(Exception e) {
//            log.info("receivedMessageForCreateRefProCharge Failed for Refurbuished Charge Creation :"+e.getMessage());
//        }
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_CREDIT_DOC_TO_CMS)
//    public void receiveMessageforcreditDocFromRevenue(CreditDocMessageList message) {
//        log.info("Received Message From RabbitMq For creditdoc Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            creditDocService.savecreditDoc(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for credit doc Creation :"+e.getMessage());
//        }
//
//    }
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_INVENTORY_SEND_UPDATE_REF_CHARGE_TO_CMS)
//    public void receivedMessageForUpdateRefProCharge(InventoryChargeMessage message) {
//        log.info("Received Message From RabbitMq For State Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            chargeService.updateRefProductCharge(message);
//        }
//        catch(Exception e) {
//            log.info("receivedMessageForUpdateRefProCharge Failed for Refurbuished Charge Updation :"+e.getMessage());
//        }
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_UPDATE_VOID_INVOICE_STATUS)
//    public void receiveMessageForVoidInvoiceFromRevenue(VoidInvoiceMessage message) {
//        log.info("Received Message From RabbitMq For creditdoc Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            creditDocService.updateCPR(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for credit doc Creation :"+e.getMessage());
//        }
//
//    }
//    @RabbitListener(queues = SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API)
//    public void receiveMessagePartnerBalanceFromRevenue(PartnerAmountMessage message) {
//        log.info("Received Message From RabbitMq For creditdoc Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            partnerService.updateAmount(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for credit doc Creation :"+e.getMessage());
//        }
//    }
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_CMS)
//    public void receiveUpadatePartner(UpdatePartnerSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            partnerService.updatePartnerData(message);
//            log.info("Partner Created Successfully From pms");
//        } catch (Exception e) {
//            log.error("receiveMessageUpdatePartner Failed :" + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    @Transactional
//    @RabbitListener(queues = SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_CMS)
//    public void receiveMessageCreatePartner(SavePartnerSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            partnerService.savePartnerEntiry(message);
//            log.info("Partner Created Successfully From pms");
//        } catch (Exception e) {
//            log.error("receiveMessageCreatePartner Failed :" + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_QUOTA_FROM_RADIUS)
//    public void receivecustomerQuotamessage(SendQuotaMsg message) {
//        log.info("Received Quota data  Message From RabbitMq is : <" + message + ">");
//        try {
//            custQuotaService.sendNotificationOfQuota(message.getQuotaData());
//        } catch (Exception e) {
//            log.info("processing quota data message  Failed :" + e.getMessage());
//            }
//    }
//    @RabbitListener(queues = SharedDataConstants.QUEUE_REJECT_ORG_INVOICE)
//    public void receiveMessageBillToOrg(OrganizationInvoiceRejectMesssage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            custPlanMappingService.updateCustPlanStatus(message);
//            log.info("Partner Created Successfully From pms");
//        } catch (Exception e) {
//            log.error("Receive Message for Reject Org Invoice Failed :" + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CREATE_DATA_ROLE_CMS)
//    public void receiveMessageRoleCreateFromCMS(CommonRoleMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            roleService.saveRole(message);
//            log.info("Client Service Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessage Role create Message :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//
//    }
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_DELETE_DATA_ROLE_CMS)
//    public void receiveMessageRoleDeleteFromCMS(CommonRoleMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            roleService.deleteRole(message);
//            log.info("Client Service Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessage Role Update/DELETE Message Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_QUOTA_INTRIM_FROM_RADIUS)
//    public void receivecustquotaintrimmessage(SendQuotaIntrimMsg message) {
//        log.info("Received Quota data  Message From RabbitMq is : <" + message + ">");
//        try {
//            custQuotaService.saveCustQuotaIntrim(message.getQuotaData());
//        } catch (Exception e) {
//            log.info("processing quota data message  Failed :" + e.getMessage());
//        }
//    }
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS)
//    public void receiveItemSerialDatamessage(InventorySerialNumberMessage message) {
//        log.info("Received Item Serial Data Message From RabbitMq is : <" + message + ">");
//        try {
//            customerInventoryMappingService.saveInventoryData(message);
//        } catch (Exception e) {
//            log.info("receiveItemSerialDatamessage Failed :" + e.getMessage());
//        }
//    }
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_DELETE_MAC_FROM_RADIUS)
//    public void receiveMessageToDeleteMAc(MacAddressMappingMessage message)
//    {
//        try {
//            log.info("Received Message From RabbitMq : <" + message + ">");
//            custMacMapppingService.deleteMacByMacId(message);
//        }
//        catch (Exception e){
//            log.info("deleteMacByMacId is failed"+e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CUSTOMER_ENDDATE_FROMRADIUS)
//    public void receiveMessageToUpdateEnddate(CustomerEndDateUpdateMessage message)
//    {
//        try {
//            log.info("Received Message From RabbitMq : <" + message + ">");
//            customerService.recieveCustomerEnddateMessage(message);
//        } catch (Exception e) {
//            log.info("recieveCustomerEnddateMessage Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_UPDATE_CONCURRENCY_FROM_RADIUS)
//    public void receiveMessageToUpdateConcurrency(CustomerUpdateMessage message)
//    {
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        custMacMapppingService.updateCustomerConcurrency(message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CHANGE_PLAN_STATUS_CMS)
//    public void receiveMessageToExpirePlanOnCreditNoteApprove(Integer message){
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        custPlanMappingService.expirePlanByCprId(message);
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_POSTPAID_TRIAL_INVOICE_FROM_REVENUE)
//    public void postpiadTrailInvoiceRevenueToCMSQueue(CustomerBillingMessage message)
//    {
//        try {
//            log.info("Received Message From RabbitMq : <" + message + ">");
//            trialDebitDocService.saveTrialDebitDoc(message);
//        } catch (Exception e) {
//            log.info("recieveCustomerEnddateMessage Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_RESERVED_QUOTA_RADIUS)
//    public void receiveUpdateCustomerReserveQuotamessage(SendQuotaMsg message) {
//        log.info("Received Quota data  Message From RabbitMq is : <" + message + ">");
//        try {
//            custQuotaService.setCustomerChunkQuota(message);
//        } catch (Exception e) {
//            log.info("processing quota data message  Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CUSTPLANMAPPINGS_REVENUE_TO_CMS_P2P)
//    public void receiveUpdateCustPlanMappingForP2Pmessage(UpdateCustplanMappingMessage message) {
//        log.info("Received Quota data  Message From RabbitMq is : <" + message + ">");
//        try {
//                custPlanMappingService.updateCustPlanMapping(message);
//        } catch (Exception e) {
//            log.info("processing quota data message  Failed :" + e.getMessage());
//        }
//    }
//
//    /**receive Payment configuration message from common started**/
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_PAYMENT_CONFIGURTION_TO_CMS)
//    public void receivePaymentConfigMessage(PaymentConfigMessage message) {
//        log.info("Received Payment Config  Message From RabbitMq is : <" + message + ">");
//        try {
//            paymentConfigService.handleRecievePaymentConfig(message);
//        } catch (Exception e) {
//            log.info("processing Payment Config message  Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UUID_DATA_TO_CMS)
//    public void receiveUuidDataFromIntegrationMessage(UuidDataDTO message) {
//        log.info("Received Payment Config  Message From RabbitMq is : <" + message + ">");
//        try {
//            customerService.saveUuidToCustomerServiceMapping(message);
//        } catch (Exception e) {
//            log.info("processing Payment Config message  Failed :" + e.getMessage());
//        }
//    }
//
//    /**receive Payment configuration message from common ended**/
//
//
//    @RabbitListener(queues = SharedDataConstants.QUEUE_CREDIT_DOC_IDS_TO_CMS)
//    public void receiveCreditDocIdsMessage(CreditDocIdsMessages message) {
//        log.info("Received CreditDocIds  Message From RabbitMq is : <" + message + ">");
//        try {
//            creditDocService.processCreditDocIds(message);
//        } catch (Exception e) {
//            log.info("processing CreditDocIds  Message  Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = SharedDataConstants.QUEUE_CREDIT_DOC_DETAILS_TO_CMS)
//    public void receiveCreditDocMessage(ListOfCreditDocForBatch message) {
//        log.info("Received CreditDoc  Message From RabbitMq is : <" + message + ">");
//        try {
//            creditDocService.processCreditDoc(message);
//        } catch (Exception e) {
//            log.info("processing CreditDoc  Message  Failed :" + e.getMessage());
//        }
//    }
//    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CUST_INV_DETAIL_TO_CMS)
//    public void receiveCustInvParamsMessage(CustInvParamsMessage message) {
//        log.info("Received Cust Inventory params  Message From RabbitMq is : <" + message + ">");
//        try {
//            if(!message.getIsUpdate())
//                custInvParamsService.saveCustInvParams(message);
//            else
//                custInvParamsService.updateCustInvParams(message);
//        } catch (Exception e) {
//            log.info("processing CreditDoc  Message  Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CREDIT_DEBIT_DOC_TO_CMS)
//    public void receiveCreditDebitDocMessage(CreditDebitDocMessage message) {
//        log.info("Received Cust Inventory params  Message From RabbitMq is : <" + message + ">");
//        try {
//            if (!message.getCreditDebitDocMappingList().isEmpty())
//                    creditDocService.savecreditDebitDocMappings(message.getCreditDebitDocMappingList());
//        } catch (Exception e) {
//            log.info("processing CreditDoc  Message  Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_MVNO_DOC_SAVE_FROM_COMMON)
//    public void receiveMvnoDocSaveMessage(MvnoDocDetailsDTO message) {
//        log.info("Received Mvno Doc save  Message From RabbitMq is : <" + message + ">");
//        try {
//            docDetailsService.saveEntity(message);
//        } catch (Exception e) {
//            log.info("processing Mvno Doc save  Message  Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_MVNO_DOC_UPDATE_FROM_COMMON)
//    public void receiveMvnoDocUpdateMessage(MvnoDocDetailsDTO message) {
//        log.info("Received Mvno Doc save  Message From RabbitMq is : <" + message + ">");
//        try {
//            docDetailsService.updateEntity(message);
//        } catch (Exception e) {
//            log.info("processing Mvno Doc save  Message  Failed :" + e.getMessage());
//        }
//    }
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_CMS_ISP)
//    public void receiveMessageMvnoUpdateIsp(UpdateMvnoData message) {
//
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        try {
//            mvnoService.updateMvnoIdIsptoIsp(message.getOldmvnoId(), message.getNewmvnoId());
//        } catch (Exception e) {
//            log.error("Error message : "+e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CUST_PLAN_DETAIL_FROM_RADIUS)
//    public void updateCustomerPlanDetailsQuota(CustomerPackageRelMessage quotaInfo) {
//        log.info("Received Message From RabbitMq : <" + quotaInfo + ">");
//        customerService.updateCustPlanStatusFromRadius(quotaInfo);
//    }
//
//    @Transactional
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_BUD_PAYMENT_CREDIT_TO_REVENUE)
//    public void receiveBudPayPaymentDetailsMessage(BudPayPaymentMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            creditDocService.updateBudPaymentData(message);
//        } catch (Exception e) {
//            log.error("receiveMessageUpdateTeam Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING_CMS)
//    public void receiveMessageCustomerMAcApigw(CustMacMappingMessage message) {
//        log.info("Received Message From RabbitMq receiveMessageCustomerMAcApigw: <" + message + ">");
//        try {
//            boolean delete = (boolean) message.getData().get("isDelete");
//            if (delete) {
//                customerService.deleteCustomerMACFromApigateway(message);
//            } else {
//                customerService.updateCustomerMacFromApiGTW(message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_SAVE_VENDOR_QUEUE)
//    public void receiveMessageForSaveVendor(SaveUpdateVendorMessage message) {
//        log.info("Received Message From RabbitMq receiveMessageCustomerMAcApigw: <" + message + ">");
//        try {
//            vendorService.saveVendorEntity(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UPDATE_VENDOR_QUEUE)
//    public void receiveMessageCustomerMAcApigw(SaveUpdateVendorMessage message) {
//        log.info("Received Message From RabbitMq receiveMessageCustomerMAcApigw: <" + message + ">");
//        try {
//            vendorService.UpdateVendorEntity(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_PAYMENT_AUDIT_TO_CMS)
//    public void receiveApymentAuditMessageFromIntegration(CustPayDTOMessage message) {
//        log.info("Received Message From RabbitMq receiveMessageCustomerMAcApigw: <" + message + ">");
//        try {
//            if(message!=null){
//                if(message.getId()==null){
//                    customerPaymentService.saveCustomerPayment(message);
//                }else{
//                    customerPaymentService.updateCustomerPayment(message);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @RabbitListener(queues = SharedDataConstants.QUEUE_PLAN_MAPPING_STATUS_UPDATE_CMS)
//    public void receiveCustPlanMappingStatusUpdateFromRevenue(CustPlanMappingStatusMessage message) {
//        log.info("Received Message From RabbitMq receiveCustPlanMappingStatusUpdateFromRevenue: <" + message + ">");
//        try {
//            if(message!=null && message.getCustPlanMappings()!=null && !message.getCustPlanMappings().isEmpty())
//            {
//                List<CustPlanMappingMessage> mappingMessages=message.getCustPlanMappings();
//                mappingMessages.stream().forEach(mapping->{
//                    CustPlanMappping custPlanMappping=custPlanMappingRepository.findById(mapping.getId());
//                    if(custPlanMappping!=null)
//                    {
//                        custPlanMappping.setStatus(mapping.getStatus());
//                        custPlanMappping.setCustPlanStatus(mapping.getCustPlanStatus());
//                        custPlanMappping.setExpiryDate(LocalDateTime.now());
//                        custPlanMappping.setEndDate(LocalDateTime.now());
//                        custPlanMappingRepository.save(custPlanMappping);
//                    }
//                });
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA_LOCATION_MAPPING)
//    public void receiveServiceAreaLocationMapping(List<LocationServiceareaMappingMessage> message) {
//        log.info("Received Message From RabbitMq receiveMessageServiceAreaLocationMapping: <" + message + ">");
//        try {
//            if (message != null && !message.isEmpty()) {
//                    serviceAreaService.saveServiceAreaLocationMapping(message);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_OTP_PROFILE_TO_CMS)
//    public void receiveMessageCustomerDeactiovationWhenMvnoIsInActive(OTPProfileMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            otpManagmentService.saveOtpProfileFromRabbitMq(message.getOtpManagement());
//            log.info("Defoult Deprovision Successfull");
//        } catch (Exception e) {
//            log.error("receiveMessage defoult update Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CPR_UPDATE_FROM_REVENUE_CMS)
//    public void receiveMessageForUpdateCprEndDate(UpdateCprMessage message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receiveMessageForUpdateCprEndDate ", APIConstants.SUCCESS, message);
//        try {
//            if (message.getCustPackAndEndDatePair().size()>0){
//                for (Map.Entry<Integer, String> custPackAndEndDate : message.getCustPackAndEndDatePair()) {
//                    CustPlanMappping mapping=custPlanMappingRepository.findById(custPackAndEndDate.getKey());
//                    if(mapping!=null)
//                    {
//                        LocalDateTime endDate = DateTimeUtil.getLocaldateTimefromString(custPackAndEndDate.getValue());
//                        mapping.setEndDate(endDate);
//                        mapping.setExpiryDate(endDate);
//                        custPlanMappingRepository.save(mapping);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("receiveMessage CPR update Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_INVOICE_NUMBER_UPDATE_FROM_REVENUE_CMS)
//    public void receiveMessageForUpdateInvoiceNumberInDebitDocument(UpdateInvoiceNumberMessage message) {
//        ApplicationLogger.logger.info("RabbitMq receive Start receiveMessageForUpdateInvoiceNumberInDebitDocument ", APIConstants.SUCCESS, message);
//        try {
//            if (message.getDebitDocumentId()!=null)
//            {
//                DebitDocument document=debitDocRepository.findById(message.getDebitDocumentId()).orElse(null);
//                if(document!=null)
//                {
//                    document.setDocnumber(message.getDebitDocumentNumber());
//                    debitDocRepository.save(document);
//                }
//            }
//        } catch (Exception e) {
//            log.error("receiveMessage Invoice Number Update Failed :" +e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
}
