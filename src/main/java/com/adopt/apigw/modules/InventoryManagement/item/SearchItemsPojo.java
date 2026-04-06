package com.adopt.apigw.modules.InventoryManagement.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SearchItemsPojo {

    private String ownerType;
    private String ownerId;
    private String productId;
    private String inwardId;
    private String itemStatus;
    private String itemType;
    private String warrantyStatus;
    private String ownership;

    private String serialNumber;
    private String macAddress;

}
