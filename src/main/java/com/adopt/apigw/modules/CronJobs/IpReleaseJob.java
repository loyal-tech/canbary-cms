package com.adopt.apigw.modules.CronJobs;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.schedulerAudit.SchedulerAudit;
import com.adopt.apigw.schedulerAudit.SchedulerAuditService;
import com.adopt.apigw.service.SchedulerLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.ippool.repository.IPPoolDtlsRepository;
import com.adopt.apigw.modules.ippool.service.IPPoolDtlsService;

import java.time.LocalDateTime;

@Slf4j
@Component
public class IpReleaseJob {
    @Autowired
    private IPPoolDtlsRepository repository;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    @Scheduled(cron = "${cronjobtimeforiprelease}}")
    public void cronJob() {
        log.info("XXXXXXXXXXXX----------CRON Time FOR IP RELEASE Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.IP_RELEASE_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_IP_RELEASE)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_IP_RELEASE);
            try {
                ApplicationLogger.logger.info("CRON JOB FOR IP RELEASE : " + LocalDateTime.now());
                repository.releaseIP(LocalDateTime.now());
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("IP Release Successfully");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(null);
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_IP_RELEASE);
                log.info("XXXXXXXXXXXX---------- Time FOR IP RELEASE Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Time FOR IP RELEASE Schedule Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Time FOR IP RELEASE Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }


    @Scheduled(cron = "${everydaycronjobtimeforiprelease}")
    public void cronJob2() {
        log.info("XXXXXXXXXXXX----------CRON Every Day IP RELEASE Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.EVERY_DAY_IP_RELEASE_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.EVERY_DAY_IP_RELEASE)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.EVERY_DAY_IP_RELEASE);
            try {
                ApplicationLogger.logger.info("Every Day IP RELEASE : " + LocalDateTime.now());
                repository.releaseIP2(LocalDateTime.now());
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Every Day IP Release Successfully");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                // schedulerAudit.setTotalCount(repository.releaseIP2(LocalDateTime.now()));
                schedulerAudit.setTotalCount(null);

            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.EVERY_DAY_IP_RELEASE);
                log.info("XXXXXXXXXXXX---------- Every Day IP RELEASE Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Every Day IP RELEASE Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Every Day IP RELEASE Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }
}