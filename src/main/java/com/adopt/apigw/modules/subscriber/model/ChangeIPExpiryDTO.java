package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.util.Date;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

@Data
public class ChangeIPExpiryDTO extends UpdateAbstarctDTO {
    private Integer currentChargeId;
    private Date revisedExpiryDate;
}
