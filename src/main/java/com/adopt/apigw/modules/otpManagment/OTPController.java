package com.adopt.apigw.modules.otpManagment;

import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.NetworkDevices.controller.NetworkDeviceController;
import com.adopt.apigw.pojo.api.GenerateOtpDto;
import com.adopt.apigw.pojo.api.ValidateOtpDto;
import com.adopt.apigw.service.common.OTPService;
import com.adopt.apigw.utils.APIConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Api(value = "OTP Management", description = "REST APIs related to Generate and Validate OTP !!!!", tags = "OTP")
@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.OTP)

public class OTPController extends ApiBaseController {

     private static final String OTP = "otp";

    private static String MODULE = " [APIController] ";

    @Autowired
    private OTPService otpService;
    private static final Logger logger = LoggerFactory.getLogger(OTPController.class);
    @ApiOperation(value = "Generate new OTP")
    @PostMapping("/generate")
    @ResponseBody
     public ResponseEntity<?> generateOTP(@RequestBody GenerateOtpDto generateOtp,@RequestParam("mvnoId") Integer mvnoId, HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO dataDTO = new GenericDataDTO();
         try {
             System.out.println("------------- Generating OTP -----------");
             otpService.generateOTP(generateOtp,mvnoId);
             RESP_CODE = APIConstants.SUCCESS;
             logger.info("Generating OTP is successfull for "+generateOtp.getEmailId()+" :  request: { From : {}}; Response : {{}}",request.getHeader("requestFrom"),RESP_CODE);
            response.put(OTP,"OTP has been generated successfully.");
         } catch (Exception ex) {
             ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
             ex.printStackTrace();
             RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
             //response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
             response.put(APIConstants.ERROR_TAG,"OTP profile is not configured to send OTP, Kindly connect to Administrator");
             logger.error("Unable Generate Otp for "+generateOtp.getEmailId()+"  :  request: { From : {}}; Response : {{};}Exception:{}", request.getHeader("requestFrom"),RESP_CODE,ex.getStackTrace());
         }
        MDC.remove("type");
         return apiResponse(RESP_CODE,response);
    }

    @ApiOperation(value = "Validate OTP")
    @PostMapping("/validate")
    @ResponseBody
     public ResponseEntity<?> validateOtp(@RequestBody ValidateOtpDto validateOtpDto, HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
         try {
            otpService.validateOTP(validateOtpDto);
            response.put(OTP, "OTP is valid.");
            RESP_CODE = APIConstants.SUCCESS;
             logger.info("Validating Otp For email "+validateOtpDto.getEmailId()+" is successfull :  request: { From : {}}; Response : {{}}",request.getHeader("requestFrom"),APIConstants.SUCCESS);
         }  catch (Exception ex) {
             ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
             ex.printStackTrace();
             RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
             response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
             response.put(APIConstants.MESSAGE,ex.getMessage());

             logger.error("Unable to validate OTP "+validateOtpDto.getEmailId()+"  :  request: { From : {}}; Response : {{};Exception:{}}", request.getHeader("requestFrom"),APIConstants.FAIL,ex.getStackTrace());
         }
        MDC.remove("type");
        return apiResponse(RESP_CODE,response);
        }

    @ApiOperation(value = "Validate OTP")
    @PostMapping("/pin")
    @ResponseBody
    public ResponseEntity<?> pinOTP(@RequestBody ValidateOtpDto validateOtpDto, HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            otpService.pinOTP(validateOtpDto);
            response.put(OTP, "OTP is valid.");
            RESP_CODE = APIConstants.SUCCESS;
            logger.info("Validating Otp For email "+validateOtpDto.getEmailId()+" is successfull :  request: { From : {}}; Response : {{}}",request.getHeader("requestFrom"),APIConstants.SUCCESS);
        }  catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            response.put(APIConstants.MESSAGE,ex.getMessage());

            logger.error("Unable to validate OTP "+validateOtpDto.getEmailId()+"  :  request: { From : {}}; Response : {{};Exception:{}}", request.getHeader("requestFrom"),APIConstants.FAIL,ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE,response);
    }
}

