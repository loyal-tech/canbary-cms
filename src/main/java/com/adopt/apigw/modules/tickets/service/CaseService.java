package com.adopt.apigw.modules.tickets.service;
//
//import com.adopt.apigw.constants.CaseConstants;
//import com.adopt.apigw.constants.ClientServiceConstant;
//import com.adopt.apigw.constants.SearchConstants;
//import com.adopt.apigw.core.dto.GenericDataDTO;
//import com.adopt.apigw.core.dto.GenericSearchModel;
//import com.adopt.apigw.core.dto.PaginationRequestDTO;
//import com.adopt.apigw.core.exceptions.DataNotFoundException;
//import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
//import com.adopt.apigw.core.service.ExBaseAbstractService2;
//import com.adopt.apigw.core.utillity.fileUtillity.FileUtility;
//import com.adopt.apigw.core.utillity.log.ApplicationLogger;
//import com.adopt.apigw.exception.CustomValidationException;
//import com.adopt.apigw.mapper.postpaid.StaffUserMapper;
//import com.adopt.apigw.model.common.*;
//import com.adopt.apigw.model.lead.LeadMaster;
//import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;
//import com.adopt.apigw.modules.Communication.Helper.CommunicationHelper;
//import com.adopt.apigw.modules.Matrix.domain.QTatMatrixWorkFlowDetails;
//import com.adopt.apigw.modules.Matrix.domain.TatMatrixWorkFlowDetails;
//import com.adopt.apigw.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
//import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
//import com.adopt.apigw.modules.Teams.domain.*;
//import com.adopt.apigw.modules.Teams.repository.HierarchyRepository;
//import com.adopt.apigw.modules.Teams.repository.TeamHierarchyMappingRepo;
//import com.adopt.apigw.modules.Teams.repository.TeamUserMappingsRepocitory;
//import com.adopt.apigw.modules.Teams.repository.TeamsRepository;
//import com.adopt.apigw.modules.Teams.service.HierarchyService;
//import com.adopt.apigw.modules.Teams.service.TeamsService;
//import com.adopt.apigw.modules.Template.domain.TemplateNotification;
//import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
//import com.adopt.apigw.modules.TicketTatMatrix.Domain.TicketTatMatrix;
//import com.adopt.apigw.modules.TicketTatMatrix.Domain.TicketTatMatrixMapping;
//import com.adopt.apigw.modules.subscriber.queryScript.SubscriberSearchQueryScript;
//import com.adopt.apigw.modules.tickets.domain.*;
//import com.adopt.apigw.modules.tickets.mapper.CaseMapper;
//import com.adopt.apigw.modules.tickets.model.*;
//import com.adopt.apigw.modules.tickets.query.CaseSearchQueryScript;
//import com.adopt.apigw.modules.tickets.repository.*;
//import com.adopt.apigw.modules.workflow.domain.QWorkflowAssignStaffMapping;
//import com.adopt.apigw.modules.workflow.domain.WorkflowAssignStaffMapping;
//import com.adopt.apigw.modules.workflow.repository.WorkflowAssignStaffMappingRepo;
//import com.adopt.apigw.pojo.api.CustomersPojo;
//import com.adopt.apigw.pojo.api.StaffUserPojo;
//import com.adopt.apigw.pojo.api.TicketETRPojo;
//import com.adopt.apigw.rabbitMq.MessageSender;
//import com.adopt.apigw.rabbitMq.RabbitMqConstants;
//import com.adopt.apigw.rabbitMq.message.TicketETRMsg;
//import com.adopt.apigw.rabbitMq.message.TicketMessageIntegration;
//import com.adopt.apigw.rabbitMq.message.TicketRescheduleMsg;
//import com.adopt.apigw.repository.LeadMasterRepository;
//import com.adopt.apigw.repository.common.*;
//import com.adopt.apigw.service.common.ClientServiceSrv;
//import com.adopt.apigw.service.common.CustomersService;
//import com.adopt.apigw.service.common.StaffUserService;
//import com.adopt.apigw.service.common.WorkflowAuditService;
//import com.adopt.apigw.spring.LoggedInUser;
//import com.adopt.apigw.spring.MessagesPropertyConfig;
//import com.adopt.apigw.utils.*;
//import com.google.gson.Gson;
//import com.itextpdf.text.Document;
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQuery;
//import org.apache.commons.collections4.IterableUtils;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.validation.Valid;
//import java.lang.reflect.Field;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.stream.Collectors;

