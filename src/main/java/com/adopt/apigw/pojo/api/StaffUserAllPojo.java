package com.adopt.apigw.pojo.api;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StaffUserAllPojo {
    private Integer id;
    @NotNull
    private String username;
}
