package com.adopt.apigw.modules.InventoryManagement.productOwner;

import com.adopt.apigw.core.dto.IBaseDto;
import lombok.Data;

@Data
public class ProductOwnerDto implements IBaseDto {

    private Long id;
    private Long productId;
    private Long ownerId;
    private String ownerType;
    private Long quantity;
    private Long usedQty;
    private Long unusedQty;
    private Long inTransitQty;
    private Long boundQty;
    private Integer mvnoId;
    private String productName;

    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }
}
