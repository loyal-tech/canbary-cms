package com.adopt.apigw.modules.paymentGatewayMaster.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.adopt.apigw.modules.paymentGatewayMaster.domain.PaymentGateWay;

import java.util.List;

public interface PaymentGatewayRepository extends JpaRepository<PaymentGateWay, Long> {

    List<PaymentGateWay> findAllByNameAndIsDeletedIsFalse(String name);

    @Query("select t FROM PaymentGateWay t where t.isDeleted = false")
    Page<PaymentGateWay> findAll(Pageable pageable);

    List<PaymentGateWay> findByUserenableflagAndStatusAndIsDeletedIsFalse(Boolean userenable, String Status);

    List<PaymentGateWay> findByPartnerenableflagAndStatusAndIsDeletedIsFalse(Boolean partnerenable, String Status);

    List<PaymentGateWay> findByUserenableflagAndPartnerenableflagAndStatusAndIsDeletedIsFalse(Boolean userenable, Boolean partnerenable, String Status);

    PaymentGateWay findByIdAndStatus(Long id, String status);

    List<PaymentGateWay> findAllByStatusAndIsDeletedIsFalse(String Status);
}
