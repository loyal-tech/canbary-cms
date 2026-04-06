package com.adopt.apigw.rabbitMq.message;

import lombok.Data;

@Data
public class InventorySerialNumberMessage {
    private Long planId;
    private String serialNumber;
    private Long planGroupId;
    private String operation;
    private Integer custId;
    private Long custInventoryId;
    private String productId;
    private String connectionNo;
    private Long itemId;
    private String itemName;
    private String macAddress;
    private String status;
    private Integer mvnoId;
    private Long qty;
    private String loggedInUserName;
    private Long vendorId;

}
