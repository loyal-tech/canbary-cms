package com.adopt.apigw.modules.servicePlan.mapper;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.adopt.apigw.modules.servicePlan.model.ServicesDTO;
@JaversSpringDataAuditable
@Mapper
public interface ServicesMapper  extends IBaseMapper<ServicesDTO, Services> {
}
