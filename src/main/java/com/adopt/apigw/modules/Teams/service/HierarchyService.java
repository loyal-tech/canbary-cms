package com.adopt.apigw.modules.Teams.service;

import com.adopt.apigw.MicroSeviceDataShare.SharedDataConstants.SharedDataConstants;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveTeamHierarchyMappingMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateTeamHierarchyMappingMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.CaseConstants;
import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.mapper.postpaid.CustomerAddressMapper;
import com.adopt.apigw.mapper.postpaid.PlangroupMapper;
import com.adopt.apigw.mapper.postpaid.StaffUserMapper;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.lead.LeadChangeAssigneePojo;
import com.adopt.apigw.model.lead.LeadMaster;
import com.adopt.apigw.model.lead.LeadQuotationChangeAssigneePojo;
import com.adopt.apigw.model.lead.LeadQuotationDetails;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.CommonList.repository.CommonListRepository;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.adopt.apigw.modules.Matrix.domain.Matrix;
import com.adopt.apigw.modules.Matrix.domain.MatrixDetails;
import com.adopt.apigw.modules.Matrix.domain.TatMatrixWorkFlowDetails;
import com.adopt.apigw.modules.Matrix.repository.MatrixRepository;
import com.adopt.apigw.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerPayment;
import com.adopt.apigw.modules.PartnerLedger.mapper.PartnerPaymentMapper;
import com.adopt.apigw.modules.PartnerLedger.model.PartnerPaymentDTO;
import com.adopt.apigw.modules.PartnerLedger.repository.PartnerPaymentRepository;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerPaymentService;
import com.adopt.apigw.modules.ServiceArea.domain.QServiceArea;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.Teams.domain.*;
import com.adopt.apigw.modules.Teams.mapper.HierarchyMapper;
import com.adopt.apigw.modules.Teams.model.HierarchyDTO;
import com.adopt.apigw.modules.Teams.model.ShowHiearachyDTO;
import com.adopt.apigw.modules.Teams.model.TeamOrderDTO;
import com.adopt.apigw.modules.Teams.repository.HierarchyRepository;
import com.adopt.apigw.modules.Teams.repository.TeamHierarchyMappingRepo;
import com.adopt.apigw.modules.Teams.repository.TeamsRepository;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.WorkFlowInProgressEntity.Entity.WorkFlowInProgressData;
import com.adopt.apigw.modules.customerDocDetails.domain.CustomerDocDetails;
import com.adopt.apigw.modules.customerDocDetails.mapper.CustomerDocDetailsMapper;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.modules.customerDocDetails.repository.CustomerDocDetailsRepository;
import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
import com.adopt.apigw.modules.tickets.domain.Case;
import com.adopt.apigw.modules.tickets.mapper.CaseMapper;
import com.adopt.apigw.modules.tickets.model.CaseDTO;
import com.adopt.apigw.modules.tickets.model.CaseUpdateDTO;
import com.adopt.apigw.modules.tickets.repository.CaseRepository;
//import com.adopt.apigw.modules.tickets.service.CaseService;
//import com.adopt.apigw.modules.tickets.service.CaseUpdateService;
import com.adopt.apigw.modules.workflow.service.WorkflowAssignStaffMappingService;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.pojo.customer.CafDto;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.*;
import com.adopt.apigw.repository.LeadMasterRepository;
import com.adopt.apigw.repository.LeadQuotationDetailsRepository;
import com.adopt.apigw.repository.common.*;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.*;
import com.adopt.apigw.service.postpaid.*;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.TatUtils;
import com.adopt.apigw.utils.WorkFlowQueryUtils;
import com.google.gson.Gson;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class HierarchyService extends ExBaseAbstractService2<HierarchyDTO, Hierarchy, Long> {

    @Autowired
    HierarchyRepository hierarchyRepository;

    @Autowired
    HierarchyMapper hierarchyMapper;


    @Autowired
    TeamsRepository teamsRepository;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;


    @Autowired
    CustomersService customersService;

//    @Autowired
//    TeamsRepository teamsRepository;

    @Autowired
    TeamsService teamsService;

    @Autowired
    TeamHierarchyMappingRepo teamHierarchyMappingRepo;

    @Autowired
    StaffUserBusinessUnitMappingRepository staffUserBusinessUnitMappingRepository;

    @Autowired
    CommonListRepository commonListRepository;


    @Autowired
    CustomerCafAssignmentService customerCafAssignmentService;

    @Autowired
    QueryFieldRepo querFieldRepo;

    @Autowired
    StaffUserService staffUserService;

    @Autowired
    WorkFlowQueryUtils workFlowQueryUtils;

    @Autowired
    ServiceAreaService serviceAreaService;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    PostpaidPlanService postpaidPlanService;

    @Autowired
    PostpaidPlanMapper postpaidPlanMapper;

    @Autowired
    private WorkflowAuditService workflowAuditService;


    @Autowired
    MessageSender messageSender;

    @Autowired
    CreditDocService creditDocService;

    @Autowired
    private PlanGroupService planGroupService;

    @Autowired
    private TatMatrixWorkFlowDetailsRepo tatMatrixWorkFlowDetailsRepo;

    @Autowired
    private TatUtils tatUtils;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    CustMacMapppingService custMacMapppingService;

//    @Autowired
//    CaseService caseService;

//    @Autowired
//    CaseMapper caseMapper;

//    @Autowired
//    CaseUpdateService caseUpdateService;

    @Autowired
    CustomerApproveRepo customerApproveRepo;

    @Autowired
    CustomerAddressService customerAddressService;

    @Autowired
    InOutWardMacRepo inOutWardMacRepo;

    @Autowired
    PlanGroupRepository planGroupRepository;

    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private CustPlanMappingService custPlanMappingService;

    @Autowired
    CaseRepository caseRepository;

    @Autowired
    CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    DebitDocRepository debitDocRepository;
    @Autowired
    DebitDocStaffAssignRepo debitDocStaffAssignRepo;

    @Autowired
    NotificationTemplateRepository templateRepository;

    @Autowired
    PartnerService partnerService;

    @Autowired
    PartnerPaymentService partnerPaymentService;

    @Autowired
    CustomerDocDetailsService customerDocDetailsService;
    @Autowired
    private LeadMasterRepository leadMasterRepository;

    @Autowired
    private PostPaidPlanServiceAreaMappingRepo planServiceAreaRepo;

    @Autowired
    WorkflowAssignStaffMappingService workflowAssignStaffMappingService;

    @Autowired
    private PlangroupMapper plangroupMapper;

    @Autowired
    private CustomerDocDetailsRepository customerDocDetailsRepository;

    @Autowired
    private PartnerPaymentRepository partnerPaymentRepository;

    @Autowired
    private PartnerPaymentMapper partnerPaymentMapper;

    @Autowired
    private CustomerDocDetailsMapper customerDocDetailsMapper;

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Autowired
    private CustomerAddressMapper customerAddressMapper;

    @Autowired
    private CustSpecialPlanMapppingRepository custSpecialPlanMapppingRepository;

    @Autowired
    private CustSpecialPlanRelMapppingRepository custSpecialPlanRelMapppingRepository;

    @Autowired
    private CustSpecialPlanMapppingService custSpecialPlanMapppingService;
    @Autowired
    CustSpecialPlanRelMapper custSpecialPlanRelMapper;

    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    PlanGroupMappingRepository planGroupMappingRepository;

    @Autowired
    CustSpecialPlanMapper custSpecialPlanMapper;

    @Autowired
    LeadQuotationDetailsRepository leadQuotationDetailsRepository;
    @Autowired
    MatrixRepository matrixRepository;
    @Autowired
    StaffUserMapper staffUserMapper;
    @Autowired
    HierarchyService hierarchyService;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    WorkflowAuditRepository workflowAuditRepository;

    public HierarchyService(HierarchyRepository repository, HierarchyMapper mapper) {
        super(repository, mapper);
    }

    private static final Logger log = LoggerFactory.getLogger(CustomersService.class);

    @Override
    public String getModuleNameForLog() {
        return "[Teams Hierarchy]";
    }

    public List<ShowHiearachyDTO> getAllHierarchy() {
        List<ShowHiearachyDTO> showHiearachyDTOList = new ArrayList<>();
        List<Hierarchy> hierarchyList = new ArrayList<>();
        //Admin
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 2 && getBUIdsFromCurrentStaff().size() == 0) {
            hierarchyList = hierarchyRepository.findAllByIsDeletedAndBuIdIsNull(false);
            if (getLoggedInUser().getLco())
                hierarchyList = hierarchyRepository.findAllByIsDeletedAndBuIdIsNull(false, getLoggedInUser().getPartnerId());

            hierarchyList.stream().filter(hierarchy -> (hierarchy.getMvnoId() == 2) && (getBUIdsFromCurrentStaff().size() == 0)).forEach(hierarchy -> {
                Map<String, String> teamsList = new HashMap<>();
                ShowHiearachyDTO showHiearachyDTO = new ShowHiearachyDTO();
                showHiearachyDTO.setId(hierarchy.getId());
                showHiearachyDTO.setHierarchyName(hierarchy.getHierarchyName());
                showHiearachyDTO.setEventName(hierarchy.getEventName());
                showHiearachyDTO.setMvnoId(hierarchy.getMvnoId());
                hierarchy.getTeamHierarchyMappingList().stream().sorted(Comparator.comparing(TeamHierarchyMapping::getOrderNumber)).forEach(teamHierarchyMapping -> {
                    if (teamHierarchyMapping.getIsDeleted().equals(false)) {
//                        Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
                        String teamName = teamsRepository.findTeamNameById(Long.valueOf(teamHierarchyMapping.getTeamId()));
                        if (Objects.nonNull(teamName))
                            teamsList.put(teamHierarchyMapping.getOrderNumber().toString(), teamName);
                    }
                });
                TreeMap<String, String> sorted = new TreeMap<>(teamsList);
                showHiearachyDTO.setTeamsSet(sorted);
                showHiearachyDTOList.add(showHiearachyDTO);
            });
        } else {
            if (getBUIdsFromCurrentStaff().size() > 0) {
                hierarchyList = hierarchyRepository.findAllByIsDeletedAndBuIdIn(false, getBUIdsFromCurrentStaff());
            } else {
                hierarchyList = hierarchyRepository.findAllByIsDeletedAndBuIdIsNull(false);
            }

            if (getLoggedInUser().getLco()) {
                if (getBUIdsFromCurrentStaff().size() > 0) {
                    hierarchyList = hierarchyRepository.findAllByIsDeletedAndBuIdIn(false, getBUIdsFromCurrentStaff(), getLoggedInUser().getPartnerId());
                } else {
                    hierarchyList = hierarchyRepository.findAllByIsDeletedAndBuIdIsNull(false, getLoggedInUser().getPartnerId());
                }

            }
            // TODO: pass mvnoID manually 6/5/2025
            hierarchyList.stream().filter(hierarchy -> (hierarchy.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || hierarchy.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(hierarchy.getBuId()))).forEach(hierarchy -> {
                Map<String, String> teamsList = new HashMap<>();
                ShowHiearachyDTO showHiearachyDTO = new ShowHiearachyDTO();
                showHiearachyDTO.setId(hierarchy.getId());
                showHiearachyDTO.setHierarchyName(hierarchy.getHierarchyName());
                showHiearachyDTO.setEventName(hierarchy.getEventName());
                showHiearachyDTO.setMvnoId(hierarchy.getMvnoId());
                List<TeamHierarchyMapping> teamHierarchyMappingList = hierarchy.getTeamHierarchyMappingList().stream().filter(teamHierarchyMapping -> teamHierarchyMapping.getOrderNumber() != null).collect(Collectors.toList());
                if (teamHierarchyMappingList != null && !teamHierarchyMappingList.isEmpty()) {
                    hierarchy.getTeamHierarchyMappingList().stream().sorted(Comparator.comparing(TeamHierarchyMapping::getOrderNumber)).forEach(teamHierarchyMapping -> {
                        if (teamHierarchyMapping.getIsDeleted().equals(false)) {
                            String teamName = teamsRepository.findTeamNameById(Long.valueOf(teamHierarchyMapping.getTeamId()));
//                            Optional<Teams> teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId()));
                            teamsList.put(teamHierarchyMapping.getOrderNumber().toString(), teamName);
                        }
                    });
                }

                TreeMap<String, String> sorted = new TreeMap<>(teamsList);
                showHiearachyDTO.setTeamsSet(sorted);
                showHiearachyDTOList.add(showHiearachyDTO);
            });
        }
        return showHiearachyDTOList;
    }


    @Autowired
    CreateDataSharedService createDataSharedService;

//    public List<ShowHiearachyDTO> getAllHierarchy1() {
//        List<Hierarchy> hierarchyList = new ArrayList<>();
//        TeamsDTO teamsDTO = new TeamsDTO();
//        Set<String> teamsList = new HashSet<>();
//        List<ShowHiearachyDTO> showHiearachyDTOList = new ArrayList<>();
//        hierarchyList = hierarchyRepository.findAll();
//
//        //Admin
//        if (getMvnoIdFromCurrentStaff() == 2 && getBUIdsFromCurrentStaff().size() == 0) {
//            hierarchyList.stream().filter(hierarchy -> hierarchy.getIsDeleted().equals(false) && (hierarchy.getMvnoId() == 2) && (getBUIdsFromCurrentStaff().size() == 0)).forEach(hierarchy -> {
//                ShowHiearachyDTO showHiearachyDTO = new ShowHiearachyDTO();
//
//                if (hierarchy.getIsDeleted().equals(false)) {
//                    showHiearachyDTO.setId(hierarchy.getId());
//                    showHiearachyDTO.setHierarchyName(hierarchy.getHierarchyName());
//                    showHiearachyDTO.setEventId(hierarchy.getEventId());
//                    showHiearachyDTO.setMvnoId(hierarchy.getMvnoId());
//                    hierarchy.getTeamHierarchyMappingList().forEach(teamHierarchyMapping -> {
//                        if (teamHierarchyMapping.getIsDeleted().equals(false)) {
//                            Optional<Teams> teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId()));
//                            teamsList.add(teams.get().getName());
//                        }
//                    });
//                    showHiearachyDTO.setTeamsSet(teamsList);
//                    showHiearachyDTOList.add(showHiearachyDTO);
//                }
//
//            });
//
//        } else {
//            hierarchyList.stream().filter(hierarchy -> hierarchy.getIsDeleted().equals(false) && (hierarchy.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || hierarchy.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1) && (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(hierarchy.getBuId()))).forEach(hierarchy -> {
//                ShowHiearachyDTO showHiearachyDTO = new ShowHiearachyDTO();
//
//                if (hierarchy.getIsDeleted().equals(false)) {
//                    showHiearachyDTO.setId(hierarchy.getId());
//                    showHiearachyDTO.setHierarchyName(hierarchy.getHierarchyName());
//                    showHiearachyDTO.setEventId(hierarchy.getEventId());
//                    showHiearachyDTO.setMvnoId(hierarchy.getMvnoId());
//                    hierarchy.getTeamHierarchyMappingList().forEach(teamHierarchyMapping -> {
//                        if (teamHierarchyMapping.getIsDeleted().equals(false)) {
//                            Optional<Teams> teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId()));
//                            teamsList.add(teams.get().getName());
//                        }
//                    });
//                    showHiearachyDTO.setTeamsSet(teamsList);
//                    showHiearachyDTOList.add(showHiearachyDTO);
//                }
//
//            });
//        }
//
//
//        return showHiearachyDTOList;
//    }


    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        QHierarchy qHierarchy = QHierarchy.hierarchy;
        BooleanExpression booleanExpression = qHierarchy.isNotNull().and(qHierarchy.isDeleted.eq(false));

        if (getLoggedInUser().getLco())
            booleanExpression = booleanExpression.and(qHierarchy.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression = booleanExpression.and(qHierarchy.lcoId.isNull());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (filterList.size() > 0) {
            for (GenericSearchModel genericSearchModel : filterList) {
                switch (genericSearchModel.getFilterColumn()) {
                    case "hierarchyname":
                        // TODO: pass mvnoID manually 6/5/2025
                        booleanExpression = booleanExpression.and(qHierarchy.hierarchyName.containsIgnoreCase(genericSearchModel.getFilterValue()).and(qHierarchy.mvnoId.in(getMvnoIdFromCurrentStaff(null))));
                        break;
                }
            }
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qHierarchy.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
        if (getBUIdsFromCurrentStaff().size() != 0)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qHierarchy.mvnoId.eq(1).or(qHierarchy.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qHierarchy.buId.in(getBUIdsFromCurrentStaff()))));
        return makeGenericResponse(genericDataDTO, hierarchyRepository.findAll(booleanExpression, pageRequest));
    }


    @Override
    public HierarchyDTO saveEntity(HierarchyDTO entity) throws Exception {
        // TODO: pass mvnoID manually 6/5/2025
        entity.setMvnoId(getMvnoIdFromCurrentStaff(null));
        Hierarchy entityDomain = hierarchyMapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
        ApplicationLogger.logger.info(getModuleNameForLog() + "--" + "saving Entity. Data[" + entityDomain.toString() + "]");
        try {
            if (getBUIdsFromCurrentStaff().size() == 1) {
                entityDomain.setBuId(getBUIdsFromCurrentStaff().get(0));
            }
            List<Hierarchy> hierarchyList = hierarchyRepository.findAll();

            //if (hierarchyList.stream().filter(hierarchy -> !hierarchy.getIsDeleted() && !hierarchy.getMvnoId().equals(entityDomain.getMvnoId())).noneMatch(hierarchy -> hierarchy.getEventName().equals(entityDomain.getEventName()))) {
            for (int i = 0; i < entity.getTeamHierarchyMappingList().size(); i++) {
                TeamHierarchyMapping teamHierarchyMapping = entity.getTeamHierarchyMappingList().get(i);
                teamHierarchyMapping.setOrderNumber(i);
            }

            // }
            Hierarchy savedHierarchy = new Hierarchy();
            HierarchyDTO savedHierarchyDTO = hierarchyMapper.domainToDTO(hierarchyRepository.save(entityDomain), new CycleAvoidingMappingContext());
            savedHierarchy = hierarchyMapper.dtoToDomain(savedHierarchyDTO, new CycleAvoidingMappingContext());
            createDataSharedService.sendEntitySaveDataForAllMicroService(savedHierarchy);
            sharedTeamHierarchyData(savedHierarchy, CommonConstants.OPERATION_ADD);
            return hierarchyMapper.domainToDTO(hierarchyRepository.save(entityDomain), new CycleAvoidingMappingContext());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while saving Entity. Data[" + entityDomain + "]" + ex.getMessage(), ex);
            throw ex;
        }

    }

    @Override
    public HierarchyDTO updateEntity(HierarchyDTO entity) throws Exception {
        Optional<Hierarchy> hierarchyold = hierarchyRepository.findById(entity.getId());
        List<Integer> oldTeamsId = hierarchyold.get().getTeamHierarchyMappingList().stream().filter(teamHierarchyMapping -> !teamHierarchyMapping.getIsDeleted()).map(TeamHierarchyMapping::getTeamId).collect(Collectors.toList());
        List<Integer> currentTeamsId = entity.getTeamHierarchyMappingList().stream().filter(teamHierarchyMapping -> !teamHierarchyMapping.getIsDeleted()).map(TeamHierarchyMapping::getTeamId).collect(Collectors.toList());
        List<Long> deletedTeams;
        HashMap<Integer, Long> countListMap = new HashMap<>();
        deletedTeams = oldTeamsId.stream().filter(integer -> !currentTeamsId.contains(integer)).map(Long::valueOf).collect(Collectors.toList());

        // TODO: pass mvnoID manually 6/5/2025
        entity.setMvnoId(getMvnoIdFromCurrentStaff(null));
        Hierarchy entityDomain = hierarchyMapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (entity == null || !(getMvnoIdFromCurrentStaff(null) == 1 || getMvnoIdFromCurrentStaff(null).intValue() == entity.getMvnoId().intValue()))
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            // TODO: pass mvnoID manually 6/5/2025
            if (entity == null || !(getMvnoIdFromCurrentStaff(null) == 1 || getMvnoIdFromCurrentStaff(null).intValue() == entity.getMvnoId().intValue()))
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);

            List<Teams> teams = new ArrayList<>();
            Optional<Teams> teams1 = null;
            Teams teamsdel = new Teams();
            for (Long deletedTeam : deletedTeams) {
                teams1 = teamsRepository.findById(deletedTeam);
                if (teams1.isPresent()) {
                    teamsdel = teams1.get();
                    teams.add(teamsdel);
                }
            }

            Integer assignmentCount = 0;
            for (Teams team : teams) {
                Set<StaffUser> staffUsers = new HashSet<>();
                staffUsers = team.getStaffUser();
                if (Objects.nonNull(staffUsers)) {
                    for (StaffUser staffUserTemp : staffUsers) {
                        List<Integer> staffIdList = new ArrayList<>();
                        staffIdList.add(staffUserTemp.getId());
                        if (entity.getEventName().equals(CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION)) {
                            QCustomerApprove qCustomerApprove = QCustomerApprove.customerApprove;
                            assignmentCount = Math.toIntExact(customerApproveRepo.count(qCustomerApprove.isNotNull().and(qCustomerApprove.currentStaff.equalsIgnoreCase(staffUserTemp.getUsername()))));
                        } else if (entity.getEventName().equals(CommonConstants.WORKFLOW_EVENT_NAME.CAF)) {
                            QCustomers qCustomers = QCustomers.customers;
                            BooleanExpression booleanExpression = qCustomers.isNotNull().and(qCustomers.currentAssigneeId.eq(staffUserTemp.getId())).and(qCustomers.nextTeamHierarchyMapping.isNotNull());
                            assignmentCount = Math.toIntExact(customersRepository.count(booleanExpression));
                            //assignmentCount = Iterators.size((Iterator<?>) customerCafAssignment);
                        } else if (entity.getEventName().equals(CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT)) {
                            QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
                            assignmentCount = Math.toIntExact(creditDocRepository.count(qCreditDocument.isNotNull().and(qCreditDocument.approverid.eq(staffUserTemp.getId()))));
                            //assignmentCount = Iterators.size((Iterator<?>) customerCafAssignment);
                        } else if (entity.getEventName().equals(CommonConstants.WORKFLOW_EVENT_NAME.PLAN)) {
                            QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
                            assignmentCount = Math.toIntExact(postpaidPlanRepo.count(qPostpaidPlan.isNotNull().and(qPostpaidPlan.nextStaff.eq(staffUserTemp.getId()))));
                            // assignmentCount = Iterators.size((Iterator<?>) customerCafAssignment);
                        }
                        if (assignmentCount != null && assignmentCount != 0) {
                            countListMap.put(staffUserTemp.getId(), assignmentCount.longValue());
                        }
                    }
                }
            }
            if (countListMap.size() != 0 && countListMap != null) {
                throw new CustomValidationException(APIConstants.FAIL, Constants.TEAM_NOT_DELETED_IF_TICKET_ASSIGN, null);
            } else {
                //entityDomain.setDeleteFlag(true);
                //write parent team logic
                List<Integer> teamss = new ArrayList<>();
                List<Integer> cloneTeams = new ArrayList<>();
                teamss = entity.getTeamHierarchyMappingList().stream().filter(teamHierarchyMapping -> !teamHierarchyMapping.getIsDeleted()).map(TeamHierarchyMapping::getTeamId).collect(Collectors.toList());
                List<TeamOrderDTO> teamOrderDTOS = new ArrayList<>();
                Optional<Teams> teamsOptional = null;
                QTeams qTeams = QTeams.teams;
                if (teamss.size() > 0) {
                    for (int i = 0; i < entity.getTeamHierarchyMappingList().size(); i++) {
                        TeamHierarchyMapping teamHierarchyMapping = entity.getTeamHierarchyMappingList().get(i);
                        teamHierarchyMapping.setOrderNumber(i);
                    }
                } else {
                    Teams finalTeam = new Teams();
                    Optional<Teams> finalTeams = null;
                    BooleanExpression teamsFinalbool = qTeams.isNotNull().and(qTeams.isDeleted.eq(false)).and(qTeams.id.eq(Long.valueOf(teamss.get(0))));
                    finalTeams = teamsRepository.findOne(teamsFinalbool);
                    finalTeam = finalTeams.get();
                    finalTeam.setParentTeams(null);
                    teamsRepository.save(finalTeam);
                }

                Hierarchy updatedHierarchy = hierarchyRepository.save(entityDomain);
                //HierarchyDTO updatedHierarchyDTO = hierarchyMapper.domainToDTO(hierarchyRepository.save(entityDomain),new CycleAvoidingMappingContext());
                //updatedHierarchy = hierarchyMapper.dtoToDomain(updatedHierarchyDTO,new CycleAvoidingMappingContext());
                createDataSharedService.updateEntityDataForAllMicroService(updatedHierarchy);
                sharedTeamHierarchyData(updatedHierarchy, CommonConstants.OPERATION_UPDATE);
                return hierarchyMapper.domainToDTO(updatedHierarchy, new CycleAvoidingMappingContext());
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while saving Entity. Data[" + entityDomain.toString() + "]" + ex.getMessage(), ex);
            throw ex;
        }
    }

