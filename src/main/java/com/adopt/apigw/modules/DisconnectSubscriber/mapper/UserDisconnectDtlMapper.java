package com.adopt.apigw.modules.DisconnectSubscriber.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.DisconnectSubscriber.domain.UserDisconnectDtl;
import com.adopt.apigw.modules.DisconnectSubscriber.model.UserDisconnectDtlDTO;

@Mapper
public interface UserDisconnectDtlMapper extends IBaseMapper<UserDisconnectDtlDTO, UserDisconnectDtl>
{}
