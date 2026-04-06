package com.adopt.apigw.pojo.api;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VoucherBatchPojo {
    private Integer id;
    private String voucherCode;
    private Integer vcId;
    private Integer planId;
    private LocalDate validity;
}
