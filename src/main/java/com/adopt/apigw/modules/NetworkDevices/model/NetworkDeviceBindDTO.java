package com.adopt.apigw.modules.NetworkDevices.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;

@Data
public class NetworkDeviceBindDTO extends Auditable implements IBaseDto {

    @JsonIgnore
    private Long id;
    private String deviceName;
    private String parentDeviceName;
    private Long currentDeviceId;
    private String currentDevicePort;
    private String portType;

    private Long otherDeviceId;
    private String otherDevicePort;
    @JsonIgnore
    private int mappingId;
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
