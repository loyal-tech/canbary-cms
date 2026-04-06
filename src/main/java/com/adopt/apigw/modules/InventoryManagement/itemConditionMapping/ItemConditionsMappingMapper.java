package com.adopt.apigw.modules.InventoryManagement.itemConditionMapping;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract  class ItemConditionsMappingMapper implements IBaseMapper<ItemConditionsMappingDto, ItemConditionsMapping> {
    @Override
    public abstract ItemConditionsMapping dtoToDomain(ItemConditionsMappingDto dto, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract ItemConditionsMappingDto domainToDTO(ItemConditionsMapping domain, @Context CycleAvoidingMappingContext context);

}
