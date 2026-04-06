package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

@Data
public class SubscriberPopupDTO {
    private Integer custId;
    private Boolean onlinerenewalflag = false;
    private Boolean voipenableflag = false;
    private String remarks;

}
