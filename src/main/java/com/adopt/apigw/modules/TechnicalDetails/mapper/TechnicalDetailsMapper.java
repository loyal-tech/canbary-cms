package com.adopt.apigw.modules.TechnicalDetails.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.TechnicalDetails.domain.TechnicalDetails;
import com.adopt.apigw.modules.TechnicalDetails.model.TechnicalDetailsDto;
import org.mapstruct.Mapper;

@Mapper
public interface TechnicalDetailsMapper extends IBaseMapper<TechnicalDetailsDto, TechnicalDetails> {
}
