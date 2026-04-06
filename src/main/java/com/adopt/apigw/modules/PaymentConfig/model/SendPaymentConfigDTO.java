package com.adopt.apigw.modules.PaymentConfig.model;


import com.adopt.apigw.modules.PaymentConfigMapping.entity.PaymentConfigMapping;
import lombok.Data;

import java.util.List;

@Data
public class SendPaymentConfigDTO {

    private Long paymentConfigId;

    private String paymentConfigName;

    private List<PaymentConfigMapping> paymentConfigMappingList;


    private Boolean isDelete;


    private Long mvnoId;

    private Boolean isActive;
}
