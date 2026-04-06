package com.adopt.apigw.pojo.CreditDoc;

import lombok.Data;

@Data
public class CreditDocChargeRelDTO {

    private Long id;

    private String chargeName;

    private Integer debitDocId;

    private double chargeAmount;

    private double discount;

    private double taxAmount;

    private double totalAmount;
}
