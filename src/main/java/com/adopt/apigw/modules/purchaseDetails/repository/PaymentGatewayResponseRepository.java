package com.adopt.apigw.modules.purchaseDetails.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.purchaseDetails.domain.PaymentGatewayResponse;

@Repository
public interface PaymentGatewayResponseRepository extends JpaRepository<PaymentGatewayResponse, Long> {
}
