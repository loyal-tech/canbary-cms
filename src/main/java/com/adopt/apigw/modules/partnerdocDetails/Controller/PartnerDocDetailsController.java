package com.adopt.apigw.modules.partnerdocDetails.Controller;

import com.adopt.apigw.constants.*;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.dto.ValidationData;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.model.postpaid.PartnerCreditDocument;
import com.adopt.apigw.model.postpaid.PartnerDebitDocument;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerPaymentService;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.partnerdocDetails.Service.PartnerDocDetailsService;
import com.adopt.apigw.modules.partnerdocDetails.model.PartnerDocDeleteModel;
import com.adopt.apigw.modules.partnerdocDetails.model.PartnerdocDTO;
import com.adopt.apigw.service.common.FileSystemService;
import com.adopt.apigw.service.postpaid.PartnerService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import brave.Tracer;
import brave.propagation.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PARTNER_DOC)
public class PartnerDocDetailsController extends ExBaseAbstractController<PartnerdocDTO> {
    private static String MODULE = " [PartnerDocDetailsController] ";

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    PartnerDocDetailsService partnerDocDetailsService;

    @Autowired
    PartnerService partnerService;

    @Autowired
    private Tracer tracer;

    private String PATH;
    private static final Logger log = LoggerFactory.getLogger(PartnerDocDetailsController.class);

