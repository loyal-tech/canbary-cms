package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.customerDocDetails.domain.CustomerDocDetails;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.Data;

import java.util.*;

@Data
public class StaffExpiredMassage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private String customerName;
    private Long staffId;

    private Map<String, Object> staffUserData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    public StaffExpiredMassage(String message, TemplateNotification template, String sourceName, StaffUser staffUser , String customerName, Integer mvnoId, Integer buId,Long staffId) {

        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.setCustomerName(customerName);
        this.staffId = staffId;

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.staffUserData.put("mobileNumber", staffUser.getPhone());
        this.staffUserData.put("emailId", staffUser.getEmail());
        this.staffUserData.put("mvnoId", staffUser.getMvnoId());
        this.staffUserData.put("username", staffUser.getUsername());
        this.staffUserData.put("countryCode", staffUser.getCountryCode());
        this.staffUserData.put("customerName" , customerName);
        if(Objects.isNull(buId)){
            this.staffUserData.put(RabbitMqConstants.BU_ID,null);
        }else{
            this.staffUserData.put(RabbitMqConstants.BU_ID,buId);
        }
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

    }
}
