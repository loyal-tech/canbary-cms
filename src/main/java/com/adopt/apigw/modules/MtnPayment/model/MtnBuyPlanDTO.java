package com.adopt.apigw.modules.MtnPayment.model;

import lombok.Data;

@Data
public class MtnBuyPlanDTO {

    private Integer planId;

    private String mobileNumber;

    private String transactionId;

    private String username;

    private String password;
}
