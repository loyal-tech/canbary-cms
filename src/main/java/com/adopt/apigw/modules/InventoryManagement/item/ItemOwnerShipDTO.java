package com.adopt.apigw.modules.InventoryManagement.item;

import lombok.Data;

@Data
public class ItemOwnerShipDTO {
    private Long id;
    private String ownershipType;
    private String remarks;
}
