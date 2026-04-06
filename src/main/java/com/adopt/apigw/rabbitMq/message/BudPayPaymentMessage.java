package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BudPayPaymentMessage {

    Integer customerId;

    String paymentStatus;

    String referenceNumber;

    Integer creditDocId;

    public BudPayPaymentMessage(Integer customerId, String referenceNumber) {
        this.customerId = customerId;
        this.referenceNumber = referenceNumber;
    }

    public BudPayPaymentMessage(Integer customerId, String paymentStatus, String referenceNumber) {
        this.customerId = customerId;
        this.paymentStatus = paymentStatus;
        this.referenceNumber = referenceNumber;
    }
}
