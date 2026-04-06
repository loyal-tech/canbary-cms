package com.adopt.apigw.soap.Dto;

import lombok.Data;

@Data
public class WsSubscribeAddOnRequest {
    private String parentId;
    private String subscriberId;
    private String alternateId;
    private Integer updateAction;
    private String cui;
    private Integer addOnPackageId;
    private String addOnPackageName;
    private String subscriptionStatusValue;
    private String subscriptionStatusName;
    private String startTime;
    private String endTime;
    private String parameter1;
    private String parameter2;
}
