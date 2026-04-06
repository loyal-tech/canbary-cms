package com.adopt.apigw.utils;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaConstant;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.CustomerApprove;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.common.TicketTatAudits;
import com.adopt.apigw.model.lead.LeadMaster;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Matrix.domain.Matrix;
import com.adopt.apigw.modules.Matrix.domain.MatrixDetails;
import com.adopt.apigw.modules.Matrix.domain.QTatMatrixWorkFlowDetails;
import com.adopt.apigw.modules.Matrix.domain.TatMatrixWorkFlowDetails;
import com.adopt.apigw.modules.Matrix.repository.MatrixRepository;
import com.adopt.apigw.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
import com.adopt.apigw.modules.Teams.domain.Hierarchy;
import com.adopt.apigw.modules.Teams.domain.QueryFieldMapping;
import com.adopt.apigw.modules.Teams.domain.TeamHierarchyMapping;
import com.adopt.apigw.modules.Teams.domain.Teams;
import com.adopt.apigw.modules.Teams.repository.HierarchyRepository;
import com.adopt.apigw.modules.Teams.repository.TeamHierarchyMappingRepo;
import com.adopt.apigw.modules.Teams.repository.TeamsRepository;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.Teams.service.TeamsService;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import com.adopt.apigw.modules.TicketTatMatrix.Domain.TicketTatMatrixMapping;
import com.adopt.apigw.modules.TicketTatMatrix.Repository.TicketTatMatrixRepository;
import com.adopt.apigw.modules.tickets.domain.*;
import com.adopt.apigw.modules.tickets.model.CaseDTO;
import com.adopt.apigw.modules.tickets.repository.*;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.pojo.api.LeadMgmtWfDTO;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.SendApproverForLeadMsg;
import com.adopt.apigw.rabbitMq.message.SendLeadAssignMessage;
import com.adopt.apigw.rabbitMq.message.TicketAssignMessege;
import com.adopt.apigw.rabbitMq.message.TicketPickMessageToTeam;
import com.adopt.apigw.repository.LeadMasterRepository;
import com.adopt.apigw.repository.common.CustomerApproveRepo;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.common.TatAuditRepository;
import com.adopt.apigw.repository.postpaid.CreditDocRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.common.WorkflowAuditService;
import com.adopt.apigw.spring.LoggedInUser;
import com.google.gson.Gson;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.adopt.apigw.core.utillity.log.ApplicationLogger.logger;

@Component
public class TatUtils {

    private static final Logger logger = LoggerFactory.getLogger(TatUtils.class);

    @Autowired
    private NotificationTemplateRepository templateRepository;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private TeamHierarchyMappingRepo teamHierarchyMappingRepo;

    @Autowired
    private TatMatrixWorkFlowDetailsRepo tatMatrixWorkFlowDetailsRepo;

    @Autowired
    private MatrixRepository matrixRepository;

    @Autowired
    private HierarchyRepository hierarchyRepository;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private HierarchyService hierarchyService;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private PostpaidPlanMapper postpaidPlanMapper;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private WorkflowAuditService workflowAuditService;

    @Autowired
    private TicketReasonCategoryTATMappingRepo ticketReasonCategoryTATMappingRepo;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private TicketReasonSubCategoryRepo ticketReasonSubCategoryrepo;

    @Autowired
    private TicketTatMatrixRepository ticketTatMatrixRepository;

    @Autowired
    private WorkFlowQueryUtils workFlowQueryUtils;

    @Autowired
    private TicketSubCategoryTatMappingRepo ticketSubCategoryTatMappingRepo;

    @Autowired
    private TatQueryFieldMappingRepo tatQueryFieldMappingRepo;

    @Autowired
    private TeamsService teamsService;
    @Autowired
    private CustomerApproveRepo customerApproveRepo;

    @Autowired
    private TatAuditRepository tatAuditRepository;
    @Autowired
    private CustomersService customersService;
    @Autowired
    private LeadMasterRepository leadMasterRepository;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private  TeamsRepository teamsRepository;


    public void sendNotificationToStaff(TatMatrixWorkFlowDetails details) {
        StaffUser staffUser = staffUserRepository.findById(details.getStaffId()).get();//staffUserService.get(details.getStaffId());
        if (details.getParentId() != null ) {
            StaffUser parentUser = staffUserRepository.findById(staffUser.getStaffUserparent().getId()).get();//staffUserService.get(details.getParentId());
//        sendTatNotification(parentUser.getUsername(), parentUser.getPhone(), parentUser.getEmail(), parentUser.getMvnoId(), details.getEventName(), details.getEntityId().toString(),
//                details.getNextFollowUpDate().toString(), staffUser.getUsername());

            //Setting ticket notification audit

            TicketTatAudits tatAudits = saveTatDetails(details.getEntityId(), details);

            if (details.getNotificationType() != null && details.getNotificationType().equals(CommonConstants.NOTIFICATION_TYPE_TEAM)) {
                Teams team = new Teams();
                String caseNumber = null;
                if (details.getTeamId() != null) {
                    team = teamsService.getById(details.getTeamId());
                }
                Optional<Customers> customers = customersRepository.findById(details.getEntityId());
                Optional<Case> caseObj = caseRepository.findById(Long.valueOf(details.getEntityId()));
                if (caseObj.isPresent()) {
                    caseNumber = caseObj.get().getCaseNumber();
                }
                Long buId = null;
                if (!parentUser.getBusinessUnitNameList().isEmpty()) {
                    buId = parentUser.getBusinessUnitNameList().get(0).getId();
                }

                sendTatNotificationTypeTeam(parentUser.getUsername(), team.getName(), details.getEventName(), details.getNextFollowUpDate().toString(), parentUser.getPhone(), parentUser.getEmail(), parentUser.getMvnoId(), caseNumber, tatAudits, buId);

                System.out.println("---------------------Notification for Response Time Breach sent to ParentStaff for team : " + team.getName() + "------------------------");

            } else {
                Long buId = null;
                if (!parentUser.getBusinessUnitNameList().isEmpty()) {
                    buId = parentUser.getBusinessUnitNameList().get(0).getId();
                }

                if(parentUser.getMvnoId().equals(1) ||parentUser.getMvnoId()==1){
                   if(details.getEventName().equals(CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION) || details.getEventName().equals(CommonConstants.WORKFLOW_EVENT_NAME.CAF)){
                     Customers customers=customersRepository.findById(details.getEntityId()).get();
                       sendTatNotification(parentUser.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), parentUser.getPhone(), parentUser.getEmail(), customers.getMvnoId().intValue(), tatAudits,customers.getBuId());
                    }
                    else  if(details.getEventName().equals(CommonConstants.WORKFLOW_EVENT_NAME.LEAD)){
                        LeadMaster leadMaster=leadMasterRepository.findById(details.getEntityId().longValue()).get();
                       sendTatNotification(parentUser.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), parentUser.getPhone(), parentUser.getEmail(), leadMaster.getMvnoId().intValue(), tatAudits,leadMaster.getBuId());
                    }

                }else{
                    sendTatNotification(parentUser.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), parentUser.getPhone(), parentUser.getEmail(), parentUser.getMvnoId(), tatAudits,buId);
                }

             //  sendTatNotification(parentUser.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), parentUser.getPhone(), parentUser.getEmail(), parentUser.getMvnoId(), tatAudits, buId);
                System.out.println("---------------------Notification for TAT Time Breach sent to ParentStaff for staff : " + staffUser.getUsername() + "------------------------");

            }
            details.setStartDateTime(LocalDateTime.now());
