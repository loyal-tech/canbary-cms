package com.adopt.apigw.modules.Invoices.controller;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.MenuConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.CustomerMapper;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.model.postpaid.TrialDebitDocument;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.subscriber.service.ChargeThread;
import com.adopt.apigw.pojo.api.StartDateEndDatePojo;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CancelRegenerateInvoice;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.CustPlanMappingRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.DebitDocService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.INVOICE_CONTROLLER_URL)
public class InvoiceController {

    private static String MODULE = " [DebitDocController] ";

    @Autowired
    MessageSender messageSender;
    @Autowired
    DebitDocService debitDocService;

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomersService customersService;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private StaffUserRepository staffUserRepository;

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private Tracer tracer;

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_VOID_INVOICE_ALL + "\",\"" + AclConstants.OPERATION_VOID_INVOICE_VIEW + "\")")
//    @GetMapping(value = "/voidInvoice")
//    public GenericDataDTO voidInvoice(@RequestParam(name = "invoiceId") Integer invoiceId, @RequestParam(name = "invoiceCancelRemarks", required = false) String invoiceCancelRemarks) {
//        Integer RESP_CODE = APIConstants.FAIL;
//        MDC.put("type", "Fetch");
////        HashMap<String, Object> response = new HashMap<>();
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            logger.info("Fetching All Invoice With id " + invoiceId + "  : Response : {{}}", genericDataDTO.getResponseCode());
//            genericDataDTO = debitDocService.voidInvoice(invoiceId, invoiceCancelRemarks);
//            DebitDocument debitDocument = debitDocService.get(invoiceId);
//
//            if(debitDocument.getPostpaidPlan()!=null) {
//                String remark = "Void invoice for invoice no: " + debitDocument.getDocnumber() + " For Plan: " + debitDocument.getPostpaidPlan().getName() + " remark: " + invoiceCancelRemarks;
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_VOID_INVOICE_MODULE, AclConstants.OPERATION_VOID_INVOICE_ALL,
//                        null, remark, Long.valueOf(debitDocument.getCustomer().getId()), debitDocService.getLoggedInUser().getUsername());
//            }
//        } catch (CustomValidationException ce) {
//            //   ApplicationLogger.logger.error("[InvoiceController]" + ce.getMessage(), ce);
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to Fetch Invoice With id " + invoiceId + "  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getStackTrace());
//        } catch (Exception e) {
//            //     ApplicationLogger.logger.error("[InvoiceController]" + e.getMessage(), e);
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to Fetch Invoice With id " + invoiceId + " :   Response : {{}};Error :{} ;Exception:{}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    @GetMapping(value = "/getTaxDetailsOfInvoice")
    public GenericDataDTO getTaxDetailsOfInvoice(@RequestParam(name = "invoiceId") Integer invoiceId) {
        Integer RESP_CODE = APIConstants.SUCCESS;
        MDC.put("type", "Fetch");
//        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            logger.info("Fetching All Invoice With id " + invoiceId + "  : Response : {{}}", genericDataDTO.getResponseCode());
            genericDataDTO = debitDocService.getTaxDetailsOfInvoice(invoiceId);
        } catch (CustomValidationException ce) {
            //   ApplicationLogger.logger.error("[InvoiceController]" + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error("Unable to Fetch Invoice With id " + invoiceId + "  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getStackTrace());
        } catch (Exception e) {
            //     ApplicationLogger.logger.error("[InvoiceController]" + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error("Unable to Fetch Invoice With id " + invoiceId + " :   Response : {{}};Error :{} ;Exception:{}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    public String getModuleNameForLog() {
        return "[InvoiceController]";
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BILLING_ALL + "\",\"" + AclConstants.OPERATION_BILLING_VIEW + "\")")
    @RequestMapping(value = {"/stop/billing/{custId}"}, method = RequestMethod.POST)
    public GenericDataDTO stopBilling(@PathVariable Integer custId, @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req) {
        MDC.put("type", "Crete");
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Customers customers = debitDocService.stopBilling(custId);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()));
            RESP_CODE = APIConstants.SUCCESS;
            logger.info("stop billing for " + customers.getUsername() + ":  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE, genericDataDTO.getResponseMessage());
        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("unable to Update voice provisional details for customer id " + custId + " :  request: { From : {}}; Response : {{} code:{};Exception: {}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BILLING_ALL + "\",\"" + AclConstants.OPERATION_BILLING_VIEW + "\")")
    @RequestMapping(value = {"/start/billing/{custId}"}, method = RequestMethod.POST)
    public GenericDataDTO startBilling(@PathVariable Integer custId, @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req) {
        MDC.put("type", "Crete");
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Customers customers = debitDocService.startBilling(custId);
            genericDataDTO.setResponseMessage("SUCCESS!");
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()));
            RESP_CODE = APIConstants.SUCCESS;
            debitDocService.createInvoiceAfterStartBilling(customers);
            logger.info("stop billing for " + customers.getUsername() + ":  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE, genericDataDTO.getResponseMessage());
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(e.getErrCode());
            logger.error("unable to Update voice provisional details for customer id " + custId + " :  request: { From : {}}; Response : {{} code:{};Exception: {}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
        } catch (Exception e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("unable to Update voice provisional details for customer id " + custId + " :  request: { From : {}}; Response : {{} code:{};Exception: {}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping("/approveDebitDoc")
    public GenericDataDTO approveDebitDoc(@RequestParam(name = "invoiceId") Long invoiceId, @RequestParam(name = "isApproveRequest") boolean isApproveRequest, @RequestParam(name = "remark") String remark, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) {
        MDC.put("type", "Crete");
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO=debitDocService.approveDebitDoc(invoiceId, isApproveRequest, remark,mvnoId);

        } catch (CustomValidationException e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("unable to Update approveDebitDoc request: { From : {}}; Response : {{} code:{};Exception: {}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
        } catch (Exception e) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("unable to  Update approveDebitDoc   request: { From : {}}; Response : {{} code:{};Exception: {}}", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping("/genarateIspInvoice")
    public GenericDataDTO generateIspInvoice(@RequestBody StartDateEndDatePojo startDateEndDatePojo, HttpServletRequest request) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        logger.info("*********cronjobtimeforispautobill**************");
        try {
            Boolean isInvoiceGenerated = debitDocService.ispInvoiceGenerate(startDateEndDatePojo.getStartDate());
            if (isInvoiceGenerated) {
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setResponseMessage("Invoice created for Isp from start date: " + startDateEndDatePojo.getStartDate() + " and End Date: " + startDateEndDatePojo.getEndDate());
                logger.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "ISP invoice generation from Start Date: " + startDateEndDatePojo.getStartDate() + " End Date: " + startDateEndDatePojo.getEndDate() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }else {
                genericDataDTO.setResponseCode(APIConstants.NO_CONTENT_FOUND);
                genericDataDTO.setResponseMessage("No ISP found on this dates");
                logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"ISP invoice generation Failed from Start Date: " +  startDateEndDatePojo.getStartDate() +" End Date: " + startDateEndDatePojo.getEndDate() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (Exception e) {
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"ISP invoice generation Failed from Start Date: " +  startDateEndDatePojo.getStartDate() +" End Date: " + startDateEndDatePojo.getEndDate() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getStackTrace().toString(), e);
        }
        return loggedInUser;
    }

}
