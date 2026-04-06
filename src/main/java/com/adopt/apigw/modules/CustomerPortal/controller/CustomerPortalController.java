package com.adopt.apigw.modules.CustomerPortal.controller;

import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.*;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.CommonList.model.CommonListDTO;
import com.adopt.apigw.modules.CommonList.service.CommonListService;
import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;
import com.adopt.apigw.modules.Communication.Helper.CommunicationHelper;
import com.adopt.apigw.modules.InventoryManagement.inventoryMapping.InventoryMappingService;
import com.adopt.apigw.modules.InventoryManagement.inventoryMapping.NMSIntegrationResDto;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerLedgerDetailsService;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerLedgerService;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerPaymentService;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.childcustomer.entity.ChildCustomer;
import com.adopt.apigw.modules.childcustomer.implemetation.ChildCustomerImpl;
import com.adopt.apigw.modules.childcustomer.repository.ChildCustomerRepo;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
import com.adopt.apigw.modules.paymentGatewayMaster.service.PaymentGatewayService;
import com.adopt.apigw.modules.placeOrder.model.OrderDTO;
import com.adopt.apigw.modules.placeOrder.service.OrderService;
import com.adopt.apigw.modules.placeOrder.service.PurchaseThread;
import com.adopt.apigw.modules.purchaseDetails.model.PurchaseDetailsDTO;
import com.adopt.apigw.modules.purchaseDetails.model.PurchaseHistoryReqDTO;
import com.adopt.apigw.modules.purchaseDetails.service.PurchaseDetailsService;
import com.adopt.apigw.modules.subscriber.model.*;
import com.adopt.apigw.modules.subscriber.service.InvoiceThread;
import com.adopt.apigw.modules.subscriber.service.ReceiptThread;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.pojo.PaginationDetails;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CustChangePasswordMsg;
import com.adopt.apigw.rabbitMq.message.CustomMessage;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.repository.postpaid.CustPlanMappingRepository;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.FileSystemService;
import com.adopt.apigw.service.postpaid.*;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = UrlConstants.BASE_PORTAL_API_URL + UrlConstants.SUBSCRIBER_BASE_URL)
public class CustomerPortalController {

    private static String MODULE = " [CustomerPortalController] ";

    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    private CustomersService customersService;
//    @Autowired
//    private CaseUpdateService caseUpdateService;
//    @Autowired
//    private CaseService caseService;
    @Autowired
    private CommonListService commonListService;
    //    @Autowired
//    private CaseReasonService caseReasonService;
    @Autowired
    private CreditDocService creditDocService;
    @Autowired
    private DebitDocService debitDocService;
    @Autowired
    private PostpaidPlanService postpaidPlanService;
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PurchaseDetailsService purchaseDetailsService;
    @Autowired
    private PartnerLedgerDetailsService partnerLedgerDetailsService;
    @Autowired
    private PartnerLedgerService partnerLedgerService;
    @Autowired
    private PartnerPaymentService partnerPaymentService;
    @Autowired
    private CustomerLedgerService customerLedgerService;
    @Autowired
    private CustomerLedgerDtlsService customerLedgerDtlsService;
    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private CustomerDocDetailsService customerDocDetailsService;
    @Autowired
    public PostpaidPlanService planService;
    @Autowired
    ClientServiceSrv clientServiceSrv;
    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    private BillRunService billRunService;
    @Autowired
    MessageSender messageSender;
    @Autowired
    NotificationTemplateRepository templateRepository;
    @Autowired
    CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    ClientServiceRepository clientServiceRepository;
    @Autowired
    private InventoryMappingService inventoryMappingService;

    @Autowired
    private ChildCustomerRepo childCustomerRepo;
    @Autowired
    private ChildCustomerImpl childCustomerService;

