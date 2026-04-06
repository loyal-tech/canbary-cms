package com.adopt.apigw.modules.mvnoDocDetails.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.Mvno.mapper.MvnoMapper;
import com.adopt.apigw.modules.Mvno.model.MvnoDTO;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.Mvno.service.MvnoService;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
import com.adopt.apigw.modules.mvnoDocDetails.model.MvnoDocDetailsDTO;
import com.adopt.apigw.modules.mvnoDocDetails.service.DocDetailsService;
import com.adopt.apigw.pojo.api.CustomerCafAssignmentPojo;
import com.adopt.apigw.service.common.FileSystemService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.LoggedInUserService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.MVNO + UrlConstants.MVNO_DOC)
public class MvnoDocDetailsController {

    @Autowired
    private Tracer tracer;

    @Autowired
    private LoggedInUserService loggedInUserService;
    
    @Autowired
    private DocDetailsService docDetailsService;

    @Autowired
    private MvnoService mvnoService;
    @Autowired
    private MvnoMapper mvnoMapper;
    @Autowired
    private MvnoRepository mvnoRepository;

    private final String MODULE_NAME = "[MvnoDocDetailsController]";

    private String PATH;
    private static final Logger log = LoggerFactory.getLogger(MvnoDocDetailsController.class);

    @PostMapping(value = UrlConstants.UPLOAD_DOC, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO uploadDocFormvno(@RequestParam String docDetailsList, @RequestParam Long mvnoId
            , @RequestParam(value = "file", required = false) MultipartFile[] file, HttpServletRequest req) {
        LoggedInUser user = loggedInUserService.getLoggedInUser();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(LogConstants.HeaderConstants.REQUEST_TYPE, LogConstants.HeaderConstants.REQUEST_CREATE);
        MDC.put(LogConstants.HeaderConstants.REQUEST_USERNAME, user.getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put(LogConstants.HeaderConstants.REQUEST_SPANID, traceContext.spanIdString());
        Integer respCode = APIConstants.FAIL;
        String SUBMODULE = MODULE_NAME + " [updateDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String docNumber = " ";
        try {
            if (null != docDetailsList) {

                List<MvnoDocDetailsDTO> mvnoDocDetailsList = new ObjectMapper().registerModule(new JavaTimeModule())
                        .readValue(docDetailsList, new TypeReference<List<MvnoDocDetailsDTO>>() {
                        });

                if (null == mvnoDocDetailsList || 0 == mvnoDocDetailsList.size()) {
                    genericDataDTO.setResponseMessage("Please provide document details!");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    respCode = HttpStatus.NOT_ACCEPTABLE.value();
                    log.info(LogConstants.REQUEST_FROM + req.getHeader(LogConstants.HeaderConstants.REQUEST_FROM) + LogConstants.REQUEST_FOR + "create mvno document [" + docNumber +" ] " + LogConstants.REQUEST_BY + user.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "mvno document with same name already exist" + LogConstants.LOG_STATUS_CODE + respCode);
                    return genericDataDTO;
                }

                if (null != mvnoDocDetailsList && 0 < mvnoDocDetailsList.size()) {
                    docNumber = mvnoDocDetailsList.stream().map(MvnoDocDetailsDTO::getDocumentNumber).collect(Collectors.toList()).toString();
                    genericDataDTO.setResponseMessage("Documents uploaded successfully.");
                    genericDataDTO.setDataList(docDetailsService.uploadDocument(mvnoDocDetailsList, mvnoId, file));
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    respCode = APIConstants.SUCCESS;
                    log.info(LogConstants.REQUEST_FROM + req.getHeader(LogConstants.HeaderConstants.REQUEST_FROM) + LogConstants.REQUEST_FOR + "create mvno document [" + docNumber +" ] " + LogConstants.REQUEST_BY + user.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
                    return genericDataDTO;
                }
            }
            genericDataDTO.setResponseMessage("Please provide document details!");
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            log.info(LogConstants.REQUEST_FROM + req.getHeader(LogConstants.HeaderConstants.REQUEST_FROM) + LogConstants.REQUEST_FOR + "create mvno document [" + docNumber +" ] " + LogConstants.REQUEST_BY + user.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "mvno document with same name already exist" + LogConstants.LOG_STATUS_CODE + respCode);
            return genericDataDTO;
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(e.getMessage());
                respCode = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader(LogConstants.HeaderConstants.REQUEST_FROM) + LogConstants.REQUEST_FOR + "create mvno document " + docNumber + LogConstants.REQUEST_BY + user.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + respCode);
                return genericDataDTO;
            }
            if (e instanceof RuntimeException) {
                genericDataDTO.setResponseMessage(e.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader(LogConstants.HeaderConstants.REQUEST_FROM) + LogConstants.REQUEST_FOR + "create mvno document " + docNumber + LogConstants.REQUEST_BY + user.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "mvno document with same name already exist" + LogConstants.LOG_STATUS_CODE + respCode);
                return genericDataDTO;
            }
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            log.info(LogConstants.REQUEST_FROM + req.getHeader(LogConstants.HeaderConstants.REQUEST_FROM) + LogConstants.REQUEST_FOR + "create mvno document " + docNumber + LogConstants.REQUEST_BY + user.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } finally {
            MDC.remove(LogConstants.HeaderConstants.REQUEST_TYPE);
            MDC.remove(LogConstants.HeaderConstants.REQUEST_USERNAME);
            MDC.remove("traceId");
            MDC.remove(LogConstants.HeaderConstants.REQUEST_SPANID);
        }
        return genericDataDTO;
    }

    @GetMapping(value = UrlConstants.DOC_BY_MVNO + "/{mvnoId}")
    public GenericDataDTO getDocByMvno(@PathVariable Long mvnoId, HttpServletRequest req) throws Exception {
        MvnoDTO mvno = mvnoMapper.domainToDTO(mvnoRepository.findById(mvnoId).get(),new CycleAvoidingMappingContext());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        LoggedInUser loggedInUser = loggedInUserService.getLoggedInUser();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(LogConstants.HeaderConstants.REQUEST_TYPE, LogConstants.HeaderConstants.REQUEST_FETCH);
        MDC.put(LogConstants.HeaderConstants.REQUEST_USERNAME, loggedInUser.getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put(LogConstants.HeaderConstants.REQUEST_SPANID, traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            if (null == mvno) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Mvno");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader(LogConstants.HeaderConstants.REQUEST_FROM) + LogConstants.REQUEST_FOR + "fetch document for mvno " + mvno.getName() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader(LogConstants.HeaderConstants.REQUEST_FROM) + LogConstants.REQUEST_FOR + "fetch document for mvno " + mvno.getName() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            List<MvnoDocDetailsDTO> mvnoDocDetailsDTOS = docDetailsService.findDocsByEntityId(mvnoId);
            mvnoDocDetailsDTOS = mvnoDocDetailsDTOS.stream().peek(mvnoDocDetailsDTO -> mvnoDocDetailsDTO.setMvnoId(mvnoId.intValue())).collect(Collectors.toList());
            return GenericDataDTO.getGenericDataDTO(mvnoDocDetailsDTOS);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader(LogConstants.HeaderConstants.REQUEST_FROM) + LogConstants.REQUEST_FOR + "fetch document for mvno " + mvno.getName() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove(LogConstants.HeaderConstants.REQUEST_TYPE);
            MDC.remove(LogConstants.HeaderConstants.REQUEST_USERNAME);
            MDC.remove(LogConstants.TRACE_ID);
            MDC.remove(LogConstants.HeaderConstants.REQUEST_SPANID);
        }
        return genericDataDTO;
    }

    @PutMapping("/approveUploadMvnoDoc")
    public GenericDataDTO assignUploadMvnoDoc(@Valid @RequestParam Long docId, @RequestParam String remarks, @RequestParam Boolean isApproveRequest,
                                                  HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        LoggedInUser loggedInUser = loggedInUserService.getLoggedInUser();
        MDC.put("type", "Update");
        MDC.put("userName", loggedInUser.getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {

            if (docId != null) {
                dataDTO = docDetailsService.getDocApprovals(docId, isApproveRequest, remarks);

            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update approve customer document" + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update approve customer document" + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }
    
    @RequestMapping(value = "/document/download/{docId}/{mvnoId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long docId, @PathVariable Long mvnoId, HttpServletRequest req) throws Exception {
        String SUBMODULE = MODULE_NAME + " [downloadDocument()] ";
        Resource resource = null;
        LoggedInUser loggedInUser = loggedInUserService.getLoggedInUser();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put(LogConstants.HeaderConstants.REQUEST_TYPE, LogConstants.HeaderConstants.REQUEST_FETCH);
        MDC.put(LogConstants.HeaderConstants.REQUEST_USERNAME, loggedInUser.getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put(LogConstants.HeaderConstants.REQUEST_SPANID, traceContext.spanIdString());
        MvnoDTO mvnoDTO = mvnoMapper.domainToDTO(mvnoRepository.findById(mvnoId).get(),new CycleAvoidingMappingContext());
        try {
            if (null == mvnoDTO) {
                return ResponseEntity.notFound().build();
            }
            MvnoDocDetailsDTO docDetailsDTO = docDetailsService.getEntityById(docId);
            if (null == docDetailsDTO) {
                return ResponseEntity.notFound().build();
            }
            FileSystemService service = com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
            resource = service.getMvnoDoc(mvnoDTO, docDetailsDTO.getUniquename());
            //resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null && resource.getFilename() != null) {
                log.info(LogConstants.REQUEST_FROM + req.getHeader(LogConstants.HeaderConstants.REQUEST_FROM) + LogConstants.REQUEST_FOR + "Download document for mvno " + mvnoDTO.getName() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                log.error(LogConstants.REQUEST_FROM + req.getHeader(LogConstants.HeaderConstants.REQUEST_FROM) + LogConstants.REQUEST_FOR + "fetch document for mvno " + mvnoDTO.getName() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + "Unable to downloadDocument " + docId + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            log.error(LogConstants.REQUEST_FROM + req.getHeader(LogConstants.HeaderConstants.REQUEST_FROM) + LogConstants.REQUEST_FOR + "fetch document for mvno " + mvnoDTO.getName() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            // ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        org.slf4j.MDC.remove("type");
        return null;
    }
}
