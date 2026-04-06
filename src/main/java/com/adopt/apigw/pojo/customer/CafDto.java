package com.adopt.apigw.pojo.customer;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.service.common.CustomersService;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CafDto {

    Customers customers;
    CustomersService customersService;
    GenericDataDTO genericDataDTO;
    Map<String,Object> map = new HashMap<>();

}
