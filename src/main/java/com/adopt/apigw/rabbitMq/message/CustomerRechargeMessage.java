package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRechargeMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private String plan;
    private String purchaseType;
    private String mobileNumber;
    private String emailId;
    private String username;
    private Integer mvnoId;
    private String countryCode;
    private Long staffId;


    private Map<String, Object> customerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    public CustomerRechargeMessage(String username,String countryCode, String mobileNumber, String emailId, Integer mvnoId, String message, TemplateNotification template, String sourceName, String plan, String purchaseType, Long buId,Long staffId) {

        this.setMessage(message);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.sourceName = sourceName;
        this.purchaseType = purchaseType;
        this.staffId =staffId;
        customerData.put("mvnoId",mvnoId);
        customerData.put("mobileNumber",mobileNumber);
        customerData.put("emailId",emailId);
        customerData.put("username",username);
        customerData.put("plan",plan);
        customerData.put("countryCode",countryCode);
        customerData.put("isCustomer",true);
        if(Objects.nonNull(buId)) {
            customerData.put(RabbitMqConstants.BU_ID, buId);
        }
        if(Objects.isNull(buId)){
            customerData.put(RabbitMqConstants.BU_ID, null);
        }
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();
    }
}
