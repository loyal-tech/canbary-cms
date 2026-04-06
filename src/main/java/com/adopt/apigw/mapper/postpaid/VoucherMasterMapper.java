package com.adopt.apigw.mapper.postpaid;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.radius.VoucherMaster;
import com.adopt.apigw.pojo.api.VoucherMasterPojo;

@Mapper
public interface VoucherMasterMapper extends IBaseMapper<VoucherMasterPojo, VoucherMaster> {
}
