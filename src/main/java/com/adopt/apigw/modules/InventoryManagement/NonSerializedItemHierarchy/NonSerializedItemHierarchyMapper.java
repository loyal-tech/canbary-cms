package com.adopt.apigw.modules.InventoryManagement.NonSerializedItemHierarchy;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class NonSerializedItemHierarchyMapper implements IBaseMapper<NonSerializedItemHierarchyDto, NonSerializedItemHierarchy> {
    @Override
    public abstract NonSerializedItemHierarchy dtoToDomain(NonSerializedItemHierarchyDto dto, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract NonSerializedItemHierarchyDto domainToDTO(NonSerializedItemHierarchy domain, @Context CycleAvoidingMappingContext context);
}
