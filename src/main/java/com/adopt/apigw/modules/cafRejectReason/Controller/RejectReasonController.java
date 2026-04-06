package com.adopt.apigw.modules.cafRejectReason.Controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.MenuConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.dto.ValidationData;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.cafRejectReason.Entity.RejectReason;
import com.adopt.apigw.modules.cafRejectReason.DTO.RejectReasonDto;
import com.adopt.apigw.modules.cafRejectReason.Service.RejectReasonService;
import com.adopt.apigw.pojo.PaginationDetails;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Api(value = "RejectReason", description = "REST APIs related to RejectReason", tags = "RejectReason")
@RestController
@RequestMapping(UrlConstants.BASE_API_URL+"/caf/rejectReason")
public class RejectReasonController {

    private static String MODULE = " [RejectReasonController] ";

    private final Logger log = LoggerFactory.getLogger(RejectReasonController.class);

    private static final String REJECT_REASON = "rejectReason";
    private static final String REJECT_REASON_LIST = "rejectReasonList";

    @Autowired
    private RejectReasonService rejectReasonService;

    @Autowired
    private Tracer tracer;

   // @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.REJECTED_RESON_MASTER+ "\")")
    @ApiOperation(value = "Search RejectReason In System")
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody PaginationRequestDTO paginationRequestDTO,
                                    HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());

        Page<RejectReason> page = null;
        Integer responseCode = APIConstants.FAIL;
        try {
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            ValidationData validationData = validateSearchCriteria(paginationRequestDTO.getFilters());
            if (validationData.isValid()) {
                responseCode = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, validationData.getMessage());
                log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search rejectReason using keyword : "+paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + validationData.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
               return apiResponse(responseCode, response);
            }
            RejectReasonService rejectReasonService1 = SpringContext.getBean(RejectReasonService.class);
            page = rejectReasonService1.search1(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(),
                    paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
            Integer Response = 0;


            if (page.isEmpty()) {
                response.put(REJECT_REASON_LIST, page);
                response.put(APIConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(REJECT_REASON_LIST, page);
            }
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search rejectReason using keyword : " +paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search rejectReason using keyword : " +paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search rejectReason using keyword : " +paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } finally {
           MDC.remove("type");
           MDC.remove("userName");
           MDC.remove("traceId");
           MDC.remove("spanId");
        }
        return apiResponse(responseCode, response);
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
  //  @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.REJECTED_RESON_MASTER+ "\")")
    @GetMapping("/findById")
    @ApiOperation(value = "Get rejectReason detail based on the given rejectReason id")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<?> findById(@RequestParam("rejectReasonId") Long rejectReasonId,
                                      HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());        HashMap<String, Object> response = new HashMap<>();
        Integer responseCode = APIConstants.FAIL;
        RejectReason rejectReasons = null;
        try {
            RejectReasonDto rejectReason = this.rejectReasonService.findById(rejectReasonId);
            if (rejectReason == null) {
                response.put(APIConstants.MESSAGE,
                        "No record found for rejectReason with the given rejectReason id :" + rejectReasonId);
            } else {
                response.put(REJECT_REASON, rejectReason);
            }
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch RejectReason"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
      } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch RejectReason"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch RejectReason" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
      } finally {
           MDC.remove("type");
           MDC.remove("userName");
           MDC.remove("traceId");
           MDC.remove("spanId");
        }
        return apiResponse(responseCode, response);
    }
   // @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.REJECTED_RESON_CREATE+ "\")")
    @PostMapping("/save")
    @ApiOperation(value = "Add new rejectReason")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<?> addRejectReason(@RequestBody RejectReasonDto rejectReason,
                                             HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        Integer responseCode = APIConstants.FAIL;
        try {
            String authTokenHeader = request.getHeader("Authorization");
            // TODO: pass mvnoID manually 6/5/2025
            this.rejectReasonService.validateRequest(rejectReason, rejectReasonService.getMvnoIdFromCurrentStaff(null), CommonConstants.OPERATION_ADD);
            response.put(REJECT_REASON, this.rejectReasonService.saveRejectReason(rejectReason));
            response.put(APIConstants.MESSAGE, "RejectReason has been added successfully");
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create RejectReason"+LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create RejectReason"+LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create RejectReason"+LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } finally {
           MDC.remove("type");
           MDC.remove("userName");
           MDC.remove("traceId");
           MDC.remove("spanId");
        }
        return apiResponse(responseCode, response);
    }
 //   @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.REJECTED_RESON_EDIT+ "\")")
    @PutMapping("/update/{id}")
    @ApiOperation(value = "Update eixsting rejectReason data based on the rejectReason id")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<?> updateRejectReason(@PathVariable Long id,
                                                @RequestBody RejectReasonDto rejectReason, HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());

        Integer responseCode = APIConstants.FAIL;
        try {
            String authTokenHeader = request.getHeader("Authorization");
            rejectReasonService.getEntityForUpdateAndDelete(id,rejectReason.getMvnoId());
            rejectReason.setId(id);
            // TODO: pass mvnoID manually 6/5/2025
            this.rejectReasonService.validateRequest(rejectReason, rejectReason.getMvnoId(), CommonConstants.OPERATION_UPDATE);
            response.put(REJECT_REASON, this.rejectReasonService.updateRejectReason(rejectReason, request));
            response.put(APIConstants.MESSAGE, "RejectReason has been updated successfully");
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update RejectReason"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (CustomValidationException e) {
            responseCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update RejectReason"+LogConstants.LOG_BY_NAME +rejectReason.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update RejectReason"+LogConstants.LOG_BY_NAME +rejectReason.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } finally {
           MDC.remove("type");
           MDC.remove("userName");
           MDC.remove("traceId");
           MDC.remove("spanId");
        }
        return apiResponse(responseCode, response);
    }
   // @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.REJECTED_RESON_DELETE+ "\")")
    @DeleteMapping("/delete")
    @ApiOperation(value = "Delete existing rejectReason based on rejectReason id")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','deleteAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<?> deleteCustomer(@RequestParam(name = "rejectReasonId", required = true) Long rejectReasonId, HttpServletRequest request,@RequestParam("mvnoId") Integer mvnoId
    ) {
        HashMap<String, Object> response = new HashMap<>();
        Integer responseCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        RejectReasonDto rejectReason = this.rejectReasonService.findById(rejectReasonId);
        try {
            rejectReasonService.getEntityForUpdateAndDelete(rejectReasonId,mvnoId);
            if (rejectReason == null) {
                response.put(APIConstants.MESSAGE,
                        "No record found for rejectReason with the given rejectReason id :" + rejectReasonId);
            } else {
                this.rejectReasonService.deleteRejectReason(rejectReasonId);
                response.put(APIConstants.MESSAGE, "RejectReason has been deleted successfully");
            }
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete RejectReason"+LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
       } catch (CustomValidationException e) {
            responseCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete RejectReason"+LogConstants.LOG_BY_NAME +rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"delete RejectReason"+LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
       } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(responseCode, response);
    }