//@Service
public class CaseService  {

//    @Autowired
//    private CaseRepository caseRepository;
//
//    @Autowired
//    private CaseFeedbackRelRepository caseFeedbackRelRepository;
//
//    @Autowired
//    private LeadMasterRepository leadMasterRepository;
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    public static final String MODULE = " [CaseService] ";
//
//    @Autowired
//    private CaseMapper caseMapper;
//
//    @Autowired
//    private CaseAssignmentService assignmentService;
//
//    @Autowired
//    private StaffUserService staffUserService;
//
//    @Autowired
//    private TatAuditRepository tatAuditRepository;


//    @Autowired
//    private CaseDTO caseDTO;

//    @Autowired
//    private CaseReasonConfigService caseReasonConfigService;

//    @Autowired
//    private CustomersService customersService;
//
//    @Autowired
//    private CaseUpdateService caseUpdateService;
//
//    @Autowired
//    TeamsService teamsService;
//
//    @Autowired
//    TicketReasonCategoryService ticketReasonCategoryService;
//    @Autowired
//    ClientServiceSrv clientService;
//
//    @Autowired
//    TeamsRepository teamsRepository;
//
//    @Autowired
//    HierarchyService hierarchyService;
//
//    @Autowired
//    ClientServiceSrv clientServiceSrv;
//
//    @Autowired
//    TeamHierarchyMappingRepo teamHierarchyMappingRepo;
//
//    @Autowired
//    ClientServiceRepository clientServiceRepository;
//
//    @Autowired
//    private TatUtils tatUtils;
//
//    @Autowired
//    TicketAssignStaffMappingRepo ticketAssignStaffMappingRepo;
//
//    @Autowired
//    private CaseService caseService;
//
//    private String PATH;
//
//    @Autowired
//    private FileUtility fileUtility;
//
//    @Autowired
//    private CaseDocDetailsService caseDocDetailsService;
//
//    @Autowired
//    WorkflowAuditService workflowAuditService;
//
//    @Autowired
//    StaffUserRepository staffUserRepository;
//
//    @Autowired
//    StaffUserMapper staffUserMapper;
//
//    @Autowired
//    private MessagesPropertyConfig messagesProperty;
//
//    @Autowired
//    private NotificationTemplateRepository templateRepository;
//
//    @Autowired
//    private MessageSender messageSender;
//
//    @Autowired
//    TicketETRAuditRepository ticketETRAuditRepository;
//
//    @Autowired
//    private TicketReasonCategoryRepo ticketReasonCategoryRepo;
//
//    @Autowired
//    private TicketReasonSubCategoryRepo ticketReasonSubCategoryRepo;
//
//    @Autowired
//    private TatQueryFieldMappingRepo tatQueryFieldMappingRepo;
//
//    @Autowired
//    private WorkFlowQueryUtils workFlowQueryUtils;
//
//    @Autowired
//    private EnterpriseETRAuditRepository enterpriseETRAuditRepository;
//
//    @Autowired
//    TatMatrixWorkFlowDetailsRepo tatMatrixWorkFlowDetailsRepo;
//
//    @Autowired
//    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;
//    @Autowired
//    TeamUserMappingsRepocitory teamUserMappingsRepocitory;
//    @Autowired
//    HierarchyRepository hierarchyRepository;
//    @Autowired
//    WorkflowAssignStaffMappingRepo workflowAssignStaffMappingRepo;
//    @Autowired
//    MvnoRepository mvnoRepository;
//
//    public CaseService(CaseRepository repository, CaseMapper mapper) {
//        super(repository, mapper);
//        sortColMap.put("id", "case_id");
//    }

//    @Transactional
//    public CaseDTO saveEntity(CaseDTO entity, List<MultipartFile> file) throws Exception {
//        try {
//
//            boolean flag= duplicateVerifyDomainAtSave(entity);
//            Customers customers;
//            String ticketCreatedFrom = null;
//            if(!flag) {
//                if(entity.getCreatedFrom() != null && entity.getCreatedFrom().equalsIgnoreCase("Selfcare CWSC")){
//                    customers = customersService.getcustForCwsc(entity.getCustomersId());
//                    if (null == entity.getCaseUpdateList() || 0 == entity.getCaseUpdateList().size()) {
//                        ticketCreatedFrom = entity.getCreatedFrom();
//                        entity.setCaseUpdateList(new ArrayList<>());
//                    }
//                }
//                else{
//                    customers = customersService.get(entity.getCustomersId());
//                    if (null == entity.getCaseUpdateList() || 0 == entity.getCaseUpdateList().size()) {
//                        ticketCreatedFrom = entity.getCreatedFrom();
//                        entity.setCaseUpdateList(new ArrayList<>());
//                    }
//                }
//
//               if (entity.getCreatedFrom() != null && (entity.getCreatedFrom().equalsIgnoreCase("CWSC") || entity.getCreatedFrom().equalsIgnoreCase("Selfcare CWSC"))) {
//                   List<GrantedAuthority> role_name = new ArrayList<>();
//                   List<Long> buid = new ArrayList<>();
//                   buid.add(customers.getBuId());
//                   role_name.add(new SimpleGrantedAuthority("ADMIN"));
//                   String mvnoName=null;
//                   if(customers.getMvnoId()!=null){
//                       mvnoName=mvnoRepository.findMvnoNameById(Long.valueOf(customers.getMvnoId()));
//                   }
//                   LoggedInUser user = new LoggedInUser(customers.getUsername(), customers.getPassword(), true, true, true, true, role_name, customers.getFirstname(), customers.getLastname(), LocalDateTime.now(), customers.getId(), customers.getPartner().getId(), "ADMIN", customers.getServicearea().getId(), customers.getMvnoId(), null, customers.getId(), buid, false, new ArrayList<String>(), new ArrayList<Long>(),mvnoName);
//                   UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
//                   SecurityContextHolder.getContext().setAuthentication(auth);
//               }
//
//
//               //Open complain communication
//
//               CommunicationHelper communicationHelper1 = new CommunicationHelper();
//               Map<String, String> map1 = new HashMap<>();
//               map1.put(CommunicationConstant.USERNAME, customers.getUsername());
//               map1.put(CommunicationConstant.COMPLAIN_NO, entity.getCaseNumber());
//               map1.put(CommunicationConstant.DESTINATION, customers.getMobile());
//               map1.put(CommunicationConstant.EMAIL, customers.getEmail());
//               communicationHelper1.generateCommunicationDetails(CommunicationConstant.OPEN_COMPLAINT, Collections.singletonList(map1));
//               if (null != entity.getCustomersId()) {
//                   customers = customersService.get(entity.getCustomersId());
//
//                   if (customers != null && !customers.getIsDeleted()) {
//                       TicketReasonCategory ticketReasonCategory = ticketReasonCategoryService.getRepository().findById(entity.getTicketReasonCategoryId()).orElse(null);
//                       TicketReasonCategoryTATMapping ticketReasonCategoryTATMapping = ticketReasonCategory.getTicketReasonCategoryTATMappingList().stream().sorted(Comparator.comparing(TicketReasonCategoryTATMapping::getOrderNumber)).collect(Collectors.toList()).get(0);
//
//                       if (!Objects.equals(entity.getPriority(), "High")) {
//                           entity.setPriority(checkTickePriority(entity, customers));
//                       }
//                       if (ticketReasonCategoryTATMapping != null) {
//                           entity.setTatMappingId(ticketReasonCategoryTATMapping.getMappingId());
//
//                           //Set Prefix
//                           String prefix = "";
//                           if (entity.getCaseType().equalsIgnoreCase(CaseConstants.CASE_TYPE_ISSUE))
//                               prefix = CaseConstants.PREFIX_TKT;
//                           else if (entity.getCaseType().equalsIgnoreCase(CaseConstants.CASE_TYPE_REQUEST))
//                               prefix = CaseConstants.PREFIX_REQ;
//                           else if (entity.getCaseType().equalsIgnoreCase(CaseConstants.CASE_TYPE_INQUIRY))
//                               prefix = CaseConstants.PREFIX_INQ;
//
//
//                           //Set CaseNumber
//                           CaseDTO caseDTO = getCaseByCaseType(entity.getCaseType());
//                           if (null != caseDTO && caseDTO.getCaseNumber() != null) {
//                               String number = caseDTO.getCaseNumber().split("-")[1];
//                               entity.setCaseNumber(prefix + "-" + Integer.parseInt(String.valueOf(Long.parseLong(number) + 1)));
//                           } else entity.setCaseNumber(prefix + "-" + "1");
//
//                           if (entity.getNextFollowupDate() != null && entity.getNextFollowupTime() != null) {
//                               entity.setNextFollowupDate(entity.getNextFollowupDate());
//                               entity.setNextFollowupTime(entity.getNextFollowupTime());
//                               //send message frome here
//                               if (entity.getCreatedFrom() == null) {
//                                   StaffUser staffUser = staffUserRepository.findById(getLoggedInUserId()).orElse(null);
//
//                               }
//                           } else {
//                               switch (ticketReasonCategoryTATMapping.getTimeUnit()) {
//                                   case "Day":
//                                       entity.setNextFollowupDate(LocalDate.now().plusDays(entity.getPriority().equals("High") ? ticketReasonCategoryTATMapping.getEscalatedTime() : ticketReasonCategoryTATMapping.getTime()));
//                                       entity.setNextFollowupTime(LocalTime.now());
//                                       break;
//                                   case "Hour":
//                                       entity.setNextFollowupDate(LocalDate.now());
//                                       entity.setNextFollowupTime(LocalTime.now().plusHours(entity.getPriority().equals("High") ? ticketReasonCategoryTATMapping.getEscalatedTime() : ticketReasonCategoryTATMapping.getTime()));
//                                       break;
//                                   case "Min":
//                                       entity.setNextFollowupDate(LocalDate.now());
//                                       entity.setNextFollowupTime(LocalTime.now().plusMinutes(entity.getPriority().equals("High") ? ticketReasonCategoryTATMapping.getEscalatedTime() : ticketReasonCategoryTATMapping.getTime()));
//                                       break;
//                               }
//                           }
//
//                           PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.TICKET_PATH).get(0).getValue();
//
//                           if (null != caseDTO && file != null && file.size() > 0) {
//                               for (MultipartFile multipartFile : file) {
//                                   CaseDocDetailsDTO caseDoc = new CaseDocDetailsDTO();
////                String subFolderName = aCase.getCaseNumber().trim().replace("-","_") + "/";
//                                   //       PATH = "D:/";
//                                   String path = PATH;
//                                   caseDoc.setTicketId(Math.toIntExact(caseDTO.getCaseId()));
//                                   caseDoc.setDocStatus("Active");
//                                   MultipartFile file1 = fileUtility.getFileFromArrayForTicket(multipartFile);
//                                   if (file1 != null) {
//                                       caseDoc.setUniquename(fileUtility.saveFileToServerForTicket(file1, path));
//                                       caseDoc.setFilename(file1.getOriginalFilename());
//                                       caseDoc = caseDocDetailsService.saveEntity(caseDoc);
//                                   }
//                               }
//
//
//                           }
//
//                           //Set helperName
//                           if (null != caseDTO && caseDTO.getHelperName() != null) {
//                               if (caseDTO.getHelperName().equals("")) {
//                                   entity.setHelperName(caseDTO.getHelperName());
//                               }
//                           }
//                           entity.setCase_order(1L);
//                           entity = super.saveEntity(entity);
//
//
//
//
//
//                           CaseUpdateDTO caseUpdateDTO = setFirstRemarkInUpdate(entity);
//                           StaffUser assignedUser;
//                           Map<Integer, StaffUserPojo> staffByParentStaffId = new HashMap<>();
//
//                           //getting lcoId from the customer
////                           if (getLoggedInUser().getLco() != null && getLoggedInUser().getLco()) {
////                               customers.setLcoId(getLoggedInUser().getPartnerId());
////                           } else {
////                               customers.setLcoId(null);
////                           }
//                          // if(caseUpdateDTO.getTeamHierarchyMappingId() == null /*&& customers.getLcoId()==null*/){
//                               if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
//                                   Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, CommonConstants.HIERARCHY_TYPE, false, true, entity);
//                                   if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
//                                       //   updateTicketLevel(entity,map,null);
//                                       caseUpdateDTO.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
//                                       StaffUser staffUser = staffUserService.get(Integer.valueOf(map.get("staffId")));
//                                       caseUpdateDTO.setAssignee(staffUser.getId());
//                                       assignedUser = staffUser;
//                                       caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
//                                       TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Integer.valueOf(map.get("nextTatMappingId"))).orElse(null);
//                                       Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
//                                       String nextFollowupDate = entity.getNextFollowupDate().toString();
//                                       String nextFollowupTime = entity.getNextFollowupTime().toString();
//                                       caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), teams.getName(), nextFollowupDate, customers.getUsername(), nextFollowupTime,entity.getCustomerAdditionalEmail(),entity.getSerialNumber());
//                                       if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
//                                           tatUtils.saveOrUpdateTicketTatMatrix(entity, map, assignedUser, false);
//                                       }
//                                       String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + entity.getCaseNumber() + " ' " + "for " + "customer name : " + " ' " + entity.getCustomerName() + " '";
//                                       hierarchyService.sendWorkflowAssignActionMessage(staffUser.getCountryCode(), staffUser.getPhone(), staffUser.getEmail(), staffUser.getMvnoId(), staffUser.getFullName(), action);
//
//                                       workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
//                                   } else {
//                                       StaffUser staffUser = staffUserService.get(getLoggedInUserId());
//                                       caseUpdateDTO.setAssignee(getLoggedInUserId());
//                                       caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
//                                       caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(getLoggedInUserId()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(),entity.getCustomerAdditionalEmail(),entity.getSerialNumber());
//                                       workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
//
//                                   }
//                               }
//                               else {
//                                   Map<String, Object> map = hierarchyService.getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, CommonConstants.HIERARCHY_TYPE, false, true, entity);
//                                   if (map.containsKey("assignableStaff")) {
//                                       //updateTicketLevel(entity,null,map);
//                                       List<StaffUserPojo> staffUserPojos = (List<StaffUserPojo>) map.get("assignableStaff");
//                                       //caseUpdateDTO.setCase_order(caseUpdateDTO.getCase_order()+1);
//                                       caseUpdateDTO.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTeamHierarchyMappingId").toString()));
//                                       for (int i = 0; i < staffUserPojos.size(); i++) {
//                                           TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
//                                           ticketAssignStaffMapping.setStaffId(staffUserPojos.get(i).getId());
//                                           ticketAssignStaffMapping.setTicketId(entity.getCaseId());
//                                           ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
//                                           workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) map.get("eventId") : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUserPojos.get(i).getId(), staffUserPojos.get(i).getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUserPojos.get(i).getUsername());
//                                           if (staffUserPojos.get(i).getParentStaffId() != null) {
//                                               staffByParentStaffId.put(staffUserPojos.get(i).getParentStaffId(), staffUserPojos.get(i));
//                                           }
//                                           String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + entity.getCaseNumber() + " ' " + "for " + "customer name : " + " ' " + customers.getUsername() + " '";
//                                           hierarchyService.sendWorkflowAssignActionMessage(staffUserPojos.get(i).getCountryCode(), staffUserPojos.get(i).getPhone(), staffUserPojos.get(i).getEmail(), staffUserPojos.get(i).getMvnoId(), staffUserPojos.get(i).getFullName(), action);
//                                       }
//                                   }
//                                   else {
//                                       StaffUser staffUserPojo;
//                                       if(ticketCreatedFrom!=null){
//                                               staffUserPojo = staffUserService.get(customers.getCreatedById());
//                                       }
//                                       else{
//                                           staffUserPojo = staffUserService.get(getLoggedInUserId());
//                                       }
//                                       TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
//                                       ticketAssignStaffMapping.setStaffId(staffUserPojo.getId());
//                                       ticketAssignStaffMapping.setTicketId(entity.getCaseId());
//                                       ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
//                                       workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) map.get("eventId") : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUserPojo.getId(), staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUserPojo.getUsername());
//                                       String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + entity.getCaseNumber() + " ' " + "for " + "customer name : " + " ' " + customers.getUsername() + " '";
//                                       hierarchyService.sendWorkflowAssignActionMessage(staffUserPojo.getCountryCode(), staffUserPojo.getPhone(), staffUserPojo.getEmail(), staffUserPojo.getMvnoId(), staffUserPojo.getFullName(), action);
//                                   }
//                                   caseUpdateDTO.setAssignee(null);
//                                   caseUpdateDTO.setStatus(entity.getCaseStatus());
//                                   if (ticketCreatedFrom == null) {
//                                       caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(getLoggedInUserId()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(),entity.getCustomerAdditionalEmail(),entity.getSerialNumber());
//                                   }else if (ticketCreatedFrom.equalsIgnoreCase("CWSC")) {
//                                       caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(customers.getCreatedById()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(),entity.getCustomerAdditionalEmail(),entity.getSerialNumber());
//                                   }
//                                   //caseUpdateService.sendCreateTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(),caseDTO.getCaseNumber());
//                               }
//                         //  }
////                           else{
////                               StaffUser staffUser = staffUserService.get(getLoggedInUserId());
////                               caseUpdateDTO.setAssignee(getLoggedInUserId());
////                               caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
////                               TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
////                               ticketAssignStaffMapping.setStaffId(getLoggedInUserId());
////                               ticketAssignStaffMapping.setTicketId(entity.getCaseId());
////                               ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
////                               caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber(), staffUserService.get(getLoggedInUserId()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString());
////                               //workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(entity.getCaseId()), entity.getCaseNumber(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
////                           }
//
//
//                            TicketTatMatrix tatMatrix = getTicketTatMatrixFromSubReasonId(entity);
//                               if(tatMatrix!=null){
//                                   if(entity.getPriority().equalsIgnoreCase("high")){
//                                       caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp1());
//                                       caseUpdateDTO.setCaseSlaTime(Math.toIntExact(tatMatrix.getSlaTimep1()));
//                                   }else if(entity.getPriority().equalsIgnoreCase("medium")){
//                                       caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp2());
//                                       caseUpdateDTO.setCaseSlaTime(Math.toIntExact(tatMatrix.getSlaTimep2()));
//                                   }else{
//                                       caseUpdateDTO.setCaseSlaUnit(tatMatrix.getSunitp3());
//                                       caseUpdateDTO.setCaseSlaTime(Math.toIntExact(tatMatrix.getSlaTime3()));
//                                   }
//                               }
//                           entity = caseUpdateService.updateEntity(caseUpdateDTO, file, false);
//                           getCaseDataFromStrig(entity);
//
//                           if (staffByParentStaffId != null && !staffByParentStaffId.isEmpty()) {
//                               for (Map.Entry<Integer, StaffUserPojo> staffMap : staffByParentStaffId.entrySet()) {
//                                   StaffUser staffUser = staffUserMapper.dtoToDomain(staffMap.getValue(), new CycleAvoidingMappingContext());
//                                   tatUtils.changeTicketTatAssignee(entity, staffUser, true, false);
//                               }
//                           }
//
//                           //updating followup date and time based on tat which selected based on condition check
//                           if (!entity.getCaseStatus().equalsIgnoreCase("Follow Up")) {
//                               updateFollowUpDateAndTimeForTicketBeforePickedUp(caseMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
//                           } else {
//                               caseUpdateService.updateTatAtStatusChangeToFollowUp(null, entity);
//                               if (ticketCreatedFrom == null) {
//                                   caseUpdateService.sendAssignTicketMessege(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getMvnoId(), entity.getCaseNumber().toString(), staffUserService.get(getLoggedInUserId()).getFullName(), entity.getNextFollowupDate().toString(), customers.getUsername(), entity.getNextFollowupTime().toString(),entity.getCustomerAdditionalEmail(),entity.getSerialNumber());
//                               }
//                           }
//
//                       }
////                    if (!ticketCreatedFrom.isEmpty() && ticketCreatedFrom.equalsIgnoreCase("CWSC")) {
////                        Case aCase = caseRepository.findById(entity.getCaseId()).orElse(null);
////                        aCase.setMvnoId(customers.getMvnoId());
////                        aCase.setBuId(customers.getBuId());
////                        caseRepository.save(aCase);
////                    }
//                   } else {
//                       throw new DataNotFoundException("Customer Not Found!");
//                   }
//               } else {
//                   throw new DataNotFoundException("Customer is not found by given id : " + entity.getCustomersId());
//               }
//           }else{
//               throw new CustomValidationException(APIConstants.FAIL, "Ticket for this Problem Domain and Sub Problem Domain and Service is already Exist! ", null);
//               //Save Entry In Assignment Table
//           }
//        } catch (Exception exception) {
//            exception.printStackTrace();
//            throw new CustomValidationException(APIConstants.FAIL, exception.getMessage(), null);
//        }
//
//        return entity;
//    }

//    public void updateTicketLevel(CaseDTO entity, Map<String, Object > map) {
//        QCase qCase=QCase.case$;
//        //boolean flag=false;
//        HashMap<String, String> convertedMap = new HashMap<>();
//        for (Map.Entry<String, Object> entry : map.entrySet()) {
//            Object value = entry.getValue();
//
//            // Add the converted value to the new Map
//            convertedMap.put(entry.getKey(), entry.getValue().toString());
//        }
//
//        StaffUser staffUser=staffUserRepository.findById(getLoggedInUserId()).orElse(null);
//        int lastDayscount = Integer.parseInt(clientService.getByName(CommonConstants.TICKET_COUNT).getValue());
//        BooleanExpression booleanExpression=qCase.isNotNull().and(qCase.customers.id.eq(entity.getCustomersId())).and(qCase.ticketReasonCategoryId.eq(entity.getTicketReasonCategoryId()))
//                .and(qCase.caseStatus.eq(CaseConstants.STATUS_CLOSED).or(qCase.caseStatus.eq(CaseConstants.STATUS_RESOLVED)));
//        List<Case>caseList= IterableUtils.toList(caseRepository.findAll(booleanExpression));
//        if(caseList.size()>=lastDayscount){
//            //flag=true;
//            if(staffUser.getStaffUserparent().getId()!=null){
//                if(!convertedMap.isEmpty()){
//                    TicketTatMatrix ticketTatMatrix = caseService.getTicketTatMatrixFromSubReasonId(entity);
//                    if(ticketTatMatrix!=null){
//                        List<TicketTatMatrixMapping> tatMatrixMappings = ticketTatMatrix.getTatMatrixMappings();
//                        Case newcase = caseMapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
//                        for (int i = 0; i < tatMatrixMappings.size(); i++){
//                            if (tatMatrixMappings.get(i).getOrderNo() == 2 ) {
//                                Integer Nextvalue = Integer.parseInt(String.valueOf(tatMatrixMappings.get(i).getMtime1()));
//                                if (entity.getPriority().equalsIgnoreCase("high") ) {
//                                    new TatMatrixWorkFlowDetails(new Long(2), "Level 2", getLoggedInUserId(),
//                                            Long.valueOf(convertedMap.get("workFlowId")), null,
//                                            staffUser.getStaffUserparent().getId(), LocalDateTime.now(),
//                                            String.valueOf(tatMatrixMappings.get(i).getMtime1()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null,
//                                            entity.getCaseId().intValue(), convertedMap.get("eventName").toString(),  Integer.valueOf(convertedMap.get("eventId").toString()),
//                                            CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf( convertedMap.get("teamId")),true);
//                                    caseRepository.save(tatUtils.UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
//                                } else if (entity.getPriority().equalsIgnoreCase("medium")) {
//                                    new TatMatrixWorkFlowDetails(new Long(2), "Level 2", getLoggedInUserId(),
//                                            Long.valueOf(convertedMap.get("workFlowId")), null,
//                                            staffUser.getStaffUserparent().getId(), LocalDateTime.now(),
//                                            String.valueOf(tatMatrixMappings.get(i).getMtime2()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null,
//                                            entity.getCaseId().intValue(), convertedMap.get("eventName").toString(), Integer.valueOf( convertedMap.get("eventId")),
//                                            CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf( convertedMap.get("teamId")),true);
//                                    caseRepository.save(tatUtils.UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
//                                } else {
//                                    new TatMatrixWorkFlowDetails(new Long(2), "Level 2", getLoggedInUserId(),
//                                            Long.valueOf(convertedMap.get("workFlowId")), null,
//                                            staffUser.getStaffUserparent().getId(), LocalDateTime.now(),
//                                            String.valueOf(tatMatrixMappings.get(i).getMtime3()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null,
//                                            entity.getCaseId().intValue(), convertedMap.get("eventName").toString(), Integer.valueOf(convertedMap.get("eventId")),
//                                            CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf(convertedMap.get("teamId")),true);
//                                    caseRepository.save(tatUtils.UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
//                                }
//                            }
//                        }
//                    }
////                TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
////                        new TatMatrixWorkFlowDetails(new Long(1), "Level 2", getLoggedInUserId(),
////                                Long.valueOf(map.get("workFlowId")), null,
////                                staffUser.getStaffUserparent().getId(), LocalDateTime.now(),
////                                String.valueOf(masterTicketTat.getRtime()), masterTicketTat.getRunit(), masterTicketTat.getTatMatrixMappings().get(0).getAction(), true, null,
////                                entity.getCaseId().intValue(), map.get("eventName"), Integer.valueOf(map.get("eventId")),
////                                CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf(map.get("teamId")),true);
////                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
//                }
//            }
//
//        }
//
//    }
//    private boolean duplicateVerifyDomainAtSave(CaseDTO entity) {
//       QCase qCase=QCase.case$;
//       boolean flag=false;
//       BooleanExpression booleanExpression=qCase.isNotNull().and(qCase.customers.id.eq(entity.getCustomersId())).and(qCase.reasonSubCategoryId.eq(entity.getReasonSubCategoryId()))
//               .and(qCase.ticketReasonCategoryId.eq(entity.getTicketReasonCategoryId())).and(qCase.caseStatus.notEqualsIgnoreCase("Resolved")).and(qCase.caseStatus.notEqualsIgnoreCase("Raise And Close"))
//                       .and(qCase.caseStatus.notEqualsIgnoreCase("Raise And Close")).and(qCase.caseStatus.notEqualsIgnoreCase("Closed")).and(qCase.caseStatus.notEqualsIgnoreCase("Rejected"));
//       List<Case>caseList=IterableUtils.toList(caseRepository.findAll(booleanExpression));
//      for(Case casedata:caseList){
//          if(casedata.getTicketServicemappingList().get(0).getServiceid().equals(entity.getTicketServicemappingList().get(0).getServiceid())){
//              flag=true;
//          }
//      }
//        return flag;
//    }
//
//
//    public CaseDTO getCaseByCaseType(String caseType) {
//        QCase qCase = QCase.case$;
//        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.caseType.eq(caseType));
//        JPAQuery<Case> caseJPAQuery = new JPAQuery<>(entityManager);
//        List<Case> caseList = caseJPAQuery.select(qCase).from(qCase).where(booleanExpression).limit(1).orderBy(qCase.caseId.desc()).fetch();
//
////        List<Case> caseList = caseRepository.findCaseByCaseTypeAndIsDeleteIsFalseOrderByCaseIdDesc(caseType).
////                stream()
////                .filter(cases ->
////                        (cases.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || cases.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1)
////                                && (cases.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(cases.getBuId())))
////
////                .collect(Collectors.toList());
//        if (null != caseList && 0 < caseList.size()) {
//            return caseMapper.domainToDTO(caseList.get(0), new CycleAvoidingMappingContext());
//        }
//        return null;
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[CaseService]";
//    }
//
//    public List<CaseDTO> getAllCaseByStaff(Integer loggedInUser) {
//        List<Case> caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDesc(loggedInUser);
//        if (getMvnoIdFromCurrentStaff() != 1)
//            caseList = caseList.stream().filter(cases -> cases.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || cases.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() && (cases.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(cases.getBuId()))).collect(Collectors.toList());
//        if (null != caseList && 0 < caseList.size()) {
//            return caseList.stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//        }
//        return new ArrayList<>();
//    }
//
//    public GenericDataDTO getAllCaseByStaffWithPagination(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String SUBMODULE = getModuleNameForLog() + "[getAllCaseByStaffWithPagination()]";
//        PageRequest pageRequest;
//        Page<Case> caseList;
//        try {
//            pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            if (getMvnoIdFromCurrentStaff() == 1)
//                caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(getLoggedInUserId(), pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            else if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
//                caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(getLoggedInUserId(), pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            else
//                caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(getLoggedInUserId(), pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
//
//
//            if (null != caseList && 0 < caseList.getSize()) {
//                makeGenericResponse(genericDataDTO, caseList);
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return genericDataDTO;
//    }
//
//    public List<CaseDTO> getAllCaseByStatus(String caseStatus) {
//        List<Case> caseList = caseRepository.findAllByCaseStatusAndIsDeleteIsFalseOrderByCaseIdDesc(caseStatus);
//        if (null != caseList && 0 < caseList.size()) {
//            return caseList.stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//        }
//        return new ArrayList<>();
//    }
//
//    public GenericDataDTO getAllCaseByStatusWithPagination(String caseStatus, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String SUBMODULE = getModuleNameForLog() + "[getAllCaseByStatusWithPagination()]";
//        PageRequest pageRequest;
//        Page<Case> caseList;
//        try {
//            pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            if (getMvnoIdFromCurrentStaff() == 1)
//                caseList = caseRepository.findAllByCaseStatusAndIsDeleteIsFalseOrderByCaseIdDesc(caseStatus, pageRequest);
//
//            else if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
//                caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(getLoggedInUserId(), pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            else
//                caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(getLoggedInUserId(), pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
//
//            if (null != caseList && 0 < caseList.getSize()) {
//                makeGenericResponse(genericDataDTO, caseList);
//            }
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return genericDataDTO;
//    }
//
//    public GenericDataDTO getAllCaseByStatusAndMyCasesWithPagination(String caseStatus, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String SUBMODULE = getModuleNameForLog() + "[getAllCaseByStatusAndMyCasesWithPagination()]";
//        PageRequest pageRequest;
//        Page<Case> caseList;
//        try {
//            pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            caseList = caseRepository.findAllByCurrentAssignee_IdAndCaseStatusAndIsDeleteIsFalse(getLoggedInUserId(), caseStatus, pageRequest);
//
//            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
//                caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDesc(getLoggedInUserId(), pageRequest);
//            else
//                caseList = caseRepository.findAllByCurrentAssignee_IdAndIsDeleteIsFalseOrderByCaseIdDescAndMvnoIdIn(getLoggedInUserId(), pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
//
//             caseList.get().forEach(item -> {
//                 if (item.getCurrentAssignee() != null && item.getCurrentAssignee().getStaffUserparent() != null) {
//                     item.setParentId(item.getCurrentAssignee().getStaffUserparent().getId().longValue());
//                 }
//             });
//
//            if (getMvnoIdFromCurrentStaff() != 1) if (null != caseList && 0 < caseList.getSize()) {
//                makeGenericResponse(genericDataDTO, caseList);
//            }
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return genericDataDTO;
//    }
//
//    public List<CaseDTO> getAllCaseByCustomer(Integer custId) {
//        List<Case> caseList = caseRepository.findAllByCustomers_IdAndIsDeleteIsFalseOrderByCaseIdDesc(custId);
//        if (getMvnoIdFromCurrentStaff() != 1)
//            caseList = caseList.stream().filter(cases -> cases.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || cases.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() && (cases.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(cases.getBuId()))).collect(Collectors.toList());
//        if (null != caseList && 0 < caseList.size()) {
//            return caseList.stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//        }
//        return new ArrayList<>();
//    }

//    public Page<Case> searchCaseBYCaseType(List<GenericSearchModel> filterList, Integer page,
//                                           Integer pageSize, String sortBy, Integer sortOrder
//    ) {
//        String SUBMODULE = MODULE + " [search()] ";
//        Pageable pageable = generatePageRequest(page, pageSize, "createdate", CommonConstants.SORT_ORDER_DESC);
////        StringBuilder commonQuery = new StringBuilder(SubscriberSearchQueryScript.COMMON_QUERY);
//        StringBuilder whereCondition = new StringBuilder("");
//        StringBuilder join = new StringBuilder("");
//        String finalQuery = "";
//
//        QCase qCase = QCase.case$;
//
//        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.isDelete.eq(false));
//        if (Objects.nonNull(qCase.serviceAreaName) &&
//                Objects.nonNull(qCase.case "parentCategoryName":
//                        booleanExpression = booleanExpression.and(qTicketReasonSubCategory.parentCategory.categoryName.containsIgnoreCase(genericSearchModel.getFilterValue()));
//                        break;) &&
//                Objects.nonNull(qCase.caseStatus)) {
//        }
//
//        try {
//            for (GenericSearchModel searchModel : filterList) {
//                if (getLoggedInUserId() != 1) {
//                    List<java.lang.Long> idList = getServiceAreaIdList();
//                    if (!CollectionUtils.isEmpty(idList))
//                        booleanExpression = booleanExpression.and(qCase.caseId.in(idList));
//                }
//                if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
//                    booleanExpression = booleanExpression.and(qCase.partner.id.eq(getLoggedInUserPartnerId()));
//                }
//
//                if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                    if (!searchModel.getFilterValue().isEmpty()) {
//                        String s1 = searchModel.getFilterValue();
////                        booleanExpression = booleanExpression
////                                .and(((qCase.serviceAreaName.like("%" + s1 + "%"))
////                                .and(qCase.caseStatus.contains(searchModel.getFilterValue()))
////                                .and(qCase.ticketReasonCategoryId.in(Long.valueOf(searchModel.getFilterValue())))));
//
//                        booleanExpression = booleanExpression
//                                .and(((qCase.customers.servicearea.id.in(Long.valueOf(searchModel.getFilterValue())))
//                                        //   .and(qCase.caseStatus.contains(searchModel.getFilterValue()))
//                                        //  .and(qCase.ticketReasonCategoryId.in(Long.valueOf(searchModel.getFilterValue())))
//                                ));
//
//
//                        if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
//                            booleanExpression = booleanExpression.and(qCase.partner.id.eq(getLoggedInUserPartnerId()));
//                            booleanExpression = booleanExpression.or(qCase.partner.name.eq(s1));
//                        }
//                        return caseRepository.findAll(booleanExpression, pageRequest);
//                    }
//                }
//                try {
//                    if (null != searchModel.getFilterCondition()) {
//                        if (searchModel.getFilterCondition().equalsIgnoreCase(SearchConstants.AND)) {
//                            return caseRepository.findAll(generateAndCondition(searchModel, booleanExpression), pageable);
//                        } else {
//                            generateOrCondition(searchModel, whereCondition, join);
//                        }
//                    } else {
//                        return caseRepository.findAll(generateAndCondition(searchModel, booleanExpression), pageable);
//                    }
//                } catch (Exception ex) {
//
//                }
//            }
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error("Unable to search  case by type response{}exceeption{}", APIConstants.FAIL, ex.getStackTrace());
//            throw ex;
//        }
//        return null;
//    }


