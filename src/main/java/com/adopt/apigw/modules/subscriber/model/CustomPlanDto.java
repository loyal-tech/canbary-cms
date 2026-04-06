package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustomPlanDto {
    private Integer planId;
    private String planName;
    private String quotaType;
    private Long dataQuota;
    private Double timeQuota;
    private String quotaUnit;
    private String quotaunittime;
    private Double validity;
    private Double price;
    private LocalDateTime activationDate;
    private LocalDateTime expiryDate;
    private LocalDateTime renewalActivationDate;
    private LocalDateTime renewalExpiryDate;
}
