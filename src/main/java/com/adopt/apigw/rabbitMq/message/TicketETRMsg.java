package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketETRMsg {

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

    private LocalDate newFollowUpDate;
    private LocalTime newFollowUpTime;

    private String customerName;

    private String ticketNumber;



    public TicketETRMsg(String username, String mobileNumber, String emailId, Integer mvnoId, String message, TemplateNotification template, String sourceName, String caseNumber, String additionalDate , String additionalTime, String staffPersonName, String remark, String status, String sender, Boolean isTemplateDynamic,String messageMode, Integer staffId, Integer custId,Long caseId,Long buId){
        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.customerData.put("mobileNumber",mobileNumber);
        this.customerData.put("emailId",emailId);
        this.customerData.put("mvnoId",mvnoId);
        this.customerData.put("caseNumber",caseNumber);
        this.customerData.put("additionalDate",additionalDate);
        this.customerData.put("additionalTime",additionalTime);
        this.customerData.put("customerName",username);
        this.customerData.put("staffPersonName", staffPersonName);
        this.customerData.put("remark",remark);
        this.customerData.put("status",status);
        this.customerData.put("sender",sender);
        this.customerData.put("isTemplateDynamic",isTemplateDynamic);
        this.customerData.put("messageMode",messageMode);
        this.customerData.put("custId",custId);
        this.customerData.put("staffId",staffId);
        this.customerData.put("caseId",caseId);
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
