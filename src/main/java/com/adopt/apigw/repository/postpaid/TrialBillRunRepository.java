package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.Tax;
import com.adopt.apigw.model.postpaid.TrialBillRun;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface TrialBillRunRepository extends JpaRepository<TrialBillRun, Integer>, QuerydslPredicateExecutor<TrialBillRun> {
//public interface BillRunRepository extends JpaRepository<BillRun, Integer>{

//	@Query(value = "select * from TBLMBillRun where lower(name) like '%' :search  '%' order by BillRunID",
//            countQuery = "select count(*) from TBLMBillRun where lower(name) like '%' :search '%'",
//            nativeQuery = true)
//    Page<BillRun> searchEntity(@Param("search") String searchText, Pageable pageable);

    List<TrialBillRun> findByStatus(String status);

    @Query("select t from TrialBillRun t where t.isDelete=false")
    List<TrialBillRun> findAll();

    @Query("update TrialBillRun t set t.isDelete=true where t.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);
}