//            details.setParentId(null);
//            if (parentUser != null)
//                if (parentUser.getStaffUserparent() != null)
//                    details.setParentId(parentUser.getStaffUserparent().getId());
//            if (details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
//                try {
//                    Optional<Case> caseEntity = caseRepository.findById(Long.valueOf(details.getEntityId()));
//                    if (details.getNotificationType() != null && !details.getNotificationType().equals(CommonConstants.NOTIFICATION_TYPE_TEAM)) {
//                        if (caseEntity.isPresent()) {
//                            TicketTatMatrix ticketTatMatrix = caseService.getTicketTatMatrixFromSubReasonId(caseService.getMapper().domainToDTO(caseEntity.get(), new CycleAvoidingMappingContext()));
//                            if (ticketTatMatrix != null) {
//                                Case case1;
//                                Optional<TicketTatMatrixMapping> ticketTatMatrix1 = ticketTatMatrix.getTatMatrixMappings().stream().filter(p -> p.getOrderNo().equals(details.getOrderNo() + 1)).findFirst();
//                                if (ticketTatMatrix1 != null && ticketTatMatrix1.isPresent()) {
//                                    Integer Nextvalue = Integer.parseInt(String.valueOf(ticketTatMatrix1.get().getMtime1()));
//                                    if (caseEntity.get().getPriority().equals("High")) {
//                                        details.setMtime(String.valueOf(ticketTatMatrix1.get().getMtime1()));
//                                        case1 = UpdateDateTime(ticketTatMatrix1.get(), caseEntity.get(), Nextvalue);
//                                    } else if (caseEntity.get().getPriority().equals("Medium")) {
//                                        details.setMtime(String.valueOf(ticketTatMatrix1.get().getMtime2()));
//                                        case1 = UpdateDateTime(ticketTatMatrix1.get(), caseEntity.get(), Nextvalue);
//                                    } else {
//                                        details.setMtime(String.valueOf(ticketTatMatrix1.get().getMtime3()));
//                                        case1 = UpdateDateTime(ticketTatMatrix1.get(), caseEntity.get(), Nextvalue);
//                                    }
//                                    caseRepository.save(case1);
//                                    details.setMunit(ticketTatMatrix1.get().getMunit());
//                                    details.setAction(ticketTatMatrix1.get().getAction());
//                                    details.setOrderNo(ticketTatMatrix1.get().getOrderNo());
//                                    details.setLevel(ticketTatMatrix1.get().getLevel());
//                                    details.setStaffId(parentUser.getId());
//                                    StaffUser newStaffuser = staffUserRepository.findById(details.getStaffId()).orElse(null);
//                                    details.setParentId(newStaffuser.getStaffUserparent().getId());
//                                } else {
//                                    details.setIsActive(false);
//                                    tatMatrixWorkFlowDetailsRepo.save(details);
//                                }
//                            } else {
//                                details.setIsActive(false);
//                                tatMatrixWorkFlowDetailsRepo.save(details);
//                            }
//                        }
//                    } else {
//                        details.setIsActive(false);
//                        tatMatrixWorkFlowDetailsRepo.save(details);
//                    }
//
//                } catch (Exception ex) {
//                    logger.error("Error during find caseDTO: " + ex.getMessage());
//                }
//            } else {
            Optional<Matrix> matrixDetails = matrixRepository.findById(details.getTatMatrixId());
            if (matrixDetails.isPresent()) {
                Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted() && dtl.getOrderNo().equals(details.getOrderNo() + 1)).findFirst();
              HashMap<String, String> map = new HashMap<>();
                if (newMatrixDetails.isPresent()) {
                    Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));

                    if (details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.CAF)|| details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION)) {
                        Optional<Customers> customers=customersRepository.findById(details.getEntityId());
                        if (newMatrixDetails.isPresent()) {
                            Customers customers1 = (Customers) UpdateDateTimefortat(newMatrixDetails.get(), customers.get(), Nextvalue);
                            StaffUser staffuseparent=staffUserRepository.findById(staffUser.getStaffUserparent().getId()).get();
                            if(staffuseparent!=null){
                                details.setParentId(staffuseparent.getId());
                            }
//                          map= (HashMap<String, String>) hierarchyService.getTeamForNextApproveForAuto(customers1.getMvnoId(), customers1.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, CommonConstants.HIERARCHY_TYPE, true, false, customerMapper.domainToDTO(customers1, new CycleAvoidingMappingContext()));
                            customersRepository.save(customers1);
//                            if (staffUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
//                                saveOrUpdateDataForTatMatrix(map, staffUser.getStaffUserparent(), customers1.getId(), details.getId());
//                            }
                        }
                    } else if (details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.LEAD)) {
                        if (newMatrixDetails.isPresent()) {
                            Optional<LeadMaster> leadMaster=leadMasterRepository.findById(details.getEntityId().longValue());
                            LeadMaster leadMaster1 = (LeadMaster) UpdateDateTimefortat(newMatrixDetails.get(), leadMaster.get(), Nextvalue);
                            details.setStaffId(details.getStaffId());
                            StaffUser staffuseparent=staffUserRepository.findById(staffUser.getStaffUserparent().getId()).get();
                            if(staffuseparent!=null){
                                details.setParentId(staffuseparent.getId());
                            }
                            leadMasterRepository.save(leadMaster1);
                            LeadMgmtWfDTO leadMgmtWfDTO = hierarchyService.leadMasterToLeadMgmtDTO(leadMaster1);
//                            map= (HashMap<String, String>) hierarchyService.getTeamForNextApproveForAuto(leadMaster1.getMvnoId().intValue(), leadMaster1.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, CommonConstants.HIERARCHY_TYPE, true, false,leadMgmtWfDTO );
//                            if (staffUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
//                                saveOrUpdateDataForTatMatrix(map, staffUser.getStaffUserparent(), leadMaster1.getId().intValue(), details.getId());
//                            }
                            SendLeadAssignMessage sendApproverForLeadMsg = new SendLeadAssignMessage(leadMgmtWfDTO);
                            kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendLeadAssignMessage.class.getSimpleName()));
//                            messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_LEAD_ASSIGN_MESSAGE);
                        }
                    }

                    details.setMtime(newMatrixDetails.get().getMtime());
                    details.setAction(newMatrixDetails.get().getAction());
                    details.setMunit(newMatrixDetails.get().getMunit());
                    details.setOrderNo(newMatrixDetails.get().getOrderNo());
                    details.setLevel(newMatrixDetails.get().getLevel());
                    details.setStaffId(details.getStaffId());
                } else {
                    details.setIsActive(false);
                    tatMatrixWorkFlowDetailsRepo.save(details);
                }
                tatMatrixWorkFlowDetailsRepo.save(details);
            }


            }
        }



    public void sendTatNotification(String parentStaffPersonName, String staffPersonName, String eventName, String assigndatetime, String mobileNumber, String emailId, Integer mvnoId, TicketTatAudits tatAudits, Long buId) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.TAT_SUCCESS);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    TicketAssignMessege ticketAssignMessege = new TicketAssignMessege(mobileNumber, emailId, RabbitMqConstants.TAT_SUCCESS, optionalTemplate.get(), parentStaffPersonName, staffPersonName, assigndatetime, eventName, mvnoId, tatAudits, buId);
                    Gson gson = new Gson();
                    gson.toJson(ticketAssignMessege);
                    if(eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
//                        messageSender.send(ticketAssignMessege, RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE);
                        kafkaMessageSender.send(new KafkaMessageData(ticketAssignMessege, TicketAssignMessege.class.getSimpleName()));
                    }else  if(eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION)){
//                        messageSender.send(ticketAssignMessege, RabbitMqConstants.QUEUE_TREMINATION_TAT_SUCCESS_MESSAGE);
                        kafkaMessageSender.send(new KafkaMessageData(ticketAssignMessege, TicketAssignMessege.class.getSimpleName(),KafkaConstant.TREMINATION_TAT_SUCCESS_MESSAGE));
                    }
                    else  if(eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.CAF)){
//                        messageSender.send(ticketAssignMessege, RabbitMqConstants.QUEUE_CAF_TAT_SUCCESS_MESSAGE);
                        kafkaMessageSender.send(new KafkaMessageData(ticketAssignMessege, TicketAssignMessege.class.getSimpleName(), KafkaConstant.CAF_TAT_SUCCESS_MESSAGE));
                    }
                    else  if(eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.LEAD)){
//                        messageSender.send(ticketAssignMessege, RabbitMqConstants.QUEUE_LEAD_TAT_SUCCESS_MESSAGE);
                        kafkaMessageSender.send(new KafkaMessageData(ticketAssignMessege, TicketAssignMessege.class.getSimpleName(),KafkaConstant.LEAD_TAT_SUCCESS_MESSAGE));
                    }else{
                        System.out.println("TAT Template not available.");
                    }
                }
            } else {
//                 log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("TAT Template not available.");
            }


        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//    public Case updateticketTatMatrix(CaseDTO caseDTO) {
