package com.adopt.apigw.pojo.customer.service;

import lombok.Data;

@Data
public class ServiceHoldStartPojo {

    private Integer customerServiceMappingId;

    private String reason;

    private int reasonId;
}
