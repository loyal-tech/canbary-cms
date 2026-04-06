package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.Data;

import java.util.*;

@Data
public class CustomerExpiredDocumentMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private String staffName;
    private Long staffId;

    private Map<String, Object> partnerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    public CustomerExpiredDocumentMessage(String message, TemplateNotification template, String sourceName, Customers customers, String staffName,Long staffId) {

        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.setStaffName(staffName);
        this.staffId = staffId;

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.partnerData.put("mobileNumber", customers.getMobile());
        this.partnerData.put("emailId", customers.getEmail());
        this.partnerData.put("mvnoId", customers.getMvnoId());
        this.partnerData.put("partnerName", customers.getFirstname());
        this.partnerData.put("countryCode", customers.getCountryCode());
        this.partnerData.put("staffName" , staffName);
        if(Objects.nonNull(customers.getBuId())){
            this.partnerData.put(RabbitMqConstants.BU_ID,customers.getBuId());
        }
        else{
            this.partnerData.put(RabbitMqConstants.BU_ID,null);
        }


        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

    }
}
