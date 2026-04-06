package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

import lombok.Data;

@Data
public class AllocateIpDTO extends UpdateAbstarctDTO {
    private Integer custId;
    private Integer chargeId;
    private Long ipPoolDtlsId;
    private String remarks;
}
