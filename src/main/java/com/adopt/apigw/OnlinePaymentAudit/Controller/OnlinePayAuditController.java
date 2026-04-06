package com.adopt.apigw.OnlinePaymentAudit.Controller;

import brave.Tracer;

import brave.propagation.TraceContext;

import com.adopt.apigw.OnlinePaymentAudit.Service.OnlinePayAuditService;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.CustomerPayment;
import com.adopt.apigw.service.common.ShorterService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL+UrlConstants.ONLINE_PAY_AUDIT)
public class OnlinePayAuditController extends ApiBaseController {


    private static String MODULE = " [OnlinePayAuditController] ";

    @Autowired
    private Tracer tracer;

@Autowired
private ShorterService shorterService;

    @Autowired
    private OnlinePayAuditService onlinePayAuditService;

    private final Logger log = LoggerFactory.getLogger(APIController.class);



    @PostMapping("/all")
    ResponseEntity<?> getAllOnlinePaymentAudit(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest request,@RequestParam("mvnoId") Integer mvnoId)throws Exception{
        HashMap<String,Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try{
            Page<CustomerPayment> onlinePayAuditList = onlinePayAuditService.getOnlinePayAuditList(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),paginationRequestDTO.getSortOrder(),paginationRequestDTO.getFilters(),mvnoId);
            if (!onlinePayAuditList.isEmpty()) {
                response.put("onlineAuditData", onlinePayAuditList.getContent());
                response.put("totalRecords",onlinePayAuditList.getTotalElements());
                response.put("Status", "Success");
                response.put("message", "success");
                response.put("ResponseCode", APIConstants.SUCCESS);
            } else {
                response.put("onlineAuditData", new ArrayList<>());
                response.put("totalRecords",0);
                response.put("message", "success");
                response.put("Status", "No Record Found !!");
                response.put("ResponseCode", APIConstants.NOT_FOUND);
            }

            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Online Payment Audit List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        }catch (Exception e){
            response.put("Status", "Failed");
            response.put("ResponseCode", APIConstants.FAIL);
            response.put("message", e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Online Payment Audit List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        }
        return apiResponse(RESP_CODE,response);
    }



    @GetMapping("/allByCustId")
    ResponseEntity<?> getAllOnlinePaymentAuditForCustomer(@RequestParam("custId") Integer custId, HttpServletRequest request)throws Exception{
        HashMap<String,Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try{
            List<CustomerPayment> onlinePayAuditList = onlinePayAuditService.getOnlinePayAuditListByCustId(custId);
            if (!onlinePayAuditList.isEmpty()) {
                response.put("onlineAuditData", onlinePayAuditList);
                response.put("Status", "Success");
                response.put("message", "success");
                response.put("ResponseCode", APIConstants.SUCCESS);
            } else {
                response.put("onlineAuditData", new ArrayList<>());
                response.put("message", "success");
                response.put("Status", "No Record Found !!");
                response.put("ResponseCode", APIConstants.NOT_FOUND);
            }

            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Online Payment Audit List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        }catch (Exception e){
            response.put("Status", "Failed");
            response.put("ResponseCode", APIConstants.FAIL);
            response.put("message", e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Online Payment Audit List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        }
        return apiResponse(RESP_CODE,response);
    }

    @GetMapping("/allByPartnerId")
    ResponseEntity<?> getAllOnlinePaymentAuditForPartner(@RequestParam("partnerId") Integer partnerId, HttpServletRequest request)throws Exception{
        HashMap<String,Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try{
            List<CustomerPayment> onlinePayAuditList = onlinePayAuditService.getOnlinePayAuditListByPartner(partnerId);
            if (!onlinePayAuditList.isEmpty()) {
                response.put("onlineAuditData", onlinePayAuditList);
                response.put("Status", "Success");
                response.put("message", "success");
                response.put("ResponseCode", APIConstants.SUCCESS);
            } else {
                response.put("onlineAuditData", new ArrayList<>());
                response.put("message", "success");
                response.put("Status", "No Record Found !!");
                response.put("ResponseCode", APIConstants.NOT_FOUND);
            }

            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Online Payment Audit List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        }catch (Exception e){
            response.put("Status", "Failed");
            response.put("ResponseCode", APIConstants.FAIL);
            response.put("message", e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Online Payment Audit List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        }
        return apiResponse(RESP_CODE,response);
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
    @GetMapping("/setUsedHash")
    public ResponseEntity<Map<String, Object>> getPaymentDetailsByHash(
            @RequestParam(name = "hash") String hash, HttpServletRequest req) {

        MDC.put("type", "Fetch");
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;

        try {
            Map<String, Object> serviceResponse = shorterService.setUsedHash(hash);
            RESP_CODE = (Integer) serviceResponse.get("status");
            response.putAll(serviceResponse);
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "Payment Details found by hash " +
                    LogConstants.REQUEST_BY + hash +
                    LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            RESP_CODE = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "Payment Details found by hash " +
                    LogConstants.REQUEST_BY + hash +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + e.getMessage() +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return ResponseEntity.ok(response);
    }

}
