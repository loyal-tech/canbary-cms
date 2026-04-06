package com.adopt.apigw.modules.Customers;

import com.adopt.apigw.pojo.api.CustomersPojo;
import lombok.Data;

@Data
public class CaptiveCustomerResponseDTO {

    private Boolean isParentCustAvailable;

    private Boolean isBranchBindWithServiceArea;

    private Boolean isPartnerBindWithServiceArea;

    private CustomersPojo parentCustomers;

}
