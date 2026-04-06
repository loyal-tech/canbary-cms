package com.adopt.apigw.modules.InventoryManagement.RequestInventory;

import com.adopt.apigw.core.dto.IBaseDto;
import lombok.Data;

@Data
public class RequestInventoryHistoryDto implements IBaseDto {

    private Long id;

//    private Long requestInventoryId;
//
//    private String requestInventoryName;
//
//    private Long requestNameId;
//
//    private Long requestToWarehouseId;
//
//    private String remarks;

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
