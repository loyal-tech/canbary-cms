package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;
import com.adopt.apigw.pojo.api.CustomerAddressPojo;

import lombok.Data;

@Data
public class AddressUpdateDTO extends UpdateAbstarctDTO {
    private CustomerAddressPojo address;
    private String addressType;
    private String remarks;
    private Integer custId;
}
