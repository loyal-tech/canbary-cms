package com.adopt.apigw.modules.InventoryManagement.ItemGroup;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class ItemAssemblyMapper implements IBaseMapper<ItemAssemblyDto, ItemAssembly> {

    @Autowired
    InOutWardMacRepo inOutWardMacRepo;
    @Override
    @Mapping(source = "itemAssembly.itemListLongId", target = "itemListLongId")
    public abstract ItemAssemblyDto domainToDTO(ItemAssembly itemAssembly, CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "dtoData.itemListLongId", target = "itemListLongId")
    public abstract ItemAssembly dtoToDomain(ItemAssemblyDto dtoData, CycleAvoidingMappingContext context);


    Integer fromEntityToId(InOutWardMACMapping entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    InOutWardMACMapping fromIdToEntity(Integer id) {
        if (id == null) {
            return null;
        }
        InOutWardMACMapping entity;
        try {
            entity = inOutWardMacRepo.findById(id.longValue()).get();
            entity.setId(id.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
}
