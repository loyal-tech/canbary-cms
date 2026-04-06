package com.adopt.apigw.mapper.postpaid;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.radius.CustReplyItem;
import com.adopt.apigw.model.radius.VoucherMaster;
import com.adopt.apigw.pojo.api.CustReplyItemPojo;
import com.adopt.apigw.pojo.api.VoucherMasterPojo;

@Mapper
public interface CustReplyItemMapper extends IBaseMapper<CustReplyItemPojo, CustReplyItem> {
}
