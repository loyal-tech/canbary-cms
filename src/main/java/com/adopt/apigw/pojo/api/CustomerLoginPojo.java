package com.adopt.apigw.pojo.api;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CustomerLoginPojo {


    @NotNull
    private String username;

    private String password;
}
