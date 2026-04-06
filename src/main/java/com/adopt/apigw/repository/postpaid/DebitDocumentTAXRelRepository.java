package com.adopt.apigw.repository.postpaid;

import com.adopt.apigw.model.postpaid.DebitDocumentTAXRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebitDocumentTAXRelRepository extends JpaRepository<DebitDocumentTAXRel, Integer>, QuerydslPredicateExecutor<DebitDocumentTAXRel> {

    @Query("select new DebitDocumentTAXRel(d.taxname,sum(d.amount),d.taxLedgerId) from DebitDocumentTAXRel  d where d.debitdocumentid=:debitDocumentId group by d.taxname,d.taxLedgerId")
    List<DebitDocumentTAXRel> getTotalTaxByType(@Param(value = "debitDocumentId") Integer debitDocumentID);

    @Query(value = "SELECT d.subscriberid AS customerId, t.percentage AS taxPercentage " +
            "FROM tbltdebitdocumenttaxrel t " +
            "JOIN tbltdebitdocument d ON d.debitdocumentid = t.debitdocumentid " +
            "WHERE d.subscriberid IN (:customerIds)",
            nativeQuery = true)
    List<Object[]> getTaxPercentageByCustomerIds(@Param("customerIds") List<Integer> customerIds);


}
