package com.adopt.apigw.modules.InventoryManagement.InOutMACMapping;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InOutWardMACMapingDTO implements IBaseDto {


    private Long id;

    private Long inwardId;

    private Long outwardId;

    String status;

    String macAddress;

    private Boolean isDeleted = false;

    private Long custInventoryMappingId;

    String serialNumber;

    private Integer mvnoId;

    private Integer currentApproverId;
    private Integer previousApproverId;
    private Integer teamHierarchyMappingId;
    private Long inwardIdOfOutward;
    private Integer isForwarded = 0;
    private String remark;
    private Long externalItemId;
    private Long itemId;
    private Long inventoryMappingId;
    private Long bulkConsumptionId;
    private String itemRemaingDays;
    private Integer isReturned = 0;
    private Long nonSerializedItemId;
    private String condition;
    private String productName;
    private Long productId;
    private boolean hasMac;
    private boolean hasSerial;
    private String ownerShip;

    @Override
    public Long getIdentityKey() {
        return this.id;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }
}
