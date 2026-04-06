package com.adopt.apigw.controller.api;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.schedulers.QuotaResetScheduler;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Api(value = "Scheduler", description = "REST APIs related to Scheduler!!!!", tags = "Scheduler", hidden = true)
@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class SchedulerTestController extends ApiBaseController {
    private static final Logger log = LoggerFactory.getLogger(SchedulerTestController.class);

    @Autowired
    private QuotaResetScheduler quotaResetScheduler;

    @GetMapping("/scheduler/quotaReset")
    public ResponseEntity<?> liveSessionPurgeScheduler(HttpServletRequest request) {

        HashMap<String, Object> response = new HashMap<>();
        try {
            quotaResetScheduler.customerPlanSchedule();
            response.put("message", "Quota Reset Prune Job Scheduler Run Successfully.");
            return apiResponse(200, response);
        } catch (Exception e) {
            log.error("Error in Quota reset : "+e.getMessage());
            return apiResponse(500, response);
        }
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        return apiResponse(responseCode, response, null);
    }
}
