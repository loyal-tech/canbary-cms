package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SubscriberUniqueChequeReqDTO {

    @NotNull(message = "PLease provide type!")
    private String type;
    @NotNull(message = "PLease provide value!")
    private String value;
    
    private Integer subscriberId;
}
