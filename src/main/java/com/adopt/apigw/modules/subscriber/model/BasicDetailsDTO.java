package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

import lombok.Data;

@Data
public class BasicDetailsDTO extends UpdateAbstarctDTO {
    private String title;
    private String name;
    private String contactPerson;
    private String aadharNumber;
    private String gst;
    private String pan;
    private String remarks;
    private Integer custId;
}
