package com.adopt.apigw.pojo.api;

import lombok.Data;

import java.time.LocalDate;
@Data
public class PartnerAddBalancePojo {
    private Integer partnerId;
    private Double balance;
    private String remark;
    private String paymentMode;
    private Integer credit;
    private String onlinesource;
    private Long sourceBank;
    private Long destinationBank;
    private String chequeno;
    private LocalDate chequedate;
    private String referenceno;
}
