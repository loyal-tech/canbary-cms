package com.adopt.apigw.pojo.api;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ServicePlan {
    Integer serviceId;
    ArrayList<PlanList> planList;
}
