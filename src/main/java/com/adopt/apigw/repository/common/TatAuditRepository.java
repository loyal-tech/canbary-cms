package com.adopt.apigw.repository.common;


import com.adopt.apigw.model.common.EtrAudit;
import com.adopt.apigw.model.common.TicketTatAudits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TatAuditRepository extends JpaRepository<TicketTatAudits,Integer>, QuerydslPredicateExecutor<TicketTatAudits> {

    List<TicketTatAudits> findAllByCaseId(Integer caseId);
}
