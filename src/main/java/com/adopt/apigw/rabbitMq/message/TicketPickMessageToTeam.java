package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.TicketTatAudits;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketPickMessageToTeam {
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

    private TicketTatAudits tatAudits;



    public TicketPickMessageToTeam(String mobileNumber, String emailId, String message, TemplateNotification template, String parentStaffPersonName, String assigndatetime, String eventName, Integer mvnoId, String teamName, String caseNumber,TicketTatAudits tatAudits,Long buId) {
        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.customerData.put("parentStaffPersonName",parentStaffPersonName);
        this.customerData.put("mobileNumber",mobileNumber);
        this.customerData.put("emailId",emailId);
        this.customerData.put("name",name);
        this.customerData.put("teamName",teamName);
        this.customerData.put("Assigndatetime",assigndatetime);
        this.customerData.put("eventName",eventName);
        this.customerData.put("mvnoId",mvnoId);
        this.customerData.put("ticketTatAudit",tatAudits);
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();
        this.customerData.put("ticketNumber",caseNumber);
        this.customerData.put("tatAudit_caseId",tatAudits.getCaseId());
        this.customerData.put("tatAudit_caseStatus",tatAudits.getCaseStatus());
        this.customerData.put("tatAudit_tatAction",tatAudits.getTatAction());
        this.customerData.put("tatAudit_tatTime",tatAudits.getTatTime());
        this.customerData.put("tatAudit_tatUnit",tatAudits.getTatUnit());
        this.customerData.put("tatAudit_slaTime",tatAudits.getSlaTime());
        this.customerData.put("tatAudit_slaUnit",tatAudits.getSlaUnit());
        this.customerData.put("tatAudit_tatStartTime",tatAudits.getTatStartTime());
        this.customerData.put("tatAudit_tatMessage",tatAudits.getTatMessage());
        this.customerData.put("tatAudit_assignStaffId",tatAudits.getAssignStaffId());
        this.customerData.put("tatAudit_assignParentStaffId",tatAudits.getAssignStaffParentId());
        this.customerData.put("tatAudit_caseLevel",tatAudits.getCaseLevel());
        this.customerData.put("tatAudit_notificationFor",tatAudits.getNotificationFor());
        this.customerData.put("tatAudit_isTatBreached",tatAudits.getIsTatBreached());
        this.customerData.put("tatAudit_isSlaBreaced",tatAudits.getIsTatBreached());
        if(Objects.nonNull(buId)){
            this.customerData.put(RabbitMqConstants.BU_ID,buId);
        }
        else{
            this.customerData.put(RabbitMqConstants.BU_ID,null);
        }
    }


}
