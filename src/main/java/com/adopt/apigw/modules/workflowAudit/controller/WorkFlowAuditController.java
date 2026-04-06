package com.adopt.apigw.modules.workflowAudit.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.WorkflowAudit;
import com.adopt.apigw.modules.WorkFlowInProgressEntity.Entity.WorkFlowInProgressData;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.WorkflowAuditService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
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
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.WORKFLOW_AUDOT)
public class WorkFlowAuditController extends ApiBaseController {

    private final Logger log = LoggerFactory.getLogger(WorkFlowAuditController.class);
    private static String MODULE = " [WorkFlowAuditController] ";

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    private Tracer tracer;




    @PostMapping("/list")
    public GenericDataDTO getWorkFlowAuditList(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "entityId") Integer entityId, @RequestParam(name = "eventName") String eventName, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        HashMap<String, Object> response = new HashMap<>();
        Page<WorkflowAudit> workFlowAuditList = null;
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            WorkflowAuditService workflowAuditService = SpringContext.getBean(WorkflowAuditService.class);
            requestDTO = setDefaultPaginationValues(requestDTO);
            if (entityId != null) {
                genericDataDTO = workflowAuditService.getListByCustomerId(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), entityId, eventName);
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Workflow audit List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception ce) {
            //  ApplicationLogger.logger.error(MODULE + ce.getMessage(), ce);
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            genericDataDTO.setResponseMessage(ce.getMessage());
            ce.printStackTrace();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Workflow audit List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
        PAGE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
        PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).get(0).getValue());
        SORT_BY = clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).get(0).getValue();
        SORT_ORDER = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).get(0).getValue());
        MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());

        if (null == requestDTO.getPage()) requestDTO.setPage(PAGE);
        if (null == requestDTO.getPageSize()) requestDTO.setPageSize(PAGE_SIZE);
        if (null == requestDTO.getSortBy()) requestDTO.setSortBy(SORT_BY);
        if (null == requestDTO.getSortOrder()) requestDTO.setSortOrder(SORT_ORDER);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
            requestDTO.setPageSize(MAX_PAGE_SIZE);
        return requestDTO;
    }

    @PostMapping("/filter")
    public GenericDataDTO filterCase(@RequestParam(name = "filterColumn") String filterColumn, @RequestParam(name = "filterValue") String filterValue, @RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            WorkflowAuditService workflowAuditService = SpringContext.getBean(WorkflowAuditService.class);
            genericDataDTO = workflowAuditService.filterAudit(filterColumn, filterValue, requestDTO);
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Workflow audit" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.getStackTrace();
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Workflow audit" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
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
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
        }
        return loggedInUser;
    }

    @GetMapping("/getWorkflowInProgressData")
    public GenericDataDTO getWorkflowInProgressData(@RequestParam(name = "mvnoid", required = false) Integer mvnoid,HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO  genericDataDTO = new GenericDataDTO();
        try {
            WorkflowAuditService workflowAuditService = SpringContext.getBean(WorkflowAuditService.class);
            genericDataDTO.setDataList(workflowAuditService.getWorkflowInProgressData(mvnoid));
            Integer responseCode = APIConstants.SUCCESS;
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "get customer payment status record" + LogConstants.LOG_BY_NAME + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            genericDataDTO.setResponseCode(RESP_CODE);
            genericDataDTO.setResponseMessage(e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "add Customer Payment Status" + LogConstants.LOG_BY_NAME + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

    }


}
