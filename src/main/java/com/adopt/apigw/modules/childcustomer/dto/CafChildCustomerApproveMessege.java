package com.adopt.apigw.modules.childcustomer.dto;

import lombok.Data;

@Data
public class CafChildCustomerApproveMessege {
    private Integer customerId;

    private Integer loggedInUser;

    private String status;

    private String cafApproveStatus;
}
