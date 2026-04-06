package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SubscriberPlanDetailsDTO {
    private Long planId;
    private String fullName;
    private String userName;
    private String planName;
    private Long usedDays;
    private Double fullAmount;
    private Double outStanding;
    private Double proratedAmount;
    private LocalDate activationDate;
    private LocalDate expiryDate;
}
