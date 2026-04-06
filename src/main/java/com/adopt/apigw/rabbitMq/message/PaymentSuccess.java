package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSuccess {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;

    private String customerName ;
    private String currencySymbol;
    private Double paymentAmount;
    private String paymentMode;
    private String mobileNumber;
    private String emailId;

    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    private String appendUrl;

    private Integer mvnoId;
    private String countryCode;

    private Integer customerId;
    private String reciptNo;
    private String paymentDate;
    private Long staffId;
    private String screen;
    private String type;
    private Integer id ;

    private Map<String,Object> customerData = new HashMap<>();


    public PaymentSuccess(String message , String customerName, String currencySymbol, Double paymentAmount, String paymentMode, Integer mvnoId, TemplateNotification template, String sourceName, String countryCode, String mobileNumber, String emailId, Integer customerId, String reciptNo, String paymentDate , Long buId, String planName, String password, Long staffId, Customers customers,Integer id,String type){
        this.setMessage(message);
        this.setMessage(message);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        //this.setApproverTeam(approverTeam);
        this.sourceName = sourceName;
        this.staffId = staffId;
        customerData.put("userName",customerName);
        customerData.put("custid",customerId);
        customerData.put("accountnumber",customers.getAcctno());
        customerData.put("currencySymbol",currencySymbol);
        customerData.put("paymentAmount",paymentAmount);
        customerData.put("paymentMode",paymentMode);
        customerData.put("mvnoId",mvnoId);
        customerData.put("emailId",emailId);
        customerData.put("mobileNumber",mobileNumber);
        customerData.put("countryCode",countryCode);
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();
        customerData.put("planname", planName);
        customerData.put("password", password);
        customerData.put("userId", customerId);
        customerData.put("reciptNo", reciptNo);
        customerData.put("paymentDate",paymentDate);
        customerData.put("isCustomer",true);
        customerData.put("type",type);
        customerData.put("screen","customer_payment");
        customerData.put("id",id);

        String firstName = customers.getFirstname() != null ? customers.getFirstname() : "";
        String lastName = customers.getLastname() != null ? customers.getLastname() : "";
        customerData.put("fullName", firstName + " " + lastName);
        if(Objects.nonNull(buId)) {
            customerData.put(RabbitMqConstants.BU_ID, buId);
        }
        if(Objects.isNull(buId)){
            customerData.put(RabbitMqConstants.BU_ID, null);
        }

    }

}
