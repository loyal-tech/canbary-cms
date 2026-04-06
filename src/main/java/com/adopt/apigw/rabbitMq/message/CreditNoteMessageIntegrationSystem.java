package com.adopt.apigw.rabbitMq.message;

import lombok.Data;

import java.util.Map;

@Data
public class CreditNoteMessageIntegrationSystem {
    Map<String, Double> data;

    String documentNumber;
    Integer creditDocId;

    Double amount;
    Integer customerId;

    public CreditNoteMessageIntegrationSystem(Map<String, Double> data, String documentNumber, Integer creditDocId, Double amount, Integer customerId)

    {
        this.data = data;
        this.documentNumber = documentNumber;
        this.creditDocId = creditDocId;
        this.amount = amount;
        this.customerId = customerId;
    }


}
