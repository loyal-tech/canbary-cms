package com.adopt.apigw.modules.StaffUserService.model;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.core.dto.IBaseDto;
import lombok.Data;

@Data
public class StaffUserServiceDTO implements IBaseDto{
    private Long id;
    private String prefix;
    private Long fromreceiptnumber;
    private Long toreceiptnumber;
    private Boolean isActive = true;
    private Boolean isDeleted = false;
    private int stfmappingId;


    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }


}
