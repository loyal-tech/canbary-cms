package com.adopt.apigw.modules.Customers;


import lombok.Data;

import java.util.List;

@Data
public class ServiceAreaFetchDTO {
    private List<Integer> sa;

    private String planGroupType;

    private List<String> planGroupTypes;

    private List<Integer> serviceIds;

    private Integer currentPlanId;

    private Boolean isQosUpgrade;

    private Boolean isQosDowngrade;

    private List<Long> locationIds;

}
