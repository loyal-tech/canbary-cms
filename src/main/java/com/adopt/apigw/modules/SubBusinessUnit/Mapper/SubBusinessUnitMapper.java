package com.adopt.apigw.modules.SubBusinessUnit.Mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.SubBusinessUnit.Domain.SubBusinessUnit;
import com.adopt.apigw.modules.SubBusinessUnit.Model.SubBusinessUnitDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class SubBusinessUnitMapper implements IBaseMapper<SubBusinessUnitDTO, SubBusinessUnit> {

    String MODULE = " [SubBusinessUnitMapper] ";

    @Override
    @Mapping(target = "displayId", source = "data.id")
    @Mapping(target = "displayName", source = "data.subbuname")
//    @Mapping(source = "data.investmentCodeList", target = "investmentcode_id")
    public abstract SubBusinessUnitDTO domainToDTO(SubBusinessUnit data,CycleAvoidingMappingContext context);
}