//
//        Case updateCase = new Case();
//        if (caseDTO.getCurrentAssigneeId() != null) {
//            StaffUser currentAssignStaff = new StaffUser();
//            currentAssignStaff = staffUserRepository.findById(caseDTO.getCurrentAssigneeId()).orElse(null);
//            TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.
//                    findByStaffIdAndEntityIdAndEventNameAndIsActive(caseDTO.getCurrentAssigneeId(), caseDTO.getCaseId().intValue(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, true);
//
//            if (tatMatrixWorkFlowDetails != null) {
//                Integer prevoiusOrderNo = Math.toIntExact(tatMatrixWorkFlowDetails.getOrderNo());
//                String previousLevel = tatMatrixWorkFlowDetails.getLevel();
//                TatMatrixWorkFlowDetails newTatMatrixWorkFlowDetails = new TatMatrixWorkFlowDetails();
//                tatMatrixWorkFlowDetails.setIsActive(false);
//                tatMatrixWorkFlowDetails.setIsOverDueReminder(false);
//                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
//                //TicketReasonCategoryTATMapping mapping = ticketReasonCategoryTATMappingRepo.findById(caseDTO.getTatMappingId()).orElse(null);
//                TicketTatMatrix masterTicketTat = caseService.getTicketTatMatrixFromSubReasonId(caseDTO);
//                if (masterTicketTat != null) {
//
//                    List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
//                    Case newcase = caseMapper.dtoToDomain(caseDTO, new CycleAvoidingMappingContext());
//                    for (int i = 0; i < tatMatrixMappings.size(); i++) {
//                        if (tatMatrixMappings.get(i).getOrderNo() == prevoiusOrderNo.intValue() ) {
//                            Integer Nextvalue = Integer.parseInt(String.valueOf(tatMatrixMappings.get(i).getMtime1()));
//                            if (caseDTO.getPriority().equalsIgnoreCase("high") ) {
//                                newTatMatrixWorkFlowDetails =
//                                        new TatMatrixWorkFlowDetails(Objects.requireNonNull(tatMatrixMappings.get(i).getOrderNo()), tatMatrixMappings.get(i).getLevel(), caseDTO.getCurrentAssigneeId(),
//                                                tatMatrixWorkFlowDetails.getWorkFlowId(), null, currentAssignStaff.getStaffUserparent().getId(), LocalDateTime.now(), String.valueOf(tatMatrixMappings.get(i).getMtime1()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null, caseDTO.getCaseId().intValue(), tatMatrixWorkFlowDetails.getEventName(), tatMatrixWorkFlowDetails.getEventId(), CommonConstants.NOTIFICATION_TYPE_STAFF, null,true);
//                               updateCase =  caseRepository.save(UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
//                            } else if (caseDTO.getPriority().equalsIgnoreCase("medium")) {
//                                newTatMatrixWorkFlowDetails =
//                                        new TatMatrixWorkFlowDetails(Objects.requireNonNull(tatMatrixMappings.get(i).getOrderNo()), tatMatrixMappings.get(i).getLevel(), caseDTO.getCurrentAssigneeId(),
//                                                tatMatrixWorkFlowDetails.getWorkFlowId(), null, currentAssignStaff.getStaffUserparent().getId(), LocalDateTime.now(), String.valueOf(tatMatrixMappings.get(i).getMtime2()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null, caseDTO.getCaseId().intValue(), tatMatrixWorkFlowDetails.getEventName(), tatMatrixWorkFlowDetails.getEventId(), CommonConstants.NOTIFICATION_TYPE_STAFF, null,true);
//                               updateCase = caseRepository.save(UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
//                            } else {
//                                newTatMatrixWorkFlowDetails =
//                                        new TatMatrixWorkFlowDetails(Objects.requireNonNull(tatMatrixMappings.get(i).getOrderNo()), tatMatrixMappings.get(i).getLevel(), caseDTO.getCurrentAssigneeId(),
//                                                tatMatrixWorkFlowDetails.getWorkFlowId(), null, currentAssignStaff.getStaffUserparent().getId(), LocalDateTime.now(), String.valueOf(tatMatrixMappings.get(i).getMtime3()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null, caseDTO.getCaseId().intValue(), tatMatrixWorkFlowDetails.getEventName(), tatMatrixWorkFlowDetails.getEventId(), CommonConstants.NOTIFICATION_TYPE_STAFF, null,true);
//                               updateCase = caseRepository.save(UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
//                            }
//                        }
//                    }
//                }
//
//
////                TatMatrixWorkFlowDetails newTatMatrixWorkFlowDetails =
////                        new TatMatrixWorkFlowDetails(Objects.requireNonNull(mapping).getOrderNumber(), mapping.getLevel(), caseDTO.getCurrentAssigneeId(),
////                                tatMatrixWorkFlowDetails.getWorkFlowId(), null, tatMatrixWorkFlowDetails.getStaffId(), LocalDateTime.now(), String.valueOf(mapping.getTime()), mapping.getTimeUnit(), mapping.getAction(), true, null, caseDTO.getCaseId().intValue(), tatMatrixWorkFlowDetails.getEventName(), tatMatrixWorkFlowDetails.getEventId(), CommonConstants.NOTIFICATION_TYPE_STAFF, null);
////                if(caseDTO.getPriority().equalsIgnoreCase("High")) {
////                    newTatMatrixWorkFlowDetails.setMtime(String.valueOf(mapping.getEscalatedTime()));
////                }else if(caseDTO.getPriority().equalsIgnoreCase("Medium")) {
////                    if(mapping.getMediumTime() != null)
////                        newTatMatrixWorkFlowDetails.setMtime(String.valueOf(mapping.getMediumTime()));
////                }else if(caseDTO.getPriority().equalsIgnoreCase("Low")) {
////                    newTatMatrixWorkFlowDetails.setMtime(String.valueOf(mapping.getTime()));
////                }
//                tatMatrixWorkFlowDetailsRepo.save(newTatMatrixWorkFlowDetails);
//            }
//        }
//        return updateCase;
//    }


    public Case UpdateDateTime(TicketTatMatrixMapping tatMatrixMappings, Case newcase, Integer Nextvalue) {
        if (tatMatrixMappings.getMunit().equalsIgnoreCase("Day")) {
            newcase.setNextFollowupDate(LocalDate.now().plusDays(Nextvalue));
        }
        if (tatMatrixMappings.getMunit().equalsIgnoreCase("Min")) {
            newcase.setNextFollowupTime(LocalTime.now().plusMinutes(Nextvalue));
        }
        if (tatMatrixMappings.getMunit().equalsIgnoreCase("Hour")) {
            newcase.setNextFollowupTime(LocalTime.now().plusHours(Nextvalue));
        }
        return newcase;
    }

    public Object UpdateDateTimefortat(MatrixDetails tatMatrixMappings, Object object, Integer Nextvalue) {
        if (object instanceof Customers) {
            if (tatMatrixMappings.getMunit().equalsIgnoreCase("Day")) {
                ((Customers) object).setNextfollowupdate(LocalDate.now().plusDays(Nextvalue));
                ((Customers) object).setNextfollowuptime(LocalTime.MIDNIGHT);
            } else if (tatMatrixMappings.getMunit().equalsIgnoreCase("Min")) {
                LocalTime currentTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
                long minutesToAdd = Nextvalue;
                long hoursToAdd = minutesToAdd / 60;
                minutesToAdd %= 60;
                LocalTime nextTime = currentTime.plusHours(hoursToAdd).plusMinutes(minutesToAdd);
                if (nextTime.isBefore(currentTime)) {
                    ((Customers) object).setNextfollowupdate(LocalDate.now().plusDays(1));
                }
                ((Customers) object).setNextfollowupdate(LocalDate.now());
                ((Customers) object).setNextfollowuptime(nextTime);
            } else if (tatMatrixMappings.getMunit().equalsIgnoreCase("Hour")) {
                LocalTime currentTime = LocalTime.now();
                long hoursToAdd = Nextvalue;
                LocalTime nextTime = currentTime.plusHours(hoursToAdd);
                if (nextTime.isBefore(currentTime)) {
                    ((Customers) object).setNextfollowupdate(LocalDate.now().plusDays(1));
                }
                ((Customers) object).setNextfollowupdate(LocalDate.now());
                ((Customers) object).setNextfollowuptime(nextTime);
            } else {
                logger.info("Invalid time unit");
            }
        } else if (object instanceof LeadMaster) {
            if (tatMatrixMappings.getMunit().equalsIgnoreCase("Day")) {
                ((LeadMaster) object).setNextfollowupdate(LocalDate.now().plusDays(Nextvalue));
                ((LeadMaster) object).setNextfollowuptime(LocalTime.MIDNIGHT);
            } else if (tatMatrixMappings.getMunit().equalsIgnoreCase("Min")) {
                LocalTime currentTime = LocalTime.now();
                long minutesToAdd = Nextvalue;
                long hoursToAdd = minutesToAdd / 60;
                minutesToAdd %= 60;
                LocalTime nextTime = currentTime.plusHours(hoursToAdd).plusMinutes(minutesToAdd);
                if (nextTime.isBefore(currentTime)) {
                    ((LeadMaster) object).setNextfollowupdate(LocalDate.now().plusDays(1));
                }
                ((LeadMaster) object).setNextfollowupdate(LocalDate.now());
                ((LeadMaster) object).setNextfollowuptime(nextTime);
            } else if (tatMatrixMappings.getMunit().equalsIgnoreCase("Hour")) {
                LocalTime currentTime = LocalTime.now();
                long hoursToAdd = Nextvalue;
                LocalTime nextTime = currentTime.plusHours(hoursToAdd);
                if (nextTime.isBefore(currentTime)) {
                    ((LeadMaster) object).setNextfollowupdate(LocalDate.now().plusDays(1));
                }
                ((LeadMaster) object).setNextfollowupdate(LocalDate.now());
                ((LeadMaster) object).setNextfollowuptime(nextTime);
            } else {
                logger.info("Invalid time unit");
            }
        } else {
            logger.info("Invalid object type");
        }
        return object;
    }

    public void changeTicketTatStatus(CaseDTO caseDTO, boolean status) {
        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.
                findAllByStaffIdAndEntityIdAndEventNameAndIsActive(caseDTO.getCurrentAssigneeId(), caseDTO.getCaseId().intValue(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, true);
        if (!CollectionUtils.isEmpty(tatMatrixWorkFlowDetails)) {
            tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetails.stream()
                    .peek(tatMatrix -> {
                        tatMatrix.setIsActive(false);
                        tatMatrix.setIsOverDueReminder(false);
                    })
                    .collect(Collectors.toList());
            tatMatrixWorkFlowDetailsRepo.saveAll(tatMatrixWorkFlowDetails);
        }
    }

    public void changeTicketTatAssignee(CaseDTO caseDTO, StaffUser assignedStaff, boolean isNotificationTypeTeam, Boolean isPickeddUp) {
        try {
            Map<String, String> map = new HashMap<>();
            Optional<TeamHierarchyMapping> teamHierarchyMapping = teamHierarchyMappingRepo.findById(caseDTO.getTeamHierarchyMappingId().intValue());
            if (teamHierarchyMapping.isPresent()) {
                map.put("workFlowId", teamHierarchyMapping.get().getHierarchyId().toString());
                map.put("eventName", CommonConstants.WORKFLOW_EVENT_NAME.CASE);
                map.put("eventId", caseDTO.getCaseId().toString());
                map.put("teamId", teamHierarchyMapping.get().getTeamId().toString());
                if (isPickeddUp)
                    map.put("fromPickedUp", "true");
                else
                    map.put("fromPickedUp", "false");
                saveOrUpdateTicketTatMatrix(caseDTO, map, assignedStaff, isNotificationTypeTeam);
                // caseService.updateFollowUpDateAndTimeForTicketAfterPickedUp(caseMapper.dtoToDomain(caseDTO,new CycleAvoidingMappingContext()));
            }
        } catch (Exception ex) {
            logger.error("Exception at change assignee on ticket tat: " + ex.getMessage());
        }
    }

    public Boolean checkTicketTatCondition(List<TatQueryFieldMapping> tatQueryFieldMappingList, CaseDTO caseDTO) {
        List<QueryFieldMapping> queryFieldMappingList = new ArrayList<>();
        for (TatQueryFieldMapping queryFieldMapping : tatQueryFieldMappingList) {
            QueryFieldMapping mapping = new QueryFieldMapping();
            mapping.setQueryOperator(queryFieldMapping.getQueryOperator());
            mapping.setQueryCondition(queryFieldMapping.getQueryCondition());
            mapping.setQueryValue(queryFieldMapping.getQueryValue());
            mapping.setQueryField(queryFieldMapping.getQueryField());
            queryFieldMappingList.add(mapping);
        }
        if (CollectionUtils.isEmpty(queryFieldMappingList))
            return false;

        return workFlowQueryUtils.checkCondition(queryFieldMappingList, CommonConstants.WORKFLOW_EVENT_NAME.CASE, caseDTO);
    }

    public void saveOrUpdateTicketTatMatrix(CaseDTO caseDTO, Map<String, String> map, StaffUser assignedStaff, boolean isNotificationTypeTeam) {
        if (caseDTO.getReasonSubCategoryId() != null) {
            Optional<TicketReasonSubCategory> ticketSubReasonCategory = ticketReasonSubCategoryrepo.findById(caseDTO.getReasonSubCategoryId());
            if (ticketSubReasonCategory.isPresent()) {
                if (!CollectionUtils.isEmpty(ticketSubReasonCategory.get().getTicketSubCategoryTatMappingList())) {
                    List<TicketSubCategoryTatMapping> ticketSubCategoryTatMappings = ticketSubReasonCategory.get().getTicketSubCategoryTatMappingList();
                    for (TicketSubCategoryTatMapping ticketSubCategoryTatMapping : ticketSubCategoryTatMappings) {
                        QTatQueryFieldMapping qTatQueryFieldMapping = QTatQueryFieldMapping.tatQueryFieldMapping;
                        BooleanExpression exp = qTatQueryFieldMapping.isNotNull().and(qTatQueryFieldMapping.tatMappingId.eq(ticketSubCategoryTatMapping.getId().intValue()));
                        List<TatQueryFieldMapping> tatQueryFieldMappingList = (List<TatQueryFieldMapping>) tatQueryFieldMappingRepo.findAll(exp);
                        if (!CollectionUtils.isEmpty(tatQueryFieldMappingList)) {
                            //If query not matched then skip
                            if (!checkTicketTatCondition(tatQueryFieldMappingList, caseDTO))
                                continue;
                        }
                        if (!isNotificationTypeTeam) {
                            List<TatMatrixWorkFlowDetails> preTatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findAllByWorkFlowIdAndEntityIdAndEventNameAndIsActive(Long.valueOf(map.get("workFlowId")), Integer.valueOf(map.get("eventId")), map.get("eventName"), true);
                            if (!CollectionUtils.isEmpty(preTatMatrixWorkFlowDetails) && !isNotificationTypeTeam) {
                                preTatMatrixWorkFlowDetails = preTatMatrixWorkFlowDetails.stream()
                                        .peek(tatMatrixWorkFlowDetails -> {
                                            tatMatrixWorkFlowDetails.setIsActive(false);
                                            tatMatrixWorkFlowDetails.setIsOverDueReminder(false);
                                        })
                                        .collect(Collectors.toList());
                                tatMatrixWorkFlowDetailsRepo.saveAll(preTatMatrixWorkFlowDetails);
                            }
                        } else {
                            List<TatMatrixWorkFlowDetails> preTatMatrixWorkFlowList = tatMatrixWorkFlowDetailsRepo.findAllByWorkFlowIdAndEntityIdAndEventNameAndIsActive(Long.valueOf(map.get("workFlowId")), Integer.valueOf(map.get("eventId")), map.get("eventName"), true);
                            if (map.get("fromPickedUp").equals("true")) {
                                for (int i = 0; i < preTatMatrixWorkFlowList.size(); i++) {
                                    if (preTatMatrixWorkFlowList.get(i) != null && isNotificationTypeTeam) {
                                        preTatMatrixWorkFlowList.get(i).setIsActive(false);
                                        preTatMatrixWorkFlowList.get(i).setIsOverDueReminder(false);
                                        tatMatrixWorkFlowDetailsRepo.save(preTatMatrixWorkFlowList.get(i));
                                    }
                                }
                            }

                        }
                        TicketTatMatrix masterTicketTat = ticketSubCategoryTatMapping.getTicketTatMatrix();
                        List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
                        if (!isNotificationTypeTeam) {
                            if (!CollectionUtils.isEmpty(tatMatrixMappings)) {
                                Optional<TicketTatMatrixMapping> mapping = tatMatrixMappings.stream().filter(ticketTatMatrixMapping -> ticketTatMatrixMapping.getOrderNo().equals(1l)).findFirst();
                                if (mapping.isPresent()) {
                                    if (mapping != null && assignedStaff.getStaffUserparent() != null) {
                                        TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
                                                new TatMatrixWorkFlowDetails(mapping.get().getOrderNo(), mapping.get().getLevel(), assignedStaff.getId(),
                                                        Long.valueOf(map.get("workFlowId")), null,
                                                        (assignedStaff != null && assignedStaff.getStaffUserparent() != null) ? assignedStaff.getStaffUserparent().getId() : null, LocalDateTime.now(),
                                                        String.valueOf(mapping.get().getMtime3()), mapping.get().getMunit(), mapping.get().getAction(), true, null,
                                                        caseDTO.getCaseId().intValue(), map.get("eventName"), Integer.valueOf(map.get("eventId")), CommonConstants.NOTIFICATION_TYPE_STAFF,
                                                        null, true);
                                        if (caseDTO.getPriority().equalsIgnoreCase("High")) {
                                            tatMatrixWorkFlowDetails.setMtime(String.valueOf(mapping.get().getMtime1()));
                                        } else if (caseDTO.getPriority().equalsIgnoreCase("Medium")) {
                                            if (mapping.get().getMunit() != null)
                                                tatMatrixWorkFlowDetails.setMtime(String.valueOf(mapping.get().getMtime2()));
                                        } else if (caseDTO.getPriority().equalsIgnoreCase("Low")) {
                                            tatMatrixWorkFlowDetails.setMtime(String.valueOf(mapping.get().getMtime3()));
                                        }
                                        tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                                        break;
                                    }
                                }
                            }
                        } else if (isNotificationTypeTeam) {
                            if (masterTicketTat != null && assignedStaff.getStaffUserparent() != null) {
                                //boolean flag=  caseService.updateTicketLevel(caseDTO,  map,masterTicketTat);
                                // if(!flag) {
//                                TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
//                                        new TatMatrixWorkFlowDetails(new Long(1), "Level 1", assignedStaff.getId(),
//                                                Long.valueOf(map.get("workFlowId")), null,
//                                                assignedStaff.getStaffUserparent().getId(), LocalDateTime.now(),
//                                                String.valueOf(masterTicketTat.getRtime()), masterTicketTat.getRunit(), masterTicketTat.getTatMatrixMappings().get(0).getAction(), true, null,
//                                                caseDTO.getCaseId().intValue(), map.get("eventName"), Integer.valueOf(map.get("eventId")),
//                                                CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf(map.get("teamId")), true);
//                                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                                if (!caseDTO.getCaseStatus().equalsIgnoreCase("Follow Up")) {
                                    TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
                                            new TatMatrixWorkFlowDetails(new Long(1), "Level 1", assignedStaff.getId(),
                                                    Long.valueOf(map.get("workFlowId")), null,
                                                    (assignedStaff != null && assignedStaff.getStaffUserparent() != null) ? assignedStaff.getStaffUserparent().getId() : null, LocalDateTime.now(),
                                                    String.valueOf(masterTicketTat.getRtime()), masterTicketTat.getRunit(), "Notification", true, null,
                                                    caseDTO.getCaseId().intValue(), map.get("eventName"), Integer.valueOf(map.get("eventId")),
                                                    CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf(map.get("teamId")), true);
                                    tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                                } else {
                                    LocalDateTime followUpDateTime = LocalDateTime.of(caseDTO.getNextFollowupDate(), caseDTO.getNextFollowupTime());
                                    TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
                                            new TatMatrixWorkFlowDetails(new Long(1), "Level 1", assignedStaff.getId(),
                                                    Long.valueOf(map.get("workFlowId")), null,
                                                    (assignedStaff != null && assignedStaff.getStaffUserparent() != null) ? assignedStaff.getStaffUserparent().getId() : null, followUpDateTime,
                                                    String.valueOf(masterTicketTat.getRtime()), masterTicketTat.getRunit(), "Notification", true, null,
                                                    caseDTO.getCaseId().intValue(), map.get("eventName"), Integer.valueOf(map.get("eventId")),
                                                    CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf(map.get("teamId")), true);
                                    tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                                }
                                break;
                                //}
                            }
                        }

                    }
                }
            }
        }
    }


    public void saveOrUpdateDataForTatMatrix(Map<String, String> map, StaffUser assignedToStaff, Integer entityId, Long preTatMatrixId) {
        try {
            if (!CollectionUtils.isEmpty(map)) {
                if (map.get("tat_id") != null && map.get("tat_id") != "null") {
                    TeamHierarchyMapping prevTeamHierarchyMapping = teamHierarchyMappingRepo.findByOrderNumberAndHierarchyId(Integer.valueOf(map.get("orderNo")) - 1, Integer.valueOf(map.get("workFlowId")));
                    Optional<TatMatrixWorkFlowDetails> preTatMatrixWorkFlowDetails = null;//Optional.of(new TatMatrixWorkFlowDetails());
                    Long preOrderNo = 0l;
                    if (prevTeamHierarchyMapping != null) {
                        if (preTatMatrixId != null) {
                            preTatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findById(preTatMatrixId);
                        } else {
                            preTatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findByWorkFlowIdAndCurrentTeamHeirarchyMappingIdAndIsActive(Long.valueOf(prevTeamHierarchyMapping.getHierarchyId()), prevTeamHierarchyMapping.getId(), true);
                        }
                    }
                    if (preTatMatrixWorkFlowDetails != null && preTatMatrixWorkFlowDetails.isPresent()) {
                        preTatMatrixWorkFlowDetails.get().setIsActive(false);
                        preTatMatrixWorkFlowDetails.get().setIsOverDueReminder(false);
                        preOrderNo = preTatMatrixWorkFlowDetails.get().getOrderNo();
                        tatMatrixWorkFlowDetailsRepo.save(preTatMatrixWorkFlowDetails.get());
                    }
                    inActivateTatWorkflowMapping(map);
                    Optional<Matrix> matrixDetails = matrixRepository.findById(Long.valueOf(map.get("tat_id")));
                    if (matrixDetails.isPresent()) {
                        Optional<MatrixDetails> details = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();

                        if (details.isPresent()) {
                            TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
                                    new TatMatrixWorkFlowDetails(details.get().getOrderNo(), details.get().getLevel(), assignedToStaff.getId(),
                                            Long.valueOf(map.get("workFlowId")), matrixDetails.get().getId(),
                                            (assignedToStaff != null && assignedToStaff.getStaffUserparent() != null) ? assignedToStaff.getStaffUserparent().getId() : null, LocalDateTime.now(),
                                            details.get().getMtime(), details.get().getMunit(), details.get().getAction(), true, map.get("nextTatMappingId") != null ? Integer.valueOf(map.get("nextTatMappingId")) : null,
                                            entityId, map.get("eventName"), map.get("eventId") != null ? Integer.valueOf(map.get("eventId")) : null, CommonConstants.NOTIFICATION_TYPE_STAFF, null, true);

                                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);

                        } else {
                            logger.error("Active tat matrix details not found for id: " + matrixDetails.get().getId());
                        }
                    } else {
                        logger.error("Tat matrix not found for id: " + matrixDetails.get().getId());
                    }
                } else {
                    inActivateTatWorkflowMapping(map);

                }
            }
        } catch (Exception ex) {
            logger.error("error while saving tat matrix data: " + ex.getMessage());
        }
    }

    public void inActivateTatWorkflowMapping(Map<String, String> map) {
        if (map.containsKey("eventId") && map.containsKey("entityId")) {
            QTatMatrixWorkFlowDetails qTatMatrixWorkFlowDetails = QTatMatrixWorkFlowDetails.tatMatrixWorkFlowDetails;
            BooleanExpression expression = qTatMatrixWorkFlowDetails.isNotNull().and(qTatMatrixWorkFlowDetails.isActive.eq(true))
                    .and(qTatMatrixWorkFlowDetails.eventId.eq(Integer.valueOf(map.get("eventId"))))
                    .and(qTatMatrixWorkFlowDetails.entityId.eq(Integer.valueOf(map.get("entityId"))));
            List<TatMatrixWorkFlowDetails> details = (List<TatMatrixWorkFlowDetails>) tatMatrixWorkFlowDetailsRepo.findAll(expression);
            if (!CollectionUtils.isEmpty(details)) {
                details.forEach(tatMatrixWorkFlowDetails -> {
                    tatMatrixWorkFlowDetails.setIsActive(false);
                    tatMatrixWorkFlowDetails.setIsOverDueReminder(false);
                });
                tatMatrixWorkFlowDetailsRepo.saveAll(details);
            }
        }
    }

    public void assignToNextApprovalStaff(TatMatrixWorkFlowDetails details) {
        try {
//            System.out.println("------------TAT Notification and Reassign Started ------------------------");
            Customers customersCaf = null;
            PostpaidPlan plan = null;
            CreditDocument creditDocument = null;
            Map<String, String> map;
            StaffUser staffUser = staffUserRepository.findById(details.getStaffId()).get();
            switch (details.getEventName()) {
                case CommonConstants.WORKFLOW_EVENT_NAME.CAF:
                    customersCaf = customersRepository.findById(details.getEntityId()).get();//customersService.get(details.getEntityId());
                    if (staffUser.getStaffUserparent() != null && details.getCurrentTeamHeirarchyMappingId()!=null)  {
                        TicketTatAudits tatAudits = saveTatDetails(details.getEntityId(), details);
                        TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(details.getCurrentTeamHeirarchyMappingId()).get();//teamHierarchyMappingRepo.findTopByHierarchyIdAndAndTatId(details.getCurrentTeamHeirarchyMappingId(), details.getTatMatrixId().intValue());
                        List<StaffUser> staffUsers = staffUserService.getByTeamIdAndServiceAreaId(Long.valueOf(teamHierarchyMapping.getTeamId()),customersCaf.getServicearea().getId());
                        if (staffUsers.contains(staffUser.getStaffUserparent())) {
                            StaffUser userparent=staffUserRepository.findById(staffUser.getStaffUserparent().getId()).get();
                            if(userparent!=null){
                                details.setParentId(userparent.getId());
                            }
                            customersCaf.setCurrentAssigneeId(staffUser.getStaffUserparent().getId());
                            customersRepository.save(customersCaf);
                            Optional<Matrix> matrixDetails = matrixRepository.findById(details.getTatMatrixId());
                            if (matrixDetails.isPresent()) {
                                Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted() && dtl.getOrderNo().equals(details.getOrderNo() + 1)).findFirst();
                                if (details.getAction().equalsIgnoreCase("both")) {
                                    if (details.getParentId() != null) {
//                                        String team = teamsRepository.findTeamNameById(details.getTeamId());
//                                        if(customersCaf != null){
//                                            List<Teams> teamsList=staffUserRepository.findByUsername()
//                                            if(staffUser.getTeam() != null && !staffUser.getTeam().isEmpty()){
//                                                Set<Teams> teams = staffUser.getTeam();
//                                                team = new ArrayList<>(teams).get(0).getName().toString();
//                                            }
//                                        }

                                        Long buId = null;
                                        if(userparent.getBusinessUnitNameList() != null && !userparent.getBusinessUnitNameList().isEmpty()){
                                            buId = userparent.getBusinessUnitNameList().get(0).getId();
                                        }
                                        if(userparent.getMvnoId().equals(1) ||userparent.getMvnoId()==1){
                                            sendTatNotification(userparent.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), userparent.getPhone(), userparent.getEmail(), customersCaf.getMvnoId(), tatAudits,customersCaf.getBuId());
                                        }else{
                                            sendTatNotification(userparent.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), userparent.getPhone(), userparent.getEmail(), userparent.getMvnoId(), tatAudits,buId);
                                        }
                                        System.out.println("------------------------TAT Notifiaction sent for Action - Both--------------------------");
                                    }
                                }

                                if (newMatrixDetails.isPresent()) {
                                    Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));
                                    details.setMtime(newMatrixDetails.get().getMtime());
                                    details.setMunit(newMatrixDetails.get().getMunit());
                                    details.setOrderNo(newMatrixDetails.get().getOrderNo());
                                    details.setLevel(newMatrixDetails.get().getLevel());
                                    details.setParentId(userparent.getId());
                                    customersCaf = (Customers) UpdateDateTimefortat(newMatrixDetails.get(), customersCaf, Nextvalue);
//                                    details.setStaffId(staffUser.getId());
                                    details.setTeamId(teamHierarchyMapping.getTeamId().longValue());
                                    customersRepository.save(customersCaf);
                                    details.setStaffId(details.getParentId());
                                    hierarchyService.sendWorkflowAssignActionMessage("", userparent.getPhone(), userparent.getEmail(), customersCaf.getMvnoId(), userparent.getUsername(), "Assigned",userparent.getId().longValue());
                                } else {
                                    details.setIsActive(false);
                                    tatMatrixWorkFlowDetailsRepo.save(details);
                                }
                                tatMatrixWorkFlowDetailsRepo.save(details);
                            }
                            workflowAuditService.saveAudit(details.getEventId(), details.getEventName(), customersCaf.getId(), customersCaf.getUsername(), userparent.getId(), userparent.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Re-Assigned to :- " + userparent.getUsername());
//                                map = hierarchyService.getTeamForNextApproveForAuto(customersCaf.getMvnoId(), customersCaf.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, CommonConstants.HIERARCHY_TYPE, true, false, customerMapper.domainToDTO(customersCaf, new CycleAvoidingMappingContext()));
//                            if (staffUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
//                                saveOrUpdateDataForTatMatrix(map, staffUser.getStaffUserparent(), customersCaf.getId(), details.getId());
//                            }

//                            }
                        }
                    }
                    else{
                        details.setIsActive(false);
                        tatMatrixWorkFlowDetailsRepo.save(details);
                    }
                    break;
                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN:
                    plan = postpaidPlanRepo.findById(details.getEntityId()).get();
                    break;
                case CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT:
                    creditDocument = creditDocRepository.findById(details.getEntityId()).get();
                    break;
                case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION:
                    Customers terminationcust = null;
                    terminationcust = customersRepository.findById(details.getEntityId()).get();
                    CustomerApprove customerApprove = customersService.finCustmerApproveForTermination(terminationcust.getId());
                    if (staffUser.getStaffUserparent() != null && details.getCurrentTeamHeirarchyMappingId()!=null && customerApprove!=null) {
                        TicketTatAudits tatAudits = saveTatDetails(details.getEntityId(), details);
                        TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(details.getCurrentTeamHeirarchyMappingId()).get();//teamHierarchyMappingRepo.findTopByHierarchyIdAndAndTatId(details.getCurrentTeamHeirarchyMappingId(), details.getTatMatrixId().intValue());
                        List<StaffUser> staffUsers = staffUserService.getByTeamId(Long.valueOf(teamHierarchyMapping.getTeamId()));
                        if (staffUsers.contains(staffUser.getStaffUserparent())) {
                            StaffUser userparent=staffUserRepository.findById(staffUser.getStaffUserparent().getId()).get();
                            if(userparent!=null){
                                details.setParentId(userparent.getId());
                            }
                            terminationcust.setCurrentAssigneeId(staffUser.getStaffUserparent().getId());
                            customerApprove.setCurrentStaff(userparent.getUsername());
                            customerApproveRepo.save(customerApprove);
                            workflowAuditService.saveAudit(details.getEventId(), details.getEventName(), terminationcust.getId(), terminationcust.getUsername(), userparent.getId(), userparent.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Re-Assigned to :- " +userparent.getUsername());
//                            map = hierarchyService.getTeamForNextApproveForAuto(terminationcust.getMvnoId(), terminationcust.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, CommonConstants.HIERARCHY_TYPE, true, false, customerMapper.domainToDTO(terminationcust, new CycleAvoidingMappingContext()));
//                            if (staffUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
//                                saveOrUpdateDataForTatMatrix(map, staffUser.getStaffUserparent(), details.getEntityId(), details.getId());
//                            }

                            Optional<Matrix> matrixDetails = matrixRepository.findById(details.getTatMatrixId());
                            if (matrixDetails.isPresent()) {
                                Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted() && dtl.getOrderNo().equals(details.getOrderNo() + 1)).findFirst();
                                if (details.getAction().equalsIgnoreCase("both")) {
                                    if (details.getParentId() != null) {
//                                        String team = "";
//                                        if(customersCaf != null){
//                                            if(staffUser.getTeam() != null && !staffUser.getTeam().isEmpty()){
//                                                Set<Teams> teams = staffUser.getTeam();
//                                                team = new ArrayList<>(teams).get(0).getName().toString();
//                                            }
//                                        }

                                        Long buId = null;
                                        if(userparent.getBusinessUnitNameList() != null && !userparent.getBusinessUnitNameList().isEmpty()){
                                            buId = userparent.getBusinessUnitNameList().get(0).getId();
                                        }
                                        if(userparent.getMvnoId().equals(1) ||userparent.getMvnoId()==1){
                                            sendTatNotification(userparent.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), userparent.getPhone(), userparent.getEmail(), terminationcust.getMvnoId(), tatAudits,terminationcust.getBuId());
                                        }else {
                                            sendTatNotification(userparent.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), userparent.getPhone(), userparent.getEmail(), userparent.getMvnoId(), tatAudits, buId);
                                        }
                                        System.out.println("------------------------TAT Notifiaction sent for Action - Both--------------------------");
                                    }
                                }
                                if (newMatrixDetails.isPresent()) {
                                    Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));
                                    details.setMtime(newMatrixDetails.get().getMtime());
                                    details.setMunit(newMatrixDetails.get().getMunit());
                                    details.setOrderNo(newMatrixDetails.get().getOrderNo());
                                    details.setLevel(newMatrixDetails.get().getLevel());