//
//    @Override
//    @Transactional
//    public void deleteEntity(HierarchyDTO entity) throws Exception {
//
//        Hierarchy workflow = hierarchyRepository.findById(entity.getId()).orElse(null);
//        try {
//
////            long count = 0L;
////            int i = 0;
//            if (workflow != null) {
//                ApplicationLogger.logger.info(getModuleNameForLog() + "--" + "deleting Entity. Data[" + workflow.toString() + "]");
//                deleteEntity(getMapper().domainToDTO(workflow, new CycleAvoidingMappingContext()));
//                workflow.setIsDeleted(true);
//                hierarchyRepository.save(workflow);
//            }
////
//
////            String eventName = entity.getEventName();
////            switch (eventName) {
////                case CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT: {
////                    QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
////                    while (count == 0 && i < workflow.getTeamHierarchyMappingList().size()) {
////                        count = count + creditDocRepository.count(qCreditDocument.nextTeamHierarchyMappingId.eq(workflow.getTeamHierarchyMappingList().get(i).getId()));
////                        i++;
////                    }
////                    break;
////                }
////                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN: {
////                    QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
////                    while (count == 0 && i < workflow.getTeamHierarchyMappingList().size()) {
////                        count = count + postpaidPlanRepo.count(qPostpaidPlan.nextTeamHierarchyMapping.eq(workflow.getTeamHierarchyMappingList().get(i).getId()));
////                        i++;
////                    }
////                    break;
////                }
////                case CommonConstants.WORKFLOW_EVENT_NAME.CAF:
////                case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION: {
////                    QCustomers qCustomers = QCustomers.customers;
////                    while (count == 0 && i < workflow.getTeamHierarchyMappingList().size()) {
////                        count = count + customersRepository.count(qCustomers.nextTeamHierarchyMapping.eq(workflow.getTeamHierarchyMappingList().get(i).getId()));
////                        i++;
////                    }
////                    break;
////
////                }
////                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN: {
////                    QCustomerInventoryMapping customerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
////                    QInOutWardMACMapping inOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
////                    while (count == 0 && i < workflow.getTeamHierarchyMappingList().size()) {
////                        count = count + customerInventoryMappingRepo.count(customerInventoryMapping.teamHierarchyMappingId.eq(workflow.getTeamHierarchyMappingList().get(i).getId()));
////                        count = count + inOutWardMacRepo.count(inOutWardMACMapping.teamHierarchyMappingId.eq(workflow.getTeamHierarchyMappingList().get(i).getId()));
////                        i++;
////                    }
////                    break;
////                }
//////                        case CommonConstants.WORKFLOW_EVENT_NAME.LEAD: {
//////                            break;
//////                        }
//////                        case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_DISCOUNT: {
//////                            break;
//////                        }
//////                        case CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION: {
//////                            break;
//////                        }
////
////                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP: {
////                    QPlanGroup qPlanGroup = QPlanGroup.planGroup;
////                    while (count == 0 && i < workflow.getTeamHierarchyMappingList().size()) {
////                        count = count + planGroupRepository.count(qPlanGroup.nextTeamHierarchyMappingId.eq(workflow.getTeamHierarchyMappingList().get(i).getId()));
////                        i++;
////                    }
////                    break;
////                }
////
////
////            }
//
////            if (count == 0) {
//
////            } else {
////                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Workflow can not be deleted as some of the approval still left..", null);
////            }
//
//
//        } catch (Exception ex) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Workflow can not be deleted as some of the approval still left..", null);
////            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while deleting Entity. Data[" + entity.toString() + "]" + ex.getMessage(), ex);
////            throw ex;
//        }
//
//    }


    @Override
    public boolean duplicateVerifyAtSave(String evenName,Integer mvnoId) {
        boolean flag = false;
        if (evenName != null) {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
                // TODO: pass mvnoID manually 6/5/2025
                flag = hierarchyRepository.existsByBuIdIsNullAndMvnoIdInAndEventNameAndIsDeleted(Arrays.asList(getMvnoIdFromCurrentStaff(null), 1), evenName, false);
            } else {
                // TODO: pass mvnoID manually 6/5/2025
                flag = hierarchyRepository.existsByMvnoIdInAndBuIdInAndEventNameAndIsDeleted(Arrays.asList(getMvnoIdFromCurrentStaff(null), 1), getBUIdsFromCurrentStaff(), evenName, false);
            }
        } else {
            throw new CustomValidationException(APIConstants.FAIL, "evenName can not be null!", null);
        }
        return flag;
    }

    public Map<String, String> getTeamForNextApproveForAuto(Integer mvnoId, Long buId, String eventName, String listType, Boolean isApproveRequest, boolean isCreateRequest, Object entity) {
        try{
            log.debug("======================================================Common method called for workflow.================================================================");
            //System.out.println("======================================================Common method called for workflow.================================================================");
            Map<String, String> map = new HashMap<>();
            Optional<Hierarchy> hierarchy;
            Long eventId = 0L;
            QTeamHierarchyMapping qTeamHierarchyMapping = QTeamHierarchyMapping.teamHierarchyMapping;
            QHierarchy qHierarchy = QHierarchy.hierarchy;
            BooleanExpression booleanExpHierarchy = qHierarchy.isNotNull().and(qHierarchy.eventName.eq(eventName).and(qHierarchy.isDeleted.eq(false)).and(qHierarchy.mvnoId.eq(mvnoId)));
            if (buId != null) {
                booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.eq(buId));
            } else {
                booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.isNull());
            }
            hierarchy = hierarchyRepository.findOne(booleanExpHierarchy);
            if (hierarchy.isPresent()) {
                log.debug("Hierachy found for the entity : " + entity.getClass());
                BooleanExpression expForTeamHirMapping = qTeamHierarchyMapping.isNotNull().and(qTeamHierarchyMapping.hierarchyId.eq(Math.toIntExact(hierarchy.get().getId())).and(qTeamHierarchyMapping.isDeleted.eq(false)));
                List<TeamHierarchyMapping> teamHierarchyMappingList = (List<TeamHierarchyMapping>) teamHierarchyMappingRepo.findAll(expForTeamHirMapping);
                Integer finalOrderNumber = null;
                TeamHierarchyMapping nextTeamMapping = null;
                TeamHierarchyMapping currentTeamMapping = new TeamHierarchyMapping();
                finalOrderNumber = getFinalOrderNumber(isApproveRequest, isCreateRequest, entity, teamHierarchyMappingList, finalOrderNumber);
                for (TeamHierarchyMapping t : teamHierarchyMappingList) {
                    finalOrderNumber = finalOrderNumber == null ? 0 : finalOrderNumber;
                    if (t.getOrderNumber().equals(finalOrderNumber)) {
                        nextTeamMapping = t;
                    }
                    if (finalOrderNumber != 0) {
                        int orderNumber = finalOrderNumber - 1;
                        List<TeamHierarchyMapping> teamHierarchyMappings = teamHierarchyMappingList.stream().filter(teamHierarchyMapping -> teamHierarchyMapping.getOrderNumber().equals(orderNumber)).collect(Collectors.toList());
                        if (teamHierarchyMappings.size() > 0) {
                            currentTeamMapping = teamHierarchyMappings.get(0);
                        } else {
                            currentTeamMapping = nextTeamMapping;
                        }
                    } else {
                        if (teamHierarchyMappingList.size() == 1) {
                            currentTeamMapping = teamHierarchyMappingList.get(0);
                        }
                    }
                }
                if (currentTeamMapping != null && currentTeamMapping.getTeamAction() != null && isApproveRequest != null) {
                    if (isApproveRequest) {
                        log.warn("Initializing Action process for the workflow");
                        workFlowQueryUtils.checkAction(currentTeamMapping.getTeamAction(), eventName, entity);
                        log.warn("Action process for the workflow completed");
                    }
                }
                try {
                    if (nextTeamMapping != null) {
                        boolean flag = true;
                        int staffId = 0;
                        if (nextTeamMapping.getQueryFieldList().size() > 0) {
                            log.warn("Initializing condition process for the workflow");
                            flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
                            log.warn("Condition process for the workflow completed");
                        }
                        if (flag) {
                            Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                            List<ServiceArea> serviceAreaList = getServiceAreaFromEntity(entity);
                            List<StaffUserPojo> staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(serviceAreaList, buId, teams);
                            staffId = workFlowQueryUtils.assignStaffFromList(staffUsers, eventName, entity);
                        }
                        int k = teamHierarchyMappingList.indexOf(nextTeamMapping);
                        while (k < teamHierarchyMappingList.size() && staffId == 0 && k >= 0) {
                            flag = true;
                            nextTeamMapping = teamHierarchyMappingList.get(k);
                            Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                            if (nextTeamMapping.getQueryFieldList().size() > 0) {
                                log.warn("Initializing condition process for the workflow");
                                flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
                                log.warn("Condition process for the workflow completed");
                            }
                            if (flag) {
                                nextTeamMapping = teamHierarchyMappingList.get(k);
                                if (teams != null) {
                                    teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                                    List<ServiceArea> serviceAreaList = getServiceAreaFromEntity(entity);
                                    List<StaffUserPojo> staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(serviceAreaList, buId, teams);
                                    staffId = workFlowQueryUtils.assignStaffFromList(staffUsers, eventName, entity);
                                }
                            }
                            if (isApproveRequest != null) {
                                if (isApproveRequest || isCreateRequest) {
                                    k++;
                                } else {
                                    k--;
                                }
                            } else {
                                k++;
                            }
                        }
                        if (staffId != 0 && nextTeamMapping != null) {
                            map.put("staffId", String.valueOf(staffId));
                            map.put("nextTatMappingId", String.valueOf(nextTeamMapping.getId()));
                            map.put("eventId", String.valueOf(eventId));
                            map.put("eventName", String.valueOf(eventName));
                            map.put("tat_id", String.valueOf(nextTeamMapping.getTat_id()));
                            map.put("current_tat_id", String.valueOf(currentTeamMapping.getTat_id()));
                            map.put("workFlowId", String.valueOf(hierarchy.get().getId()));
                            map.put("orderNo", String.valueOf(nextTeamMapping.getOrderNumber()));
                        }
                    } else {
                        map.put("eventId", String.valueOf(eventId));
                        map.put("eventName", String.valueOf(eventName));
                    }
                    return map;
                } catch (CustomValidationException ex) {
                    ex.printStackTrace();
                    ApplicationLogger.logger.error(ex.getMessage(), ex);
                    throw new CustomValidationException(ex.getErrCode(), ex.getMessage(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                    ApplicationLogger.logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
            log.debug("Hierachy not found for the entity : " + entity.getClass());

            return map;
        }
        catch (CustomValidationException ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            throw new CustomValidationException(ex.getErrCode(), ex.getMessage(), null);
        }
        catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<TeamHierarchyDTO> getApproveProgress(String eventName, Long entityId) {
        List<TeamHierarchyDTO> teamHierarchyDTOList = new ArrayList<TeamHierarchyDTO>();
        Integer mvnoId = 0;
        Long buId = 0L;
        Integer currentTeamMappingId = 0;
        try {
            switch (eventName) {
                case CommonConstants.WORKFLOW_EVENT_NAME.CAF:
                case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION: {
                    Customers customers = customersRepository.findByIdLight(Integer.parseInt(entityId.toString()));
                    if (customers != null) {
                        if (customers.getMvnoId() != null) {
                            mvnoId = customers.getMvnoId();
                        }
                        if (customers.getBuId() != null) {
                            buId = customers.getBuId();
                        }
                        if (customers.getNextTeamHierarchyMapping() != null) {
                            currentTeamMappingId = customers.getNextTeamHierarchyMapping();
                        }
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE:
                case CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT: {
                    CreditDocument creditDocument = creditDocRepository.findById(entityId.intValue()).orElse(null);
                    if (creditDocument != null) {
                        if (creditDocument.getMvnoId() != null) {
                            mvnoId = creditDocument.getMvnoId();
                        }
                        if (creditDocument.getBuID() != null) {
                            buId = creditDocument.getBuID();
                        }
                        if (creditDocument.getNextTeamHierarchyMappingId() != null) {
                            currentTeamMappingId = creditDocument.getNextTeamHierarchyMappingId();
                        }
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN: {
                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(entityId.intValue()).orElse(null);
                    if (postpaidPlan != null) {
                        if (postpaidPlan.getMvnoId() != null) {
                            mvnoId = postpaidPlan.getMvnoId();
                        }
                        if (postpaidPlan.getBuId() != null) {
                            buId = postpaidPlan.getBuId();
                        }
                        if (postpaidPlan.getNextTeamHierarchyMapping() != null) {
                            currentTeamMappingId = postpaidPlan.getNextTeamHierarchyMapping();
                        }
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN: {
                    CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(entityId).orElse(null);
                    if (customerInventoryMapping != null) {
                        if (customerInventoryMapping.getCustomer().getMvnoId() != null) {
                            mvnoId = customerInventoryMapping.getMvnoId();
                        }
                        if (customerInventoryMapping.getCustomer().getBuId() != null) {
                            buId = customerInventoryMapping.getCustomer().getBuId();
                        }
                        if (customerInventoryMapping.getTeamHierarchyMappingId() != null) {
                            currentTeamMappingId = customerInventoryMapping.getTeamHierarchyMappingId();
                        }
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_TERMINATION: {
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityId.intValue()).orElse(null);
                    Customers customers = customersRepository.findById(customerServiceMapping.getCustId()).orElse(null);
                    if (customerServiceMapping != null) {
                        if (customers.getMvnoId() != null) {
                            mvnoId = customers.getMvnoId();
                        }
                        if (customers.getBuId() != null) {
                            buId = customers.getBuId();
                        }
                        if (customers.getNextTeamHierarchyMapping() != null) {
                            currentTeamMappingId = customers.getNextTeamHierarchyMapping();
                        }
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CHANGE_DISCOUNT: {
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityId.intValue()).orElse(null);
                    Customers customers = customersRepository.findById(customerServiceMapping.getCustId()).orElse(null);
                    if (customerServiceMapping != null) {
                        if (customers.getMvnoId() != null) {
                            mvnoId = customers.getMvnoId();
                        }
                        if (customers.getBuId() != null) {
                            buId = customers.getBuId();
                        }
                        if (customers.getNextTeamHierarchyMapping() != null) {
                            currentTeamMappingId = customers.getNextTeamHierarchyMapping();
                        }
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION: {
                    CustomerAddress customerAddress = customerAddressService.get(entityId.intValue(),mvnoId);
                    if (customerAddress != null) {
                        if (customerAddress.getCustomer() != null) {
                            mvnoId = customerAddress.getCustomer().getMvnoId();
                        }
                        if (customerAddress.getCustomer().getBuId() != null) {
                            buId = customerAddress.getCustomer().getBuId();
                        }
                        if (customerAddress.getNextTeamHierarchyMappingId() != null) {
                            currentTeamMappingId = customerAddress.getNextTeamHierarchyMappingId();
                        }
                    }
                    break;
                }
//                case CommonConstants.WORKFLOW_EVENT_NAME.CASE: {
//                    Case aCase = caseService.getRepository().findById(entityId).orElse(null);
//                    if (aCase != null) {
//                        if (aCase.getCustomers().getMvnoId() != null) {
//                            mvnoId = aCase.getCustomers().getMvnoId();
//                        }
//                        if (aCase.getCustomers().getBuId() != null) {
//                            buId = aCase.getCustomers().getBuId();
//                        }
//                        if (aCase.getTeamHierarchyMappingId() != null) {
//                            currentTeamMappingId = Math.toIntExact(aCase.getTeamHierarchyMappingId());
//                        }
//                    }
//                    break;
//                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP: {
                    PlanGroup planGroup = planGroupRepository.findById(entityId.intValue()).orElse(null);
                    if (planGroup != null) {
                        if (planGroup.getMvnoId() != null) {
                            mvnoId = planGroup.getMvnoId();
                        }
                        if (planGroup.getBuId() != null) {
                            buId = planGroup.getBuId();
                        }
                        if (planGroup.getNextTeamHierarchyMappingId() != null) {
                            currentTeamMappingId = planGroup.getNextTeamHierarchyMappingId();
                        }
                    }
                    break;
                }

                case CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING: {
                    CustSpecialPlanRelMappping planGroup = custSpecialPlanRelMapppingRepository.findById(entityId).orElse(null);

                    if (planGroup != null) {
                        if (planGroup.getMvnoId() != null) {
                            mvnoId = planGroup.getMvnoId();
                        }
                        if (planGroup.getBuId() != null) {
                            buId = planGroup.getBuId();
                        }
                        if (planGroup.getNextTeamHierarchyMapping() != null) {
                            currentTeamMappingId = planGroup.getNextTeamHierarchyMapping();
                        }
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION: {
                    DebitDocument debitDocument = debitDocRepository.findById(entityId.intValue()).orElse(null);
                    CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(debitDocument.getCustpackrelid());
                    CustPlanMappping actualCustomerPlanMapping = custPlanMappingRepository.findById(custPlanMappping.getCustRefId());
                    if (debitDocument != null) {
                        if (actualCustomerPlanMapping.getCustomer().getMvnoId() != null) {
                            mvnoId = actualCustomerPlanMapping.getCustomer().getMvnoId();
                        }
                        if (actualCustomerPlanMapping.getCustomer().getBuId() != null) {
                            buId = actualCustomerPlanMapping.getCustomer().getBuId();
                        }
                        if (debitDocument.getNextTeamHierarchyMappingId() != null) {
                            currentTeamMappingId = debitDocument.getNextTeamHierarchyMappingId();
                        }
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE: {
                    PartnerPayment partnerPayment = partnerPaymentService.getRepository().getOne(entityId);
                    if (partnerPayment.getPartner() != null) {
                        if (partnerPayment != null) {
                            mvnoId = partnerPayment.getPartner().getMvnoId();
                        }
                        if (partnerPayment.getPartner().getBuId() != null) {
                            buId = partnerPayment.getPartner().getBuId();
                        }
                        if (partnerPayment.getNextTeamHierarchyMappingId() != null) {
                            currentTeamMappingId = partnerPayment.getNextTeamHierarchyMappingId();
                        }
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION: {
                    CustomerDocDetails customerDocDetails = customerDocDetailsService.getRepository().getOne(entityId);
                    if (customerDocDetails.getCustomer() != null) {
                        if (customerDocDetails != null) {
                            mvnoId = customerDocDetails.getCustomer().getMvnoId();
                        }
                        if (customerDocDetails.getCustomer().getBuId() != null) {
                            buId = customerDocDetails.getCustomer().getBuId();
                        }
                        if (customerDocDetails.getNextTeamHierarchyMappingId() != null) {
                            currentTeamMappingId = customerDocDetails.getNextTeamHierarchyMappingId();
                        }
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD: {
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityId.intValue()).orElse(null);
                    Customers customers = customersRepository.findById(customerServiceMapping.getCustId()).orElse(null);
                    if (customerServiceMapping != null) {
                        if (customers.getMvnoId() != null) {
                            mvnoId = customers.getMvnoId();
                        }
                        if (customers.getBuId() != null) {
                            buId = customers.getBuId();
                        }
                        if (customers.getNextTeamHierarchyMapping() != null) {
                            currentTeamMappingId = customers.getNextTeamHierarchyMapping();
                        }
                    }
                    break;
                }
            }
            Optional<Hierarchy> hierarchy;
            QHierarchy qHierarchy = QHierarchy.hierarchy;
            BooleanExpression booleanExpHierarchy = qHierarchy.isNotNull().and(qHierarchy.eventName.eq(eventName).and(qHierarchy.isDeleted.eq(false)).and(qHierarchy.mvnoId.eq(mvnoId)));
            if (buId != null && buId != 0) {
                booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.eq(buId));
                hierarchy = hierarchyRepository.findOne(booleanExpHierarchy);
            } else {
                booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.isNull());
                hierarchy = hierarchyRepository.findOne(booleanExpHierarchy);
            }
            if (hierarchy.isPresent()) {
                List<TeamHierarchyMapping> teamHierarchyMappings = hierarchy.get().getTeamHierarchyMappingList();
                if (currentTeamMappingId == null || currentTeamMappingId == 0) {
                    for (int i = 0; i < teamHierarchyMappings.size(); i++) {
                        TeamHierarchyDTO dto = new TeamHierarchyDTO();
                        dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
                        dto.setStatus("Approved");
                        if (i + 1 == teamHierarchyMappings.size()) {
                            dto.setParentTeamsId(null);
                        } else {
                            dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
                        }
                        dto.setTeamName(teamsRepository.findTeamNameById(teamHierarchyMappings.get(i).getTeamId().longValue()));
                        teamHierarchyDTOList.add(dto);

                    }

                } else {
                    int currentOrder = 0;
                    for (TeamHierarchyMapping t : hierarchy.get().getTeamHierarchyMappingList()) {
                        if (Objects.equals(t.getId(), currentTeamMappingId)) {
                            currentOrder = t.getOrderNumber();
                        }
                    }
                    for (int i = 0; i < teamHierarchyMappings.size(); i++) {
                        if (teamHierarchyMappings.get(i).getOrderNumber() < currentOrder) {
                            TeamHierarchyDTO dto = new TeamHierarchyDTO();
                            dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
                            dto.setStatus("Approved");
                            if (i + 1 == teamHierarchyMappings.size()) {
                                dto.setParentTeamsId(null);
                            } else {
                                dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
                            }
                            dto.setTeamName(teamsRepository.findTeamNameById(teamHierarchyMappings.get(i).getTeamId().longValue()));
                            teamHierarchyDTOList.add(dto);
                        } else {
                            TeamHierarchyDTO dto = new TeamHierarchyDTO();
                            dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
                            dto.setStatus("Pending");
                            if (i + 1 == teamHierarchyMappings.size()) {
                                dto.setParentTeamsId(null);
                            } else {
                                dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
                            }
                            dto.setTeamName(teamsRepository.findTeamNameById(teamHierarchyMappings.get(i).getTeamId().longValue()));
                            teamHierarchyDTOList.add(dto);

                        }
                    }
                }
            }
            return teamHierarchyDTOList;


        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage());
        }
        return teamHierarchyDTOList;
    }

    List<ServiceArea> getServiceAreaFromEntity(Object entity) {
        List<Integer> ids = new ArrayList<>();
        List<PlanGroup> plangroupList = new ArrayList<>();
        List<ServiceArea> serviceAreaList = new ArrayList<>();
        if (Objects.nonNull(entity) && entity.getClass().equals(CustomersPojo.class)) {
            serviceAreaList.add(serviceAreaService.getRepository().findById(((CustomersPojo) entity).getServiceareaid()).get());
        } else if (Objects.nonNull(entity) && entity.getClass().equals(PostpaidPlanPojo.class)) {
            serviceAreaList.addAll(((PostpaidPlanPojo) entity).getServiceAreaNameList().stream().map(serviceAreaDTO -> serviceAreaService.getMapper().dtoToDomain(serviceAreaDTO, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
        } else if (Objects.nonNull(entity) && entity.getClass().equals(CreditDocument.class)) {
            serviceAreaList.add(serviceAreaService.getRepository().findById(((CreditDocument) entity).getCustomer().getServicearea().getId()).get());
        } else if (Objects.nonNull(entity) && entity.getClass().equals(CustomerInventoryMapping.class)) {
            serviceAreaList.add(serviceAreaService.getRepository().findById(((CustomerInventoryMapping) entity).getCustomer().getServicearea().getId()).get());
        } else if (Objects.nonNull(entity) && entity.getClass().equals(CaseDTO.class)) {
            serviceAreaList.add(serviceAreaService.getRepository().findById(customersService.get(((CaseDTO) entity).getCustomersId(),getMvnoIdFromCurrentStaff(((CaseDTO) entity).getCustomersId())).getServicearea().getId()).get());
        } else if (Objects.nonNull(entity) && entity.getClass().equals(PlanGroup.class)) {
            plangroupList.add((PlanGroup) entity);
            for (PlanGroup planGroup1 : plangroupList) {
                for (ServiceArea serviceArea : planGroup1.getServicearea()) {
                    ids.add(serviceArea.getId().intValue());
                }
            }
            List<Long> serviceareaids = planServiceAreaRepo.findAllByServiceIdIn(ids).stream().map(postPaidPlanServiceAreaMapping -> postPaidPlanServiceAreaMapping.getServiceId()).collect(Collectors.toList()).stream().map(integer -> integer.longValue()).collect(Collectors.toList());
            QServiceArea qServiceArea = QServiceArea.serviceArea;
            BooleanExpression booleanExpression = qServiceArea.isNotNull().and(qServiceArea.id.in(serviceareaids));
            serviceAreaList.addAll((List<ServiceArea>) serviceAreaRepository.findAll(booleanExpression));
        } else if (Objects.nonNull(entity) && entity.getClass().equals(CustomerAddressPojo.class)) {
            Customers customers = customersService.getById(((CustomerAddressPojo) entity).getCustomerId());
            serviceAreaList.add(serviceAreaService.getRepository().findById(customers.getServicearea().getId()).get());
        } else if (Objects.nonNull(entity) && entity.getClass().equals(LeadMgmtWfDTO.class)) {
            QServiceArea serviceArea = QServiceArea.serviceArea;
            BooleanExpression booleanExpression = serviceArea.isNotNull();
            LeadMgmtWfDTO leadMgmtWfDTO = (LeadMgmtWfDTO) entity;
            if (leadMgmtWfDTO.getMvnoId() != null && Objects.nonNull(leadMgmtWfDTO.getServiceareaid())) {
                booleanExpression = booleanExpression.and(serviceArea.id.eq(leadMgmtWfDTO.getServiceareaid()));
            } else {
                booleanExpression = booleanExpression.and(serviceArea.mvnoId.eq(leadMgmtWfDTO.getMvnoId().intValue()));
            }
            serviceAreaList = IterableUtils.toList(serviceAreaRepository.findAll(booleanExpression));
        } else if (Objects.nonNull(entity) && entity.getClass().equals(CustomerServiceMapping.class)) {
            Integer customerId = ((CustomerServiceMapping) entity).getCustId();
            Customers customers = customersRepository.findById(customerId).orElse(null);
            serviceAreaList.add(serviceAreaService.getRepository().findById(customers.getServicearea().getId()).get());
        } else if (Objects.nonNull(entity) && entity.getClass().equals(OrganizationBillDTO.class)) {
            Customers customers = ((OrganizationBillDTO) entity).getActualCustomers();
            serviceAreaList.add(serviceAreaService.getRepository().findById(customers.getServicearea().getId()).get());
        } else if (Objects.nonNull(entity) && entity.getClass().equals(PartnerPaymentDTO.class)) {
            Partner partner = partnerService.get(((PartnerPaymentDTO) entity).getPartnerId(),getMvnoIdFromCurrentStaff(((CaseDTO) entity).getCustomersId()));
            serviceAreaList.addAll(partner.getServiceAreaList());
        } else if (Objects.nonNull(entity) && entity.getClass().equals(CustomerDocDetailsDTO.class)) {
            Customers customer = customersRepository.findById(((CustomerDocDetailsDTO) entity).getCustId()).orElse(null);
            if (customer != null) {
                serviceAreaList.add(serviceAreaService.getRepository().findById(customer.getServicearea().getId()).get());
            }
        } else if (Objects.nonNull(entity) && entity.getClass().equals(CustSpecialPlanRelMappping.class)) {
            QServiceArea serviceArea = QServiceArea.serviceArea;
            BooleanExpression booleanExpression = serviceArea.isNotNull();
            CustSpecialPlanRelMappping custSpecialPlanRelMappping = (CustSpecialPlanRelMappping) entity;
            if (custSpecialPlanRelMappping.getMvnoId() != null) {
                booleanExpression = booleanExpression.and(serviceArea.mvnoId.eq(custSpecialPlanRelMappping.getMvnoId().intValue()));
            }
            serviceAreaList = IterableUtils.toList(serviceAreaRepository.findAll(booleanExpression));
        } else if (Objects.nonNull(entity) && entity.getClass().equals(CustSpecialPlanRelMapppingPojo.class)) {
            Set<CustSpecialPlanMapppingPojo> set = new HashSet<>();
            if (((CustSpecialPlanRelMapppingPojo) entity).getPlanMapping() != null) {
                set = ((CustSpecialPlanRelMapppingPojo) entity).getPlanMapping();
            } else {
                set = ((CustSpecialPlanRelMapppingPojo) entity).getPlanGroupMapping();
            }
            Set<ServiceArea> serviceAreaSet = new HashSet<>();
            for (CustSpecialPlanMapppingPojo mapping : set) {
                PostpaidPlan postpaidPlan = null;
                if (mapping.getNormalPlanId() != null) {
                    postpaidPlan = postpaidPlanRepo.findById(mapping.getNormalPlanId()).orElse(null);
                }
                if (mapping.getNormalPlan() != null) {
                    postpaidPlan = mapping.getNormalPlan();
                } else if (mapping.getNormalPlanGroup() != null) {
                    PlanGroupMapping planGroupMapping = planGroupMappingRepository.findById(mapping.getNormalPlanGroup().getPlanGroupId()).orElse(null);
                    postpaidPlan = planGroupMapping.getPlan();
                } else if (mapping.getNormalPlanGroupId() != null) {
                    PlanGroupMapping planGroupMapping = planGroupMappingRepository.findById(mapping.getNormalPlanGroupId()).orElse(null);
                    postpaidPlan = planGroupMapping.getPlan();
                }
                if (postpaidPlan != null) {
                    postpaidPlan.getServiceAreaNameList();
                    for (ServiceArea serviceArea : postpaidPlan.getServiceAreaNameList()) {
                        if (!serviceAreaSet.contains(serviceArea)) {
                            serviceAreaSet.add(serviceArea);
                        }
                    }
                }
            }
            serviceAreaList = new ArrayList<>(serviceAreaSet);
        } else if (Objects.nonNull(entity) && entity.getClass().equals(InOutWardMACMapping.class)) {
            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(((InOutWardMACMapping) entity).getCustInventoryMappingId()).get();
            serviceAreaList.add(serviceAreaService.getRepository().findById(customerInventoryMapping.getCustomer().getServicearea().getId()).get());
        } else if (Objects.nonNull(entity) && entity.getClass().equals(LeadQuotationWfDTO.class)) {
            QServiceArea serviceArea = QServiceArea.serviceArea;
            BooleanExpression booleanExpression = serviceArea.isNotNull();
            LeadQuotationWfDTO leadQuotationWfDTO = (LeadQuotationWfDTO) entity;
            if (leadQuotationWfDTO.getMvnoId() != null) {
                booleanExpression = booleanExpression.and(serviceArea.mvnoId.eq(leadQuotationWfDTO.getMvnoId().intValue()));
            }
            serviceAreaList = IterableUtils.toList(serviceAreaRepository.findAll(booleanExpression));
        }
        return serviceAreaList;
    }

    public Map<String, Object> getTeamForNextApprove(Integer mvnoId, Long buId, String eventName, String listType, Boolean isApproveRequest, boolean isCreateRequest, Object entity) {
        try{
            System.out.println("======================================================Common method called for workflow.================================================================");
            Map<String, Object> map = new HashMap<>();
            Hierarchy hierarchy = hierarchyRepository.findOne(
                    QHierarchy.hierarchy.isNotNull()
                            .and(QHierarchy.hierarchy.eventName.eq(eventName)
                                    .and(QHierarchy.hierarchy.isDeleted.eq(false))
                                    .and(QHierarchy.hierarchy.mvnoId.eq(mvnoId))
                                    .and(buId != null ? QHierarchy.hierarchy.buId.eq(buId) : QHierarchy.hierarchy.buId.isNull()))
            ).orElse(null);
            if (hierarchy != null) {
                List<TeamHierarchyMapping> teamHierarchyMappingList = IterableUtils.toList(teamHierarchyMappingRepo.findAll(
                        QTeamHierarchyMapping.teamHierarchyMapping.isNotNull()
                                .and(QTeamHierarchyMapping.teamHierarchyMapping.hierarchyId.eq(Math.toIntExact(hierarchy.getId()))
                                        .and(QTeamHierarchyMapping.teamHierarchyMapping.isDeleted.eq(false)))
                ));
                Integer finalOrderNumber = null;
                TeamHierarchyMapping nextTeamMapping = null;
                TeamHierarchyMapping currentTeamMapping = null;
                finalOrderNumber = getFinalOrderNumber(isApproveRequest, isCreateRequest, entity, teamHierarchyMappingList, finalOrderNumber);
                for (TeamHierarchyMapping t : teamHierarchyMappingList) {
                    finalOrderNumber = finalOrderNumber == null ? 0 : finalOrderNumber;
                    if (t.getOrderNumber().equals(finalOrderNumber)) {
                        nextTeamMapping = t;
                    }
                    if (finalOrderNumber != 0) {
                        int orderNumber = finalOrderNumber - 1;
                        List<TeamHierarchyMapping> teamHierarchyMappings = teamHierarchyMappingList.stream().filter(teamHierarchyMapping -> teamHierarchyMapping.getOrderNumber().equals(orderNumber)).collect(Collectors.toList());
                        if (teamHierarchyMappings.size() > 0) {
                            currentTeamMapping = teamHierarchyMappings.get(0);
                        } else {
                            currentTeamMapping = nextTeamMapping;
                        }
                    } else {
                        if (teamHierarchyMappingList.size() == 1) {
                            currentTeamMapping = teamHierarchyMappingList.get(0);
                        }
                    }
                }

                if (currentTeamMapping != null && currentTeamMapping.getTeamAction() != null && isApproveRequest != null) {
                    if (isApproveRequest) {
                        workFlowQueryUtils.checkAction(currentTeamMapping.getTeamAction(), eventName, entity);
                    }
                }

                try {
                    if (nextTeamMapping != null) {
                        boolean flag = true;
                        List<StaffUserPojo> staffUsers = new ArrayList<>();
                        if (nextTeamMapping.getQueryFieldList().size() > 0) {
                            flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
                        }
                        if (flag) {
                            Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                                staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), buId, teams);
                        }
                        int k = teamHierarchyMappingList.indexOf(nextTeamMapping);
                        while (k < teamHierarchyMappingList.size() && staffUsers.size() == 0 && k >= 0) {
                            flag = true;
                            nextTeamMapping = teamHierarchyMappingList.get(k);
                            Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                            if (nextTeamMapping.getQueryFieldList().size() > 0) {
                                flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
                            }
                            if (flag) {
                                nextTeamMapping = teamHierarchyMappingList.get(k);
                                if (teams != null) {
                                    teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                                    staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), buId, teams);
                                }
                            }
                            if (isApproveRequest != null) {
                                if (isApproveRequest || isCreateRequest) {
                                    k++;
                                } else {
                                    k--;
                                }
                            } else {
                                k++;
                            }

                        }
                        if (staffUsers.size() != 0 && nextTeamMapping.getId() != 0) {
                            map.put("assignableStaff", staffUsers.stream().collect(Collectors.toMap(StaffUserPojo::getId, s -> s, (a, b) -> a)).values().stream().collect(Collectors.toList()));
                            map.put("nextTeamHierarchyMappingId", nextTeamMapping.getId());
//                            map.put("tat_id", String.valueOf(nextTeamMapping.getTat_id()));
                            map.put("current_tat_id", String.valueOf(currentTeamMapping != null ? currentTeamMapping.getTat_id() : nextTeamMapping.getTat_id()));
                            map.put("workFlowId", String.valueOf(hierarchy.getId()));
                            map.put("orderNo", String.valueOf(nextTeamMapping.getOrderNumber()));
                        }
                        if (nextTeamMapping != null && nextTeamMapping.getTat_id() != null) {
                            map.put("tat_id", String.valueOf(nextTeamMapping.getTat_id()));
                        }
                        map.put("eventId", 0);
                        map.put("eventName", String.valueOf(eventName));

                    }
                    return map;
                } catch (CustomValidationException e) {
                    ApplicationLogger.logger.error(e.getMessage());
                    throw new CustomValidationException(e.getErrCode(), e.getMessage(), null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            return map;
        }
        catch (CustomValidationException e) {
            e.printStackTrace();
            ApplicationLogger.logger.error(e.getMessage());
            throw new CustomValidationException(e.getErrCode(), e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    private Integer getFinalOrderNumber(Boolean isApproveRequest, boolean isCreateRequest, Object entity, List<TeamHierarchyMapping> teamHierarchyMappingList, Integer finalOrderNumber) {
        int teamHierarchyMappingId;
        if (isCreateRequest) {
            finalOrderNumber = 0;
        } else {
            if (Objects.nonNull(entity) && entity.getClass().equals(CustomersPojo.class)) {
                if (((CustomersPojo) entity).getNextTeamHierarchyMapping() != null) {
                    teamHierarchyMappingId = ((CustomersPojo) entity).getNextTeamHierarchyMapping();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }
            } else if (Objects.nonNull(entity) && entity.getClass().equals(PostpaidPlanPojo.class)) {
                if (((PostpaidPlanPojo) entity).getNextTeamHierarchyMapping() != null) {
                    teamHierarchyMappingId = ((PostpaidPlanPojo) entity).getNextTeamHierarchyMapping();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }
            } else if (Objects.nonNull(entity) && entity.getClass().equals(CreditDocument.class)) {
                if (((CreditDocument) entity).getNextTeamHierarchyMappingId() != null) {
                    teamHierarchyMappingId = ((CreditDocument) entity).getNextTeamHierarchyMappingId();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }
            } else if (Objects.nonNull(entity) && entity.getClass().equals(CustomerInventoryMapping.class)) {
                if (((CustomerInventoryMapping) entity).getTeamHierarchyMappingId() != null) {
                    teamHierarchyMappingId = ((CustomerInventoryMapping) entity).getTeamHierarchyMappingId();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }
            } else if (Objects.nonNull(entity) && entity.getClass().equals(CaseDTO.class)) {
                if (((CaseDTO) entity).getTeamHierarchyMappingId() != null) {
                    teamHierarchyMappingId = ((CaseDTO) entity).getTeamHierarchyMappingId();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }
            } else if (Objects.nonNull(entity) && entity.getClass().equals(PlanGroup.class)) {
                if (((PlanGroup) entity).getNextTeamHierarchyMappingId() != null) {
                    teamHierarchyMappingId = ((PlanGroup) entity).getNextTeamHierarchyMappingId();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }

            } else if (Objects.nonNull(entity) && entity.getClass().equals(CustomerAddressPojo.class)) {
                if (((CustomerAddressPojo) entity).getNextTeamHierarchyMappingId() != null) {
                    teamHierarchyMappingId = ((CustomerAddressPojo) entity).getNextTeamHierarchyMappingId();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }
            } else if (Objects.nonNull(entity) && entity.getClass().equals(LeadMgmtWfDTO.class)) {
                if (((LeadMgmtWfDTO) entity).getNextTeamMappingId() != null) {
                    teamHierarchyMappingId = ((LeadMgmtWfDTO) entity).getNextTeamMappingId();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }
            } else if (Objects.nonNull(entity) && entity.getClass().equals(CustomerServiceMapping.class)) {
                if (((CustomerServiceMapping) entity).getNextTeamHierarchyMappingId() != null) {
                    teamHierarchyMappingId = ((CustomerServiceMapping) entity).getNextTeamHierarchyMappingId();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }
            } else if (Objects.nonNull(entity) && entity.getClass().equals(OrganizationBillDTO.class)) {
                if (((OrganizationBillDTO) entity).getDebitDocument().getNextTeamHierarchyMappingId() != null) {
                    teamHierarchyMappingId = ((OrganizationBillDTO) entity).getDebitDocument().getNextTeamHierarchyMappingId();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }
            } else if (Objects.nonNull(entity) && entity.getClass().equals(PartnerPaymentDTO.class)) {
                if (((PartnerPaymentDTO) entity).getNextTeamHierarchyMappingId() != null) {
                    teamHierarchyMappingId = ((PartnerPaymentDTO) entity).getNextTeamHierarchyMappingId();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }
            } else if (Objects.nonNull(entity) && entity.getClass().equals(CustomerDocDetailsDTO.class)) {
                if (((CustomerDocDetailsDTO) entity).getNextTeamHierarchyMappingId() != null) {
                    teamHierarchyMappingId = ((CustomerDocDetailsDTO) entity).getNextTeamHierarchyMappingId();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }
            } else if (Objects.nonNull(entity) && entity.getClass().equals(CustSpecialPlanRelMapppingPojo.class)) {
                if (((CustSpecialPlanRelMapppingPojo) entity).getNextTeamHierarchyMapping() != null) {
                    teamHierarchyMappingId = ((CustSpecialPlanRelMapppingPojo) entity).getNextTeamHierarchyMapping();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }


            } else if (Objects.nonNull(entity) && entity.getClass().equals(InOutWardMACMapping.class)) {
                if (((InOutWardMACMapping) entity).getTeamHierarchyMappingId() != null) {
                    teamHierarchyMappingId = ((InOutWardMACMapping) entity).getTeamHierarchyMappingId();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }


            } else if (Objects.nonNull(entity) && entity.getClass().equals(LeadQuotationWfDTO.class)) {
                if (((LeadQuotationWfDTO) entity).getNextTeamMappingId() != null) {
                    teamHierarchyMappingId = ((LeadQuotationWfDTO) entity).getNextTeamMappingId();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }


            } else if (Objects.nonNull(entity) && entity.getClass().equals(CustSpecialPlanRelMappping.class)) {
                if (((CustSpecialPlanRelMappping) entity).getNextTeamHierarchyMapping() != null) {
                    teamHierarchyMappingId = ((CustSpecialPlanRelMappping) entity).getNextTeamHierarchyMapping();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }
            }
        }
        return finalOrderNumber;
    }

    private Integer getFinalOrderNumber(Boolean isApproveRequest, List<TeamHierarchyMapping> teamHierarchyMappingList, Integer finalOrderNumber,
                                        int teamHierarchyMappingId) {
        List<TeamHierarchyMapping> teamHierarchyMappings = teamHierarchyMappingList.stream().filter(teamHierarchyMapping -> teamHierarchyMapping.getId().equals(teamHierarchyMappingId)).collect(Collectors.toList());
        if (teamHierarchyMappings.size() > 0) {
            if (isApproveRequest) {
                finalOrderNumber = teamHierarchyMappings.get(0).getOrderNumber() + 1;
            } else {
                finalOrderNumber = teamHierarchyMappings.get(0).getOrderNumber() - 1;
            }
        }
        return finalOrderNumber;
    }

    @Transactional
    public GenericDataDTO assignFromStaffList(Integer nextAssignStaff, String eventName, Integer entityId,
                                              boolean isApproveRequest) throws NoSuchFieldException {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            System.out.println("------------------------------assignFromStaffList method called------------------------------ ");
            Customers customers = customersRepository.findById(entityId).orElse(null);
            Integer mvnoId=null;
            if(Objects.nonNull(customers)){
                mvnoId=customers.getMvnoId();
            }else{
                mvnoId=getMvnoIdFromCurrentStaff(null);
            }
            StaffUser staffUser = staffUserService.get(getLoggedInUserId(),mvnoId);
            StaffUser assignedToStaff = staffUserService.get(nextAssignStaff,mvnoId);
            PostpaidPlan postpaidPlan = null;
            PlanGroup planGroup = null;
            CreditDocument creditDocument = null;
            CustomerInventoryMapping customerInventoryMapping = new CustomerInventoryMapping();
            CaseDTO caseDTO = null;
            CustomerAddress customerAddress = null;
            PartnerPayment partnerPayment = null;
            CustPlanMappping custPlanMappping = null;
            CustomerServiceMapping customerServiceMapping = null;
            Map<String, String> map = new HashMap<>();
            CustomerDocDetails customerDocDetails = null;
            LeadMaster leadMaster = null;
            switch (eventName) {
                case CommonConstants.WORKFLOW_EVENT_NAME.CAF: {
                    System.out.println("--------------------------CAF assignFromStaffList Initiated--------------------------");
                    System.out.println("-----------customer found from entityId :- "+entityId+"-----------");
                    if(customers!=null){
                        System.out.println("-----------customers.getMvnoId() :- "+customers.getMvnoId()+"-----------");
                        System.out.println("-----------customers.getBuId() :- "+customers.getBuId()+"-----------");
                        map = getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()));
                        System.out.println("------------------ map value: - "+map+"------------------");
                        System.out.println("------------------ map.containsKey(\"staffId\"): - "+map.containsKey("staffId")+"------------------");
                        System.out.println("------------------ map.containsKey(\"nextTatMappingId\"): - "+map.containsKey("nextTatMappingId")+"------------------");
                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                            customers.setNextTeamHierarchyMapping(Integer.valueOf(map.get("nextTatMappingId")));
                            customers.setCurrentAssigneeId(assignedToStaff.getId());
                            workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), customers.getId(), customers.getUsername(), staffUser.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
                            workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), customers.getId(), customers.getUsername(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedToStaff.getUsername());
                            if (map.containsKey("tat_id") &&  map.get("tat_id").equals("null") || !map.get("current_tat_id").equals("null")) {
                                System.out.println("------------------ map.get(\"tat_id\"): - "+map.get("tat_id")+"------------------");
                                System.out.println("------------------ map.get(\"current_tat_id\"): - "+map.get("current_tat_id")+"------------------");
                                if (!map.get("current_tat_id").equals("null")) {
                                    map.put("tat_id", String.valueOf(map.get("current_tat_id")));
                                }
                            }
                            if( map.containsKey("tat_id") && !map.get("tat_id").equals("null") ) {
                                System.out.println("------------------ map.get(\"tat_id\"): - "+map.get("tat_id")+"------------------");
                                customers = updateDateAndTimeForTAT(map,customers);
                            }
                            customersService.save(customers);
//                        if(customers.getNextTeamHierarchyMapping()!=null) {
//                            tatUtils.changeTatAssignee(customers, staffUser, false, true);
//                        }
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER + " with name : " + " ' " + customers.getFullName() + " '";
                            sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), customers.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());

                        }
                    }

                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN: {
                    postpaidPlan = postpaidPlanRepo.findById(entityId).orElse(null);
                    map = getTeamForNextApproveForAuto(postpaidPlan.getMvnoId(), postpaidPlan.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PLAN, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, postpaidPlanMapper.domainToDTO(postpaidPlan, new CycleAvoidingMappingContext()));
                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                        postpaidPlan.setNextTeamHierarchyMapping(Integer.valueOf(map.get("nextTatMappingId")));
                        postpaidPlan.setNextStaff(assignedToStaff.getId());
                        workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), postpaidPlan.getId(), postpaidPlan.getName(), postpaidPlan.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
                        workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), postpaidPlan.getId(), postpaidPlan.getName(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedToStaff.getUsername());
                        postpaidPlanService.save(postpaidPlan);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.PLAN + " with name : " + " ' " + postpaidPlan.getName() + " '";
                        sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT: {
                    creditDocument = creditDocRepository.findById(entityId).orElse(null);
                    if(creditDocument.getStatus().equalsIgnoreCase("pending")) {
                        map = getTeamForNextApproveForAuto(creditDocument.getMvnoId(), creditDocument.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, creditDocument);
                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                            creditDocument.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                            creditDocument.setApproverid(assignedToStaff.getId());
                            workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), creditDocument.getId(), creditDocument.getReferenceno(), staffUser.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
                            workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), creditDocument.getId(), creditDocument.getReferenceno(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedToStaff.getUsername());
                            creditDocService.save(creditDocument);
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.PAYMENT + " with payment amount : " + " ' " + creditDocument.getAmount() + " ' " + "and " + "reference number : " + " ' " + creditDocument.getReferenceno() + " '";
                            sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action, assignedToStaff.getId().longValue());
                        }
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP: {
                    planGroup = planGroupRepository.findById(entityId).orElse(null);
                    map = getTeamForNextApproveForAuto(planGroup.getMvnoId(), planGroup.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, planGroup);
                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                        planGroup.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        planGroup.setNextStaff(assignedToStaff.getId());
                        workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), planGroup.getPlanGroupId(), planGroup.getPlanGroupName(), planGroup.getPlanGroupId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
                        workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), planGroup.getPlanGroupId(), planGroup.getPlanGroupName(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedToStaff.getUsername());
                        planGroupService.save(planGroup);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.PLAN_GROUP + " with name : " + " ' " + planGroup.getPlanGroupName() + " '";
                        sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN: {
                    customerInventoryMapping = customerInventoryMappingRepo.findById(Long.valueOf(entityId)).orElse(null);
                    if (customerInventoryMapping != null) {
                        map = getTeamForNextApproveForAuto(customerInventoryMapping.getCustomer().getMvnoId(), customerInventoryMapping.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerInventoryMapping);
                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                            customerInventoryMapping.setNextApprover(assignedToStaff);
                            customerInventoryMapping.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(customerInventoryMapping.getId()), customerInventoryMapping.getProduct().getName(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedToStaff.getUsername());
//                            generateAudit(isApproveRequest, staffUser, customerInventoryMapping, map);
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.INVENTORY + " with product name : " + " ' " + customerInventoryMapping.getProduct().getName() + " ' " + "and " + "quantity : " + " ' " + customerInventoryMapping.getQty() + " '";
                            sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());
                        }
                        customerInventoryMappingRepo.save(customerInventoryMapping);
                    }
                    break;
                }
//                case CommonConstants.WORKFLOW_EVENT_NAME.CASE: {
//                    Case aCase = caseService.getRepository().findById(Long.valueOf(entityId)).orElse(null);
//                    CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
//                    caseUpdateDTO.setTicketId(aCase.getCaseId());
//                    if (aCase != null) {
//                        map = getTeamForNextApproveForAuto(aCase.getCustomers().getMvnoId(), aCase.getCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
//                            caseUpdateDTO.setAssignee(assignedToStaff.getId());
//                            caseUpdateDTO.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
//                            if (!aCase.getCaseStatus().equalsIgnoreCase("Follow Up")) {
//                                caseService.updateFollowUpDateAndTimeForTicketAfterPickedUp(aCase);
//                            }
////                            caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
//                            TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Integer.valueOf(map.get("nextTatMappingId"))).orElse(null);
//                            Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
//                            String nextFollowupDate = aCase.getNextFollowupDate().toString();
//                            String nextFollwupTime = aCase.getNextFollowupTime().toString();//workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), staffUser.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Approved By :- " + staffUser.getUsername());
//                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedToStaff.getUsername());
//                            caseUpdateService.sendAssignTicketMessege(aCase.getCustomers().getUsername(), aCase.getCustomers().getMobile(), aCase.getCustomers().getEmail(), aCase.getCustomers().getMvnoId(), aCase.getCaseNumber(), teams.getName(), nextFollowupDate, aCase.getCustomers().getUsername(), nextFollwupTime);
//                            String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + aCase.getCaseNumber() + " ' " + "for " + "customer name : " + " ' " + aCase.getCustomers().getUsername() + " '";
//                            try {
//                                caseDTO = caseUpdateService.updateEntity(caseUpdateDTO, null, false);
//                            } catch (Exception e) {
//                                ApplicationLogger.logger.error("Error in update ticket " + aCase.getCaseNumber());
//                            }
//                            sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());
//                        }
//
//                    }
//                    break;
//                }
                case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION: {
                    customers = customersRepository.findById(entityId).orElse(null);
                    map = getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()));
                    CustomerApprove customerApprove = customersService.finCustmerApproveForTermination(customers.getId());
                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                        customers.setNextTeamHierarchyMapping(Integer.valueOf(map.get("nextTatMappingId")));
                        StaffUser staffUser1 = staffUserRepository.findById(Integer.valueOf(map.get("staffId"))).orElse(null);
                        customers.setCafApproveStatus("Approved");
                        if (assignedToStaff.getStaffUserparent() != null) {
                            customerApprove.setParentStaff(assignedToStaff.getStaffUserparent().getUsername());
                        } else {
                            customerApprove.setParentStaff(null);
                        }
                        customers.setCurrentAssigneeId(nextAssignStaff);
                        customerApprove.setCurrentStaff(assignedToStaff.getUsername());
                        customerApprove.setFirstName(assignedToStaff.getFirstname());
                        customerApprove.setLastName(assignedToStaff.getLastname());
                        customerApprove.setCustName(customers.getUsername());
//                        customerCafAssignmentRepository.save(customeTerminationAssignment);
                        workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), customers.getId(), customers.getUsername(), staffUser.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
                        workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), customers.getId(), customers.getUsername(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedToStaff.getUsername());
                        customersService.save(customers);
                        customerApproveRepo.save(customerApprove);
//                        if(customers.getNextTeamHierarchyMapping()!=null) {
//                            tatUtils.changeTatAssignee(customers, staffUser, false, true);
//                        }

                        workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), customers.getId(), customers.getUsername(), staffUser.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
                        workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), customers.getId(), customers.getUsername(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedToStaff.getUsername());
                        if (map.containsKey("tat_id") && map.get("tat_id").equals("null") && !map.get("current_tat_id").equals("null")) {
                            if (!map.get("current_tat_id").equals("null")) {
                                map.put("tat_id", map.get("current_tat_id"));
                            }
                        }
                        if( map.containsKey("tat_id") &&  !map.get("tat_id").equals("null") ) {
                            customers = updateDateAndTimeForTAT(map,customers);
                        }
                        customersService.save(customers);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER + " with name : " + " ' " + customers.getFullName() + " '";
                        sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), customers.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());

                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION: {
                    customerAddress = customerAddressRepository.findById(entityId).orElse(null);
                    try {
                        CustomerAddressPojo customerAddressPojo = customerAddressService.convertCustomerAddressModelToCustomerAddressPojo(customerAddress);
                        Customers customers1 = customersService.getById(customerAddressPojo.getCustomerId());
                        map = getTeamForNextApproveForAuto(customers1.getMvnoId(), customers1.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerAddressPojo);
                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                            customerAddress.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                            customerAddress.setNextStaff(assignedToStaff.getId());
                            workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), customerAddress.getId(), customers1.getFullName(), customerAddress.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
                            workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), customerAddress.getId(), customers1.getFullName(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedToStaff.getUsername());
                            customerAddressService.save(customerAddress);
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.SHIFT_LOCATION + " for customer : " + " ' " + customerAddress.getCustomer().getFullName() + " '";
                            sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_TERMINATION: {
                    customerServiceMapping = customerServiceMappingRepository.findById(entityId).orElse(null);
                    customers = customersRepository.findById(customerServiceMapping.getCustId()).orElse(null);
                    map = getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_TERMINATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerServiceMapping);
                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                        customerServiceMapping.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        customerServiceMapping.setNextStaff(assignedToStaff.getId());
                        workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), customerServiceMapping.getId(), customers.getCustname(), staffUser.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
                        workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), customerServiceMapping.getId(), customers.getCustname(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedToStaff.getUsername());
                        customerServiceMappingRepository.save(customerServiceMapping);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER_SERVICE_TERMINATION + " for customer name : " + " ' " + customers.getFullName() + " '";
                        sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());

                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CHANGE_DISCOUNT: {
                    customerServiceMapping = customerServiceMappingRepository.findById(entityId).orElse(null);
                    customers = customersRepository.findById(customerServiceMapping.getCustId()).orElse(null);
                    map = getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CHANGE_DISCOUNT, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerServiceMapping);
                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                        customerServiceMapping.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        customerServiceMapping.setNextStaff(assignedToStaff.getId());
                        workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), customerServiceMapping.getId(), customers.getCustname(), staffUser.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
                        workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), customerServiceMapping.getId(), customers.getCustname(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedToStaff.getUsername());
                        customerServiceMappingRepository.save(customerServiceMapping);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.CHANGE_DISCOUNT + " for customer name : " + " ' " + customers.getFullName() + " '";
                        sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());

                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION: {
                    DebitDocument debitDocument = debitDocRepository.findById(entityId).orElse(null);
                    if (debitDocument != null) {
                        custPlanMappping = custPlanMappingRepository.findById(debitDocument.getCustpackrelid());
                        CustPlanMappping actualCustomerPlanMapping = custPlanMappingRepository.findById(custPlanMappping.getCustRefId());
                        OrganizationBillDTO organizationBillDTO = new OrganizationBillDTO();
                        organizationBillDTO.setDebitDocument(debitDocument);
                        organizationBillDTO.setActualCustomers(actualCustomerPlanMapping.getCustomer());
                        map = getTeamForNextApproveForAuto(organizationBillDTO.getActualCustomers().getMvnoId(), organizationBillDTO.getActualCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, organizationBillDTO);
                        debitDocument.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        debitDocument.setNextStaff(assignedToStaff.getId());
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId(), debitDocument.getCustRefName(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Remarks  : " + "\n" + "Assigned to :- " + assignedToStaff.getUsername());
                        debitDocRepository.save(debitDocument);
                        debitDocStaffAssignRepo.deleteAllByDebitDocId(debitDocument.getId());
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.BILL_TO_ORGANIZATION + " ' " + organizationBillDTO.getDebitDocument().getDocnumber() + " '";
                        sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());

                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE: {
                    partnerPayment = partnerPaymentRepository.findById(entityId.longValue()).orElse(null);
                    try {
                        PartnerPaymentDTO partnerPaymentDTO = partnerPaymentService.getMapper().domainToDTO(partnerPayment, new CycleAvoidingMappingContext());
                        map = getTeamForNextApproveForAuto(partnerPayment.getPartner().getMvnoId(), partnerPayment.getPartner().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, partnerPaymentDTO);
                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                            partnerPayment.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                            partnerPayment.setNextStaff(assignedToStaff.getId());
                            //workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), partnerPayment.getPartner().getId(), partnerPayment.getPartner().getName(), partnerPayment.getPartner().getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
                            workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), entityId, partnerPayment.getPartner().getName(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedToStaff.getUsername());
                            partnerPaymentService.getRepository().save(partnerPayment);
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.PARTNER_BALANCE + " for customer : " + " ' " + partnerPayment.getPartner().getName() + " '";
                            sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING: {
                    CustSpecialPlanRelMappping specialPlanRelMappping = custSpecialPlanRelMapppingRepository.findById(entityId.longValue()).orElse(null);
                    try {
                        CustSpecialPlanRelMapppingPojo custSpecialPlanRelMapppingPojo = null;
                        Set<CustSpecialPlanMapppingPojo> set1 = new HashSet<>();
                        if (specialPlanRelMappping != null) {
                            // Convert specialPlanRelMappping to CustSpecialPlanRelMapppingPojo
                            custSpecialPlanRelMapppingPojo =
                                    custSpecialPlanRelMapper.domainToDTO(specialPlanRelMappping, new CycleAvoidingMappingContext());
                            // Check if CustSpecialPlanMapppingList is not null
                            if (specialPlanRelMappping.getCustSpecialPlanMapppingList() != null) {
                                // Convert CustSpecialPlanMapppingList to CustSpecialPlanMapppingPojo
                                for (CustSpecialPlanMappping custSpecialPlanMappping : specialPlanRelMappping.getCustSpecialPlanMapppingList()) {
                                    if (custSpecialPlanMappping.getNormalPlan() != null) {
                                        set1.add(custSpecialPlanMapper.domainToDTO(custSpecialPlanMappping, new CycleAvoidingMappingContext()));
                                        custSpecialPlanRelMapppingPojo.setPlanMapping(set1);
                                    } else if (custSpecialPlanMappping.getNormalPlanGroup() != null) {
                                        PostpaidPlan plan = custSpecialPlanMappping.getNormalPlanGroup().getPlanMappingList().get(0).getPlan();
                                        set1.add(custSpecialPlanMapper.domainToDTO(custSpecialPlanMappping, new CycleAvoidingMappingContext()));
                                        custSpecialPlanRelMapppingPojo.setPlanGroupMapping(set1);
                                    }
                                }
                            }

                            // Set the converted set to the CustSpecialPlanRelMapppingPojo

                        }
                        map = getTeamForNextApproveForAuto(specialPlanRelMappping.getMvnoId(), specialPlanRelMappping.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, specialPlanRelMappping);
                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                            specialPlanRelMappping.setNextTeamHierarchyMapping(Integer.valueOf(map.get("nextTatMappingId")));
                            specialPlanRelMappping.setNextStaff(assignedToStaff.getId());
                            workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), Math.toIntExact(entityId), specialPlanRelMappping.getMappingName(), assignedToStaff.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
                            workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), Math.toIntExact(entityId), specialPlanRelMappping.getMappingName(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedToStaff.getUsername());
                            custSpecialPlanRelMapppingRepository.save(specialPlanRelMappping);
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.SPECIAL_PLAN_MAPPING + " for planmapping name : " + " ' " + specialPlanRelMappping.getMappingName() + " '";
                            sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION: {
                    customerDocDetails = customerDocDetailsRepository.findById(entityId.longValue()).orElse(null);
                    try {
                        CustomerDocDetailsDTO customerDocDetailsDTO = customerDocDetailsService.getMapper().domainToDTO(customerDocDetails, new CycleAvoidingMappingContext());
                        map = getTeamForNextApproveForAuto(customerDocDetails.getCustomer().getMvnoId(), customerDocDetails.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerDocDetailsDTO);
                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                            customerDocDetails.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                            customerDocDetails.setNextStaff(assignedToStaff.getId());
                            workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), Math.toIntExact(customerDocDetails.getDocId()), customerDocDetails.getCustomer().getUsername(), assignedToStaff.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
                            workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), Math.toIntExact(customerDocDetails.getDocId()), customerDocDetails.getCustomer().getUsername(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedToStaff.getUsername());
                            customerDocDetailsService.getRepository().save(customerDocDetails);
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER_DOCUMENT + " for customer : " + " ' " + customerDocDetails.getCustomer().getUsername() + " '";
                            sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE: {
                    creditDocument = creditDocRepository.findById(entityId).orElse(null);
                    if(creditDocument.getStatus().equalsIgnoreCase("pending")) {
                        map = getTeamForNextApproveForAuto(creditDocument.getMvnoId(), creditDocument.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, creditDocument);
                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                            creditDocument.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                            creditDocument.setApproverid(assignedToStaff.getId());
                            workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), creditDocument.getId(), creditDocument.getReferenceno(), staffUser.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
                            workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), creditDocument.getId(), creditDocument.getReferenceno(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedToStaff.getUsername());
                            creditDocService.save(creditDocument);
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.CREDIT_NOTE + " with payment amount : " + " ' " + creditDocument.getAmount() + " ' " + "and " + "reference number : " + " ' " + creditDocument.getReferenceno() + " '";
                            sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action, assignedToStaff.getId().longValue());
                        }
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_REPLACE: {
                    InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(Long.valueOf(entityId)).orElse(null);
                    customerInventoryMapping = customerInventoryMappingRepo.findById(inOutWardMACMapping.getCustInventoryMappingId()).get();
                    if (inOutWardMACMapping != null) {
                        map = getTeamForNextApproveForAuto(customerInventoryMapping.getCustomer().getMvnoId(), customerInventoryMapping.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, inOutWardMACMapping);
                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                            inOutWardMACMapping.setCurrentApproveId(assignedToStaff.getId());
                            inOutWardMACMapping.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(inOutWardMACMapping.getId()), inOutWardMACMapping.getSerialNumber(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedToStaff.getUsername());
//                            generateAudit(isApproveRequest, staffUser, customerInventoryMapping, map);
//                            String action = CommonConstants.WORKFLOW_MSG_ACTION.INVENTORY + " with product name : " + " ' " + customerInventoryMapping.getProduct().getName() + " ' " + "and " + "quantity : " + " ' " + customerInventoryMapping.getQty() + " '";
//                            sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());
                        }
                        inOutWardMacRepo.save(inOutWardMACMapping);
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD: {
                    customerServiceMapping = customerServiceMappingRepository.findById(entityId).orElse(null);
                    customers = customersRepository.findById(customerServiceMapping.getCustId()).orElse(null);
                    map = getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerServiceMapping);
                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                        customerServiceMapping.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        customerServiceMapping.setNextStaff(assignedToStaff.getId());
                        workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), customerServiceMapping.getId(), customers.getCustname(), staffUser.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
                        workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), map.get("eventName"), customerServiceMapping.getId(), customers.getCustname(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedToStaff.getUsername());
                        customerServiceMappingRepository.save(customerServiceMapping);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER_SERVICE_ADD + " for customer name : " + " ' " + customers.getFullName() + " '";
                        sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), assignedToStaff.getMvnoId(), assignedToStaff.getFullName(), action,assignedToStaff.getId().longValue());

                    }
                    break;
                }


            }
            if (eventName.equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
                if (assignedToStaff.getStaffUserparent() != null && !CollectionUtils.isEmpty(map) && caseDTO != null && !map.get("eventId").equals("0") && !map.get("eventId").equals(null)) {
                    tatUtils.saveOrUpdateTicketTatMatrix(caseDTO, map, assignedToStaff, false);
                }
            }
            else if (assignedToStaff.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                List<TatMatrixWorkFlowDetails> list = tatMatrixWorkFlowDetailsRepo.findAllByStaffIdAndEntityIdAndEventNameAndIsActive(Integer.parseInt(map.get("staffId")), Integer.parseInt(map.get("eventId")), map.get("eventName"), true);
                list.stream().peek(i -> i.setIsActive(false)).collect(Collectors.toList());
                tatMatrixWorkFlowDetailsRepo.saveAll(list);
                map.put("entityId", String.valueOf(entityId));
                map.put("eventId", String.valueOf(entityId));
                if (map.containsKey("tat_id")) {
                    if (map.get("tat_id").equals("null")) {
                        if (!map.get("current_tat_id").equals("null")) {
                            map.put("tat_id", String.valueOf(map.get("current_tat_id")));
                        }
                    }
                }
                if (map.containsKey("tat_id") && !map.get("tat_id").equals("null")) {
                    Long tat_id = Long.valueOf(map.get("tat_id"));
                    Optional<Matrix> matrixDetails = matrixRepository.findById(tat_id);
                    if (matrixDetails.isPresent()) {
                        Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();
                    }
                    tatUtils.saveOrUpdateDataForTatMatrix(map, assignedToStaff, entityId, matrixDetails.get().getId());
                }
            } else if (!CollectionUtils.isEmpty(map)) {
                List<TatMatrixWorkFlowDetails> list = tatMatrixWorkFlowDetailsRepo.findAllByStaffIdAndEntityIdAndEventNameAndIsActive(Integer.parseInt(map.get("staffId")), Integer.parseInt(map.get("eventId")), map.get("eventName"), true);
                list.stream().peek(i -> i.setIsActive(false)).collect(Collectors.toList());
                tatMatrixWorkFlowDetailsRepo.saveAll(list);
                map.put("entityId", String.valueOf(entityId));
                map.put("eventId", String.valueOf(entityId));
                if (map.containsKey("tat_id")) {
                    if (map.get("tat_id").equals("null")) {
                        if (!map.get("current_tat_id").equals("null")) {
                            map.put("tat_id", String.valueOf(map.get("current_tat_id")));
                        }
                    }
                }
                if (map.containsKey("tat_id") && !map.get("tat_id").equals("null")) {
                    Long tat_id = Long.valueOf(map.get("tat_id"));
                    Optional<Matrix> matrixDetails = matrixRepository.findById(tat_id);
                    if (matrixDetails.isPresent()) {
                        Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();
                    }
                    tatUtils.saveOrUpdateDataForTatMatrix(map, assignedToStaff, entityId, matrixDetails.get().getId());
                }
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }

    private void generateAudit(boolean isApproveRequest, StaffUser staffUser, CustomerInventoryMapping
            customerInventoryMapping, Map<String, String> map) {
        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(customerInventoryMapping.getId()), customerInventoryMapping.getProduct().getName(), staffUser.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + staffUser.getUsername());
    }


    @Transactional
    public void leadManagementWorkflowRequest(LeadMgmtWfDTO leadMgmtWfDTO) {
        StaffUser loggedInUser = staffUserService.get(leadMgmtWfDTO.getCurrentLoggedInStaffId(),leadMgmtWfDTO.getMvnoId().intValue());
        LeadMgmtWfDTO sendUpdateDto = new LeadMgmtWfDTO();
        sendUpdateDto.setStatus(leadMgmtWfDTO.getStatus());
        sendUpdateDto.setMvnoId(leadMgmtWfDTO.getMvnoId());
        sendUpdateDto.setBuId(leadMgmtWfDTO.getBuId());
        LeadMaster leadMaster = leadMasterRepository.findById(leadMgmtWfDTO.getId()).orElse(null);

        if (leadMgmtWfDTO.getStatus().equals(CommonConstants.LEADINQ) || leadMgmtWfDTO.getStatus().equals(CommonConstants.LEADRINQ)) {
            if (clientServiceSrv.getValueByNameAndmvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN, leadMgmtWfDTO.getMvnoId().intValue()).equals("TRUE")) {
                Map<String, String> map = getTeamForNextApproveForAuto(Math.toIntExact(leadMgmtWfDTO.getMvnoId()), leadMgmtWfDTO.getBuId() == null ? null : leadMgmtWfDTO.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD, CommonConstants.HIERARCHY_TYPE, false, true, leadMgmtWfDTO);
                if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Integer.valueOf(map.get("nextTatMappingId"))).orElse(null);
                    Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
                    StaffUser assignedStaffUser = staffUserService.get(Integer.valueOf(map.get("staffId")),leadMgmtWfDTO.getMvnoId().intValue());
                    CustomerCafAssignmentService customerCafAssignmentService = SpringContext.getBean(CustomerCafAssignmentService.class);
                    sendUpdateDto.setNextTeamMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                    sendUpdateDto.setNextApproveStaffId(Integer.valueOf(map.get("staffId")));
                    sendUpdateDto.setId(leadMgmtWfDTO.getId());
                    sendUpdateDto.setCurrentLoggedInStaffId(leadMgmtWfDTO.getCurrentLoggedInStaffId());
                    sendUpdateDto.setTeamName(teams.getName());
                    sendUpdateDto.setFlag("Assigned");
                    String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD + " for Lead Name : " + " ' " + leadMgmtWfDTO.getFirstname() + " '";
                    workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD, leadMgmtWfDTO.getId().intValue(), leadMgmtWfDTO.getFirstname(), assignedStaffUser.getId(), assignedStaffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedStaffUser.getUsername());
                    sendWorkflowAssignActionMessage(assignedStaffUser.getCountryCode(), assignedStaffUser.getPhone(), assignedStaffUser.getEmail(), assignedStaffUser.getMvnoId(), assignedStaffUser.getFullName(), action,assignedStaffUser.getId().longValue());

                } else {
                    sendUpdateDto.setNextApproveStaffId(loggedInUser.getId());
                    sendUpdateDto.setNextTeamMappingId(null);
                    sendUpdateDto.setId(leadMgmtWfDTO.getId());
                    sendUpdateDto.setCurrentLoggedInStaffId(leadMgmtWfDTO.getCurrentLoggedInStaffId());
                    sendUpdateDto.setTeamName("-");
                    sendUpdateDto.setFlag("Assigned");
                    String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD + " for Lead Name : " + " ' " + leadMgmtWfDTO.getFirstname() + " '";
                    workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD, leadMgmtWfDTO.getId().intValue(), leadMgmtWfDTO.getFirstname(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + loggedInUser.getUsername());
                    sendWorkflowAssignActionMessage(loggedInUser.getCountryCode(), loggedInUser.getPhone(), loggedInUser.getEmail(), loggedInUser.getMvnoId(), loggedInUser.getFullName(), action,loggedInUser.getId().longValue());
                }
            } else {
                sendUpdateDto.setNextApproveStaffId(loggedInUser.getId());
                sendUpdateDto.setNextTeamMappingId(null);
                sendUpdateDto.setId(leadMgmtWfDTO.getId());
                sendUpdateDto.setCurrentLoggedInStaffId(leadMgmtWfDTO.getCurrentLoggedInStaffId());
                sendUpdateDto.setTeamName("-");
                sendUpdateDto.setFlag("Assigned");
                Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(Math.toIntExact(loggedInUser.getMvnoId()), leadMaster.getBuId() == null ? null : leadMaster.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD, CommonConstants.HIERARCHY_TYPE, false, true, sendUpdateDto);
                Hierarchy hierarchy = null;
                HashMap<String, String> tatMapDetails = new HashMap<>();
                Optional<StaffUser> staffUser = staffUserRepository.findById(loggedInUser.getId());
                if (map.containsKey("tat_id") && !CollectionUtils.isEmpty(map) && !map.get("tat_id").equals("null")) {
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
                                leadMaster.setSlaUnit(matrix1.getRunit());
                                Long minutes;
                                Long hours;
                                if (matrix1.getRunit().equalsIgnoreCase("MIN")) {
                                    minutes = Long.valueOf(matrix1.getRtime());

                                    LocalTime currentTimenow = LocalTime.now();
                                    LocalTime nextFollowupTime = currentTimenow.plusMinutes(minutes);
                                    if (nextFollowupTime.getHour() < currentTimenow.getHour() ||
                                            (nextFollowupTime.getHour() == currentTimenow.getHour() && nextFollowupTime.getMinute() < currentTimenow.getMinute())) {
                                        LocalDate nextFollowupDate = LocalDate.now().plusDays(1);

                                        leadMaster.setNextfollowupdate(nextFollowupDate);
                                        leadMaster.setNextfollowuptime(nextFollowupTime);
                                    } else {
                                        leadMaster.setNextfollowupdate(LocalDate.now());
                                        leadMaster.setNextfollowuptime(nextFollowupTime);
                                    }
                                } else if (matrix1.getRunit().equalsIgnoreCase("HOUR")) {
                                    hours = Long.valueOf(matrix1.getRtime());
                                    LocalTime currentTimenow = LocalTime.now();
                                    long total_hours = currentTimenow.getHour() + hours;
                                    if (total_hours >= 24) {
                                        long days = total_hours / 24;
                                        long remainingHours = total_hours % 24;
                                        long remainingMinutes = currentTimenow.getMinute();
                                        leadMaster.setNextfollowupdate(LocalDate.now().plusDays(days));
                                        leadMaster.setNextfollowuptime(LocalTime.of((int) remainingHours, (int) remainingMinutes));
                                    } else {
                                        leadMaster.setNextfollowupdate(LocalDate.now());
                                        leadMaster.setNextfollowuptime(currentTimenow.plusHours(hours));
                                    }
                                } else {
                                    leadMaster.setNextfollowupdate(LocalDate.now().plusDays(Long.valueOf(matrix1.getRtime())));
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
                                                    leadMaster.getId().intValue(), tatMapDetails.get("eventName"), tatMapDetails.get("eventId") != null ? Integer.valueOf(tatMapDetails.get("eventId")) : null, CommonConstants.NOTIFICATION_TYPE_STAFF, null, true);
//                                               tatUtils.saveOrUpdateDataForTatMatrix( tatMapDetails, staffUser.get(),subscriber.getId(),null);
                                    tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                                    //tatUtils.saveOrUpdateDataForTatMatrix( tatMapDetails, staffUser.get(),customers.getId(),null);
                                    //	tatUtils.saveOrUpdateDataForTatMatrix(tatMapDetails, staffUser.get(), leadMaster.getId().intValue(), null);
                                }

                            }
                        }
                    }
                }
                sendUpdateDto.setNextfollowuptime(leadMaster.getNextfollowuptime().truncatedTo(ChronoUnit.SECONDS).toString());
                sendUpdateDto.setNextfollowupdate(leadMaster.getNextfollowupdate().toString());
                leadMasterRepository.save(leadMaster);
                String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD + " for Lead Name : " + " ' " + leadMgmtWfDTO.getFirstname() + " '";
                workflowAuditService.saveAudit(null, CommonConstants.EVENT_NAME.LEAD, leadMgmtWfDTO.getId().intValue(), leadMgmtWfDTO.getFirstname(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + loggedInUser.getUsername());
                sendWorkflowAssignActionMessage(loggedInUser.getCountryCode(), loggedInUser.getPhone(), loggedInUser.getEmail(), leadMgmtWfDTO.getMvnoId().intValue(), loggedInUser.getFullName(), action,loggedInUser.getId().longValue());
            }
            SendApproverForLeadMsg sendApproverForLeadMsg = new SendApproverForLeadMsg(sendUpdateDto);
