package com.adopt.apigw.mapper.postpaid;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.CustChargeDetails;
import com.adopt.apigw.pojo.DBMappingMasterPojo;
import com.adopt.apigw.pojo.api.CustChargeDetailsPojo;

@Mapper
public interface CustChargeMapper extends IBaseMapper<CustChargeDetailsPojo, CustChargeDetails> {
}
