package com.adopt.apigw.modules.InventoryManagement.NonSerializedItem;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract  class NonSerializedItemMapper implements IBaseMapper<NonSerializedItemDto, NonSerializedItem> {
    @Override
    public abstract NonSerializedItem dtoToDomain(NonSerializedItemDto dto, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract NonSerializedItemDto domainToDTO(NonSerializedItem domain, @Context CycleAvoidingMappingContext context);

}
