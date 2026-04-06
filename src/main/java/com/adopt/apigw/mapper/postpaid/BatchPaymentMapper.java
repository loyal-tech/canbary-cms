package com.adopt.apigw.mapper.postpaid;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.common.BatchPayment;
import com.adopt.apigw.pojo.api.BatchPaymentPojo;
import org.mapstruct.Mapper;

@Mapper
public interface BatchPaymentMapper  extends IBaseMapper<BatchPaymentPojo  , BatchPayment> {
}
