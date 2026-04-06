package com.adopt.apigw.modules.InventoryManagement.item;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract  class ItemMapper implements IBaseMapper<ItemDto, Item> {
    @Override
    public abstract Item dtoToDomain(ItemDto dto, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract ItemDto domainToDTO(Item domain, @Context CycleAvoidingMappingContext context);

}
