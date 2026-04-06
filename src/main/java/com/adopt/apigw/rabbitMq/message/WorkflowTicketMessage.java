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
public class WorkflowTicketMessage {

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

    private String eventName;
    private Long staffId;

    private Map<String,Object> customerData = new HashMap<>();



    public WorkflowTicketMessage(String message, String sourceName, TemplateNotification template, String staffPersonName, String mobileNumber, String emailId, Integer mvnoId, String workflowAction,Long buId,Long staffId) {

        //Message parameters
        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        //Staff parameters
        this.customerData.put("mobileNumber",mobileNumber);
        this. customerData.put("emailId",emailId);
        this.customerData.put("mvnoId",mvnoId);
        this.customerData.put("action", workflowAction);
        this.customerData.put("staffPersonName",staffPersonName);
        if(Objects.nonNull(buId)){
            this.customerData.put(RabbitMqConstants.BU_ID,buId);
        }
        else{
            this.customerData.put(RabbitMqConstants.BU_ID,null);
        }
        this.staffId = staffId;


        //Email Configuration parameters
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();
    }
}
