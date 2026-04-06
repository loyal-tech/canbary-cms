package com.adopt.apigw.soap.Dto;

import lombok.Data;

@Data
public class TerminateAddOnMessage {
    private Integer custId;
    private String Status;
    private Boolean GenerateCreditnote=false;
    private Integer custpackrelid;


}
