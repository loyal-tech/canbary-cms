package com.adopt.apigw.controller.common.plan;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.Customers.LightPostpaidPlanDTO;
import com.adopt.apigw.modules.Customers.ServiceAreaFetchDTO;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.LoggedInUserService;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL+UrlConstants.PLAN)
public class PlanDetailsController extends ApiBaseController {
    private static String MODULE = " [PlanDetailsController] ";
    private final Logger log = LoggerFactory.getLogger(APIController.class);

    @Autowired
    private LoggedInUserService loggedInUserService;

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private Tracer tracer;


    @PostMapping(value = "/getAllPlansBySAandType")
    public ResponseEntity<?> GetAllPlansBySAAndType(@RequestBody ServiceAreaFetchDTO serviceAreaFetchDTO,
                                                             HttpServletRequest request,@RequestParam("mvnoId")  Integer mvnoId) {
        HashMap<String, Object> response = new HashMap<>();
        Integer responseCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        LoggedInUser loggedInUser = loggedInUserService.getLoggedInUser();
        MDC.put("userName",loggedInUser.getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            List<LightPostpaidPlanDTO> postpaidPlanList = postpaidPlanService.getPlanDetailsByServiceAreaSiteNameAndServiceId(serviceAreaFetchDTO.getSa(), serviceAreaFetchDTO.getServiceIds(), serviceAreaFetchDTO.getPlanGroupTypes(),mvnoId);
            if(!postpaidPlanList.isEmpty()) {
                response.put("planList", postpaidPlanList);
                response.put(APIConstants.MESSAGE, "plan fetch successfully");
                responseCode = APIConstants.SUCCESS;
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch All Plan By Service Area" + LogConstants.REQUEST_BY +loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            }
            if(postpaidPlanList.isEmpty()) {
                response.put(APIConstants.MESSAGE, "No record found");
                response.put("planList", postpaidPlanList);
                responseCode = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Fetch All Plan By Service Area" + LogConstants.REQUEST_BY + loggedInUser.getUsername()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE+responseCode);

            }
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All Plan By Service Area" + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+responseCode);
        } catch (Exception e) {
            responseCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All Plan By Service Area" + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(responseCode, response);
    }
}
