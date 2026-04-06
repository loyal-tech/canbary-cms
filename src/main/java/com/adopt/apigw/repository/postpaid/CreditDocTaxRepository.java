package com.adopt.apigw.repository.postpaid;

import com.adopt.apigw.model.creditdoc.CreditDocChargeRel;
import com.adopt.apigw.model.creditdoc.CreditDocTaxRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditDocTaxRepository extends JpaRepository<CreditDocTaxRel, Integer>, QuerydslPredicateExecutor<CreditDocTaxRel> {
}
