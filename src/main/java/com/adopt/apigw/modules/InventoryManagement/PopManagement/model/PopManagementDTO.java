package com.adopt.apigw.modules.InventoryManagement.PopManagement.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PopManagementDTO extends Auditable implements IBaseDto {

    private Long id;
    private String name;
    private String latitude;
    private String longitude;
    List<Long> serviceAreaIdsList;
    List<ServiceAreaDTO> serviceAreaNameList = new ArrayList<>();
    private String status;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private String popCode;

    private Integer displayId;
    private String displayName;

    @Override
    public Long getIdentityKey() {
        return id;
    }
    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }
}
