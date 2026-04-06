package com.adopt.apigw.rabbitMq.message;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BudpayChangePlanMessage {
    Integer customerId;

    String paymentStatus;

    String referenceNumber;

    Integer creditDocId;

    Integer planId;

    Double amount;

    Integer staffId;
}
