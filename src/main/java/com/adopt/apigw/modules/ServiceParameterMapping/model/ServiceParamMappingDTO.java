package com.adopt.apigw.modules.ServiceParameterMapping.model;

import com.adopt.apigw.core.dto.IBaseDto2;
import com.adopt.apigw.modules.ServiceParameters.domain.ServiceParameter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ServiceParamMappingDTO implements IBaseDto2 {

    private Long id;
    private Long serviceid;
    private Long serviceParamId;
//    private ServiceParameter serviceParameter;
    private String value;
    private Boolean isMandatory;
    private  String serviceParamName;

    @Override
    @JsonIgnore
    public Long getIdentityKey() {
        return id;
    }

    @Override
    @JsonIgnore
    public Integer getMvnoId() {
        return null;
    }

    @Override
    @JsonIgnore
    public void setMvnoId(Integer mvnoId) {

    }

    @Override
    @JsonIgnore
    public Long getBuId() {
        return null;
    }
}
