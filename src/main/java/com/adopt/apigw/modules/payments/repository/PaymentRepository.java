package com.adopt.apigw.modules.payments.repository;

import com.adopt.apigw.modules.BankManagement.domain.BankManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.payments.domain.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findByTxnId(String txnId);

}
