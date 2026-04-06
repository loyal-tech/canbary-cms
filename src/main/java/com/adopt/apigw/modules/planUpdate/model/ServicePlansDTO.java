package com.adopt.apigw.modules.planUpdate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ServicePlansDTO {
    private Integer planId;
    private String planName;
    private LocalDate expiry;
    private String quotaType;
    private Double dataTotalQuota;
    private Double dataUsedQuota;
    private String dataQuotaUnit;
    private Double timeTotalQuota;
    private Double timeUsedQuota;
    private String timeQuotaUnit;
    private Long qosPolicyId;
}
