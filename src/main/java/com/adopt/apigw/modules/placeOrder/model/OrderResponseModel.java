package com.adopt.apigw.modules.placeOrder.model;

import lombok.Data;

import java.util.Map;

@Data
public class OrderResponseModel {

    private Map<String, Object> pgDetails;
    private Double amount = 0.0;
    private String uniqueId;
    private String submitUrl;
    private Boolean isPurchased;
    private Boolean isPaymentRemaining;


}
