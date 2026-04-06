package com.adopt.apigw.modules.NetworkDevices.mapper.SloatMapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDevices;
import com.adopt.apigw.modules.NetworkDevices.model.SloatModel.NetworkDTO;

@Mapper
public interface NetworkMapper extends IBaseMapper<NetworkDTO, NetworkDevices> {
}
