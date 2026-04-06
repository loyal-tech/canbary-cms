package com.adopt.apigw.service.BulkService;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class DownloadBulkPlanController {

    @Autowired
    private DownloadBulkPlanService bulkPlan;

    @Autowired
    private UploadBulkPlanService uploadBulkData;

    @CrossOrigin(origins = "*")
    @GetMapping("/downloadPlanUpdatebulk")
    public ResponseEntity<Object> getBulkPlan() throws Exception {

        try {
            Integer mvnoIdFromCurrentStaff = getLoggedInUser().getMvnoId();
            Resource resource = bulkPlan.writePostpaidPlansToExcel(mvnoIdFromCurrentStaff);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "bulk_" + "source" + ".xlsx\"")
                    .body(resource);

        } catch (Exception e) {
            HttpStatus status = HttpStatus.EXPECTATION_FAILED;
            return ResponseEntity.ok(
                    Response.builder()
                            .status(status.value())
                            .body((Response.Body) null)
                            .reason(e.getMessage())
                            .build()
            );
        }
    }


    @CrossOrigin(origins = "*")
    @PostMapping(value = "/uploadPlanUpdatebulk")
    public ResponseEntity<Object> uploadBulkPlan(@RequestParam(value = "file") MultipartFile file){
        Map<String, String> response = new HashMap<>();
        try {

            LoggedInUser loggedInUser = getLoggedInUser();

            String save = uploadBulkData.uploadBulkData(file, loggedInUser.getMvnoId(), loggedInUser.getUsername());
            response.put("message", "Plan updated successfully");
            response.put("details", save);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        }catch (CustomValidationException ex){
            HttpStatus status = HttpStatus.EXPECTATION_FAILED;
            response.put("message", "Error Occured while validating Data.");
            response.put("statuscode",String.valueOf(status.value()));
            response.put("cause", ex.getMessage());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
        catch (Exception e) {
            HttpStatus status = HttpStatus.EXPECTATION_FAILED;
            response.put("message", "Error Occured while validating Data.");
            response.put("statuscode",String.valueOf(status.value()));
            response.put("cause", e.getMessage());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }

//    public Integer getMvnoIdFromCurrentStaff() {
//        Integer mvnoId = null;
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
//            }
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff response:{},exception:{}", APIConstants.FAIL, e.getStackTrace());
//        }
//        return mvnoId;
//    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getLoggedInUser response:{},exception:{}", APIConstants.FAIL, e.getStackTrace());
        }
        return loggedInUser;
    }
}
