package com.adopt.apigw.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadSourceDto {

	private Long id;

	private String leadSourceName;

	private String status;
		
	private Long mvnoId;

	private Long buId;
	
	public LeadSourceDto(com.adopt.apigw.model.lead.LeadSource leadSource) {
		this.id = leadSource.getId();
		this.leadSourceName = leadSource.getLeadSourceName();
		this.status = leadSource.getStatus();
		this.mvnoId = leadSource.getMvnoId();
		this.buId = leadSource.getBuId();
	}
}
