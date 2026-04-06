package com.adopt.apigw.modules.childcustomer.dto;

import lombok.Data;

@Data
public class ChangePasswordPojo {
    private String userName ;
    private String oldPassword;
    private String newPassword;
}
