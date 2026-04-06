package com.adopt.apigw.repository;

import com.adopt.apigw.pojo.PlanAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanAuditRepository extends JpaRepository<PlanAudit,Long> {
}
