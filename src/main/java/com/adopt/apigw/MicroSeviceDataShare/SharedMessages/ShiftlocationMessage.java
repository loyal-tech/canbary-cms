package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

@Data
public class ShiftlocationMessage {
    private Integer oldpartnerId;
    private Integer newPartnerId;
    private Integer custId;
    private Long serviceAreaId;
    private Double transferBalance;
    private Double tranferConnission;
    private Integer chargeId;
    private Double amount;
    private Integer billableCustomerId;
    private String paymentowner;
    private Double discount;
}
