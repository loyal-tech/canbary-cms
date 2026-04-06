package com.adopt.apigw.modules.DisconnectSubscriber.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.modules.DisconnectSubscriber.domain.UserDisconnect;

import lombok.Data;

@Data
public class UserDisconnectDtlDTO implements IBaseDto {
    private Long id;
    private String sessionid;
    private String NASIPAddress;
    private String FramedIPAddress;
    private UserDisconnect userDisconnect;
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
