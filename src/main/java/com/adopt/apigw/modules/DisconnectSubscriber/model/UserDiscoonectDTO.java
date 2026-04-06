package com.adopt.apigw.modules.DisconnectSubscriber.model;

import com.adopt.apigw.core.dto.IBaseDto;

import lombok.Data;

@Data
public class UserDiscoonectDTO implements IBaseDto {
    private Long id;
    private String remark;
    private String reqtype;
    private String username;
    private Boolean isDeleted = false;
    
    private Integer mvnoId;
    @Override
    public Long getIdentityKey() {
        return id;
    }


	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}
}
