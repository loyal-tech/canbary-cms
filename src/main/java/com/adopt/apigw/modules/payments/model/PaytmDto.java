package com.adopt.apigw.modules.payments.model;

import lombok.Data;

@Data
public class PaytmDto {
    private Integer partnerId;
    private Double amount;
}
