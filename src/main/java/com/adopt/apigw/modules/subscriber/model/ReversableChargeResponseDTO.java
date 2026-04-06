package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

@Data
public class ReversableChargeResponseDTO {
    private CustomersBasicDetailsPojo customersBasicDetails;
    private ReverseChargeRequestDTO basicReverseCharge;
}
