package com.adopt.apigw.modules.InventoryManagement.NonSerializedItemHierarchy;

import com.adopt.apigw.core.dto.IBaseDto;
import lombok.Data;
@Data
public class NonSerializedItemHierarchyDto implements IBaseDto {

    private Long id;
    private Long parentItemId;
    private Long childItemId;
    private Long qty;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    @Override
    public Long getIdentityKey() {
        return null;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }
}
