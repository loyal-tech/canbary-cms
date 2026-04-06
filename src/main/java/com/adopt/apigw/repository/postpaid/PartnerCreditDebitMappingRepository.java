package com.adopt.apigw.repository.postpaid;

import com.adopt.apigw.model.postpaid.PartnerCreditDebitMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PartnerCreditDebitMappingRepository extends JpaRepository<PartnerCreditDebitMapping, Integer>, QuerydslPredicateExecutor<PartnerCreditDebitMapping> {
}
