package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

@Data
public class ActivatePlanReqModel {
    private Long planId;
    private Integer custId;
    private String remarks;
}
