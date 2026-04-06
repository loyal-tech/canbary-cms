package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

import lombok.Data;

@Data
public class NetworkDetailsDTO extends UpdateAbstarctDTO {
    private String networkType;
    private Long defaultPool;
    private Long olt;
    private Long oltPort;
    private Long oltSlot;
    private Long serviceArea;
    private String onuId;
    private String connectionType;
    private String remarks;
    private Integer custId;
    private String serviceType;
}
