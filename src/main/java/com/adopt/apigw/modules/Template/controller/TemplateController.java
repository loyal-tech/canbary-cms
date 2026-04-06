package com.adopt.apigw.modules.Template.controller;

import com.adopt.apigw.modules.tickets.controller.CaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.ValidationData;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.Template.domain.Template;
import com.adopt.apigw.modules.Template.model.TemplateDTO;
import com.adopt.apigw.modules.Template.service.TemplateService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.TEMPLATE)
public class TemplateController extends ExBaseAbstractController<TemplateDTO> {

    @Autowired
    TemplateService templateService;
    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);
    public TemplateController(TemplateService service) {
        super(service);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST,consumes = "multipart/form-data", produces = "application/json")
    public GenericDataDTO createTemplate(HttpServletRequest request, Authentication authentication) throws Exception{
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            ValidationData validation = validateSave(request);
            if (!validation.isValid()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                logger.error("Unable to create new template  :  request: { From : {}, Request Url : {}}; Response : {{}{};}",  request.getHeader("requestFrom"),request.getRequestURL(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            Template dtoData = templateService.save(request);
            genericDataDTO.setData(dtoData);
            genericDataDTO.setTotalRecords(1);
            logger.info("Template is created successfully  request: { From : {}, Request Url : {}}; Response : {{}{}}",  request.getHeader("requestFrom"),request.getRequestURL(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Failed to save data. Please try after some time");
            logger.error("Unable to create new template   :  request: { From : {}, Request Url : {}}; Response : {{}{};Exception:{}}",  request.getHeader("requestFrom"),request.getRequestURL(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
        }

        return genericDataDTO;
    }

    public ValidationData validateSave(HttpServletRequest request) {
        return new ValidationData();
    }

    @Override
    public String getModuleNameForLog() {
        return "[TemplateController]";
    }
}
