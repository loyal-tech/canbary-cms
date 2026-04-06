package com.adopt.apigw.rabbitMq.message;

import lombok.Data;

@Data
public class CustomerTerminationMessage {
    private Integer custId;
    private String Status;
    private Boolean GenerateCreditnote=false;


}
