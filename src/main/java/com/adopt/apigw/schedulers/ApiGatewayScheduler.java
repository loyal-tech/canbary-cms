package com.adopt.apigw.schedulers;

import com.adopt.apigw.constants.CaseConstants;
import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.kafka.KafkaConstant;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.Branch.repository.BranchRepository;
import com.adopt.apigw.modules.CronJobs.CronjobConst;
import com.adopt.apigw.modules.DebitDocumentInventoryRel.DebitDocNumberMappingPojo;
import com.adopt.apigw.modules.DebitDocumentInventoryRel.DebitDocumentInventoryRelRepository;
import com.adopt.apigw.modules.DunningHistory.domain.DunningHistory;
import com.adopt.apigw.modules.DunningHistory.repository.DunningHistoryRepository;
import com.adopt.apigw.modules.DunningRuleBranchMapping.repository.DunningRuleBranchMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingService;
import com.adopt.apigw.modules.InventoryManagement.item.Item;
import com.adopt.apigw.modules.InventoryManagement.item.ItemRepository;
import com.adopt.apigw.modules.InventoryManagement.item.ItemServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMapping;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMappingRepository;
import com.adopt.apigw.modules.Matrix.domain.Matrix;
import com.adopt.apigw.modules.Matrix.domain.MatrixDetails;
import com.adopt.apigw.modules.Matrix.domain.QTatMatrixWorkFlowDetails;
import com.adopt.apigw.modules.Matrix.domain.TatMatrixWorkFlowDetails;
import com.adopt.apigw.modules.Matrix.repository.MatrixRepository;
import com.adopt.apigw.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.Mvno.service.MvnoService;
import com.adopt.apigw.modules.RvenueClient.RevenueClient;
import com.adopt.apigw.modules.Teams.repository.HierarchyRepository;
import com.adopt.apigw.modules.Teams.service.TeamsService;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.customerDocDetails.domain.CustomerDocDetails;
import com.adopt.apigw.modules.mvnoDocDetails.domain.MvnoDocDetails;
import com.adopt.apigw.modules.mvnoDocDetails.service.DocDetailsService;
import com.adopt.apigw.modules.partnerdocDetails.domain.PartnerdocDetails;
import com.adopt.apigw.modules.planUpdate.repository.CustomerPackageRepository;
import com.adopt.apigw.modules.subscriber.model.Constants;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqModel;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.modules.tickets.domain.Case;
import com.adopt.apigw.modules.tickets.repository.CaseRepository;
import com.adopt.apigw.pojo.api.CreditDebitDataPojo;
import com.adopt.apigw.pojo.api.CreditDebitMappingPojo;
import com.adopt.apigw.pojo.api.DunningRuleActionPojo;
import com.adopt.apigw.pojo.api.DunningRulePojo;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.*;
import com.adopt.apigw.repository.common.ShorterRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.schedulerAudit.SchedulerAudit;
import com.adopt.apigw.schedulerAudit.SchedulerAuditService;
import com.adopt.apigw.service.SchedulerLockService;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.EzBillServiceUtility;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.postpaid.*;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.StatusConstants;
import com.adopt.apigw.utils.TatUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import feign.RetryableException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.Key;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
@Slf4j

@Component
@ConditionalOnProperty(name = "spring.enable.scheduling")
public class ApiGatewayScheduler {

    @Autowired
    DebitDocumentTAXRelRepository relRepository;

    @Autowired
    DebitDocRepository debitDocRepository;
    @Autowired
    CustomersService customersService;

    @Autowired
    CustomerLedgerDtlsService customerLedgerDtlsService;

    @Autowired
    NotificationTemplateRepository templateRepository;

    @Autowired
    DunningRuleRepository dunningRuleRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ClientServiceSrv clientService;

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    DunningRuleService dunningRuleService;

    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    DebitDocService debitDocService;

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    PartnerAuditHistoryService auditHistoryService;

    @Autowired
    private PartnerAuditHistoryRepository auditHistoryRepository;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private TatMatrixWorkFlowDetailsRepo tatMatrixWorkFlowDetailsRepo;

    @Autowired
    private TatUtils tatUtils;

    @Autowired
    private HierarchyRepository hierarchyRepository;

    @Autowired
    private TeamsService teamsService;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemWarrantyMappingRepository itemWarrantyMappingRepository;

    @Autowired
    private ItemServiceImpl itemService;
    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    EzBillServiceUtility ezBillServiceUtility;
    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;
//    @Autowired
//    private CaseService caseService;

    @Autowired
    CustomerServiceMappingRepository customerServiceMappingRepository;
    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private StaffUserRepository staffUserRepository;

    public static final String TAT_BREACHED_REMIDER_TIME_NAME = "tatBreachedReminderTime";
    public static final String TAT_OVERDUE_REMIDER_TIME_NAME = "tatOverDueReminderTime";

    @Autowired
    private CustomerPackageRepository customerPackageRepository;

    @Autowired
    private DunningHistoryRepository dunningHistoryRepository;

    @Autowired
    private DunningRuleBranchMappingRepository dunningRuleBranchMappingRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private PlanServiceRepository planServiceRepository;

    @Autowired
    private CustPlanMappingService custPlanMappingService;

    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    private DebitDocumentInventoryRelRepository debitDocumentInventoryRelRepository;

    @Autowired
    @Lazy
    private CustomerInventoryMappingService customerInventoryMappingService;
    @Autowired
    private DocDetailsService mvnoDocDetailsService;

    @Autowired
    private MatrixRepository matrixRepository;

    @Autowired
    private MvnoService mvnoService;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private  RevenueClient revenueClient;

    @Autowired
    private ShorterRepository shorterRepository;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    @Value("${auto-assign-username: superadmin}")
    private String autoAssignUsername;

    @Value("${auto-assign-password: superadmin@2021@SUPERADMIN}")
    private String autoAssignPasswod;
    private static final Integer BATCH_SIZE=100;
    //private static final Logger log = LoggerFactory.getLogger(ApiGatewayScheduler.class);

