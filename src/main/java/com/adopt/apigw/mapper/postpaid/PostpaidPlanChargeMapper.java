package com.adopt.apigw.mapper.postpaid;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.PostpaidPlanCharge;
import com.adopt.apigw.pojo.api.PostpaidPlanChargePojo;

@Mapper
public abstract class PostpaidPlanChargeMapper implements IBaseMapper<PostpaidPlanChargePojo, PostpaidPlanCharge> {

    @Override
//    @Mapping(target = "postpaidPlan", source = "planId")
//    @Mapping(target = "custPlanMappping", source = "planId")
    public abstract PostpaidPlanCharge dtoToDomain(PostpaidPlanChargePojo pojo, @Context CycleAvoidingMappingContext context);

    @Override
//    @Mapping(target = "planId", source = "postpaidPlan")
    public abstract PostpaidPlanChargePojo domainToDTO(PostpaidPlanCharge domain, @Context CycleAvoidingMappingContext context);
/*
    Integer fromPlanToId(PostpaidPlan entity) {
        return entity == null ? null : entity.getId();
    }

    PostpaidPlan fromIdToPlan(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        PostpaidPlan entity;
        try {
            entity = postpaidPlanService.get(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }*/
}
