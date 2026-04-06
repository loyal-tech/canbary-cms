package com.adopt.apigw.modules.planUpdate.controller;

import com.adopt.apigw.utils.UtilsCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.ValidationData;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.planUpdate.model.CustomerPackageDTO;
import com.adopt.apigw.modules.planUpdate.service.CustomerPackageService;
import com.adopt.apigw.service.common.CustomersService;

import java.util.List;

@RestController
@RequestMapping(value = UrlConstants.BASE_API_URL + UrlConstants.CUSTOMER_PACKAGE)
public class CustomerPackageController  extends ExBaseAbstractController<CustomerPackageDTO> {
    private static String MODULE = " [CustomerPackageController] ";
    @Autowired
    private CustomerPackageService customerPackageService;

    @Autowired
    private CustomersService customersService;

    public CustomerPackageController(CustomerPackageService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "CustomerPackageController";
    }
    private static final Logger logger = LoggerFactory.getLogger(CustomerPackageController.class);
    @PostMapping(value = "/updateExpiry", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO updateExpiryDate(@RequestBody CustomerPackageDTO entityDTO, BindingResult result, Authentication authentication) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
      //  logger.info("Updating Expiry Date   "+requestDTO.getCustId()+" is successfull :   Response : {{}}",genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
        try {
            if (result.hasErrors()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
                logger.error("unable to update Expiry date to  plan id "+entityDTO.getPlanId()+":   Response : {{}{};}",genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            CustomerPackageDTO customerPackageDTO = customerPackageService.getEntityById(entityDTO.getCustPackageId(),entityDTO.getMvnoId());
            String updatedValues = UtilsCommon.getUpdatedDiff(customerPackageDTO,entityDTO);
            ValidationData validation = validateSave(customerPackageDTO);
            if (!validation.isValid()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                logger.error("unable to update Expiry date to  plan id "+entityDTO.getPlanId()+" :   Response : {{}{};}",genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            customerPackageDTO.setExpiryDate(entityDTO.getExpiryDate());
            genericDataDTO.setData(customerPackageService.updateEntity(customerPackageDTO));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Updating Expiry date  for plan "+updatedValues +" is successfull :   Response : {{}}",genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
            genericDataDTO.setTotalRecords(1);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Failed to save data. Please try after some time");
            logger.error("unable to Update Expiry date to plan id : "+entityDTO.getPlanId()+" :   Response : {{}{};exception:{}}",genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode(),ex.getStackTrace());
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/updateSpeed", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO updateSpeed(@RequestBody CustomerPackageDTO entityDTO, BindingResult result, Authentication authentication) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            if (result.hasErrors()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
                logger.error("unable to upate Speed with plan id : "+entityDTO.getPlanId()+" :   Response : {{}{};}",genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            CustomerPackageDTO customerPackageDTO = customerPackageService.getEntityById(entityDTO.getCustPackageId(),entityDTO.getMvnoId());
            customerPackageDTO.setDownloadqos(entityDTO.getDownloadqos());
            customerPackageDTO.setDownloadts(entityDTO.getDownloadts());
            customerPackageDTO.setUploadqos(entityDTO.getUploadqos());
            customerPackageDTO.setUploadts(entityDTO.getUploadts());
            String updatedValues = UtilsCommon.getUpdatedDiff(customerPackageDTO,entityDTO);
            ValidationData validation = validateSave(customerPackageDTO);
            if (!validation.isValid()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                logger.error("unable to upate Speed with plan id : "+entityDTO.getPlanId()+":   Response : {{}{};}",genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            genericDataDTO.setData(customerPackageService.updateEntity(customerPackageDTO));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Updating Speed of plan id  "+updatedValues+" is successfull :   Response : {{}}",genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
            genericDataDTO.setTotalRecords(1);
        } catch (Exception ex) {
          //  ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Failed to save data. Please try after some time");
            logger.error("unable to upate Speed with plan id : "+entityDTO.getPlanId()+" :   Response : {{}{};exception{}}",genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode(),ex.getStackTrace());
        }
        return genericDataDTO;
    }

    @GetMapping("/getCustomerPackage/{custId}")
    public GenericDataDTO getCustPackage(@RequestParam Integer id) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<CustomerPackageDTO> customerPackageDTOList = customerPackageService.findAllByCustomersId(id);
            genericDataDTO.setData(customerPackageDTOList);
            genericDataDTO.setTotalRecords(customerPackageDTOList.size());
            return genericDataDTO;
        } catch (Exception ex){
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }
        return genericDataDTO;
    }

}
