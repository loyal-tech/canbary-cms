package com.adopt.apigw.modules.purchaseDetails.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.purchaseDetails.domain.PaymentGatewayResponse;
import com.adopt.apigw.modules.purchaseDetails.model.PaymentGatewayResponseDTO;

@Mapper
public abstract class PaymentGatewayResponseMapper implements IBaseMapper<PaymentGatewayResponseDTO, PaymentGatewayResponse> {
}
