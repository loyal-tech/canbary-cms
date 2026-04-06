package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.BillRun;
import com.adopt.apigw.model.postpaid.Tax;
import com.querydsl.core.types.dsl.BooleanExpression;

import javax.transaction.Transactional;
import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface BillRunRepository extends JpaRepository<BillRun, Integer>, QuerydslPredicateExecutor<BillRun> {
//public interface BillRunRepository extends JpaRepository<BillRun, Integer>{

//	@Query(value = "select * from TBLMBillRun where lower(name) like '%' :search  '%' order by BillRunID",
//            countQuery = "select count(*) from TBLMBillRun where lower(name) like '%' :search '%'",
//            nativeQuery = true)
//    Page<BillRun> searchEntity(@Param("search") String searchText, Pageable pageable);

    List<BillRun> findByStatus(String status);

    List<BillRun> findByStatusAndMvnoIdIn(String status, List mvnoIds);
//    List<BillRun> findByStatusAndMvnoIdInAndBuIdIn(String status, List mvnoIds, List buIds);

    @Query("select t from BillRun t where t.isDelete=false")
    List<BillRun> findAll();

//    @Query(value = "select * from TBLMBILLRUN t where t.is_delete=false and t.MVNOID in :mvnoIds and t.BUID in :buIds", nativeQuery = true)
//    List<BillRun> findAll(@Param("mvnoIds")List mvnoIds, @Param("buIds")List buIds);

    @Query(value = "select * from TBLMBILLRUN t where t.is_delete=false and t.MVNOID in :mvnoIds", nativeQuery = true)
    List<BillRun> findAll(@Param("mvnoIds")List mvnoIds);

    @Query("update BillRun b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    @Query(value = "select * from tblmbillrun t where t.is_delete = 0 AND t.MVNOID= :MVNOID OR t.MVNOID IS NULL", countQuery = "select count(*) from tblmbillrun t where t.is_delete = 0 AND t.MVNOID= :MVNOID OR t.MVNOID IS NULL", nativeQuery = true)
    Page<BillRun> findAll(Pageable pageable);

//    @Query(nativeQuery = true,
//           value = "select * from tblmbillrun t1 where t1.is_delete = false AND t1.MVNOID in :MVNOIDS",
//           countQuery = "select count(*) from tblmbillrun t1 where t1.is_delete = false AND t1.MVNOID in :MVNOIDS")
//    Page<BillRun> findAll(Pageable pageable, @Param("MVNOIDS") List MVNOIDS);
//
//    @Query(nativeQuery = true,value = "select * from tblmbillrun t1 where t1.is_delete = false AND t1.MVNOID in :MVNOIDS AND t1.BUID in :buIds"
//            ,countQuery = "select count(*) from tblmbillrun t1 where t1.is_delete = false AND t1.MVNOID in :MVNOIDS AND t1.BUID in :buIds")
//    Page<BillRun> findAll(Pageable pageable, @Param("MVNOIDS") List MVNOIDS, @Param("buIds") List buIds);
}
