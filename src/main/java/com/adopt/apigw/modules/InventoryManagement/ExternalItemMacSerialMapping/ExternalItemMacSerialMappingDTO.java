package com.adopt.apigw.modules.InventoryManagement.ExternalItemMacSerialMapping;

import com.adopt.apigw.core.dto.IBaseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExternalItemMacSerialMappingDTO implements IBaseDto {

    private Long id;
    private Long externalItemId;
    private String macAddress;
    private Boolean isDeleted = false;
    private Long custInventoryMappingId;
    private String serialNumber;
    private Integer mvnoId;
    private Long itemId;
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }
}
