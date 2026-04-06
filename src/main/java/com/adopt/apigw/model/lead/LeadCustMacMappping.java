package com.adopt.apigw.model.lead;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffInclude;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblleadcustmacmapping")
public class LeadCustMacMappping {

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "custmacmapid", nullable = false, length = 40)
	private Integer id;

	@Column(name = "mac_address")
	private String macAddress;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;
	
	public LeadCustMacMappping(LeadCustMacMapppingPojo leadCustMacMapppingPojo) {
		this.id = leadCustMacMapppingPojo.getId();
		this.macAddress = leadCustMacMapppingPojo.getMacAddress();
		this.isDeleted = leadCustMacMapppingPojo.getIsDeleted();
		if(leadCustMacMapppingPojo.getLeadMasterId() != null)
			this.leadMaster = new LeadMaster(leadCustMacMapppingPojo.getLeadMasterId());
	}
}
