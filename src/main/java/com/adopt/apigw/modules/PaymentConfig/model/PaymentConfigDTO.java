package com.adopt.apigw.modules.PaymentConfig.model;

import com.adopt.apigw.modules.PaymentConfigMapping.entity.PaymentConfigMapping;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class PaymentConfigDTO {

    private Long paymentConfigId;

    private String paymentConfigName;

    private List<PaymentConfigMapping> paymentConfigMappingList;


    private Boolean isDelete;

    private LocalDateTime createDate;

    private Long mvnoId;

    private Boolean isActive;

}
