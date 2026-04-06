package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

import lombok.Data;

@Data
public class ReplaceIPDTO extends UpdateAbstarctDTO {
    private Long currentPoolDetailsId;
    private Long currentAllocatedId;
    private Integer currentChargeId;
    private Long newPoolDetailsId;
    private String remarks;
}
