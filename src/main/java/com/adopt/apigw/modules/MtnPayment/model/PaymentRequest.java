package com.adopt.apigw.modules.MtnPayment.model;


import lombok.Data;

@Data
public class PaymentRequest {

    private String amount;
    private String transactionId;

    private String currency;

    private String fromfri;
}
