package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AutoRenewalBoosterPlanMessage {

    private Boolean renewalForBooster;
    private Integer custPlanMappingId;

}
