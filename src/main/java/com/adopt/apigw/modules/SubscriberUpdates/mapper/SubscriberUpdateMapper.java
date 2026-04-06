package com.adopt.apigw.modules.SubscriberUpdates.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.SubscriberUpdates.domain.SubscriberUpdate;
import com.adopt.apigw.modules.SubscriberUpdates.model.SubscriberUpdateDTO;

@Mapper
public interface SubscriberUpdateMapper extends IBaseMapper<SubscriberUpdateDTO, SubscriberUpdate> {
}
