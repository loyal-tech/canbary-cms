package com.adopt.apigw.modules.workflow.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.workflow.service.WorkflowAssignStaffMappingService;
import com.adopt.apigw.modules.workflowAudit.controller.WorkFlowAuditController;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static com.adopt.apigw.core.utillity.log.ApplicationLogger.logger;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.WORKFLOW)
public class WorkflowController {

    private final Logger log = LoggerFactory.getLogger(WorkflowController.class);
    private static String MODULE = " [WorkFlowAuditController] ";


    @Autowired
    WorkflowAssignStaffMappingService workflowAssignStaffMappingService;

    @Autowired
    private Tracer tracer;

    @GetMapping("/pickupworkflow")
    public GenericDataDTO pickUpWorkflow(@RequestParam(name = "eventName") String eventName,
                                         @RequestParam(name = "entityId") Integer entityId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        HashMap<String, Object> response = new HashMap<>();
        try {
            workflowAssignStaffMappingService.pickAndAssignWorkflow(eventName, entityId);
            Integer responseCode = APIConstants.SUCCESS;
            response.put(APIConstants.MESSAGE, "Workflow picked up successfully.");
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Workflow picked up event" + LogConstants.LOG_BY_NAME + eventName + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            genericDataDTO.setResponseMessage("Picked up successfully");
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            return genericDataDTO;
        } catch (CustomValidationException customValidationException) {
            Integer responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_TAG, customValidationException.getMessage());
            RESP_CODE = customValidationException.getErrCode();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Workflow picked up event" + LogConstants.LOG_BY_NAME + eventName + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customValidationException.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            genericDataDTO.setResponseMessage("You are not authorized to pick up the workflow.");
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            return genericDataDTO;
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Workflow picked up event" + LogConstants.LOG_BY_NAME + eventName + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            return genericDataDTO;
        }
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
