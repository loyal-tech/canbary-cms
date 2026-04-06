package com.adopt.apigw.schedulers;

import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustMacMappping;
import com.adopt.apigw.model.postpaid.CustQuotaDetails;
import com.adopt.apigw.modules.CronJobs.CronjobConst;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.AppproveOrgInvoiceMessage;
import com.adopt.apigw.rabbitMq.message.CafClosedMessage;
import com.adopt.apigw.rabbitMq.message.QuotaCustomMessage;
import com.adopt.apigw.repository.common.CustQuotaRepository;
import com.adopt.apigw.repository.postpaid.CustMacMapppingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.schedulerAudit.SchedulerAudit;
import com.adopt.apigw.schedulerAudit.SchedulerAuditService;
import com.adopt.apigw.service.SchedulerLockService;
import com.adopt.apigw.utils.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "spring.enable.scheduling")
public class CafClosedSchedular {

    @Autowired
    CustomersRepository customerRepository;

    @Autowired
    CustQuotaRepository custQuotaRepository;

    @Autowired
    CustMacMapppingRepository custMacMapppingRepository;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    @Value("${spring.enable.cafclosed.scheduling}")
    private boolean cafClosedSchedular;

    private static final Logger logger = LoggerFactory.getLogger(CafClosedSchedular.class);

    @Scheduled(cron = "${cronjobtimeforcafclosedschedular}")
    public void runSchedulerForCustomer() {
        if (cafClosedSchedular)
            cafClosedSchedular();
        else
            logger.info("Caf closed schedular is off.");
    }

    public void cafClosedSchedular() {
        log.info("XXXXXXXXXXXX----------CRON Closed caf Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.CAF_CLOSED_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.CAF_SCHEDULAR_CRONJOB)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.CAF_SCHEDULAR_CRONJOB);
            try {
                /**Here it will find all caf **/
                List<Integer> listOfCustomer=customerRepository.findCustomersWithAllPlansExpired();
                /**Here it will update  caf to closed **/
                if(!listOfCustomer.isEmpty()) {
                    setStatusToClosedToAllCaf(listOfCustomer);
                }

                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Caf closed Scheduler Run Success");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(listOfCustomer.size());
                logger.info("****************** Closed caf Scheduler End At " + LocalDateTime.now() + "   ******************");
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.CAF_SCHEDULAR_CRONJOB);
                log.info("XXXXXXXXXXXX---------- Closed caf Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Closed caf Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Closed caf Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }


    public void setStatusToClosedToAllCaf(List<Integer> custIds){
        int updated_rows =customerRepository.closeCustomersByIds(custIds);
        log.info("row updated for customer count is : "+updated_rows);
        int updated_cpr_rows = customerRepository.stopAllPlansByCustomerIds(custIds);
        log.info("row updated for customer plan count is : "+updated_cpr_rows);
        CafClosedMessage cafClosedMessage = new CafClosedMessage();
        cafClosedMessage.setCustIds(custIds);
        kafkaMessageSender.send(new KafkaMessageData(cafClosedMessage, CafClosedMessage.class.getSimpleName()));
    }
}