//                                    details.setStaffId(staffUser.getId());
                                    details.setTeamId(teamHierarchyMapping.getTeamId().longValue());
                                    terminationcust = (Customers) UpdateDateTimefortat(newMatrixDetails.get(), terminationcust, Nextvalue);
                                    details.setParentId(userparent.getId());
                                    details.setStaffId(details.getParentId());
                                    customersRepository.save(terminationcust);
                                    //details.setStaffId(details.getParentId());
                                    hierarchyService.sendWorkflowAssignActionMessage("", userparent.getPhone(), userparent.getEmail(), terminationcust.getMvnoId(), userparent.getUsername(), "Assigned",userparent.getId().longValue());
                                } else {
                                    details.setIsActive(false);
                                    tatMatrixWorkFlowDetailsRepo.save(details);
                                }
                                tatMatrixWorkFlowDetailsRepo.save(details);
                            }
//                            map = hierarchyService.getTeamForNextApproveForAuto(terminationcust.getMvnoId(), terminationcust.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION, CommonConstants.HIERARCHY_TYPE, true, false, customerMapper.domainToDTO(terminationcust, new CycleAvoidingMappingContext()));
//                            if (staffUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
//                                saveOrUpdateDataForTatMatrix(map, staffUser.getStaffUserparent(), details.getEntityId(), details.getId());
//                            }
                        }
                    }
                    else{
                        details.setIsActive(false);
                        tatMatrixWorkFlowDetailsRepo.save(details);
                    }
                    break;
