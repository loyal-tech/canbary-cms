package com.adopt.apigw.modules.NetworkDevices.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDeviceBind;
import com.adopt.apigw.modules.NetworkDevices.model.NetworkDeviceBindDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class NetworkDeviceBindMapper implements IBaseMapper<NetworkDeviceBindDTO, NetworkDeviceBind> {
}
