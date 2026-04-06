package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.Area.model.AreaDTO;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaMessage {

    private Long id;

    private String name;

    private String status;

    private Boolean isDeleted;

    private Integer countryId;

    private Integer cityId;

    private Integer stateId;

    private Integer pincodeId;

    private Integer mvnoId;
    private String createdByName;

    private String lastModifiedByName;

    private Integer createdById;
    private Integer lastModifiedById;

    public AreaMessage(AreaDTO areaDTO) {
        this.id = areaDTO.getId();
        this.name = areaDTO.getName();
        this.status=areaDTO.getStatus();
        this.isDeleted=areaDTO.getIsDeleted();
        this.countryId=areaDTO.getCountryId();
        this.cityId=areaDTO.getCityId();
        this.stateId=areaDTO.getStateId();
        this.pincodeId=areaDTO.getPincodeId();
        this.mvnoId=areaDTO.getMvnoId();
        this.createdById=areaDTO.getCreatedById();
        this.lastModifiedById=areaDTO.getLastModifiedById();
        this.createdByName= areaDTO.getCreatedByName();
        this.lastModifiedByName= areaDTO.getLastModifiedByName();
    }
}
