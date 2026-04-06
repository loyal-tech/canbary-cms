package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.Auditable;

import com.adopt.apigw.model.postpaid.PlanGroupMappingChargeRelDto;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import lombok.Data;

import javax.persistence.Transient;
import java.util.List;

@Data
public class PlanGroupMappingDTO extends Auditable {

	private Integer planGroupMappingId;
	private Integer planId;
	@Transient
	private PostpaidPlanPojo postpaidPlanPojo;
	private Integer planGroupId;
	private PostpaidPlan plan;
	private Double validity;
	private Integer mvnoId;
	private String service;
	private Double newOfferPrice;
	private List<PlanGroupMappingChargeRelDto> chargeList;
	private String mvnoName;

}
