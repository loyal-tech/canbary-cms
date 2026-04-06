package com.adopt.apigw.mapper.postpaid;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.BillRun;
import com.adopt.apigw.pojo.api.BillRunPojo;

@Mapper
public interface BillRunMapper extends IBaseMapper<BillRunPojo, BillRun> {
}
