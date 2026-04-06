package com.adopt.apigw.mapper.postpaid;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.DiscountMapping;
import com.adopt.apigw.pojo.api.DiscountMappingPojo;

@Mapper
public interface DiscountPlanMappingMapper extends IBaseMapper<DiscountMappingPojo, DiscountMapping> {
}
