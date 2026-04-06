package com.adopt.apigw.pojo.api;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ServicePlansPojo {
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
    private Long investmentid;

}