    //search


 //   @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
////        QTicketReasonSubCategory qTicketReasonSubCategory = QTicketReasonSubCategory.ticketReasonSubCategory;
////        BooleanExpression booleanExpression = qTicketReasonSubCategory.isNotNull().and(qTicketReasonSubCategory.isDeleted.eq(false));
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        QCase qCase = QCase.case$;
//        QTeams qTeams = QTeams.teams;
//        BooleanExpression booleanExpression = QCase.case$.isNotNull().and(qCase.isDelete.eq(false));
//        if (filterList.size() > 0) {
//            for (GenericSearchModel genericSearchModel : filterList) {
//                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.CUSTOMER_USERNAME)) {
//                    booleanExpression = booleanExpression.and(qCase.customers.username.containsIgnoreCase(genericSearchModel.getFilterValue()));
//                }
//                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.TICKET_STATUS)) {
//                    booleanExpression = booleanExpression.and(qCase.caseStatus.containsIgnoreCase(genericSearchModel.getFilterValue()));
//                }
//                if (genericSearchModel.getFilterColumn().equalsIgnoreCase(CommonConstants.TICKET_SEARCH_OPTION.TICKET_PRIORITY)) {
//                    booleanExpression = booleanExpression.and(qCase.priority.containsIgnoreCase(genericSearchModel.getFilterValue()));
//                }
//                if (genericSearchModel.getFilterColumn().equalsIgnoreCase(CommonConstants.TICKET_SEARCH_OPTION.ASSIGNED_TEAM)) {
//                    List<Teams> teams = teamsRepository.findAllByNameContainingIgnoreCase(genericSearchModel.getFilterValue());
//                    List<StaffUser> staffUsers = new ArrayList<>();
//                    if (teams.size() > 0) {
//                        staffUsers = teams.stream().flatMap(t -> t.getStaffUser().stream()).collect(Collectors.toList());
//                    }
//                    booleanExpression = booleanExpression.and(qCase.currentAssignee.in(staffUsers));
//                }
//                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.CUSTOMER_SERVICE_AREA)) {
//                    booleanExpression = booleanExpression.and(qCase.customers.servicearea.name.containsIgnoreCase(genericSearchModel.getFilterValue()));
//                }
//                if (genericSearchModel.getFilterColumn().equalsIgnoreCase(CommonConstants.TICKET_SEARCH_OPTION.TICKET_NUMBER)) {
//                    booleanExpression = booleanExpression.and(qCase.caseNumber.containsIgnoreCase(genericSearchModel.getFilterValue()));
//                }
//                if (genericSearchModel.getFilterColumn().equalsIgnoreCase(CommonConstants.TICKET_SEARCH_OPTION.TICKET_PROBLEM_DOMAIN)) {
//                    List<TicketReasonCategory> ticketReasonCategories = ticketReasonCategoryRepo.findAllByCategoryNameContainingIgnoreCase(genericSearchModel.getFilterValue());
//                    List<Long> longArrayList = new ArrayList<>();
//                    if (ticketReasonCategories.size() > 0) {
//                        longArrayList = ticketReasonCategories.stream().map(t -> t.getId()).collect(Collectors.toList());
//                    }
//                    booleanExpression = booleanExpression.and(qCase.ticketReasonCategoryId.in(longArrayList));
//                }
//                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.USER_ID)) {
//                    booleanExpression = booleanExpression.and(qCase.customers.id.eq(Integer.valueOf(genericSearchModel.getFilterValue())));
//                }
//                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.TICKET_LEVEL)) {
//                    List<TatMatrixWorkFlowDetails> ldlist = tatMatrixWorkFlowDetailsRepo.findAllByLevelAndIsActive(genericSearchModel.getFilterValue(), true);
//                    List<Long> idlist1 = ldlist.stream().map(TatMatrixWorkFlowDetails::getEntityId).map(integer -> integer.longValue()).collect(Collectors.toList());
//                    booleanExpression = booleanExpression.and(qCase.caseId.in((idlist1)));
//                }
//                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.RESPONSE_TIME_BREACH)) {
//                    List<Long> caselist = tatMatrixWorkFlowDetailsRepo.getAlltatBreachdetails();
//                    booleanExpression = booleanExpression.and(qCase.caseId.in(caselist));
//                }
//
//                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.TAT_BREATCH)) {
//                    BooleanExpression booleanExpression1 = qCase.isNotNull().and(qCase.caseStatus.notEqualsIgnoreCase("Closed"));
//                    List<Case> caseList = IterableUtils.toList(caseRepository.findAll(booleanExpression1));
//                    List<Long> expiredlist = new ArrayList<>();
//                    List<Long> caseidlist = caseList.stream().map(caselist2 -> caselist2.getCaseId()).collect(Collectors.toList());
//                    if (caseidlist.size() > 0) {
//                        for (Case idlist : caseList) {
//                            TicketReasonSubCategory ticketSubReasonCategory = ticketReasonSubCategoryRepo.findById(idlist.getCaseId()).orElse(null);
//                            if (ticketSubReasonCategory != null) {
//                                ticketSubReasonCategory.getTicketSubCategoryTatMappingList();
//                                if (!ticketSubReasonCategory.getTicketSubCategoryTatMappingList().isEmpty()) {
//                                    List<TicketSubCategoryTatMapping> ticketSubCategoryTatMappings = ticketSubReasonCategory.getTicketSubCategoryTatMappingList();
//                                    for (TicketSubCategoryTatMapping ticketSubCategoryTatMapping : ticketSubCategoryTatMappings) {
////                                            QTatQueryFieldMapping qTatQueryFieldMapping = QTatQueryFieldMapping.tatQueryFieldMapping;
////                                            BooleanExpression exp = qTatQueryFieldMapping.isNotNull().and(qTatQueryFieldMapping.tatMappingId.eq(ticketSubCategoryTatMapping.getId().intValue()));
////                                            List<TatQueryFieldMapping> tatQueryFieldMappingList = (List<TatQueryFieldMapping>) tatQueryFieldMappingRepo.findAll(exp);
////                                            if (!CollectionUtils.isEmpty(tatQueryFieldMappingList)) {
////                                                //If query not matched then skip
////                                                if (!tatUtils.checkTicketTatCondition(tatQueryFieldMappingList, caseMapper.domainToDTO(idlist, new CycleAvoidingMappingContext())))
////                                                    continue;
////                                            }
//                                        TicketTatMatrix masterTicketTat = ticketSubCategoryTatMapping.getTicketTatMatrix();
//                                        Long rtime = masterTicketTat.getRtime();
//                                        String runit = masterTicketTat.getRunit();
//                                        if (!idlist.getCaseStatus().equalsIgnoreCase("Closed")) {
//                                            if (runit.equalsIgnoreCase("Day")) {
//                                                if ((idlist.getCreatedate().plusDays(rtime).compareTo(LocalDateTime.now())) > 0) {
//                                                    expiredlist.add(idlist.getCaseId());
//                                                }
//                                            } else if (runit.equalsIgnoreCase("Hour")) {
//                                                if ((idlist.getNextFollowupTime().plusHours(rtime).compareTo(LocalTime.now())) > 0) {
//                                                    expiredlist.add(idlist.getCaseId());
//                                                }
//                                            } else {
//                                                if ((idlist.getNextFollowupTime().plusMinutes(rtime).compareTo(LocalTime.now())) > 0) {
//                                                    expiredlist.add(idlist.getCaseId());
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                }
//                            }
//
//
//                        }
//
//                    }
//                    booleanExpression = booleanExpression.and(qCase.caseId.in(expiredlist));
//
//                }
//                if (genericSearchModel.getFilterColumn().equals(CommonConstants.TICKET_SEARCH_OPTION.MY_TICKETS)) {
//                    booleanExpression = booleanExpression.and(qCase.createdById.eq(getLoggedInUserId()));
//                }
//            }
//        }
//        if (getMvnoIdFromCurrentStaff() != 1)
//            booleanExpression = booleanExpression.and(qCase.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
//        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
//            booleanExpression = booleanExpression.and(qCase.mvnoId.eq(1).or(qCase.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qCase.buId.in(getBUIdsFromCurrentStaff()))));
//        }
//
//        if (getLoggedInUser().getLco())
//            booleanExpression = booleanExpression.and(qCase.lcoId.eq(getLoggedInUser().getPartnerId()));
//        else
//            booleanExpression = booleanExpression.and(qCase.lcoId.isNull());
//
//        return makeGenericResponse(genericDataDTO, caseRepository.findAll(booleanExpression, pageRequest));
//    }

