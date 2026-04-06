package com.adopt.apigw.modules.tickets.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tblcasereasonconfig")
public class CaseReasonConfig implements IBaseData<Long>{
	
/*
	create table tblcasereasonconfig
	(
	    config_id SERIAL PRIMARY KEY,
		staffid BIGINT UNSIGNED NOT NULL,		
      	reasonid BIGINT UNSIGNED NOT NULL,
		is_deleted BOOLEAN   NOT NULL DEFAULT false
	);
    ALTER TABLE tblcasereasonconfig ADD FOREIGN KEY (staffid) REFERENCES tblstaffuser (staffid);
    ALTER TABLE tblcasereasonconfig ADD FOREIGN KEY (reasonid) REFERENCES tblcasereasons (reason_id);
	ALTER TABLE tblcasereasonconfig ADD serviceareaid bigint REFERENCES tblservicearea (service_area_id);

*/
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id", nullable = false, length = 40)
    private Long id;

	@ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
	@JoinColumn(name = "serviceareaid")
	private ServiceArea serviceArea;

	@ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
	@JoinColumn(name = "staffid")
	private StaffUser staffUser;
//
//	@ManyToOne
//	@JoinColumn(name = "reasonid")
//	@ToString.Exclude
//    @EqualsAndHashCode.Exclude
//	@JsonBackReference
//	private CaseReason caseReason;

	@Column(columnDefinition = "Boolean default false", nullable = false)
	private Boolean isDeleted = false;
	
	@Column(name = "MVNOID", nullable = false, length = 40)
	private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

	@JsonIgnore
	@Override
	public void setDeleteFlag(boolean deleteFlag) {
		this.isDeleted = deleteFlag;
	}

	@JsonIgnore
	@Override
	public boolean getDeleteFlag() {
		return isDeleted;
	}

}
