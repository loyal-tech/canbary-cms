package com.adopt.apigw.modules.planUpdate.controller;

import com.adopt.apigw.modules.otpManagment.OTPManagementController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.ValidationData;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.planUpdate.model.CustomerPackageDTO;
import com.adopt.apigw.modules.planUpdate.model.QuotaDtlsDTO;
import com.adopt.apigw.modules.planUpdate.model.ServicePlansDTO;
import com.adopt.apigw.modules.planUpdate.service.CustomerPackageService;
import com.adopt.apigw.modules.planUpdate.service.QuotaDtlsService;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = UrlConstants.BASE_API_URL + UrlConstants.QUOTA_DTLS)
public class QuotaDtlsController extends ExBaseAbstractController<QuotaDtlsDTO> {
    private static String MODULE = " [QuotaDtlsController] ";
    @Autowired
    private QuotaDtlsService quotaDtlsService;

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private CustomerPackageService customerPackageService;

    public QuotaDtlsController(QuotaDtlsService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "QuotaDtlsController";
    }
    private static final Logger logger = LoggerFactory.getLogger(OTPManagementController.class);
    @PostMapping("/updateQuota")
    public GenericDataDTO updateQuota(@Valid @RequestBody QuotaDtlsDTO entityDTO, BindingResult result, Authentication authentication) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (result.hasErrors()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
                logger.error("unable to update quota to  plan id "+entityDTO.getPostpaidPlanId()+":  request: { From : {}, Request Url : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            ValidationData validation = validateUpdate(entityDTO);
            if (!validation.isValid()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                logger.error("unable to update quota to  plan id "+entityDTO.getPostpaidPlanId()+":  request: { From : {}, Request Url : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            QuotaDtlsDTO dtoData = quotaDtlsService.getEntityById(entityDTO.getIdentityKey(),entityDTO.getMvnoId());
            dtoData.setQuotaType(entityDTO.getQuotaType() != null ? entityDTO.getQuotaType() : dtoData.getQuotaType());
            dtoData.setTotalQuota(entityDTO.getTotalQuota() != null ? entityDTO.getTotalQuota() : dtoData.getTotalQuota());
            dtoData.setQuotaUnit(entityDTO.getQuotaUnit() != null ? entityDTO.getQuotaUnit() : dtoData.getQuotaUnit());
            dtoData.setTimeQuotaUnit(entityDTO.getTimeQuotaUnit() != null ? entityDTO.getTimeQuotaUnit() : dtoData.getTimeQuotaUnit());
            dtoData.setTimeTotalQuota(entityDTO.getTimeTotalQuota() != null ? entityDTO.getTimeTotalQuota() : dtoData.getTimeTotalQuota());

            genericDataDTO.setData(quotaDtlsService.updateEntity(dtoData));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Updating quota for plan id "+dtoData.getPostpaidPlanId() +" is successfull :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
            genericDataDTO.setTotalRecords(1);
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
               // ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                logger.error("unable to update quota to  plan id "+entityDTO.getPostpaidPlanId()+":  request: { From : {}, Request Url : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode(),ex.getStackTrace());
            } else {
              //  ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
                logger.error("unable to update quota to  plan id "+entityDTO.getPostpaidPlanId()+":  request: { From : {}, Request Url : {}}; Response : {{};exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode(),ex.getStackTrace());
            }
        }
        return genericDataDTO;
    }

    @GetMapping("/plans")
    public GenericDataDTO getCustPlans(@RequestParam Integer id) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<QuotaDtlsDTO> quotaDtlsDTOList = quotaDtlsService.findAllByCustomersId(id);
            List<CustomerPackageDTO> customerPackageDTOList = customerPackageService.findAllByCustomersId(id);
            List<ServicePlansDTO> servicePlansDTOList = new ArrayList<>();
            for (int i = 0; i < quotaDtlsDTOList.size() || i < customerPackageDTOList.size(); i++) {
                ServicePlansDTO servicePlansDTO = new ServicePlansDTO();
                if (i < quotaDtlsDTOList.size()) {
                    QuotaDtlsDTO quotaDtlsDTO = quotaDtlsDTOList.get(i);
                    servicePlansDTO.setPlanId(quotaDtlsDTO.getPostpaidPlanId());
                    servicePlansDTO.setPlanName(postpaidPlanService.findNameById(quotaDtlsDTO.getPostpaidPlanId()));
                    servicePlansDTO.setQuotaType(quotaDtlsDTO.getQuotaType());
                    servicePlansDTO.setDataTotalQuota(quotaDtlsDTO.getTotalQuota());
                    servicePlansDTO.setDataUsedQuota(quotaDtlsDTO.getUsedQuota());
                    servicePlansDTO.setDataQuotaUnit(quotaDtlsDTO.getQuotaUnit());
                    servicePlansDTO.setTimeTotalQuota(Double.valueOf(quotaDtlsDTO.getTimeTotalQuota()));
                    servicePlansDTO.setTimeUsedQuota(Double.valueOf(quotaDtlsDTO.getTimeQuotaUsed()));
                    servicePlansDTO.setTimeQuotaUnit(quotaDtlsDTO.getTimeQuotaUnit());
                }

                if (i < customerPackageDTOList.size()) {
                    CustomerPackageDTO customerPackageDTO = customerPackageDTOList.get(i);
                    servicePlansDTO.setExpiry(customerPackageDTO.getExpiryDate());
                    servicePlansDTO.setQosPolicyId(customerPackageDTO.getQospolicyId());
                }
                servicePlansDTOList.add(servicePlansDTO);
            }
            genericDataDTO.setData(servicePlansDTOList);
            genericDataDTO.setTotalRecords(servicePlansDTOList.size());
            logger.info("Fetchng customer Plans are successfull :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("unable fetch Customer plans  request: { From : {}, Request Url : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode(),ex.getStackTrace());
        }
        return genericDataDTO;
    }

}
