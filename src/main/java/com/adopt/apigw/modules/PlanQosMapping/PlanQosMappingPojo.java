package com.adopt.apigw.modules.PlanQosMapping;

import lombok.Data;

@Data
public class PlanQosMappingPojo {
    private  Integer planid;
    private Long qosid;
    private Double frompercentage;
    private Double topercentage;

    private Boolean isdelete;

   private String qosPolicyName;

}

