package com.adopt.apigw.modules.subscriber.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRenewalStatusRequest {
    private Integer custPlanMapppingId;
    private Boolean renewalForBooster;
}
