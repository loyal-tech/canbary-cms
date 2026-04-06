package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.Data;

import java.util.*;

@Data
public class TicketFollowUpMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;

    private Map<String, Object> customerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    public TicketFollowUpMessage(String message, TemplateNotification template, String sourceName, String mobileNo, String emailId, Integer mvnoId, String FollowupDateTime, Integer FollowupTime, String caseNumber, String staffPersonName, String parentStaffPersonName, String followUpName,Long buId) {

        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.customerData.put("mobileNumber", mobileNo);
        this.customerData.put("emailId", emailId);
        this.customerData.put("mvnoId", mvnoId);
        this.customerData.put("followupDateTime", FollowupDateTime);
        this.customerData.put("followupTime", FollowupTime);
        this.customerData.put("caseNumber", caseNumber);
        this.customerData.put("staffPersonName", staffPersonName);
        this.customerData.put("parentStaffPersonName", parentStaffPersonName);
        this.customerData.put("followUpName", followUpName);
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
