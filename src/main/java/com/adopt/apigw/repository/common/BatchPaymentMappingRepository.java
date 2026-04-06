package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.common.BatchPayment;
import com.adopt.apigw.model.common.BatchPaymentMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchPaymentMappingRepository extends JpaRepository<BatchPaymentMapping, Long>, QuerydslPredicateExecutor<BatchPaymentMapping> {
}
