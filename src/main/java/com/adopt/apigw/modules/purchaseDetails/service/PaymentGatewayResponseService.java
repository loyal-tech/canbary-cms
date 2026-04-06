package com.adopt.apigw.modules.purchaseDetails.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.paymentGatewayMaster.mapper.PaymentGatewayMapper;
import com.adopt.apigw.modules.purchaseDetails.domain.PaymentGatewayResponse;
import com.adopt.apigw.modules.purchaseDetails.domain.PurchaseDetails;
import com.adopt.apigw.modules.purchaseDetails.mapper.PaymentGatewayResponseMapper;
import com.adopt.apigw.modules.purchaseDetails.model.PaymentGatewayResponseDTO;
import com.adopt.apigw.modules.purchaseDetails.model.PurchaseDetailsDTO;
import com.adopt.apigw.modules.purchaseDetails.repository.PaymentGatewayResponseRepository;

@Service
public class PaymentGatewayResponseService extends ExBaseAbstractService<PaymentGatewayResponseDTO, PaymentGatewayResponse, Long> {
    @Autowired
    PaymentGatewayResponseRepository repository;

    public PaymentGatewayResponseService(PaymentGatewayResponseRepository repository, PaymentGatewayResponseMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }
}
