package com.adopt.apigw.modules.payments.repository;

import com.adopt.apigw.modules.CommonList.domain.CommonList;
import com.adopt.apigw.modules.payments.domain.Payment;
import com.adopt.apigw.modules.subscriber.Domain.ServiceAudit;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceAuditRepository extends JpaRepository<ServiceAudit, Long> , QuerydslPredicateExecutor<ServiceAudit> {

    @Query(value = "select * from tbltserviceaudit t where t.custservicemappingid  =:custpackid" , nativeQuery = true)
    List<ServiceAudit> findResonIdByCpr(@Param("custpackid") Integer custpackid);

//    @Query(value ="select * from tbltserviceaudit t where t.custservicemappingid =:custpackid" ,
//            countQuery = "select count(*) from tbltserviceaudit t where t.custservicemappingid = :servicemappingId",
//            nativeQuery = true)
//    Page<ServiceAudit> findAll(PageRequest pageRequest,@Param("custpackid")Integer servicemappingId);
}