    public PartnerDocDetailsController(PartnerDocDetailsService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PartnerDocDetailsController]";
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER_DOCS_EDIT+"\")")
    @Override
    @PostMapping(value = "/update",consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO update(@Valid @RequestBody PartnerdocDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", partnerService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
// TODO: pass mvnoID manually 6/5/2025
        if(getMvnoIdFromCurrentStaff(null) != null)
            // TODO: pass mvnoID manually 6/5/2025
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff(null));

        try {
            if (result.hasErrors()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
                log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update PartnerDocDetails for PartnerId :  "+entityDTO.getPartnerId()+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED +   LogConstants.LOG_ERROR + "unable to fatch updated entity "+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            ValidationData validation = validateUpdate(entityDTO);
            if (!validation.isValid()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update PartnerDocDetails for PartnerId :  "+entityDTO.getPartnerId()+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED +   LogConstants.LOG_ERROR + "AUnable to update entity "+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }

            partnerDocDetailsService.getEntityForUpdateAndDelete(entityDTO.getPartnerId());
            entityDTO.setUniquename(entityDTO.getUniquename());
            genericDataDTO.setData(partnerDocDetailsService.updateEntity(entityDTO));
            RESP_CODE = APIConstants.SUCCESS;
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setTotalRecords(1);
            log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update PartnerDocDetails for PartnerId :  "+entityDTO.getPartnerId()+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update PartnerDocDetails for PartnerId :  " +entityDTO.getPartnerId()+LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            if (ex instanceof CustomValidationException){
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update PartnerDocDetails for PartnerId :  " +entityDTO.getPartnerId()+LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            } else {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
                log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update PartnerDocDetails for PartnerId :  " +entityDTO.getPartnerId()+LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER_DOCS_DELETE+ "\")")
    @PostMapping(value = "/deletePartnerDoc", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO deletePartnerDoc(@RequestBody PartnerDocDeleteModel partnerDocDeleteModel,HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", partnerService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        String name=partnerService.get(partnerDocDeleteModel.getPartnerId(),mvnoId).getName();
        String SUBMODULE = getModuleNameForLog() + " [deletePartnerDoc()] ";
       try {
            if (null == partnerDocDeleteModel) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide DocumentList!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Delete Doccuments for PartnerID : " +partnerDocDeleteModel.getPartnerId()+  LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Document List Not Found"+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (partnerDocDeleteModel.getDocIdList() == null || 0 == partnerDocDeleteModel.getDocIdList().size()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide DocumentList!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Delete Doccuments for PartnerId : " +partnerDocDeleteModel.getPartnerId()+  LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Document List Not Found"+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (partnerDocDeleteModel.getPartnerId() == null) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Partner!");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Delete Doccuments for PartnerId : " +partnerDocDeleteModel.getPartnerId()+  LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Partner List Not Found"+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            partnerDocDetailsService.getEntityForUpdateAndDelete(partnerDocDeleteModel.getPartnerId());
            if (SubscriberConstants.DELETED_SUCCESSFULLY.equalsIgnoreCase(partnerDocDetailsService.deleteDocument(partnerDocDeleteModel.getDocIdList(), partnerDocDeleteModel.getPartnerId()))) {
                genericDataDTO.setResponseMessage(SubscriberConstants.DELETED_SUCCESSFULLY);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Documents for PartnerId : " +partnerDocDeleteModel.getPartnerId()+ name+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("Problem in deletion!");
                genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Delete Documents for PartnerId : " +partnerDocDeleteModel.getPartnerId() + name + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                return genericDataDTO;
            }
            if (ex instanceof CustomValidationException){
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Documents for PartnerId :  " +partnerDocDeleteModel.getPartnerId()+LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Failed to delete data. Please try after some time");
           RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
           log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Delete Doccuments for PartnerId : " +partnerDocDeleteModel.getPartnerId()+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
       }
       finally {
           MDC.remove("type");
           MDC.remove("userName");
           MDC.remove("traceId");
           MDC.remove("spanId");
       }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER_DOCS_CREATE + "\")")
    @PostMapping(value = UrlConstants.UPLOAD_DOC_PARTNER, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO uploadDocForPartner(@RequestParam String docDetailsList, @RequestParam(value = "file", required = false) MultipartFile[] file,HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Create");
        MDC.put("userName", partnerService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());        String SUBMODULE = getModuleNameForLog() + " [updateDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try
        {
            if (null != docDetailsList) {
                List<PartnerdocDTO> partnerdocDTOList = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(docDetailsList, new TypeReference<List<PartnerdocDTO>>() {});

                if (null == partnerdocDTOList || 0 == partnerdocDTOList.size()) {
                    genericDataDTO.setResponseMessage("Please provide document details!");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"upload Doccuments for Partner " +  LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Unable to Upload DocFor Partner"+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }

                if (partnerdocDTOList.get(0).getFilename()==null || partnerdocDTOList.get(0).getFilename().equalsIgnoreCase("")) {
                    genericDataDTO.setResponseMessage("Please Upload File!");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"upload Doccuments for Partner " +  LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Unable to Upload DocFor Partner"+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }

                if (null != partnerdocDTOList && 0 < partnerdocDTOList.size()) {
                    partnerDocDetailsService.getEntityForUpdateAndDelete(partnerdocDTOList.get(0).getPartnerId());
                    genericDataDTO.setDataList(partnerDocDetailsService.uploadDocument(partnerdocDTOList, file));
                    genericDataDTO.setResponseMessage("Documents uploaded successfully.");
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    RESP_CODE = APIConstants.SUCCESS;
                    log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"upload Doccuments for Partner "+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }

            }
            genericDataDTO.setResponseMessage("Please provide document details!");
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"upload Doccuments for Partner "+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(e.getMessage());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"upload Documents for Partner " + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }

            if (e instanceof CustomValidationException){
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage(e.getMessage());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"upload Documents for Partner " + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }

            if (e instanceof RuntimeException) {
                genericDataDTO.setResponseMessage(e.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"upload Documents for Partner " + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"upload Documents for Partner " + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

//    @PostMapping(value = UrlConstants.UPLOAD_DOC_ONLINE)
//    public GenericDataDTO uploadDocOnlineForPartner(@RequestBody PartnerdocDTO partnerdocDTO, @RequestParam Boolean isUpdate) {
//        String SUBMODULE = getModuleNameForLog() + " [uploadDocOnlineForPartner()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Create");
//        String name=partnerService.get(partnerdocDTO.getPartnerId()).getName();
//        try {
//            if (null != partnerdocDTO) {
//                genericDataDTO.setData(partnerDocDetailsService.uploadDocumentOnline(partnerdocDTO, isUpdate));
//                genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                genericDataDTO.setResponseMessage("Documents uploaded successfully.");
//                logger.info("Uploading Partner Doccument With name "+name+"is Successfull:  request: { module:{} message: {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode() );
//                return genericDataDTO;
//            }
//            genericDataDTO.setResponseMessage("Please provide document details!");
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            logger.error("Unable to Upload Doccuments  for Partner "+name+":  request: { Response : {{}};Error :{} ;",getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
//            return genericDataDTO;
//        } catch (Exception e) {
//            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
//            if (e instanceof DataNotFoundException) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                genericDataDTO.setResponseMessage(e.getMessage());
//                logger.error("Unable to Upload Doccuments  for Partner "+name+":  request: { Response : {{}};Error :{};Exception:{} ;",getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode(),e.getStackTrace());
//                return genericDataDTO;
//            }
//            if (e instanceof RuntimeException) {
//                genericDataDTO.setResponseMessage(e.getMessage());
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                logger.error("Unable to Upload Doccuments  for Partner "+name+":  request: { Response : {{}};Error :{};Exception:{} ;",getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode(),e.getStackTrace());
//                return genericDataDTO;
//            }
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to Upload Doccuments  for Partner "+name+":  request: { Response : {{}};Error :{};Exception:{} ;",getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode(),e.getStackTrace());
//
//            MDC.remove("type");
//            return genericDataDTO;
//        }
   // }
//@PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER_VIEW_DOCS+ "\")")
    @GetMapping(value = UrlConstants.DOC_BY_PARTNER + "/{partnerId}")
    public GenericDataDTO getDocByPartner(@PathVariable Integer partnerId,HttpServletRequest req) {
//        String name=partnerService.get(partnerId).getName();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = getModuleNameForLog() + " [getDocByPartner()] ";
        try {
            if (null == partnerId) {
                RESP_CODE = APIConstants.NOT_FOUND;
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Partner");
                log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"fetch for  Doccuments for PartnerId : " +partnerId+  LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Partner id is null"+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Doccuments for PartnerId : " +partnerId+  LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return GenericDataDTO.getGenericDataDTO(partnerDocDetailsService.findDocsByPartnerId(partnerId));
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Doccuments for PartnerId : " +partnerId+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//    @GetMapping(value = UrlConstants.DOC_BY_STATUS_AND_PARTNER + "/{custId}")
//    public GenericDataDTO isCustDocPending(@PathVariable Integer custId) {
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String name=customersService.get(custId).getFullName();
//        String SUBMODULE = getModuleNameForLog() + " [isCustDocPending()] ";
//        try {
//            if (null == custId) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Please Provide Customer");
//                logger.error("Unable to fetch pending  Doccuments for customer "+name+":  request: { Response : {{}};Error :{};",getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
//            }
//            logger.info("Fetching pending documenft list for customer "+name+"request: { Response : {{}};",getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
//            genericDataDTO.setData(customerDocDetailsService.isCustDocPending(custId));
//        } catch (Exception ex) {
//            //   ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to fetch pending  Doccuments for customer "+name+":  request: { Response : {{}};Error :{};Exception:{} ;",getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode(),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PARTNER_DOC_ALL + "\",\"" + AclConstants.OPERATION_PARTNER_DOC_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody PartnerdocDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", partnerService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        boolean flag = partnerDocDetailsService.deleteVerification(entityDTO.getPartnerId());
        try {
            if (flag) {
                partnerDocDetailsService.getEntityForUpdateAndDelete(entityDTO.getPartnerId());
                partnerDocDetailsService.deleteEntity(entityDTO);
                PartnerdocDTO partnerdocDTO = (PartnerdocDTO) dataDTO.getData();
                if (partnerdocDTO != null) {
                    RESP_CODE = APIConstants.SUCCESS;
                    log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete PartnerDocDetails for : "+entityDTO.getPartnerId() + LogConstants.REQUEST_BY +partnerService .getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                }else {
                    dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                    dataDTO.setResponseMessage(DeleteContant.CUSTUMER_DOC_EXITS);
                    RESP_CODE = APIConstants.NOT_FOUND;
                    log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete PartnerDocDetails for : "+entityDTO.getPartnerId() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                }
            }
        } catch (Exception ex) {
            if (ex instanceof CustomValidationException){
                dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                dataDTO.setResponseMessage(ex.getMessage());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Delete PartnerDocDetails for : "+entityDTO.getPartnerId()+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return dataDTO;
            }
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Delete PartnerDocDetails for : "+entityDTO.getPartnerId()+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }




    @GetMapping(value = "/partnerPaymentHistory/{partnerId}")
    public GenericDataDTO getPaymentHistory(@PathVariable Integer partnerId, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());        String SUBMODULE = MODULE + " [PartnerPayment] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (partnerId == null) {
                genericDataDTO.setResponseMessage("ID not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch Payment History for partner Id : "+ partnerId+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Unable to find payment History For Customer"+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Partner partners = partnerService.get(partnerId,mvnoId);
            if (partners == null) {
                genericDataDTO.setResponseMessage("Records not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch Payment History for partner Id : "+ partnerId+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            List<PartnerCreditDocument> paymentHistories = partnerService.getByLcoId(partnerId);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(paymentHistories);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch Payment History for partner Id : "+ partnerId+  LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch Payment History for partner Id : "+ partnerId + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/getAllPartnerCreditList")
    public GenericDataDTO getAllPartnerCredit(@RequestBody PaginationRequestDTO paginationRequestDTO,HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [getAllPartnerBalance()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            PartnerPaymentService partnerPaymentService = SpringContext.getBean(PartnerPaymentService.class);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetch All partner Creditlist  " + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return partnerPaymentService.getAllPartnerCredit(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());

        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All partner Creditlist "+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @GetMapping(value = "/partnerInvoiceHistory/{partnerId}")
    public GenericDataDTO getPartnerInvoiceHistory(@PathVariable Integer partnerId, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [PartnerPayment] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (partnerId == null) {
                genericDataDTO.setResponseMessage("ID not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch PartnerInvoiceHistory for partnerID : "+partnerId + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Unable to Fetch PartnerInvoiceHistory"+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            Partner partners = partnerService.get(partnerId,mvnoId);
            if (partners == null) {
                genericDataDTO.setResponseMessage("Records not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch PartnerInvoiceHistory for partnerID : "+partnerId + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            List<PartnerDebitDocument> paymentHistories = partnerService.getByPartnerId(partnerId,mvnoId);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(paymentHistories);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch PartnerInvoiceHistory for partnerID : "+partnerId +  LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Fetch PartnerInvoiceHistory for partnerID : "+partnerId+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PostMapping("/getAllPartnerInvoiceList")
    public GenericDataDTO getAllPartnerInvoice(@RequestBody PaginationRequestDTO paginationRequestDTO,HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [getAllPartnerBalance()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch All partnerInvoice : " + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            PartnerPaymentService partnerPaymentService = SpringContext.getBean(PartnerPaymentService.class);
            return partnerPaymentService.getAllPartnerInvoice(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());

        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Fetch All PartnerInvoice : "+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @RequestMapping(value = "/document/download/{docId}/{partnerId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long docId, @PathVariable Integer partnerId,HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());        String SUBMODULE = MODULE + " [downloadDocument()] ";
        Resource resource = null;
        try {
            Partner partner = partnerService.get(partnerId,mvnoId);
            if (null == partner) {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"download Ducuments for partnerId : " +partnerId+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_INFO  + "Unable to Download document  for customer"+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
            PartnerdocDTO docDetailsDTO = partnerDocDetailsService.getEntityById(docId,mvnoId);
            if (null == docDetailsDTO) {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"download Ducuments for partnerId : " +partnerId+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_INFO  + "Unable to Download document  for customer"+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
            FileSystemService service = SpringContext.getBean(FileSystemService.class);
            resource = service.getPartnerDoc(partner.getName().trim(), docDetailsDTO.getUniquename());
            // resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null || resource.exists()) {
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"download Ducuments for partnerId : " +partnerId+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);

            } else {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"download Ducuments for partnerId : " +partnerId+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_INFO  + "Unable to Download document  for customer"+LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"download Ducuments for partnerId : " +partnerId+ LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            //ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return null;
    }

}
