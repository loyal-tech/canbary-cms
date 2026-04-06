package com.adopt.apigw.modules.paymentGatewayMaster.domain;

import lombok.Data;

@Data
public class PaymentGatewayResponse {

    private String pgTransactionId;

    private Long orderId;

    private Integer redirectTimeInSecond;

    private String username;

    private String password;

    private String redirecturl;


}
