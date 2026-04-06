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
public class CustServiceActiveMsg {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;

    private Map<String,Object> customerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;
    private String username;
    private String mobileNumber;
    private String emailId;
    private String status;
    private Integer mvnoId;
    private Long staffId;

    public CustServiceActiveMsg(String username, String  mobileNumber, String emailId, String status, Integer mvnoId, String message, TemplateNotification template, String sourceName,Integer buId,Long staffId){
        this.setMessage(message);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.sourceName = sourceName;
        this.staffId = staffId;
        customerData.put("username",username);
        customerData.put("mobileNumber",mobileNumber);
        customerData.put("emailId",emailId);
        customerData.put("status", status);
        customerData.put("mvnoId", mvnoId);
        customerData.put("buId", buId);
        customerData.put("isCustomer",true);
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();
    }


}
