package com.adopt.apigw.pojo.api;

import lombok.Data;

@Data
public class PlanByPartnerReqDTO {
    private Integer partnerId;
    private Integer serviceType;
    private String planGroup;

    public PlanByPartnerReqDTO() {
    }

    public PlanByPartnerReqDTO(Integer partnerId, Integer serviceType, String planGroup) {
        this.partnerId = partnerId;
        this.serviceType = serviceType;
        this.planGroup = planGroup;
    }
}
