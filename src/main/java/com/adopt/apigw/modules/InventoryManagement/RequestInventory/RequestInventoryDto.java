package com.adopt.apigw.modules.InventoryManagement.RequestInventory;

import com.adopt.apigw.core.dto.IBaseDto;
import lombok.Data;

import java.util.List;

@Data
public class RequestInventoryDto implements IBaseDto {

    private Long id;

    private String requestInventoryName;

    private String OnBehalfOf;

    private Long requestNameId;

    private Long requestToWarehouseId;

   private List<RequestInventoryProductMappingDto> requestInvenotryProductMappings;

//    private String status;
//    private String remarks;
//    private String reason;
    private String  requesterName;
    private String requestToName;
//
//    private String inventoryRequestStatus;

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
