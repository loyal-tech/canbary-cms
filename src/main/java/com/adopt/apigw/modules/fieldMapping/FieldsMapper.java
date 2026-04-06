package com.adopt.apigw.modules.fieldMapping;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class FieldsMapper implements IBaseMapper<FieldsDTO,Fields>{

    public abstract FieldsDTO domainToDTO(Fields data, @Context CycleAvoidingMappingContext context);

    public abstract Fields dtoToDomain(FieldsDTO dtoData, @Context CycleAvoidingMappingContext context);
}
