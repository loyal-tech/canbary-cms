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
public class CustomerStatusInActiveMessage {
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

    public CustomerStatusInActiveMessage(String username,String  mobileNumber, String emailId, String status,Integer mvnoId, String message, TemplateNotification template, String sourceName,Long buId,Long staffId){
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
        customerData.put("isCustomer",true);
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
