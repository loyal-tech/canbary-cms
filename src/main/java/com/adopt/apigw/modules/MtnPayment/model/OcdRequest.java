package com.adopt.apigw.modules.MtnPayment.model;

import lombok.Data;

@Data
public class OcdRequest {

    private Integer planId;

    private String mobileNumber;

    private String transactionNumber;


}
