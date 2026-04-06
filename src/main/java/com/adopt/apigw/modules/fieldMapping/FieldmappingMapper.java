package com.adopt.apigw.modules.fieldMapping;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class FieldmappingMapper implements IBaseMapper<FielmappingDto,FieldsBuidMapping> {
    public abstract FielmappingDto domainToDTO(FieldsBuidMapping data, @Context CycleAvoidingMappingContext context);
    public abstract FieldsBuidMapping dtoToDomain(FielmappingDto dtoData, @Context CycleAvoidingMappingContext context);
}
