package com.adopt.apigw.schedulers;

import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustMacMappping;
import com.adopt.apigw.model.postpaid.CustQuotaDetails;
import com.adopt.apigw.modules.CronJobs.CronjobConst;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CustomMessage;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.enable.scheduling")
public class QuotaResetScheduler {

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

    @Value("${spring.enable.quotareset.scheduling}")
    private boolean quotaResetEnable;

    private static final Logger logger = LoggerFactory.getLogger(APIController.class);

    @Scheduled(cron = "${cronjobtimeforquotaresetdaymidnight}")
    public void runSchedulerForCustomer() {
        if (quotaResetEnable)
            customerPlanSchedule();
        else
            logger.info("Quota reset Flag Disable");
    }

    public void customerPlanSchedule() {
        log.info("XXXXXXXXXXXX----------CRON Quota Reset Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.QUOTA_RESET_DAY_MIDNIGHT_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.QUOTA_RESET_DAY_MIDNIGHT)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.QUOTA_RESET_DAY_MIDNIGHT);
            try {
                LocalDateTime todayDate=LocalDateTime.now();
                List<Integer> listOfCustomerId = customerRepository.findAllIdByIsDeletedIsFalseAndStatusIn(
                        Arrays.asList(new String[]{"Active", "active", "ACTIVE", "InActive", "Inactive", "inactive", "Suspend", "suspend"}));
                listOfCustomerId.forEach(custId -> {
                    List<CustQuotaDetails> custQuotaDetailss = custQuotaRepository.findByCustomerId(custId);
                    custQuotaDetailss.forEach(CustQuotaDetails -> {
//                if ((CustQuotaDetails.getCurrentSessionUsageVolume() != null && CustQuotaDetails.getCurrentSessionUsageVolume() > 0 )  || (CustQuotaDetails.getCurrentSessionUsageTime() != null && CustQuotaDetails.getCurrentSessionUsageTime() > 0)) {
//                    CustQuotaDetails.setSkipQuotaUpdate(Boolean.TRUE);
//                }
                        Customers cust = customerRepository.findById(CustQuotaDetails.getCustomer().getId()).orElse(null);
                        if (cust != null) {
                            if (CustQuotaDetails.getPostpaidPlan().getEndDate().isAfter(ChronoLocalDate.from(todayDate))) {
                                List<CustMacMappping> addressMappings = custMacMapppingRepository.findByCustomerId(cust.getId());
                                cust.setCustMacMapppingList(addressMappings);
                                updateCustQuotaDetails(CustQuotaDetails, cust, todayDate);
                            } else if (CustQuotaDetails.getPostpaidPlan().getEndDate().isBefore(ChronoLocalDate.from(LocalDateTime.now())) && cust.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS))
                                inActivateCustomer(cust);
                        }
                    });
                });
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Quota Reset Scheduler Scheduler Run Success");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(listOfCustomerId.size());
                logger.info("******************   Quota Reset Scheduler End At " + LocalDateTime.now() + "   ******************");
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.QUOTA_RESET_DAY_MIDNIGHT);
                log.info("XXXXXXXXXXXX---------- Quota Reset Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Quota Reset Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Quota Reset Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }
   public void updateCustQuotaDetails(CustQuotaDetails custQuotaDetails, Customers customer,LocalDateTime todayDate) {
       String quotaResetInterval = custQuotaDetails.getPostpaidPlan().getQuotaResetInterval();
       LocalDate lastQuotaReset=null;
       if(custQuotaDetails.getLastQuotaReset()!=null)
           lastQuotaReset=custQuotaDetails.getLastQuotaReset().toLocalDate();

       if (quotaResetInterval!=null && quotaResetInterval.equals("Daily"))
           custQuotaDetails = resetQuota(custQuotaDetails, customer, 1L,todayDate);
       if (quotaResetInterval!=null && quotaResetInterval.equals("Weekly") && lastQuotaReset!=null && todayDate.minusDays(7).equals(lastQuotaReset))
           custQuotaDetails = resetQuota(custQuotaDetails, customer, 7L,todayDate);
       if (quotaResetInterval!=null && quotaResetInterval.equals("Monthly") && lastQuotaReset!=null && todayDate.minusDays(30).equals(lastQuotaReset))
           custQuotaDetails = resetQuota(custQuotaDetails, customer, 30L,todayDate);
   }

    public CustQuotaDetails resetQuota(CustQuotaDetails custQuotaDetails, Customers customer, Long nextResetDays,LocalDateTime todayDate) {
        try{
            UpdateCustomerQuotaDto updateCustomerDto = new UpdateCustomerQuotaDto();
            updateCustomerDto.setCustId(customer.getId());
            Long i = customer.getMvnoId().longValue();
            updateCustomerDto.setMvnoId(i);
            updateCustomerDto.setSkipQuotaUpdate(custQuotaDetails.getSkipQuotaUpdate());
            Double volumeBasedTotalQuota = custQuotaDetails.getTotalQuotaKB();
            Double timeBasedTotalQuota = custQuotaDetails.getTimeTotalQuotaSec();

            boolean isUpdated = false;
            if (custQuotaDetails.getPostpaidPlan().getQuotatype().equals("Data")) {
                custQuotaDetails.setTotalQuotaKB(volumeBasedTotalQuota);
                custQuotaDetails.setUsedQuotaKB(0.0000);
                custQuotaDetails.setUsedQuota(0.0000);
                updateCustomerDto.setUserName(customer.getUsername());
                updateCustomerDto.setCustId(customer.getId());
                updateCustomerDto.setQuotaDetailId(custQuotaDetails.getId());
                updateCustomerDto.setUsedQuota(0.0000);
                updateCustomerDto.setUsedQuotaKB(0.0000);
                isUpdated = true;
            }
            if (custQuotaDetails.getPostpaidPlan().getQuotatype().equals("Time")) {
                custQuotaDetails.setTimeTotalQuotaSec(timeBasedTotalQuota);
                custQuotaDetails.setTimeUsedQuotaSec(0.0000);
                custQuotaDetails.setTimeQuotaUsed(0.0000);
                updateCustomerDto.setUserName(customer.getUsername());
                updateCustomerDto.setCustId(customer.getId());
                updateCustomerDto.setQuotaDetailId(custQuotaDetails.getId());
                updateCustomerDto.setUsedTimeQuota(0.0000);
                updateCustomerDto.setUsedTimeQuotaSec(0.0000);
                isUpdated = true;
            }
            if (custQuotaDetails.getPostpaidPlan().getQuotatype().equals("Both")) {
                custQuotaDetails.setTimeTotalQuotaSec(timeBasedTotalQuota);
                custQuotaDetails.setTimeUsedQuotaSec(0.0000);
                custQuotaDetails.setTimeQuotaUsed(0.0000);
                custQuotaDetails.setTotalQuotaKB(volumeBasedTotalQuota);
                custQuotaDetails.setUsedQuotaKB(0.0000);
                custQuotaDetails.setUsedQuota(0.0000);
                updateCustomerDto.setUserName(customer.getUsername());
                updateCustomerDto.setCustId(customer.getId());
                updateCustomerDto.setQuotaDetailId(custQuotaDetails.getId());
                updateCustomerDto.setUsedTimeQuota(0.0000);
                updateCustomerDto.setUsedTimeQuotaSec(0.0000);
                updateCustomerDto.setUsedQuota(0.0000);
                updateCustomerDto.setUsedQuotaKB(0.0000);
                isUpdated = true;
            }
            if(isUpdated)
                updateCustomerInRadius(updateCustomerDto);
            custQuotaDetails.setLastQuotaReset(todayDate);
            return custQuotaRepository.save(custQuotaDetails);
        }catch(Exception e){
            logger.error("Error To Reset Quoat: "+e.getMessage()+" customer: "+customer.getUsername());
            return null;
        }
    }

    public void inActivateCustomer(Customers customer) {
        try{
            if (customer != null) {
                customer.setStatus("Inactive");
                customerRepository.save(customer);
            }
        } catch (Exception ex) {
            logger.error("Error to change customer status to inactive");
        }
    }

    public void updateCustomerInRadius(UpdateCustomerQuotaDto updateCustomerDto) {
        String spanId = UUID.randomUUID().toString().replaceAll("-", "");
        String traceId = UUID.randomUUID().toString().replaceAll("-", "");
        QuotaCustomMessage quotaCustomMessage = new QuotaCustomMessage(updateCustomerDto, spanId, traceId);
        //messageSender.send(quotaCustomMessage, RabbitMqConstants.QUEUE_UPDATE_CUSTOMER_QUOTA);
        kafkaMessageSender.send(new KafkaMessageData(quotaCustomMessage,quotaCustomMessage.getClass().getSimpleName()));
    }
}
