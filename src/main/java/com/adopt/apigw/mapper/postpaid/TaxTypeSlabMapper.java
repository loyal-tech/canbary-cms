package com.adopt.apigw.mapper.postpaid;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.TaxTypeSlab;
import com.adopt.apigw.pojo.api.TaxTypeSlabPojo;

@Mapper
public interface TaxTypeSlabMapper extends IBaseMapper<TaxTypeSlabPojo, TaxTypeSlab> {
}
