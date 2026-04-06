package com.adopt.apigw.modules.DisconnectSubscriber.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.DisconnectSubscriber.domain.UserDisconnect;
import com.adopt.apigw.modules.DisconnectSubscriber.model.UserDiscoonectDTO;

@Mapper
public interface UserDisconnectMapper extends IBaseMapper<UserDiscoonectDTO, UserDisconnect> {
}
