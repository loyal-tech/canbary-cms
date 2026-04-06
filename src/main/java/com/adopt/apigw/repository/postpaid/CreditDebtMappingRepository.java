package com.adopt.apigw.repository.postpaid;

import com.adopt.apigw.model.postpaid.CreditDebitDocMapping;
import com.adopt.apigw.model.postpaid.DebitDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditDebtMappingRepository extends JpaRepository<CreditDebitDocMapping, Integer>, QuerydslPredicateExecutor<CreditDebitDocMapping> {

    List<CreditDebitDocMapping> findBydebtDocId(Integer debtDocId);

    List<CreditDebitDocMapping> findBydebtDocIdAndCreditDocId(Integer debtDocId,Integer creditDocId);

    List<DebitDocument> findBycreditDocId(Integer creditDocId);

    List<CreditDebitDocMapping> findByCreditDocId(Integer creditDocId);

    List<CreditDebitDocMapping> findAllBydebtDocIdIn(List<Integer> debtDocId);

    @Modifying
    @Query("update CreditDebitDocMapping m set m.debtDocId=:newDebitDocId where m.debtDocId=:oldDebitDocId")
    void updateByOldDebitDocId(@Param(value = "newDebitDocId") Integer newDebitDocId, @Param(value = "oldDebitDocId") Integer oldDebitDocId);
    @Query(value = "SELECT MAX(m.id) FROM CreditDebitDocMapping m")
    Integer findlast();
}
