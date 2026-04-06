package com.adopt.apigw.modules.CommonList.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.CommonList.domain.CommonList;
import com.adopt.apigw.modules.CommonList.model.CommonListDTO;
import org.mapstruct.Mapping;

@Mapper
public abstract class CommonListMapper implements IBaseMapper<CommonListDTO, CommonList> {
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "text")
    public abstract CommonListDTO domainToDTO(CommonList data, @Context CycleAvoidingMappingContext context);

    public abstract CommonList dtoToDomain(CommonListDTO dtoData, @Context CycleAvoidingMappingContext context);

}
