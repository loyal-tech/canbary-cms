package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

import lombok.Data;

@Data
public class CancelPlanRequestDTO extends UpdateAbstarctDTO {
    private Integer custId;
    private Integer planId;
    private Boolean isFullRefund;
    private Boolean isProrated;
    private String remarks;
    private Boolean isActive;
    private Boolean isFuture;

    public CancelPlanRequestDTO() {

    }

    public CancelPlanRequestDTO(Integer custId, Boolean isFullRefund, Boolean isProrated, String remarks, Boolean isActive, Boolean isFuture) {
        this.custId = custId;
        this.isFullRefund = isFullRefund;
        this.isProrated = isProrated;
        this.remarks = remarks;
        this.isActive = isActive;
        this.isFuture = isFuture;
    }
}
