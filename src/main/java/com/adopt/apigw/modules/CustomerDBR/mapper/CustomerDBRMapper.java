package com.adopt.apigw.modules.CustomerDBR.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.CustomerDBR.domain.CustomerDBR;
import com.adopt.apigw.modules.CustomerDBR.model.CustomerDBRDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerDBRMapper extends IBaseMapper<CustomerDBRDTO, CustomerDBR> {

    public abstract CustomerDBR dtoToDomain(CustomerDBRDTO dtoData, @Context CycleAvoidingMappingContext context);

}
