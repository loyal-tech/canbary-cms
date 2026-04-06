package com.adopt.apigw.rabbitMq.message;

import java.util.*;

import com.adopt.apigw.modules.Template.domain.TemplateNotification;

import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.Data;

@Data
public class CafFollowUpMessage {

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
    private String customer_type;
    private Integer customer_id;
	
	public CafFollowUpMessage(String message, TemplateNotification template, String sourceName, String mobileNo,String emailId,Integer mvnoId,String FollowupDateTime,Integer FollowupTime,String customerName,String staffPersonName,String parentStaffPersonName,String followUpName,Long buId,String username,Long staffId, Integer customer_id ,String customer_type) {

        this.setMessage(message);
        this.setSourceName(sourceName);
        this.setEmailTemplate(template.getEmailTemplateData());
        this.setSmsTemplate(template.getSmsTemplateData());
        this.setAppendUrl(template.getAppendUrl());

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.staffId=staffId;

        this.customerData.put("mobileNumber", mobileNo);
        this.customerData.put("emailId", emailId);
        this.customerData.put("mvnoId", mvnoId);
        this.customerData.put("followupDateTime", FollowupDateTime);
        this.customerData.put("followupTime", FollowupTime);
        this.customerData.put("customerName", customerName);
        this.customerData.put("username",username);
        this.customerData.put("staffPersonName", staffPersonName);
        this.customerData.put("parentStaffPersonName", parentStaffPersonName);
        this.customerData.put("followUpName", followUpName);
        this.customerData.put("customer_type", customer_type);
        this.customerData.put("customer_id", customer_id);
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
