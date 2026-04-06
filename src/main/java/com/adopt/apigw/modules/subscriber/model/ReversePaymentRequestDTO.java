package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.time.LocalDateTime;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

@Data
public class ReversePaymentRequestDTO extends UpdateAbstarctDTO {
    private Integer payment_id;
    private LocalDateTime rev_date;
    private String rev_remarks;
    private Integer custId;
    private double rev_amt;
}