//            messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL);
            kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendApproverForLeadMsg.class.getSimpleName()));
        }
    }


    @Override
    public HierarchyDTO getEntityById(Long aLong,Integer mvnoId) throws Exception {
        QHierarchy qHierarchy = QHierarchy.hierarchy;
        List<Hierarchy> hierarchyList = IterableUtils.toList(hierarchyRepository.findAll(qHierarchy.id.eq(aLong)));
        if (hierarchyList.size() > 0) {
            Hierarchy hierarchyFind = hierarchyList.get(0);
            return hierarchyMapper.domainToDTO(hierarchyFind, new CycleAvoidingMappingContext());
        }
        return null;
    }

    @Override
    public HierarchyDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }

    public GenericDataDTO approveLead(LeadMgmtWfDTO leadMgmtWfDTO, GenericDataDTO genericDataDTO) {
        LeadMgmtWfDTO sendUpdateDto = new LeadMgmtWfDTO();
        List<StaffUserPojo> staffUserPojos = new ArrayList<>();
        HashMap<String, String> tatMapDetails = new HashMap<>();
        LeadMaster leadMaster = leadMasterRepository.findById(leadMgmtWfDTO.getId()).orElse(null);
        StaffUser loggedInUser = staffUserRepository.findById(leadMgmtWfDTO.getCurrentLoggedInStaffId()).orElse(null);
        if (clientServiceSrv.getValueByNameAndmvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,leadMaster.getMvnoId().intValue()).equals("TRUE")) {
            StaffUser assignedUser = null;
            Map<String, String> map = getTeamForNextApproveForAuto(Math.toIntExact(leadMgmtWfDTO.getMvnoId()), leadMgmtWfDTO.getBuId() == null ? null : leadMgmtWfDTO.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD, CommonConstants.HIERARCHY_TYPE, leadMgmtWfDTO.isApproveRequest(), false, leadMgmtWfDTO);
            if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Integer.valueOf(map.get("nextTatMappingId"))).orElse(null);
                Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
                StaffUser assignedStaffUser = staffUserRepository.findById(Integer.valueOf(map.get("staffId"))).orElse(null);
                assignedUser = assignedStaffUser;
                leadMaster.setNextApproveStaffId(Integer.valueOf(map.get("staffId")));
                leadMaster.setNextTeamMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                sendUpdateDto.setNextTeamMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                sendUpdateDto.setNextApproveStaffId(Integer.valueOf(map.get("staffId")));
                sendUpdateDto.setId(leadMgmtWfDTO.getId());
                sendUpdateDto.setCurrentLoggedInStaffId(leadMgmtWfDTO.getCurrentLoggedInStaffId());
                sendUpdateDto.setTeamName(teams.getName());
                sendUpdateDto.setFlag("Assigned");
                sendUpdateDto.setRemark(leadMgmtWfDTO.getRemark());
                sendUpdateDto.setRejectedReasonMasterId(leadMgmtWfDTO.getRejectedReasonMasterId());
                SendApproverForLeadMsg sendApproverForLeadMsg = new SendApproverForLeadMsg(sendUpdateDto);
