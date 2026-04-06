package com.adopt.apigw.modules.InventoryManagement.itemWarranty;

import com.adopt.apigw.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ItemWarrantyMappingDto implements IBaseDto {
    private Long id;
    private Long itemId;
    private String warranty;
    private Boolean isDeleted = false;
    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }
}
