package com.adopt.apigw.modules.FieldServiceParamMapping;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.core.dto.IBaseDto2;
import lombok.Data;

@Data
public class FieldServiceParamMappingDto implements IBaseDto2, IBaseDto {

    private Long id;
    private Long fieldid;
    private Long serviceparamid;
    private Boolean is_mandatory;
    private String module;
    private Boolean is_deleted;

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

    @Override
    public Long getBuId() {
        return null;
    }
}
