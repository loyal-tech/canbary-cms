package com.adopt.apigw.modules.Template.controller;

import com.adopt.apigw.constants.NotificationConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.model.TemplateNotificationDTO;
import com.adopt.apigw.modules.Template.service.NotificationTemplateService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "Template Management", description = "REST APIs related to Template Entity!!!!", tags = "Template")
@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.TEMPLATES)
public class TemplateNotificationController {

    private static final Logger logger = LoggerFactory.getLogger(TemplateNotificationController.class);
    @Autowired
    NotificationTemplateService templateService;

    @GetMapping("/all")
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEMPLATE_NOTIFICATION_ALL + "\",\"" + AclConstants.OPERATION_TEMPLATE_NOTIFICATION_VIEW + "\")")
    @ApiOperation(value = "Get list of templates in the system")
    public GenericDataDTO findAll() {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setDataList(templateService.findAll());
            logger.info("Fetcing all templates  request: { From : {}, }; Response : {{}{}}",getModuleNameForLog() ,genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception e) {
            if (e instanceof CustomValidationException){
                ApplicationLogger.logger.error(" [findAll] " + e.getMessage(), e);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(e.getMessage());
                logger.error("Unable to fetch templates  :  request: { From : {}, Request Url : {}}; Response : {Error:{},Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
            }
            else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to load template!!");
                logger.error("Unable to cfetch templates   :  request: { From : {}, Request Url : {}}; Response : {Error:{},exception{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
            }
        }
        MDC.remove("type");
        return genericDataDTO;
    }
    @GetMapping("/search")
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEMPLATE_NOTIFICATION_ALL + "\",\"" + AclConstants.OPERATION_TEMPLATE_NOTIFICATION_VIEW + "\")")
    @ApiOperation(value = "Get list of templates in the system")
    public GenericDataDTO searchByName(@RequestParam(name = "templateName", required = false) String templateName) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");
        try {
            if(templateName == null)
            {
                genericDataDTO.setDataList(templateService.findAll());
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setResponseMessage("find all records");
                logger.info("Fetching template by name "+ templateName+": { From : {}, Request Url : {}}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            genericDataDTO.setDataList(templateService.findByTemplateName(templateName));

                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setResponseMessage("Find templates successfully");
            if (null != genericDataDTO) {

                if (genericDataDTO.getDataList().isEmpty()) {
                    genericDataDTO = new GenericDataDTO();
                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                    genericDataDTO.setResponseMessage("No Record Found!");
                    genericDataDTO.setDataList(new ArrayList<>());
                    genericDataDTO.setTotalRecords(0);
                    genericDataDTO.setPageRecords(0);
                    genericDataDTO.setCurrentPageNumber(1);
                    genericDataDTO.setTotalPages(1);

                }

                return genericDataDTO;
            }
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Failed to load template!!");
            logger.error("Unable toFetch template with name "+templateName+":  request: { From : {}, }; Response : {Error{},Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping("/save")
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEMPLATE_NOTIFICATION_ALL + "\",\"" + AclConstants.OPERATION_TEMPLATE_NOTIFICATION_ADD + "\")")
    @ApiOperation(value = "Add new template")
    public GenericDataDTO saveTemplate(@RequestBody TemplateNotificationDTO templateDto) {
        MDC.put("type", "Create");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            // response.put(TEMPLATE, templateService.saveTemplate(templateDto));
            genericDataDTO.setData(templateService.saveTemplate(templateDto));
            logger.info("Saving New Template  with name "+templateDto.getTemplateName()+": { From : {},}; Response : {{}{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Failed to save template!!");
            logger.error("Unable save new template  with name "+templateDto.getTemplateName()+"  :  request: { From : {}, }; Response : {{}{};Exception:{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());

        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PutMapping("/update")
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEMPLATE_NOTIFICATION_ALL + "\",\"" + AclConstants.OPERATION_TEMPLATE_NOTIFICATION_EDIT + "\")")
    @ApiOperation(value = "Update existing template")
    public GenericDataDTO updateTemplate(@RequestBody List<TemplateNotificationDTO> templateDtos) {
        MDC.put("type", "Update");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setData(templateService.udpateTemplate(templateDtos));
            logger.info("Updating Existing Templates:"+templateDtos.get(0).getTemplateName()+"  request: { From : {}, }; Response : {{}{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception e) {
            if (e instanceof CustomValidationException){
                ApplicationLogger.logger.error(" [findAll] " + e.getMessage(), e);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(e.getMessage());
                logger.error("Unable to update Existing Templates With name "+templateDtos.get(0).getTemplateName()+"  :  request: { From : {}, }; Response : {{}{};Exception:{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
            }
            else{
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to update template!!");
                logger.error("Unable to update Existing Templates With name "+templateDtos.get(0).getTemplateName()+  ":  request: { From : {}}; Response : {{}{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getMessage());
            }
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @DeleteMapping("/delete")
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TEMPLATE_NOTIFICATION_ALL + "\",\"" + AclConstants.OPERATION_TEMPLATE_NOTIFICATION_DELETE + "\")")
    @ApiOperation(value = "Delete existing template")
    public GenericDataDTO deleteTemplate(@RequestParam(name = "templateId", required = true) Long templateId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");
        try {
            genericDataDTO.setData(templateService.deleteTemplate(templateId));
            logger.info("Template with name "+templateService.getEntityForUpdateAndDelete(templateId).getTemplateName()+" is deleted, request: { From : {}, }; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;

        } catch (Exception e) {
            if (e instanceof CustomValidationException) {
                ApplicationLogger.logger.error(" [findAll] " + e.getMessage(), e);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(e.getMessage());
                logger.error("Unable todelete template with name "+templateService.getEntityForUpdateAndDelete(templateId).getTemplateName()+"   :  request: { From : {}}; Response : {{}{};Exception:{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to update template!!");
                logger.error("Unable todelete template with name "+templateService.getEntityForUpdateAndDelete(templateId).getTemplateName()+"  :  request: { From : {}, }; Response : {{}{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
            }
        }
        MDC.remove("type");
        return genericDataDTO;
    }
    public String getModuleNameForLog() {
        return "[TemplateNotificationController]";
    }
}
