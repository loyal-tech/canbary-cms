package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.Data;

import java.util.*;

@Data
public class CustomerDeactivationMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private Long staffId;


    private Map<String, Object> customerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    public CustomerDeactivationMessage(String message, TemplateNotification template, String sourceName, Customers customer, Double amount, String currency, String remarks,String planname,String date,Long staffId) {

        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.staffId = staffId;
        this.customerData.put("mobileNumber", customer.getMobile());
        this.customerData.put("emailId", customer.getEmail());
        this.customerData.put("mvnoId", customer.getMvnoId());
        this.customerData.put("username", customer.getUsername());
        this.customerData.put("amount", amount);
        this.customerData.put("countryCode", customer.getCountryCode());
        this.customerData.put("currency", currency);
        this.customerData.put("remarks",remarks);
        this.customerData.put("planname",planname);
        this.customerData.put("date",date);
        if(Objects.nonNull(customer.getBuId())){
            this.customerData.put(RabbitMqConstants.BU_ID,customer.getBuId());
        }
        String firstName = customer.getFirstname() != null ? customer.getFirstname() : "";
        String lastName = customer.getLastname() != null ? customer.getLastname() : "";
        customerData.put("fullName", firstName + " " + lastName);
        this.customerData.put("userId", customer.getId());
        customerData.put("accountnumber",customer.getAcctno());
        if(Objects.isNull(customer.getBuId())){
            this.customerData.put(RabbitMqConstants.BU_ID,null);
        }


        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

    }

}
