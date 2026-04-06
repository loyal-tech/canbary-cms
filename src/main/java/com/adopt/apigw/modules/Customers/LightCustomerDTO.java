package com.adopt.apigw.modules.Customers;

import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.pojo.api.CustPlanMapppingPojo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LightCustomerDTO {

    private Integer id;

    private String username;

    private String password;

    private String countryCode;

    private String mobile;

    private List<CustPlanMapppingPojo> planMappingList = new ArrayList<>();

    private String email;

    private String custtype;

    public LightCustomerDTO(Integer id, String username, String password,String countryCode, String mobile, String email,String customertype,List<CustPlanMapppingPojo> planMappingList){
        this.id = id;
        this.username = username;
        this.password = password;
        this.countryCode = countryCode;
        this.mobile = mobile;
        this.email = email;
        this.custtype = customertype;
        this.planMappingList = planMappingList;
    }

}
