package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

@Data
public class ReversePaymentResponseDTO {
    private CustomersBasicDetailsPojo customersBasicDetails;
    private ReversePaymentRequestDTO basicReversePayment;
}
