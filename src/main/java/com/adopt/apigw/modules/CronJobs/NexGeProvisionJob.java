package com.adopt.apigw.modules.CronJobs;

import com.adopt.apigw.schedulerAudit.SchedulerAudit;
import com.adopt.apigw.schedulerAudit.SchedulerAuditService;
import com.adopt.apigw.service.SchedulerLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.integrations.NexgeVoice.service.NexgeVoiceProvisionService;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.utils.CommonConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NexGeProvisionJob {

    @Autowired
    private CustomersRepository repository;

    @Autowired
    private NexgeVoiceProvisionService provisionService;

    @Autowired
    private PostpaidPlanService planService;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    @Scheduled(cron = "${cronjobtimefornexgeprovision}}")
    public void cronJobForNexGeProvision() {
        log.info("XXXXXXXXXXXX----------CRON NexGe Provision Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.NEX_GE_PROVISION_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.NEX_GE_PROVISION)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.NEX_GE_PROVISION);
            try {
                ApplicationLogger.logger.info("CRON JOB FOR NexGe Provision : " + LocalDate.now());
                //For Provision
                List<Customers> customersList = repository.findAllForProvisionInNexGe();
                if (null != customersList && 0 < customersList.size()) {
                    for (Customers customers : customersList) {
                        Boolean provisionFlag = provisionService.performCustomerProvision(customers);
                        customers.setVoiceprovision(provisionFlag);
                        repository.save(customers);
                    }
                    schedulerAudit.setEndTime(LocalDateTime.now());
                    schedulerAudit.setDescription("IP Release Successfully");
                    schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                    schedulerAudit.setTotalCount(null);
                }
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.NEX_GE_PROVISION);
                log.info("XXXXXXXXXXXX---------- NexGe Provision Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("NexGe Provision Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------NexGe Provision Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }

    @Scheduled(cron = "${cronjobtimefornexgechangeplan}}")
    public void cronJobForChangePlan() {
        log.info("XXXXXXXXXXXX----------CRON Change Plan Scheduler Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.CHANGE_PLAN_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.CHANGE_PLAN)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.CHANGE_PLAN);
            try {
                ApplicationLogger.logger.info("CRON JOB FOR NexGe Update Plan : " + LocalDate.now());
                List<Customers> customersList = repository.findAllForChangePlanInNexGe();
                if (null != customersList && 0 < customersList.size()) {
                    for (Customers customers : customersList) {
                        if (null != customers.getPlanMappingList() && 0 < customers.getPlanMappingList().size()) {
                            List<CustPlanMappping> newActivatedPlanMappingList = customers.getPlanMappingList().stream()
                                    .filter(data -> data.getStartDate().isAfter(LocalDateTime.now())
                                            && data.getService().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA)).collect(Collectors.toList());
                            for (CustPlanMappping custPlanMappping : newActivatedPlanMappingList) {
                                if (null != custPlanMappping.getPlanId()) {
                                    PostpaidPlan postpaidPlan = planService.get(custPlanMappping.getPlanId(),customers.getMvnoId());
                                    if (null != postpaidPlan.getParam1() && postpaidPlan.getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_RENEW)) {
                                        provisionService.performServicePlanUpdate(customers.getId().toString(), customers.getAcctno(), postpaidPlan.getParam1());
                                    }
                                }
                            }
                        }
                    }
                    schedulerAudit.setEndTime(LocalDateTime.now());
                    schedulerAudit.setDescription("IP Release Successfully");
                    schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                    schedulerAudit.setTotalCount(customersList.size());
                }
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.CHANGE_PLAN);
                log.info("XXXXXXXXXXXX---------- Change Plan Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Change Plan Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Change Plan Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }
}
