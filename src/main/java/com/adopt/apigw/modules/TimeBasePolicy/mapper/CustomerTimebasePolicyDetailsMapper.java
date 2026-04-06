package com.adopt.apigw.modules.TimeBasePolicy.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.TimeBasePolicy.domain.TimeBasePolicyDetails;
import com.adopt.apigw.modules.TimeBasePolicy.module.TimeBasePolicyDetailsDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerTimebasePolicyDetailsMapper extends IBaseMapper<TimeBasePolicyDetailsDTO, TimeBasePolicyDetails> {
}
