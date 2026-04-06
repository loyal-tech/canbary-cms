package com.adopt.apigw.mapper.postpaid;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.EndMacMappping;
import com.adopt.apigw.model.postpaid.EndMacMapppingPojo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class EndMacMapper implements IBaseMapper<EndMacMapppingPojo, EndMacMappping> {

    @Override
    public abstract EndMacMappping dtoToDomain(EndMacMapppingPojo pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract EndMacMapppingPojo domainToDTO(EndMacMappping domain, @Context CycleAvoidingMappingContext context);

}
