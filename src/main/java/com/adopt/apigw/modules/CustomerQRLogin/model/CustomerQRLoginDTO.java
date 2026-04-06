package com.adopt.apigw.modules.CustomerQRLogin.model;

import lombok.Data;

@Data
public class CustomerQRLoginDTO {

    private String code;

    private String username;


    private String password;
}
