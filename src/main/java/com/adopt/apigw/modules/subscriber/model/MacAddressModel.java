package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

@Data
public class MacAddressModel {

    private Integer id;
    private String macAddress;
    private Integer custId;
}
