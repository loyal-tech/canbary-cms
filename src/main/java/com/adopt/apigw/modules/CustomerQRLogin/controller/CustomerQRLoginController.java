package com.adopt.apigw.modules.CustomerQRLogin.controller;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.CustomerQRLogin.domain.CustomerQRLogin;
import com.adopt.apigw.modules.CustomerQRLogin.model.CustomerQRLoginDTO;
import com.adopt.apigw.modules.CustomerQRLogin.service.CustomerQRLoginService;
import com.adopt.apigw.modules.Voucher.module.APIResponseController;
import com.adopt.apigw.utils.APIConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(value = "CustomerQRLogin", description = "REST APIs related for Customer QR Login !!!!", tags = "CustomerQRLogin")
@RestController
@RequestMapping(UrlConstants.BASE_API_URL+UrlConstants.CUSTOMERQRLOGIN)
public class CustomerQRLoginController {

    @Autowired
    private CustomerQRLoginService customerQRLoginService;

    @Autowired
    private APIResponseController apiResponseController;

    @PostMapping( "/saveqrcode")
    @ApiModelProperty("An api for  save customer qr code")
    public ResponseEntity<?> saveCustomerQRcode(@RequestBody CustomerQRLoginDTO customerQRLoginDTO) {
        MDC.put("type", "crete");
        String SUBMODULE = " [customerQrCode()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            customerQRLoginService.validateSaveGeneratedCode(customerQRLoginDTO);
            CustomerQRLogin customerQRLogin =customerQRLoginService.saveGeneratedCode(customerQRLoginDTO);
            response.put("customerqrlogin" , customerQRLogin);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
        }
        MDC.remove("type");
        return apiResponseController.apiResponse(RESP_CODE, response);
    }

    @PutMapping( "/savecustomerlogin")
    @ApiModelProperty("An api for  save customer login with code")
    public ResponseEntity<?> saveCustomerLogin(@RequestBody CustomerQRLoginDTO customerQRLoginDTO) {
        MDC.put("type", "update");
        String SUBMODULE = " [customerQrCode()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            customerQRLoginService.validateSaveCustomerLogin(customerQRLoginDTO);
            CustomerQRLogin customerQRLogin =customerQRLoginService.savecustomerUsernamePassword(customerQRLoginDTO);
            if(customerQRLogin != null) {
                response.put("customerqrlogin", customerQRLogin);
                response.put(APIConstants.MESSAGE , "Web Device Login Successfully");
                RESP_CODE = APIConstants.SUCCESS;
            }
            else{
                response.put(APIConstants.MESSAGE , "QR code already used or expired.");
                RESP_CODE = APIConstants.SUCCESS;
            }

        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
        }
        MDC.remove("type");
        return apiResponseController.apiResponse(RESP_CODE, response);
    }

    @PostMapping("/getqrstatus")
    public ResponseEntity<?> getQrStatus(@RequestBody CustomerQRLoginDTO customerQRLoginDTO) throws Exception {
        MDC.put("type", "get");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        try {
            if (customerQRLoginDTO != null) {
                CustomerQRLogin customerQRLogin = customerQRLoginService.getQrStatus(customerQRLoginDTO.getCode());
                if(customerQRLogin != null){
                    response.put("qrstatus" , true);
                    response.put(APIConstants.MESSAGE,"qr status found");
                }
                else{
                    response.put("qrstatus" , false);
                    response.put(APIConstants.MESSAGE,"qr status not found");
                }
                response.put("qrresponse",customerQRLogin);
                RESP_CODE = APIConstants.SUCCESS;
                return apiResponseController.apiResponse(RESP_CODE , response);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.MESSAGE, ce.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.put(APIConstants.MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        }
        MDC.remove("type");
        return apiResponseController.apiResponse(RESP_CODE , response);
    }

    @PostMapping( "/expireqrcode")
    @ApiModelProperty("An api for expire customer qr code")
    public ResponseEntity<?> expireCustomerQRcode(@RequestBody CustomerQRLoginDTO customerQRLoginDTO) {
        MDC.put("type", "crete");
        String SUBMODULE = " [customerQrCode()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            customerQRLoginService.validateSaveGeneratedCode(customerQRLoginDTO);
            CustomerQRLogin customerQRLogin =customerQRLoginService.expireGeneratedCode(customerQRLoginDTO);
            if(customerQRLogin != null) {
                response.put("expiredcustomerqrlogin", customerQRLogin);
                RESP_CODE = APIConstants.SUCCESS;
                response.put(APIConstants.MESSAGE , "customer qr code has been expired");
            }
            else{
                response.put("expiredcustomerqrlogin", null);
                RESP_CODE = APIConstants.SUCCESS;
                response.put(APIConstants.MESSAGE , "customer qr code not found");
            }

        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
        }
        MDC.remove("type");
        return apiResponseController.apiResponse(RESP_CODE, response);
    }
}
