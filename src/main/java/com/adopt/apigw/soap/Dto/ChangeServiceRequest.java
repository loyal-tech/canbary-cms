package com.adopt.apigw.soap.Dto;

import lombok.Data;

@Data
public class ChangeServiceRequest {
    private String actionItem;
    private String requestId;
    private String userName;
    private Double overrides;
    private String serviceId;
}
