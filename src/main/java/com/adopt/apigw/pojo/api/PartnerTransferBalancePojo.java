package com.adopt.apigw.pojo.api;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class PartnerTransferBalancePojo {
    private Integer partnerId;
    private String transferFrom;
    private Double amount;
    private String remarks;
}
