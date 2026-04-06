package com.adopt.apigw.modules.otpManagment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.model.common.OTPManagement;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.pojo.api.OTPManagementDto;
import com.adopt.apigw.pojo.api.UpdateOTPManagementDto;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.OTPManagmentService;
import com.adopt.apigw.utils.APIConstants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Api(value = "OTP Profile Management", description = "REST APIs related to OTP Management Entity!!!!", tags = "OTP Profile")
@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.OTP_MANAGMENT)

public class OTPManagementController extends ApiBaseController {

    private static final String OTP_PROFILE = "otpProfile";
    private static final String OTP_PROFILE_LIST = "otpProfileList";
    private static String MODULE = " [APIController] ";


    @Autowired
    OTPManagmentService otpManagementService;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    private static final Logger logger = LoggerFactory.getLogger(OTPManagementController.class);
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OTP_ALL + "\",\"" + AclConstants.OPERATION_OTP_ADD + "\")")
    @ApiOperation(value = "Add new OTP profile")
    @PostMapping
     public ResponseEntity<?> add(@RequestBody OTPManagementDto otpProfile, @RequestParam("mvnoId") Integer mvnoId, HttpServletRequest request) {
        MDC.put("type", "create");
         HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
         try {
            OTPManagement otpManagement=otpManagementService.saveOtpProfile(otpProfile,mvnoId,request);
            response.put(OTP_PROFILE,otpManagement);
            RESP_CODE = APIConstants.SUCCESS;
             logger.info("creating OtpProfile with name "+otpProfile.getProfileName()+" is successfull :  request: { From : {}}; Response : {{}}", request.getHeader("requestFrom"),APIConstants.SUCCESS);
        } catch (Exception ex) {
      //      ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            response.put(APIConstants.ERROR_MESSAGE,ex.getMessage());
             logger.error("Unable to create OtpProfile with name "+otpProfile.getProfileName()+"  :  request: { From : {}}; Response : {{};Exception:{}}", request.getHeader("requestFrom"), APIConstants.FAIL,ex.getStackTrace());
        }
        MDC.remove("type");
         return apiResponse(RESP_CODE,response);
    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OTP_ALL + "\",\"" + AclConstants.OPERATION_OTP_EDIT + "\")")
    @ApiOperation(value = "Update existing otp profile based on the given profile id")
    @PutMapping("/{profileId}")
     public ResponseEntity<?> update(@Valid @RequestBody UpdateOTPManagementDto optManagement, HttpServletRequest request, @RequestParam("mvnoId") Integer mvnoId) {
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Update");
        Integer RESP_CODE = APIConstants.FAIL;

        try {


            OTPManagement otpManagement = otpManagementService.updateOtpProfile(optManagement,request,mvnoId);
            response.put(OTP_PROFILE, otpManagement);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info("updating otp profile with oldname "+otpManagement.getProfileName()+" to  "+otpManagement.getProfileName()+" is successfull :  request: { From : {}}; Response : {{}}", request.getHeader("requestFrom"),APIConstants.SUCCESS);
        } catch (Exception ex) {
       //     ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.error("Unable to Update Otp management With profile "+optManagement.getProfileId()+"  :  request: { From : {}}; Response : {{};Exception:{}}",request.getHeader("requestFrom"),APIConstants.FAIL,ex.getStackTrace() ); ;
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE,response);

    }



