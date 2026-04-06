package com.adopt.apigw.modules.payments.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.payments.domain.Payment;
import com.adopt.apigw.modules.payments.model.PaymentDTO;

@Mapper
public interface PaymentMapper extends IBaseMapper<PaymentDTO, Payment> {
}
