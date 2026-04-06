package com.adopt.apigw.schedulers;

import com.adopt.apigw.constants.CaseConstants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.CronJobs.CronjobConst;
import com.adopt.apigw.modules.Teams.domain.Teams;
import com.adopt.apigw.modules.Teams.service.TeamsService;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.tickets.domain.*;
//import com.adopt.apigw.modules.tickets.service.CaseUpdateService;
import com.adopt.apigw.modules.tickets.service.TicketReasonCategoryService;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.TicketAssignMessege;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.schedulerAudit.SchedulerAudit;
import com.adopt.apigw.schedulerAudit.SchedulerAuditService;
import com.adopt.apigw.service.SchedulerLockService;
import com.adopt.apigw.utils.CommonConstants;
import com.google.gson.Gson;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.enable.scheduling")
public class TicketReassignScheduler {


//    @Autowired
//    CaseService caseService;
//    @Autowired
//    CaseUpdateService caseUpdateService;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    TeamsService teamsService;
//
//    @Autowired
//    CustomersService customersService;

    @Autowired
    TicketReasonCategoryService ticketReasonCategoryService;

    @Autowired
    private NotificationTemplateRepository templateRepository;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

//    @Autowired
//    private AuthenticationManager authenticationManager;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    @Scheduled(cron = "${cronJobTimeForReassignTicket}")
    public void runSchedulerForTicket() {
        log.info("XXXXXXXXXXXX----------CRON TIME_FOR_REASSIGN_TICKET_SCHEDULER START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.TIME_FOR_REASSIGN_TICKET_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_REASSIGN_TICKET)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_REASSIGN_TICKET);
            try {
//        ApplicationLogger.logger.debug("***** Adopt Api gateway scheduler  for ticker action  Starting *****");
                System.out.println("***** Adopt Api gateway scheduler  for ticket action  Starting *****");
                List<String> status = Arrays.asList(CommonConstants.TICKET_STATUS.IN_PROGRESS, CommonConstants.TICKET_STATUS.OPEN, CommonConstants.TICKET_STATUS.PENDING, CommonConstants.TICKET_STATUS.RESOLVED, CommonConstants.TICKET_STATUS.ASSIGNED);
                QCase qCase = QCase.case$;
                JPAQuery<Case> caseJPAQuery = new JPAQuery<>(entityManager);
                BooleanExpression booleanExpression = qCase.ticketReasonCategoryId.isNotNull().and(qCase.currentAssignee.isNotNull()).and(qCase.tatMappingId.isNotNull());
                List<Tuple> caseList = caseJPAQuery.select(qCase.caseId, qCase.ticketReasonCategoryId, qCase.currentAssignee, qCase.tatMappingId, qCase.customers, qCase.nextFollowupDate, qCase.nextFollowupTime).from(qCase).where(qCase.isDelete.eq(false).and(qCase.caseStatus.in(status)).and(qCase.currentAssignee.isNotNull()).and(booleanExpression).and(qCase.nextFollowupDate.before(LocalDate.now()).or(qCase.nextFollowupDate.eq(LocalDate.now())))).fetch();
//        ApplicationLogger.logger.debug(caseJPAQuery.toString());
                if (caseList.size() > 0) caseList.forEach(caseDTO -> {
                    if (Objects.equals(caseDTO.get(qCase.nextFollowupDate), LocalDate.now())) {
                        if (!Objects.requireNonNull(caseDTO.get(qCase.nextFollowupTime)).isBefore(LocalTime.now())) {
                            return;
                        }
                    }
//            Set<Teams> teams = staffUserService.get(caseDTO.get(qCase.currentAssignee).getId()).getTeam();
                    try {
                        TicketReasonCategory ticketReasonCategoryDTO = ticketReasonCategoryService.getRepository().findById(caseDTO.get(qCase.ticketReasonCategoryId)).orElse(null);
                        if (Objects.nonNull(ticketReasonCategoryDTO)) {
                            List<TicketReasonCategoryTATMapping> ticketReasonCategoryTATMapping = ticketReasonCategoryDTO.getTicketReasonCategoryTATMappingList().stream().sorted(Comparator.comparing(TicketReasonCategoryTATMapping::getOrderNumber)).collect(Collectors.toList());
                            TicketReasonCategoryTATMapping currentTAT = ticketReasonCategoryTATMapping.stream().filter(t -> t.getMappingId().equals(caseDTO.get(qCase.tatMappingId))).findAny().orElse(null);
                            int index = ticketReasonCategoryTATMapping.indexOf(currentTAT);
                            TicketReasonCategoryTATMapping nextTAT = new TicketReasonCategoryTATMapping();
                            if (ticketReasonCategoryTATMapping.size() - 1 != index) {
                                nextTAT = ticketReasonCategoryTATMapping.get(index + 1);
                            } else if (ticketReasonCategoryTATMapping.size() - 1 == index) {
                                nextTAT = currentTAT;
                            }
                            if (nextTAT != currentTAT)
                                switch (nextTAT.getAction()) {
//                            case CommonConstants.TICKET_ACTION.BOTH:
//                                reassignTicketToNextTeamInTATMapping(nextTAT.getTeamId(), caseDTO.get(qCase.caseId), nextTAT, caseDTO.get(qCase.customers));
//                                sendNotificationToStaff(nextTAT.getTeamId(), caseDTO.get(qCase.caseId));
//                                break;
//                            case CommonConstants.TICKET_ACTION.REASSIGN:
//                                reassignTicketToNextTeamInTATMapping(nextTAT.getTeamId(), caseDTO.get(qCase.caseId), nextTAT, caseDTO.get(qCase.customers));
//                                break;
//                            case CommonConstants.TICKET_ACTION.NOTIFICATION:
//                                sendNotificationOnly(nextTAT.getTeamId(), caseDTO.get(qCase.caseId), nextTAT);
//                                break;
                                }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                });

                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Reassign Ticket Scheduler Run Success");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(caseList.size());
//        AutomatePaymentAdjust();
                System.out.println("***** Reassign Ticket Scheduler for ticket action  stopping *****");
//        ApplicationLogger.logger.debug("***** Api gateway scheduler  for ticker action  stopping *****");
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_REASSIGN_TICKET);
                log.info("XXXXXXXXXXXX---------- Reassign Ticket Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Reassign Ticket Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Reassign Ticket Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }

    }

//    void reassignTicketToNextTeamInTATMapping(Integer nextTeamId, Long caseId, TicketReasonCategoryTATMapping nextTAT, Customers customers) {
////
////        {
////            "assignee":1,
////                "remark":"sdfd",
////                "status":"Assigned",
////                "remarkType":"Update",
////                "ticketId":1,
////                "nextFollowupDate":"2022-08-25",
////                "nextFollowupTime":"11:54:35"
////        }
//
//        try {
//            Case cases = caseService.getRepository().findById(caseId).orElse(null);
//            Teams team = teamsService.getRepository().findById(Long.valueOf(nextTeamId)).orElse(null);
////   	staffUserRepository.findById(caseUpdateService.assingTicketToStaffFromTeam(team,customers));
//            StaffUser assignee = staffUserRepository.findById(caseUpdateService.assingTicketToStaffFromTeam(team, customers)).orElse(null);
//            cases.setCurrentAssignee(assignee);
//            cases.setTatMappingId(nextTAT.getMappingId());
////            cases.set
//
////            CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
////            caseUpdateDTO.setAssignee(nextTeamId);
////            caseUpdateDTO.setRemark("Reassigned because of not resolved in time.");
////            caseUpdateDTO.setRemarkType("Update");
////            caseUpdateDTO.setTicketId(cases.getCaseId());
//
//            switch (nextTAT.getTimeUnit()) {
//                case "Day":
//                    cases.setNextFollowupDate(cases.getNextFollowupDate().plusDays(cases.getPriority().equals("High") ? nextTAT.getEscalatedTime() : nextTAT.getTime()));
//                    break;
//                case "Hour":
//                    cases.setNextFollowupDate(cases.getNextFollowupDate());
//                    cases.setNextFollowupTime(cases.getNextFollowupTime().plusHours(cases.getPriority().equals("High") ? nextTAT.getEscalatedTime() : nextTAT.getTime()));
//                    break;
//                case "Min":
//                    cases.setNextFollowupDate(cases.getNextFollowupDate());
//                    cases.setNextFollowupTime(cases.getNextFollowupTime().plusMinutes(cases.getPriority().equals("High") ? nextTAT.getEscalatedTime() : nextTAT.getTime()));
//                    break;
//            }
//            cases.setCaseStatus(CaseConstants.STATUS_ASSIGNED);
//
//            CaseUpdateDetails caseUpdateDetails = new CaseUpdateDetails();
//            caseUpdateDetails.setOldvalue(cases.getCurrentAssignee().getUsername());
//            caseService.getRepository().save(cases);
//
//            //manually caseupdateobject for case
//            CaseUpdate caseUpdate = new CaseUpdate();
//            caseUpdate.setTicket(cases);
//            caseUpdate.setCreatedate(LocalDateTime.now());
//            caseUpdate.setUpdatedate(LocalDateTime.now());
//            caseUpdate.setCreatedById(cases.getCurrentAssignee().getId());
//            caseUpdate.setLastModifiedById(cases.getCurrentAssignee().getId());
//            caseUpdate.setCommentBy("Staff");
//            caseUpdate.setCreateby(cases.getCurrentAssignee().getUsername());
//            caseUpdate.setUpdateby(cases.getCurrentAssignee().getUsername());
//            caseUpdate.setCreatedByName(cases.getCurrentAssignee().getUsername());
//            caseUpdate.setRemarkType("Update");
////            cases.getCaseUpdateList().add(caseUpdate);
////            cases.setCaseUpdateList(cases.getCaseUpdateList());
////            caseService.getRepository().save(cases);
//            caseUpdate = caseUpdateService.getRepository().save(caseUpdate);
//
//            //case update details attached to case update
//            caseUpdateDetails.setNewvalue(cases.getCurrentAssignee().getUsername());
//            caseUpdateDetails.setEntitytype("Assignee");
//            caseUpdateDetails.setOperation("Change Assignee");
//            caseUpdateDetails.setCaseUpdate(caseUpdate);
//            List<CaseUpdateDetails> updateDetails = caseUpdate.getUpdateDetails();
//            updateDetails.add(caseUpdateDetails);
//            caseUpdate.setUpdateDetails(updateDetails);
//            caseUpdateService.getRepository().save(caseUpdate);
//            caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), cases.getCaseNumber(), team.getName(), cases.getNextFollowupDate().toString(), customers.getFullName(),cases.getNextFollowupTime().toString());
//
//        } catch (Exception e) {
//            ApplicationLogger.logger.info(e.getMessage());
//        }
//
//    }

//    void sendNotificationToStaff(Integer nextTeamId, Long ticketId) {
//        Case cases = caseService.getRepository().findById(ticketId).orElse(null);
//
//        Teams team = teamsService.getRepository().findById(Long.valueOf(nextTeamId)).orElse(null);
//        StaffUser staffUser = cases.getCurrentAssignee();
//        sendAssignTicketMessege(staffUser.getFullName(), staffUser.getPhone(), staffUser.getEmail(), staffUser.getMvnoId(), cases.getCaseNumber(), team.getName(), cases.getNextFollowupDate().toString(), staffUser.getFullName());
//    }


    public void sendAssignTicketMessege(String username, String mobileNumber, String emailId, Integer mvnoId, String caseNumber, String name, String nextFollowupDate, String staffusername) {
        try {

            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.TICKET_SUCCESS);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    TicketAssignMessege ticketAssignMessege = new TicketAssignMessege(username, mobileNumber, emailId, mvnoId, RabbitMqConstants.TICKET_ASSIGN_SUCCESS, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, caseNumber, name, nextFollowupDate, staffusername);
                    Gson gson = new Gson();
                    gson.toJson(ticketAssignMessege);
//                    messageSender.send(ticketAssignMessege, RabbitMqConstants.QUEUE_TICKET_ASSIGN_TEAM_SUCCESS);
                    kafkaMessageSender.send(new KafkaMessageData(ticketAssignMessege,TicketAssignMessege.class.getSimpleName() ));
                }
            } else {
                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("Ticket is not assigned.");
            }


        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

//    void sendNotificationOnly(Integer nextTeamId, Long ticketId, TicketReasonCategoryTATMapping nextTAT) {
//        Case cases = caseService.getRepository().findById(ticketId).orElse(null);
//        Teams team = teamsService.getRepository().findById(Long.valueOf(nextTeamId)).orElse(null);
////            cases.set
//
////            CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
////            caseUpdateDTO.setAssignee(nextTeamId);
////            caseUpdateDTO.setRemark("Reassigned because of not resolved in time.");
////            caseUpdateDTO.setRemarkType("Update");
////            caseUpdateDTO.setTicketId(cases.getCaseId());
//
//        if (Objects.nonNull(cases)) {
//            switch (nextTAT.getTimeUnit()) {
//                case "Day":
//                    cases.setNextFollowupDate(cases.getNextFollowupDate().plusDays(cases.getPriority().equals("High") ? nextTAT.getEscalatedTime() : nextTAT.getTime()));
//                    break;
//                case "Hour":
//                    cases.setNextFollowupDate(cases.getNextFollowupDate());
//                    cases.setNextFollowupTime(cases.getNextFollowupTime().plusHours(cases.getPriority().equals("High") ? nextTAT.getEscalatedTime() : nextTAT.getTime()));
//                    break;
//                case "Min":
//                    cases.setNextFollowupDate(cases.getNextFollowupDate());
//                    cases.setNextFollowupTime(cases.getNextFollowupTime().plusMinutes(cases.getPriority().equals("High") ? nextTAT.getEscalatedTime() : nextTAT.getTime()));
//                    break;
//            }
//            caseService.getRepository().save(cases);
//            StaffUser staffUser = cases.getCurrentAssignee();
//            sendAssignTicketMessege(staffUser.getFullName(), staffUser.getPhone(), staffUser.getEmail(), staffUser.getMvnoId(), cases.getCaseNumber(), team.getName(), cases.getNextFollowupDate().toString(), staffUser.getFullName());
//        }
//    }
}
