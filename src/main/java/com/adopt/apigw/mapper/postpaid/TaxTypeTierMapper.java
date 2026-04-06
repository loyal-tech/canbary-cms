package com.adopt.apigw.mapper.postpaid;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.TaxTypeTier;
import com.adopt.apigw.pojo.api.TaxTypeTierPojo;

@Mapper
public interface TaxTypeTierMapper extends IBaseMapper<TaxTypeTierPojo, TaxTypeTier> {
}
