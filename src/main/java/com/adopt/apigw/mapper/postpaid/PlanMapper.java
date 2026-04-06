package com.adopt.apigw.mapper.postpaid;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.radius.Plan;
import com.adopt.apigw.pojo.api.PlanPojo;

@Mapper
public interface PlanMapper  extends IBaseMapper<PlanPojo, Plan> {
}