//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OTP_ALL + "\",\"" + AclConstants.OPERATION_OTP_DELETE + "\")")
    @ApiOperation(value = "Delete existing otp profile based on the given profile id")
    @DeleteMapping("/{profileId}")
    public ResponseEntity<?> delete(@PathVariable(name = "profileId", required = true) Long profileId, HttpServletRequest request, @RequestParam("mvnoId") Integer mvnoId) {
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Delete");
        Integer RESP_CODE = APIConstants.FAIL;

        try {
            otpManagementService.deleteOtpProfileById(profileId,mvnoId);
            response.put(OTP_PROFILE,"Profile has been deleted successfully");
            RESP_CODE = APIConstants.SUCCESS;
            logger.info("Deleting otp Profile with Id "+profileId+" is successfull :  request: { From : {}}; Response : {{}}", request.getHeader("requestFrom"),APIConstants.SUCCESS);

        } catch (Exception ex) {
       //     ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.error("Unable to Delete otp profile with id "+profileId+" :  request: { From : {}}; Response : {{};exception:{}}",request.getHeader("requestFrom"),APIConstants.FAIL,ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE,response);

    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OTP_ALL + "\",\"" + AclConstants.OPERATION_OTP_VIEW + "\")")
    @ApiOperation(value = "Get otp profile based on the given profile id")
    @GetMapping("/{profileIds}")
     public ResponseEntity<?> get(@PathVariable(name = "profileIds", required = true) Long profileId,
                                  @RequestParam("mvnoId") Integer mvnoId) {
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;

        try {
            OTPManagement otpManagement=otpManagementService.getOtpProfileById(profileId,mvnoId);
             response.put(OTP_PROFILE, otpManagement);
             RESP_CODE = APIConstants.SUCCESS;
            logger.info("Fetching Otp profile with name "+otpManagement.getProfileName()+" is successfull :  request: { From : {}}; Response : {{}}", getModuleNameForLog(),RESP_CODE,response);
         } catch (Exception ex) {
       ///     ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("unable to fetch profile ids with name "+profileId+" :  request: { From : {}}; Response : {{}exception:{};}", getModuleNameForLog(),RESP_CODE,ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE,response);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OTP_ALL + "\",\"" + AclConstants.OPERATION_OTP_VIEW + "\")")
    @ApiOperation(value = "Get otp profile based on the given profile name")
    @GetMapping(value = {"/profile/{name}"})
    public ResponseEntity<?> getByName(@PathVariable(name = "name", required = true) String name,@RequestParam (name = "mvnoId") Integer mvnoId) {
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;

        try {
            List<OTPManagement> otpManagementList=otpManagementService.getOtpProfileByProfileName(name,mvnoId);

            if (otpManagementList.isEmpty()) {
                response.put(APIConstants.MESSAGE, "No Records Found!");
                response.put("otpManagementList", new ArrayList<>());
                RESP_CODE = APIConstants.NULL_VALUE;
                logger.error("Unable to fetch Otp Profile  :  request: { From : {}}; Response : {{};}", getModuleNameForLog(),RESP_CODE,response);
                return apiResponse(RESP_CODE , response);
            }
            response.put(OTP_PROFILE_LIST, otpManagementList);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info("Fetching Otp Profile with anme "+name+" is successfull :  request: { From : {}}; Response : {{}}", getModuleNameForLog(),RESP_CODE,response);
        } catch (Exception ex) {
        //    ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to Fetch OTP profile With name "+name+"  :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(), RESP_CODE,response,ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE,response);
    }



//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OTP_ALL + "\",\"" + AclConstants.OPERATION_OTP_VIEW + "\")")
    @ApiOperation(value = "Get otp profile based on the given profile id")
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(HttpServletRequest request,Integer mvnoId) {
        MDC.put("type", "Fetch");
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;

        try {
            List<OTPManagement> otpManagementList=otpManagementService.findAll(mvnoId);

            response.put(OTP_PROFILE_LIST, otpManagementList);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info("fetching Otp Profile is successfull :  request: { From : {}}; Response : {{}}",request.getHeader("requestFrom"),APIConstants.SUCCESS);
        } catch (Exception ex) {
         //   ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to fetch Otp Profile :  request: { From : {}}; Response : {{};Exception:{}}",request.getHeader("requestFrom"),APIConstants.FAIL,ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE,response);
    }
    public String getModuleNameForLog() {
        return "[OtpManagementController]";
    }
}
