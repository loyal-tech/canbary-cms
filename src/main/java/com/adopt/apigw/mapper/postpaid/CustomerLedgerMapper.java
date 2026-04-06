package com.adopt.apigw.mapper.postpaid;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.CustomerLedger;
import com.adopt.apigw.model.postpaid.CustomerLedgerPojo;

@Mapper
public interface CustomerLedgerMapper extends IBaseMapper<CustomerLedgerPojo, CustomerLedger> {
}
