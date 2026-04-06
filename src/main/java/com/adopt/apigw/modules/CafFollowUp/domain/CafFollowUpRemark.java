package com.adopt.apigw.modules.CafFollowUp.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.adopt.apigw.core.data.IBaseData;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTCAFFOLLOWUPREMARK")
public class CafFollowUpRemark implements IBaseData<Long>{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "caf_follow_up_remark_id", nullable = false)
	private Long id;

	@Column(name = "remark")
	private String remark;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "caf_follow_up_id")
	private CafFollowUp cafFollowUp;
	
	@CreationTimestamp
	@Column(name = "created_on")
	private LocalDateTime createdOn;

	@Override
	public Long getPrimaryKey() {
		return id;
	}

	@Override
	public void setDeleteFlag(boolean deleteFlag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getDeleteFlag() {
		// TODO Auto-generated method stub
		return false;
	}
}
