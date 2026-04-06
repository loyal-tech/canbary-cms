package com.adopt.apigw.modules.ServiceArea.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.modules.Area.model.AreaDTO;
import com.adopt.apigw.modules.Pincode.model.PincodeDTO;
import com.adopt.apigw.modules.PriceGroup.model.PriceBookDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
public class ServiceAreaPincodeRelDTO implements IBaseDto {

    private Long id;
    private Long serviceAreaId;
    private Long pincodeId;
    private Integer mvnoId;

    @Override
    public Long getIdentityKey() {return id;}

    @Override
    public Integer getMvnoId() { return mvnoId; }

    @Override
    public void setMvnoId(Integer mvnoId) { this.mvnoId = mvnoId;  }
}
