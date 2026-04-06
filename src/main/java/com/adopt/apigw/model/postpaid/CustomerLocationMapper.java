package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.LocationMaster.domain.CustomerLocationMapping;
import com.adopt.apigw.pojo.api.CustomerLocationMappingDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class})
public abstract class CustomerLocationMapper implements IBaseMapper<CustomerLocationMappingDto, CustomerLocationMapping> {
    @Override
    public abstract CustomerLocationMappingDto domainToDTO(CustomerLocationMapping data, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CustomerLocationMapping dtoToDomain(CustomerLocationMappingDto dtoData, @Context CycleAvoidingMappingContext context);



}
