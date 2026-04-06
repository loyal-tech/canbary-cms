package com.adopt.apigw.modules.subscriber.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.*;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.dto.ValidationData;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;
import com.adopt.apigw.modules.Communication.Helper.CommunicationHelper;
import com.adopt.apigw.modules.RvenueClient.RevenueClient;
import com.adopt.apigw.modules.ServiceArea.SubscriberMapper;
import com.adopt.apigw.modules.SubscriberUpdates.Utils.SubscriberUpdateUtils;
import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateConstant;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.customerDocDetails.domain.CustomerDocDetails;
import com.adopt.apigw.modules.customerDocDetails.repository.CustomerDocDetailsRepository;
import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
import com.adopt.apigw.modules.ippool.model.IPPoolDtlsDTO;
import com.adopt.apigw.modules.ippool.service.IPPoolDtlsService;
import com.adopt.apigw.modules.ippool.utils.IpConfigConstant;
import com.adopt.apigw.modules.subscriber.mapper.SubscriberDetailsMapper;
import com.adopt.apigw.modules.subscriber.model.*;
import com.adopt.apigw.modules.subscriber.service.*;
import com.adopt.apigw.pojo.ExtendPlanValidity;
import com.adopt.apigw.pojo.FlagDTO;
import com.adopt.apigw.pojo.PaginationDetails;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.pojo.customer.plans.ExtendPlanValidityInBulk;
import com.adopt.apigw.pojo.customer.plans.PromiseToPayPojoInBulk;
import com.adopt.apigw.pojo.customer.service.TerminateServiceInBulkPojo;
import com.adopt.apigw.repository.LeadMasterRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.*;
import com.adopt.apigw.service.postpaid.*;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.SUBSCRIBER)
public class SubscriberController {

    private static String MODULE = " [SubscriberController] ";
    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    private CustomersService customersService;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;
    @Autowired
    private ChargeService chargeService;
    @Autowired
    private Environment env;
    @Autowired
    private CustChargeService custChargeService;

    @Autowired
    private CustPlanExtendValidityMappingRepository custPlanExtendValidityMappingRepository;
    @Autowired
    private CreditDocService creditDocService;
    @Autowired
    private DebitDocService debitDocService;
    @Autowired
    private CustomerDocDetailsService customerDocDetailsService;
    @Autowired
    private BillRunService billRunService;
    @Autowired
    private IPPoolDtlsService ipPoolDtlsService;
    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private PostpaidPlanService postpaidPlanService;
    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private WorkflowAuditService workflowAuditService;

    private String docType;
    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private DbrService dbrService;

    @Autowired
    private SubscriberMapper subscriberMapper;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private SubscriberDetailsMapper subscriberDetailsMapper;

    @Autowired
    private CustomerChargeHistoryRepo customerChargeHistoryRepo;

    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    RevenueClient revenueClient;

    private final Logger logger = LoggerFactory.getLogger(SubscriberController.class);
    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;
    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private LeadMasterRepository leadMasterRepository;

