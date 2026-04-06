package com.adopt.apigw.pojo.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class DunningRulePojo extends ParentPojo{

    private Integer id;

    @NotNull
    private String name;



    private String ccemail;

    private String mobile;


    @NotNull
    private String creditclass;

    @NotNull
    private String status;

    private List<DunningRuleActionPojo> dunningRuleActionPojoList;

    private Boolean isDelete;

    private Integer mvnoId;

    private String customerType;

    private String dunningType;

    private String dunningSubSector;
    private String dunningSubType;
    private String dunningSector;
    private Integer lcoId;

    private String customerPayType;

    private String dunningFor;

    private List<Long> serviceAreaIds;

    private List<Long> branchIds;

    private List<Long> partnerIds;

    private List<String> branchNames;

    private List<String> partnerNames;
    private  Boolean isGeneratepaymentLink=false;


}
