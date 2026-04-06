package com.adopt.apigw.modules.paymentGatewayMaster.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.paymentGatewayMaster.domain.PaymentGateWay;
import com.adopt.apigw.modules.paymentGatewayMaster.dto.PaymentGatewayDTO;

@Mapper
public interface PaymentGatewayMapper extends IBaseMapper<PaymentGatewayDTO, PaymentGateWay> {
}
