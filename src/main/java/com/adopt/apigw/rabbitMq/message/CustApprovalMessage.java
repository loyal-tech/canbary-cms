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
public class CustApprovalMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;

    private String approverTeam;
    private String reqStatus;
    private String username;
    private String mobileNumber;
    private String emailId;
    private Integer mvnoId;
    private String countryCode;
    private Long staffId;
    private String CustStatus;
    private String CustType;




    private Map<String,Object> customerData=new HashMap<>();
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;

//    public CustApprovalMessage(Customers customersVo, String message, TemplateNotification template, String sourceName, String approverTeam){
//        Map<String,Object> map = new HashMap<>();
//        map.put(RabbitMqConstants.USER_NAME, customersVo.getUsername());
//        map.put(RabbitMqConstants.MOBILE_NUMBER, customersVo.getMobile());
//        map.put(RabbitMqConstants.EMAIL_ADDRESS, customersVo.getEmail());
//        map.put(RabbitMqConstants.MVNO_ID,customersVo.getMvnoId());
//
//
//        this.setMessage(message);
//        this.setEmailTemplate(template.getEmailTemplateData());
//        this.setSmsTemplate(template.getSmsTemplateData());
//        this.setAppendUrl(template.getAppendUrl());
//        this.messageDate = new Date();
//        this.messageId = UUID.randomUUID().toString();
//        this.setApproverTeam(approverTeam);
//        this.sourceName = sourceName;
//
//
//    }

    public CustApprovalMessage(String username,String countryCode, String mobileNumber, String emailId, Integer mvnoId, String message, TemplateNotification template, String sourceName, String approverTeam , Long buId,Long staffId,String custStatus,String custType){

        this.setMessage(message);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        //this.setApproverTeam(approverTeam);
        this.sourceName = sourceName;
        this.staffId = staffId;
        customerData.put("username",username);
        customerData.put("mobileNumber",mobileNumber);
        customerData.put("emailId",emailId);
        customerData.put("mvnoId",mvnoId);
        customerData.put("approverTeam",approverTeam);
        customerData.put("countryCode",countryCode);
        customerData.put("custStatus",custStatus);
        customerData.put("custType",custType);
        customerData.put("isCustomer",true);
        this.customerData.put("staffid",staffId);
        if(Objects.nonNull(buId)) {
            customerData.put(RabbitMqConstants.BU_ID, buId);
        }
        if(Objects.isNull(buId)){
            customerData.put(RabbitMqConstants.BU_ID, null);
        }
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();

//        this.username = username;
//        this.emailId = emailId;
//        this.mobileNumber = mobileNumber;
    }


}
