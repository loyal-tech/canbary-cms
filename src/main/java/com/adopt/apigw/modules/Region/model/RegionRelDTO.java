package com.adopt.apigw.modules.Region.model;

import com.adopt.apigw.core.dto.IBaseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegionRelDTO implements IBaseDto {

    private Long id;
    private Long regionid;
    private Long branchid;
    private Integer mvnoid;


    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoid;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoid = mvnoId;
    }
}
