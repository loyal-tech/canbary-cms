package com.adopt.apigw.modules.tickets.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.tickets.domain.TatQueryFieldMapping;
import com.adopt.apigw.modules.tickets.model.TatQueryFieldMappingDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TatQueryFieldMapper extends IBaseMapper<TatQueryFieldMappingDTO, TatQueryFieldMapping> {
}
