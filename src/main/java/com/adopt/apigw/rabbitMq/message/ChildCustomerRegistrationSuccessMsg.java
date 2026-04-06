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
public class ChildCustomerRegistrationSuccessMsg {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private String registrationStatus;
    private String password;

    private Map<String,Object> customerData = new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

    private String mobileNumber;
    private String emailId;
    private String username;
    private Long mvnoId;
    private String countryCode;
    private Long staffId;

    private String registrationDate;
    private String planname;
    private String accountNumber;
    private  Long id;
    private String types;
    public  ChildCustomerRegistrationSuccessMsg(String registrationDate, String planname, String username, String password, String countryCode, String  mobileNumber,
                                                String emailId, Long mvnoId, String message, String sourceName, String status, Integer buId,
                                                String accountNumber,String parentCustomerName,Long staffId,Long id,String types) {
        this.setMessage(message);
//        this.setEmailTemplate(template.getEmailTemplateData());
//        this.setSmsTemplate(template.getSmsTemplateData());
//        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.setRegistrationStatus(status);
        this.sourceName = sourceName;
        this.staffId=staffId;
        customerData.put("username",username);
        customerData.put("registrationDate",registrationDate);
        customerData.put("planname",planname);
        customerData.put("mvnoId",mvnoId);
        customerData.put("mobileNumber",mobileNumber);
        customerData.put("emailId",emailId);
        customerData.put("password",password);
        customerData.put("countryCode",countryCode);
        customerData.put("accountNumber", accountNumber);
        customerData.put("isCustomer",true);
        customerData.put("id",id);
        customerData.put("type",types);
        customerData.put("status",status);

        if(Objects.nonNull(buId)) {
            customerData.put(RabbitMqConstants.BU_ID, buId);
        }
        if(Objects.isNull(buId)){
            customerData.put(RabbitMqConstants.BU_ID, null);
        }
        this.customerData.put("parentCustomerName",parentCustomerName);
//        this.isEmailConfigured = template.isEmailEventConfigured();
//        this.isSmsConfigured = template.isSmsEventConfigured();
    }
}
