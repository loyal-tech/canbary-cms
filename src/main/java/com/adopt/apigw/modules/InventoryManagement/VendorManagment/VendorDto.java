package com.adopt.apigw.modules.InventoryManagement.VendorManagment;

import com.adopt.apigw.core.dto.IBaseDto;
import lombok.Data;

@Data
public class VendorDto implements IBaseDto {

    private Long id;
    private String name;
    private String status;

    private Integer mvnoId;

    private boolean isDeleted;


    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

}
