package com.adopt.apigw.modules.Customers;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.*;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.CustomerPayment;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.QCustomers;
import com.adopt.apigw.model.common.Shorter;
import com.adopt.apigw.model.lead.LeadMaster;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.MtnPayment.service.MtnPaymentService;
import com.adopt.apigw.modules.TumilIdValidation.IdValidationRepository;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.cafRejectReason.DTO.CafRejectDto;
import com.adopt.apigw.modules.childcustomer.dto.ChildCustPojo;
import com.adopt.apigw.modules.childcustomer.implemetation.ChildCustomerImpl;
import com.adopt.apigw.modules.planUpdate.repository.CustomerPackageRepository;
import com.adopt.apigw.modules.subscriber.controller.SubscriberController;
import com.adopt.apigw.modules.subscriber.model.CustomerListPojo;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqDTO;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqDTOList;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqModel;
import com.adopt.apigw.modules.subscriber.service.ChargeThread;
import com.adopt.apigw.modules.subscriber.service.InvoiceCreationThread;
import com.adopt.apigw.modules.subscriber.service.ReceiptThread;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.pojo.NewCustPojos.NewAddressListPojo;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.pojo.customer.plans.GetPlansByFilter;
import com.adopt.apigw.repository.LeadMasterRepository;
import com.adopt.apigw.repository.postpaid.CustPlanMappingRepository;
import com.adopt.apigw.repository.postpaid.DebitDocRepository;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CaptivePortalCustomerService;
import com.adopt.apigw.service.common.CustMilestoneDetailsService;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.ShorterService;
import com.adopt.apigw.service.postpaid.BillRunService;
import com.adopt.apigw.service.postpaid.DebitDocService;
import com.adopt.apigw.service.postpaid.PartnerService;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class CustomersController extends ApiBaseController {

    private final Logger logger = LoggerFactory.getLogger(CustomersController.class);

    private static String MODULE = " [APIController] ";
    private static final String CUSTOMER_PAYMENT = "CustomerPayment";
    private static final String OTP = "otp";

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;

    @Autowired
    CustomerPackageRepository customerPackageRepository;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    CustomersService customersService;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private CustMilestoneDetailsService custMilestoneDetailsService;

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private MtnPaymentService mtnPaymentService;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private LeadMasterRepository leadMasterRepository;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private CaptivePortalCustomerService captivePortalCustomerService;

    @Autowired
    private Tracer tracer;

    @Autowired
    private ShorterService shorterService;

    @Autowired
    private CreateDataSharedService createDataSharedService;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private DebitDocService debitDocService;
    @Autowired
    private ChildCustomerImpl childCustomerService;
    private static  final Logger LOGGER = LoggerFactory.getLogger(CustomersController.class);

    @Autowired
    private IdValidationRepository idValidationRepository;

    public String getModuleNameForLog() {

        return "[CustomersController]";

    }

    @GetMapping("/getByCustomerService")
    public ResponseEntity<?> getbyservice(@RequestParam("service") String service , PaginationRequestDTO paginationRequestDTO ,@RequestParam("customerType") String customerType,HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        ArrayList<Integer> distinctcustid =  new ArrayList<>();
        Page<CustomerShowDTO> customerShowDTOPageList = null;
        List<Integer> pages = new ArrayList<>();

        try {
            if(service.length() == 0){

            }
            if (service != null && !service.equals("")) {
                List<Integer> custids = new ArrayList<>();
                custids = customerPackageRepository.customerPackageListByService(service);
                if(custids.isEmpty()){
                    RESP_CODE = APIConstants.NULL_VALUE;
                    response.put(APIConstants.MESSAGE, "No Records Found!");
                    response.put("customers" , new ArrayList<>());
                    return apiResponse(RESP_CODE , response);

                }
                if (custids.size() != 0) {
                    HashMap<Integer, Integer> hashmap = new HashMap<Integer, Integer>();

                    //use for loop to pull the elements of array to hashmap's key
                    for (int j = 0; j < custids.toArray().length; j++) {
                        hashmap.put(custids.get(j), j);
                    }

                    System.out.println(hashmap.keySet());
                    distinctcustid = new ArrayList<Integer>(hashmap.keySet());
                    System.out.println(distinctcustid);
                }

            }
            customerShowDTOPageList = getCustomersPagination(distinctcustid , paginationRequestDTO , customerType);
            if(customerShowDTOPageList!=null) {
                Integer i = ((Integer) customerShowDTOPageList.getTotalPages());
                pages.add(i);
                pages.add((int) customerShowDTOPageList.getTotalElements());
                response.put("customers", customerShowDTOPageList);
            }
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch by Customer Service" + LogConstants.REQUEST_BY +getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);

        }catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch by Customer Service" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    public Page<CustomerShowDTO> getCustomersPagination(List<Integer> ids , PaginationRequestDTO paginationRequestDTO , String customerType){
        QCustomers qCustomer = QCustomers.customers;
        Page<Customers> customers = null;
        List<CustomerShowDTO> customerShowDTOS = new ArrayList<>();
        int count =0;
        BooleanExpression exp = qCustomer.isNotNull();
        Pageable pageable = PageRequest.of(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
        if(!ids.isEmpty()) {
            exp = exp.and(qCustomer.id.in(ids));
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getBUIdsFromCurrentStaff().size() == 0 && getMvnoIdFromCurrentStaff(null) != 1){
            // TODO: pass mvnoID manually 6/5/2025
            exp = exp.and(qCustomer.mvnoId.eq(1).or(qCustomer.mvnoId.eq(getMvnoIdFromCurrentStaff(null))));
        }
        else{
                exp = exp.and(qCustomer.buId.in(getBUIdsFromCurrentStaff()));
        }

        if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
            exp = exp.and(qCustomer.partner.id.eq(getLoggedInUserPartnerId()));
        }
        exp = exp.and(qCustomer.isDeleted.isFalse());
        exp = exp.and(qCustomer.custtype.equalsIgnoreCase(customerType));
        exp = exp.and(qCustomer.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS));


        Predicate builder = exp;
        if (paginationRequestDTO.getPage() > 0) {
            paginationRequestDTO.setPage(paginationRequestDTO.getPage() - 1);
        }
        Pageable pageable1 = PageRequest.of(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
        customers = customersRepository.findAll(builder , pageable1);
        Page<CustomerShowDTO> customerShowDTOS1 = null;
        for(Customers customers1 : customers)
        {
            customerShowDTOS.add(convertcustomertodto(customers1 , customers1.getServicearea().getName()));
            customerShowDTOS1 = new PageImpl<CustomerShowDTO>(customerShowDTOS, pageable1, customers.getTotalElements());

        }

        return customerShowDTOS1;
    }
    public CustomerShowDTO convertcustomertodto(Customers customers ,String servicearea){
        CustomerShowDTO customerShowDTO = new CustomerShowDTO();
        if(customers != null){
            int count =0;
            customerShowDTO.setAcctno(customers.getAcctno());
            customerShowDTO.setFirstname(customers.getFirstname());
            customerShowDTO.setLastname(customers.getLastname());
            customerShowDTO.setUsername(customers.getUsername());
            customerShowDTO.setId(customers.getId());
            customerShowDTO.setStatus(customers.getStatus());
            customerShowDTO.setMobile(customers.getMobile());
            customerShowDTO.setServiceareaname(servicearea);
            customerShowDTO.setServiceArea(servicearea);
            customerShowDTO.setName(customers.getFullName());
            customerShowDTO.setEmail(customers.getEmail());
            customerShowDTO.setConnectivity(true);
            customerShowDTO.setOutstanding(customers.getOutStandingAmount());
            customerShowDTO.setCusttype(customers.getCusttype());
            customerShowDTO.setCalendarType(customers.getCalendarType());
            customerShowDTO.setConnectionMode(customers.getConnectionMode());
            CustNetworkDetailsDTO custNetworkDetailsDTO = new CustNetworkDetailsDTO();
            custNetworkDetailsDTO.setServiceareaname(servicearea);
            customerShowDTO.setNetworkDetails(custNetworkDetailsDTO);
            customerShowDTO.setIsinvoicestop(customers.getIsinvoicestop());
        }
         return customerShowDTO;

    }

    @GetMapping("/customer/checkPlanCustomerBinding/{id}")
    public ResponseEntity<?> customerUsernameIsAlreadyExists(@PathVariable Integer id) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomersService customerService = SpringContext.getBean(CustomersService.class);
            response.put("isAlreadyExists", customerService.customerPlanBindingAlreadyExist(id));
            RESP_CODE = APIConstants.SUCCESS;
           // logger.info("fetching   customerUsernameIsAlreadyExists "+username+":  :  request: { From : {}}; Response : {{}}", MODULE,response,RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
           // logger.error("Unable to fetch customerUsernameIsAlreadyExists "+username+":  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",MODULE,RESP_CODE,response,ce.getStackTrace());
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
          //  logger.error("Unable to fetch customerUsernameIsAlreadyExists "+username+": :  request: { From : {},}; Response : {{}};Error :{} ;Exception:{}", MODULE,RESP_CODE,response,ex.getStackTrace());
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);

    }
