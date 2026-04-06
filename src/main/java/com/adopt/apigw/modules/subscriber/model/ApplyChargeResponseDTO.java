package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.sql.Date;

@Data
public class ApplyChargeResponseDTO {
    private CustomersBasicDetailsPojo customersBasicDetails;
    private ApplyChargeRequestDTO basicChargeDetails;
}
