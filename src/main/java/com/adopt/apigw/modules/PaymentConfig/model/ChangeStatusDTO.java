package com.adopt.apigw.modules.PaymentConfig.model;

import lombok.Data;

@Data
public class ChangeStatusDTO {

    private Long paymentConfigId;

    private Boolean isActive;


}
