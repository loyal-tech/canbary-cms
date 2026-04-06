package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.Location;
import com.adopt.apigw.model.postpaid.PartnerBillRun;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
//public interface PartnerBillRunRepository extends JpaRepository<PartnerBillRun, Integer>, QuerydslPredicateExecutor<PartnerBillRun>   {
public interface PartnerBillRunRepository extends JpaRepository<PartnerBillRun, Integer>, QuerydslPredicateExecutor<PartnerBillRun> {

//	@Query(value = "select * from TBLMPartnerBillRun where lower(name) like '%' :search  '%' order by PartnerBillRunID",
//            countQuery = "select count(*) from TBLMPartnerBillRun where lower(name) like '%' :search '%'",
//            nativeQuery = true)
//    Page<PartnerBillRun> searchEntity(@Param("search") String searchText, Pageable pageable);

    List<PartnerBillRun> findByStatus(String status);

    @Query("select t from PartnerBillRun t where t.isDelete=false")
    List<PartnerBillRun> findAll();

    @Query("update PartnerBillRun b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);
}
