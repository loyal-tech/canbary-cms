package com.adopt.apigw.modules.Template.service;

import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.NotificationConstants;
import com.adopt.apigw.constants.cacheKeys;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.Template.domain.Event;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.model.TemplateNotificationDTO;
import com.adopt.apigw.modules.Template.repository.EventRepository;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.service.CacheService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.ValidateCrudTransactionData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationTemplateService {

    @Autowired
    NotificationTemplateRepository templateRepository;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplates;
    @Autowired
    private CacheService cacheService;
    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    ObjectMapper objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    public List<TemplateNotification> findAll()
    {
        try
        {
            if(getMvnoIdFromCurrentStaff() == 1)
                return templateRepository.findAll();
            else
                return templateRepository.findAll().stream().filter(templateNotification -> templateNotification.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || templateNotification.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
        } catch(CustomValidationException e) {
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        }
        catch(Throwable e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }
   
    public List<TemplateNotification> findByTemplateName(String templateName)
    {
        String cacheKey = cacheKeys.TEMPLATENOTIFICATION + templateName;
        List<TemplateNotification> templateNotifications = null;
//        long startTime = System.currentTimeMillis();
        try {
            templateNotifications = cacheService.getListFromCache(cacheKey, TemplateNotification.class);

            if (templateNotifications != null && !templateNotifications.isEmpty()) {
                return templateNotifications;
            }

            // Fetch from database if not found in cache
            templateNotifications = templateRepository.findAllByTemplateNameContainingIgnoreCase(templateName);

            if (templateNotifications != null && !templateNotifications.isEmpty()) {
                // Cache the fetched data for future use
                cacheService.putInCache(cacheKey, templateNotifications); // Cache for 10 minutes
                return templateNotifications;
            }
        } catch (Exception e) {
            log.error("Error while fetching template notifications for template name: " + templateName, e);
        }
        return null; // Return null if not found in cache or DB
    }


    public TemplateNotification saveTemplate(TemplateNotificationDTO templateDto)
    {
        try
        {
            templateDto.setMvnoId(getMvnoIdFromCurrentStaff());
            TemplateNotification templateVo = validateEventId(templateDto);
            validateTemplateData(templateDto);
            Integer count = templateRepository.countByEventEventId(templateDto.getEventId());
            if(count == 1)
            {
                throw new RuntimeException("You can not create template with event id : '"+templateDto.getEventId()+"', Because it is already used by another tempalte.");
            }
            templateVo.setCreateDate(new Timestamp(new Date().getTime()));
            templateVo.setLastModificationDate(new Timestamp(new Date().getTime()));
            if(templateVo.getSmsTemplateData().equals(NotificationConstants.BLANK_STRING))
            {
                templateVo.setSmsTemplateData(null);
            }
            if(templateVo.getEmailTemplateData().equals(NotificationConstants.BLANK_STRING))
            {
                templateVo.setEmailTemplateData(null);
            }
            return templateRepository.save(templateVo);
        } catch(CustomValidationException e) {
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        } catch(Throwable e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }


    public List<TemplateNotificationDTO> udpateTemplate(List<TemplateNotificationDTO> templateDtos)
    {
        try
        {
            templateDtos = templateDtos.stream().filter(templateNotificationDTO -> templateNotificationDTO.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());
            for(int i=0;i< templateDtos.size();i++)
            {
                TemplateNotificationDTO templateDto = templateDtos.get(i);
                TemplateNotification emailTemplateVo = validateEventId(templateDto);
                emailTemplateVo.setStatus(NotificationConstants.ACTIVE);
                validateTemplateData(templateDto);
                Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(templateDto.getTemplateName());
//			if(!optionalTemplate.isPresent())
//			{
//				throw new RuntimeException("No record found with template name '"+templateDto.getTemplateName()+"', Please enter valid template name to update the template record.");
//			}
//			else
//			{
//				Integer count = templateRepository.countByEventEventId(templateDto.getEventId());
//				if(count == 1 && optionalTemplate.get().getEvent().getEventId() != templateDto.getEventId())
//				{
//					throw new RuntimeException("You can not update template with event id : '"+templateDto.getEventId()+"', Because it is already used by another tempalte.");
//				}

                emailTemplateVo.setTemplateId(optionalTemplate.get().getTemplateId());
                emailTemplateVo.setCreateDate(optionalTemplate.get().getCreateDate());
                emailTemplateVo.setLastModificationDate(new Timestamp(new Date().getTime()));
                templateRepository.save(emailTemplateVo);
            }
            return templateDtos;
        } catch(CustomValidationException e) {
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        }
        catch(Throwable e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateTemplateData(TemplateNotificationDTO templateDto)
    {
        try
        {
            if(templateDto.isEmailEventConfigured() && !ValidateCrudTransactionData.validateStringTypeFieldValue(templateDto.getEmailTemplateData()))
            {
                throw new RuntimeException(NotificationConstants.BASIC_STRING_MSG+"Email template data is mandatory. Please enter valid email template data.");
            }
            else if(templateDto.isSmsEventConfigured() && !ValidateCrudTransactionData.validateStringTypeFieldValue(templateDto.getSmsTemplateData()))
            {
                throw new RuntimeException(NotificationConstants.BASIC_STRING_MSG+"Sms template data is mandatory. Please enter valid sms template data.");
            }
            else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(templateDto.getTemplateName()))
            {
                throw new RuntimeException(NotificationConstants.BASIC_STRING_MSG+"Template name is mandatory. Please enter valid template name.");
            }
//			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(templateDto.getStatus()))
//			{
//				throw new RuntimeException(NotificationConstants.BASIC_STRING_MSG+"Template status is mandatory. Please enter valid template status.");
//			}
//			else if(!templateDto.getStatus().equals(NotificationConstants.ACTIVE) && !templateDto.getStatus().equals(NotificationConstants.IN_ACTIVE))
//			{
//				throw new RuntimeException("Please enter valid template status. It should be "+NotificationConstants.ACTIVE+" OR "+NotificationConstants.IN_ACTIVE+".");
//			}
        }
        catch(Throwable e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    private TemplateNotification validateEventId(TemplateNotificationDTO templateDto)
    {
        try
        {
            if(!ValidateCrudTransactionData.validateLongTypeFieldValue(templateDto.getEventId()))
            {
                throw new RuntimeException(NotificationConstants.BASIC_NUMERIC_MSG+"Event id is mandatory. Please enter valid event id.");
            }
            else
            {
                Optional<Event> optionalEvent = eventRepository.findById(templateDto.getEventId());
                if(!optionalEvent.isPresent())
                    throw new RuntimeException("No record found with event id : '"+templateDto.getEventId()+"'");
                else
                    return new TemplateNotification(templateDto,optionalEvent.get());
            }
        }
        catch(Throwable e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }


    public String deleteTemplate(Long templateId)
    {
        try
        {
            getEntityForUpdateAndDelete(templateId);
            templateRepository.deleteById(templateId);
        } catch(CustomValidationException e) {
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        }
        catch(Throwable e)
        {
            throw new RuntimeException(e.getMessage());
        }
        return "Template deleted successfully : with templated id "+ templateId;
    }

    private Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }

    public TemplateNotification getEntityForUpdateAndDelete(Long id) {
        TemplateNotification templateNotification = templateRepository.findById(id).get();
        if(templateNotification == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == templateNotification.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return templateNotification;
    }


}
