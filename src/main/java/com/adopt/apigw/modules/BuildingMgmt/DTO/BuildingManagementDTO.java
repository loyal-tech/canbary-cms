package com.adopt.apigw.modules.BuildingMgmt.DTO;

import com.adopt.apigw.core.dto.IBaseDto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BuildingManagementDTO  implements IBaseDto {
    private Long buildingMgmtId;
    private String buildingName;
    private Integer pincodeId;
    private Integer areaId;
    private Integer subAreaId;
    private Integer mvnoId;
    private Integer buid;
    private Boolean isDeleted;
    private List<BuildingMappingDTO> buildingMappings;
    private String buildingType;


    @Override
    public Long getIdentityKey() {
        return this.buildingMgmtId;
    }

    @Override
    public Integer getMvnoId() {
        return this.mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId=mvnoId;
    }
}