    @Autowired
    private Tracer tracer;
    @Autowired
    private CustomerDocDetailsRepository customerDocDetailsRepository;

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_VIEW + "\")")
    @GetMapping(value = "/getBasicCustDetails/{customerId}")
    public GenericDataDTO getBasicSubscriberDetails(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getBasicSubscriberDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Basic Subscriber Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Basic Subscriber Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.getBasicDetailsOfSubscriber(customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Basic Subscriber Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ////ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Basic Subscriber Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_VIEW_QOS + "\")")
    @GetMapping(value = "/getQosDetails/{customerId}")
    public GenericDataDTO getQosDetails(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getQosDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Qos Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Qos Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.getQosPolicyDetails(customerId));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Qos Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Qos Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_QOS + "\")")
    @PostMapping("/changeQos")
    public GenericDataDTO changeQos(@RequestBody ChangeQosRequestDTO requestDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [changeQos()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == requestDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Change Qos" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            if (null == customers) {
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Change Qos" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.changeQos(requestDTO, customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            String updatedValues = UtilsCommon.getUpdatedDiff(subscriberService.get(requestDTO.getCustId(),customers.getMvnoId()), requestDTO);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Change Qos" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //	//ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Change Qos" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Change Qos" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_VOICE_DETAILS + "\")")
    @PostMapping(value = "/voiceDetails")
    public GenericDataDTO changeVoiceDetails(@RequestBody CustomerVoiceDetailsDTO pojo, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [voicedetails] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (pojo.getId() == null) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Void Detais" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(pojo.getId()).get();
            if (null == customers) {
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Void Detais" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            String updatedValues = UtilsCommon.getUpdatedDiff(customersService.convertCustomersModelToCustomersPojo(customers), pojo);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.changeVoiceDetails(pojo));
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Void Detais" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_UPDATE_VOICE_DETAILS,
                    null, updatedValues, pojo.getId().longValue(), "");
        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Void Detais" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @GetMapping("/getLocationDetail/{custId}")
    public GenericDataDTO getLatitudeAndLongitude(@PathVariable Integer custId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + "[getLocationDetail]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (custId == null) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Latitude And Longitude" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(custId).get();
            if (customers == null) {
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Latitude And Longitude" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Latitude And Longitude" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            genericDataDTO.setData(subscriberService.getLocationDetail(customers));
        } catch (Exception e) {
            e.printStackTrace();
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Latitude And Longitude" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PostMapping("/updateLocationDetail")
    public GenericDataDTO updateLatitudeAndLongitude(@RequestBody CustomerLocationDTO pojo, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + "[UpdateLocationDetail]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (pojo.getCustId() == null) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Location Detail" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(pojo.getCustId()).get();
            if (customers == null) {
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Location Detail" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            String updatedValues = UtilsCommon.getUpdatedDiff(customers, pojo);

            genericDataDTO.setData(subscriberService.updateLocationDetail(pojo, customers));
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Location Detail" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            ////ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Location Detail" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_VIEW_VOICE_DETAILS + "\")")
    @GetMapping(value = "/voiceDetails/{custId}")
    public GenericDataDTO getCustomerVoice(@PathVariable Integer custId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + "[getCustomerDetails]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (custId == null) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Customer voice" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(custId).get();
//            logger.info("Fetching voice details of customer " + customers.getUsername() + " :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            if (customers == null) {
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Customer voice" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setData(subscriberService.getVoiceDetailById(custId));
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Customer voice" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            ////ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Customer voice" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_VIEW_QUOTA + "\")")
    @GetMapping(value = "/getQuota/{customerId}")
    public GenericDataDTO getQuotaDetails(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getQuotaDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Quota Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Quota Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Quota Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return GenericDataDTO.getGenericDataDTO(subscriberService.getQuota(customerId));
        } catch (Exception ex) {
            //	//ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Quota Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_QUOTA + "\")")
    @PostMapping("/changeQuota")
    public GenericDataDTO changeQuota(@RequestBody QuotaDtlsModel requestDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [changeQuota()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == requestDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Quota" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            if (null == customers) {
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Quota" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }

            String updatedValues = UtilsCommon.getUpdatedDiff(customers, requestDTO);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            CustomersBasicDetailsPojo pojo = subscriberService.changeQuota(requestDTO, customers);
            genericDataDTO.setData(pojo);
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);

            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_UPDATE_QUOTA,
                    req.getRemoteAddr(), null, pojo.getId().longValue(), pojo.getName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Quota" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //	//ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Quota" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @GetMapping(value = "/getStatus/{customerId}")
    public GenericDataDTO getStatus(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getStatus()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Status" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Status" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(null);
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Status" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //	//ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete pincode" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_STATUS + "\")")
    @PostMapping("/changeStatus")
    public GenericDataDTO changeStatus(@RequestBody StatusDTO requestDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [changeStatus()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == requestDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update customer status by : " + requestDTO.getCurrentStatus() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            if (null == customers) {
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update customer status by : " + requestDTO.getCurrentStatus() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            String updatedValues = UtilsCommon.getUpdatedDiff(customers, requestDTO);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.changeStatus(requestDTO));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_COUNTRY, AclConstants.OPERATION_COUNTRY_ADD,
                    req.getRemoteAddr(), updatedValues, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update customer status by : " + requestDTO.getCurrentStatus() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //	//ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update customer status by : " + requestDTO.getCurrentStatus() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_VIEW_EXPIRY + "\")")
    @GetMapping(value = "/getExpiry/{customerId}")
    public GenericDataDTO getExpiry(@PathVariable Integer customerId , HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getExpiry()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                return genericDataDTO;
            }
            List<CustomerPlansModel> customerPlansModelList = subscriberService.getExpiry(customerId);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(customerPlansModelList);
            genericDataDTO.setTotalRecords(customerPlansModelList.size());
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
        } catch (Exception ex) {
            //	//ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_EXPIRY + "\")")
    @PostMapping("/changeExpiry")
    public GenericDataDTO changeExpiry(@RequestBody ExpiryDTO requestDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [changeExpiry()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == requestDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                return genericDataDTO;
            }
            String updatedValues = UtilsCommon.getUpdatedDiff(customers, requestDTO);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.changeExpiry(requestDTO));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
                    AclConstants.OPERATION_CUSTOMER_UPDATE_EXPIRY, req.getRemoteAddr(), updatedValues,
                    customers.getId().longValue(), customers.getFullName());
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//				logger.error("Unable to change expiry date to customer "+customersService.get(requestDTO.getCustId()).getUsername()+" :  request: { From : {}}; Response : {{} code:{}};Exception: {}",req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),ex.getStackTrace());
                return genericDataDTO;

            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }

    @GetMapping("/getDocTypeForCustomer")
    public GenericDataDTO getDocType(HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = MODULE + " [getDocType()] ";
        docType = clientServiceSrv.getClientSrvByName(ClientServiceConstant.API_DOCTYPE).get(0).getValue();
        try {
            if (null == docType) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide DocumentType!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Doc Type For Customer" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);

                return genericDataDTO;
            }
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Doc Type For Customer" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return GenericDataDTO.getGenericDataDTO(subscriberService.getDocumentList(docType));
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Doc Type For Customer" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
///			logger.info("Fetching All Hierarchy :  request: { From : {}}; Response : {{} code:{}}",req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            return genericDataDTO;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }

    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_VIEW + "\")")
    @GetMapping("/search")
    public GenericDataDTO searchCustomer(@RequestParam(name = "s", defaultValue = "") String search, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = MODULE + " [searchCustomer()] ";
        try {
            if ("".equals(search)) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please provide search criteria!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Customer using keyword : " + search + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_FAILED + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
                return genericDataDTO;
            }
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Customer using keyword : " + search + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return GenericDataDTO.getGenericDataDTO(subscriberService.searchCustomer(search));
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Customer using keyword : " + search + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            MDC.remove("type");
            return genericDataDTO;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_MANAGE_MAC + "\")")
    @GetMapping(value = "/getMacDetails/{custId}")
    public GenericDataDTO getMacDetails(@PathVariable Integer custId , HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = MODULE + " [getMacDetails()] ";
        try {
            genericDataDTO.setData(subscriberService.getMacDetails(custId));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Mac-Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = APIConstants.EXPECTATION_FAILED;
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Mac-Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_MANAGE_MAC + "\")")
    @PostMapping(value = "/updateMacDetails")
    public GenericDataDTO updateMacDetails(@RequestBody MacUpdateModel dto) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = MODULE + " [updateMacDetails()] ";
        try {
            if (null == dto.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(dto.getCustId()).get();
            String updatedValues = UtilsCommon.getUpdatedDiff(customers, dto);
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                return genericDataDTO;
            }

            genericDataDTO.setData(subscriberService.updateMacDetails(dto,customers.getMvnoId()));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_RESET_MAC + "\")")
    @PostMapping(value = "/resetMac/{custId}")
    public GenericDataDTO resetMac(@PathVariable Integer custId, @RequestBody ResetMacDTO requestDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = MODULE + " [resetMac()] ";
        try {
            if (null == custId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(custId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                return genericDataDTO;
            }

            genericDataDTO.setData(subscriberService.resetMacDetails(custId, requestDTO.getRemarks()));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_PLANS + "\",\""
//            + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_PLANS+ "\")")
    @GetMapping(value = "/getActivePlanList/{customerId}")
    public GenericDataDTO getActivePlanList(@PathVariable Integer customerId, @RequestParam(name = "serviceId", required = false) Integer serviceId, @RequestParam(name = "isNotChangePlan", required = false, defaultValue = "false") Boolean isNotChangePlan, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getActivePlanList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Active Plan List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Active Plan List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
//            logger.info("Featching Active plans For user " + customers.getUsername() + ":  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            List<CustomerPlansModel> planslist = subscriberService.getActivePlanList(customerId, isNotChangePlan);
            planslist = planslist.stream().filter(i -> !i.isIsdeleteforVoid()).collect(Collectors.toList());
            if (serviceId != null) {
                return GenericDataDTO.getGenericDataDTO(subscriberService.getActivePlanListForServiceId(planslist, serviceId));
            }
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Active Plan List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return GenericDataDTO.getGenericDataDTO(planslist);
        } catch (Exception ex) {
            ex.printStackTrace();
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Active Plan List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_PLANS + "\",\""
//            + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_PLANS + "\",\"" +
//            MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_PLANS + "\",\"" +
//            MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_PLANS + "\")")
    @GetMapping(value = "/getFuturePlanList/{customerId}")
    public GenericDataDTO getFuturePlanList(@PathVariable Integer customerId, @RequestParam(name = "serviceId", required = false) Integer serviceId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getFuturePlanList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Future Plan list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Future Plan list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Future Plan list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            List<CustomerPlansModel> planslist = subscriberService.getFuturePlanList(customerId, true);
            if (serviceId != null) {
                return GenericDataDTO.getGenericDataDTO(subscriberService.getActivePlanListForServiceId(planslist, serviceId));
            }
            return GenericDataDTO.getGenericDataDTO(planslist);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Future Plan list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_PLANS + "\",\""
//            + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_PLANS + "\",\"" +
//            MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_PLANS + "\",\"" +
//            MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_PLANS + "\")")
    @GetMapping(value = "/getExpiredPlanList/{customerId}")
    public GenericDataDTO getExpiredPlanList(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getExpiredPlanList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Expired Plan List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Expired Plan List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Expired Plan List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return GenericDataDTO.getGenericDataDTO(subscriberService.getExpiredPlanList(customerId, true));
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Expired Plan List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_APPLY_CHARGE + "\")")
    @PostMapping("/applyCharge")
    public GenericDataDTO applyCharge(@RequestBody ApplyChargeRequestDTO requestDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [applyCharge()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == requestDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                logger.info("Unable to apply charge for customer " + requestDTO.getCustId() + " :  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                return genericDataDTO;
            }
            Charge charge = chargeRepository.findById(requestDTO.getCharge_id()).orElse(null);
            if (null == charge) {
                genericDataDTO.setResponseMessage("No any charges found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                logger.info("Fetching All Hierarchy :  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            if (charge.getChargecategory().equalsIgnoreCase(ChargeConstants.CHARGE_CATEGORY_IP)) {
                List<CustChargeDetailsPojo> tempCustChargeDetails = new ArrayList<>();
                List<CustChargeDetailsPojo> custChargeDetails = custChargeService
                        .findCustChargeByChargeCategory(customers, ChargeConstants.CHARGE_CATEGORY_IP);
                if (custChargeDetails.size() > 0) {
                    custChargeDetails.forEach(data -> {
                        if (requestDTO.getStartdate()
                                .isBefore(data.getEnddate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
                            tempCustChargeDetails.add(data);
                        }
                    });
                }
                if (tempCustChargeDetails.size() > 0) {
                    genericDataDTO
                            .setResponseMessage("Please select other date. Ip already purchased with selected date");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                    logger.info("Unable to apply charge for customer " + customers.getUsername() + " :  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                    return genericDataDTO;
                }
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            ApplyChargeResponseDTO applyChargeResponseDTO = subscriberService.applyCharge(requestDTO, customers,
                    charge);
            genericDataDTO.setData(applyChargeResponseDTO);
            // Genrate ChargeBill
            try {
                Runnable chargeRunnable = new ChargeThread(customers.getId(),
                        Collections.singletonList(applyChargeResponseDTO.getBasicChargeDetails().getCustChargeId()),
                        customersService, 0L, "", null);
                Thread billchargeThread = new Thread(chargeRunnable);
                billchargeThread.start();
            } catch (Exception e) {
                //ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
//                logger.info("Unable to apply charge for customer " + customers.getUsername() + " :  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                e.printStackTrace();
            }
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_APPLY_CHARGE,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
//            logger.info("Applying charges to customer " + customers.getUsername() + " :  request: { From : {}, }; Response : {{} code:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//                logger.error("Unable to apply charge for customer " + customersService.get(requestDTO.getCustId()).getUsername() + ":  request: { From : {}}; Response : {{} code:{}}exception:{}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            logger.error("Unable to apply charge for customer " + customersService.get(requestDTO.getCustId()).getUsername() + " :  request: { From : {}}; Response : {{} code:{}}exception:{}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_APPLY_CHARGE + "\")")
    @GetMapping("/getSubscriberCharges")
    public GenericDataDTO getSubscriberCharges(HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getSubscriberCharges()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<String> envChargeList = Arrays.asList(Objects.requireNonNull(clientServiceSrv
                    .getClientSrvByName(ClientServiceConstant.SUBSCRIBER_CHARGE_GROUP).get(0).getValue()).split(","));
            List<ChargePojo> chargeList = this.chargeService.findAllByChargeCategories(envChargeList);
            if (null == chargeList) {
                genericDataDTO.setResponseMessage("Charges not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Subscriber Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(chargeList);
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Subscriber Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Subscriber Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_ROLLBACK_CHARGE + "\")")
    @GetMapping(value = "/getReversibleCharge/{customerId}")
    public GenericDataDTO getReversibleCharge(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getReversibleCharge()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Reversible Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Reversible Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.subscriberService.getReversibleCharge(customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Reversible Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Reversible Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Reversible Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_ROLLBACK_CHARGE + "\")")
    @PostMapping("/reverseCharge")
    public GenericDataDTO applyReverseCharge(@RequestBody ReverseChargeRequestDTO requestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [reverseCharge()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == requestDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reverse Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reverse Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.reverseCharge(requestDTO, customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_APPLY_CHARGE,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reverse Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reverse Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reverse Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_ROLLBACK_CHARGE + "\")")
    @GetMapping(value = "/calculateReverseCharge/{chargeId}")
    public GenericDataDTO calculateReverseCharge(@PathVariable Integer chargeId, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [calculateReverseCharge()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == chargeId) {
                genericDataDTO.setResponseMessage("Please provide Charge id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Calculate Reverse Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            CustChargeDetails custChargeDetails = this.custChargeService.get(chargeId,mvnoId);
            if (null == custChargeDetails) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Calculate Reverse Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            /*
             * if (custChargeDetails.getEnddate() == null ||
             * custChargeDetails.getEnddate().before(custChargeDetails.getStartdate())) {
             * genericDataDTO.
             * setResponseMessage("End date is can not be null and End date must be after and greater than start date"
             * ); genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value()); return
             * genericDataDTO; }
             */
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.subscriberService.calculateReverseCharge(custChargeDetails));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Calculate Reverse Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Calculate Reverse Charge" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_CONTACT_DETAILS + "\")")
    @GetMapping(value = "/getContactDetails/{customerId}")
    public GenericDataDTO getContactDetails(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getContactDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Contact Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Contact Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService
                    .getContactDetails(customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext())));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Contact Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Contact Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_CONTACT_DETAILS + "\")")
    @PostMapping(value = "/updateContactDetails")
    public GenericDataDTO updateContactDetails(@RequestBody ContactDetailsDTO contactDetailsDTO, HttpServletRequest req)
            throws NoSuchFieldException {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [updateContactDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == contactDetailsDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update ContactDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(contactDetailsDTO.getCustId()).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update ContactDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            String updatedValues = UtilsCommon.getUpdatedDiff(customers, contactDetailsDTO);
            genericDataDTO.setData(subscriberService.updateContactDetails(contactDetailsDTO));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
                    AclConstants.OPERATION_CUSTOMER_UPDATE_CONTACT_DETAILS, req.getRemoteAddr(), null,
                    customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update ContactDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update ContactDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_BASIC_DETAILS + "\")")
    @GetMapping(value = "/getBasicDetails/{customerId}")
    public GenericDataDTO getBasicDetails(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getBasicDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Customer Update Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Customer Update Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;

            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService
                    .getBasicDetails(customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext())));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Customer Update Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Customer Update Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_BASIC_DETAILS + "\")")
    @PostMapping(value = "/updateBasicDetails")
    public GenericDataDTO updateBasicDetails(@RequestBody BasicDetailsDTO basicDetailsDTO, HttpServletRequest req)
            throws NoSuchFieldException {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [updateBasicDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == basicDetailsDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Customer Update Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(basicDetailsDTO.getCustId()).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Customer Update Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            String updatedValues = UtilsCommon.getUpdatedDiff(customers, basicDetailsDTO);
            genericDataDTO.setData(subscriberService.updateBasicDetails(basicDetailsDTO));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
                    AclConstants.OPERATION_CUSTOMER_UPDATE_BASIC_DETAILS, req.getRemoteAddr(), null,
                    customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Customer Update Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Customer Update Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_NETWORK_DETAILS + "\")")
    @GetMapping(value = "/getNetworkDetails/{customerId}")
    public GenericDataDTO getNetworkDetails(@PathVariable Integer customerId, HttpServletRequest req) {

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        String SUBMODULE = MODULE + " [getNetworkDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Network Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Network Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.getNetworkDetails(customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Network Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Network Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_NETWORK_DETAILS + "\")")
    @PostMapping(value = "/updateNetworkDetails")
    public GenericDataDTO updateNetworkDetails(@RequestBody NetworkDetailsDTO networkDetailsDTO, HttpServletRequest req)
            throws NoSuchFieldException {

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [updateNetworkDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == networkDetailsDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Network Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(networkDetailsDTO.getCustId()).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Network Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            String updatedValues = UtilsCommon.getUpdatedDiff(customers, networkDetailsDTO);
            genericDataDTO.setData(subscriberService.updateNetworkDetails(networkDetailsDTO));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
                    AclConstants.OPERATION_CUSTOMER_UPDATE_NETWORK_DETAILS, req.getRemoteAddr(), null,
                    customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Network Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Network Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_ADDRESS_DETAILS + "\")")
    @GetMapping(value = "/getAddressDetails/{customerId}")
    public GenericDataDTO getAddressDetails(@PathVariable Integer customerId, HttpServletRequest req) {

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getAddressDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Address Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Address Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.getAddressDetails(customerId));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Address Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Address Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_ADDRESS_DETAILS + "\")")
    @PostMapping(value = "/updateAddressDetails")
    public GenericDataDTO updateAddressDetails(@RequestBody AddressUpdateDTO addressUpdateDTO, HttpServletRequest req)
            throws NoSuchFieldException {

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [updateAddressDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == addressUpdateDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Address Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(addressUpdateDTO.getCustId()).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Address Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            String updatedValues = UtilsCommon.getUpdatedDiff(customers, addressUpdateDTO);
            genericDataDTO.setData(subscriberService.updateAddressDetails(addressUpdateDTO, customers));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
                    AclConstants.OPERATION_CUSTOMER_UPDATE_ADDRESS_DETAILS, req.getRemoteAddr(), null,
                    customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Address Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Address Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_VIEW_STATUS + "\")")
    @GetMapping(value = "/customers/status/{custId}")
    public GenericDataDTO getCustomerStatus(@PathVariable Integer custId, HttpServletRequest req) {

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == custId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Customer Status" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(custId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Customer Status" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setData(this.subscriberService.getCustomersStatus(customers));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW_STATUS,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Customer Status" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Customer Status" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_CHANGE_CPE_PASSWORD + "\")")
    @PostMapping(value = "/customer/updatePassword/")
    public GenericDataDTO updateCustomersPassword(@RequestBody PasswordPojo pojo, HttpServletRequest req)
            throws NoSuchFieldException {

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [updateCustomerPassword()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(pojo.getCustId()).get();
            if (customers == null) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            customers.setPassword(pojo.getNewpassword());
            customersService.update(customers);

            CommunicationHelper communicationHelper = new CommunicationHelper();
            Map<String, String> map = new HashMap<>();
            map.put(CommunicationConstant.USERNAME, customers.getUsername());
            map.put(CommunicationConstant.PASSWORD, pojo.getNewpassword());
            map.put(CommunicationConstant.EMAIL, customers.getEmail());
            map.put(CommunicationConstant.DESTINATION, customers.getMobile());
            communicationHelper.generateCommunicationDetails(2L, Collections.singletonList(map));

            // Subscriber Update
            SubscriberUpdateUtils.updateSubscriber(null, null, UpdateConstant.UPDATE_PASSWORD, customers, null, null);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
                    AclConstants.OPERATION_CUSTOMER_CHANGE_CPE_PASSWORD, req.getRemoteAddr(), null,
                    pojo.getCustId().longValue(), "");
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_RESET_CPE_PASSWORD + "\")")
    @PostMapping(value = "/customer/resetPassword/")
    public GenericDataDTO resetCustomersPassword(@RequestBody PasswordPojo pojo, HttpServletRequest req)
            throws NoSuchFieldException {

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [resetCustomerPassword()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reset Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(pojo.getCustId()).get();
            if (customers == null) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reset Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            customers.setPassword(customers.getUsername());
            customersService.update(customers);
            // Subscriber Update
            SubscriberUpdateUtils.updateSubscriber(null, null, UpdateConstant.RESET_PASSWORD, customers,
                    pojo.getRemarks(), null);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
                    AclConstants.OPERATION_CUSTOMER_RESET_CPE_PASSWORD, req.getRemoteAddr(), null,
                    pojo.getCustId().longValue(), "");
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reset Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reset Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_CHANGE_SELFCARE_PASSWORD + "\")")
    @PostMapping(value = "/self/updatePassword/")
    public GenericDataDTO updateSelfPassword(@RequestBody PasswordPojo pojo, HttpServletRequest req)
            throws NoSuchFieldException {

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [updateSelfPassword()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(pojo.getCustId()).get();
            if (customers == null) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            customers.setSelfcarepwd(pojo.getNewpassword());
            customersService.update(customers);
            // Subscriber Update
            SubscriberUpdateUtils.updateSubscriber(null, null, UpdateConstant.UPDATE_SELFCARE_PASSWORD, customers,
                    pojo.getRemarks(), null);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
                    AclConstants.OPERATION_CUSTOMER_CHANGE_SELFCARE_PASSWORD, req.getRemoteAddr(), null,
                    pojo.getCustId().longValue(), "");
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_RESET_SELFCARE_PASSWORD + "\")")
    @PostMapping(value = "/self/resetPassword/")
    public GenericDataDTO resetSelfPassword(@RequestBody PasswordPojo pojo, HttpServletRequest req)
            throws NoSuchFieldException {

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [resetSelfPassword()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reset Self Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(pojo.getCustId()).get();
            if (customers == null) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reset Self Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            customers.setSelfcarepwd(customers.getUsername());
            customersService.update(customers);

            // Subscriber Update
            SubscriberUpdateUtils.updateSubscriber(null, null, UpdateConstant.RESET_SELFCARE_PASSWORD, customers,
                    pojo.getRemarks(), null);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
                    AclConstants.OPERATION_CUSTOMER_RESET_SELFCARE_PASSWORD, req.getRemoteAddr(), null,
                    pojo.getCustId().longValue(), "");
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reset Self Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reset Self Customer Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }

        return genericDataDTO;
    }

    @PostMapping(value = "/staff/updatePassword/")
    public GenericDataDTO updateStaffPassword(@RequestBody StaffPasswordPojo pojo, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws NoSuchFieldException {
        String SUBMODULE = MODULE + " [updateStaffPassword()] ";

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getStaffId()) {
                genericDataDTO.setResponseMessage("Please provide User!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Staff Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            StaffUser staffUser = staffUserService.get(pojo.getStaffId(),mvnoId);
            if (staffUser == null) {
                genericDataDTO.setResponseMessage("User not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Staff Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            staffUser.setPassword(encoder.encode(pojo.getNewpassword()));
            staffUserService.update(staffUser);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Staff Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Staff Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PostMapping(value = "/staff/resetPassword/")
    public GenericDataDTO resetStaffPassword(@RequestBody StaffPasswordPojo pojo, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws NoSuchFieldException {

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [resetStaffPassword()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getStaffId()) {
                genericDataDTO.setResponseMessage("Please provide User !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reset Staff Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            StaffUser staffUser = staffUserService.get(pojo.getStaffId(),mvnoId);
            if (staffUser == null) {
                genericDataDTO.setResponseMessage("User not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reset Staff Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            staffUser.setPassword(encoder.encode(staffUser.getUsername()));
            staffUserService.update(staffUser);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reset Staff Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reset Staff Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\""
            + AclConstants.OPERATION_STAFF_USER_CHANGE_PASSWORD + "\")")
    @PostMapping(value = "/staff/changePassword")
    public GenericDataDTO changeStaffPassword(@RequestBody ChangePasswordDTO pojo, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws NoSuchFieldException {
        String SUBMODULE = MODULE + " [changeStaffPassword()] ";

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getId()) {
                genericDataDTO.setResponseMessage("Please provide User!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Change Staff Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            StaffUser staffUser = staffUserService.get(pojo.getId(),mvnoId);
            if (staffUser == null) {
                genericDataDTO.setResponseMessage("User not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Change Staff Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(pojo.getOldPassword(), staffUser.getPassword())) {
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Change Staff Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("Old Password Not Match!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Change Staff Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_INFO + " Old Password Not Match! " + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }

            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);

        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Change Staff Password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PostMapping(value = "/validateForgotPassword")
    public GenericDataDTO ValidateForgotPassword(@RequestBody ForgotPassowrdDTO pojo, HttpServletRequest req) throws NoSuchFieldException {
        String SUBMODULE = MODULE + " [ValidateForgotPassword()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            if (null == pojo.getUsername()) {
                genericDataDTO.setResponseMessage("Please provide UserName !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Validate Forgot password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_INFO + "Please provide UserName !" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (null == pojo.getOtp()) {
                genericDataDTO.setResponseMessage("Please provide OTP !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Validate Forgot password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_INFO + "Please provide OTP !" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            StaffUser staffUser = staffUserService.getByUserName(pojo.getUsername());
            if (staffUser == null) {
                genericDataDTO.setResponseMessage("User not found! Please Enter Valid User !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Validate Forgot password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            } else {
                String res = staffUserService.validateForgotPassword(staffUser, pojo);
                if (res.equalsIgnoreCase(CommonConstants.FLASH_MSG_TYPE_ERROR)) {
                    genericDataDTO.setResponseMessage(res);
                    genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                } else if (res.equalsIgnoreCase(CommonConstants.FLASH_MSG_TYPE_SUCCESS)) {
                    genericDataDTO.setResponseMessage(res);
                    genericDataDTO.setData(staffUser.getId());
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    RESP_CODE = APIConstants.SUCCESS;
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Validate Forgot password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                }
            }
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Validate Forgot password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PostMapping(value = "/forgotPassword")
    public GenericDataDTO forgotPassword(@RequestBody ForgotPassowrdDTO pojo) throws NoSuchFieldException {
        String SUBMODULE = MODULE + " [forgotPassword()] ";
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getUsername()) {
                genericDataDTO.setResponseMessage("Please provide UserName !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                logger.error("Forgot password for user " + pojo.getUsername() + "  :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            StaffUser staffUser = staffUserService.getByUserName(pojo.getUsername());
            if (staffUser == null) {
                genericDataDTO.setResponseMessage("User not found! Please Enter Valid User !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                logger.error("Forgot password for user " + staffUser.getUsername() + ":  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            } else {
                String otp = staffUserService.forgotPass(staffUser);
                genericDataDTO.setResponseMessage(otp);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                logger.info("Forgot password for user " + staffUser.getUsername() + " :  request: { From : {},}; Response : {{} code:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_PROFILE + "\")")
    @PostMapping(value = "/updateProfile")
    public GenericDataDTO updateProfile(@RequestBody UpdateProfileDTO pojo, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws NoSuchFieldException {
        String SUBMODULE = MODULE + " [UpdateProfile()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == pojo.getId()) {
                genericDataDTO.setResponseMessage("Please provide Staff !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Profile" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_INFO + "Please provide UserName !" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            StaffUser staffUser = staffUserService.get(pojo.getId(),mvnoId);
            String updatedValues = UtilsCommon.getUpdatedDiff(staffUser, pojo);
            if (staffUser == null) {
                genericDataDTO.setResponseMessage("User not found! Please Enter Valid User !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Profile" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            } else {

                StaffUserPojo staffUserPojo = staffUserService.updateProfile(staffUser, pojo);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setData(staffUserPojo);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Profile" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }

            CommunicationHelper communicationHelper = new CommunicationHelper();
            Map<String, String> map = new HashMap<>();
            map.put(CommunicationConstant.EMAIL, staffUser.getEmail());
            map.put(CommunicationConstant.MOBILE, staffUser.getPhone());
            communicationHelper.generateCommunicationDetails(1L, Collections.singletonList(map));

            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Profile" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Profile" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_RECORD_PAYMENT + "\")")
    @PostMapping(value = "/recordPayment")
    public GenericDataDTO recordPayment(@RequestBody RecordPaymentRequestDTO requestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [recordPayment()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == requestDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                logger.error("unable to fetch payment record for cudtomer " + requestDTO.getCustId() + ":  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                logger.error("Unable to fetch record payment for customer " + customers.getUsername() + ":  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            RecordpaymentResponseDTO recordpaymentResponseDTO = subscriberService.recordPayment(requestDTO, customers);
            genericDataDTO.setData(recordpaymentResponseDTO);
            if (null != customers) {
                List<CreditDocument> creditDocumentList = recordpaymentResponseDTO.getCreditDocument();
                Runnable receiptRunnable = new ReceiptThread(billRunService, creditDocumentList);
                Thread receiptThread = new Thread(receiptRunnable);
                receiptThread.start();
            }
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
                    AclConstants.OPERATION_CUSTOMER_RECORD_PAYMENT, req.getRemoteAddr(), null,
                    customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Validate Forgot password" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//                logger.error("Fetching All Hierarchy :  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            logger.error("Unable to fetch payment record for customer " + customersService.get(requestDTO.getCustId()).getUsername() + ":  request: { From : {}}; Response : {{} code:{};exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_RECORD_PAYMENT + "\")")
    @GetMapping(value = "/getTdsPendingPayments/{customerId}")
    public GenericDataDTO getTdsPendingPayments(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getTdsPendingPayments()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Tds Pending Payments" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_INFO + "Please provide UserName !" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Tds Pending Payments" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(this.subscriberService.getTdsPendingPayment(customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Tds Pending Payments" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Tds Pending Payments" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_PAYMENT + "\")")
    @GetMapping(value = "/getAllPaymentsByCustomer/{customerId}")
    public GenericDataDTO getAllPaymentsByCustomer(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getAllPaymentsByCustomer()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_INFO + "Please provide UserName !" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(this.subscriberService.getAllPayment(customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_ROLLBACK_PAYMENT + "\")")
    @GetMapping(value = "/getReversiblePayment/{customerId}")
    public GenericDataDTO getReversiblepayment(@PathVariable Integer customerId) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getReversiblepayment()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                logger.error("Unable to fetch reservable paymeent for customer " + customerId + " :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                logger.error("Unable to fetch reservable paymeent for customer " + customers.getUsername() + " :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.subscriberService.getReversiblePayment(customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
//            logger.info("fetching reservable payment for customer " + customers.getUsername() + " :  request: { From : {}, }; Response : {{} code:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            logger.error("Unable to fetch reservable paymeent for customer " + customersService.get(customerId).getUsername() + " :  request: { From : {}}; Response : {{} code:{};exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_ROLLBACK_PAYMENT + "\")")
    @PostMapping("/reversePayment")
    public GenericDataDTO reversePayment(@RequestBody ReversePaymentRequestDTO requestDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [reverseCharge()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == requestDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reverse payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_INFO + "Please provide UserName !" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reverse payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.reversePayment(requestDTO, customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reverse payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reverse payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reverse payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_PAYMENT + "\")")
    @PostMapping(value = "/updatePayment/{id}")
    public GenericDataDTO updatePayment(@RequestBody RecordPaymentRequestDTO requestDTO, @PathVariable Integer id, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [recordPayment()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == requestDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update PAyment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_INFO + "Please provide UserName !" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            String updatedValues = UtilsCommon.getUpdatedDiff(requestDTO, customers);
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update PAyment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            CreditDocument document = this.creditDocService.get(id,customers.getMvnoId());
            if (null == document) {
                genericDataDTO.setResponseMessage("Payment not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update PAyment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.updatePayment(requestDTO, document, customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update PAyment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_UPDATE_PAYMENT,
                    null, updatedValues, null, "");
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update PAyment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update PAyment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_VIEW_PAYMENT_HISTORY + "\")")
    @GetMapping(value = "/paymentHistory/{custId}")
    public GenericDataDTO getPaymentHistory(@PathVariable Integer custId, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getDbcdrProcessing()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (custId == null) {
                genericDataDTO.setResponseMessage("ID not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Payment History" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(custId).get();
            if (customers == null) {
                genericDataDTO.setResponseMessage("Records not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Payment History" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            List<PaymentHistoryDTO> paymentHistories = creditDocService.getByCustId(custId);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(paymentHistories);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Payment History" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Payment History" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_VIEW_PURCHASE_HISTORY + "\")")
    @GetMapping(value = "/purchasedHistory/{custId}")
    public GenericDataDTO getPurchasedHistory(@PathVariable Integer custId, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getPurchasedHistory()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (custId == null) {
                genericDataDTO.setResponseMessage("ID not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Purchase History" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(custId).get();
            if (customers == null) {
                genericDataDTO.setResponseMessage("Records not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Purchase History" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            List<PurchasedHistoryDTO> purchasedtHistories = subscriberService.getByPurchaseHistoryCustId(custId);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(purchasedtHistories);
            genericDataDTO.setTotalRecords(purchasedtHistories.size());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Purchase History" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Purchase History" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @GetMapping(value = "/getCaseCountByCustomer/{custId}")
    public GenericDataDTO getCaseCountByCustomer(@PathVariable Integer custId, HttpServletRequest req) throws Exception {
        String SUBMODULE = MODULE + " [getCaseCountByCustomer()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (custId == null) {
                genericDataDTO.setResponseMessage("ID not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch fetch Case by customer " + custId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(custId).get();
            if (customers == null) {
                genericDataDTO.setResponseMessage("Records not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch fetch Case by customer " + custId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch case by customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return GenericDataDTO.getGenericDataDTO(subscriberService.getCaseCountByCustomer(custId));
        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch case by customer" + LogConstants.LOG_BY_NAME + customersRepository.findById(custId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/checkUniqueCustomer")
    public GenericDataDTO checkCustomerUnique(@Valid @RequestBody SubscriberUniqueChequeReqDTO reqDTO,
                                              BindingResult result, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [checkCustomerUnique()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == reqDTO) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Request Parameter");
                Integer RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch check Unique customer " + reqDTO + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + "Access denined for update operation " + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            if (result.hasErrors()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
                Integer RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch check Unique customer " + reqDTO + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + "Access denined for update operation " + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (null != reqDTO) {
                if (null != reqDTO.getType() && reqDTO.getType().equalsIgnoreCase(SubscriberConstants.TYPE_EMAIL)
                        && subscriberService.checkCustomerUniqueEmail(reqDTO.getValue(), reqDTO.getSubscriberId())) {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage("Email already exists!");
                    Integer RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch check Unique customer " + reqDTO + LogConstants.LOG_BY_NAME + reqDTO.getValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "email with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }
                if (null != reqDTO.getType() && reqDTO.getType().equalsIgnoreCase(SubscriberConstants.TYPE_CAF_NO)
                        && subscriberService.checkCustomerUniqueCafno(reqDTO.getValue())) {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage("CafNo already exists!");
                    Integer RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Hierarchy" + LogConstants.LOG_BY_NAME + reqDTO.getValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "caf no with same already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }
                if (null != reqDTO.getType() && reqDTO.getType().equalsIgnoreCase(SubscriberConstants.TYPE_MOBILE)
                        && subscriberService.checkCustomerUniqueMobile(reqDTO.getValue(), reqDTO.getSubscriberId())) {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage("Mobile Number already exists!");
                    Integer RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch unique Subcsriber for " + subscriberService.checkCustomerUniqueUsername(reqDTO.getValue()) + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "mobile number with same already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }
                if (null != reqDTO.getType() && reqDTO.getType().equalsIgnoreCase(SubscriberConstants.TYPE_USERNAME)
                        && subscriberService.checkCustomerUniqueUsername(reqDTO.getValue())) {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage("Username already exists!");
                    Integer RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch unique Subscriber for " + subscriberService.checkCustomerUniqueUsername(reqDTO.getValue()) + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "username with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                Integer RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Unique customer" + reqDTO.getSubscriberId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            Integer RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch fetch unique user for " + customersRepository.findById(reqDTO.getSubscriberId()).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

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
        return outputStr;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_GET_PURCHASE_INVOICE + "\")")
    @RequestMapping(value = {"/invoice/download/{invoiceid}"}, method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadInvoice(@PathVariable Integer invoiceid, Model model, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [downloadInvoice()] ";
        Resource resource = null;
        try {
            DebitDocument doc = debitDocService.get(invoiceid,mvnoId);
            FileSystemService service = com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
            resource = service.getInvoice(doc.getBillrunid() + File.separator + doc.getDocnumber() + ".pdf");
            // resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Download invoice for customer for invoice id" + invoiceid + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Download invoice for customer for invoice id" + invoiceid + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Download invoice for customer for invoice id" + invoiceid + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return null;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_GET_PAYMENT_RECEIPT + "\")")
    @RequestMapping(value = {"/payment/download/{paymentid}"}, method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadPaymentReceipt(@PathVariable Integer paymentid, Model model, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [downloadPaymentReceipt()] ";
        try {
            // DebitDocument doc = entityService.get(invoiceid);
            Resource resource = null;
            FileSystemService service = com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
            resource = service.getPaymentReceipt(paymentid + File.separator + paymentid + ".pdf");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Download recipt for customer for payment id" + paymentid + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);

            } else {
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Download recipt for customer for payment id" + paymentid + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Download recipt for customer for payment id" + paymentid + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return null;
    }

    // TODO Need to add ACL permissions by verifying this API uses
//    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER_DOCS_DOWNLOAD + "\")")
    @RequestMapping(value = "/document/download/{docId}/{custId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long docId, @PathVariable Integer custId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [downloadDocument()] ";
        Resource resource = null;
        try {
            String customerName = customersRepository.findUsernameById(custId).trim();
            if (null == customerName) {
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Download document for customer " + customersRepository.findById(custId).get().getUsername() + " for doccument id" + docId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
            Optional<CustomerDocDetails> docDetailsDTO = customerDocDetailsRepository.findById(docId);
            if (!docDetailsDTO.isPresent()) {
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Download document for customer " + customersRepository.findById(custId).get().getUsername() + " for doccument id" + docId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
            FileSystemService service = com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
            resource = service.getCustDoc(customerName, docDetailsDTO.get().getUniquename(), custId);
            // resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                // logger.info("Unable to Download recipt for customer " + customers.getUsername() + " for payment id" + docId + " :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);

            } else {
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Download document for customer " + customersRepository.findById(custId).get().getUsername() + " for doccument id" + docId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Download document for customer " + customersRepository.findById(custId).get().getUsername() + " for doccument id" + docId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return null;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_OTHER_DETAILS + "\")")
    @GetMapping("/getPopup/{custId}")
    public GenericDataDTO getSubscriberPopup(@PathVariable Integer custId, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [Subscriber-Popup()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (custId == null) {
                genericDataDTO.setResponseMessage("ID not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch fetch Popup details for customer " + customersRepository.findById(custId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.getSubscriberOtherDetails(custId));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Popup details for customer " + customersRepository.findById(custId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Popup details for customer " + customersRepository.findById(custId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_OTHER_DETAILS + "\")")
    @PostMapping("/updatePopup")
    private GenericDataDTO updateSubscriberPopup(@RequestBody SubscriberPopupDTO subscriberPopupDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [Subscriber-Popup()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (subscriberPopupDTO.getCustId() == null) {
                genericDataDTO.setResponseMessage("Please Enter Id");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Subscriber Popup for customer " + subscriberPopupDTO.getCustId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + "Access denined for update operation " + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            CustomersService customersService = SpringContext.getBean(CustomersService.class);
            SubscriberService subscriberService = SpringContext.getBean(SubscriberService.class);
            Customers customers = customersRepository.findById(subscriberPopupDTO.getCustId()).get();
            if (customers == null) {
                genericDataDTO.setResponseMessage("Customer Not Found !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update subscriber popup for customer" + LogConstants.LOG_BY_NAME + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + "Access denined for update operation " + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }

            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setData(subscriberService.updateSubscriberOtherDetails(subscriberPopupDTO));
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update subscriber popup for customer" + LogConstants.LOG_BY_NAME + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update subscriber popup for customer" + LogConstants.LOG_BY_NAME + customersRepository.findById(subscriberPopupDTO.getCustId()).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_VIEW_PLANS + "\")")
    @GetMapping(value = "/getSubscriberCurrentPlan/{customerId}")
    public GenericDataDTO getSubscriberCurrentPlan(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [getSubscriberCurrentPlan()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch subscriber plan for customer " + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Subscriber current plan For customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.subscriberService.getSubscriberCurrentPlan(customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch subscriber plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW_PLANS,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch subscriber plan for customer " + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_CHANGE_PLAN + "\"," +
            "\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_CHANGE_PLAN + "\"," +
            "\"" + MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_CHANGE_PLAN + "\"," +
            "\"" + MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_CHANGE_PLAN + "\")")
    @PostMapping(value = "/changePlan")
    public GenericDataDTO changePlan(@RequestBody ChangePlanRequestDTO requestDTO,
                                     @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [changePlan()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == requestDTO.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change plan for customer " + requestDTO.getCustId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            String updatedValues = UtilsCommon.getUpdatedDiff(customers, requestDTO);
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change plan for customer " + requestDTO.getCustId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }

            if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON)) {
                List<CustomerPlansModel> currentPlanList = this.subscriberService.getActivePlanList(customers.getId(), false);
                if (currentPlanList.size() <= 0) {
                    genericDataDTO.setResponseMessage("Subscriber must have any active plan for AddOn Purchase");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                    RESP_CODE = APIConstants.NOT_FOUND;
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change plan for customer " + requestDTO.getCustId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }
            }
            if (requestDTO.getOnlinePurType() != null && requestDTO.getOnlinePurType().equalsIgnoreCase(SubscriberConstants.PURCHASE_FROM_FLUTTERWAVE)) {
                requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_FROM_FLUTTERWAVE);
            } else {
                if (subscriberService.getCustomerPlanList(customers.getId(), false).size() <= 0) {
                    requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_NEW);
                } else {
                    requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_RENEW);
                }
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update plan for customer " + updatedValues + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            List<CustomersBasicDetailsPojo> custBasicDetailsPojoList = new ArrayList<CustomersBasicDetailsPojo>();
            if (requestDTO.getPlanGroupId() != null && requestDTO.getPlanGroupId() != 0 && requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW)) {
                if (requestDTO.getPlanList() != null) {
                    Double maxValidity = requestDTO.getPlanList().stream().map(PostpaidPlan::getValidity).max(Double::compare).get();
                    for (PostpaidPlan plan : requestDTO.getPlanList()) {
                        requestDTO.setPlanId(plan.getId());
                        CustomChangePlanDTO customChangePlanDTO = subscriberService.changePlan(requestDTO, customers, false, 0.0,
                                requestFrom, maxValidity);
                        if (customChangePlanDTO.getRecordpaymentResponseDTO() != null) {
                            if (customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument().size() > 0) {
                                List<CreditDocument> creditDocumentList = customChangePlanDTO.getRecordpaymentResponseDTO()
                                        .getCreditDocument();
                                Runnable receiptRunnable = new ReceiptThread(billRunService, creditDocumentList);
                                Thread receiptThread = new Thread(receiptRunnable);
                                receiptThread.start();
                            }
                        }

                        CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                        custBasicDetailsPojoList.add(basicDetailsPojo);
                        try {
                            Customers customer = customersService.get(basicDetailsPojo.getId(),customers.getMvnoId());
                            debitDocService.createInvoice(customer, Constants.RENEW, "", null,null, null,false,false,null,null,null);
                        } catch (Exception e) {
                            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
                            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                            e.printStackTrace();
                        }
                    }
                }
                genericDataDTO.setData(custBasicDetailsPojoList);
            } else {
                CustomChangePlanDTO customChangePlanDTO = subscriberService.changePlan(requestDTO, customers, false, 0.0,
                        requestFrom, null);
                if (customChangePlanDTO.getRecordpaymentResponseDTO() != null) {
                    if (customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument().size() > 0) {
                        List<CreditDocument> creditDocumentList = customChangePlanDTO.getRecordpaymentResponseDTO()
                                .getCreditDocument();
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
                    Runnable invoiceRunnable = new InvoiceThread(
                            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), customer, customersService, "", null, null);
                    Thread invoiceThread = new Thread(invoiceRunnable);
                    invoiceThread.start();
                    /*
                     * Customers customers2 =
                     * customersService.savePaymentXMLDocument(customerMapper.dtoToDomain(
                     * customersPojo, new CycleAvoidingMappingContext())); if (null != customers) {
                     * Runnable receiptRunnable = new ReceiptThread(billRunService,
                     * customers.getCreditDocuments()); Thread receiptThread = new
                     * Thread(receiptRunnable); receiptThread.start(); }
                     */
                } catch (Exception e) {
                    //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
                    RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                    logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    e.printStackTrace();
                }
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
                        auditChangePlan = customers.getFullName() + "change plan type is" + requestDTO.getPurchaseType()
                                + "..and plan is :- " + postpaidPlan.getDisplayName();
                    }
                }

            }
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW_PLANS,
                    req.getRemoteAddr(), auditChangePlan, customers.getId().longValue(), customers.getFullName());

        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update to update plan for customer " + customersRepository.findById(requestDTO.getCustId()).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update to update plan for customer " + customersRepository.findById(requestDTO.getCustId()).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_CHANGE_PLAN + "\"," +
            "\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_CHANGE_PLAN + "\"," +
            "\"" + MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_CHANGE_PLAN + "\"," +
            "\"" + MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_CHANGE_PLAN + "\")")
    @PostMapping(value = "/changePlan01")
    public GenericDataDTO changePlan01(@RequestBody ChangePlanRequestDTOList requestDTOs, @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        String SUBMODULE = MODULE + " [changePlan()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        Boolean flag = true;
        FlagDTO FlagDTO = new FlagDTO();

        Thread invoiceThread = null;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        if (requestDTOs != null) {
            Customers customers = customersRepository.findById(requestDTOs.getChangePlanRequestDTOList().get(0).getCustId()).get();
            if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                if (requestFrom.equals("pw") && !getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficient(requestDTOs, getLoggedInUserPartnerId(),mvnoId)) {
                    genericDataDTO.setResponseMessage("Partner has Insufficient balance!");
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    // logger.error("Unable to customers with name " + customers.getCustname() + "  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                    return genericDataDTO;
                }

                if (requestFrom.equals("pw") && getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficientForLCO(requestDTOs, getLoggedInUserPartnerId(),mvnoId)) {
                    genericDataDTO.setResponseMessage("Partner has Insufficient balance!");
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "customers with name " + customers.getCustname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }
            }
        }

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
                if (custIds != null && custIds.size() > 0) {
                    customersforInvoice.addAll(customersRepository.findAllById(custIds));
                }
                if (custIdOptional.isPresent()) {
                    custId = custIdOptional.get();
                } else {
                    custIdOptional = requestDTOs.getChangePlanRequestDTOList().stream().map(ChangePlanRequestDTO::getCustId).findFirst();
                    if (custIdOptional.isPresent()) {
                        custId = custIdOptional.get();
                    }
                }

                Customers parentCustomers = customersRepository.findById(custId).get();
                customersforInvoice.add(parentCustomers);
                List<CustChargeOverrideDTO> custChargeDetailsList = requestDTOs.getCustChargeDetailsList();
                List<CustChargeOverrideDTO> custChargeOverrideDTOS = new ArrayList<>();
                for (ChangePlanRequestDTO requestDTO : requestDTOs.getChangePlanRequestDTOList()) {
                    if (null == requestDTO.getCustId()) {
                        genericDataDTO.setResponseMessage("Please provide customer id!");
                        RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                        logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customersRepository.findById(custId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                        return genericDataDTO;
                    }
                    if (requestDTO.getPaymentOwner() == null) {
                        requestDTO.setPaymentOwner("");
                    }
                    Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
                    String updatedValues = UtilsCommon.getUpdatedDiff(customers, requestDTO);
                    if (null == customers) {
                        genericDataDTO.setResponseMessage("Customer not found!");
                        RESP_CODE = APIConstants.NOT_FOUND;
                        genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                        logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                        return genericDataDTO;
                    }

                    if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON)) {
                        List<CustomerPlansModel> currentPlanList = this.subscriberService.getActivePlanList(customers.getId(), false);
                        if (currentPlanList.size() <= 0) {
                            genericDataDTO.setResponseMessage("Subscriber must have any active plan for AddOn Purchase");
                            RESP_CODE = APIConstants.NOT_FOUND;
                            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                            return genericDataDTO;
                        }
                    }

                    if (subscriberService.getCustomerPlanList(customers.getId(), false).size() <= 0) {
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_NEW);
                    } else {
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_RENEW);
                    }
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Plan For customer " + updatedValues + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    List<CustomersBasicDetailsPojo> custBasicDetailsPojoList = new ArrayList<CustomersBasicDetailsPojo>();
                    CustomersBasicDetailsPojo basicDetailsPojo = null;
                    if (requestDTO.getPlanGroupId() != null && requestDTO.getPlanGroupId() != 0 && requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW)) {
//                        if (requestDTO.getNewPlanList() != null) {
                        if (CollectionUtils.isEmpty(requestDTO.getNewPlanList())) {
                            List<Integer> newPlanList = requestDTO.getPlanBindWithOldPlans().stream().filter(newPlanBindWithOldPlan -> newPlanBindWithOldPlan.getNewPlanId() != null).map(NewPlanBindWithOldPlan::getNewPlanId).collect(Collectors.toList());
                            //Double maxValidity = requestDTO.getPlanList().stream().map(PostpaidPlan::getValidity).max(Double::compare).get();
                            requestDTO.setNewPlanList(newPlanList);
                        }
                        CustomChangePlanDTO customChangePlanDTO = null;
                        for (NewPlanBindWithOldPlan newPlanBindWithOldPlan : requestDTO.getPlanBindWithOldPlans()) {
                            if (newPlanBindWithOldPlan.getNewPlanId() != null) {
                                requestDTO.setPlanId(newPlanBindWithOldPlan.getNewPlanId());
                                requestDTO.setCustServiceMappingId(newPlanBindWithOldPlan.getCustServiceMappingId());
                                requestDTO.setIsTriggerCoaDm(requestDTOs.getIsTriggerCoaDm());
                                customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers, false, 0.0, requestFrom, null, number,requestDTOs.getDateOverrideDtos(),null);
                                Thread.sleep(1000);//Added bcuz sometimes in billing engine get conflict data.., need solution for this
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
                                    List<CustChargeOverrideDTO> custChargeOverrideDTOs = custChargeDetailsList.stream()
                                            .filter(custCharge -> custCharge.getParentId().equals(customers.getId()))
                                            .collect(Collectors.toList());

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

                        genericDataDTO.setData(custBasicDetailsPojoList);
                    } else {
                        if(Objects.nonNull(requestDTOs.getIsTriggerCoaDm())) {
                            requestDTO.setIsTriggerCoaDm(requestDTOs.getIsTriggerCoaDm());
                        }
                        CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers, false, 0.0, requestFrom, null, number, requestDTOs.getDateOverrideDtos(),requestDTOs);
                        if (customChangePlanDTO.getRecordpaymentResponseDTO() != null) {
                            if (customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument().size() > 0 && !customers.getIsinvoicestop()) {
                                List<CreditDocument> creditDocumentList = customChangePlanDTO.getRecordpaymentResponseDTO()
                                        .getCreditDocument();
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
                                    //                                if (custChargeOverrideDTO.getParentId() != null)
                                    //                                    custChargeOverrideDTO.setCustid(custChargeOverrideDTO.getParentId());
                                    CustChargeOverrideDTO chargeOverrideDTO = custChargeService.createCustChargeOverride(custChargeOverrideDTO);
                                    basicDetailsPojo.setCustChargeOverride(chargeOverrideDTO);
                                    custChargeOverrideDTOS.add(chargeOverrideDTO);

                                }
                            }
                        }
                        genericDataDTO.setData(basicDetailsPojo);

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
                                auditChangePlan = getLoggedInUser().getFullName()  + " change plan type is " + requestDTO.getPurchaseType()
                                        + "..and plan is :- " + postpaidPlan.getDisplayName();
//                                logger.info("Fetching All Hierarchy :  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                            }
                        }

                    }
                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CHANGE_PLAN, AclConstants.OPERATION_CUSTOMER_CHANGE_PLAN,
                            req.getRemoteAddr(), auditChangePlan, customers.getId().longValue(), customers.getFullName());

                }

                try {
                    if (requestDTOs.getChangePlanRequestDTOList().get(0).getPurchaseType().equalsIgnoreCase("Addon")) {
                        Double discount = requestDTOs.getChangePlanRequestDTOList().get(0).getDiscount();
                        if (discount <= 0){
                            FlagDTO.setDiscount(false);
                        }else {
                            FlagDTO.setDiscount(true);
                        }
                        if (customersforInvoice.size() > 1) {
                            Integer parentId = custIdOptional.get();
                            //this is because only when more than one child ia renewing plan, in revenue we need atleast one id as parent id
                            custIds.removeIf(i -> i.equals(parentId));
                            List<Integer> childIds = custIds;
                            debitDocService.createInvoice(customersforInvoice, Constants.ADD_ON, parentId, childIds, requestDTOs.getRecordPayment(), null,false,false,null, requestDTOs.getChildId());
                        } else {
                            debitDocService.createInvoice(parentCustomers, Constants.ADD_ON, "", requestDTOs.getRecordPayment(),null, null,false,false,null,requestDTOs.getChildId(),FlagDTO);
                        }
                    } else {
                        if (customersforInvoice.size() > 1) {
                            Integer parentId = custIdOptional.get();
                            //this is because only when more than one child ia renewing plan, in revenue we need atleast one id as parent id
                            custIds.removeIf(i -> i.equals(parentId));
                            List<Integer> childIds = custIds;
                            debitDocService.createInvoice(customersforInvoice, Constants.RENEW, parentId, childIds, requestDTOs.getRecordPayment(), null,false,false,null,requestDTOs.getChildId());
                        } else {
                            debitDocService.createInvoice(parentCustomers, Constants.RENEW, "", requestDTOs.getRecordPayment(),null, null,false,false,null,requestDTOs.getChildId(),null);
                        }
                    }
//                            if (requestDTOs.getChangePlanRequestDTOList().get(0).getPurchaseType().equalsIgnoreCase("Addon")){
//                                debitDocService.createInvoice(customers,Constants.ADD_ON);
//                            }else {
//                                debitDocService.createInvoice(customers,Constants.RENEW);
//                            }
//                            flag = false;


                } catch (Exception e) {
                    logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for user " + parentCustomers.getId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
                    e.printStackTrace();
                }

            }
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for user " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for user " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_ALLOCATE_IP + "\")")
    @GetMapping(value = "/getSubscriberPurchasedIp/{customerId}")
    public GenericDataDTO getSubscriberPurchasedCharge(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [getSubscriberPurchasedIp()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Subscriber purchased ip for customer " + customerId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "Discount with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Subscriber purchased ip for customer " + customerId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            List<CustChargeDetailsPojo> custChargeDetailsPojoList = this.subscriberService
                    .getSubscriberPurchasedCharge(customers);
            if (null == custChargeDetailsPojoList) {
                genericDataDTO.setResponseMessage("No any charges found for Ip allocation!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Subscriber purchased ip for customer " + customerId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(custChargeDetailsPojoList);
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Subscriber purchased ip for customer " + customerId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Subscriber purchased ip for customer " + customerId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_ALLOCATE_IP + "\")")
    @GetMapping(value = "/getSubscriberChargeForIpRollback/{customerId}")
    public GenericDataDTO getSubscriberChargeForIpRollback(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [getSubscriberPurchasedIp()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Customer ip rollback for customer" + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "Discount with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Customer ip rollback for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            List<CustChargeDetailsPojo> custChargeDetailsPojoList = this.subscriberService
                    .getSubscriberPurchasedChargeForIpRollback(customers);
            if (null == custChargeDetailsPojoList) {
                genericDataDTO.setResponseMessage("No any charges found for Ip allocation!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Customer ip rollback for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(custChargeDetailsPojoList);
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Customer ip rollback for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch ip rollback for  customer " + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_ALLOCATE_IP + "\")")
    @PostMapping("/allocateIp")
    public GenericDataDTO allocateIp(@RequestBody AllocateIpDTO requestDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [allocateIp()] ";
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Allocated ip for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            IPPoolDtlsDTO ipPoolDtlsDTO = ipPoolDtlsService.getEntityById(requestDTO.getIpPoolDtlsId(), auditLogService.getMvnoIdFromCurrentStaff(requestDTO.getCustId()));
            if (!ipPoolDtlsDTO.getStatus().equalsIgnoreCase(IpConfigConstant.IP_STATUS_BLOCK)
                    && ipPoolDtlsDTO.getBlockByCustId() == null) {
                genericDataDTO.setResponseMessage("Requested IP may not be exist or not blocked for this customer");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Allocated ip for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }

            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.subscriberService.allocateIp(customers, requestDTO, false));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_ALLOCATE_IP,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Allocated ip for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Ip address for customer " + customersRepository.findById(requestDTO.getCustId()).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Ip address for customer " + customersRepository.findById(requestDTO.getCustId()).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getSubscriberAllocatedIp/{customerId}")
    public GenericDataDTO getSubscriberAllocatedIp(@PathVariable Integer customerId, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getSubscriberAllocatedIp()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;

        try {
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Subscriber allocated ip for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(subscriberService.getSubscriberAllocatedIp(customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Subscriber allocated ip for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Allocated ip address for customer " + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Allocated ip address for customer " + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_RELEASE_IP + "\")")
    @PostMapping("/releaseIP/{customerId}")
    public GenericDataDTO releaseIp(@RequestBody IPPoolDtlsDTO requestDTO, @PathVariable Integer customerId,
                                    HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [releaseIp()] ";
        Integer RESP_CODE = APIConstants.FAIL;

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "relese Ip address for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            this.subscriberService.releaseIp(requestDTO, customers, IpConfigConstant.IP_TERMINATION_RELEASE);
            genericDataDTO.setData(subscriberService.getBasicDetailsOfSubscriber(customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_RELEASE_IP,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "RElesing Ip address for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "relese ip addresss: for customer " + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "relese ip addresss: for customer " + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_REPLACE_IP + "\")")
    @PostMapping("/replaceIP/{customerId}")
    public GenericDataDTO replaceIp(@RequestBody ReplaceIPDTO requestDTO, @PathVariable Integer customerId,
                                    HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [replaceIp()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "replace Ip address for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.subscriberService.replaceIp(requestDTO, customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_REPLACE_IP,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Ip address for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update ip address for customer " + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_ROLLBACK_IP + "\")")
    @PostMapping("/rollBackIP/{customerId}")
    public GenericDataDTO rollBackIP(@RequestBody ReverseChargeRequestDTO requestDTO, @PathVariable Integer customerId,
                                     HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [rollBackIP()] ";
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch rollback ipaddress for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            this.subscriberService.rollBackIp(requestDTO, customers);
            genericDataDTO.setData(subscriberService.getBasicDetailsOfSubscriber(customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_ROLLBACK_IP,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch ipaddress for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "rollback ipaddress for customer " + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_CHANGE_IP_EXPIRY + "\")")
    @PostMapping("/changeIPExpiry/{customerId}")
    public GenericDataDTO changeIPExpiry(@RequestBody ChangeIPExpiryDTO requestDTO, @PathVariable Integer customerId,
                                         HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [changeIPExpiry()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Customers customers = customersRepository.findById(customerId).get();
            String updatedValues = UtilsCommon.getUpdatedDiff(customers, requestDTO);
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update ip expiry for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.subscriberService.changeIPExpiry(requestDTO, customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
                    AclConstants.OPERATION_CUSTOMER_CHANGE_IP_EXPIRY, req.getRemoteAddr(), null,
                    customers.getId().longValue(), customers.getFullName());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Ip address " + updatedValues + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update ip expiry for customer " + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update ip expiry for customer " + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_ADJUSTMENTS + "\")")
    @PostMapping("/adjustPayment/{customerId}")
    public GenericDataDTO adjustPayment(@RequestBody AdjustPaymentDTO requestDTO, @PathVariable Integer customerId, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [adjustPayment()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch adjust Payment for customer " + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            CustomAdjustPayModel customAdjustPayModel = subscriberService.adjustPayment(requestDTO, customers);
            if (customAdjustPayModel.getRecordpaymentResponseDTO() != null) {
                if (customAdjustPayModel.getRecordpaymentResponseDTO().getCreditDocument().size() > 0) {
                    List<CreditDocument> creditDocumentList = customAdjustPayModel.getRecordpaymentResponseDTO()
                            .getCreditDocument();
                    Runnable receiptRunnable = new ReceiptThread(billRunService, creditDocumentList);
                    Thread receiptThread = new Thread(receiptRunnable);
                    receiptThread.start();
                }
            }
            String updatedValues = UtilsCommon.getUpdatedDiff(customers, requestDTO);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(customAdjustPayModel.getBasicDetailsPojo());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch adjust Payment for customer " + updatedValues + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch adjust Payment for customer " + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch adjust Payment for customer " + customersRepository.findById(customerId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_CANCEL_PLAN + "\")")
    @GetMapping(value = "/getSubscriberPlanDetails/{planId}")
    public GenericDataDTO getSubscriberPlanDetails(@PathVariable Integer planId, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getSubscriberPLanDetails()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == planId) {
                genericDataDTO.setResponseMessage("Plan Id is mandatory");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Customer plan details for plan " + planId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(planId);
            if (custPlanMappping == null) {
                genericDataDTO.setResponseMessage("Customer's plan does not exist");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Customer plan details for plan " + planId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            CustPlanMapppingPojo customerPlansModel = customerMapper.mapCustPlanMapToCustPlanMapPojo(custPlanMappping,
                    new CycleAvoidingMappingContext());
            if (null == customerPlansModel) {
                genericDataDTO.setResponseMessage("Plan not found");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Customer plan details for plan " + planId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.subscriberService.getSubscriberPlanDetails(customerPlansModel));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER,
                    AclConstants.OPERATION_CUSTOMER_ROLLBACK_CHARGE, req.getRemoteAddr(), null,
                    customerPlansModel.getId().longValue(), "");
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Customer plan details for plan " + planId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception ex) {

            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Customer plan details for plan " + planId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Customer plan details for plan " + planId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_CANCEL_PLAN + "\")")
    @PostMapping(value = "/cancelPlan")
    public GenericDataDTO cancelPlan(@RequestBody CancelPlanRequestDTO requestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [cancelPlan()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(requestDTO.getPlanId());
            if (custPlanMappping == null) {
                genericDataDTO.setResponseMessage("Customer's plan does not exist");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch ancel plan for customer " + customersRepository.findById(requestDTO.getCustId()).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            CustPlanMapppingPojo customerPlansModel = customerMapper.mapCustPlanMapToCustPlanMapPojo(custPlanMappping,
                    new CycleAvoidingMappingContext());
            if (null == customerPlansModel) {
                genericDataDTO.setResponseMessage("Plan not found");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch ancel plan for customer " + customersRepository.findById(requestDTO.getCustId()).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (custPlanMappping.getEndDate() != null) {
                genericDataDTO.setResponseMessage("Plan already ended");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch ancel plan for customer " + customersRepository.findById(requestDTO.getCustId()).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.subscriberService.cancelPlan(requestDTO, customers, customerPlansModel));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_CANCEL_PLAN,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch ancel plan for customer " + customersRepository.findById(requestDTO.getCustId()).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch ancel plan for customer " + customersRepository.findById(requestDTO.getCustId()).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch ancel plan for customer " + customersRepository.findById(requestDTO.getCustId()).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_APPLY_CHARGE + "\")")
    @GetMapping(value = "/getSubscriberCharges/{custId}")
    public GenericDataDTO getSubscriberCharges(@PathVariable Integer custId, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getSubscriberCharges()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Customers customers = customersRepository.findById(custId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch subscriber charges for customer " + customersRepository.findById(custId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.subscriberService.getSubscriberCharges(customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_APPLY_CHARGE,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch subscriber charges for customer " + customersRepository.findById(custId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "subscriber charges for customer " + customersRepository.findById(custId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_PAYMENT + "\")")
    @GetMapping(value = "/getSubscriberUpdates/{custId}")
    public GenericDataDTO getSubscriberUpdates(@PathVariable Integer custId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [getSubscriberUpdates()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Customers customers = customersRepository.findById(custId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Subscriber updates for customer " + customersRepository.findById(custId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.subscriberService.getSubscriberUpdates(customers));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Subscriber updates for customer " + customersRepository.findById(custId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Subscriber updates for customer " + customersRepository.findById(custId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_CANCEL_PLAN + "\")")
    @PostMapping(value = "/activatePlan")
    public GenericDataDTO activePlan(@RequestBody ActivatePlanReqModel requestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [activePlan()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(requestDTO.getPlanId().intValue());
            if (custPlanMappping == null) {
                genericDataDTO.setResponseMessage("Customer's plan does not exist");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                //   logger.error("Unable to activate plan for customer " + requestDTO.getCustId() + " :  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            CustPlanMapppingPojo customerPlansModel = customerMapper.mapCustPlanMapToCustPlanMapPojo(custPlanMappping,
                    new CycleAvoidingMappingContext());
            if (null == customerPlansModel) {
                genericDataDTO.setResponseMessage("Plan not found");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create activatePlan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "plan with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (custPlanMappping.getEndDate() != null) {
                genericDataDTO.setResponseMessage("Plan already ended");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create activatePlan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "plan with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.subscriberService.activatePlan(requestDTO, customers, customerPlansModel));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_CANCEL_PLAN,
                    req.getRemoteAddr(), null, customers.getId().longValue(), customers.getFullName());
            //   logger.info("Activating plan for customer " + customers.getUsername() + " :  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create activatePlan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_VIEW_VOICE_DETAILS + "\")")
    @GetMapping(value = UrlConstants.GET_VOICE_PROVISION_DETAILS + "/{custId}")
    public GenericDataDTO getVoiceProvisionDetails(@PathVariable Integer custId, HttpServletRequest req) {
        String SUBMODULE = MODULE + "[getVoiceProvisionDetails]";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (custId == null) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                //   logger.error("unable to Fetch Voice provision details for customer " + custId + " :  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(custId).get();
            if (customers == null) {
                genericDataDTO.setResponseMessage("Customer not found!");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                //   logger.error("unable to fetch voiceprovisional details for customer " + customers.getUsername() + " :  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            genericDataDTO.setData(subscriberService.getVoiceProvision(custId));
            // logger.info("Fetching voice provisional details for customer " + customers.getUsername() + " :  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (Exception e) {
            e.printStackTrace();
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            //  logger.info("Unable to fetch voice provisional details for customer " + customersService.get(custId).getUsername() + " :  request: { From : {}}; Response : {{} code:{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_UPDATE_VOICE_DETAILS + "\")")
    @PostMapping(value = UrlConstants.UPDATE_VOICE_PROVISION_DETAILS)
    public GenericDataDTO updateVoiceProvisionDetails(@RequestBody VoiceProvisionReqDTO pojo, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [updateVoiceProvisionDetails] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            if (pojo.getCustId() == null) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update voice provision details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                // logger.error("unable to Update voice provisional details for customer " + pojo.getCustId() + " 	:  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(pojo.getCustId()).get();
            String updatedValues = UtilsCommon.getUpdatedDiff(customers, pojo);
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update voice provision details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                //  logger.error("unable to Update voice provisional details for customer " + updatedValues + " :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.changeVoiceProvision(pojo));
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update voice provision details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            //    logger.info("Updating voice provisional for customer " + updatedValues + " :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update voice provision details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + APIConstants.ERROR_MESSAGE + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            //  logger.error("unable to Update voice provisional details for customer" + customersService.get(pojo.getCustId()).getUsername() + " :  request: { From : {}}; Response : {{} code:{};Exception: {}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = UrlConstants.CHECK_ELIGIBLE_ADDON + "/{custId}")
    public GenericDataDTO checkEligibilityAddon(@PathVariable Integer custId, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [checkEligibilityAddon] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (custId == null) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Elegibility for customer" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(custId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Elegibility for customer" + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(subscriberService.checkEligibilityAddon(customers));
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Elegibility for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Elegibility for customer " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = UrlConstants.GET_CUSTOMER_BY_INVOICE_TYPE + "/{invoiceType}")
    public ResponseEntity<?> getByInvoiceType(@PathVariable String invoiceType, @RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Page<Customers> pageRes = null;
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomersService customerService = SpringContext.getBean(CustomersService.class);
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            pageRes = customerService.getByInvoiceType(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), "createdate",
                    paginationRequestDTO.getSortOrder(), invoiceType);
            if (null != pageRes && 0 < pageRes.getSize()) {
                response.put("customerList", pageRes.stream().map(data -> {
                    return customerMapper.domainToDTO(data, new CycleAvoidingMappingContext());
                }).collect(Collectors.toList()));
            } else
                response.put("customerList", new ArrayList<>());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer by invoice type " + invoiceType + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            //ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer by invoice type " + invoiceType + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer by invoice type " + invoiceType + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, pageRes);
    }

    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
        PAGE = Integer
                .parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
        PAGE_SIZE = Integer.parseInt(
                clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).get(0).getValue());
        SORT_BY = clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).get(0).getValue();
        SORT_ORDER = Integer.parseInt(
                clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).get(0).getValue());
        MAX_PAGE_SIZE = Integer
                .parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());

        if (null == requestDTO.getPage())
            requestDTO.setPage(PAGE);
        if (null == requestDTO.getPageSize())
            requestDTO.setPageSize(PAGE_SIZE);
        if (null == requestDTO.getSortBy())
            requestDTO.setSortBy(SORT_BY);
        if (null == requestDTO.getSortOrder())
            requestDTO.setSortOrder(SORT_ORDER);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
            requestDTO.setPageSize(MAX_PAGE_SIZE);
        return requestDTO;
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
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (responseCode.equals(APIConstants.NOT_FOUND)) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }else if (responseCode.equals(HttpStatus.PAYMENT_REQUIRED.value())) {
                return new ResponseEntity<>(response, HttpStatus.PAYMENT_REQUIRED);
            }
            else if (responseCode.equals(HttpStatus.UNAUTHORIZED.value())) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getStackTrace());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public PaginationDetails setPaginationDetails(Page page) {
        PaginationDetails pageDetails = new PaginationDetails();
        pageDetails.setTotalPages(page.getTotalPages());
        pageDetails.setTotalRecords(page.getTotalElements());
        pageDetails.setTotalRecordsPerPage(page.getNumberOfElements());
        pageDetails.setCurrentPageNumber(page.getNumber() + 1);
        return pageDetails;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_VIEW + "\")")
    @PostMapping(UrlConstants.GET_CUSTOMER_BY_INVOICE_TYPE + "/search/{invoiceType}")
    public ResponseEntity<?> searchByInvoiceType(@RequestBody PaginationRequestDTO requestDTO, @PathVariable String invoiceType,@RequestParam("mvnoId") Integer mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<Customers> customerList = null;
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            ValidationData validationData = validateSearchCriteria(requestDTO.getFilters());
            if (validationData.isValid()) {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, validationData.getMessage());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch By Invoice Type" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response, null);
            }
            CustomersService subscriberService = SpringContext.getBean(CustomersService.class);
            customerList = subscriberService.searchByInvoiceType(requestDTO.getFilters(),
                    requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(),
                    invoiceType, requestDTO.getStatus(),mvnoId);
            Integer Response = 0;
            if (customerList.isEmpty()) {
                Response = APIConstants.NULL_VALUE;
                response.put(APIConstants.MESSAGE, "No Records Found!");
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch By Invoice Type" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response, null);

            }
            if (null != customerList && 0 < customerList.getSize()) {
                response.put("customerList", customerList.getContent().stream().map(data -> {
                    try {
                        return subscriberDetailsMapper.domainToDTO(data, new CycleAvoidingMappingContext());
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()));
            } else {
                response.put("customerList", new ArrayList<>());
            }
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch By Invoice Type" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            ;
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch By Invoice Type" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (RuntimeException re) {
            re.printStackTrace();
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, re.getStackTrace());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch By Invoice Type" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + re.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, e.getStackTrace());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch By Invoice Type" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, customerList);
    }

    public ValidationData validateSearchCriteria(List<GenericSearchModel> filterList) {
        ValidationData validationData = new ValidationData();
        if (null == filterList || 0 < filterList.size()) {
            validationData.setValid(false);
            validationData.setMessage("Please Provide Search Criteria");
            return validationData;
        }
        validationData.setValid(true);
        return validationData;
    }

//	@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_EDIT + "\")")
//	@PostMapping(UrlConstants.CHANGE_CUSTOMER_DISCOUNT + "/{custId}")
//	public ResponseEntity<?> changeCustomerDiscount(@RequestBody List<ChangeDiscountDTO> requestDTO, @PathVariable Integer custId) {
//		HashMap<String, Object> response = new HashMap<>();
//		TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "Fetch");
//        MDC.put("userName", getLoggedInUser().getUsername());
//MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));//        MDC.put("spanId",traceContext.spanIdString());
//		List<ChangeDiscountDTO> result = new ArrayList<ChangeDiscountDTO>();
//		try {
//			CustomersService subscriberService = SpringContext.getBean(CustomersService.class);
//			for (int i=0; i < requestDTO.size(); i++){
//				if (requestDTO.get(i).getNewDiscount() != null) {
//					//if (requestDTO.get(i).getNewDiscount() != requestDTO.get(i).getOldDiscount()) {
//					if (!requestDTO.get(i).getNewDiscount().equals(requestDTO.get(i).getOldDiscount())){
//						ChangeDiscountDTO result1 = customersService.saveChangeCustDiscount(requestDTO.get(i), custId);
//						response.put("changeDiscount", result1);
//					}
//					if (!requestDTO.get(i).getNewDiscount().equals(requestDTO.get(i).getOldDiscount())){
////						workflowAuditService.workFlowAuditCustomerChangeDiscount(requestDTO.get(i), custId);
//					}
//				}
//			}
//			result = subscriberService.changeCustomerDiscount(requestDTO, custId);
//			response.put("discountDetails", result);
//			MDC.remove("type");
//			logger.info("Fetching Customer Discount For Cusromer "+customersService.get(custId).getUsername()+":  request: { From : {}}; Response : {{} code:{}}",getModuleNameForLog(),HttpStatus.OK,APIConstants.SUCCESS);
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.put(APIConstants.ERROR_TAG, e.getStackTrace());
//			MDC.remove("type");
//			logger.error("unable to fetch customer discount for customer "+customersService.get(custId).getUsername()+":  request: { From : {}}; Response : {{} code:{};Exception:{}}",getModuleNameForLog(),HttpStatus.EXPECTATION_FAILED,APIConstants.FAIL,e.getStackTrace());
//			return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
//		}
//	}

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_EDIT + "\")")
    @PostMapping(UrlConstants.CHANGE_CUSTOMER_DISCOUNT + "/{custId}")
    public ResponseEntity<?> changeCustomerDiscount(@RequestBody List<ChangeDiscountDTO> requestDTO, @PathVariable Integer custId) {
        HashMap<String, Object> response = new HashMap<>();
        List<ChangeDiscountDTO> result = new ArrayList<ChangeDiscountDTO>();
        try {
            CustomersService subscriberService = SpringContext.getBean(CustomersService.class);
            result = subscriberService.changeCustomerDiscount(requestDTO, custId);
            response.put("discountDetails", result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_CHANGE_DISCOUNT + "\",\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_CHANGE_DISCOUNT + "\",\"" + MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_CHANGE_DISCOUNT + "\",\"" + MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_CHANGE_DISCOUNT + "\")")
    @PostMapping(UrlConstants.CHANGE_CUSTOMER_DISCOUNT_SERVICE_LEVEL + "/{custId}")
    public ResponseEntity<?> changeCustomerDiscountServiceLevel(@RequestBody List<ChangeDiscountDTO> requestDTO, @PathVariable Integer custId) {
        HashMap<String, Object> response = new HashMap<>();
        List<ChangeDiscountDTO> result = new ArrayList<ChangeDiscountDTO>();
        try {
            Customers customer = customersService.getById(custId);
            Integer dataMvnoId = customer.getMvnoId();
            Integer currentMvnoId = subscriberService.getLoggedInMvnoId(custId);
            if(currentMvnoId==1 || dataMvnoId.equals(currentMvnoId)){
                CustomersService subscriberService = SpringContext.getBean(CustomersService.class);
                result = subscriberService.changeCustomerDiscountServiceLevel(requestDTO, custId);
                response.put("discountDetails", result);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }else{
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            }
        }catch (CustomValidationException ce){
            ce.printStackTrace();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        } catch (Exception e) {
            e.printStackTrace();
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_EDIT + "\")")
    @GetMapping(UrlConstants.DISCOUNT_CUSTOMER_DETAIL + "/{custId}")
    public ResponseEntity<?> fetchCustomerDiscount(@PathVariable Integer custId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        List<ChangeDiscountDTO> result = new ArrayList<ChangeDiscountDTO>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            CustomersService custService = SpringContext.getBean(CustomersService.class);
            result = custService.fetchCustomerDiscountDetail(custId);
            response.put("discountDetails", result);
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Discount" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put(APIConstants.ERROR_TAG, e.getStackTrace());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Discount" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    //    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_CHANGE_DISCOUNT + "\",\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_CHANGE_DISCOUNT + "\",\"" + MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_CHANGE_DISCOUNT + "\",\"" + MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_CHANGE_DISCOUNT+ "\")")
    @GetMapping(UrlConstants.DISCOUNT_CUSTOMER_DETAIL_SERVICE_LEVEL + "/{custId}")
    public ResponseEntity<?> fetchCustomerDiscountServiceLevel(@PathVariable Integer custId, @RequestParam(required = false, defaultValue = "true") boolean isExpiredRequired,@RequestParam(required = false) String custStatus, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        List<CustServiceMappingDTO> result = new ArrayList<CustServiceMappingDTO>();
        try {
            CustomersService custService = SpringContext.getBean(CustomersService.class);
            result = custService.fetchCustomerDiscountDetailServiceLevel(custId, isExpiredRequired,custStatus);
            if(result.isEmpty()){
                response.put(APIConstants.MESSAGE, "No discount found for customer");
                response.put("responseCode", APIConstants.NO_CONTENT);
                RESP_CODE = APIConstants.SUCCESS;
                return apiResponse(RESP_CODE, response, null);
            }
            response.put("discountDetails", result);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Discount For customer" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put(APIConstants.ERROR_TAG, e.getStackTrace());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Discount For customer" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_EDIT + "\")")
    @GetMapping(UrlConstants.SERVICE_WISE_PLANS + "/{custId}")
    public ResponseEntity<?> fetchServiceWisePlansForRenewalTime(@PathVariable Integer custId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            SubscriberService subscriberService = SpringContext.getBean(SubscriberService.class);
            List<ServicePlan> servicePlans = subscriberService.getServiceWisePlansForRenewalTime(custId);
            response.put("serviceWisePlans", servicePlans);
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch service Wise plans for reneval time for customer" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put(APIConstants.ERROR_TAG, e.getStackTrace());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch service Wise plans for reneval time for customer" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_EDIT + "\")")
    @GetMapping(UrlConstants.LAST_RENEWAL_PLANGROUP_ID + "/{custId}")
    public ResponseEntity<?> fetchLastRenewalPlanGroupId(@PathVariable Integer custId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            SubscriberService subscriberService = SpringContext.getBean(SubscriberService.class);
            String planGroupId = subscriberService.getLastRenewalPlanGroupId(custId);
            response.put("lastRenewalPlanGroupId", planGroupId);
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Last Renewal PlanGroup" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put(APIConstants.ERROR_TAG, e.getStackTrace());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Last Renewal PlanGroup" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

    }

    @GetMapping(UrlConstants.IS_CUSTOMER_READY_TO_TERMINATE + "/{custId}")
    public ResponseEntity<?> checkCustomerIsValidForTerminate(@PathVariable Integer custId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomersService customersService1 = SpringContext.getBean(CustomersService.class);
            boolean isValid = customersService.checkCustomerTerminationFlow(custId);
            response.put("lastRenewalPlanGroupId", isValid);
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer for termination" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put(APIConstants.ERROR_TAG, e.getStackTrace());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer for termination" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
    }


    public String getModuleNameForLog() {
        return "[SubscriberController]";
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_SERVICE_CREATE + "\",\"" + MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_SERVICE_CREATE + "\",\"" + MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_SERVICE_CREATE + "\",\""
            + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_SERVICE_CREATE + "\")")
    @PostMapping("/addNewService")
    public ResponseEntity<?> addNewService(@Valid @RequestBody CustomersPojo pojo,
                                           @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req, String serviceFor)
            throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        FlagDTO flagDTO = new FlagDTO();

        try {
            if ((getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID && null != pojo.getPartnerid()
                    && !pojo.getPartnerid().equals(CommonConstants.DEFAULT_PARTNER_ID))) {
                if (requestFrom.equals("pw") && !getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficient(pojo)) {
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    response.put(APIConstants.ERROR_TAG, "Partner has Insufficient balance!");
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Service" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "Tax with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return apiResponse(RESP_CODE, response, null);
                } else if (requestFrom.equals("pw") && getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficientForLCO(pojo)) {
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    response.put(APIConstants.ERROR_TAG, "Partner has Insufficient balance!");
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Service" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "Tax with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
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

            Set<CustomerChargeHistory> customerChargeHistoryList = customerChargeHistoryRepo.findByCustPlanMapppingIdIn(pojo.getPlanMappingList().stream().map(i -> i.getId()).collect(Collectors.toList()));
            List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
            customerServiceMappingList.add(customerServiceMappingRepository.findById(pojo.getPlanMappingList().get(0).getCustServiceMappingId()).get());
           if (pojo.getStatus().equalsIgnoreCase("NewActivation")){
               flagDTO.setIsCafNewService(true);
           }
            createDataSharedService.sendChangePlanForAllMicroService(Collections.singletonList(custPlanMappingRepository.findById(pojo.getCustPackageId())), null, customerChargeHistoryList, "isCAFCustomer", null, customerServiceMappingList, null, null, "", null,null, null,false,false,null,null,null,flagDTO);
//                    debitDocService.createInvoiceAfterStartBilling(customers);

            //Save customer detail with time base policy
            customersService.CustTimeBasePolicyDetailsSend(pojo);
            Customers customer = customerMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext());

            Customers customers1 = customersService.getById(pojo.getId());
            customers1.setNextBillDate(pojo.getNextBillDate());
            customers1.setLastBillDate(pojo.getLastBillDate());
            if (pojo.getLeadId() != null) {
                //  LeadMaster leadMaster = leadMasterRepository.findById(pojo.getLeadId()).orElse(null);
                pojo.setLeadSource(leadMasterRepository.getLeadSourceNameFromLeadId(pojo.getLeadId()));
            }
            LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers1, customers1.getNextBillDate());
            if(nextQuotaReset != null) {
                customers1.setNextQuotaResetDate(nextQuotaReset);
            } else {
                customers1.setNextQuotaResetDate(LocalDate.now());
            }
//            Customers customers2=new Customers(customers1);
            customersRepository.save(customers1);

//            dbrService.addDbrForNewService(pojo.getPlanMappingList(), customer);
            try {
                if ((pojo.getCusttype() != null & !"".equals(pojo.getCusttype())
                        && pojo.getCusttype().equalsIgnoreCase("Prepaid"))
                        && (recordPaymentPojo == null
                        || (recordPaymentPojo != null && recordPaymentPojo.getAmount() <= 0))) {
//					ApplicationLogger.logger.error(MODULE + "Invoice can not generate due to 0 payment amount");
                    //  logger.error("Invoice can not generate due to 0 payment amount :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL);
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
                        customers1.getOverChargeList().forEach(data -> custChargeIdList.add(data.getId()));
                        Runnable chargeRunnable = new ChargeThread(customers1.getId(), custChargeIdList,
                                customersService, 0L, "", null);
                        Thread billchargeThread = new Thread(chargeRunnable);
                        billchargeThread.start();
                    }

                }

            } catch (Exception e) {
//				ApplicationLogger.logger.error(MODULE + e.getMessage(), e);
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Service" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                e.printStackTrace();
            }


            try {

                boolean invoice = false;

                List<CustPlanMapppingPojo> custPlanList = pojo.getPlanMappingList();
                for (int i = 0; i < custPlanList.size(); i++) {
                    CustPlanMapppingPojo custPlan = custPlanList.get(i);
//					pojo.setCustPackageId(custPlan.getId());
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
//                        Runnable chargeRunnable1 = new InvoiceCreationThread(pojo, customersService, null, false, null, CommonConstants.INVOICE_TYPE.NEW_SERVICE);
//                        Thread billchargeThread1 = new Thread(chargeRunnable1);
//                        billchargeThread1.start();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_ADD,
                    req.getRemoteAddr(), null, pojo.getId().longValue(), "");
            response.put("customer", pojo);
            RESP_CODE = APIConstants.SUCCESS;
            // logger.info("Service added Succefully :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), HttpStatus.OK, APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
//			ApplicationLogger.logger.error(MODULE + ce.getMessage(), ce);
//	//		logger.error("Unable to add new service :  request: { From : {}}; Response : {{} code:{};Exception :{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL, ce.getStackTrace());
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            // logger.error("Unable to add new service :  request: { From : {}}; Response : {{} code:{};Exception :{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL, exception.getStackTrace());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, exception.getMessage());
        } catch (Exception ex) {
//			ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
            //  logger.error("Unable to add new service :  request: { From : {}}; Response : {{} code:{};Exception :{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL, ex.getStackTrace());
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }


    public List<Long> getBUIdsFromCurrentStaff() {
        List<java.lang.Long> mvnoIds = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }
//
//    @PostMapping("" +
//            "/withdraw/payment")
//    public ResponseEntity<?> createRecordPayment(@Valid @RequestBody RecordPaymentPojo pojo) throws Exception {
//        MDC.put("type", "Crete");
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
//            creditDocService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
//            //  if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
//            //  throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
//            pojo = creditDocService.withDrawal(pojo, true, false, false);
//            //	workflowAuditService.workFlowAuditPayment(pojo, getLoggedInUserId());
//            response.put("recordpayment", pojo);
//            RESP_CODE = APIConstants.SUCCESS;
//            logger.info("createRecordPayment  for " + customersService.get(pojo.getCustomerid()).getUsername() + ":  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE, response);
//        } catch (CustomValidationException ce) {
//            //ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//            logger.error("Unable to createRecordPayment  for " + pojo.getCustomerid() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
//        } catch (Exception ex) {
//            //ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
//            ex.printStackTrace();
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to createRecordPayment  for " + pojo.getCustomerid() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return apiResponse(RESP_CODE, response, null);
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\"" + AclConstants.OPERATION_CUSTOMER_CHANGE_PLAN + "\")")
    @PostMapping(value = "/getStartAndEndDate")
    public GenericDataDTO getStartAndEndDate(@RequestBody ChangePlanRequestDTOList requestDTOs, @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [changePlan()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (requestDTOs != null) {
                String number = String.valueOf(UtilsCommon.gen());
                for (ChangePlanRequestDTO requestDTO : requestDTOs.getChangePlanRequestDTOList()) {
                    if (null == requestDTO.getCustId()) {
                        genericDataDTO.setResponseMessage("Please provide customer id!");
                        RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                        genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                        logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Start And End Date" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                        return genericDataDTO;
                    }
                    Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
                    String updatedValues = UtilsCommon.getUpdatedDiff(customers, requestDTO);
                    if (null == customers) {
                        genericDataDTO.setResponseMessage("Customer not found!");
                        RESP_CODE = APIConstants.NOT_FOUND;
                        genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                        logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Start And End Date" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                        return genericDataDTO;
                    }

                    if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON)) {
                        List<CustomerPlansModel> currentPlanList = this.subscriberService.getActivePlanList(customers.getId(), false);
                        if (currentPlanList.size() <= 0) {
                            genericDataDTO.setResponseMessage("Subscriber must have any active plan for AddOn Purchase");
                            RESP_CODE = APIConstants.NOT_FOUND;
                            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Start And End Date" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                            return genericDataDTO;
                        }
                    }

                    if (subscriberService.getCustomerPlanList(customers.getId(), false).size() <= 0) {
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_NEW);
                    } else {
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_RENEW);
                    }
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Approval Progress for lead" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    if (requestDTO.getPlanGroupId() != null && requestDTO.getPlanGroupId() != 0 && requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW)) {
                        if (requestDTO.getNewPlanList() != null) {
                            //Double maxValidity = requestDTO.getPlanList().stream().map(PostpaidPlan::getValidity).max(Double::compare).get();
                            CustomChangePlanDTO customChangePlanDTO = null;
                        }

                    }

                    HashMap<String, Object> response = subscriberService.getStartEndAndExpirydate(requestDTO, requestFrom);
                    genericDataDTO.setData(response);
                }
            }
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Approval Progress for lead" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Approval Progress for lead" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_CHANGE_PLAN + "\"," +
//            "\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_CHANGE_PLAN + "\"," +
//            "\"" + MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_CHANGE_PLAN + "\"," +
//            "\"" + MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_CHANGE_PLAN + "\")")
    @PostMapping(value = "/deactivatePlanInBulk")
    public ResponseEntity<?> deactivatePlanInBulk(@RequestBody DeactivatePlanReqDTOList requestDTOs, @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req) {
        MDC.put("type", "Create");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Thread invoiceThread = null;
        try {
            Integer custId = null;
            Optional<Integer> custIdOptional = requestDTOs.getDeactivatePlanReqDTOS().stream().filter(DeactivatePlanReqDTO::getIsParent).map(DeactivatePlanReqDTO::getCustId).findFirst();
            List<Integer> custIds = requestDTOs.getDeactivatePlanReqDTOS().stream().filter(i -> !i.getIsParent()).map(DeactivatePlanReqDTO::getCustId).collect(Collectors.toList());
            Set<Customers> customersforInvoice = new HashSet<>();
            if (custIds != null && custIds.size() > 0) {
                customersforInvoice.addAll(customersRepository.findAllById(custIds));
            }
            if (custIdOptional.isPresent()) {
                custId = custIdOptional.get();
            } else {
                custIdOptional = requestDTOs.getDeactivatePlanReqDTOS().stream().map(DeactivatePlanReqDTO::getCustId).findFirst();
                if (custIdOptional.isPresent()) {
                    custId = custIdOptional.get();
                }
            }
            Customers customers = customersRepository.findById(custId).get();
            Integer currentMvnoId = subscriberService.getLoggedInMvnoId(customers.getId());
            Integer dataMvnoId = customers.getMvnoId();
            String token = req.getHeader("Authorization");
            boolean isPlanWithin24Hour = subscriberService.isChangePlanBuyWithin24Hour(customers.getId());
            logger.info("********* isPlanWithin24Hour : " + isPlanWithin24Hour + "*********");
            if (!customers.getStatus().equalsIgnoreCase(SubscriberConstants.ACTIVATION_PENDING) &&
                    !customers.getStatus().equalsIgnoreCase(SubscriberConstants.NEW_ACTIVATION) &&
                    !customers.getStatus().equalsIgnoreCase(SubscriberConstants.TERMINATE) &&
                    !isPlanWithin24Hour
            ) {
                logger.info("********* customer status : " + customers.getStatus() + "  with customer Id : " + customers.getId() + "*********");
                List<DebitDocument> debitDocuments = revenueClient.getDebitDocumentByCustId(customers.getId(), token);
                DebitDocument debitDocument = subscriberService.getLatestDebitDocument(debitDocuments);
                if (debitDocument != null) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Change plan not allowed as First settle the invoice with customer!", null);
                }
            }

            if(currentMvnoId==1 || dataMvnoId.equals(currentMvnoId)){

                if (custId == null) {
                    throw new CustomValidationException(417, "Customer id can not be null!", null);
                }

                if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                    if (requestFrom.equals("pw") && !getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficient(requestDTOs.getDeactivatePlanReqDTOS().get(0))) {
                        RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                        response.put(APIConstants.ERROR_TAG, "Partner has Insufficient balance!");
                        logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivate Plan In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                        return apiResponse(RESP_CODE, response, null);
                    }

                    if (requestFrom.equals("pw") && getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficientForLCO(requestDTOs.getDeactivatePlanReqDTOS().get(0))) {
                        RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                        response.put(APIConstants.ERROR_TAG, "Partner has Insufficient balance!");
                        logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivate Plan In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                        return apiResponse(RESP_CODE, response, null);
                    }
                }
                //  Customers customers = customersService.get(requestDTOs.getCustId());
    //            Customers customers = customersService.get(custId);
                customersforInvoice.add(customers);
                DebitDocument debitDocuments = debitDocRepository.findTopByCustomerAndBillrunstatus(customers.getId(),"VOID");

                if (requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate()!=null && (debitDocuments!=null || !requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate().equalsIgnoreCase("Next Bill Date")) && customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)){
                    requestDTOs.getDeactivatePlanReqDTOS().get(0).setChangePlanDate("Today");
                }
                DeactivatePlanReqDTOList result = new DeactivatePlanReqDTOList();
                if (customers.getStatus().equalsIgnoreCase(SubscriberConstants.ACTIVE) || customers.getStatus().equalsIgnoreCase(SubscriberConstants.NEW_ACTIVATION) || customers.getStatus().equalsIgnoreCase(SubscriberConstants.ACTIVATION_PENDING) ) {
                    result = subscriberService.deActivatePlanInList(requestDTOs);
                }
                else{
                    throw  new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),"Customer status must be in (Active , NewActivation)",null);
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

                String paymentOwner = requestDTOs.getDeactivatePlanReqDTOS().stream().map(DeactivatePlanReqDTO::getPaymentOwner).findFirst().orElse(null);
                Integer paymentOwnerId = requestDTOs.getDeactivatePlanReqDTOS().stream().map(DeactivatePlanReqDTO::getPaymentOwnerId).findFirst().orElse(null);
    //            debitDocService.createInvoice(customers, null, 200, new HashSet<Integer>(debitDocIds), null, paymentOwner, paymentOwnerId, false, CommonConstants.INVOICE_TYPE.CHANGE_PLAN);

                /*  this is for post paid changePLan next bill date */
                boolean changePlanNextBillDate = false;
                if(requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate()!=null && requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate().equalsIgnoreCase("Next Bill Date")  && customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)){
                    changePlanNextBillDate = true;
                }
                FlagDTO flagDTO = new FlagDTO();
                Double discount = requestDTOs.getDeactivatePlanReqDTOS().get(0).getDeactivatePlanReqModels().get(0).getDiscount();
                if (discount == null ){
                    flagDTO.setDiscount(false);
                }else {
                    flagDTO.setDiscount(true);
                }
                List<Integer> oldMappingIds = requestDTOs.getDeactivatePlanReqDTOS()
                        .stream()
                        .filter(dto -> dto.getDeactivatePlanReqModels() != null)
                        .flatMap(dto -> dto.getDeactivatePlanReqModels().stream())
                        .map(DeactivatePlanReqModel::getOldCustPlanMappingId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                flagDTO.setOldCustPlanMappingIds(oldMappingIds);

                if(requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate() != null && requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate().equalsIgnoreCase("Next Bill Date")){
                    flagDTO.setIsFuturePlan(true);
                }

                if (customersforInvoice.size() > 1) {
                    Integer parentId = custIdOptional.get();
                    List<Integer> childIds = custIds;
                    childIds.removeIf(i -> i.equals(parentId));
                    debitDocService.createInvoice(customersforInvoice, CommonConstants.INVOICE_TYPE.CHANGE_PLAN, parentId, childIds, requestDTOs.getRecordPayment(), null,changePlanNextBillDate,false,null,null);
                } else {
                    debitDocService.createInvoice(customers, CommonConstants.INVOICE_TYPE.CHANGE_PLAN, "", requestDTOs.getRecordPayment(),null, null,changePlanNextBillDate,false,null,null,flagDTO);
                }
                for (DeactivatePlanReqDTO reqDTO : requestDTOs.getDeactivatePlanReqDTOS()) {
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
                for (DeactivatePlanReqDTO reqDTO : requestDTOs.getDeactivatePlanReqDTOS()) {
                    if (reqDTO.getDeactivatePlanReqModels().get(0).isBillToOrg()) {
                        Thread.sleep(2000);
                        subscriberService.orgCustInvoiceForChangePlan(customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()), reqDTO);
                    }
                }
                if(Objects.nonNull(requestDTOs.getSkipQuotaUpdate())) {
                    if (customersforInvoice.size() > 1) {
                        for (Customers costomer : customersforInvoice) {
                       subscriberService.skipQuotaUpdate(costomer.getId(),requestDTOs.getSkipQuotaUpdate());
                        }
                    } else {
                        subscriberService.skipQuotaUpdate(customers.getId(),requestDTOs.getSkipQuotaUpdate());
                    }
                }

                LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers, customers.getNextBillDate());
                if(nextQuotaReset != null) {
                    customers.setNextQuotaResetDate(nextQuotaReset);
                } else {
                    customers.setNextQuotaResetDate(LocalDate.now());
                }
                Customers updatedCustomer = customersRepository.save(customers);

                List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustomerId(customers.getId());
                List<CustomerServiceMapping> customerServiceMappinList = customerServiceMappingRepository.findAllByCustId(customers.getId());
                createDataSharedService.updateCustomerEntityForAllMicroServce(updatedCustomer, custPlanMapppingList, customerServiceMappinList);

                List<String> oldValue = new ArrayList<>();
                if (!CollectionUtils.isEmpty(custPlanMapppingList)) {
                    List<Integer> planIds = custPlanMapppingList.stream().map(CustPlanMappping::getPlanId).collect(Collectors.toList());
                    List<String> existingPlanNames = postpaidPlanRepo.findAllByIdIn(planIds).stream().map(PostpaidPlan::getName).filter(Objects::nonNull).collect(Collectors.toList());
                    oldValue.addAll(existingPlanNames);
                }

                String oldPaymentOwner = requestDTOs.getDeactivatePlanReqDTOS().stream().map(DeactivatePlanReqDTO::getPaymentOwner).filter(Objects::nonNull).findFirst().orElse(null);
                if (oldPaymentOwner != null) {
                    oldValue.add(oldPaymentOwner);
                }

                List<String> newValue = new ArrayList<>();
                if (!CollectionUtils.isEmpty(result.getDeactivatePlanReqDTOS())) {
                    for (DeactivatePlanReqDTO dto : result.getDeactivatePlanReqDTOS()) {
                        List<String> planNames = dto.getDeactivatePlanReqModels().stream().map(model -> postpaidPlanRepo.findById(model.getNewPlanId())
                                        .map(PostpaidPlan::getName).orElse(null)).collect(Collectors.toList());
                        newValue.addAll(planNames);
                        if (dto.getPaymentOwner() != null) {
                            newValue.add(dto.getPaymentOwner());
                        }
                    }
                }
                String remark = UpdateDiffFinder.generateListDiffRemark(oldValue, newValue,"Change Plan");
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CHANGE_PLAN_ADD, req.getRemoteAddr(), remark, updatedCustomer.getId().longValue(), updatedCustomer.getFullName()
                );
                response.put("deActivateResponse", result);
                RESP_CODE = APIConstants.SUCCESS;
                //  logger.info("createRecordPayment  for " + custId + ":  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE, response);
            }else{
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.ERROR_TAG, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Change Plan " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + Constants.MVNO_DELETE_UPDATE_ERROR_MSG + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            RESP_CODE = ex.getErrCode();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            //logger.error("Unable to change plan  for " + requestDTOs.getCustId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            //logger.error("Unable to createRecordPayment  for " + requestDTOs.getCustId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_CHANGE_PLAN + "\"," +
            "\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_CHANGE_PLAN + "\"," +
            "\"" + MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_CHANGE_PLAN + "\"," +
            "\"" + MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_CHANGE_PLAN + "\")")
    @PostMapping(value = "/deactivatePlan")
    public ResponseEntity<?> deactivatePlan(@RequestBody DeactivatePlanReqDTO requestDTOs, @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req) {
        MDC.put("type", "Create");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Thread invoiceThread = null;
        try {
            Integer customerId = requestDTOs.getCustId();
            Customers customers = customersRepository.findById(customerId).get();
            Integer currentMvnoId = subscriberService.getLoggedInMvnoId(customerId);
            Integer dataMvnoId = customers.getMvnoId();
            if(currentMvnoId==1 || dataMvnoId.equals(currentMvnoId)){
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
            //  Customers customers = customersService.get(requestDTOs.getCustId());
//            Customers customers = customersService.get(requestDTOs.getCustId());
            DeactivatePlanReqDTO result = new DeactivatePlanReqDTO();
            Random rnd = new Random();
            int renewalId = rnd.nextInt(999999);
            if (customers.getStatus().equalsIgnoreCase(SubscriberConstants.ACTIVE)) {
                result = subscriberService.deActivatePlan(requestDTOs, renewalId,false,null);
            }
            if (customers.getStatus().equalsIgnoreCase(SubscriberConstants.NEW_ACTIVATION)) {
                result = subscriberService.deActivatePlanForCAFCustomer(requestDTOs);
            }
            LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers, customers.getNextBillDate());
            if(nextQuotaReset != null) {
                customers.setNextQuotaResetDate(nextQuotaReset);
            } else {
                customers.setNextQuotaResetDate(LocalDate.now());
            }
            customersRepository.save(customers);
            if (requestDTOs.getDeactivatePlanReqModels().get(0).isBillToOrg()) {
                subscriberService.orgCustInvoiceForChangePlan(customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()), requestDTOs);
            }
            response.put("deActivateResponse", result);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivatePlan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }else{
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.ERROR_TAG, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivatePlan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + Constants.MVNO_DELETE_UPDATE_ERROR_MSG + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException ex) {
            //ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ex.printStackTrace();
            RESP_CODE = ex.getErrCode();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivatePlan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivatePlan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_DELETE_TRIAL_PLANS + "\",\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_DELETE_TRIAL_PLANS + "\")")
    @PostMapping(value = "/cancel/trailplan")
    public ResponseEntity<?> cancelTrailPlan(@RequestBody TrialPlanDTO requestDTOs, @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req) {
        MDC.put("type", "Crete");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomChangePlanDTO result = subscriberService.cancelTrialPlan(requestDTOs, req);
            response.put("trialPlanResponse", result);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create trailplan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ex) {
            //ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ex.printStackTrace();
            RESP_CODE = ex.getErrCode();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create trailplan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create trailplan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_EXTEND_TRIAL_PLANS + "\",\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_EXTEND_TRIAL_PLANS + "\")")
    @PostMapping(value = "/extendTrailPlan")
    public ResponseEntity<?> extendTrailPlan(@RequestBody TrialPlanDTO requestDTOs, @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req) {
        MDC.put("type", "Crete");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomChangePlanDTO result = subscriberService.extendTrailPlan(requestDTOs, req);
            response.put("trialPlanResponse", result);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create extend Trail Plan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ex) {
            //ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ex.printStackTrace();
            RESP_CODE = ex.getErrCode();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create extend Trail Plan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create extend Trail Plan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_SUBSCRIBE_TRIAL_PLANS + "\",\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_SUBSCRIBE_TRIAL_PLANS + "\")")
    @PostMapping(value = "/trailToNormalPlan")
    public ResponseEntity<?> covertTrailPlanToNormal(@RequestBody TrialPlanDTO requestDTOs, @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req) {
        MDC.put("type", "Crete");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Thread invoiceThread = null;
        try {
            CustomChangePlanDTO result = subscriberService.covertTrailPlanToNormal(requestDTOs, req);
            response.put("trialPlanResponse", result);
            CustomersBasicDetailsPojo basicDetailsPojo = result.getCustomersBasicDetailsPojo();
            RESP_CODE = APIConstants.SUCCESS;
//            try {
//                Customers customer = customersService.get(basicDetailsPojo.getId());
//                if (customer.getStatus().equalsIgnoreCase("Active")) {
//                    if (requestDTOs.getPlanGroupId() != null) {
//                        debitDocService.createInvoice(customer, null, 200, null, "", null, null, false, CommonConstants.INVOICE_TYPE.TRIAL_TO_NORMAL);
//                    } else {
//                        customer.setBillRunCustPackageRelId(result.getCustpackagerelid());
//                        Runnable invoiceRunnable = new InvoiceThread(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), customer, customersService, "", null, null);
//                        invoiceThread = new Thread(invoiceRunnable);
//                        invoiceThread.start();
//                    }
//                }
//            } catch (Exception e) {
//                logger.info("Fetching All Hierarchy :  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), RESP_CODE, result);
//                //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
//                e.printStackTrace();
//            }
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create trail To Normal Plan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ex) {
            //ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ex.printStackTrace();
            RESP_CODE = ex.getErrCode();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create trail To Normal Plan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create trail To Normal Plan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_PLANS_PROMIS_TO_PAY + "\",\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_PLANS_PROMIS_TO_PAY + "\")")
    @PostMapping(value = "/promiseToPayInBulk")
    public GenericDataDTO addPromiseToPay(@RequestBody PromiseToPayPojoInBulk request, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [getBasicSubscriberDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == request.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch promise To Pay In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }

            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            List<CustServiceMappingDTO> customersMapppingPojos = subscriberService.addPromiseToPayInBulk(request);
            genericDataDTO.setDataList(customersMapppingPojos);
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch promise To Pay In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch promise To Pay In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ////ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch promise To Pay In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_SERVICE_TERMINATION + "\",\""
            + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_SERVICE_TERMINATION + "\")")
    @PostMapping(value = "/terminateServiceInBulk")
    public ResponseEntity<?> terminateServiceInBulk(@RequestBody TerminateServiceInBulkPojo terminateServiceInBulkPojo, HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Delete");
        HashMap<String, Object> response = new HashMap<>();
        try {
            customersService.terminateServiceInBulk(terminateServiceInBulkPojo);
            response.put("Success", APIConstants.SUCCESS);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return apiResponse(RESP_CODE, response, null);
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
//            + AclConstants.OPERATION_CUSTOMER_CANCEL_PLAN + "\")")
    @PostMapping(value = "/deleteService/{planId}")
    public ResponseEntity<?> removeService(@PathVariable Integer planId, @RequestParam Integer custId, @RequestParam Integer planMapId, @RequestParam(required = false) Integer reasonId, HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Delete");
        HashMap<String, Object> response = new HashMap<>();
        try {
            PostpaidPlanService postpaidPlanService = SpringContext.getBean(PostpaidPlanService.class);
            PostpaidPlan plan = postpaidPlanService.getEntityForUpdateAndDelete(planId,custChargeService.getMvnoIdFromCurrentStaff(custId));
            if (plan != null) {
                PostpaidPlanPojo pojo = postpaidPlanService.convertPostpaidPlanModelToPostpaidPlanPojo(plan);
                postpaidPlanService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                customersService.removeService(planMapId);
                response.put("Success", APIConstants.SUCCESS);
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return apiResponse(RESP_CODE, response, null);
    }


    @GetMapping(value = "/getPlanByCustService/{customerId}")
    public GenericDataDTO getPlanByCustService(@PathVariable Integer customerId, @RequestParam(required = false) String status, @RequestParam(required = false, defaultValue = "false") Boolean isAllRequired, @RequestParam(required = false, defaultValue = "false") Boolean isNotChangePlan, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;

        String SUBMODULE = MODULE + " [getPlanByCustService()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch get Active service list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + "Access denined for update operation " + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch get Active service list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + "No Customer data found" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching get plan list from user" + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            genericDataDTO.setDataList(customersService.getPlanByCustServiceList(customerId, status, isAllRequired, isNotChangePlan));
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch get Active service list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }



    @GetMapping(value = "/getCustServiceByCustId/{customerId}")
    public GenericDataDTO getCustServiceByCustId(@PathVariable Integer customerId, @RequestParam(required = false) String status, @RequestParam(required = false, defaultValue = "false") Boolean isAllRequired, @RequestParam(required = false, defaultValue = "false") Boolean isNotChangePlan, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;

        String SUBMODULE = MODULE + " [getCustServiceByCustId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch get Active service list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + "Access denined for update operation " + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch get Active service list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + "No Customer data found" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching get plan list from user" + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            genericDataDTO.setDataList(customersService.getCustServiceListByCustId(customerId, status, isAllRequired, isNotChangePlan));
            genericDataDTO.setResponseCode(RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch get Active service list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }


    @GetMapping(value = "/getPlanByCustServiceByUserName/{userName}")
    public GenericDataDTO getPlanByCustService(@PathVariable String userName, @RequestParam(required = false) String status, @RequestParam(required = false, defaultValue = "false") Boolean isAllRequired, @RequestParam(required = false, defaultValue = "false") Boolean isNotChangePlan, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;

        String SUBMODULE = MODULE + " [getPlanByCustService()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == userName) {
                genericDataDTO.setResponseMessage("Please provide userName!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch get Active service list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + "Access denined for update operation " + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Integer customerId = customersRepository.findCustIdByUserName(userName);
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch get Active service list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + "Access denined for update operation " + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch get Active service list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + "No Customer data found" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching get plan list from user" + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            genericDataDTO.setDataList(customersService.getPlanByCustServiceList(customerId, status, isAllRequired, isNotChangePlan));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch get Active service list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_SERVICE_HOLD_RESUME + "\",\""
            + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_SERVICE_HOLD_RESUME + "\")")
    @PostMapping("/holdServiceInBulk")
    public ResponseEntity<?> holdServiceInBulk(@Valid @RequestBody DeactivatePlanReqDTOList planReqDTOList, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {

            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
            List<CustPlanMapppingPojo> result = subscriberService.holdServiceInBulk(planReqDTOList);
            //dbrService.dbrHoldOnServicePause(result);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_ADD,
                    req.getRemoteAddr(), null, planReqDTOList.getCustId(), "");
            response.put("customer", result);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create hold service bulk record " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (CustomValidationException ce) {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create hold service bulk record" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (RuntimeException exception) {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create hold service bulk record" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, exception.getMessage());
        } catch (Exception ex) {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create hold service bulk record" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }

    @PostMapping("/resumeServiceInBulk")
    public ResponseEntity<?> resumeServiceInBulk(@Valid @RequestBody DeactivatePlanReqDTOList planReqDTOList, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
            List<CustPlanMapppingPojo> result = subscriberService.resumeServiceInBulk(planReqDTOList);
            //dbrService.dbrResumeOnServiceResume(result);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_ADD,
                    req.getRemoteAddr(), null, planReqDTOList.getCustId().longValue(), "");
            response.put("customer", result);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Service In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (CustomValidationException ce) {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Service In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (RuntimeException exception) {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Service In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, exception.getMessage());
        } catch (Exception ex) {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Service In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }


    @PostMapping("/nickName")
    public ResponseEntity<?> nickname(@Valid @RequestParam Integer custServiceMappingId, String name, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
            CustomerServiceMapping result = subscriberService.saveNickName(custServiceMappingId, name);
            response.put("NickName", result);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create nickname" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create nickname" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (RuntimeException exception) {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create nickname" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, exception.getMessage());
        } catch (Exception ex) {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create nickname" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }

    public int getLoggedInUserPartnerId() {
        int partnerId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
            partnerId = -1;
        }
        return partnerId;
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
        }
        return loggedInUser;
    }

    @PutMapping(value = "/changeInvoiceformat/{custPlanMapppingId}")
    public GenericDataDTO changeInvoiceFormat(@PathVariable Integer custPlanMapppingId, @Valid @RequestParam(name = "status", required = true) String Status, @Valid @RequestParam(name = "invoicetype", required = true) String invoicetype, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getPlanByCustService()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            if (null == custPlanMapppingId) {
                genericDataDTO.setResponseMessage("Please provide custPlanMapppingId id!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Invoice Format" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Optional<CustomerServiceMapping> customerServiceMapping = customerServiceMappingRepository.findById(custPlanMapppingId);
            if (null == customerServiceMapping) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Invoice Format" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Invoice Format" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return customersService.changeInvoiceFormat(custPlanMapppingId, Status, invoicetype);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Invoice Format" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_PLANS_EXTEND_VALIDITY + "\",\""
            + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_PLANS_EXTEND_VALIDITY + "\")")
    @PostMapping(value = "/extendPlanValidityInBulk")
    public GenericDataDTO extendPlanValidityInBulk(@RequestBody ExtendPlanValidityInBulk extendPlanValidityInBulk, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [getPlanByCustService()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<String> oldValue = extractOldExtendValidityData(extendPlanValidityInBulk.getExtendPlanValidity());
            List<CustPlanMapppingPojo> customerPlansModelList = subscriberService.extendServiceValidityInBulk(extendPlanValidityInBulk);
            List<String> newValue = new ArrayList<>();
            newValue.add(String.valueOf(extendPlanValidityInBulk.getExtendPlanValidity().get(0).getDownStartDate()));
            newValue.add(String.valueOf(extendPlanValidityInBulk.getExtendPlanValidity().get(0).getDownEndDate()));
            newValue.add(extendPlanValidityInBulk.getExtendPlanValidity().get(0).getExtend_validity_remarks());
            genericDataDTO.setDataList(customerPlansModelList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            String fieldName = getLoggedInUser().getFullName();
            String remark = UpdateDiffFinder.generateListDiffRemark(oldValue, newValue ,fieldName);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_EXTEND_VALIDITY, req.getRemoteAddr(), remark, customerPlansModelList.get(0).getCustomer().getId().longValue(),customerPlansModelList.get(0).getCustomer().getFirstname());
        } catch (CustomValidationException ex) {
            ApplicationLogger.logger.error(SUBMODULE + Arrays.toString(ex.getStackTrace()), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch extend Plan Validity In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + Arrays.toString(ex.getStackTrace()), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch extend Plan Validity In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
    private List<String> extractOldExtendValidityData(List<ExtendPlanValidity> extendList) {
        List<String> oldValue = new ArrayList<>();
        Integer custPlanId = extendList.stream().map(ExtendPlanValidity::getCustPlanMapppingId).findFirst().orElse(null);
        if (custPlanId == null) return oldValue;
        List<CustPlanExtendValidityMapping> existingMappings = custPlanExtendValidityMappingRepository.findAllByCustServiceMappingId(custPlanId);
        if (!existingMappings.isEmpty()) {
            existingMappings.sort(Comparator.comparing(CustPlanExtendValidityMapping::getDownTimeStartDate).reversed());
            CustPlanExtendValidityMapping latestData = existingMappings.get(0);
            oldValue.add(String.valueOf(latestData.getDownTimeStartDate()));
            oldValue.add(String.valueOf(latestData.getDownTimeExpiryDate()));
            oldValue.add(latestData.getExtendValidityremarks());
        }
        return oldValue;
    }

    @GetMapping(value = "/changediscountaudit/{custpackid}")
    public GenericDataDTO getchangeplanaudit(@PathVariable Integer custpackid) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (custpackid != null) {
                genericDataDTO.setDataList(subscriberService.getdiscoutAudit(custpackid));
            } else {
                genericDataDTO.setDataList(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        genericDataDTO.setResponseMessage("Success");
        genericDataDTO.setResponseCode(200);
        return genericDataDTO;
    }

    @GetMapping(value = "/approveCustomerServiceTermination")
    public GenericDataDTO approveCustomerServiceTermination(@RequestParam(name = "customerServiceMappingId") Integer customerServiceMappingId, @RequestParam(name = "isApproveRequest") Boolean isApproveRequest, @RequestParam(name = "remarks") String remarks) {
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Delete");
        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = customersService.approveCustomerServiceTermiantion(customerServiceMappingId, isApproveRequest, remarks);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/getBillableCust/{customerId}")
    public GenericDataDTO getBillableCust(@PathVariable Integer customerId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getPlanByCustService()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            if (null == customerId) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Billable Customer" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Customers customers = customersRepository.findById(customerId).get();
            if (null == customers) {
                genericDataDTO.setResponseMessage("Customer not found!");
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Billable Customer" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Billable Customer" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            Customers billableCust = customersService.getBillableCust(customerId);
            genericDataDTO.setData(subscriberMapper.domainToDTO(billableCust, new CycleAvoidingMappingContext()));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch parentablecustomersList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> Key_Extractor) {
        Map<Object, Boolean> Employee_Map = new ConcurrentHashMap<>();
        return t -> Employee_Map.putIfAbsent(Key_Extractor.apply(t), Boolean.TRUE) == null;
    }

    @GetMapping(value = "/getCircuitDetailsByCustServiceMapId/{custServiceMapId}")
    public GenericDataDTO getCircuitDetailsByCustServiceMapId(@PathVariable Integer custServiceMapId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setData(subscriberService.getCircuitDetailsByCustServiceMapId(custServiceMapId));
        return genericDataDTO;
    }

    @PostMapping(value = "/servicestatusAudit/{custpackid}")
    public GenericDataDTO getserviceStatusAudit(@PathVariable Integer custpackid, @RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setData(subscriberService.getserviceStatusAudit(custpackid, paginationRequestDTO.getFilters(), paginationRequestDTO));

            return genericDataDTO;

            // return subscriberService.getserviceStatusAudit(custpackid,paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
            //logger.info("Fetching Branch list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch service status audit" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        return genericDataDTO;
    }


    @GetMapping(value = "/approveCustomerServiceAdd")
    public GenericDataDTO approveCustomerServiceAdd(@RequestParam(name = "customerServiceMappingId") Integer customerServiceMappingId, @RequestParam(name = "isApproveRequest") Boolean isApproveRequest, @RequestParam(name = "remarks") String remarks,HttpServletRequest request) {
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Delete");
        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            String token = request.getHeader("Authorization");
            genericDataDTO = customersService.approveCustomerServiceAdd(customerServiceMappingId, isApproveRequest, remarks,token);

        } catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            //throw new RuntimeException(e);
        } catch (Exception e) {
            genericDataDTO.setResponseCode(417);
            genericDataDTO.setResponseMessage(e.getMessage());
            //throw new RuntimeException(e);
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/approveBulkCustomerServiceAdd")
    public GenericDataDTO approveCustomerServiceAdd(@RequestParam(name = "customerServiceMappingId") List<Integer> customerServiceMappingId, @RequestParam(name = "isApproveRequest") Boolean isApproveRequest, @RequestParam(name = "remarks") String remarks) {
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Delete");
        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = customersService.approveBulkCustomerServiceAdd(customerServiceMappingId, isApproveRequest, remarks);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_SERVICE_STOP + "\",\""
            + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_SERVICE_STOP + "\")")
    @PostMapping("/stopServiceInBulk")
    public ResponseEntity<?> stopServiceInBulk(@Valid @RequestBody DeactivatePlanReqDTOList planReqDTOList, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {

            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
            List<CustPlanMapppingPojo> result = subscriberService.stopServiceInBulk(planReqDTOList);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_ADD,
                    req.getRemoteAddr(), null, planReqDTOList.getCustId(), "");
            response.put("customer", result);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Service stopped" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Service stopped" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (RuntimeException exception) {
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Service stopped" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, exception.getMessage());
        } catch (Exception ex) {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Service stopped" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }

    @GetMapping(value = "/getSerialNumber")
    public GenericDataDTO getAllSerialNumberByService(@RequestParam(name = "custId") Integer custId, @RequestParam(name = "serviceIds") List<Integer> serviceIds, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            List<CustPlanMappingDropdownPojo> serialnumberList = subscriberService.getSerialNumber(custId, serviceIds);
            genericDataDTO.setDataList(serialnumberList);
            genericDataDTO.setResponseCode(200);
            genericDataDTO.setResponseMessage("Success");
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all searial number by device : " + custId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
    @GetMapping(value = "/activateServiceFromHold")
    public GenericDataDTO activateServiceFromHold(@RequestParam(name = "serviceId") Integer servicemappingId, HttpServletRequest req){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try{
            List<CustPlanMappping> custPlanMapppings=custPlanMappingRepository.findAllByCustServiceMappingId(servicemappingId);
           if(custPlanMapppings.size()>0) {
               List<Integer> cprIds = custPlanMapppings.stream().filter(list->list.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE )).map(mappping -> mappping.getId() ).collect(Collectors.toList());
               customerDocDetailsService.changeStatusDisableToActive(cprIds);
           }
            genericDataDTO.setResponseCode(200);
            genericDataDTO.setResponseMessage("Success");
        }catch (Exception ex){
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "activateServiceFromHold: " + servicemappingId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return  genericDataDTO;
    }

    @PostMapping(value = "/autoApprovalPayment")
    public GenericDataDTO autoApprovalPayment(@RequestBody AutoRenewOrAddonPlanRequestDto renewOrAddonPlanRequestDto, @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req) throws Exception {
        String SUBMODULE = MODULE + " [autoApprovalPayment()] ";

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        Boolean flag = true;
        Thread invoiceThread = null;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        ChangePlanRequestDTOList requestDTOs = subscriberService.convert(renewOrAddonPlanRequestDto);
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
                if (custIds != null && custIds.size() > 0) {
                    customersforInvoice.addAll(customersRepository.findAllById(custIds));
                }
                if (custIdOptional.isPresent()) {
                    custId = custIdOptional.get();
                } else {
                    custIdOptional = requestDTOs.getChangePlanRequestDTOList().stream().map(ChangePlanRequestDTO::getCustId).findFirst();
                    if (custIdOptional.isPresent()) {
                        custId = custIdOptional.get();
                    }
                }

                Customers parentCustomers = customersRepository.findById(custId).get();
                customersforInvoice.add(parentCustomers);
                List<CustChargeOverrideDTO> custChargeDetailsList = requestDTOs.getCustChargeDetailsList();
                List<CustChargeOverrideDTO> custChargeOverrideDTOS = new ArrayList<>();
                for (ChangePlanRequestDTO requestDTO : requestDTOs.getChangePlanRequestDTOList()) {
                    if (null == requestDTO.getCustId()) {
                        genericDataDTO.setResponseMessage("Please provide customer id!");
                        RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                        logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customersRepository.findById(custId).get().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                        return genericDataDTO;
                    }
                    if (requestDTO.getPaymentOwner() == null) {
                        requestDTO.setPaymentOwner("");
                    }
                    Customers customers = customersRepository.findById(requestDTO.getCustId()).get();
                    String updatedValues = UtilsCommon.getUpdatedDiff(customers, requestDTO);
                    if (null == customers) {
                        genericDataDTO.setResponseMessage("Customer not found!");
                        RESP_CODE = APIConstants.NOT_FOUND;
                        genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                        logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                        return genericDataDTO;
                    }

                    if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON)) {
                        List<CustomerPlansModel> currentPlanList = this.subscriberService.getActivePlanList(customers.getId(), false);
                        if (currentPlanList.size() <= 0) {
                            genericDataDTO.setResponseMessage("Subscriber must have any active plan for AddOn Purchase");
                            RESP_CODE = APIConstants.NOT_FOUND;
                            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                            return genericDataDTO;
                        }
                    }

                    if (subscriberService.getCustomerPlanList(customers.getId(), false).size() <= 0) {
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_NEW);
                    } else {
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_RENEW);
                    }
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Plan For customer " + updatedValues + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
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
                                customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers, false, 0.0, requestFrom, null, number,requestDTOs.getDateOverrideDtos(),null);
                                Thread.sleep(1000);//Added bcuz sometimes in billing engine get conflict data.., need solution for this
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
                                    List<CustChargeOverrideDTO> custChargeOverrideDTOs = custChargeDetailsList.stream()
                                            .filter(custCharge -> custCharge.getParentId().equals(customers.getId()))
                                            .collect(Collectors.toList());

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

                        genericDataDTO.setData(custBasicDetailsPojoList);
                    } else {
                        if(Objects.nonNull(requestDTOs.getIsTriggerCoaDm())) {
                            requestDTO.setIsTriggerCoaDm(requestDTOs.getIsTriggerCoaDm());
                        }
                        CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers, false, 0.0, requestFrom, null, number, requestDTOs.getDateOverrideDtos(),null);
                        if (customChangePlanDTO.getRecordpaymentResponseDTO() != null) {
                            if (customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument().size() > 0 && !customers.getIsinvoicestop()) {
                                List<CreditDocument> creditDocumentList = customChangePlanDTO.getRecordpaymentResponseDTO()
                                        .getCreditDocument();
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
                        genericDataDTO.setData(basicDetailsPojo);
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
                                auditChangePlan = customers.getFullName() + "change plan type is" + requestDTO.getPurchaseType()
                                        + "..and plan is :- " + postpaidPlan.getDisplayName();
                            }
                        }
                    }
                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW_PLANS, req.getRemoteAddr(), auditChangePlan, customers.getId().longValue(), customers.getFullName());
                }
                try {
                    if (requestDTOs.getChangePlanRequestDTOList().get(0).getPurchaseType().equalsIgnoreCase("Addon")) {
                        if (customersforInvoice.size() > 1) {
                            Integer parentId = custIdOptional.get();
                            custIds.removeIf(i -> i.equals(parentId));
                            List<Integer> childIds = custIds;
                            debitDocService.createInvoice(customersforInvoice, Constants.ADD_ON, parentId, childIds, requestDTOs.getRecordPayment(), null,false,requestDTOs.getChangePlanRequestDTOList().get(0).getIsAutoPaymentRequired(),requestDTOs.getChangePlanRequestDTOList().get(0).getCreditDocumentPaymentPojoList(),null);
                        } else {
                            debitDocService.createInvoice(parentCustomers, Constants.ADD_ON, "", requestDTOs.getRecordPayment(),null, null,false,requestDTOs.getChangePlanRequestDTOList().get(0).getIsAutoPaymentRequired(),requestDTOs.getChangePlanRequestDTOList().get(0).getCreditDocumentPaymentPojoList(),null,null);
                        }
                    } else {
                        if (customersforInvoice.size() > 1) {
                            Integer parentId = custIdOptional.get();
                            custIds.removeIf(i -> i.equals(parentId));
                            List<Integer> childIds = custIds;
                            debitDocService.createInvoice(customersforInvoice, Constants.RENEW, parentId, childIds, requestDTOs.getRecordPayment(), null,false,requestDTOs.getChangePlanRequestDTOList().get(0).getIsAutoPaymentRequired(),requestDTOs.getChangePlanRequestDTOList().get(0).getCreditDocumentPaymentPojoList(),null);
                        } else {
                            debitDocService.createInvoice(parentCustomers, Constants.RENEW, "", requestDTOs.getRecordPayment(),null, null,false,requestDTOs.getChangePlanRequestDTOList().get(0).getIsAutoPaymentRequired(),requestDTOs.getChangePlanRequestDTOList().get(0).getCreditDocumentPaymentPojoList(),null,null);
                        }
                    }
                } catch (Exception e) {
                    logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for user " + parentCustomers.getId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for user " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for user " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PostMapping("/updateAllPlanStatuses")
    public ResponseEntity<?> updateAllPlanStatuses(@RequestBody UpdateRenewalStatusRequest planList) {
        try {
            System.out.println("call uodate plan status");
            Map<String, Object> response = subscriberService.updateAllRenewalStatuses(planList);

            if ("SUCCESS".equalsIgnoreCase((String) response.get("status"))) {
                return ResponseEntity.ok(response); // HTTP 200
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // HTTP 400 with message
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update plan list");
        }
    }

}
