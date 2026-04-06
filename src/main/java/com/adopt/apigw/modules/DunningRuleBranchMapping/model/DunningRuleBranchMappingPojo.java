package com.adopt.apigw.modules.DunningRuleBranchMapping.model;

public class DunningRuleBranchMappingPojo {

    private Integer id;

    private Long branchId;

    private Long serviceAreaId;

    private Integer dunningRuleId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Long getServiceAreaId() {
        return serviceAreaId;
    }

    public void setServiceAreaId(Long serviceAreaId) {
        this.serviceAreaId = serviceAreaId;
    }

    public Integer getDunningRuleId() {
        return dunningRuleId;
    }

    public void setDunningRuleId(Integer dunningRuleId) {
        this.dunningRuleId = dunningRuleId;
    }
}
