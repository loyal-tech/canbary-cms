package com.adopt.apigw.modules.ChangePlanDTOs;

import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.Data;

import java.util.*;

@Data
public class ChangePlanNotification {
    private  String oldPlanName;
    private String newPlanName;
    private Integer validity;
    private String expiryDate;
    private String username;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String messageId;
    private boolean isEmailConfigured;
    private boolean isSmsConfigured;
    private Map<String, Object> customerData = new HashMap<>();
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private Long staffId;

    public ChangePlanNotification(String emailId, String mobileNumber, String customerUsername,  String expiryDate, String ccEmail, String countryCode, Integer mvnoid, Integer buid,String oldPlanName,String newPlanName,Integer validityDays,String validityUnits,Integer custId ,Long staffId,String status
    ) {

        this.setMessage("Plane Expiry Notification");
        this.setSourceName(sourceName);

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.staffId=staffId;
        this.customerData.put("mobileNumber", mobileNumber);
        this.customerData.put("emailId", emailId);
        this.customerData.put("mvnoId", mvnoid);
        this.customerData.put("oldPlanName",oldPlanName);
        this.customerData.put("newPlanName",newPlanName);
        this.customerData.put("validityDays",validityDays);
        this.customerData.put("username", customerUsername);
        this.customerData.put("countryCode", countryCode);
        this.customerData.put("expiryDate" , expiryDate);
        this.customerData.put("buId",buid);
        this.customerData.put("custId",custId);
        this.customerData.put("customer_id",custId);
        this.customerData.put("parent_id",custId);
        this.customerData.put(RabbitMqConstants.BU_ID,null);
        this.customerData.put("validityUnits",validityUnits);
        this.customerData.put("type","change_plan");
        this.customerData.put("status",status);
        if(Objects.nonNull(ccEmail) && ccEmail.length() > 0){
            this.customerData.put("altEmail" , ccEmail);
        }
        this.isEmailConfigured = true;
        this.isSmsConfigured = true;

    }



}
