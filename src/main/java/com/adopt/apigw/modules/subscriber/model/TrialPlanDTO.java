package com.adopt.apigw.modules.subscriber.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TrialPlanDTO {

    private Integer planId;

    private Integer planGroupId;

    private Integer cprId;

    private Integer custId;

    private Integer extendDays;

    private String billingStartFrom;

    private String remarks;
}
