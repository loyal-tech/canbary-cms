package com.adopt.apigw.modules.tickets.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class CaseReasonConfigPojo implements IBaseDto {

    private Long id;

    private Long serviceareaid;

    private String sericeAreaName;

    private Long staffid;

    private String staffUserName;

    private Long reasonid;

    private String caseReasonName;

    private Boolean isDeleted = false;
    
    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }



	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}
}
