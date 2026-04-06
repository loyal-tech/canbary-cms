package com.adopt.apigw.repository.postpaid;

import com.adopt.apigw.model.postpaid.DebitDocDetails;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface DebitDocDetailRepository  extends JpaRepository<DebitDocDetails, Integer>, QuerydslPredicateExecutor<DebitDocDetails> {

    List<DebitDocDetails> findAllByDebitdocumentid(Integer debitdocumentid);
    List<DebitDocDetails> findAllByDebitdocdetailidIn(List<Integer> debitdocDetailids);


}