//  public void getserviceAreaByCust(List<Case> caseList){
//    }

//    private BooleanExpression generateAndCondition(GenericSearchModel searchModel, BooleanExpression booleanExpression) {
//        return booleanExpression;
//    }
//
//    public CaseDTO assignedTo(Long caseId) throws Exception {
//        String SUBMODULE = getModuleNameForLog() + " [assignedTo()] ";
//        try {
//            CaseDTO caseDTO = getEntityForUpdateAndDelete(caseId);
//            if (null != caseDTO) {
//                StaffUser staffUser = staffUserService.get(getLoggedInUserId());
//                if (null != staffUser) {
//                    //Update Case
//                    caseDTO.setCurrentAssigneeId(staffUser.getId());
//                    caseDTO.setCaseStatus(CaseConstants.STATUS_ASSIGNED);
//
//                    //Initialize CaseUpdate
//                    CaseUpdateDTO caseUpdate = new CaseUpdateDTO();
//                    List<CaseUpdateDetailsDTO> updateDetailsList = new ArrayList<>();
//
//                    //Set UpdateDetails For Assignee
//                    CaseUpdateDetailsDTO assigneeDetails = new CaseUpdateDetailsDTO();
//                    assigneeDetails.setOperation(CaseConstants.OPERATION_CHANGE_ASSIGNEE);
//                    assigneeDetails.setNewvalue(staffUser.getFirstname() + " " + staffUser.getLastname());
//                    assigneeDetails.setEntitytype(CaseConstants.ENTITY_ASSIGNEE);
//                    assigneeDetails.setCaseUpdate(caseUpdate);
//
//                    //Set UpdateDetails For Status
//                    CaseUpdateDetailsDTO changeStatus = new CaseUpdateDetailsDTO();
//                    changeStatus.setOperation(CaseConstants.OPERATION_CHANGE_STATUS);
//                    changeStatus.setNewvalue(CaseConstants.STATUS_ASSIGNED);
//                    changeStatus.setEntitytype(CaseConstants.ENTITY_STATUS);
//                    changeStatus.setCaseUpdate(caseUpdate);
//
//                    updateDetailsList.add(assigneeDetails);
//                    updateDetailsList.add(changeStatus);
//
//                    caseUpdate.setTicketId(caseDTO.getCaseId());
//                    caseUpdate.setCreateby(staffUser.getFullName());
//                    caseUpdate.setUpdateby(staffUser.getFullName());
//                    caseUpdate.setUpdateDetails(updateDetailsList);
//                    caseDTO.getCaseUpdateList().add(caseUpdate);
//
//                    CaseDTO updatedDTO = updateEntity(caseDTO);
//                    assignmentService.saveEntity(new CaseAssignmentDTO(updatedDTO.getCaseId(), staffUser.getId(), LocalDate.now()));
//                    return updatedDTO;
//                } else {
//                    throw new DataNotFoundException("Assignee Not Found!");
//                }
//            } else throw new DataNotFoundException("Case Not Found!");
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    @Override
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        PageRequest pageRequest;
//        Page<Case> paginationList = null;
//
//        try {
//            if (sortBy == null || "".equals(sortBy) || "id".equals(sortBy)) {
//                sortBy = "caseId";
//            }
//            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//            QCase qCase = QCase.case$;
//            BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.isDelete.eq(false));
//            if (getLoggedInUserId() != 1) {
//                booleanExpression = booleanExpression.and(qCase.customers.servicearea.id.in(getServiceAreaIdList()));
//            }
//            if (getMvnoIdFromCurrentStaff() != 1)
//                booleanExpression = booleanExpression.and(qCase.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
//                booleanExpression = booleanExpression.and(qCase.mvnoId.eq(1).or(qCase.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qCase.buId.in(getBUIdsFromCurrentStaff()))));
//            }
//
//            if (getLoggedInUser().getLco())
//                booleanExpression = booleanExpression.and(qCase.lcoId.eq(getLoggedInUser().getPartnerId()));
//            else
//                booleanExpression = booleanExpression.and(qCase.lcoId.isNull());
//
//            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//                paginationList = caseRepository.findAll(booleanExpression, pageRequest);
//            } else {
//                booleanExpression = booleanExpression.and(qCase.partner.id.eq(getLoggedInUserPartnerId()));
//                paginationList = caseRepository.findAll(booleanExpression, pageRequest);
//            }
//            paginationList.get().forEach(item -> {
//                if (item.getCurrentAssignee() != null && item.getCurrentAssignee().getStaffUserparent() != null) {
//                    item.setParentId(item.getCurrentAssignee().getStaffUserparent().getId().longValue());
//                }
//            });
//
//            if (null != paginationList && 0 < paginationList.getSize()) {
//                makeGenericResponse(genericDataDTO, paginationList);
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return genericDataDTO;
//    }
//
//    @Override
//    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
//        Sheet sheet = workbook.createSheet("Cases");
//        createExcel(workbook, sheet, CaseDTO.class, getFields());
//    }
//
//    public void excelGenerateForMyCases(Workbook workbook) throws Exception {
//        Sheet sheet = workbook.createSheet("MyCases");
//        new ExcelUtil<CaseDTO>().generateExcel(workbook, sheet, CaseDTO.class, getAllCaseByStaff(getLoggedInUserId()), getFields());
//    }
//
//    public void pdfGenerateForMyCases(Document doc) throws Exception {
//        new PdfUtil<CaseDTO>().generatePdf(doc, CaseDTO.class, getAllCaseByStaff(getLoggedInUserId()), getFields());
//    }
//
//    @Override
//    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
//        createPDF(doc, CaseDTO.class, getFields());
//    }
//
//    private Field[] getFields() throws NoSuchFieldException {
//        return new Field[]{CaseDTO.class.getDeclaredField("caseId"), CaseDTO.class.getDeclaredField("caseNumber"), CaseDTO.class.getDeclaredField("caseStartedOnString"), CaseDTO.class.getDeclaredField("caseFor"), CaseDTO.class.getDeclaredField("caseStatus"), CaseDTO.class.getDeclaredField("caseTitle"), CaseDTO.class.getDeclaredField("caseType"), CaseDTO.class.getDeclaredField("caseOrigin"), CaseDTO.class.getDeclaredField("priority"), CaseDTO.class.getDeclaredField("userName"), CaseDTO.class.getDeclaredField("customerName"), CaseDTO.class.getDeclaredField("mobile"), CaseDTO.class.getDeclaredField("currentAssigneeName"), CaseDTO.class.getDeclaredField("partnerName")};
//    }

    //   @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Pageable pageable = generatePageRequest(page, pageSize, "caseId", sortOrder);
//        StringBuilder commonQuery = new StringBuilder(CaseSearchQueryScript.COMMON_QUERY);
//        StringBuilder whereCondition = new StringBuilder("");
//        StringBuilder join = new StringBuilder("");
//        String finalQuery = "";
//        try {
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        QCase qCase = QCase.case$;
//                        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.isDelete.eq(false));
//                        BooleanExpression booleanExpression1 = qCase.isNotNull().and(qCase.isDelete.eq(false));
//                        if (getLoggedInUserId() != 1) {
//                            booleanExpression = booleanExpression.and(qCase.customers.servicearea.id.in(getServiceAreaIdList()));
//                            booleanExpression1 = booleanExpression1.and(qCase.customers.servicearea.id.in(getServiceAreaIdList()));
//
//                        }
//                        if (getMvnoIdFromCurrentStaff() != 1) {
//                            booleanExpression = booleanExpression.and(qCase.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                            booleanExpression1 = booleanExpression1.and(qCase.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                        }
//                        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
//                            booleanExpression = booleanExpression.and(qCase.mvnoId.eq(1).or(qCase.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qCase.buId.in(getBUIdsFromCurrentStaff()))));
//                            booleanExpression1 = booleanExpression1.and(qCase.mvnoId.eq(1).or(qCase.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qCase.buId.in(getBUIdsFromCurrentStaff()))));
//                        }
//                        if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
//                            booleanExpression = booleanExpression.and(qCase.partner.id.eq(getLoggedInUserPartnerId()));
//                            booleanExpression1 = booleanExpression1.and(qCase.partner.id.eq(getLoggedInUserPartnerId()));
//
//                        }
//                        if (!searchModel.getFilterValue().isEmpty()) {
//                            String searchKey = searchModel.getFilterValue();
//                            booleanExpression = booleanExpression.and((qCase.caseNumber.contains(searchKey).or(qCase.customers.username.contains(searchKey).or(qCase.customers.firstname.contains(searchKey).or(qCase.customers.mobile.contains(searchKey).or(qCase.caseStatus.eq(searchKey)
////                                                                    .or(qCase.currentAssignee.firstname.contains(searchKey)
////                                                                            .or(qCase.currentAssignee.lastname.contains(searchKey)
//                                    .or(qCase.caseOrigin.contains(searchKey).or(qCase.priority.contains(searchKey).or(qCase.caseType.contains(searchKey).or(qCase.caseTitle.contains(searchKey)))))))))));
//                            Page<Case> caseList = caseRepository.findAll(booleanExpression, pageable);
//
//                            if (null != caseList && 0 < caseList.getSize() && !caseList.isEmpty()) {
//                                makeGenericResponse(genericDataDTO, caseList);
//                                return genericDataDTO;
//                            }
//                            booleanExpression1 = booleanExpression1.and(qCase.currentAssignee.firstname.contains(searchKey).or(qCase.currentAssignee.lastname.contains(searchKey)));
//                        }
//                        Page<Case> caseListByAsignee = caseRepository.findAll(booleanExpression1, pageable);
//                        if (null != caseListByAsignee && 0 < caseListByAsignee.getSize()) {
//                            makeGenericResponse(genericDataDTO, caseListByAsignee);
//                        }
//                        return genericDataDTO;
//                    }
////                    if (null != searchModel.getFilterCondition()) {
////                        if (searchModel.getFilterCondition().equalsIgnoreCase(SearchConstants.AND)) {
////                            generateAndCondition(searchModel, whereCondition, join);
////                        } else {
////                            generateOrCondition(searchModel, whereCondition, join);
////                        }
////                    } else {
////                        generateAndCondition(searchModel, whereCondition, join);
////                    }
//                }
////                if (whereCondition.length() > 0) {
////                    commonQuery.append(" " + (join.length() >= 0 ? join : " ") + SubscriberSearchQueryScript.WHERE + whereCondition);
////                    finalQuery = commonQuery.toString();
////                }
////                Query q = entityManager.createNativeQuery(finalQuery, GenericIdModel.class);
////                List<GenericIdModel> resultList = q.getResultList();
////                if (null != resultList && 0 < resultList.size()) {
////                    List<String> idList = new ArrayList<>();
////                    for (GenericIdModel idModel : resultList) {
////                        idList.add(idModel.getId().toString());
////                    }
////                    return makeGenericResponse(genericDataDTO, caseRepository.findAllBy(idList, pageable));
////                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return null;
//    }

//    public GenericDataDTO getCaseByNumberOrTypeOrOriginOrCustomerDetailsByPartner(String s1, Pageable pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getCaseByNumberOrCustomerDetailsByPartner()] ";
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            Page<Case> caseList = caseRepository.findAllByCaseNumberOrCaseStatusOrCurrentAssignee_FirstnameByPartner(pageRequest
//                    , s1, s1, s1, s1, s1, s1, s1, s1, s1, s1, getLoggedInUserPartnerId(),s1);
//            if (null != caseList && 0 < caseList.getSize()) {
//                makeGenericResponse(genericDataDTO, caseList);
//            }
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }


//    public Case getEntityByCaseNumber(String caseNumber) {
//        return caseRepository.findByCaseNumber(caseNumber);
//    }

//    private String generateAndCondition(GenericSearchModel searchModel, StringBuilder whereCondition, StringBuilder join) {
//        if (null != searchModel.getFilterOperator()) {
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.EQUAL_TO)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.REASON)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.REASON_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_REASON + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CURRENT_ASSIGNEE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.STAFF_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(" ( " + CaseSearchQueryScript.STAFF_FNAME + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "' OR " + CaseSearchQueryScript.STAFF_LNAME + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "' )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//            }
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.NOT_EQUAL)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.REASON)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.REASON_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_REASON + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CURRENT_ASSIGNEE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.STAFF_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(" ( " + CaseSearchQueryScript.STAFF_FNAME + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "' OR " + CaseSearchQueryScript.STAFF_LNAME + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "' )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//            }
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.CONTAINS)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.REASON)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.REASON_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_REASON + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CURRENT_ASSIGNEE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.STAFF_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(" ( " + CaseSearchQueryScript.STAFF_FNAME + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%' OR " + CaseSearchQueryScript.STAFF_LNAME + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%' )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//
//            }
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.STARTS_WITH)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.REASON)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.REASON_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_REASON + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CURRENT_ASSIGNEE)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.STAFF_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(" ( " + CaseSearchQueryScript.STAFF_FNAME + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%' OR " + CaseSearchQueryScript.STAFF_LNAME + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%' )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.AND);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//            }
//        }
//        return whereCondition.toString();
//    }

