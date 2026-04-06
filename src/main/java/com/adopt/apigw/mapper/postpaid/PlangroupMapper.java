package com.adopt.apigw.mapper.postpaid;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.PlanGroup;
import com.adopt.apigw.pojo.api.PlanGroupDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract interface PlangroupMapper extends IBaseMapper<PlanGroupDTO, PlanGroup> {
//
//    public abstract PlanGroupDTO domainTODTO(PlanGroup planGroup, @Context CycleAvoidingMappingContext context);
//
//    public abstract PlanGroup dtoToDomain(PlanGroupDTO dtoData, @Context CycleAvoidingMappingContext context);
}
