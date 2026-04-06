package com.adopt.apigw.modules.childcustomer.parentchildmappingrel;

import lombok.Data;

@Data
public class VerifyChildDTO {

    private  String userName;

    private String mobileNumber;

    private Long parentId;
}
