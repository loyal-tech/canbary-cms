package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

import java.time.LocalDate;

@Data
public class ExpiryDTO extends UpdateAbstarctDTO {
    private Integer custId;
    private String service;
    private Integer planId;
    private Integer planmapid;
    private String plan;
    private String currentExpiryDate;
    @NotNull
    private LocalDate revisedExpiryDate;
    private String remarks;
}
