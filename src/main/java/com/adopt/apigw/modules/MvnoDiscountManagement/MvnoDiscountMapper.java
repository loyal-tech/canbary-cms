package com.adopt.apigw.modules.MvnoDiscountManagement;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = MvnoMapperHelper.class)
public abstract class MvnoDiscountMapper implements IBaseMapper<MvnoDiscountMappingDTO, MvnoDiscountMapping> {

    @Override
    @Mappings({
            @Mapping(source = "dtoData.mvnoId", target = "mvno"),
    })
    public abstract MvnoDiscountMapping dtoToDomain(MvnoDiscountMappingDTO dtoData, CycleAvoidingMappingContext context);

    @Override
    @Mappings({
            @Mapping(source = "data.mvno", target = "mvnoId"),
    })
    public abstract MvnoDiscountMappingDTO domainToDTO(MvnoDiscountMapping data, CycleAvoidingMappingContext context);

}
