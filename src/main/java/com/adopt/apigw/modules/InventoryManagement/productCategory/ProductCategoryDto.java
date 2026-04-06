package com.adopt.apigw.modules.InventoryManagement.productCategory;

import com.adopt.apigw.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ProductCategoryDto implements IBaseDto {

    private Long id;
    String name;
    String unit;
    String type;
    String status;

    private Integer mvnoId;
    private Boolean isDeleted = false;
    private boolean hasMac;
    private boolean hasSerial;
    private String productId;
    private boolean hasTrackable;
    private boolean hasPort;
    private boolean hasCas=false;

    private String dtvCategory;

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
