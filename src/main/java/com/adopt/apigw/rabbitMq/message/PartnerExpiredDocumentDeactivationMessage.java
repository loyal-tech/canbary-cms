package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class PartnerExpiredDocumentDeactivationMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private String staffName;


    private Map<String, Object> partnerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    public PartnerExpiredDocumentDeactivationMessage(String message, TemplateNotification template, String sourceName, Partner partner, String staffName) {

        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.setStaffName(staffName);

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.partnerData.put("mobileNumber", partner.getMobile());
        this.partnerData.put("emailId", partner.getEmail());
        this.partnerData.put("mvnoId", partner.getMvnoId());
        this.partnerData.put("partnerName", partner.getName());
        this.partnerData.put("countryCode", partner.getCountryCode());
        this.partnerData.put("staffName" , staffName);


        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

    }

}