    @Async
    @Scheduled(cron = "${cronJobTimeForTatMatrix}")
    public void runSchedulerForTAT() {
        System.out.println("***** -------------Adopt Api gateway scheduler  for tat matrix action  Starting----------------- *****");
        log.info("XXXXXXXXXXXX----------CRON TIME_FOR_TAT_MATRIX_SCHEDULER START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.TIME_FOR_TAT_MATRIX_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_TAT_MATRIX)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_TAT_MATRIX);
            try {
                List<GrantedAuthority> role_name = new ArrayList<>();
                role_name.add(new SimpleGrantedAuthority("ADMIN"));
                LoggedInUser user = new LoggedInUser("admin", "admin@123", true, true, true, true, role_name, "admin", "admin", LocalDateTime.now(), 2, 1, "ADMIN", null, 2, null, 2, new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(), "admin", null, null, null);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(auth);

                List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetailsList = tatMatrixWorkFlowDetailsRepo.findAllByIsActive(Boolean.TRUE);

                //filtering unwanted tickets which are closed or the tat has already gone
//        for (int i = 0; i < tatMatrixWorkFlowDetailsList.size(); i++) {
//            Case checkCase = caseRepository.findById(tatMatrixWorkFlowDetailsList.get(i).getEntityId().longValue()).orElse(null);
//            if (!checkCase.getCaseStatus().equalsIgnoreCase("Closed") || !checkCase.getCaseStatus().equalsIgnoreCase("Raise And Close")) {
//                LocalDateTime endDate = calculateTatEndDate(tatMatrixWorkFlowDetailsList.get(i));
//                if(endDate.isBefore(LocalDateTime.now())){
//                    tatMatrixWorkFlowDetailsList.get(i).setIsActive(false);
//                    tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetailsList.get(i));
//                }
//
//            }
//        }
                List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findAllByIsActive(Boolean.TRUE);
                LocalDateTime currentDateTime = LocalDateTime.now();

                if (!CollectionUtils.isEmpty(tatMatrixWorkFlowDetails)) {
                    for (TatMatrixWorkFlowDetails details : tatMatrixWorkFlowDetails) {
                        Matrix matrix = matrixRepository.findById(details.getTatMatrixId()).get();
                        Optional<MatrixDetails> newMatrixDetails = matrix.getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted() && dtl.getOrderNo().equals(details.getOrderNo())).findFirst();
//                TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails1=tatMatrixWorkFlowDetailsRepo.findAllByWorkFlowIdAndEntityIdAndEventNameAndIsActive()
                        if (newMatrixDetails.isPresent()) {
                            StaffUser staffUser = new StaffUser();

                            staffUser = staffUserRepository.findById(details.getStaffId()).orElse(null);
                            StaffUser parentStaffUser = new StaffUser();
                            if (details.getParentId() != null) {
                                parentStaffUser = staffUserRepository.findById(details.getParentId()).orElse(null);
                            }

                            details.setAction(newMatrixDetails.get().getAction());
                            LocalTime time = LocalTime.now();
                            time = LocalTime.of(time.getHour(), time.getMinute());
                            LocalDateTime endDateTime = null;
//                if (details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
//                    Case casedataa = caseService.getRepository().findById(details.getEntityId().longValue()).orElse(null);
//                    // if (casedataa.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)) {
////                        LocalDate date = LocalDate.now();
////                        LocalDateTime localDateTime = LocalDateTime.of(casedataa.getNextFollowupDate(), casedataa.getNextFollowupTime());
////                        if (date.isEqual(casedataa.getNextFollowupDate())) {
////                            if (time.equals(casedataa.getNextFollowupTime()) || time.isBefore(casedataa.getNextFollowupTime())) {
////                                endDateTime = LocalDateTime.of(casedataa.getNextFollowupDate(), casedataa.getNextFollowupTime());
////                            }
////                        } else if (date.isAfter(casedataa.getNextFollowupDate())) {
////                            details.setIsActive(false);
////                            tatMatrixWorkFlowDetailsRepo.save(details);
////                        }
////                    }
//                    //else {
//                    endDateTime = calculateTatEndDate(details);
//                    // }
//                    //calculate reminder time and send notification before tat breach
//                    if (endDateTime != null) {
//                        LocalDateTime reminderTime = calculateTatBreachedReminderTime(endDateTime).truncatedTo(ChronoUnit.MINUTES);
//                        System.out.println("---------------------- Reminder Time ------------------------" + reminderTime + "LOCALDATE_TIME -> " + LocalDateTime.now());
//                        if (reminderTime.truncatedTo(ChronoUnit.MINUTES).equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)) || reminderTime.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES).equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)) || reminderTime.minusMinutes(1).truncatedTo(ChronoUnit.MINUTES).equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
//                            System.out.println("------------------- TAT_BREACH REMINDER INTIT---------------------------");
//                            if(parentStaffUser!=null){
//                                sendReminderforTat(parentStaffUser.getPhone(), parentStaffUser.getEmail(), staffUser.getUsername(), parentStaffUser.getUsername(), casedataa.getCaseNumber(), parentStaffUser.getMvnoId());
//                            }
//                            System.out.println("------------------------ TAT_BREACH REMINDER END-----------------------------");
//                        }
//                        details.setNextFollowUpDate(endDateTime);
//                        calculatedate(endDateTime, details, currentDateTime);
//                    }
//                } else {
                            endDateTime = calculateTatEndDate(details);
                            details.setNextFollowUpDate(endDateTime);
                            calculatedate(endDateTime, details, currentDateTime);
//                }

//                if (endDateTime != null) {
//
//                    if (endDateTime.equals(currentDateTime) || endDateTime.isBefore(currentDateTime)) {
//                        switch (details.getAction()) {
//                            case CommonConstants.TICKET_ACTION.BOTH:
//                                tatUtils.assignToNextApprovalStaff(details);
//                                if (details.getParentId() != null)
//                                    tatUtils.sendNotificationToStaff(details);
//                                break;
//                            case CommonConstants.TICKET_ACTION.REASSIGN:
//                                tatUtils.assignToNextApprovalStaff(details);
//                                break;
//                            case CommonConstants.TICKET_ACTION.NOTIFICATION:
//                                if (details.getParentId() != null)
//                                    tatUtils.sendNotificationToStaff(details);
//                                break;
//                        }
//                    }
//                }
                        }
                    }
                }
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Tat Matrix Scheduler Run Success");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(null);
                System.out.println("***** Adopt Api gateway scheduler  for tat matrix action  End *****");
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_TAT_MATRIX);
                log.info("XXXXXXXXXXXX---------- Tat Matrix Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Tat Matrix Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Tat Matrix Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }

    private void calculatedate(LocalDateTime endDateTime, TatMatrixWorkFlowDetails details, LocalDateTime currentDateTime) {
        if (endDateTime != null) {

            if (endDateTime.truncatedTo(ChronoUnit.MINUTES).equals(currentDateTime.truncatedTo(ChronoUnit.MINUTES)) || endDateTime.truncatedTo(ChronoUnit.MINUTES).isBefore(currentDateTime.truncatedTo(ChronoUnit.MINUTES))) {
                switch (details.getAction()) {
                    case CommonConstants.TICKET_ACTION.BOTH:
                        tatUtils.assignToNextApprovalStaff(details);
//                        tatUtils.sendNotificationToStaff(details);
                        break;
                    case CommonConstants.TICKET_ACTION.REASSIGN:
                        tatUtils.assignToNextApprovalStaff(details);
                        break;
                    case CommonConstants.TICKET_ACTION.NOTIFICATION:
                        tatUtils.sendNotificationToStaff(details);
                        break;
                }
            }
        }
    }

//    @Async
//    @Scheduled(cron = "${cronJobTimeForDunning}")
//    public void runSchedulerForDunning() throws Exception {
//        List<GrantedAuthority> role_name = new ArrayList<>();
//        ApplicationLogger.logger.debug("***** Api gateway scheduler  for dunning Starting *****");
//        role_name.add(new SimpleGrantedAuthority("ADMIN"));
//        LoggedInUser user = new LoggedInUser(autoAssignUsername, autoAssignPasswod, true, true, true, true, role_name, "c", autoAssignUsername, LocalDateTime.now(), 1, 1, "ADMIN", null, 1, null, 1, new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),autoAssignUsername,null,null,null);
//        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
//        SecurityContextHolder.getContext().setAuthentication(auth);
//        ApplicationLogger.logger.debug("***** Adopt Api gateway scheduler  for dunning Starting *****");
//        System.out.println("********* Adopt Api gateway schedular for dunning started*****");
//        String token=  GenerateTokenUsingLoggedInUser(getLoggedInUser());
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<DebitDocNumberMappingPojo> pojos=new ArrayList<>();
//        try {
//            pojos=revenueClient.getDebitDocNumber(token);
//            if (pojos.size()>0) {
//                System.out.println("Invoic List size"+pojos.size());
//            }else{
//                System.out.println("No Invoice Found");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        //sendSmsNotificationForDunningCustomer();
////        if(!CollectionUtils.isEmpty(pojos)){
//            sendDunningNotification(pojos,token);
////        }
//    }


    @Scheduled(cron = "${cronJobTimeForAutomatePayment}")
    void AutomatePaymentAdjust() {
        log.info("XXXXXXXXXXXX----------CRON TIME_FOR_AUTOMATE_PAYMENT_SCHEDULER START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.TIME_FOR_AUTOMATE_PAYMENT_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_AUTOMATE_PAYMENT)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_AUTOMATE_PAYMENT);
            try {
                // List<Customers> customersList = new ArrayList<>();
                Long creditDocumentCount = null;
                QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
                JPAQuery<CreditDocument> query = new JPAQuery<>(entityManager);
                creditDocumentCount = query
                        .from(qCreditDocument)
                        .where(qCreditDocument.adjustedAmount.lt(qCreditDocument.amount)
                                .and(qCreditDocument.paytype.eq(Constants.ADVANCE)))
//                .orderBy(qCreditDocument.customer.id.asc())
                        .fetchCount();
                Long pageSize = (creditDocumentCount / 100);
                for (int i = 0; i <= pageSize; i++) {
                    //Pageable pageable = PageRequest.of(i, 100, Sort.by(Sort.Direction.ASC, "id"));

                    Pageable pageable = PageRequest.of(i, 100);
                    //List<Integer> custmerId = new ArrayList<>();
                    BooleanExpression credBoolexp = qCreditDocument.isNotNull().and(qCreditDocument.adjustedAmount.lt(qCreditDocument.amount)).and(qCreditDocument.paytype.eq("advance"));
//         creditDocumentList
                    Page<CreditDocument> response = creditDocRepository.findAll(credBoolexp, pageable);
                    List<CreditDocument> list = response.getContent();
                    list.forEach(creditDocument -> {
                        CreditDebitMappingPojo creditDebitMappingPojo = new CreditDebitMappingPojo();
                        CreditDebitDataPojo creditDebitDataPojo = new CreditDebitDataPojo();
                        List<CreditDebitDataPojo> creditDebitDataPojoList = new ArrayList<>();
                        List<DebitDocument> debitDocumentList = debitDocRepository.findAllByCustomer(creditDocument.getCustomer());
                        debitDocumentList.stream().filter(debitDocument -> !debitDocument.getBillrunstatus().equalsIgnoreCase("Cancelled") && !debitDocument.getBillrunstatus().equalsIgnoreCase("Void")).forEach(debitDocument -> {
                            if (debitDocument.getAdjustedAmount() == null) {
                                debitDocument.setAdjustedAmount(0.00);
                            }
                            if (debitDocument.getAdjustedAmount() < debitDocument.getTotalamount()) {
                                creditDebitMappingPojo.setInvoiceId(debitDocument.getId());
                                creditDebitDataPojo.setAmount(creditDocument.getAmount());
                                creditDebitDataPojo.setId(creditDocument.getId());
                                creditDebitDataPojoList.add(creditDebitDataPojo);
                                creditDebitMappingPojo.setCreditDocumentList(creditDebitDataPojoList);
                                try {
                                    debitDocService.InvoicePaymentDone(creditDebitMappingPojo);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }

                        });

                    });
                    schedulerAudit.setEndTime(LocalDateTime.now());
                    schedulerAudit.setDescription("Automate Payment Scheduler Run Success");
                    schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                    schedulerAudit.setTotalCount(list.size());
//        BooleanExpression debBoolexp = qDebitDocument.isNotNull().and(qDebitDocument.adjustedAmount.lt(qDebitDocument.totalamount));
//        debitDocumentList = (List<DebitDocument>) debitDocRepository.findAll(debBoolexp);
//        List<CreditDocument> finalCreditDocumentList = creditDocumentList;
//        debitDocumentList.forEach(debitDocument -> {
//            for (Integer custId : custmerId) {
//                if (custId.equals(debitDocument.getCustomer().getId())) {
//                    creditDebitMappingPojo.setInvoiceId(debitDocument.getId());
//                    creditDebitDataPojo.setAmount(finalCreditDocumentList.get(custId).getAmount());
//                    creditDebitDataPojo.setId(finalCreditDocumentList.get(custId).getId());
//                    creditDebitDataPojoList.add(creditDebitDataPojo);
//                    creditDebitMappingPojo.setCreditDocumentList(creditDebitDataPojoList);
//                }
//                //creditDebitMappingPojoList.add(creditDebitMappingPojo);
//                try {
//                    debitDocService.InvoicePaymentDone(creditDebitMappingPojo);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//        });


//        BooleanExpression debbooleanExpression = QDebitDocument.debitDocument.isNotNull().and(qDebitDocument.adjustedAmount.ne(qDebitDocument.totalamount)).and(qDebitDocument.adjustedAmount.lt(qDebitDocument.totalamount));
//        debitDocumentList = (List<DebitDocument>) debitDocRepository.findAll(debbooleanExpression);
//        for (int i = 0; i < debitDocumentList.size(); i++) {
//            BooleanExpression credbooleanExpression = QCreditDocument.creditDocument.isNotNull().and(qCreditDocument.customer.eq(debitDocumentList.get(i).getCustomer()).and(qCreditDocument.adjustedAmount.lt(qCreditDocument.amount)));
//            creditDocumentList = (List<CreditDocument>) creditDocRepository.findAll(credbooleanExpression);
//            creditDebitMappingPojo.setInvoiceId(debitDocumentList.get(i).getId());
//            for (int j = 0; j < creditDocumentList.size(); j++) {
//                creditDebitDataPojo.setId(creditDocumentList.get(j).getId());
//                creditDebitDataPojo.setAmount(creditDocumentList.get(j).getAmount());
//                creditDebitDataPojoList.add(creditDebitDataPojo);
//            }
//            creditDebitMappingPojo.setCreditDocumentList(creditDebitDataPojoList);
//            try {
//                debitDocService.InvoicePaymentDone(creditDebitMappingPojo);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }

                }
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_AUTOMATE_PAYMENT);
                log.info("XXXXXXXXXXXX---------- Automate Payment Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Automate Payment Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Automate Payment Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }


    @Scheduled(cron = "${cronJobTimeForPartnerCount}")
    public void runSchedulerForPartnerCount() {
        log.info("XXXXXXXXXXXX----------CRON TIME_FOR_PARTNER_COUNT_SCHEDULER START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.TIME_FOR_PARTNER_COUNT_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_PARTNER_COUNT)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_PARTNER_COUNT);
            try {
                ApplicationLogger.logger.debug("***** Adopt Api gateway scheduler  for partner count Starting *****");
                updatePartnerCount();
                ApplicationLogger.logger.debug("***** Api gateway scheduler  for partner count Starting *****");

                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Partner Count Scheduler Run Success");
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
                schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_PARTNER_COUNT);
                log.info("XXXXXXXXXXXX---------- Time For Partner Count Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Time For Partner Count Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Time For Partner Count Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }

//    Scheduler for activating service in EZbill

//    @Scheduled(cron = "${cronJobTimeForActivateServiceInEZBill}")
//    public void runSchedulerForActivateServiceInEZBill() {
//        ApplicationLogger.logger.debug("***** Adopt Api gateway scheduler  for Activating service in ezBill  Starting *****");
//        activateServiceInEZBill();
//        ApplicationLogger.logger.debug("***** Api gateway scheduler  for  Activating service  in ezBill  Starting *****");
//    }


    public void updatePartnerCount() {
        QPartner qPartner = QPartner.partner;
        LocalDate currenDate = LocalDate.now();
        BooleanExpression expression = qPartner.isNotNull().and(qPartner.isDelete.eq(false)).and(qPartner.status.eq("ACTIVE"))
                .and(qPartner.resetDate.eq(LocalDate.now()));
        List<Partner> partnerList = (List<Partner>) partnerRepository.findAll(expression);
        if (!CollectionUtils.isEmpty(partnerList)) {
            List<PartnerAuditHistory> partnerAuditHistories = new ArrayList<>();
            for (Partner partner : partnerList) {
                PartnerAuditHistory partnerAuditHistory = new PartnerAuditHistory();
                partnerAuditHistory.setPartnerId(partner.getId());
                if (partner.getLastbilldate() != null)
                    partnerAuditHistory.setLastAuditdate(partner.getLastbilldate().atTime(LocalTime.now()));
                else
                    partnerAuditHistory.setLastAuditdate(partner.getCreatedate());
                partnerAuditHistory.setCreatedate(LocalDateTime.now());
                partnerAuditHistory.setNewCustomerCount(partner.getNewCustomerCount());
                partnerAuditHistory.setRenewCustomerCount(partner.getRenewCustomerCount());
                partnerAuditHistory.setTotalCustomerCount(partner.getTotalCustomerCount());
                partnerAuditHistory.setMvnoId(partner.getMvnoId());
                partnerAuditHistory.setBuId(partner.getBuId());
                partnerAuditHistory.setPartnerName(partner.getName());
                partnerAuditHistories.add(partnerAuditHistory);
                partner.setNewCustomerCount(0l);
                partner.setRenewCustomerCount(0l);
                partner.setTotalCustomerCount(0l);
                LocalDate resetDate = partnerService.getResetDate(partner.getCalendarType(), currenDate);
                if (resetDate.equals(currenDate))
                    resetDate.plusMonths(1);
                partner.setResetDate(resetDate);
                partner.setLastbilldate(LocalDate.now());
            }
            if (!CollectionUtils.isEmpty(partnerAuditHistories))
                auditHistoryRepository.saveAll(partnerAuditHistories);
            partnerRepository.saveAll(partnerList);
        }
    }

    @Scheduled(cron = "${cronJobTimeForwarrentydays}")
    public void getitemwarrantyUpdatation() throws Exception {
        log.info("XXXXXXXXXXXX----------CRON TIME_FOR_WARRANTY_DAYS_SCHEDULER START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.TIME_FOR_WARRANTY_DAYS_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_WARRANTY_DAYS)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_WARRANTY_DAYS);
            try {
                List<Item> itemList = itemRepository.findBywarranty();
                itemList.stream().forEach(r -> {
                    LocalDateTime expDate = r.getExpireDate();
                    LocalDateTime currentDate = LocalDateTime.now();
                    if (r.getExpireDate() != null && currentDate != null) {
                        Duration duration = Duration.between(currentDate, expDate);
                        if (duration.toDays() == 0L) {
                            r.setWarranty("Expired");
                            r.setRemainingDays(null);
                            itemRepository.save(r);
                            List<ItemWarrantyMapping> itemWarrantyMapping = itemWarrantyMappingRepository.findByItemId(r.getId());
                            if (!(itemWarrantyMapping.isEmpty())) {
                                ItemWarrantyMapping itemWarrantyMapping1 = itemWarrantyMapping.get(itemWarrantyMapping.size() - 1);
                                if (itemWarrantyMapping1.getWarranty().equalsIgnoreCase("InWarranty"))
                                    itemWarrantyMapping1.setWarranty("Expired");
                                itemWarrantyMappingRepository.save(itemWarrantyMapping1);
                            }
                        } else {
                            r.setRemainingDays(String.valueOf(duration.toDays()));
                            itemRepository.save(r);
                        }
                    }
                });
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Warranty days Scheduler Run Success");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(itemList.size());
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_WARRANTY_DAYS);
                log.info("XXXXXXXXXXXX---------- Warranty days Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Warranty days Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Warranty days Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }

    }

    void sendDunningNotification(List<DebitDocNumberMappingPojo> numberMappingPojos,String token) throws Exception {
        List<DunningRule> dunningRuleList = dunningRuleRepository.findAllByStatus();
        List<DunningRulePojo> dunningRulePojoList = new ArrayList<>();
//        if (dunningRuleList.size() > 0) {
//           dunningRulePojoList = dunningRuleList.forEach().map(data -> {
//                try {
//                    return dunningRuleService.convertDunningRuleModelToDunningRulePojo(data);
//                } catch (NoSuchFieldException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//                return null;
//            }).collect(Collectors.toList());
        for(DunningRule dunningRule:dunningRuleList) {
                dunningRulePojoList.add(dunningRuleService.convertDunningRuleModelToDunningRulePojo(dunningRule));
        }

            for (DunningRulePojo dunningRule : dunningRulePojoList) {
                String type = dunningRule.getCustomerType();
                String category = dunningRule.getCreditclass();
                String dunningType = dunningRule.getDunningType();
                String dunningSector = dunningRule.getDunningSector();
                String customerPayType = dunningRule.getCustomerPayType();
                List<Long> branchIds = dunningRuleBranchMappingRepository.findAllBranchIdByDunningId(dunningRule.getId()).stream().filter(aLong -> aLong != null).collect(Collectors.toList());
                List<Long> partnerIds = dunningRuleBranchMappingRepository.findAllPartnerIdByDunningId(dunningRule.getId()).stream().filter(aLong -> aLong != null).collect(Collectors.toList());
                String ccEmail = dunningRule.getCcemail();
                String ccMobile = dunningRule.getMobile();
                switch (dunningType) {
                    case CommonConstants.DUNNING_TYPE_PAYMENT:
                        for (DunningRuleActionPojo action : dunningRule.getDunningRuleActionPojoList()) {
                            switch (action.getAction()) {
                                case CommonConstants.DUNNING_ACTION_TYPE_EMAIL:
                                    sendEmailNotificationForDunning(
                                            customersService.getCustomersForDunning(type, dunningSector, category, action.getDays(), customerPayType, branchIds, partnerIds), ccEmail ,action.getDays(),dunningRule.getIsGeneratepaymentLink(), token);
                                    break;
                                case CommonConstants.DUNNING_ACTION_TYPE_SMS:
                                    sendSMSNotificationForDunning(
                                            customersService.getCustomersForDunning(type, dunningSector, category, action.getDays(), customerPayType, branchIds, partnerIds), ccMobile , action.getDays(),dunningRule.getIsGeneratepaymentLink(),token);
                                    break;
                                case CommonConstants.DUNNING_ACTION_TYPE_DEACTIVATION:
                                    sendDeactivationNotificationForDunning(
                                            customersService.getCustomersForDunning(type, dunningSector, category, action.getDays(), customerPayType, branchIds, partnerIds), action.getDays(), ccEmail, ccMobile);
                                    break;

                                default:
                                    break;
                            }
                        }
                        break;
                    case CommonConstants.DUNNING_TYPE_ADVANCENOTIFICATION: {
                        for (DunningRuleActionPojo action : dunningRule.getDunningRuleActionPojoList()) {
                            switch (action.getAction()) {
                                case CommonConstants.DUNNING_ACTION_TYPE_EMAIL:
                                    sendEmailNotificationForAdvance(
                                            customersService.getAdvanceNotificationForDunning(type, dunningSector, category, action.getDays(), customerPayType, branchIds, partnerIds), action.getDays(), ccEmail,token,dunningRule.getIsGeneratepaymentLink());
                                    break;
                                case CommonConstants.DUNNING_ACTION_TYPE_SMS:
                                    sendSMSNotificationForDunningAdvance(
                                            customersService.getAdvanceNotificationForDunning(type, dunningSector, category, action.getDays(), customerPayType, branchIds, partnerIds), action.getDays(), ccMobile,token,dunningRule.getIsGeneratepaymentLink());
                                    break;
                                case CommonConstants.DUNNING_ACTION_TYPE_DEACTIVATION:
                                    sendDeactivationAdvanceNotificationForDunning(
                                            customersService.getAdvanceNotificationForDunning(type, dunningSector, category, action.getDays(), customerPayType, branchIds, partnerIds), action.getDays(), ccMobile, ccEmail);
                                    break;

                                default:
                                    break;
                            }
                        }

                    }
                    break;
                    case CommonConstants.DUNNING_TYPE_DOCUMENT: {

                        for (DunningRuleActionPojo action : dunningRule.getDunningRuleActionPojoList()) {
                            switch (action.getAction()) {
                                case CommonConstants.DUNNING_ACTION_TYPE_EMAIL:
                                    sendEmailNotificationForStaffDunning(
                                            customersService.getDocumentForDunning(action.getDays()), branchIds, partnerIds, ccEmail);
                                    break;
                                case CommonConstants.DUNNING_ACTION_TYPE_SMS:
                                    sendSMSNotificationForStaffDunning(customersService.getDocumentForDunning(action.getDays()), branchIds, partnerIds, ccMobile);
                                    break;
                                case CommonConstants.DUNNING_ACTION_TYPE_DEACTIVATION:
                                    sendDeactivationNotificationForStaffDunning(
                                            customersService.getDocumentForDunning(action.getDays()), branchIds, partnerIds, ccMobile, ccEmail);
                                    break;

                                default:
                                    break;
                            }
                        }
                    }
                    break;
                    case CommonConstants.DUNNING_TYPE_PARTNER_DOCUMENT: {
                        for (DunningRuleActionPojo action : dunningRule.getDunningRuleActionPojoList()) {
                            switch (action.getAction()) {
                                case CommonConstants.DUNNING_ACTION_TYPE_EMAIL:
                                    sendEmailNotificationForStaffPartnerDunning(
                                            customersService.getDocumentForPartnerDunning(action.getDays()), partnerIds, ccEmail);
                                    break;
                                case CommonConstants.DUNNING_ACTION_TYPE_SMS:

                                    sendSMSNotificationForStaffPartnerDunning(customersService.getDocumentForPartnerDunning(action.getDays()), partnerIds, ccMobile);
                                    break;
                                case CommonConstants.DUNNING_ACTION_TYPE_DEACTIVATION:
                                    sendDeactivationNotificationForStaffPartnerDunning(customersService.getDocumentForPartnerDunning(action.getDays()), partnerIds, ccMobile, ccEmail);
                                    break;

                                default:
                                    break;
                            }
                        }
                    }
                    break;
                    case CommonConstants.DUNNING_TYPE_MVNO_DOCUMENT: {
                        for (DunningRuleActionPojo action : dunningRule.getDunningRuleActionPojoList()) {
                            switch (action.getAction()) {
                                case CommonConstants.DUNNING_ACTION_TYPE_EMAIL:
                                    sendMvnoDocumentDunning(mvnoDocDetailsService.getMvnoDocumentforDunning(action.getDays() , dunningRule.getMvnoId()) , dunningRule.getCcemail() , true , false);
                                    break;
                                case CommonConstants.DUNNING_ACTION_TYPE_SMS:
                                    sendMvnoDocumentDunning(mvnoDocDetailsService.getMvnoDocumentforDunning(action.getDays() , dunningRule.getMvnoId()) , dunningRule.getCcemail() , false , true);
                                    break;
                                case CommonConstants.DUNNING_ACTION_TYPE_DEACTIVATION:
                                    sendDeactivationNotificationForMvnoDunning(mvnoDocDetailsService.getMvnoDocumentforDunning(action.getDays() , dunningRule.getMvnoId()),dunningRule.getCcemail() ,true,true);
                                    break;
                                default:
                                    break;
                            }
                        }

                    }
                    break;
                    case CommonConstants.DUNNING_TYPE_MVNO_PAYMENT: {
                        List<Mvno>mvnos=mvnoRepository.findAll();
                        for (DunningRuleActionPojo action : dunningRule.getDunningRuleActionPojoList()) {

                             switch (action.getAction()) {
                                 case CommonConstants.DUNNING_ACTION_TYPE_EMAIL:
                                     sendMvnoPaymentDunning(action.getDays(), dunningRule.getMvnoId(), dunningRule.getCcemail(), true, false,numberMappingPojos);
                                     break;
                                 case CommonConstants.DUNNING_ACTION_TYPE_SMS:
                                     sendMvnoPaymentDunning(action.getDays(), dunningRule.getMvnoId(), dunningRule.getCcemail(), false, true,numberMappingPojos);
                                     break;
                                 case CommonConstants.DUNNING_ACTION_TYPE_DEACTIVATION:
                                     sendDeactivationNotificationForMvnoPaymentDunning(action.getDays(), dunningRule.getMvnoId(), dunningRule.getCcemail(), false, true,numberMappingPojos);
                                     break;
                                 default:
                                     break;
                             }
                         }
                        }
                        break;
                    case CommonConstants.DUNNING_TYPE_MVNO_ADVANCE_NOTIFICATION: {
                        List<Mvno>mvnos=mvnoRepository.findAll();
                        for (DunningRuleActionPojo action : dunningRule.getDunningRuleActionPojoList()) {

                            switch (action.getAction()) {
                                case CommonConstants.DUNNING_ACTION_TYPE_EMAIL:
                                    sendMvnoPaymentAdvanceNotificationDunning(action.getDays(), dunningRule.getMvnoId(), dunningRule.getCcemail(), true, false,numberMappingPojos);
                                    break;
                                case CommonConstants.DUNNING_ACTION_TYPE_SMS:
                                    sendMvnoPaymentAdvanceNotificationDunning(action.getDays(), dunningRule.getMvnoId(), dunningRule.getCcemail(), false, true,numberMappingPojos);
                                    break;
                                case CommonConstants.DUNNING_ACTION_TYPE_DEACTIVATION:
                                    sendDeactivationNotificationForMvnoPaymentDunning(action.getDays(), dunningRule.getMvnoId(), dunningRule.getCcemail(), false, true,numberMappingPojos);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }


            }

    }
    private void sendMvnoPaymentDunning(Integer days, Integer mvnoId, String ccEmail, boolean sendEmail, boolean sendSms,List<DebitDocNumberMappingPojo> numberMappingPojos) {
        List<Mvno> mvnoList = mvnoRepository.findAll();

        mvnoList.forEach(mvno -> {
            if (!processMvnoStaffUser(mvno, days, sendEmail, sendSms, ccEmail,numberMappingPojos)) {
                log.info("No active staff user found or MVNO staff is inactive for MVNO ID: {}", mvno.getId());
            }
        });
    }

    private boolean processMvnoStaffUser(Mvno mvno, Integer days, boolean sendEmail, boolean sendSms, String ccEmail,List<DebitDocNumberMappingPojo>numberMappingPojos) {
        List<StaffUser> staffUsers = staffUserRepository.StatusAndIsDeleteIsFalseAndMvnoIdIn("ACTIVE", Collections.singletonList(mvno.getId().intValue()));
        staffUsers.addAll(staffUserRepository.findStaffidListByMvnoDeativationFlag(Math.toIntExact(mvno.getId())));

        if (staffUsers.isEmpty()) return false;

        StaffUser staffUser = staffUsers.get(0);
        if (staffUser.getIsDelete()) return false;

        processMvnoPaymentReminder(mvno, staffUser, days, sendEmail, sendSms, ccEmail,numberMappingPojos);
        return true;
    }

    private void processMvnoPaymentReminder(Mvno mvno, StaffUser staffUser, Integer days, boolean sendEmail, boolean sendSms, String ccEmail,List<DebitDocNumberMappingPojo>numberMappingPojos) {
        if (mvno.getCustInvoiceRefId() == null) return;

        LocalDateTime adjustedDate = LocalDateTime.now().minusDays(days);
        List<DebitDocumentSummary> debitDocumentSummary = debitDocRepository.findDebitDocumentAndSum( mvno.getCustInvoiceRefId(), adjustedDate);

        if (CollectionUtils.isEmpty(debitDocumentSummary) ) {
            log.info("No DebitDocumentSummary found for MVNO ID: {}", mvno.getId());
            return;
        }

        if (!shouldSendNotification(debitDocumentSummary.get(0), sendEmail, sendSms, staffUser, mvno)) {
            log.info("Email or SMS already sent for DebitDocument ID: {}", debitDocumentSummary.get(0).getDebitDocument().getId());
            return;
        }

        for (DebitDocNumberMappingPojo numberMappingPojo : numberMappingPojos) {
            if (Objects.equals(numberMappingPojo.getDebitdocId(), debitDocumentSummary.get(0).getDebitDocument().getId())) {
                debitDocumentSummary.get(0).getDebitDocument().setDocnumber(numberMappingPojo.getDocnumber());
                debitDocumentSummary.get(0).getDebitDocument().setBillrunstatus(numberMappingPojo.getBillRunStatus());
            }
        }

        debitDocumentSummary=debitDocumentSummary.stream().filter(x->x.getDebitDocument()!=null && x.getDebitDocument().getBillrunstatus()!=null && !x.getDebitDocument().getBillrunstatus().equalsIgnoreCase("Cancelled")).collect(Collectors.toList());
//        for (DebitDocNumberMappingPojo numberMappingPojo : numberMappingPojos) {
//            if (Objects.equals(numberMappingPojo.getDebitdocId(), debitDocumentSummary.get(0).getDebitDocument().getId())) {
//                debitDocumentSummary.get(0).getDebitDocument().setDocnumber(numberMappingPojo.getDocnumber());
//            }
//        }
        if(debitDocumentSummary!=null && !debitDocumentSummary.isEmpty())
            sendPaymentReminderAndAudit(debitDocumentSummary.get(0), staffUser, mvno, ccEmail, sendEmail, sendSms,numberMappingPojos);
    }

    private void sendPaymentReminderAndAudit(DebitDocumentSummary debitDocumentSummary, StaffUser staffUser, Mvno mvno, String ccEmail, boolean sendEmail, boolean sendSms,List<DebitDocNumberMappingPojo> numberMappingPojos) {
        String userName=getLoggedInUser().getFirstName();
        LocalDate dueDate = debitDocumentSummary.getDebitDocument().getDuedate().toLocalDate().plusDays(mvno.getMvnoPaymentDueDays());

        mvnoPaymentReminderNotification(staffUser, debitDocumentSummary.getDebitDocument(), ccEmail, sendEmail, sendSms, dueDate);

        if (sendEmail) {
            dunningAudit("MVNO_PAYMENT_EXPIRE_" + debitDocumentSummary.getDebitDocument().getId(), "Email", Long.valueOf(staffUser.getId()),
                    mvno.getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno Payment email sent", staffUser.getMvnoId());
        }

        if (sendSms) {
            dunningAudit("MVNO_PAYMENT_EXPIRE_" + debitDocumentSummary.getDebitDocument().getId(), "SMS", Long.valueOf(staffUser.getId()),
                    mvno.getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno Payment SMS sent", staffUser.getMvnoId());
        }
    }


    //    private void sendMvnoPaymentDunning(Integer days, Integer mvnoId, String ccemail, boolean sendEmail, boolean sendSms) {
//        List<Mvno>mvnoList= mvnoRepository.findAll();
//        for(Mvno id :mvnoList) {
//           String status = "ACTIVE";
//           List<StaffUser> staffUserList = staffUserRepository.StatusAndIsDeleteIsFalseAndMvnoIdIn(status ,  Collections.singletonList(id.getId().intValue()));
//           if(!staffUserList.isEmpty()){
//               StaffUser staffUser = staffUserList.get(0);
//               if(!staffUser.getIsDelete() ){
//                   boolean flag = true;
//                   if (id.getCustInvoiceRefId()!=null ) {
//                       LocalDateTime adjustedDate = LocalDateTime.now().plusDays(id.getMvnoPaymentDueDays());
//                     DebitDocumentSummary debitDocumentSummary=  debitDocRepository.findDebitDocumentAndSum(id.getId(),id.getCustInvoiceRefId(),adjustedDate);
////                       Double pendingAmount = debitDocService.getTotalAmountDebitDocumentsByMvno(Math.toIntExact(id.getId()), false, days);
////                       List<DebitDocument> lastInvoice = debitDocRepository.lastInvoice(id.getCustInvoiceRefId());
////                       if (!CollectionUtils.isEmpty(lastInvoice)) {
////                           DebitDocument debitDocument = lastInvoice.get(0);
//////                           List<CreditDebitDocMapping> creditDebitDocMappingList = creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
//////                           List<CreditDocument> creditDocument = creditDocRepository.findAllById(Collections.singleton(creditDebitDocMappingList.get(0).getCreditDocId()));
////                           LocalDate duedate = debitDocument.getStartdate().toLocalDate().plusDays(id.getMvnoPaymentDueDays());
////                           if ( LocalDate.now().equals(duedate.plusDays(days)) &&!debitDocument.getPaymentStatus().equalsIgnoreCase("Fully Adjusted")) {
//                               if (sendEmail) {
//                                   flag = getFlagForDunnningDuplicationNew("MVNO_PAYMENT_EXPIRE" + debitDocumentSummary.getDebitDocument().getId(), "Email", Long.valueOf(staffUser.getId()), id.getCustInvoiceRefId(), null, LocalDateTime.now());
//                               }
//                               if (sendSms) {
//                                   flag = getFlagForDunnningDuplicationNew("MVNO_PAYMENT_EXPIRE" + debitDocumentSummary.getDebitDocument().getId(), "SMS", Long.valueOf(staffUser.getId()), id.getCustInvoiceRefId(), null, LocalDateTime.now());
//                               }
//                               if (flag) {
//                                   LocalDate duedate = debitDocumentSummary.getDebitDocument().getDuedate().toLocalDate().plusDays(id.getMvnoPaymentDueDays());
//                                   mvnoPaymentReminderNotification(staffUser, debitDocumentSummary.getDebitDocument(), ccemail, sendEmail, sendSms, duedate);
//                                   if (sendEmail) {
//                                       dunningAudit("MVNO_PAYMENT_EXPIRE_" + debitDocumentSummary.getDebitDocument().getId(), "Email", Long.valueOf(staffUser.getId()), id.getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno Payment email send", staffUser.getMvnoId());
//                                   }
//                                   if (sendSms) {
//                                       dunningAudit("MVNO_PAYMENT_EXPIRE_" + debitDocumentSummary.getDebitDocument().getId(), "SMS", Long.valueOf(staffUser.getId()), id.getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno Payment email send", staffUser.getMvnoId());
//                                   }
//                               } else {
//                                   log.info("Email or sms is already send");
//                               }
////                           }
//                       }
//                   }
//               else{
//                   log.info("mvno staff is not active");
//               }
//           }
//           else{
//               log.info("no staff found by given mvnoId");
//           }
//
//       }
//    }


    private void sendMvnoPaymentAdvanceNotificationDunning(Integer days, Integer mvnoId, String ccEmail, boolean sendEmail, boolean sendSms,List<DebitDocNumberMappingPojo>numberMappingPojos) {
        List<Mvno> mvnoList = mvnoRepository.findAll();
        System.out.println();

        mvnoList.forEach(mvno -> {
            if (!processAdvanceNotificationForMvno(mvno, days, sendEmail, sendSms, ccEmail,numberMappingPojos)) {
                log.info("Skipping MVNO ID: {} due to no valid staff or active conditions not met", mvno.getId());
            }
        });
    }

    private boolean processAdvanceNotificationForMvno(Mvno mvno, Integer days, boolean sendEmail, boolean sendSms, String ccEmail,List<DebitDocNumberMappingPojo>numberMappingPojos) {
        List<StaffUser> staffUsers = staffUserRepository.StatusAndIsDeleteIsFalseAndMvnoIdIn("ACTIVE", Collections.singletonList(mvno.getId().intValue()));

        if (staffUsers.isEmpty()) return false;

        StaffUser staffUser = staffUsers.get(0);
//        if (staffUser.getCreatedById() != 1) return false;

        return processAdvancePaymentNotification(mvno, staffUser, days, sendEmail, sendSms, ccEmail,numberMappingPojos);
    }

    private boolean processAdvancePaymentNotification(Mvno mvno, StaffUser staffUser, Integer days, boolean sendEmail, boolean sendSms, String ccEmail,List<DebitDocNumberMappingPojo>numberMappingPojos) {
        if (mvno.getCustInvoiceRefId() == null) return false;

        LocalDateTime adjustedDate = LocalDateTime.now().plusDays(days);
        List<DebitDocumentSummary> debitDocumentSummary = debitDocRepository.findDebitDocumentAndSum( mvno.getCustInvoiceRefId(), adjustedDate);

        if (CollectionUtils.isEmpty(debitDocumentSummary)) {
            log.info("No DebitDocumentSummary found for MVNO ID: {}", mvno.getId());
            return false;
        }

        if (!shouldSendAdvanceNotification(debitDocumentSummary.get(0), sendEmail, sendSms, staffUser, mvno)) {
            log.info("Email or SMS already sent for DebitDocument ID: {}", debitDocumentSummary.get(0).getDebitDocument().getId());
            return false;
        }

        for (DebitDocNumberMappingPojo numberMappingPojo : numberMappingPojos) {
            if (Objects.equals(numberMappingPojo.getDebitdocId(), debitDocumentSummary.get(0).getDebitDocument().getId())) {
                debitDocumentSummary.get(0).getDebitDocument().setDocnumber(numberMappingPojo.getDocnumber());
                debitDocumentSummary.get(0).getDebitDocument().setBillrunstatus(numberMappingPojo.getBillRunStatus());
            }
        }

        debitDocumentSummary=debitDocumentSummary.stream().filter(x->x.getDebitDocument()!=null && x.getDebitDocument().getBillrunstatus()!=null && !x.getDebitDocument().getBillrunstatus().equalsIgnoreCase("Cancelled")).collect(Collectors.toList());
        if(debitDocumentSummary!=null && !debitDocumentSummary.isEmpty())
            sendAdvanceNotificationAndAudit(debitDocumentSummary.get(0), staffUser, mvno, ccEmail, sendEmail, sendSms);
        return true;
    }

    private boolean shouldSendAdvanceNotification(DebitDocumentSummary debitDocumentSummary, boolean sendEmail, boolean sendSms, StaffUser staffUser, Mvno mvno) {
        boolean flag = true;

        if (sendEmail) {
            flag = getFlagForDunnningDuplicationNew("MVNO_PAYMENT_ADVANCE_" + debitDocumentSummary.getDebitDocument().getId(),
                    "Email", Long.valueOf(staffUser.getId()), mvno.getCustInvoiceRefId(), null, LocalDateTime.now());
        }

        if (sendSms && flag) {
            flag = getFlagForDunnningDuplicationNew("MVNO_PAYMENT_ADVANCE_" + debitDocumentSummary.getDebitDocument().getId(),
                    "SMS", Long.valueOf(staffUser.getId()), mvno.getCustInvoiceRefId(), null, LocalDateTime.now());
        }

        return flag;
    }

    private void sendAdvanceNotificationAndAudit(DebitDocumentSummary debitDocumentSummary, StaffUser staffUser, Mvno mvno, String ccEmail, boolean sendEmail, boolean sendSms) {
        LocalDate dueDate = debitDocumentSummary.getDebitDocument().getDuedate().toLocalDate().plusDays(mvno.getMvnoPaymentDueDays());

        mvnoPaymentAdvanceNotification(staffUser, debitDocumentSummary.getDebitDocument(), ccEmail, sendEmail, sendSms, dueDate);

        if (sendEmail) {
            dunningAudit("MVNO_PAYMENT_ADVANCE_" + debitDocumentSummary.getDebitDocument().getId(), "Email", Long.valueOf(staffUser.getId()),
                    mvno.getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno Payment Advance email sent", staffUser.getMvnoId());
        }

        if (sendSms) {
            dunningAudit("MVNO_PAYMENT_ADVANCE_" + debitDocumentSummary.getDebitDocument().getId(), "SMS", Long.valueOf(staffUser.getId()),
                    mvno.getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno Payment Advance SMS sent", staffUser.getMvnoId());
        }
    }


//    private void sendMvnoPaymentAdvanceNotificationDunning(Integer days, Integer mvnoId, String ccemail, boolean sendEmail, boolean sendSms) {
//        List<Mvno>mvnoList= mvnoRepository.findAll();
//        for(Mvno id :mvnoList) {
//            String status = "ACTIVE";
//            List<StaffUser> staffUserList = staffUserRepository.StatusAndIsDeleteIsFalseAndMvnoIdIn(status ,  Collections.singletonList(id.getId().intValue()));
//            if(!staffUserList.isEmpty()){
//                StaffUser staffUser = staffUserList.get(0);
//                if(staffUser.getCreatedById() == 1){
//                    boolean flag = true;
//                    if (id.getCustInvoiceRefId()!=null ) {
//                        LocalDateTime adjustedDate = LocalDateTime.now().minusDays(id.getMvnoPaymentDueDays());
//                        DebitDocumentSummary debitDocumentSummary=  debitDocRepository.findDebitDocumentAndSum(id.getId(),id.getCustInvoiceRefId(),adjustedDate);
////                        Double pendingAmount = debitDocService.getTotalAmountDebitDocumentsByMvno(Math.toIntExact(id.getId()), false, days);
////                        List<DebitDocument> lastInvoice = debitDocRepository.lastInvoice(id.getCustInvoiceRefId());
////                        if (!CollectionUtils.isEmpty(lastInvoice)) {
////                            DebitDocument debitDocument = lastInvoice.get(0);
//////                            List<CreditDebitDocMapping> creditDebitDocMappingList = creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
//////                            List<CreditDocument> creditDocument = creditDocRepository.findAllById(Collections.singleton(creditDebitDocMappingList.get(0).getCreditDocId()));
////                            LocalDate duedate = debitDocument.getStartdate().toLocalDate().plusDays(Integer.valueOf(id.getMvnoPaymentDueDays()));
////                            if (LocalDate.now().equals(duedate.minusDays(days)) &&!debitDocument.getPaymentStatus().equalsIgnoreCase("Fully Adjusted")) {
//                                if(Objects.nonNull(debitDocumentSummary)){
//                                if (sendEmail) {
//                                    flag = getFlagForDunnningDuplicationNew("MVNO_PAYMENT_ADVANCE_" +debitDocumentSummary.getDebitDocument().getId(), "Email", Long.valueOf(staffUser.getId()), id.getCustInvoiceRefId(), null, LocalDateTime.now());
//                                }
//                                if (sendSms) {
//                                    flag = getFlagForDunnningDuplicationNew("MVNO_PAYMENT_ADVANCE_" + debitDocumentSummary.getDebitDocument().getId(), "SMS", Long.valueOf(staffUser.getId()), id.getCustInvoiceRefId(), null, LocalDateTime.now());
//                                }
//                                if (flag) {
//                                    LocalDate duedate = debitDocumentSummary.getDebitDocument().getDuedate().toLocalDate().plusDays(id.getMvnoPaymentDueDays());
//                                    mvnoPaymentAdvanceNotification(staffUser, debitDocumentSummary.getDebitDocument(), ccemail, sendEmail, sendSms,duedate);
//                                    if (sendEmail) {
//                                        dunningAudit("MVNO_PAYMENT_ADVANCE_" + debitDocumentSummary.getDebitDocument().getId(), "Email", Long.valueOf(staffUser.getId()), id.getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno Payment email send", staffUser.getMvnoId());
//                                    }
//                                    if (sendSms) {
//                                        dunningAudit("MVNO_PAYMENT_ADVANCE_" + debitDocumentSummary.getDebitDocument().getId(), "SMS", Long.valueOf(staffUser.getId()), id.getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno Payment email send", staffUser.getMvnoId());
//                                    }
//                                } else {
//                                    log.info("Email or sms is already send");
//                                }
//                            }
//                        }
////                    }
//
//                }
//                else{
//                    log.info("mvno staff is not active");
//                }
//            }
//            else{
//                log.info("no staff found by given mvnoId");
//            }
//
//        }
//    }



    void sendSmsNotificationForDunningCustomer() {
        List<CustomerDocDetails> allDocList = customersService.getDocumentForDunningCustomer();
        List<CustomerDocDetails> docForCust = new ArrayList<>();
        for (CustomerDocDetails customerDocDetails : allDocList) {
            Integer custId = customerDocDetails.getCustomer().getId();
            Customers customer = customersRepository.findByIdAndIsDeletedIsFalse(custId);
            if (customer != null) {
                if (customer.getBranch() != null) {
                    Branch branch = branchRepository.findByIdAndIsDeletedIsFalse(customer.getBranch());
                    if (branch != null) {
                        if (branch.getDunningDays() != null) {
                            if (branch.getDunningDays().equals("1 Day") || branch.getDunningDays().equals("7 Day")) {
                                if (branch.getDunningDays().equals("1 Day"))
                                    if (customerDocDetails.getCreatedate().plusDays(1).isBefore(LocalDateTime.now())) {
                                        docForCust.add(customerDocDetails);
                                        //  sendSMSNotificationForStaffDunning(docForCust);
                                        docForCust.remove(customerDocDetails);

                                    }
                                if (branch.getDunningDays().equals("7 Day")) {
                                    if (customerDocDetails.getCreatedate().plusDays(7).isBefore(LocalDateTime.now())) {
                                        docForCust.add(customerDocDetails);
                                        //   sendSMSNotificationForStaffDunning(docForCust);
                                        docForCust.remove(customerDocDetails);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    void sendEmailNotificationForDunning(List<Customers> customers, String ccEmail, Integer dateDiff,Boolean isGeneratePaymentLink,String token) {
        Integer batchSize = 500;
        List<List<Customers>> batches = new ArrayList<>();
        for (int i = 0; i < customers.size(); i += batchSize) {
            batches.add(customers.subList(i, Math.min(i + batchSize, customers.size())));
        }
        batches.parallelStream().forEach(batch -> {
            List<Integer> customerIds = batch.stream().map(Customers::getId).collect(Collectors.toList());
            Map<Integer, String> paymentUrlMap = (Objects.nonNull(isGeneratePaymentLink) && Boolean.TRUE.equals(isGeneratePaymentLink))
                    ? generatePaymentLinks(customerIds, token)
                    : new HashMap<>();
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_DUNNING_TEMPLATE);
            batch.forEach(tuple -> {
                CustomerLedgerDtlsPojo pojo = new CustomerLedgerDtlsPojo();
                pojo.setCustId(tuple.getId());
                CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getWalletAmt(pojo);
                String pendingAmount = clientService.getValueByNameAndmvnoId("MINIMUM_PAYMENT_REQUIRED", tuple.getMvnoId());

                if (Objects.isNull(infoPojo.getClosingBalance())) {
                    infoPojo.setClosingBalance(0.0);
                }

                if (tuple.getWalletbalance() > 0) {
                    try {
                        Customers customers1 = customersService.get(tuple.getId(),tuple.getMvnoId());
                        if (customers1 == null) return;

                        Boolean flag = getFlagForDunnningDuplicationNew("Customer Payment", "Email", null, tuple.getId(), null, LocalDateTime.now());
                        List<Long> cprIds = customerPackageRepository.getPrepaidCustPackageIdBycustomeranddatediff(tuple.getId(), dateDiff);
                        Boolean invoiceFlag = IsInvoiceInapprovalOrNot(cprIds.stream().map(Long::intValue).collect(Collectors.toList()));

                        if (flag && invoiceFlag) {
                            log.info("Dunning Alreay sent or not " + flag, invoiceFlag);
                            customers1.setIsDunningActivate(true);
                            customers1.setLastDunningDate(LocalDateTime.now());
                            customersRepository.updateDunningStatus(tuple.getId(),LocalDateTime.now());

                            String currencySymbol = clientService.getCurrencyByNameAndMvnoId(ClientServiceConstant.CURRENCY_SYMBOL, customers1.getMvnoId()).getValue();
                            if (tuple.getStatus().equalsIgnoreCase("Active") && optionalTemplate.isPresent()) {
                                TemplateNotification template = optionalTemplate.get();
                                log.info("Is Template COnfig " + template.isSmsEventConfigured());
                                if (template.isSmsEventConfigured() || template.isEmailEventConfigured()) {
                                    CustomerDunningMessage dunningMessage = new CustomerDunningMessage(
                                            RabbitMqConstants.CUSTOMER_DUNNING_TEMPLATE_HEADER, template,
                                            RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, tuple,
                                            Math.abs(tuple.getWalletbalance()), currencySymbol, LocalDate.now(), paymentUrlMap.getOrDefault(customers1.getId(),""));
                                    dunningMessage.setEmailConfigured(true);
                                    dunningMessage.setSmsConfigured(false);
                                    String messageJson = new Gson().toJson(dunningMessage);
                                    dunningAudit("Customer Payment", "Email", null, tuple.getId(), null, LocalDateTime.now(), messageJson, customers1.getMvnoId());
                                    kafkaMessageSender.send(new KafkaMessageData(dunningMessage, CustomerDunningMessage.class.getSimpleName(), KafkaConstant.BSS_CUSTOMER_DUNNING));
                                }
                            }
                            if (!ccEmail.isEmpty()) {
                                tuple.setEmail(ccEmail);
                                CustomerDunningMessage dunningMessage = new CustomerDunningMessage(
                                        RabbitMqConstants.CUSTOMER_DUNNING_TEMPLATE_HEADER, optionalTemplate.get(),
                                        RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, tuple,
                                        Math.abs(tuple.getWalletbalance()), currencySymbol, LocalDate.now(),  paymentUrlMap.getOrDefault(customers1.getId(),""));
                                dunningMessage.setEmailConfigured(true);
                                dunningMessage.setSmsConfigured(false);

                                String messageJson = new Gson().toJson(dunningMessage);
                                dunningAudit("Customer Payment to CC", "Email", null, tuple.getId(), null, LocalDateTime.now(), messageJson, customers1.getMvnoId());
                                kafkaMessageSender.send(new KafkaMessageData(dunningMessage, CustomerDunningMessage.class.getSimpleName(), KafkaConstant.BSS_CUSTOMER_DUNNING));
                            }
                        }
                    } catch (Throwable e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            });
        });
    }


    void sendSMSNotificationForDunning(List<Customers> customers, String ccMobile , Integer dateDiff,Boolean isGeneratePaymentLink,String token) {
       // List<Customers> distinctCustomers = customers.stream().distinct().collect(Collectors.toList());
        Integer batchSize = 500;
        List<List<Customers>> batches = new ArrayList<>();
        for (int i = 0; i < customers.size(); i += batchSize) {
            batches.add(customers.subList(i, Math.min(i + batchSize, customers.size())));
        }
        batches.parallelStream().forEach(batch -> {
            List<Integer> customerIds = batch.stream().map(Customers::getId).collect(Collectors.toList());
            Map<Integer, String> paymentUrlMap = (Objects.nonNull(isGeneratePaymentLink) && Boolean.TRUE.equals(isGeneratePaymentLink))
                    ? generatePaymentLinks(customerIds, token)
                    : new HashMap<>();
            batch.forEach(customer->{
                CustomerLedgerDtlsPojo pojo = new CustomerLedgerDtlsPojo();
                pojo.setCustId(customer.getId());

                CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getWalletAmt(pojo);
                String pendingAmount = clientService.getValueByNameAndmvnoId("MINIMUM_PAYMENT_REQUIRED",customer.getMvnoId());
                if (Objects.isNull(infoPojo.getClosingBalance())) {
                    infoPojo.setClosingBalance(0.0);
                }
                if (customer.getWalletbalance() > 0) {
                    try {
                        Customers customers1 = customersService.get(customer.getId(),customer.getMvnoId());
                        Boolean flag = getFlagForDunnningDuplicationNew("Customer Payment", "SMS", null, customer.getId(), null, LocalDateTime.now());
                        List<Long> cprIds = customerPackageRepository.getPrepaidCustPackageIdBycustomeranddatediff(customers1.getId(), dateDiff);
                        Boolean invoiceflag = IsInvoiceInapprovalOrNot(cprIds.stream().map(aLong -> aLong.intValue()).collect(Collectors.toList()));
                        if (flag && invoiceflag) {
                            customers1.setIsDunningActivate(true);
                            customers1.setLastDunningDate(LocalDateTime.now());
                            // customers1.setDunningActivateFor("PaymentSMS");
                            customersRepository.updateDunningStatus(customer.getId(),LocalDateTime.now());
                            String currencySymbol = clientService.getCurrencyByNameAndMvnoId(ClientServiceConstant.CURRENCY_SYMBOL , customers1.getMvnoId()).getValue();
                            Optional<TemplateNotification> optionalTemplate = templateRepository
                                    .findByTemplateName(RabbitMqConstants.CUSTOMER_DUNNING_TEMPLATE);
                            if (customer.getStatus().equalsIgnoreCase("Active")) {
                                if (optionalTemplate.isPresent()) {
                                    if (optionalTemplate.get().isSmsEventConfigured()
                                            || optionalTemplate.get().isEmailEventConfigured()) {
                                        CustomerDunningMessage dunningMessage = new CustomerDunningMessage(
                                                RabbitMqConstants.CUSTOMER_DUNNING_TEMPLATE_HEADER, optionalTemplate.get(),
                                                RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer,
                                                Math.abs(customer.getWalletbalance()), currencySymbol, LocalDate.now(),paymentUrlMap.getOrDefault(customer.getId(),""));
                                        dunningMessage.setEmailConfigured(false);
                                        dunningMessage.setSmsConfigured(true);
                                        Gson gson = new Gson();
                                        gson.toJson(dunningMessage);
                                        sendDunningMessage(dunningMessage,customer,"Customer Payment","SMS");
//                                        dunningAudit("Customer Payment", "SMS", null, customer.getId(), null, LocalDateTime.now(), dunningMessage.toString(),customers1.getMvnoId());
//                                        kafkaMessageSender.send(new KafkaMessageData(dunningMessage,CustomerDunningMessage.class.getSimpleName(),KafkaConstant.BSS_CUSTOMER_DUNNING));
//                                    messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_CUSTOMER_DUNNING);
                                    }
                                    if (ccMobile.length() > 1) {
                                        customer.setMobile(ccMobile);
                                        if (optionalTemplate.get().isSmsEventConfigured()
                                                || optionalTemplate.get().isEmailEventConfigured()) {
                                            CustomerDunningMessage dunningMessage = new CustomerDunningMessage(
                                                    RabbitMqConstants.CUSTOMER_DUNNING_TEMPLATE_HEADER, optionalTemplate.get(),
                                                    RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer,
                                                    Math.abs(customer.getWalletbalance()), currencySymbol, LocalDate.now(),paymentUrlMap.getOrDefault(customer.getId(),""));
                                            dunningMessage.setEmailConfigured(false);
                                            dunningMessage.setSmsConfigured(true);
                                            Gson gson = new Gson();
                                            gson.toJson(dunningMessage);
                                            sendDunningMessage(dunningMessage,customer,"Customer Payment","SMS");
//                                            dunningAudit("Customer Payment", "SMS", null, customer.getId(), null, LocalDateTime.now(), dunningMessage.toString(),customers1.getMvnoId());
//                                            kafkaMessageSender.send(new KafkaMessageData(dunningMessage,CustomerDunningMessage.class.getSimpleName(),KafkaConstant.BSS_CUSTOMER_DUNNING));
//                                        messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_CUSTOMER_DUNNING);
                                        }
                                    }
                                }
                            }
                        }

                    } catch (Throwable e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            });
        });
    }

    void sendSMSNotificationForStaffDunning(List<CustomerDocDetails> customerDocDetails, List<Long> branchIds, List<Long> partnerIds, String ccmobile) {
        //List<CustomerDocDetails> customerDocDetailscheck = customerDocDetails.stream().filter(customerDocDetails2 -> customerDocDetails2.getStartDate() != null && customerDocDetails2.getEndDate() != null).collect(Collectors.toList());
        for (CustomerDocDetails customerDocDetails1 : customerDocDetails) {
            try {
                Integer staffId = customerDocDetails1.getCreatedById();
                StaffUser staffUser = staffUserRepository.findById(staffId).get();
                Customers customer = customersRepository.findById(customerDocDetails1.getCustomer().getId()).get();
                Boolean flag = getFlagForDunningDuplication(customer.getIsDunningActivate() ,"DocumentSMS" , "DocumentSMS" ,  LocalDateTime.now() , customer );
                Boolean flagevent = getFlagForDunnningDuplicationNew("Customer Document", "SMS", Long.valueOf(staffUser.getId()), customerDocDetails1.getCustomer().getId(), null, LocalDateTime.now());
                if (flag && flagevent) {
                    customer.setIsDunningActivate(true);
                    customer.setLastDunningDate(LocalDateTime.now());
                    customer.setDunningActivateFor("DocumentSMS");
                    customersRepository.save(customer);
                    if (!branchIds.isEmpty()) {
                        if (branchIds.contains(customer.getBranch())) {
                            if (staffUser.getStatus().equalsIgnoreCase("Active")) {

                                Optional<TemplateNotification> optionalTemplate = templateRepository
                                        .findByTemplateName(RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE);
                                if (optionalTemplate.isPresent()) {
                                    if (optionalTemplate.get().isSmsEventConfigured()
                                            || optionalTemplate.get().isEmailEventConfigured()) {
                                        Integer buId = null;
                                        if(customerDocDetails1.getCustomer().getBuId() != null){
                                            buId = customerDocDetails1.getCustomer().getBuId().intValue();
                                        }
                                        StaffExpiredMassage dunningMessage = new StaffExpiredMassage(
                                                RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE_HEADER, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, staffUser, customerDocDetails1.getCustomer().getUsername(),customerDocDetails1.getCustomer().getMvnoId(),buId,staffUser.getId().longValue());
                                        dunningMessage.setEmailConfigured(false);
                                        dunningMessage.setSmsConfigured(true);
                                        Gson gson = new Gson();
                                        gson.toJson(dunningMessage);
                                        dunningAudit("Customer Document", "SMS", Long.valueOf(staffId), customerDocDetails1.getCustomer().getId(), null, LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                        messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_DOCUMENT_DUNNING_STAFF);
                                    kafkaMessageSender.send(new KafkaMessageData(dunningMessage,StaffExpiredMassage.class.getSimpleName(),KafkaConstant.CUSTOMER_DOCUMENT_DUNNING_TO_STAFF));
                                    }
                                }
                                Optional<TemplateNotification> optionalTemplate2 = templateRepository
                                        .findByTemplateName(RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE);
                                if (optionalTemplate2.isPresent()) {
                                    if (optionalTemplate2.get().isSmsEventConfigured()
                                            || optionalTemplate2.get().isEmailEventConfigured()) {
                                        CustomerExpiredDocumentMessage dunningMessage = new CustomerExpiredDocumentMessage(
                                                RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer, staffUser.getUsername(),staffId.longValue());
                                        dunningMessage.setEmailConfigured(false);
                                        dunningMessage.setSmsConfigured(true);
                                        Gson gson = new Gson();
                                        gson.toJson(dunningMessage);
                                        dunningAudit("Customer Document send to customer", "SMS", Long.valueOf(staffId), customerDocDetails1.getCustomer().getId(),null, LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                        messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT);
                                        kafkaMessageSender.send(new KafkaMessageData(dunningMessage,CustomerExpiredDocumentMessage.class.getSimpleName()));
                                        if (ccmobile.length() > 1) {
                                            customer.setMobile(ccmobile);
                                            CustomerExpiredDocumentMessage dunningMessage2 = new CustomerExpiredDocumentMessage(
                                                    RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer, staffUser.getUsername(),staffId.longValue());
                                            dunningMessage.setEmailConfigured(false);
                                            dunningMessage.setSmsConfigured(true);
                                            Gson gson2 = new Gson();
                                            gson2.toJson(dunningMessage2);

                                            dunningAudit("Customer Document send to CC", "SMS", Long.valueOf(staffId), null, Long.valueOf(customerDocDetails1.getCustomer().getId()), LocalDateTime.now(), dunningMessage2.toString(),staffUser.getMvnoId());
//                                            messageSender.send(dunningMessage2, RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT);
                                            kafkaMessageSender.send(new KafkaMessageData(dunningMessage,CustomerExpiredDocumentMessage.class.getSimpleName()));
                                        }

                                    }
                                }
                            }
                        }
                    }
                    if(!partnerIds.isEmpty()) {
                        if (partnerIds.contains(customer.getPartner().getId().longValue())) {
                            if (staffUser.getStatus().equalsIgnoreCase("Active")) {

                                Optional<TemplateNotification> optionalTemplate = templateRepository
                                        .findByTemplateName(RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE);
                                if (optionalTemplate.isPresent()) {
                                    if (optionalTemplate.get().isSmsEventConfigured()
                                            || optionalTemplate.get().isEmailEventConfigured()) {
                                        Integer buId = null;
                                        if(customerDocDetails1.getCustomer().getBuId() != null){
                                            buId = customerDocDetails1.getCustomer().getBuId().intValue();
                                        }
                                        StaffExpiredMassage dunningMessage = new StaffExpiredMassage(
                                                RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE_HEADER, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, staffUser, customerDocDetails1.getCustomer().getUsername(),customerDocDetails1.getCustomer().getMvnoId(),buId,staffUser.getId().longValue());
                                        dunningMessage.setEmailConfigured(false);
                                        dunningMessage.setSmsConfigured(true);
                                        Gson gson = new Gson();
                                        gson.toJson(dunningMessage);
                                        dunningAudit("Customer Document", "SMS", Long.valueOf(staffId), customerDocDetails1.getCustomer().getId(), Long.valueOf(customer.getPartner().getId()), LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                        messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_DOCUMENT_DUNNING_STAFF);
                                        kafkaMessageSender.send(new KafkaMessageData(dunningMessage,StaffExpiredMassage.class.getSimpleName(),KafkaConstant.CUSTOMER_DOCUMENT_DUNNING_TO_STAFF));
                                    }
                                }
                                Optional<TemplateNotification> optionalTemplate2 = templateRepository
                                        .findByTemplateName(RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE);
                                if (optionalTemplate2.isPresent()) {
                                    if (optionalTemplate2.get().isSmsEventConfigured()
                                            || optionalTemplate2.get().isEmailEventConfigured()) {
                                        CustomerExpiredDocumentMessage dunningMessage = new CustomerExpiredDocumentMessage(
                                                RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer, staffUser.getUsername(),staffId.longValue());
                                        dunningMessage.setEmailConfigured(false);
                                        dunningMessage.setSmsConfigured(true);
                                        Gson gson = new Gson();
                                        gson.toJson(dunningMessage);
                                        dunningAudit("Customer Document send to customer", "SMS", Long.valueOf(staffId), customerDocDetails1.getCustomer().getId(), Long.valueOf(customer.getPartner().getId()), LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                        messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT);
                                        kafkaMessageSender.send(new KafkaMessageData(dunningMessage,CustomerExpiredDocumentMessage.class.getSimpleName()));
                                        if (ccmobile.length() > 1) {
                                            customer.setMobile(ccmobile);
                                            CustomerExpiredDocumentMessage dunningMessage2 = new CustomerExpiredDocumentMessage(
                                                    RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer, staffUser.getUsername(),staffId.longValue());
                                            dunningMessage.setEmailConfigured(false);
                                            dunningMessage.setSmsConfigured(true);
                                            Gson gson2 = new Gson();
                                            gson2.toJson(dunningMessage2);

                                            dunningAudit("Customer Document send to CC", "SMS", Long.valueOf(staffId), null, Long.valueOf(customerDocDetails1.getCustomer().getId()), LocalDateTime.now(), dunningMessage2.toString(),staffUser.getMvnoId());
//                                            messageSender.send(dunningMessage2, RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT);
                                            kafkaMessageSender.send(new KafkaMessageData(dunningMessage,CustomerExpiredDocumentMessage.class.getSimpleName()));
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            }
        }

    }

    void sendDeactivationNotificationForStaffDunning(List<CustomerDocDetails> customerDocDetails, List<Long> branchIds, List<Long> partnerIds, String ccmobile, String ccemail) {
        //List<CustomerDocDetails> customerDocDetailscheck = customerDocDetails.stream().filter(customerDocDetails2 -> customerDocDetails2.getStartDate() != null && customerDocDetails2.getEndDate() != null).collect(Collectors.toList());
        for (CustomerDocDetails customerDocDetails1 : customerDocDetails) {
            try {
                Integer staffId = customerDocDetails1.getCreatedById();
                StaffUser staffUser = staffUserRepository.findById(staffId).get();
                Customers customer = customersRepository.findById(customerDocDetails1.getCustomer().getId()).get();
                Boolean flag = getFlagForDunnningDuplicationNew("Customer Document", "Deactivation", Long.valueOf(staffId), customerDocDetails1.getCustomer().getId(), null, LocalDateTime.now());


                    if (!branchIds.isEmpty()) {
                        if (branchIds.contains(customer.getBranch())) {
                            List<Integer> custServiceMappingIds = customerServiceMappingRepository.findByCustId(customer.getId()).stream().map(customerServiceMapping -> customerServiceMapping.getId()).collect(Collectors.toList());
                            String remarks = "Disable due to Document not provided";
                            System.out.println("$$$$$Disabled CALLED$$$$$$");
                            custPlanMappingService.changeStatusOfCustServices(custServiceMappingIds , StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE , remarks,false);

                        // List<Long> serviceIds =  customerServiceMappingRepository.getAllServiceIdByCustomerId(customer.getId());
                         System.out.println("&&&&&&&&&");
                        //    System.out.println(serviceIds);
                            System.out.println("^^^^^^^^^^^");
                            if(flag) {
                                if (staffUser.getStatus().equalsIgnoreCase("Active")) {
                                    Optional<TemplateNotification> optionalTemplate = templateRepository
                                            .findByTemplateName(RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE);
                                    if (optionalTemplate.isPresent()) {
                                        if (optionalTemplate.get().isSmsEventConfigured()
                                                || optionalTemplate.get().isEmailEventConfigured()) {
                                            Integer buId = null;
                                            if(customerDocDetails1.getCustomer().getBuId() != null){
                                                buId = customerDocDetails1.getCustomer().getBuId().intValue();
                                            }
                                            StaffExpiredMassage dunningMessage = new StaffExpiredMassage(
                                                    RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE_HEADER, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, staffUser, customerDocDetails1.getCustomer().getUsername(),customerDocDetails1.getCustomer().getMvnoId(),buId,staffUser.getId().longValue());
                                            dunningMessage.setEmailConfigured(true);
                                            dunningMessage.setSmsConfigured(false);
                                            Gson gson = new Gson();
                                            gson.toJson(dunningMessage);
                                            dunningAudit("Customer Document", "Deactivation", Long.valueOf(staffId), customerDocDetails1.getCustomer().getId(), null, LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                            messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_DOCUMENT_DUNNING_STAFF);
                                            kafkaMessageSender.send(new KafkaMessageData(dunningMessage,StaffExpiredMassage.class.getSimpleName(),KafkaConstant.CUSTOMER_DOCUMENT_DUNNING_TO_STAFF));
                                        }
                                    }
                                    Optional<TemplateNotification> optionalTemplate2 = templateRepository
                                            .findByTemplateName(RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE);
                                    if (optionalTemplate2.isPresent()) {
                                        if (optionalTemplate2.get().isSmsEventConfigured()
                                                || optionalTemplate2.get().isEmailEventConfigured()) {
                                            CustomerExpiredDocumentMessage dunningMessage = new CustomerExpiredDocumentMessage(
                                                    RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer, staffUser.getUsername(),staffId.longValue());
                                            dunningMessage.setEmailConfigured(true);
                                            dunningMessage.setSmsConfigured(false);
                                            Gson gson = new Gson();
                                            gson.toJson(dunningMessage);
                                            dunningAudit("Customer Document send to customer", "Deactivation", Long.valueOf(staffId), null, Long.valueOf(customerDocDetails1.getCustomer().getId()), LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                            messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT);
                                            kafkaMessageSender.send(new KafkaMessageData(dunningMessage,CustomerExpiredDocumentMessage.class.getSimpleName()));
                                            if (ccemail.length() > 1) {
                                                customer.setEmail(ccemail);
                                                CustomerExpiredDocumentMessage dunningMessage2 = new CustomerExpiredDocumentMessage(
                                                        RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer, staffUser.getUsername(),staffId.longValue());
                                                dunningMessage.setEmailConfigured(true);
                                                dunningMessage.setSmsConfigured(false);
                                                Gson gson2 = new Gson();
                                                gson2.toJson(dunningMessage2);

                                                dunningAudit("Customer Document send to CC", "Email", Long.valueOf(staffId), null, Long.valueOf(customerDocDetails1.getCustomer().getId()), LocalDateTime.now(), dunningMessage2.toString(),staffUser.getMvnoId());
//                                                messageSender.send(dunningMessage2, RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT);
                                                kafkaMessageSender.send(new KafkaMessageData(dunningMessage,CustomerExpiredDocumentMessage.class.getSimpleName()));
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!partnerIds.isEmpty()) {
                        if (partnerIds.contains(customer.getPartner().getId().longValue())) {
                            List<Integer> custServiceMappingIds = customerServiceMappingRepository.findByCustId(customer.getId()).stream().map(customerServiceMapping -> customerServiceMapping.getId()).collect(Collectors.toList());
                            String remarks = "Disable due to Document not provided";System.out.println("$$$$$Disabled CALLED$$$$$$");
                            custPlanMappingService.changeStatusOfCustServices(custServiceMappingIds , StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE , remarks,false);
                            if(flag) {
                                if (staffUser.getStatus().equalsIgnoreCase("Active")) {

                                    Optional<TemplateNotification> optionalTemplate = templateRepository
                                            .findByTemplateName(RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE);
                                    if (optionalTemplate.isPresent()) {
                                        if (optionalTemplate.get().isSmsEventConfigured()
                                                || optionalTemplate.get().isEmailEventConfigured()) {
                                            Integer buId = null;
                                            if(customerDocDetails1.getCustomer().getBuId() != null){
                                                buId = customerDocDetails1.getCustomer().getBuId().intValue();
                                            }
                                            StaffExpiredMassage dunningMessage = new StaffExpiredMassage(
                                                    RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE_HEADER, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, staffUser, customerDocDetails1.getCustomer().getUsername(),customerDocDetails1.getCustomer().getMvnoId(),buId,staffUser.getId().longValue());
                                            dunningMessage.setEmailConfigured(true);
                                            dunningMessage.setSmsConfigured(false);
                                            Gson gson = new Gson();
                                            gson.toJson(dunningMessage);
                                            dunningAudit("Customer Document", "Deactive", Long.valueOf(staffId), customerDocDetails1.getCustomer().getId(), null, LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                            messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF);
                                            kafkaMessageSender.send(new KafkaMessageData(dunningMessage, dunningMessage.getClass().getSimpleName(),KafkaConstant.CUSTOMER_DOCUMENT_DUNNING_TO_STAFF));

                                        }
                                    }
                                    Optional<TemplateNotification> optionalTemplate2 = templateRepository
                                            .findByTemplateName(RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE);
                                    if (optionalTemplate2.isPresent()) {
                                        if (optionalTemplate2.get().isSmsEventConfigured()
                                                || optionalTemplate2.get().isEmailEventConfigured()) {
                                            CustomerExpiredDocumentMessage dunningMessage = new CustomerExpiredDocumentMessage(
                                                    RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer, staffUser.getUsername(),staffId.longValue());
                                            dunningMessage.setEmailConfigured(true);
                                            dunningMessage.setSmsConfigured(false);
                                            Gson gson = new Gson();
                                            gson.toJson(dunningMessage);
                                            dunningAudit("Customer Document send to customer", "Deactive", Long.valueOf(staffId), null, Long.valueOf(customerDocDetails1.getCustomer().getId()), LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                            messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT);
                                            kafkaMessageSender.send(new KafkaMessageData(dunningMessage,CustomerExpiredDocumentMessage.class.getSimpleName()));
                                            if (ccemail.length() > 1) {
                                                customer.setEmail(ccemail);
                                                CustomerExpiredDocumentMessage dunningMessage2 = new CustomerExpiredDocumentMessage(
                                                        RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer, staffUser.getUsername(),staffId.longValue());
                                                dunningMessage.setEmailConfigured(true);
                                                dunningMessage.setSmsConfigured(false);
                                                Gson gson2 = new Gson();
                                                gson2.toJson(dunningMessage2);

                                                dunningAudit("Customer Document send to CC", "Deactive", Long.valueOf(staffId), null, Long.valueOf(customerDocDetails1.getCustomer().getId()), LocalDateTime.now(), dunningMessage2.toString(),staffUser.getMvnoId());
//                                                messageSender.send(dunningMessage2, RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT);
                                                kafkaMessageSender.send(new KafkaMessageData(dunningMessage,CustomerExpiredDocumentMessage.class.getSimpleName()));
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }

            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            }
        }

    }

    void sendEmailNotificationForStaffDunning(List<CustomerDocDetails> customerDocDetails, List<Long> branchIds, List<Long> partnerIds, String ccEmail) {
        //   List<CustomerDocDetails> customerDocDetailscheck = customerDocDetails.stream().filter(customerDocDetails2 -> customerDocDetails2.getStartDate() != null && customerDocDetails2.getEndDate() != null).collect(Collectors.toList());
        for (CustomerDocDetails customerDocDetails1 : customerDocDetails) {
            try {
                Integer staffId = customerDocDetails1.getCreatedById();
                StaffUser staffUser = staffUserRepository.findById(staffId).get();
                Customers customer = customersRepository.findById(customerDocDetails1.getCustomer().getId()).get();
                Boolean flag = getFlagForDunningDuplication(customer.getIsDunningActivate() ,"DocumentEmail" , "DocumentEmail" , LocalDateTime.now(), customer );
                Boolean flagevent = getFlagForDunnningDuplicationNew("Customer Document", "Email", Long.valueOf(staffUser.getId()), customerDocDetails1.getCustomer().getId(), null, LocalDateTime.now());
  //             Boolean flag2 = true;
                // getFlagForDunningDuplication(customer.getIsDunningActivate() ,"DocumentEmail" , "DocumentEmail" , customer.getLastDunningDate() , customer );
                if(flag &&flagevent){
                if(!branchIds.isEmpty()) {
                        if (branchIds.contains(customer.getBranch())) {
                        if (staffUser.getStatus().equalsIgnoreCase("Active")) {

                            Optional<TemplateNotification> optionalTemplate = templateRepository
                                    .findByTemplateName(RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE);
                            if (optionalTemplate.isPresent()) {
                                if (optionalTemplate.get().isSmsEventConfigured()
                                        || optionalTemplate.get().isEmailEventConfigured()) {
                                    Integer buId = null;
                                    if(customerDocDetails1.getCustomer().getBuId() != null){
                                        buId = customerDocDetails1.getCustomer().getBuId().intValue();
                                    }
                                    StaffExpiredMassage dunningMessage = new StaffExpiredMassage(
                                            RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE_HEADER, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, staffUser, customerDocDetails1.getCustomer().getUsername(),customerDocDetails1.getCustomer().getMvnoId(),buId,staffUser.getId().longValue());
                                    dunningMessage.setEmailConfigured(true);
                                    dunningMessage.setSmsConfigured(false);
                                    Gson gson = new Gson();
                                    gson.toJson(dunningMessage);
                                    customer.setLastDunningDate(LocalDateTime.now());
                                    customersRepository.save(customer);
                                    dunningAudit("Customer Document", "Email", Long.valueOf(staffId), customerDocDetails1.getCustomer().getId(), null, LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                    messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_DOCUMENT_DUNNING_STAFF);
                                    kafkaMessageSender.send(new KafkaMessageData(dunningMessage,StaffExpiredMassage.class.getSimpleName(),KafkaConstant.CUSTOMER_DOCUMENT_DUNNING_TO_STAFF));
                                }
                            }
                            Optional<TemplateNotification> optionalTemplate2 = templateRepository
                                    .findByTemplateName(RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE);
                            if (optionalTemplate2.isPresent()) {
                                if (optionalTemplate2.get().isSmsEventConfigured()
                                        || optionalTemplate2.get().isEmailEventConfigured()) {
                                    CustomerExpiredDocumentMessage dunningMessage = new CustomerExpiredDocumentMessage(
                                            RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer, staffUser.getUsername(),staffId.longValue());
                                    dunningMessage.setEmailConfigured(true);
                                    dunningMessage.setSmsConfigured(false);
                                    Gson gson = new Gson();
                                    gson.toJson(dunningMessage);
                                    customer.setLastDunningDate(LocalDateTime.now());
                                    customersRepository.save(customer);
                                    dunningAudit("Customer Document send to customer", "Email", Long.valueOf(staffId), null, Long.valueOf(customerDocDetails1.getCustomer().getId()), LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                    messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT);
                                    kafkaMessageSender.send(new KafkaMessageData(dunningMessage,CustomerExpiredDocumentMessage.class.getSimpleName()));
                                    if (ccEmail.length() > 1) {
                                        customer.setEmail(ccEmail);
                                        CustomerExpiredDocumentMessage dunningMessage2 = new CustomerExpiredDocumentMessage(
                                                RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer, staffUser.getUsername(),staffId.longValue());
                                        dunningMessage.setEmailConfigured(true);
                                        dunningMessage.setSmsConfigured(false);
                                        Gson gson2 = new Gson();
                                        gson2.toJson(dunningMessage2);

                                        dunningAudit("Customer Document send to CC", "Email", Long.valueOf(staffId), null, Long.valueOf(customerDocDetails1.getCustomer().getId()), LocalDateTime.now(), dunningMessage2.toString(),staffUser.getMvnoId());
//                                        messageSender.send(dunningMessage2, RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT);
                                        kafkaMessageSender.send(new KafkaMessageData(dunningMessage,CustomerExpiredDocumentMessage.class.getSimpleName()));
                                    }

                                }
                            }
                        }
                    }
                }
                    if(!partnerIds.isEmpty()) {
                        if (partnerIds.contains(customer.getPartner().getId().longValue())) {
                            if (staffUser.getStatus().equalsIgnoreCase("Active")) {

                                Optional<TemplateNotification> optionalTemplate = templateRepository
                                        .findByTemplateName(RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE);
                                if (optionalTemplate.isPresent()) {
                                    if (optionalTemplate.get().isSmsEventConfigured()
                                            || optionalTemplate.get().isEmailEventConfigured()) {
                                        Integer buId = null;
                                        if(customerDocDetails1.getCustomer().getBuId() != null){
                                            buId = customerDocDetails1.getCustomer().getBuId().intValue();
                                        }
                                        StaffExpiredMassage dunningMessage = new StaffExpiredMassage(
                                                RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE_HEADER, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, staffUser, customerDocDetails1.getCustomer().getUsername(),customerDocDetails1.getCustomer().getMvnoId(),buId,staffUser.getId().longValue());
                                        dunningMessage.setEmailConfigured(true);
                                        dunningMessage.setSmsConfigured(false);
                                        Gson gson = new Gson();
                                        gson.toJson(dunningMessage);
                                        dunningAudit("Customer Document", "Email", Long.valueOf(staffId), customerDocDetails1.getCustomer().getId(), null, LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                        messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_DOCUMENT_DUNNING_STAFF);
                                        kafkaMessageSender.send(new KafkaMessageData(dunningMessage,StaffExpiredMassage.class.getSimpleName(),KafkaConstant.CUSTOMER_DOCUMENT_DUNNING_TO_STAFF));
                                    }
                                }
                                Optional<TemplateNotification> optionalTemplate2 = templateRepository
                                        .findByTemplateName(RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE);
                                if (optionalTemplate2.isPresent()) {
                                    if (optionalTemplate2.get().isSmsEventConfigured()
                                            || optionalTemplate2.get().isEmailEventConfigured()) {
                                        CustomerExpiredDocumentMessage dunningMessage = new CustomerExpiredDocumentMessage(
                                                RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer, staffUser.getUsername(),staffId.longValue());
                                        dunningMessage.setEmailConfigured(true);
                                        dunningMessage.setSmsConfigured(false);
                                        Gson gson = new Gson();
                                        gson.toJson(dunningMessage);
                                        dunningAudit("Customer Document send to customer", "Email", Long.valueOf(staffId), null, Long.valueOf(customerDocDetails1.getCustomer().getId()), LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                        messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT);
                                        kafkaMessageSender.send(new KafkaMessageData(dunningMessage,CustomerExpiredDocumentMessage.class.getSimpleName()));
                                        if (ccEmail.length() > 1) {
                                            customer.setEmail(ccEmail);
                                            CustomerExpiredDocumentMessage dunningMessage2 = new CustomerExpiredDocumentMessage(
                                                    RabbitMqConstants.CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer, staffUser.getUsername(),staffId.longValue());
                                            dunningMessage.setEmailConfigured(true);
                                            dunningMessage.setSmsConfigured(false);
                                            Gson gson2 = new Gson();
                                            gson2.toJson(dunningMessage2);

                                            dunningAudit("Customer Document send to CC", "Email", Long.valueOf(staffId), null, Long.valueOf(customerDocDetails1.getCustomer().getId()), LocalDateTime.now(), dunningMessage2.toString(),staffUser.getMvnoId());
//                                            messageSender.send(dunningMessage2, RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT);
                                            kafkaMessageSender.send(new KafkaMessageData(dunningMessage,CustomerExpiredDocumentMessage.class.getSimpleName()));
                                        }

                                    }
                                }
                            }
                        }
                    }

                }
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            }
        }

    }

    void sendSMSNotificationForStaffPartnerDunning(List<PartnerdocDetails> partnerdocDetails, List<Long> partnerIds, String ccMobile) {
        // List<PartnerdocDetails> partnerdocDetailsCheck = partnerdocDetails.stream().filter(partnerdocDetails2 -> partnerdocDetails2.getStartDate() != null && partnerdocDetails2.getEndDate() != null).collect(Collectors.toList());
        for (PartnerdocDetails partnerdocDetails1 : partnerdocDetails) {
            try {
                Integer staffId = partnerdocDetails1.getCreatedById();
                StaffUser staffUser = staffUserRepository.findById(staffId).get();
                Partner partner = partnerRepository.findById(partnerdocDetails1.getPartner().getId()).get();
                Boolean flag = getFlagForDunningDuplication(null, "PartnerDocumentSMS", "PartnerDocumentSMS", LocalDateTime.now(), partner);
                if (flag) {
                    partner.setLastDunningDate(LocalDateTime.now());
                    partner.setDunningActivateFor("PartnerDocumentSMS");
                    partnerRepository.save(partner);
                    if (!partnerIds.isEmpty()) {
                        if (partnerIds.contains(partner.getId().longValue())) {
                            if (partner.getStatus().equalsIgnoreCase("Active")) {
                                Optional<TemplateNotification> optionalTemplate = templateRepository
                                        .findByTemplateName(RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE);
                                if (optionalTemplate.isPresent()) {
                                    if (optionalTemplate.get().isSmsEventConfigured()
                                            || optionalTemplate.get().isEmailEventConfigured()) {
                                        StaffExpiredMassage dunningMessage = new StaffExpiredMassage(RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE_HEADER, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, staffUser, partnerdocDetails1.getPartner().getName(),partnerdocDetails1.getPartner().getMvnoId(),partnerdocDetails1.getPartner().getBuId().intValue(),staffUser.getId().longValue());
                                        dunningMessage.setEmailConfigured(false);
                                        dunningMessage.setSmsConfigured(true);
                                        Gson gson = new Gson();
                                        gson.toJson(dunningMessage);
                                        dunningAudit("Partner Document", "SMS", Long.valueOf(staffId), null, Long.valueOf(partnerdocDetails1.getPartner().getId()), LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                        messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_DOCUMENT_DUNNING_STAFF);
                                        kafkaMessageSender.send(new KafkaMessageData(dunningMessage,StaffExpiredMassage.class.getSimpleName()));
                                    }
                                }
                                Optional<TemplateNotification> optionalTemplate2 = templateRepository
                                        .findByTemplateName(RabbitMqConstants.PARTNER_DUNNING_DOCUMENT_TEMPLATE);
                                if (optionalTemplate2.isPresent()) {
                                    if (optionalTemplate.get().isSmsEventConfigured()
                                            || optionalTemplate.get().isEmailEventConfigured()) {
                                        PartnerExpiredDocumentMessage dunningMessage = new PartnerExpiredDocumentMessage(
                                                RabbitMqConstants.PARTNER_DUNNING_DOCUMENT_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, partner, staffUser.getUsername(),staffId.longValue());
                                        dunningMessage.setEmailConfigured(false);
                                        dunningMessage.setSmsConfigured(true);
                                        Gson gson = new Gson();
                                        gson.toJson(dunningMessage);
                                        dunningAudit("Partner Document send to partner", "SMS", Long.valueOf(staffId), null, Long.valueOf(partnerdocDetails1.getPartner().getId()), LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
                                        kafkaMessageSender.send(new KafkaMessageData(dunningMessage,PartnerExpiredDocumentMessage.class.getSimpleName(),KafkaConstant.PARTNER_DUNNING_DOCUMENT));
//                                        messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT);
                                    }
                                    if (ccMobile.length() > 0) {
                                        partner.setMobile(ccMobile);
                                        if (optionalTemplate.get().isSmsEventConfigured()
                                                || optionalTemplate.get().isEmailEventConfigured()) {
                                            PartnerExpiredDocumentMessage dunningMessage = new PartnerExpiredDocumentMessage(
                                                    RabbitMqConstants.PARTNER_DUNNING_DOCUMENT_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, partner, staffUser.getUsername(),staffId.longValue());
                                            dunningMessage.setEmailConfigured(false);
                                            dunningMessage.setSmsConfigured(true);
                                            Gson gson = new Gson();
                                            gson.toJson(dunningMessage);
                                            dunningAudit("Partner Document send to CC", "SMS", Long.valueOf(staffId), null, Long.valueOf(partnerdocDetails1.getPartner().getId()), LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
                                            kafkaMessageSender.send(new KafkaMessageData(dunningMessage,PartnerExpiredDocumentMessage.class.getSimpleName(),KafkaConstant.PARTNER_DUNNING_DOCUMENT));
//                                            messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            }
        }

    }

    void sendEmailNotificationForStaffPartnerDunning(List<PartnerdocDetails> partnerdocDetails, List<Long> partnerIds, String ccEmail) {
        //  List<PartnerdocDetails> partnerdocDetailsCheck = partnerdocDetails.stream().filter(partnerdocDetails2 -> partnerdocDetails2.getStartDate() != null && partnerdocDetails2.getEndDate() != null).collect(Collectors.toList());
        for (PartnerdocDetails partnerdocDetails1 : partnerdocDetails) {
            try {
                Integer staffId = partnerdocDetails1.getCreatedById();
                StaffUser staffUser = staffUserRepository.findById(staffId).get();
                Partner partner = partnerRepository.findById(partnerdocDetails1.getPartner().getId()).get();
                Boolean flag = getFlagForDunningDuplication(null, "PartnerDocumentEmail", "PartnerDocumentEmail", LocalDateTime.now(), partner);
                if (flag) {
                    partner.setLastDunningDate(LocalDateTime.now());
                    partner.setDunningAction("PartnerDocumentEmail");
                    partnerRepository.save(partner);
                    if (!partnerIds.isEmpty()) {
                        if (partnerIds.contains(partner.getId().longValue())) {
                            if (partner.getStatus().equalsIgnoreCase("Active")) {
                                Optional<TemplateNotification> optionalTemplate = templateRepository
                                        .findByTemplateName(RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE);
                                if (optionalTemplate.isPresent()) {
                                    if (optionalTemplate.get().isSmsEventConfigured()
                                            || optionalTemplate.get().isEmailEventConfigured()) {
                                        Integer buId = null;
                                        if(partnerdocDetails1.getPartner().getBuId() != null){
                                            buId = partnerdocDetails1.getPartner().getBuId().intValue();
                                        }
                                        StaffExpiredMassage dunningMessage = new StaffExpiredMassage(
                                                RabbitMqConstants.EXPIRED_DOCUMENT_TEMPLATE_HEADER, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, staffUser, partnerdocDetails1.getPartner().getName(),partnerdocDetails1.getPartner().getMvnoId(),buId,staffUser.getId().longValue());
                                        dunningMessage.setEmailConfigured(true);
                                        dunningMessage.setSmsConfigured(false);
                                        Gson gson = new Gson();
                                        gson.toJson(dunningMessage);
                                        dunningAudit("Partner Document", "Email", Long.valueOf(staffId), null, Long.valueOf(partnerdocDetails1.getPartner().getId()), LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                        messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_DOCUMENT_DUNNING_STAFF);
                                        kafkaMessageSender.send(new KafkaMessageData(dunningMessage,StaffExpiredMassage.class.getSimpleName()));
                                    }
                                }
                                Optional<TemplateNotification> optionalTemplate2 = templateRepository
                                        .findByTemplateName(RabbitMqConstants.PARTNER_DUNNING_DOCUMENT_TEMPLATE);
                                if (optionalTemplate2.isPresent()) {
                                    if (optionalTemplate.get().isSmsEventConfigured()
                                            || optionalTemplate.get().isEmailEventConfigured()) {
                                        PartnerExpiredDocumentMessage dunningMessage = new PartnerExpiredDocumentMessage(
                                                RabbitMqConstants.PARTNER_DUNNING_DOCUMENT_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, partner, staffUser.getUsername(),staffId.longValue());
                                        dunningMessage.setEmailConfigured(true);
                                        dunningMessage.setSmsConfigured(false);
                                        Gson gson = new Gson();
                                        gson.toJson(dunningMessage);
                                        dunningAudit("Partner Document send to partner", "Email", Long.valueOf(staffId), null, Long.valueOf(partnerdocDetails1.getPartner().getId()), LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                        messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT);
                                        kafkaMessageSender.send(new KafkaMessageData(dunningMessage,PartnerExpiredDocumentMessage.class.getSimpleName(),KafkaConstant.PARTNER_DUNNING_DOCUMENT));
                                    }
                                    if (ccEmail.length() > 1) {
                                        partner.setEmail(ccEmail);
                                        if (optionalTemplate.get().isSmsEventConfigured()
                                                || optionalTemplate.get().isEmailEventConfigured()) {
                                            PartnerExpiredDocumentMessage dunningMessage = new PartnerExpiredDocumentMessage(
                                                    RabbitMqConstants.PARTNER_DUNNING_DOCUMENT_TEMPLATE_HEADER, optionalTemplate2.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, partner, staffUser.getUsername(),staffId.longValue());
                                            dunningMessage.setEmailConfigured(true);
                                            dunningMessage.setSmsConfigured(false);
                                            Gson gson = new Gson();
                                            gson.toJson(dunningMessage);
                                            dunningAudit("Partner Document send to partner to cc", "Email", Long.valueOf(staffId), null, Long.valueOf(partnerdocDetails1.getPartner().getId()), LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                                            messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT);
                                            kafkaMessageSender.send(new KafkaMessageData(dunningMessage,PartnerExpiredDocumentMessage.class.getSimpleName(),KafkaConstant.PARTNER_DUNNING_DOCUMENT));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            }
        }

    }

    void sendDeactivationNotificationForStaffPartnerDunning(List<PartnerdocDetails> partnerdocDetails, List<Long> partnerIds, String ccMobile, String ccEmail) {
        // List<PartnerdocDetails> partnerdocDetailsCheck = partnerdocDetails.stream().filter(partnerdocDetails2 -> partnerdocDetails2.getStartDate() != null && partnerdocDetails2.getEndDate() != null).collect(Collectors.toList());
        for (PartnerdocDetails partnerdocDetails1 : partnerdocDetails) {
            try {
                Integer staffId = partnerdocDetails1.getCreatedById();
                StaffUser staffUser = staffUserRepository.findById(staffId).get();
                Partner partner = partnerRepository.findById(partnerdocDetails1.getPartner().getId()).get();
                if (partner.getStatus().equalsIgnoreCase("Active")) {
                    if (!partnerIds.isEmpty()) {
                        if (partnerIds.contains(partner.getId().longValue())) {
                            getInactiveAllCustomerRelatedStaff(partnerdocDetails1.getPartner().getId(), partnerdocDetails1.getCreatedById());/**This method use for deativate all staff**/
                            Optional<TemplateNotification> optionalTemplate = templateRepository
                                    .findByTemplateName(RabbitMqConstants.PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF_TEMPLATE);
                            if (optionalTemplate.isPresent()) {
                                if (optionalTemplate.get().isSmsEventConfigured()
                                        || optionalTemplate.get().isEmailEventConfigured()) {
                                    Integer buId = null;
                                    if(partnerdocDetails1.getPartner().getBuId() != null){
                                        buId = partnerdocDetails1.getPartner().getBuId().intValue();
                                    }
                                    StaffExpiredMassage dunningMessage = new StaffExpiredMassage(
                                            RabbitMqConstants.PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF_TEMPLATE_HEADER, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, staffUser, partnerdocDetails1.getPartner().getName(),partnerdocDetails1.getPartner().getMvnoId(),buId,staffUser.getId().longValue());
                                    dunningMessage.setEmailConfigured(true);
                                    dunningMessage.setSmsConfigured(true);
                                    Gson gson = new Gson();
                                    gson.toJson(dunningMessage);
                                    dunningAudit("Partner Document", "Deative", Long.valueOf(staffId), null, Long.valueOf(partnerdocDetails1.getPartner().getId()), LocalDateTime.now(), dunningMessage.toString(),partner.getMvnoId());
//                                    messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF);
                                    kafkaMessageSender.send(new KafkaMessageData(dunningMessage, dunningMessage.getClass().getSimpleName()));

                                }
                                if (ccEmail.length() > 1 && ccMobile.length() > 1) {
                                    partner.setEmail(ccEmail);
                                    partner.setMobile(ccMobile);
                                    if (optionalTemplate.get().isSmsEventConfigured()
                                            || optionalTemplate.get().isEmailEventConfigured()) {
                                        Integer buId = null;
                                        if(partnerdocDetails1.getPartner().getBuId() != null){
                                            buId = partnerdocDetails1.getPartner().getBuId().intValue();
                                        }
                                        StaffExpiredMassage dunningMessage = new StaffExpiredMassage(
                                                RabbitMqConstants.PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF_TEMPLATE_HEADER, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, staffUser, partnerdocDetails1.getPartner().getName(),partnerdocDetails1.getPartner().getMvnoId(),buId,staffUser.getId().longValue());
                                        dunningMessage.setEmailConfigured(true);
                                        dunningMessage.setSmsConfigured(true);
                                        Gson gson = new Gson();
                                        gson.toJson(dunningMessage);
                                        dunningAudit("Partner Document", "Deative", Long.valueOf(staffId), null, Long.valueOf(partnerdocDetails1.getPartner().getId()), LocalDateTime.now(), dunningMessage.toString(),partner.getMvnoId());
//                                        messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF);
                                        kafkaMessageSender.send(new KafkaMessageData(dunningMessage, dunningMessage.getClass().getSimpleName()));
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage());
            }
        }

    }

    void sendDeactivationNotificationForDunning(List<Customers> customers, Integer dateDiff, String ccmail, String ccMobile) {
        //  List<Customers> distinctCustomers = customers.stream().distinct().collect(Collectors.toList());
        List<List<Customers>> batches = Lists.partition(customers, 1000);
        int threadCount = Runtime.getRuntime().availableProcessors()* 2; // Avoid too many threads
        LocalDateTime startTime=LocalDateTime.now();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Void>> futures = new ArrayList<>();
        for (List<Customers> batch : batches) {
            futures.add(executor.submit(() -> {
                try {
                        sendDeactivationNotificationInBatch(batch, dateDiff, ccmail, ccMobile);
                } catch (Exception e) {
                    log.error("Deactivation Customer batch size {}: {}", batch.size(), e.getMessage());
                }
                return null;
            }));
        }
        for (Future<Void> future : futures) {
            try {
                log.info("Feign call Successfull  for getDebidocId {}: {}", futures.size());
                future.get();
            } catch (Exception e) {
                log.error("Thread execution error: {}", e.getMessage());
            }
        }
        LocalDateTime endTime=LocalDateTime.now();
        Duration duration=Duration.between(startTime,endTime);
        System.out.println("Total Time taken to run dunning"+duration.toMinutes());
        System.out.println("Total Time taken to run dunning in hrs"+duration.toHours());
        executor.shutdown();


//        for (Customers customer : customers) {
//            CustomerLedgerDtlsPojo pojo = new CustomerLedgerDtlsPojo();
//            pojo.setCustId(customer.getId());
////            List<String> serviceNames = new ArrayList<>();
//            List<Long> cprIds = new ArrayList<>();
//            CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getWalletAmt(pojo);
//            Customers changeStatusCustomer = customersRepository.findCustomerById(customer.getId()).get();
//            System.out.println(changeStatusCustomer.getIsDunningActivate());
//            String pendingAmount = clientService.getValueByNameAndmvnoId("MINIMUM_PAYMENT_REQUIRED",changeStatusCustomer.getMvnoId());
//            Boolean flag = getFlagForDunnningDuplicationNew("Customer Payment", "Deactivation", null, changeStatusCustomer.getId(), null, LocalDateTime.now());
//            List<Long> cprIds2=new ArrayList<>();
//            if(customer.getCusttype().equalsIgnoreCase("Prepaid")){
//                cprIds2= customerPackageRepository.getPrepaidCustPackageIdBycustomeranddatediffforDeactivate(customer.getId(), dateDiff);
//            }else {
//                cprIds2 = customerPackageRepository.getPostpaidCustPackageIdBycustomeranddatediffDeactivate(customer.getId(), dateDiff);
//            }
//            Boolean invoiceflag = IsInvoiceInapprovalOrNot(cprIds2.stream().map(aLong -> aLong.intValue()).collect(Collectors.toList()));
//            if(cprIds2.size()>0) {
//                List<Integer> serviceMappingIds = custPlanMappingRepository.getAllByCustServiceMappingIdInCprIds(cprIds2.stream().map(aLong -> aLong.intValue()).collect(Collectors.toList()));
////                List<Integer> inventoryServiceMappingIds = customerInventoryMappingService.getServiceInventoryMapping(customer.getId());
////                if(!inventoryServiceMappingIds.isEmpty()){
////                    String remark = "Disable due to Unpaid";
////                    custPlanMappingService.changeStatusOfCustServices(inventoryServiceMappingIds, StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE, remark, false);
////                }
//                String remark = "Disable due to Unpaid";
//                System.out.println("$#####disable called######$");
//                Boolean isInvoicePaid = IsInvoiceNotPaid(cprIds2.stream().map(aLong -> aLong.intValue()).collect(Collectors.toList()));
//                if (customer.getWalletbalance() > Double.parseDouble(pendingAmount) && isInvoicePaid) {
//                    if (!CollectionUtils.isEmpty(serviceMappingIds)) {
//                        custPlanMappingService.changeStatusOfCustServices(serviceMappingIds, StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE, remark, false);
//                    }
//                }
//                List<PostpaidPlan> plan=new ArrayList<>();
//                if (flag && invoiceflag) {
//                    if (customer.getCusttype().equalsIgnoreCase("Postpaid")) {
//                        cprIds = customerPackageRepository.getPostpaidCustPackageIdBycustomeranddatediffDeactivate(customer.getId(), dateDiff);
//
//                        List<Date>  datelist= customerPackageRepository.getPostpaidEndDateBycustomeranddatediffDeactivate(customer.getId(), dateDiff);
//                        plan = customerPackageRepository.getPostpaidBycustomeranddatediffDeactivate(customer.getId(), dateDiff);
//                        if(plan.size()>0) {
//                            planname = plan.get(0).getName();
//                            planDate = datelist.stream()
//                                    .map(date -> LocalDate.parse(date.toString()))
//                                    .collect(Collectors.toList());
////                        serviceNames = customerPackageRepository.getServiceNameWithEndDateAndPlanIdForPostpaid(customer.getId(), planDate.get(0), plan.get(0).getId());
////                        PlanService planService = planServiceRepository.findByName(serviceNames.get(0));
////                        planService.getId();
//
//
//                        }else{
//                            System.out.println("No Customer Found for Termination");
//                        }
//                    }
//                    if (customer.getCusttype().equalsIgnoreCase("Prepaid")) {
//                        planDate = customerPackageRepository.getPrepaidEndDateBycustomeranddatediffDeactivate(customer.getId(), dateDiff);
//                        plan = customerPackageRepository.getPrepaidBycustomeranddatediffDeactivate(customer.getId(), dateDiff);
//                        planname = plan.get(0).getName();
////                        serviceNames = customerPackageRepository.getServiceNameWithStartDateAndPlanIdForPrepaid(customer.getId(), planDate.get(0));
//                    }
//                    if (Objects.isNull(infoPojo.getClosingBalance())) {
//                        infoPojo.setClosingBalance(0.0);
//                    }
//                    System.out.println("$#####Wallet Balance ######$"+customer.getWalletbalance());
//                    if (customer.getWalletbalance() > Double.parseDouble(pendingAmount) &&  plan.size()>0) {
//                        try {
////                            if (changeStatusCustomer.getStatus().equalsIgnoreCase("Active")) {
//                                //planname = getAllPlan.get(0).getPlanName();
//                                if (changeStatusCustomer.getStatus().equalsIgnoreCase("Active")) {
//                                    log.info("Customer ID: {} has grace day: {}", customer.getId(), customer.getGraceDay());
//                                    System.out.println("Customer ID: "+ customer.getId() +" has grace day :"+ customer.getGraceDay() );
//                                    System.out.println("$#####Deactivated ######$");
//                                    // changeStatusCustomer.setStatus("InActive");
////                                changeStatusCustomer.setIsDunningActivate(true);
////                                changeStatusCustomer.setLastDunningDate(LocalDateTime.now());
////                                changeStatusCustomer.setDunningActivateFor("Payment");
////                                changeStatusCustomer.setDunningAction("Deactivation");
////                                customersService.save(changeStatusCustomer);
////                                    String currencySymbol = clientService.getValueByName(ClientServiceConstant.CURRENCY_SYMBOL);
//                                    String currencySymbol = "";
//                                    ClientService currenyClientService = clientService.getByNameAndMvnoIdEquals(ClientServiceConstant.CURRENCY_SYMBOL, customer.getMvnoId());
//                                    if (currenyClientService != null) {
//                                        currencySymbol = currenyClientService.getValue();
//                                    }
//                                    Optional<TemplateNotification> optionalTemplate = templateRepository
//                                            .findByTemplateName(RabbitMqConstants.CUSTOMER_DEACTIVATION_TEMPLATE);
//                                    if (optionalTemplate.isPresent()) {
//                                        if (optionalTemplate.get().isSmsEventConfigured()
//                                                || optionalTemplate.get().isEmailEventConfigured()) {
//
//                                            ArrayList<String> st = new ArrayList<>();
//                                            LocalDateTime localDateTime = LocalDateTime.now();
//                                            CustomerDeactivationMessage dunningMessage = new CustomerDeactivationMessage(
//                                                    RabbitMqConstants.CUSTOMER_DEACTIVATION_TEMPLATE_HEADER, optionalTemplate.get(),
//                                                    RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer,
//                                                    infoPojo.getClosingBalance(), currencySymbol, "Insufficient Balance/Plan Expired", planname, planDate.get(0).toString(),(long) customersService.getLoggedInStaffId());
//                                            dunningMessage.setEmailConfigured(true);
//                                            dunningMessage.setSmsConfigured(true);
//                                            Gson gson = new Gson();
//                                            gson.toJson(dunningMessage);
//                                            dunningAudit("Customer Payment", "Deactivation", null, customer.getId(), null, LocalDateTime.now(), dunningMessage.toString(), customer.getMvnoId());
//                                            kafkaMessageSender.send(new KafkaMessageData(dunningMessage, CustomerDeactivationMessage.class.getSimpleName()));
////                                             messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_CUSTOMER_DEACTIVATION);
//
//
//                                        }
//                                        if (ccmail.length() > 0 && ccMobile.length() > 0) {
//                                            customer.setEmail(ccmail);
//                                            customer.setMobile(ccMobile);
//                                            CustomerDeactivationMessage dunningMessage = new CustomerDeactivationMessage(
//                                                    RabbitMqConstants.CUSTOMER_DEACTIVATION_TEMPLATE_HEADER, optionalTemplate.get(),
//                                                    RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer,
//                                                    infoPojo.getClosingBalance(), currencySymbol, "Insufficient Balance/Plan Expired", planname, planDate.get(0).toString(),(long) customersService.getLoggedInStaffId());
//                                            dunningMessage.setEmailConfigured(true);
//                                            dunningMessage.setSmsConfigured(true);
//                                            Gson gson = new Gson();
//                                            gson.toJson(dunningMessage);
//                                            dunningAudit("Customer Payment to CC", "Deactivation", null, customer.getId(), null, LocalDateTime.now(), dunningMessage.toString(), customer.getMvnoId());
////                                             messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_CUSTOMER_DEACTIVATION);
//                                            kafkaMessageSender.send(new KafkaMessageData(dunningMessage, CustomerDeactivationMessage.class.getSimpleName()));
//                                        }
//                                    }
//
//                                }
//
//                        } catch (Throwable e) {
//                            throw new RuntimeException(e.getMessage());
//                        }
//                    }
//
//                }else {
//                    System.out.println("No Customer Found for Termination");
//                }
//            }
//        }

    }

    void sendDeactivationAdvanceNotificationForDunning(List<Customers> customers, Integer dateDiff, String ccmail, String ccMobile) {
        //  List<Customers> distinctCustomers = customers.stream().distinct().collect(Collectors.toList());
       // String plan = null;
        String planname = "";
        for (Customers customer : customers) {
            CustomerLedgerDtlsPojo pojo = new CustomerLedgerDtlsPojo();
            pojo.setCustId(customer.getId());
            List<LocalDate> planDate = customerPackageRepository.getExpirydateBycustomeranddatediff(customer.getId(), dateDiff);
            List<PostpaidPlan> plan = customerPackageRepository.getPlanBycustomeranddatediff(customer.getId(), dateDiff);
            planname = plan.get(0).getName();
            CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getWalletAmt(pojo);
            Customers changeStatusCustomer = customersRepository.findById(customer.getId()).get();
            System.out.println(changeStatusCustomer.getIsDunningActivate());
            String pendingAmount = clientService.getValueByNameAndmvnoId("MINIMUM_PAYMENT_REQUIRED",changeStatusCustomer.getMvnoId());
            if (Objects.isNull(infoPojo.getClosingBalance())) {
                infoPojo.setClosingBalance(0.0);
            }
            if (infoPojo.getClosingBalance() > Double.parseDouble(pendingAmount)) {

                try {
                    if (changeStatusCustomer.getStatus().equalsIgnoreCase("Active")) {
                        if (changeStatusCustomer.getStatus().equalsIgnoreCase("Active")) {
                            //changeStatusCustomer.setStatus("InActive");
                            changeStatusCustomer.setIsDunningActivate(true);
                            changeStatusCustomer.setLastDunningDate(LocalDateTime.now());
                            changeStatusCustomer.setDunningActivateFor("Payment");
                            changeStatusCustomer.setDunningAction("Deactivation");
                            customersService.save(changeStatusCustomer);
                            String currencySymbol = clientService.getValueByNameAndmvnoId(ClientServiceConstant.CURRENCY_SYMBOL,changeStatusCustomer.getMvnoId());
                            Optional<TemplateNotification> optionalTemplate = templateRepository
                                    .findByTemplateName(RabbitMqConstants.CUSTOMER_DEACTIVATION_TEMPLATE);
                            if (optionalTemplate.isPresent()) {
                                if (optionalTemplate.get().isSmsEventConfigured()
                                        || optionalTemplate.get().isEmailEventConfigured()) {

                                    ArrayList<String> st = new ArrayList<>();
                                   // LocalDateTime localDateTime = LocalDateTime.now();
                                    CustomerDeactivationMessage dunningMessage = new CustomerDeactivationMessage(
                                            RabbitMqConstants.CUSTOMER_DEACTIVATION_TEMPLATE_HEADER, optionalTemplate.get(),
                                            RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer,
                                            infoPojo.getClosingBalance(), currencySymbol, "Insufficient Balance/Plan Expired", planname, planDate.get(0).toString(),(long) customersService.getLoggedInStaffId());
                                    dunningMessage.setEmailConfigured(true);
                                    dunningMessage.setSmsConfigured(true);
                                    Gson gson = new Gson();
                                    gson.toJson(dunningMessage);
                                    dunningAudit("AdvanceNotification", "Deactivation", null, customer.getId(), null, LocalDateTime.now(), dunningMessage.toString(),customer.getMvnoId());
//                                    messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_CUSTOMER_DEACTIVATION);
                                    kafkaMessageSender.send(new KafkaMessageData(dunningMessage, CustomerDeactivationMessage.class.getSimpleName()));


                                }
                                if (ccmail.length() > 0 && ccMobile.length() > 0) {
                                    customer.setEmail(ccmail);
                                    customer.setMobile(ccMobile);
                                    CustomerDeactivationMessage dunningMessage = new CustomerDeactivationMessage(
                                            RabbitMqConstants.CUSTOMER_DEACTIVATION_TEMPLATE_HEADER, optionalTemplate.get(),
                                            RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer,
                                            infoPojo.getClosingBalance(), currencySymbol, "Insufficient Balance/Plan Expired", planname, planDate.get(0).toString(),(long) customersService.getLoggedInStaffId());
                                    dunningMessage.setEmailConfigured(true);
                                    dunningMessage.setSmsConfigured(true);
                                    Gson gson = new Gson();
                                    gson.toJson(dunningMessage);
                                    dunningAudit("AdvanceNotification to CC", "Deactivation", null, customer.getId(), null, LocalDateTime.now(), dunningMessage.toString(),customer.getMvnoId());
//                                    messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_CUSTOMER_DEACTIVATION);
                                    kafkaMessageSender.send(new KafkaMessageData(dunningMessage, CustomerDeactivationMessage.class.getSimpleName()));
                                }
                            }

                        }
                    }
                } catch (Throwable e) {
                    throw new RuntimeException(e.getMessage());
                }
            }

        }
    }

    public LocalDateTime calculateTatEndDate(TatMatrixWorkFlowDetails details) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = details.getStartDateTime();
        long hours = 0;
        long minutes = 0;
        switch (details.getMunit().toLowerCase(Locale.ROOT)) {
            case "day":
                endDate = startDate.plusDays(Long.valueOf(details.getMtime()));
                break;
            case "hour":
                hours = Long.valueOf(details.getMtime());
                if (hours >= 24) {
                    long days = hours / 24;
                    long remainingHours = hours % 24;
                    endDate = startDate.plusDays(days).plusHours(remainingHours);
                } else {
                    endDate = startDate.plusHours(hours);
                }
                break;
            case "min":
                minutes = Long.valueOf(details.getMtime());
                if (minutes >= 60) {
                    long remainingMinutes = minutes % 60;
                    hours += minutes / 60;  // Use the existing 'hours' variable
                    if (hours >= 24) {
                        long days = hours / 24;
                        long remainingHours = hours % 24;
                        endDate = startDate.plusDays(days).plusHours(remainingHours).plusMinutes(remainingMinutes);
                    } else {
                        endDate = startDate.plusHours(hours).plusMinutes(remainingMinutes);
                    }
                } else {
                    endDate = startDate.plusMinutes(minutes);
                }
                break;
        }
        return endDate;
    }

//    public LocalDateTime calculateTatEndDate(TatMatrixWorkFlowDetails details) {
//        LocalDateTime endDate = LocalDateTime.now();
//        LocalDateTime startDate = details.getStartDateTime();
//        switch (details.getMunit().toLowerCase(Locale.ROOT)) {
//            case "day":
//                endDate = startDate.plusDays(Long.valueOf(details.getMtime()));
//                break;
//            case "hour":
//                endDate = startDate.plusHours(Long.valueOf(details.getMtime()));
//                break;
//            case "min":
//                endDate = startDate.plusMinutes(Long.valueOf(details.getMtime()));
//                break;
//        }
//        return endDate;
//    }

    private void activateServiceInEZBill() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<CustPlanMappping> expiredCustomerPlanMapping = custPlanMappingRepository.findAllDTVServiceExpiringToday();
        for (CustPlanMappping expiredCustPlanMapping : expiredCustomerPlanMapping) {
            List<CustPlanMappping> futureCustomerPlanMappingWithSameService = custPlanMappingRepository.getAllWithConnectionNumberAndForDTVService(expiredCustPlanMapping.getCustServiceMappingId());
            for (CustPlanMappping futurePlanMapping : futureCustomerPlanMappingWithSameService) {
                if (expiredCustPlanMapping.getGraceDateTime() != null && expiredCustPlanMapping.getGraceDateTime().isAfter(currentTime)) {
                    ezBillServiceUtility.deactivateService(expiredCustomerPlanMapping, 13);
                    ezBillServiceUtility.extendExpiryDateInEZBill(expiredCustPlanMapping, expiredCustPlanMapping.getGraceDateTime());
                } else {
                    ezBillServiceUtility.deactivateService(expiredCustomerPlanMapping, 13);
                    ezBillServiceUtility.renewPlanInEzBill(futurePlanMapping, expiredCustPlanMapping.getId(), "Renew");
                }
            }
        }
    }

    public LocalDateTime calculateTatBreachedReminderTime(LocalDateTime endDateTime) {
        LocalDateTime reminderTime = null;
        ClientService clientService = clientServiceSrv.getByName(TAT_BREACHED_REMIDER_TIME_NAME);
        String tatReminderBreachTime = null;
        if (clientService == null) {
            tatReminderBreachTime = String.valueOf("15-M");
        } else {
            tatReminderBreachTime = String.valueOf(clientService.getValue()).replaceAll("\\s+", "");
        }
        String reminderTimeMode = tatReminderBreachTime.substring(tatReminderBreachTime.indexOf('-') + 1);
        Integer remindetrTimeValue = Integer.valueOf(tatReminderBreachTime.substring(0, tatReminderBreachTime.indexOf('-')));

        switch (reminderTimeMode.toLowerCase(Locale.ROOT)) {
            case "d":
                reminderTime = endDateTime.minusDays(Long.parseLong(String.valueOf(remindetrTimeValue)));
                break;
            case "h":
                reminderTime = endDateTime.minusHours(Long.parseLong(String.valueOf(remindetrTimeValue)));
                break;
            case "m":
                reminderTime = endDateTime.minusMinutes(Long.parseLong(String.valueOf(remindetrTimeValue)));
                break;
        }
        return reminderTime;
    }


    public LocalDateTime calculateTatBreachedOverDueTime(LocalDateTime endDateTime) {
        LocalDateTime reminderTime = null;
        ClientService clientService = clientServiceSrv.getByName(TAT_OVERDUE_REMIDER_TIME_NAME);
        String tatOverDueReminderTime = null;
        if (clientService == null) {
            tatOverDueReminderTime = String.valueOf("15-M");
        }
        tatOverDueReminderTime = String.valueOf(clientService.getValue());
        String reminderTimeMode = tatOverDueReminderTime.substring(tatOverDueReminderTime.indexOf('-') + 1);
        Integer remindetrTimeValue = Integer.valueOf(tatOverDueReminderTime.substring(0, tatOverDueReminderTime.indexOf('-')));

        switch (reminderTimeMode.toLowerCase(Locale.ROOT)) {
            case "d":
                reminderTime = endDateTime.plusDays(Long.parseLong(String.valueOf(remindetrTimeValue)));
                break;
            case "h":
                reminderTime = endDateTime.plusHours(Long.parseLong(String.valueOf(remindetrTimeValue)));
                break;
            case "m":
                reminderTime = endDateTime.plusMinutes(Long.parseLong(String.valueOf(remindetrTimeValue)));
                break;
        }
        return reminderTime;
    }


    public void sendReminderforTat(String mobileNumber, String emailId, String staffName, String parentStaffName, String caseNumber, Integer mvnoId) {

        try {

            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.TICKET_TAT_REMINDER_NOTIFICATION);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    Optional<StaffUser> staffUser = Optional.ofNullable(staffUserRepository.findStaffUserByUsername(staffName));
                    Long buId = null;
                    if(staffUser.isPresent()) {
                        if (Objects.nonNull(staffUser.get().getBusinessUnit())) {
                            buId = staffUser.get().getBusinessUnit().getId();
                        }
                    }
                    TicketTatReminderNotification ticketTatReminderNotification = new TicketTatReminderNotification(RabbitMqConstants.TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION_MSG,
                            optionalTemplate.get(),
                            RabbitMqConstants.TICKET_TAT_REMINDER_NOTIFICATION_MSG,
                            mobileNumber,
                            emailId,
                            mvnoId,
                            staffName,
                            parentStaffName,
                            caseNumber,buId);
                    Gson gson = new Gson();
                    gson.toJson(ticketTatReminderNotification);
                    kafkaMessageSender.send(new KafkaMessageData(ticketTatReminderNotification, TicketTatReminderNotification.class.getSimpleName()));
//                    messageSender.send(ticketTatReminderNotification, RabbitMqConstants.QUEUE_TICKET_TAT_BREACHED_REMINDER);
                }
            } else {
                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("Ticket reminder not send to parent staff");
            }


        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    public void sendReminderforOverDueTat(String mobileNumber, String emailId, String staffName, String parentStaffName, String caseNumber, Integer mvnoId) {

        try {

            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    TicketTatOverDueNotification ticketTatOverDueNotification = new TicketTatOverDueNotification(RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY,
                            optionalTemplate.get(),
                            RabbitMqConstants.TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION_MSG,
                            mobileNumber,
                            emailId,
                            mvnoId,
                            staffName,
                            parentStaffName,
                            caseNumber);
                    Gson gson = new Gson();
                    gson.toJson(ticketTatOverDueNotification);
                    // messageSender.send(ticketTatOverDueNotification, RabbitMqConstants.QUEUE_TICKET_OVERDUE_TAT_BREACHED_REMINDER);
                }
            } else {
                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("Ticket reminder not send to parent staff");
            }


        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    // @Scheduled(cron = "${cronJobTimeForTatOverDueMatrix}")
    public void runSchedulerForTATOverDue() {
        System.out.println("***** Adopt Api gateway scheduler  for TAT_OVERDUE matrix action  Starting *****");
        QTatMatrixWorkFlowDetails qTatMatrixWorkFlowDetails = QTatMatrixWorkFlowDetails.tatMatrixWorkFlowDetails;
        BooleanExpression booleanExpression = qTatMatrixWorkFlowDetails.isNotNull().and(qTatMatrixWorkFlowDetails.isActive.eq(false).and(qTatMatrixWorkFlowDetails.isOverDueReminder.eq(true)));
        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetails = (List<TatMatrixWorkFlowDetails>) tatMatrixWorkFlowDetailsRepo.findAll(booleanExpression);
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (!CollectionUtils.isEmpty(tatMatrixWorkFlowDetails)) {
            for (TatMatrixWorkFlowDetails details : tatMatrixWorkFlowDetails) {
                StaffUser staffUser = new StaffUser();
                staffUser = staffUserRepository.findById(details.getStaffId()).orElse(null);
                StaffUser parentStaffUser = new StaffUser();
                parentStaffUser = staffUserRepository.findById(details.getParentId()).orElse(null);

                LocalTime time = LocalTime.now();
                time = LocalTime.of(time.getHour(), time.getMinute());
                LocalDateTime endDateTime = null;
                if (details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
                    Case casedataa = caseRepository.findById(details.getEntityId().longValue()).get();
                    if (casedataa.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)) {
                        LocalDate date = LocalDate.now();
                        LocalDateTime localDateTime = LocalDateTime.of(casedataa.getNextFollowupDate(), casedataa.getNextFollowupTime());
                        if (date.isEqual(casedataa.getNextFollowupDate())) {
                            if (time.equals(casedataa.getNextFollowupTime()) || time.isBefore(casedataa.getNextFollowupTime())) {
                                endDateTime = LocalDateTime.of(casedataa.getNextFollowupDate(), casedataa.getNextFollowupTime());
                            }
                        }
                    } else {
                        endDateTime = calculateTatEndDate(details);
                    }
                    //calculate reminder time and send notification before tat breach
                    //LocalDateTime reminderTime = calculateTatBreachedReminderTime(endDateTime).truncatedTo(ChronoUnit.MINUTES);
                    LocalDateTime overDueTime = calculateTatBreachedOverDueTime(endDateTime).truncatedTo(ChronoUnit.MINUTES);
                    if (overDueTime.equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
                        sendReminderforOverDueTat(parentStaffUser.getPhone(), parentStaffUser.getEmail(), staffUser.getUsername(), parentStaffUser.getUsername(), casedataa.getCaseNumber(), parentStaffUser.getMvnoId());
                        details.setIsOverDueReminder(false);
                    }
                    //details.setNextFollowUpDate(endDateTime);
                    //calculatedate(endDateTime, details, currentDateTime);
                } else {
                    endDateTime = calculateTatEndDate(details);
                    //details.setNextFollowUpDate(endDateTime);
                    //calculatedate(endDateTime, details, currentDateTime);
                }
                tatMatrixWorkFlowDetailsRepo.save(details);

//                if (endDateTime != null) {
//
//                    if (endDateTime.equals(currentDateTime) || endDateTime.isBefore(currentDateTime)) {
//                        switch (details.getAction()) {
//                            case CommonConstants.TICKET_ACTION.BOTH:
//                                tatUtils.assignToNextApprovalStaff(details);
//                                if (details.getParentId() != null)
//                                    tatUtils.sendNotificationToStaff(details);
//                                break;
//                            case CommonConstants.TICKET_ACTION.REASSIGN:
//                                tatUtils.assignToNextApprovalStaff(details);
//                                break;
//                            case CommonConstants.TICKET_ACTION.NOTIFICATION:
//                                if (details.getParentId() != null)
//                                    tatUtils.sendNotificationToStaff(details);
//                                break;
//                        }
//                    }
//                }
            }
        }
        System.out.println("***** Adopt Api gateway scheduler  for TAT_OVERDUE matrix action  End *****");
    }

    void sendEmailNotificationForAdvance(List<Customers> customers, Integer dateDiff, String ccEmail, String token, Boolean isGeneratePaymentLink) {
        log.info("EMAIL FOR ADVANCE DUNNING STARTED WITH CUSTOMER : " + customers.size());
        try {

            int batchSize = 500;
            List<List<Customers>> customerBatches = Lists.partition(customers, batchSize);
            customerBatches.parallelStream().forEach(batch -> {
                        List<Integer> customerIds = batch.stream().map(Customers::getId).collect(Collectors.toList());

                        Map<Integer, Boolean> flagMap = getFlagsForDunningDuplicationEmailNew(customerIds);
                        Map<Integer, List<LocalDate>> expiryDatesMap = getExpiryDates(customerIds, dateDiff);
                        Map<Integer, List<String>> postpaidPlanMap = getPostpaidPlans(customerIds);
                        Map<Integer, Double> paymentMap = getTotalPayMent(customerIds);
                        Map<Integer, List<LocalDate>> startDateMap = getStartDates(customerIds);
                        Map<Integer, List<LocalDate>> endDateMap = getEndDates(customerIds);
                        Map<Integer, List<LocalDateTime>> dueDateMap = getDuedate(customerIds, dateDiff);
                        Map<Integer, Double> taxAmountMap = getTaxAmounts(customerIds);
                        Map<Integer, Double> subtotalMap = getSubtotal(customerIds);
                        Map<Integer, Double> totalDueMap = getTotalDue(customerIds);

//                        Map<Integer, String> invoiceNumber = getInvoiceNumber(customerIds, dateDiff);
                        Map<Integer,List<String>> debitdocId=getDebidocId(customerIds,dateDiff,token);

                        Map<Integer, Double> taxPercentageMap = new HashMap<>();
                        try {
                            Map<Integer, List<Double>> taxResponse = getTaxPercentagesInParallel(customerIds,token);
                            if (!taxResponse.isEmpty()) {
                                taxResponse.forEach((customerId, taxList) -> {
                                    Double totalTax = taxList.stream().mapToDouble(Double::doubleValue).sum();  // Sum all taxes
                                    taxPercentageMap.put(customerId, totalTax);
                                });
                            }
                        } catch (Exception e) {
                            log.error("Error fetching tax percentages for customers: " + customerIds, e);
                        }


                        Map<Integer, Double> walletBalanceMap = new HashMap<>();

                        List<Integer> customerIdList = customers.stream()
                                .map(Customers::getId)
                                .collect(Collectors.toList());

                        log.info("Requesting wallet balances for customer IDs: {}", customerIdList);

                        try {
                            List<CustomerLedgerDtlsPojo> pojoList = new ArrayList<>();
                            for (Integer id : customerIdList) {
                                CustomerLedgerDtlsPojo pojo = new CustomerLedgerDtlsPojo();
                                pojo.setCustId(id);
                                pojoList.add(pojo);
                            }
                            if (!pojoList.isEmpty()) {
                                Map<Integer, Double> responseMap= getWalletAmountsInParallel(pojoList,token);
                                log.info("Total customers with balances fetched: {}", walletBalanceMap.size());
                                 walletBalanceMap.putAll(responseMap);
                                    log.info("Parsed wallet balances: {}", walletBalanceMap);
                            }
                        } catch (Exception e) {
                            log.error("Error fetching wallet balances for customers: {}", customerIdList, e);
                        }

                Map<Integer, String> paymentUrlMap = (Objects.nonNull(isGeneratePaymentLink) && Boolean.TRUE.equals(isGeneratePaymentLink))
                        ? generatePaymentLinks(customerIds, token)
                        : new HashMap<>();

                Optional<TemplateNotification> optionalTemplate = templateRepository
                        .findByTemplateName(RabbitMqConstants.CUSTOMER_DUNNING_ADVANCE_NOTIFICATION_TEMPLATE);

                batch.forEach(customer -> {
                    if (flagMap.getOrDefault(customer.getId(), false)) {
                        try {
                         customersRepository.updateDunningStatus(customer.getId(), LocalDateTime.now());

                            List<String> postpaidPlan = postpaidPlanMap.getOrDefault(customer.getId(), new ArrayList<>());
                            List<LocalDate> expiryDate = expiryDatesMap.getOrDefault(customer.getId(), new ArrayList<>());
                            List<LocalDate> startDate = startDateMap.getOrDefault(customer.getId(), new ArrayList<>());
                            List<LocalDate>endDate = endDateMap.getOrDefault(customer.getId(), new ArrayList<>());
                            List<LocalDateTime> dueDate = dueDateMap.getOrDefault(customer.getId(), new ArrayList<>());

                            if (!postpaidPlan.isEmpty()) {
                                customer.setPlanName(postpaidPlan.get(0));
                                ClientService clientService1 = clientService.getCurrencyByNameAndMvnoId(ClientServiceConstant.CURRENCY_SYMBOL, customer.getMvnoId());
                                String currencySymbol = clientService1.getValue();
                                String paymentUrl = paymentUrlMap.getOrDefault(customer.getId(), null);
                                Double amount = paymentMap.getOrDefault(customer.getId(), 0.0);
                                Double taxAmount = taxAmountMap.getOrDefault(customer.getId(), 0.0);
                                Double taxPercentage = taxPercentageMap.getOrDefault(customer.getId(), 0.0);
                                Double subTotal = subtotalMap.getOrDefault(customer.getId(), 0.0);
                                Double totalDue = totalDueMap.getOrDefault(customer.getId(), 0.0);
                                Double walletBalance = walletBalanceMap.getOrDefault(customer.getId().toString(), 0.0);
                                String debitdocNumber= Optional.ofNullable(debitdocId.get(customer.getId()))
                                        .filter(list -> !list.isEmpty())
                                        .map(list -> list.get(list.size() - 1))
                                        .orElse(null);
                                if (optionalTemplate.isPresent() && "Active".equalsIgnoreCase(customer.getStatus())) {
                                    TemplateNotification template = optionalTemplate.get();

                                    if (template.isSmsEventConfigured() || template.isEmailEventConfigured()) {
                                        sendAdvanceDunningEmailNotificationInBatch(customer, template, expiryDate, startDate, endDate, currencySymbol, paymentUrl, amount, taxAmount, taxPercentage, dueDate, subTotal, totalDue, walletBalance, debitdocNumber);
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            log.error("Error processing customer ID: " + customer.getId(), e);
                        }
                    }
                });
            });
        } catch (Exception e) {
            log.error("Error in sendEmailNotificationForAdvance", e);
        }

    }

private Map<Integer, List<String>> getDebidocId(List<Integer> customerIds, Integer dateDiff, String token) {
    Map<Integer, List<String>> invoiceNumberMap = new ConcurrentHashMap<>();
    List<Integer> debitDocIds = debitDocRepository.getInvoiceNumber(customerIds, dateDiff);

    // Split into batches of 100
    List<List<Integer>> batches = Lists.partition(debitDocIds, BATCH_SIZE);
    int threadCount = Runtime.getRuntime().availableProcessors()* 2; // Avoid too many threads

    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    List<Future<Void>> futures = new ArrayList<>();

    for (List<Integer> batch : batches) {
        futures.add(executor.submit(() -> {
            try {
                log.info("Feign call for customer  {}: {}",batch);
                ResponseEntity<Map<Integer, List<String>>> response = revenueClient.getDebitDocNumber(token, batch);
                log.info("Feign call Successfull  {}: {}", batch.size());
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    invoiceNumberMap.putAll(response.getBody());
                }
            } catch (Exception e) {
                log.error("Feign call failed for batch size {}: {}", batch.size(), e.getMessage());
            }
            return null;
        }));
    }
    for (Future<Void> future : futures) {
        try {
            log.info("Feign call Successfull  for getDebidocId {}: {}", futures.size());
            future.get();
        } catch (Exception e) {
            log.error("Thread execution error: {}", e.getMessage());
        }
    }

    executor.shutdown();
    return invoiceNumberMap;
}

    private Map<Integer, Double> getTotalDue(List<Integer> customerIds) {
        return debitDocRepository.getTotalDueByCustomerIds(customerIds)
                .stream()
                .collect(Collectors.toMap(
                        obj -> (Integer) obj[0],
                        obj -> (Double) obj[1],
                        Double::sum
                ));
    }

    private Map<Integer, Double> getSubtotal(List<Integer> customerIds) {
        return debitDocRepository.getSubtotalByCustomerIds(customerIds)
                .stream()
                .collect(Collectors.toMap(
                        obj -> (Integer) obj[0],
                        obj -> (Double) obj[1],
                        Double::sum
                ));
    }

    private Map<Integer, List<LocalDateTime>> getDuedate(List<Integer> customerIds, int dateDiff) {
        return debitDocRepository.getDueDatesByCustomerIds(customerIds, dateDiff)
                .stream()
                .collect(Collectors.groupingBy(
                        obj -> (Integer) obj[0],
                        Collectors.mapping(obj -> (LocalDateTime) obj[1], Collectors.toList())
                ));
    }


    void sendSMSNotificationForDunningAdvance(List<Customers> customers, Integer dateDiff, String ccMobile,String token,Boolean isGeneratepaymentLink) {
        //  List<Customers> distinctCustomers = customers.stream().distinct().collect(Collectors.toList());
        log.info("SMS FOR ADVANCE DUNNING STARTED WITH CUSTOMER : "+customers.size());
        try {
            int batchSize = 500;
            List<List<Customers>> customerBatches = Lists.partition(customers, batchSize);
            customerBatches.parallelStream().forEach(batch -> {
                List<Integer> customerIds = batch.stream().map(Customers::getId).collect(Collectors.toList());
                Map<Integer, Boolean> flagMap = getFlagsForDunningDuplicationNew(customerIds);
                Map<Integer, List<LocalDate>> expiryDatesMap = getExpiryDates(customerIds, dateDiff);
                Map<Integer, List<String>> postpaidPlanMap = getPostpaidPlans(customerIds);
                Map<Integer, Double> paymentMap = getTotalPayMent(customerIds);
                Map<Integer, List<LocalDate>> startDateMap = getStartDates(customerIds);
                Map<Integer, List<LocalDate>> endDateMap = getEndDates(customerIds);
                Map<Integer, List<LocalDateTime>> dueDateMap = getDuedate(customerIds, dateDiff);
                Map<Integer, Double> taxAmountMap = getTaxAmounts(customerIds);
                Map<Integer, Double> subtotalMap = getSubtotal(customerIds);
                Map<Integer, Double> totalDueMap = getTotalDue(customerIds);
                Map<Integer,List<String>> debitdocId=getDebidocId(customerIds,dateDiff,token);
                Map<Integer, Double> taxPercentageMap = new HashMap<>();
                try {
                    Map<Integer, List<Double>> taxResponse = getTaxPercentagesInParallel( customerIds,token);
                    if (!taxResponse.isEmpty()) {
                        taxResponse.forEach((customerId, taxList) -> {
                            Double totalTax = taxList.stream().mapToDouble(Double::doubleValue).sum();  // Sum all taxes
                            taxPercentageMap.put(customerId, totalTax);
                        });
                    }
                } catch (Exception e) {
                    log.error("Error fetching tax percentages for customers: " + customerIds, e);
                }


                Map<Integer, Double> walletBalanceMap = new HashMap<>();

                List<Integer> customerIdList = customers.stream()
                        .map(Customers::getId)
                        .collect(Collectors.toList());

                log.info("Requesting wallet balances for customer IDs: {}", customerIdList);

                try {
                    List<CustomerLedgerDtlsPojo> pojoList = new ArrayList<>();
                    for (Integer id : customerIdList) {
                        CustomerLedgerDtlsPojo pojo = new CustomerLedgerDtlsPojo();
                        pojo.setCustId(id);
                        pojoList.add(pojo);
                    }

                    if (!pojoList.isEmpty()) {
//                        ResponseEntity<?> response = revenueClient.getWalletAmounts(pojoList, token);
//
//                        log.info("Wallet API response status: {}", response.getStatusCode());
//                        log.info("Wallet API response body: {}", response.getBody());
//
//                        if (response != null && response.getBody() instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<Integer, Double> responseMap = getWalletAmountsInParallel(pojoList, token);
                            walletBalanceMap.putAll(responseMap);

                            log.info("Parsed wallet balances: {}", walletBalanceMap);
//                        } else {
//                            log.warn("Unexpected response body type: {}", response.getBody().getClass().getName());
//                        }
                    }
                } catch (Exception e) {
                    log.error("Error fetching wallet balances for customers: {}", customerIdList, e);
                }

                Map<Integer, String> paymentUrlMap;
                if (Objects.nonNull(isGeneratepaymentLink) && Boolean.TRUE.equals(isGeneratepaymentLink)) {
                    paymentUrlMap = generatePaymentLinks(customerIds, token);
                } else {
                    paymentUrlMap = new HashMap<>();
                }
                Optional<TemplateNotification> optionalTemplate = templateRepository
                        .findByTemplateName(RabbitMqConstants.CUSTOMER_DUNNING_ADVANCE_NOTIFICATION_TEMPLATE);

                batch.forEach(customer -> {
                    if (flagMap.getOrDefault(customer.getId(), false)) {
                        try {
                            customersRepository.updateDunningStatus(customer.getId(), LocalDateTime.now());

                            List<String> postpaidPlan = postpaidPlanMap.getOrDefault(customer.getId(), new ArrayList<>());
                            List<LocalDate> expiryDate = expiryDatesMap.getOrDefault(customer.getId(), new ArrayList<>());
                            List<LocalDate> startDate = startDateMap.getOrDefault(customer.getId(), new ArrayList<>());
                            List<LocalDate> endDate = endDateMap.getOrDefault(customer.getId(), new ArrayList<>());
                            List<LocalDateTime> dueDate = dueDateMap.getOrDefault(customer.getId(), new ArrayList<>());
//                            Map<Integer, String> invoiceNumberMap = getInvoiceNumber(customerIds, dateDiff);

                            if (!postpaidPlan.isEmpty()) {
                                customer.setPlanName(postpaidPlan.get(0));
                                ClientService clientService1 = clientService.getCurrencyByNameAndMvnoId(ClientServiceConstant.CURRENCY_SYMBOL, customer.getMvnoId());
                                String currencySymbol = clientService1.getValue();
                                String paymentUrl = paymentUrlMap.getOrDefault(customer.getId(), null);
                                Double amount = paymentMap.getOrDefault(customer.getId(), 0.0);
                                Double taxAmount = taxAmountMap.getOrDefault(customer.getId(), 0.0);
                                Double taxPercentage = taxPercentageMap.getOrDefault(customer.getId(), 0.0);
                                Double subTotal = subtotalMap.getOrDefault(customer.getId(), 0.0);
                                Double totalDue = totalDueMap.getOrDefault(customer.getId(), 0.0);
                                Double walletBalance = walletBalanceMap.getOrDefault(customer.getId().toString(), 0.0);
                                String invoiceNumber= Optional.ofNullable(debitdocId.get(customer.getId()))
                                        .filter(list -> !list.isEmpty())
                                        .map(list -> list.get(list.size() - 1))
                                        .orElse(null);

                                if (optionalTemplate.isPresent() && customer.getStatus().equalsIgnoreCase("Active")) {
                                    TemplateNotification template = optionalTemplate.get();

                                    if (template.isSmsEventConfigured() || template.isEmailEventConfigured()) {
                                        sendAdvanceDunningNotificationInBatch(customer, template, expiryDate, startDate, endDate, currencySymbol, paymentUrl, amount, taxAmount, taxPercentage, dueDate, subTotal, totalDue, walletBalance,invoiceNumber);
                                    }
                                    if (ccMobile.length() > 1) {
                                        customer.setMobile(ccMobile);
                                        sendAdvanceDunningNotificationInBatch(customer, template, expiryDate, startDate,endDate, currencySymbol, paymentUrl, amount, taxAmount, taxPercentage, dueDate, subTotal, totalDue, walletBalance,invoiceNumber);
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    }
                });
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public void dunningAudit(String event, String action, Long staffid, Integer custid, Long partnerid, LocalDateTime dunningTime, String dunningMessage,Integer mvnoid) {
        DunningHistory dunningHistory = new DunningHistory();
        dunningHistory.setEventName(event);
        dunningHistory.setAction(action);
        if (Objects.nonNull(staffid)) {
            dunningHistory.setStaffid(staffid);
        }
        if (Objects.nonNull(custid)) {
            dunningHistory.setCustid(custid);
        }
        if (Objects.nonNull(partnerid)) {
            dunningHistory.setPartnerid(partnerid);
        }
        dunningHistory.setDunningMessageDate(dunningTime);
        dunningHistory.setDunningMessage(dunningMessage);
        dunningHistory.setMvnoid(mvnoid);
        dunningHistoryRepository.save(dunningHistory);
    }

    /**
     * This method use for deactivate staff in partner dunning deactivation method
     **/
    public void getInactiveAllCustomerRelatedStaff(Integer partnerId, Integer staffId) {
        List<Integer> AllPartnerId = new ArrayList<>();
        List<Integer> childPartnerIds = partnerRepository.getChildPartnerIdFromParentPartnerId(partnerId);
        AllPartnerId.add(partnerId);
        if (!childPartnerIds.isEmpty()) {
            AllPartnerId.addAll(childPartnerIds);
        }
        List<StaffUser> deactivateStaffList = staffUserRepository.getAllStaffUserByPartnerIds(AllPartnerId);
        deactivateStaffList = deactivateStaffList.stream().peek(staffUser -> staffUser.setStatus("INACTIVE")).collect(Collectors.toList()); /**Deativate All staff that partner document fail**/
        staffUserRepository.saveAll(deactivateStaffList);
        List<Partner> partnerList = partnerRepository.getAllPartnerByPartnerIds(AllPartnerId);
        partnerList = partnerList.stream().peek(partner -> partner.setStatus("INACTIVE")).collect(Collectors.toList());
        partnerRepository.saveAll(partnerList);
        partnerList.forEach(partner -> PartnerDeactivateMessageSend(partner, staffId));

    }

    public void PartnerDeactivateMessageSend(Partner partner, Integer staffId) {
        StaffUser staffUser = staffUserRepository.findById(staffId).get();
        Optional<TemplateNotification> optionalTemplate = templateRepository
                .findByTemplateName(RabbitMqConstants.PARTNER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE);
        if (optionalTemplate.isPresent()) {
            if (optionalTemplate.get().isSmsEventConfigured()
                    || optionalTemplate.get().isEmailEventConfigured()) {
                PartnerExpiredDocumentMessage dunningMessage = new PartnerExpiredDocumentMessage(
                        RabbitMqConstants.PARTNER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, partner, staffUser.getUsername(),staffId.longValue());
                dunningMessage.setEmailConfigured(true);
                dunningMessage.setSmsConfigured(true);
                Gson gson = new Gson();
                gson.toJson(dunningMessage);
                dunningAudit("Partner Document Deactivation Send to Partner", "Deative", Long.valueOf(staffId), null, Long.valueOf(partner.getId()), LocalDateTime.now(), dunningMessage.toString(),staffUser.getMvnoId());
//                messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION);
                kafkaMessageSender.send(new KafkaMessageData(dunningMessage, dunningMessage.getClass().getSimpleName(),KafkaConstant.PARTNER_DUNNING_DOCUMENT_DEACTIVATION));
            }
        }
    }

    /**Send mvno document expired email started**/

     public void sendMvnoDocumentDunning(List<MvnoDocDetails> mvnoDocDetailsList , String ccEmail , boolean sendEmail , boolean sendSms){
         log.info("mvno document list size: "+mvnoDocDetailsList.size());
         for(MvnoDocDetails mvnoDocDetails : mvnoDocDetailsList){
             String status = "ACTIVE";
             List<StaffUser> staffUserList = staffUserRepository.StatusAndIsDeleteIsFalseAndMvnoIdIn(status , Collections.singletonList(mvnoDocDetails.getMvno().getId().intValue()));
             if(!staffUserList.isEmpty()){
                 StaffUser staffUser = staffUserList.get(0);
//                 if(staffUser.getCreatedById() == 1){
                     boolean flag = true;
                     if(sendEmail){
                          flag = getFlagForDunnningDuplicationNew("MVNO_DOCUMENT_"+mvnoDocDetails.getUniquename(), "Email", Long.valueOf(staffUser.getId()), mvnoDocDetails.getMvno().getCustInvoiceRefId(), null, LocalDateTime.now());
                     }
                     if(sendSms){
                          flag = getFlagForDunnningDuplicationNew("MVNO_DOCUMENT_"+mvnoDocDetails.getUniquename(), "SMS", Long.valueOf(staffUser.getId()), mvnoDocDetails.getMvno().getCustInvoiceRefId(), null, LocalDateTime.now());
                     }
                     if(flag){
                         mvnoDocumentNotification(staffUser , mvnoDocDetails , ccEmail , sendEmail , sendSms);
                         if(sendEmail) {
                             dunningAudit("MVNO_DOCUMENT_" + mvnoDocDetails.getUniquename(), "Email", Long.valueOf(staffUser.getId()),Optional.ofNullable(mvnoDocDetails).map(MvnoDocDetails::getMvno).map(Mvno::getCustInvoiceRefId).orElse(null), null, LocalDateTime.now(), "Mvno document email send",staffUser.getMvnoId());
                         }
                         if(sendSms){
                             dunningAudit("MVNO_DOCUMENT_" + mvnoDocDetails.getUniquename(), "SMS", Long.valueOf(staffUser.getId()), Optional.ofNullable(mvnoDocDetails).map(MvnoDocDetails::getMvno).map(Mvno::getCustInvoiceRefId).orElse(null), null, LocalDateTime.now(), "Mvno document sms send",staffUser.getMvnoId());
                         }
                         dunningAudit("MVNO_DOCUMENT_" + mvnoDocDetails.getUniquename(), CommonConstants.INACTIVE_STATUS , Long.valueOf(staffUser.getId()), Optional.ofNullable(mvnoDocDetails).map(MvnoDocDetails::getMvno).map(Mvno::getCustInvoiceRefId).orElse(null), null, LocalDateTime.now(), "Mvno document sms send",staffUser.getMvnoId());
                     }
                     else{
                         log.info("Email or sms is already send");
                     }
//                 }
//                 else{
//                     log.info("mvno staff is not active");
//                 }
             }
             else{
                 log.info("no staff found by given mvnoId");
             }
         }
     }


    /**Send mvno document expired email ended**/

    /**Send MVNO document expired notification code started**/
    public void mvnoDocumentNotification(StaffUser staffUser , MvnoDocDetails mvnoDocDetails , String ccEmail , boolean sendEmail , boolean sendSms){
        log.info("come in document notification for document: "+mvnoDocDetails.getFilename());
      MvnoDocumentDunningMessage mvnoDocumentDunningMessage = new MvnoDocumentDunningMessage(staffUser , mvnoDocDetails , ccEmail , sendEmail , sendSms);
      Gson gson = new Gson();
      gson.toJson(mvnoDocumentDunningMessage);
      kafkaMessageSender.send(new KafkaMessageData(mvnoDocumentDunningMessage,MvnoDocumentDunningMessage.class.getSimpleName(),KafkaConstant.SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION));
//      messageSender.send(mvnoDocumentDunningMessage , RabbitMqConstants.QUEUE_SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION);
    }

    /**Send MVNO document expired notification code ended**/

    /**  Send MVNo PAYMENT **/
    public void mvnoPaymentAdvanceNotification(StaffUser staffUser , DebitDocument mvnoDocDetails , String ccEmail , boolean sendEmail , boolean sendSms,LocalDate duedate){
        log.info("come in document notification for document: "+mvnoDocDetails.getId());
        MvnoPaymentDunningMessage mvnoDocumentDunningMessage = new MvnoPaymentDunningMessage(staffUser , mvnoDocDetails , ccEmail , sendEmail , sendSms, duedate);
        Gson gson = new Gson();
        gson.toJson(mvnoDocumentDunningMessage);
        kafkaMessageSender.send(new KafkaMessageData(mvnoDocumentDunningMessage,MvnoPaymentDunningMessage.class.getSimpleName(),KafkaConstant.SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION));
//        messageSender.send(mvnoDocumentDunningMessage , RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION);
    }
    public void mvnoPaymentReminderNotification(StaffUser staffUser , DebitDocument mvnoDocDetails , String ccEmail , boolean sendEmail , boolean sendSms, LocalDate duedate){
        log.info("come in document notification for document: "+mvnoDocDetails.getId());
        MvnoPaymentDunningMessage mvnoDocumentDunningMessage = new MvnoPaymentDunningMessage(staffUser , mvnoDocDetails , ccEmail , sendEmail , sendSms,duedate);
        Gson gson = new Gson();
        gson.toJson(mvnoDocumentDunningMessage);
//        messageSender.send(mvnoDocumentDunningMessage , RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION);
        kafkaMessageSender.send(new KafkaMessageData(mvnoDocumentDunningMessage, mvnoDocumentDunningMessage.getClass().getSimpleName(),KafkaConstant.SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION));
    }
    /**  Send MVNo PAYMENT END **/
    public boolean getFlagForDunningDuplication(Boolean IsDunningActivate, String dunningActivateFor, String dunningAction, LocalDateTime LastDunningDate, Customers customers) {
        boolean flag = true;
        if (Objects.isNull(IsDunningActivate)) {
            return flag;
        } else {
            if (Objects.nonNull(customers.getDunningActivateFor())) {
                if (Objects.nonNull(customers.getDunningActivateFor())) {
                    if (customers.getDunningActivateFor().equalsIgnoreCase(dunningActivateFor)) {
                        if (customers.getLastDunningDate().toLocalDate().equals(LastDunningDate.toLocalDate())) {
                            flag = false;
                        }
                    }
                }
            }
            if (Objects.nonNull(customers.getDunningAction())) {
                if (Objects.nonNull(customers.getDunningAction())) {
                    if (customers.getDunningAction().equalsIgnoreCase(dunningAction)) {
                        if (customers.getLastDunningDate().toLocalDate().equals(LastDunningDate.toLocalDate())) {
                            flag = false;
                        }

                    }
                }
            }

        }
        return flag;
    }

    public boolean getFlagForDunningDuplication(Boolean IsDunningActivate, String dunningActivateFor, String dunningAction, LocalDateTime LastDunningDate, Partner partner) {
        boolean flag = true;
            if (Objects.nonNull(partner.getDunningActivateFor())) {
                if (Objects.nonNull(partner.getDunningActivateFor())) {
                    if (partner.getDunningActivateFor().equalsIgnoreCase(dunningActivateFor)) {
                        if (partner.getLastDunningDate().toLocalDate().equals(LastDunningDate.toLocalDate())) {
                            flag = false;
                        }
                    }
                }
            }
                if (Objects.nonNull(partner.getDunningAction())) {
                    if (Objects.nonNull(partner.getDunningAction())) {
                        if (partner.getDunningAction().equalsIgnoreCase(dunningAction)) {
                            if (partner.getLastDunningDate().toLocalDate().equals(LastDunningDate.toLocalDate())) {
                                flag = false;
                            }

                        }
                    }
                }



        return flag;
    }

    public boolean getFlagForDunnningDuplicationNew(String event, String action, Long staffid, Integer custid, Long partnerid, LocalDateTime dunningTime){
      Integer count = dunningHistoryRepository.CountDunningHappen(event , action , staffid , custid ,partnerid);
      if(count > 0){
          List<DunningHistory> dunningHistories = dunningHistoryRepository.CountDunningHappenWithAllHistory(event , action , staffid , custid ,partnerid);
          LocalDate date = dunningHistories.get(dunningHistories.size()-1).getDunningMessageDate().toLocalDate();
          if(date.isEqual(dunningTime.toLocalDate())){
              return  false;
          }
      }
      return true;
    }

    /**Add this flag to if invoice is clear or not**/
    public boolean IsInvoiceInapprovalOrNot(List<Integer> custPackRelId){
        Boolean flag = true;
        List<DebitDocument> debitDocumentList = debitDocRepository.findAllByCustpackrelidIn(custPackRelId);
        debitDocumentList.removeIf(debitDocument -> debitDocument.getPaymentStatus().equalsIgnoreCase(StatusConstants.INVOICE_STATUS.CLEAR)
                           || debitDocument.getPaymentStatus().equalsIgnoreCase(StatusConstants.INVOICE_STATUS.CANCELLED)
                            || debitDocument.getPaymentStatus().equalsIgnoreCase(StatusConstants.INVOICE_STATUS.FULLYPAID));
       if(debitDocumentList.stream().anyMatch(debitDocument -> debitDocument.getPaymentStatus().equalsIgnoreCase(StatusConstants.INVOICE_STATUS.UNPAID))){
            flag = true;
        }
       else {
           if (!debitDocumentList.isEmpty()) {
               List<CreditDebitDocMapping> creditDebitDocMappingList = creditDebtMappingRepository.findAllBydebtDocIdIn(debitDocumentList.stream().map(debitDocument -> debitDocument.getId()).collect(Collectors.toList()));
               if (!creditDebitDocMappingList.isEmpty()) {
                   List<CreditDocument> creditDocuments = creditDocRepository.findAllByIdIn(creditDebitDocMappingList.stream().map(creditDebitDocMapping -> creditDebitDocMapping.getCreditDocId()).collect(Collectors.toList()));
                   if (!creditDocuments.isEmpty()) {
                       Boolean flag2 = creditDocuments.stream().anyMatch(creditDocument -> creditDocument.getNextTeamHierarchyMappingId() != null);
                       flag = !flag2;
                   }
               }

           }
       }

        return flag;
    }

    public boolean IsInvoiceNotPaid(List<Integer> custPackRelId){
        Boolean flag = true;
        {
            List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByIdIn(custPackRelId);
            if(!custPlanMapppingList.isEmpty()){
                custPlanMapppingList = custPlanMapppingList.stream().filter(custPlanMappping -> custPlanMappping.getDebitdocid() != null).collect(Collectors.toList());
                if(!custPlanMapppingList.isEmpty()) {
                    List<Integer> debitdocids = custPlanMapppingList.stream().map(custPlanMappping -> custPlanMappping.getDebitdocid()).map(aLong -> aLong.intValue()).collect(Collectors.toList());
                    List<DebitDocument> debitDocumentList = debitDocRepository.findAllByIdIn(debitdocids);
                    if (!debitDocumentList.isEmpty()) {
                        Boolean isAllInvoiceClear = true;
                        isAllInvoiceClear = debitDocumentList.stream().anyMatch(debitDocument -> debitDocument.getPaymentStatus().equalsIgnoreCase(StatusConstants.INVOICE_STATUS.CLEAR) || debitDocument.getPaymentStatus().equalsIgnoreCase(StatusConstants.INVOICE_STATUS.FULLYPAID));
                        if (isAllInvoiceClear) {
                            flag = !isAllInvoiceClear;
                        }

                    }
                }
            }
        }
        return flag;
    }

    @Scheduled(cron = "${cronJobTimeForServiceHold}")
    public void setServiceHoldForCustomerInvoice() {
        log.info("XXXXXXXXXXXX----------CRON TIME_FOR_SERVICE_HOLD_SCHEDULER START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.TIME_FOR_SERVICE_HOLD_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_SERVICE_HOLD)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_SERVICE_HOLD);
            try {
                System.out.println("********* SERVICE HOLD SCHEDULAR STARTED********");
                String days = clientService.getValueByName("SERVICE_HOLD_DAYS");
                List<Integer> debitdocIds = custPlanMappingRepository.getAllCprIdsForServiceHold(Integer.parseInt(days));
                List<Integer> cprIds = custPlanMappingRepository.getAllByCustPlanMappingIdInDebitDocIds(debitdocIds.stream().map(integer -> integer.longValue()).collect(Collectors.toList()));
                if (!cprIds.isEmpty()) {
                    List<Integer> finalcprIds = new ArrayList<>();
                    finalcprIds = custPlanMappingRepository.getAllByCustPlanMappingIdInCustPlanMappingIdsandstatus(cprIds);
                    if (!finalcprIds.isEmpty()) {
                        finalcprIds = finalcprIds.stream().distinct().collect(Collectors.toList());
                        List<Integer> serviceMappingIds = custPlanMappingRepository.getAllByCustServiceMappingIdInCprIds(finalcprIds);
                        if (!CollectionUtils.isEmpty(serviceMappingIds)) {
                            String remark = "Service Hold Due to pending approval";
                            custPlanMappingService.changeStatusOfCustServices(serviceMappingIds, StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD, remark, false);
                        }
                    }

                }
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Service Hold Scheduler Run Success");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(null);
                System.out.println("********* SERVICE HOLD SCHEDULAR ENDED *********");
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_SERVICE_HOLD);
                log.info("XXXXXXXXXXXX---------- Service Hold Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Service Hold Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Service Hold Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }



    public void sendDeactivationNotificationForMvnoDunning(List<MvnoDocDetails> mvnoDocDetailsList ,String ccEmail , boolean sendEmail , boolean sendSms) {
        List<Mvno> mvnos = mvnoDocDetailsList.stream().map(MvnoDocDetails::getMvno).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(mvnos)) {
            Set<Long> mvnoIds = mvnos.stream()
                    .map(Mvno::getId)
                    .collect(Collectors.toSet());
           if(!mvnoIds.isEmpty()) {
               for (MvnoDocDetails mvnoDocDetails : mvnoDocDetailsList) {
                   String status = "ACTIVE";
                   List<StaffUser> staffUserList = staffUserRepository.StatusAndIsDeleteIsFalseAndMvnoIdIn(status, Collections.singletonList(mvnoDocDetails.getMvno().getId().intValue()));
                   if (!staffUserList.isEmpty()) {
                       StaffUser staffUser = staffUserList.get(0);
//                       if (staffUser.getCreatedById() == 1) {
                           boolean flag = true;
                           if (sendEmail) {
                               flag = getFlagForDunnningDuplicationNew("MVNO_DEACTIVATION_"+ mvnoDocDetails.getUniquename(), "Email", Long.valueOf(staffUser.getId()), mvnoDocDetails.getMvno().getCustInvoiceRefId(), null, LocalDateTime.now());
                           }
                           if (sendSms) {
                               flag = getFlagForDunnningDuplicationNew("MVNO_DEACTIVATION_"+ mvnoDocDetails.getUniquename(), "SMS", Long.valueOf(staffUser.getId()), mvnoDocDetails.getMvno().getCustInvoiceRefId(), null, LocalDateTime.now());
                           }
                           if (flag) {
                               mvnoExpireNotification(staffUser, mvnoDocDetails, ccEmail, sendEmail, sendSms);
                               mvnoService.changeMvnoStatus(Collections.singleton(mvnoDocDetails.getMvno().getId()), CommonConstants.INACTIVE_STATUS);
                               if (sendEmail) {
                                   dunningAudit("MVNO_DEACTIVATION_"+ mvnoDocDetails.getUniquename() , "Email", Long.valueOf(staffUser.getId()), mvnoDocDetails.getMvno().getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno document email send", staffUser.getMvnoId());
                               }
                               if (sendSms) {
                                   dunningAudit("MVNO_DEACTIVATION_"+ mvnoDocDetails.getUniquename(), "SMS", Long.valueOf(staffUser.getId()), mvnoDocDetails.getMvno().getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno document sms send", staffUser.getMvnoId());
                               }
                               dunningAudit("MVNO_DEACTIVATION_"+ mvnoDocDetails.getUniquename(), CommonConstants.INACTIVE_STATUS , Long.valueOf(staffUser.getId()), mvnoDocDetails.getMvno().getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno document sms send", staffUser.getMvnoId());
                           } else {
                               log.info("Email or sms is already send");
                           }
//                       } else {
//                           log.info("mvno staff is not active");
//                       }
                   } else {
                       log.info("no staff found by given mvnoId");
                   }
               }
           }

        }
    }
    public void mvnoExpireNotification(StaffUser staffUser , MvnoDocDetails mvnoDocDetails , String ccEmail , boolean sendEmail , boolean sendSms){
        log.info("MVNo Deactivation Notification: "+mvnoDocDetails.getFilename());
        MvnoExpiryMessage mvnoExpiryMessage = new MvnoExpiryMessage(staffUser , mvnoDocDetails , ccEmail , sendEmail , sendSms);
        Gson gson = new Gson();
        gson.toJson(mvnoExpiryMessage);
        kafkaMessageSender.send(new KafkaMessageData(mvnoExpiryMessage,MvnoExpiryMessage.class.getSimpleName(),KafkaConstant.SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION));
//        messageSender.send(mvnoExpiryMessage , RabbitMqConstants.QUEUE_SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION);
    }
    public void sendDeactivationNotificationForMvnoPaymentDunning(Integer days, Integer mvnoId, String ccEmail, boolean sendEmail, boolean sendSms,List<DebitDocNumberMappingPojo> numberMappingPojos) {
        List<Mvno> mvnoList = mvnoRepository.findAllByCustInvoiceRefIdNotNullAndStatusEquals("Active");

        mvnoList.forEach(mvno -> {
            if (!processMvnoStaffUsers(mvno, days, sendEmail, sendSms, ccEmail,numberMappingPojos)) {
                log.info("No active staff found for MVNO ID: {}", mvno.getId());
            }
        });
    }

    private boolean processMvnoStaffUsers(Mvno mvno, Integer days, boolean sendEmail, boolean sendSms, String ccEmail,List<DebitDocNumberMappingPojo>numberMappingPojos) {
        List<StaffUser> staffUsers = staffUserRepository.StatusAndIsDeleteIsFalseAndMvnoIdIn("ACTIVE", Collections.singletonList(mvno.getId().intValue()));
        if (staffUsers.isEmpty()) return false;
//deactivation
        StaffUser staffUser = staffUsers.get(0);
        if (staffUser.getIsDelete()) {
            log.info("Staff user for MVNO ID {} is deleted", mvno.getId());
            return false;
        }

        processDebitDocument(mvno, staffUser, days, sendEmail, sendSms, ccEmail,numberMappingPojos);
        return true;
    }

    private void processDebitDocument(Mvno mvno, StaffUser staffUser, Integer days, boolean sendEmail, boolean sendSms, String ccEmail,List<DebitDocNumberMappingPojo>numberMappingPojos) {
        if (mvno.getCustInvoiceRefId() == null) return;

        LocalDateTime adjustedDate = LocalDateTime.now().plusDays(days);
        List<DebitDocumentSummary> debitDocumentSummary = debitDocRepository.findDebitDocumentAndSum( mvno.getCustInvoiceRefId(), adjustedDate);

        if (CollectionUtils.isEmpty(debitDocumentSummary) ) {
            log.info("No DebitDocumentSummary found for MVNO ID: {}", mvno.getId());
            return;
        }

        if (!shouldSendNotification(debitDocumentSummary.get(0), sendEmail, sendSms, staffUser, mvno)) {
            log.info("Email or SMS already sent for DebitDocument ID: {}", debitDocumentSummary.get(0).getDebitDocument().getId());
            return;
        }

        for (DebitDocNumberMappingPojo numberMappingPojo : numberMappingPojos) {
            if (Objects.equals(numberMappingPojo.getDebitdocId(), debitDocumentSummary.get(0).getDebitDocument().getId())) {
                debitDocumentSummary.get(0).getDebitDocument().setDocnumber(numberMappingPojo.getDocnumber());
                debitDocumentSummary.get(0).getDebitDocument().setBillrunstatus(numberMappingPojo.getBillRunStatus());
            }
        }
        debitDocumentSummary=debitDocumentSummary.stream().filter(x->x.getDebitDocument()!=null && x.getDebitDocument().getBillrunstatus()!=null && !x.getDebitDocument().getBillrunstatus().equalsIgnoreCase("Cancelled")).collect(Collectors.toList());

        if(debitDocumentSummary!=null && !debitDocumentSummary.isEmpty()) {
            LocalDate dueDate = debitDocumentSummary.get(0).getDebitDocument().getDuedate().toLocalDate().plusDays(mvno.getMvnoPaymentDueDays());
            sendNotificationsAndAudit(debitDocumentSummary.get(0), staffUser, ccEmail, sendEmail, sendSms, dueDate, mvno, numberMappingPojos);
        }
    }

    private boolean shouldSendNotification(DebitDocumentSummary debitDocumentSummary, boolean sendEmail, boolean sendSms, StaffUser staffUser, Mvno mvno) {
        boolean flag = true;

        if (sendEmail) {
            flag = getFlagForDunnningDuplicationNew("MVNO_PAYMENT_DEACTIVATION_" + debitDocumentSummary.getDebitDocument().getId(),
                    "Email", Long.valueOf(staffUser.getId()), mvno.getCustInvoiceRefId(), null, LocalDateTime.now());
        }

        if (sendSms && flag) {
            flag = getFlagForDunnningDuplicationNew("MVNO_PAYMENT_DEACTIVATION_" + debitDocumentSummary.getDebitDocument().getId(),
                    "SMS", Long.valueOf(staffUser.getId()), mvno.getCustInvoiceRefId(), null, LocalDateTime.now());
        }

        return flag;
    }

    private void sendNotificationsAndAudit(DebitDocumentSummary debitDocumentSummary, StaffUser staffUser, String ccEmail, boolean sendEmail, boolean sendSms, LocalDate dueDate, Mvno mvno,List<DebitDocNumberMappingPojo>numberMappingPojos) {
        mvnoPaymentReminderNotification(staffUser, debitDocumentSummary.getDebitDocument(), ccEmail, sendEmail, sendSms, dueDate);
        mvnoService.changeMvnoStatus(Collections.singleton(mvno.getId()), CommonConstants.INACTIVE_STATUS);

        logAndAuditNotifications(debitDocumentSummary, staffUser, sendEmail, sendSms, mvno);
    }

    private void logAndAuditNotifications(DebitDocumentSummary debitDocumentSummary, StaffUser staffUser, boolean sendEmail, boolean sendSms, Mvno mvno) {
        Integer debitDocumentId = debitDocumentSummary.getDebitDocument().getId();
        Long staffUserId = Long.valueOf(staffUser.getId());

        if (sendEmail) {
            dunningAudit("MVNO_PAYMENT_DEACTIVATION_" + debitDocumentId, "Email", staffUserId, mvno.getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno Payment email sent", staffUser.getMvnoId());
        }
        if (sendSms) {
            dunningAudit("MVNO_PAYMENT_DEACTIVATION_" + debitDocumentId, "SMS", staffUserId, mvno.getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno Payment SMS sent", staffUser.getMvnoId());
        }

        dunningAudit("MVNO_PAYMENT_DEACTIVATION_" + debitDocumentId, CommonConstants.INACTIVE_STATUS, staffUserId, mvno.getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno Payment status changed", staffUser.getMvnoId());
    }
//    public void sendDeactivationNotificationForMvnoPaymentDunning(Integer days, Integer mvnoId, String ccemail, boolean sendEmail, boolean sendSms) {
//        List<Mvno>mvnoList= mvnoRepository.findAllByCustInvoiceRefIdNotNullAndStatusEquals("Active");
//        for(Mvno id :mvnoList) {
//            String status = "ACTIVE";
//            List<StaffUser> staffUserList = staffUserRepository.StatusAndIsDeleteIsFalseAndMvnoIdIn(status ,  Collections.singletonList(id.getId().intValue()));
//            if(!staffUserList.isEmpty()){
//                StaffUser staffUser = staffUserList.get(0);
//                if(!staffUser.getIsDelete()){
//                    boolean flag = true;
//                    if (id.getCustInvoiceRefId()!=null ) {
//                        LocalDateTime adjustedDate = LocalDateTime.now().plusDays(id.getMvnoPaymentDueDays());
//                        DebitDocumentSummary debitDocumentSummary=  debitDocRepository.findDebitDocumentAndSum(id.getId(),id.getCustInvoiceRefId(),adjustedDate);
////                        Double pendingAmount = debitDocService.getTotalAmountDebitDocumentsByMvno(Math.toIntExact(id.getId()), false, days);
////                        List<DebitDocument> lastInvoice = debitDocRepository.lastInvoice(id.getCustInvoiceRefId());
////                        if (!CollectionUtils.isEmpty(lastInvoice)) {
////                            DebitDocument debitDocument = lastInvoice.get(0);
//////                            List<CreditDebitDocMapping> creditDebitDocMappingList = creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
//////                            List<CreditDocument> creditDocument = creditDocRepository.findAllById(Collections.singleton(creditDebitDocMappingList.get(0).getCreditDocId()));
//////                            String value =clientServiceSrv.getValueByNameAndmvnoId( "MVNO_INVOICE_DUEDATE",1);
////                            LocalDate duedate = debitDocument.getStartdate().toLocalDate().plusDays(id.getMvnoPaymentDueDays());
////                            if (LocalDate.now().equals(duedate.plusDays(days))&&!debitDocument.getPaymentStatus().equalsIgnoreCase("Fully Paid")) {
//                              if(Objects.nonNull(debitDocumentSummary) ){
//                                if (sendEmail) {
//                                    flag = getFlagForDunnningDuplicationNew("MVNO_PAYMENT_DEACTIVATION_" + debitDocumentSummary.getDebitDocument().getId(), "Email", Long.valueOf(staffUser.getId()), id.getCustInvoiceRefId(), null, LocalDateTime.now());
//                                }
//                                if (sendSms) {
//                                    flag = getFlagForDunnningDuplicationNew("MVNO_PAYMENT_DEACTIVATION_" +debitDocumentSummary.getDebitDocument().getId(), "SMS", Long.valueOf(staffUser.getId()), id.getCustInvoiceRefId(), null, LocalDateTime.now());
//                                }
//                                if (flag) {
//                                    LocalDate duedate = debitDocumentSummary.getDebitDocument().getDuedate().toLocalDate().plusDays(id.getMvnoPaymentDueDays());
//                                    mvnoPaymentReminderNotification(staffUser, debitDocumentSummary.getDebitDocument(), ccemail, sendEmail, sendSms,duedate);
//                                    mvnoService.changeMvnoStatus(Collections.singleton(id.getId()), CommonConstants.INACTIVE_STATUS);
//                                    if (sendEmail) {
//                                        dunningAudit("MVNO_PAYMENT_DEACTIVATION_" + debitDocumentSummary.getDebitDocument().getId(), "Email", Long.valueOf(staffUser.getId()), id.getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno Payment email send", staffUser.getMvnoId());
//                                    }
//                                    if (sendSms) {
//                                        dunningAudit("MVNO_PAYMENT_DEACTIVATION_" + debitDocumentSummary.getDebitDocument().getId(), "SMS", Long.valueOf(staffUser.getId()), id.getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno Payment email send", staffUser.getMvnoId());
//                                    }
//                                    dunningAudit("MVNO_PAYMENT_DEACTIVATION_" + debitDocumentSummary.getDebitDocument().getId(), CommonConstants.INACTIVE_STATUS , Long.valueOf(staffUser.getId()), id.getCustInvoiceRefId(), null, LocalDateTime.now(), "Mvno Payment email send", staffUser.getMvnoId());
//
//                                } else {
//                                    log.info("Email or sms is already send");
//                                }
//                            }
//                        }
//                    }
//
////                }
//                else{
//                    log.info("mvno staff is not active");
//                }
//            }
//            else{
//                log.info("no staff found by given mvnoId");
//            }
//
//        }
//    }
@Scheduled(cron = "${cronJobTimeForPlanExpiryNotification}")
public void sendPlanExpiryNotification() {
    log.info("XXXXXXXXXXXX----------CRON TIME_FOR_PLAN_EXPIRY_NOTIFICATION START---------XXXXXXXXXXXX");
    SchedulerAudit schedulerAudit = new SchedulerAudit();
    schedulerAudit.setStartTime(LocalDateTime.now());
    schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.TIME_FOR_PLAN_EXPIRY_NOTIFICATION_SCHEDULER);
    if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_PLAN_EXPIRY_NOTIFICATION)) {
        schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_PLAN_EXPIRY_NOTIFICATION);
        try {
            List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();

            QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
            BooleanExpression booleanExpression = qCustPlanMappping.isNotNull().and(qCustPlanMappping.isDelete.eq(false)).and(qCustPlanMappping.isPlanExpiryNotified.eq(false).or(qCustPlanMappping.isPlanExpiryNotified.isNull())).and(qCustPlanMappping.endDate.before(LocalDateTime.now()));

            custPlanMapppingList = (List<CustPlanMappping>) custPlanMappingRepository.findAll(booleanExpression);

            if (!custPlanMapppingList.isEmpty()) {
                for (CustPlanMappping custPlanMappping : custPlanMapppingList) {
                    if (custPlanMappping.getCustomer() != null) {
                        String PlanName = postpaidPlanRepo.findNameById(custPlanMappping.getPlanId());
                        if (custPlanMappping.getCustomer().getBuId() != null) {
                            sendPlanExpiryMessage(custPlanMappping.getCustomer().getEmail(), custPlanMappping.getCustomer().getMobile(), custPlanMappping.getCustomer().getUsername(), PlanName, custPlanMappping.getEndDate().toString(), custPlanMappping.getCustomer().getAltemail(), custPlanMappping.getCustomer().getCountryCode(), custPlanMappping.getCustomer().getMvnoId(), custPlanMappping.getCustomer().getBuId().intValue(), custPlanMappping.getCustomer().getId(),custPlanMappping.getNextStaff().longValue());
                            System.out.println("--------------------------------------PLAN EXPIRY MESSAGE INITIATED--------------------------------------------------");

                        } else {
                            sendPlanExpiryMessage(custPlanMappping.getCustomer().getEmail(), custPlanMappping.getCustomer().getMobile(), custPlanMappping.getCustomer().getUsername(), PlanName, custPlanMappping.getEndDate().toString(), custPlanMappping.getCustomer().getAltemail(), custPlanMappping.getCustomer().getCountryCode(), custPlanMappping.getCustomer().getMvnoId(), null, custPlanMappping.getCustomer().getId(),custPlanMappping.getNextStaff().longValue());
                            System.out.println("--------------------------------------PLAN EXPIRY MESSAGE INITIATED--------------------------------------------------");
                        }

                    }
                    custPlanMappping.setIsPlanExpiryNotified(true);
                    custPlanMappingRepository.save(custPlanMappping);
                }

            }
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Expiry Plan Notification Scheduler Run Success");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
            schedulerAudit.setTotalCount(null);
            System.out.println("***** cronJobTimeForPlanExpiryNotification Ended !!! *****");
        } catch (Exception ex) {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
            log.error("**********Scheduler Showing ERROR***********");
        } finally {
            schedulerAuditService.saveEntity(schedulerAudit);
            schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_PLAN_EXPIRY_NOTIFICATION);
            log.info("XXXXXXXXXXXX---------- Expiry Plan Notification Scheduler Locked released ---------XXXXXXXXXXXX");
        }
    } else {
        schedulerAudit.setEndTime(LocalDateTime.now());
        schedulerAudit.setDescription("Expiry Plan Notification Scheduler Lock held by another instance");
        schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
        schedulerAuditService.saveEntity(schedulerAudit);
        log.warn("XXXXXXXXXXXX----------Expiry Plan Notification Scheduler Locked held by another instance---------XXXXXXXXXXXX");
    }
}

    public void sendPlanExpiryMessage(String emailId, String mobileNumber, String customerUsername, String planName, String expiryDate,String ccMail, String countryCode, Integer mvnoid, Integer buid,Integer custId,Long staffId){
        //create a message and send it to notification.

        PlanExpiryNotificationMessage planExpiryNotificationMessage = new PlanExpiryNotificationMessage(emailId,mobileNumber,customerUsername,planName,expiryDate,ccMail,countryCode,mvnoid,buid,custId,staffId);
        kafkaMessageSender.send(new KafkaMessageData(planExpiryNotificationMessage,PlanExpiryNotificationMessage.class.getSimpleName()));
//        messageSender.send(planExpiryNotificationMessage, RabbitMqConstants.QUEUE_SEND_PLAN_EXPIRY_NOTIFICATION);
        System.out.println("--------------------------------------PLAN EXPIRY MESSAGE SENT TO NOTIFICATION--------------------------------------------------");

    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("ApiGatewayScheduler" + e.getStackTrace(), e);
        }
        return loggedInUser;
    }

    public String GenerateTokenUsingLoggedInUser(LoggedInUser loggedInUser){

        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode("asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4"),
                SignatureAlgorithm.HS256.getJcaName());
        String subString = null;
        try {
            subString = new ObjectMapper().writeValueAsString(loggedInUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        long expirationTime = 1732354051L * 1000L; // Convert seconds to milliseconds

        //Update sign with with new method
        String token = Jwts.builder()
                .setSubject(subString)
                .setExpiration(new Date(System.currentTimeMillis() + CommonConstants.EXPIRATION_TIME))
                .signWith(hmacKey)
                .compact();
        token =  "Bearer " + " " +token;
        return token;
    }

    Map<Integer, Boolean> getFlagsForDunningDuplicationNew(List<Integer> customerIds) {
        return customerIds.stream().collect(Collectors.toMap(id -> id, id ->
                getFlagForDunnningDuplicationNew("AdvanceNotification", "SMS", null, id, null, LocalDateTime.now())));
    }

    Map<Integer, Boolean> getFlagsForDunningDuplicationEmailNew(List<Integer> customerIds) {
        return customerIds.stream().collect(Collectors.toMap(id -> id, id ->
                getFlagForDunnningDuplicationNew("AdvanceNotification", "Email", null, id, null, LocalDateTime.now())));
    }

    Map<Integer, List<Long>> getCustomerPackageIds(List<Integer> customerIds, int dateDiff) {
        return customerPackageRepository.getPrepaidCustPackageIdBycustomerInanddatediff(customerIds, dateDiff)
                .stream()
                .collect(Collectors.groupingBy(
                        obj -> (Integer) obj[0],  // Extract customer ID
                        Collectors.mapping(obj -> (Long) obj[1], Collectors.toList())
                ));
    }

    public Map<Integer, List<LocalDate>> getStartDates(List<Integer> customerIds) {
        return customerPackageRepository.getStartDateByCustomerIdAndDateDiff(customerIds)
                .stream()
                .collect(Collectors.groupingBy(
                        start -> (Integer) start[0],
                        Collectors.mapping(start -> (LocalDate) start[1], Collectors.toList())
                ));
    }

    public Map<Integer, List<LocalDate>> getEndDates(List<Integer> customerIds) {
        return customerPackageRepository.getEndDateByCustomerIdAndDateDiff(customerIds)
                .stream()
                .collect(Collectors.groupingBy(
                        obj -> (Integer) obj[0],
                        Collectors.mapping(obj -> (LocalDate) obj[1], Collectors.toList())
                ));
    }


    Map<Integer, List<String>> getPostpaidPlans(List<Integer> customerIds) {
        return customerPackageRepository.getPlanBycustomerInanddatediff(customerIds)
                .stream().collect(Collectors.groupingBy(obj -> (Integer)obj[0],
                        Collectors.mapping(planname -> ((String) planname[1]), Collectors.toList())));
    }
    Map<Integer, Double> getTotalPayMent(List<Integer> customerIds) {
        return customerPackageRepository.getAmoutInanddatediff(customerIds).stream()
                .collect(Collectors.toMap(
                        obj -> (Integer) obj[0],
                        obj -> (Double) obj[1],
                        Double::sum
                ));
    }
    Map<Integer, List<LocalDate>> getExpiryDates(List<Integer> customerIds, int dateDiff) {
        return customerPackageRepository.getExpirydateBycustomerInanddatediff(customerIds, dateDiff)
                .stream()
                .collect(Collectors.groupingBy(
                        obj -> (Integer) obj[0],
                        Collectors.mapping(expiry -> (LocalDate) expiry[1], Collectors.toList())
                ));
    }

    private Map<Integer, Double> getTaxAmounts(List<Integer> customerIds) {
        return debitDocRepository.getTaxAmountsByCustomerIds(customerIds)
                .stream()
                .collect(Collectors.toMap(
                        obj -> ((Number) obj[0]).intValue(),
                        obj -> ((Number) obj[1]).doubleValue()
                ));
    }


    private Map<Integer, Double> getTaxPercentages(List<Integer> customerIds) {
        return relRepository.getTaxPercentageByCustomerIds(customerIds)
                .stream()
                .collect(Collectors.toMap(
                        obj -> ((Number) obj[0]).intValue(),
                        obj -> ((Number) obj[1]).doubleValue()
                ));
    }



    Map<Integer, String> generatePaymentLinks(List<Integer> customerIds, String token) {
        return customerIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> shorterRepository.findLatestShorterByCustIdAndIshashusedIsFalse(id).stream()
                                .findFirst()
                                .map(Shorter::getHash)
                                .orElseGet(() -> subscriberService.generatePaymentLinkForRenewShedular(id, token))
                ));
    }

    void sendAdvanceDunningNotificationInBatch(Customers customer, TemplateNotification template,
                                               List<LocalDate> expiryDate, List<LocalDate> startDate,
                                               List<LocalDate> endDate, String currencySymbol,
                                               String paymentUrl, Double amount, Double taxAmount,
                                               Double taxPercentage, List<LocalDateTime> dueDate,
                                               Double subTotal, Double totalDue, Double walletBalance,String debitdocNumber) {
        if (customer == null || template == null || customer.getId() == null) {
            log.error("Invalid data: Missing customer or template for notification.");
            return;
        }

        // Create CustomerDunningMessage
        CustomerDunningMessage dunningMessage = new CustomerDunningMessage(
                RabbitMqConstants.CUSTOMER_DUNNING_ADVANCE_NOTIFICATION_TEMPLATE_HEADER,
                template,
                RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY,
                customer,
                amount != null ? amount : 0.0,
                currencySymbol != null ? currencySymbol : "",
                expiryDate != null && !expiryDate.isEmpty() ? expiryDate.get(0) : null,
                startDate != null && !startDate.isEmpty() ? startDate.get(0) : null,
                endDate != null && !endDate.isEmpty() ? endDate.get(0) : null,
                dueDate != null && !dueDate.isEmpty() ? dueDate.get(0).toLocalDate().atStartOfDay() : null,
                paymentUrl != null ? paymentUrl : "",
                taxAmount != null ? taxAmount : 0.0,
                taxPercentage != null ? taxPercentage : 0.0,
                subTotal != null ? subTotal : 0.0,
                totalDue != null ? totalDue : 0.0,
                walletBalance != null ? walletBalance : 0.0,
                debitdocNumber,(long)customersService.getLoggedInStaffId());

        dunningMessage.setEmailConfigured(false);
        dunningMessage.setSmsConfigured(true);


        String messageJson = new Gson().toJson(dunningMessage);
        dunningAudit("AdvanceNotification", "SMS", null, customer.getId(), null,
                LocalDateTime.now(), messageJson, customer.getMvnoId());

        log.info("Sending Dunning Notification: {}", messageJson);


        kafkaMessageSender.send(new KafkaMessageData(dunningMessage,
                CustomerDunningMessage.class.getSimpleName(),
                KafkaConstant.DUNNING_ADVANCE_NOTIFICATION));
    }


    void sendAdvanceDunningEmailNotificationInBatch(Customers customer, TemplateNotification template,
                                                    List<LocalDate> expiryDate, List<LocalDate> startDate, List<LocalDate> endDate, String currencySymbol, String paymentUrl, Double amount, Double taxAmount, Double taxPercentage, List<LocalDateTime> dueDate, Double subTotal, Double totalDue, Double walletBalance,String debitdocNumber) {
        {
            CustomerDunningMessage dunningMessage = new CustomerDunningMessage(
                    RabbitMqConstants.CUSTOMER_DUNNING_ADVANCE_NOTIFICATION_TEMPLATE_HEADER,
                    template,
                    RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY,
                    customer,
                    amount != null ? amount : 0.0,
                    currencySymbol != null ? currencySymbol : "",
                    expiryDate != null && !expiryDate.isEmpty() ? expiryDate.get(0) : null,
                    startDate != null && !startDate.isEmpty() ? startDate.get(0) : null,
                    endDate != null && !endDate.isEmpty() ? endDate.get(0) : null,
                    dueDate != null && !dueDate.isEmpty() ? dueDate.get(0).toLocalDate().atStartOfDay() : null,
                    paymentUrl != null ? paymentUrl : "",
                    taxAmount != null ? taxAmount : 0.0,
                    taxPercentage != null ? taxPercentage : 0.0,
                    subTotal != null ? subTotal : 0.0,
                    totalDue != null ? totalDue : 0.0,
                    walletBalance != null ? walletBalance : 0.0,
                    debitdocNumber,(long)customersService.getLoggedInStaffId()
            );

            dunningMessage.setEmailConfigured(true);
            dunningMessage.setSmsConfigured(false);

            String messageJson = new Gson().toJson(dunningMessage);
            dunningAudit("AdvanceNotification", "Email", null, customer.getId(), null, LocalDateTime.now(), messageJson, customer.getMvnoId());

            kafkaMessageSender.send(new KafkaMessageData(dunningMessage, CustomerDunningMessage.class.getSimpleName(), KafkaConstant.DUNNING_ADVANCE_NOTIFICATION));
        }

    }

//    @Scheduled( cron = "${cronjobforServiceHold}")
//    public void holdService(){
//        SchedulerAudit schedulerAudit = new SchedulerAudit();
//        System.out.println(".................Service Hold Scheduler Started....................");
//        schedulerAudit.setStartTime(LocalDateTime.now());
//        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.SERVICE_HOLD_SERVICE_SCHEDULER);
//        if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_CUSTOMER_SERVICE_HOLD)) {
//            schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_CUSTOMER_SERVICE_HOLD);
//            try {
//                if (LocalDate.now().getDayOfMonth() == 1) {
//                    customerServiceMappingRepository.resetServiceHoldCount();
//                }
//                List<CustomerServiceMapping> customerServiceMappingList = customerServiceMappingRepository.findAllByStatus(CommonConstants.WORKFLOW_AUDIT_STATUS.INPROGRESS);
//                List<Integer> custserviceMappingIds = new ArrayList<>();
//                Map<Integer, Long> idToDateDiffMap = new HashMap<>();
//                if (customerServiceMappingList.size() > 0) {
//                    System.out.println(".................Service Hold Started....................");
//                    customerServiceMappingList.forEach(customerServiceMapping -> {
//                        customerServiceMapping.setStatus(CommonConstants.CUSTOMER_STATUS_HOLD);
//                        customerServiceMapping.setPreviousStatus(CommonConstants.CUSTOMER_STATUS_HOLD);
//                        customerServiceMapping.setServiceHoldDate(LocalDateTime.now());
//                        custserviceMappingIds.add(customerServiceMapping.getId());
//                        idToDateDiffMap.put(customerServiceMapping.getId(), Long.valueOf(ChronoUnit.DAYS.between(customerServiceMapping.getServiceHoldDate(), customerServiceMapping.getServiceResumeDate())));
//                    });
//
//                    List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.getDebitDocIdByCustServiceMappingIdInCprIds(custserviceMappingIds);
//                    custPlanMapppingList.forEach(custplanmapping -> {
//                        custplanmapping.setStatus(CommonConstants.CUSTOMER_STATUS_HOLD);
//                        custplanmapping.setCustPlanStatus(CommonConstants.CUSTOMER_STATUS_HOLD);
//                        DeactivatePlanReqModel pojo = new DeactivatePlanReqModel();
//                        pojo.setCustServiceMappingId(custplanmapping.getCustServiceMappingId());
//                        pojo.setCprId(custplanmapping.getId());
//                        subscriberService.saveserviceAudit(custplanmapping, pojo, StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD);
//                    });
//                    System.out.println(".................Sevice Hold Ended  for " + custserviceMappingIds.toString() + "....................");
//                    custPlanMappingService.changeStatusOfCustServices(custserviceMappingIds, StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD, "Service Resume ", false);
//                    custPlanMappingRepository.saveAll(custPlanMapppingList);
//                    customerServiceMappingRepository.updateServiceStatus(custserviceMappingIds,StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD);
//                    System.out.println(".................Service Hold  Ended  for " + custserviceMappingIds.toString() + "....................");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }finally {
//                schedulerAudit.setEndTime(LocalDateTime.now());
//                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
//                schedulerAuditService.saveEntity(schedulerAudit);
//                schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_CUSTOMER_SERVICE_HOLD);
//                log.info("XXXXXXXXXXXX---------- Service Hold Scheduler Locked Scheduler Locked released ---------XXXXXXXXXXXX");
//            }
//        } else {
//            schedulerAudit.setEndTime(LocalDateTime.now());
//            schedulerAudit.setDescription("Service Hold Scheduler Lock held by another instance");
//            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
//            schedulerAuditService.saveEntity(schedulerAudit);
//            log.warn("XXXXXXXXXXXX----------Service Hold Scheduler Locked held by another instance---------XXXXXXXXXXXX");
//        }
//    }
    @Scheduled( cron = "${cronjobforServiceResume}")
    public void resumeService() {
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.SERVICE_RESUME_SERVICE_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_CUSTOMER_SERVICE_RESUME)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_CUSTOMER_SERVICE_RESUME);
            try {
                List<CustomerServiceMapping> customerServiceMappingList = customerServiceMappingRepository.findAllByStatusAndDate(CommonConstants.CUSTOMER_STATUS_HOLD);
                List<Integer> custserviceMappingIds = new ArrayList<>();
                Map<Integer, Long> idToDateDiffMap = new HashMap<>();
                Map<Integer, LocalDate> idToDateMap = new HashMap<>();
                Map<Integer, Boolean> idToStatus = new HashMap<>();
                if (customerServiceMappingList.size() > 0) {
                    System.out.println("................. Service Resume Started....................");
                     customerServiceMappingList.forEach(customerServiceMapping -> {
                        customerServiceMapping.setStatus(CommonConstants.ACTIVE_STATUS);
                        custserviceMappingIds.add(customerServiceMapping.getId());
                        idToDateMap.put(customerServiceMapping.getId(), customerServiceMapping.getServiceHoldDate().toLocalDate().plusDays(1));
                        idToStatus.put(customerServiceMapping.getId(),customerServiceMapping.getGenerateCreditDoc());
                        idToDateDiffMap.put(customerServiceMapping.getId(), Long.valueOf(ChronoUnit.DAYS.between(customerServiceMapping.getServiceHoldDate(), LocalDateTime.now())));
                    });

                    List<ServiceChnageStatus> serviceChnageStatuses = new ArrayList<>();
                    List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingIdIn(custserviceMappingIds);
//                    List<Integer> debitDocIds = custPlanMapppingList.stream()
//                            .map(CustPlanMappping::getDebitdocid)
//                            .filter(Objects::nonNull)
//                            .map(id -> id.intValue())
//                            .collect(Collectors.toList());
//
//                    List<DebitDocument> debitDocuments = debitDocRepository.findAllByIdIn(debitDocIds);
//                    List<DebitDocument> filtered = debitDocuments.stream()
//                            .filter(doc ->
//                                    !doc.getStartdate().isAfter(LocalDateTime.now()) &&
//                                            doc.getEndate().isAfter(LocalDateTime.now())
//                            )
//                            .collect(Collectors.toList());
//
//                    System.out.println("Filtered size: " + filtered.size());
//                    Set<Integer> validDebitDocIds = filtered.stream()
//                            .map(DebitDocument::getId)
//                            .collect(Collectors.toSet());
                    custPlanMapppingList.forEach(custplanmapping -> {
                            custplanmapping.setStatus(CommonConstants.ACTIVE_STATUS);
                            custplanmapping.setCustPlanStatus(CommonConstants.ACTIVE_STATUS);
//                        if(validDebitDocIds.contains(custplanmapping.getDebitdocid())) {
                            ServiceChnageStatus serviceChnageStatus = new ServiceChnageStatus();
                            serviceChnageStatus.setCustId(custplanmapping.getCustomer().getId());
                            serviceChnageStatus.setCustPlanmappigId(custplanmapping.getId());
                            serviceChnageStatus.setServiceId(custplanmapping.getServiceId());
                            serviceChnageStatus.setServiceMappingId(custplanmapping.getCustServiceMappingId());
                            serviceChnageStatus.setStartTime(idToDateMap.get(custplanmapping.getCustServiceMappingId()).atStartOfDay());
                            serviceChnageStatus.setEndTime(LocalDateTime.now());
                            serviceChnageStatus.setDebitDocId(custplanmapping.getDebitdocid());
                            serviceChnageStatus.setTotalDays(Math.toIntExact(idToDateDiffMap.get(custplanmapping.getCustServiceMappingId())));
                            if ("Postpaid".equalsIgnoreCase(custplanmapping.getCustomer().getCusttype()) &&  idToStatus.getOrDefault(custplanmapping.getCustServiceMappingId(), true)) {
                                serviceChnageStatuses.add(serviceChnageStatus);
                            }
                            DeactivatePlanReqModel pojo = new DeactivatePlanReqModel();
                            pojo.setCustServiceMappingId(custplanmapping.getCustServiceMappingId());
                            pojo.setCprId(custplanmapping.getId());

                            subscriberService.saveserviceAudit(custplanmapping, pojo, StatusConstants.CUSTOMER_SERVICE_STATUS.RESUME);
//                        }
                    });
                    custPlanMappingService.changeStatusOfCustServices(custserviceMappingIds, StatusConstants.CUSTOMER_SERVICE_STATUS.RESUME, "Service Resume ", false);
                    customerServiceMappingRepository.updateServiceStatus(custserviceMappingIds,CommonConstants.ACTIVE_STATUS);
                    custPlanMappingRepository.saveAll(custPlanMapppingList);
                    if (serviceChnageStatuses.size() > 0) {
                        kafkaMessageSender.send(new KafkaMessageData(serviceChnageStatuses, ServiceChnageStatus.class.getSimpleName(), KafkaConstant.CHANGE_SERVICE_STATUS_MESSAGE));
                        System.out.println(".................Service Resume  Ended  for " + custserviceMappingIds.toString() + "....................");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_CUSTOMER_SERVICE_RESUME);
                log.info("XXXXXXXXXXXX---------- Service Resume Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Service Resume Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Service Resume Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }

    private  Map<Integer, List<Double>> getTaxPercentagesInParallel(List<Integer> customerIds, String token) {
        Map<Integer, List<Double>> taxPercentageMap = new ConcurrentHashMap<>();
        System.out.println("getTaxPercentagesInParallel started: {}");
        // Batch size 100
        List<List<Integer>> batches = Lists.partition(customerIds, BATCH_SIZE);
        int threadCount = Runtime.getRuntime().availableProcessors()*2; // Limit thread usage

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Void>> futures = new ArrayList<>();

        for (List<Integer> batch : batches) {
            futures.add(executor.submit(() -> {
                try {
                    ResponseEntity<Map<Integer, List<Double>>> response = revenueClient.getTaxPercentagesByCustomers(token, batch);
                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        taxPercentageMap.putAll(response.getBody());
                    }
                    log.info("Tax fetched Successfully" +batch);
                } catch (Exception e) {
                    log.error("Error in getTaxPercentagesByCustomers for batch {}: {}", batch.size(), e.getMessage());
                }
                return null;
            }));
        }

        // Wait for all threads to complete
        for (Future<Void> future : futures) {
            try {
                log.info("Feign call Successfull  for getTaxPercentagesInParallel {}: {}", futures.size());
                future.get();
            } catch (Exception e) {
                log.error("Thread execution exception: {}", e.getMessage());
            }
        }

        executor.shutdown();
        System.out.println(" getTaxPercentagesInParallel Executed Successfully ");
        return taxPercentageMap;
    }
    private Map<Integer, Double> getWalletAmountsInParallel(List<CustomerLedgerDtlsPojo> pojoList, String token) {
        Map<Integer, Double> walletBalanceMap = new ConcurrentHashMap<>();
        System.out.println("getWalletAmountsInParallel started: {}");
        List<List<CustomerLedgerDtlsPojo>> batches = Lists.partition(pojoList, BATCH_SIZE);
        int threadCount = Runtime.getRuntime().availableProcessors()*2 ; // Prevent over-threading
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Void>> futures = new ArrayList<>();

        for (List<CustomerLedgerDtlsPojo> batch : batches) {
            futures.add(executor.submit(() -> {
                try {
                    ResponseEntity<?> response = revenueClient.getWalletAmounts(batch, token);
                    log.info("Wallet API batch size: {}, status: {}", batch.size(), response.getStatusCode());

                    if (response != null && response.getStatusCode().is2xxSuccessful() && response.getBody() instanceof Map) {
                        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
                        Object walletDetails = responseBody.get("customerWalletDetails");

                        if (walletDetails instanceof Map<?, ?>) {
                            @SuppressWarnings("unchecked")
                            Map<Integer, Double> batchResult = (Map<Integer, Double>) walletDetails;
                            walletBalanceMap.putAll(batchResult);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error fetching wallet amounts for batch: {}", e.getMessage());
                }
                return null;
            }));
        }

        for (Future<Void> future : futures) {
            try {
                log.info("Feign call Successfull  for getWalletAmountsInParallel {}: {}", futures.size());
                future.get();
            } catch (Exception e) {
                log.error("Thread execution error: {}", e.getMessage());
            }
        }

        executor.shutdown();
        System.out.println(" getWalletAmountsInParallel Executed Successfully ");
        return walletBalanceMap;
    }

    public void sendDeactivationNotificationInBatch(List<Customers> customers, Integer dateDiff, String ccmail, String ccMobile){
        String planname = "";
        List<LocalDate> planDate = new ArrayList<>();
        for (Customers customer : customers) {
            CustomerLedgerDtlsPojo pojo = new CustomerLedgerDtlsPojo();
            pojo.setCustId(customer.getId());
//            List<String> serviceNames = new ArrayList<>();
            List<Long> cprIds = new ArrayList<>();
            CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getWalletAmt(pojo);
            Customers changeStatusCustomer = customersRepository.findCustomerById(customer.getId()).get();
            System.out.println(changeStatusCustomer.getIsDunningActivate());
            String pendingAmount = clientService.getValueByNameAndmvnoId("MINIMUM_PAYMENT_REQUIRED",changeStatusCustomer.getMvnoId());
            Boolean flag = getFlagForDunnningDuplicationNew("Customer Payment", "Deactivation", null, changeStatusCustomer.getId(), null, LocalDateTime.now());
            List<Long> cprIds2=new ArrayList<>();
            if(customer.getCusttype().equalsIgnoreCase("Prepaid")){
                cprIds2= customerPackageRepository.getPrepaidCustPackageIdBycustomeranddatediffforDeactivate(customer.getId(), dateDiff);
            }else {
                cprIds2 = customerPackageRepository.getPostpaidCustPackageIdBycustomeranddatediffDeactivate(customer.getId(), dateDiff);
            }
            Boolean invoiceflag = IsInvoiceInapprovalOrNot(cprIds2.stream().map(aLong -> aLong.intValue()).collect(Collectors.toList()));
            if(cprIds2.size()>0) {
                List<Integer> serviceMappingIds = custPlanMappingRepository.getAllByCustServiceMappingIdInCprIds(cprIds2.stream().map(aLong -> aLong.intValue()).collect(Collectors.toList()));
//                List<Integer> inventoryServiceMappingIds = customerInventoryMappingService.getServiceInventoryMapping(customer.getId());
//                if(!inventoryServiceMappingIds.isEmpty()){
//                    String remark = "Disable due to Unpaid";
//                    custPlanMappingService.changeStatusOfCustServices(inventoryServiceMappingIds, StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE, remark, false);
//                }
                String remark = "Disable due to Unpaid";
                System.out.println("$#####disable called######$");
                Boolean isInvoicePaid = IsInvoiceNotPaid(cprIds2.stream().map(aLong -> aLong.intValue()).collect(Collectors.toList()));
                if (customer.getWalletbalance() > Double.parseDouble(pendingAmount) && isInvoicePaid) {
                    if (!CollectionUtils.isEmpty(serviceMappingIds)) {
                        custPlanMappingService.changeStatusOfCustServices(serviceMappingIds, StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE, remark, false);
                    }
                }
                List<PostpaidPlan> plan=new ArrayList<>();
                if (flag && invoiceflag) {
                    if (customer.getCusttype().equalsIgnoreCase("Postpaid")) {
                        cprIds = customerPackageRepository.getPostpaidCustPackageIdBycustomeranddatediffDeactivate(customer.getId(), dateDiff);

                        List<Date>  datelist= customerPackageRepository.getPostpaidEndDateBycustomeranddatediffDeactivate(customer.getId(), dateDiff);
                        plan = customerPackageRepository.getPostpaidBycustomeranddatediffDeactivate(customer.getId(), dateDiff);
                        if(plan.size()>0) {
                            planname = plan.get(0).getName();
                            planDate = datelist.stream()
                                    .map(date -> LocalDate.parse(date.toString()))
                                    .collect(Collectors.toList());
//                        serviceNames = customerPackageRepository.getServiceNameWithEndDateAndPlanIdForPostpaid(customer.getId(), planDate.get(0), plan.get(0).getId());
//                        PlanService planService = planServiceRepository.findByName(serviceNames.get(0));
//                        planService.getId();


                        }else{
                            System.out.println("No Customer Found for Termination");
                        }
                    }
                    if (customer.getCusttype().equalsIgnoreCase("Prepaid")) {
                        planDate = customerPackageRepository.getPrepaidEndDateBycustomeranddatediffDeactivate(customer.getId(), dateDiff);
                        plan = customerPackageRepository.getPrepaidBycustomeranddatediffDeactivate(customer.getId(), dateDiff);
                        planname = plan.get(0).getName();
//                        serviceNames = customerPackageRepository.getServiceNameWithStartDateAndPlanIdForPrepaid(customer.getId(), planDate.get(0));
                    }
                    if (Objects.isNull(infoPojo.getClosingBalance())) {
                        infoPojo.setClosingBalance(0.0);
                    }
                    System.out.println("$#####Wallet Balance ######$"+customer.getWalletbalance());
                    if (customer.getWalletbalance() > Double.parseDouble(pendingAmount) &&  plan.size()>0) {
                        try {
//                            if (changeStatusCustomer.getStatus().equalsIgnoreCase("Active")) {
                            //planname = getAllPlan.get(0).getPlanName();
                            if (changeStatusCustomer.getStatus().equalsIgnoreCase("Active")) {
                                log.info("Customer ID: {} has grace day: {}", customer.getId(), customer.getGraceDay());
                                System.out.println("Customer ID: "+ customer.getId() +" has grace day :"+ customer.getGraceDay() );
                                System.out.println("$#####Deactivated ######$");
                                // changeStatusCustomer.setStatus("InActive");
//                                changeStatusCustomer.setIsDunningActivate(true);
//                                changeStatusCustomer.setLastDunningDate(LocalDateTime.now());
//                                changeStatusCustomer.setDunningActivateFor("Payment");
//                                changeStatusCustomer.setDunningAction("Deactivation");
//                                customersService.save(changeStatusCustomer);
//                                    String currencySymbol = clientService.getValueByName(ClientServiceConstant.CURRENCY_SYMBOL);
                                String currencySymbol = "";
                                ClientService currenyClientService = clientService.getByNameAndMvnoIdEquals(ClientServiceConstant.CURRENCY_SYMBOL, customer.getMvnoId());
                                if (currenyClientService != null) {
                                    currencySymbol = currenyClientService.getValue();
                                }
                                Optional<TemplateNotification> optionalTemplate = templateRepository
                                        .findByTemplateName(RabbitMqConstants.CUSTOMER_DEACTIVATION_TEMPLATE);
                                if (optionalTemplate.isPresent()) {
                                    if (optionalTemplate.get().isSmsEventConfigured()
                                            || optionalTemplate.get().isEmailEventConfigured()) {

                                        ArrayList<String> st = new ArrayList<>();
                                        LocalDateTime localDateTime = LocalDateTime.now();
                                        CustomerDeactivationMessage dunningMessage = new CustomerDeactivationMessage(
                                                RabbitMqConstants.CUSTOMER_DEACTIVATION_TEMPLATE_HEADER, optionalTemplate.get(),
                                                RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer,
                                                infoPojo.getClosingBalance(), currencySymbol, "Insufficient Balance/Plan Expired", planname, planDate.get(0).toString(),(long) customersService.getLoggedInStaffId());
                                        dunningMessage.setEmailConfigured(true);
                                        dunningMessage.setSmsConfigured(true);
                                        Gson gson = new Gson();
                                        gson.toJson(dunningMessage);
//                                        kafkaMessageSender.send(new KafkaMessageData(dunningMessage, CustomerDeactivationMessage.class.getSimpleName()));
//                                             messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_CUSTOMER_DEACTIVATION);
                                        sendDunningMessage(dunningMessage,customer,"Customer Payment","Deactivation");

                                    }
                                    if (ccmail.length() > 0 && ccMobile.length() > 0) {
                                        customer.setEmail(ccmail);
                                        customer.setMobile(ccMobile);
                                        CustomerDeactivationMessage dunningMessage = new CustomerDeactivationMessage(
                                                RabbitMqConstants.CUSTOMER_DEACTIVATION_TEMPLATE_HEADER, optionalTemplate.get(),
                                                RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customer,
                                                infoPojo.getClosingBalance(), currencySymbol, "Insufficient Balance/Plan Expired", planname, planDate.get(0).toString(),(long) customersService.getLoggedInStaffId());
                                        dunningMessage.setEmailConfigured(true);
                                        dunningMessage.setSmsConfigured(true);
                                        Gson gson = new Gson();
                                        gson.toJson(dunningMessage);
//                                        dunningAudit("Customer Payment to CC", "Deactivation", null, customer.getId(), null, LocalDateTime.now(), dunningMessage.toString(), customer.getMvnoId());
//                                             messageSender.send(dunningMessage, RabbitMqConstants.QUEUE_BSS_CUSTOMER_DEACTIVATION);
//                                        kafkaMessageSender.send(new KafkaMessageData(dunningMessage, CustomerDeactivationMessage.class.getSimpleName()));
                                        sendDunningMessage(dunningMessage,customer,"Customer Payment to CC","Deactivation");
                                    }
                                }

                            }

                        } catch (Throwable e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    }

                }else {
                    System.out.println("No Customer Found for Termination");
                }
            }
        }
    }
    @Async
    public <T> void sendDunningMessage(T message, Customers customer, String event, String action) {
        dunningAudit(event, action, null, customer.getId(), null,
                LocalDateTime.now(), message.toString(), customer.getMvnoId());

        kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
    }


}
