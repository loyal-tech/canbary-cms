package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

import lombok.Data;

@Data
public class StatusDTO extends UpdateAbstarctDTO {
    private Integer custId;
    private String currentStatus;
    private String changedStatus;
    private String remarks;
}
