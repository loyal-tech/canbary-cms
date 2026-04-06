package com.adopt.apigw.modules.CafFollowUp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.CafFollowUp.domain.CafFollowUpAudit;

@Repository
public interface CafFollowUpAuditRepository extends JpaRepository<CafFollowUpAudit, Long>{

}
