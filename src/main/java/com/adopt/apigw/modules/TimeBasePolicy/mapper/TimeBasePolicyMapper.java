package com.adopt.apigw.modules.TimeBasePolicy.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.TimeBasePolicy.domain.TimeBasePolicy;
import com.adopt.apigw.modules.TimeBasePolicy.module.TimeBasePolicyDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TimeBasePolicyMapper extends IBaseMapper<TimeBasePolicyDTO, TimeBasePolicy> {
}
