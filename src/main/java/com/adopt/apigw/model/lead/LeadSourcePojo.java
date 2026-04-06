package com.adopt.apigw.model.lead;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadSourcePojo {

	private Long id;

	private String leadSourceName;

	private String status;

    private Boolean isDelete = false;
	
	private Long mvnoId;

	private Long buId;
	
	public LeadSourcePojo(LeadSource leadSource) {
		this.id = leadSource.getId();
		this.leadSourceName = leadSource.getLeadSourceName();
		this.status = leadSource.getStatus();
		this.isDelete = leadSource.getIsDelete();
		this.mvnoId = leadSource.getMvnoId();
		this.buId = leadSource.getBuId();
	}
}
