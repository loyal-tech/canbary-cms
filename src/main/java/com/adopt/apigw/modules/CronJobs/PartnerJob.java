package com.adopt.apigw.modules.CronJobs;

import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerInvoiceCreationThread;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.schedulerAudit.SchedulerAudit;
import com.adopt.apigw.schedulerAudit.SchedulerAuditService;
import com.adopt.apigw.service.SchedulerLockService;
import com.adopt.apigw.service.postpaid.CreditDocService;
import com.adopt.apigw.service.postpaid.PartnerCommissionService;
import com.adopt.apigw.service.postpaid.TempPartnerLedgerDetailsRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class PartnerJob {

    @Autowired
    MessageSender messageSender;

    @Autowired
    PartnerCommissionService partnerCommissionService;

    @Autowired
    PartnerCreditDocRepository partnerCreditDocRepository;

    @Autowired
    TempPartnerLedgerDetailsRepository tempPartnerLedgerDetailsRepository;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired
    CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    CreditDocService creditDocService;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;


    @Scheduled(cron = "${cronjobtimeforpartnerinvoice}}")
    public void cronJobForPartnerInvoiceCreation() {
        log.info("XXXXXXXXXXXX----------CRON Partner Invoice Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.PARTNER_INVOICE_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.PARTNER_INVOICE)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.PARTNER_INVOICE);
            try {
                LocalDate currentTime = LocalDate.now();
                currentTime = currentTime.minusDays(1);
                Runnable invoiceRunnable = new PartnerInvoiceCreationThread(currentTime, messageSender);
                Thread invoiceThread = new Thread(invoiceRunnable);
                invoiceThread.start();

                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Partner Invoice Scheduler Run Success");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(null);
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.PARTNER_INVOICE);
                log.info("XXXXXXXXXXXX---------- Partner Invoice Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Partner Invoice Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Partner Invoice Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }

    public void manualAdjustmentAgainstInvoice() {
        QPartnerDebitDocument qPartnerCreditDocument = QPartnerDebitDocument.partnerDebitDocument;
        BooleanExpression expression = qPartnerCreditDocument.isNotNull();
        expression = expression.and(qPartnerCreditDocument.isDelete.eq(false));
        expression = expression.and(qPartnerCreditDocument.totalamount.gt(qPartnerCreditDocument.adjustedamount));
        List<PartnerDebitDocument> list = (List<PartnerDebitDocument>) partnerCreditDocRepository.findAll(expression);
        list.stream().forEach(record -> {
            partnerCommissionService.adjustInvoiceAmount(record.getTotalamount(), record.getId().longValue());
        });
    }
}
