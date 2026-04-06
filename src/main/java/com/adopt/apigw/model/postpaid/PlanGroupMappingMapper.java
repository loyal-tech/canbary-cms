package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.pojo.api.PlanGroupMappingDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class PlanGroupMappingMapper implements IBaseMapper<PlanGroupMappingDTO,PlanGroupMapping> {

    public abstract PlanGroupMappingDTO domainToDTO(PlanGroupMapping data , @Context CycleAvoidingMappingContext context);


}
