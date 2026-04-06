package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

@Data
public class MacUpdateDetailsModel {
    private Integer id;
    private String macAddress;
    private Boolean isDeleted;
}
