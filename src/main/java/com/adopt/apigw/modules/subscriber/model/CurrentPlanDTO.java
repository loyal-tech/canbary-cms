package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

@Data
public class CurrentPlanDTO {
    private String userName;
    private Double currentOutstanding;
    private String currentPlanName;
    private Long daysToExpiry;
    private Double usedDataQuota;
    private String totalDataQuota;
    private String quotaUnit;
    private String quotaunittime;
    private Double timeQuota;
    private Double refundableAmount;
    private Integer custCurrentPlanId;
}
