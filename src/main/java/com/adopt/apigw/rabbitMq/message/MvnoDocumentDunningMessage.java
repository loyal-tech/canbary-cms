package com.adopt.apigw.rabbitMq.message;


import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.mvnoDocDetails.domain.MvnoDocDetails;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
public class MvnoDocumentDunningMessage {


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

    public MvnoDocumentDunningMessage(StaffUser staffUser , MvnoDocDetails mvnoDocDetails , String ccEmail , boolean isEmailSend, boolean isSmsSend) {

        this.setMessage("Mvno Document Expire");
        this.setSourceName(sourceName);

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.customerData.put("mobileNumber", staffUser.getPhone());
        this.customerData.put("emailId", staffUser.getEmail());
        this.customerData.put("mvnoId", 1);
        this.customerData.put("username", staffUser.getUsername());
        this.customerData.put("countryCode", staffUser.getCountryCode());
        this.customerData.put("documentName", mvnoDocDetails.getFilename());
        this.customerData.put("expiryDate" , mvnoDocDetails.getEndDate().toString());
        this.customerData.put("staffId",staffUser.getId());
        this.customerData.put(RabbitMqConstants.BU_ID,null);
        if(Objects.nonNull(ccEmail) && ccEmail.length() > 0){
          this.customerData.put("altEmail" , ccEmail);
        }
        this.isEmailConfigured = isEmailSend;
        this.isSmsConfigured = isSmsSend;

    }

}
