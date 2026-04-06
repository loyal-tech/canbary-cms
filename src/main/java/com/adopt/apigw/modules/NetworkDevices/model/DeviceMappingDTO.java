package com.adopt.apigw.modules.NetworkDevices.model;

import lombok.Data;

import java.util.Set;

@Data
public class DeviceMappingDTO {

    private Long deviceId;
    private Set<Long> inPortDevices;
    private Set<Long> outPortDevices;

}