//                messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL);
                kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendApproverForLeadMsg.class.getSimpleName()));
                String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD + " for Lead Name : " + " ' " + leadMaster.getFirstname() + " '";
                workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD, leadMaster.getId().intValue(), leadMaster.getFirstname(), loggedInUser.getId(), loggedInUser.getUsername(), leadMgmtWfDTO.isApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), leadMgmtWfDTO.isApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " with remarks : " + leadMgmtWfDTO.getRemark() + " by :- " + loggedInUser.getUsername());
                workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD, leadMaster.getId().intValue(), leadMaster.getFirstname(), assignedUser.getId(), assignedUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedUser.getUsername());
                sendWorkflowAssignActionMessage(assignedUser.getCountryCode(), assignedStaffUser.getPhone(), assignedStaffUser.getEmail(), leadMaster.getMvnoId().intValue(), assignedUser.getFullName(), action,assignedStaffUser.getId().longValue());
                //updating tatmapping id in apigw lead
                leadMgmtWfDTOToLeadMaster(sendUpdateDto);
            } else {
                sendUpdateDto.setFinalApproved(true);
                genericDataDTO.setData("FINAL_APPROVED");
                sendUpdateDto.setId(leadMgmtWfDTO.getId());
                sendUpdateDto.setCurrentLoggedInStaffId(leadMgmtWfDTO.getCurrentLoggedInStaffId());
                sendUpdateDto.setNextApproveStaffId(leadMgmtWfDTO.getCurrentLoggedInStaffId());
                sendUpdateDto.setFlag("Approved");
//                leadMaster.setNextApproveStaffId(null);
//                leadMaster.setNextTeamMappingId(null);
                sendUpdateDto.setRemark(leadMgmtWfDTO.getRemark());
                tatMapDetails.put("eventId", "0");
                tatMapDetails.put("entityId", leadMgmtWfDTO.getId().toString());
                tatUtils.inActivateTatWorkflowMapping(tatMapDetails);
                sendUpdateDto.setRejectedReasonMasterId(leadMgmtWfDTO.getRejectedReasonMasterId());
                SendApproverForLeadMsg sendApproverForLeadMsg = new SendApproverForLeadMsg(sendUpdateDto);
                workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD, leadMaster.getId().intValue(), leadMaster.getFirstname(), loggedInUser.getId(), loggedInUser.getUsername(), leadMgmtWfDTO.isApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), leadMgmtWfDTO.isApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED + " with remarks : " + leadMgmtWfDTO.getRemark() + " by :- " + loggedInUser.getUsername() : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " with remarks : " + leadMgmtWfDTO.getRemark() + " by :- " + loggedInUser.getUsername());
//                messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL);
                kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendApproverForLeadMsg.class.getSimpleName()));
                //updating tatmapping id in apigw lead
                leadMgmtWfDTOToLeadMaster(sendUpdateDto);
            }
        } else {
            Boolean b = false;

            if (!leadMgmtWfDTO.isApproveRequest() && (leadMgmtWfDTO.getNextTeamMappingId() == null)) {
                b = Integer.parseInt(leadMaster.getCreatedBy()) == leadMgmtWfDTO.getCurrentLoggedInStaffId();
            }
            if (true == b) {
                if (leadMgmtWfDTO.getFlag().equalsIgnoreCase("Reject")) {
                    genericDataDTO.setData("FINAL_REJECTED");
                    sendUpdateDto.setFlag("Rejected");
                    sendUpdateDto.setLeadStatus("Rejected");
                    tatMapDetails.put("eventId", "0");
                    tatMapDetails.put("entityId", leadMgmtWfDTO.getId().toString());
                    tatUtils.inActivateTatWorkflowMapping(tatMapDetails);
                }

                sendUpdateDto.setId(leadMgmtWfDTO.getId());
//                sendUpdateDto.setCurrentLoggedInStaffId(leadMgmtWfDTO.getCurrentLoggedInStaffId());
//                sendUpdateDto.setNextApproveStaffId(leadMgmtWfDTO.getCurrentLoggedInStaffId());
//                leadMaster.setNextApproveStaffId(null);
//                leadMaster.setNextTeamMappingId(null);
//                sendUpdateDto.setFlag("Approved");
                sendUpdateDto.setRemark(leadMgmtWfDTO.getRemark());
                sendUpdateDto.setRejectedReasonMasterId(leadMgmtWfDTO.getRejectedReasonMasterId());
                SendApproverForLeadMsg sendApproverForLeadMsg = new SendApproverForLeadMsg(sendUpdateDto);
                workflowAuditService.saveAudit(null, CommonConstants.EVENT_NAME.LEAD, leadMaster.getId().intValue(), leadMaster.getFirstname(), loggedInUser.getId(), loggedInUser.getUsername(), leadMgmtWfDTO.isApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), leadMgmtWfDTO.isApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED + " with remarks : " + leadMgmtWfDTO.getRemark() + " by :- " + loggedInUser.getUsername() : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + "with remarks : " + leadMgmtWfDTO.getRemark() + " by :- " + loggedInUser.getUsername());
//                messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL);
                kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendApproverForLeadMsg.class.getSimpleName()));
                leadMgmtWfDTOToLeadMaster(sendUpdateDto);

            } else {

                Map<String, Object> map = getTeamForNextApprove(Math.toIntExact(leadMaster.getMvnoId()), leadMgmtWfDTO.getBuId() == null ? null : leadMgmtWfDTO.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD, CommonConstants.HIERARCHY_TYPE, leadMgmtWfDTO.isApproveRequest(), false, leadMgmtWfDTO);
                if (map.containsKey("assignableStaff")) {
                    staffUserPojos = (List<StaffUserPojo>) map.get("assignableStaff");
                    genericDataDTO.setDataList(staffUserPojos);
                    workflowAuditService.saveAudit(null, CommonConstants.EVENT_NAME.LEAD, leadMaster.getId().intValue(), leadMaster.getFirstname(), loggedInUser.getId(), loggedInUser.getUsername(), leadMgmtWfDTO.isApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), leadMgmtWfDTO.isApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED + " with remarks : " + leadMgmtWfDTO.getRemark() + " by :- " + loggedInUser.getUsername() : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + "with remarks : " + leadMgmtWfDTO.getRemark() + " by :- " + loggedInUser.getUsername());
                    tatMapDetails.put("workFlowId", map.get("workFlowId").toString());
                    tatMapDetails.put("eventId", map.get("eventId").toString());
                    tatMapDetails.put("eventName", map.get("eventName").toString());
//                    tatUtils.saveOrUpdateDataForTatMatrix(tatMapDetails, loggedInUser, leadMgmtWfDTO.getId().intValue(),null);
                    return genericDataDTO;
                } else {
                    //sendUpdateDto.setFinalApproved(true);
                    if (leadMgmtWfDTO.getFlag().equalsIgnoreCase("Reject")) {
                        genericDataDTO.setData("FINAL_REJECTED");
                        sendUpdateDto.setFlag("Rejected");
                        sendUpdateDto.setLeadStatus("Rejected");
                        tatMapDetails.put("eventId", leadMaster.getId().toString());
                        tatMapDetails.put("entityId", leadMgmtWfDTO.getId().toString());
                        tatUtils.inActivateTatWorkflowMapping(tatMapDetails);
                    } else {
                        genericDataDTO.setData("FINAL_APPROVED");
                        sendUpdateDto.setFlag("Approved");
                        tatMapDetails.put("eventId", "0");
                        tatMapDetails.put("entityId", leadMgmtWfDTO.getId().toString());
                        tatUtils.inActivateTatWorkflowMapping(tatMapDetails);
                    }
                    sendUpdateDto.setId(leadMgmtWfDTO.getId());
//                sendUpdateDto.setCurrentLoggedInStaffId(leadMgmtWfDTO.getCurrentLoggedInStaffId());
//                sendUpdateDto.setNextApproveStaffId(leadMgmtWfDTO.getCurrentLoggedInStaffId());
//                leadMaster.setNextApproveStaffId(null);
//                leadMaster.setNextTeamMappingId(null);
//                sendUpdateDto.setFlag("Approved");
                    sendUpdateDto.setRemark(leadMgmtWfDTO.getRemark());
                    sendUpdateDto.setRejectedReasonMasterId(leadMgmtWfDTO.getRejectedReasonMasterId());
                    sendUpdateDto.setNextApproveStaffId(getLoggedInUserId());
                    sendUpdateDto.setNextTeamMappingId(leadMgmtWfDTO.getNextTeamMappingId());
                    SendApproverForLeadMsg sendApproverForLeadMsg = new SendApproverForLeadMsg(sendUpdateDto);
                    workflowAuditService.saveAudit(null, CommonConstants.EVENT_NAME.LEAD, leadMaster.getId().intValue(), leadMaster.getFirstname(), loggedInUser.getId(), loggedInUser.getUsername(), leadMgmtWfDTO.isApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), leadMgmtWfDTO.isApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED + " with remarks : " + leadMgmtWfDTO.getRemark() + " by :- " + loggedInUser.getUsername() : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + "with remarks : " + leadMgmtWfDTO.getRemark() + " by :- " + loggedInUser.getUsername());
//                    messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL);
                    kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendApproverForLeadMsg.class.getSimpleName()));

                    leadMgmtWfDTOToLeadMaster(sendUpdateDto);
                }
            }

        }


        return genericDataDTO;
    }


    public LeadMgmtWfDTO assignFromStaffListForLead(Integer nextAssignStaff, String eventName, LeadMgmtWfDTO
            leadMgmtWfDTO) {
        StaffUser assignedUser = null;

        StaffUser loggedInUser = staffUserRepository.findById(getLoggedInUserId()).orElse(null);
        StaffUser assignedToStaff = staffUserRepository.findById(nextAssignStaff).orElse(null);
        LeadMgmtWfDTO sendUpdateDto = new LeadMgmtWfDTO();
        assignedUser = assignedToStaff;
        LeadMaster leadMaster = leadMasterRepository.findById(leadMgmtWfDTO.getId()).orElse(null);
        Map<String, String> map = getTeamForNextApproveForAuto(Math.toIntExact(leadMaster.getMvnoId()), leadMgmtWfDTO.getBuId() == null ? null : leadMgmtWfDTO.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD, CommonConstants.HIERARCHY_TYPE, leadMgmtWfDTO.isApproveRequest(), false, leadMgmtWfDTO);
        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
            TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Integer.valueOf(map.get("nextTatMappingId"))).orElse(null);
            Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
            CustomerCafAssignmentService customerCafAssignmentService = SpringContext.getBean(CustomerCafAssignmentService.class);
            //String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD + " for Lead Name : " + " ' " + leadMaster.getFirstname() + " '";
            //workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD, leadMaster.getId().intValue(), leadMaster.getFirstname(), loggedInUser.getId(), loggedInUser.getUsername(), leadMgmtWfDTO.isApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), leadMgmtWfDTO.isApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED + " with remarks : " + leadMgmtWfDTO.getRemark() + " by :- " + loggedInUser.getUsername() : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + "with remarks : " + leadMgmtWfDTO.getRemark() + " by :- " + loggedInUser.getUsername());
            //workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD, leadMaster.getId().intValue(), leadMaster.getFirstname(), assignedUser.getId(), assignedUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedUser.getUsername());
            sendWorkflowAssignActionMessage(assignedUser.getCountryCode(), assignedUser.getPhone(), assignedUser.getEmail(), leadMaster.getMvnoId().intValue(), assignedUser.getFullName(), CommonConstants.WORKFLOW_MSG_ACTION.LEAD,assignedUser.getId().longValue());
            sendUpdateDto.setNextTeamMappingId(Integer.valueOf(map.get("nextTatMappingId")));
            sendUpdateDto.setNextApproveStaffId(assignedToStaff.getId());
            sendUpdateDto.setId(leadMgmtWfDTO.getId());
            sendUpdateDto.setCurrentLoggedInStaffId(leadMgmtWfDTO.getCurrentLoggedInStaffId());
            sendUpdateDto.setTeamName(teams.getName());
            sendUpdateDto.setFlag("Assigned");

            if (map.containsKey("nextTatMappingId")) {
                leadMaster.setNextTeamMappingId(leadMgmtWfDTO.getNextTeamMappingId());
                leadMaster.setNextApproveStaffId(leadMgmtWfDTO.getNextApproveStaffId());
                if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                    List<TatMatrixWorkFlowDetails> list = tatMatrixWorkFlowDetailsRepo.findAllByStaffIdAndEntityIdAndEventNameAndIsActive(Integer.parseInt(map.get("staffId")), Integer.parseInt(map.get("eventId")), map.get("eventName"), true);
                    list.stream().peek(i -> i.setIsActive(false)).collect(Collectors.toList());
                    tatMatrixWorkFlowDetailsRepo.saveAll(list);
                    if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                        map.put("tat_id", map.get("current_tat_id"));
                    tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, leadMaster.getId().intValue(), null);
                    if (!map.get("tat_id").equalsIgnoreCase("null")) {
                        Optional<Matrix> matrixDetails = matrixRepository.findById(Long.valueOf(map.get("tat_id")));
                        if (matrixDetails.isPresent()) {
                            Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();
                            Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));
                            if (newMatrixDetails.isPresent()) {
                                leadMaster = (LeadMaster) tatUtils.UpdateDateTimefortat(newMatrixDetails.get(), leadMaster, Nextvalue);
                                //details.setStaffId(details.getParentId());
                            }
                        }
                    }
                }

            }
            if (leadMaster.getNextfollowuptime() != null) {
                sendUpdateDto.setNextfollowuptime(leadMaster.getNextfollowuptime().truncatedTo(ChronoUnit.SECONDS).toString());
            }
            if (leadMaster.getNextfollowupdate() != null) {
                sendUpdateDto.setNextfollowupdate(leadMaster.getNextfollowupdate().toString());
            }
            SendApproverForLeadMsg sendApproverForLeadMsg = new SendApproverForLeadMsg(sendUpdateDto);
