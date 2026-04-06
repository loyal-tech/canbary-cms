package com.adopt.apigw.modules.payments.model;

import lombok.Data;

import java.util.Date;

@Data
public class PaymentDTO {

    private String email;
    private String name;
    private String phone;
    private String productInfo;
    private String amount;
    private PaymentStatus paymentStatus;
    private Date paymentDate;
    private String txnId;
    private String mihpayId;
    private PaymentMode mode;
    private String hash;
    private String sUrl;
    private String fUrl;
    private String key;
    private String command;

}