//                case CommonConstants.WORKFLOW_EVENT_NAME.CASE: {
//                    Case aCase = caseService.getRepository().findById(Long.valueOf(details.getEntityId())).orElse(null);
//                    if (staffUser.getStaffUserparent() != null) {
//                        aCase.setCurrentAssignee(staffUser.getStaffUserparent());
//                        caseRepository.save(aCase);
//                        //Audit for TAT
//                        TicketTatAudits tatAudits = saveTatDetails(details.getEntityId(), details);
//
//                        System.out.println("------------------------TAT Reassign done for Action - Both--------------------------");
//                        details.setStartDateTime(LocalDateTime.now());
//
//                        // send notification if action is "BOTH"
//                        if (details.getAction().equalsIgnoreCase("both")) {
//                            if (details.getParentId() != null) {
//                                StaffUser parentUser = staffUserRepository.findById(staffUser.getStaffUserparent().getId()).get();
//                                sendTatNotification(parentUser.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), parentUser.getPhone(), parentUser.getEmail(), parentUser.getMvnoId(), tatAudits,parentUser.getBusinessUnit().getId());
//
//                                System.out.println("------------------------TAT Notifiaction sent for Action - Both--------------------------");
//                            }
//                        }
//                        details.setParentId(null);
//                        //changing level to next level
//
//                        if (details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
//                            try {
//                                // Case caseEntity = caseRepository.findById(Long.valueOf(details.getEntityId())).orElse(null);
//                                if (details.getNotificationType() != null && !details.getNotificationType().equals(CommonConstants.NOTIFICATION_TYPE_TEAM)) {
//                                    if (aCase != null) {
//                                        TicketTatMatrix ticketTatMatrix = caseService.getTicketTatMatrixFromSubReasonId(caseService.getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                                        if (ticketTatMatrix != null) {
//                                            //Case case1;
//                                            Optional<TicketTatMatrixMapping> ticketTatMatrix1 = ticketTatMatrix.getTatMatrixMappings().stream().filter(p -> p.getOrderNo().equals(details.getOrderNo() + 1)).findFirst();
//                                            if (ticketTatMatrix1 != null && ticketTatMatrix1.isPresent()) {
//                                                Integer Nextvalue = Integer.parseInt(String.valueOf(ticketTatMatrix1.get().getMtime1()));
//                                                if (aCase.getPriority().equals("High")) {
//                                                    details.setMtime(String.valueOf(ticketTatMatrix1.get().getMtime1()));
//                                                    aCase = UpdateDateTime(ticketTatMatrix1.get(), aCase, Nextvalue);
//                                                } else if (aCase.getPriority().equals("Medium")) {
//                                                    details.setMtime(String.valueOf(ticketTatMatrix1.get().getMtime2()));
//                                                    aCase = UpdateDateTime(ticketTatMatrix1.get(), aCase, Nextvalue);
//                                                } else {
//                                                    details.setMtime(String.valueOf(ticketTatMatrix1.get().getMtime3()));
//                                                    aCase = UpdateDateTime(ticketTatMatrix1.get(), aCase, Nextvalue);
//                                                }
//                                                details.setMunit(ticketTatMatrix1.get().getMunit());
//
//                                                //Setting followup time for next level
//                                                if (ticketTatMatrix1.get().getMunit().equalsIgnoreCase("Min")) {
//                                                    aCase.setNextFollowupDate(LocalDateTime.now().toLocalDate());
//                                                    aCase.setNextFollowupTime(LocalTime.now().plusMinutes(Long.parseLong(details.getMtime())));
//                                                } else if (ticketTatMatrix1.get().getMunit().equalsIgnoreCase("Hours")) {
//                                                    aCase.setNextFollowupDate(LocalDateTime.now().toLocalDate());
//                                                    aCase.setNextFollowupTime(LocalTime.now().plusHours(Long.parseLong(details.getMtime())));
//
//                                                } else if (ticketTatMatrix1.get().getMunit().equalsIgnoreCase("Day")) {
//                                                    aCase.setNextFollowupDate(LocalDateTime.now().toLocalDate().plusDays(Long.parseLong(details.getMtime())));
//                                                    aCase.setNextFollowupTime(LocalTime.now());
//                                                }
////                                                caseRepository.save(case1);
//                                                caseRepository.save(aCase);
//                                                details.setOrderNo(ticketTatMatrix1.get().getOrderNo());
//                                                details.setLevel(ticketTatMatrix1.get().getLevel());
//                                                details.setAction(ticketTatMatrix1.get().getAction());
//                                                details.setStaffId(staffUser.getStaffUserparent().getId());
//                                                details.setAction(ticketTatMatrix1.get().getAction());
//                                                StaffUser newStaffuser = staffUserRepository.findById(details.getStaffId()).orElse(null);
//                                                details.setParentId(newStaffuser.getStaffUserparent().getId());
//                                            } else {
//                                                details.setIsActive(false);
//                                                tatMatrixWorkFlowDetailsRepo.save(details);
//                                            }
//                                        } else {
//                                            details.setIsActive(false);
//                                            tatMatrixWorkFlowDetailsRepo.save(details);
//                                        }
//                                    }
//                                } else {
//                                    details.setIsActive(false);
//                                    tatMatrixWorkFlowDetailsRepo.save(details);
//                                }
//
//                                tatMatrixWorkFlowDetailsRepo.save(details);
//
//                            } catch (Exception ex) {
//                                logger.error("Error during find caseDTO: " + ex.getMessage());
//                            }
//                        } else {
//                            Optional<Matrix> matrixDetails = matrixRepository.findById(details.getTatMatrixId());
//                            if (matrixDetails.isPresent()) {
//                                Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted() && dtl.getOrderNo().equals(details.getOrderNo() + 1)).findFirst();
//
//                                if (newMatrixDetails.isPresent()) {
//                                    details.setMtime(newMatrixDetails.get().getMtime());
//                                    details.setMunit(newMatrixDetails.get().getMunit());
//                                    details.setOrderNo(newMatrixDetails.get().getOrderNo());
//                                    details.setLevel(newMatrixDetails.get().getLevel());
//                                    //details.setStaffId(details.getParentId());
//                                } else {
//                                    details.setIsActive(false);
//                                    tatMatrixWorkFlowDetailsRepo.save(details);
//                                }
//                                tatMatrixWorkFlowDetailsRepo.save(details);
//                            }
//                        }
//                    } else {
//                        details.setIsActive(false);
//                        tatMatrixWorkFlowDetailsRepo.save(details);
//                    }
//                    break;
//                }
                case CommonConstants.WORKFLOW_EVENT_NAME.LEAD: {
                    LeadMaster leadMaster = leadMasterRepository.findById(Long.valueOf(details.getEntityId())).orElse(null);
                    if (leadMaster != null && details.getCurrentTeamHeirarchyMappingId()!=null) {
                        TicketTatAudits tatAudits = saveTatDetails(details.getEntityId(), details);
                        LeadMgmtWfDTO leadMgmtWfDTO = hierarchyService.leadMasterToLeadMgmtDTO(leadMaster);
                        TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(details.getCurrentTeamHeirarchyMappingId()).get();//teamHierarchyMappingRepo.findTopByHierarchyIdAndAndTatId(details.getCurrentTeamHeirarchyMappingId(), details.getTatMatrixId().intValue());
                        List<StaffUser> staffUsers = staffUserService.getByTeamId(Long.valueOf(teamHierarchyMapping.getTeamId()));
                        if (staffUsers.contains(staffUser.getStaffUserparent()) && details.getCurrentTeamHeirarchyMappingId()!=null) {
                            StaffUser userparent=staffUserRepository.findById(staffUser.getStaffUserparent().getId()).get();
                            if(userparent!=null){
                                details.setParentId(userparent.getId());
                            }
                            map = hierarchyService.getTeamForNextApproveForAuto(leadMaster.getMvnoId().intValue(), leadMaster.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD, CommonConstants.HIERARCHY_TYPE, true, false, new LeadMgmtWfDTO(leadMaster));
                            leadMaster.setNextApproveStaffId(staffUser.getStaffUserparent().getId());
                            leadMasterRepository.save(leadMaster);
//                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.LEAD, Math.toIntExact(leadMaster.getId()), leadMaster.getUsername(), userparent.getId(), userparent.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Picked By :- " + staffUser.getUsername());
//                            if (staffUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
//                                saveOrUpdateDataForTatMatrix(map, staffUser.getStaffUserparent(), details.getEntityId(), details.getId());
//                            }
                            Optional<Matrix> matrixDetails = matrixRepository.findById(details.getTatMatrixId());
                            if (matrixDetails.isPresent()) {
                                Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted() && dtl.getOrderNo().equals(details.getOrderNo() + 1)).findFirst();
                                if (details.getAction().equalsIgnoreCase("both")) {
                                    if (details.getParentId() != null) {
                                        String team = "";
                                        if(leadMaster != null){
                                            if(staffUser.getTeam() != null && !staffUser.getTeam().isEmpty()){
                                                Set<Teams> teams = staffUser.getTeam();
                                                team = new ArrayList<>(teams).get(0).getName().toString();
                                            }
                                        }

                                        Long buId = null;
                                        if(userparent.getBusinessUnitNameList() != null && !userparent.getBusinessUnitNameList().isEmpty()){
                                            buId = userparent.getBusinessUnitNameList().get(0).getId();
                                        }
                                        if(staffUser.getMvnoId().equals(1)|| staffUser.getMvnoId()==1) {
                                            sendTatNotification(userparent.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), userparent.getPhone(), userparent.getEmail(), leadMaster.getMvnoId().intValue(), tatAudits, leadMaster.getBuId());
                                        }else{
                                            sendTatNotification(userparent.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), userparent.getPhone(), userparent.getEmail(), userparent.getMvnoId(), tatAudits, buId);
                                        }
                                        System.out.println("------------------------TAT Notifiaction sent for Action - Both--------------------------");
                                    }
                                }
                                if (newMatrixDetails.isPresent()) {
                                    Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));
                                    details.setMtime(newMatrixDetails.get().getMtime());
                                    details.setMunit(newMatrixDetails.get().getMunit());
                                    details.setOrderNo(newMatrixDetails.get().getOrderNo());
                                    details.setLevel(newMatrixDetails.get().getLevel());