//            messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL);
            kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendApproverForLeadMsg.class.getSimpleName()));
            //updating tatmapping id in apigw lead
            leadMgmtWfDTOToLeadMaster(sendUpdateDto);

            //Tat  matrix


            leadMasterRepository.save(leadMaster);

        } else {
            sendUpdateDto.setFinalApproved(true);
            sendUpdateDto.setId(leadMgmtWfDTO.getId());
            sendUpdateDto.setFlag("Approved");
            SendApproverForLeadMsg sendApproverForLeadMsg = new SendApproverForLeadMsg(sendUpdateDto);
//            messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL);
            kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendApproverForLeadMsg.class.getSimpleName()));
        }

        return sendUpdateDto;
    }


    public List getApprovalProgressForLead(Integer mvnoId, Long buId, Integer nextTeamHierarchyMappingId) {
        List<TeamHierarchyDTO> teamHierarchyDTOList = new ArrayList<TeamHierarchyDTO>();
        try {
            Optional<Hierarchy> hierarchy;
            QHierarchy qHierarchy = QHierarchy.hierarchy;
            BooleanExpression booleanExpHierarchy = qHierarchy.isNotNull().and(qHierarchy.eventName.eq(CommonConstants.WORKFLOW_EVENT_NAME.LEAD).and(qHierarchy.isDeleted.eq(false)).and(qHierarchy.mvnoId.eq(mvnoId)));
            if (buId != null && buId != 0) {
                booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.eq(buId));
                hierarchy = hierarchyRepository.findOne(booleanExpHierarchy);
            } else {
                booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.isNull());
                hierarchy = hierarchyRepository.findOne(booleanExpHierarchy);
            }
            if (hierarchy.isPresent()) {
                List<TeamHierarchyMapping> teamHierarchyMappings = hierarchy.get().getTeamHierarchyMappingList();
                if (nextTeamHierarchyMappingId == 0 && nextTeamHierarchyMappingId == null) {
                    for (int i = 0; i < teamHierarchyMappings.size(); i++) {
                        TeamHierarchyDTO dto = new TeamHierarchyDTO();
                        dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
                        dto.setStatus("Approved");
                        if (i + 1 == teamHierarchyMappings.size()) {
                            dto.setParentTeamsId(null);
                        } else {
                            dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
                        }
                        dto.setTeamName(teamsRepository.findById(teamHierarchyMappings.get(i).getTeamId().longValue()).get().getName());
                        teamHierarchyDTOList.add(dto);

                    }
                } else {
                    int currentOrder = 0;
                    for (TeamHierarchyMapping t : hierarchy.get().getTeamHierarchyMappingList()) {
                        if (Objects.equals(t.getId(), nextTeamHierarchyMappingId)) {
                            currentOrder = t.getOrderNumber();
                        }
                    }
                    for (int i = 0; i < teamHierarchyMappings.size(); i++) {
                        if (teamHierarchyMappings.get(i).getOrderNumber() < currentOrder) {
                            TeamHierarchyDTO dto = new TeamHierarchyDTO();
                            dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
                            dto.setStatus("Approved");
                            if (i + 1 == teamHierarchyMappings.size()) {
                                dto.setParentTeamsId(null);
                            } else {
                                dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
                            }
                            dto.setTeamName(teamsRepository.findById(teamHierarchyMappings.get(i).getTeamId().longValue()).get().getName());
                            teamHierarchyDTOList.add(dto);
                        } else {
                            TeamHierarchyDTO dto = new TeamHierarchyDTO();
                            dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
                            dto.setStatus("Pending");
                            if (i + 1 == teamHierarchyMappings.size()) {
                                dto.setParentTeamsId(null);
                            } else {
                                dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
                            }
                            dto.setTeamName(teamsRepository.findById(teamHierarchyMappings.get(i).getTeamId().longValue()).get().getName());
                            teamHierarchyDTOList.add(dto);

                        }
                    }
                }
            }
            return teamHierarchyDTOList;


        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage());
        }
        return teamHierarchyDTOList;
    }


    public void sendWorkflowAssignActionMessage(String countryCode, String mobileNumber, String emailId, Integer
            mvnoId, String staffPersonName, String action, Long staffId)
    {
        System.out.println("----------------------------- In sendWorkflowAssignActionMessage---------------------");
        try {

            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.WORKFLOW_ASSIGN_ACTION);
            System.out.println("----------------------------- optionalTemplate: "+optionalTemplate.toString()+"---------------------");

            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    // Set message in queue to send notification after opt generated successfully.
                    Long buId = null;
                    if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                        buId = getBUIdsFromCurrentStaff().get(0);
                    }
                    WorkflowTicketMessage workflowTicketMessage = new WorkflowTicketMessage(RabbitMqConstants.WORKFLOW_ASSIGN_ACTION_MESSAGE, RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, optionalTemplate.get(), staffPersonName, mobileNumber, emailId, mvnoId, action, buId,staffId);
                    Gson gson = new Gson();
                    gson.toJson(workflowTicketMessage);
                    kafkaMessageSender.send(new KafkaMessageData(workflowTicketMessage, WorkflowTicketMessage.class.getSimpleName()));
//                    messageSender.send(workflowTicketMessage, RabbitMqConstants.QUEUE_WORKFLOW_ACTION_ASSIGN_MESSAGE);
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    public List<StaffUserPojo> getStaffFromCurrentTeammapping(Integer id, Object entity) {
        TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(id).orElse(null);
        Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
        List<StaffUserPojo> staffUserPojos = new ArrayList<>();
        if (entity instanceof CaseDTO) {
            CaseDTO aCase = (CaseDTO) entity;
            Customers customers = customersRepository.findById(aCase.getCustomersId()).orElse(null);
            if (customers != null) {
                staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), customers.getBuId(), teams);
            }
        } else if (entity instanceof LeadMaster) {
            LeadMaster leadMaster = (LeadMaster) entity;
            LeadMgmtWfDTO leadMgmtWfDTO = leadMasterToLeadMgmtDTO(leadMaster);
            staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(leadMgmtWfDTO), ((LeadMaster) entity).getBuId(), teams);

        } else if (entity instanceof CustomersPojo) {
            CustomersPojo customersPojo = (CustomersPojo) entity;
            Customers customers = customersRepository.findById(customersPojo.getId()).orElse(null);
            if (customers != null) {
                staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), customers.getBuId(), teams);
            }
        } else if (entity instanceof PostpaidPlanPojo) {
            PostpaidPlanPojo postpaidPlanPojo = (PostpaidPlanPojo) entity;
            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(postpaidPlanPojo.getId()).orElse(null);
            if (postpaidPlan != null) {
                staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), postpaidPlan.getBuId(), teams);
            }
        } else if (entity instanceof PlanGroup) {
            PlanGroup planGroup = (PlanGroup) entity;
            PlanGroup planGroup1 = planGroupRepository.findById(planGroup.getPlanGroupId()).orElse(null);
            if (planGroup != null) {
                staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), planGroup.getBuId(), teams);
            }
        } else if (entity instanceof CustSpecialPlanRelMappping) {
            CustSpecialPlanRelMappping custSpecialPlanRelMappping = (CustSpecialPlanRelMappping) entity;
            CustSpecialPlanRelMappping custSpecialPlanRelMappping1 = custSpecialPlanRelMapppingRepository.findById(custSpecialPlanRelMappping.getId()).orElse(null);
            if (custSpecialPlanRelMappping != null) {
                staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), custSpecialPlanRelMappping.getBuId(), teams);
            }
        } else if (entity instanceof CustomerAddressPojo) {
            CustomerAddressPojo customerAddressPojo = (CustomerAddressPojo) entity;
            CustomerAddress customerAddress = customerAddressRepository.findById(customerAddressPojo.getId()).orElse(null);
            if (customerAddress != null) {
                staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), customerAddress.getCustomer().getBuId(), teams);
            }
        } else if (entity instanceof CustPlanMappping) {
            CustPlanMappping custPlanMappping = (CustPlanMappping) entity;
            if (custPlanMappping != null) {
                staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), custPlanMappping.getCustomer().getBuId(), teams);
            }
        } else if (entity instanceof PartnerPaymentDTO) {
            PartnerPaymentDTO partnerPaymentDTO = (PartnerPaymentDTO) entity;
            if (partnerPaymentDTO != null) {
//                    Partner partner=partnerRepository.findById(Math.toIntExact(partnerPaymentDTO.getId())).get();
                Partner partner = partnerRepository.findById((int) partnerPaymentDTO.getPartnerId().longValue()).orElse(null);

                staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), partner.getBuId(), teams);
            }
        } else if (entity instanceof CustomerDocDetailsDTO) {
            CustomerDocDetailsDTO customerDocDetailsDTO = (CustomerDocDetailsDTO) entity;
            if (customerDocDetailsDTO != null) {
                staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), customerDocDetailsDTO.getCustomer().getBuId(), teams);
            }
        } else if (entity instanceof CreditDocument) {
            CreditDocument creditDocument = (CreditDocument) entity;
            if (creditDocument != null) {
                staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), creditDocument.getCustomer().getBuId(), teams);
            }
        } else if (entity instanceof DebitDocument) {
            DebitDocument debitDocument = (DebitDocument) entity;
            CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(debitDocument.getCustpackrelid());
            CustPlanMappping actualCustomerPlanMapping = custPlanMappingRepository.findById(custPlanMappping.getCustRefId());
            OrganizationBillDTO organizationBillDTO = new OrganizationBillDTO();
            organizationBillDTO.setDebitDocument(debitDocument);
            organizationBillDTO.setActualCustomers(actualCustomerPlanMapping.getCustomer());
            try {
//                CustomersPojo customersPojo = customersService.convertCustomersModelToCustomersPojo(debitDocument.getCustomer());/
                if (debitDocument != null) {
                    staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(organizationBillDTO), actualCustomerPlanMapping.getCustomer().getBuId(), teams);
                } else if (entity instanceof CreditDocument) {
                    CreditDocument creditDocument = (CreditDocument) entity;
                    if (creditDocument != null) {
                        staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), creditDocument.getBuID(), teams);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (entity instanceof CustomerServiceMapping) {
            CustomerServiceMapping customerServiceMapping = (CustomerServiceMapping) entity;
            if (customerServiceMapping != null) {
                Customers customers = customersRepository.findById(customerServiceMapping.getCustId()).orElse(null);
                staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), customers.getBuId(), teams);
            }
        } else if (entity instanceof LeadQuotationDetails) {
            LeadQuotationDetails leadQuotationDetails = (LeadQuotationDetails) entity;
            LeadQuotationWfDTO leadQuotationWfDTO = leadQuotationDetailsToLeadQuotationWfDTO(leadQuotationDetails);
            staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(leadQuotationWfDTO), ((LeadQuotationDetails) entity).getBuId(), teams);

        }
        return staffUserPojos;
    }

    @Transactional
    public GenericDataDTO assignEveryStaff(Integer entityId, String eventName, Boolean isApproveRequest) throws
            Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Object> map = new HashMap<>();
            switch (eventName) {
                case CommonConstants.WORKFLOW_EVENT_NAME.CAF: {
                    Customers customers = customersRepository.findById(entityId).orElse(null);
                    map = getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()));
                    if (!map.isEmpty()) {
                        if (map.containsKey("tat_id") && map.get("tat_id").equals("null") && !map.get("current_tat_id").equals("null")) {
                            map.put("tat_id", map.get("current_tat_id"));
                        }
                        if (map.containsKey("tat_id") && !map.get("tat_id").equals("null")) {
                            Optional<Matrix> matrixDetails = matrixRepository.findById(Long.valueOf((String) map.get("tat_id")));
                            if (matrixDetails.isPresent()) {
                                Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();
                                Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));
                                if (newMatrixDetails.isPresent()) {
                                    customers = (Customers) tatUtils.UpdateDateTimefortat(newMatrixDetails.get(), customers, Nextvalue);
                                    //details.setStaffId(details.getParentId());
                                }
                            }
                        }

                        workflowAssignStaffMappingService.assignWorkflowToStaff(customers.getId(), eventName, customers, map);
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION: {
                    // CustomerDocDetails customerDocDetails = customerDocDetailsRepository.getOne(Long.valueOf(entityId));
                    CustomerDocDetails customerDocDetails = customerDocDetailsRepository.findById(Long.valueOf(entityId)).orElse(null);
                    map = getTeamForNextApprove(customerDocDetails.getCustomer().getMvnoId(), customerDocDetails.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerDocDetailsMapper.domainToDTO(customerDocDetails, new CycleAvoidingMappingContext()));
                    workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, customerDocDetails, map);
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN: {
                    PostpaidPlan postpaidPlan = postpaidPlanService.get(entityId,getMvnoIdFromCurrentStaff(entityId));
                    map = getTeamForNextApprove(postpaidPlan.getMvnoId(), postpaidPlan.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PLAN, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, postpaidPlanMapper.domainToDTO(postpaidPlan, new CycleAvoidingMappingContext()));
                    workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, postpaidPlan, map);
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP: {
                    PlanGroup planGroup = planGroupRepository.findById(entityId).orElse(null);
                    map = getTeamForNextApprove(planGroup.getMvnoId(), planGroup.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, planGroup);
                    workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, planGroup, map);
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT: {
                    CreditDocument creditDocument = creditDocRepository.findById(entityId).orElse(null);
                    if(creditDocument.getStatus().equalsIgnoreCase("pending")) {
                        map = getTeamForNextApprove(creditDocument.getCustomer().getMvnoId(), creditDocument.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, creditDocument);
                        workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, creditDocument, map);
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE: {
                    PartnerPayment partnerPayment = partnerPaymentRepository.findById(entityId.longValue()).orElse(null);
                    map = getTeamForNextApprove(partnerPayment.getPartner().getMvnoId(), partnerPayment.getPartner().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, partnerPaymentMapper.domainToDTO(partnerPayment, new CycleAvoidingMappingContext()));
                    workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, partnerPayment, map);
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_TERMINATION: {
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityId).orElse(null);
                    Customers customers = customersRepository.findById(customerServiceMapping.getCustId()).orElse(null);
                    map = getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_TERMINATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerServiceMapping);
                    workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, customerServiceMapping, map);
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CHANGE_DISCOUNT: {
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityId).orElse(null);
                    Customers customers = customersRepository.findById(customerServiceMapping.getCustId()).orElse(null);
                    map = getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CHANGE_DISCOUNT, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerServiceMapping);
                    workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, customerServiceMapping, map);
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION: {
                    CustomerAddress customerAddress = customerAddressRepository.findById(entityId).orElse(null);
                    map = getTeamForNextApprove(customerAddress.getCustomer().getMvnoId(), customerAddress.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerAddressMapper.domainToDTO(customerAddress, new CycleAvoidingMappingContext()));
                    workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, customerAddress, map);
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION: {
                    Customers customers = customersRepository.findById(entityId).orElse(null);
                    map = getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()));
                    if (map.containsKey("tat_id") && map.get("tat_id").equals("null") && !map.get("current_tat_id").equals("null")) {
                        map.put("tat_id", map.get("current_tat_id"));
                    }
                    if (map.containsKey("tat_id") && !map.get("tat_id").equals("null")) {
                        Optional<Matrix> matrixDetails = matrixRepository.findById(Long.valueOf((String) map.get("tat_id")));
                        if (matrixDetails.isPresent()) {
                            Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();
                            Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));
                            if (newMatrixDetails.isPresent()) {
                                customers = (Customers) tatUtils.UpdateDateTimefortat(newMatrixDetails.get(), customers, Nextvalue);
                                //details.setStaffId(details.getParentId());
                            }
                        }
                    }
                    workflowAssignStaffMappingService.assignWorkflowToStaff(customers.getId(), eventName, customers, map);
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION: {
                    CustPlanMappping custPlanMappping = new CustPlanMappping();
                    DebitDocument debitDocument = debitDocRepository.findById(entityId).orElse(null);
                    custPlanMappping = custPlanMappingRepository.findById(debitDocument.getCustpackrelid());
                    CustPlanMappping actualCustomerPlanMapping = custPlanMappingRepository.findById(custPlanMappping.getCustRefId());
                    Customers customers = customersRepository.findById(actualCustomerPlanMapping.getCustomer().getId()).orElse(null);
                    OrganizationBillDTO organizationBillDTO = new OrganizationBillDTO();
                    organizationBillDTO.setDebitDocument(debitDocument);
                    organizationBillDTO.setActualCustomers(customers);
                    map = getTeamForNextApprove(organizationBillDTO.getActualCustomers().getMvnoId(), organizationBillDTO.getActualCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, organizationBillDTO);
                    workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, debitDocument, map);
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING: {
                    CustSpecialPlanRelMappping custSpecialPlanRelMappping = custSpecialPlanRelMapppingRepository.findById(entityId.longValue()).orElse(null);
                    map = getTeamForNextApprove(custSpecialPlanRelMappping.getMvnoId(), custSpecialPlanRelMappping.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, custSpecialPlanRelMappping);
                    workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, custSpecialPlanRelMappping, map);
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.LEAD: {
                    LeadMaster leadMaster = leadMasterRepository.findById(entityId.longValue()).orElse(null);
                    map = getTeamForNextApprove(leadMaster.getMvnoId().intValue(), leadMaster.getBuId() == null ? null : leadMaster.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, leadMasterToLeadMgmtDTO(leadMaster));
                    leadMaster.setNextTeamMappingId((Integer) map.get("nextTeamHierarchyMappingId"));
                    leadMaster.setNextApproveStaffId(null);

                    if (map.containsKey("tat_id") && map.get("tat_id").equals("null") && !map.get("current_tat_id").equals("null")) {
                        map.put("tat_id", map.get("current_tat_id"));
                    }
                    if (map.containsKey("tat_id") && !map.get("tat_id").equals("null")) {
                        Optional<Matrix> matrixDetails = matrixRepository.findById(Long.valueOf((String) map.get("tat_id")));
                        if (matrixDetails.isPresent()) {
                            Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();
                            Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));
                            if (newMatrixDetails.isPresent()) {
                                leadMaster = (LeadMaster) tatUtils.UpdateDateTimefortat(newMatrixDetails.get(), leadMaster, Nextvalue);
                                //details.setStaffId(details.getParentId());
                            }
                        }
                    }


                    LeadMaster savedLeadMaster = leadMasterRepository.save(leadMaster);
//                    SendUpdatedLeadInfo sendUpdatedLeadInfo = new SendUpdatedLeadInfo(leadMasterToLeadMgmtDTO(leadMaster));
//                    messageSender.send(sendUpdatedLeadInfo, RabbitMqConstants.QUEUE_SEND_UPDATE_LEAD_INFO);
//                    leadMaster.setNextTeamMappingId((Integer) map.get("nextTeamHierarchyMappingId"));
//                    leadMaster.setNextApproveStaffId(null);
//                    LeadMaster savedLeadMaster = leadMasterRepository.save(leadMaster);
//                    SendApproverForLeadMsg sendApproverForLeadMsg = new SendApproverForLeadMsg(leadMasterToLeadMgmtDTO(savedLeadMaster));
                    workflowAssignStaffMappingService.assignWorkflowToStaff(leadMaster.getId().intValue(), eventName, savedLeadMaster, map);
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE: {
                    CreditDocument creditDocument = creditDocRepository.findById(entityId).orElse(null);
                    if(creditDocument.getStatus().equalsIgnoreCase("pending")) {
                       map = getTeamForNextApprove(creditDocument.getCustomer().getMvnoId(), creditDocument.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, creditDocument);
                       workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, creditDocument, map);
                   }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD: {
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityId).orElse(null);
                    Customers customers = customersRepository.findById(customerServiceMapping.getCustId()).orElse(null);
                    map = getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerServiceMapping);
                    workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, customerServiceMapping, map);
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.LEAD_QUOTATION: {
                    LeadQuotationDetails leadQuotationDetails = leadQuotationDetailsRepository.findByQuotationDetailId(entityId.longValue());
                    map = getTeamForNextApprove(leadQuotationDetails.getMvnoId().intValue(), leadQuotationDetails.getBuId() == null ? null : leadQuotationDetails.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD_QUOTATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, leadQuotationDetailsToLeadQuotationWfDTO(leadQuotationDetails));
                    leadQuotationDetails.setNextTeamMappingId((Integer) map.get("nextTeamHierarchyMappingId"));
                    leadQuotationDetails.setNextApproveStaffId(null);
                    LeadQuotationDetails savedLeadQuotationDetails = leadQuotationDetailsRepository.save(leadQuotationDetails);
                    SendLeadQuotationMessage sendLeadQuotationMessage = new SendLeadQuotationMessage(leadQuotationDetailsToLeadQuotationWfDTO(leadQuotationDetails));
                    kafkaMessageSender.send(new KafkaMessageData(sendLeadQuotationMessage, SendLeadQuotationMessage.class.getSimpleName()));
//                    messageSender.send(sendLeadQuotationMessage, RabbitMqConstants.QUEUE_LEAD_QUOTATION_ASSIGN_MESSAGE);
                    workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, savedLeadQuotationDetails, map);
                    break;
                }

            }
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            genericDataDTO.setResponseMessage("Successfully Assigned ");
        } catch (Exception e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }


    public GenericDataDTO reassignLead(Long leadMasterId) {
        LeadMaster leadMaster = leadMasterRepository.findById(leadMasterId).orElse(null);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<StaffUserPojo> staffUserList = new ArrayList<>();
        StaffUser staffUser = staffUserRepository.findById(leadMaster.getNextApproveStaffId()).orElse(null);
        StaffUser parentStaff = staffUser.getStaffUserparent();
        if (leadMaster.getNextApproveStaffId() != null) {
            if (getLoggedInUserId() == leadMaster.getNextApproveStaffId()) {
                if (leadMaster.getNextApproveStaffId() != null) {
                    staffUserList = getStaffFromCurrentTeammapping(leadMaster.getNextTeamMappingId(), leadMaster);
                    staffUserList = staffUserList.stream().filter(staffUserPojo -> !staffUserPojo.getId().equals(getLoggedInUser().getStaffId())).collect(Collectors.toList());
                    genericDataDTO.setDataList(staffUserList);
                }
            } else if (parentStaff != null) {
                if (staffUser.getStaffUserparent() != null && staffUserList.size() == 0) {
                    if (staffUser.getStaffUserparent().getId() == getLoggedInUserId()) {
//                        genericDataDTO.setData(CaseConstants.CHANGE_PROBLEM_DOMAIN);
                        if (leadMaster.getNextTeamMappingId() != null) {
                            staffUserList = getStaffFromCurrentTeammapping(leadMaster.getNextTeamMappingId(), leadMaster);
                            staffUserList = staffUserList.stream().filter(staffUserPojo -> !staffUserPojo.getId().equals(getLoggedInUser().getStaffId())).collect(Collectors.toList());
                            genericDataDTO.setDataList(staffUserList);
                        }
                        for (StaffUserPojo staffUserPojo : staffUserList) {
                            tatUtils.changeTatAssignee(leadMaster, staffUserMapper.dtoToDomain(staffUserPojo, new CycleAvoidingMappingContext()), false, true);
                        }

                    } else {
                        staffUser = staffUser.getStaffUserparent();
                        staffUserList = getStaffFromCurrentTeammapping(leadMaster.getNextTeamMappingId(), leadMaster);
                    }
                }
            }

        }
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        return genericDataDTO;

    }

    public void leadMgmtWfDTOToLeadMaster(LeadMgmtWfDTO leadMgmtWfDTO) {
        LeadMaster leadMaster = new LeadMaster();
        leadMaster.setStatus(leadMgmtWfDTO.getStatus());
        leadMaster.setLeadStatus(leadMgmtWfDTO.getStatus());
        leadMaster = leadMasterRepository.findById(leadMgmtWfDTO.getId()).orElse(null);
        if (leadMaster != null) {
            leadMaster.setNextTeamMappingId(leadMgmtWfDTO.getNextTeamMappingId());
            leadMaster.setNextApproveStaffId(leadMgmtWfDTO.getNextApproveStaffId());
            leadMasterRepository.save(leadMaster);
        }
    }

    public LeadMgmtWfDTO leadMasterToLeadMgmtDTO(LeadMaster leadMaster) {
        LeadMgmtWfDTO leadMgmtWfDTO = new LeadMgmtWfDTO();

        leadMgmtWfDTO.setId(leadMaster.getId());
        leadMgmtWfDTO.setBuId(leadMaster.getBuId());
        leadMgmtWfDTO.setMvnoId(leadMaster.getMvnoId());
        leadMgmtWfDTO.setServiceareaid(leadMaster.getServiceareaid());
        leadMgmtWfDTO.setNextTeamMappingId(leadMaster.getNextTeamMappingId());
        leadMgmtWfDTO.setNextApproveStaffId(leadMaster.getNextApproveStaffId());
        if (leadMaster.getNextfollowuptime() != null) {
            leadMgmtWfDTO.setNextfollowuptime(leadMaster.getNextfollowuptime().truncatedTo(ChronoUnit.SECONDS).toString());
        }
        if (leadMaster.getNextfollowupdate() != null) {
            leadMgmtWfDTO.setNextfollowupdate(leadMaster.getNextfollowupdate().toString());
        }

        return leadMgmtWfDTO;
    }


    public GenericDataDTO updateLeadAssignee(LeadChangeAssigneePojo leadChangeAssigneePojo) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        LeadMaster existingLeadMaster = new LeadMaster();
        existingLeadMaster = leadMasterRepository.findById(leadChangeAssigneePojo.getLeadMasterId()).orElse(null);
        StaffUser loggedInStaff = staffUserRepository.findById(getLoggedInUserId()).orElse(null);
        StaffUser currentAssineeStaff = staffUserRepository.findById(existingLeadMaster.getNextApproveStaffId()).orElse(null);
        StaffUser upcomingAssignee = staffUserRepository.findById(leadChangeAssigneePojo.getAssignee()).orElse(null);

        LeadMgmtWfDTO leadMgmtWfDTO = new LeadMgmtWfDTO();
        leadMgmtWfDTO.setId(leadChangeAssigneePojo.getLeadMasterId());
        TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails = null;
        List<TatMatrixWorkFlowDetails> result = tatMatrixWorkFlowDetailsRepo.findByStaffIdAndEntityIdAndEventNameAndCurrentTeamHeirarchyMappingId(
                currentAssineeStaff.getId(),
                existingLeadMaster.getId().intValue(),
                CommonConstants.WORKFLOW_EVENT_NAME.LEAD,
                existingLeadMaster.getNextTeamMappingId()
        );
        if (result != null && !result.isEmpty()) {
            tatMatrixWorkFlowDetails = result.get(0);
//            tatMatrixWorkFlowDetails=tatMatrixWorkFlowDetailsRepo.findByStaffIdAndEntityIdAndEventName(currentAssineeStaff.getId(),existingLeadMaster.getId().intValue(),CommonConstants.WORKFLOW_EVENT_NAME.LEAD);
        }
        if (tatMatrixWorkFlowDetails != null) {
            Optional<Matrix> matrixDetails = matrixRepository.findById(tatMatrixWorkFlowDetails.getTatMatrixId());
            Integer Nextvalue = Integer.parseInt(String.valueOf(matrixDetails.get().getMatrixDetailsList().get(0).getMtime()));
            existingLeadMaster = (LeadMaster) tatUtils.UpdateDateTimefortat(matrixDetails.get().getMatrixDetailsList().get(0), existingLeadMaster, Nextvalue);
        }

        leadMgmtWfDTO.setNextApproveStaffId(leadChangeAssigneePojo.getAssignee());

        //saving assignee
        existingLeadMaster.setNextApproveStaffId(leadChangeAssigneePojo.getAssignee());
        leadMasterRepository.save(existingLeadMaster);
        leadMgmtWfDTO.setCurrentLoggedInStaffId(getLoggedInUserId());
        leadMgmtWfDTO.setRemark(leadChangeAssigneePojo.getRemark());
        leadMgmtWfDTO.setRemarkType(leadChangeAssigneePojo.getRemarkType());
        leadMgmtWfDTO.setStatus(leadChangeAssigneePojo.getStatus());
        leadMgmtWfDTO.setOldValue(String.valueOf(existingLeadMaster.getNextApproveStaffId()));
        leadMgmtWfDTO.setNewValue(String.valueOf(leadChangeAssigneePojo.getAssignee()));
        leadMgmtWfDTO.setCreateDateString(existingLeadMaster.getCreateDateString());
        leadMgmtWfDTO.setCreatedBy(existingLeadMaster.getCreatedBy());
        leadMgmtWfDTO.setUpdateDateString(LocalDateTime.now().toString());
        leadMgmtWfDTO.setLastUpdatedBy(loggedInStaff.getUsername());
        leadMgmtWfDTO.setEntityType("LeadMaster");
        leadMgmtWfDTO.setIsForLeadAssign(true);
        leadMgmtWfDTO.setOperation(CommonConstants.LEAD_CHANGE_ASSIGNEE);


        if (existingLeadMaster.getNextTeamMappingId() != null) {
            tatUtils.changeTatAssignee(existingLeadMaster, loggedInStaff, false, true);
        }
        leadMasterRepository.save(existingLeadMaster);
        if (existingLeadMaster.getNextfollowupdate() != null) {
            leadMgmtWfDTO.setNextfollowupdate(existingLeadMaster.getNextfollowupdate().toString());
            leadMgmtWfDTO.setNextfollowuptime(existingLeadMaster.getNextfollowuptime().toString());
        }
        SendApproverForLeadMsg sendApproverForLeadMsg = new SendApproverForLeadMsg(leadMgmtWfDTO);
