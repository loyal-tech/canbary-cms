package com.adopt.apigw.modules.customerDocDetails.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.*;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.ValidationData;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.exceptions.FileNotCreatedException;
import com.adopt.apigw.core.utillity.fileUtillity.FileUtility;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDeleteModel;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
import com.adopt.apigw.pojo.api.CustomerCafAssignmentPojo;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.CUST_DOC)
public class CustomerDocDetailsController extends ExBaseAbstractController<CustomerDocDetailsDTO> {
    private static String MODULE = " [CustomerDocDetailsController] ";
    @Autowired
    AuditLogService auditLogService;
    @Autowired
    private CustomerDocDetailsService customerDocDetailsService;

    @Autowired
    private CustomersService customersService;
    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private Tracer tracer;

    private String PATH;
    private static final Logger log = LoggerFactory.getLogger(CustomerDocDetailsController.class);

    public CustomerDocDetailsController(CustomerDocDetailsService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[CustomerDocDetailsController]";
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_DELETE_DOC + "\",\"" + MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_DELETE_DOC + "\",\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_DELETE_DOC + "\"" +
            ",\"" + MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_DELETE_DOC + "\")")
    @PostMapping(value = "/deleteCustDoc", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO deleteCustDoc(@RequestBody CustomerDocDeleteModel docDeleteModel, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String name = customersRepository.findById(docDeleteModel.getCustId()).get().getFullName();
        String SUBMODULE = getModuleNameForLog() + " [deleteCustDoc()] ";
        try {
            if (null == docDeleteModel) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide DocumentList!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete customer document" + LogConstants.LOG_BY_NAME + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (docDeleteModel.getDocIdList() == null || 0 == docDeleteModel.getDocIdList().size()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide DocumentList!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete customer document" + LogConstants.LOG_BY_NAME + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (docDeleteModel.getCustId() == null) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Customer!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete customer document" + LogConstants.LOG_BY_NAME + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (SubscriberConstants.DELETED_SUCCESSFULLY.equalsIgnoreCase(customerDocDetailsService.deleteDocument
                    (docDeleteModel.getDocIdList(), docDeleteModel.getCustId()))) {
                genericDataDTO.setResponseMessage(SubscriberConstants.DELETED_SUCCESSFULLY);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete customer document" + LogConstants.LOG_BY_NAME + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("Problem in deletion!");
                genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete customer document for id: " + docDeleteModel.getCustId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                //   ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                return genericDataDTO;
            }
            //  ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Failed to delete data. Please try after some time");
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete customer document" + LogConstants.LOG_BY_NAME + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_UPLOAD_DOC + "\",\"" + MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_UPLOAD_DOC + "\",\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_UPLOAD_DOC + "\"" +
//            ",\"" + MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_UPLOAD_DOC + "\")")
    @PostMapping(value = UrlConstants.UPLOAD_DOC, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO uploadDocForCustomer(@RequestParam String docDetailsList
            , @RequestParam(value = "file", required = false) MultipartFile[] file, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer respCode = APIConstants.FAIL;
        String SUBMODULE = getModuleNameForLog() + " [updateDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String docNumber = " ";
        try {
            if (null != docDetailsList) {

                List<CustomerDocDetailsDTO> customerDocDetailsList = new ObjectMapper().registerModule(new JavaTimeModule())
                        .readValue(docDetailsList, new TypeReference<List<CustomerDocDetailsDTO>>() {
                        });

                if (null == customerDocDetailsList || 0 == customerDocDetailsList.size()) {
                    genericDataDTO.setResponseMessage("Please provide document details!");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    respCode = HttpStatus.NOT_ACCEPTABLE.value();
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customer document [" + docNumber +" ] " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "customer document with same name already exist" + LogConstants.LOG_STATUS_CODE + respCode);
                    return genericDataDTO;
                }

                if (null != customerDocDetailsList && 0 < customerDocDetailsList.size()) {
                    docNumber = customerDocDetailsList.stream().map(CustomerDocDetailsDTO::getDocumentNumber).collect(Collectors.toList()).toString();
                    genericDataDTO.setResponseMessage("Documents uploaded successfully.");
                        genericDataDTO.setDataList(customerDocDetailsService.uploadDocument(customerDocDetailsList, file,getMvnoIdFromCurrentStaff(customerDocDetailsList.get(0).getCustId())));
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    respCode = APIConstants.SUCCESS;
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customer document [" + docNumber +" ] " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTUMER_DOC, AclConstants.OPERATION_UPLOADDOC, req.getRemoteAddr(), null, Long.valueOf(customerDocDetailsList.get(0).getCustId()),"");
                    return genericDataDTO;
                }
            }
            genericDataDTO.setResponseMessage("Please provide document details!");
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customer document [" + docNumber +" ] " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "customer document with same name already exist" + LogConstants.LOG_STATUS_CODE + respCode);
            return genericDataDTO;
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(e.getMessage());
                respCode = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customer document " + docNumber + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + respCode);
                return genericDataDTO;
            }
            if (e instanceof FileNotCreatedException) {
                genericDataDTO.setResponseCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
                genericDataDTO.setResponseMessage("Failed to create file: " + e.getMessage());
                respCode = HttpStatus.UNPROCESSABLE_ENTITY.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                        LogConstants.REQUEST_FOR + "create customer document " + docNumber +
                        LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                        LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                        LogConstants.LOG_ERROR + e.getMessage() +
                        LogConstants.LOG_STATUS_CODE + respCode);
                return genericDataDTO;
            }
            if (e instanceof RuntimeException) {
                genericDataDTO.setResponseMessage(e.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customer document " + docNumber + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "customer document with same name already exist" + LogConstants.LOG_STATUS_CODE + respCode);
                return genericDataDTO;
            }
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customer document " + docNumber + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_UPLOAD_DOC + "\",\"" + MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_UPLOAD_DOC + "\",\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_UPLOAD_DOC + "\"" + ",\"" + MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_UPLOAD_DOC + "\")")
    @PostMapping(value = UrlConstants.UPLOAD_DOC_ONLINE)
    public GenericDataDTO uploadDocOnlineForCustomer(@RequestBody CustomerDocDetailsDTO customerDocDetailsDTO, @RequestParam Boolean isUpdate, HttpServletRequest req) {
        String SUBMODULE = getModuleNameForLog() + " [uploadDocOnlineForCustomer()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String name = customersRepository.findById(customerDocDetailsDTO.getCustId()).get().getFullName();
        try {
//            if (customerDocDetailsDTO.getDocumentNumber()!= null && customerDocDetailsDTO.getDocumentNumber().contains("*")) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Document number Invalid, It's not contain any *", null);
//            }
            Integer currentMvnoId = customersService.getLoggedInMvnoId(customerDocDetailsDTO.getCustId());
            Customers customer = customersService.getById(customerDocDetailsDTO.getCustId());
            Integer dataMvnoId = customer.getMvnoId();
            if(currentMvnoId==1 || dataMvnoId.equals(currentMvnoId)){

            if (null != customerDocDetailsDTO) {
                if ((customerDocDetailsDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAR_CARD)) || (customerDocDetailsDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD)) || (customerDocDetailsDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.GST_NUMBER))) {
                    genericDataDTO.setData(customerDocDetailsService.uploadDocumentOnline(customerDocDetailsDTO, isUpdate));
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage("Documents uploaded successfully.");
                    RESP_CODE = APIConstants.SUCCESS;
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customer online document" + LogConstants.LOG_BY_NAME +name+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTUMER_DOC, AclConstants.OPERATION_UPLOADDOC, req.getRemoteAddr(), null, Long.valueOf(customerDocDetailsDTO.getCustId()),name);
                    return genericDataDTO;
                } else {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    String message = null;
                    if (customerDocDetailsDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAR_CARD)) {
                        message = "Adhar card number should be 12 digit long";
                    } else if (customerDocDetailsDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD)) {
                        message = "Pan card number should be 10 digit long";
                    } else {
                        message = "GST number Should be between 3 to 15 digits long";
                    }
                    genericDataDTO.setResponseMessage(message);
                    return genericDataDTO;
                }

            }
            genericDataDTO.setResponseMessage("Please provide document details!");
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customer online document" + LogConstants.LOG_BY_NAME + name+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;
            }else{
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            }
        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(e.getMessage());
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customer online document" + LogConstants.LOG_BY_NAME +name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (e instanceof RuntimeException) {
                genericDataDTO.setResponseMessage(e.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customer online document" + LogConstants.LOG_BY_NAME + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customer online document" + LogConstants.LOG_BY_NAME + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = UrlConstants.DOC_BY_CUSTOMER + "/{custId}")
    public GenericDataDTO getDocByCustomer(@PathVariable Integer custId,  HttpServletRequest req) {
//        String name = customersService.get(custId).getFullName();
        String name = customersRepository.findUsernameById(custId);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = getModuleNameForLog() + " [getDocByCustomer()] ";
        try {
            if (null == custId) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Customer");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch document for customer " + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch document for customer " + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return GenericDataDTO.getGenericDataDTO(customerDocDetailsService.findDocsByCustomerId(custId));
        } catch (Exception ex) {
            //   ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch document for customer " + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = UrlConstants.DOC_BY_STATUS_AND_CUSTOMER + "/{custId}")
    public GenericDataDTO isCustDocPending(@PathVariable Integer custId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String name = customersRepository.findUsernameById(custId);
        String SUBMODULE = getModuleNameForLog() + " [isCustDocPending()] ";
        try {
            if (null == custId) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Customer");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer pending document for " + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer pending document for " + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            genericDataDTO.setData(customerDocDetailsService.isCustDocPending(custId));
        } catch (Exception ex) {
            //   ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer pending document for " + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = UrlConstants.APPROVED_CUST_DOC + "/{docId}/{status}")
    public GenericDataDTO approveCustDoc(@PathVariable Long docId, @PathVariable String status, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = getModuleNameForLog() + " [approveCustDoc()] ";
        try {
            if (null == docId) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Customer");
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update customer approve document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update customer approve document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            genericDataDTO.setData(customerDocDetailsService.approveCustDoc(docId, status));
        } catch (Exception ex) {
            //   ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update customer approve document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_DELETE_DOC + "\",\"" + MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_DELETE_DOC + "\",\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_DELETE_DOC + "\"" +
            ",\"" + MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_DELETE_DOC + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTUMER_DOC_ALL + "\",\"" + AclConstants.OPERATION_CUSTUMER_DOC_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody CustomerDocDetailsDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            String name = customersRepository.findById(entityDTO.getCustId()).get().getFullName();
            boolean flag = customerDocDetailsService.deleteVerification(entityDTO.getCustId());

            Customers customers = customersRepository.findById(entityDTO.getCustId()).orElse(null);
            // TODO: pass mvnoID manually 6/5/2025
            if (customers == null || !(getMvnoIdFromCurrentStaff(entityDTO.getCustId()) == 1 || getMvnoIdFromCurrentStaff(entityDTO.getCustId()).intValue() == customers.getMvnoId().intValue() && (customers.getMvnoId() == 1 || customersService.getBUIdsFromCurrentStaff().size() == 0 || customersService.getBUIdsFromCurrentStaff().contains(customers.getBuId())))) {
                //throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
                dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                dataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
                RESP_CODE = APIConstants.EXPECTATION_FAILED;
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete customer document" + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return dataDTO;
            }


            if (flag) {
                customerDocDetailsService.deleteEntity(entityDTO);
                CustomerDocDetailsDTO customerDocDetailsDTO = (CustomerDocDetailsDTO) dataDTO.getData();
                RESP_CODE = APIConstants.SUCCESS;
                dataDTO.setResponseCode(RESP_CODE);
                dataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                if (customerDocDetailsDTO != null) {
                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTUMER_DOC,
                            AclConstants.OPERATION_CUSTUMER_DOC_DELETE, req.getRemoteAddr(), null, customerDocDetailsDTO.getDocId(), customerDocDetailsDTO.getDocType());
                    RESP_CODE = APIConstants.SUCCESS;
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete customer document" + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                }
            } else {
                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                dataDTO.setResponseMessage(DeleteContant.CUSTUMER_DOC_EXITS);
                RESP_CODE = HttpStatus.METHOD_NOT_ALLOWED.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete customer document" + name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    @PutMapping("/approveUploadCustomerDoc")
    public GenericDataDTO assignUploadCustomerDoc(@Valid @RequestParam Long docId, @RequestParam String remarks, @RequestParam Boolean isApproveRequest, @RequestBody CustomerCafAssignmentPojo customerCafAssignmentPojo,
                                                  HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {

            CustomerDocDetailsService customerDocDetailsService = SpringContext.getBean(CustomerDocDetailsService.class);
            if (docId != null) {
                dataDTO = customerDocDetailsService.updateUploadCustomerDocAssignment(docId, isApproveRequest, remarks);

            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update approve customer document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update approve customer document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
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
    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO update(@RequestParam String docDetailsList
            , @RequestParam(value = "file", required = false) MultipartFile[] file, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId
    ) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            CustomerDocDetailsDTO entityDTO = new ObjectMapper().registerModule(new JavaTimeModule())
                    .readValue(docDetailsList, new TypeReference<CustomerDocDetailsDTO>() {
                    });

            ValidationData validation = validateUpdate(entityDTO);
            if (!validation.isValid()) {
                RESP_CODE=HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Entity " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }

            CustomerDocDetailsDTO dtoData = customerDocDetailsService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey(),mvnoId);
            String updatedValues = UtilsCommon.getUpdatedDiff(dtoData,entityDTO);
            genericDataDTO.setData(customerDocDetailsService.updateEntity(entityDTO, file));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setTotalRecords(1);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTUMER_DOC,AclConstants.OPERATION_CUSTUMER_DOC_EDIT, req.getRemoteAddr(), null, entityDTO.getDocId(), entityDTO.getDocType());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Entity " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                RESP_CODE=HttpStatus.NOT_FOUND.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Entity " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +APIConstants.MESSAGE+ex.getMessage()+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else if (ex instanceof CustomValidationException){
                RESP_CODE=HttpStatus.NOT_FOUND.value();
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Entity " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +APIConstants.MESSAGE+ex.getMessage()+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                RESP_CODE=HttpStatus.NOT_FOUND.value();
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Entity " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +APIConstants.MESSAGE+ex.getMessage()+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
}
