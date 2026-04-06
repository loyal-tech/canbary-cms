package com.adopt.apigw.rabbitMq.message;


import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Data
public class CustomerDunningMessage {


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
    private Long staffId;

    public CustomerDunningMessage(String message, TemplateNotification template, String sourceName, Customers customer, Double amount, String currency, LocalDate expirydate, LocalDate startDate, LocalDate endDate,LocalDateTime dueDate, String paymentUrl, Double taxAmount, Double taxPercentage,  Double subTotal, Double totalDue, Double walletBalance,String debitdocNumber,Long staffId) {

        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.staffId =staffId;
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.customerData.put("mobileNumber", customer.getMobile());
        this.customerData.put("custid", customer.getId());
        this.customerData.put("emailId", customer.getEmail());
        this.customerData.put("mvnoId", customer.getMvnoId());
        this.customerData.put("username", customer.getUsername());
        this.customerData.put("planname", customer.getPlanName());
        this.customerData.put("amount", amount != null ? amount : 0.0);
        this.customerData.put("countryCode", customer.getCountryCode());
        this.customerData.put("currency", currency);
        this.customerData.put("ExpiryDate", expirydate != null ? expirydate.toString() : null);
        this.customerData.put("startDate", startDate != null ? startDate.toString() : null);
        this.customerData.put("dueDate", dueDate != null ? dueDate.toString() : null);
        this.customerData.put("endDate", endDate != null ? endDate.toString() : null);
        this.customerData.put("paymentUrl", paymentUrl);
        this.customerData.put("taxAmount", taxAmount != null ? taxAmount : 0.0);
        this.customerData.put("taxPercentage", taxPercentage != null ? taxPercentage : 0.0);
        this.customerData.put("subTotal", subTotal != null ? subTotal : 0.0);
        this.customerData.put("totalDue", totalDue != null ? totalDue : 0.0);
        this.customerData.put("credit", walletBalance != null ? walletBalance : 0.0);
        this.customerData.put("invoiceNumber",debitdocNumber);

        if (Objects.nonNull(customer.getBuId())) {
            this.customerData.put(RabbitMqConstants.BU_ID, customer.getBuId());
        } else {
            this.customerData.put(RabbitMqConstants.BU_ID, null);
        }
        String firstName = customer.getFirstname() != null ? customer.getFirstname() : "";
        String lastName = customer.getLastname() != null ? customer.getLastname() : "";
        this.customerData.put("fullName", firstName + " " + lastName);
        this.customerData.put("accountnumber",customer.getAcctno());
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();
    }


    public CustomerDunningMessage(String message, TemplateNotification template, String sourceName, Customers customer, Double amount, String currency, LocalDate expirydate,String payentUrl) {

        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.customerData.put("mobileNumber", customer.getMobile());
        this.customerData.put("custid",customer.getId());
        this.customerData.put("emailId", customer.getEmail());
        this.customerData.put("mvnoId", customer.getMvnoId());
        this.customerData.put("username", customer.getUsername());
        this.customerData.put("planname" , customer.getPlanName());
        this.customerData.put("amount", amount);
        this.customerData.put("countryCode", customer.getCountryCode());
        this.customerData.put("currency", currency);
        this.customerData.put("ExpiryDate" , expirydate.toString());
        this.customerData.put("paymentUrl",payentUrl);
        if(Objects.nonNull(customer.getBuId())){
            this.customerData.put(RabbitMqConstants.BU_ID,customer.getBuId());
        }
        if(Objects.isNull(customer.getBuId())){
            this.customerData.put(RabbitMqConstants.BU_ID,null);
        }

        String firstName = customer.getFirstname() != null ? customer.getFirstname() : "";
        String lastName = customer.getLastname() != null ? customer.getLastname() : "";
        this.customerData.put("fullName", firstName + " " + lastName);
        this.customerData.put("accountnumber",customer.getAcctno());
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

    }

}
