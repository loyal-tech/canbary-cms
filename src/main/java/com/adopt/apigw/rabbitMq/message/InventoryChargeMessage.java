package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryChargeMessage {
    private String name;
    private String desc;
    private String chargetype;
    private double price;
    private double actualprice;
    private Integer taxId;
    private Boolean isDelete;
    private String chargecategory;
    private Integer mvnoId;
    private Long buId;
    private String service;
    private String status;
    private Double taxamount;
    private Boolean isinventorycharge;
    private Integer createdById;
    private Integer lastModifiedById;
    private Long productId;
    private String inventoryChargeType;
}
