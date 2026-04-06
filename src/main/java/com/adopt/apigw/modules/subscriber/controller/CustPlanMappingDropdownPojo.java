package com.adopt.apigw.modules.subscriber.controller;

import lombok.Data;

@Data
public class CustPlanMappingDropdownPojo {
    private  Integer id;


    private String serialNumber;

    public CustPlanMappingDropdownPojo() {
    }

    public CustPlanMappingDropdownPojo(Integer id, String serialNumber) {
        this.id = id;
        this.serialNumber = serialNumber;
    }
}
