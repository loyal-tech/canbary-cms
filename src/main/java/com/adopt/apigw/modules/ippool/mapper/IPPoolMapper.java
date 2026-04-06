package com.adopt.apigw.modules.ippool.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.ippool.domain.IPPool;
import com.adopt.apigw.modules.ippool.model.IPPoolDTO;
import org.mapstruct.Mapping;

@Mapper
public interface IPPoolMapper extends IBaseMapper<IPPoolDTO, IPPool> {
    @Override
    @Mapping(target = "displayId", source = "poolId")
    @Mapping(target = "displayPoolName", source = "poolName")
    public abstract IPPoolDTO domainToDTO(IPPool domain, @Context CycleAvoidingMappingContext context);
}
