package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

@Data
public class CustomerLocationDTO {
    private Integer custId;
    private String latitude;
    private String longitude;
    private String url;
    private String remarks;
    private String gis_code;
}