//    private String generateOrCondition(GenericSearchModel searchModel, StringBuilder whereCondition, StringBuilder join) {
//        if (null != searchModel.getFilterOperator()) {
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.EQUAL_TO)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() >= 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//
//            }
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.NOT_EQUAL)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.NOT_EQUAL_TO + "'" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.NOT_EQUAL_TO + " '" + searchModel.getFilterValue() + "'");
//                }
//
//
//            }
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.CONTAINS)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.LIKE + "'%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.LIKE + " '%" + searchModel.getFilterValue() + "%'");
//                }
//
//
//            }
//
//            if (searchModel.getFilterOperator().equalsIgnoreCase(SearchConstants.STARTS_WITH)) {
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_NAME)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_FIRSTNAME + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_USERNAME)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_USERNAME + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_EMAIL)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_EMAIL + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CUST_MOBILE)) {
//                    if (join.length() != 0) join.append(CaseSearchQueryScript.CUST_JOIN);
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(SubscriberSearchQueryScript.CUST_MOBILE + SubscriberSearchQueryScript.LIKE + "'" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PORT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_PORT + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SLOT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SLOT + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.OLT)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_NETWORK_DEVICE + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_SERVICE_AREA + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.ONU)) {
//                    if (whereCondition.length() > 0) whereCondition.append(SubscriberSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.EXISTS_BY_ONU + SubscriberSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'" + CaseSearchQueryScript.CUST_CONDITION + " )");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_ORIGIN)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_ORIGIN + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_NUMBER)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_NUMBER + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.PRIORITY)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_PRIORITY + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_TYPE)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_TYPE + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//                if (searchModel.getFilterColumn().equalsIgnoreCase(SearchConstants.CASE_STATUS)) {
//                    if (whereCondition.length() > 0) whereCondition.append(CaseSearchQueryScript.OR);
//                    whereCondition.append(CaseSearchQueryScript.CASE_STATUS + " " + CaseSearchQueryScript.LIKE + " '" + searchModel.getFilterValue() + "%'");
//                }
//
//
//            }
//        }
//        return whereCondition.toString();
//    }

//    private CaseUpdateDTO setFirstRemarkInUpdate(CaseDTO entity) {
//        CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
//        caseUpdateDTO.setTicketId(entity.getCaseId());
//        if (entity.getFirstRemark() != null) {
//            if (entity.getFirstRemark().length() <= 4000) {
//                caseUpdateDTO.setRemark(entity.getFirstRemark());
//            } else {
//                throw new CustomValidationException(APIConstants.FAIL, "Remark message should be less then 40000 characters", null);
//            }
//        }
//        if (null != getLoggedInUser().getRolesList() && 0 < getLoggedInUser().getRolesList().length()) {
//            String[] roleArray = getLoggedInUser().getRolesList().split(",");
//            if (roleArray.length > 0) {
//                List<String> list = Arrays.stream(roleArray).filter("8"::equalsIgnoreCase).collect(Collectors.toList());
//                if (null != list && 0 < list.size()) {
//                    caseUpdateDTO.setCommentBy(CaseConstants.COMMENT_BY_CUSTOMER);
//                }
//            }
//        }
//        if (caseUpdateDTO.getCommentBy() == null) {
//            caseUpdateDTO.setCommentBy(CaseConstants.COMMENT_BY_STAFF);
//        }
//        caseUpdateDTO.setRemarkType(CaseConstants.REMARK_TYPE_EXTERNAL);
//        return caseUpdateDTO;
//    }

//    public Long findMinimumAssignReuqestByStaff(Integer id) {
//        return caseRepository.findMinimumAssignReuqestByStaff(id);
//    }

//    public CaseDTO ticketRating(@Valid CaseFeedbackRel  caseFeedbackDTO) throws Exception {
//        CaseDTO dto = null;
//        Optional<Case> caseOptional = caseRepository.findById(caseFeedbackDTO.getTicketid());
//        if (caseOptional.isPresent()) {
//            Case caseDb = caseOptional.get();
//            if (caseDb.getCaseStatus().equalsIgnoreCase("Closed")) {
////                caseDb.setRating(caseFeedbackDTO.getRating());
////                caseDb.setCustomerFeedback(caseFeedbackDTO.getCustomerFeedback());
////                caseDb = caseRepository.save(caseDb);
//                caseFeedbackDTO.setCreated_date(LocalDateTime.now());
//                caseFeedbackRelRepository.save(caseFeedbackDTO);
//                dto = getMapper().domainToDTO(caseDb, new CycleAvoidingMappingContext());
//            } else {
//                throw new CustomValidationException(APIConstants.FAIL, "Sorry! You can only rate for closed ticket.", null);
//            }
//        } else {
//            throw new CustomValidationException(APIConstants.FAIL, "Sorry! Ticket is not found for id : " + caseFeedbackDTO.getTicketid(), null);
//        }
//        return dto;
//    }


//    private String checkTickePriority(CaseDTO entity, Customers customers) {
//        int lastDayscount = Integer.parseInt(clientService.getByName(CommonConstants.TICKET_COUNT).getValue());
//        int lastDayscountSameCategory = Integer.parseInt(clientService.getByName(CommonConstants.TICKET_COUNT_IN_LAST_DAYS).getValue());
//        int lastDaysCountCustomer = Integer.parseInt(clientService.getByName(CommonConstants.TICKET_COUNT_SAME_CATEGORY).getValue());
//        int lastDaysCountSameCategory = Integer.parseInt(clientService.getByName(CommonConstants.TICKET_COUNT_SAME_CATEGORY_DAYS).getValue());
//        QCase qCase = QCase.case$;
//        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.customers.id.eq(customers.getId()));
//        long ticketCount = caseRepository.count(booleanExpression.and(qCase.createdate.between(LocalDateTime.now().minusDays(lastDaysCountCustomer), LocalDateTime.now())));
//        if (ticketCount >= lastDayscount) {
//            return "High";
//        } else {
//            booleanExpression = booleanExpression.and(qCase.ticketReasonCategoryId.eq(entity.getTicketReasonCategoryId())).and(qCase.createdate.between(LocalDateTime.now().minusDays(lastDaysCountSameCategory), LocalDateTime.now()));
//            if (caseRepository.count(booleanExpression) >= lastDaysCountCustomer) {
//                return "High";
//            }
//        }
//
//        return "Low";
//    }

//    @Transactional
//    public GenericDataDTO approveTicket(Long caseId, boolean isApproveRequest, String remarks) {
//        String SUBMODULE = getModuleNameForLog() + " [approveTicket()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        //addding entry to tat
//        HashMap<String, String> tatMapDetails = new HashMap<>();
//        StaffUser getCurrentStaffuser = new StaffUser();
//
//        try {
//            Case aCase = caseRepository.findById(caseId).orElse(null);
//
//            StaffUser loggedInStaffUser = staffUserService.get(getLoggedInUserId());
//            CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
//            caseUpdateDTO.setTicketId(caseId);
//            PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CUST_DOC_PATH).get(0).getValue();
//            CaseDTO caseDTO = caseService.getEntityForUpdateAndDelete(caseUpdateDTO.getTicketId());
//            List<CaseDocDetailsDTO> finalResponseList = new ArrayList<>();
//
////            if (getLoggedInUser().getLco() != null && getLoggedInUser().getLco()) {
////                loggedInStaffUser.setLcoId(getLoggedInUser().getPartnerId());
////            } else {
////                loggedInStaffUser.setLcoId(null);
////            }
//          //  if(loggedInStaffUser.getLcoId()==null){
//                if (clientServiceRepository.findValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
//                    if (!loggedInStaffUser.getUsername().equalsIgnoreCase("admin")) {
//                        Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(aCase.getCustomers().getMvnoId(), aCase.getCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                        int staffId = 0;
//                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
//                            caseUpdateDTO.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
//                            staffId = Integer.parseInt(map.get("staffId"));
//                            StaffUser assignedStaffUser = staffUserService.get(staffId);
//                            caseUpdateDTO.setAssignee(staffId);
//                            caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
//                            TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Integer.valueOf(map.get("nextTatMappingId"))).orElse(null);
//                            Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
//                            String nextFollowupDate = aCase.getNextFollowupDate().toString();
//                            String nextFollowupTime = aCase.getNextFollowupTime().toString();
//                            if (isApproveRequest) {
//                                if (aCase.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)) {
//                                    aCase.setCaseStatus(CaseConstants.STATUS_IN_PROGRESS);
//                                    caseRepository.save(aCase);
//                                }
//                            }
//                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), loggedInStaffUser.getId(), loggedInStaffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? "remarks :- " + remarks + " Approved By :- " + loggedInStaffUser.getUsername() : "remarks :- " + remarks + " Rejected By :- " + loggedInStaffUser.getUsername());
//                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), assignedStaffUser.getId(), assignedStaffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaffUser.getUsername());
//                            caseUpdateService.sendAssignTicketMessege(aCase.getCustomers().getUsername(), aCase.getCustomers().getMobile(), aCase.getCustomers().getEmail(), aCase.getCustomers().getMvnoId(), aCase.getCaseNumber(), teams.getName(), nextFollowupDate, aCase.getCustomers().getUsername(), nextFollowupTime,aCase.getCustomerAdditionalEmail(),aCase.getSerialNumber());
//                        } else {
//                            if (isApproveRequest) {
//                                if (aCase.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_IN_PROGRESS)) {
//                                    aCase.setCaseStatus(CaseConstants.STATUS_RESOLVED);
//                                    caseRepository.save(aCase);
//                                }
//                                caseUpdateDTO.setStatus(CaseConstants.STATUS_RESOLVED);
//                            } else {
//                                caseUpdateDTO.setStatus(CaseConstants.REJECT);
//                            }
//                            aCase.setCurrentAssignee(null);
//                            aCase.setFinalClosedBy(loggedInStaffUser);
//                            aCase.setFinalClosedDate(LocalDateTime.now());
//                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), loggedInStaffUser.getId(), loggedInStaffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? "remarks :- " + remarks + " Approved By :- " + loggedInStaffUser.getUsername() : "remarks :- " + remarks + " Rejected By :- " + loggedInStaffUser.getUsername());
//
//                        }
//                        caseUpdateService.updateEntity(caseUpdateDTO, null, false);
//                        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                    } else {
////                        if (isApproveRequest) {
////                            if (aCase.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_IN_PROGRESS)) {
////                                aCase.setCaseStatus(CaseConstants.STATUS_RESOLVED);
////                                caseRepository.save(aCase);
////                            }
////                        }
//                        caseUpdateDTO.setStatus(CaseConstants.STATUS_RESOLVED);
//                        aCase.setCurrentAssignee(null);
//                        aCase.setFinalClosedBy(loggedInStaffUser);
//                        aCase.setFinalClosedDate(LocalDateTime.now());
//                        caseUpdateService.updateEntity(caseUpdateDTO, null, false);
//                        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), loggedInStaffUser.getId(), loggedInStaffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? "remarks :- " + remarks + " Approved By :- " + loggedInStaffUser.getUsername() : "remarks :- " + remarks + " Rejected By :- " + loggedInStaffUser.getUsername());
//                    }
//                }
//                else {
//                    if (!loggedInStaffUser.getUsername().equalsIgnoreCase("admin")) {
//                        Map<String, Object> map = hierarchyService.getTeamForNextApprove(aCase.getCustomers().getMvnoId(), aCase.getCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                        if (map.containsKey("assignableStaff")) {
//                                List<TeamHierarchyMapping> teamHierarchyMapping = teamHierarchyMappingRepo.findAllByHierarchyId(Integer.valueOf(String.valueOf(map.get("workFlowId"))));
//                                    if (teamHierarchyMapping.size() > 0 &&teamHierarchyMapping.size()-2 == Integer.valueOf(String.valueOf(map.get("orderNo")))&& !isApproveRequest) {
//                                        caseUpdateDTO.setStatus(CaseConstants.REJECT);
//                                        aCase.setFinalClosedBy(loggedInStaffUser);
//                                        aCase.setFinalClosedDate(LocalDateTime.now());
//                                        aCase.setCurrentAssignee(null);
//                                        caseUpdateService.updateEntity(caseUpdateDTO, null, false);
//                                    }else {
//
//                                        genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
//                                        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                                        aCase.setCase_order(aCase.getCase_order() + 1);
//                                        caseUpdateService.updateEntity(caseUpdateDTO, null, false);
//                                        tatMapDetails.put("workFlowId", map.get("workFlowId").toString());
//                                        tatMapDetails.put("eventId", caseDTO.getCaseId().toString());
//                                        tatMapDetails.put("eventName", map.get("eventName").toString());
//                                        getCurrentStaffuser = staffUserRepository.findById(caseDTO.getCurrentAssigneeId()).orElse(null);
//                                        tatUtils.saveOrUpdateTicketTatMatrix(caseDTO, tatMapDetails, getCurrentStaffuser, false);
//                                        if (isApproveRequest) {
//                                            if (aCase.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)) {
//                                                aCase.setCaseStatus(CaseConstants.STATUS_IN_PROGRESS);
//                                                aCase.setNextFollowupDate(LocalDate.now());
//                                                aCase.setNextFollowupTime(LocalTime.now());
//                                                caseRepository.save(aCase);
//                                            }
//                                        }
//
//                                        workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) map.get("eventId") : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), loggedInStaffUser.getId(), loggedInStaffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? "remarks :- " + remarks + " Approved By :- " + loggedInStaffUser.getUsername() : "remarks :- " + remarks + " Rejected By :- " + loggedInStaffUser.getUsername());
//                                    }
//                        } else {
//                            if (isApproveRequest) {
//                                if (aCase.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_IN_PROGRESS)) {
//                                    aCase.setCaseStatus(CaseConstants.STATUS_RESOLVED);
//                                    caseRepository.save(aCase);
//                                }
//                                caseUpdateDTO.setStatus(CaseConstants.STATUS_RESOLVED);
//                            } else {
//                                caseUpdateDTO.setStatus(CaseConstants.REJECT);
//                            }
//
//                            aCase.setFinalClosedBy(loggedInStaffUser);
//                            aCase.setFinalClosedDate(LocalDateTime.now());
//                            TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails= tatMatrixWorkFlowDetailsRepo.findByEventIdAndIsActive(aCase.getCaseId().intValue(),isApproveRequest);
//                            if(tatMatrixWorkFlowDetails!=null) {
//                                tatMapDetails.put("workFlowId", tatMatrixWorkFlowDetails.getWorkFlowId().toString());
//                                tatMapDetails.put("eventId", tatMatrixWorkFlowDetails.getEventId().toString());
//                                tatMapDetails.put("eventName", tatMatrixWorkFlowDetails.getEventName());
//                            }
//
//                            getCurrentStaffuser = staffUserRepository.findById(caseDTO.getCurrentAssigneeId()).orElse(null);
//
//                            aCase.setCurrentAssignee(null);
//                            caseUpdateService.updateEntity(caseUpdateDTO, null, false);
//                            if(tatMapDetails!=null && !tatMapDetails.isEmpty()  ) {
//                                tatUtils.saveOrUpdateTicketTatMatrix(caseDTO, tatMapDetails, getCurrentStaffuser, false);
//                            }
//                            workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) map.get("eventId") : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), loggedInStaffUser.getId(), loggedInStaffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? "remarks :- " + remarks + " Approved By :- " + loggedInStaffUser.getUsername() : " remarks :- " + remarks + " Rejected By :- " + loggedInStaffUser.getUsername());
//                        }
//                    } else {
//                        if (isApproveRequest) {
//                            if (aCase.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_IN_PROGRESS)) {
//                                aCase.setCaseStatus(CaseConstants.STATUS_RESOLVED);
//                                caseRepository.save(aCase);
//                            }
//                        }
//                        caseUpdateDTO.setStatus(CaseConstants.STATUS_RESOLVED);
//                        aCase.setCurrentAssignee(null);
//                        aCase.setFinalClosedBy(loggedInStaffUser);
//                        aCase.setFinalClosedDate(LocalDateTime.now());
//                        caseUpdateService.updateEntity(caseUpdateDTO, null, false);
//                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), loggedInStaffUser.getId(), loggedInStaffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? "remarks :- " + remarks + " Approved By :- " + loggedInStaffUser.getUsername() : "remarks :- " + remarks + " Rejected By :- " + loggedInStaffUser.getUsername());
//                    }
//                }
//           // }
////            else{
////                if (isApproveRequest) {
////                    if (aCase.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_IN_PROGRESS)) {
////                        aCase.setCaseStatus(CaseConstants.STATUS_RESOLVED);
////                        caseRepository.save(aCase);
////                    }
////                }
////                caseUpdateDTO.setStatus(CaseConstants.STATUS_CLOSED);
////                aCase.setCurrentAssignee(null);
////                aCase.setFinalClosedBy(loggedInStaffUser);
////                aCase.setFinalClosedDate(LocalDateTime.now());
////                caseUpdateService.updateEntity(caseUpdateDTO, null, false);
////                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), loggedInStaffUser.getId(), loggedInStaffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? "remarks :- " + remarks + " Approved By :- " + loggedInStaffUser.getUsername() : "remarks :- " + remarks + " Rejected By :- " + loggedInStaffUser.getUsername());
////
////            }
//
//            return genericDataDTO;
//        } catch (CustomValidationException ex) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
//        } catch (Exception ex) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
//
//        }
//    }


   // @Transactional
//    public GenericDataDTO assignPickedTicket(Long caseId, Integer staffId, String remark) throws Exception {
//        Case aCase = caseRepository.findById(caseId).orElse(null);
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        StaffUser pickedUser = staffUserRepository.findById(staffId).orElse(null);
//
//        if (aCase == null) {
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage("Case not found..");
//            return genericDataDTO;
//        } else {
//            CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
//            caseUpdateDTO.setRemarkType("Picked by remarks ");
//            caseUpdateDTO.setRemark(remark);
//            caseUpdateDTO.setAssignee(staffId);
//            caseUpdateDTO.setTicketId(caseId);
//            Case newCase = new Case();
//
//
//            TicketTatMatrix ticketTatMatrix = caseService.getTicketTatMatrixFromSubReasonId(caseService.getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
//
//            Integer Nextvalue = Integer.parseInt(String.valueOf(ticketTatMatrix.getTatMatrixMappings().get(0).getMtime3()));
//            TicketTatMatrixMapping ticketTatMatrixMapping = ticketTatMatrix.getTatMatrixMappings().get(0);
//
////            if(!aCase.getCaseStatus().equalsIgnoreCase("Follow Up")){
////                newCase = tatUtils.UpdateDateTime(ticketTatMatrixMapping, aCase, Nextvalue);
////                caseUpdateDTO.setNextFollowupDate(newCase.getNextFollowupDate());
////                caseUpdateDTO.setNextFollowupTime(newCase.getNextFollowupTime());
////
////            }else{
////                newCase = tatUtils.UpdateDateTime(ticketTatMatrixMapping, aCase, Nextvalue);
////                caseUpdateDTO.setNextFollowupDate(LocalDate.now());
////                caseUpdateDTO.setNextFollowupTime(newCase.getNextFollowupTime());
////            }
//            caseService.updateFollowUpDateAndTimeForTicketAfterPickedUp(aCase);
//
//            caseUpdateDTO.setStatus(CaseConstants.STATUS_IN_PROGRESS);
//            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), pickedUser.getId(), pickedUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.PICKED, LocalDateTime.now(), "Remark : -" + remark + " Picked By :- " + pickedUser.getUsername());
//            Map<String,Object> detailsMap = getDetailsforTatWorkflowDetailsEntry(caseMapper.domainToDTO(aCase,new CycleAvoidingMappingContext()),true);
//            caseService.updateTicketLevel(caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()),detailsMap);
//            caseUpdateService.updateEntity(caseUpdateDTO, null, true);
//            for (TicketAssignStaffMapping ticketAssignStaffMapping : aCase.getTicketAssignStaffMappings()) {
//                ticketAssignStaffMappingRepo.delete(ticketAssignStaffMapping);
//            }
//            if (aCase.getTeamHierarchyMappingId() != null) {
//                tatUtils.changeTicketTatAssignee(caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()), pickedUser, false, true);
//            }
//
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        }
//        return genericDataDTO;
//    }

