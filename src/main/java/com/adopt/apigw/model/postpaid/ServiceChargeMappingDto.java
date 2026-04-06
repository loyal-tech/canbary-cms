package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.core.dto.IBaseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceChargeMappingDto implements IBaseDto {

    private Long id;
    private Long chargeid;
    private Long servicesid;

    @Override
    public Long getIdentityKey() {
        return null;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }
}
