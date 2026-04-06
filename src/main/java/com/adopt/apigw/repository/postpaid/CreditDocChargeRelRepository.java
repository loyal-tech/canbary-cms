package com.adopt.apigw.repository.postpaid;

import com.adopt.apigw.model.creditdoc.CreditDocChargeRel;
import com.adopt.apigw.model.postpaid.CreditDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditDocChargeRelRepository extends JpaRepository<CreditDocChargeRel, Integer>, QuerydslPredicateExecutor<CreditDocChargeRel> {

    List<CreditDocChargeRel> findAllByCreditDocument(CreditDocument creditDocument);
}
