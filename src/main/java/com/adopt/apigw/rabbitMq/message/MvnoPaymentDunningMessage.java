package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.modules.DebitDocumentInventoryRel.DebitDocNumberMappingPojo;
import com.adopt.apigw.modules.mvnoDocDetails.domain.MvnoDocDetails;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
public class MvnoPaymentDunningMessage {
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
    public MvnoPaymentDunningMessage(StaffUser staffUser , DebitDocument debitDocument , String ccEmail , boolean isEmailSend, boolean isSmsSend, LocalDate duedate) {

        this.setMessage("Mvno Payment Expire");
        this.setSourceName(sourceName);

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();

        this.customerData.put("mobileNumber", staffUser.getPhone());
        this.customerData.put("emailId", staffUser.getEmail());
        this.customerData.put("mvnoId", 1);
        this.customerData.put("username", staffUser.getUsername());
        this.customerData.put("countryCode", staffUser.getCountryCode());
        this.customerData.put("documentName", debitDocument.getDocnumber());
        this.customerData.put("invoiceNumber", debitDocument.getDocnumber());
        this.customerData.put("expiryDate" , debitDocument.getDuedate().toString());
        this.customerData.put("staffId",staffUser.getId());
        this.customerData.put(RabbitMqConstants.BU_ID,null);
        if(Objects.nonNull(ccEmail) && ccEmail.length() > 0){
            this.customerData.put("altEmail" , ccEmail);
        }
        this.isEmailConfigured = isEmailSend;
        this.isSmsConfigured = isSmsSend;

    }


}