    @Autowired
    private CustomerMapper customerMapper;

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;
    private static final Logger logger = LoggerFactory.getLogger(CustomerPortalController.class);
    @Autowired
    private PostpaidPlanChargeService postpaidPlanChargeService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginPojo pojo,@RequestParam("mvnoId") Integer mvnoId) throws Exception {

        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            // TODO: pass mvnoID manually 6/5/2025
            Optional<ChildCustomer> childCustomerList = childCustomerRepo.findByUserNameAndMvnoIdInAndIsdeleted(pojo.getUsername(),Arrays.asList(mvnoId.longValue(),1L),false);
            // TODO: pass mvnoID manually 6/5/2025
            if (childCustomerList.isPresent() && (childCustomerList.get().getIsParent().equals(true) && !childCustomerService.hasMultipleParents(pojo.getUsername(),mvnoId.longValue()))) {
                ApplicationLogger.logger.info("Entered Password:" + pojo.getPassword() + ":In DB Password:" + childCustomerList.get().getPassword());
                if (pojo.getPassword().equals(childCustomerList.get().getPassword())) {
                    Customers customers = customersRepository.findById(childCustomerList.get().getParentCustId().intValue()).get();
                    ApplicationLogger.logger.info("Login Success");
                    response.put("userId", customers.getId());
                    response.put("email", customers.getEmail());
                    response.put("mvnoId", customers.getMvnoId());
                    response.put("fistName", customers.getFirstname());
                    response.put("lastName", customers.getLastname());
                    response.put("partnerId", customers.getPartner().getId());
                    response.put("accountNumber",customers.getAcctno());
                    response.put(CommonConstants.RESPONSE_MESSAGE, "Login Success.");
                    RESP_CODE = APIConstants.SUCCESS;
                    logger.info("User Name " + pojo.getUsername() + " is Successfull  :  request: {  Response : {{}}", RESP_CODE, response);
                } else {
                    ApplicationLogger.logger.info("Password Not Match");
                    response.put(CommonConstants.RESPONSE_MESSAGE, "Password Not Match");
                    RESP_CODE = APIConstants.FAIL;
                    logger.error("Password Not Match for user  " + pojo.getUsername() + " :  request: {Response : {{}};Error :{} ;", RESP_CODE, response);
                }
            }
            else {
                response = childCustomerService.childLogin(pojo,childCustomerList);
                String status = (String) response.get("status");
                if ("SUCCESS".equalsIgnoreCase(status)) {
                    RESP_CODE = APIConstants.SUCCESS;
                } else {
                    RESP_CODE = APIConstants.EXPECTATION_FAILED;
                }
            }
        } catch (CustomValidationException ce) {
            //   ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to Login for User " + pojo.getUsername() + " :  request: {Response : {{}};Error :{};Exception:{} ;", RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            // ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to Login for User " + pojo.getUsername() + " :  request: {Response : {{}};Error :{};Exception:{} ;", RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);

    }
    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_VIEW + "\")")
    @GetMapping(value = "/getBasicCustDetails/{customerId}")
    public GenericDataDTO getBasicSubscriberDetails(@PathVariable Integer customerId, HttpServletRequest req) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getBasicSubscriberDetails()] ";

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String name = ((CustomersBasicDetailsPojo) genericDataDTO.getData()).getName();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("Unable to fetch besic details of Customer,  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to fetch besic details of Customer,  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.getBasicDetailsOfSubscriber(customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            CustomersBasicDetailsPojo entity = (CustomersBasicDetailsPojo) genericDataDTO.getData();
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW, req.getRemoteAddr(), null, entity.getId().longValue(), entity.getName());
            logger.info("Fetching get BasicSubscriber Details of Customer with name  " + name + ":  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        } catch (Exception ex) {
            // ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to fetch Basic Details of  Customer with name " + name + ",  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        }
        MDC.remove("type");
        return genericDataDTO;
    }
    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_CHANGE_PASSWORD + "\",\""
            + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_CHANGE_PASSWORD + "\")")
    @PostMapping(value = "/updatePassword")
    public GenericDataDTO updateCustomersPassword(@RequestBody PasswordPojo pojo, HttpServletRequest req) throws NoSuchFieldException {
        MDC.put("type", "Create");
        String SUBMODULE = MODULE + " [updateCustomerPassword()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Customers customer = customersRepository.findById(pojo.getCustId()).get();
            Integer dataMvnoId = customer.getMvnoId();
            Integer currentMvnoId = customersService.getLoggedInMvnoId(pojo.getCustId());
            if(currentMvnoId==1 || dataMvnoId.equals(currentMvnoId)){
                if (null == pojo.getCustId()) {
                    genericDataDTO.setResponseMessage("Please provide customer !");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    logger.error("Unable to Change Customer Password,  request: { From : {}, }; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                    return genericDataDTO;
                }
                String   oldvalue= customer.getPassword();
                String newvalue =  pojo.getNewpassword();

                // Customers customers = customersService.get(pojo.getCustId());
                Customers customers = customersService.getCutomer(pojo);

                if (customers == null) {
                    genericDataDTO.setResponseMessage("Customer not found!");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                    logger.error("Unable to Update Customer Password,  request: { From : {}, }; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                    return genericDataDTO;
                }
                String nmsEnable = clientServiceRepository.findValueByNameAndMvnoId(NMSIntegrationConstants.NMS_INTEGRATION.NMS_INTEGRATION_ENABLE, customers.getMvnoId());
                if(nmsEnable != null && nmsEnable.equalsIgnoreCase(NMSIntegrationConstants.NMS_INTEGRATION.TRUE_FLAG)) {
                    String token = req.getHeader(NMSIntegrationConstants.NMS_INTEGRATION.AUTHORIZATION);
                    NMSIntegrationResDto nmsIntegrationResDto = inventoryMappingService.sendNMSUpdateWANConfig(token, pojo.getCustId(), customers.getUsername(), pojo.getPassword());
                    if (!nmsIntegrationResDto.isApiFlag()) {
                        genericDataDTO.setResponseMessage(nmsIntegrationResDto.getApiMessage());
                        genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                        logger.error("Unable to Update Customer Password,  request: { From : {}, }; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                        return genericDataDTO;
                    }
                }
                String name = customers.getUsername();
                customers.setPassword(pojo.getNewpassword());
                customersService.update(customers);
                 childCustomerService.updateChildPasswordAdmin(pojo);
                // method call for updateCustomerEntityForAllMicroServce//
                CustomersPojo customersPojo = new CustomersPojo();
                customersPojo = customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext());
                List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustomerId(customersPojo.getId());
                List<CustomerServiceMapping> customerServiceMappinList = customerServiceMappingRepository.findAllByCustId(customersPojo.getId());
                createDataSharedService.updateCustomerEntityForAllMicroServce(customerMapper.dtoToDomain(customersPojo, new CycleAvoidingMappingContext()), custPlanMapppingList, customerServiceMappinList);
                /*method called for cust change password send notification*/
                sendCustChangePasswordMessage(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getStatus(), customers.getMvnoId(), customers.getBuId(), customers.getPassword(),customers.getLastModifiedById().longValue());

                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setTotalRecords(1);
                genericDataDTO.setPageRecords(1);
                genericDataDTO.setTotalPages(1);
                genericDataDTO.setCurrentPageNumber(1);
                logger.info("Customer With name " + name + " has updated password Successfully  request: { From : {}, }; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                String fieldName = customer.getFullName();
                String remark = UpdateDiffFinder.generateDiffRemark(oldvalue, newvalue, fieldName);
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CHANGE_PASSWORD, AclConstants.OPERATION_CHANGE_PASSWORD,
                        req.getRemoteAddr(), remark, customers.getId().longValue(), customers.getFullName());
            }else {
                genericDataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());

            }
        } catch (Exception ex) {
            //     ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to Update Customer Password with Customer  " + pojo.getCustId() + " ,  request: { From : {}, }; Response : {{}};Error :{} ; exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    //    @PostMapping(value = "/resetPassword")
//    public GenericDataDTO resetCustomersPassword(@RequestBody PasswordPojo pojo) throws NoSuchFieldException {
//        String SUBMODULE = MODULE + " [resetCustomerPassword()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            if (null == pojo.getCustId()) {
//                genericDataDTO.setResponseMessage("Please provide customer id!");
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                return genericDataDTO;
//            }
//            Customers customers = customersService.get(pojo.getCustId());
//            if (customers == null) {
//                genericDataDTO.setResponseMessage("Customer not found!");
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                return genericDataDTO;
//            }
//            customers.setPassword(customers.getUsername());
//            customersService.update(customers);
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(1);
//            genericDataDTO.setTotalPages(1);
//            genericDataDTO.setCurrentPageNumber(1);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//        }
//        return genericDataDTO;
//    }
    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_CHANGE_SELFCARE_PASSWORD + "\")")
    @PostMapping(value = "/self/updatePassword")
    public GenericDataDTO updateSelfPassword(@RequestBody PasswordPojo pojo, HttpServletRequest req) throws NoSuchFieldException {
        String SUBMODULE = MODULE + " [updateSelfPassword()] ";
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("Unable to Update Password  :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(pojo.getCustId()).get();
            if (customers == null) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to to Update Password  :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            customers.setSelfcarepwd(pojo.getNewpassword());
            customersService.update(customers);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            Customers cust = (Customers) genericDataDTO.getData();
            logger.info("Customer With name " + customers.getUsername() + " 's password is Updated  Successfully :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
//                    AclConstants.OPERATION_CUSTOMER_CHANGE_SELFCARE_PASSWORD, req.getRemoteAddr(), null, cust.getId().longValue(), cust.getUsername());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to to Update Password with customer id " + pojo.getCustId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_RESET_SELFCARE_PASSWORD + "\")")
    @PostMapping(value = "/self/resetPassword")
    public GenericDataDTO resetSelfPassword(@RequestBody PasswordPojo pojo, HttpServletRequest req) throws NoSuchFieldException {
        String SUBMODULE = MODULE + " [resetSelfPassword()] ";
        MDC.put("type", "Update");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("Unable to Update Password  :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(pojo.getCustId()).get();
            if (customers == null) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to to Update Password  :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            customers.setSelfcarepwd(customers.getUsername());
            customersService.update(customers);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            logger.info("Customer With name " + customers.getUsername() + " 's password is Updated  Successfully :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
//                    AclConstants.OPERATION_CUSTOMER_RESET_SELFCARE_PASSWORD, req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
        } catch (Exception ex) {
            //  ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to to Update Password with customer id " + pojo.getCustId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping(value = "/forgotPassword")
    public GenericDataDTO forgotPassword(@RequestBody ForgotPassowrdDTO pojo) throws NoSuchFieldException {
        String SUBMODULE = MODULE + " [forgotPassword()] ";
        MDC.put("type", "Update");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getUsername()) {
                genericDataDTO.setResponseMessage("Please provide UserName !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("Unable to Update Password  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersService.getByUserName(pojo.getUsername());
            customersService.getEntityForUpdateAndDelete(customers.getId(),customers.getMvnoId());
            if (customers == null) {
                genericDataDTO.setResponseMessage("User not found! Please Enter Valid User !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to Update Password  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            } else {
                String otp = customersService.forgotPass(customers);
                genericDataDTO.setData(otp);
                genericDataDTO.setResponseMessage(otp);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                logger.info("Customer With name " + pojo.getUsername() + " 's password is Updated  Successfully :  request: { From : {}}; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode());

            }
        } catch (Exception ex) {
            // ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to to Update Password with customer name " + pojo.getUsername() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping(value = "/validateForgotPassword")
    public GenericDataDTO ValidateForgotPassword(@RequestBody ForgotPassowrdDTO pojo) throws NoSuchFieldException {
        String SUBMODULE = MODULE + " [ValidateForgotPassword()] ";
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getUsername()) {
                genericDataDTO.setResponseMessage("Please provide UserName !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("User not found :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            if (null == pojo.getOtp()) {
                genericDataDTO.setResponseMessage("Please provide OTP !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("Password format is invalid from user " + pojo.getUsername() + "  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersService.getByUserName(pojo.getUsername());
            customersService.getEntityForUpdateAndDelete(customers.getId(),customers.getMvnoId());
            if (customers == null) {
                genericDataDTO.setResponseMessage("User not found! Please Enter Valid User !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("User not found:  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            } else {
                String res = customersService.validateForgotPassword(customers, pojo);
                genericDataDTO.setResponseMessage(res);
                if (res.equalsIgnoreCase("Success")) {
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    logger.info("Customer With name " + pojo.getUsername() + " 's password is validated  Successfully  :  request: { From : {}}; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode());
                } else {
                    genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                    logger.error("Invalid Password format from " + pojo.getUsername() + ":  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                }

            }
        } catch (Exception ex) {
            //  ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Invalid Password format from " + pojo.getUsername() + "  :  request: { From : {}}; Response : {{}};Error :{} ;exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping(value = "/updateProfile")
    public GenericDataDTO updateProfile(@RequestBody UpdateProfileDTO pojo) throws NoSuchFieldException {
        String SUBMODULE = MODULE + " [UpdateProfile()] ";
        MDC.put("type", "Update");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getId()) {
                genericDataDTO.setResponseMessage("Please provide Customer !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("Unable to Update profile  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersService.getEntityForUpdateAndDelete(pojo.getId(),customersService.getMvnoIdFromCurrentStaff(pojo.getId()));
            String updatedValues = UtilsCommon.getUpdatedDiff(customers, pojo);
            if (customers == null) {
                genericDataDTO.setResponseMessage("User not found! Please Enter Valid User !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to Update profile  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            } else {
                CustomersPojo customersPojo = customersService.updateProfile(customers, pojo);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setData(customersPojo);
                CommunicationHelper communicationHelper = new CommunicationHelper();
                Map<String, String> map = new HashMap<>();
                map.put(CommunicationConstant.EMAIL, customersPojo.getEmail().toString());
                map.put(CommunicationConstant.DESTINATION, customersPojo.getMobile());
                if (customersPojo.getGst() != null) {
                    map.put(CommunicationConstant.GSTIN, customersPojo.getGst());
                } else {
                    map.put(CommunicationConstant.GSTIN, "XXXXXX");
                }
                communicationHelper.generateCommunicationDetails(CommunicationConstant.PROFILE_UPDATE, Collections.singletonList(map));
                logger.info("Customer With  user name " + pojo.getFirstname() + " 's profile  is Updated  Successfully  :  request: { From : {}}; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode());
            }
        } catch (Exception ex) {
            //     ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to Update profile with user name " + pojo.getFirstname() + "  :  request: { From : {}}; Response : {{}};Error :{} ;exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping(value = "/changePassword")
    public GenericDataDTO changeCustomerPassword(@RequestBody ChangePasswordDTO pojo) throws NoSuchFieldException {
        String SUBMODULE = MODULE + " [changeCustomerPassword()] ";
        MDC.put("type", "Update");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getId()) {
                genericDataDTO.setResponseMessage("Please provide User!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("Unable to Update Password  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(pojo.getId()).get();
            if (customers == null) {
                genericDataDTO.setResponseMessage("User not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to Update Password  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            if (customers.getPassword().equals(pojo.getOldPassword())) {
                genericDataDTO.setResponseMessage("Password change successfully");
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                customers.setPassword(pojo.getNewPassword());
                Customers customers1 = customersService.save(customers);
                CustomersPojo customersPojo = customerMapper.domainToDTO(customers1 , new CycleAvoidingMappingContext());
                CustomMessage customMessage = new CustomMessage(customersPojo);
                List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustomerId(customersPojo.getId());
                List<CustomerServiceMapping> customerServiceMappinList = customerServiceMappingRepository.findAllByCustId(customersPojo.getId());
                createDataSharedService.updateCustomerEntityForAllMicroServce(customerMapper.dtoToDomain(customersPojo, new CycleAvoidingMappingContext()),custPlanMapppingList,customerServiceMappinList);
                /*method called for cust change password send notification*/
                sendCustChangePasswordMessage(customers1.getUsername(), customers1.getMobile(), customers1.getEmail(), customers1.getStatus(), customers1.getMvnoId(),customers1.getBuId(),customers1.getPassword(),customers1.getLastModifiedById().longValue());
                //messageSender.send(customMessage, RabbitMqConstants.QUEUE_APIGW_CUSTOMER);
                kafkaMessageSender.send(new KafkaMessageData(customMessage,customMessage.getClass().getSimpleName(),"CUSTOMER_CREATE"));
                logger.info("Customer With name " + customers.getUsername() + " 's password is Updated  Successfully  :  request: { From : {}}; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode());
            } else {
                genericDataDTO.setResponseMessage("Old Password Not Match!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("Unable to Update Password From user " + customers.getUsername() + " :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }

            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
        } catch (Exception ex) {
            // ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to Update Password  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_APPLY_CHARGE + "\")")
    @GetMapping(value = "/getSubscriberCharges/{custId}")
    public GenericDataDTO getSubscriberCharges(@PathVariable Integer custId, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getSubscriberPLanDetails()] ";
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Customers customers = customersRepository.findById(custId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to get Customer details  : request: { From : {}}; Response : {{}};Error :{}  ;", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.subscriberService.getSubscriberCharges(customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            logger.info("Customer With name " + customers.getUsername() + " 's details fetched Successfully :  request: { From : {}}}; Response : {{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode());
//            Customers customers1 = (Customers) genericDataDTO.getData();
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
//                    AclConstants.OPERATION_CUSTOMER_APPLY_CHARGE, req.getRemoteAddr(), null, customers1.getId().longValue(), customers1.getFullName());
        } catch (Exception ex) {
            //  ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to fetch user data  :  request: { From : {}}; Response : {{}};Error :{}Exception:{} ;", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
 //   @PostMapping(path = "/updateCaseDetails" + UrlConstants.CASE_UPDATE_DETAILS, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public GenericDataDTO updateDetails(@RequestParam String caseUpdate, @RequestParam(value = "file", required = false) List<MultipartFile> file, HttpServletRequest req) {
//        String SUBMODULE = MODULE + " [updateDetails()] ";
//        MDC.put("type", "Update");
//        CaseUpdateDTO convDTO;
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseMessage("Success");
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        logger.info("update case details Successfully :  request: { From : {}}}; Response : {{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode());
//        try {
//            convDTO = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(caseUpdate, CaseUpdateDTO.class);
//            CaseDTO caseDTO = caseUpdateService.updateEntity(convDTO, file, false);
//            genericDataDTO.setData(caseDTO);
//            genericDataDTO.setTotalRecords(1);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE, AclConstants.OPERATION_CASE_EDIT, req.getRemoteAddr(), null, caseDTO.getCaseId(), caseDTO.getUserName());
//            logger.error("Unable to Update case details  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            MDC.remove("type");
//            return genericDataDTO;
//        } catch (JsonProcessingException e) {
//            //    ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
//            genericDataDTO.setResponseCode(HttpStatus.FAILED_DEPENDENCY.value());
//            genericDataDTO.setResponseMessage(HttpStatus.FAILED_DEPENDENCY.getReasonPhrase());
//            logger.error("Unable to Update case details  :  request: { From : {}}; Response : {{}};Error :{} ;Ecxeption :{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
//            return genericDataDTO;
//        } catch (IOException e) {
//            //   ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_MODIFIED.value());
//            genericDataDTO.setResponseMessage("File not saved");
//            logger.error("Unable to Update case details  :  request: { From : {}}; Response : {{}};Error :{} ; Exception : {}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
//            return genericDataDTO;
//        } catch (Exception e) {
//            //    ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to Update Cse Details  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
//            MDC.remove("type");
//            return genericDataDTO;
//        }
//
//    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_ADD + "\")")
//    @PostMapping(path = "/createCase")
//    public GenericDataDTO save(@Valid @RequestBody CaseDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Create");
//        String SUBMODULE = MODULE + " [getSubscriberPLanDetails()] ";
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        logger.info("Case created  Successfully  with title " + entityDTO.getCaseTitle() + ":  request: { From : {}}}; Response : {{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode());
//        try {
//
//            if (result.hasErrors()) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
//                logger.error("Unable to  create Case with title  " + entityDTO.getCaseTitle() + "  :  request: {  From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//                return genericDataDTO;
//            }
//
//            if (null == entityDTO.getCaseType()) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Please Select CaseType!");
//                logger.error("Unable to  case title " + entityDTO.getCaseTitle() + "  :  request: {  From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//                return genericDataDTO;
//            }
//
//            //Set Prefix
//            String prefix = "";
//            if (entityDTO.getCaseType().equalsIgnoreCase(CaseConstants.CASE_TYPE_ISSUE))
//                prefix = CaseConstants.PREFIX_TKT;
//            else if (entityDTO.getCaseType().equalsIgnoreCase(CaseConstants.CASE_TYPE_REQUEST))
//                prefix = CaseConstants.PREFIX_REQ;
//            else if (entityDTO.getCaseType().equalsIgnoreCase(CaseConstants.CASE_TYPE_INQUIRY))
//                prefix = CaseConstants.PREFIX_INQ;
//
//            //Set CaseNumber
//            CaseDTO caseDTO = caseService.getCaseByCaseType(entityDTO.getCaseType());
//            if (null != caseDTO) {
//                String number = caseDTO.getCaseNumber().split("-")[1];
//                entityDTO.setCaseNumber(prefix + "-" + Integer.parseInt(String.valueOf(Long.parseLong(number) + 1)));
//            } else entityDTO.setCaseNumber(prefix + "-" + "1");
//
//            if (null != entityDTO.getCurrentAssigneeId()) entityDTO.setCaseStatus(CaseConstants.STATUS_ASSIGNED);
//            else entityDTO.setCaseStatus(CaseConstants.STATUS_UN_ASSIGNED);
//
//            if (null != entityDTO.getCustomersId()) {
//                Customers customers = customersService.get(entityDTO.getCustomersId());
//                if (null != customers && null != customers.getPartner()) {
//                    entityDTO.setPartnerid(customers.getPartner().getId());
//                }
//            }
//
//            entityDTO = caseService.saveEntity(entityDTO);
//            genericDataDTO.setData(entityDTO);
//            genericDataDTO.setTotalRecords(1);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE, AclConstants.OPERATION_CASE_ADD, req.getRemoteAddr(), null, entityDTO.getCaseId(), entityDTO.getUserName());
//            logger.info("update case created Successfully with title " + entityDTO.getCaseTitle() + ":  request: { From : {}}}; Response : {{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode());
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(MODULE + " [case save()] " + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to create case with titile " + entityDTO.getCaseTitle() + " :  request: { From : {}}; Response : {{}};Error :{} ;exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    protected String getDefaultErrorMessages(List<FieldError> list) {

        if (null == list || list.size() < 1) {
            return "Something went wrong, Please try after some time";
        }
        String outputStr = "";
        String cm = "";
        for (FieldError fe : list) {
            outputStr = outputStr + cm + fe.getDefaultMessage() + ". Rejected Value: (" + fe.getRejectedValue() + ")";
            cm = " \n";

        }
        MDC.remove("type");
        return outputStr;
    }

    @GetMapping(path = "/commonList" + UrlConstants.GENERIC + "/{type}")
    public GenericDataDTO getCommonListWithCacheByType(@PathVariable("type") String type) throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = MODULE + "[getCommonListByPaymentStatus]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        logger.info("Fetching Common List Successfully :  request: { From : {}, }; Response : {{}}", SUB_MODULE, genericDataDTO.getResponseCode());
        try {
            ApplicationLogger.logger.info(SUB_MODULE);
            List<CommonListDTO> list = commonListService.getCommonListByType(type);
            List<CommonListDTO> sortedList = list.stream().sorted(Comparator.comparing(CommonListDTO::getIdentityKey).reversed()).collect(Collectors.toList());
            genericDataDTO.setDataList(sortedList);
            genericDataDTO.setTotalRecords(sortedList.size());
            logger.info("Fetching Common List Successfully :  request: { From : {}, }; Response : {{}}", SUB_MODULE, genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUB_MODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable Fetch Common List   :  request: { From : {}}; Response : {{}};Error :{} ;exception:{}", SUB_MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_REASON_ALL + "\",\"" + AclConstants.OPERATION_CASE_REASON_VIEW + "\")")
//    @GetMapping(path = "/caseReason/all")
//    public GenericDataDTO getAllCaseReason() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        try {
//            List<CaseReasonDTO> list = caseReasonService.getAllEntities();
//            genericDataDTO.setDataList(list);
//            genericDataDTO.setTotalRecords(list.size());
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage("Failed to load data");
//        }
//
//        return genericDataDTO;
//    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_VIEW_PLANS + "\")")
    @GetMapping(value = "/getActivePlanList/{customerId}")
    public GenericDataDTO getActivePlanList(@PathVariable Integer customerId, @RequestParam(name = "isNotChangePlan", required = true, defaultValue = "false") Boolean isNotChangePlan) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getActivePlanList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("Unable to Update Fetch get Active plan list  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to Update Fetch get Active plan list  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            logger.info("Fetching get plan list from usr " + customers.getUsername() + " :  request: { From : {}, }; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode());
            return GenericDataDTO.getGenericDataDTO(subscriberService.getActivePlanList(customerId, isNotChangePlan));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to Fetch Plan list  :  request: { From : {}}; Response : {{}};Error :{} ;exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping(value = "/getTrialPlanList/{customerId}")
    public GenericDataDTO getTrailPlanList(@PathVariable Integer customerId, @RequestParam(name = "serviceId", required = false) Integer serviceId) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getTrailPlanList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("Unable to Update Fetch get Trail plan list  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to Update Fetch get Trail plan list  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            logger.info("Fetching get plan list from usr " + customers.getUsername() + " :  request: { From : {}, }; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode());
            List<CustomerPlansModel> planslist = subscriberService.getTrialPlanList(customerId, true);
            if (serviceId != null) {
                return GenericDataDTO.getGenericDataDTO(subscriberService.getActivePlanListForServiceId(planslist, serviceId));
            }
            return GenericDataDTO.getGenericDataDTO(planslist);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to Fetch Plan list  :  request: { From : {}}; Response : {{}};Error :{} ;exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_VIEW_PLANS + "\")")
    @GetMapping(value = "/getFuturePlanList/{customerId}")
    public GenericDataDTO getFuturePlanList(@PathVariable Integer customerId) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getFuturePlanList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("Unable to Fetch future planlist by customer  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to Fetch future planlist by customer  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            logger.info("Fetching get future plan list from customer " + customers.getUsername() + " :  request: { From : {}, }; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode());
            return GenericDataDTO.getGenericDataDTO(subscriberService.getFuturePlanList(customerId, true));

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to Fetch future planlist by customer :  request: { From : {}}; Response : {{}};Error :{} ;excepton:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_VIEW_PLANS + "\")")
    @GetMapping(value = "/getExpiredPlanList/{customerId}")
    public GenericDataDTO getExpiredPlanList(@PathVariable Integer customerId) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getExpiredPlanList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("Unable to Fetch Expired plan list by customer :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to fetch expired plan list by customer  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            logger.info("Fetching get Expired plan list from customer " + customers.getUsername() + " :  request: { From : {}, }; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode());
            return GenericDataDTO.getGenericDataDTO(subscriberService.getExpiredPlanList(customerId, true));

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to fetch expired plan list by user :  request: { From : {}}; Response : {{}};Error :{};exception:{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_VIEW_PAYMENT_HISTORY + "\")")
    @GetMapping(value = "/paymentHistory/{custId}")
    public GenericDataDTO getPaymentHistory(@PathVariable Integer custId) throws Exception {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getDbcdrProcessing()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (custId == null) {
                genericDataDTO.setResponseMessage("ID not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to Fetch Payment History by user  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(custId).get();
            if (customers == null) {
                genericDataDTO.setResponseMessage("Records not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to fetch Payment History by user  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            List<PaymentHistoryDTO> paymentHistories = creditDocService.getByCustId(custId);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(paymentHistories);
            logger.info("Fetching payment History by user  " + customers.getUsername() + " :  request: { From : {}, }; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode());
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to fetch psyment history by user:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_VIEW_PURCHASE_HISTORY + "\")")
    @GetMapping(value = "/purchasedHistory/{custId}")
    public GenericDataDTO getPurchasedHistory(@PathVariable Integer custId) throws Exception {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getDbcdrProcessing()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (custId == null) {
                genericDataDTO.setResponseMessage("ID not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to Fetch purchased history of customer :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(custId).get();
            if (customers == null) {
                genericDataDTO.setResponseMessage("Records not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to Fetch purchase history of customer  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            List<PurchasedHistoryDTO> purchasedtHistories = subscriberService.getByPurchaseHistoryCustId(custId);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(purchasedtHistories);
            genericDataDTO.setTotalRecords(purchasedtHistories.size());
            logger.info("Fetching Purchase history of customer  " + customers.getUsername() + " :  request: { From : {}, }; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode());
        } catch (Exception e) {
            // ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to fetch purchase history of customer :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }


    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
 //   @GetMapping(value = UrlConstants.GET_CASES_BY_CUSTOMER + "/{custId}")
//    public GenericDataDTO getCasesByCustomer(@PathVariable Integer custId) {
//        MDC.put("type", "Fetch");
//        String SUBMODULE = MODULE + " [getCasesByCustomer()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            Customers customers = customersService.get(custId);
//            if (null == custId) {
//                genericDataDTO.setResponseMessage("Please Provide Customer!");
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                logger.error("Unable to fetch case by customer   :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//                return genericDataDTO;
//            }
//            logger.info("Fetching get future plan list from usr " + customers.getUsername() + " :  request: { From : {}, }; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode());
//            return GenericDataDTO.getGenericDataDTO(caseService.getAllCaseByCustomer(custId));
//        } catch (Exception ex) {
//            //   ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            logger.error("Unable to fetch future plan of customer :  request: { From : {}}; Response : {{}};Error :{} ;exception : {}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_POSTPAID_PLAN_ALL + "\",\"" + AclConstants.OPERATION_POSTPAID_PLAN_VIEW + "\")")
    @PostMapping(value = "/plan/Bypartner")
    public GenericDataDTO getPlanByPartner(@RequestBody PlanByPartnerReqDTO dto) throws Exception {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getPlanByPartner()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (dto.getPartnerId() == null) {
                genericDataDTO.setResponseMessage("Please select partner!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to create plan by partner :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            } else {
                genericDataDTO.setDataList(postpaidPlanService.getPlanByPartnerId(dto));
                genericDataDTO.setTotalRecords(genericDataDTO.getDataList().size());
                logger.info("plan by parnter with name  " + dto.getPlanGroup() + " is created successfully " + dto.getPlanGroup() + " :  request: { From : {}, }; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode());
            }
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to create plan by praner " + dto.getPlanGroup() + "  :  request: { From : {}}; Response : {{}};Error :{} ;exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping(value = "/getPGForUsers")
    public GenericDataDTO getPGForUsers() {
        MDC.put("type", "Fetch");
        String SUBMODULE = "Payment gateway" + " [getSubscriberPLanDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.paymentGatewayService.getPGForUsers());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            logger.info("Fetching get pg for Users  :  request: { From : {}, }; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to fetch pg for Users  :  request: { From : {}}; Response : {{}};Error :{} ;exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping("/order/place")
    public GenericDataDTO placeOrder(@RequestBody OrderDTO requestDTO, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = "Payment gateway" + " [getSubscriberPLanDetails()] ";
        MDC.put("type", "Create");
        Customers customers = customersRepository.findById(requestDTO.getCustId().intValue()).get();
        try {
            PostpaidPlan postpaidPlan = planService.get(requestDTO.getEntityid().intValue(),customers.getMvnoId());
            if (!postpaidPlan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW) && !postpaidPlan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_ADDON)) {
                logger.error("Please select valid plan  for customer " + customers.getUsername() + ":  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                throw new RuntimeException("Please select valid plan");
            } else {
                if (postpaidPlan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_ADDON)) {
                    orderService.validatePurchaseAddon(requestDTO);
                }
                genericDataDTO.setData(orderService.placeOrder(requestDTO, "", request));
            }
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Plan is activated successully for user" + customers.getUsername() + " :  request: { From : {}, }; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode());
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                genericDataDTO.setResponseMessage(e.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                logger.error("Unable to place order of customer " + customers.getUsername() + ":  request: { From : {}}; Response : {{}};Error :{} ;exception: {}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
                return genericDataDTO;
            }
            e.printStackTrace();
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to place order of customer " + customers.getUsername() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @RequestMapping(value = "/order/process", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public void processOrder(@RequestParam Map<String, Object> response, HttpServletResponse httpResponse, HttpServletRequest request) throws IOException {
        MDC.put("type", "Create");
        Properties properties = PropertyReaderUtil.getPropValues(PGConstants.PGCONFIG_FILE);
        String serverurl = properties.getProperty(PGConstants.PG_CONFIG_PAYU_SERVER_URL_CUST_PORTAL);
        try {
            PurchaseDetailsDTO dto = orderService.processPayment(response, request);

            Runnable purchaseRunnable = new PurchaseThread(dto, purchaseDetailsService, subscriberService, customersService, orderService, partnerLedgerDetailsService, partnerLedgerService, partnerPaymentService, customerLedgerService, customerLedgerDtlsService, request);

            SecurityContext context = SecurityContextHolder.getContext();
            DelegatingSecurityContextRunnable wrappedRunnable = new DelegatingSecurityContextRunnable(purchaseRunnable, context);

            Thread invoiceThread = new Thread(wrappedRunnable);
            invoiceThread.start();
            final String clientURL = serverurl + PGConstants.CLIENT_REDIRECT_URL_CUST_PORTAL.replace("{TXNID}", dto.getTransid());
            httpResponse.sendRedirect(clientURL);
            logger.info("Plan is ordered successully for user" + response + " :  request: { From : {},url: {} }; Response : {{}}", request.getHeader("requestFrom"), request.getRequestURL(), APIConstants.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to place order of customer:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", request.getHeader("requestFrom"), request.getRequestURL(), APIConstants.FAIL, e.getStackTrace());

            httpResponse.sendRedirect(serverurl + PGConstants.CLIENT_REDIRECT_URL_CUST_PORTAL.replace("{TXNID}", null));
        }
        MDC.remove("type");
    }

    //Billing Charge
    @PostMapping("/getTaxDetails/byPlan")
    public ResponseEntity<?> getTaxDetailByPlan(@RequestBody TaxDetailCountReqDTO pojo) throws Exception {
        MDC.put("type", "Create");
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = "Payment gateway" + " [getSubscriberPLanDetails()] ";
        HashMap<String, Object> response = new HashMap<>();
        try {
            TaxService taxService = SpringContext.getBean(TaxService.class);
            Double taxAmount = taxService.taxCalculationByPlan(pojo, postpaidPlanChargeService.getPostpaidPlanChargesByPlanId(pojo.getPlanId()));
            if (taxAmount != null) {
                response.put("TotalAmount", taxAmount);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info("Tax details for plan id " + pojo.getPlanId() + " :  request: { From : {} }; Response : {{}}", SUBMODULE, RESP_CODE);
            } else {
                response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
                RESP_CODE = APIConstants.FAIL;
                logger.error("Unable to get tax details for plan id " + pojo.getPlanId() + " :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, RESP_CODE, response);
            }
        } catch (CustomValidationException ce) {
            //ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to get tax details by plan id  :  request: { From : {}}; Response : {{}};Error :{};exception: {} ;", SUBMODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            // ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to get tax by plann id   :  request: { From : {}}; Response : {{}};Error :{} ;exception:{}", SUBMODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/getTaxDetails/byCharge")
    public ResponseEntity<?> getTaxDetailByCustomer(@RequestBody TaxDetailCountReqDTO pojo) throws Exception {
        MDC.put("type", "Create");
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [getPurchaseHistoryByParam()] ";
        HashMap<String, Object> response = new HashMap<>();
        try {
            TaxService taxService = SpringContext.getBean(TaxService.class);
            Double taxAmount = taxService.taxCalculationByCharge(pojo);
            if (taxAmount != null) {
                response.put("TotalAmount", taxAmount);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info("Get tax details by charge is successfull :  request: { From : {} }; Response : {{}}", SUBMODULE, RESP_CODE);
            } else {
                response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
                RESP_CODE = APIConstants.FAIL;
                logger.error("Unable to get charge details by charge:  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, RESP_CODE, response);
            }
        } catch (CustomValidationException ce) {
            //  ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to get Tax details by charge :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", SUBMODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            // ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to get tax details by charge :  request: { From : {}}; Response : {{}};Error :{} ;Excseption:{}", SUBMODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {

        String SUBMODULE = MODULE + " [apiResponse()] ";
        try {
            //logger.info(new ObjectMapper().writeValueAsString(response));
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);

            if (null != page) {
                response.put("pageDetails", setPaginationDetails(page));
            }

            if (responseCode.equals(APIConstants.SUCCESS)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (responseCode.equals(APIConstants.FAIL)) {
                // logger.error("Unable to Update Password  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE,APIConstants.FAIL,response);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                //   logger.error("Unable to Update Password  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE,APIConstants.INTERNAL_SERVER_ERROR,response);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (responseCode.equals(APIConstants.NOT_FOUND)) {
                //    logger.error("Unable to Update Password  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE,APIConstants.NOT_FOUND,response);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                //    logger.error("Unable to Update Password  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE,APIConstants.NOT_FOUND,response);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            //    ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            MDC.remove("type");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        return apiResponse(responseCode, response, null);
    }

    public PaginationDetails setPaginationDetails(Page page) {
        PaginationDetails pageDetails = new PaginationDetails();
        pageDetails.setTotalPages(page.getTotalPages());
        pageDetails.setTotalRecords(page.getTotalElements());
        pageDetails.setTotalRecordsPerPage(page.getNumberOfElements());
        pageDetails.setCurrentPageNumber(page.getNumber() + 1);
        return pageDetails;
    }

    @GetMapping("/txnStatus/{txnId}")
    public GenericDataDTO getTxnStatus(@PathVariable String txnId) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getPurchaseHistoryByParam()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            PurchaseDetailsDTO dto = purchaseDetailsService.getPurchaseBYTxnId(txnId);
            if (dto == null) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Txn Id can not be null or not found!!");
                logger.error("Unable to get Transaction Status by transaction Id " + txnId + "  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            genericDataDTO.setData(dto);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Fetching transaction status by transaction id " + txnId + " :  request: { From : {} }; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to fetch transaction details with transactionId " + txnId + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping("/purchaseHistory")
    public GenericDataDTO getPurchaseHistoryByParam(@RequestBody PurchaseHistoryReqDTO reqDTO) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getPurchaseHistoryByParam()] ";
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == reqDTO) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Request!");
                logger.error("Unable to Get Purchase History by param " + reqDTO + "  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            PaginationRequestDTO paginationRequestDTO = setDefaultPaginationValues(new PaginationRequestDTO());
            if (null == reqDTO.getPage()) reqDTO.setPage(paginationRequestDTO.getPage());
            if (null == reqDTO.getPageSize()) reqDTO.setPageSize(paginationRequestDTO.getPageSize());
            if (null == reqDTO.getSortOrder()) reqDTO.setSortOrder(paginationRequestDTO.getSortOrder());
            if (null == reqDTO.getSortBy()) reqDTO.setSortBy(paginationRequestDTO.getSortBy());
            if (null != reqDTO.getPageSize() && reqDTO.getPageSize() > MAX_PAGE_SIZE) reqDTO.setPageSize(MAX_PAGE_SIZE);
            logger.info("Fetching purchase history by param " + reqDTO + " :  request: { From : {} }; Response : {{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            return purchaseDetailsService.getAllPurchaseHistoryByParam(reqDTO);
        } catch (Exception ex) {
            // ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to fetch purchase History by param " + reqDTO + " :  request: { From : {}}; Response : {{}};Error :{} ;exception:{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
            MDC.remove("type");
            return genericDataDTO;
        }
    }

    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {

        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        this.PAGE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
        this.PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).get(0).getValue());
        this.SORT_BY = clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).get(0).getValue();
        this.SORT_ORDER = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).get(0).getValue());

        if (null == requestDTO.getPage()) requestDTO.setPage(PAGE);
        if (null == requestDTO.getPageSize()) requestDTO.setPageSize(PAGE_SIZE);
        if (null == requestDTO.getSortBy()) requestDTO.setSortBy(SORT_BY);
        if (null == requestDTO.getSortOrder()) requestDTO.setSortOrder(SORT_ORDER);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
            requestDTO.setPageSize(MAX_PAGE_SIZE);

        return requestDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
//    @GetMapping("/getCase/{id}")
//    public GenericDataDTO getCaseById(@PathVariable String id, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setData(caseService.getEntityById(Long.parseLong(id)));
//        CaseDTO caseEntity = (CaseDTO) genericDataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE, AclConstants.OPERATION_CASE_VIEW, req.getRemoteAddr(), null, caseEntity.getCaseId().longValue(), caseEntity.getUserName());
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_GET_PURCHASE_INVOICE + "\")")
    @RequestMapping(value = {"/invoice/download/{invoiceid}"}, method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadInvoice(@PathVariable Integer invoiceid, Model model,@RequestParam("mvnoId") Integer mvnoId) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [downloadInvoice()] ";
        Resource resource = null;
        try {
            DebitDocument doc = debitDocService.get(invoiceid,mvnoId);
            FileSystemService service = com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
            resource = service.getInvoice(doc.getBillrunid() + File.separator + doc.getDocnumber() + ".pdf");
            //resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                logger.info("Invoice With id " + invoiceid + " is downloaded Successfully  :  request: { From : {} }; Response : {{}}", SUBMODULE, APIConstants.SUCCESS);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                logger.error("Unable to Udownload invoice with id " + invoiceid + "   :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            logger.error("Unable to download invoice with id" + invoiceid + ":  request: { From : {}}; Response : {{}};Error :{};exception : {};", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound(), ex.getStackTrace());
            //      ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        MDC.remove("type");
        return null;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_GET_PAYMENT_RECEIPT + "\")")
    @RequestMapping(value = {"/payment/download/{paymentid}"}, method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadPaymentReceipt(@PathVariable Integer paymentid, Model model) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [downloadPaymentReceipt()] ";
        try {
            //DebitDocument doc = entityService.get(invoiceid);
            Resource resource = null;
            FileSystemService service = com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
            resource = service.getPaymentReceipt(paymentid + File.separator + paymentid + ".pdf");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                logger.info("Downloading Payment Recipt with PaymentId " + paymentid + " downloaded Successfully  :  request: { From : {} }; Response : {{}}", SUBMODULE, APIConstants.SUCCESS);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                logger.error("Unable to download payment Recipt with payment Id " + paymentid + "  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            logger.error("Unable to download Payment recipt with payment Id " + paymentid + " :  request: { From : {}}; Response : {{}};Error :{} ;exception:{}", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound(), ex.getStackTrace());
            ///    ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        MDC.remove("type");
        return null;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_GET_DOCUMENT + "\")")
    @RequestMapping(value = "/document/download/{docId}/{custId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long docId, @PathVariable Integer custId) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [downloadDocument()] ";
        Resource resource = null;
        try {
            Customers customers = customersRepository.findById(custId).get();
            if (null == customers) {
                return ResponseEntity.notFound().build();
            }
            CustomerDocDetailsDTO docDetailsDTO = customerDocDetailsService.getEntityById(docId, auditLogService.getMvnoIdFromCurrentStaff(custId));
            if (null == docDetailsDTO) {
                return ResponseEntity.notFound().build();
            }
            FileSystemService service = com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
            resource = service.getCustDoc(customers.getUsername().trim(), docDetailsDTO.getUniquename(), customers.getId());
            //resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                logger.info("Downloading document with  " + docId + " downloaded Successfully  :  request: { From : {} }; Response : {{}}", SUBMODULE, APIConstants.SUCCESS);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                logger.error("Unable to downloadDocument " + docId + " :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            logger.error("Unable to downloadDocument " + docId + "   :  request: { From : {}}; Response : {{}};Error :{} ;exception: {}", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound(), ex.getStackTrace());
            // ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        MDC.remove("type");
        return null;
    }

    @GetMapping(value = UrlConstants.CHECK_ELIGIBLE_ADDON + "/{custId}")
    public GenericDataDTO checkEligibilityAddon(@PathVariable Integer custId) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [checkEligibilityAddon] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Customers customers = customersRepository.findById(custId).get();
            String name =customers.getFullName();
            if (custId == null) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("Unable to Update check Elegibility Addon to " + name + "  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound());
                return genericDataDTO;
            }
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to check Elegebility addon to " + name + "  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound());
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.checkEligibilityAddon(customers));
            logger.info("checking elegebility for Addon " + name + " downloaded Successfully  :  request: { From : {} }; Response : {{}}", SUBMODULE, APIConstants.SUCCESS);
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to check elegebility :  request: { From : {}}; Response : {{}};Error :{} ;exception: {}", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound(), e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping(value = "/postpaidplan/all")
    public GenericDataDTO getAllPostpaidPlanList(HttpServletRequest req) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getAllPostpaidPlanList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(postpaidPlanService.getAllPlanList());
            logger.info("Checking postpaid Plan   :  request: { From : {} }; Response : {{}}", SUBMODULE, APIConstants.SUCCESS);
        } catch (Exception ex) {
            // ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to Fetch Postpaid plan  :  request: { From : {}}; Response : {{}};Error :{} ;exception:{}", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping(value = "/changePlan")
    public GenericDataDTO changePlan(@RequestBody ChangePlanRequestDTO requestDTO, @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [changePlan()] ";
        MDC.put("type", "Update");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == requestDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error("Unable to change plan :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            String updatedValues = UtilsCommon.getUpdatedDiff(customers, requestDTO);
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to  change Plan for Customer " + updatedValues + ":  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound());
                return genericDataDTO;
            }
            if (!requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON)) {
                List<CustomerPlansModel> currentPlanList = this.subscriberService.getActivePlanList(customers.getId(), false);
                if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW) || requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_UPGRADE)) {
                    if (currentPlanList.size() <= 0) {
                        genericDataDTO.setResponseMessage("Subscriber must have any active plan if Purchase type is renew");
                        genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                        logger.error("Unable to Change Paln  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound());
                        return genericDataDTO;
                    }
                } else {
                    if (currentPlanList.size() > 0) {
                        genericDataDTO.setResponseMessage("Subscriber already have active plan, Please select purchase type as Renew or Upgrade");
                        genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                        logger.error("Unable to Update Password  :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound());
                        return genericDataDTO;
                    }
                }
            } else {
//                ClientServicePojo clientServicePojo = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CONVERT_VOL_BOOST_TOPUP).get(0);
//                if (clientServicePojo.getValue().equalsIgnoreCase("1")) {
//                    subscriberService.validatePurchaseAddon(requestDTO);
//                }
            }
            if (subscriberService.getCustomerPlanList(customers.getId(), false).size() <= 0) {
                requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_NEW);
            } else {
                requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_RENEW);
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            logger.info("Plan changed For customer " + customers.getUsername() + " is  Successfully changed :  request: { From : {} }; Response : {{}}", SUBMODULE, APIConstants.SUCCESS);
            CustomChangePlanDTO customChangePlanDTO = subscriberService.changePlan(requestDTO, customers, false, 0.0, requestFrom, null);
            if (customChangePlanDTO.getRecordpaymentResponseDTO() != null) {
                if (customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument().size() > 0) {
                    List<CreditDocument> creditDocumentList = customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument();
                    Runnable receiptRunnable = new ReceiptThread(billRunService, creditDocumentList);
                    Thread receiptThread = new Thread(receiptRunnable);
                    receiptThread.start();
                }
            }

            CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
            genericDataDTO.setData(basicDetailsPojo);
            try {
                Customers customer = customersRepository.findById(basicDetailsPojo.getId()).get();
                customer.setBillRunCustPackageRelId(customChangePlanDTO.getCustpackagerelid());
                Runnable invoiceRunnable = new InvoiceThread(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), customer, customersService, "", null, null);
                Thread invoiceThread = new Thread(invoiceRunnable);
                invoiceThread.start();
            } catch (Exception e) {
                //  ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
                logger.error("Unable to change Plan :  request: { From : {}}; Response : {{}};Error :{} ;Exception: {}", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound(), e.getStackTrace());
                e.printStackTrace();
            }

            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            String auditChangePlan = null;
            if (requestDTO.getPurchaseType() != null) {
                if (requestDTO.getPlanId() != null) {
                    PostpaidPlan postpaidPlan = postpaidPlanService.get(requestDTO.getPlanId(),customers.getMvnoId());
                    if (postpaidPlan != null) {
                        auditChangePlan = customers.getFullName() + "change plan type is" + requestDTO.getPurchaseType() + "..and plan is :- " + postpaidPlan.getDisplayName();
                    }
                }

            }

            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW_PLANS, req.getRemoteAddr(), auditChangePlan, customers.getId().longValue(), customers.getFullName());

        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                logger.error("Unable to change plan  :  request: { From : {}}; Response : {{}};Error :{} ;Exception: {}", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound(), ex.getStackTrace());
                return genericDataDTO;
            }
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("Unable to change plan  :  request: { From : {}}; Response : {{}};Error :{} ;exception:{}", SUBMODULE, HttpStatus.NOT_FOUND, ResponseEntity.notFound(), ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    /*method for customer change password*/
    public void sendCustChangePasswordMessage(String username, String mobileNumber, String emailId, String status, Integer mvnoId,Long buId,String password,Long staffId) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_CHANGE_PASSWORD_TEMPLATE);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    CustChangePasswordMsg custChangePasswordMsg = new CustChangePasswordMsg(username, mobileNumber, emailId, status, mvnoId, RabbitMqConstants.CUSTOMER_CHANGE_PASSWORD_EVENT, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY,buId,password,staffId);
                    Gson gson = new Gson();
                    gson.toJson(custChangePasswordMsg);
                    kafkaMessageSender.send(new KafkaMessageData(custChangePasswordMsg,CustChangePasswordMsg.class.getSimpleName()));
//                    messageSender.send(custChangePasswordMsg, RabbitMqConstants.QUEUE_CUSTOMER_CHANGE_PASSWORD_NOTIFICATION);
                }
            } else {
                System.out.println("Message of Customer Change Password is not sent because template is not present.");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
