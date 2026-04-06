package com.adopt.apigw.schedulerAudit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulerAuditRepository extends JpaRepository<SchedulerAudit, Long> {
}

