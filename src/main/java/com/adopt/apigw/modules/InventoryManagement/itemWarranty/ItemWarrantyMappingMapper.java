package com.adopt.apigw.modules.InventoryManagement.itemWarranty;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract  class ItemWarrantyMappingMapper implements IBaseMapper<ItemWarrantyMappingDto, ItemWarrantyMapping> {
    @Override
    public abstract ItemWarrantyMapping dtoToDomain(ItemWarrantyMappingDto dto, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract ItemWarrantyMappingDto domainToDTO(ItemWarrantyMapping domain, @Context CycleAvoidingMappingContext context);

}
