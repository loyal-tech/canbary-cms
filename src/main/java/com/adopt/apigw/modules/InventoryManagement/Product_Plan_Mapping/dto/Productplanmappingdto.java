package com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.dto;

import com.adopt.apigw.core.dto.IBaseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Productplanmappingdto implements IBaseDto {

    private Long id;
    private Long planId;
    private Long productCategoryId;
    private String product_type;
    private LocalDateTime createdate;
    private LocalDateTime updatedate;
    private String createdByName;
    private String lastModifiedByName;
    private Integer createdById;
    private Integer lastModifiedById;
    private Long productId;
    private String revisedCharge;
    private String ownershipType;
    private  String name;
    private String productCategoryName;
    private String productName;
    private String planName;
    private Integer productQuantity;

    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }

    public Productplanmappingdto(final Productplanmappingdto productplanmappingdto) {
        this.id = productplanmappingdto.getId();
        this.planId = productplanmappingdto.getPlanId();
        this.productCategoryId = productplanmappingdto.getProductCategoryId();
        this.product_type = productplanmappingdto.getProduct_type();
        this.createdByName = productplanmappingdto.getCreatedByName();
        this.lastModifiedByName = productplanmappingdto.getLastModifiedByName();
        this.createdById = productplanmappingdto.getCreatedById();
        this.lastModifiedById = productplanmappingdto.getLastModifiedById();
        this.productId = productplanmappingdto.getProductId();
        this.revisedCharge = productplanmappingdto.getRevisedCharge();
        this.ownershipType = productplanmappingdto.getOwnershipType();
        this.name = productplanmappingdto.getName();
        this.productCategoryName = productplanmappingdto.getProductCategoryName();
        this.productName = productplanmappingdto.getProductName();
        this.planName = productplanmappingdto.getPlanName();
        this.productQuantity = productplanmappingdto.getProductQuantity();
    }
}
