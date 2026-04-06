package com.adopt.apigw.modules.BuildingMgmt.DTO;

import com.adopt.apigw.core.dto.IBaseDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuildingMappingDTO implements IBaseDto {
    private Long id;
    private String buildingNumber;
    private Long buildingMgmtId;
    private Boolean isDeleted;

    @Override
    public Long getIdentityKey() {
        return this.id;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }


}
