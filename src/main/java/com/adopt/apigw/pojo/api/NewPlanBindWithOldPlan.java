package com.adopt.apigw.pojo.api;

import lombok.Data;

@Data
public class NewPlanBindWithOldPlan {
    private Integer newPlanId;
    private Integer oldPlanId;
    private Integer custServiceMappingId;
    private Double discount;
}
