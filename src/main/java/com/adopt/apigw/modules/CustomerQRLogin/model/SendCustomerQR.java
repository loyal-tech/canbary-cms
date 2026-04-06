package com.adopt.apigw.modules.CustomerQRLogin.model;

import lombok.Data;

@Data
public class SendCustomerQR {
    private String code;
    private String username;
    private String password;
    private String status;
}
