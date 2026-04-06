package com.adopt.apigw.modules.CustomerFeedback.controller;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.CustomerFeedback.domain.CustomerFeedback;
import com.adopt.apigw.modules.CustomerFeedback.model.CustomerFeedbackDTO;
import com.adopt.apigw.modules.CustomerFeedback.service.CustomerFeedbackService;
import com.adopt.apigw.modules.Voucher.module.APIResponseController;
import com.adopt.apigw.utils.APIConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@Api(value = "CustomerFeedback", description = "REST APIs related for Customer Feedback!!!!", tags = "CustomerFeedback")
@RestController
@RequestMapping(UrlConstants.BASE_API_URL+UrlConstants.CUSTOMERFEEDBACK)
public class CustomerFeedbackController {

    @Autowired
    private CustomerFeedbackService customerFeedbackService;

    @Autowired
    private APIResponseController apiResponseController;

    @PostMapping( "/create")
    @ApiModelProperty("An api for create save customer feedback")
    public ResponseEntity<?> saveCustomerFeedback(@Valid @RequestBody List<CustomerFeedbackDTO> customerFeedbackDTO) {
        MDC.put("type", "crete");
        String SUBMODULE = " [customerRating()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            customerFeedbackService.validateSaveRequest(customerFeedbackDTO);
            List<CustomerFeedbackDTO> returnCustomerUpdateDTO = customerFeedbackService.saveCustomerFeedback(customerFeedbackDTO);
            response.put("customerFeedback", returnCustomerUpdateDTO);
            response.put("message", "Customer Feedback add successfully");
            RESP_CODE = APIConstants.SUCCESS;
            //ApplicationLogger.logger.info("customerRating   " + returnCustomerUpdateDTO.getRating() + ":  request: { From : {}}; Response : {{}}", "CustomerFeedback", response, RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            System.out.println(ce.getMessage());
            //ApplicationLogger.logger.error("Unable to save customer feedback " + customerFeedbackDTO.getCustId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "CustomerFeedback", RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            System.out.println(ex.getMessage());
            //ApplicationLogger.logger.error("Unable to Unable to rate ticket " + customerFeedbackDTO.getCustId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "CustomerFeedback", RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponseController.apiResponse(RESP_CODE, response);
    }

    @PostMapping( "/update")
    @ApiModelProperty("An api for update customer update")
    public ResponseEntity<?> updateCustomerFeedback(@Valid @RequestBody CustomerFeedbackDTO customerFeedbackDTO) {
        MDC.put("type", "update");
        String SUBMODULE = " [customerRating()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            customerFeedbackService.validateUpdateRequest(customerFeedbackDTO);
            CustomerFeedbackDTO returnCustomerUpdateDTO = customerFeedbackService.updateCustomerFeedBack(customerFeedbackDTO);
            response.put("customerFeedback", returnCustomerUpdateDTO);
            response.put("message", "Customer Feedback update successfully");
            RESP_CODE = APIConstants.SUCCESS;
            ApplicationLogger.logger.info("customerRating   " + returnCustomerUpdateDTO.getRating() + ":  request: { From : {}}; Response : {{}}", "CustomerFeedback", response, RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            ApplicationLogger.logger.error("Unable to update customer feedback " + customerFeedbackDTO.getCustId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "CustomerFeedback", RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            ApplicationLogger.logger.error("Unable to Update to rate ticket " + customerFeedbackDTO.getCustId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "CustomerFeedback", RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponseController.apiResponse(RESP_CODE, response);
    }

    @GetMapping( "/findByCustomerId")
    @ApiModelProperty("An API for find customer feedback by customer id")
    public ResponseEntity<?> findCustomerFeedbackByCustomerId(@RequestParam Long customerId) {
        MDC.put("type", "find");
        String SUBMODULE = " [customerRating()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            CustomerFeedbackDTO returnCustomerDTO = customerFeedbackService.findCustomerFeedBackByCustomerId(customerId);
            if(returnCustomerDTO != null) {
                RESP_CODE = APIConstants.SUCCESS;
                response.put("customerFeedback", returnCustomerDTO);
                response.put("message", "Customer Feedback Find successfully");
            }
            else{
                RESP_CODE = 204;
                response.put("message", "Customer Feedback not found By customer");
            }
            ApplicationLogger.logger.info("customer feedback  for  " + customerId + ":  request: { From : {}}; Response : {{}}", "CustomerFeedback", response, RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            ApplicationLogger.logger.error("Unable to find customer feedback  by" + customerId + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "CustomerFeedback", RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            ApplicationLogger.logger.error("Unable to find customer feedback by " + customerId + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "CustomerFeedback", RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponseController.apiResponse(RESP_CODE, response);
    }

    @GetMapping( "/delete")
    @ApiModelProperty("Soft delete Customer Feedback by  Id")
    public ResponseEntity<?> deleteCustomerFeedback(@RequestParam Long customerFeedbackId) {
        MDC.put("type", "delete");
        String SUBMODULE = " [customerRating()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            RESP_CODE = APIConstants.SUCCESS;
            customerFeedbackService.deleteCustomerFeedBack(customerFeedbackId);
            response.put("message" , "Customer Feedback delete Successfully");
            ApplicationLogger.logger.info("customer feedback delete by customer  feedback id" + customerFeedbackId + ":  request: { From : {}}; Response : {{}}", "CustomerFeedback", response, RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            ApplicationLogger.logger.error("Error delete customer feedback customer feedback id" + customerFeedbackId + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "CustomerFeedback", RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            ApplicationLogger.logger.error("Error delete customer feedback " + customerFeedbackId + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "CustomerFeedback", RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponseController.apiResponse(RESP_CODE, response);
    }


    @GetMapping( "/getFormFrequency")
    @ApiModelProperty("Get feedback form frequency ")
    public ResponseEntity<?> feedbackFormFrequency(@RequestParam Integer custid) {
        MDC.put("type", "delete");
        String SUBMODULE = " [customerRating()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            RESP_CODE = APIConstants.SUCCESS;
            response.put("IsFormShow",customerFeedbackService.checkCustomerfeedbackFrequecny(custid));
            //response.put("message" , "Customer Feedback Frequncy De");
            ApplicationLogger.logger.info("customer feedback frequency by customer id" + custid.toString() + ":  request: { From : {}}; Response : {{}}", "CustomerFeedback", response, RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            ApplicationLogger.logger.error("Error while fetching customer feedback frequency" + custid.toString() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "CustomerFeedback", RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            ApplicationLogger.logger.error("Error while fetching customer feedback frequency" + custid.toString() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "CustomerFeedback", RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponseController.apiResponse(RESP_CODE, response);
    }


    @GetMapping( "/getFeedBackDetails")
    @ApiModelProperty("Get feedback form frequency ")
    public ResponseEntity<?> getFeedBackDetails(@RequestParam Integer custid) {
        MDC.put("type", "delete");
        String SUBMODULE = " [customerRating()] ";
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            RESP_CODE = APIConstants.SUCCESS;
            response.put("feedBackDetails",customerFeedbackService.getAvragefeedbackBasedOnEvent(custid));
            //response.put("message" , "Customer Feedback Frequncy De");
            ApplicationLogger.logger.info("customer feedback details by customer id" + custid.toString() + ":  request: { From : {}}; Response : {{}}", "CustomerFeedback", response, RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            ApplicationLogger.logger.error("Error while fetching feedback details " + custid.toString() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "CustomerFeedback", RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            ApplicationLogger.logger.error("Error while fetching customer feedback details" + custid.toString() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "CustomerFeedback", RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponseController.apiResponse(RESP_CODE, response);
    }



}
