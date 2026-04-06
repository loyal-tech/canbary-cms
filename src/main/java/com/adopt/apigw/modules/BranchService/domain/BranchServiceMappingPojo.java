package com.adopt.apigw.modules.BranchService.domain;

import lombok.Data;

@Data
public class BranchServiceMappingPojo {
    private Integer id;

    private Long branchId;
    private Integer serviceId;
    private Double revenueShareper;

    private Boolean isDeleted = false;

}
