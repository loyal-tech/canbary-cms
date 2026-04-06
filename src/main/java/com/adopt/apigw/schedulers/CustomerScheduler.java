package com.adopt.apigw.schedulers;

import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.core.dto.ConnectionNumberDto;

import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.CustPlanMapppingRepository;
import com.adopt.apigw.modules.CronJobs.CronjobConst;

import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqModel;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.repository.postpaid.CustPlanMappingRepository;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.DebitDocumentInventoryRel.DebitDocNumberMappingPojo;
import com.adopt.apigw.modules.RvenueClient.RevenueClient;
import com.adopt.apigw.repository.common.ShorterRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.schedulerAudit.SchedulerAudit;
import com.adopt.apigw.schedulerAudit.SchedulerAuditService;
import com.adopt.apigw.service.CustomerThreadService;
import com.adopt.apigw.service.SchedulerLockService;
import com.adopt.apigw.service.postpaid.CustPlanMappingService;
import com.adopt.apigw.utils.NumberSequenceUtil;
import com.adopt.apigw.utils.StatusConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.CommonConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@EnableScheduling
@ConditionalOnProperty(name = "spring.enable.scheduling")
public class CustomerScheduler {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    CustomerThreadService customerThreadService;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    @Autowired
    private NumberSequenceUtil numberSequenceUtil;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private CreateDataSharedService createDataSharedService;


    @Autowired
    private RevenueClient revenueClient;

    @Value("${auto-assign-username: superadmin}")
    private String autoAssignUsername;

    @Value("${auto-assign-password: superadmin@2021@SUPERADMIN}")
    private String autoAssignPasswod;
    @Autowired
    private ApiGatewayScheduler apiGatewaySchedulerl;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    SubscriberService subscriberService;
    @Autowired
    CustPlanMappingService custPlanMappingService;

