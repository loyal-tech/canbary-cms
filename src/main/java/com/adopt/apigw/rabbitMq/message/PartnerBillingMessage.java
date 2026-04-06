package com.adopt.apigw.rabbitMq.message;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustomerBillingMessage.class)
public class PartnerBillingMessage {

    private static final String BILLING = "billing";
    private static final String INVOICE_DATE = "invoice_date";

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private Map<String, Object> data;


    public PartnerBillingMessage() {
    }

    public PartnerBillingMessage(String invoiceDate) {
        Map<String, Object> map = new HashMap<>();
        map.put(INVOICE_DATE,invoiceDate);
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer Billing Engine from Api Gateway";
        this.data = map;
        this.sourceName = BILLING;
    }
}