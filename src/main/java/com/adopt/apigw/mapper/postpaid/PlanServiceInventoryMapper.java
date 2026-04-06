package com.adopt.apigw.mapper.postpaid;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.PlanServiceInventoryMapping;
import com.adopt.apigw.pojo.api.PlanServiceInventoryMappingPojo;
import org.mapstruct.Mapper;

@Mapper
public interface PlanServiceInventoryMapper  extends IBaseMapper<PlanServiceInventoryMappingPojo, PlanServiceInventoryMapping> {
}