//    @Transactional
//    public GenericDataDTO assignEveryStaffFromList(Long caseId, String remark, Boolean isApproveRequest) throws Exception {
//        Case aCase = caseRepository.findById(caseId).orElse(null);
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
//        Map<String, Object> map = hierarchyService.getTeamForNextApprove(aCase.getCustomers().getMvnoId(), aCase.getCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()));
//        if (map.containsKey("assignableStaff")) {
//            List<StaffUserPojo> staffUserPojos = (List<StaffUserPojo>) map.get("assignableStaff");
//            caseUpdateDTO.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTeamHierarchyMappingId").toString()));
//            caseUpdateDTO.setTicketId(caseId);
//            caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
//            aCase.setCurrentAssignee(null);
//            Map<Integer, StaffUserPojo> staffByParentStaffId = new HashMap<>();
//            //List<StaffUserPojo> staffByUniqueParent = new ArrayList<>();
//            for (StaffUserPojo staffUserPojo : staffUserPojos) {
//                TicketAssignStaffMapping ticketAssignStaffMapping = new TicketAssignStaffMapping();
//                ticketAssignStaffMapping.setStaffId(staffUserPojo.getId());
//                ticketAssignStaffMapping.setTicketId(aCase.getCaseId());
//                ticketAssignStaffMappingRepo.save(ticketAssignStaffMapping);
//                workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) (map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), staffUserPojo.getId(), staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUserPojo.getUsername());
//                if (staffUserPojo.getParentStaffId() != null) {
//                    staffByParentStaffId.put(staffUserPojo.getParentStaffId(), staffUserPojo);
//                }
//                //caseService.updateTicketLevel(caseMapper.domainToDTO(aCase,new CycleAvoidingMappingContext()),map);
//                //after assign to all responstime is set in followupdate and followuptime
//                if (!aCase.getCaseStatus().equalsIgnoreCase("Follow Up")) {
//                    caseService.updateFollowUpDateAndTimeForTicketBeforePickedUp(aCase);
//                }
//                String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + aCase.getCaseNumber() + " ' " + "for " + "customer name : " + " ' " + aCase.getCustomerName() + " '";
//                hierarchyService.sendWorkflowAssignActionMessage(staffUserPojo.getCountryCode(), staffUserPojo.getPhone(), staffUserPojo.getEmail(), staffUserPojo.getMvnoId(), staffUserPojo.getFullName(), action);
//            }
//
//            caseRepository.save(aCase);
//            caseUpdateService.updateEntity(caseUpdateDTO, null, true);
//            if (staffByParentStaffId != null && !staffByParentStaffId.isEmpty()) {
//                for (Map.Entry<Integer, StaffUserPojo> staffMap : staffByParentStaffId.entrySet()) {
//                    StaffUser staffUser = staffUserMapper.dtoToDomain(staffMap.getValue(), new CycleAvoidingMappingContext());
//                    tatUtils.changeTicketTatAssignee(caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()), staffUser, true, false);
//                }
//            }
//        }
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        return genericDataDTO;
//    }

//    public GenericDataDTO reassignTicket(Long caseId) throws Exception {
//        Case aCase = caseRepository.findById(caseId).orElse(null);
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        List<StaffUserPojo> staffUserList = new ArrayList<>();
//        if (aCase.getCurrentAssignee() != null) {
//            if (getLoggedInUserId() == aCase.getCurrentAssignee().getId()) {
//                if (aCase.getTeamHierarchyMappingId() != null) {
//                    staffUserList = hierarchyService.getStaffFromCurrentTeammapping(aCase.getTeamHierarchyMappingId().intValue(), getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                    genericDataDTO.setDataList(staffUserList);
//                }
//            } else if (aCase.getCurrentAssignee().getStaffUserparent() != null) {
//                StaffUser staffUser = aCase.getCurrentAssignee();
//                while (staffUser.getStaffUserparent() != null && staffUserList.size() == 0) {
//                    if (staffUser.getStaffUserparent().getId() == getLoggedInUserId()) {
//                        genericDataDTO.setData(CaseConstants.CHANGE_PROBLEM_DOMAIN);
//                        if (aCase.getTeamHierarchyMappingId() != null) {
//                            staffUserList = hierarchyService.getStaffFromCurrentTeammapping(aCase.getTeamHierarchyMappingId().intValue(), getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                            genericDataDTO.setDataList(staffUserList);
//                        }
//                    } else {
//                        staffUser = staffUser.getStaffUserparent();
//                    }
//                }
//            }
//
//        }
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        return genericDataDTO;
//    }

//    public GenericDataDTO reassignLead(Long caseId) throws Exception {
//        //please remove Case aCase for reassign lead
//        LeadMaster leadMaster = leadMasterRepository.findById(caseId).orElse(null);
//        Case aCase = caseRepository.findById(caseId).orElse(null);
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        List<StaffUserPojo> staffUserList = new ArrayList<>();
//        if (leadMaster.getCreatedBy() != null) {
//            int id = Integer.parseInt(leadMaster.getCreatedBy());
//            if (getLoggedInUserId() == id) {
//                if (leadMaster.getCreatedBy() != null) {
//                    staffUserList = hierarchyService.getStaffFromCurrentTeammapping(leadMaster.getNextApproveStaffId(), getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                    genericDataDTO.setDataList(staffUserList);
//                }
//            } else if (aCase.getCurrentAssignee().getStaffUserparent() != null) {
//                StaffUser staffUser = aCase.getCurrentAssignee();
//                while (staffUser.getStaffUserparent() != null && staffUserList.size() == 0) {
//                    if (staffUser.getStaffUserparent().getId() == getLoggedInUserId()) {
//                        genericDataDTO.setData(CaseConstants.CHANGE_PROBLEM_DOMAIN);
//                        if (aCase.getTeamHierarchyMappingId() != null) {
//                            staffUserList = hierarchyService.getStaffFromCurrentTeammapping(aCase.getTeamHierarchyMappingId().intValue(), getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                            genericDataDTO.setDataList(staffUserList);
//                        }
//                    } else {
//                        staffUser = staffUser.getStaffUserparent();
//                    }
//                }
//            }
//
//        }
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        return genericDataDTO;
//    }

//    public GenericDataDTO linkTicket(Long caseId, Integer linkTicketId) {
//        Case aCase = caseRepository.findById(caseId).orElse(null);
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        if (aCase != null) {
//            aCase.setParentTicketId(linkTicketId);
//            caseRepository.save(aCase);
//        }
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        return genericDataDTO;
//
//    }

//    public GenericDataDTO updateDocumentDetails(Long caseId, List<MultipartFile> file) throws Exception {
//
//        Case aCase = caseRepository.findById(caseId).orElse(null);
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.TICKET_PATH).get(0).getValue();
//
//        if (null != aCase && file != null && file.size() > 0) {
//            for (MultipartFile multipartFile : file) {
//                CaseDocDetailsDTO caseDoc = new CaseDocDetailsDTO();
////                String subFolderName = aCase.getCaseNumber().trim().replace("-","_") + "/";
//                String path = PATH;
//                caseDoc.setTicketId(Math.toIntExact(aCase.getCaseId()));
//                caseDoc.setDocStatus("Active");
//                MultipartFile file1 = fileUtility.getFileFromArrayForTicket(multipartFile);
//                if (file1 != null) {
//                    caseDoc.setUniquename(fileUtility.saveFileToServerForTicket(file1, path));
//                    caseDoc.setFilename(file1.getOriginalFilename());
//                    caseDoc = caseDocDetailsService.saveEntity(caseDoc);
//                }
//            }
//
//
//        }
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        return genericDataDTO;
//
//
//    }

//    public GenericDataDTO getTicketApprovals(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", CommonConstants.SORT_ORDER_DESC);
//        QCase qCase = QCase.case$;
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.isDelete.eq(false)).and(qCase.currentAssignee.id.in(getLoggedInUserId())).and(qCase.caseStatus.ne(CaseConstants.STATUS_CLOSED));
//        if (getMvnoIdFromCurrentStaff() != 1)
//            booleanExpression = booleanExpression.and(qCase.customers.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
//        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
//            booleanExpression = booleanExpression.and(qCase.customers.mvnoId.eq(1).or(qCase.customers.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qCase.customers.buId.in(getBUIdsFromCurrentStaff()))));
//        }
//
//        Page<Case> paginationList = caseRepository.findAll(booleanExpression, pageRequest);
//        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
//        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
//        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
//        genericDataDTO.setTotalPages(paginationList.getTotalPages());
//        return genericDataDTO;
//    }

//    public GenericDataDTO sendETRTicketNotification(TicketETRPojo ticketETRPojo) throws Exception {
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        // gettitng nacessery data from pojo
//        Case aCase = caseRepository.findById(ticketETRPojo.getTicketId().longValue()).orElse(null);
//        CustomersPojo customers = customersService.findById(ticketETRPojo.getCustId());
//        StaffUser staffUser = staffUserRepository.findById(aCase.getCurrentAssignee().getId()).orElse(null);
//
//        //setting template notification content
//        HashMap<Object, Object> data = new HashMap<>();
//
//        //adding customers data
//        data.put("custId", customers.getId());
//        data.put("customerName", customers.getUsername());
//        data.put("mobileNumber", customers.getMobile());
//        data.put("email", customers.getEmail());
//        data.put("mvnoId", customers.getMvnoId());
//
//
//        //adding case data
//        data.put("caseId", aCase.getCaseId());
//        data.put("caseNumber", aCase.getCaseNumber());
//        data.put("staffId", staffUser.getId());
//        data.put("staffName", staffUser.getFullName());
//        data.put("additionalDate", ticketETRPojo.getNotificationDate());
//        data.put("additionalTime", ticketETRPojo.getNotificationTime());
//        data.put("remark", ticketETRPojo.getRemark());
//        data.put("status", ticketETRPojo.getStatus());
//        data.put("sender", ticketETRPojo.getSender());
//
//
//        //adding addtional data
//        data.put("typeNotification", ticketETRPojo.getSelectedNotificationType());
//        data.put("isTemplateDynamic", ticketETRPojo.getIsTemplateDynamic());
//
//        //auditdata
//        HashMap<String, Boolean> notificationType = new HashMap<>();
//        notificationType.putAll((Map<String, Boolean>) data.get("typeNotification"));
////        if (notificationType.get("sms")) {
////            data.put("notificationMode", "sms");
////        } else if (notificationType.get("email")) {
////            data.put("notificationMode", "email");
////        }
//        if ((Boolean) data.get("isTemplateDynamic")) {
//            data.put("messageMode", "Dynamic");
//        } else {
//            data.put("messageMode", "Static");
//        }
//
//        //calling senderFunction
//        sendETRTicketMessege(data);
//
//        //setiitng value in generic data
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//
//        return genericDataDTO;
//
//    }


//    public void sendETRTicketMessege(HashMap<Object, Object> data) {
//        try {
//            TemplateNotification optionalTemplate = new TemplateNotification();
//
//            if (!(Boolean) data.get("isTemplateDynamic")) {
//                optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.TICKET_ETR_TEMPLATE).orElse(null);
//            } else {
//                optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.TICKET_ETR_TEMPLATE_DYNAMIC).orElse(null);
//            }
//
//            HashMap<String, Boolean> notificationType = new HashMap<>();
//            notificationType.putAll((Map<String, Boolean>) data.get("typeNotification"));
//
//
//            if (notificationType.get("sms") && notificationType.get("email")) {
//                optionalTemplate.setSmsEventConfigured(true);
//                optionalTemplate.setEmailEventConfigured(true);
//
//            } else if (notificationType.get("email") && !notificationType.get("sms")) {
//                optionalTemplate.setEmailEventConfigured(true);
//                optionalTemplate.setSmsEventConfigured(false);
//            } else if (notificationType.get("sms") && !notificationType.get("email")) {
//                optionalTemplate.setEmailEventConfigured(false);
//                optionalTemplate.setSmsEventConfigured(true);
//            }
//
//
//            if (optionalTemplate != null) {
//                Long buId = null;
//                if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
//                    buId =  getBUIdsFromCurrentStaff().get(0);
//                }
//
//                TicketETRMsg ticketETRMsg = new TicketETRMsg(data.get("customerName").toString(),
//                        data.get("mobileNumber").toString(),
//                        data.get("email").toString(),
//                        Integer.valueOf(data.get("mvnoId").toString()),
//                        RabbitMqConstants.TICKET_ETR_TEMPLATE,
//                        optionalTemplate,
//                        RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY,
//                        data.get("caseNumber").toString(),
//                        data.get("additionalDate").toString(),
//                        data.get("additionalTime").toString(),
//                        data.get("staffName").toString(),
//                        data.get("remark").toString(),
//                        data.get("status").toString(),
//                        data.get("sender").toString(),
//                        (Boolean) data.get("isTemplateDynamic"),
//                        //data.get("notificationMode").toString(),
//                        data.get("messageMode").toString(),
//                        (Integer) data.get("custId"),
//                        (Integer) data.get("staffId"),
//                        (Long) data.get("caseId"),
//                        buId);
//                Gson gson = new Gson();
//                gson.toJson(ticketETRMsg);
//                messageSender.send(ticketETRMsg, RabbitMqConstants.QUEUE_TICKET_ETR);
//
//
//            } else {
//                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
//                System.out.println("Ticket ETR Failed, There might be templeate issues");
//            }
//
//
//        } catch (Throwable e) {
//            throw new RuntimeException(e.getMessage());
//        }
//
//    }

