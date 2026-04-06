package com.adopt.apigw.model.postpaid;


import lombok.Data;

import java.util.List;

@Data
public class PlanGroupMappingChargeRelDto{
    private Integer id;
    private Double chargeprice;
    private String chargeName;

    private List<PlanGroupMappingChargeRel> planGroupMappingChargeRelList;
    private Double totalPrice;
}
