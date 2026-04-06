package com.adopt.apigw.service.BulkService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostpaidPlanDTO {

    private Integer postpaidPlanId;
    private String name;
    private String displayName;
    private String planCode;
    private String planCategory;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long quota;
    private String quotaType;
    private String usageQuotaType;
    private String maxConcurrentSession;
    private String quotaRestInterval;
    private String quotaUnit;
    private String status;
    private String planGroup;
    private Double validity;
    private String unitsOfValidity;
    private Boolean allowOverUsage;
    private String qosName;
    private String qosPolicyName;
    private String baseParam1;
    private String baseParam2;
    private String baseParam3;
    private String thParam1;
    private String thParam3;
    private String basePolicyName;
    private String qosSpeed;

}

