package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

@Data
public class UpdateGeneratedConnectionNumberMessage {
    private Integer id;
    private Integer customerId;
    private String connectionNo;
    private Integer partnerId;
    private Integer mvnoId;
}
