package com.adopt.apigw.modules.auditLog.repository;

import com.adopt.apigw.modules.Area.domain.Area;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.auditLog.domain.AuditLogEntry;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntry, Long> , QuerydslPredicateExecutor<AuditLogEntry> {

    @Query(nativeQuery = true
            , value = "SELECT * from tblauditlog t where t.audit_id IN (:s1)"
            , countQuery = "SELECT count(*) from tblauditlog t where t.audit_id IN (:s1)")
    Page<AuditLogEntry> findAllBy(Pageable pageable, @Param("s1") List<String> s1);

    @Query(value = "select * from tblauditlog t where MVNOID in :mvnoIds", nativeQuery = true)
    Page<AuditLogEntry> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);


    List<AuditLogEntry> findAllByentityRefId(BooleanExpression entityId);
}