//    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.REJECTED_RESON_MASTER+ "\")")
    @GetMapping("/all")
    @ApiOperation(value = "Get list of rejectReason in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<?> findAll(@RequestParam(value = "page", defaultValue = "1", required = false) Integer page, @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize, HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        Integer responseCode = APIConstants.FAIL;
        try {
            String authTokenHeader = request.getHeader("Authorization");
            PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
            paginationRequestDTO.setPage(page);
            paginationRequestDTO.setPageSize(pageSize);
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            Page<RejectReasonDto> rejectReasonDto = this.rejectReasonService.findAll(paginationRequestDTO);
            if (rejectReasonDto.isEmpty()) {
                response.put(REJECT_REASON_LIST, rejectReasonDto);
                response.put(APIConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(REJECT_REASON_LIST, rejectReasonDto);
            }
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"fetch All RejectReason list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+responseCode);
            return apiResponse(responseCode, response);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch All RejectReason list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+responseCode);
        } catch (Exception e) {
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch All RejectReason list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(responseCode, response);
    }

    @GetMapping("/allRejectedReasonsList")
    @ApiOperation(value = "Get list of rejectReason in the system")
    public ResponseEntity<?> findAllRejectedReasonsList(HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());

        Integer responseCode = APIConstants.FAIL;
        List<RejectReason> rejectReasonList = new ArrayList<>();
        try {
            rejectReasonList = rejectReasonService.findAllRejectedReasonsList();

            if (rejectReasonList.isEmpty()) {
                response.put(REJECT_REASON_LIST, new ArrayList<>());
                response.put(APIConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(REJECT_REASON_LIST, rejectReasonList);
            }
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"fetch All rejectReason list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+responseCode);
            return apiResponse(responseCode, response);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch All rejectReason list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+responseCode);
        } catch (Exception e) {
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch All rejectReason list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(responseCode, response);
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
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
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

    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {


        if (null == requestDTO.getPage())
            requestDTO.setPage(1);
        if (null == requestDTO.getPageSize())
            requestDTO.setPageSize(5);
        if (null == requestDTO.getSortBy())
            requestDTO.setSortBy("createdate");
        if (null == requestDTO.getSortOrder())
            requestDTO.setSortOrder(0);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > 10)
            requestDTO.setPageSize(requestDTO.getPageSize());

        return requestDTO;
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


}
