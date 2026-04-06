package com.adopt.apigw.modules.CafFollowUp.model;

import java.time.LocalDateTime;

import com.adopt.apigw.core.dto.IBaseDto;

import lombok.Data;

@Data
public class CafFollowUpRemarkDTO implements IBaseDto{

	private Long id;

	private String remark;
	
	private Long cafFollowUpId;
	
	private String cafFollowUpName;
	
	private LocalDateTime createdOn;

	@Override
	public Long getIdentityKey() {
		return id;
	}

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMvnoId(Integer mvnoId) {
		// TODO Auto-generated method stub
		
	}
}
