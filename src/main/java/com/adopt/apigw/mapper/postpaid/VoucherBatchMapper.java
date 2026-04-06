package com.adopt.apigw.mapper.postpaid;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.radius.VoucherBatch;
import com.adopt.apigw.pojo.api.VoucherBatchPojo;

@Mapper
public interface VoucherBatchMapper extends IBaseMapper<VoucherBatchPojo, VoucherBatch> {
}
