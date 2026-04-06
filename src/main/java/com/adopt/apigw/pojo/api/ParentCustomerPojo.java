package com.adopt.apigw.pojo.api;

import lombok.Data;

@Data
public class ParentCustomerPojo {
    private Integer id;
    private String name;

    public ParentCustomerPojo(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
