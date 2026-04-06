package com.adopt.apigw.mapper.postpaid;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.DiscountPlanMapping;
import com.adopt.apigw.pojo.api.DiscountPlanMappingPojo;

@Mapper
public interface DiscountMappingMapper extends IBaseMapper<DiscountPlanMappingPojo, DiscountPlanMapping> {
}
