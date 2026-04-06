package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.time.LocalDate;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

@Data
public class AdjustPaymentDTO extends UpdateAbstarctDTO {
    private String paymentType;
    private Double amount;
    private LocalDate paymentDate;
    private String remarks;
}
