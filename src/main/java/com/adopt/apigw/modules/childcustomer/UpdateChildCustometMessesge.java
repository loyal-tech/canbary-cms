package com.adopt.apigw.modules.childcustomer;

import lombok.Data;

@Data
public class UpdateChildCustometMessesge {
    private Long Id;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String email;
    private Long parentCustId;
    private String status;
    private String mobileNumber;
    private Boolean isParent;

}
