package com.adopt.apigw.rabbitMq.message;

import lombok.Data;
@Data
public class NasUpdateMessage {

    private Integer customerId;
    private String nasPort;
    private String  framedIp;

    public NasUpdateMessage() {
        this.customerId = getCustomerId();
        this.framedIp = getFramedIp();
        this.nasPort = getNasPort();
    }
}
