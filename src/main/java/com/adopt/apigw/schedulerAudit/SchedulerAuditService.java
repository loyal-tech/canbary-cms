package com.adopt.apigw.schedulerAudit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchedulerAuditService {

    @Autowired
    private SchedulerAuditRepository schedulerAuditRepository;

    public void saveEntity(SchedulerAudit schedulerAudit) {
        schedulerAuditRepository.save(schedulerAudit);
    }
}