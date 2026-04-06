package com.adopt.apigw.modules.InventoryManagement.itemConditionMapping;

import com.adopt.apigw.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ItemConditionsMappingDto implements IBaseDto {
    private Long id;
    private Long itemId;
    private String condition;
    private String remarks;
    private String filename;
    private String uniquename;
    private String otherreason;
    private Boolean isDeleted = false;
    private Integer mvnoId;

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
