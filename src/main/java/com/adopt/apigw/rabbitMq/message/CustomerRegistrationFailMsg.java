package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRegistrationFailMsg {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private String registrationStatus;
    private String password;

    private Map<String,Object> customerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    private String mobileNumber;
    private String emailId;
    private String username;
    private Integer mvnoId;
    private String countryCode;
    public CustomerRegistrationFailMsg(String username, String password,String countryCode, String  mobileNumber, String emailId, Integer mvnoId,  String message, TemplateNotification template, String sourceName, String status) {

        this.setMessage(message);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.setRegistrationStatus(status);
        this.sourceName = sourceName;
        customerData.put("username",username);
        customerData.put("mvnoId",mvnoId);
        customerData.put("mobileNumber",mobileNumber);
        customerData.put("emailId",emailId);
        customerData.put("password",password);
        customerData.put("countryCode",countryCode);
        customerData.put("isCustomer",true);
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();
    }


}
