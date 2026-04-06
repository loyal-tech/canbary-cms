package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.TrialBillRun;
import com.adopt.apigw.model.postpaid.TrialDebitDocument;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface TrialDebitDocRepository extends JpaRepository<TrialDebitDocument, Integer>, QuerydslPredicateExecutor<TrialDebitDocument> {

//	@Query(value = "select * from TBLMDebitDocument where lower(name) like '%' :search  '%' order by DebitDocumentID",
//            countQuery = "select count(*) from TBLMDebitDocument where lower(name) like '%' :search '%'",
//            nativeQuery = true)
//    Page<DebitDocument> searchEntity(@Param("search") String searchText, Pageable pageable);

//	List<DebitDocument> findByStatus(String status);

    List<TrialDebitDocument> findByBillrunid(Integer billRunId);

    @Query("select t from TrialDebitDocument t where t.isDelete=false")
    List<TrialDebitDocument> findAll();

    @Query("update TrialDebitDocument t set t.isDelete=true where t.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    List<TrialDebitDocument> findAllById(Integer id);

    List<TrialDebitDocument> findAllByCustpackrelidIn(List<Integer> integers);

    List<TrialDebitDocument> findAllByIdIn(List<Integer> ids);

    @Query(value = "SELECT trialdebitdocumentnumber FROM TBLTTRIALDEBITDOCUMENT WHERE trialdebitdocumentid = :invoiceId", nativeQuery = true)
    String findDocnumberById(@Param("invoiceId") Integer invoiceId);

}