//    public int getLoggedInUserPartnerId() {
//        int partnerId = -1;
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
//            }
//        } catch (Exception e) {
//            partnerId = -1;
//        }
//        return partnerId;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_POSTPAID_PLAN_ALL + "\",\""
//            + AclConstants.OPERATION_POSTPAID_PLAN_VIEW + "\")")
    @GetMapping("/plansByTypeServiceModeStatusAndMultipleServiceArea")
    public ResponseEntity<?> getPlanByTypeServiceModeStatusAndMultipleServiceArea(@RequestParam(name = "type") String type,@RequestParam("mvnoId") Integer mvnoId,
                                                                                  @RequestParam(name = "serviceId") Integer serviceId, @RequestParam(name = "serviceAreaId") List<Integer> serviceAreaId,
                                                                                  @RequestParam(name = "mode") String mode, @RequestParam(name = "status") String status, @RequestParam(required = false) Integer custId
            , @RequestParam(defaultValue = "ALL", required = false) String planGroup
            , @RequestParam(name = "planCategory", required = false) String planCategory, @RequestParam(name = "validity", required = false) Integer validity, @RequestParam(name = "unitsOfValidity", required = false) String unitsOfValidity, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (null == serviceId) {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, "Please Provide Service!");
                RESP_CODE = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"plans  By Type ServiceModeStatusAndMultipleServiceArea" + LogConstants.REQUEST_BY +getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);

                return apiResponse(RESP_CODE, response);
            }
            PostpaidPlanService postpaidPlanService = SpringContext.getBean(PostpaidPlanService.class);


            List<PostpaidPlanPojo> postpaidPlanList = postpaidPlanService.findPlanByTypeServiceModeStatusAndServiceArea(type, serviceId, serviceAreaId, mode, status, planGroup, planCategory, validity, unitsOfValidity, custId,mvnoId);
            response.put("postPaidPlan", postpaidPlanList);
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"plans  By Type ServiceModeStatusAndMultipleServiceArea" + LogConstants.REQUEST_BY +getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);

        } catch (Exception ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"plans  By Type ServiceModeStatusAndMultipleServiceArea"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }
