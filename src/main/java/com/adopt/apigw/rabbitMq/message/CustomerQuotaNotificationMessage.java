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
public class CustomerQuotaNotificationMessage {

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
    private Long staffId;

    private Map<String,Object> customerData = new HashMap<>();

    public CustomerQuotaNotificationMessage(String customerMobileNumber, String customerEmailId, String message, TemplateNotification template, String customerName, Integer percentage , String planname, String countryCode, Integer mvnoId, Long buId,Long staffId) {
        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.staffId=staffId;
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.customerData.put("username",customerName);
        this.customerData.put("percentage",percentage.toString());
        this.customerData.put("planname",planname);
        this.customerData.put("mobileNumber",customerMobileNumber);
        this.customerData.put(RabbitMqConstants.COUNTRY_CODE, countryCode);
        this.customerData.put("emailId",customerEmailId);
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
