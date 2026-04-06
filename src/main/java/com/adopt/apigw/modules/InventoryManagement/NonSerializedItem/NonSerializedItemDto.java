package com.adopt.apigw.modules.InventoryManagement.NonSerializedItem;

import com.adopt.apigw.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class NonSerializedItemDto implements IBaseDto {
    private Long id;
    private String name;
    private Integer mvnoId;
    private String nonSerializedItemcondition;
    private Boolean isDeleted = false;

    private Long currentInwardId;
    private String currentInwardType;
    private Long productId;
    private Long ownerId;
    private String ownerType;
    private String warranty;
    private Integer warrantyPeriod;
    private String currentInwardNumber;
    private String ownerName;
    private String productName;

    private String itemStatus;
    private String ownershipType;
    private String remarks;
    private Long externalItemId;

    private String remainingDays;
    private String filename;
    private Long itemConditionId;

    private Long qty;

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
