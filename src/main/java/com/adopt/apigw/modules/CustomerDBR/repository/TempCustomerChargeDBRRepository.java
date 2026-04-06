package com.adopt.apigw.modules.CustomerDBR.repository;

import com.adopt.apigw.modules.CustomerDBR.domain.CustomerChargeDBR;
import com.adopt.apigw.modules.CustomerDBR.domain.TempCustomerChargeDBR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TempCustomerChargeDBRRepository extends JpaRepository<TempCustomerChargeDBR, Long>, QuerydslPredicateExecutor<TempCustomerChargeDBR> {
}