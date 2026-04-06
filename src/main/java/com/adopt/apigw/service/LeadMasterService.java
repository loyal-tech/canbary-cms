package com.adopt.apigw.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.lead.*;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.BusinessUnit.repository.BusinessUnitRepository;
import com.adopt.apigw.modules.Matrix.domain.Matrix;
import com.adopt.apigw.modules.Matrix.domain.MatrixDetails;
import com.adopt.apigw.modules.Matrix.domain.TatMatrixWorkFlowDetails;
import com.adopt.apigw.modules.Matrix.repository.MatrixRepository;
import com.adopt.apigw.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.Teams.domain.Hierarchy;
import com.adopt.apigw.modules.Teams.domain.TeamHierarchyMapping;
import com.adopt.apigw.modules.Teams.domain.Teams;
import com.adopt.apigw.modules.Teams.repository.HierarchyRepository;
import com.adopt.apigw.modules.Teams.repository.TeamHierarchyMappingRepo;
import com.adopt.apigw.modules.Teams.repository.TeamsRepository;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.workflow.service.WorkflowAssignStaffMappingService;
import com.adopt.apigw.pojo.LeadCustPlanMapppingPojo;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CustDocumentVerificationMsg;
import com.adopt.apigw.rabbitMq.message.LeadCreationMsg;
import com.adopt.apigw.rabbitMq.message.LeadMasterPojoMessage;
import com.adopt.apigw.rabbitMq.message.SendApproverForLeadMsg;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.common.WorkflowAuditService;
import com.adopt.apigw.service.postpaid.ChargeService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.TatUtils;
import com.google.gson.Gson;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.adopt.apigw.modules.customerDocDetails.repository.CustomerDocDetailsRepository;
import com.adopt.apigw.repository.LeadCustChargeDetailsRepository;
import com.adopt.apigw.repository.LeadCustMacMapppingRepository;
import com.adopt.apigw.repository.LeadCustPlanMapppingRepository;
import com.adopt.apigw.repository.LeadCustomerAddressRepository;
import com.adopt.apigw.repository.LeadDocDetailsRepository;
import com.adopt.apigw.repository.LeadMasterRepository;
import com.adopt.apigw.repository.LeadSourceRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;

@Service
public class LeadMasterService {

	public static final String MODULE = "[LeadMasterService]";

	private final Logger logger = LoggerFactory.getLogger(LeadMasterService.class);

	@Autowired
	private LeadMasterRepository leadMasterRepository;

	@Autowired
	private LeadCustPlanMapppingRepository leadCustPlanMapppingRepository;

	@Autowired
	private LeadCustomerAddressRepository leadCustomerAddressRepository;

	@Autowired
	private LeadCustChargeDetailsRepository leadCustChargeDetailsRepository;

	@Autowired
	private CustomerDocDetailsRepository customerDocDetailsRepository;

	@Autowired
	private LeadCustMacMapppingRepository leadCustMacMapppingRepository;

	@Autowired
	private StaffUserRepository staffUserRepository;

	@Autowired
	private LeadSourceRepository leadSourceRepository;

	@Autowired
	private LeadDocDetailsRepository leadDocDetailsRepository;
	@Autowired
	MessageSender messageSender;
	@Autowired
	NotificationTemplateRepository templateRepository;
	@Autowired
	StaffUserService staffUserService;
	@Autowired
	HierarchyService hierarchyService;
	@Autowired
	private ClientServiceSrv  clientServiceSrv;
	@Autowired
	private TeamHierarchyMappingRepo teamHierarchyMappingRepo;
	@Autowired
	TeamsRepository teamsRepository;
	@Autowired
	private WorkflowAuditService workflowAuditService;

	public Integer MAX_PAGE_SIZE;

	public Map<String, String> sortColMap = new HashMap<>();

	public PageRequest pageRequest = null;

	@Autowired
	private MvnoRepository mvnoRepository;

	@Autowired
	private BusinessUnitRepository businessUnitRepository;

	@Autowired
	private ChargeService chargeService;
	@Autowired
	MatrixRepository matrixRepository;
	@Autowired
	HierarchyRepository hierarchyRepository;
	@Autowired
	TatUtils tatUtils;
	@Autowired
	TatMatrixWorkFlowDetailsRepo tatMatrixWorkFlowDetailsRepo;

	@Autowired
	ClientServiceRepository clientServiceRepository;

	@Autowired
	WorkflowAssignStaffMappingService workflowAssignStaffMappingService;
	@Autowired
	private KafkaMessageSender kafkaMessageSender;


	@Autowired
	private CustomersService customersService;