//    public int getLoggedInUserPartnerId() {
//        int partnerId = -1;
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
//            }
//        } catch (Exception e) {
//            partnerId = -1;
//        }
//        return partnerId;
//    }
@GetMapping("/getCustomerByServiceId/{id}")
public GenericDataDTO getCustomerByServiceId(Long serviceId) throws Exception {
    GenericDataDTO genericDataDTO = new GenericDataDTO();
    try {
        genericDataDTO.setDataList(customersService.getCustomerByServiceId(serviceId));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
    } catch (Exception e) {
        genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        genericDataDTO.setResponseMessage("Failed to load data");
    }
    return genericDataDTO;
}


    @PostMapping("/caf/close")
    @ApiOperation(value = "Close Caf")
    public ResponseEntity<?> closeCaf(@RequestBody CafRejectDto cafRejectDto,
                                                         HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        Integer responseCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Delete");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            this.customersService.rejectCaf(cafRejectDto);
            response.put(APIConstants.MESSAGE, "Caf has been closed successfully");
            responseCode = APIConstants.SUCCESS;
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"close caf" + LogConstants.REQUEST_BY +getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"close cafe"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+responseCode);
        } catch (Exception e) {
            responseCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"close caf"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(responseCode, response);
    }

    @GetMapping(value = "/getRenewPlanByCustomer/{customerId}")
    public GenericDataDTO getExpiredPlanList(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getTotalPlanList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE =HttpStatus.NOT_ACCEPTABLE.value();
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch All Expired PlanList"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE+RESP_CODE);
                return genericDataDTO;
            }
            Customers customers =  customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch All Expired PlanList"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE+RESP_CODE);
                return genericDataDTO;
            }
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch All Expired PlanList" + LogConstants.REQUEST_BY +getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);

            if(GenericDataDTO.getGenericDataDTO(subscriberService.getTotalPlanList(customerId)).getDataList().isEmpty())
            {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                genericDataDTO.setResponseMessage("No Record Found!");
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);

            }
            else {
                return GenericDataDTO.getGenericDataDTO(subscriberService.getTotalPlanList(customerId));
            }
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All Expired PlanList"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }

        return genericDataDTO;
    }


    @PostMapping(value = "/getAllPlansByServiceArea")
    public ResponseEntity<?> GetAllPlansByServiceArea(@RequestBody ServiceAreaFetchDTO serviceAreaFetchDTO,
                                      HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        Integer responseCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            List<LightPostpaidPlanDTO> postpaidPlanList = customersService.getAllPostpaidPlanByServiceArea(serviceAreaFetchDTO.getSa() , serviceAreaFetchDTO.getPlanGroupType(),serviceAreaFetchDTO.getCurrentPlanId() , serviceAreaFetchDTO.getIsQosUpgrade() , serviceAreaFetchDTO.getIsQosDowngrade(),serviceAreaFetchDTO.getLocationIds());
            if(!postpaidPlanList.isEmpty()) {
                response.put("planList", postpaidPlanList);
                response.put(APIConstants.MESSAGE, "plan fetch successfully");
                responseCode = APIConstants.SUCCESS;
                RESP_CODE = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch All Plan By Service Area" + LogConstants.REQUEST_BY +getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            }
            if(postpaidPlanList.isEmpty()) {
                response.put(APIConstants.MESSAGE, "No record found");
                response.put("planList", postpaidPlanList);
                responseCode = APIConstants.NOT_FOUND;
                LOGGER.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch All Plan By Service Area" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE+responseCode);

            }
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All Plan By Service Area" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+responseCode);
        } catch (Exception e) {
            responseCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All Plan By Service Area" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(responseCode, response);
    }

    @PostMapping(value = "/getAllPlansByServiceAreaAndType")
    public ResponseEntity<?> GetAllPlansByServiceAreaAndType(@RequestBody ServiceAreaFetchDTO serviceAreaFetchDTO,
                                                      HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        Integer responseCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            List<LightPostpaidPlanDTO> postpaidPlanList = customersService.getAllPostpaidPlanByServiceAreaAndType(serviceAreaFetchDTO.getSa(), serviceAreaFetchDTO.getPlanGroupTypes(), serviceAreaFetchDTO.getServiceIds());
            if(!postpaidPlanList.isEmpty()) {
                response.put("planList", postpaidPlanList);
                response.put(APIConstants.MESSAGE, "plan fetch successfully");
                responseCode = APIConstants.SUCCESS;
                RESP_CODE = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch All Plan By Service Area" + LogConstants.REQUEST_BY +getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            }
            if(postpaidPlanList.isEmpty()) {
                response.put(APIConstants.MESSAGE, "No record found");
                response.put("planList", postpaidPlanList);
                responseCode = APIConstants.NOT_FOUND;
                LOGGER.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch All Plan By Service Area" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE+responseCode);

            }
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All Plan By Service Area" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+responseCode);
        } catch (Exception e) {
            responseCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All Plan By Service Area" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(responseCode, response);
    }

    @GetMapping("/getPartnerByServiceAreaIds/{serviceAreaId}")
    public ResponseEntity<?> getPartnersByServiceAreaId(@Valid @PathVariable List<Integer> serviceAreaId, HttpServletRequest req,@RequestParam(value ="mvnoId")Integer mvnoId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
            response.put("partnerList", partnerService.convertResponseModelIntoPojo(partnerService.getPartnersByServiceAreaId(serviceAreaId,mvnoId)));
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch Partner By Service Area" + LogConstants.REQUEST_BY +getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch Partner By Service Area" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch Partner By Service Area" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/customer/changedunningenabalestatus")
    @ApiOperation(value = "This api for change dunning status")
    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_DUNNUNG_STATUS + "\",\""
            + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_DUNNUNG_STATUS + "\")")
    public ResponseEntity<?> changedunningenabalestatus(@RequestParam Integer custId, @RequestParam Boolean dunningStatus,
                                      HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        Integer responseCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            this.customersService.changeCustomerDunningStatus(custId , dunningStatus);
            response.put(APIConstants.MESSAGE, "Dunning status change Successfully");
            responseCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"change Dunning status" + LogConstants.REQUEST_BY +getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+responseCode);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"change Dunning status"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (Exception e) {
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"change Dunning status"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(responseCode, response);
    }

    @GetMapping("/customer/changenotificationenabalestatus")
    @ApiOperation(value = "This api for change dunning status")
    public ResponseEntity<?> changenotificationenablestatus(@RequestParam Integer custId, @RequestParam Boolean notificationStatus,
                                                        HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        Integer responseCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            this.customersService.changeCustomerNotificationStatus(custId , notificationStatus);
            response.put(APIConstants.MESSAGE, "Notification status change Successfully");
            responseCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"change notification status" + LogConstants.REQUEST_BY +getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+responseCode);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"change notification status"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (Exception e) {
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"change notification status"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(responseCode, response);
    }

    @PostMapping(value="/getPlansByFilters")
    public List<PostpaidPlan> getPlansByFilters(@RequestBody GetPlansByFilter requestDto,@RequestParam (name = "parentPlanId", required = false)Integer parentPlanId,@RequestParam (name = "mvnoId", required = false)Integer mvnoId, HttpServletRequest req){
        try {
            List<PostpaidPlan> postpaidPlanList = postpaidPlanService.getPlansByFilters(requestDto,parentPlanId,mvnoId);
            return postpaidPlanList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value="/getPlanGroupByFilters")
    public List<PlanGroup> getPlanGroupByFilters(@RequestBody GetPlansByFilter requestDto, HttpServletRequest req){
        try {
            List<PlanGroup> planGroupList = postpaidPlanService.getPlanGroupByFilters(requestDto);
            return planGroupList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/customer/checkPlanCustomerBindingIsFreePlanAddon/{id}")
    public ResponseEntity<?> checkPlanCustomerBindingIsFreePlanAddon(@PathVariable Integer id,HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomersService customerService = SpringContext.getBean(CustomersService.class);
            response.put("isFreePlanAddonIsBind", customerService.checkPlanCustomerBindingIsFreePlanAddon(id));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            // logger.error("Unable to fetch customerUsernameIsAlreadyExists "+username+":  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",MODULE,RESP_CODE,response,ce.getStackTrace());
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            //  logger.error("Unable to fetch customerUsernameIsAlreadyExists "+username+": :  request: { From : {},}; Response : {{}};Error :{} ;Exception:{}", MODULE,RESP_CODE,response,ex.getStackTrace());
        }
        return apiResponse(RESP_CODE, response);


    }

    @ApiOperation(value = "Get list of  all payment history")
    @PostMapping("/findAllPaymentByCustomer")
    public ResponseEntity<?> findAllPaymentByCustomerId(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req) {
        HashMap<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            Page<CustomerPayment> getAllCustomerPaymentHistory = mtnPaymentService.findAllPaymentHistoryByCustomer(requestDTO);
            Integer responseCode = APIConstants.SUCCESS;
            response.put("customerpaymenthistory", getAllCustomerPaymentHistory);
             return apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
//            logger.error("Error while fetch PaymentHistory: " + e.getMessage());
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponse(responseCode, response);
        }
    }

    @ApiOperation(value = "This API will be update customer Secondary mobile no")
    @PostMapping("/customers/updateCustomerMobileNo")
    public ResponseEntity<?> updateCustomerMobileNo(@RequestBody UpdateCustDTO updateCustDTO,HttpServletRequest req)  throws Exception{
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            customersService.updateMobileNo(updateCustDTO.getCustId(),updateCustDTO.getAltmobile());
            Integer responseCode = APIConstants.SUCCESS;
            response.put("msg" , "customer update successfully");
            LOGGER.info(LogConstants.REQUEST_FROM +req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update customer Secondary mobile no"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+responseCode);

            return apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"update customer Secondary mobile no"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponse(responseCode, response);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @PostMapping("/addNewServiceForEmail")
    public ResponseEntity<?> addNewServiceForEmail(@Valid @RequestBody CustomersPojo pojo,
                                                   @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req, String serviceFor)
            throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            if ((getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID && null != pojo.getPartnerid()
                    && !pojo.getPartnerid().equals(CommonConstants.DEFAULT_PARTNER_ID))) {
                if (requestFrom.equals("pw") && !customersService.getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficient(pojo)) {
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    response.put(APIConstants.ERROR_TAG, "Partner has Insufficient balance!");
                    LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Create Invoice"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_INFO +"Partner has Insufficient balance!" +LogConstants.LOG_STATUS_CODE+RESP_CODE);
                    return apiResponse(RESP_CODE, response, null);
                } else if (requestFrom.equals("pw") && customersService.getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficientForLCO(pojo)) {
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    response.put(APIConstants.ERROR_TAG, "Partner has Insufficient balance!");
                    LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Create Invoice"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_INFO +"Partner has Insufficient balance!" +LogConstants.LOG_STATUS_CODE+RESP_CODE);
                    return apiResponse(RESP_CODE, response, null);
                }
            }
            RecordPaymentPojo recordPaymentPojo = pojo.getPaymentDetails();
            CustomersService customersService = SpringContext.getBean(CustomersService.class);
            BillRunService billRunService = SpringContext.getBean(BillRunService.class);
//			customersService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
            pojo = customersService.newService(pojo, requestFrom, false, serviceFor,req.getHeader("Authorization"));

            //Save customer detail with time base policy
            customersService.CustTimeBasePolicyDetailsSend(pojo);
            Customers customer = customerMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext());
            Customers customers1 = customersService.getById(customer.getId());
            customers1.setNextBillDate(customer.getNextBillDate());
            customers1.setLastBillDate(customer.getLastBillDate());
            LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers1, customers1.getNextBillDate());
            if(nextQuotaReset != null) {
                customers1.setNextQuotaResetDate(nextQuotaReset);
            } else {
                customers1.setNextQuotaResetDate(LocalDate.now());
            }

            if (pojo.getLeadId() != null) {
                LeadMaster leadMaster = leadMasterRepository.findById(pojo.getLeadId()).orElse(null);
                customers1.setLeadSource(leadMaster.getLeadSource().getLeadSourceName());
            }
            customersRepository.save(customers1);

            try {
                if ((pojo.getCusttype() != null & !"".equals(pojo.getCusttype())
                        && pojo.getCusttype().equalsIgnoreCase("Prepaid"))
                        && (recordPaymentPojo == null
                        || (recordPaymentPojo != null && recordPaymentPojo.getAmount() <= 0))) {
                    RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                    LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Generate Invoice"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + "Invoice can not generate due to 0 payment amount"+LogConstants.LOG_STATUS_CODE+RESP_CODE);

                } else {
                    // Generate Invoice
                    if (null != pojo.getPlanMappingList() && 0 < pojo.getPlanMappingList().size()) {
                        Integer custPackRel = pojo.getPlanMappingList().get(0).getId();
                        customer.setBillRunCustPackageRelId(custPackRel);
                        Runnable invoiceRunnable = new InvoiceCreationThread(pojo, customersService, null, false, null, CommonConstants.INVOICE_TYPE.NEW_SERVICE);
                        Thread invoiceThread = new Thread(invoiceRunnable);
                        invoiceThread.start();
                    }

                    // Generate Receipt
                    Customers customers = customersService.savePaymentXMLDocument(
                            customerMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext()));
                    if (null != customers) {
                        Runnable receiptRunnable = new ReceiptThread(billRunService, customers.getCreditDocuments());
                        Thread receiptThread = new Thread(receiptRunnable);
                        receiptThread.start();
                    }
                    customers = customerMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext());

                    // Generate Charge Invoice
                    if (null != customers && null != customers.getOverChargeList()
                            && 0 < customers.getOverChargeList().size()) {
                        List<Integer> custChargeIdList = new ArrayList<>();
                        customers.getOverChargeList().forEach(data -> custChargeIdList.add(data.getId()));
                        Runnable chargeRunnable = new ChargeThread(customers.getId(), custChargeIdList,
                                customersService, 0L, "", null);
                        Thread billchargeThread = new Thread(chargeRunnable);
                        billchargeThread.start();
                    }
                }

                /** @Author dhaval khalasi automatic approve Service**/
                if(!customers1.getCustomerServiceMappingList().isEmpty()){
                    List<CustomerServiceMapping> customerServiceMapping =  customerServiceMappingRepository.findByCustId(customers1.getId());
                    if(!customerServiceMapping.isEmpty()) {
                        String token = req.getHeader("Authorization");
                        customersService.approveCustomerServiceAdd(customerServiceMapping.get(customerServiceMapping.size()-1).getId(), true, "automatic approve from email",req.getHeader("Authorization"));
                    }
                }
                /**@Author dhaval khalasi code ended**/
            } catch (Exception e) {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"add New Service For Email"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
                e.printStackTrace();
            }

            try {
                boolean invoice = false;
                List<CustPlanMapppingPojo> custPlanList = pojo.getPlanMappingList();
                for (int i = 0; i < custPlanList.size(); i++) {
                    CustPlanMapppingPojo custPlan = custPlanList.get(i);

                    if (custPlan.getOfferPrice() > 0) {
                        invoice = true;
                    }
                }

                if (invoice) {
                    if (customers1.getParentCustomers() != null) {
                        List<CustPlanMappping> mappings = custPlanMappingRepository.findAllByCustomerId(customers1.getId());
                        if (mappings != null && !mappings.isEmpty()) {
                            mappings = mappings.stream().filter(x -> !x.getIsInvoiceCreated()).collect(Collectors.toList());
                        }

                        if (mappings != null && !mappings.isEmpty()) {
                            Boolean isGroup = mappings.stream().filter(x -> x.getInvoiceType().equals(CommonConstants.INVOICE_TYPE_GROUP)).collect(Collectors.toList()).size() > 0;
                            if (isGroup) {
                                Runnable chargeRunnable1 = new InvoiceCreationThread(pojo, customersService, CommonConstants.INVOICE_TYPE_GROUP, false, null, CommonConstants.INVOICE_TYPE.NEW_SERVICE);
                                Thread billchargeThread1 = new Thread(chargeRunnable1);
                                billchargeThread1.start();
                            }

                            Boolean isIndependent = mappings.stream().filter(x -> x.getInvoiceType().equals(CommonConstants.INVOICE_TYPE_INDEPENDENT)).collect(Collectors.toList()).size() > 0;
                            if (isIndependent) {
                                Thread.sleep(2000);
                                Runnable chargeRunnable1 = new InvoiceCreationThread(pojo, customersService, CommonConstants.INVOICE_TYPE_INDEPENDENT, false, null, CommonConstants.INVOICE_TYPE.NEW_SERVICE);
                                Thread billchargeThread1 = new Thread(chargeRunnable1);
                                billchargeThread1.start();
                            }
                        }
                    } else {
//                        Runnable chargeRunnable1 = new InvoiceCreationThread(pojo, customersService, null, false);
//                        Thread billchargeThread1 = new Thread(chargeRunnable1);
//                        billchargeThread1.start();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"add New Service For Email"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
                e.printStackTrace();
            }

            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_ADD,
                    req.getRemoteAddr(), null, pojo.getId().longValue(), "");
            response.put("customer", pojo);
            RESP_CODE=APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM +req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "add New Service For Email"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"add New Service For Email"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);

            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"add New Service For Email"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_TAG, exception.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"add New Service For Email"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }


    @GetMapping("/getCustomerAwifis")
    @ApiOperation(value = "Get list of customers based on the given user name")
    // @PreAuthorize("@roleAccesses.hasPermission('wifiCustomer','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<?> getCustomer(@RequestParam("userName") String userName,
                                         @RequestParam(name = "mvnoId", required = true) Long mvnoId, @RequestParam(name = "password", required = false) String password,
                                         @RequestParam(name = "mobileNo", required = false) String mobileNo, @RequestParam(name = "cid", required = false) String cid,
                                         @RequestParam(name = "mac", required = false) String mac, HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        ResponseEntity<?> responseEntity = null;
