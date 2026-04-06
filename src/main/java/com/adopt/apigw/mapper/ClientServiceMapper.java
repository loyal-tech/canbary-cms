package com.adopt.apigw.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.pojo.ClientServicePojo;
import org.mapstruct.Mapping;

@Mapper
public abstract class ClientServiceMapper implements IBaseMapper<ClientServicePojo, ClientService> {
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "name")
    public abstract ClientServicePojo domainToDTO(ClientService data, @Context CycleAvoidingMappingContext context);

    public abstract ClientService dtoToDomain(ClientServicePojo dtoData, @Context CycleAvoidingMappingContext context);
}
