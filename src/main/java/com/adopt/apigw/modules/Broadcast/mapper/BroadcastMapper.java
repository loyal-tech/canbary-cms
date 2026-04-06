package com.adopt.apigw.modules.Broadcast.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Broadcast.domain.Broadcast;
import com.adopt.apigw.modules.Broadcast.model.BroadcastDTO;

@Mapper
public interface BroadcastMapper extends IBaseMapper<BroadcastDTO, Broadcast> {
}
