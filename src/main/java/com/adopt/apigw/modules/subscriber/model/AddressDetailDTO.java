package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

@Data
public class AddressDetailDTO {
    private AddressDetailsModel present;
    private AddressDetailsModel permanent;
    private AddressDetailsModel payment;
}
