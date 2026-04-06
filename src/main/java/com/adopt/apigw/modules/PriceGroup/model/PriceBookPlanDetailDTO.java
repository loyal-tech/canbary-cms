package com.adopt.apigw.modules.PriceGroup.model;

import javax.persistence.Column;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.postpaid.PlanGroup;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.pojo.api.PlanGroupDTO;
import com.adopt.apigw.pojo.api.PostpaidPlanPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class PriceBookPlanDetailDTO implements IBaseDto {
    private Long id;
    private Double offerprice;
    private Double partnerofficeprice;
    private String revsharen = "No";
    private String registration = "No";
    private String renewal = "No";

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private PriceBookDTO priceBook;

    private PostpaidPlanPojo postpaidPlan;

    private Boolean isDeleted = false;
    
    private String revenueSharePercentage;
    
    private Boolean isTaxIncluded = true;
    
    private Integer mvnoId;

    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}


    public PlanGroupDTO planGroup;


}
