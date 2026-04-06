package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessUnitMessage {

	private Long id;

	private String buname;

	private String bucode;

	private String status;

	private Boolean isDeleted = false;

	private Integer mvnoId;

	private String planBindingType;
	
	public BusinessUnitMessage(BusinessUnit businessUnit) {
		this.id = businessUnit.getId();
		this.buname = businessUnit.getBuname();
		this.bucode = businessUnit.getBucode();
		this.status = businessUnit.getStatus();
		this.isDeleted = businessUnit.getIsDeleted();
		this.mvnoId = businessUnit.getMvnoId();
		if(businessUnit.getPlanBindingType()!= null)
			this.planBindingType = businessUnit.getPlanBindingType();
	}
}
