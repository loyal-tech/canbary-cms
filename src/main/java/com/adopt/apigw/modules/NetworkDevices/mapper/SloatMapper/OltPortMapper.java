package com.adopt.apigw.modules.NetworkDevices.mapper.SloatMapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.NetworkDevices.domain.OLTPortDetails;
import com.adopt.apigw.modules.NetworkDevices.model.SloatModel.OLTPortDTO;

@Mapper
public interface OltPortMapper extends IBaseMapper<OLTPortDTO, OLTPortDetails> {
}
