package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanExpiryNotificationMessage {

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
    public PlanExpiryNotificationMessage(String emailId, String mobileNumber, String customerUsername, String planName, String expiryDate, String ccEmail, String countryCode, Integer mvnoid, Integer buid, Integer custId,Long staffId) {

        this.setMessage("Plane Expiry Notification");
        this.setSourceName(sourceName);

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.customerData.put("mobileNumber", mobileNumber);
        this.customerData.put("emailId", emailId);
        this.customerData.put("mvnoId", mvnoid);
        this.customerData.put("plan",planName);
        this.customerData.put("username", customerUsername);
        this.customerData.put("countryCode", countryCode);
        this.customerData.put("expiryDate" , expiryDate);
        this.customerData.put("buId",buid);
        this.customerData.put("custId",custId);
        this.customerData.put("customer_id",custId);
        this.customerData.put("parent_id",custId);
        this.customerData.put("type","plan_details");
        this.customerData.put(RabbitMqConstants.BU_ID,null);
        this.customerData.put("staffId",staffId);
        this.staffId = staffId;
        if(Objects.nonNull(ccEmail) && ccEmail.length() > 0){
            this.customerData.put("altEmail" , ccEmail);
        }
        this.isEmailConfigured = true;
        this.isSmsConfigured = true;

    }

}
