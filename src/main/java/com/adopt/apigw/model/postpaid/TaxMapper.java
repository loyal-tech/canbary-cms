package com.adopt.apigw.model.postpaid;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.pojo.api.TaxPojo;

@Mapper
public interface TaxMapper  extends IBaseMapper<TaxPojo, Tax> {
}
