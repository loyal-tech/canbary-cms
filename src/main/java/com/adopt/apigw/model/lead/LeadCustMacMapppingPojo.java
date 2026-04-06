package com.adopt.apigw.model.lead;

import com.adopt.apigw.rabbitMq.message.LeadCustMacMapppingPojoMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadCustMacMapppingPojo {

	private Integer id;

	private String macAddress;

	private Boolean isDeleted = false;

	private Long leadMasterId;
	
	public LeadCustMacMapppingPojo(LeadCustMacMappping leadCustMacMappping) {
		this.id = leadCustMacMappping.getId();
		this.macAddress = leadCustMacMappping.getMacAddress();
		this.isDeleted = leadCustMacMappping.getIsDeleted();
		if(leadCustMacMappping.getLeadMaster() != null)
			this.leadMasterId = leadCustMacMappping.getLeadMaster().getId();
	}
	public LeadCustMacMapppingPojo(LeadCustMacMapppingPojoMessage leadCustMacMappping) {
		this.id = leadCustMacMappping.getId();
		this.macAddress = leadCustMacMappping.getMacAddress();
		this.isDeleted = leadCustMacMappping.getIsDeleted();
		this.leadMasterId = leadCustMacMappping.getLeadMasterId();
	}
}
