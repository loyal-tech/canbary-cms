package com.adopt.apigw.modules.fieldMapping;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class ScreenFieldMappingMapper implements IBaseMapper<ScreenFieldMappingDto,ScreenFieldMapping> {

    public abstract ScreenFieldMappingDto domainToDTO(ScreenFieldMapping data, @Context CycleAvoidingMappingContext context);

    public abstract ScreenFieldMapping dtoToDomain(ScreenFieldMappingDto dtoData, @Context CycleAvoidingMappingContext context);
}
