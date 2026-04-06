package com.adopt.apigw.pojo.customer.plans;

import com.adopt.apigw.pojo.ExtendPlanValidity;
import lombok.Data;

import java.util.List;

@Data
public class ExtendPlanValidityInBulk {

    private List<ExtendPlanValidity> extendPlanValidity;
}