//    public void saveETRAudit(HashMap<String, Object> data) {
//        EtrAudit etrAudit = new EtrAudit();
//        etrAudit.setCaseNumber(data.get("caseNumber").toString());
//        etrAudit.setCaseId((Integer) data.get("caseId"));
//        etrAudit.setCustId((Integer) data.get("custId"));
//        etrAudit.setCustUserName(data.get("customerName").toString());
//        etrAudit.setStaffId((Integer) data.get("staffId"));
//        etrAudit.setStaffPersonName(data.get("staffPersonName").toString());
//        etrAudit.setNotificationSentTime(LocalTime.now());
//        etrAudit.setNotificationSentDate(LocalDate.now());
//        etrAudit.setNotificationMessage(data.get("notificationMessage").toString());
//        etrAudit.setNotificationMode(data.get("notificationMode").toString());
//        etrAudit.setMessageMode(data.get("messageMode").toString());
//        etrAudit.setNotificationStatus(data.get("notificationStatus").toString());
//        ticketETRAuditRepository.save(etrAudit);
//    }

//    public void saveEnterpriseETRAudit(HashMap<String, Object> data) {
//        EnterpriseETRAudit etrAudit = new EnterpriseETRAudit();
//        etrAudit.setCustId((Long) data.get("custId"));
//        etrAudit.setCustUserName(data.get("customerName").toString());
//        etrAudit.setStaffId((Long) data.get("custId"));
//        etrAudit.setStaffPersonName(data.get("staffPersonName").toString());
//        etrAudit.setNotificationSentTime(LocalTime.now());
//        etrAudit.setNotificationSentDate(LocalDate.now());
//        etrAudit.setNotificationMessage(data.get("notificationMessage").toString());
//        etrAudit.setNotificationMode(data.get("notificationMode").toString());
//        etrAudit.setMessageMode(data.get("messageMode").toString());
//        etrAudit.setNotificationStatus(data.get("notificationStatus").toString());
//        enterpriseETRAuditRepository.save(etrAudit);
//    }

//    public GenericDataDTO getETRDetailsForCase(Long caseId) {
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        List<EtrAudit> etrAudits = new ArrayList<>();
//        try {
//            QEtrAudit qEtrAudit = QEtrAudit.etrAudit;
//            BooleanExpression qEtrAuditBool = qEtrAudit.isNotNull().and(qEtrAudit.caseId.eq(Math.toIntExact(caseId)));
//            etrAudits = (List<EtrAudit>) ticketETRAuditRepository.findAll(qEtrAuditBool);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        genericDataDTO.setDataList(etrAudits);
//        return genericDataDTO;
//    }


//    public void updateFollowUpDateAndTimeForTicketBeforePickedUp(Case aCase) {
//
//        CaseDTO caseDTO = caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext());
//        try {
//            if (aCase.getReasonSubCategoryId() != null) {
////            Optional<TicketReasonSubCategory> ticketSubReasonCategory = ticketReasonSubCategoryRepo.findById(aCase.getReasonSubCategoryId());
//
//                TicketTatMatrix masterTicketTat = getTicketTatMatrixFromSubReasonId(caseDTO);
//                if (masterTicketTat != null) {
//                    List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
//                    Long responseTime = masterTicketTat.getRtime();
//                    String responsUnit = masterTicketTat.getRunit();
//                    LocalDate localDate = LocalDate.now();
//                    LocalTime localTime = LocalTime.now();
////                    if (!responsUnit.isEmpty()) {
////                        if (responsUnit.equals("Day")) {
////                            aCase.setNextFollowupDate(localDate.plusDays(responseTime));
////                        } else if (responsUnit.equals("Hour")) {
////                            aCase.setNextFollowupTime(localTime.plusHours(responseTime));
////                        } else if (responsUnit.equals("Min")) {
////                            aCase.setNextFollowupTime(localTime.plusMinutes(responseTime));
////                        }
////                        try {
////                            caseService.saveEntity(caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()));
////
////                        } catch (Exception e) {
////                            throw new RuntimeException(e);
////                        }
////                    }
//                    if (!responsUnit.isEmpty()) {
//                        if (responsUnit.equals("Day")) {
//                            aCase.setNextFollowupDate(localDate.plusDays(responseTime));
//                        } else if (responsUnit.equals("Hour")) {
//                            if (responseTime >= 24) {
//                                long days = responseTime / 24;
//                                long remainingHours = responseTime % 24;
//                                aCase.setNextFollowupDate(localDate.plusDays(days));
//                                aCase.setNextFollowupTime(localTime.plusHours(remainingHours));
//                            } else {
//                                aCase.setNextFollowupTime(localTime.plusHours(responseTime));
//                            }
//                        } else if (responsUnit.equals("Min")) {
//                            if (responseTime >= 60) {
//                                long hours = responseTime / 60;
//                                long remainingMinutes = responseTime % 60;
//                                aCase.setNextFollowupTime(localTime.plusHours(hours).plusMinutes(remainingMinutes));
//                            } else {
//                                aCase.setNextFollowupTime(localTime.plusMinutes(responseTime));
//                            }
//                        }
//
//                        try {
//                            caseService.saveEntity(caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//                } else {
//                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "There might be an issue in TAT selection query condition !! after correction please try again", null);
//                }
//            }
//        } catch (CustomValidationException e) {
//            throw new CustomValidationException(e.getErrCode(), e.getMessage(), null);
//        }
//    }


//    public void updateFollowUpDateAndTimeForTicketAfterPickedUp(Case aCase) {
//
//        CaseDTO caseDTO = caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext());
//
//        try {
//            if (aCase.getReasonSubCategoryId() != null) {
//
////            Optional<TicketReasonSubCategory> ticketSubReasonCategory = ticketReasonSubCategoryRepo.findById(aCase.getReasonSubCategoryId());
//                TicketTatMatrix masterTicketTat = getTicketTatMatrixFromSubReasonId(caseDTO);
//
//                if (masterTicketTat != null) {
//                    List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
//                    if (tatMatrixMappings.size() > 0) {
//                        for (TicketTatMatrixMapping tatMatrixMapping : tatMatrixMappings) {
//
//                            if (tatMatrixMapping.getOrderNo() == 1) {
//                                Long primarySLATime = 0L;
//                                if (aCase.getPriority().equals("High")) {
//                                    primarySLATime = tatMatrixMapping.getMtime1();
//                                } else if (aCase.getPriority().equals("Medium")) {
//                                    primarySLATime = tatMatrixMapping.getMtime2();
//                                } else {
//                                    primarySLATime = tatMatrixMapping.getMtime3();
//                                }
//                                String primarySLAUnit = tatMatrixMapping.getMunit();
//                                LocalDate localDate = LocalDate.now();
//                                LocalTime localTime = LocalTime.now();
//                                if (!primarySLAUnit.isEmpty()) {
//                                    if (primarySLAUnit.equals("Day")) {
//                                        aCase.setNextFollowupTime(localTime);
//                                        aCase.setNextFollowupDate(localDate.plusDays(primarySLATime));
//                                    } else if (primarySLAUnit.equals("Hour")) {
//                                        aCase.setNextFollowupTime(localTime);
//                                        aCase.setNextFollowupTime(localTime.plusHours(primarySLATime));
//                                    } else if (primarySLAUnit.equals("Min")) {
//                                        aCase.setNextFollowupDate(localDate);
//                                        aCase.setNextFollowupTime(localTime.plusMinutes(primarySLATime));
//                                    }
//                                    try {
//                                       caseRepository.save(aCase);
//
//                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
//                                    }
//                                }
//
//                            }
//                        }
//
//                    }
//                } else {
//                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "There might be an issue in TAT selection query condition !! after correction please try again", null);
//                }
//            }
//        } catch (CustomValidationException e) {
//            throw new CustomValidationException(e.getErrCode(), e.getMessage(), null);
//        }
//    }


//    public Boolean checkTicketTatCondition(List<TatQueryFieldMapping> tatQueryFieldMappingList, Case caseDTO) {
//        List<QueryFieldMapping> queryFieldMappingList = new ArrayList<>();
//        for (TatQueryFieldMapping queryFieldMapping : tatQueryFieldMappingList) {
//            QueryFieldMapping mapping = new QueryFieldMapping();
//            mapping.setQueryOperator(queryFieldMapping.getQueryOperator());
//            mapping.setQueryCondition(queryFieldMapping.getQueryCondition());
//            mapping.setQueryValue(queryFieldMapping.getQueryValue());
//            mapping.setQueryField(queryFieldMapping.getQueryField());
//            queryFieldMappingList.add(mapping);
//        }
//        if (CollectionUtils.isEmpty(queryFieldMappingList))
//            return false;
//        return workFlowQueryUtils.checkCondition(queryFieldMappingList, CommonConstants.WORKFLOW_EVENT_NAME.CASE, caseDTO);
//    }

//    public TicketTatMatrix getTicketTatMatrixFromSubReasonId(CaseDTO caseDTO) {
//        Optional<TicketReasonSubCategory> ticketSubReasonCategory = ticketReasonSubCategoryRepo.findById(caseDTO.getReasonSubCategoryId());
//        if (ticketSubReasonCategory.isPresent()) {
//            if (!CollectionUtils.isEmpty(ticketSubReasonCategory.get().getTicketSubCategoryTatMappingList())) {
//                List<TicketSubCategoryTatMapping> ticketSubCategoryTatMappings = ticketSubReasonCategory.get().getTicketSubCategoryTatMappingList();
//                for (TicketSubCategoryTatMapping ticketSubCategoryTatMapping : ticketSubCategoryTatMappings) {
//                    QTatQueryFieldMapping qTatQueryFieldMapping = QTatQueryFieldMapping.tatQueryFieldMapping;
//                    BooleanExpression exp = qTatQueryFieldMapping.isNotNull().and(qTatQueryFieldMapping.tatMappingId.eq(ticketSubCategoryTatMapping.getId().intValue()));
//                    List<TatQueryFieldMapping> tatQueryFieldMappingList = (List<TatQueryFieldMapping>) tatQueryFieldMappingRepo.findAll(exp);
//                    if (!CollectionUtils.isEmpty(tatQueryFieldMappingList)) {
//                        //If query not matched then skip
//                        if (!tatUtils.checkTicketTatCondition(tatQueryFieldMappingList, caseDTO))
//                            continue;
//                    }
//                    TicketTatMatrix masterTicketTat = ticketSubCategoryTatMapping.getTicketTatMatrix();
////                    List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
//                    return masterTicketTat;
//                }
//            }
//        }
//        return null;
//    }

//    public GenericDataDTO getTatDetails(Long caseId) {
//        Case aCase = caseRepository.findById(caseId).orElse(null);
//        CaseDTO caseDTO = caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext());
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        if (aCase != null) {
//            Optional<TicketReasonSubCategory> ticketSubReasonCategory = ticketReasonSubCategoryRepo.findById(caseDTO.getReasonSubCategoryId());
//            if (ticketSubReasonCategory.isPresent()) {
//                if (!CollectionUtils.isEmpty(ticketSubReasonCategory.get().getTicketSubCategoryTatMappingList())) {
//                    List<TicketSubCategoryTatMapping> ticketSubCategoryTatMappings = ticketSubReasonCategory.get().getTicketSubCategoryTatMappingList();
//                    for (TicketSubCategoryTatMapping ticketSubCategoryTatMapping : ticketSubCategoryTatMappings) {
//                        QTatQueryFieldMapping qTatQueryFieldMapping = QTatQueryFieldMapping.tatQueryFieldMapping;
//                        BooleanExpression exp = qTatQueryFieldMapping.isNotNull().and(qTatQueryFieldMapping.tatMappingId.eq(ticketSubCategoryTatMapping.getId().intValue()));
//                        List<TatQueryFieldMapping> tatQueryFieldMappingList = (List<TatQueryFieldMapping>) tatQueryFieldMappingRepo.findAll(exp);
//                        if (!CollectionUtils.isEmpty(tatQueryFieldMappingList)) {
//                            //If query not matched then skip
//                            if (!tatUtils.checkTicketTatCondition(tatQueryFieldMappingList, caseDTO))
//                                continue;
//                        }
//                        TicketTatMatrix masterTicketTat = ticketSubCategoryTatMapping.getTicketTatMatrix();
////                    List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
//                        genericDataDTO.setData(masterTicketTat);
//                        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                        return genericDataDTO;
//                    }
//                }
//            }
//
//        }
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        return genericDataDTO;
//
//    }


//    public void sendTicketRescheduleMessege(String staffusername, String mobileNumber, String emailId, Integer mvnoId, String caseNumber, String nextFollowupDate, String nextFollowUpTime) {
//        try {
//            String followUpDateAndTime = nextFollowupDate + "," + nextFollowUpTime;
//
//            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.TICKET_RESCHEDULE_MESSAGE);
//            if (optionalTemplate.isPresent()) {
//                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
//                    Long buId = null;
//                    if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
//                        buId =  getBUIdsFromCurrentStaff().get(0);
//                    }
//                    TicketRescheduleMsg ticketRescheduleMsg = new TicketRescheduleMsg(staffusername, mobileNumber, emailId, RabbitMqConstants.TICKET_RESCHEDULE_SUCCESS_MSG, optionalTemplate.get(), caseNumber, followUpDateAndTime, mvnoId,buId);
//                    Gson gson = new Gson();
//                    gson.toJson(ticketRescheduleMsg);
//                    messageSender.send(ticketRescheduleMsg, RabbitMqConstants.QUEUE_TICKET_RESCHEDULE_SUCCESS_MSG);
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

//    public List<StaffUserPojo> findAllStaffUser(Integer serviceAreaId) {
//        try {
//            QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
//            BooleanExpression exp = qStaffUserServiceAreaMapping.isNotNull().and(qStaffUserServiceAreaMapping.serviceId.eq(serviceAreaId));
//            List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = (List<StaffUserServiceAreaMapping>) staffUserServiceAreaMappingRepository.findAll(exp);
//            List<Integer> serviceArea = new ArrayList<>();
//            if (staffUserServiceAreaMappingList.size() > 0) {
//                for (StaffUserServiceAreaMapping ids : staffUserServiceAreaMappingList) {
//                    Integer num = ids.getStaffId();
//                    serviceArea.add(num);
//                }
//            }
//            QStaffUser qStaffUser = QStaffUser.staffUser;
//            BooleanExpression exp1 = qStaffUser.isNotNull().and(qStaffUser.isDelete.eq(false).and(qStaffUser.id.in(serviceArea)));
//            List<StaffUser> staffUserList = (List<StaffUser>) staffUserRepository.findAll(exp1);
//            return staffUserService.convertResponseModelIntoPojo(staffUserList);
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }


//    public GenericDataDTO linkBulkTicket(List<Integer> caseUpdateDTOList, Integer linkTicketId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        for(int i =0; i<caseUpdateDTOList.size();i++){
//            Case aCase = caseRepository.findById(caseUpdateDTOList.get(i).longValue()).orElse(null);
//            if (aCase != null) {
//                aCase.setParentTicketId(linkTicketId);
//                caseRepository.save(aCase);
//            }
//        }
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        return genericDataDTO;
//    }