//                                    details.setStaffId(staffUser.getId());
                                    details.setTeamId(teamHierarchyMapping.getTeamId().longValue());
                                    details.setParentId(userparent.getId());
                                    leadMaster = (LeadMaster) UpdateDateTimefortat(newMatrixDetails.get(), leadMaster, Nextvalue);
                                    details.setStaffId(details.getParentId());
                                    leadMasterRepository.save(leadMaster);
                                    hierarchyService.sendWorkflowAssignActionMessage("", userparent.getPhone(), userparent.getEmail(), leadMaster.getMvnoId().intValue(), userparent.getUsername(), "Assigned",userparent.getId().longValue());
                                } else {
                                    details.setIsActive(false);
                                    tatMatrixWorkFlowDetailsRepo.save(details);
                                }
                                tatMatrixWorkFlowDetailsRepo.save(details);
                            }
                            workflowAuditService.saveAudit(details.getEventId(), details.getEventName(), leadMaster.getId().intValue(), leadMaster.getUsername(), userparent.getId(), userparent.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Re-Assigned to :- " + userparent.getUsername());
                            if(leadMaster.getNextfollowuptime()!=null){
                                leadMgmtWfDTO.setNextApproveStaffId(leadMaster.getNextApproveStaffId());
                                leadMgmtWfDTO.setNextfollowupdate(leadMaster.getNextfollowupdate().toString());
                                leadMgmtWfDTO.setNextfollowuptime(leadMaster.getNextfollowuptime().truncatedTo(ChronoUnit.SECONDS).toString());
                            }
                            SendLeadAssignMessage sendApproverForLeadMsg = new SendLeadAssignMessage(leadMgmtWfDTO);
                            kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendLeadAssignMessage.class.getSimpleName()));
