package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

@Data
public class CalculateChargePojo {
    Integer custChargeId;
    Double fullAmount;
    Double proratedAmount;
}