//    public GenericDataDTO reassignTicketInBulk(List<Long> caseIds) throws Exception {
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        for(int i =0; i<caseIds.size();i++){
//            Case aCase = caseRepository.findById(caseIds.get(i)).orElse(null);
//            List<StaffUserPojo> staffUserList = new ArrayList<>();
////            if (aCase.getCurrentAssignee() != null) {
////                if (getLoggedInUserId() == aCase.getCurrentAssignee().getId()) {
////                    if (aCase.getTeamHierarchyMappingId() != null) {
////                        staffUserList = hierarchyService.getStaffFromCurrentTeammapping(aCase.getTeamHierarchyMappingId().intValue(), getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
////                        genericDataDTO.setDataList(staffUserList);
////                    }
////                } else
//                    if (aCase.getCurrentAssignee().getStaffUserparent() != null) {
//                    StaffUser staffUser = aCase.getCurrentAssignee();
//                    if (staffUser.getStaffUserparent() != null) {
//                        if (staffUser.getStaffUserparent().getId() == getLoggedInUserId()) {
//                            //genericDataDTO.setData(CaseConstants.CHANGE_PROBLEM_DOMAIN);
//                            if (aCase.getTeamHierarchyMappingId() != null) {
//                                staffUserList = hierarchyService.getStaffFromCurrentTeammapping(aCase.getTeamHierarchyMappingId().intValue(), getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                                genericDataDTO.setDataList(staffUserList);
//                            }
//                        }else {
//                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),"Only Parent staff can reassign bulk tickets !!",null);
//                        }
////                        else {
////                            staffUser = staffUser.getStaffUserparent();
////                        }
//                    }
//                }
//
//            //}
//        }
//
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        return genericDataDTO;
//    }

//    public GenericDataDTO filterCase(String filter, PaginationRequestDTO requestDTO) {
//        PageRequest pageRequest = generatePageRequest(requestDTO.getPage(), requestDTO.getPageSize(), "createdate", CommonConstants.SORT_ORDER_DESC);
//        GenericDataDTO genericDataDTO=new GenericDataDTO();
//        QHierarchy qHierarchy=QHierarchy.hierarchy;
//        QCase qCase=QCase.case$;
//        BooleanExpression booleanExpression=qCase.isNotNull();
//        if(filter.equalsIgnoreCase(CaseConstants.ASSIGNED_TO_ME))   {
//       booleanExpression=booleanExpression.and(qCase.currentAssignee.id.eq(getLoggedInUserId()));
//        }
//        if(filter.equalsIgnoreCase(CaseConstants.ASSIGN_TO_MY_TEAM)){
//            List<Long>teamIdList=teamsRepository.findAllByStaff(getLoggedInUserId());
//            List<Long>teamHierarchyMappingList=new ArrayList<>();
//            BooleanExpression booleanExpression1=qHierarchy.isNotNull().and(qHierarchy.isDeleted.eq(false)).and(qHierarchy.eventName.equalsIgnoreCase("Case"));
//            List<Hierarchy> hierarchyList=  IterableUtils.toList(hierarchyRepository.findAll(booleanExpression1));
//            for (Hierarchy hierarchy : hierarchyList) {
//                for (TeamHierarchyMapping teamMapping : hierarchy.getTeamHierarchyMappingList()) {
//                    if (teamIdList.contains(teamMapping.getTeamId().longValue())) {
//                        teamHierarchyMappingList.add(teamMapping.getId().longValue());
//                    }
//                }
//            }
//            booleanExpression=booleanExpression.and(qCase.teamHierarchyMappingId.in(teamHierarchyMappingList));
//        }
//        if(filter.equalsIgnoreCase(CaseConstants.UNPICKED)){
//            QTicketAssignStaffMapping qTicketAssignStaffMapping=QTicketAssignStaffMapping.ticketAssignStaffMapping;
//            List<Long>teamIdList=teamsRepository.findAllByStaff(getLoggedInUserId());
//            List<TeamUserMapping>teamUserMappings= teamUserMappingsRepocitory.findAllByTeamIdIsIn(teamIdList);
//
//        List<Integer>integerList=teamUserMappings.stream().map(i->i.getStaffId().intValue()).collect(Collectors.toList());
//         BooleanExpression booleanExpression1=qTicketAssignStaffMapping.isNotNull()
//                .and(qTicketAssignStaffMapping.staffId.in(integerList));
//        //booleanExpression=booleanExpression.and()
//        List<TicketAssignStaffMapping>ticketAssignStaffMappings= IterableUtils.toList(ticketAssignStaffMappingRepo.findAll(booleanExpression1));
//        List<Long>caseidlist=ticketAssignStaffMappings.stream().map(i->i.getTicketId()).collect(Collectors.toList());
//        booleanExpression=booleanExpression.and(qCase.caseId.in(caseidlist).and(qCase.caseStatus.notEqualsIgnoreCase(CaseConstants.STATUS_CLOSED)));
//        }
//        Page<Case> paginationList = caseRepository.findAll(booleanExpression, pageRequest);
//        paginationList.get().forEach(item -> {
//            if (item.getCurrentAssignee() != null && item.getCurrentAssignee().getStaffUserparent() != null) {
//                item.setParentId(item.getCurrentAssignee().getStaffUserparent().getId().longValue());
//            }
//        });
//        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
//        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
//        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
//        genericDataDTO.setTotalPages(paginationList.getTotalPages());
//        return genericDataDTO;
//    }


//    public GenericDataDTO getChildTickets(Long caseId) {
//        //Case aCase = caseRepository.findById(caseId).orElse(null);
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        QCase qCase = QCase.case$;
//        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.parentTicketId.eq(caseId.intValue()));
//        List<Case> childCaseList = (List<Case>) caseRepository.findAll(booleanExpression);
//        List<CaseDTO> caseDTOS = new ArrayList<>();
//        for (Case childCase:childCaseList) {
//            caseDTOS.add(caseMapper.domainToDTO(childCase,new CycleAvoidingMappingContext()));
//        }
//        System.out.println("CASE_DTO$"+caseDTOS);
//        if(childCaseList.size()>0){
//            genericDataDTO.setDataList(caseDTOS);
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        }else{
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage("No child tickets linked with this ticket !!!!");
//        }
//
//        return genericDataDTO;
//
//    }

//    public void getCaseDataFromStrig(CaseDTO entity){
//        Case acase = new Case ();
//        acase = caseRepository.findById(entity.getCaseId()).orElse(null);
//        try {
//            List<CaseUpdateDTO> caseUpdateList = entity.getCaseUpdateList();
//            if (caseUpdateList != null) {
//                List<CaseUpdateDTO> caseUpdateDTOS = new ArrayList<>();
//                for (CaseUpdateDTO caseUpdateDTO : caseUpdateList) {
//                    CaseUpdateDTO caseUpdateDTOSlist = new CaseUpdateDTO(caseUpdateDTO);
//                    caseUpdateDTOS.add(caseUpdateDTOSlist);
//                }
//
//         /*   caseUpdateList.get(0).setCreateDateString(null);
//            caseUpdateList.get(0).setCreatedate(null);
//            caseUpdateList.get(0).setUpdatedate(null);*/
//
//                List<TicketServicemapping> ticketServicemappingList = entity.getTicketServicemappingList();
//                List<TicketAssignStaffMapping> ticketAssignStaffMappings = entity.getTicketAssignStaffMappings();
//
//                TicketReasonCategory ticketReasonCategory = ticketReasonCategoryRepo.findById(acase.getTicketReasonCategoryId()).orElse(null);
//
//                TicketReasonSubCategory ticketReasonSubCategory =  ticketReasonSubCategoryRepo.findById(acase.getReasonSubCategoryId()).orElse(null);
//
//                acase.setTicketReasonCategoryName(ticketReasonCategory.getCategoryName());
//                acase.setReasonSubCategoryName(ticketReasonSubCategory.getSubCategoryName());
//
//
//
//                TicketMessageIntegration ticketMessageIntegration = new TicketMessageIntegration(acase, caseUpdateDTOS, ticketServicemappingList );  //ticketAssignStaffMappings
//                //messageSender.send(ticketMessageIntegration, RabbitMqConstants.QUEUE_APIGW_TICKET_MESSAGE_INTEGRATION_SYSTEM);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }


//    public Map<String,Object> getDetailsforTatWorkflowDetailsEntry(CaseDTO caseDTO, Boolean isPickeddUp)
//    {
//        Map<String, Object> map = new HashMap<>();
//        if(caseDTO.getTeamHierarchyMappingId()!=null){
//            Optional<TeamHierarchyMapping> teamHierarchyMapping = teamHierarchyMappingRepo.findById(caseDTO.getTeamHierarchyMappingId().intValue());
//            if (teamHierarchyMapping.isPresent()) {
//                map.put("workFlowId", teamHierarchyMapping.get().getHierarchyId().toString());
//                map.put("eventName", CommonConstants.WORKFLOW_EVENT_NAME.CASE);
//                map.put("eventId", caseDTO.getCaseId().toString());
//                map.put("teamId", teamHierarchyMapping.get().getTeamId().toString());
//                if (isPickeddUp)
//                    map.put("fromPickedUp", "true");
//                else
//                    map.put("fromPickedUp", "false");
//                // caseService.updateFollowUpDateAndTimeForTicketAfterPickedUp(caseMapper.dtoToDomain(caseDTO,new CycleAvoidingMappingContext()));
//            }
//        }
//        return  map;
//    }

//    public void saveTATAudit(HashMap<String, Object> data) {
//        TicketTatAudits tatAudits = new TicketTatAudits();
//        tatAudits.setCaseId((Integer) data.get("tatAudit_caseId"));
//        tatAudits.setCaseStatus(data.get("tatAudit_caseStatus").toString());
//        tatAudits.setTatTime((Integer) data.get("tatAudit_tatTime"));
//        tatAudits.setTatAction(data.get("tatAudit_tatAction").toString());
//        tatAudits.setTatUnit(data.get("tatAudit_tatUnit").toString());
//        tatAudits.setSlaTime((Integer) data.get("tatAudit_slaTime"));
//        tatAudits.setSlaUnit(data.get("tatAudit_slaUnit").toString());
//        tatAudits.setTatStartTime(data.get("tatAudit_tatStartTime").toString());
//
//        tatAudits.setAssignStaffId((Integer) data.get("tatAudit_assignStaffId"));
//        tatAudits.setAssignStaffParentId((Integer) data.get("tatAudit_assignParentStaffId"));;
//        tatAudits.setCaseLevel(data.get("tatAudit_caseLevel").toString());
//        tatAudits.setNotificationFor(data.get("tatAudit_notificationFor").toString());
//        tatAudits.setIsTatBreached(data.get("tatAudit_isTatBreached").toString());
//        tatAudits.setIsSlaBreached(data.get("tatAudit_isSlaBreaced").toString());
//        if(tatAudits!=null){
//            tatAudits.setTatMessage((String) data.get("notificationMessage"));
//            tatAudits.setMessageMode(data.get("notificationMode").toString());
//            tatAudits.setMessageStatus(data.get("notificationStatus").toString());
//            tatAuditRepository.save(tatAudits);
//        }
//
//    }




//    public GenericDataDTO getTatAuditDetails(Long caseId) throws Exception {
//        Case aCase = caseRepository.findById(caseId).orElse(null);
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        List<TicketTatAudits> ticketTatAuditsList = new ArrayList<>();
//        ticketTatAuditsList = tatAuditRepository.findAllByCaseId(Math.toIntExact(aCase.getCaseId()));
//        for (TicketTatAudits tatAuditDetails:ticketTatAuditsList) {
//            StaffUser staffUser = staffUserService.getRepository().findById(tatAuditDetails.getAssignStaffId()).orElse(null);
//            tatAuditDetails.setStaffName(staffUser.getFullName());
//        }
//        if(ticketTatAuditsList.size()>0){
//            genericDataDTO.setDataList(ticketTatAuditsList);
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        }
//        return genericDataDTO;
//    }


//    public void saveSelfCareTicket(TicketMessageIntegration message) {
//        try{
//            System.out.println("Selfcare Ticket message :-> " + message);
//            CaseDTO selfCareCaseDTO = convertSelfCareCreateCaseRequestToCaseDTO(message);
//            caseService.saveEntity(selfCareCaseDTO,null);
//        } catch (CustomValidationException ex) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
//        } catch (Exception ex) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
//        }
//    }

//    private CaseDTO convertSelfCareCreateCaseRequestToCaseDTO(TicketMessageIntegration message) {
//        try {
//            CaseDTO selfCareCaseDTO = new CaseDTO();
//            selfCareCaseDTO.setCreatedFrom("Selfcare CWSC");
//            selfCareCaseDTO.setCaseTitle(message.getCaseTitle());
//            selfCareCaseDTO.setCaseType(message.getCaseType());
//            selfCareCaseDTO.setCaseFor(message.getCaseFor());
//            selfCareCaseDTO.setCaseOrigin(message.getCaseOrigin());
//            selfCareCaseDTO.setCaseStatus(message.getCaseStatus());
//            selfCareCaseDTO.setPriority(message.getPriority());
//            selfCareCaseDTO.setCustomersId(message.getCustomers());
//            selfCareCaseDTO.setCaseForPartner(message.getCaseForPartner());
//            selfCareCaseDTO.setNextFollowupDate(LocalDate.parse(message.getNextFollowupDate()));
//            selfCareCaseDTO.setNextFollowupTime(LocalTime.parse(message.getNextFollowupTime()));
//            selfCareCaseDTO.setFirstRemark(message.getFirstRemark());
//            selfCareCaseDTO = setCustomerSpecificCaseParams(selfCareCaseDTO, message);
//            selfCareCaseDTO = setReasonAndSubReasonCategory(selfCareCaseDTO);
//            selfCareCaseDTO.setSource(message.getSource());
//            selfCareCaseDTO.setSubSource(message.getSubSource());
//            selfCareCaseDTO.setDepartment(message.getDepartment());
//            selfCareCaseDTO.setCustomerAdditionalEmail(message.getCustomerAdditionalEmail());
//            selfCareCaseDTO.setCustomerAdditionalMobileNumber(message.getCustomerAdditionalMobileNumber());
//            selfCareCaseDTO.setCase_order(1L);
//            selfCareCaseDTO.setTicketServicemappingList(message.getTicketServicemappingList());
//            return selfCareCaseDTO;
//
//        } catch (CustomValidationException ex) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
//        } catch (Exception ex) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
//
//        }
//    }

//    private CaseDTO setReasonAndSubReasonCategory(CaseDTO selfCareCaseDTO) {
//        try{
//            List<TicketReasonCategory> reasonCategoryList = ticketReasonCategoryRepo.findAllByCategoryNameContainingIgnoreCase("Default");
//            List<TicketReasonSubCategory> reasonSubCategoryList = ticketReasonSubCategoryRepo.findAllBySubCategoryNameEqualsIgnoreCase("Default");
//            if(reasonSubCategoryList !=null && reasonCategoryList !=null){
//                selfCareCaseDTO.setTicketReasonCategoryId(reasonCategoryList.get(0).getId());
//                selfCareCaseDTO.setReasonSubCategoryId(reasonSubCategoryList.get(0).getId());
//            }
//            return selfCareCaseDTO;
//        }catch (CustomValidationException ex) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
//        } catch (Exception ex) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
//        }
//    }

//    private CaseDTO setCustomerSpecificCaseParams(CaseDTO selfCareCaseDTO, TicketMessageIntegration message) {
//        try {
//            Customers customer = customersService.getByUserName(message.getUserName());
//            if (customer != null) {
//                if(customer.getServicearea()!=null){
//                    selfCareCaseDTO.setServiceAreaId(customer.getServicearea().getId());
//                    selfCareCaseDTO.setServiceAreaName(customer.getServiceAreName());
//                }
//                selfCareCaseDTO.setMobile(customer.getMobile());
//                selfCareCaseDTO.setUserName(customer.getUsername());
//                selfCareCaseDTO.setCustomerName(customer.getCustname());
//                if(customer.getPartner()!=null){
//                    selfCareCaseDTO.setPartnerid(customer.getPartner().getId());
//                    selfCareCaseDTO.setPartnerName(customer.getPartnerName());
//                }
//                if(customer.getMvnoId()!=null){
//                    selfCareCaseDTO.setMvnoId(customer.getMvnoId());
//                }
//                if(customer.getBuId()!=null){
//                    selfCareCaseDTO.setBuId(customer.getBuId());
//                }
//                if(customer.getLcoId()!=null){
//                    selfCareCaseDTO.setLcoId(customer.getLcoId());
//                }
//                return selfCareCaseDTO;
//            }
//            return selfCareCaseDTO;
//        } catch (CustomValidationException ex) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
//        } catch (Exception ex) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
//        }
//    }
}


