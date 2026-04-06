package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.util.Date;

@Data
public class CustomChangePlanDTO {
    CustomersBasicDetailsPojo customersBasicDetailsPojo;
    RecordpaymentResponseDTO recordpaymentResponseDTO;
    Integer custpackagerelid;
    Integer renewalId;
    String remarks;
    Date startdate;
    Date enddate;
}
