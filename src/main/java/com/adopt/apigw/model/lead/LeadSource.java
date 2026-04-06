package com.adopt.apigw.model.lead;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLMLEADSOURCE")
public class LeadSource {

	@Id
	@Column(name = "lead_source_id", nullable = false)
	private Long id;

	@Column(name = "lead_source_name", nullable = false, length = 250)
	private String leadSourceName;

	@Column(name = "status", nullable = false, length = 250)
	private String status;

	@Column(name = "is_delete",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
	
	@Column(name = "mvno_id")
	private Long mvnoId;

	@Column(name = "bu_id")
	private Long buId;
	
	public LeadSource(LeadSourcePojo leadSourcePojo) {
		this.id = leadSourcePojo.getId();
		this.leadSourceName = leadSourcePojo.getLeadSourceName();
		this.status = leadSourcePojo.getStatus();
		this.isDelete = leadSourcePojo.getIsDelete();
		this.mvnoId = leadSourcePojo.getMvnoId();
		this.buId = leadSourcePojo.getBuId();
	}

}