//                            messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_LEAD_ASSIGN_MESSAGE);
//                            map = hierarchyService.getTeamForNextApproveForAuto(leadMgmtWfDTO.getMvnoId().intValue(), leadMgmtWfDTO.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD, CommonConstants.HIERARCHY_TYPE, true, false,leadMgmtWfDTO);
//                            if (staffUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
//                                saveOrUpdateDataForTatMatrix(map, staffUser.getStaffUserparent(), details.getEntityId(), details.getId());
//                            }
                        }
                        else{
                            details.setIsActive(false);
                            tatMatrixWorkFlowDetailsRepo.save(details);
                            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No customer found. ", null);
                        }
                    } else {
                        details.setIsActive(false);
                        tatMatrixWorkFlowDetailsRepo.save(details);
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "No customer found. ", null);
                    }
                    break;
                }
            }


//            if(!CollectionUtils.isEmpty(map) && map.containsKey("assignableStaff"))
                //hierarchyService.assignFromStaffList(map.get("assignableStaff").get(0).getId(), details.getEventName(), details.getEntityId(), true);
        } catch (Exception ex) {
            logger.error("Error while assign next staff by Tat schedualar: " + ex.getMessage());
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public void sendTatNotificationTypeTeam(String parentStaffPersonName, String teamName, String eventName, String assigndatetime, String mobileNumber, String emailId, Integer mvnoId, String ticketNumber, TicketTatAudits tatAudits, Long buId) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.TAT_SEND_PARENT_TO_TEAM);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {

                    TicketPickMessageToTeam ticketAssignMessege = new TicketPickMessageToTeam(mobileNumber, emailId, RabbitMqConstants.TAT_NO_RESPONSE_TAKEN, optionalTemplate.get(), parentStaffPersonName, assigndatetime, eventName, mvnoId, teamName, ticketNumber, tatAudits, buId);
                    Gson gson = new Gson();
                    gson.toJson(ticketAssignMessege);
                    kafkaMessageSender.send(new KafkaMessageData(ticketAssignMessege,TicketPickMessageToTeam.class.getSimpleName() ));
//                    messageSender.send(ticketAssignMessege, RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM);
                }
            } else {
//                 log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("TAT Template not available.");
            }


        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public TicketTatAudits saveTatDetails(Integer aCaseId, TatMatrixWorkFlowDetails details) {
        //Fetching the details of case
        TicketTatAudits tatAudits = new TicketTatAudits();
        Case ticketCase = new Case();
        Customers customers=new Customers();
        LeadMaster leadMaster=new LeadMaster();
        if(details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.CAF) ||details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION) ){
            customers= customersRepository.findById(aCaseId).orElse(null);
        if (!details.getAction().equalsIgnoreCase(CommonConstants.TICKET_ACTION.REASSIGN)) {
            if (customers != null) {
                tatAudits.setCaseId(customers.getId());
                tatAudits.setSlaTime(customers.getSlaTime());
                tatAudits.setSlaUnit(customers.getSlaUnit());
                tatAudits.setCaseStatus(customers.getStatus());
//                tatAudits.setEvent_name(details.getEventName());

                if (details != null) {
                    tatAudits.setTatTime(Integer.valueOf(details.getMtime()));
                    tatAudits.setTatUnit(details.getMunit());
                    tatAudits.setTatAction(details.getAction());
                    tatAudits.setTatStartTime(String.valueOf(details.getStartDateTime()));
                    tatAudits.setAssignStaffId(details.getStaffId());
                    tatAudits.setAssignStaffParentId(details.getParentId());
                    tatAudits.setCaseLevel(details.getLevel());
                    tatAudits.setIsTatBreached("Yes");
                    if (details.getNotificationType().equalsIgnoreCase("team")) {
                        tatAudits.setNotificationFor("Response Time Breach");
                    } else if (details.getNotificationType().equalsIgnoreCase("staff")) {
                        tatAudits.setNotificationFor("Tat Time Breach");

                    }

                }

            }
        } else {
            if (ticketCase != null) {
                tatAudits.setCaseId(customers.getId());
                tatAudits.setSlaTime(customers.getSlaTime());
                tatAudits.setSlaUnit(customers.getSlaUnit());
                tatAudits.setCaseStatus(customers.getStatus());
//                tatAudits.setEvent_name(details.getEventName());

                if (details != null) {
                    tatAudits.setTatTime(Integer.valueOf(details.getMtime()));
                    tatAudits.setTatUnit(details.getMunit());
                    tatAudits.setTatAction(details.getAction());
                    tatAudits.setTatStartTime(String.valueOf(details.getStartDateTime()));
                    tatAudits.setAssignStaffId(details.getStaffId());
                    tatAudits.setAssignStaffParentId(details.getParentId());
                    tatAudits.setCaseLevel(details.getLevel());
                    tatAudits.setIsTatBreached("Yes");
                    if (details.getNotificationType().equalsIgnoreCase("team")) {
                        tatAudits.setNotificationFor("Response Time Breach");
                    } else if (details.getNotificationType().equalsIgnoreCase("staff")) {
                        tatAudits.setNotificationFor("Tat Time Breach");

                    }

                }

            }
        }

        }
        else{
            leadMaster= leadMasterRepository.findById(aCaseId.longValue()).orElse(null);
            if (!details.getAction().equalsIgnoreCase(CommonConstants.TICKET_ACTION.REASSIGN)) {
                if (customers != null) {
                    tatAudits.setCaseId(leadMaster.getId().intValue());
                    tatAudits.setSlaTime(leadMaster.getSlaTime());
                    tatAudits.setSlaUnit(leadMaster.getSlaUnit());
                    tatAudits.setCaseStatus(leadMaster.getStatus());
//                    tatAudits.setEvent_name(details.getEventName());

                    if (details != null) {
                        tatAudits.setTatTime(Integer.valueOf(details.getMtime()));
                        tatAudits.setTatUnit(details.getMunit());
                        tatAudits.setTatAction(details.getAction());
                        tatAudits.setTatStartTime(String.valueOf(details.getStartDateTime()));
                        tatAudits.setAssignStaffId(details.getStaffId());
                        tatAudits.setAssignStaffParentId(details.getParentId());
                        tatAudits.setCaseLevel(details.getLevel());
                        tatAudits.setIsTatBreached("Yes");
                        if (details.getNotificationType().equalsIgnoreCase("team")) {
                            tatAudits.setNotificationFor("Response Time Breach");
                        } else if (details.getNotificationType().equalsIgnoreCase("staff")) {
                            tatAudits.setNotificationFor("Tat Time Breach");

                        }

                    }

                }
            } else {
                if (ticketCase != null) {
                    tatAudits.setCaseId(leadMaster.getId().intValue());
                    tatAudits.setSlaTime(leadMaster.getSlaTime());
                    tatAudits.setSlaUnit(leadMaster.getSlaUnit());
                    tatAudits.setCaseStatus(leadMaster.getStatus());
//                    tatAudits.setEvent_name(details.getEventName());

                    if (details != null) {
                        tatAudits.setTatTime(Integer.valueOf(details.getMtime()));
                        tatAudits.setTatUnit(details.getMunit());
                        tatAudits.setTatAction(details.getAction());
                        tatAudits.setTatStartTime(String.valueOf(details.getStartDateTime()));
                        tatAudits.setAssignStaffId(details.getStaffId());
                        tatAudits.setAssignStaffParentId(details.getParentId());
                        tatAudits.setCaseLevel(details.getLevel());
                        tatAudits.setIsTatBreached("Yes");
                        if (details.getNotificationType().equalsIgnoreCase("team")) {
                            tatAudits.setNotificationFor("Response Time Breach");
                        } else if (details.getNotificationType().equalsIgnoreCase("staff")) {
                            tatAudits.setNotificationFor("Tat Time Breach");

                        }

                    }

                }
            }
        }
        TicketTatAudits savedTatAudit = tatAuditRepository.save(tatAudits);

        return tatAudits;

    }

    public List<Long> getBUIdsFromCurrentStaff() {
        List<Long> mvnoIds = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }

    public void changeTatAssignee(Object enity, StaffUser assignedStaff, boolean isNotificationTypeTeam, Boolean isPickeddUp) {
        try {
            String eventname=null;
            Map<String, String> map = new HashMap<>();
            if (Objects.nonNull(enity) && enity.getClass().equals(Customers.class)) {
           if(((Customers) enity).getStatus().equalsIgnoreCase("Active")){
               eventname= CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION;
                }else{
               eventname= CommonConstants.WORKFLOW_EVENT_NAME.CAF;
           }
                Optional<TeamHierarchyMapping> teamHierarchyMapping = teamHierarchyMappingRepo.findById(((Customers) enity).getNextTeamHierarchyMapping().intValue());
                if (teamHierarchyMapping.isPresent()) {
                    map.put("workFlowId", teamHierarchyMapping.get().getHierarchyId().toString());
                    map.put("eventName", eventname);
                    map.put("eventId", ((Customers) enity).getId().toString());
                    map.put("entityId", ((Customers) enity).getId().toString());
                    map.put("teamId", teamHierarchyMapping.get().getTeamId().toString());
                    map.put("orderNo", teamHierarchyMapping.get().getOrderNumber().toString());
                    map.put("tat_id", teamHierarchyMapping.get().getTat_id().toString());
                    map.put("nextTatMappingId",((Customers) enity).getNextTeamHierarchyMapping().toString());
//                    map.put("nextTatMappingId",teamHierarchyMapping.get().getId().toString());
                    if (isPickeddUp)
                        map.put("fromPickedUp", "true");
                    else
                        map.put("fromPickedUp", "false");
                    saveOrUpdateDataForTatMatrix(map, assignedStaff, ((Customers) enity).getId(), null);
                    // caseService.updateFollowUpDateAndTimeForTicketAfterPickedUp(caseMapper.dtoToDomain(caseDTO,new CycleAvoidingMappingContext()));
                }
            }
//            if (Objects.nonNull(enity) && enity.getClass().equals(Customers.class)) {
//
//                Optional<TeamHierarchyMapping> teamHierarchyMapping = teamHierarchyMappingRepo.findById(((Customers) enity).getNextTeamHierarchyMapping().intValue());
//                if (teamHierarchyMapping.isPresent()) {
//                    map.put("workFlowId", teamHierarchyMapping.get().getHierarchyId().toString());
//                    map.put("eventName", eventname);
//                    map.put("eventId", ((Customers) enity).getId().toString());
//                    map.put("teamId", teamHierarchyMapping.get().getTeamId().toString());
//                    map.put("orderNo", teamHierarchyMapping.get().getOrderNumber().toString());
//                    map.put("tat_id", teamHierarchyMapping.get().getTat_id().toString());
////                    map.put("nextTatMappingId",teamHierarchyMapping.get().getId().toString());
//                    if (isPickeddUp)
//                        map.put("fromPickedUp", "true");
//                    else
//                        map.put("fromPickedUp", "false");
//                    saveOrUpdateDataForTatMatrix(map, assignedStaff, ((Customers) enity).getId(), null);
//                    // caseService.updateFollowUpDateAndTimeForTicketAfterPickedUp(caseMapper.dtoToDomain(caseDTO,new CycleAvoidingMappingContext()));
//                }
//            }
            else if (Objects.nonNull(enity) && enity.getClass().equals(LeadMaster.class)) {
                Optional<TeamHierarchyMapping> teamHierarchyMapping = teamHierarchyMappingRepo.findById(((LeadMaster) enity).getNextTeamMappingId().intValue());
                if (teamHierarchyMapping.isPresent()) {
                    map.put("workFlowId", teamHierarchyMapping.get().getHierarchyId().toString());
                    map.put("eventName", CommonConstants.WORKFLOW_EVENT_NAME.LEAD);
                    map.put("eventId", ((LeadMaster) enity).getId().toString());
                    map.put("nextTatMappingId",teamHierarchyMapping.get().getId().toString());
                    map.put("teamId", teamHierarchyMapping.get().getTeamId().toString());
                    map.put("orderNo", teamHierarchyMapping.get().getOrderNumber().toString());
                    map.put("tat_id", teamHierarchyMapping.get().getTat_id().toString());
                    map.put("entityId", ((LeadMaster) enity).getId().toString());
                    if (isPickeddUp)
                        map.put("fromPickedUp", "true");
                    else
                        map.put("fromPickedUp", "false");
                    saveOrUpdateDataForTatMatrix(map, assignedStaff, ((LeadMaster) enity).getId().intValue(), null);
                    // caseService.updateFollowUpDateAndTimeForTicketAfterPickedUp(caseMapper.dtoToDomain(

                }
            } else {
                logger.error("Exception at change assignee on ticket tat: ");
            }

        } catch (Exception ex) {
            logger.error("Exception at change assignee on ticket tat: " + ex.getMessage());
        }
    }

}
