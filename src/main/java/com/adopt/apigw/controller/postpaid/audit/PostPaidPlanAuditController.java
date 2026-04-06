package com.adopt.apigw.controller.postpaid.audit;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import com.adopt.apigw.pojo.PaginationDetails;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class PostPaidPlanAuditController {

    @Autowired
    private PostPaidPlanAuditService planAuditService;
    @Autowired
    private Tracer tracer;
    private static final Logger log = LoggerFactory.getLogger(PostPaidPlanAuditController.class);

    @ApiOperation(value = "Get list of plan in the system")
    @PostMapping("/list")
    public ResponseEntity<Map<String, Object>> planAuditList(@RequestBody PaginationDTO paginationDTO, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        Integer responseCode = HttpStatus.NO_CONTENT.value();

        TraceContext traceContext = tracer.currentSpan().context();
        org.slf4j.MDC.put("type", "Fetch");
        org.slf4j.MDC.put("userName", getLoggedInUser().getUsername());
        org.slf4j.MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        org.slf4j.MDC.put("spanId", traceContext.spanIdString());
        try {
            PageableResponse pageableResponse = planAuditService.getPlanAudit(mvnoId, paginationDTO);
            response.put("planList", pageableResponse);
            response.put("responseMsg", LogConstants.LOG_SUCCESS);
            responseCode = HttpStatus.OK.value();
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetching plan list :,"   + LogConstants.REQUEST_BY +  MDC.get("userName") +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return (ResponseEntity<Map<String, Object>>) apiResponse(responseCode, response);
        } catch (Exception e) {
            responseCode =HttpStatus.BAD_REQUEST.value();
            response.put("responseMsg", e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching plan list," + LogConstants.REQUEST_BY +MDC.get("userName")+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return (ResponseEntity<Map<String, Object>>) apiResponse(responseCode, response);
        }

    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {

//        String SUBMODULE = MODULE + " [apiResponse()] ";
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

}