//        messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL);
        kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendApproverForLeadMsg.class.getSimpleName()));
        workflowAuditService.saveAudit(null, CommonConstants.EVENT_NAME.LEAD, existingLeadMaster.getId().intValue(), existingLeadMaster.getFirstname(), leadChangeAssigneePojo.getAssignee(), upcomingAssignee.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + upcomingAssignee.getUsername());

        return genericDataDTO;

    }

    public LeadMgmtWfDTO convertLeadReasonMgmtWfDTOToLeadMgmtWfDTO(LeadReasonMgmtWfDTO leadReasonMgmtWfDTO) {
        LeadMgmtWfDTO leadMgmtWfDTO = new LeadMgmtWfDTO();

        if (leadReasonMgmtWfDTO.getFlag().equalsIgnoreCase("Reject")) {
            LeadMaster leadMaster = new LeadMaster();
            leadMaster = leadMasterRepository.getOne(leadReasonMgmtWfDTO.getId());
            Long rejectedReasonMasterId = leadReasonMgmtWfDTO.getRejectedReasonMasterId();
            if (leadReasonMgmtWfDTO.getRejectedReasonMasterId() != null) {
//                leadMaster.setRejectReasonId(Math.toIntExact(leadReasonMgmtWfDTO.getRejectedReasonMasterId()));
                Integer rejectReasonId = Math.toIntExact(rejectedReasonMasterId);
                leadMaster.setRejectReasonId(rejectReasonId);
            }
            leadMaster.setRemarks(leadReasonMgmtWfDTO.getRemark());
            leadMasterRepository.save(leadMaster);
            leadMgmtWfDTO.setMvnoId(leadMaster.getMvnoId());
            leadMgmtWfDTO.setBuId(leadMaster.getBuId());
        }
        leadMgmtWfDTO.setId(leadReasonMgmtWfDTO.getId());
        leadMgmtWfDTO.setMvnoId(leadReasonMgmtWfDTO.getMvnoId());
        leadMgmtWfDTO.setUsername(leadReasonMgmtWfDTO.getUsername());
        leadMgmtWfDTO.setFirstname(leadReasonMgmtWfDTO.getFirstname());
        leadMgmtWfDTO.setStatus(leadReasonMgmtWfDTO.getStatus());
        leadMgmtWfDTO.setNextApproveStaffId(leadReasonMgmtWfDTO.getNextApproveStaffId());
        leadMgmtWfDTO.setNextTeamMappingId(leadReasonMgmtWfDTO.getNextTeamMappingId());
        leadMgmtWfDTO.setServiceareaid(leadReasonMgmtWfDTO.getServiceareaid());
        leadMgmtWfDTO.setFlag(leadReasonMgmtWfDTO.getFlag());
        leadMgmtWfDTO.setRemark(leadReasonMgmtWfDTO.getRemark());
        leadMgmtWfDTO.setCurrentLoggedInStaffId(leadReasonMgmtWfDTO.getCurrentLoggedInStaffId());
        leadMgmtWfDTO.setTeamName(leadReasonMgmtWfDTO.getTeamName());
        leadMgmtWfDTO.setFinalApproved(leadReasonMgmtWfDTO.isFinalApproved());
        leadMgmtWfDTO.setApproveRequest(leadReasonMgmtWfDTO.isApproveRequest());
        leadMgmtWfDTO.setRejectedReasonMasterId(leadReasonMgmtWfDTO.getRejectedReasonMasterId());

        return leadMgmtWfDTO;
    }

    public GenericDataDTO reassignWorkflowGetStaffList(Integer entityId, String eventName) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<StaffUserPojo> staffUserList = new ArrayList<>();
            long currentAssignee = 0;
            long nextTeamHirMappingId = 0;
            Map<String, Object> map = new HashMap<>();
            Object entity = null;
            CustomersPojo customersPojo = null;
            switch (eventName) {
                case CommonConstants.WORKFLOW_EVENT_NAME.CAF: {
                    customersPojo = customerMapper.domainToDTO(customersService.get(entityId,creditDocRepository.getMvnoIdByCreditDocId(entityId)), new CycleAvoidingMappingContext());
                    if (Objects.isNull(customersPojo.getCurrentAssigneeId())) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "No Sataff Found for assign ", null);
                    }
                    currentAssignee = customersPojo.getCurrentAssigneeId();
                    if (customersPojo.getNextTeamHierarchyMapping() != null) {
                        nextTeamHirMappingId = customersPojo.getNextTeamHierarchyMapping();
                    } else {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Approval is not started yet.So not able to find staff.Please send for approval first. ", null);
                    }
                    entity = customersPojo;
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN: {
                    PostpaidPlanPojo postpaidPlanPojo = postpaidPlanMapper.domainToDTO(postpaidPlanService.get(entityId,getMvnoIdFromCurrentStaff()), new CycleAvoidingMappingContext());
                    currentAssignee = postpaidPlanPojo.getNextStaff();
                    if (postpaidPlanPojo.getNextTeamHierarchyMapping() != null) {
                        nextTeamHirMappingId = postpaidPlanPojo.getNextTeamHierarchyMapping();
                    } else {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Approval is not started yet.So not able to find staff.Please send for approval first. ", null);
                    }
                    entity = postpaidPlanPojo;
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP: {
                    PlanGroup planGroupDTO = planGroupRepository.findById(entityId).get();
                    currentAssignee = planGroupDTO.getNextStaff();
                    if (planGroupDTO.getNextTeamHierarchyMappingId() != null) {
                        nextTeamHirMappingId = planGroupDTO.getNextTeamHierarchyMappingId();
                    } else {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Approval is not started yet.So not able to find staff.Please send for approval first. ", null);
                    }
                    entity = planGroupDTO;
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING: {
                    CustSpecialPlanRelMappping custSpecialPlanRelMappping = custSpecialPlanRelMapppingRepository.findById(entityId.longValue()).orElse(null);
                    currentAssignee = custSpecialPlanRelMappping.getNextStaff();
                    if (custSpecialPlanRelMappping.getNextTeamHierarchyMapping() != null) {
                        nextTeamHirMappingId = custSpecialPlanRelMappping.getNextTeamHierarchyMapping();
                    } else {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Approval is not started yet.So not able to find staff.Please send for approval first. ", null);
                    }
                    entity = custSpecialPlanRelMappping;
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION: {
                    CustomerAddressPojo customerAddressPojo = customerAddressMapper.domainToDTO(customerAddressService.get(entityId,customersPojo.getMvnoId()), new CycleAvoidingMappingContext());
                    currentAssignee = customerAddressPojo.getNextStaff();
                    if (customerAddressPojo.getNextTeamHierarchyMappingId() != null) {
                        nextTeamHirMappingId = customerAddressPojo.getNextTeamHierarchyMappingId();
                    } else {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Approval is not started yet.So not able to find staff.Please send for approval first. ", null);
                    }
                    entity = customerAddressPojo;
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_TERMINATION: {
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityId).orElse(null);
                    currentAssignee = customerServiceMapping.getNextStaff();
                    if (customerServiceMapping.getNextTeamHierarchyMappingId() != null) {
                        nextTeamHirMappingId = customerServiceMapping.getNextTeamHierarchyMappingId();
                    } else {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Approval is not started yet.So not able to find staff.Please send for approval first. ", null);
                    }
                    entity = customerServiceMapping;
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CHANGE_DISCOUNT: {
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityId).orElse(null);
                    currentAssignee = customerServiceMapping.getNextStaff();
                    if (customerServiceMapping.getNextTeamHierarchyMappingId() != null) {
                        nextTeamHirMappingId = customerServiceMapping.getNextTeamHierarchyMappingId();
                    } else {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Approval is not started yet.So not able to find staff.Please send for approval first. ", null);
                    }
                    entity = customerServiceMapping;
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE: {
                    PartnerPayment partnerPayment = partnerPaymentRepository.findById(entityId.longValue()).orElse(null);
                    PartnerPaymentDTO partnerPaymentDTO = partnerPaymentMapper.domainToDTO(partnerPayment, new CycleAvoidingMappingContext());
                    currentAssignee = partnerPaymentDTO.getNextStaff();
                    if (partnerPaymentDTO.getNextTeamHierarchyMappingId() != null) {
                        nextTeamHirMappingId = partnerPaymentDTO.getNextTeamHierarchyMappingId();
                    } else {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Approval is not started yet.So not able to find staff.Please send for approval first. ", null);
                    }
                    entity = partnerPaymentDTO;
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION: {
                    CustomerDocDetails customerDocDetails = customerDocDetailsRepository.findById(entityId.longValue()).orElse(null);
                    CustomerDocDetailsDTO customerDocDetailsDTO = customerDocDetailsMapper.domainToDTO(customerDocDetails, new CycleAvoidingMappingContext());
                    currentAssignee = customerDocDetailsDTO.getNextStaff();
                    if (customerDocDetailsDTO.getNextTeamHierarchyMappingId() != null) {
                        nextTeamHirMappingId = customerDocDetailsDTO.getNextTeamHierarchyMappingId();
                    } else {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Approval is not started yet.So not able to find staff.Please send for approval first. ", null);
                    }
                    entity = customerDocDetailsDTO;
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT:
                case CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE: {
                    CreditDocument creditDocument = creditDocService.get(entityId,creditDocRepository.getMvnoIdByCreditDocId(entityId));
                    currentAssignee = creditDocument.getApproverid();
                    if (creditDocument.getNextTeamHierarchyMappingId() != null) {
                        nextTeamHirMappingId = creditDocument.getNextTeamHierarchyMappingId();
                    } else {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Approval is not started yet.So not able to find staff.Please send for approval first. ", null);
                    }
                    entity = creditDocument;
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION: {
                    Integer mvnoID=customersRepository.getCustomerMvnoIdByCustId(entityId);
                    CustomersPojo customersPojo1 = customerMapper.domainToDTO(customersService.get(entityId,mvnoID), new CycleAvoidingMappingContext());
                    //customersPojo1.setCurrentAssigneeId();
                    if (customersPojo1.getNextTeamHierarchyMapping() != null && customersPojo1.getCurrentAssigneeId() != null) {
                        currentAssignee = customersPojo1.getCurrentAssigneeId();
                        nextTeamHirMappingId = customersPojo1.getNextTeamHierarchyMapping();
                    } else {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Approval is not started yet.So not able to find staff.Please send for approval first. ", null);
                    }
                    customersPojo=customersPojo1;
                    entity = customersPojo1;
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION: {
                    DebitDocument debitDocument = debitDocRepository.findById(entityId).orElse(null);
                    if (debitDocument != null) {
                        currentAssignee = debitDocument.getNextStaff();
                        if (debitDocument.getNextTeamHierarchyMappingId() != null) {
                            nextTeamHirMappingId = debitDocument.getNextTeamHierarchyMappingId();
                        } else {
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Approval is not started yet.So not able to find staff.Please send for approval first. ", null);
                        }
                        entity = debitDocument;
                    }
                    break;
                }
            }

            if (currentAssignee != 0) {
                StaffUser staffUser = staffUserService.get((int) currentAssignee,customersPojo.getMvnoId());
                if (getLoggedInUserId() == currentAssignee) {
                    if (nextTeamHirMappingId != 0) {
                        staffUserList = getStaffFromCurrentTeammapping((int) nextTeamHirMappingId, entity);
                        staffUserList = staffUserList.stream().filter(staffUserPojo -> !staffUserPojo.getId().equals(getLoggedInUser().getStaffId())).collect(Collectors.toList());
                        genericDataDTO.setDataList(staffUserList);
                    }
                } else if (staffUser.getStaffUserparent() != null) {
                    if (staffUser.getStaffUserparent() != null && staffUserList.size() == 0) {
                        if (staffUser.getStaffUserparent().getId() == getLoggedInUserId()) {
//                            genericDataDTO.setData(CaseConstants.CHANGE_PROBLEM_DOMAIN);
                            if (nextTeamHirMappingId != 0) {
                                staffUserList = getStaffFromCurrentTeammapping((int) nextTeamHirMappingId, entity);
                                genericDataDTO.setDataList(staffUserList);
                            }
                        } else {
                            staffUser = staffUser.getStaffUserparent();
                            staffUserList = getStaffFromCurrentTeammapping((int) nextTeamHirMappingId, entity);
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }

    public GenericDataDTO reassignWorkflow(Integer entityId, String eventName, Integer assignToStaffId, String remark) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (remark == null && remark.isEmpty()) {
            remark = "";
        }
        try {
            Customers customers = customersRepository.findById(entityId).orElse(null);
            StaffUser assignToStaff = staffUserService.get(assignToStaffId,customers.getMvnoId());
            String entityName = null;
            switch (eventName) {
                case CommonConstants.WORKFLOW_EVENT_NAME.CAF: {
                    if (customers != null) {
                        TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails = null;
                        List<TatMatrixWorkFlowDetails> result = tatMatrixWorkFlowDetailsRepo.findByStaffIdAndEntityIdAndEventNameAndCurrentTeamHeirarchyMappingId(
                                customers.getCurrentAssigneeId(),
                                customers.getId().intValue(),
                                CommonConstants.WORKFLOW_EVENT_NAME.CAF,
                                customers.getNextTeamHierarchyMapping()
                        );
                        if (result != null && !result.isEmpty()) {
                            tatMatrixWorkFlowDetails = result.get(0);
//                            tatMatrixWorkFlowDetails=tatMatrixWorkFlowDetailsRepo.findByStaffIdAndEntityIdAndEventName(customers.getCurrentAssigneeId(), customers.getId().intValue(),CommonConstants.WORKFLOW_EVENT_NAME.CAF);
                        }

                        if (tatMatrixWorkFlowDetails != null) {
                            Optional<Matrix> matrixDetails = matrixRepository.findById(tatMatrixWorkFlowDetails.getTatMatrixId());
                            Integer Nextvalue = Integer.parseInt(String.valueOf(matrixDetails.get().getMatrixDetailsList().get(0).getMtime()));
                            customers = (Customers) tatUtils.UpdateDateTimefortat(matrixDetails.get().getMatrixDetailsList().get(0), customers, Nextvalue);
                        }
                        customers.setCurrentAssigneeId(assignToStaffId);
                        entityName = customers.getUsername();
                        customersRepository.save(customers);
                        if (customers.getNextTeamHierarchyMapping() != null) {
                            tatUtils.changeTatAssignee(customers, assignToStaff, false, true);
                        }
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER + " with username : " + " ' " + customers.getUsername() + " '";
                        sendWorkflowAssignActionMessage(assignToStaff.getCountryCode(), assignToStaff.getPhone(), assignToStaff.getEmail(), customers.getMvnoId(), assignToStaff.getFullName(), action,assignToStaff.getId().longValue());
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION: {
                    // Customers customers = customersRepository.findById(entityId).orElse(null);
                    if (customers != null) {
//                        CustomerApprove customerApprove = customerApproveRepo.findByCustomerID(customers.getId());
                        CustomerApprove customerApprove = customerApproveRepo.findByCustomerIDAndStatus(customers.getId(), "pending");
                        if (assignToStaff.getStaffUserparent() != null) {
                            customerApprove.setParentStaff(assignToStaff.getStaffUserparent().getUsername());
                        } else {
                            customerApprove.setParentStaff(null);
                        }
                        TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails = null;

                        List<TatMatrixWorkFlowDetails> result = tatMatrixWorkFlowDetailsRepo.findByStaffIdAndEntityIdAndEventNameAndCurrentTeamHeirarchyMappingId(
                                customers.getCurrentAssigneeId(),
                                customers.getId().intValue(),
                                CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION,
                                customers.getNextTeamHierarchyMapping()
                        );
                        if (result != null && !result.isEmpty()) {
                            tatMatrixWorkFlowDetails = result.get(0);
//                            tatMatrixWorkFlowDetails=tatMatrixWorkFlowDetailsRepo.findByStaffIdAndEntityIdAndEventName(customers.getCurrentAssigneeId(), customers.getId().intValue(),CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION);
                        }
                        if (tatMatrixWorkFlowDetails != null) {
                            Optional<Matrix> matrixDetails = matrixRepository.findById(tatMatrixWorkFlowDetails.getTatMatrixId());
                            Integer Nextvalue = Integer.parseInt(String.valueOf(matrixDetails.get().getMatrixDetailsList().get(0).getMtime()));
                            customers = (Customers) tatUtils.UpdateDateTimefortat(matrixDetails.get().getMatrixDetailsList().get(0), customers, Nextvalue);
                        }
                        customers.setCurrentAssigneeId(assignToStaffId);
                        customersRepository.save(customers);
                        entityName = customers.getUsername();
                        customers.setCurrentAssigneeId(assignToStaffId);
                        customersRepository.save(customers);
                        customerApprove.setCurrentStaff(assignToStaff.getUsername());
                        customerApproveRepo.save(customerApprove);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER + " with username : " + " ' " + customers.getUsername() + " '";
                        sendWorkflowAssignActionMessage(assignToStaff.getCountryCode(), assignToStaff.getPhone(), assignToStaff.getEmail(), customers.getMvnoId(), assignToStaff.getFullName(), action,assignToStaff.getId().longValue());
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN: {
                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(entityId).orElse(null);
                    if (postpaidPlan != null) {
                        postpaidPlan.setNextStaff(assignToStaffId);
                        entityName = postpaidPlan.getName();
                        postpaidPlanRepo.save(postpaidPlan);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.PLAN + " with username : " + " ' " + postpaidPlan.getName() + " '";
                        sendWorkflowAssignActionMessage(assignToStaff.getCountryCode(), assignToStaff.getPhone(), assignToStaff.getEmail(), assignToStaff.getMvnoId(), assignToStaff.getFullName(), action,assignToStaff.getId().longValue());
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP: {
                    PlanGroup planGroup = planGroupRepository.findById(entityId).orElse(null);
                    if (planGroup != null) {
                        planGroup.setNextStaff(assignToStaffId);
                        entityName = planGroup.getPlanGroupName();
                        planGroupRepository.save(planGroup);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.PLAN_GROUP + " with username : " + " ' " + planGroup.getPlanGroupName() + " '";
                        sendWorkflowAssignActionMessage(assignToStaff.getCountryCode(), assignToStaff.getPhone(), assignToStaff.getEmail(), assignToStaff.getMvnoId(), assignToStaff.getFullName(), action,assignToStaff.getId().longValue());
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING: {
                    CustSpecialPlanRelMappping custSpecialPlanRelMappping = custSpecialPlanRelMapppingRepository.findById(entityId.longValue()).orElse(null);
                    if (custSpecialPlanRelMappping != null) {
                        custSpecialPlanRelMappping.setNextStaff(assignToStaffId);
                        entityName = custSpecialPlanRelMappping.getName();
                        custSpecialPlanRelMapppingRepository.save(custSpecialPlanRelMappping);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.SPECIAL_PLAN_MAPPING + " with username : " + " ' " + custSpecialPlanRelMappping.getMappingName() + " '";
                        sendWorkflowAssignActionMessage(assignToStaff.getCountryCode(), assignToStaff.getPhone(), assignToStaff.getEmail(), assignToStaff.getMvnoId(), assignToStaff.getFullName(), action,assignToStaff.getId().longValue());
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION: {
                    CustomerAddress customerAddress = customerAddressRepository.findById(entityId).orElse(null);
                    if (customerAddress != null) {
                        customerAddress.setNextStaff(assignToStaffId);
                        customerAddressRepository.save(customerAddress);
                        entityName = customerAddress.getCustomer().getFirstname();
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.SHIFT_LOCATION + " with username : " + " ' " + customerAddress.getCustomer().getUsername() + " '";
                        sendWorkflowAssignActionMessage(assignToStaff.getCountryCode(), assignToStaff.getPhone(), assignToStaff.getEmail(), assignToStaff.getMvnoId(), assignToStaff.getFullName(), action,assignToStaff.getId().longValue());
                        workflowAuditService.saveAudit(null, eventName, customerAddress.getCustomer().getId(), customerAddress.getCustomer().getUsername(), assignToStaff.getId(), assignToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignToStaff.getUsername() + "/n " + remark);
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_TERMINATION: {
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityId).orElse(null);
                    if (customerServiceMapping != null) {
                        customerServiceMapping.setNextStaff(assignToStaffId);
                        customerServiceMappingRepository.save(customerServiceMapping);
                        entityName = customerServiceMapping.getServiceName();
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER_SERVICE_TERMINATION + " with username : " + " ' " + customerServiceMapping.getCreatedByName() + " '";
                        sendWorkflowAssignActionMessage(assignToStaff.getCountryCode(), assignToStaff.getPhone(), assignToStaff.getEmail(), assignToStaff.getMvnoId(), assignToStaff.getFullName(), action,assignToStaff.getId().longValue());
                    }
                    break;

                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CHANGE_DISCOUNT: {
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityId).orElse(null);
                    if (customerServiceMapping != null) {
                        customerServiceMapping.setNextStaff(assignToStaffId);
                        customerServiceMappingRepository.save(customerServiceMapping);
                        entityName = customerServiceMapping.getCustomerName();
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.CHANGE_DISCOUNT + " with username : " + " ' " + customerServiceMapping.getCreatedByName() + " '";
                        sendWorkflowAssignActionMessage(assignToStaff.getCountryCode(), assignToStaff.getPhone(), assignToStaff.getEmail(), assignToStaff.getMvnoId(), assignToStaff.getFullName(), action,assignToStaff.getId().longValue());
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE: {
                    PartnerPayment partnerPayment = partnerPaymentRepository.findById(entityId.longValue()).orElse(null);
                    if (partnerPayment != null) {
                        partnerPayment.setNextStaff(assignToStaffId);
                        partnerPaymentRepository.save(partnerPayment);
                        entityName = partnerPayment.getPartnerName();
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.PARTNER_BALANCE + " with username : " + " ' " + partnerPayment.getPartnerName() + " '";
                        sendWorkflowAssignActionMessage(assignToStaff.getCountryCode(), assignToStaff.getPhone(), assignToStaff.getEmail(), assignToStaff.getMvnoId(), assignToStaff.getFullName(), action,assignToStaff.getId().longValue());
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION: {
                    CustomerDocDetails customerDocDetails = customerDocDetailsRepository.findById(entityId.longValue()).orElse(null);
                    if (customerDocDetails != null) {
                        customerDocDetails.setNextStaff(assignToStaffId);
                        entityName = customerDocDetails.getCustomer().getUsername();
                        customerDocDetailsRepository.save(customerDocDetails);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER_DOCUMENT + " with username : " + " ' " + customerDocDetails.getCreatedByName() + " '";
                        sendWorkflowAssignActionMessage(assignToStaff.getCountryCode(), assignToStaff.getPhone(), assignToStaff.getEmail(), assignToStaff.getMvnoId(), assignToStaff.getFullName(), action,assignToStaff.getId().longValue());
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE:
                case CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT: {
                    CreditDocument creditDocument = creditDocRepository.findById(entityId).orElse(null);
                    if (creditDocument != null) {
                        creditDocument.setApproverid(assignToStaffId);
                        creditDocRepository.save(creditDocument);
                        entityName = creditDocument.getCustomer().getUsername();
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.PAYMENT + " with username : " + " ' " + creditDocument.getCreatedByName() + " '";
                        sendWorkflowAssignActionMessage(assignToStaff.getCountryCode(), assignToStaff.getPhone(), assignToStaff.getEmail(), assignToStaff.getMvnoId(), assignToStaff.getFullName(), action,assignToStaff.getId().longValue());
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION: {
                    DebitDocument debitDocument = debitDocRepository.findById(entityId).orElse(null);
                    if (debitDocument != null) {
                        debitDocument.setNextStaff(assignToStaffId);
                        debitDocRepository.save(debitDocument);
                        entityName = debitDocument.getCustomer().getUsername();
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.BILL_TO_ORGANIZATION + " with username : " + " ' " + debitDocument.getCustRefName() + " '";
                        sendWorkflowAssignActionMessage(assignToStaff.getCountryCode(), assignToStaff.getPhone(), assignToStaff.getEmail(), assignToStaff.getMvnoId(), assignToStaff.getFullName(), action,assignToStaff.getId().longValue());
                    }
                    break;
                }

                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD: {
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityId).orElse(null);
                    if (customerServiceMapping != null) {
                        customerServiceMapping.setNextStaff(assignToStaffId);
                        entityName = customerServiceMapping.getCustomerName();
                        customerServiceMappingRepository.save(customerServiceMapping);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER_SERVICE_ADD + " with username : " + " ' " + customerServiceMapping.getCreatedByName() + " '";
                        sendWorkflowAssignActionMessage(assignToStaff.getCountryCode(), assignToStaff.getPhone(), assignToStaff.getEmail(), assignToStaff.getMvnoId(), assignToStaff.getFullName(), action,assignToStaff.getId().longValue());
                    }
                    break;
                }

            }
            if (customers != null) {
                workflowAuditService.saveAudit(null, eventName, entityId, entityName, assignToStaff.getId(), assignToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignToStaff.getUsername() + "/n " + remark);
            }
        } catch (Exception e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }

    @Transactional
    public void leadQuotationWorkflowRequest(LeadQuotationWfDTO leadQuotationWfDTO) {
        StaffUser loggedInUser = staffUserService.get(leadQuotationWfDTO.getCurrentLoggedInStaffId(),leadQuotationWfDTO.getMvnoId().intValue());
        LeadQuotationWfDTO sendUpdateDto = new LeadQuotationWfDTO();
        sendUpdateDto.setStatus(leadQuotationWfDTO.getStatus());
        sendUpdateDto.setMvnoId(leadQuotationWfDTO.getMvnoId());
        sendUpdateDto.setBuId(leadQuotationWfDTO.getBuId());
        LeadQuotationDetails leadQuotationDetails = leadQuotationDetailsRepository.save(new LeadQuotationDetails(leadQuotationWfDTO));

        if (leadQuotationWfDTO.getStatus().equals(CommonConstants.QUOTATION_STATUS_NEW_ACTIVATION)) {
            if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
                Map<String, String> map = getTeamForNextApproveForAuto(Math.toIntExact(leadQuotationWfDTO.getMvnoId()), leadQuotationWfDTO.getBuId() == null ? null : leadQuotationWfDTO.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD_QUOTATION, CommonConstants.HIERARCHY_TYPE, false, true, leadQuotationWfDTO);
                if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Integer.valueOf(map.get("nextTatMappingId"))).orElse(null);
                    Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
                    StaffUser assignedStaffUser = staffUserService.get(Integer.valueOf(map.get("staffId")),leadQuotationWfDTO.getMvnoId().intValue());
                    CustomerCafAssignmentService customerCafAssignmentService = SpringContext.getBean(CustomerCafAssignmentService.class);

                    sendUpdateDto.setNextTeamMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                    sendUpdateDto.setNextApproveStaffId(Integer.valueOf(map.get("staffId")));
                    sendUpdateDto.setQuotationId(leadQuotationWfDTO.getQuotationId());
                    sendUpdateDto.setCurrentLoggedInStaffId(leadQuotationWfDTO.getCurrentLoggedInStaffId());
                    sendUpdateDto.setTeamName(teams.getName());
                    sendUpdateDto.setFlag("Assigned");
                    sendUpdateDto.setFinalApproved(false);
                    leadQuotationDetails.setFinalApproved(false);
                    leadQuotationDetails.setNextApproveStaffId(Integer.valueOf(map.get("staffId")));
                    leadQuotationDetails.setNextTeamMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                    String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD_QUOTATION + " for Lead Name : " + " ' " + leadQuotationWfDTO.getFirstName() + " '";
                    workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD_QUOTATION, leadQuotationWfDTO.getQuotationId().intValue(), leadQuotationWfDTO.getFirstName(), assignedStaffUser.getId(), assignedStaffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedStaffUser.getUsername());
                    sendWorkflowAssignActionMessage(assignedStaffUser.getCountryCode(), assignedStaffUser.getPhone(), assignedStaffUser.getEmail(), assignedStaffUser.getMvnoId(), assignedStaffUser.getFullName(), action,assignedStaffUser.getId().longValue());

                } else {
                    sendUpdateDto.setNextApproveStaffId(loggedInUser.getId());
                    sendUpdateDto.setNextTeamMappingId(null);
                    sendUpdateDto.setQuotationId(leadQuotationWfDTO.getQuotationId());
                    sendUpdateDto.setCurrentLoggedInStaffId(leadQuotationWfDTO.getCurrentLoggedInStaffId());
                    sendUpdateDto.setTeamName("-");
                    sendUpdateDto.setFlag("Assigned");
                    leadQuotationDetails.setNextApproveStaffId(loggedInUser.getId());
                    leadQuotationDetails.setNextTeamMappingId(null);
                    String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD_QUOTATION + " for Lead Name : " + " ' " + leadQuotationWfDTO.getFirstName() + " '";
                    workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD_QUOTATION, leadQuotationWfDTO.getQuotationId().intValue(), leadQuotationWfDTO.getFirstName(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + loggedInUser.getUsername());
                    sendWorkflowAssignActionMessage(loggedInUser.getCountryCode(), loggedInUser.getPhone(), loggedInUser.getEmail(), loggedInUser.getMvnoId(), loggedInUser.getFullName(), action,loggedInUser.getId().longValue());
                }
            } else {
                sendUpdateDto.setNextApproveStaffId(loggedInUser.getId());
                sendUpdateDto.setNextTeamMappingId(null);
                sendUpdateDto.setQuotationId(leadQuotationWfDTO.getQuotationId());
                sendUpdateDto.setCurrentLoggedInStaffId(leadQuotationWfDTO.getCurrentLoggedInStaffId());
                sendUpdateDto.setTeamName("-");
                sendUpdateDto.setFlag("Assigned");
                sendUpdateDto.setFinalApproved(false);
                leadQuotationDetails.setNextApproveStaffId(loggedInUser.getId());
                leadQuotationDetails.setNextTeamMappingId(null);
                leadQuotationDetails.setFinalApproved(false);
                String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD_QUOTATION + " for Lead Name : " + " ' " + leadQuotationWfDTO.getFirstName() + " '";
                workflowAuditService.saveAudit(null, CommonConstants.EVENT_NAME.LEAD_QUOTATION, leadQuotationWfDTO.getQuotationId().intValue(), leadQuotationWfDTO.getFirstName(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + loggedInUser.getUsername());
                sendWorkflowAssignActionMessage(loggedInUser.getCountryCode(), loggedInUser.getPhone(), loggedInUser.getEmail(), loggedInUser.getMvnoId(), loggedInUser.getFullName(), action,loggedInUser.getId().longValue());
            }
            leadQuotationDetailsRepository.save(leadQuotationDetails);
            SendLeadQuotationMessage sendLeadQuotationMessage = new SendLeadQuotationMessage(sendUpdateDto);
//            messageSender.send(sendLeadQuotationMessage, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL_QUOTATION);
            kafkaMessageSender.send(new KafkaMessageData(sendLeadQuotationMessage, SendLeadQuotationMessage.class.getSimpleName()));
        }
    }

    public GenericDataDTO approveLeadQuotation(LeadQuotationWfDTO leadQuotationWfDTO, GenericDataDTO genericDataDTO,Integer mvnoId) {
        LeadQuotationWfDTO sendUpdateDto = new LeadQuotationWfDTO();
        List<StaffUserPojo> staffUserPojos = new ArrayList<>();
        LeadQuotationDetails leadQuotationDetails = leadQuotationDetailsRepository.findByQuotationDetailId(leadQuotationWfDTO.getQuotationId());
        StaffUser loggedInUser = staffUserService.get(getLoggedInUserId(),mvnoId);
        if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
            StaffUser assignedUser = null;
            Map<String, String> map = getTeamForNextApproveForAuto(Math.toIntExact(leadQuotationDetails.getMvnoId()), leadQuotationDetails.getBuId() == null ? null : leadQuotationDetails.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD_QUOTATION, CommonConstants.HIERARCHY_TYPE, leadQuotationWfDTO.getApproveRequest(), false, leadQuotationWfDTO);
            if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Integer.valueOf(map.get("nextTatMappingId"))).orElse(null);
                Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
                StaffUser assignedStaffUser = staffUserService.get(Integer.valueOf(map.get("staffId")),mvnoId);
                assignedUser = assignedStaffUser;
                leadQuotationDetails.setNextApproveStaffId(Integer.valueOf(map.get("staffId")));
                leadQuotationDetails.setNextTeamMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                sendUpdateDto.setNextTeamMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                sendUpdateDto.setNextApproveStaffId(Integer.valueOf(map.get("staffId")));
                sendUpdateDto.setQuotationId(leadQuotationWfDTO.getQuotationId());
                sendUpdateDto.setCurrentLoggedInStaffId(leadQuotationWfDTO.getCurrentLoggedInStaffId());
                sendUpdateDto.setTeamName(teams.getName());
                sendUpdateDto.setFlag("Assigned");
                sendUpdateDto.setRemark(leadQuotationWfDTO.getRemark());
                sendUpdateDto.setRejectedReasonMasterId(leadQuotationWfDTO.getRejectedReasonMasterId());
                leadQuotationDetailsRepository.save(leadQuotationDetails);
                SendLeadQuotationMessage sendLeadQuotationMessage = new SendLeadQuotationMessage(sendUpdateDto);
                kafkaMessageSender.send(new KafkaMessageData(sendLeadQuotationMessage, SendLeadQuotationMessage.class.getSimpleName()));
//                messageSender.send(sendLeadQuotationMessage, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL_QUOTATION);
                String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD_QUOTATION + " for Lead Name : " + " ' " + leadQuotationDetails.getFirstName() + " '";
                workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD_QUOTATION, leadQuotationDetails.getQuotationDetailId().intValue(), leadQuotationDetails.getFirstName(), loggedInUser.getId(), loggedInUser.getUsername(), leadQuotationWfDTO.getApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), leadQuotationWfDTO.getApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " with remarks : " + leadQuotationWfDTO.getRemark() + " by :- " + loggedInUser.getUsername());
                workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD_QUOTATION, leadQuotationDetails.getQuotationDetailId().intValue(), leadQuotationDetails.getFirstName(), assignedUser.getId(), assignedUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedUser.getUsername());
                sendWorkflowAssignActionMessage(assignedUser.getCountryCode(), assignedStaffUser.getPhone(), assignedStaffUser.getEmail(), assignedUser.getMvnoId(), assignedUser.getFullName(), action,assignedUser.getId().longValue());
                //updating tatmapping id in apigw lead
                leadQuotationWfDTOToLeadQuotationDetail(sendUpdateDto);
            } else {
                sendUpdateDto.setFinalApproved(true);
                genericDataDTO.setData("FINAL_APPROVED");
                sendUpdateDto.setQuotationId(leadQuotationWfDTO.getQuotationId());
                sendUpdateDto.setCurrentLoggedInStaffId(leadQuotationWfDTO.getCurrentLoggedInStaffId());
                sendUpdateDto.setNextApproveStaffId(leadQuotationWfDTO.getCurrentLoggedInStaffId());
                sendUpdateDto.setFlag("Approved");
//                leadMaster.setNextApproveStaffId(null);
//                leadMaster.setNextTeamMappingId(null);
                sendUpdateDto.setRemark(leadQuotationWfDTO.getRemark());
                sendUpdateDto.setRejectedReasonMasterId(leadQuotationWfDTO.getRejectedReasonMasterId());
                SendLeadQuotationMessage sendLeadQuotationMessage = new SendLeadQuotationMessage(sendUpdateDto);
                leadQuotationDetailsRepository.save(leadQuotationDetails);
                workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD_QUOTATION, leadQuotationDetails.getQuotationDetailId().intValue(), leadQuotationDetails.getFirstName(), loggedInUser.getId(), loggedInUser.getUsername(), leadQuotationWfDTO.getApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), leadQuotationWfDTO.getApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED + " with remarks : " + leadQuotationWfDTO.getRemark() + " by :- " + loggedInUser.getUsername() : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " with remarks : " + leadQuotationWfDTO.getRemark() + " by :- " + loggedInUser.getUsername());
                kafkaMessageSender.send(new KafkaMessageData(sendLeadQuotationMessage, SendLeadQuotationMessage.class.getSimpleName()));
//                messageSender.send(sendLeadQuotationMessage, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL_QUOTATION);
                //updating tatmapping id in apigw lead
                leadQuotationWfDTOToLeadQuotationDetail(sendUpdateDto);
            }
        } else {
            Map<String, Object> map = getTeamForNextApprove(Math.toIntExact(leadQuotationDetails.getMvnoId()), leadQuotationDetails.getBuId() == null ? null : leadQuotationDetails.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD_QUOTATION, CommonConstants.HIERARCHY_TYPE, leadQuotationWfDTO.getApproveRequest(), false, leadQuotationWfDTO);
            if (map.containsKey("assignableStaff")) {
                staffUserPojos = (List<StaffUserPojo>) map.get("assignableStaff");
                genericDataDTO.setDataList(staffUserPojos);
                leadQuotationDetailsRepository.save(leadQuotationDetails);
                //workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD_QUOTATION, leadQuotationDetails.getQuotationDetailId().intValue(), leadQuotationDetails.getFirstName(), loggedInUser.getId(), loggedInUser.getUsername(), leadQuotationWfDTO.getApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), leadQuotationWfDTO.getApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED + " with remarks : " + leadQuotationWfDTO.getRemark() + " by :- " + loggedInUser.getUsername() : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + "with remarks : " + leadQuotationWfDTO.getRemark() + " by :- " + loggedInUser.getUsername());
                return genericDataDTO;
            } else {
                sendUpdateDto.setFinalApproved(true);
                if (leadQuotationWfDTO.getFlag().equalsIgnoreCase("Reject")) {
                    genericDataDTO.setData("FINAL_REJECTED");
                    sendUpdateDto.setFlag("Rejected");
                    sendUpdateDto.setStatus("Rejected");
                } else {
                    genericDataDTO.setData("FINAL_APPROVED");
                    sendUpdateDto.setFlag("Approved");
                }
                sendUpdateDto.setQuotationId(leadQuotationDetails.getQuotationDetailId());
//                sendUpdateDto.setCurrentLoggedInStaffId(leadMgmtWfDTO.getCurrentLoggedInStaffId());
//                sendUpdateDto.setNextApproveStaffId(leadMgmtWfDTO.getCurrentLoggedInStaffId());
//                leadMaster.setNextApproveStaffId(null);
//                leadMaster.setNextTeamMappingId(null);
//                sendUpdateDto.setFlag("Approved");
                sendUpdateDto.setRemark(leadQuotationWfDTO.getRemark());
                sendUpdateDto.setRejectedReasonMasterId(leadQuotationWfDTO.getRejectedReasonMasterId());
                leadQuotationDetailsRepository.save(leadQuotationDetails);
                SendLeadQuotationMessage sendLeadQuotationMessage = new SendLeadQuotationMessage(sendUpdateDto);
                workflowAuditService.saveAudit(null, CommonConstants.EVENT_NAME.LEAD_QUOTATION, leadQuotationDetails.getQuotationDetailId().intValue(), leadQuotationDetails.getFirstName(), loggedInUser.getId(), loggedInUser.getUsername(), leadQuotationWfDTO.getApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), leadQuotationWfDTO.getApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED + " with remarks : " + leadQuotationWfDTO.getRemark() + " by :- " + loggedInUser.getUsername() : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + "with remarks : " + leadQuotationWfDTO.getRemark() + " by :- " + loggedInUser.getUsername());
//                messageSender.send(sendLeadQuotationMessage, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL_QUOTATION);
                kafkaMessageSender.send(new KafkaMessageData(sendLeadQuotationMessage, SendLeadQuotationMessage.class.getSimpleName()));

                leadQuotationWfDTOToLeadQuotationDetail(sendUpdateDto);
            }

        }


        return genericDataDTO;
    }

    public void leadQuotationWfDTOToLeadQuotationDetail(LeadQuotationWfDTO leadQuotationWfDTO) {
        LeadQuotationDetails leadQuotationDetails = new LeadQuotationDetails();
        leadQuotationDetails.setStatus(leadQuotationWfDTO.getStatus());
        leadQuotationDetails = leadQuotationDetailsRepository.findByQuotationDetailId(leadQuotationWfDTO.getQuotationId());
        leadQuotationDetails.setNextTeamMappingId(leadQuotationWfDTO.getNextTeamMappingId());
        leadQuotationDetails.setNextApproveStaffId(leadQuotationWfDTO.getNextApproveStaffId());
        leadQuotationDetailsRepository.save(leadQuotationDetails);

    }

    public LeadQuotationWfDTO leadQuotationDetailsToLeadQuotationWfDTO(LeadQuotationDetails leadQuotationDetails) {
        LeadQuotationWfDTO leadQuotationWfDTO = new LeadQuotationWfDTO();

        leadQuotationWfDTO.setQuotationId(leadQuotationDetails.getQuotationDetailId());
        leadQuotationWfDTO.setBuId(leadQuotationDetails.getBuId());
        leadQuotationWfDTO.setMvnoId(leadQuotationDetails.getMvnoId());
        leadQuotationWfDTO.setNextTeamMappingId(leadQuotationDetails.getNextTeamMappingId());
        leadQuotationWfDTO.setNextApproveStaffId(leadQuotationDetails.getNextApproveStaffId());

        return leadQuotationWfDTO;
    }

    public LeadQuotationWfDTO assignFromStaffListForLeadQuotation(Integer nextAssignStaff, String eventName, LeadQuotationWfDTO
            leadQuotationWfDTO,Integer mvnoId) {
        StaffUser assignedUser = null;
//        StaffUser loggedInUser = staffUserService.get(getLoggedInUserId());
        StaffUser assignedToStaff = staffUserService.get(nextAssignStaff,mvnoId);
        LeadQuotationWfDTO sendUpdateDto = new LeadQuotationWfDTO();
        assignedUser = assignedToStaff;
        LeadQuotationDetails leadQuotationDetails = leadQuotationDetailsRepository.findByQuotationDetailId(leadQuotationWfDTO.getQuotationId());
        Map<String, String> map = getTeamForNextApproveForAuto(Math.toIntExact(leadQuotationDetails.getMvnoId()), leadQuotationDetails.getBuId() == null ? null : leadQuotationDetails.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.LEAD_QUOTATION, CommonConstants.HIERARCHY_TYPE, leadQuotationWfDTO.getApproveRequest(), false, leadQuotationWfDTO);
        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
            TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Integer.valueOf(map.get("nextTatMappingId"))).orElse(null);
            Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
            String action = CommonConstants.WORKFLOW_MSG_ACTION.LEAD_QUOTATION + " for Lead Name : " + " ' " + leadQuotationDetails.getFirstName() + " '";
            //workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD_QUOTATION, leadQuotationDetails.getQuotationDetailId().intValue(), leadQuotationDetails.getFirstName(), loggedInUser.getId(), loggedInUser.getUsername(), leadQuotationWfDTO.getApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), leadQuotationWfDTO.getApproveRequest() ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED + " with remarks : " + leadQuotationWfDTO.getRemark() + " by :- " + loggedInUser.getUsername() : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + "with remarks : " + leadQuotationWfDTO.getRemark() + " by :- " + loggedInUser.getUsername());
            //workflowAuditService.saveAudit(Integer.parseInt(map.get("eventId")), CommonConstants.EVENT_NAME.LEAD_QUOTATION, leadQuotationDetails.getQuotationDetailId().intValue(), leadQuotationDetails.getFirstName(), assignedUser.getId(), assignedUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + assignedUser.getUsername());
            sendWorkflowAssignActionMessage(assignedUser.getCountryCode(), assignedUser.getPhone(), assignedUser.getEmail(), assignedUser.getMvnoId(), assignedUser.getFullName(), action,assignedToStaff.getId().longValue());
            sendUpdateDto.setNextTeamMappingId(Integer.valueOf(map.get("nextTatMappingId")));
            sendUpdateDto.setNextApproveStaffId(assignedToStaff.getId());
            sendUpdateDto.setQuotationId(leadQuotationWfDTO.getQuotationId());
            sendUpdateDto.setCurrentLoggedInStaffId(leadQuotationWfDTO.getCurrentLoggedInStaffId());
            sendUpdateDto.setApproveRequest(leadQuotationWfDTO.getApproveRequest());
            sendUpdateDto.setTeamName(teams.getName());
            sendUpdateDto.setFlag("Assigned");
            SendLeadQuotationMessage sendApproverForLeadMsg = new SendLeadQuotationMessage(sendUpdateDto);
//            messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL_QUOTATION);
            kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendLeadQuotationMessage.class.getSimpleName()));

            //updating tatmapping id in apigw lead
            leadQuotationWfDTOToLeadQuotationDetail(sendUpdateDto);

            //Tat  matrix
            if (map.containsKey("nextTatMappingId")) {

                if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                    if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                        map.put("tat_id", map.get("current_tat_id"));
                    tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, assignedUser.getId(), null);
                }
            }


        } else {
            sendUpdateDto.setFinalApproved(true);
            sendUpdateDto.setQuotationId(leadQuotationWfDTO.getQuotationId());
            sendUpdateDto.setFlag("Approved");
            SendLeadQuotationMessage sendApproverForLeadMsg = new SendLeadQuotationMessage(sendUpdateDto);
//            messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL_QUOTATION);
            kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendLeadQuotationMessage.class.getSimpleName()));
        }

        return sendUpdateDto;
    }

    public List getApprovalProgressForLeadQuotation(Integer mvnoId, Long buId, Integer nextTeamHierarchyMappingId) {
        List<TeamHierarchyDTO> teamHierarchyDTOList = new ArrayList<TeamHierarchyDTO>();
        try {
            Optional<Hierarchy> hierarchy;
            QHierarchy qHierarchy = QHierarchy.hierarchy;
            BooleanExpression booleanExpHierarchy = qHierarchy.isNotNull().and(qHierarchy.eventName.eq(CommonConstants.WORKFLOW_EVENT_NAME.LEAD_QUOTATION).and(qHierarchy.isDeleted.eq(false)).and(qHierarchy.mvnoId.eq(mvnoId)));
            if (buId != null && buId != 0) {
                booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.eq(buId));
                hierarchy = hierarchyRepository.findOne(booleanExpHierarchy);
            } else {
                booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.isNull());
                hierarchy = hierarchyRepository.findOne(booleanExpHierarchy);
            }
            if (hierarchy.isPresent()) {
                List<TeamHierarchyMapping> teamHierarchyMappings = hierarchy.get().getTeamHierarchyMappingList();
                if (nextTeamHierarchyMappingId == 0 && nextTeamHierarchyMappingId == null) {
                    for (int i = 0; i < teamHierarchyMappings.size(); i++) {
                        TeamHierarchyDTO dto = new TeamHierarchyDTO();
                        dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
                        dto.setStatus("Approved");
                        if (i + 1 == teamHierarchyMappings.size()) {
                            dto.setParentTeamsId(null);
                        } else {
                            dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
                        }
                        dto.setTeamName(teamsRepository.findById(teamHierarchyMappings.get(i).getTeamId().longValue()).get().getName());
                        teamHierarchyDTOList.add(dto);

                    }
                } else {
                    int currentOrder = 0;
                    for (TeamHierarchyMapping t : hierarchy.get().getTeamHierarchyMappingList()) {
                        if (Objects.equals(t.getId(), nextTeamHierarchyMappingId)) {
                            currentOrder = t.getOrderNumber();
                        }
                    }
                    for (int i = 0; i < teamHierarchyMappings.size(); i++) {
                        if (teamHierarchyMappings.get(i).getOrderNumber() < currentOrder) {
                            TeamHierarchyDTO dto = new TeamHierarchyDTO();
                            dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
                            dto.setStatus("Approved");
                            if (i + 1 == teamHierarchyMappings.size()) {
                                dto.setParentTeamsId(null);
                            } else {
                                dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
                            }
                            dto.setTeamName(teamsRepository.findById(teamHierarchyMappings.get(i).getTeamId().longValue()).get().getName());
                            teamHierarchyDTOList.add(dto);
                        } else {
                            TeamHierarchyDTO dto = new TeamHierarchyDTO();
                            dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
                            dto.setStatus("Pending");
                            if (i + 1 == teamHierarchyMappings.size()) {
                                dto.setParentTeamsId(null);
                            } else {
                                dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
                            }
                            dto.setTeamName(teamsRepository.findById(teamHierarchyMappings.get(i).getTeamId().longValue()).get().getName());
                            teamHierarchyDTOList.add(dto);

                        }
                    }
                }
            }
            return teamHierarchyDTOList;


        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage());
        }
        return teamHierarchyDTOList;
    }

    public GenericDataDTO reassignLeadQuotation(Long leadQuotationId) {
        LeadQuotationDetails leadQuotationDetails = leadQuotationDetailsRepository.findByQuotationDetailId(leadQuotationId);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<StaffUserPojo> staffUserList = new ArrayList<>();
        StaffUser staffUser = staffUserRepository.findById(leadQuotationDetails.getNextApproveStaffId()).orElse(null);
        StaffUser parentStaff = staffUser.getStaffUserparent();
        if (leadQuotationDetails.getNextApproveStaffId() != null) {
            if (getLoggedInUserId() == leadQuotationDetails.getNextApproveStaffId()) {
                if (leadQuotationDetails.getNextApproveStaffId() != null) {
                    staffUserList = getStaffFromCurrentTeammapping(leadQuotationDetails.getNextTeamMappingId(), leadQuotationDetails);
                    genericDataDTO.setDataList(staffUserList);
                }
            } else if (parentStaff != null) {
                while (staffUser.getStaffUserparent() != null && staffUserList.size() == 0) {
                    if (staffUser.getId() == getLoggedInUserId()) {
//                        genericDataDTO.setData(CaseConstants.CHANGE_PROBLEM_DOMAIN);
                        if (leadQuotationDetails.getNextTeamMappingId() != null) {
                            staffUserList = getStaffFromCurrentTeammapping(leadQuotationDetails.getNextTeamMappingId(), leadQuotationDetails);
                            genericDataDTO.setDataList(staffUserList);
                        }
                    } else {
                        staffUser = staffUser.getStaffUserparent();
                    }
                }
            }

        }
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        return genericDataDTO;

    }

    public GenericDataDTO updateLeadQuotationAssignee(LeadQuotationChangeAssigneePojo leadQuotationChangeAssigneePojo) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        LeadQuotationDetails existingLeadQuotation = new LeadQuotationDetails();
        existingLeadQuotation = leadQuotationDetailsRepository.findByQuotationDetailId(leadQuotationChangeAssigneePojo.getLeadQuotationDetailId());
        StaffUser loggedInStaff = staffUserRepository.findById(getLoggedInUserId()).orElse(null);
        StaffUser currentAssineeStaff = staffUserRepository.findById(existingLeadQuotation.getNextApproveStaffId()).orElse(null);
        StaffUser upcomingAssignee = staffUserRepository.findById(leadQuotationChangeAssigneePojo.getAssignee()).orElse(null);

        LeadQuotationWfDTO leadQuotationWfDTO = new LeadQuotationWfDTO();
        leadQuotationWfDTO.setQuotationId(leadQuotationChangeAssigneePojo.getLeadQuotationDetailId());
        leadQuotationWfDTO.setNextApproveStaffId(leadQuotationChangeAssigneePojo.getAssignee());


        //saving assignee
        existingLeadQuotation.setNextApproveStaffId(leadQuotationChangeAssigneePojo.getAssignee());
        leadQuotationDetailsRepository.save(existingLeadQuotation);
        leadQuotationWfDTO.setCurrentLoggedInStaffId(getLoggedInUserId());
        leadQuotationWfDTO.setRemark(leadQuotationChangeAssigneePojo.getRemark());
        leadQuotationWfDTO.setRemarkType(leadQuotationChangeAssigneePojo.getRemarkType());
        leadQuotationWfDTO.setStatus(leadQuotationChangeAssigneePojo.getStatus());

        SendLeadQuotationMessage sendLeadQuotationMessage = new SendLeadQuotationMessage(leadQuotationWfDTO);
        kafkaMessageSender.send(new KafkaMessageData(sendLeadQuotationMessage, SendLeadQuotationMessage.class.getSimpleName()));
//        messageSender.send(sendLeadQuotationMessage, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL_QUOTATION);
        workflowAuditService.saveAudit(null, CommonConstants.EVENT_NAME.LEAD_QUOTATION, existingLeadQuotation.getQuotationDetailId().intValue(), leadQuotationWfDTO.getFirstName(), leadQuotationChangeAssigneePojo.getAssignee(), upcomingAssignee.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), " Assigned to :- " + upcomingAssignee.getUsername());

        return genericDataDTO;

    }

    public void rejectDirectFromCreatedStaff(String eventName, Integer entityid) {
        boolean b = false;

        try {
            Customers customersCaf =null;
            Integer mvnoId = customersRepository.findMvnoIdById(entityid);
            switch (eventName) {
                case CommonConstants.WORKFLOW_EVENT_NAME.CAF: {
                    customersCaf = customersRepository.findById(entityid).orElse(null);
                    if (customersCaf != null) {

                        if (customersCaf.getNextTeamHierarchyMapping() == null) {
                            b = (customersCaf.getCreatedById()) == getLoggedInUserId();
                            if (true == b) {
                                customersCaf.setCafApproveStatus("Rejected");
                                customersCaf.setStatus(SubscriberConstants.REJECT);
                            }
                        }

                    }
                    break;
                }


                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN: {
                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(entityid).orElse(null);
                    if (postpaidPlan != null) {

                        if (postpaidPlan.getNextTeamHierarchyMapping() == null) {
                            b = (postpaidPlan.getCreatedById()) == getLoggedInUserId();
                            if (true == b) {
                                postpaidPlan.setPlanStatus("Rejected");
                                postpaidPlan.setStatus(SubscriberConstants.REJECT);
                            }
                        }

                    }
                    break;
                }


                case CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP: {
                    PlanGroup planGroup = planGroupRepository.findById(entityid).orElse(null);
                    if (planGroup != null) {

                        if (planGroup.getNextTeamHierarchyMappingId() == null) {
                            b = (planGroup.getCreatedById()) == getLoggedInUserId();
                            if (true == b) {
                                planGroup.setStatus("Rejected");
                                planGroup.setStatus(SubscriberConstants.REJECT);
                            }
                        }

                    }
                    break;
                }


                case CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING: {
                    CustSpecialPlanRelMappping custSpecialPlanRelMappping = custSpecialPlanRelMapppingRepository.findById(Long.valueOf(entityid)).orElse(null);
                    if (custSpecialPlanRelMappping != null) {

                        if (custSpecialPlanRelMappping.getNextTeamHierarchyMapping() == null) {
                            b = (custSpecialPlanRelMappping.getCreatedById()) == getLoggedInUserId();
                            if (true == b) {
                                custSpecialPlanRelMappping.setStatus("Rejected");
                                custSpecialPlanRelMappping.setStatus(SubscriberConstants.REJECT);
                            }
                        }

                    }
                    break;
                }


                case CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT:
                case CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE: {
                    CreditDocument creditDocument = creditDocService.get(entityid,mvnoId);
                    if (creditDocument != null) {

                        if (creditDocument.getNextTeamHierarchyMappingId() == null) {
                            b = (creditDocument.getCreatedById()) == getLoggedInUserId();
                            if (true == b) {
                                creditDocument.setStatus("Rejected");
                            }
                        }

                    }
                    break;
                }


//                   case CommonConstants.WORKFLOW_EVENT_NAME.CHANGE_DISCOUNT: {
//                       CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityid).orElse(null);
//                       if (customerServiceMapping != null) {
//
//                           if (customerServiceMapping.getNextTeamHierarchyMappingId() == null) {
//                               b = (customerServiceMapping.getLastModifiedById()) == getLoggedInUserId();
//                               if (true == b) {
//                                   customerServiceMapping.setDiscount_status(SubscriberConstants.REJECT);
//                               }
//                           }
//
//                       }
//                       break;
//                   }


                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD:

                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_TERMINATION: {
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(entityid).orElse(null);
                    if (customerServiceMapping != null) {

                        if (customerServiceMapping.getNextTeamHierarchyMappingId() == null) {
                            b = (customerServiceMapping.getCreatedById()) == getLoggedInUserId();
                            if (true == b) {
                                customerServiceMapping.setStatus("Rejected");
                                customerServiceMapping.setStatus(SubscriberConstants.REJECT);
                            }
                        }

                    }
                    break;
                }

                case CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION: {
                    CustomerDocDetails customerDocDetails = customerDocDetailsRepository.findById(entityid.longValue()).orElse(null);
                    if (customerDocDetails != null) {

                        if (customerDocDetails.getNextTeamHierarchyMappingId() == null) {
                            b = (customerDocDetails.getCreatedById()) == getLoggedInUserId();
                            if (true == b) {
                                customerDocDetails.setDocStatus("Rejected");
                                customerDocDetails.setDocStatus(SubscriberConstants.REJECT);
                            }
                        }

                    }
                    break;
                }

                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN: {
                    CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(Long.valueOf(entityid)).orElse(null);
                    if (customerInventoryMapping != null) {

                        if (customerInventoryMapping.getTeamHierarchyMappingId() == null) {
                            b = (customerInventoryMapping.getCreatedById()) == getLoggedInUserId();
                            if (true == b) {
                                customerInventoryMapping.setStatus("Rejected");
                                customerInventoryMapping.setStatus(SubscriberConstants.REJECT);
                            }
                        }

                    }
                    break;
                }


                case CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION: {
                    CustomerAddress customerAddress = customerAddressRepository.findById(entityid).orElse(null);
                    if (customerAddress != null) {

                        if (customerAddress.getNextTeamHierarchyMappingId() == null) {
                            b = (customerAddress.getCreatedById()) == getLoggedInUserId();
                            if (true == b) {
                                customerAddress.setStatus("Rejected");
                                customerAddress.setStatus(SubscriberConstants.REJECT);
                            }
                        }

                    }
                    break;
                }


                case CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION: {
                    DebitDocument debitDocument = debitDocRepository.findById(entityid).orElse(null);
                    if (debitDocument != null) {

                        if (debitDocument.getNextTeamHierarchyMappingId() == null) {
                            b = (debitDocument.getCreatedById()) == getLoggedInUserId();
                            if (true == b) {
                                debitDocument.setStatus("Rejected");
                                debitDocument.setStatus(SubscriberConstants.REJECT);
                            }
                        }

                    }
                    break;
                }


                case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION: {
                    Customers customers = customersRepository.findById(entityid).orElse(null);
                    if (customers != null) {

                        if (customers.getNextTeamHierarchyMapping() == null) {
                            b = (customers.getCreatedById()) == getLoggedInUserId();
                            if (true == b) {
                                String currentStaus = customers.getStatus();
                                customers.setStatus(currentStaus);
                                //customers.setStatus(SubscriberConstants.REJECT);
                            }
                        }

                    }
                    break;
                }


            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sharedTeamHierarchyData(Hierarchy hierarchy, Integer operation) {
        if (operation.equals(CommonConstants.OPERATION_ADD)) {
            SaveTeamHierarchyMappingMessage message = new SaveTeamHierarchyMappingMessage();
            message.setTeamHierarchyMappingList(hierarchy.getTeamHierarchyMappingList());
            message.setHierarchyId(hierarchy.getId());
            message.setOperationId(CommonConstants.OPERATION_ADD);
            //messageSender.send(message, SharedDataConstants.QUEUE_TEAM_HIERARCHY_CREATE_DATA_SHARE_COMMONAPIGW);
            kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
        } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
            UpdateTeamHierarchyMappingMessage message = new UpdateTeamHierarchyMappingMessage();
            message.setTeamHierarchyMappingList(hierarchy.getTeamHierarchyMappingList());
            message.setHierarchyId(hierarchy.getId());
            message.setOperationId(CommonConstants.OPERATION_UPDATE);
            //messageSender.send(message, SharedDataConstants.QUEUE_TEAM_HIERARCHY_UPDATE_DATA_SHARE_COMMONAPIGW);
            kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
        } else if (operation.equals(CommonConstants.OPERATION_DELETE)) {
            UpdateTeamHierarchyMappingMessage message = new UpdateTeamHierarchyMappingMessage();
            message.setIsDeleted(true);
            message.setTeamHierarchyMappingList(hierarchy.getTeamHierarchyMappingList());
            message.setHierarchyId(hierarchy.getId());
            message.setOperationId(CommonConstants.OPERATION_DELETE);
            //messageSender.send(message, SharedDataConstants.QUEUE_TEAM_HIERARCHY_UPDATE_DATA_SHARE_COMMONAPIGW);
            kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
        }
    }


    public Map<String, Object> getCurrentTeamsConfigDetails(String eventName, Integer mvnoId, Long buId, Boolean isApproveRequest, Boolean isCreateRequest, Object entity) {
        Map<String, Object> map = new HashMap<>();
        Hierarchy hierarchy = hierarchyRepository.findOne(
                QHierarchy.hierarchy.isNotNull()
                        .and(QHierarchy.hierarchy.eventName.eq(eventName)
                                .and(QHierarchy.hierarchy.isDeleted.eq(false))
                                .and(QHierarchy.hierarchy.mvnoId.eq(mvnoId))
                                .and(buId != null ? QHierarchy.hierarchy.buId.eq(buId) : QHierarchy.hierarchy.buId.isNull()))
        ).orElse(null);
        if (hierarchy != null) {
            List<TeamHierarchyMapping> teamHierarchyMappingList = IterableUtils.toList(teamHierarchyMappingRepo.findAll(
                    QTeamHierarchyMapping.teamHierarchyMapping.isNotNull()
                            .and(QTeamHierarchyMapping.teamHierarchyMapping.hierarchyId.eq(Math.toIntExact(hierarchy.getId()))
                                    .and(QTeamHierarchyMapping.teamHierarchyMapping.isDeleted.eq(false)))
            ));
            Integer finalOrderNumber = null;
            TeamHierarchyMapping nextTeamMapping = null;
            TeamHierarchyMapping currentTeamMapping = null;
            finalOrderNumber = getFinalOrderNumber(isApproveRequest, isCreateRequest, entity, teamHierarchyMappingList, finalOrderNumber);
            for (TeamHierarchyMapping t : teamHierarchyMappingList) {
                finalOrderNumber = finalOrderNumber == null ? 0 : finalOrderNumber;
                if (t.getOrderNumber().equals(finalOrderNumber)) {
                    nextTeamMapping = t;
                }
                if (finalOrderNumber != 0) {
                    int orderNumber = finalOrderNumber - 1;
                    List<TeamHierarchyMapping> teamHierarchyMappings = teamHierarchyMappingList.stream().filter(teamHierarchyMapping -> teamHierarchyMapping.getOrderNumber().equals(orderNumber)).collect(Collectors.toList());
                    if (teamHierarchyMappings.size() > 0) {
                        currentTeamMapping = teamHierarchyMappings.get(0);
                    } else {
                        currentTeamMapping = nextTeamMapping;
                    }
                } else {
                    if (teamHierarchyMappingList.size() > 1) {
                        currentTeamMapping = teamHierarchyMappingList.get(0);
                    } else {
                        currentTeamMapping = nextTeamMapping;
                    }
                }
                map.put(CommonConstants.CURRENT_TEAM_ACTION, currentTeamMapping.getTeamAction());
                map.put(CommonConstants.CURRENT_TEAM_AUTO_APPROVE_ENABLE, currentTeamMapping.getIsAutoApprove());
                map.put(CommonConstants.CURRENT_TEAM_AUTO_ASSIGN_ENABLE, currentTeamMapping.getIsAutoAssign());
                return map;
            }
        }
        return null;
    }


    public Map<String, Object> getTeamForNextApproveHybrid(Integer mvnoId, Long buId, String eventName, String listType, Boolean isApproveRequest, boolean isCreateRequest, Object entity) {
        log.debug("======================================================Common method called for workflow.================================================================");
        Map<String, Object> map = new HashMap<>();
        Optional<Hierarchy> hierarchy;
        Long eventId = 0L;
        QTeamHierarchyMapping qTeamHierarchyMapping = QTeamHierarchyMapping.teamHierarchyMapping;
        QHierarchy qHierarchy = QHierarchy.hierarchy;

        // Build the hierarchy query
        BooleanExpression booleanExpHierarchy = qHierarchy.isNotNull()
                .and(qHierarchy.eventName.eq(eventName))
                .and(qHierarchy.isDeleted.eq(false))
                .and(qHierarchy.mvnoId.eq(mvnoId));
        if (buId != null) {
            booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.eq(buId));
        } else {
            booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.isNull());
        }

        hierarchy = hierarchyRepository.findOne(booleanExpHierarchy);

        if (hierarchy.isPresent()) {
            log.debug("Hierarchy found for the entity: " + entity.getClass());
            BooleanExpression expForTeamHirMapping = qTeamHierarchyMapping.isNotNull()
                    .and(qTeamHierarchyMapping.hierarchyId.eq(Math.toIntExact(hierarchy.get().getId())))
                    .and(qTeamHierarchyMapping.isDeleted.eq(false));
            List<TeamHierarchyMapping> teamHierarchyMappingList = (List<TeamHierarchyMapping>) teamHierarchyMappingRepo.findAll(expForTeamHirMapping);

            Integer finalOrderNumber = null;
            TeamHierarchyMapping nextTeamMapping = null;
            TeamHierarchyMapping currentTeamMapping = null;

            finalOrderNumber = getFinalOrderNumber(isApproveRequest, isCreateRequest, entity, teamHierarchyMappingList, finalOrderNumber);

            for (TeamHierarchyMapping t : teamHierarchyMappingList) {
                finalOrderNumber = finalOrderNumber == null ? 0 : finalOrderNumber;
                if (t.getOrderNumber().equals(finalOrderNumber)) {
                    nextTeamMapping = t;
                }
                if (finalOrderNumber != 0) {
                    int orderNumber = finalOrderNumber - 1;
                    List<TeamHierarchyMapping> teamHierarchyMappings = teamHierarchyMappingList.stream()
                            .filter(teamHierarchyMapping -> teamHierarchyMapping.getOrderNumber().equals(orderNumber))
                            .collect(Collectors.toList());
                    if (teamHierarchyMappings.size() > 0) {
                        currentTeamMapping = teamHierarchyMappings.get(0);
                    } else {
                        currentTeamMapping = nextTeamMapping;
                    }
                } else {
                    if (teamHierarchyMappingList.size() > 1) {
                        currentTeamMapping = teamHierarchyMappingList.get(0);
                    } else {
                        currentTeamMapping = nextTeamMapping;
                    }
                }
            }

            if (currentTeamMapping != null && currentTeamMapping.getTeamAction() != null && isApproveRequest != null) {
                if (isApproveRequest) {
                    log.warn("Initializing Action process for the workflow");
                    workFlowQueryUtils.checkAction(currentTeamMapping.getTeamAction(), eventName, entity);
                    log.warn("Action process for the workflow completed");
                }
            }

            try {
                if (nextTeamMapping != null) {
                    boolean flag = true;
                    if (nextTeamMapping != null && Objects.nonNull(nextTeamMapping.getIsAutoAssign()) && nextTeamMapping.getIsAutoAssign()) {
                        // Auto-approval logic (from method 2)
                        int staffId = 0;
                        if (nextTeamMapping.getQueryFieldList().size() > 0) {
                            log.warn("Initializing condition process for the workflow");
                            flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
                            log.warn("Condition process for the workflow completed");
                        }
                        if (flag) {
                            Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                            List<ServiceArea> serviceAreaList = getServiceAreaFromEntity(entity);
                            List<StaffUserPojo> staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(serviceAreaList, buId, teams);
                            staffId = workFlowQueryUtils.assignStaffFromList(staffUsers, eventName, entity);
                        }
                        int k = teamHierarchyMappingList.indexOf(nextTeamMapping);
                        while (k < teamHierarchyMappingList.size() && staffId == 0 && k >= 0) {
                            flag = true;
                            nextTeamMapping = teamHierarchyMappingList.get(k);
                            Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                            if (nextTeamMapping.getQueryFieldList().size() > 0) {
                                log.warn("Initializing condition process for the workflow");
                                flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
                                log.warn("Condition process for the workflow completed");
                            }
                            if (flag) {
                                nextTeamMapping = teamHierarchyMappingList.get(k);
                                if (teams != null) {
                                    teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                                    List<ServiceArea> serviceAreaList = getServiceAreaFromEntity(entity);
                                    List<StaffUserPojo> staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(serviceAreaList, buId, teams);
                                    staffId = workFlowQueryUtils.assignStaffFromList(staffUsers, eventName, entity);
                                }
                            }
                            if (isApproveRequest != null) {
                                if (isApproveRequest || isCreateRequest) {
                                    k++;
                                } else {
                                    k--;
                                }
                            } else {
                                k++;
                            }
                        }
                        if (staffId != 0 && nextTeamMapping != null) {
                            map.put("staffId", String.valueOf(staffId));
                            map.put("nextTatMappingId", String.valueOf(nextTeamMapping.getId()));
                            map.put("eventId", String.valueOf(eventId));
                            map.put("eventName", String.valueOf(eventName));
                            map.put("tat_id", String.valueOf(nextTeamMapping.getTat_id()));
                            map.put("current_tat_id", String.valueOf(currentTeamMapping != null ? currentTeamMapping.getTat_id() : nextTeamMapping.getTat_id()));
                            map.put("workFlowId", String.valueOf(hierarchy.get().getId()));
                            map.put("orderNo", String.valueOf(nextTeamMapping.getOrderNumber()));
                            map.put("nextTeamHierarchyMappingId", nextTeamMapping.getId());

                            map.put("isAutoAssign",nextTeamMapping.getIsAutoAssign().toString());

                        }
                    }
                    else {
                        // Manual approval logic (from method 1)
                        List<StaffUserPojo> staffUsers = new ArrayList<>();
                        if (nextTeamMapping.getQueryFieldList().size() > 0) {
                            flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
                        }
                        if (flag) {
                            Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                            staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), buId, teams);
                        }
                        int k = teamHierarchyMappingList.indexOf(nextTeamMapping);
                        while (k < teamHierarchyMappingList.size() && staffUsers.size() == 0 && k >= 0) {
                            flag = true;
                            nextTeamMapping = teamHierarchyMappingList.get(k);
                            Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                            if (nextTeamMapping.getQueryFieldList().size() > 0) {
                                flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
                            }
                            if (flag) {
                                nextTeamMapping = teamHierarchyMappingList.get(k);
                                if (teams != null) {
                                    teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                                    staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), buId, teams);
                                }
                            }
                            if (isApproveRequest != null) {
                                if (isApproveRequest || isCreateRequest) {
                                    k++;
                                } else {
                                    k--;
                                }
                            } else {
                                k++;
                            }
                        }
                        if (staffUsers.size() != 0 && nextTeamMapping.getId() != 0) {
                            map.put("assignableStaff", staffUsers);
                            map.put("nextTeamHierarchyMappingId", nextTeamMapping.getId());
                            map.put("tat_id", String.valueOf(nextTeamMapping.getTat_id()));
                            map.put("current_tat_id", String.valueOf(currentTeamMapping != null ? currentTeamMapping.getTat_id() : nextTeamMapping.getTat_id()));
                            map.put("workFlowId", String.valueOf(hierarchy.get().getId()));
                            map.put("orderNo", String.valueOf(nextTeamMapping.getOrderNumber()));
                            map.put("isAutoAssign",nextTeamMapping.getIsAutoAssign() != null ? nextTeamMapping.getIsAutoAssign().toString() : false);
                        }
                        map.put("eventId", 0);
                        map.put("eventName", String.valueOf(eventName));
                    }
                }
                return map;
            } catch (CustomValidationException ex) {
                ApplicationLogger.logger.error(ex.getMessage(), ex);
                throw new CustomValidationException(ex.getErrCode(), ex.getMessage(), null);
            } catch (Exception e) {
                ApplicationLogger.logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        log.debug("Hierarchy not found for the entity: " + entity.getClass());
        return map;
    }


    public List<String> findByListofEventBasedOnActionPerformed(String actionPerformed, Integer mvnoId, Integer buIds) {
        List<Long> hierarchyIdList = new ArrayList<>();
        List<String> eventNameList = new ArrayList<>();
        if (buIds != null) {
            hierarchyIdList = teamHierarchyMappingRepo.findHierarchyIdByActionNameAndMvnoIdAndBuIds(actionPerformed, mvnoId, buIds);
        } else {
            hierarchyIdList = teamHierarchyMappingRepo.findHierarchyIdByActionNameAndMvnoId(actionPerformed, mvnoId);
        }
        if (hierarchyIdList != null && hierarchyIdList.size() > 0) {
            eventNameList = hierarchyRepository.findAllEventNamesByHierarchIds(hierarchyIdList);
            if (!eventNameList.isEmpty() && eventNameList.size() > 0) {
                return eventNameList;
            }
        }
        return null;
    }


//    public void autoApproveEntityBasedOnEventTrigger(Integer entityId, String triggeredActionName, Integer mvnoId, Integer buIds) {
//        try {
//            List<String> eventNameList = hierarchyService.findByListofEventBasedOnActionPerformed(triggeredActionName, mvnoId, buIds);
//            if (!eventNameList.isEmpty() && eventNameList.size() > 0) {
//                for (String eventName : eventNameList) {
//                    switch (eventName) {
//                        case CommonConstants.WORKFLOW_EVENT_NAME.CAF: {
//                            Map<String, Object> map = new HashMap<>();
//                            Customers customers = customersRepository.findById(entityId).orElse(null);
//                            if (customers != null) {
//                                if (customers.getStatus().equalsIgnoreCase(CommonConstants.NEW_ACTIVATION_STATUS)) {
//                                    map = hierarchyService.getCurrentTeamsConfigDetails(CommonConstants.WORKFLOW_EVENT_NAME.CAF, customers.getMvnoId(), customers.getBuId(), true, customers.getNextTeamHierarchyMapping() == null, customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()));
//                                    if(triggeredActionName.equalsIgnoreCase(map.get(CommonConstants.CURRENT_TEAM_ACTION).toString())){
//                                        if(map.get(CommonConstants.CURRENT_TEAM_AUTO_APPROVE_ENABLE).equals(true)) {
//                                            autoApproveCAF(customers,entityId);
//                                        }
//                                    }
//
//                                }
//                            } else {
//                                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Customer not found !!", null);
//                            }
//                            break;
//                        }
//                        case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION:{
//                            Map<String, Object> map = new HashMap<>();
//                            Customers customers = customersRepository.findById(entityId).orElse(null);
//                            if (customers != null) {
//                                if (customers.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)) {
//                                    map = hierarchyService.getCurrentTeamsConfigDetails(CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION, customers.getMvnoId(), customers.getBuId(), true, customers.getNextTeamHierarchyMapping() == null, customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()));
//                                    if(triggeredActionName.equalsIgnoreCase(map.get(CommonConstants.CURRENT_TEAM_ACTION).toString())) {
//                                        if (map.get(CommonConstants.CURRENT_TEAM_AUTO_APPROVE_ENABLE).equals(true)) {
//                                            autoApproveTerminate(customers, entityId);
//                                        }
//                                    }
//                                }
//                            } else {
//                                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Customer not found !!", null);
//                            }
//                            break;
//                        }
//                    }
//                }
//
//            }
//
//        } catch (CustomValidationException e) {
//            e.getMessage();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    @Transactional
    public void autoApproveEntityBasedOnEventTrigger(Integer entityId, String triggeredActionName, Integer mvnoId, Integer buIds) {
        try {
            List<String> eventNameList = hierarchyService.findByListofEventBasedOnActionPerformed(triggeredActionName, mvnoId, buIds);
            System.out.println(" ############################### Fetching eventNameList for auto approval actions: " + eventNameList + " ###############################");
            if (eventNameList.size() > 0 && !eventNameList.isEmpty()) {
                Object entity = fetchEntity(entityId);
                if (entity == null) {
                    return;
                }
                for (String eventName : eventNameList) {
                    switch (eventName) {
                        case CommonConstants.WORKFLOW_EVENT_NAME.CAF:
                            System.out.println(" ############################### Initiation of CAF Auto Approval Process:  ###############################");
                            processAutoApproval(entity, entityId, triggeredActionName, CommonConstants.NEW_ACTIVATION_STATUS, eventName,mvnoId);
                            System.out.println(" ############################### Completion of CAF Auto Approval Process:  ###############################");
                            break;
                        case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION:
                            processAutoApproval(entity, entityId, triggeredActionName, CommonConstants.ACTIVE_STATUS, eventName,mvnoId);
                            break;

                        // More cases can be added here in the future
                    }
                }
            }
        } catch (CustomValidationException e) {
            log.error("Validation Error: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected Error in autoApproveEntityBasedOnEventTrigger", e);
        }
    }

    private Object fetchEntity(Integer entityId) {
        Object entity = customersRepository.findById(entityId).orElse(null);

        if (entity == null) {
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Entity not found!", null);
        }

        return entity;
    }

    private void processAutoApproval(Object entity, Integer entityId, String triggeredActionName, String requiredStatus, String eventName,Integer mvnoId) {

        if (entity instanceof Customers) {
            Customers customer = (Customers) entity;
            String clientServiceValue = clientServiceSrv.getValueByNameAndmvnoId(ClientServiceConstant.WORKFLOW_HYBRID_ASSIGN,customer.getMvnoId());

            if (!customer.getStatus().equalsIgnoreCase(requiredStatus)) {
                return; // Entity status doesn't match the event criteria
            }
            Map<String, Object> configMap = getTeamConfigForCustomer(customer, eventName);
            System.out.println("################### Auto Approval For ConfigMapping: " + configMap + " and  HYBRID_WORKFLOW Set To : "+clientServiceValue+" ###################");
            if (configMap != null && clientServiceValue!=null && (shouldAutoApprove(configMap, triggeredActionName) || clientServiceValue.equalsIgnoreCase("True"))) {
                if (CommonConstants.WORKFLOW_EVENT_NAME.CAF.equals(eventName)) {
                    System.out.println("################### Initiating for autoApproveCAF  ###################");
                    autoApproveCAF(customer, entityId, configMap,mvnoId);
                    System.out.println("################### Completion for autoApproveCAF  ###################");
                } else if (CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION.equals(eventName)) {
                    autoApproveTerminate(customer, entityId, configMap);
                }
            }
//            else  if(configMap !=null ){
//
//                if (CommonConstants.WORKFLOW_EVENT_NAME.CAF.equals(eventName)) {
//                    String clientServiceValue = clientServiceSrv.getValueByNameAndmvnoId(ClientServiceConstant.WORKFLOW_HYBRID_ASSIGN,customer.getMvnoId());
//                    System.out.println("################### Initiating for autoApproveCAF  ###################");
//                    autoApproveCAF(customer, entityId, configMap);
//                    System.out.println("################### Completion for autoApproveCAF  ###################");
//                }
//
//            }


        }
        // Future entity types can be handled here with additional conditions
    }

    private Map<String, Object> getTeamConfigForCustomer(Customers customer, String eventName) {
        return hierarchyService.getCurrentTeamsConfigDetails(
                eventName,
                customer.getMvnoId(),
                customer.getBuId(),
                true,
                customer.getNextTeamHierarchyMapping() == null,
                customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext())
        );
    }

    private boolean shouldAutoApprove(Map<String, Object> configMap, String triggeredActionName) {
        return triggeredActionName.equalsIgnoreCase(String.valueOf(configMap.get(CommonConstants.CURRENT_TEAM_ACTION))) && (
                Boolean.TRUE.equals(configMap.get(CommonConstants.CURRENT_TEAM_AUTO_APPROVE_ENABLE)) || configMap.get(CommonConstants.CURRENT_TEAM_AUTO_APPROVE_ENABLE).equals(null));
    }


    public void autoApproveCAF(Customers customers, Integer entityId, Map<String, Object> configMap,Integer mvnoId) {
        try {
            Map<String,Object> map  = new HashMap<>();
            String clientServiceValue = clientServiceSrv.getValueByNameAndmvnoId(ClientServiceConstant.WORKFLOW_HYBRID_ASSIGN,customers.getMvnoId());
            if (clientServiceValue == null){
                clientServiceValue = "false";
            }
            System.out.println("################## HYBRID_WORKFLOW_FLAG :" + clientServiceValue + " #########################");
            if (customers.getCurrentAssigneeId() == null) {
                //auto pick from the first team
                System.out.println("################## Initation of autoPickAndAssign #########################");
                customers = (Customers) workflowAssignStaffMappingService.autoPickAndAssign(CommonConstants.WORKFLOW_EVENT_NAME.CAF, entityId);
                System.out.println("################## Completion of autoPickAndAssign #########################");
            } else {
                // if staff has already picked the entity in that case this will run
                StaffUserPojo staffUserPojo = staffUserService.findByStaffId(customers.getCurrentAssigneeId());
                workflowAssignStaffMappingService.generateToken(staffUserPojo);
            }
            if (customers != null) {
                //approve and get nextteam stafflist
                System.out.println("################## Initation of updateCustomerCafAssignment Approve Process #########################");
                CustomerCafAssignmentPojo customerCafAssignmentPojo = new CustomerCafAssignmentPojo();
                customerCafAssignmentPojo.setStaffId(customers.getCurrentAssigneeId());
                customerCafAssignmentPojo.setCustcafId(entityId);
                customerCafAssignmentPojo.setFlag("approved");
                customerCafAssignmentPojo.setRemark("Auto Approved");
                CafDto cafDto = customersService.updateCustomerCafAssignment(customerCafAssignmentPojo,null,mvnoId);
                if (cafDto.getCustomers() != null && cafDto.getCustomers().getCafApproveStatus() != null && cafDto.getCustomers().getStatus().equalsIgnoreCase(SubscriberConstants.ACTIVE)) {
                    CaftoCustomerMessage caftoCustomerMessage = new CaftoCustomerMessage();
                    caftoCustomerMessage.setCustomerId(cafDto.getCustomers().getId());
                    caftoCustomerMessage.setLoggedInUser(getLoggedInUserId());
                    caftoCustomerMessage.setStatus(cafDto.getCustomers().getStatus());
                    caftoCustomerMessage.setCafApproveStatus(cafDto.getCustomers().getCafApproveStatus() != null ? cafDto.getCustomers().getCafApproveStatus() : null);
//                       messageSender.send(caftoCustomerMessage, SharedDataConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_REVENUE);
                    kafkaMessageSender.send(new KafkaMessageData(caftoCustomerMessage, CaftoCustomerMessage.class.getSimpleName()));

                }
                log.info("updateCustomerCafAssignment success --------");
                System.out.println("################## Completion of updateCustomerCafAssignment Approve Process #########################");
                map = cafDto.getMap();
                //assign to next teams all staff
                if (clientServiceValue.equalsIgnoreCase("false") || map.get("isAutoAssign").toString().equalsIgnoreCase("false")) {
                    System.out.println("################## Initation of assignEveryStaff for next team Approve Process #########################");
                    hierarchyService.assignEveryStaff(entityId, CommonConstants.WORKFLOW_EVENT_NAME.CAF, true);
                    System.out.println("################## Completion of assignEveryStaff for next team Approve Process #########################");
                    log.info("assignEveryStaff success ------");
                }
            } else {
                log.warn("Customer not found after auto assign call ::::::: " + entityId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void autoApproveTerminate(Customers customers, Integer entityId, Map<String, Object> configMap) {
        try {
            if (customers.getCurrentAssigneeId() == null) {
                //auto pick from the first team
                customers = (Customers) workflowAssignStaffMappingService.autoPickAndAssign(CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION, entityId);
                log.info("autoPickAndAssign success ----------");
            } else {
                // if staff has already picked the entity in that case this will run
                StaffUserPojo staffUserPojo = staffUserService.findByStaffId(customers.getCurrentAssigneeId());
                workflowAssignStaffMappingService.generateToken(staffUserPojo);
            }
            if (customers != null) {
                //approve and get nextteam stafflist
                CustomerCafAssignmentPojo customerCafAssignmentPojo = new CustomerCafAssignmentPojo();
                customerCafAssignmentPojo.setStaffId(customers.getCurrentAssigneeId());
                customerCafAssignmentPojo.setCustcafId(entityId);
                customerCafAssignmentPojo.setFlag("approved");
                customerCafAssignmentPojo.setRemark("Auto Approved");
                GenericDataDTO genericDataDTO = customersService.custApproveStatus(customers.getId(), "Approved", null, "Auto Approved");
                log.info("updateCustomerCafAssignment success --------");
                //assign to next teams all staff
                if (Boolean.FALSE.equals(configMap.get(CommonConstants.CURRENT_TEAM_AUTO_ASSIGN_ENABLE))) {
                    hierarchyService.assignEveryStaff(entityId, CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION, true);
                    log.info("assignEveryStaff success ------");
                }

            } else {
                log.warn("Customer not found after auto assign call ::::::: " + entityId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public GenericDataDTO getCurrentTeamAction(Integer customersId, String eventName ){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            Customers customers = customersRepository.findById(customersId).orElse(null);
            if(customers!=null){
                Map<String, Object> configMap = getTeamConfigForCustomer(customers, eventName);
                genericDataDTO.setData(configMap.get(CommonConstants.CURRENT_TEAM_ACTION));
                genericDataDTO.setResponseMessage("Success");
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                return genericDataDTO;
            }else{
                System.out.println("---------------------No customer found with id:-"+customersId+"----------------------------");
            }
            genericDataDTO.setData("");
            genericDataDTO.setResponseMessage("Failed");
            genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
            return genericDataDTO;
        }catch (Exception e){
            e.printStackTrace();
        }
        return genericDataDTO;
    }


    public Customers updateDateAndTimeForTAT(Map<String, String> map, Customers customers){
        Optional<Matrix> matrixDetails = matrixRepository.findById(Long.valueOf(map.get("tat_id")));
        if (matrixDetails.isPresent()) {
            Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();
            Integer Nextvalue = Integer.parseInt(String.valueOf(newMatrixDetails.get().getMtime()));
            if (newMatrixDetails.isPresent()) {
                customers = (Customers) tatUtils.UpdateDateTimefortat(newMatrixDetails.get(), customers, Nextvalue);
                //details.setStaffId(details.getParentId());
                return customers;
            }
        }
        return customers;
    }

}
