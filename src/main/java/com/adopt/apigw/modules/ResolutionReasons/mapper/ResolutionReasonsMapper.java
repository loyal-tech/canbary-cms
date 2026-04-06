package com.adopt.apigw.modules.ResolutionReasons.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.ResolutionReasons.domain.ResolutionReasons;
import com.adopt.apigw.modules.ResolutionReasons.model.ResolutionReasonsDTO;

@Mapper
public interface ResolutionReasonsMapper extends IBaseMapper<ResolutionReasonsDTO, ResolutionReasons> {
}
