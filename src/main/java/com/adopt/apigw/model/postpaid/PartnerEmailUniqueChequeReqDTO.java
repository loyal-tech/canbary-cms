package com.adopt.apigw.model.postpaid;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PartnerEmailUniqueChequeReqDTO {
    @NotNull(message = "PLease provide value!")
    private String value;
    
    private Integer partnerId;
}
