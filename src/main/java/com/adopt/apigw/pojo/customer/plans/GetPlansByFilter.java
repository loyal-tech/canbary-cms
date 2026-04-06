package com.adopt.apigw.pojo.customer.plans;

import lombok.Data;

@Data
public class GetPlansByFilter {

    Integer custId;
    Integer customerServiceMappingID;
    Integer planGroupId;
    String changePlanType;
}