	@Transactional
	public LeadMasterPojo save(LeadMasterPojo leadMasterPojo) {
		String SUBMODULE = MODULE + "save()";
		try {
			LeadMaster leadMaster = new LeadMaster(leadMasterPojo, null);
			if (leadMaster.getLeadSource() != null) {
				leadMaster.setLeadSource(leadMaster.getLeadSource());
				this.leadSourceRepository.save(leadMaster.getLeadSource());
			}

			if(leadMasterPojo.getIsLeadQuickInv()!= null)
				leadMaster.setIsLeadQuickInv(leadMasterPojo.getIsLeadQuickInv()==true?1:0);

			LeadMaster savedLeadMaster = this.leadMasterRepository.save(leadMaster);
			leadMaster = new LeadMaster(leadMasterPojo);
			savedLeadMaster = saveAllLeadMasterListsEntity(leadMaster, savedLeadMaster);
			StaffUser loggedInUser = staffUserService.get(Integer.valueOf(leadMasterPojo.getCreatedBy()),savedLeadMaster.getMvnoId().intValue());
			LeadMgmtWfDTO leadMgmtWfDTO=new LeadMgmtWfDTO(leadMaster);
			leadMgmtWfDTO.setCurrentLoggedInStaffId(leadMasterPojo.getCurrentLoggedInStaffId());
			leadMgmtWfDTO.setNextApproveStaffId(loggedInUser.getId());

			if ( Objects.nonNull( leadMaster.getStatus()) &&  leadMaster.getStatus().equals(CommonConstants.LEADINQ) || (Objects.nonNull(leadMgmtWfDTO.getStatus()) && leadMgmtWfDTO.getStatus().equals(CommonConstants.LEADRINQ))) {
				if (clientServiceRepository.findValueByNameandMvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,leadMaster.getMvnoId().intValue()).equals("TRUE")) {
					Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(Math.toIntExact(loggedInUser.getMvnoId()), leadMaster.getBuId()== null ? null : leadMaster.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD, CommonConstants.HIERARCHY_TYPE, false, true, leadMaster);
					if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
						TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Integer.valueOf(map.get("nextTatMappingId"))).orElse(null);
						Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
						StaffUser assignedStaffUser = staffUserService.get(Integer.valueOf(map.get("staffId")),savedLeadMaster.getMvnoId().intValue());

						leadMgmtWfDTO.setNextTeamMappingId(Integer.valueOf(map.get("nextTatMappingId")));
						leadMgmtWfDTO.setNextApproveStaffId(Integer.valueOf(map.get("staffId")));
						leadMgmtWfDTO.setId(leadMgmtWfDTO.getId());
						leadMgmtWfDTO.setCurrentLoggedInStaffId(loggedInUser.getId());
						leadMgmtWfDTO.setTeamName(teams.getName());
						leadMgmtWfDTO.setFlag("Assigned");
						savedLeadMaster.setNextApproveStaffId(Integer.valueOf(map.get("staffId")));
						savedLeadMaster.setNextTeamMappingId(Integer.valueOf(map.get("nextTatMappingId")));
						String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD + " for Lead Name : " + " ' " + leadMgmtWfDTO.getFirstname() + " '";
						workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD, leadMgmtWfDTO.getId().intValue(), leadMgmtWfDTO.getFirstname(), leadMgmtWfDTO.getId().intValue(), assignedStaffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedStaffUser.getUsername());
						hierarchyService.sendWorkflowAssignActionMessage(assignedStaffUser.getCountryCode(), assignedStaffUser.getPhone(), assignedStaffUser.getEmail(), leadMaster.getMvnoId().intValue(), assignedStaffUser.getFullName(), action,assignedStaffUser.getId().longValue());

					} else {
						leadMgmtWfDTO.setNextApproveStaffId(loggedInUser.getId());
						leadMgmtWfDTO.setNextTeamMappingId(null);
						leadMgmtWfDTO.setId(leadMgmtWfDTO.getId());
						savedLeadMaster.setNextApproveStaffId(Integer.valueOf(leadMasterPojo.getCreatedBy()));
						savedLeadMaster.setNextTeamMappingId(null);
						leadMgmtWfDTO.setCurrentLoggedInStaffId(loggedInUser.getId());
						leadMgmtWfDTO.setTeamName("-");
//						leadMgmtWfDTO.setLeadStatus("Converted");
						leadMgmtWfDTO.setFlag("Assigned");
						String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD + " for Lead Name : " + " ' " + leadMgmtWfDTO.getFirstname() + " '";
						workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD, leadMgmtWfDTO.getId().intValue(), leadMgmtWfDTO.getFirstname(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + loggedInUser.getUsername());
						hierarchyService.sendWorkflowAssignActionMessage(loggedInUser.getCountryCode(), loggedInUser.getPhone(), loggedInUser.getEmail(), leadMaster.getMvnoId().intValue(), loggedInUser.getFullName(), action,loggedInUser.getId().longValue());
					}
				}
				else {
					if(Objects.nonNull(leadMasterPojo.isLeadFromCWSC()) && !leadMasterPojo.isLeadFromCWSC()) {
						if(clientServiceRepository.findValueByNameandMvnoId(ClientServiceConstant.IS_WORKFLOW_ALL_ASSIGN,loggedInUser.getMvnoId()) != null && clientServiceRepository.findValueByNameandMvnoId(ClientServiceConstant.IS_WORKFLOW_ALL_ASSIGN,loggedInUser.getMvnoId()).equalsIgnoreCase("TRUE")){
							logger.warn("-------------- IS_WORKFLOW_ALL_ASSIGN : TRUE ---------- FOR----- LEAD : "+leadMaster.getFirstname());
							String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD + " for Lead Name : " + " ' " + leadMaster.getFirstname() + " '";
							Map<String, Object> map = hierarchyService.getTeamForNextApprove(leadMaster.getMvnoId().intValue(), leadMaster.getBuId() == null ? null : leadMaster.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD, CommonConstants.HIERARCHY_TYPE, true, false, hierarchyService.leadMasterToLeadMgmtDTO(leadMaster));
							List<StaffUserPojo> staffuserlist = (List<StaffUserPojo>) map.get("assignableStaff");
							if(staffuserlist != null && staffuserlist.size() >0){
								if(clientServiceRepository.findValueByNameandMvnoId(CommonConstants.LEAD_CAF_VISIBILITY_RISTRICT,loggedInUser.getMvnoId()) != null && clientServiceRepository.findValueByNameandMvnoId(CommonConstants.LEAD_CAF_VISIBILITY_RISTRICT,loggedInUser.getMvnoId()).equals("true")){
									logger.warn("-------------- LEAD_CAF_VISIBILITY_RISTRICT : TRUE ---------- FOR----- LEAD : "+leadMaster.getFirstname());
									if(customersService.checkstaffExistinCAFworkflow(loggedInUser.getId(), Integer.parseInt(map.get("nextTeamHierarchyMappingId").toString()))){
										logger.warn("-------------- LoggedIn Staff: " + loggedInUser.getFullName()+" is available in First Team of Workflow ---------- FOR LEAD : "+leadMaster.getFirstname());
										leadMaster.setNextApproveStaffId(loggedInUser.getId());
										leadMaster.setNextTeamMappingId((Integer) map.get("nextTeamHierarchyMappingId"));
										leadMgmtWfDTO.setNextApproveStaffId(loggedInUser.getId());
										leadMgmtWfDTO.setNextTeamMappingId((Integer) map.get("nextTeamHierarchyMappingId"));
									}
								}
								else{
									logger.warn("-------------- LEAD_CAF_VISIBILITY_RISTRICT : FALSE ---------- FOR----- LEAD : "+leadMaster.getFirstname());
									leadMaster.setNextTeamMappingId((Integer) map.get("nextTeamHierarchyMappingId"));
									leadMgmtWfDTO.setNextTeamMappingId(leadMaster.getNextTeamMappingId());
									leadMaster.setNextApproveStaffId(null);
									leadMgmtWfDTO.setNextApproveStaffId(null);
									workflowAuditService.saveAudit(null, CommonConstants.EVENT_NAME.LEAD, leadMaster.getId().intValue(), leadMaster.getFirstname(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + loggedInUser.getUsername());
									hierarchyService.sendWorkflowAssignActionMessage(loggedInUser.getCountryCode(), loggedInUser.getPhone(), loggedInUser.getEmail(), leadMaster.getMvnoId().intValue(), loggedInUser.getFullName(), action,loggedInUser.getId().longValue());
									if(map.get("tat_id").equals("null") && !map.get("current_tat_id").equals("null") ){
										map.put("tat_id",map.get("current_tat_id"));
									}
									if(!map.get("tat_id").equals("null")) {
										Optional<Matrix> matrixDetails = matrixRepository.findById(Long.valueOf((String) map.get("tat_id")));
										if (matrixDetails.isPresent()) {
											Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();
											Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));
											if (newMatrixDetails.isPresent()) {
												leadMaster = (LeadMaster) tatUtils.UpdateDateTimefortat(newMatrixDetails.get(), leadMaster, Nextvalue);
												leadMgmtWfDTO.setNextfollowupdate(leadMaster.getNextfollowupdate().toString());
												leadMgmtWfDTO.setNextfollowuptime(leadMaster.getNextfollowuptime().truncatedTo(ChronoUnit.SECONDS).toString());
												//details.setStaffId(details.getParentId());
											}
										}
									}
									workflowAssignStaffMappingService.assignWorkflowToStaff(null, CommonConstants.WORKFLOW_EVENT_NAME.LEAD, savedLeadMaster, map);
								}

							}
						}else{
							logger.warn("-------------- IS_WORKFLOW_ALL_ASSIGN : FALSE ---------- FOR----- LEAD : "+leadMaster.getFirstname());
							leadMgmtWfDTO.setNextApproveStaffId(loggedInUser.getId());
							leadMgmtWfDTO.setNextTeamMappingId(null);
							leadMgmtWfDTO.setId(leadMaster.getId());
							leadMgmtWfDTO.setCurrentLoggedInStaffId(loggedInUser.getId());
							leadMgmtWfDTO.setTeamName("-");
							leadMaster.setNextApproveStaffId(loggedInUser.getId());
							leadMaster.setNextTeamMappingId(null);
							leadMgmtWfDTO.setFlag("Assigned");
							Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(Math.toIntExact(loggedInUser.getMvnoId()), leadMaster.getBuId() == null ? null : leadMaster.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD, CommonConstants.HIERARCHY_TYPE, false, true, leadMgmtWfDTO);
							Hierarchy hierarchy = null;
							HashMap<String, String> tatMapDetails = new HashMap<>();
							Optional<StaffUser> staffUser = staffUserRepository.findById(loggedInUser.getId());
							if (!CollectionUtils.isEmpty(map) && !map.get("tat_id").equals("null")) {
								Long tat_id = Long.valueOf(String.valueOf(map.get("tat_id")));
								Optional<Matrix> matrix = matrixRepository.findById(tat_id);
								List<Long> buidlist = staffUser.get().getBusinessUnitNameList().stream().map(i -> i.getId()).collect(Collectors.toList());
								if (matrix.isPresent()) {
									if (buidlist != null && buidlist.size() > 0) {
										hierarchy = hierarchyRepository.findByMvnoIdAndBuIdInAndEventNameAndIsDeleted(leadMaster.getMvnoId().intValue(), buidlist, CommonConstants.WORKFLOW_EVENT_NAME.LEAD, false);
									} else {
										hierarchy = hierarchyRepository.findByBuIdIsNullAndMvnoIdAndEventNameAndIsDeleted(leadMaster.getMvnoId().intValue(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD, false);
									}
									if (hierarchy != null) {
										if (matrix.isPresent()) {
											Matrix matrix1 = matrix.get();
											savedLeadMaster.setSlaUnit(matrix1.getRunit());
											Long minutes;
											Long hours;
											if (matrix1.getRunit().equalsIgnoreCase("MIN")) {
												minutes = Long.valueOf(matrix1.getRtime());

												LocalTime currentTimenow = LocalTime.now();
												LocalTime nextFollowupTime = currentTimenow.plusMinutes(minutes);
												if (nextFollowupTime.getHour() < currentTimenow.getHour() ||
														(nextFollowupTime.getHour() == currentTimenow.getHour() && nextFollowupTime.getMinute() < currentTimenow.getMinute())) {
													LocalDate nextFollowupDate = LocalDate.now().plusDays(1);

													savedLeadMaster.setNextfollowupdate(nextFollowupDate);
													savedLeadMaster.setNextfollowuptime(nextFollowupTime);
												} else {
													savedLeadMaster.setNextfollowupdate(LocalDate.now());
													savedLeadMaster.setNextfollowuptime(nextFollowupTime);
												}
											} else if (matrix1.getRunit().equalsIgnoreCase("HOUR")) {
												hours = Long.valueOf(matrix1.getRtime());
												LocalTime currentTimenow = LocalTime.now();
												long total_hours = currentTimenow.getHour() + hours;
												if (total_hours >= 24) {
													long days = total_hours / 24;
													long remainingHours = total_hours % 24;
													long remainingMinutes = currentTimenow.getMinute();
													savedLeadMaster.setNextfollowupdate(LocalDate.now().plusDays(days));
													savedLeadMaster.setNextfollowuptime(LocalTime.of((int) remainingHours, (int) remainingMinutes));
												} else {
													savedLeadMaster.setNextfollowupdate(LocalDate.now());
													savedLeadMaster.setNextfollowuptime(currentTimenow.plusHours(hours));
												}
											} else {
												savedLeadMaster.setNextfollowupdate(LocalDate.now().plusDays(Long.valueOf(matrix1.getRtime())));
											}
											if (staffUser.get().getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
												tatMapDetails.put("workFlowId", map.get("workFlowId").toString());
												tatMapDetails.put("eventId", map.get("eventId").toString());
												tatMapDetails.put("eventName", map.get("eventName").toString());
												tatMapDetails.put("tat_id", map.get("tat_id").toString());
												tatMapDetails.put("orderNo", map.get("orderNo").toString());
												TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
														new TatMatrixWorkFlowDetails(new Long("1"), "Level 1", staffUser.get().getId(),
																Long.valueOf(tatMapDetails.get("workFlowId")), Long.valueOf(tatMapDetails.get("tat_id")),
																(staffUser != null && staffUser.get().getStaffUserparent() != null) ? staffUser.get().getStaffUserparent().getId() : null, LocalDateTime.now(),
																matrix1.getRtime().toString(), matrix1.getRunit(), "Notification", true, tatMapDetails.get("nextTatMappingId") != null ? Integer.valueOf(tatMapDetails.get("nextTatMappingId")) : null,
																savedLeadMaster.getId().intValue(), tatMapDetails.get("eventName"), tatMapDetails.get("eventId") != null ? Integer.valueOf(tatMapDetails.get("eventId")) : null, CommonConstants.NOTIFICATION_TYPE_STAFF, null, true);
//                                               tatUtils.saveOrUpdateDataForTatMatrix( tatMapDetails, staffUser.get(),subscriber.getId(),null);
												tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
												//tatUtils.saveOrUpdateDataForTatMatrix( tatMapDetails, staffUser.get(),customers.getId(),null);
												//	tatUtils.saveOrUpdateDataForTatMatrix(tatMapDetails, staffUser.get(), leadMaster.getId().intValue(), null);
											}

										}
									}
								}
							}
							if (savedLeadMaster.getNextfollowuptime() != null) {
								leadMgmtWfDTO.setNextfollowuptime(savedLeadMaster.getNextfollowuptime().truncatedTo(ChronoUnit.SECONDS).toString());
								leadMgmtWfDTO.setNextfollowupdate(savedLeadMaster.getNextfollowupdate().toString());
							}
							String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD + " for Lead Name : " + " ' " + leadMaster.getFirstname() + " '";
							workflowAuditService.saveAudit(null, CommonConstants.EVENT_NAME.LEAD, leadMaster.getId().intValue(), leadMaster.getFirstname(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + loggedInUser.getUsername());
							hierarchyService.sendWorkflowAssignActionMessage(loggedInUser.getCountryCode(), loggedInUser.getPhone(), loggedInUser.getEmail(), leadMaster.getMvnoId().intValue(), loggedInUser.getFullName(), action,loggedInUser.getId().longValue());
						}
					}
					else{
						String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD + " for Lead Name : " + " ' " + leadMaster.getFirstname() + " '";
						Map<String, Object> map = hierarchyService.getTeamForNextApprove(leadMaster.getMvnoId().intValue(), leadMaster.getBuId() == null ? null : leadMaster.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD, CommonConstants.HIERARCHY_TYPE, true, false, hierarchyService.leadMasterToLeadMgmtDTO(leadMaster));
						List<StaffUserPojo> staffuserlist = (List<StaffUserPojo>) map.get("assignableStaff");
						if(staffuserlist != null && staffuserlist.size() >0){
							if(clientServiceRepository.findValueByNameandMvnoId(CommonConstants.LEAD_CAF_VISIBILITY_RISTRICT,loggedInUser.getMvnoId()) != null && clientServiceRepository.findValueByNameandMvnoId(CommonConstants.LEAD_CAF_VISIBILITY_RISTRICT,loggedInUser.getMvnoId()).equals("true")){
								logger.warn("-------------- LEAD_CAF_VISIBILITY_RISTRICT : TRUE ---------- FOR----- LEAD : "+leadMaster.getFirstname()+" lead is flag for isLeadFromCWSC either is null or true" );
								logger.warn("-------------- LEAD_CAF_VISIBILITY_RISTRICT : TRUE ---------- FOR----- LEAD : "+leadMaster.getFirstname());
								if(customersService.checkstaffExistinCAFworkflow(loggedInUser.getId(), Integer.parseInt(map.get("nextTeamHierarchyMappingId").toString()))){
									leadMaster.setNextApproveStaffId(loggedInUser.getId());
									leadMaster.setNextTeamMappingId((Integer) map.get("nextTeamHierarchyMappingId"));
									leadMgmtWfDTO.setNextApproveStaffId(loggedInUser.getId());
									leadMgmtWfDTO.setNextTeamMappingId((Integer) map.get("nextTeamHierarchyMappingId"));
								}
							}
							else{
								logger.warn("-------------- LEAD_CAF_VISIBILITY_RISTRICT : FALSE ---------- FOR----- LEAD : "+leadMaster.getFirstname()+" lead is flag for isLeadFromCWSC either is null or true" );
								logger.warn("-------------- LEAD_CAF_VISIBILITY_RISTRICT : FALSE ---------- FOR----- LEAD : "+leadMaster.getFirstname());
								leadMaster.setNextTeamMappingId((Integer) map.get("nextTeamHierarchyMappingId"));
								leadMgmtWfDTO.setNextTeamMappingId(leadMaster.getNextTeamMappingId());
								leadMaster.setNextApproveStaffId(null);
								leadMgmtWfDTO.setNextApproveStaffId(null);
								workflowAuditService.saveAudit(null, CommonConstants.EVENT_NAME.LEAD, leadMaster.getId().intValue(), leadMaster.getFirstname(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + loggedInUser.getUsername());
								hierarchyService.sendWorkflowAssignActionMessage(loggedInUser.getCountryCode(), loggedInUser.getPhone(), loggedInUser.getEmail(), leadMaster.getMvnoId().intValue(), loggedInUser.getFullName(), action,loggedInUser.getId().longValue());
								if(map.get("tat_id").equals("null") && !map.get("current_tat_id").equals("null") ){
									map.put("tat_id",map.get("current_tat_id"));
								}
								if(!map.get("tat_id").equals("null")) {
									Optional<Matrix> matrixDetails = matrixRepository.findById(Long.valueOf((String) map.get("tat_id")));
									if (matrixDetails.isPresent()) {
										Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();
										Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));
										if (newMatrixDetails.isPresent()) {
											leadMaster = (LeadMaster) tatUtils.UpdateDateTimefortat(newMatrixDetails.get(), leadMaster, Nextvalue);
											leadMgmtWfDTO.setNextfollowupdate(leadMaster.getNextfollowupdate().toString());
											leadMgmtWfDTO.setNextfollowuptime(leadMaster.getNextfollowuptime().truncatedTo(ChronoUnit.SECONDS).toString());
											//details.setStaffId(details.getParentId());
										}
									}
								}
								workflowAssignStaffMappingService.assignWorkflowToStaff(null, CommonConstants.WORKFLOW_EVENT_NAME.LEAD, savedLeadMaster, map);
							}

						}
						else{
							logger.warn("--------------- Lead workflow is assign to the ISP staff ------------------");
							Mvno leadMvno = mvnoRepository.findById(leadMaster.getMvnoId()).get();
							StaffUser staffUser = staffUserRepository.findByUsernameAndMvnoId(leadMvno.getUsername(), leadMaster.getMvnoId().intValue());
							leadMaster.setNextTeamMappingId(null);
							leadMgmtWfDTO.setNextTeamMappingId(null);
							if(clientServiceRepository.findValueByNameandMvnoId(CommonConstants.LEAD_CAF_VISIBILITY_RISTRICT,loggedInUser.getMvnoId()) != null && clientServiceRepository.findValueByNameandMvnoId(CommonConstants.LEAD_CAF_VISIBILITY_RISTRICT,loggedInUser.getMvnoId()).equals("true")){
								if(customersService.checkstaffExistinCAFworkflow(loggedInUser.getId(), Integer.parseInt(map.get("nextTeamHierarchyMappingId").toString()))){
									leadMaster.setNextApproveStaffId(staffUser.getId());
									leadMaster.setNextTeamMappingId((Integer) map.get("nextTeamHierarchyMappingId"));
									leadMgmtWfDTO.setNextApproveStaffId(staffUser.getId());
									leadMgmtWfDTO.setNextTeamMappingId((Integer) map.get("nextTeamHierarchyMappingId"));
								}
							}
							else if(staffUser != null ){
								leadMaster.setNextApproveStaffId(staffUser.getId());
								leadMgmtWfDTO.setNextApproveStaffId(staffUser.getId());
							}
						}


					}
					leadMasterRepository.save(leadMaster);
					SendApproverForLeadMsg sendApproverForLeadMsg = new SendApproverForLeadMsg(leadMgmtWfDTO);
//					messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL);
					kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg,SendApproverForLeadMsg.class.getSimpleName() ));
				}
			}

			/*called method for send notification*/
			if(leadMaster.getBuId()!=null){
				sendLeadCreationMessage(leadMaster.getFirstname(), leadMaster.getMobile(), leadMaster.getEmail(), leadMaster.getLeadNo(), leadMaster.getMvnoId().intValue(), leadMaster.getBuId().intValue(), Long.valueOf(leadMasterPojo.getCreatedBy()));
			}else{
				sendLeadCreationMessage(leadMaster.getFirstname(), leadMaster.getMobile(), leadMaster.getEmail(), leadMaster.getLeadNo(), leadMaster.getMvnoId().intValue(), null, Long.valueOf(leadMasterPojo.getCreatedBy()));
			}


			logger.info("Lead has been created successfully");
			return new LeadMasterPojo(leadMaster);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}

	}

	public LeadMaster saveAllLeadMasterListsEntity(LeadMaster leadMaster, LeadMaster savedLeadMaster) {
		String SUBMODULE = MODULE + "saveAllLeadMasterListsEntity()";
		try {
			List<LeadCustPlanMappping> leadCustPlanMappingList = this.leadCustPlanMapppingRepository
					.findByLeadMasterId(savedLeadMaster.getId());
			if (leadCustPlanMappingList != null && leadCustPlanMappingList.size() > 0) {
				this.leadCustPlanMapppingRepository.deleteAll(leadCustPlanMappingList);
			}
			if (leadMaster.getPlanMappingList() != null && leadMaster.getPlanMappingList().size() > 0) {
				leadMaster.getPlanMappingList()
						.forEach(custPlanMapping -> custPlanMapping.setLeadMaster(savedLeadMaster));
				savedLeadMaster.setPlanMappingList(
						this.leadCustPlanMapppingRepository.saveAll(leadMaster.getPlanMappingList()));
			}

			List<LeadCustomerAddress> leadCustomerAddressList = this.leadCustomerAddressRepository
					.findByLeadMasterId(savedLeadMaster.getId());
			if (leadCustomerAddressList != null && leadCustomerAddressList.size() > 0) {
				this.leadCustomerAddressRepository.deleteAll(leadCustomerAddressList);
			}
			// save addresslist
			if (leadMaster.getAddressList() != null && leadMaster.getAddressList().size() > 0) {
				leadMaster.getAddressList().forEach(custAddress -> custAddress.setLeadMaster(savedLeadMaster));
				savedLeadMaster.setAddressList(this.leadCustomerAddressRepository.saveAll(leadMaster.getAddressList()));
			}

			List<LeadCustChargeDetails> overChrageList = this.leadCustChargeDetailsRepository
					.findByLeadMasterId(savedLeadMaster.getId());
			if (overChrageList != null && overChrageList.size() > 0) {
				this.leadCustChargeDetailsRepository.deleteAll(overChrageList);
			}

			// save overChargeList
			if (leadMaster.getOverChargeList() != null && leadMaster.getOverChargeList().size() > 0) {
				leadMaster.getOverChargeList().forEach(overcharge -> overcharge.setLeadMaster(savedLeadMaster));
				savedLeadMaster.setOverChargeList(
						this.leadCustChargeDetailsRepository.saveAll(leadMaster.getOverChargeList()));
			}

			// save indiChargeList
			if (leadMaster.getIndiChargeList() != null && leadMaster.getIndiChargeList().size() > 0) {
				leadMaster.getIndiChargeList().forEach(indicharge -> indicharge.setLeadMaster(savedLeadMaster));
				savedLeadMaster.setIndiChargeList(
						this.leadCustChargeDetailsRepository.saveAll(leadMaster.getIndiChargeList()));
			}

			List<LeadCustMacMappping> leadCustMacMappingList = this.leadCustMacMapppingRepository
					.findByLeadMasterId(savedLeadMaster.getId());
			if (leadCustMacMappingList != null && leadCustMacMappingList.size() > 0) {
				this.leadCustMacMapppingRepository.deleteAll(leadCustMacMappingList);
			}

			// save custMacMapppingList
			if (leadMaster.getCustMacMapppingList() != null && leadMaster.getCustMacMapppingList().size() > 0) {
				leadMaster.getCustMacMapppingList()
						.forEach(custMacMapping -> custMacMapping.setLeadMaster(savedLeadMaster));
				savedLeadMaster.setCustMacMapppingList(
						this.leadCustMacMapppingRepository.saveAll(leadMaster.getCustMacMapppingList()));
			}

			List<LeadDocDetails> leadDocDetailsList = this.leadDocDetailsRepository
					.findByLeadMasterId(savedLeadMaster.getId());
			if (leadDocDetailsList != null && leadDocDetailsList.size() > 0) {
				this.leadDocDetailsRepository.deleteAll(leadDocDetailsList);
			}

			// save leadDocumentList
			if (leadMaster.getLeadDocDetailsList() != null && leadMaster.getLeadDocDetailsList().size() > 0) {
				leadMaster.getLeadDocDetailsList()
						.forEach(leadDocDetails -> leadDocDetails.setLeadMaster(savedLeadMaster));
				savedLeadMaster.setLeadDocDetailsList(
						this.leadDocDetailsRepository.saveAll(leadMaster.getLeadDocDetailsList()));
			}



			return savedLeadMaster;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}

	}

	public GenericDataDTO getAllLeadList(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
		PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, CommonConstants.SORT_ORDER_DESC);
		QLeadMaster qLeadMaster = QLeadMaster.leadMaster;
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		Long mvnoId = getLoggedInUser().getMvnoId().longValue();
		Page<LeadMaster> paginationList = null;
		BooleanExpression booleanExpression = qLeadMaster.isNotNull().and(qLeadMaster.isDeleted.eq(false).and(qLeadMaster.mvnoId.eq(mvnoId)));
		try{
			if(filterList.size() != 0){
				for(GenericSearchModel searchModel : filterList) {
					if (searchModel.getFilterCondition() != null) {
						if (searchModel.getFilterColumn().equalsIgnoreCase("status")) {
							booleanExpression = booleanExpression.and(qLeadMaster.leadStatus.equalsIgnoreCase(searchModel.getFilterValue()));
						}
						if (searchModel.getFilterColumn().equalsIgnoreCase("mobile")) {
							booleanExpression = booleanExpression.and(qLeadMaster.mobile.equalsIgnoreCase(searchModel.getFilterValue().replaceAll("\\s+|[^\\w\\s]", "")));
						}
						if (searchModel.getFilterColumn().replaceAll("\\s+|[^\\w\\s]", "").equalsIgnoreCase("createdby")) {
							booleanExpression = booleanExpression.and(qLeadMaster.createdBy.equalsIgnoreCase(searchModel.getFilterValue()));
						}
						if (searchModel.getFilterColumn().equalsIgnoreCase("name")) {
							booleanExpression = booleanExpression.and(qLeadMaster.username.eq(searchModel.getFilterValue()));
						}
						if(searchModel.getFilterColumn().equalsIgnoreCase("Last Modified On")){
							booleanExpression = booleanExpression.and(qLeadMaster.lastModifiedBy.equalsIgnoreCase(searchModel.getFilterValue()));
						}
						if (searchModel.getFilterCondition().equalsIgnoreCase(SearchConstants.AND)) {
								paginationList = leadMasterRepository.findAll(booleanExpression, pageRequest);
						}
					}
				}
			}else {
				paginationList = leadMasterRepository.findAll(booleanExpression, pageRequest);
			}
			genericDataDTO.setDataList(paginationList.getContent());
				genericDataDTO.setResponseCode(HttpStatus.OK.value());
				genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
				genericDataDTO.setTotalRecords(paginationList.getTotalElements());
				genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
				genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
				genericDataDTO.setTotalPages(paginationList.getTotalPages());
				return genericDataDTO;


		}catch (Exception ex){
			ApplicationLogger.logger.error("Unable to search  lead by type response{}exceeption{}", APIConstants.FAIL, ex.getStackTrace());
			throw ex;
		}

	}

	public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
		this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
		if (pageSize > MAX_PAGE_SIZE) pageSize = MAX_PAGE_SIZE;

		if (null != sortColMap && 0 < sortColMap.size()) {
			if (sortColMap.containsKey(sortBy)) {
				sortBy = sortColMap.get(sortBy);
			}
		}

		if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
			pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
		else pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).ascending());
		return pageRequest;
	}

	public LeadMasterPojo saveLeadMasterFromQuickInv(LeadMasterPojo pojo){
		String SUBMODULE = MODULE + "saveLeadMasterFromQuickInv()";
		Long staffId = pojo.getCreatedBy()!= null? Long.parseLong(pojo.getCreatedBy()):null;
		try {
			if (pojo.getMvnoId() != null) {
				Optional<Mvno> optionalMvno = mvnoRepository.findById(pojo.getMvnoId());
				if (!optionalMvno.isPresent()) {
					throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR,
							"MVNO is not set for Api Gateway module. Please configure that.", null);

				}
			}
			pojo.setIsLeadQuickInv(true);
			LeadMaster leadMaster = new LeadMaster(pojo);
			leadMaster.setLeadStatus("Inquiry");

//			if (leadMaster.getFeasibility() != null && leadMaster.getFeasibility().equalsIgnoreCase("N/A"))
//				leadMaster.setFeasibilityRequired("NA");
//			else
			leadMaster.setFeasibilityRequired(leadMaster.getFeasibility());
			if (pojo.getPlanMappingList() != null && pojo.getPlanMappingList().size() > 0) {
				List<LeadCustPlanMappping> custPlanMapppingList = new ArrayList<>();
				for (LeadCustPlanMapppingPojo custPlanMapppingPojo : pojo.getPlanMappingList()) {
					custPlanMapppingList.add(new LeadCustPlanMappping(custPlanMapppingPojo));
				}
				leadMaster.setPlanMappingList(custPlanMapppingList);
			}
			LeadMaster savedLeadMaster = this.leadMasterRepository.save(leadMaster);
			LeadMasterPojo updatedLeadMasterPojo = new LeadMasterPojo(savedLeadMaster);

			try {
				// send message
//				List<LeadDocDetailsDTO> leadDocDetailsDTOList = new ArrayList<LeadDocDetailsDTO>();
//				List<LeadDocDetails> leadDocDetailsList = this.leadDocDetailsService
//						.findDocsByLeadId(savedLeadMaster.getId());
//				for (LeadDocDetails leadDocDetails : leadDocDetailsList) {
//					leadDocDetailsDTOList.add(new LeadDocDetailsDTO(leadDocDetails));
//				}
//				updatedLeadMasterPojo.setLeadDocDetailsList(leadDocDetailsDTOList);
				if(savedLeadMaster.getLeadSource()!= null && savedLeadMaster.getLeadSource().getId() != null) {
					LeadSourcePojo leadSourcePojo = new LeadSourcePojo(
							this.leadSourceRepository.findById(savedLeadMaster.getLeadSource().getId()).get());
					updatedLeadMasterPojo.setLeadSourcePojo(leadSourcePojo);
				}
				updatedLeadMasterPojo.setCreatedBy(savedLeadMaster.getCreatedBy());
				updatedLeadMasterPojo.setStatus("Inquiry");
				updatedLeadMasterPojo.setIsLeadQuickInv(true);
			} catch (Exception e) {
				logger.error("Error While send Lead Message : ", e.getMessage());
			}
			logger.info("Lead has been created successfully");
			return updatedLeadMasterPojo;
		} catch (Exception ex) {
			logger.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}


	/* send message for lead creation notification*/
	public void sendLeadCreationMessage(String firstname, String mobileNumber, String emailId, String leadNo, Integer mvnoId, Integer buId,Long staffId) {
		try {
			Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.LEAD_CREATION_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
					LeadCreationMsg leadCreationMsg = new LeadCreationMsg(firstname, mobileNumber, emailId, leadNo, mvnoId, RabbitMqConstants.LEAD_CREATION_EVENT, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, buId,staffId);
					Gson gson = new Gson();
					gson.toJson(leadCreationMsg);
//					messageSender.send(leadCreationMsg, RabbitMqConstants.QUEUE_LEAD_CREATION_NOTIFICATION);
					kafkaMessageSender.send(new KafkaMessageData(leadCreationMsg,LeadCreationMsg.class.getSimpleName()));
				}
			} else {
				System.out.println("Message of lead creation is not sent because template is not present.");
			}
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public List<Long> getBUIdsFromCurrentStaff() {
		List<java.lang.Long> mvnoIds = null;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
			}
		} catch (Exception e) {
			ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff error{},exception{}" ,APIConstants.FAIL,e.getStackTrace());
		}
		return mvnoIds;
	}
	public LoggedInUser getLoggedInUser() {
		LoggedInUser loggedInUser = null;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
			}
		} catch (Exception e) {
			ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
		}
		return loggedInUser;
	}
}