    @Async
    @Scheduled(cron = "${cronJobTimeForDunning}")
    public void runSchedulerForDunning() throws Exception {

        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.TIME_FOR_DUNNING_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_DUNNING)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_DUNNING);
            try {
                List<GrantedAuthority> role_name = new ArrayList<>();
                ApplicationLogger.logger.debug("***** Api gateway scheduler  for dunning Starting *****");
                role_name.add(new SimpleGrantedAuthority("ADMIN"));
                LoggedInUser user = new LoggedInUser(autoAssignUsername, autoAssignPasswod, true, true, true, true, role_name, "c", autoAssignUsername, LocalDateTime.now(), 1, 1, "ADMIN", null, 1, null, 1, new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(), autoAssignUsername, null, null, null);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(auth);
                ApplicationLogger.logger.debug("***** Adopt Api gateway scheduler  for dunning Starting *****");
                System.out.println("********* Adopt Api gateway schedular for dunning started*****");
                String token = apiGatewaySchedulerl.GenerateTokenUsingLoggedInUser(apiGatewaySchedulerl.getLoggedInUser());
                ObjectMapper objectMapper = new ObjectMapper();
                List<DebitDocNumberMappingPojo> pojos = new ArrayList<>();
                try {
                    pojos = revenueClient.getDebitDocNumber(token);
                    if (pojos.size() > 0) {
                        System.out.println("Invoic List size" + pojos.size());
                    } else {
                        System.out.println("No Invoice Found");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //sendSmsNotificationForDunningCustomer();
//        if(!CollectionUtils.isEmpty(pojos)){
                apiGatewaySchedulerl.sendDunningNotification(pojos, token);
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Dunning Scheduler Run Success");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);

//        }
            }catch (Exception e){
                e.getMessage();
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription( e.getMessage());
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_DUNNING);
                log.info("XXXXXXXXXXXX---------- Dunning Scheduler Locked released ---------XXXXXXXXXXXX");
            }
            }else{
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Dunning Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
        }
    }




    @Scheduled(cron = "${cronjobtimeforupdatecustomeranditsservice}}")
    public void updateCustomerScheduler() throws InterruptedException {
        log.info("XXXXXXXXXXXX----------CRON Update Customer And Service Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.UPDATE_CUSTOMER_AND_SERVICE_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.UPDATE_CUSTOMER_AND_SERVICE)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.UPDATE_CUSTOMER_AND_SERVICE);
            try {
                Thread thread = new Thread(customerThreadService.newRunnable(), "T1");
                thread.run();

                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Update Customer And Service Scheduler Run Success");
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
                schedulerLockService.releaseSchedulerLock(CronjobConst.UPDATE_CUSTOMER_AND_SERVICE);
                log.info("XXXXXXXXXXXX---------- Update Customer And Service Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Update Customer And Service Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Update Customer And Service Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }

    @Scheduled(cron = "${cronjobtimeforconnectiongenerate}")
    public void ConnectionNumberGanrate() throws InterruptedException {
        log.info("XXXXXXXXXXXX----------CRON TIME_FOR_CONNECTION_NUMBER_GENERATE Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.TIME_FOR_PLAN_CONNECTION_NUMBER_GENERATE_SCHEDULER);

        if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_CONNECTION_NUMBER_GENERATE)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_CONNECTION_NUMBER_GENERATE);
            try {
                List<ConnectionNumberDto> customers = customerServiceMappingRepository.findCustomersWithNullOrEmptyConnectionNumber();
                for (ConnectionNumberDto row : customers) {
                    Integer mappingId = row.getCustServiceMappingId();
                    Integer mvnoId = row.getMvnoId();
                    Integer partnerId = (row.getPartnerId() != null) ? row.getPartnerId() : null;
                    Integer lcoId = customersRepository.findLcoIdByCustId(row.getCustomerId());
                    String mvnoName = row.getMvnaoName();

                    boolean isLCO = lcoId != null;
                    String newConnectionNo = numberSequenceUtil.getConnectionNumberGenerate(isLCO, partnerId, mvnoId,mvnoName);
                    log.info("Generating connection number for mappingId: {}, mvnoId: {}, partnerId: {}", mappingId, mvnoId, partnerId);
                    customerServiceMappingRepository.updateConnectionNumberByMappingId(mappingId, newConnectionNo, partnerId, mvnoId);
                    createDataSharedService.sendGeneratedConnectionNumber(mappingId, row.getCustomerId(), newConnectionNo, partnerId, mvnoId);
                }
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Connection Number Generate Scheduler Run Success");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(customers.size());
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error("**********Scheduler Showing ERROR***********");
                log.error(ex.toString(), ex);
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_CONNECTION_NUMBER_GENERATE);
                log.info("XXXXXXXXXXXX---------- Update Customer And Service Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Connection Number Generate Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Connection Number Generate Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }

    @Scheduled( cron = "${cronjobforServiceHold}")
    @Transactional
    public void holdService(){
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        System.out.println(".................Service Hold Scheduler Started....................");
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.SERVICE_HOLD_SERVICE_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_CUSTOMER_SERVICE_HOLD)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_CUSTOMER_SERVICE_HOLD);
            try {
                if (LocalDate.now().getDayOfMonth() == 1) {
                    customerServiceMappingRepository.resetServiceHoldCount();
                }
                List<CustomerServiceMapping> customerServiceMappingList = customerServiceMappingRepository.findAllByStatus(CommonConstants.WORKFLOW_AUDIT_STATUS.INPROGRESS);
                List<Integer> custserviceMappingIds = new ArrayList<>();
                Map<Integer, Long> idToDateDiffMap = new HashMap<>();
                if (customerServiceMappingList.size() > 0) {
                    System.out.println(".................Service Hold Started....................");
                    customerServiceMappingList.forEach(customerServiceMapping -> {
                        customerServiceMapping.setStatus(CommonConstants.CUSTOMER_STATUS_HOLD);
                        customerServiceMapping.setPreviousStatus(CommonConstants.CUSTOMER_STATUS_HOLD);
                        customerServiceMapping.setServiceHoldDate(LocalDateTime.now());
                        custserviceMappingIds.add(customerServiceMapping.getId());
                        idToDateDiffMap.put(customerServiceMapping.getId(), Long.valueOf(ChronoUnit.DAYS.between(customerServiceMapping.getServiceHoldDate(), customerServiceMapping.getServiceResumeDate())));
                    });
                    LocalDateTime startTime=LocalDateTime.now();
                    List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingIdsIn(custserviceMappingIds);
                    LocalDateTime endTime=LocalDateTime.now();
                    Duration duration=Duration.between(startTime,endTime);
                    System.out.println(".................Total Time took fetch custplanmapping " + duration.toMillis() + "....................");
                  List<Integer> custplanmappingIds=new ArrayList<>();
                    custPlanMapppingList.forEach(custplanmapping -> {
                        custplanmappingIds.add(custplanmapping.getId());
                        custplanmapping.setStatus(CommonConstants.CUSTOMER_STATUS_HOLD);
                        custplanmapping.setCustPlanStatus(CommonConstants.CUSTOMER_STATUS_HOLD);
                        DeactivatePlanReqModel pojo = new DeactivatePlanReqModel();
                        pojo.setCustServiceMappingId(custplanmapping.getCustServiceMappingId());
                        pojo.setCprId(custplanmapping.getId());
                        subscriberService.saveserviceAudit(custplanmapping, pojo, StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD);
                    });
                    System.out.println(".................Sevice Hold Ended  for " + custserviceMappingIds.toString() + "....................");
                    custPlanMappingService.changeStatusOfCustServices(custserviceMappingIds, StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD, "Service Resume ", false);
//                    custPlanMappingRepository.saveAll(custPlanMapppingList);
                    custPlanMappingRepository.updateStatus(CommonConstants.CUSTOMER_STATUS_HOLD,custplanmappingIds);
                    customerServiceMappingRepository.updateServiceStatus(custserviceMappingIds,StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD);
                    System.out.println(".................Service Hold  Ended  for " + custserviceMappingIds.toString() + "....................");
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
                e.printStackTrace();
            }finally {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_CUSTOMER_SERVICE_HOLD);
                log.info("XXXXXXXXXXXX---------- Service Hold Scheduler Locked Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Service Hold Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Service Hold Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }
}

