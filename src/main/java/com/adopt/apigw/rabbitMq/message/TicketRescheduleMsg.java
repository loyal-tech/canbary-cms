package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRescheduleMsg {


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

    private String parentStaffPersonName;

    private String remark ;
    private String caseNumber;
    private String followupDateTime;

    private Map<String,Object> customerData = new HashMap<>();

    public TicketRescheduleMsg(String staffPersonName, String staffMobileNumber, String staffEmailId, String message, TemplateNotification template,String caseNumber,String followupDateTime,Integer mvnoId ,Long buId) {
        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.customerData.put("mobileNumber",staffMobileNumber);
        this.customerData.put("emailId",staffEmailId);
        this.customerData.put("caseNumber",caseNumber);
        this.customerData.put("staffPersonName",staffPersonName);
        this.customerData.put("mvnoId",mvnoId);
        this.customerData.put("followupDateTime",followupDateTime);
        if(Objects.nonNull(buId)){
            this.customerData.put(RabbitMqConstants.BU_ID,buId);
        }
        else{
            this.customerData.put(RabbitMqConstants.BU_ID,null);
        }
        this.staffPersonName = staffPersonName;
        this.caseNumber = caseNumber;
        this.followupDateTime = followupDateTime;
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

    }
}
