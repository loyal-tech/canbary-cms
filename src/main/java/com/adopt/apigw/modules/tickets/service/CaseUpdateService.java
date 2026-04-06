package com.adopt.apigw.modules.tickets.service;
//
//import com.adopt.apigw.constants.CaseConstants;
//import com.adopt.apigw.constants.ClientServiceConstant;
//import com.adopt.apigw.core.dto.GenericDataDTO;
//import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
//import com.adopt.apigw.core.service.ExBaseAbstractService;
//import com.adopt.apigw.core.utillity.fileUtillity.FileUtility;
//import com.adopt.apigw.core.utillity.log.ApplicationLogger;
//import com.adopt.apigw.exception.CustomValidationException;
//import com.adopt.apigw.model.common.Customers;
//import com.adopt.apigw.model.common.StaffUser;
//import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
//import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;
//import com.adopt.apigw.modules.Communication.Helper.CommunicationHelper;
//import com.adopt.apigw.modules.Matrix.domain.TatMatrixWorkFlowDetails;
//import com.adopt.apigw.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
//import com.adopt.apigw.modules.ResolutionReasons.service.ResolutionReasonsService;
//import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
//import com.adopt.apigw.modules.Teams.domain.Teams;
//import com.adopt.apigw.modules.Teams.repository.TeamsRepository;
//import com.adopt.apigw.modules.Template.domain.TemplateNotification;
//import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
//import com.adopt.apigw.modules.TicketFollowUp.Model.TicketFollowUpDTO;
//import com.adopt.apigw.modules.TicketFollowUp.Service.TicketFollowUpService;
//import com.adopt.apigw.modules.TicketTatMatrix.Domain.TicketTatMatrix;
//import com.adopt.apigw.modules.TicketTatMatrix.Repository.TicketTatMatrixRepository;
//import com.adopt.apigw.modules.tickets.domain.*;
//import com.adopt.apigw.modules.tickets.mapper.CaseMapper;
//import com.adopt.apigw.modules.tickets.mapper.CaseUpdateDetailsMapper;
//import com.adopt.apigw.modules.tickets.mapper.CaseUpdateMapper;
//import com.adopt.apigw.modules.tickets.model.*;
//import com.adopt.apigw.modules.tickets.repository.*;
//import com.adopt.apigw.pojo.TimeUnitWithTotal;
//import com.adopt.apigw.rabbitMq.MessageSender;
//import com.adopt.apigw.rabbitMq.RabbitMqConstants;
//import com.adopt.apigw.rabbitMq.message.*;
//import com.adopt.apigw.service.common.ClientServiceSrv;
//import com.adopt.apigw.service.common.CustomersService;
//import com.adopt.apigw.service.common.StaffUserService;
//import com.adopt.apigw.spring.LoggedInUser;
//import com.adopt.apigw.utils.APIConstants;
//import com.adopt.apigw.utils.CommonConstants;
//import com.adopt.apigw.utils.TatUtils;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.google.gson.Gson;
//import com.querydsl.core.types.dsl.BooleanExpression;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.transaction.Transactional;
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.*;
//import java.util.Map.Entry;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//@Service
public class CaseUpdateService  {
//
//    public CaseUpdateService(CaseUpdateRepository repository, CaseUpdateMapper mapper) {
//        super(repository, mapper);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return " [CaseUpdateService()] ";
//    }
//
//    @Autowired
//    private CaseService caseService;
//    @Autowired
//    private StaffUserService staffUserService;
//    @Autowired
//    private CaseAssignmentService assignmentService;
//    @Autowired
//    private CustomersService customersService;
//
//    @Autowired
//    private ClientServiceSrv clientServiceSrv;
//
//    @Autowired
//    private TeamsRepository teamsRepository;
//
//    @Autowired
//    private NotificationTemplateRepository templateRepository;
//
//    @Autowired
//    private MessageSender messageSender;
//    @Autowired
//    private CaseUpdateRepository caseUpdateRepository;
//    @Autowired
//    private CaseUpdateDetailsService caseUpdateDetailsService;
//    @Autowired
//    private CaseUpdateDetailsMapper mapper;
//    @Autowired
//    CaseUpdateDetailsMapper caseUpdateDetailsMapper;
//    @Autowired
//    CaseUpdateMapper caseUpdateMapper;
//    private String PATH;
//
//    //Whatsapp no configuration
//    private String WHATSAPPNO1;
//    private String WHATSAPPNO2;
//    private String CLIENTCONTACTNO;
//    @Autowired
//    private FileUtility fileUtility;
//
//    @Autowired
//    TicketReasonCategoryService ticketReasonCategoryService;
//    @Autowired
//    TicketReasonSubCategoryService ticketReasonSubCategoryService;
//
//    @Autowired
//    ResolutionReasonsService resolutionReasonsService;
//
//    @Autowired
//    CaseRepository caseRepository;
//
//    @Autowired
//    CaseMapper caseMapper;
//
//    @Autowired
//    GroupReasonMappingRepository groupReasonMappingRepository;
//
//    @Autowired
//    private TatUtils tatUtils;
//
//    @Autowired
//    CaseUpdateDetailsRepository caseUpdateDetailsRepository;
//
//    @Autowired
//    private CaseDocDetailsService caseDocDetailsService;
//
//    @Autowired
//    private TatMatrixWorkFlowDetailsRepo tatMatrixWorkFlowDetailsRepo;
//
//    @Autowired
//    TicketFollowUpService ticketFollowUpService;
//
//    @Autowired
//    RootCauseReasonRepository rootCauseReasonRepository;
//    @Autowired
//    TicketTatMatrixRepository ticketTatMatrixRepository;
//    private static final Logger logger = LoggerFactory.getLogger(CaseUpdateService.class);
//
//    @Transactional
////    public CaseDTO updateEntity(CaseUpdateDTO entity, List<MultipartFile> file, Boolean isPickedUp) {
////        String SUBMODULE = getModuleNameForLog() + " [updateEntity()] ";
////        try {
////            CaseDTO dbObj = caseService.getEntityForUpdateAndDelete(entity.getTicketId());
////            String prePriority = dbObj.getPriority();
////            Customers customers = customersService.get(dbObj.getCustomersId());
////            String Email = null;
////            StaffUser staffUser = null;
////            if (customers != null) {
////                Email = customers.getEmail();
////            }
////
////            if (null != dbObj) {
////                Map<String, Map<String, String>> mainMap = new HashMap<>();
////
////                //Status
////                if (null != dbObj.getCaseStatus() && null != entity.getStatus() && !dbObj.getCaseStatus().equalsIgnoreCase(entity.getStatus())) {
////                    Map<String, String> valueMap = new HashMap<>();
////
////                    //Status = Closed
////                    if (entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_CLOSED)) {
////                        dbObj.setFinalClosedById(getLoggedInUserId());
////
////                        dbObj.setFinalClosedDate(LocalDateTime.now());
////
////                        CommunicationHelper communicationHelper = new CommunicationHelper();
////                        Map<String, String> map = new HashMap<>();
////                        map.put(CommunicationConstant.USERNAME, dbObj.getUserName());
////                        map.put(CommunicationConstant.COMPLAIN_NO, dbObj.getCaseNumber());
////                        map.put(CommunicationConstant.DESTINATION, dbObj.getMobile());
////                        map.put(CommunicationConstant.EMAIL, Email);
////                        CLIENTCONTACTNO = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CLIENT_CONTACT_NO).get(0).getValue();
////                        map.put(CommunicationConstant.CONTACT_NO, CLIENTCONTACTNO);
////                        WHATSAPPNO1 = clientServiceSrv.getClientSrvByName(ClientServiceConstant.WHATSAPPNO1).get(0).getValue();
////                        WHATSAPPNO2 = clientServiceSrv.getClientSrvByName(ClientServiceConstant.WHATSAPPNO2).get(0).getValue();
////                        map.put(CommunicationConstant.WHATSAPP_NO_1, WHATSAPPNO1);
////                        map.put(CommunicationConstant.WHATSAPP_NO_2, WHATSAPPNO2);
////                        communicationHelper.generateCommunicationDetails(CommunicationConstant.TICKET_CLOSED, Collections.singletonList(map));
////
////                        //bulk operation for linked ticket
////                        bulkOperationPerform(entity);
////                    }
////
////                    //Status = FollowUp
////                    if (entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)) {
////                        if (entity.getNextFollowupDate() != null && entity.getNextFollowupTime() != null) {
////                            Case case1=caseRepository.findById(entity.getTicketId()).get();
////                            CaseUpdateDTO caseUpdateDTO = updateTatAtStatusChangeToFollowUps(entity);
////                            dbObj.setNextFollowupDate(caseUpdateDTO.getNextFollowupDate());
////                            dbObj.setNextFollowupTime(caseUpdateDTO.getNextFollowupTime());
////                        }
////                    }
////                    //Status = Resolved
////                    if (entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_RESOLVED)) {
////                        dbObj.setFinalResolutionId(entity.getFinalResolutionId().intValue());
////                        dbObj.setFinalResolutionDate(LocalDateTime.now());
////                        dbObj.setFinalResolvedById(getLoggedInUserId());
////
////                        CommunicationHelper communicationHelper1 = new CommunicationHelper();
////                        Map<String, String> map1 = new HashMap<>();
////                        map1.put(CommunicationConstant.USERNAME, dbObj.getUserName());
////                        map1.put(CommunicationConstant.COMPLAIN_NO, dbObj.getCaseNumber());
////                        map1.put(CommunicationConstant.DESTINATION, dbObj.getMobile());
////                        map1.put(CommunicationConstant.EMAIL, Email);
////                        communicationHelper1.generateCommunicationDetails(CommunicationConstant.COMPLAIN_RESOLUTION, Collections.singletonList(map1));
////
////                        // bulk operation perform on resolve status
////                        //bulkOperationPerform(entity);
////
////                    }
////
////                    if (dbObj.getCaseStatus().equals(CaseConstants.STATUS_IN_PROGRESS)){
////                        if(entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_REOPEN)){
////                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Ticket can only Reopen if it is Resolved.", null);
////                        }
////                        if(entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_CLOSED)){
////                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can't close the ticket as Ticket is In-Progress.", null);
////                        }
////                    }
////
////                    //On hold status update
////                    if ((entity.getStatus().equalsIgnoreCase("On hold") || entity.getStatus().equalsIgnoreCase("pending") || entity.getStatus().equalsIgnoreCase("Out of domain")) &&
////                            !(dbObj.getCaseStatus().equalsIgnoreCase("Pending") || dbObj.getCaseStatus().equalsIgnoreCase("On hold") || dbObj.getCaseStatus().equalsIgnoreCase("Out of Domain")))
////                    {
////                        setOnHoldStartTime(entity);
////                    } else if (!(entity.getStatus().equalsIgnoreCase("Pending") || entity.getStatus().equalsIgnoreCase("On hold") || entity.getStatus().equalsIgnoreCase("Out Of Domain")) &&
////                            (dbObj.getCaseStatus().equalsIgnoreCase("On hold") || dbObj.getCaseStatus().equalsIgnoreCase("pending") || dbObj.getCaseStatus().equalsIgnoreCase("Out of domain"))) {
////
////                        if (dbObj.getCaseSlaTime() != null && dbObj.getCaseSlaUnit() != null) {
////                            entity.setCaseSlaTime(dbObj.getCaseSlaTime());
////                            entity.setCaseSlaUnit(dbObj.getCaseSlaUnit());
////                            entity.setPriority(dbObj.getPriority());
////                        }
////                        if(entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)){
////                            removeStartStopTimeWhenFolloupSet(entity);
////                        }else{
////                            CaseUpdateDTO caseUpdateDTOs = setOnHoldEndTime(entity);
////                            dbObj.setNextFollowupDate(caseUpdateDTOs.getNextFollowupDate());
////                            dbObj.setNextFollowupTime(caseUpdateDTOs.getNextFollowupTime());
////                            dbObj.setCaseSlaTime(caseUpdateDTOs.getCaseSlaTime());
////                            dbObj.setCaseSlaUnit(caseUpdateDTOs.getCaseSlaUnit());
////
////                        }
////
////                    }
////                    if(dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP) && !entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)){
////                        CaseUpdateDTO caseUpdateDTO = restartTATbeforeFollowupEndAndStatusChage(entity);
////                        dbObj.setNextFollowupDate(entity.getNextFollowupDate());
////                        dbObj.setNextFollowupTime(entity.getNextFollowupTime());
////                    }
////
////                    if (entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_REOPEN)){
////                        if(!dbObj.getCaseStatus().equals(CaseConstants.STATUS_RESOLVED)){
////                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Ticket can only Reopen if it is Resolved.", null);
////                        }
////                    }
////
////                    valueMap.put(CaseConstants.OLD_VALUE, dbObj.getCaseStatus());
////                    valueMap.put(CaseConstants.NEW_VALUE, entity.getStatus());
////                    mainMap.put(CaseConstants.STATUS, valueMap);
////
//////                    if(dbObj.getCaseStatus().equalsIgnoreCase("pending") && !entity.getStatus().equalsIgnoreCase("pending")||dbObj.getCaseStatus().equalsIgnoreCase("On Hold") && !entity.getStatus().equalsIgnoreCase("On Hold")){
//////                        restartTat(dbObj,entity);
//////                    }
////                    dbObj.setCaseStatus(entity.getStatus());
////                    if(entity.getIs_closed()!=null){
////                        if(entity.getIs_closed()==true){
////                            dbObj.setCaseStatus(CaseConstants.STATUS_CLOSED);
////                        }
////                    }
////                   /* called method for closed ticket notification*/
////                    if(dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_CLOSED)){
////                        Long buId = null;
////                        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
////                            buId =  getBUIdsFromCurrentStaff().get(0);
////                        }
////                        sendCustTicketCloseMessage(dbObj.getUserName(), dbObj.getMobile(), dbObj.getEmail(), dbObj.getCaseStatus(), dbObj.getMvnoId(), dbObj.getCaseNumber(),buId);
////
////                    }
////
////                }
////
////                if(entity.getCaseSlaUnit()!=null && entity.getCaseSlaTime()!=null && entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_OPEN)){
////                    dbObj.setCaseSlaTime(entity.getCaseSlaTime());
////                    dbObj.setCaseSlaUnit(entity.getCaseSlaUnit());
////                }
////
////                //Assignee
////                if (null != entity.getAssignee() && !entity.getAssignee().equals(dbObj.getCurrentAssigneeId())) {
////                    Map<String, String> valueMap = new HashMap<>();
////
////                    StaffUser newAssignee = staffUserService.get(entity.getAssignee());
////                    if (null != dbObj.getCurrentAssigneeId()) {
////                        StaffUser oldAssignee = staffUserService.get(dbObj.getCurrentAssigneeId());
////                        valueMap.put(CaseConstants.OLD_VALUE, oldAssignee.getFirstname() + " " + oldAssignee.getLastname());
////                    } else {
////                        valueMap.put(CaseConstants.OLD_VALUE, "-");
////                    }
////
////                    if (newAssignee != null) {
////                        valueMap.put(CaseConstants.NEW_VALUE, newAssignee.getFirstname() + " " + newAssignee.getLastname());
////                        dbObj.setCurrentAssigneeId(newAssignee.getId());
////                        assignmentService.saveEntity(new CaseAssignmentDTO(entity.getTicketId(), newAssignee.getId(), LocalDate.now()));
////                        if(dbObj.getTeamHierarchyMappingId() != null) {
////                            if(!isPickedUp)
////                                tatUtils.changeTicketTatAssignee(dbObj, newAssignee, false, false);
////                            else
////                                tatUtils.changeTicketTatAssignee(dbObj, newAssignee, true, true);
////                        }
////                    }
////                    mainMap.put(CaseConstants.ASSIGNEE, valueMap);
////
////                }
////
////                //Priority
////                if (null != dbObj.getPriority() && null != entity.getPriority() && !entity.getPriority().isEmpty() && !dbObj.getPriority().equalsIgnoreCase(entity.getPriority())) {
////                    Map<String, String> valueMap = new HashMap<>();
////                    valueMap.put(CaseConstants.OLD_VALUE, dbObj.getPriority());
////                    valueMap.put(CaseConstants.NEW_VALUE, entity.getPriority());
////                    mainMap.put(CaseConstants.PRIORITY, valueMap);
////                    TicketTatMatrix ticketTatMatrix=ticketTatMatrixRepository.findById(dbObj.getTatMappingId()).orElse(null);
////                    dbObj.setPriority(entity.getPriority());
////                    if(dbObj.getPriority().equalsIgnoreCase("High")){
////                        dbObj.setCaseSlaTime(Math.toIntExact(ticketTatMatrix.getSlaTimep1()));
////                        dbObj.setCaseSlaUnit(ticketTatMatrix.getSunitp1());
////                    } else if (dbObj.getPriority().equalsIgnoreCase("Medium")) {
////                        dbObj.setCaseSlaTime(Math.toIntExact(ticketTatMatrix.getSlaTimep2()));
////                        dbObj.setCaseSlaUnit(ticketTatMatrix.getSunitp2());
////                    }else{
////                        dbObj.setCaseSlaTime(Math.toIntExact(ticketTatMatrix.getSlaTime3()));
////                        dbObj.setCaseSlaUnit(ticketTatMatrix.getSunitp3());
////                    }
////
////                }
////
////                //Source
////                if (null != entity.getSource() && !entity.getSource().isEmpty()) {
////                    dbObj.setSource(entity.getSource());
////                }
////
////                //Sub-Source
////                if (null != entity.getSubSource() && !entity.getSubSource().isEmpty()) {
////                    dbObj.setSubSource(entity.getSubSource());
////                }
////
////                //Remark
////                if (null != entity.getRemark() && null != entity.getRemarkType() && !entity.getRemark().isEmpty()) {
////
////
////                    Map<String, String> remarkMap = new HashMap<>();
////                    remarkMap.put(CaseConstants.NEW_VALUE, entity.getRemark());
////                    remarkMap.put(CaseConstants.REMARK_TYPE, entity.getRemarkType());
////                    remarkMap.put(CaseConstants.UNIQUE_FILENAME, entity.getAttachment());
////                    remarkMap.put(CaseConstants.FILENAME, entity.getFilename());
////                    mainMap.put(CaseConstants.REMARK, remarkMap);
////                }
////
////                if (null != entity && file != null && file.size() > 0) {
////                    Case aCase = caseRepository.findById(dbObj.getCaseId()).orElse(null);
////                    if (aCase != null) {
////                        for (MultipartFile multipartFile : file) {
////                            CaseDocDetailsDTO caseDoc = new CaseDocDetailsDTO();
////                            PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.TICKET_PATH).get(0).getValue();
//////                            String subFolderName = aCase.getCaseNumber().trim().replace("-", "_") + "/";
////                            String path = PATH;
////                            caseDoc.setTicketId(Math.toIntExact(dbObj.getCaseId()));
////                            caseDoc.setDocStatus("Active");
////                            MultipartFile file1 = fileUtility.getFileFromArrayForTicket(multipartFile);
////                            if (file1 != null) {
////                                caseDoc.setUniquename(fileUtility.saveFileToServerForTicket(file1, path));
////                                caseDoc.setFilename(caseDoc.getUniquename());
////                                caseDoc = caseDocDetailsService.saveEntity(caseDoc);
////                            }
////                        }
////                    }
////
////
////                }
////
//////                On reason category change
////                if (null != entity.getTicketReasonCategoryId() && !dbObj.getTicketReasonCategoryId().equals(entity.getTicketReasonCategoryId())) {
////                    Map<String, String> reasonCategoryMap = new HashMap<>();
////                    reasonCategoryMap.put(CaseConstants.NEW_VALUE, ticketReasonCategoryService.getEntityById(entity.getTicketReasonCategoryId()).getCategoryName());
////                    reasonCategoryMap.put(CaseConstants.OLD_VALUE, ticketReasonCategoryService.getEntityById(dbObj.getTicketReasonCategoryId()).getCategoryName());
////                    dbObj.setTicketReasonCategoryId(entity.getTicketReasonCategoryId());
////                    mainMap.put(CaseConstants.REASON_CATEGORY, reasonCategoryMap);
////                    //adding notification when change ticket problem domain
////                    StaffUser stffuser =  staffUserService.getRepository().findById(dbObj.getCurrentAssigneeId()).get();
////                    sendProblemDomainChangeMsg(reasonCategoryMap.get(CaseConstants.NEW_VALUE),dbObj.getCaseNumber(),reasonCategoryMap.get(CaseConstants.OLD_VALUE),stffuser.getFirstname(),stffuser.getPhone(),stffuser.getEmail(),stffuser.getMvnoId());
////                }
////
////                //  On reason sub category change
////                if (null != entity.getReasonSubCategoryId() && dbObj.getReasonSubCategoryId() != null && !dbObj.getReasonSubCategoryId().equals(entity.getReasonSubCategoryId())) {
////                    Map<String, String> reasonCategoryMap = new HashMap<>();
////                    reasonCategoryMap.put(CaseConstants.NEW_VALUE, ticketReasonSubCategoryService.getEntityById(entity.getReasonSubCategoryId()).getSubCategoryName());
////                    reasonCategoryMap.put(CaseConstants.OLD_VALUE, ticketReasonSubCategoryService.getEntityById(dbObj.getReasonSubCategoryId()).getSubCategoryName());
////                    mainMap.put(CaseConstants.REASON_SUB_CATEGORY, reasonCategoryMap);
////                    dbObj.setReasonSubCategoryId(entity.getReasonSubCategoryId());
////                    //update TAT matrix for existing tickets
////
////                    Case updatedCase = tatUtils.updateticketTatMatrix(dbObj);
////                    dbObj.setNextFollowupTime(updatedCase.getNextFollowupTime());
////                    dbObj.setNextFollowupDate(updatedCase.getNextFollowupDate());
////
////
////                }
////
//////                On reason change
////                if (null != entity.getGroupReasonId() && dbObj.getGroupReasonId() != null && !dbObj.getGroupReasonId().equals(entity.getGroupReasonId())) {
////                    Map<String, String> reasonMap = new HashMap<>();
////                    String newValue = ticketReasonSubCategoryService.getEntityById(entity.getReasonSubCategoryId()).getTicketSubCategoryGroupReasonMappingList().stream().filter(t -> t.getId().equals(entity.getGroupReasonId())).findAny().get().getReason();
////                    Optional<TicketSubCategoryGroupReasonMapping> oldReason = groupReasonMappingRepository.findById(dbObj.getGroupReasonId());
////                    String oldValue = "-";
////                    if (oldReason.isPresent()) {
////                        oldValue = oldReason.get().getReason();
////                    }
////                    reasonMap.put(CaseConstants.NEW_VALUE, newValue);
////                    reasonMap.put(CaseConstants.OLD_VALUE, oldValue);
////                    mainMap.put(CaseConstants.REASON, reasonMap);
////                    dbObj.setGroupReasonId(entity.getGroupReasonId());
////                }
////
////
//////                Type change
////                if (null != entity.getCaseType() && !entity.getCaseType().isEmpty() && !dbObj.getCaseType().equals(entity.getCaseType())) {
////                    Map<String, String> typeMap = new HashMap<>();
////                    typeMap.put(CaseConstants.NEW_VALUE, entity.getCaseType());
////                    typeMap.put(CaseConstants.OLD_VALUE, dbObj.getCaseType());
////                    mainMap.put(CaseConstants.CASE_TYPE, typeMap);
////                    dbObj.setCaseType(entity.getCaseType());
////                }
////
//////                Resolution change
////                if (null != entity.getFinalResolutionId()) {
////                    Map<String, String> typeMap = new HashMap<>();
////                    if (Objects.isNull(dbObj.getFinalResolutionId())) {
////                        typeMap.put(CaseConstants.OLD_VALUE, "-");
////                        typeMap.put(CaseConstants.NEW_VALUE, resolutionReasonsService.getEntityById(entity.getFinalResolutionId().longValue()).getName());
////                        mainMap.put(CaseConstants.RESOLUTION_REASON, typeMap);
////                        dbObj.setFinalResolutionId(entity.getFinalResolutionId());
////                    } else if (!dbObj.getFinalResolutionId().equals(entity.getFinalResolutionId())) {
////                        typeMap.put(CaseConstants.OLD_VALUE, resolutionReasonsService.getEntityById(dbObj.getFinalResolutionId().longValue()).getName());
////                        typeMap.put(CaseConstants.NEW_VALUE, resolutionReasonsService.getEntityById(entity.getFinalResolutionId().longValue()).getName());
////                        mainMap.put(CaseConstants.RESOLUTION_REASON, typeMap);
////                        dbObj.setFinalResolutionId(entity.getFinalResolutionId());
////                    }
////
////
////                }
////
//////               TAT mapping changed
////                if (null != entity.getTatMappingId() && !dbObj.getTatMappingId().equals(entity.getTatMappingId())) {
////                    dbObj.setTatMappingId(entity.getTatMappingId());
////                }
////
//////                Title changes
////
////                if (null != entity.getCaseTitle() && !dbObj.getCaseTitle().equals(entity.getCaseTitle())) {
////                    Map<String, String> typeMap = new HashMap<>();
////                    typeMap.put(CaseConstants.NEW_VALUE, entity.getCaseTitle());
////                    typeMap.put(CaseConstants.OLD_VALUE, dbObj.getCaseTitle());
////                    mainMap.put(CaseConstants.CASE_TITLE, typeMap);
////                    dbObj.setCaseTitle(entity.getCaseTitle());
//////                        dbObj.setTatMappingId(entity.getTatMappingId());
////                }
////
////                // Root cause changed
////
////                if (null != entity.getRootCauseReasonId()) {
////                    Map<String, String> typeMap = new HashMap<>();
////                    if (Objects.isNull(dbObj.getRootCauseReasonId())) {
////                        typeMap.put(CaseConstants.OLD_VALUE, "-");
////                        typeMap.put(CaseConstants.NEW_VALUE, rootCauseReasonRepository.findById(entity.getRootCauseReasonId()).get().getRootCauseReason());
////                        mainMap.put(CaseConstants.CASE_ROOT_CAUSE, typeMap);
////                        dbObj.setRootCauseReasonId(entity.getRootCauseReasonId());
////                    } else if (!dbObj.getRootCauseReasonId().equals(entity.getRootCauseReasonId())) {
////                        typeMap.put(CaseConstants.OLD_VALUE, rootCauseReasonRepository.findById(dbObj.getRootCauseReasonId()).get().getRootCauseReason());
////                        typeMap.put(CaseConstants.NEW_VALUE, rootCauseReasonRepository.findById(entity.getRootCauseReasonId()).get().getRootCauseReason());
////                        mainMap.put(CaseConstants.CASE_ROOT_CAUSE, typeMap);
////                        dbObj.setRootCauseReasonId(entity.getRootCauseReasonId());
////                    }
////
////                }
////
////                if (entity.getSource() != null) {
////                    dbObj.setSource(entity.getSource());
////                }
////                if (entity.getSubSource() != null) {
////                    dbObj.setSubSource(entity.getSubSource());
////                }
////                if (entity.getCustomerAdditionalEmail() != null) {
////                    dbObj.setCustomerAdditionalEmail(entity.getCustomerAdditionalEmail());
////                }
////                if (entity.getSubSource() != null) {
////                    dbObj.setCustomerAdditionalMobileNumber(entity.getCustomerAdditionalMobileNumber());
////                }
////
////                // Update Helper Name
////                if (entity.getHelperName() != null) {
////                    dbObj.setHelperName(entity.getHelperName());
////                }
////
//////              All the changes for ticket should me contain in this map and it will generate update details according to that .
////                if (null != mainMap && mainMap.size() > 0) {
////                    List<CaseUpdateDetailsDTO> detailsList = new ArrayList<>();
////                    for (Map.Entry<String, Map<String, String>> map : mainMap.entrySet()) {
//////                        CaseUpdateDetailsDTO statusDetails = new CaseUpdateDetailsDTO();
//////                        switch (map.getKey()) {
//////                            case CaseConstants.STATUS:
//////                                statusDetails.setEntitytype(CaseConstants.ENTITY_STATUS);
//////                                statusDetails.setOperation(CaseConstants.OPERATION_CHANGE_STATUS);
//////                                break;
//////                            case CaseConstants.REASON:
//////                                statusDetails.setEntitytype(CaseConstants.ENTITY_STATUS);
//////                                statusDetails.setOperation(CaseConstants.OPERATION_CHANGE_STATUS);
//////                                break;
//////                        }
////                        if (map.getKey().equalsIgnoreCase(CaseConstants.STATUS)) {
////                            CaseUpdateDetailsDTO statusDetails = new CaseUpdateDetailsDTO();
////                            statusDetails.setEntitytype(CaseConstants.ENTITY_STATUS);
////                            statusDetails.setOperation(CaseConstants.OPERATION_CHANGE_STATUS);
////                            statusDetails.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
////                            statusDetails.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
////                            statusDetails.setCaseUpdate(entity);
//////                            statusDetails.setResolutionId(entity.getResolutionId().longValue());
////                            detailsList.add(statusDetails);
////                            sendCustomerTicketStatusChangeMessage(customers.getUsername(), entity.getStatus(), customers.getCountryCode(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), RabbitMqConstants.SEND_CUSTOMER_STATUS_CHANGE_TEMPLATE, RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, Integer.valueOf(entity.getTicketId().intValue()));//add parameters
////                        }
////
////                        if (map.getKey().equalsIgnoreCase(CaseConstants.ASSIGNEE)) {
////                            CaseUpdateDetailsDTO assigneeDetails = new CaseUpdateDetailsDTO();
////                            assigneeDetails.setEntitytype(CaseConstants.ENTITY_ASSIGNEE);
////                            assigneeDetails.setOperation(CaseConstants.OPERATION_CHANGE_ASSIGNEE);
////                            assigneeDetails.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
////                            assigneeDetails.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
////                            assigneeDetails.setCaseUpdate(entity);
////                            detailsList.add(assigneeDetails);
////                        }
////
////                        if (map.getKey().equalsIgnoreCase(CaseConstants.REASON)) {
////                            CaseUpdateDetailsDTO reasonDetails = new CaseUpdateDetailsDTO();
////                            reasonDetails.setEntitytype(CaseConstants.ENTITY_REASON);
////                            reasonDetails.setOperation(CaseConstants.OPERATION_CHANGE_REASON);
////                            reasonDetails.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
////                            reasonDetails.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
////                            reasonDetails.setCaseUpdate(entity);
////                            detailsList.add(reasonDetails);
////                        }
////
////                        if (map.getKey().equalsIgnoreCase(CaseConstants.PRIORITY)) {
////                            CaseUpdateDetailsDTO priorityDetails = new CaseUpdateDetailsDTO();
////                            priorityDetails.setEntitytype(CaseConstants.ENTITY_PRIORITY);
////                            priorityDetails.setOperation(CaseConstants.OPERATION_CHANGE_PRIORITY);
////                            priorityDetails.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
////                            priorityDetails.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
////                            priorityDetails.setCaseUpdate(entity);
////                            detailsList.add(priorityDetails);
////                        }
////
////                        if (map.getKey().equalsIgnoreCase(CaseConstants.REMARK)) {
////                            CaseUpdateDetailsDTO remarkDetails = new CaseUpdateDetailsDTO();
////                            remarkDetails.setEntitytype(CaseConstants.ENTITY_REMARKS);
////                            remarkDetails.setOperation(CaseConstants.OPERATION_ADD_REMARKS);
////                            remarkDetails.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
////                            remarkDetails.setRemarktype(map.getValue().get(CaseConstants.REMARK_TYPE));
////                            remarkDetails.setAttachment(map.getValue().get(CaseConstants.UNIQUE_FILENAME));
////                            remarkDetails.setFilename(map.getValue().get(CaseConstants.FILENAME));
////                            remarkDetails.setCaseUpdate(entity);
////                            detailsList.add(remarkDetails);
////                        }
////
////                        if (map.getKey().equalsIgnoreCase(CaseConstants.REASON_CATEGORY)) {
////                            CaseUpdateDetailsDTO updates = new CaseUpdateDetailsDTO();
////                            updates.setEntitytype(CaseConstants.ENTITY_REASON_CATEGORY);
////                            updates.setOperation(CaseConstants.OPERATION_CHANGE_REASON_CATEGORY);
////                            updates.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
////                            updates.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
////                            updates.setCaseUpdate(entity);
////                            detailsList.add(updates);
////                        }
////
////                        if (map.getKey().equalsIgnoreCase(CaseConstants.REASON_SUB_CATEGORY)) {
////                            CaseUpdateDetailsDTO updates = new CaseUpdateDetailsDTO();
////                            updates.setEntitytype(CaseConstants.ENTITY_REASON_SUB_CATEGORY);
////                            updates.setOperation(CaseConstants.OPERATION_CHANGE_REASON_SUB_CATEGORY);
////                            updates.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
////                            updates.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
////                            updates.setCaseUpdate(entity);
////                            detailsList.add(updates);
////                        }
////
////                        if (map.getKey().equalsIgnoreCase(CaseConstants.CASE_TYPE)) {
////                            CaseUpdateDetailsDTO updates = new CaseUpdateDetailsDTO();
////                            updates.setEntitytype(CaseConstants.ENTITY_CASE_TYPE);
////                            updates.setOperation(CaseConstants.OPERATION_CHANGE_TYPE);
////                            updates.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
////                            updates.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
////                            updates.setCaseUpdate(entity);
////                            detailsList.add(updates);
////                        }
////
////                        if (map.getKey().equalsIgnoreCase(CaseConstants.RESOLUTION_REASON)) {
////                            CaseUpdateDetailsDTO updates = new CaseUpdateDetailsDTO();
////                            updates.setEntitytype(CaseConstants.ENTITY_RESOLUTION_REASON);
////                            updates.setOperation(CaseConstants.OPERATION_RESOLUTION);
////                            updates.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
////                            updates.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
////                            updates.setCaseUpdate(entity);
////                            detailsList.add(updates);
////                        }
////
////                        if (map.getKey().equalsIgnoreCase(CaseConstants.CASE_TITLE)) {
////                            CaseUpdateDetailsDTO statusDetails = new CaseUpdateDetailsDTO();
////                            statusDetails.setEntitytype(CaseConstants.ENTITY_CASE_TITLE);
////                            statusDetails.setOperation(CaseConstants.OPERATION_CHANGE_TITLE);
////                            statusDetails.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
////                            statusDetails.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
////                            statusDetails.setCaseUpdate(entity);
////                            detailsList.add(statusDetails);
////                        }
////
////                        if (map.getKey().equalsIgnoreCase(CaseConstants.CASE_ROOT_CAUSE)) {
////                            CaseUpdateDetailsDTO statusDetails = new CaseUpdateDetailsDTO();
////                            statusDetails.setEntitytype(CaseConstants.ENTITY_CASE_ROOT_CAUSE);
////                            statusDetails.setOperation(CaseConstants.OPERATION_CHANGE_CASE_ROOT_CAUSE);
////                            statusDetails.setNewvalue(map.getValue().get(CaseConstants.NEW_VALUE));
////                            statusDetails.setOldvalue(map.getValue().get(CaseConstants.OLD_VALUE));
////                            statusDetails.setCaseUpdate(entity);
////                            detailsList.add(statusDetails);
////                        }
////
////                        entity.setUpdateDetails(detailsList);
////                    }
////                }
////                if (null == entity.getCommentBy()) entity.setCommentBy(CaseConstants.COMMENT_BY_CUSTOMER);
////                //commented as we are already doing this on creation
//////                staffUser = staffUserService.get(getLoggedInUserId());
////                if (null != staffUser) {
////                    entity.setUpdateby(staffUser.getFullName());
////                    entity.setCreateby(staffUser.getFullName());
////                }
////
//////                dbObj.setNextFollowupDate(null != entity.getNextFollowupDate() ? entity.getNextFollowupDate() : dbObj.getNextFollowupDate());
//////                dbObj.setNextFollowupTime(null != entity.getNextFollowupTime() ? entity.getNextFollowupTime() : dbObj.getNextFollowupTime());
////
////                entity.setTicket(dbObj);
////                dbObj.getCaseUpdateList().add(0, entity);
////            }
////            if(dbObj.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)){
////                GenericDataDTO folloupname=ticketFollowUpService.generateNameOfTheFollowUp(dbObj.getCaseId().intValue());
////                TicketFollowUpDTO ticketFollowUpDTO=new TicketFollowUpDTO();
////                ticketFollowUpDTO.setFollowUpName(folloupname.getData().toString());
////                ticketFollowUpDTO.setCaseId(dbObj.getCaseId().intValue());
////                ticketFollowUpDTO.setStatus("pending");
////                ticketFollowUpDTO.setMvnoId(dbObj.getMvnoId());
////                ticketFollowUpDTO.setIsMissed(true);
////                ticketFollowUpDTO.setIsSend(true);
////                ticketFollowUpDTO.setRemarks(dbObj.getFirstRemark());
////                ticketFollowUpDTO.setCreatedBy(getLoggedInUserId());
////                ticketFollowUpDTO.setStaffUserId(getLoggedInUserId());
////                ticketFollowUpDTO.setFollowUpDatetime(dbObj.getNextFollowupTime().atDate(dbObj.getNextFollowupDate()));
////                ticketFollowUpService.save(ticketFollowUpDTO);
////
////
////            }
////            if (dbObj.getCurrentAssigneeId() != null && (!prePriority.equalsIgnoreCase(dbObj.getPriority()))) {
////                StaffUser currentStaff = new StaffUser();
////                currentStaff = staffUserService.getRepository().findById(dbObj.getCurrentAssigneeId()).orElse(null);
////                if(currentStaff!=null){
////                    if(currentStaff.getStaffUserparent().getId()!=null){
////                        if(getLoggedInUserId()==currentStaff.getStaffUserparent().getId()){
////                            Case updatedCase = tatUtils.updateticketTatMatrix(dbObj);
////                            dbObj.setNextFollowupTime(updatedCase.getNextFollowupTime());
////                            dbObj.setNextFollowupDate(updatedCase.getNextFollowupDate());
////                        }else{
////                            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Only Parent Staff can change the ticket priority !!", null);
////                        }
////                    }
////
////                }
////
////            }
////
//////            else if (dbObj.getCaseStatus().equalsIgnoreCase("pending")) {
//////                tatUtils.changeTicketTatStatus(dbObj, false);
//////            }
//////            else if (entity.getStatus() != null && !entity.getStatus().equalsIgnoreCase("ON HOLD")) {
//////                tatUtils.changeTicketTatStatus(dbObj, true);
//////            }
////            if (entity.getTeamHierarchyMappingId() != null) {
////                dbObj.setTeamHierarchyMappingId(entity.getTeamHierarchyMappingId());
////            }
////                if(entity.getCaseFeedbackRel()!=null){
////                    List<CaseFeedbackRel> caseFeedbackRelList=new ArrayList<>();
////                    for(CaseFeedbackRel caseFeedbackRel:entity.getCaseFeedbackRel()){
////                        if(caseFeedbackRel!=null) {
////                            caseFeedbackRel.setCreated_date(LocalDateTime.now());
////                            caseFeedbackRelList.add(caseFeedbackRel);
////                        }
////                    }
////                    dbObj.setCaseFeedbackRel(caseFeedbackRelList);
////
////
////                }
////            ;
////
////            return caseService.updateEntity(dbObj);
////        } catch (CustomValidationException ce) {
////            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ce.getMessage(), null);
////        } catch (JsonProcessingException e) {
////            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
////            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, e.getMessage(), null);
////        } catch (Exception ex) {
////            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
////            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ex.getMessage(), null);
////        }
////    }
//
//    public void sendAssignTicketMessege(String username, String mobileNumber, String emailId, Integer mvnoId, String caseNumber, String name, String nextFollowupDate, String staffusername, String nextFollowUpTime,String altEmail,String serialNumber) {
//        try {
//            String folleoupdateAndTime  = nextFollowupDate+","+nextFollowUpTime;
//
//            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.TICKET_SUCCESS);
//            if (optionalTemplate.isPresent()) {
//                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
//                    Long buId = null;
//                    if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
//                        buId = getBUIdsFromCurrentStaff().get(0);
//                    }
//                    TicketAssignMessege ticketAssignMessege = new TicketAssignMessege(username, mobileNumber, emailId, mvnoId, RabbitMqConstants.TICKET_ASSIGN_SUCCESS, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, caseNumber, name, folleoupdateAndTime, staffusername,altEmail,serialNumber,buId);
//                    Gson gson = new Gson();
//                    gson.toJson(ticketAssignMessege);
//                    messageSender.send(ticketAssignMessege, RabbitMqConstants.QUEUE_TICKET_ASSIGN_TEAM_SUCCESS);
//                }
//            } else {
//                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
//                System.out.println("Ticket is not assigned.");
//            }
//
//
//        } catch (Throwable e) {
//            throw new RuntimeException(e.getMessage());
//        }
//
//    }
//    public void sendAssignTicketMessege(String username, String mobileNumber, String emailId, Integer mvnoId, String caseNumber, String name, String nextFollowupDate, String staffusername, String nextFollowUpTime) {
//        try {
//            String folleoupdateAndTime  = nextFollowupDate+","+nextFollowUpTime;
//
//            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.TICKET_SUCCESS);
//            if (optionalTemplate.isPresent()) {
//                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
//                    TicketAssignMessege ticketAssignMessege = new TicketAssignMessege(username, mobileNumber, emailId, mvnoId, RabbitMqConstants.TICKET_ASSIGN_SUCCESS, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, caseNumber, name, folleoupdateAndTime, staffusername);
//                    Gson gson = new Gson();
//                    gson.toJson(ticketAssignMessege);
//                    messageSender.send(ticketAssignMessege, RabbitMqConstants.QUEUE_TICKET_ASSIGN_TEAM_SUCCESS);
//                }
//            } else {
//                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
//                System.out.println("Ticket is not assigned.");
//            }
//
//
//        } catch (Throwable e) {
//            throw new RuntimeException(e.getMessage());
//        }
//
//    }
//    public Integer assingTicketToStaffFromTeam(Teams team, Customers customers) {
//        String SUBMODULE = getModuleNameForLog() + " [assingTicketToStaffFromTeam()] ";
//        Integer staffId = null;
//        Set<StaffUser> staffList = team.getStaffUser();
//        if (staffList != null && staffList.size() > 0) {
//            List<StaffUser> tempStaffList = new ArrayList<StaffUser>();
//            for (StaffUser staff : staffList) {
//                if (staff.getServiceAreaNameList() != null && staff.getServiceAreaNameList().size() > 0) {
//                    for (ServiceArea serviceArea : staff.getServiceAreaNameList()) {
//                        if (Objects.equals(customers.getServicearea().getId(), serviceArea.getId())) {
//                            if (staff.getBusinessUnitNameList().size() > 0) {
//                                if (customers.getBuId() != null) {
//                                    for (BusinessUnit businessUnit : staff.getBusinessUnitNameList()) {
//                                        if (customers.getBuId().equals(businessUnit.getId())) {
//                                            tempStaffList.add(staff);
//                                        }
//                                    }
//                                }
//                            } else {
//                                tempStaffList.add(staff);
//                            }
//                        }
//                    }
//                } else if (staff.getServiceAreaNameList().size() == 0) {
//                    if (staff.getBusinessUnitNameList().size() > 0) {
//                        if (customers.getBuId() != null) {
//                            for (BusinessUnit businessUnit : staff.getBusinessUnitNameList()) {
//                                if (customers.getBuId().equals(businessUnit.getId())) {
//                                    tempStaffList.add(staff);
//                                }
//                            }
//                        }
//                    } else {
//                        tempStaffList.add(staff);
//                    }
//
//                }
//            }
//
//            if (tempStaffList != null && tempStaffList.size() > 0) {
//                HashMap<Integer, Long> countListmap = new HashMap<Integer, Long>();
//                for (StaffUser staffUserTemp : tempStaffList) {
//                    Long assingmentCount = caseService.findMinimumAssignReuqestByStaff(staffUserTemp.getId());
//                    if (assingmentCount != null) {
//                        countListmap.put(staffUserTemp.getId(), assingmentCount);
//                    }
//
//                }
//                Long minValueInMap = (Collections.min(countListmap.values()));  // This will return min value in the HashMap
//                for (Entry<Integer, Long> entry : countListmap.entrySet()) {  // Iterate through HashMap
//                    if (entry.getValue() == minValueInMap) {
//                        staffId = entry.getKey();     // staff id with minimum reuqest
//                    }
//                }
//                if (countListmap.size() > 0 && staffId != null) {
//                    return staffId;
//                }
//            } else {
//                ApplicationLogger.logger.error(SUBMODULE + "Staff service is mismatch with customer's service area..");
//                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Staff service area is mismatch with customer's service area.", null);
//            }
//        }
//        return staffId;
//    }
//
//    public int getLoggedInUserId() {
//        int loggedInUserId = -1;
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
//            }
//        } catch (Exception e) {
//            loggedInUserId = -1;
//        }
//        return loggedInUserId;
//    }
//
//    public CaseDTO assignTicketFromTeam(Long caseId, Integer teamId, String remark) {
////        try {
////            Case aCase = caseRepository.findById(caseId).orElse(null);
////            Teams teams = teamsRepository.findById(teamId.longValue()).orElse(null);
////            Customers customers = new Customers();
////            if (null != aCase.getCustomers().getId()) {
////                customers = customersService.get(aCase.getCustomers().getId());
////            }
////            TicketReasonCategoryDTO ticketReasonCategoryDTO = ticketReasonCategoryService.getEntityById(aCase.getTicketReasonCategoryId());
////            TicketReasonCategoryTATMapping ticketReasonCategoryTATMapping = ticketReasonCategoryDTO.getTicketReasonCategoryTATMappingList().stream().filter(tatMapping -> tatMapping.getTeamId() == teamId).findFirst().get();
////            CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
////            caseUpdateDTO.setTicketId(caseId);
////            caseUpdateDTO.setAssignee(assingTicketToStaffFromTeam(teams, customers));
////            caseUpdateDTO.setRemark(remark);
////            caseUpdateDTO.setRemarkType("Change Assignee");
////            caseUpdateDTO.setTatMappingId(ticketReasonCategoryTATMapping.getMappingId());
////            return updateEntity(caseUpdateDTO, null);
////        } catch (Exception ex) {
////            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
////
////
////        }
//        return null;
//    }
//
////    public GenericDataDTO bulkUpdateDetails(List<Long> caseids, String status, String remark) throws Exception {
////        GenericDataDTO genericDataDTO = new GenericDataDTO();
////        try {
////
////            for (Long caseid : caseids) {
////                Case dbObj = caseRepository.findById(caseid).orElse(null);
////                CaseUpdate caseUpdate = caseUpdateRepository.findById(dbObj.getPrimaryKey()).get();
////                CaseUpdateDetails caseUpdateDetails = caseUpdateDetailsRepository.findById(dbObj.getCaseId()).orElse(null);
////
////                caseUpdate.setRemarkType("Change Status");
////
////                caseUpdateDetails.setNewvalue(status);
////                CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
////                caseUpdateDTO.setRemark(remark);//caseUpdateMapper.domainToDTO(caseUpdate, new CycleAvoidingMappingContext());
////                caseUpdateDTO.setStatus(status);
////                caseUpdateDTO.setTicketId(caseid);
////                caseUpdateDTO.setRemarkType(caseUpdate.getRemarkType());
////
////                // caseUpdateDTO.setCaseType("Change Status");
////                caseUpdateDTO.setCaseType(dbObj.getCaseType());
////                updateEntity(caseUpdateDTO, null, false);
////
////                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
////                genericDataDTO.setResponseCode(HttpStatus.OK.value());
////
////            }
////
////        } catch (Exception ex) {
////            ex.getStackTrace();
////        }
////        return genericDataDTO;
////    }
//
//    public void sendCustomerTicketStatusChangeMessage(String username, String status, String countryCode, String mobileNumber, String emailId, Integer mvnoId, String message, String sourceName, Integer ticketnumber) {
//        try {
//            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.SEND_CUSTOMER_STATUS_CHANGE_TEMPLATE);
//            if (optionalTemplate.isPresent()) {
//                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
//                    // Set message in queue to send notification after ticket status change .
//                    Long buId = null;
//                    if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
//                        buId =  getBUIdsFromCurrentStaff().get(0);
//                    }
//                    Map<String, Object> customerData = new HashMap<>();
//                    CustTicketStatusMessage custTicketStatusMessage = new CustTicketStatusMessage(username, status, customerData, countryCode, mobileNumber, emailId, mvnoId, message, optionalTemplate.get(), sourceName, ticketnumber,buId);
//                    Gson gson = new Gson();
//                    gson.toJson(custTicketStatusMessage);
//                    messageSender.send(custTicketStatusMessage, RabbitMqConstants.QUEUE_SEND_CUSTOMER_STATUS_CHANGE);
//                }
//            }
//
//        } catch (Throwable e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }
//
//    public void bulkOperationPerform(CaseUpdateDTO entity){
//        QCase qCase = QCase.case$;
//        BooleanExpression linkedTicketBoolExp = qCase.isNotNull().and(qCase.isDelete.eq(false))
//                .and(qCase.parentTicketId.eq(Math.toIntExact(entity.getTicketId())));
//        List<Case> cases = new ArrayList<>();
//        cases = (List<Case>) caseRepository.findAll(linkedTicketBoolExp);
//        if(!cases.isEmpty()) {
//            for (int i = 0; i < cases.size(); i++) {
//                if(entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_RESOLVED)){
//                    cases.get(i).setCaseStatus(CaseConstants.STATUS_RESOLVED);
//                }else if(entity.getStatus().equalsIgnoreCase(CaseConstants.STATUS_CLOSED)){
//                    cases.get(i).setCaseStatus(CaseConstants.STATUS_CLOSED);
//                }
//                cases.get(i).setTeamHierarchyMappingId(null);
//                cases.get(i).setAssigneeName(null);
//                cases.get(i).setCurrentAssignee(null);
//                caseRepository.save(cases.get(i));
//            }
//        }
//    }
//
//
//    public void sendProblemDomainChangeMsg(String newValue, String ticketNumber, String oldValue, String staffPersonName, String parentMobileNumber, String parentEmailId, Integer mvnoId) {
//        try {
//            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.SEND_PROBLEM_DOMAIN_TEMPLATE_NAME);
//            if (optionalTemplate.isPresent()) {
//                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
//                    Long buId = null;
//                    if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
//                        buId =  getBUIdsFromCurrentStaff().get(0);
//                    }
//                    SendProblemDomainChangeMsg sendProblemDomainChangeMsg = new SendProblemDomainChangeMsg(parentMobileNumber,parentEmailId,RabbitMqConstants.SEND_PROBLEM_DOMAIN_REMARK_MSG,optionalTemplate.get(),oldValue,staffPersonName,newValue ,mvnoId,ticketNumber,buId);
//                    Gson gson = new Gson();
//                    gson.toJson(sendProblemDomainChangeMsg);
//                    messageSender.send(sendProblemDomainChangeMsg, RabbitMqConstants.QUEUE_SEND_PROBLEM_DOMAIN_CHANGE_MSG);
//                }
//            } else {
////                 log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
//                System.out.println("TAT Template not available.");
//            }
//
//
//        } catch (Throwable e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }
//
//
//    public void sendCreateTicketMessege(String customerName, String mobileNumber, String emailId, Integer mvnoId, String caseNumber) {
//        try {
//
//            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.TICKET_CREATION);
//            if (optionalTemplate.isPresent()) {
//                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
//                    TicketCreationMessage ticketCreationMessage = new TicketCreationMessage(mobileNumber,emailId,RabbitMqConstants.TICKET_CREATION_SUCCESS, optionalTemplate.get(),customerName,caseNumber,mvnoId);
//                    Gson gson = new Gson();
//                    gson.toJson(ticketCreationMessage);
//                    messageSender.send(ticketCreationMessage, RabbitMqConstants.QUEUE_TICKET_CREATION_SUCCESS);
//                }
//            } else {
//                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
//                System.out.println("Ticket is not assigned.");
//            }
//
//
//        } catch (Throwable e) {
//            throw new RuntimeException(e.getMessage());
//        }
//
//    }
//
//    public void restartTat(CaseDTO oldCaseDto, CaseUpdateDTO caseUpdateDTO){
//            TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.
//                    findAllByStaffIdAndEntityIdAndEventNameAndIsActiveAndNotificationType(oldCaseDto.getCurrentAssigneeId(), oldCaseDto.getCaseId().intValue(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, false, "Staff");
//            if(tatMatrixWorkFlowDetails != null) {
//                tatMatrixWorkFlowDetails.setIsActive(true);
//                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
//            }
//
//    }
//
//    public void updateTatAtStatusChangeToFollowUp(Case acase, CaseDTO caseDTO) {
//
//        LocalDateTime followupDateTime;
//        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetailsList = new ArrayList<>();
//        List<TatMatrixWorkFlowDetails> updatedTatmatrixList = new ArrayList<>();
//
//        if (acase != null) {
//            followupDateTime = LocalDateTime.of(acase.getNextFollowupDate(), acase.getNextFollowupTime());
//            tatMatrixWorkFlowDetailsList = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActiveAndEventName(acase.getCaseId().intValue(), true, "CASE");
//
//        } else if (caseDTO != null) {
//            followupDateTime = LocalDateTime.of(caseDTO.getNextFollowupDate(), caseDTO.getNextFollowupTime());
//            tatMatrixWorkFlowDetailsList = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActiveAndEventName(Math.toIntExact(caseDTO.getCaseId()), true, "CASE");
//        } else {
//            return;
//        }
//        // updateing start date first
//
//        for(TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails : tatMatrixWorkFlowDetailsList){
//            tatMatrixWorkFlowDetails.setStartDateTime(followupDateTime);
//            updatedTatmatrixList.add(tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails));
//        }
//
//
//        for (int i =0; i<updatedTatmatrixList.size(); i++) {
//
//            String munit = updatedTatmatrixList.get(i).getMunit();
//            long mtime = Long.parseLong(updatedTatmatrixList.get(i).getMtime());
//
//            if (munit.equalsIgnoreCase("Day")) {
//                followupDateTime = updatedTatmatrixList.get(i).getStartDateTime().plusDays(mtime);
//            } else if (munit.equalsIgnoreCase("Hours")) {
//                followupDateTime = updatedTatmatrixList.get(i).getStartDateTime().plusHours(mtime);
//            } else {
//                followupDateTime = updatedTatmatrixList.get(i).getStartDateTime().plusMinutes(mtime);
//            }
//        }
//        if(acase!=null){
//            acase.setNextFollowupDate(followupDateTime.toLocalDate());
//            acase.setNextFollowupTime(followupDateTime.toLocalTime());
//            caseRepository.save(acase);
//        }else if(caseDTO!=null){
//            caseDTO.setNextFollowupDate(followupDateTime.toLocalDate());
//            caseDTO.setNextFollowupTime(followupDateTime.toLocalTime());
//            caseRepository.save(caseMapper.dtoToDomain(caseDTO,new CycleAvoidingMappingContext()));
//        }
//
//    }
//
//    public CaseUpdateDTO updateTatAtStatusChangeToFollowUps(CaseUpdateDTO acase) {
//
//        LocalDateTime followupDateTime =  null;
//        LocalDateTime followUpStrtTime = LocalDateTime.now();
//        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetailsList = new ArrayList<>();
//        List<TatMatrixWorkFlowDetails> updatedTatmatrixList = new ArrayList<>();
//
//        CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
//
//        if (acase != null) {
//            followupDateTime = LocalDateTime.of(acase.getNextFollowupDate(), acase.getNextFollowupTime());
//            tatMatrixWorkFlowDetailsList = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActiveAndEventName(acase.getTicketId().intValue(), true, "CASE");
//
//            for(TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails : tatMatrixWorkFlowDetailsList){
//
//                //calculateTimeDifference() is substracting the total worked time on ticket
//                TimeUnitWithTotal timeUnitWithTotal = calculateTotalWorkedTime(followUpStrtTime,tatMatrixWorkFlowDetails.getStartDateTime());
//                TimeUnitWithTotal finalTimeunitWitTotal = calculateTimeDifference(Long.parseLong(tatMatrixWorkFlowDetails.getMtime()),tatMatrixWorkFlowDetails.getMunit(),timeUnitWithTotal.getUnit(),timeUnitWithTotal.getTotalTime());
//                /*for next message send we are setting the followup date in start time, hence once followup is schedule we are adding the
//                  ther remaing time into OLA time */
//                tatMatrixWorkFlowDetails.setStartDateTime(followupDateTime);
//                tatMatrixWorkFlowDetails.setMunit(finalTimeunitWitTotal.getUnit());
//                tatMatrixWorkFlowDetails.setMtime(String.valueOf(finalTimeunitWitTotal.getTotalTime()));
//                updatedTatmatrixList.add(tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails));
//            }
//        }
//
//        // This loop will iteratate and set the next followup date and time including OLA time to show on GUI counter
//        for (int i =0; i<updatedTatmatrixList.size(); i++) {
//
//            String munit = updatedTatmatrixList.get(i).getMunit();
//            long mtime = Long.parseLong(updatedTatmatrixList.get(i).getMtime());
//
//            if (munit.equalsIgnoreCase("Day")) {
//                followupDateTime = updatedTatmatrixList.get(i).getStartDateTime().plusDays(mtime);
//            } else if (munit.equalsIgnoreCase("Hours")) {
//                followupDateTime = updatedTatmatrixList.get(i).getStartDateTime().plusHours(mtime);
//            } else {
//                followupDateTime = updatedTatmatrixList.get(i).getStartDateTime().plusMinutes(mtime);
//            }
//        }
//        if(acase!=null){
//            caseUpdateDTO.setNextFollowupDate(followupDateTime.toLocalDate());
//            caseUpdateDTO.setNextFollowupTime(followupDateTime.toLocalTime());
//           // caseRepository.save(acase);
//        }
//        return caseUpdateDTO;
//    }
//
//
//    /* method for send notification ticket close*/
//    public void sendCustTicketCloseMessage(String username, String mobileNumber, String emailId, String status, Integer mvnoId, String caseNumber,Long buId) {
//        try {
//            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_TICKET_CLOSE_TEMPLATE);
//            if (optionalTemplate.isPresent()) {
//                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
//                    CustTicketCloseMsg custTicketCloseMsg = new CustTicketCloseMsg(username, mobileNumber, emailId, status, mvnoId, caseNumber, RabbitMqConstants.CUSTOMER_TICKET_CLOSE_EVENT, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY,buId);
//                    Gson gson = new Gson();
//                    gson.toJson(custTicketCloseMsg);
//                    messageSender.send(custTicketCloseMsg, RabbitMqConstants.QUEUE_CUSTOMER_TICKET_CLOSE_NOTIFICATION);
//                }
//            } else {
//                System.out.println("Message of Customer Ticket Close is not sent because template is not present.");
//            }
//        } catch (Throwable e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }
//
//
//    public CaseUpdateDTO setOnHoldEndTime (CaseUpdateDTO caseUpdateDTO){
//       CaseUpdateDTO caseUpdateDTOs = new CaseUpdateDTO();
//
//        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetailsList = new ArrayList<>();
//
//        tatMatrixWorkFlowDetailsList = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActiveAndEventName(caseUpdateDTO.getTicketId().intValue(),true, "CASE");
//
//        LocalDateTime localDateTime = LocalDateTime.now();
//
//        List<TatMatrixWorkFlowDetails> savedTatMatrixWorkflowdetailsList = new ArrayList<>();
//
//        if (tatMatrixWorkFlowDetailsList != null) {
//            for (int i = 0; i < tatMatrixWorkFlowDetailsList.size(); i++) {
//                tatMatrixWorkFlowDetailsList.get(i).setTicketHoldTimeEnd(localDateTime);
//                savedTatMatrixWorkflowdetailsList.add(tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetailsList.get(i)));
//            }
//           caseUpdateDTOs = addAdditionalTimeInTicket(savedTatMatrixWorkflowdetailsList,caseUpdateDTO);
//        }
//
//    return caseUpdateDTOs;
//    }
//
//    public void setOnHoldStartTime (CaseUpdateDTO caseUpdateDTO){
//        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetailsList = new ArrayList<>();
//
//        tatMatrixWorkFlowDetailsList = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActiveAndEventName(caseUpdateDTO.getTicketId().intValue(),true, "CASE");
//
//        LocalDateTime localDateTime = LocalDateTime.now();
//
//        if (tatMatrixWorkFlowDetailsList != null) {
//            for (int i = 0; i < tatMatrixWorkFlowDetailsList.size(); i++) {
//                tatMatrixWorkFlowDetailsList.get(i).setTicketHoldTimeInit(localDateTime);
//                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetailsList.get(i));
//            }
//        }
//
//    }
//
//
//
////    public CaseUpdateDTO addAdditionalTimeInTicket (List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetails, CaseUpdateDTO caseUpdateDTO)
////    {
////        CaseUpdateDTO caseUpdateDTO1 = new CaseUpdateDTO();
////        Long totalAddtionalTime = null;
////        if(tatMatrixWorkFlowDetails.size()>0){
////
////            for (TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails1:tatMatrixWorkFlowDetails) {
////                Duration additionalTime = Duration.between(tatMatrixWorkFlowDetails1.getTicketHoldTimeInit(), tatMatrixWorkFlowDetails1.getTicketHoldTimeEnd());
////
////                long days = additionalTime.toDays();
////                long hours = additionalTime.toHours() % 24;
////                long minutes = additionalTime.toMinutes() % 60;
////                long seconds = additionalTime.getSeconds() % 60;
////
////                Integer previourMtime = Integer.valueOf(tatMatrixWorkFlowDetails1.getMtime());   // 12
////                String previousMunit = tatMatrixWorkFlowDetails1.getMunit();     // M : Minutes , H: Hours , D : Day
////
////                if (days > 0) {
////                    tatMatrixWorkFlowDetails1.setMtime(String.valueOf(days+(previourMtime)));
////                    tatMatrixWorkFlowDetails1.setMunit("Day");
////                    totalAddtionalTime = days;
////                } else {
////                    if (hours > 0) {
////                        tatMatrixWorkFlowDetails1.setMtime(String.valueOf(previourMtime+hours));
////                        tatMatrixWorkFlowDetails1.setMunit("Hours");
////                        totalAddtionalTime = hours;
////
////                    } else {
////                        if (minutes > 0) {
////                            tatMatrixWorkFlowDetails1.setMtime(String.valueOf(previourMtime+minutes));
////                            tatMatrixWorkFlowDetails1.setMunit("Min");
////                            totalAddtionalTime = minutes;
////                        }
////                    }
////                }
////                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails1);
////                caseUpdateDTO = caseFolloupdateUpdate(tatMatrixWorkFlowDetails1, caseUpdateDTO);
////
////                caseUpdateDTO1 = updateSlaTime(caseUpdateDTO,totalAddtionalTime,"",tatMatrixWorkFlowDetails.get(0).getEntityId());
////            }
////
////        }
////        caseUpdateDTO.setCaseSlaTime(caseUpdateDTO1.getCaseSlaTime());
////        caseUpdateDTO.setCaseSlaUnit(caseUpdateDTO1.getCaseSlaUnit());
////        return caseUpdateDTO;
////    }
//
//    public CaseUpdateDTO addAdditionalTimeInTicket(List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetails, CaseUpdateDTO caseUpdateDTO) {
//        CaseUpdateDTO caseUpdateDTO1 = new CaseUpdateDTO();
//        Long totalAdditionalTime = null;
//        String totalAdditionalTimeUnit = null ;
//
//        if (tatMatrixWorkFlowDetails.size() > 0) {
//            for (TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails1 : tatMatrixWorkFlowDetails) {
//                Duration additionalTime = Duration.between(tatMatrixWorkFlowDetails1.getTicketHoldTimeInit(), tatMatrixWorkFlowDetails1.getTicketHoldTimeEnd());
//
//                long days = additionalTime.toDays();
//                long hours = additionalTime.toHours() % 24;
//                long minutes = additionalTime.toMinutes() % 60;
//                long seconds = additionalTime.getSeconds() % 60;
//
//                Integer previousMTime = Integer.valueOf(tatMatrixWorkFlowDetails1.getMtime());
//                String previousMUnit = tatMatrixWorkFlowDetails1.getMunit();
//
//                // Convert Mtime to the smaller unit
//                if (days > 0) {
//                    totalAdditionalTimeUnit = "Day";
//                    totalAdditionalTime = days;
//                } else if (hours > 0) {
//                    totalAdditionalTimeUnit = "Hour";
//                    totalAdditionalTime = hours;
//                } else if (minutes > 0) {
//                    totalAdditionalTimeUnit = "Min";
//                    totalAdditionalTime=minutes;
//                }
//
//                TimeUnitWithTotal timeUnitWithTotal = addTimeInSmallerUnit(previousMTime,previousMUnit,totalAdditionalTime.intValue(),totalAdditionalTimeUnit);
//
//
//                // Set the Mtime and Munit
//                tatMatrixWorkFlowDetails1.setMtime(String.valueOf(timeUnitWithTotal.getTotalTime()));
//                tatMatrixWorkFlowDetails1.setMunit(timeUnitWithTotal.getUnit());
//
//                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails1);
//               // caseUpdateDTO = caseFolloupdateUpdate(tatMatrixWorkFlowDetails1, caseUpdateDTO);
//
//                caseUpdateDTO1 = updateSlaTime(caseUpdateDTO, totalAdditionalTime,totalAdditionalTimeUnit, "", tatMatrixWorkFlowDetails.get(0).getEntityId());
//            }
//        }
//
//        caseUpdateDTO.setCaseSlaTime(caseUpdateDTO1.getCaseSlaTime());
//        caseUpdateDTO.setCaseSlaUnit(caseUpdateDTO1.getCaseSlaUnit());
//        return caseUpdateDTO;
//    }
//
//
//    private TimeUnitWithTotal addTimeInSmallerUnit(int previousTime, String previousUnit, int additionalTime, String additionalUnit) {
//        long totalTime = 0;
//        String smallerUnit;
//
//        if (previousUnit.equals("Day") && (additionalUnit.equals("Hour") || additionalUnit.equals("Min"))) {
//            smallerUnit = additionalUnit;
//        } else if (previousUnit.equals("Hour") && additionalUnit.equals("Min")) {
//            smallerUnit = additionalUnit;
//        } else {
//            smallerUnit = previousUnit;
//        }
//
//        if (smallerUnit.equalsIgnoreCase("Day")) {
//            long previousTimeInDays = convertToDays(previousTime, previousUnit);
//            long additionalTimeInDays = convertToDays(additionalTime, additionalUnit);
//            totalTime = previousTimeInDays + additionalTimeInDays;
//        } else if (smallerUnit.equalsIgnoreCase("Hour")) {
//            long previousTimeInHours = convertToHours(previousTime, previousUnit);
//            long additionalTimeInHours = convertToHours(additionalTime, additionalUnit);
//            totalTime = previousTimeInHours + additionalTimeInHours;
//        } else if (smallerUnit.equalsIgnoreCase("Min")) {
//            long previousTimeInMinutes = convertToMinutes(previousTime, previousUnit);
//            long additionalTimeInMinutes = convertToMinutes(additionalTime, additionalUnit);
//            totalTime = previousTimeInMinutes + additionalTimeInMinutes;
//        }
//
//        return new TimeUnitWithTotal(totalTime, smallerUnit);
//    }
//
//    private long convertToDays(int time, String unit) {
//        if (unit.equals("Hour")) {
//            return time / 24;
//        } else if (unit.equals("Min")) {
//            return time / (24 * 60);
//        }
//        return time;
//    }
//
//    private long convertToHours(int time, String unit) {
//        if (unit.equals("Day")) {
//            return time * 24;
//        } else if (unit.equals("Min")) {
//            return time / 60;
//        }
//        return time;
//    }
//
//    private long convertToMinutes(int time, String unit) {
//        if (unit.equals("Day")) {
//            return time * 24 * 60;
//        } else if (unit.equals("Hour")) {
//            return time * 60;
//        }
//        return time;
//    }
//
//
//
//
//
//
//
//    public CaseUpdateDTO caseFolloupdateUpdate(TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails, CaseUpdateDTO caseUpdateDTO){
//
//        TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails1 = tatMatrixWorkFlowDetailsRepo.findById(tatMatrixWorkFlowDetails.getId()).orElse(null);
//
//        //finding case
//        Case cases = caseRepository.findById(Long.valueOf(tatMatrixWorkFlowDetails1.getEntityId())).orElse(null);
//
//
//        // calculate upcoming date
//        LocalDateTime localDate = tatMatrixWorkFlowDetails1.getStartDateTime();
//        LocalDateTime updateFollowupDateTime = null;
//        if (tatMatrixWorkFlowDetails1.getMunit().equalsIgnoreCase("Min")) {
//            updateFollowupDateTime = localDate.plusMinutes(Long.valueOf(tatMatrixWorkFlowDetails1.getMtime()));
//        } else if (tatMatrixWorkFlowDetails1.getMunit().equalsIgnoreCase("Hours")) {
//            updateFollowupDateTime = localDate.plusHours(Long.valueOf(tatMatrixWorkFlowDetails1.getMtime()));
//        } else if (tatMatrixWorkFlowDetails1.getMunit().equalsIgnoreCase("Day")) {
//            updateFollowupDateTime = localDate.plusDays(Long.valueOf(tatMatrixWorkFlowDetails1.getMtime()));
//        }
////        caseMapper.domainToDTO(cases,new CycleAvoidingMappingContext());
//        caseUpdateDTO.setNextFollowupDate(updateFollowupDateTime.toLocalDate());
//        caseUpdateDTO.setNextFollowupTime(updateFollowupDateTime.toLocalTime());
//        //caseUpdateRepository.save(caseUpdateMapper.dtoToDomain(caseUpdateDTO,new CycleAvoidingMappingContext()));
//        return caseUpdateDTO;
//    }
//
//
//    public CaseUpdateDTO updateSlaTime(CaseUpdateDTO caseUpdateDTO,Long totalAdditionalTime,String totalAdditionalUnit ,String priority,Integer caseId){
//        TicketTatMatrix tatMatrix = new TicketTatMatrix();
//        Case cases = new Case();
//        CaseDTO caseDTO = new CaseDTO();
//
//        if(caseId != null){
//            cases = caseRepository.findById(Long.valueOf(caseId)).orElse(null);
//            caseDTO = caseMapper.domainToDTO(cases, new CycleAvoidingMappingContext());
//            tatMatrix = caseService.getTicketTatMatrixFromSubReasonId(caseDTO);
//        }
//        if (caseUpdateDTO.getCaseSlaTime()==null && caseUpdateDTO.getCaseSlaUnit()==null){
//            if (tatMatrix != null) {
//                if (cases.getPriority().equalsIgnoreCase("Medium")) {
//                    caseUpdateDTO.setCaseSlaTime((int) (totalAdditionalTime + tatMatrix.getSlaTimep2()));
//                    caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp2());
//                } else if (cases.getPriority().equalsIgnoreCase("High")) {
//                    caseUpdateDTO.setCaseSlaTime((int) (totalAdditionalTime + tatMatrix.getSlaTimep1()));
//                    caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp1());
//                } else {
//                    caseUpdateDTO.setCaseSlaTime((int) (totalAdditionalTime + tatMatrix.getSlaTime3()));
//                    caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp3());
//                }
//            }
//
//        }
//        else{
//            if (tatMatrix != null) {
//                if (caseUpdateDTO.getPriority().equalsIgnoreCase("High")) {
//                    TimeUnitWithTotal timeUnitWithTotal = addTimeInSmallerUnit(tatMatrix.getSlaTimep1().intValue(),tatMatrix.getSunitp1(),totalAdditionalTime.intValue(),totalAdditionalUnit);
//                    caseUpdateDTO.setCaseSlaTime((int) timeUnitWithTotal.getTotalTime());
//                    caseUpdateDTO.setCaseSlaUnit(timeUnitWithTotal.getUnit());
//                } else if (caseUpdateDTO.getPriority().equalsIgnoreCase("Medium")) {
//                    TimeUnitWithTotal timeUnitWithTotal = addTimeInSmallerUnit(tatMatrix.getSlaTimep2().intValue(),tatMatrix.getSunitp2(),totalAdditionalTime.intValue(),totalAdditionalUnit);
//                    caseUpdateDTO.setCaseSlaTime((int) timeUnitWithTotal.getTotalTime());
//                    caseUpdateDTO.setCaseSlaUnit(timeUnitWithTotal.getUnit());
//                } else {
//                    TimeUnitWithTotal timeUnitWithTotal = addTimeInSmallerUnit(tatMatrix.getSlaTime3().intValue(),tatMatrix.getSunitp3(),totalAdditionalTime.intValue(),totalAdditionalUnit);
//                    caseUpdateDTO.setCaseSlaTime((int) timeUnitWithTotal.getTotalTime());
//                    caseUpdateDTO.setCaseSlaUnit(timeUnitWithTotal.getUnit());
//                }
//            }
//
//        }
//
//        return caseUpdateDTO;
//    }
//
//    public void removeStartStopTimeWhenFolloupSet(CaseUpdateDTO caseUpdateDTO) {
//
//        TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActive(caseUpdateDTO.getTicketId().intValue(), true);
//
//
//        if (tatMatrixWorkFlowDetails.getTicketHoldTimeInit() != null || tatMatrixWorkFlowDetails.getTicketHoldTimeEnd() != null) {
//            tatMatrixWorkFlowDetails.setTicketHoldTimeInit(null);
//            tatMatrixWorkFlowDetails.setTicketHoldTimeEnd(null);
//            tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
//        }
//
//
//    }
//
//
//    public static TimeUnitWithTotal calculateTotalWorkedTime(LocalDateTime currentTime, LocalDateTime previousTime) {
//        Duration duration = Duration.between(currentTime, previousTime);
//        long totalSeconds = duration.getSeconds();
//
//        TimeUnitWithTotal timeUnitWithTotal = new TimeUnitWithTotal();
//
//        if (totalSeconds < 0) {
//            totalSeconds *= -1;
//        }
//
//        if (totalSeconds >= 86400) {
//            timeUnitWithTotal.setTotalTime(totalSeconds / 86400);
//            timeUnitWithTotal.setUnit("Day");
//        } else if (totalSeconds >= 3600) {
//            timeUnitWithTotal.setTotalTime(totalSeconds / 3600);
//            timeUnitWithTotal.setUnit("Hour");
//        } else if (totalSeconds >= 60) {
//            timeUnitWithTotal.setTotalTime(totalSeconds / 60);
//            timeUnitWithTotal.setUnit("Min");
//        }
//
//        return timeUnitWithTotal;
//    }
//
//
//    public TimeUnitWithTotal calculateTimeDifference(long mtime, String mUnit, String workedUnit, long workedTime) {
//        TimeUnitWithTotal timeUnitWithTotal = new TimeUnitWithTotal();
//
//        TimeUnitWithTotal result = removeTimeInSmallerUnit(Math.toIntExact(mtime), mUnit, (int) workedTime, workedUnit);
//        timeUnitWithTotal.setTotalTime(result.getTotalTime());
//        timeUnitWithTotal.setUnit(result.getUnit());
//
//        return timeUnitWithTotal;
//    }
//
//
//    private TimeUnitWithTotal removeTimeInSmallerUnit(int previousTime, String previousUnit, int additionalTime, String additionalUnit) {
//        long totalTime = 0;
//        String smallerUnit;
//
//        if (previousUnit.equalsIgnoreCase("Day") && (additionalUnit.equalsIgnoreCase("Hour") || additionalUnit.equalsIgnoreCase("Min"))) {
//            smallerUnit = additionalUnit;
//        } else if (previousUnit.equalsIgnoreCase("Hour") && additionalUnit.equalsIgnoreCase("Min")) {
//            smallerUnit = additionalUnit;
//        } else {
//            smallerUnit = previousUnit;
//        }
//
//        if (smallerUnit.equalsIgnoreCase("Day")) {
//            long previousTimeInDays = convertToDays(previousTime, previousUnit);
//            long additionalTimeInDays = convertToDays(additionalTime, additionalUnit);
//            totalTime = previousTimeInDays - additionalTimeInDays;
//        } else if (smallerUnit.equalsIgnoreCase("Hour")) {
//            long previousTimeInHours = convertToHours(previousTime, previousUnit);
//            long additionalTimeInHours = convertToHours(additionalTime, additionalUnit);
//            totalTime = previousTimeInHours - additionalTimeInHours;
//        } else if (smallerUnit.equalsIgnoreCase("Min")) {
//            long previousTimeInMinutes = convertToMinutes(previousTime, previousUnit);
//            long additionalTimeInMinutes = convertToMinutes(additionalTime, additionalUnit);
//            totalTime = previousTimeInMinutes - additionalTimeInMinutes;
//        }
//
//        return new TimeUnitWithTotal(totalTime, smallerUnit);
//    }
//
//
//public CaseUpdateDTO restartTATbeforeFollowupEndAndStatusChage(CaseUpdateDTO caseUpdateDTO){
//    TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActive(caseUpdateDTO.getTicketId().intValue(), true);
//    if (tatMatrixWorkFlowDetails!=null) {
//        tatMatrixWorkFlowDetails.setStartDateTime(LocalDateTime.now());
//        tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
//
//        LocalDateTime localDate = tatMatrixWorkFlowDetails.getStartDateTime();
//        LocalDateTime updateFollowupDateTime = null;
//        if (tatMatrixWorkFlowDetails.getMunit().equalsIgnoreCase("Min")) {
//            updateFollowupDateTime = localDate.plusMinutes(Long.valueOf(tatMatrixWorkFlowDetails.getMtime()));
//        } else if (tatMatrixWorkFlowDetails.getMunit().equalsIgnoreCase("Hours")) {
//            updateFollowupDateTime = localDate.plusHours(Long.valueOf(tatMatrixWorkFlowDetails.getMtime()));
//        } else if (tatMatrixWorkFlowDetails.getMunit().equalsIgnoreCase("Day")) {
//            updateFollowupDateTime = localDate.plusDays(Long.valueOf(tatMatrixWorkFlowDetails.getMtime()));
//        }
//
//        caseUpdateDTO.setNextFollowupDate(updateFollowupDateTime.toLocalDate());
//        caseUpdateDTO.setNextFollowupTime(updateFollowupDateTime.toLocalTime());
//
//    }
//    return  caseUpdateDTO;
//}
//
}
