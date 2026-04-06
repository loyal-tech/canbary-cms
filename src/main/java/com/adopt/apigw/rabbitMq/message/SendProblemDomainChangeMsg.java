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
public class SendProblemDomainChangeMsg {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;

    private String username;
    private String mobileNumber;
    private String emailId;
    private Integer mvnoId;
    private String countryCode = "+91";

    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    private String staffPersonName;

    private String ticketNumber;

    private String oldValue;

    private String newValue;

    private Map<String,Object> customerData = new HashMap<>();

    public SendProblemDomainChangeMsg(String parentMobileNumber, String parentEmailId, String message, TemplateNotification template, String oldValue, String staffPersonName, String newValue, Integer mvnoId, String ticketNumber,Long buId) {
        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.customerData.put("oldValue",oldValue);
        this.customerData.put("mobileNumber",parentMobileNumber);
        this.customerData.put("emailId",parentEmailId);
        this.customerData.put("ticketNumber",ticketNumber);
        this.customerData.put("staffPersonName",staffPersonName);
        this.customerData.put("newValue",newValue);
        this.customerData.put("mvnoId",mvnoId);
        if(Objects.nonNull(buId)){
            this.customerData.put(RabbitMqConstants.BU_ID,buId);
        }
        else{
            this.customerData.put(RabbitMqConstants.BU_ID,null);
        }
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

    }
}
