package com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import lombok.Data;

@Data
public class ExternalItemManagementDTO extends Auditable implements IBaseDto {
    private Long id;
    private Product productId;
    private Long qty;
    private Long usedQty;
    private Long unusedQty;
    private String ownershipType;
    private String status;
    private Integer mvnoId;
    private Boolean isDeleted = false;
    private ServiceArea serviceAreaId;
    private Long inTransitQty;
    private Long rejectedQty;
    private String approvalStatus;
    private String externalItemGroupNumber;
    private Long totalMacSerial;
    private String approvalRemark;
    private transient String ownerName;

    private Long ownerId;
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
