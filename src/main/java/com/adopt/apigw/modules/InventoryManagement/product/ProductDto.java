package com.adopt.apigw.modules.InventoryManagement.product;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ProductDto implements IBaseDto {

    private Long id;
    String name;
//    String unit;
    String description;
    String status;

    Integer mvnoId;
    private Boolean isDeleted = false;
//    private boolean hasMac;
//    private boolean hasSerial;
//    private Integer oldProductCharge;
    private Integer expiryTime;
    private String expiryTimeUnit;
    private Integer refurburshiedProductCharge;

    ProductCategory productCategory;

    private Integer availableInPorts;
    private Integer totalInPorts;
    private Integer availableOutPorts;
    private Integer totalOutPorts;
    private String productId;
    private String navLedgerId;

    private Integer newProductCharge;
    private Double refurburshiedProductRefAmountInWarranty;
    private Double refurburshiedProductRefAmountPostWarranty;
    private Double newProductRefAmountInWarranty;
    private Double newProductRefAmountPostWarranty;
    private Double newProductAmount;
    private Double refurburshiedProductAmount;

    private Long caseId;

    private Long vendorId;

    private Long newPrice;

    private Long refurburshiedPrice;

    private Long refurburshiedProductTax;

    private Long newProductTax;

    private String refurburshiedProductTaxName;

    private String newProductTaxName;

    private Long actualpricenewProduct;

    private Long actualpricerefurbishedProduct;



    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

//    @JsonIgnore
    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }
}
