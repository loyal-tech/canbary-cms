package com.adopt.apigw.modules.MtnPayment.model;

import lombok.Data;

@Data
public class DebitCompletedRequest {

    private String transactionid;

    private String externaltransactionid;

}
