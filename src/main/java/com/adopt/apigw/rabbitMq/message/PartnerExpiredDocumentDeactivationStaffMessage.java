package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class PartnerExpiredDocumentDeactivationStaffMessage {
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

    public PartnerExpiredDocumentDeactivationStaffMessage(String message, TemplateNotification template, String sourceName, StaffUser staffUser , String customerName) {

        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.setCustomerName(customerName);

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.staffUserData.put("mobileNumber", staffUser.getPhone());
        this.staffUserData.put("emailId", staffUser.getEmail());
        this.staffUserData.put("mvnoId", staffUser.getMvnoId());
        this.staffUserData.put("username", staffUser.getUsername());
        this.staffUserData.put("countryCode", staffUser.getCountryCode());
        this.staffUserData.put("customerName" , customerName);


        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

    }
}
