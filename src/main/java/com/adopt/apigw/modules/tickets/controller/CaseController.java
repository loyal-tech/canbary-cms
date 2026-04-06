package com.adopt.apigw.modules.tickets.controller;
//
//import com.adopt.apigw.constants.CaseConstants;
//import com.adopt.apigw.constants.Constants;
//import com.adopt.apigw.constants.UrlConstants;
//import com.adopt.apigw.core.controller.ExBaseAbstractController2;
//import com.adopt.apigw.core.dto.*;
//import com.adopt.apigw.core.exceptions.DataNotFoundException;
//import com.adopt.apigw.core.utillity.log.ApplicationLogger;
//import com.adopt.apigw.exception.CustomValidationException;
//import com.adopt.apigw.model.common.ClientService;
//import com.adopt.apigw.model.common.Customers;
//import com.adopt.apigw.modules.ServiceArea.SubscriberMapper;
//import com.adopt.apigw.modules.Voucher.module.APIResponseController;
//import com.adopt.apigw.modules.acl.constants.AclConstants;
//import com.adopt.apigw.modules.auditLog.service.AuditLogService;
//import com.adopt.apigw.modules.tickets.domain.CaseDocDetails;
//import com.adopt.apigw.modules.tickets.mapper.CaseMapper;
//import com.adopt.apigw.modules.tickets.model.CaseDTO;
//import com.adopt.apigw.modules.tickets.model.CaseUpdateDTO;
//import com.adopt.apigw.modules.tickets.service.CaseDocDetailsService;
//import com.adopt.apigw.modules.tickets.service.CaseService;
//import com.adopt.apigw.modules.tickets.service.CaseUpdateService;
//import com.adopt.apigw.modules.tickets.service.LiveCustomerNetworkDetailsService;
//import com.adopt.apigw.pojo.api.CustomersPojo;
//import com.adopt.apigw.pojo.api.TicketETRPojo;
//import com.adopt.apigw.service.common.ClientServiceSrv;
//import com.adopt.apigw.service.common.CustomersService;
//import com.adopt.apigw.service.common.FileSystemService;
//import com.adopt.apigw.spring.LoggedInUser;
//import com.adopt.apigw.spring.SpringContext;
//import com.adopt.apigw.utils.APIConstants;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.pdf.PdfWriter;
//import io.swagger.annotations.ApiOperation;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.validation.Valid;
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//import static com.adopt.apigw.service.postpaid.TaxService.MODULE;
//
//@RestController
//@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.CASE)
public class CaseController {
//
//    @Autowired
//    CaseMapper caseMapper;
//    @Autowired
//    private CaseService caseService;
//    @Autowired
//    private CaseUpdateService caseUpdateService;
//    @Autowired
//    private LiveCustomerNetworkDetailsService liveCustomerNetworkDetailsService;
//    @Autowired
//    private CustomersService customersService;
//
//    @Autowired
//    private SubscriberMapper subscriberMapper;
//    @Autowired
//    private AuditLogService auditLogService;
//
//    @Autowired
//    CaseDocDetailsService caseDocDetailsService;
//
//    @Autowired
//    ClientServiceSrv clientServiceSrv;
//
//    @Autowired
//    private APIResponseController responseController;
//
//    public CaseController(CaseService service) {
//        super(service);
//    }
//
//    private static final Logger logger = LoggerFactory.getLogger(CaseController.class);
//
//    public LoggedInUser getLoggedInUser() {
//        LoggedInUser loggedInUser = null;
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
//            }
//        } catch (Exception e) {
//        }
//        return loggedInUser;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_ADD + "\")")
//    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public GenericDataDTO save(@Valid @RequestParam String entityDTO, @RequestParam(required = false, value = "file") List<MultipartFile> file, HttpServletRequest req) {
//
//        MDC.put("type", "crate");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        CaseDTO caseDTO;
//        try {
//            caseDTO = new ObjectMapper().registerModule(new JavaTimeModule())
//                    .readValue(entityDTO, new TypeReference<CaseDTO>() {
//                    });
//            if (null == caseDTO.getCaseType()) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Please Select CaseType!");
//                logger.error("Unable to create new Cases with name " + caseDTO.getCaseTitle() + "  :  request: { From : {}}; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            }
//            if (null != caseDTO.getCustomersId()) {
//                Customers customers = customersService.get(caseDTO.getCustomersId());
//                if (null != customers && null != customers.getPartner()) {
//                    caseDTO.setPartnerid(customers.getPartner().getId());
//                }
//            }
//
//            if (getMvnoIdFromCurrentStaff() != null) {
//                caseDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
//                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
//            }
//            if (getMvnoIdFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() == 1) {
//                caseDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
//            }
//
//            if(getLoggedInUser().getLco())
//                caseDTO.setLcoId(getLoggedInUser().getPartnerId());
//            else
//                caseDTO.setLcoId(null);
//
//            caseDTO = caseService.saveEntity(caseDTO, file);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE,
//                    AclConstants.OPERATION_CASE_ADD, req.getRemoteAddr(), null, caseDTO.getCaseId(), caseDTO.getCustomerName());
//            genericDataDTO.setData(entityDTO);
//            genericDataDTO.setTotalRecords(1);
//            logger.info("reating case With  title " + caseDTO.getCaseTitle() + "  is sucessfull:  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            return genericDataDTO;
//        } catch (CustomValidationException e) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("Unable to create case with title   :  request: { From : {}}; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
//
//            return genericDataDTO;
//        } catch (Exception ex) {
//            //ApplicationLogger.logger.error(getModuleNameForLog() + " [save] " + ex.getStackTrace(), ex);
//            if (ex instanceof DataNotFoundException) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                genericDataDTO.setResponseMessage(ex.getMessage());
//                logger.error("Unable to create case with title  :  request: { From : {}}; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
//                return genericDataDTO;
//            }
//            if (ex instanceof RuntimeException) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(ex.getMessage());
//                logger.error("Unable to create case with title  :  request: { From : {}}; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
//
//                return genericDataDTO;
//            }
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to create case with title   :  request: { From : {}}; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
//
//        }
//        MDC.remove("type");
//
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @PostMapping(path = UrlConstants.CASE_UPDATE_DETAILS, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public GenericDataDTO updateDetails(@RequestParam String caseUpdate, @RequestParam(value = "file", required = false) List<MultipartFile> file, HttpServletRequest req) {
//
//        MDC.put("type", "Update");
//        String SUBMODULE = getModuleNameForLog() + " [updateDetails()] ";
//        CaseUpdateDTO convDTO;
//
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseMessage("Success");
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//
//        try {
//            convDTO = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(caseUpdate, CaseUpdateDTO.class);
//            CaseDTO caseDTO = caseUpdateService.updateEntity(convDTO, file, false);
//            caseService.getCaseDataFromStrig(caseDTO);
//            if (getMvnoIdFromCurrentStaff() != null) {
//                convDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            genericDataDTO.setData(caseDTO);
//            genericDataDTO.setTotalRecords(1);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE,
//                    AclConstants.OPERATION_CASE_EDIT, req.getRemoteAddr(), null, caseDTO.getCaseId(), caseDTO.getCustomerName());
//            logger.info("Updating the case with title " + caseDTO.getCaseTitle() + " :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            return genericDataDTO;
//        } catch (JsonProcessingException e) {
//            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
//            genericDataDTO.setResponseCode(HttpStatus.FAILED_DEPENDENCY.value());
//            genericDataDTO.setResponseMessage(HttpStatus.FAILED_DEPENDENCY.getReasonPhrase());
//            logger.error("Unable to update case with title " + caseUpdate + "  :  request: { From : {}}; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
//
//            return genericDataDTO;
//        } catch (CustomValidationException ce) {
//            //ApplicationLogger.logger.error(ce.getMessage()), ce);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to update case with title " + caseUpdate + "  :  request: { From : {}}; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getStackTrace());
//
//            return genericDataDTO;
//        } catch (IOException e) {
//            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_MODIFIED.value());
//            genericDataDTO.setResponseMessage("File not saved");
//            logger.error("Unable to update case with title " + caseUpdate + "  :  request: { From : {}}; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
//
//            return genericDataDTO;
//        } catch (Exception e) {
//            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("Unable to update case with title " + caseUpdate + "  :  request: { From : {}}; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
//
//            MDC.remove("type");
//
//            return genericDataDTO;
//        }
//    }
//
//
////    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
////    @PostMapping(path = UrlConstants.CASE_UPDATE_DETAILS, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
////    public GenericDataDTO updateDetailsByBulkStatus
////            (@RequestParam List<Case> caseUpdate, @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest req) {
////
////        MDC.put("type", "Update");
////        String SUBMODULE = getModuleNameForLog() + " [updateDetails()] ";
////        CaseUpdateDTO convDTO;
////
////        GenericDataDTO genericDataDTO = new GenericDataDTO();
////        genericDataDTO.setResponseMessage("Success");
////        genericDataDTO.setResponseCode(HttpStatus.OK.value());
////
////        try {
////            for(Case case1: caseUpdate ) {
////                case1.getCaseId();
////            }
////
////                convDTO = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(caseUpdate, CaseUpdateDTO.class);
////
////            CaseDTO caseDTO = caseUpdateService.updateEntity(convDTO, file);
////            if (getMvnoIdFromCurrentStaff() != null) {
////                convDTO.setMvnoId(getMvnoIdFromCurrentStaff());
////            }
////            genericDataDTO.setDataList();
////            genericDataDTO.setData(caseDTO);
////            genericDataDTO.setTotalRecords(1);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE,
////                    AclConstants.OPERATION_CASE_EDIT, req.getRemoteAddr(), null, caseDTO.getCaseId(), caseDTO.getCustomerName());
////            logger.info("Updating the case with title "+caseDTO.getCaseTitle()+" :  request: { From : {}}; Response : {{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
////            return genericDataDTO;
////        } catch (JsonProcessingException e) {
////            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
////            genericDataDTO.setResponseCode(HttpStatus.FAILED_DEPENDENCY.value());
////            genericDataDTO.setResponseMessage(HttpStatus.FAILED_DEPENDENCY.getReasonPhrase());
////            logger.error("Unable to update case with title "+caseUpdate+"  :  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
////
////            return genericDataDTO;
////        } catch (CustomValidationException ce) {
////            //ApplicationLogger.logger.error(ce.getMessage()), ce);
////            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
////            genericDataDTO.setResponseMessage(ce.getMessage());
////            logger.error("Unable to update case with title "+caseUpdate+"  :  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ce.getStackTrace());
////
////            return genericDataDTO;
////        } catch (IOException e) {
////            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
////            genericDataDTO.setResponseCode(HttpStatus.NOT_MODIFIED.value());
////            genericDataDTO.setResponseMessage("File not saved");
////            logger.error("Unable to update case with title "+caseUpdate+"  :  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
////
////            return genericDataDTO;
////        } catch (Exception e) {
////            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
////            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
////            genericDataDTO.setResponseMessage(e.getMessage());
////            logger.error("Unable to update case with title "+caseUpdate+"  :  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
////
////            MDC.remove("type");
////
////            return genericDataDTO;
////        }
////    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
//    @PostMapping(path = UrlConstants.CASES_ASSIGNED_TO_ME)
//    public GenericDataDTO getLoggedInUserCases(@RequestBody PaginationRequestDTO requestDTO) throws Exception {
//
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String SUBMODULE = getModuleNameForLog() + " [getLoggedInUserCases()] ";
//        try {
//            logger.info("Fetching all cases by logged in users " + requestDTO + " :  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            return caseService.getAllCaseByStaffWithPagination
//                    (requestDTO.getPage(), requestDTO.getPageSize()
//                            , requestDTO.getSortBy(), requestDTO.getSortOrder());
//
//        } catch (Exception ex) {
//            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to fetch all cases by logged in users" + requestDTO + "  :  request: { From : {}}; Response : {{}{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
//            MDC.remove("type");
//            return genericDataDTO;
//        }
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
//    @PostMapping(value = UrlConstants.CASES_BY_STATUS)
//    public GenericDataDTO getCaseByStatus(@RequestBody PaginationRequestDTO requestDTO) {
//
//        MDC.put("type", "Fetch");
//        String SUBMODULE = getModuleNameForLog() + " [getCaseByStatus()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            if (null == requestDTO.getStatus()) {
//                genericDataDTO.setResponseMessage("Please Select Status!");
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                logger.error("Unable to fetch all cases by status" + requestDTO.getStatus() + "  :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            }
//            logger.info("Fetching All cases by status " + requestDTO.getStatus() + " :  request: { From : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            return caseService.getAllCaseByStatusWithPagination(requestDTO.getStatus(), requestDTO.getPage()
//                    , requestDTO.getPageSize()
//                    , requestDTO.getSortBy()
//                    , requestDTO.getSortOrder());
//        } catch (Exception ex) {
//            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            logger.error("Unable to fetch all cases by status" + requestDTO.getStatus() + "  :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
//
//        }
//        MDC.remove("type");
//
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
//    @GetMapping(value = UrlConstants.CASES_BY_ASSIGNEE + "/{staffId}")
//    public GenericDataDTO getCasesByAssignee(@PathVariable Integer staffId) {
//
//        MDC.put("type", "Fetch");
//        String SUBMODULE = getModuleNameForLog() + " [getCasesByAssignee()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            if (null == staffId) {
//                genericDataDTO.setResponseMessage("Please Select Assignee!");
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                logger.error("Unable to fetch all cases by Asignee  Staf " + staffId + "  :  request: { From : {},}; Response : {{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            }
//            logger.info("Fetching All cases by assignee  staff " + staffId + " :  request: { From : {}, }; Response : {{}{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            return GenericDataDTO.getGenericDataDTO(caseService.getAllCaseByStaff(staffId));
//        } catch (Exception ex) {
//            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            logger.error("Unable to fetch all cases by Asignee  Staf " + staffId + "  :  request: { From : {}}; Response : {{};Exception:{}{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
//
//        }
//        MDC.remove("type");
//
//        return genericDataDTO;
//    }
//
//    @GetMapping(value = UrlConstants.GET_LIVE_NETWORK_DETAILS + "/{custId}")
//    public GenericDataDTO getLiveUserDetailsByCustomer(@PathVariable Integer custId, HttpServletRequest req) {
//
//        MDC.put("type", "Fetch");
//        String SUBMODULE = getModuleNameForLog() + " [getLiveUserDetailsByCustomer()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            if (null == custId) {
//                genericDataDTO.setResponseMessage("Please Provide Customer!");
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                logger.error("Unable to fetch user details for uer " + custId + "  :  request: { From : {}}; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//
//                return genericDataDTO;
//            }
//            logger.info("Fetching All user details for user" + customersService.get(custId).getUsername() + " :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            return GenericDataDTO.getGenericDataDTO(liveCustomerNetworkDetailsService.getCustomerWiseNetworkDetailsFromLiveUser(custId));
//        } catch (Exception ex) {
//            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            logger.error("Unable to fetch Live data for user  " + customersService.get(custId).getUsername() + "  :  request: { From : {}}; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
//
//        }
//        MDC.remove("type");
//
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
//    @GetMapping(value = UrlConstants.GET_CASES_BY_CUSTOMER + "/{custId}")
//    public GenericDataDTO getCasesByCustomer(@PathVariable Integer custId) {
//
//        MDC.put("type", "Fetch");
//        String SUBMODULE = getModuleNameForLog() + " [getCasesByCustomer()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            if (null == custId) {
//                genericDataDTO.setResponseMessage("Please Provide Customer!");
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                logger.error("Unable to fetch cases for Customer " + custId + "  :  request: { From : {}, }; Response : {{}{};}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//
//                return genericDataDTO;
//            }
//            logger.info("Fetching All casees for customer " + customersService.get(custId).getUsername() + " :  request: { From : {}}; Response : {{}}", customersService.get(custId).getUsername(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            return GenericDataDTO.getGenericDataDTO(caseService.getAllCaseByCustomer(custId));
//
//        } catch (Exception ex) {
//            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            logger.error("Unable to fetch all Cses Forr customer " + customersService.get(custId).getUsername() + "  :  request: { From : {}}; Response : {{};Exception:{}}", customersService.get(custId).getUsername(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
//
//        }
//        MDC.remove("type");
//
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
//    @GetMapping(value = UrlConstants.ASSIGNED_TO + "/{caseId}")
//    public GenericDataDTO caseAssignedTo(@PathVariable Long caseId) {
//
//        MDC.put("type", "Fetch");
//        String SUBMODULE = getModuleNameForLog() + " [caseAssignedTo()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            if (null == caseId) {
//                genericDataDTO.setResponseMessage("Please Provide Case!");
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                logger.error("Unable to fetch all assigned Cass to " + caseId + "  :  request: { From : {}}; Response : {{}{};}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            }
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setData(caseService.assignedTo(caseId));
//            logger.info("Fetching case details assigned to " + caseService.assignedTo(caseId) + " :  request: { From : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            return genericDataDTO;
//        } catch (Exception ex) {
//            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//            if (ex instanceof DataNotFoundException) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                genericDataDTO.setResponseMessage(ex.getMessage());
//                logger.info("Unable to fetch all assigned Cass to " + caseId + "  :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
//                return genericDataDTO;
//            }
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            logger.error("Unable to fetch all assigned Cass to " + caseId + "  :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
//
//        }
//        MDC.remove("type");
//
//        return genericDataDTO;
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[CaseController]";
//    }
//
//    //@Deprecated
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
//    @PostMapping("/case/search")
//    public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO) {
//
//        Integer RESP_CODE = APIConstants.FAIL;
//        MDC.put("type", "Fetch");
//        Object Page;
//        HashMap<String, Object> response = new HashMap<>();
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            CaseService caseService = SpringContext.getBean(CaseService.class);
//            genericDataDTO = caseService.search(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
//            if (genericDataDTO.getTotalPages() > 0) {
//                response.put(APIConstants.MESSAGE, "No Records Found!");
//                logger.error("Unable to Fetch Fetch case by type :  request: { From : {}}; Response : {{}};Error :{} ;", MODULE, RESP_CODE, response);
//            }
//            RESP_CODE = APIConstants.SUCCESS;
//            logger.info("Fetching case::> From : {}, Response_Code: {}, Response : {}", MODULE, RESP_CODE, response);
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            genericDataDTO.setResponseCode(APIConstants.FAIL);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.name());
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//            logger.error("Unable to Fetch Fetch case by type::> From : {}, Response_Code: {}, Response : {}", MODULE, RESP_CODE, response);
//        } catch (RuntimeException re) {
//            re.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.FAIL);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.toString());
//            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//            response.put(APIConstants.ERROR_TAG, re.getMessage());
//            logger.error("Unable to Fetch Fetch case by type::> From : {}, Response_Code: {}, Response : {}", MODULE, RESP_CODE, response);
//        } catch (Exception e) {
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, e.getMessage());
//            logger.error("Unable to Fetch Fetch case by type::> From : {}, Response_Code: {}, Response : {}", MODULE, RESP_CODE, response);
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    public ValidationData validateSearchCriteria(List<GenericSearchModel> filterList) {
//        ValidationData validationData = new ValidationData();
//        if (null == filterList || 0 < filterList.size()) {
//            validationData.setValid(false);
//            validationData.setMessage("Please Provide Search Criteria");
//            return validationData;
//        }
//        validationData.setValid(true);
//        return validationData;
//    }
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//        return super.getAllWithoutPagination();
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody CaseDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = super.delete(entityDTO, authentication, req);
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE, AclConstants.OPERATION_CASE_DELETE, req.getRemoteAddr(), null, entityDTO.getCaseId().longValue(), entityDTO.getCustomerName());
//        MDC.remove("type");
//
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody CaseDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//
//        MDC.put("type", "Fetch");
//        if (getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        GenericDataDTO genericDataDTO = super.update(entityDTO, result, authentication, req);
//        CaseDTO caseEntity = (CaseDTO) genericDataDTO.getData();
//        caseService.getCaseDataFromStrig(caseEntity);
//        if (caseEntity != null)
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE,
//                    AclConstants.OPERATION_CASE_EDIT, req.getRemoteAddr(), null, caseEntity.getCaseId().longValue(), caseEntity.getUserName());
//        //    logger.info("Fetching All Entities by id "+id+" :  request: { From : {}}; Response : {{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        MDC.remove("type");
//
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
//    @Override
//    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = super.getEntityById(id, req);
//        CaseDTO caseEntity = (CaseDTO) genericDataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE,
//                AclConstants.OPERATION_CASE_VIEW, req.getRemoteAddr(), null, caseEntity.getCaseId().longValue(), caseEntity.getUserName());
//        MDC.remove("type");
//
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
//    @Override
//    @PostMapping
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//
//        MDC.put("type", "Fetch");
//        String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
//                genericDataDTO = caseService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
//                        , requestDTO.getPageSize()
//                        , requestDTO.getSortBy()
//                        , requestDTO.getSortOrder()
//                        , requestDTO.getFilters());
//            if (null != requestDTO.getFilters() && 0 < requestDTO.getFilters().size())
//                genericDataDTO = caseService.search(requestDTO.getFilters()
//                        , requestDTO.getPage(), requestDTO.getPageSize()
//                        , requestDTO.getSortBy()
//                        , requestDTO.getSortOrder());
//            if (null != requestDTO.getFilterBy() && requestDTO.getFilterBy().equalsIgnoreCase(CaseConstants.FILTER_BY_MY_CASES))
//                genericDataDTO = caseService.getAllCaseByStaffWithPagination(requestDTO.getPage()
//                        , requestDTO.getPageSize()
//                        , requestDTO.getSortBy()
//                        , requestDTO.getSortOrder());
//            if (null != requestDTO.getFilterBy() && requestDTO.getFilterBy().equalsIgnoreCase(CaseConstants.FILTER_BY_STATUS))
//                genericDataDTO = caseService.getAllCaseByStatusWithPagination(requestDTO.getStatus()
//                        , requestDTO.getPage()
//                        , requestDTO.getPageSize()
//                        , requestDTO.getSortBy()
//                        , requestDTO.getSortOrder());
//            if (null != requestDTO.getFilterBy() && requestDTO.getFilterBy().equalsIgnoreCase(CaseConstants.FILTER_BY_BOTH))
//                genericDataDTO = caseService.getAllCaseByStatusAndMyCasesWithPagination(requestDTO.getStatus()
//                        , requestDTO.getPage()
//                        , requestDTO.getPageSize()
//                        , requestDTO.getSortBy()
//                        , requestDTO.getSortOrder());
//
//            if (null != genericDataDTO) {
//                logger.info("Fetching All cases:  request: { From : {}}; Response : {{}{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            } else {
//                genericDataDTO = new GenericDataDTO();
//                genericDataDTO.setDataList(new ArrayList<>());
//                genericDataDTO.setTotalRecords(0);
//                genericDataDTO.setPageRecords(0);
//                genericDataDTO.setCurrentPageNumber(1);
//                genericDataDTO.setTotalPages(1);
//                logger.error("Unable to fetch all cases  :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//
//            }
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to fetch all cases :  request: { From : {},}; Response : {{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//
//        }
//        MDC.remove("type");
//
//        return genericDataDTO;
//    }
//
//    @GetMapping(value = "/excel/mycases")
//    public void exportToExcelForMyCases(HttpServletResponse response) throws Exception {
//
//        MDC.put("type", "Fetch");
//        response.setContentType("application/octet-stream");
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String currentDateTime = dateFormatter.format(new Date());
//
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=Excel_" + currentDateTime + ".xlsx";
//        response.setHeader(headerKey, headerValue);
//        Workbook workbook = new XSSFWorkbook();
//        caseService.excelGenerateForMyCases(workbook);
//        ServletOutputStream outputStream = response.getOutputStream();
//        workbook.write(outputStream);
//        workbook.close();
//        outputStream.close();
//        MDC.remove("type");
//
//    }
//
//    @GetMapping(value = "/pdf/mycases")
//    public void generatePdfForMyCases(HttpServletResponse response) throws Exception {
//        response.setContentType("application/pdf");
//
//        MDC.put("type", "Fetch");
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String currentDateTime = dateFormatter.format(new Date());
//
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=Pdf_" + currentDateTime + ".pdf";
//        response.setHeader(headerKey, headerValue);
//
//        Document pdfDoc = new Document();
//        PdfWriter.getInstance(pdfDoc, response.getOutputStream());
//        MDC.remove("type");
//
//        caseService.pdfGenerateForMyCases(pdfDoc);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
//    @GetMapping(value = "/assignTicketFromTeam/{caseId}")
//    public GenericDataDTO assignTicketFromTeam(@PathVariable Long caseId, @RequestParam(required = false) Integer teamId, @RequestParam String remark) {
//
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setData(caseUpdateService.assignTicketFromTeam(caseId, teamId, remark));
//            logger.info("Fetching All cases assigned from team " + teamId + "  :  request: { From : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        } catch (Exception ex) {
//            //ApplicationLogger.logger.error(getModuleNameForLog() + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to fetch al cases from team " + teamId + "  :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
//
//        }
//        MDC.remove("type");
//
//        return genericDataDTO;
//
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @GetMapping("/approveTicket")
//    public GenericDataDTO approveTicket(@RequestParam(name = "caseId") Long caseId, @RequestParam(name = "isApproveRequest") boolean isApproveRequest, @RequestParam(name = "remarks", required = false) String remarks ) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//
//            MDC.put("type", "Fetch");
//
//            logger.info("Getting Ticket Approve from  with id  " + caseId + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            MDC.remove("type");
//            genericDataDTO = caseService.approveTicket(caseId, isApproveRequest, remarks);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Approved Successfully..");
//        } catch (CustomValidationException ex) {
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            return genericDataDTO;
//        } catch (Exception ex) {
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            return genericDataDTO;
//
//        }
//
//        return genericDataDTO;
//
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @GetMapping("/assignPickedTicket")
//    public GenericDataDTO assignPickedTicket(@RequestParam(name = "caseId") Long caseId, @RequestParam(name = "staffId") Integer staffId, @RequestParam(name = "remark") String remark) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Fetch");
//        try {
//            logger.info("Getting Ticket Approve from  with id  " + caseId + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return caseService.assignPickedTicket(caseId, staffId, remark);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Approve Ticket  with " + caseId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @GetMapping("/assignEveryStaffFromList")
//    public GenericDataDTO assignEveryStaffFromList(@RequestParam(name = "caseId") Long caseId, @RequestParam(name = "remark") String remark, @RequestParam(name = "isApproveRequest") Boolean isApproveRequest) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Fetch");
//        try {
//            logger.info("Getting Ticket Approve from  with id  " + caseId + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return caseService.assignEveryStaffFromList(caseId, remark,isApproveRequest);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Approve Ticket  with " + caseId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @PutMapping(path = UrlConstants.CASE_BULK_UPDATE_DETAILS)
//    public GenericDataDTO bulkUpdateDetails(@RequestBody List<CaseUpdateDTO> caseUpdate, HttpServletRequest req) throws JsonProcessingException, IOException {
//
//        MDC.put("type", "Update");
//        String SUBMODULE = getModuleNameForLog() + " [updateDetails()] ";
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseMessage("Success");
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//
//        try {
//            for (CaseUpdateDTO case1 : caseUpdate) {
//                CaseDTO caseDTO = caseUpdateService.updateEntity(case1, null,false);
//            }
//
//            return genericDataDTO;
//
//
//        } catch (CustomValidationException ce) {
//            //ApplicationLogger.logger.error(ce.getMessage()), ce);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to update case with title  :  request: { From : {}}; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getStackTrace());
//
//            return genericDataDTO;
//        } catch (Exception e) {
//            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("Unable to update case with title :  request: { From : {}}; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
//
//            MDC.remove("type");
//
//            return genericDataDTO;
//
//        }
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @GetMapping("/linkTicket")
//    public GenericDataDTO linkTicket(@RequestParam(name = "caseId") Long caseId, @RequestParam(name = "linkTicketId") Integer linkTicketId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Fetch");
//        try {
//            logger.info("Getting Ticket Approve from  with id  " + caseId + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return caseService.linkTicket(caseId, linkTicketId);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Approve Ticket  with " + caseId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @PostMapping(value = "/updateDocumentDetails", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public GenericDataDTO updateDocumentDetails(@RequestParam(name = "caseId") Long caseId, @RequestParam(value = "file") List<MultipartFile> file, HttpServletRequest req) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Fetch");
//        try {
//            logger.info("Getting Ticket Approve from  with id  " + caseId + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return caseService.updateDocumentDetails(caseId, file);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Approve Ticket  with " + caseId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @GetMapping("/reassignTicket")
//    public GenericDataDTO reassignTicket(@RequestParam(name = "caseId") Long caseId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Fetch");
//        try {
//            logger.info("Getting Ticket Reassigned from  with id  " + caseId + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return caseService.reassignTicket(caseId);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Reassign Ticket  with " + caseId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
//            + AclConstants.OPERATION_CUSTOMER_GET_DOCUMENT + "\")")
//    @RequestMapping(value = "/document/download/{ticketId}/{docId}", method = RequestMethod.GET)
//    public ResponseEntity<Resource> downloadDocument(@PathVariable Long docId, @PathVariable Long ticketId) {
//        MDC.put("type", "Fetch");
//        String SUBMODULE = MODULE + " [downloadDocument()] ";
//        Resource resource = null;
//        try {
//            CaseDTO caseDTO = caseService.getEntityById(ticketId);
//            CustomersPojo customers = customersService.findById(caseDTO.getCustomersId());
//            if (null == caseDTO) {
//                logger.error("Unable to Download recipt for case" + caseDTO.getCaseNumber() + " for document id" + docId + " :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL);
//                return ResponseEntity.notFound().build();
//            }
//            CaseDocDetails docDetailsDTO = caseDocDetailsService.downloadDocument(docId, ticketId);
//            if (null == docDetailsDTO) {
//                logger.error("Unable to Download doc]umrnt  for customer " + caseDTO.getCaseNumber() + "for document id" + docId + " :  request: { From : {}}; Response : {{} code:{};Exception:{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL);
//                return ResponseEntity.notFound().build();
//            }
//            FileSystemService service = com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
//                resource = service.getTicketDoc(caseDTO.getCaseNumber().trim(), customers.getUsername(),docDetailsDTO.getUniquename());
//            // resource=service.getInvoice("12123");
//            String contentType = "application/octet-stream";
//            if (resource != null && resource.exists()) {
//                logger.info("Unable to Download recipt for case " + caseDTO.getCaseNumber()+ " for payment id" + docId + " :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL);
//                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
//                        .header(HttpHeaders.CONTENT_DISPOSITION,
//                                "attachment; filename=\"" + resource.getFilename() + "\"")
//                        .body(resource);
//
//            } else {
//                logger.error("Unable to Download recipt for case " + caseDTO.getCaseNumber() + "for document id" + docId + " :  request: { From : {}}; Response : {{} code:{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL);
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception ex) {
//            logger.error("Unable to Download document for case  for doccument id" + docId + " :  request: { From : {}}; Response : {{} code:{};Exception:{}}", getModuleNameForLog(), HttpStatus.EXPECTATION_FAILED, APIConstants.FAIL, ex.getStackTrace());
//            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//        }
//        MDC.remove("type");
//
//        return null;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @RequestMapping(value ="/sendETRtoCustomer", method = RequestMethod.POST)
//    public GenericDataDTO sendETRTicketNotification(@Valid @RequestBody TicketETRPojo entityDTO, HttpServletRequest req) throws Exception {
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Fetch");
//        try {
//            logger.info("Generating ETR for ticket :  " + entityDTO.getTicketId() + "  is Successful:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return caseService.sendETRTicketNotification(entityDTO);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Generate ETR for Ticket  with " + entityDTO.getTicketId() + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
//    @PostMapping(value = "/getTicketETRReport/{caseId}")
//    public GenericDataDTO getTicketETRReport(@PathVariable Long caseId) {
//
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            logger.info("Fetching All etr for case  " + caseId + "  :  request: { From : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            return caseService.getETRDetailsForCase(caseId);
//        } catch (Exception ex) {
//            //ApplicationLogger.logger.error(getModuleNameForLog() + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to fetch all etr details for case " + caseId + "  :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
//
//        }
//        MDC.remove("type");
//
//        return genericDataDTO;
//
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @GetMapping("/getTatDetials")
//    public GenericDataDTO getTatDetails(@RequestParam(name = "caseId") Long caseId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Fetch");
//        try {
//            logger.info("Getting Ticket TAT details  with id  " + caseId + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return caseService.getTatDetails(caseId);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Approve Ticket  with " + caseId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @GetMapping("/getAllStaffUserByServiceArea/{serviceAreaId}")
//    public GenericDataDTO getAllStaffByServiceArea(@PathVariable Integer serviceAreaId, HttpServletRequest req) {
//        String SUBMODULE = getModuleNameForLog() + " [getAllStaffUserByServiceAreaId] ";
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO = GenericDataDTO.getGenericDataDTO(caseService.findAllStaffUser(serviceAreaId));
//            if (null != genericDataDTO) {
//
//                if (genericDataDTO.getDataList().isEmpty())
//                {
//                    genericDataDTO = new GenericDataDTO();
//                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
//                    genericDataDTO.setResponseMessage("No Record Found!");
//                    genericDataDTO.setDataList(new ArrayList<>());
//                    genericDataDTO.setTotalRecords(0);
//                    genericDataDTO.setPageRecords(0);
//                    genericDataDTO.setCurrentPageNumber(1);
//                    genericDataDTO.setTotalPages(1);
//
//                }
//
//                logger.info("No data Found  :  request: { From : {}}; Response : {{}};}", req.getHeader("requestFrom"),genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            }
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to Search data  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),HttpStatus.EXPECTATION_FAILED.value(),HttpStatus.EXPECTATION_FAILED.getReasonPhrase(),ex.getStackTrace());
//            return genericDataDTO;
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @PostMapping("/linkBulkTicket")
//    public GenericDataDTO linkTicket(@RequestBody List<Integer> childTickets,@RequestParam(name = "linkTicketId") Integer linkTicketId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//
//        List <Integer> casesIDs = new ArrayList<>();
//        for(int i =0;i<childTickets.size();i++){
//            casesIDs.add(Math.toIntExact(childTickets.get(i)));
//        }
//        try {
//                //logger.info("Getting Ticket Approve from  with id  " + casesIDs.get(i) + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//                genericDataDTO.setData(caseService.linkBulkTicket(casesIDs, linkTicketId));
//
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            //logger.error("Unable to Approve Ticket  with " + caseId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @PostMapping("/reassignTicketInBulk")
//    public GenericDataDTO reassignTicketInBulk(@RequestBody List<CaseDTO> childTickets) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Fetch");
//        try {
//            List<Long> casesIDs = new ArrayList<>();
//            for (int i = 0; i < childTickets.size(); i++) {
//                casesIDs.add(childTickets.get(i).getCaseId());
//            }
//            //logger.info("Getting Ticket Reassigned from  with id  " + caseId + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return caseService.reassignTicketInBulk(casesIDs);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            //logger.error("Unable to Reassign Ticket  with " + caseId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
//    @PostMapping("/filter")
//    public GenericDataDTO filterCase(@RequestParam(name="filter") String filter, @RequestBody PaginationRequestDTO requestDTO) {
//
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO=caseService.filterCase(filter,requestDTO);
//            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
//        }catch (Exception e){
//            e.getStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.FAIL);
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @GetMapping("/getChildTickets")
//    public GenericDataDTO getChildTickets(@RequestParam(name = "caseId") Long caseId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Fetch");
//        try {
//            logger.info("Getting child Tickets  with id  " + caseId + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return caseService.getChildTickets(caseId);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable fetch child tickets Ticket  with " + caseId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @GetMapping("/findAll/ContactFailed")
////	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
//    public ResponseEntity<Map<String, Object>> findAllServicerType(HttpServletRequest request) {
//        Map<String, Object> response = new HashMap<>();
//        MDC.put("","");
//        Integer responseCode = APIConstants.FAIL;
//        try {
//            ClientService clientService = clientServiceSrv.getByName("ContactFailed");
//            //List<String>contactfieldList=new ArrayList<>();
//
//            if (clientService == null) {
//                response.put("ContactFailed", new ArrayList<>());
//                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
//            } else {
//                List<String> servicerTypeList = new ArrayList<String>(
//                        Arrays.asList(clientService.getValue().split(" , ")));
//                response.put("ContactFailed", servicerTypeList);
//            }
//            responseCode = APIConstants.SUCCESS;
//            logger.info("Fetching ServicerTypeList :  request: { From : {}}; Response : {{}}", MODULE, responseCode,
//                    response);
//        } catch (CustomValidationException e) {
//            responseCode = e.getErrCode();
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } catch (Exception e) {
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } finally {
//            MDC.remove("type");
//        }
//        return responseController.apiResponse(responseCode, response);
//
//    }
//    @GetMapping("/findAll/ProblemType")
////	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
//    public ResponseEntity<Map<String, Object>> findAllByProblemType(HttpServletRequest request) {
//        Map<String, Object> response = new HashMap<>();
//        MDC.put("","");
//        Integer responseCode = APIConstants.FAIL;
//        try {
//            ClientService clientService = clientServiceSrv.getByName("ProblemType");
//            //List<String>contactfieldList=new ArrayList<>();
//
//            if (clientService == null) {
//                response.put("ProblemTypeList", new ArrayList<>());
//                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
//            } else {
//                List<String> servicerTypeList = new ArrayList<String>(
//                        Arrays.asList(clientService.getValue().split(" , ")));
//                response.put("ProblemTypeList", servicerTypeList);
//            }
//            responseCode = APIConstants.SUCCESS;
//            logger.info("Fetching ServicerTypeList :  request: { From : {}}; Response : {{}}", MODULE, responseCode,
//                    response);
//        } catch (CustomValidationException e) {
//            responseCode = e.getErrCode();
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } catch (Exception e) {
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } finally {
//            MDC.remove("type");
//        }
//        return responseController.apiResponse(responseCode, response);
//
//    }
//    @GetMapping("/findAll/PaymentMode")
////	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
//    public ResponseEntity<Map<String, Object>> findAllByPaymentMode(HttpServletRequest request) {
//        Map<String, Object> response = new HashMap<>();
//        MDC.put("","");
//        Integer responseCode = APIConstants.FAIL;
//        try {
//            ClientService clientService = clientServiceSrv.getByName("PaymentMode");
//            //List<String>contactfieldList=new ArrayList<>();
//
//            if (clientService == null) {
//                response.put("PaymentModeList", new ArrayList<>());
//                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
//            } else {
//                List<String> servicerTypeList = new ArrayList<String>(
//                        Arrays.asList(clientService.getValue().split(" , ")));
//                response.put("PaymentModeList", servicerTypeList);
//            }
//            responseCode = APIConstants.SUCCESS;
//            logger.info("Fetching ServicerTypeList :  request: { From : {}}; Response : {{}}", MODULE, responseCode,
//                    response);
//        } catch (CustomValidationException e) {
//            responseCode = e.getErrCode();
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } catch (Exception e) {
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } finally {
//            MDC.remove("type");
//        }
//        return responseController.apiResponse(responseCode, response);
//
//    }
//    @GetMapping("/findAll/TicketsRaisedoption")
////	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
//    public ResponseEntity<Map<String, Object>> findAllByTicketsRaisedoption(HttpServletRequest request) {
//        Map<String, Object> response = new HashMap<>();
//        MDC.put("","");
//        Integer responseCode = APIConstants.FAIL;
//        try {
//            ClientService clientService = clientServiceSrv.getByName("TicketsRaisedoption");
//            //List<String>contactfieldList=new ArrayList<>();
//
//            if (clientService == null) {
//                response.put("TicketsRaisedoptionList", new ArrayList<>());
//                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
//            } else {
//                List<String> servicerTypeList = new ArrayList<String>(
//                        Arrays.asList(clientService.getValue().split(" , ")));
//                response.put("TicketsRaisedoptionList", servicerTypeList);
//            }
//            responseCode = APIConstants.SUCCESS;
//            logger.info("Fetching ServicerTypeList :  request: { From : {}}; Response : {{}}", MODULE, responseCode,
//                    response);
//        } catch (CustomValidationException e) {
//            responseCode = e.getErrCode();
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } catch (Exception e) {
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } finally {
//            MDC.remove("type");
//        }
//        return responseController.apiResponse(responseCode, response);
//
//    }
//    @GetMapping("/findAll/Satisfied")
////	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
//    public ResponseEntity<Map<String, Object>> findAllByTicketsSatisfied(HttpServletRequest request) {
//        Map<String, Object> response = new HashMap<>();
//        MDC.put("","");
//        Integer responseCode = APIConstants.FAIL;
//        try {
//            ClientService clientService = clientServiceSrv.getByName("Satisfied");
//            //List<String>contactfieldList=new ArrayList<>();
//
//            if (clientService == null) {
//                response.put("SatisfiedList", new ArrayList<>());
//                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
//            } else {
//                List<String> servicerTypeList = new ArrayList<String>(
//                        Arrays.asList(clientService.getValue().split(" , ")));
//                response.put("SatisfiedList", servicerTypeList);
//            }
//            responseCode = APIConstants.SUCCESS;
//            logger.info("Fetching ServicerTypeList :  request: { From : {}}; Response : {{}}", MODULE, responseCode,
//                    response);
//        } catch (CustomValidationException e) {
//            responseCode = e.getErrCode();
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } catch (Exception e) {
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } finally {
//            MDC.remove("type");
//        }
//        return responseController.apiResponse(responseCode, response);
//
//    }
//    @GetMapping("/findAll/Unsatisfied")
////	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
//    public ResponseEntity<Map<String, Object>> findAllByTicketsUnsatisfied(HttpServletRequest request) {
//        Map<String, Object> response = new HashMap<>();
//        MDC.put("","");
//        Integer responseCode = APIConstants.FAIL;
//        try {
//            ClientService clientService = clientServiceSrv.getByName("Unsatisfied");
//            //List<String>contactfieldList=new ArrayList<>();
//
//            if (clientService == null) {
//                response.put("UnsatisfiedList", new ArrayList<>());
//                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
//            } else {
//                List<String> servicerTypeList = new ArrayList<String>(
//                        Arrays.asList(clientService.getValue().split(" , ")));
//                response.put("UnsatisfiedList", servicerTypeList);
//            }
//            responseCode = APIConstants.SUCCESS;
//            logger.info("Fetching ServicerTypeList :  request: { From : {}}; Response : {{}}", MODULE, responseCode,
//                    response);
//        } catch (CustomValidationException e) {
//            responseCode = e.getErrCode();
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } catch (Exception e) {
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } finally {
//            MDC.remove("type");
//        }
//        return responseController.apiResponse(responseCode, response);
//
//    }
//    @GetMapping("/findAll/Feedback")
////	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
//    public ResponseEntity<Map<String, Object>> findAllByFeedback(HttpServletRequest request) {
//        Map<String, Object> response = new HashMap<>();
//        MDC.put("","");
//        Integer responseCode = APIConstants.FAIL;
//        try {
//            ClientService clientService = clientServiceSrv.getByName("Feedback");
//            //List<String>contactfieldList=new ArrayList<>();
//
//            if (clientService == null) {
//                response.put("UnsatisfiedList", new ArrayList<>());
//                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
//            } else {
//                List<String> servicerTypeList = new ArrayList<String>(
//                        Arrays.asList(clientService.getValue().split(" , ")));
//                response.put("UnsatisfiedList", servicerTypeList);
//            }
//            responseCode = APIConstants.SUCCESS;
//            logger.info("Fetching ServicerTypeList :  request: { From : {}}; Response : {{}}", MODULE, responseCode,
//                    response);
//        } catch (CustomValidationException e) {
//            responseCode = e.getErrCode();
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } catch (Exception e) {
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } finally {
//            MDC.remove("type");
//        }
//        return responseController.apiResponse(responseCode, response);
//
//    }
//    @GetMapping("/findAll/informationofpaymentmode")
////	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
//    public ResponseEntity<Map<String, Object>> findAllByinformationofpaymentmode(HttpServletRequest request) {
//        Map<String, Object> response = new HashMap<>();
//        MDC.put("","");
//        Integer responseCode = APIConstants.FAIL;
//        try {
//            ClientService clientService = clientServiceSrv.getByName("informationofpaymentmode");
//            //List<String>contactfieldList=new ArrayList<>();
//
//            if (clientService == null) {
//                response.put("paymentinfolist", new ArrayList<>());
//                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
//            } else {
//                List<String> servicerTypeList = new ArrayList<String>(
//                        Arrays.asList(clientService.getValue().split(" , ")));
//                response.put("paymentinfolist", servicerTypeList);
//            }
//            responseCode = APIConstants.SUCCESS;
//            logger.info("Fetching ServicerTypeList :  request: { From : {}}; Response : {{}}", MODULE, responseCode,
//                    response);
//        } catch (CustomValidationException e) {
//            responseCode = e.getErrCode();
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } catch (Exception e) {
//            response.put(APIConstants.FAIL.toString(), e.getMessage());
//            logger.error(
//                    "Unable to Fetch ServicerTypeList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//                    MODULE, responseCode, response, e.getStackTrace());
//        } finally {
//            MDC.remove("type");
//        }
//        return responseController.apiResponse(responseCode, response);
//
//    }
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @GetMapping("/getTatAuditDetails")
//    public GenericDataDTO getTatAuditDetails(@RequestParam(name = "caseId") Long caseId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Fetch");
//        try {
//            logger.info("Getting Ticket Tat Audit from  with id  " + caseId + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return caseService.getTatAuditDetails(caseId);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to find the tat audit Ticket  with " + caseId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
}