//        MDC.put(WifiConstants.TYPE, WifiConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            List<CustomersPojo> customerList = customersService.getCustomer(userName, mvnoId, password, mobileNo, cid, mac);
            //log.debug("Request For Fetch Customer by name: "+userName);
            Integer responseCode = 0;
            if (customerList.isEmpty()) {
                responseCode = HttpStatus.NO_CONTENT.value();
                response.put("Error", "No Records Found!");
                responseEntity = apiResponse(HttpStatus.OK.value(), response);
                RESP_CODE =HttpStatus.NO_CONTENT.value();
                LOGGER.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"fetch list of customers"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE+RESP_CODE);
//                snmpCounters.incrementGetCustomerByCidMacFailure();
            } else {
                responseCode = HttpStatus.OK.value();
                response.put("CUSTOMER_LIST", customerList);
                responseEntity = apiResponse(HttpStatus.OK.value(), response);
                RESP_CODE=APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch list of customers"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);
                //                snmpCounters.incrementGetCustomerByCidMacSuccess();
            }
            responseEntity = apiResponse(HttpStatus.OK.value(), response);
            return apiResponse(responseCode, response);
        } catch (Exception e) {
            responseEntity = apiResponse(HttpStatus.OK.value(), response);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch list of customers"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+responseEntity);

//            apiResponseController.buildErrorMessageForResponse(response, e);
//            snmpCounters.incrementGetCustomerByCidMacFailure();
            return apiResponse(HttpStatus.EXPECTATION_FAILED.value(), response);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }


    @ApiOperation(value = "This API will be get or create org cust")
    @PostMapping("/customers/checkCaptiveCust")
    public ResponseEntity<?> getOrCreateOrgCust(@RequestBody FindCustomerDTO findCustomerDTO,HttpServletRequest req)  throws Exception{
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
          CustomerListPojo customersPojo  =   captivePortalCustomerService.getCustomerByUsernameAndMac(findCustomerDTO.getUsername() , findCustomerDTO.getMac(), findCustomerDTO.getCountryId());
          if(customersPojo != null) {
              Boolean isOrgCust = captivePortalCustomerService.IsCustOrgCust(customersPojo.getId());
              Boolean isIndipendentCust = captivePortalCustomerService.IsIndividualCust(findCustomerDTO.getUsername());
              Boolean isQuotaAvailable = false;
              if(isIndipendentCust){
                  isQuotaAvailable = captivePortalCustomerService.IsQuotaAvailableForIndividualCust(findCustomerDTO.getUsername());
              }
              response.put("msg", "customer find successfully");
              response.put("customerList", customersPojo);
              response.put("isParentData", isOrgCust);
              response.put("isIndividual" , isIndipendentCust);
              response.put("isQuotaAvailable" , isQuotaAvailable);
              RESP_CODE=APIConstants.SUCCESS;
              LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch or create org cust"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);
              return apiResponse(RESP_CODE, response);
          }
          else{
              Boolean isOrgCust = false;
              Boolean isQuotaAvailable = false;
              Boolean isIndipendentCust = captivePortalCustomerService.IsIndividualCust(findCustomerDTO.getUsername());
              Integer responseCode = APIConstants.NOT_FOUND;
              response.put("msg", "customer not found");
              response.put("customerList", new ArrayList<>());
              response.put("isParentData", isOrgCust);
              response.put("isIndividual" , isIndipendentCust);
              response.put("isQuotaAvailable" , false);
              RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
              LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"fetch or create org cust"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE+RESP_CODE);

              return apiResponse(responseCode, response);
          }
        }

        catch (CustomValidationException ce) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch or create org cust"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+responseCode);
            response.put(APIConstants.ERROR_MESSAGE, ce.getMessage());
            return apiResponse(responseCode , response);
        }
        catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch or create org cust"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+responseCode);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponse(responseCode, response);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

    }


    @ApiOperation(value = "This API will find if parent customer has e quota or not (This is a client specific api)")
    @PostMapping("/customers/checkParentQuota")
    public ResponseEntity<?> checkParentQuota(@RequestBody FindCustomerDTO findCustomerDTO,HttpServletRequest req)  throws Exception{
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
                Boolean flag = captivePortalCustomerService.IsQuotaAvalibleOnMac(findCustomerDTO.getMac());
                Integer responseCode = APIConstants.SUCCESS;
                response.put("isQuotaAvailableOnMac" , flag);
                if(flag) {
                    response.put("msg", "Quota is available on mac");
                }
                else{
                    response.put("msg" , "Quota is not available on mac");
                }
            RESP_CODE=APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Customer Quota"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);

            return apiResponse(responseCode, response);
        }

        catch (CustomValidationException ce) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch All Customer Quota"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+responseCode);
            response.put(APIConstants.ERROR_MESSAGE, ce.getMessage());
            return apiResponse(responseCode , response);
        }
        catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch All Customer Quota"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+responseCode);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponse(responseCode, response);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

    }

    @ApiOperation(value = "This API will fetch captive cust of find any else return empty (This is client specific api)")
    @PostMapping("/customers/getCaptiveCust")
    public ResponseEntity<?> getCaptiveCustUsingMac(@RequestBody FindCustomerDTO findCustomerDTO,HttpServletRequest req)  throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            CaptiveCustomerResponseDTO captiveCustomerResponseDTO = captivePortalCustomerService.getCaptiveCustResponse(findCustomerDTO.getMac());
            Integer responseCode = APIConstants.SUCCESS;
            response.put("captiveCust", captiveCustomerResponseDTO);
            response.put("msg", "Captive Cust find successfully");
            RESP_CODE=APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All captive cust"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            return apiResponse(responseCode, response);
        } catch (CustomValidationException ce) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch All captive cust"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, ce.getMessage());
            return apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch All captive cust"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponse(responseCode, response);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }


    }

    @PostMapping("/customers/addCustomerFromMvno")
    public ResponseEntity<?> saveCustomerWithLimitedData(@RequestBody CustomersPojo customersPojo, @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom,HttpServletRequest req)  throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            CustomersPojo pojo = captivePortalCustomerService.saveCustomerWithLimitedData(customersPojo, requestFrom);
            pojo.setCustomerCreated(true);
            ChildCustPojo childCustPojo = new ChildCustPojo(pojo);
            CustomerPaymentDto customerPayment = customersService.sharedCustomerDataForm(pojo,true,customersPojo.getCustomerPaymentDto());

            childCustomerService.create(childCustPojo,req);
            pojo.setCustomerPaymentDto(customerPayment);
            Integer responseCode = APIConstants.SUCCESS;
            response.put("customer", pojo);
            response.put("msg", "Customer save successfully..!");
            RESP_CODE=APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Save Customer from CWSC"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            return apiResponse(responseCode, response);
        } catch (Exception e) {
            e.printStackTrace();
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Save Customer from CWSC"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponse(responseCode, response);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }


    }

    @ApiOperation(value = "This API will fetch customer with only required data")
    @GetMapping("/customers/getCustomerById")
    public ResponseEntity<?> getCustomerById(@RequestParam(name = "custId") Integer custId,HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            LightCustomerDTO lightCustomerDTO = customersService.getByCustomerId(custId);
            Integer responseCode = APIConstants.SUCCESS;
            response.put("customers", lightCustomerDTO);
            response.put("msg", "Customer find successfully");
            RESP_CODE=APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all Customer"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);

            return apiResponse(responseCode, response);
        } catch (CustomValidationException ce) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all Customer"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, ce.getMessage());
            return apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all Customere"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponse(responseCode, response);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }


    }

    @ApiOperation(value = "This API will fetch account Number By customer Id")
    @GetMapping("/customers/getAccountNoByCustId")
    public ResponseEntity<?> getAccountNoByCustId(@RequestParam(name = "custId") Integer custId,HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        try {
            String accountNumber = null;
            Integer mvnoId = 0;
            Object[] result = customersService.getAccountNoByCustId(custId);
            if (result == null || result.length == 0) {
                throw new CustomValidationException(417, "Customer not Found: " + custId, null);
            }
            if (result != null && result.length > 0 && result[0] instanceof Object[]) {
                Object[] innerArray = (Object[]) result[0];
                if (innerArray.length >= 2) {
                    accountNumber = innerArray[0] != null ? ((String) innerArray[0]): null;
                    mvnoId = innerArray[1] != null ? ((Integer) innerArray[1]) : 0;
                }
            }
            Integer responseCode = APIConstants.SUCCESS;
            response.put("accountNumber", accountNumber);
            response.put("mvnoId", mvnoId);
            RESP_CODE=APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all Customer"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);

            return apiResponse(responseCode, response);
        } catch (CustomValidationException ce) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all Customer"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, ce.getMessage());
            return apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all Customere"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponse(responseCode, response);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
        }
    }

    @ApiOperation(value = "This API will fetch customer with only required data")
    @GetMapping("/customers/getCustomerByMobile")
    public ResponseEntity<?> getCustomerByMobile(@RequestParam(name = "mobileNo" , required = false) String mobileNo,@RequestParam(name = "email" , required = false) String email,HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put("spanId",traceContext.spanIdString());
        try {
            List<LightCustomerPlanMappingDTO>customerPlanMappingDTOList = customersService.getCustomerListByMobileNumberOrEmail(mobileNo,email);
            response.put("customers", customerPlanMappingDTOList);
            Integer responseCode = APIConstants.INTERNAL_SERVER_ERROR;
            if(customerPlanMappingDTOList.isEmpty()){
                responseCode = APIConstants.NO_CONTENT_FOUND;
                response.put("msg", "No Customer found by given mobile number");
                RESP_CODE=APIConstants.NO_CONTENT_FOUND;
            }
            else{
                responseCode = APIConstants.SUCCESS;
                response.put("msg", "Customer find successfully");
                RESP_CODE=APIConstants.SUCCESS;
            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all Customer"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);

            return apiResponse(responseCode, response);
        } catch (CustomValidationException ce) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all Customer"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, ce.getMessage());
            return apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all Customere"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponse(responseCode, response);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }


    }
    @ApiOperation(value = "This API will fetch customer with only required data")
    @GetMapping("/customer/planListByAccountNo")
    public ResponseEntity<?> getCustomerPlanListByAccountNo(@RequestParam(name = "accountNo") String accountNo,HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put("spanId",traceContext.spanIdString());
        try {
            Map<String, Object> map= postpaidPlanService.getCustomerPlanListByAccountNo(accountNo, getLoggedInUser().getMvnoId());
            List<LightPostpaidPlanDTO> postpaidPlans = customersService.getAllPostpaidPlanByServiceAreaByPlanGroupType(
                    null,
                    Arrays.asList(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL, SubscriberConstants.PLAN_PURCHASE_RENEW, SubscriberConstants.PLAN_PURCHASE_NEW),
                    (Integer) map.get("id"),
                    true,
                    false,
                    Collections.emptyList()
            );
            // TODO: pass mvnoID manually 6/5/2025
            List<Map<String, Object>> filteredList = postpaidPlans.stream()
                    .filter(plan -> plan.getMvnoId() != null && plan.getMvnoId().equals(getMvnoIdFromCurrentStaff(null)))
                    .map(plan -> {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("id", plan.getId());
                        resultMap.put("Name", plan.getName());
                        resultMap.put("Price", plan.getOfferprice());
                        return resultMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(filteredList);
        } catch (CustomValidationException ce) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "fetch all Customer" +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + ce.getMessage() +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR, ce.getMessage());
            return ResponseEntity.status(ce.getErrCode()).body(response);
        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "fetch all Customer" +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + e.getMessage() +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }


    @ApiOperation(value = "This API will upgrade the plan by name")
    @PostMapping("/customer/upgradePlanByAccountNoAndPlanName")
    public ResponseEntity<?> upgradePlanByAccountNoAndPlanName(@RequestParam(name = "accountNo") String accountNo, @RequestParam(name = "packageName") String packageName, HttpServletRequest req) throws Exception {
        MDC.put("type", "Create");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Thread invoiceThread = null;
        String requestFrom = "bss";
        try {
            DeactivatePlanReqDTO requestDTOs = new DeactivatePlanReqDTO();
            requestDTOs = subscriberService.upgradePlanByAccountNoAndPlanName(accountNo, packageName);
            Customers customers =  customersRepository.findById(requestDTOs.getCustId()).get();
            Double planPrice = 0.0;
            LocalDate planExpiryDate = null;

            if(customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVE)) {
                Integer custId = null;
                DeactivatePlanReqDTOList deactivatePlanReqDTOList = new DeactivatePlanReqDTOList();
                requestDTOs.setIsParent(true);
                deactivatePlanReqDTOList.setDeactivatePlanReqDTOS(Arrays.asList(requestDTOs));
                deactivatePlanReqDTOList.setSkipQuotaUpdate(false);
                Optional<Integer> custIdOptional = deactivatePlanReqDTOList.getDeactivatePlanReqDTOS().stream().filter(DeactivatePlanReqDTO::getIsParent).map(DeactivatePlanReqDTO::getCustId).findFirst();
                List<Integer> custIds = deactivatePlanReqDTOList.getDeactivatePlanReqDTOS().stream().filter(i -> !i.getIsParent()).map(DeactivatePlanReqDTO::getCustId).collect(Collectors.toList());
                Set<Customers> customersforInvoice = new HashSet<>();
                if (custIds != null && custIds.size() > 0) {
                    customersforInvoice.addAll(customersRepository.findAllById(custIds));
                }
                if (custIdOptional.isPresent()) {
                    custId = custIdOptional.get();
                } else {
                    custIdOptional = deactivatePlanReqDTOList.getDeactivatePlanReqDTOS().stream().map(DeactivatePlanReqDTO::getCustId).findFirst();
                    if (custIdOptional.isPresent()) {
                        custId = custIdOptional.get();
                    }
                }
                Customers customersPojo =  customersRepository.findById(custId).get();
                Integer currentMvnoId = subscriberService.getLoggedInMvnoId(custId);
                Integer dataMvnoId = customersPojo.getMvnoId();

                if (currentMvnoId == 1 || dataMvnoId.equals(currentMvnoId)) {

                    if (custId == null) {
                        throw new CustomValidationException(417, "Customer id can not be null!", null);
                    }

                    if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                        if (deactivatePlanReqDTOList.equals("pw") && !getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficient(deactivatePlanReqDTOList.getDeactivatePlanReqDTOS().get(0))) {
                            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                            response.put(APIConstants.ERROR_TAG, "Partner has Insufficient balance!");
                            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivate Plan In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                            return apiResponse(RESP_CODE, response, null);
                        }

                        if (deactivatePlanReqDTOList.equals("pw") && getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficientForLCO(deactivatePlanReqDTOList.getDeactivatePlanReqDTOS().get(0))) {
                            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                            response.put(APIConstants.ERROR_TAG, "Partner has Insufficient balance!");
                            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivate Plan In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                            return apiResponse(RESP_CODE, response, null);
                        }
                    }
                    //  Customers customers = customersService.get(requestDTOs.getCustId());
                    //            Customers customers = customersService.get(custId);
                    customersforInvoice.add(customersPojo);
                    DebitDocument debitDocuments = debitDocRepository.findTopByCustomerAndBillrunstatus(customersPojo.getId(), "VOID");

                    if (deactivatePlanReqDTOList.getDeactivatePlanReqDTOS().get(0).getChangePlanDate() != null && (debitDocuments != null || !deactivatePlanReqDTOList.getDeactivatePlanReqDTOS().get(0).getChangePlanDate().equalsIgnoreCase("Next Bill Date")) && customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {
                        deactivatePlanReqDTOList.getDeactivatePlanReqDTOS().get(0).setChangePlanDate("Today");
                    }
                    DeactivatePlanReqDTOList result = new DeactivatePlanReqDTOList();
                    if (customersPojo.getStatus().equalsIgnoreCase(SubscriberConstants.ACTIVE)) {
                        result = subscriberService.deActivatePlanInList(deactivatePlanReqDTOList);
                    }

                    List<DeactivatePlanReqDTO> list = result.getDeactivatePlanReqDTOS();

                    List<List<Integer>> lists = new ArrayList<>();
                    for (DeactivatePlanReqDTO model : list) {
                        List<DeactivatePlanReqModel> deactivatePlanReqModels = model.getDeactivatePlanReqModels();
                        List<List<Integer>> list1 = deactivatePlanReqModels.stream().map(DeactivatePlanReqModel::getDebitDocIds).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(list1))
                            lists.addAll(list1);
                    }
                    List<Integer> debitDocIds = lists.stream().filter(Objects::nonNull).flatMap(List::stream).collect(Collectors.toList());

                    String paymentOwner = deactivatePlanReqDTOList.getDeactivatePlanReqDTOS().stream().map(DeactivatePlanReqDTO::getPaymentOwner).findFirst().orElse(null);
                    Integer paymentOwnerId = deactivatePlanReqDTOList.getDeactivatePlanReqDTOS().stream().map(DeactivatePlanReqDTO::getPaymentOwnerId).findFirst().orElse(null);
                    //            debitDocService.createInvoice(customers, null, 200, new HashSet<Integer>(debitDocIds), null, paymentOwner, paymentOwnerId, false, CommonConstants.INVOICE_TYPE.CHANGE_PLAN);

                    /*  this is for post paid changePLan next bill date */
                    boolean changePlanNextBillDate = false;
                    if (deactivatePlanReqDTOList.getDeactivatePlanReqDTOS().get(0).getChangePlanDate() != null && deactivatePlanReqDTOList.getDeactivatePlanReqDTOS().get(0).getChangePlanDate().equalsIgnoreCase("Next Bill Date") && customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {
                        changePlanNextBillDate = true;
                    }
                    if (customersforInvoice.size() > 1) {
                        Integer parentId = custIdOptional.get();
                        List<Integer> childIds = custIds;
                        childIds.removeIf(i -> i.equals(parentId));
                        debitDocService.createInvoice(customersforInvoice, CommonConstants.INVOICE_TYPE.CHANGE_PLAN, parentId, childIds, deactivatePlanReqDTOList.getRecordPayment(), null, changePlanNextBillDate, false, null,null);
                    } else {
                        debitDocService.createInvoice(customersPojo, CommonConstants.INVOICE_TYPE.CHANGE_PLAN, "", deactivatePlanReqDTOList.getRecordPayment(), null, null, changePlanNextBillDate, false, null,null,null);
                    }
                    for (DeactivatePlanReqDTO reqDTO : deactivatePlanReqDTOList.getDeactivatePlanReqDTOS()) {
                        List<Integer> custServiceMappingIdList = reqDTO.getDeactivatePlanReqModels().stream().map(DeactivatePlanReqModel::getCustServiceMappingId).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(custServiceMappingIdList)) {
                            List<CustomerServiceMapping> customerServiceMappings = customerServiceMappingRepository.findAllByIdIn(custServiceMappingIdList);
                            if (!CollectionUtils.isEmpty(customerServiceMappings)) {
                                customerServiceMappings = customerServiceMappings.stream().peek(customerServiceMapping -> customerServiceMapping.setCustId(reqDTO.getCustId())).collect(Collectors.toList());
                                customerServiceMappingRepository.saveAll(customerServiceMappings);
                            }
                        }
                    }

                    //TODO: added timer to don't conflict bor bill to subisu invoice, need to find solution
                    for (DeactivatePlanReqDTO reqDTO : deactivatePlanReqDTOList.getDeactivatePlanReqDTOS()) {
                        if (reqDTO.getDeactivatePlanReqModels().get(0).isBillToOrg()) {
                            Thread.sleep(2000);
                            subscriberService.orgCustInvoiceForChangePlan(customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()), reqDTO);
                        }
                    }
                    if (Objects.nonNull(deactivatePlanReqDTOList.getSkipQuotaUpdate())) {
                        if (customersforInvoice.size() > 1) {
                            for (Customers costomer : customersforInvoice) {
                                subscriberService.skipQuotaUpdate(costomer.getId(), deactivatePlanReqDTOList.getSkipQuotaUpdate());
                            }
                        } else {
                            subscriberService.skipQuotaUpdate(customersPojo.getId(), deactivatePlanReqDTOList.getSkipQuotaUpdate());
                        }
                    }

                    LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customersPojo, customersPojo.getNextBillDate());
                    if (nextQuotaReset != null) {
                        customersPojo.setNextQuotaResetDate(nextQuotaReset);
                    } else {
                        customersPojo.setNextQuotaResetDate(LocalDate.now());
                    }
                    Customers updatedCustomer = customersRepository.save(customersPojo);

                    List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustomerId(customersPojo.getId());
                    List<CustomerServiceMapping> customerServiceMappinList = customerServiceMappingRepository.findAllByCustId(customersPojo.getId());
                    createDataSharedService.updateCustomerEntityForAllMicroServce(updatedCustomer, custPlanMapppingList, customerServiceMappinList);
                    Object[] planDetails = subscriberService.findPlanPriceAndEndDateByPlanId(requestDTOs.getDeactivatePlanReqModels().get(0).getNewPlanId(), customers.getId());
                    if (planDetails != null && planDetails.length > 0 && planDetails[0] instanceof Object[]) {
                        Object[] innerArray = (Object[]) planDetails[0];
                        if (innerArray.length >= 3) {
                            planPrice = innerArray[2] != null ? ((Number) innerArray[2]).doubleValue() : 0.0;
                            planExpiryDate = innerArray[1] != null ? ((java.time.LocalDateTime) innerArray[1]).toLocalDate() : null;
                        }
                    }
                    response.put("AccountNo", accountNo);
                    response.put("PackageName", packageName);
                    response.put("Price", planPrice);
                    response.put("PaymentDue", planExpiryDate);
                    response.put("StatusCode", "SUCCESS");
                } else {
                    RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                    response.put(APIConstants.ERROR_TAG, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
                    RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                    response.put("AccountNo", accountNo);
                    response.put("PackageName", packageName);
                    response.put("Price", planPrice);
                    response.put("PaymentDue", planExpiryDate);
                    response.put("StatusCode", "FAILED");
                    logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Change Plan " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + Constants.MVNO_DELETE_UPDATE_ERROR_MSG + LogConstants.LOG_STATUS_CODE + RESP_CODE);

                }
            } else if(customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVATION_PENDING) || customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.NEW_ACTIVATION)) {
                Integer currentMvnoId = subscriberService.getLoggedInMvnoId(customers.getId());
                Integer dataMvnoId = customers.getMvnoId();
                if (currentMvnoId == 1 || dataMvnoId.equals(currentMvnoId)) {
                    if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                        if (requestFrom.equals("pw") && !getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficient(requestDTOs)) {
                            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                            response.put(APIConstants.ERROR_TAG, "Partner has Insufficient balance!");
                            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivatePlan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                            return apiResponse(RESP_CODE, response, null);
                        }

                        if (requestFrom.equals("pw") && getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficientForLCO(requestDTOs)) {
                            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                            response.put(APIConstants.ERROR_TAG, "Partner has Insufficient balance!");
                            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivatePlan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                            return apiResponse(RESP_CODE, response, null);
                        }
                    }
                    DeactivatePlanReqDTO result = new DeactivatePlanReqDTO();
                    Random rnd = new Random();
                    int renewalId = rnd.nextInt(999999);
                    if (customers.getStatus().equalsIgnoreCase(SubscriberConstants.ACTIVE)) {
                        result = subscriberService.deActivatePlan(requestDTOs, renewalId, false, null);
                    }
                    if (customers.getStatus().equalsIgnoreCase(SubscriberConstants.NEW_ACTIVATION)) {
                        result = subscriberService.deActivatePlanForCAFCustomer(requestDTOs);
                    }
                    LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers, customers.getNextBillDate());
                    if (nextQuotaReset != null) {
                        customers.setNextQuotaResetDate(nextQuotaReset);
                    } else {
                        customers.setNextQuotaResetDate(LocalDate.now());
                    }
                    customersRepository.save(customers);
                    if (requestDTOs.getDeactivatePlanReqModels().get(0).isBillToOrg()) {
                        subscriberService.orgCustInvoiceForChangePlan(customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()), requestDTOs);
                    }
                    Object[] planDetails = subscriberService.findPlanPriceAndEndDateByPlanId(requestDTOs.getDeactivatePlanReqModels().get(0).getNewPlanId(), customers.getId());
                    if (planDetails != null && planDetails.length > 0 && planDetails[0] instanceof Object[]) {
                        Object[] innerArray = (Object[]) planDetails[0];
                        if (innerArray.length >= 3) {
                            planPrice = innerArray[2] != null ? ((Number) innerArray[2]).doubleValue() : 0.0;
                            planExpiryDate = innerArray[1] != null ? ((java.time.LocalDateTime) innerArray[1]).toLocalDate() : null;
                        }
                    }
                    response.put("AccountNo", accountNo);
                    response.put("PackageName", packageName);
                    response.put("Price", planPrice);
                    response.put("PaymentDue", planExpiryDate);
                    response.put("StatusCode", "SUCCESS");
                } else {
                    RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                    response.put("AccountNo", accountNo);
                    response.put("PackageName", packageName);
                    response.put("Price", planPrice);
                    response.put("PaymentDue", planExpiryDate);
                    response.put("StatusCode", "FAILED");
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivatePlan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + Constants.MVNO_DELETE_UPDATE_ERROR_MSG + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                }
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            response.put("StatusCode", ex.getErrCode());
            response.put("error", ex.getMessage());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivatePlan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            response.put("StatusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("error", ex.getMessage());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivatePlan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return ResponseEntity.ok(response);
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

    @PostMapping("/generatePaymentLinkForRenew/{custId}")
    public GenericDataDTO generatePaymentLinkForRenew(@PathVariable Integer custId, HttpServletRequest req) {
        LOGGER.info("************* Inside generatePaymentLink For renew *************");
        Integer RESP_CODE = APIConstants.FAIL;
        String token = req.getHeader("Authorization");
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            dataDTO.setData(subscriberService.generatePaymentLinkForRenew(custId,token));
            dataDTO.setResponseMessage("Renewal link generated successfully! Click to proceed with your renewal.");
            dataDTO.setResponseCode(APIConstants.SUCCESS);
            RESP_CODE = APIConstants.SUCCESS;
            dataDTO.setResponseCode(RESP_CODE);
            return dataDTO;
        }catch (CustomValidationException e) {
            dataDTO.setResponseMessage(e.getMessage());
            dataDTO.setData(null);
            Integer responseCode = APIConstants.FAIL;
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            dataDTO.setResponseCode(RESP_CODE);
            return dataDTO;
        }
        catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
            dataDTO.setData(null);
            dataDTO.setResponseMessage("Something went wrong.");
            dataDTO.setResponseCode(RESP_CODE);
            return dataDTO;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

    }

    @ApiOperation(value = "This API will fetch plan price By customer Id")
    @GetMapping("/customers/getplanPriceByPlanId/{planId}")
    public Double getplanPriceByPlanId(@PathVariable Integer planId,HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        try {
            return customersService.getplanPriceByPlanId(planId);
        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "fetch plan price" +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + e.getMessage() +
                    LogConstants.LOG_STATUS_CODE + APIConstants.FAIL, e);
            return 0.0;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
        }
    }

    @ApiOperation(value = "This API will fetch plan name By customer Id")
    @GetMapping("/customers/getplanNameByPlanId/{planId}")
    public String getplanNameByPlanId(@PathVariable Integer planId,HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        try {
            return customersService.getplanNameByPlanId(planId);
        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "fetch plan price" +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + e.getMessage() +
                    LogConstants.LOG_STATUS_CODE + APIConstants.FAIL, e);
            return "";
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
        }
    }


    @ApiOperation(value = "This API will fetch plan price By customer Id")
    @GetMapping("/customers/getplanIdByCustId/{custId}")
    public Integer getplanIdByCustId(@PathVariable Integer custId,HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        try {
            return subscriberService.getLatestPlanByCustId(custId);
        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "fetch getplanIdByCustId " +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + e.getMessage() +
                    LogConstants.LOG_STATUS_CODE + APIConstants.FAIL, e);
            return 0;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
        }
    }

    @PostMapping(value = "/getAllPlansByServiceAreaAndTypeInternal")
    public ResponseEntity<?> getAllPlansByServiceAreaAndTypeInternal(@RequestBody ServiceAreaFetchDTO serviceAreaFetchDTO) {
        HashMap<String, Object> response = new HashMap<>();
        Integer responseCode = APIConstants.FAIL;
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            List<LightPostpaidPlanDTO> postpaidPlanList = customersService.getAllPostpaidPlanByServiceAreaAndType(serviceAreaFetchDTO.getSa(), serviceAreaFetchDTO.getPlanGroupTypes(), serviceAreaFetchDTO.getServiceIds());
            if(!postpaidPlanList.isEmpty()) {
                response.put("planList", postpaidPlanList);
                response.put(APIConstants.MESSAGE, "plan fetch successfully");
                responseCode = APIConstants.SUCCESS;
                RESP_CODE = APIConstants.SUCCESS;
            }
            if(postpaidPlanList.isEmpty()) {
                response.put(APIConstants.MESSAGE, "No record found");
                response.put("planList", postpaidPlanList);
                responseCode = HttpStatus.NO_CONTENT.value();

            }
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
        } catch (Exception e) {
            responseCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
        } finally {
        }
        return apiResponse(responseCode, response);
    }

    @ApiOperation(value = "This API will fetch plan price By customer Id")
    @GetMapping("/customers/getPlanPriceByCustId/{custId}")
    public Double getPlanPriceByCustId(@PathVariable Integer custId,HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        try {
            return customersService.getPlanPriceByCustId(custId);
        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "fetch plan price" +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + e.getMessage() +
                    LogConstants.LOG_STATUS_CODE + APIConstants.FAIL, e);
            return 0.0;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
        }
    }
    @GetMapping(value = "getCustomerAddressDetails/{id}")
    public GenericDataDTO getCustomerDetails(@PathVariable Integer id){
        GenericDataDTO response = new GenericDataDTO();
        try{
            List<NewAddressListPojo> addressListPojos = customersService.getAddressDataByCustomerId(id);
            if(addressListPojos.isEmpty() || Objects.isNull(addressListPojos.get(0))){
                response.setResponseMessage("No records found for CustomerId : "+id);
                response.setResponseCode(HttpStatus.OK.value());
                return response;
            }
            response.setDataList(addressListPojos);
            response.setResponseCode(HttpStatus.OK.value());
            response.setTotalRecords(addressListPojos.size());
            response.setResponseMessage("Fetch Successfully.");
        }catch (Exception e){
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage(e.getMessage());
        }
        return response;
    }

    @GetMapping("/checkHouseHoldIdExist")
    public Boolean checkHouseHoldIdExist(@RequestParam("hhId")String hhId,HttpServletRequest req){
        if (hhId == null) {
            return false;
        }
        return idValidationRepository.existsByHouseholdIdAndIsDeletedFalse(hhId);
    }



}
