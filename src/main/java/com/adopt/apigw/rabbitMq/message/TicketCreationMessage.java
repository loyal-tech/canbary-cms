package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreationMessage {
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
    private String caseNumber;
    private String name;
    private LocalDate nextFollowupDate;


    private Map<String,Object> customerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    private String parentStaffPersonName;

    private String staffPersonName;

    private String eventName;

    private String assigndatetime;



    public TicketCreationMessage(String mobileNumber, String emailId,String message,TemplateNotification template, String customerName, String caseNumber, Integer mvnoId) {
        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.customerData.put("customerName",parentStaffPersonName);
        this.customerData.put("mobileNumber",mobileNumber);
        this.customerData.put("emailId",emailId);
        //this.customerData.put("name",name);
        this.customerData.put("caseNumber",caseNumber);
//      this.customerData.put("Assigndatetime",assigndatetime);
//      this.customerData.put("parentStaffPersonName",parentStaffPersonName);
        //this.customerData.put("eventName",eventName);
        this.customerData.put("mvnoId",mvnoId);
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();
    }

}
