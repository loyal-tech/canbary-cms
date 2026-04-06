package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.Data;

import java.util.*;

@Data
public class StaffStatusChangeMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private String customerName;


    private Map<String, Object> staffUserData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;
    public StaffStatusChangeMessage(String message, TemplateNotification template, String sourceName, StaffUser staffUser , String newStatus) {

        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.staffUserData.put("mobileNumber", staffUser.getPhone());
        this.staffUserData.put("emailId", staffUser.getEmail());
        this.staffUserData.put("mvnoId", staffUser.getMvnoId());
        this.staffUserData.put("username", staffUser.getUsername());
        this.staffUserData.put("status" , newStatus);
        this.staffUserData.put("countryCode", staffUser.getCountryCode());
        this.staffUserData.put("staffId",staffUser.getId());
        if(Objects.nonNull(staffUser.getBusinessUnit())){
            this.staffUserData.put(RabbitMqConstants.BU_ID,staffUser.getBusinessUnit().getId());
        }
        if(Objects.isNull(staffUser.getBusinessUnit())){
            this.staffUserData.put(RabbitMqConstants.BU_ID,null);
        }



        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

    }
}
