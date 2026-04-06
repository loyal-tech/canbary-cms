package com.adopt.apigw.modules.Customers;

import lombok.Data;

@Data
public class FindCustomerDTO {

    private String username;

    private String mac;

    private String countryId;

}
