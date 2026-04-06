package com.adopt.apigw.modules.NetworkDevices.model;

import lombok.Data;

import java.util.List;

@Data
public class DevicePortMappingDTO {

    private Long deviceId;
    private List<NetworkDevicePortsBinding> inPortDevices;
    private List<NetworkDevicePortsBinding> outPortDevices;

}
