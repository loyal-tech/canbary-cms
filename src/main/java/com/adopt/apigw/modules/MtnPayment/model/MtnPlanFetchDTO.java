package com.adopt.apigw.modules.MtnPayment.model;

import lombok.Data;

@Data
public class MtnPlanFetchDTO {

    private String service;

    private String mobileNumber;

    private String transactionId;

    private String username;

    private String password;
}
