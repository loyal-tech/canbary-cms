package com.adopt.apigw.modules.tickets.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

@Data
public class CaseUpdateDetailsDTO implements IBaseDto {

    private Long id;
    private String operation;
    private String entitytype;
    private String oldvalue;
    private String newvalue;
    private String attachment;
    private String filename;
    private Long resolutionId;
    private String remarktype;
    @JsonBackReference
    @ToString.Exclude
    private CaseUpdateDTO caseUpdate;
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
