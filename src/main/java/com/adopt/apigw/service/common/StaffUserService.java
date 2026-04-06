package com.adopt.apigw.service.common;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveStaffUserSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateStaffUserSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.constants.cacheKeys;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.repository.CustomRepository;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.mapper.postpaid.StaffUserMapper;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.Branch.repository.BranchRepository;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.BusinessUnit.mapper.BusinessUnitMapper;
import com.adopt.apigw.modules.BusinessUnit.repository.BusinessUnitRepository;
import com.adopt.apigw.modules.BusinessUnit.service.BusinessUnitService;
import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;
import com.adopt.apigw.modules.Communication.Helper.CommunicationHelper;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.mapper.ServiceAreaMapper;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.Teams.domain.QTeamUserMapping;
import com.adopt.apigw.modules.Teams.domain.TeamUserMapping;
import com.adopt.apigw.modules.Teams.domain.Teams;
import com.adopt.apigw.modules.Teams.mapper.TeamsMapper;
import com.adopt.apigw.modules.Teams.repository.TeamUserMappingsRepocitory;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.Teams.service.TeamsService;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.auditLog.model.AuditForResponseModel;
import com.adopt.apigw.modules.role.domain.Role;
import com.adopt.apigw.modules.role.mapper.RoleMapper;
import com.adopt.apigw.modules.role.model.RoleDTO;
import com.adopt.apigw.modules.role.repository.RoleRepository;
import com.adopt.apigw.modules.role.service.RoleService;
import com.adopt.apigw.modules.staffLedgerDetails.dto.StaffLedgerDetailsDto;
import com.adopt.apigw.modules.staffLedgerDetails.repository.StaffLedgerDetailsRepository;
import com.adopt.apigw.modules.subscriber.model.ForgotPassowrdDTO;
import com.adopt.apigw.modules.subscriber.model.UpdateProfileDTO;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.MessageReceiverRabbitMq;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.*;
import com.adopt.apigw.repository.common.*;
import com.adopt.apigw.service.CacheService;
import com.adopt.apigw.service.postpaid.CreditDocService;
import com.adopt.apigw.service.postpaid.PartnerService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.adopt.apigw.utils.UpdateDiffFinder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StaffUserService extends AbstractService<StaffUser, StaffUserPojo, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(StaffUserService.class);
    public StaffUserService(BranchRepository branchRepository,
                            RoleRepository roleRepository) {
        sortColMap.put("id", "staffid");
        sortColMap.put("name", "firstname");
        sortColMap.put("userName", "username");
        sortColMap.put("roleName", "srn.concatname");
        this.branchRepository = branchRepository;
        this.roleRepository = roleRepository;
    }

    @Autowired
    private StaffUserServiceRepository staffUserServiceRepository;
    @Autowired
    private StaffUserRepository entityRepository;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private RoleService roleService;

    @Autowired
    private TeamsService teamsService;

    @Autowired
    private TeamsMapper teamsMapper;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private StaffUserMapper staffUserMapper;

    @Autowired
    private ServiceAreaService serviceAreaService;

    @Autowired
    private BusinessUnitService businessUnitService;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private BusinessUnitRepository businessUnitRepository;

    @Autowired
    private StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;


    @Autowired
    private StaffUserBusinessUnitMappingRepository staffUserBusinessUnitMappingRepository;
    @Autowired
    MessageSender messageSender;


    @Autowired
    MessageReceiverRabbitMq messageReceiver;

    @Autowired
    NotificationTemplateRepository templateRepository;


    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Autowired
    private BusinessUnitMapper businessUnitMapper;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    CustomersService customersService;

    @Autowired
    CreditDocService creditDocService;

    @Autowired
    CustomerCafAssignmentService customerCafAssignmentService;

    @Autowired
    HierarchyService hierarchyService;
    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    CustomRepository customRepository;

    @Autowired
    TeamUserMappingsRepocitory teamUserMappingsRepocitory;

    @Autowired
    KafkaMessageSender kafkaMessageSender;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    private CacheService cacheService;

    private static String MODULE = " [StaffUserService] ";
    private final BranchRepository branchRepository;
    private final RoleRepository roleRepository;
    @Autowired
    private RoleMapper roleMapper;


    @Autowired
    private StaffLedgerDetailsRepository staffLedgerDetailsRepository;

    public String getModuleNameForLog() {
        return "[StaffUserService]";
    }

//    @Autowired
//    CustomerCafAssignmentRepository customerCafAssignmentRepository;

    //private static String MODULE = " [StaffUserService] ";

    @Override
    public JpaRepository<StaffUser, Integer> getRepository() {
        return entityRepository;
    }

    public Page<StaffUser> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
            return entityRepository.searchEntity(searchText, pageRequest);
        } else {
            return entityRepository.searchEntity(searchText, pageRequest, getLoggedInUserPartnerId());
        }

    }

    public List<StaffUser> getAllActiveEntities() {
        List<StaffUser> staffUsers = new ArrayList<>();
        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
            staffUsers = entityRepository.findByStatusAndIsDeleteIsFalse(CommonConstants.ACTIVE_STATUS);
        } else {
            staffUsers = entityRepository.findByStatusAndPartneridAndIsDeleteIsFalse(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId());
        }
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
            //staffUsers.stream().filter(staff -> (staff.getMvnoId() == getMvnoIdFromCurrentStaff() && staff.getMvnoId() != 1));
            // TODO: pass mvnoID manually 6/5/2025
            staffUsers = entityRepository.findAllUsername(Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            return staffUsers;
        } else {
            // TODO: pass mvnoID manually 6/5/2025
            staffUsers = entityRepository.findAllUsername(Arrays.asList(getMvnoIdFromCurrentStaff(null), 1), getBUIdsFromCurrentStaff());
            return staffUsers;
        }
    }

    public List<StaffUser> getStaffUserFromUsername(String username) {
        return entityRepository.findByUsername(username);
    }

    public List<StaffUser> getActiveStaffUserFromUsername(String username) {
        return entityRepository.findByUsernameAndStatusAndIsDeleteIsFalse(username, CommonConstants.ACTIVE_STATUS);
    }

    public List<StaffUserPojo> searchUserCustom(String searchText) throws Exception {
        List<StaffUser> list = entityRepository.findAllUsername(searchText);
        return convertResponseModelIntoPojo(list);
    }

    public void increaseFailAttempts(String username) {
        List<StaffUser> userList = entityRepository.findByUsername(username);
        if (userList != null && userList.size() > 0) {
            StaffUser user = userList.get(0);
            user.setFailcount(user.getFailcount() + 1);
            entityRepository.save(user);
        }
    }

    public void resetFailAttempts(String username) {
        String SUBMODULE = MODULE + "[resetFailAttempts()]";
        List<StaffUser> userList = entityRepository.findByUsername(username);
        try {
            if (userList != null && userList.size() > 0) {
                StaffUser user = userList.get(0);
                user.setLast_login_time(LocalDateTime.now());
                user.setFailcount(0);
                entityRepository.save(user);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Transactional
    public void createPartnerUser(Partner partner) throws Exception {
        String SUBMODULE = MODULE + "[createPartnerUser()]";
        StaffUser user = new StaffUser();
        try {
            user.setUsername(partner.getEmail());
            user.setPassword(UtilsCommon.generateBcryptPassword(partner.getEmail()));
            user.setPartnerid(partner.getId());
            user.setEmail(partner.getEmail());
            user.setPhone(partner.getMobile());
            user.setFirstname(partner.getName());
            user.setIsDelete(partner.getIsDelete());
            user.setLastname(partner.getName());
            user.setStatus(CommonConstants.ACTIVE_STATUS);
            user.setMvnoId(partner.getMvnoId());
            // user.getBusinessUnit().setId(partner.getBuId());

            HashSet<Role> roles = new HashSet<>();
            //Add default role
            String roleId = getLoggedInUser().getRolesList();
            Role role = null;
            if(partner.getPartnerType().equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO)) {
                String roleName = UtilsCommon.getPartnerRoleName();
                if (!roleName.isEmpty()) {
                    List<Role> roleList = roleRepository.findAllByRolename(roleName);
                    if(!CollectionUtils.isEmpty(roleList))
                        role = roleService.convertRolePojoToRoleModel(roleMapper.domainToDTO(roleRepository.findById(roleList.get(0).getId()).get(),new CycleAvoidingMappingContext()));
                }

            } else {
                role = roleService.convertRolePojoToRoleModel(roleMapper.domainToDTO(roleRepository.findById(UtilsCommon.getPartnerRoleId().longValue()).get(),new CycleAvoidingMappingContext()));
            }
            if(role == null) {
                role = roleService.convertRolePojoToRoleModel(roleMapper.domainToDTO(roleRepository.findById(Long.valueOf(roleId)).get(),new CycleAvoidingMappingContext()));
            }
            roles.add(role);
            user.setRoles(roles);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        user = save(user);
        if (partner.getServiceAreaList().size() > 0) {
            StaffUser finalUser = user;
            partner.getServiceAreaList().forEach(serviceArea -> {
                StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
                staffUserServiceAreaMapping.setStaffId(finalUser.getId());
                staffUserServiceAreaMapping.setServiceId(Math.toIntExact(serviceArea.getId()));
                staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
                staffUserServiceAreaMapping.setLastmodifiedOn(LocalDateTime.now());
                staffUserServiceAreaMapping.setCreatedById(finalUser.getId());
                staffUserServiceAreaMapping.setLastModifiedByName(finalUser.getUsername());
                staffUserServiceAreaMapping.setLastModifiedById(finalUser.getId());
                staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
                staffUserServiceAreaMapping.setCreatedByName(finalUser.getUsername());
                staffUserServiceAreaMappingRepository.save(staffUserServiceAreaMapping);
            });
            if (partner.getId() > 0 && getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() == 1) {
                StaffUser finalUser2 = user;
                StaffUserBusinessUnitMapping staffUserBusinessUnitMapping = new StaffUserBusinessUnitMapping();
                staffUserBusinessUnitMapping.setStaffId(finalUser2.getId());
                staffUserBusinessUnitMapping.setCreatedOn(LocalDateTime.now());
                staffUserBusinessUnitMapping.setLastmodifiedOn(LocalDateTime.now());
                staffUserBusinessUnitMapping.setCreatedById(finalUser2.getId());
                staffUserBusinessUnitMapping.setLastModifiedByName(finalUser2.getUsername());
                staffUserBusinessUnitMapping.setLastModifiedById(finalUser2.getId());
                staffUserBusinessUnitMapping.setCreatedOn(LocalDateTime.now());
                staffUserBusinessUnitMapping.setCreatedByName(finalUser2.getUsername());
                Long l = getBUIdsFromCurrentStaff().get(0);
                Integer i = l.intValue();
                staffUserBusinessUnitMapping.setBusinessunitId(i);

                staffUserBusinessUnitMappingRepository.save(staffUserBusinessUnitMapping);
            }

        }
        List<ServiceAreaDTO> serviceAreaDTOS = user.getServiceAreaNameList().stream().map(data -> serviceAreaMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
        BooleanExpression booleanExpression = qStaffUserServiceAreaMapping.isNotNull().and(qStaffUserServiceAreaMapping.staffId.eq(user.getId()));
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = IterableUtils.toList(staffUserServiceAreaMappingRepository.findAll(booleanExpression));
        StaffUserPojo staffUserPojo = staffUserMapper.domainToDTO(user, new CycleAvoidingMappingContext());
        StaffUserMessage staffUserMessage = new StaffUserMessage(staffUserPojo, staffUserServiceAreaMappings, serviceAreaDTOS);
//        messageSender.send(staffUserMessage, RabbitMqConstants.QUEUE_STAFFUSER_SEND_RADIUS_SUCCESS1, RabbitMqConstants.QUEUE_STAFFUSER_SEND_TASK_MGMT_SUCCESS);
        kafkaMessageSender.send(new KafkaMessageData(staffUserMessage,staffUserMessage.getClass().getSimpleName()));
        UserMessage userMessage = new UserMessage(staffUserPojo);
        kafkaMessageSender.send(new KafkaMessageData(userMessage, UserMessage.class.getSimpleName()));
//        messageSender.send(userMessage, RabbitMqConstants.QUEUE_STAFF_MANAGEMENT_SUCCESS);


    }

    public Page<StaffUser> getList(Integer pageNumber) {
        return getList(pageNumber, CommonConstants.DB_PAGE_SIZE);
    }

    @Override
    public Page<StaffUser> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        if (getLoggedInUser().getLco()) {
            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                if (filterList == null || 0 == filterList.size()) {
                    // TODO: pass mvnoID manually 6/5/2025
                    if (mvnoId == 1)
                        return entityRepository.findAll(pageRequest, getLoggedInUser().getPartnerId());
                    if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
                        // TODO: pass mvnoID manually 6/5/2025
                        return entityRepository.findAll(pageRequest, Arrays.asList(mvnoId, 1), getLoggedInUser().getPartnerId());
                    } else {
                        // TODO: pass mvnoID manually 6/5/2025
                        return entityRepository.findAll(pageRequest, Arrays.asList(mvnoId, 1), getBUIdsFromCurrentStaff(), getLoggedInUser().getPartnerId());
                    }
                } else return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
            } else {
                if (filterList == null || 0 == filterList.size()) {
                    // TODO: pass mvnoID manually 6/5/2025
                    if (mvnoId == 1) return entityRepository.findAll(pageRequest);
                    // TODO: pass mvnoID manually 6/5/2025
                    return entityRepository.findByPartneridAndIsDeleteIsFalse(getLoggedInUserPartnerId(), pageRequest, mvnoId);
                } else return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
            }
        } else {
            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                if (filterList == null || 0 == filterList.size()) {
                    // TODO: pass mvnoID manually 6/5/2025
                    if (mvnoId == 1) return entityRepository.findAll(pageRequest);
                    if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
                        // TODO: pass mvnoID manually 6/5/2025
                        return entityRepository.findAll(pageRequest, Arrays.asList(mvnoId, 1));
                    } else {
                        // TODO: pass mvnoID manually 6/5/2025
                        return entityRepository.findAll(pageRequest, Arrays.asList(mvnoId, 1), getBUIdsFromCurrentStaff());
                    }
                } else return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
            } else {
                if (filterList == null || 0 == filterList.size()) {
                    // TODO: pass mvnoID manually 6/5/2025
                    if (mvnoId == 1) return entityRepository.findAll(pageRequest);
                    // TODO: pass mvnoID manually 6/5/2025
                    return entityRepository.findByPartneridAndIsDeleteIsFalse(getLoggedInUserPartnerId(), pageRequest, mvnoId);
                } else return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
            }
        }
    }

    public Page<StaffUser> getList(Integer pageNumber, int customPageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, customPageSize);
        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
            return getRepository().findAll(pageRequest);
        } else {
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.findByPartneridAndIsDeleteIsFalse(getLoggedInUserPartnerId(), pageRequest, getMvnoIdFromCurrentStaff(null));
        }
    }

    public List<StaffUser> getAllUsers() {
        return entityRepository.findAll();
    }

    public void deleteStaffUser(Integer id) {
        entityRepository.deleteById(id);
        Optional<StaffUser> staffUser = entityRepository.findById(id);
        staffUser.get().setIsDelete(true);
        StaffUserPojo staffUserPojo = staffUserMapper.domainToDTO(staffUser.get(), new CycleAvoidingMappingContext());

        List<ServiceAreaDTO> serviceAreaDTOS = staffUser.get().getServiceAreaNameList().stream().map(data -> serviceAreaMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());

        QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
        BooleanExpression booleanExpression = qStaffUserServiceAreaMapping.isNotNull().and(qStaffUserServiceAreaMapping.staffId.eq(id));
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = IterableUtils.toList(staffUserServiceAreaMappingRepository.findAll(booleanExpression));

        StaffUserMessage staffUserMessage = new StaffUserMessage(staffUserPojo, staffUserServiceAreaMappings, serviceAreaDTOS);
//        messageSender.send(staffUserMessage, RabbitMqConstants.QUEUE_STAFFUSER_SEND_RADIUS_SUCCESS1, RabbitMqConstants.QUEUE_STAFFUSER_SEND_TASK_MGMT_SUCCESS);
        kafkaMessageSender.send(new KafkaMessageData(staffUserMessage,staffUserMessage.getClass().getSimpleName()));
//        messageSender.send(staffUserMessage,RabbitMqConstants.QUEUE_STAFFUSER_SEND_DELETE);
        createDataSharedService.deleteEntityDataForAllMicroService(staffUser);

//        messageSender.send(staffUserMessage, RabbitMqConstants.QUEUE_STAFFUSER_SEND_DELETE);


    }

    public StaffUser getStaffUserForAdd() {
        return new StaffUser();
    }

    public StaffUser getStaffUserForEdit(Integer id) throws Exception {
        return entityRepository.getOne(id);
    }

    public StaffUser getByUserName(String uname) throws Exception {
        StaffUser staffUser;
        staffUser = entityRepository.findUsername(uname);
        return staffUser;
    }

    public String forgotPass(StaffUser staffUser) throws Exception {
        Random random = new Random();
        String otp = String.format("%04d", random.nextInt(10000));
        staffUser.setOtp(otp);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatDateTime = now.format(format);
        LocalDateTime validate = LocalDateTime.parse(formatDateTime, format);
        staffUser.setOtpvalidate(validate);
        update(staffUser);
        CommunicationHelper communicationHelper = new CommunicationHelper();
        Map<String, String> map = new HashMap<>();
        map.put(CommunicationConstant.DESTINATION, staffUser.getPhone());
        map.put(CommunicationConstant.EMAIL, staffUser.getEmail());
        map.put(CommunicationConstant.OTP, otp);
        communicationHelper.generateCommunicationDetails(18L, Collections.singletonList(map));

        return "Success--" + otp;
    }

    public String validateForgotPassword(StaffUser staffUser, ForgotPassowrdDTO dto) throws Exception {
        String response;
        LocalDateTime time = staffUser.getOtpvalidate();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatDateTime = now.format(format);
        LocalDateTime validate = LocalDateTime.parse(formatDateTime, format);
        LocalDateTime tempDateTime = LocalDateTime.from(staffUser.getOtpvalidate());
        long minutes = tempDateTime.until(validate, ChronoUnit.MINUTES);
        long min = Long.parseLong(messagesProperty.get("staffuser.validate.time"));
        if (staffUser.getOtp().equalsIgnoreCase(dto.getOtp()) && minutes <= min) {
            response = CommonConstants.FLASH_MSG_TYPE_SUCCESS;
        } else {
            response = CommonConstants.FLASH_MSG_TYPE_ERROR;
        }
        return response;
    }

    public StaffUserPojo updateProfile(StaffUser staffUser, UpdateProfileDTO dto) throws Exception {
        staffUser.setFirstname(dto.getFirstname());
        staffUser.setLastname(dto.getLastname());
        staffUser.setEmail(dto.getEmail());
        staffUser.setPhone(dto.getPhone());
        update(staffUser);
        StaffUserPojo staffUserPojo = staffUserMapper.domainToDTO(staffUser, new CycleAvoidingMappingContext());
        return staffUserPojo;
    }

    public StaffUser saveStaffUser(StaffUser staffUser) throws Exception {
        String SUBMODULE = MODULE + " [saveStaffUser()] ";
        try {
            if (staffUser != null) {
                if (staffUser.getId() == null) {
                    PasswordEncoder encoder = new BCryptPasswordEncoder();
                    staffUser.setPassword(encoder.encode(staffUser.getPassword()));
                }
                if (staffUser.getPartnerid() != null) {
                    staffUser.setPartnerid(staffUser.getPartnerid());
                }

                if (getLoggedInUser().getLco())
                    staffUser.setLcoId(getLoggedInUser().getPartnerId());
                else
                    staffUser.setLcoId(null);

                StaffUser user = entityRepository.findStaffUserByUsername(staffUser.getUsername());
                if (staffUser.getId() != null) {
                    QStaffUserServiceAreaMapping qstaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
                    BooleanExpression booleanExpression = qstaffUserServiceAreaMapping.isNotNull().and(qstaffUserServiceAreaMapping.staffId.eq(staffUser.getId()));
                    List<StaffUserServiceAreaMapping> oldstaffUserServiceAreaMappings = IterableUtils.toList(staffUserServiceAreaMappingRepository.findAll(booleanExpression));
                    user = entityRepository.save(staffUser);
                    QStaffUserServiceAreaMapping qstaffUserServiceAreaMapping11 = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
                    BooleanExpression booleanExpression11 = qstaffUserServiceAreaMapping.isNotNull().and(qstaffUserServiceAreaMapping.staffId.eq(staffUser.getId()));
                    List<StaffUserServiceAreaMapping> newstaffUserServiceAreaMappings = IterableUtils.toList(staffUserServiceAreaMappingRepository.findAll(booleanExpression11));

                    QStaffUserBusinessUnitMapping qStaffUserBusinessUnitMapping = QStaffUserBusinessUnitMapping.staffUserBusinessUnitMapping;
                    BooleanExpression booleanExpression2 = qStaffUserBusinessUnitMapping.isNotNull().and(qStaffUserBusinessUnitMapping.staffId.eq(staffUser.getId()));
                    List<StaffUserBusinessUnitMapping> oldstaffUserBusinessUnitMappings = IterableUtils.toList(staffUserBusinessUnitMappingRepository.findAll(booleanExpression2));
                    QStaffUserBusinessUnitMapping qStaffUserBusinessUnitMapping1 = QStaffUserBusinessUnitMapping.staffUserBusinessUnitMapping;
                    BooleanExpression booleanExpression22 = qStaffUserBusinessUnitMapping.isNotNull().and(qStaffUserBusinessUnitMapping.staffId.eq(staffUser.getId()));
                    List<StaffUserBusinessUnitMapping> newstaffUserBusinessUnitMappings = IterableUtils.toList(staffUserBusinessUnitMappingRepository.findAll(booleanExpression22));

                    //user = entityRepository.save(staffUser);

                    if (oldstaffUserServiceAreaMappings != newstaffUserServiceAreaMappings) {
                        boolean flag = true;
                    }

                    if (oldstaffUserBusinessUnitMappings != newstaffUserBusinessUnitMappings) {
                        boolean flag = true;
                    }
                } else {
                    if (user != null) staffUser.setId(user.getId());
                    user = entityRepository.save(staffUser);
                }
                StaffUserPojo staffUserPojo = staffUserMapper.domainToDTO(staffUser, new CycleAvoidingMappingContext());
                QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
                BooleanExpression booleanExpression = qStaffUserServiceAreaMapping.isNotNull().and(qStaffUserServiceAreaMapping.staffId.eq(user.getId()));
                List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = IterableUtils.toList(staffUserServiceAreaMappingRepository.findAll(booleanExpression));

                QStaffUserBusinessUnitMapping qStaffUserBusinessUnitMapping = QStaffUserBusinessUnitMapping.staffUserBusinessUnitMapping;
                BooleanExpression booleanExpression1 = qStaffUserBusinessUnitMapping.isNotNull().and(qStaffUserBusinessUnitMapping.staffId.eq(user.getId()));
                List<StaffUserBusinessUnitMapping> staffUserBusinessUnitMappings = IterableUtils.toList(staffUserBusinessUnitMappingRepository.findAll(booleanExpression1));

                //  Set<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = (Set<StaffUserServiceAreaMapping>) staffUserServiceAreaMappingRepository.findAll(booleanExpression);

                //  Set<Integer> users= staffUserServiceAreaMappings.stream().map(StaffUserServiceAreaMapping::getServiceId).collect(Collectors.toSet());
                // Set<StaffUserServiceAreaMapping> staffUserServiceAreaMappings1=staffUserServiceAreaMappingRepository.findAllById(user.getId());

                List<ServiceAreaDTO> serviceAreaDTOS = staffUser.getServiceAreaNameList().stream().map(data -> serviceAreaMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                StaffUserMessage staffUserMessage = new StaffUserMessage(staffUserPojo, staffUserServiceAreaMappings, serviceAreaDTOS);
                //List<BusinessUnitDTO> businessUnitDTOS = staffUser.getBusinessUnitNameList().stream().map(data -> businessUnitMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                //StaffUserMessage staffUserMessage = new StaffUserMessage(staffUserPojo, staffUserServiceAreaMappings, staffUserBusinessUnitMappings, serviceAreaDTOS, businessUnitDTOS);
//                messageSender.send(staffUserMessage, RabbitMqConstants.QUEUE_STAFFUSER_SEND_RADIUS_SUCCESS1, RabbitMqConstants.QUEUE_STAFFUSER_SEND_TASK_MGMT_SUCCESS);
                kafkaMessageSender.send(new KafkaMessageData(staffUserMessage,staffUserMessage.getClass().getSimpleName()));

                UserMessage userMessage = new UserMessage();
                userMessage.setId(user.getId());
                userMessage.setUsername(user.getUsername());
                userMessage.setPassword(user.getPassword());
                userMessage.setFirstname(user.getFirstname());
                userMessage.setLastname(user.getLastname());
                userMessage.setEmail(user.getEmail());
                if (user.getRoles() != null) {
                    Set<RoleMessage> roleMessageList = new HashSet<>();
                    for (Role role : user.getRoles()) {
                        roleMessageList.add(new RoleMessage(role));
                    }
                    userMessage.setRoles(roleMessageList);
                }
                userMessage.setPhone(user.getPhone());
                userMessage.setFailcount(user.getFailcount());
                userMessage.setStatus(user.getStatus());
                if (user.getLast_login_time() != null)
                    userMessage.setLast_login_time(user.getLast_login_time().toString());
                if (user.getCreatedate() != null) userMessage.setCreatedate(user.getCreatedate().toString());
                if (user.getUpdatedate() != null) userMessage.setUpdatedate(user.getUpdatedate().toString());
                userMessage.setPartnerid(user.getPartnerid());
                userMessage.setOtp(user.getOtp());
                if (user.getOtpvalidate() != null) userMessage.setOtpvalidate(user.getOtpvalidate().toString());
                userMessage.setIsDelete(user.getIsDelete());
                userMessage.setCountryCode(user.getCountryCode());
                userMessage.setSysstaff(user.getSysstaff());
                if (user.getServicearea() != null) userMessage.setServiceareaId(user.getServicearea().getId());
                if (user.getBusinessUnit() != null) userMessage.setBusinessunitid(user.getBusinessUnit().getId());
                if (user.getStaffUserparent() != null)
                    userMessage.setStaffUserparentId(user.getStaffUserparent().getId());
                userMessage.setMvnoId(user.getMvnoId());
                userMessage.setBranchId(user.getBranchId());
                if (user.getBusinessUnitNameList() != null && user.getBusinessUnitNameList().size() > 0) {
                    Set<BusinessUnitMessage> businessUnitMessageList = new HashSet<BusinessUnitMessage>();
                    for (BusinessUnit businessUnit : user.getBusinessUnitNameList()) {
                        businessUnitMessageList.add(new BusinessUnitMessage(businessUnit));
                    }
                    userMessage.setBusinessUnitMessageList(businessUnitMessageList);
                }
                if (user.getTeam() != null && user.getTeam().size() > 0) {
                    Set<TeamsMessage> teamsMessageList = new HashSet<TeamsMessage>();
                    for (Teams teams : user.getTeam()) {
                        teamsMessageList.add(new TeamsMessage(teams));
                    }
                    userMessage.setTeamMessageList(teamsMessageList);
                }

//                messageSender.send(userMessage, RabbitMqConstants.QUEUE_USER);
//                messageSender.send(userMessage, RabbitMqConstants.QUEUE_STAFF_MANAGEMENT_SUCCESS);
                kafkaMessageSender.send(new KafkaMessageData(userMessage, UserMessage.class.getSimpleName()));

//                messageSender.send(userMessage, RabbitMqConstants.QUEUE_RESPONSE_TO_SAVE_STAFFUSER_FROM_GATEWAY);
//                messageSender.send(userMessage, RabbitMqConstants.QUEUE_STAFF_SAVE_USER_SEND);
                log.info("{} Queue staff user",userMessage);


                return user;
            }
            return null;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public StaffUserPojo save(StaffUserPojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [save()] ";
        try {
            // TODO: pass mvnoID manually 6/5/2025
            pojo.setMvnoId(pojo.getMvnoId());
            if (pojo.getBusinessUnitIdsList() != null) {
                pojo.setBusinessUnitNameList(businessUnitRepository.findAllById(pojo.getBusinessUnitIdsList()));
            }

            if (getLoggedInUser().getLco()) pojo.setLcoId(getLoggedInUser().getPartnerId());
            else pojo.setLcoId(null);

            //if (pojo.getServiceAreaIdsList() != null || !pojo.getBusinessUnitIdsList().isEmpty()) {
            if (pojo.getServiceAreaIdsList() != null) {
                pojo.setServiceAreaNameList(serviceAreaRepository.findAllById(pojo.getServiceAreaIdsList()));
//                if(pojo.getBusinessUnitIdsList() != null) {
//                    pojo.setBusinessUnitNameList(businessUnitRepository.findAllById(pojo.getBusinessUnitIdsList()));
//                }
                StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
                obj = saveStaffUser(obj);
                pojo = convertStaffUserModelToStaffUserPojo(obj,pojo.getMvnoId());

                if(obj != null) {
                    StaffUser staffUserEntity = obj;
                    Set<Role> roleList = new HashSet<>();
                    Set<Teams> teamsList = new HashSet<>();
                    List<Integer> teamIds = new ArrayList<>();
                    List<Teams> teamsLst = new ArrayList<>();
                    List<ServiceArea> serviceAreaList = new ArrayList<>();
                    List<BusinessUnit> businessUnitList = new ArrayList<>();
                    if(obj.getRoles() != null) {
                        for (Role item : obj.getRoles()) {
                            Role role = new Role();
                            role.setId(item.getId());
                            roleList.add(role);
                        }
                        staffUserEntity.setRoles(roleList);
                    }
                    if(obj.getTeam() != null) {
//                        for (Teams item : obj.getTeam()) {
//                            Teams teams = new Teams();
//                            teams.setId(item.getId());
//                            teams.setName(item.getName());
//                            teams.setStatus(item.getStatus());
//                            teams.setIsDeleted(item.getIsDeleted());
//                            teams.setPartner(item.getPartner());
//                            teams.setMvnoId(item.getMvnoId());
//                            teamsList.add(teams);
//                        }
                        staffUserEntity.setTeam(obj.getTeam());
                    }
                    if(obj.getServiceAreaNameList() != null) {
                        for (ServiceArea item : obj.getServiceAreaNameList()) {
                            ServiceArea serviceArea = new ServiceArea();
                            serviceArea.setId(item.getId());
                            serviceArea.setName(item.getName());
                            serviceArea.setPincodeList(item.getPincodeList());
                            serviceAreaList.add(serviceArea);
                        }
                        staffUserEntity.setServiceAreaNameList(serviceAreaList);
                    }
                    if(obj.getBusinessUnitNameList() != null) {
                        for (BusinessUnit item : obj.getBusinessUnitNameList()) {
                            BusinessUnit businessUnit = new BusinessUnit();
                            businessUnit.setId(item.getId());
                            businessUnitList.add(businessUnit);
                        }
                        staffUserEntity.setBusinessUnitNameList(businessUnitList);
                    }
                    createDataSharedService.sendEntitySaveDataForAllMicroService(staffUserEntity);
                }

            } else {
                StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
                obj = saveStaffUser(obj);
                pojo = convertStaffUserModelToStaffUserPojo(obj,pojo.getMvnoId());

                if(obj != null) {
                    StaffUser staffUserEntity = obj;
                    Set<Role> roleList = new HashSet<>();
                    Set<Teams> teamsList = new HashSet<>();
                    List<ServiceArea> serviceAreaList = new ArrayList<>();
                    List<BusinessUnit> businessUnitList = new ArrayList<>();
                    if(obj.getRoles() != null) {
                        for (Role item : obj.getRoles()) {
                            Role role = new Role();
                            role.setId(item.getId());
                            roleList.add(role);
                        }
                        staffUserEntity.setRoles(roleList);
                    }
                    if(obj.getTeam() != null) {
//                        for (Teams item : obj.getTeam()) {
//                            Teams teams = new Teams();
//                            teams.setId(item.getId());
//                            teamsList.add(teams);
//                        }
                        staffUserEntity.setTeam(obj.getTeam());
                    }
                    if(obj.getServiceAreaNameList() != null) {
                        for (ServiceArea item : obj.getServiceAreaNameList()) {
                            ServiceArea serviceArea = new ServiceArea();
                            serviceArea.setId(item.getId());
                            serviceArea.setName(item.getName());
                            serviceArea.setPincodeList(item.getPincodeList());
                            serviceAreaList.add(serviceArea);
                        }
                        staffUserEntity.setServiceAreaNameList(serviceAreaList);
                    }
                    if(obj.getBusinessUnitNameList() != null) {
                        for (BusinessUnit item : obj.getBusinessUnitNameList()) {
                            BusinessUnit businessUnit = new BusinessUnit();
                            businessUnit.setId(item.getId());
                            businessUnitList.add(businessUnit);
                        }
                        staffUserEntity.setBusinessUnitNameList(businessUnitList);
                    }
                    createDataSharedService.sendEntitySaveDataForAllMicroService(staffUserEntity);
                }
            }
            //StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);

            return pojo;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public StaffUserPojo saveWithMvno(StaffUserPojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [save()] ";
        try {
            if (pojo.getServiceAreaIdsList() != null) {
                pojo.setServiceAreaNameList(serviceAreaRepository.findAllById(pojo.getServiceAreaIdsList()));
                StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
                obj = saveStaffUser(obj);
                pojo = convertStaffUserModelToStaffUserPojo(obj,pojo.getMvnoId());
            } else {
                StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
                obj = saveStaffUser(obj);
                pojo = convertStaffUserModelToStaffUserPojo(obj,pojo.getMvnoId());
            }
            return pojo;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }


    public StaffUserPojo update(StaffUserPojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [update()] ";
        StaffUser oldObj = null;
        if (pojo.getId() != null) {
            oldObj = get(pojo.getId(), pojo.getMvnoId());
        }
        try {
            // TODO: pass mvnoID manually 6/5/2025
            pojo.setMvnoId(pojo.getMvnoId());
            if (pojo.getServiceAreaIdsList() != null)
                pojo.setServiceAreaNameList(serviceAreaRepository.findAllById(pojo.getServiceAreaIdsList()));
            if (pojo.getBusinessUnitIdsList() != null)
                pojo.setBusinessUnitNameList(businessUnitRepository.findAllById(pojo.getBusinessUnitIdsList()));
            changestatus(pojo.getStatus(), pojo.getUsername());
            StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
            obj.setMvnoId(pojo.getMvnoId());
            obj = saveStaffUser(obj);
            if(obj != null) {
                StaffUser staffUserEntity = obj;
                Set<Role> roleList = new HashSet<>();
                Set<Teams> teamsList = new HashSet<>();
                List<ServiceArea> serviceAreaList = new ArrayList<>();
                List<BusinessUnit> businessUnitList = new ArrayList<>();
                if(obj.getRoles() != null) {
                    for (Role item : obj.getRoles()) {
                        Role role = new Role();
                        role.setId(item.getId());
                        roleList.add(role);
                    }
                    staffUserEntity.setRoles(roleList);
                }
                if(obj.getTeam() != null) {
//                    for (Teams item : obj.getTeam()) {
//                        Teams teams = new Teams();
//                        teams.setId(item.getId());
//                        teamsList.add(teams);
//                    }
                    staffUserEntity.setTeam(obj.getTeam());
                }
                if(obj.getServiceAreaNameList() != null) {
                    for (ServiceArea item : obj.getServiceAreaNameList()) {
                        ServiceArea serviceArea = new ServiceArea();
                        serviceArea.setId(item.getId());
                        serviceArea.setName(item.getName());
                        serviceArea.setPincodeList(item.getPincodeList());
                        serviceAreaList.add(serviceArea);
                    }
                    staffUserEntity.setServiceAreaNameList(serviceAreaList);
                }
                if(obj.getBusinessUnitNameList() != null) {
                    for (BusinessUnit item : obj.getBusinessUnitNameList()) {
                        BusinessUnit businessUnit = new BusinessUnit();
                        businessUnit.setId(item.getId());
                        businessUnitList.add(businessUnit);
                    }
                    staffUserEntity.setBusinessUnitNameList(businessUnitList);
                }
                createDataSharedService.updateEntityDataForAllMicroService(staffUserEntity);
            }
            if(oldObj!=null) {
                log.info("StaffUser update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
            }
            pojo = convertStaffUserModelToStaffUserPojo(obj,pojo.getMvnoId());
            return pojo;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public StaffUser convertStaffUserPojoToStaffUserModel(StaffUserPojo staffUserPojo) throws Exception {
        String SUBMODULE = MODULE + " [convertStaffUserPojoToStaffUserModel()] ";
        StaffUser staffUser = null;
        List<RoleDTO> roleDTOList = new ArrayList<>();
        try {
            if (staffUserPojo != null) {
                staffUser = new StaffUser();
                if (staffUserPojo.getId() != null) {
                    staffUser = get(staffUserPojo.getId(),staffUserPojo.getMvnoId());
                    staffUser.setId(staffUserPojo.getId());
                }
                staffUser.setUsername(staffUserPojo.getUsername());
                if (null == staffUserPojo.getId()) {
                    staffUser.setPassword(staffUserPojo.getPassword());
                }
                staffUser.setLcoId(staffUserPojo.getLcoId());
                staffUser.setEmail(staffUserPojo.getEmail());
                staffUser.setFirstname(staffUserPojo.getFirstname());
                staffUser.setLastname(staffUserPojo.getLastname());
                staffUser.setStatus(staffUserPojo.getStatus());
                staffUser.setPhone(staffUserPojo.getPhone());
                staffUser.setCountryCode(staffUserPojo.getCountryCode());
                staffUser.setFailcount(staffUserPojo.getFailcount());
                staffUser.setCreatedate(staffUserPojo.getCreatedate());
                staffUser.setUpdatedate(staffUserPojo.getUpdatedate());
                staffUser.setLast_login_time(staffUserPojo.getLast_login_time());
                staffUser.setPartnerid(staffUserPojo.getPartnerid());
                staffUser.setFullName(staffUserPojo.getFirstname() + " " + staffUserPojo.getLastname());
                staffUser.setSysstaff(staffUserPojo.getSysstaff());
                staffUser.setServiceAreaNameList(staffUserPojo.getServiceAreaNameList());
                staffUser.setBusinessUnitNameList(staffUserPojo.getBusinessUnitNameList());
                staffUser.setStaffUserServiceMappings(staffUserPojo.getStaffUserServiceMappingList());
//                if (staffUserPojo.getMvnoId() != null) {
                staffUser.setMvnoId(staffUserPojo.getMvnoId());
//                }
                if (staffUserPojo.getServiceAreaId() != null) {
                    ServiceArea serviceArea = serviceAreaService.getByID(staffUserPojo.getServiceAreaId());
                    staffUser.setServicearea(serviceArea);
                }

                if (staffUserPojo.getBusinessunitid() != null) {
                    BusinessUnit businessUnit = businessUnitService.getById(staffUserPojo.getBusinessunitid());
                    staffUser.setBusinessUnit(businessUnit);
                }

                if (staffUserPojo.getParentStaffId() != null) {
                    StaffUser staffUser2 = get(staffUserPojo.getParentStaffId(),staffUserPojo.getMvnoId());
                    staffUser.setStaffUserparent(staffUser2);
                }

                if (staffUserPojo.getRoleIds() != null && staffUserPojo.getRoleIds().size() > 0) {

                    // TODO: pass mvnoID manually 6/5/2025
                    if(staffUserPojo.getMvnoId()!=null){
                         roleDTOList = roleService.getAllByIdIn(staffUserPojo.getRoleIds().stream().map(Integer::longValue).collect(Collectors.toList()),staffUserPojo.getMvnoId());
                    }
                    else{
                         roleDTOList = roleService.getAllByIdInForCWSC(staffUserPojo.getRoleIds().stream().map(Integer::longValue).collect(Collectors.toList()),staffUserPojo.getMvnoId(),staffUser.getPartnerid());
                    }


                    staffUser.getRoles().clear();
                    staffUser.getRoles().addAll(roleDTOList.stream().map(dto -> roleService.convertRolePojoToRoleModel(dto)).collect(Collectors.toSet()));
                }

                if (staffUserPojo.getTeamIds() != null && staffUserPojo.getTeamIds().size() > 0) {
                    Set<Teams> teamList = teamsService.getAllByIdIn(new ArrayList<>(staffUserPojo.getTeamIds())).stream().map(dto -> teamsMapper.dtoToDomain(dto, new CycleAvoidingMappingContext())).collect(Collectors.toSet());
                    staffUser.getTeam().clear();
                    staffUser.getTeam().addAll(teamList);
                }
                if (staffUserPojo.getBranchId() != null) {
                    staffUser.setBranchId(staffUserPojo.getBranchId());
                }

                staffUser.setHrmsId(staffUserPojo.getHrmsId());
                staffUser.setProfileImage(staffUserPojo.getProfileImage());

                if(staffUserPojo.getDepartment()!=null) {
                    staffUser.setDepartment(staffUserPojo.getDepartment());
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return staffUser;
    }

    public StaffUserPojo convertStaffUserModelToStaffUserPojo(StaffUser staffUser,Integer mvnoId) throws Exception {
        String SUBMODULE = MODULE + " [convertStaffUserModelToStaffUserPojo()] ";
        StaffUserPojo pojo = null;
        try {
            if (staffUser != null) {
                pojo = new StaffUserPojo();
                pojo.setId(staffUser.getId());
                pojo.setUsername(staffUser.getUsername());
                if (null == pojo.getId()) pojo.setPassword(staffUser.getPassword());
                pojo.setEmail(staffUser.getEmail());
                pojo.setLcoId(staffUser.getLcoId());
                pojo.setFirstname(staffUser.getFirstname());
                pojo.setLastname(staffUser.getLastname());
                pojo.setStatus(staffUser.getStatus());
                pojo.setPhone(staffUser.getPhone());
                pojo.setCountryCode(staffUser.getCountryCode());
                pojo.setFailcount(staffUser.getFailcount());
                pojo.setCreatedate(staffUser.getCreatedate());
                pojo.setUpdatedate(staffUser.getUpdatedate());
                pojo.setLast_login_time(pojo.getLast_login_time());
                pojo.setPartnerid(staffUser.getPartnerid());
                pojo.setSysstaff(staffUser.getSysstaff());
                pojo.setFullName(staffUser.getFullName());
                pojo.setServicearea(staffUser.getServicearea());
                pojo.setBusinessUnit(staffUser.getBusinessUnit());
                pojo.setServiceAreaIdsList(staffUser.getServiceAreaNameList().stream().map(ServiceArea::getId).collect(Collectors.toList()));
                pojo.setBusinessUnitIdsList(staffUser.getBusinessUnitNameList().stream().map(BusinessUnit::getId).collect(Collectors.toList()));
                pojo.setStaffUserServiceMappingList(staffUser.getStaffUserServiceMappings());
                //                if (staffUser.getMvnoId() != null) {
                pojo.setMvnoId(staffUser.getMvnoId());
//                }
//                if (staffUser.getServicearea() != null) pojo.setServiceAreaId(staffUser.getServicearea().getId());
//                if (staffUser.getBusinessUnit() != null) pojo.setBusinessunitid(staffUser.getBusinessUnit().getId());

                if (staffUser.getServiceAreaNameList() != null && staffUser.getServiceAreaNameList().size() > 0) {
                    List<Integer> serviceAreaIds = new ArrayList<>();
                    List<String> serviceArealist = new ArrayList<>();
                    for (ServiceArea serviceArea : staffUser.getServiceAreaNameList()) {
                        serviceAreaIds.add(serviceArea.getId().intValue());
                        serviceArealist.add(serviceArea.getName());
                    }
                    pojo.setServiceAreasId(serviceAreaIds);
                    pojo.setServiceAreasNameList(serviceArealist);

                }

                if (staffUser.getBusinessUnitNameList() != null && staffUser.getBusinessUnitNameList().size() > 0) {
                    List<Integer> bussinessUnitIds = new ArrayList<>();
                    List<String> bussinessUnitNameList = new ArrayList<>();
                    for (BusinessUnit businessUnit : staffUser.getBusinessUnitNameList()) {
                        bussinessUnitIds.add(businessUnit.getId().intValue());
                        bussinessUnitNameList.add(businessUnit.getBuname());
                    }
                    pojo.setBusinessunitids(bussinessUnitIds);
                    pojo.setBusinessUnitNamesList(bussinessUnitNameList);

                }

                if (staffUser.getStaffUserparent() != null)
                    pojo.setParentStaffId(staffUser.getStaffUserparent().getId());

                if (staffUser.getRoles() != null && staffUser.getRoles().size() > 0) {
                    List<Integer> roleIds = new ArrayList<>();
                    List<String> roleNameList = new ArrayList<>();
                    for (Role role : staffUser.getRoles()) {
                        roleIds.add(role.getId().intValue());
                        roleNameList.add(role.getRolename());
                    }
                    pojo.setRoleIds(roleIds);
                    pojo.setRoleName(roleNameList);
                }
                if (null != staffUser.getCreatedate()) {
                    pojo.setRegDate(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm a").format(staffUser.getCreatedate()));
                }
                if (null != staffUser.getPartnerid()) {
                    Partner partner = partnerService.get(staffUser.getPartnerid(),mvnoId);
                    if (null != partner) {
                        pojo.setPartnerName(null != partner.getName() ? partner.getName() : "-");
                    } else {
                        pojo.setPartnerName("-");
                    }
                }

                if (null != staffUser.getStaffUserparent()) {
                    Optional<StaffUser> parent = entityRepository.findById(staffUser.getStaffUserparent().getId());
                    if (parent.isPresent()) {
                        pojo.setParentstaffname(parent.get().getUsername());
                    } else {
                        pojo.setParentstaffname("-");
                    }
                }
                if (null != staffUser.getTeam() && 0 < staffUser.getTeam().size()) {
                    Set<Long> teamIds = new HashSet<>();
                    List<String> teamNameList = new ArrayList<>();
                    for (Teams role : staffUser.getTeam()) {
                        teamIds.add(role.getId());
                        teamNameList.add(role.getName());
                    }
                    pojo.setTeamIds(teamIds);
                    pojo.setTeamNameList(teamNameList);
                }
                if (staffUser.getBranchId() != null) {
                    Branch branch = branchRepository.findById(Long.valueOf(staffUser.getBranchId())).orElse(null);
                    if (branch != null) {
                        pojo.setBranchId(staffUser.getBranchId());
                        pojo.setBranchName(branch.getName());
                    } else {
                        pojo.setBranchName("-");
                    }
                } else {
                    pojo.setBranchName("-");
                }
                pojo.setHrmsId(staffUser.getHrmsId());
                pojo.setProfileImage(staffUser.getProfileImage());
                pojo.setDisplayId(staffUser.getId());
                pojo.setDisplayName(staffUser.getFirstname());

                if(staffUser.getDepartment() !=null){
                    pojo.setDepartment(staffUser.getDepartment());
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    public List<StaffUserPojo> convertResponseModelIntoPojo(List<StaffUser> staffUserList) throws Exception {
        String SUBMODULE = MODULE + "[convertResponseModelIntoPojo()]";
        List<StaffUserPojo> pojoListRes = new ArrayList<>();
        try {
            if (staffUserList != null && staffUserList.size() > 0) {
                for (StaffUser staffUser : staffUserList) {
                    pojoListRes.add(convertStaffUserModelToStaffUserPojo(staffUser,staffUser.getMvnoId()));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;
    }

    public void validateRequest(StaffUserPojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
            if (pojo.getId() != null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
            }
            if (pojo.getPassword() == null) {
                throw new CustomValidationException(APIConstants.FAIL, "Please Enter Password", null);
            }
        }
        if (!(pojo.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS) || pojo.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS) || pojo.getStatus().equalsIgnoreCase(CommonConstants.TERMINATED))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (pojo != null && !operation.equals(CommonConstants.OPERATION_DELETE)) {
            if (pojo.getRoleIds() == null || pojo.getRoleIds().size() == 0) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.staffuser.role.required"), null);
            }
        }
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
        if (pojo != null && nameValidation(pojo) && !(operation.equals(CommonConstants.OPERATION_UPDATE)) && !(operation.equals(CommonConstants.OPERATION_DELETE))) {
            throw new CustomValidationException(APIConstants.FAIL, "Username is already in use.", null);
        }
    }

    public boolean nameValidation(StaffUserPojo pojo) {
        List<StaffUser> staffUserList = getStaffUserFromUsername(pojo.getUsername());
        boolean result = false;
        if (staffUserList != null && staffUserList.size() > 0) {

            for (StaffUser user : staffUserList) {
                if (user.getIsDelete().equals(0)) {
                    result = true;
                } else {
                    result = false;
                }
            }
        } else {
            result = false;
        }
        return result;
    }

//    public StaffUser changePassword(UserPasswordChangePojo pojo) {
//        String SUBMODULE = MODULE + "[changePassword()]";
//        List<StaffUser> staffUserList = this.getStaffUserFromUsername(pojo.getUserName());
//        if (staffUserList != null && staffUserList.size() > 0) {
//            StaffUser staffUser = staffUserList.get(0);
//            if (staffUser != null) {
//                PasswordEncoder encoder = new BCryptPasswordEncoder();
//                //if (encoder.matches(pojo.getOldPassword(), staffUser.getPassword())) {
//                staffUser.setNewpassword(encoder.encode(pojo.getNewPassword()));
//                staffUser.setPassword(staffUser.getNewpassword());
//                entityRepository.save(staffUser);
//                return staffUser;
//                //} else {
//                //throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.staffuser.oldpassword.mismatch"), null);
//                //}
//            }
//        } else {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.staffuser.not.found"), null);
//        }
//        return null;
//    }

    public List<StaffUserPojo> findStaffUserByRoleId(Long roleId) {
        String SUBMODULE = MODULE + " [findStaffUserByRoleId()] ";
        try {
            List<StaffUser> staffUserList = new ArrayList<>();
            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                staffUserList = entityRepository.findStaffByRole(roleId);
            } else {
                staffUserList = entityRepository.findStaffByRoleAndPartnerid(roleId, getLoggedInUserPartnerId());
            }
            if (null != staffUserList && 0 < staffUserList.size()) {
                List<StaffUserPojo> staffUserPojos = staffUserList.stream().map(data -> staffUserMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                // TODO: pass mvnoID manually 6/5/2025
                return staffUserPojos.stream().filter(staff -> (getMvnoIdFromCurrentStaff(null).intValue() == 1 || (staff.getMvnoId().intValue() == getMvnoIdFromCurrentStaff(null).intValue() || staff.getMvnoId().intValue() == 1))).collect(Collectors.toList());
//                return staffUserPojos.stream().filter(staff -> (staff.getMvnoId() == 1 || staff.getMvnoId() == getMvnoIdFromCurrentStaff())).collect(Collectors.toList());

            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return new ArrayList<>();
    }

    public List<StaffUserPojo> searchStaff(String searchText) throws Exception {
        String SUBMODULE = MODULE + " [searchCustomersCustom()] ";
        try {
            QStaffUser staffUser = QStaffUser.staffUser;
            BooleanExpression builder = staffUser.isNotNull();
            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                builder = builder.andAnyOf(staffUser.firstname.startsWithIgnoreCase(searchText), staffUser.lastname.startsWithIgnoreCase(searchText), staffUser.phone.startsWith(searchText), staffUser.email.startsWith(searchText), staffUser.username.startsWith(searchText)).and(staffUser.isDelete.isFalse()).and(staffUser.roles.any().id.in(CommonConstants.BACK_OFFICE_STAFF_ROLE_ID));
            }
            if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                builder = builder.andAnyOf(staffUser.firstname.startsWithIgnoreCase(searchText), staffUser.lastname.startsWithIgnoreCase(searchText), staffUser.phone.startsWith(searchText), staffUser.email.startsWith(searchText), staffUser.username.startsWith(searchText)).and(staffUser.partnerid.eq(getLoggedInUserPartnerId())).and(staffUser.isDelete.isFalse()).and(staffUser.roles.any().id.in(CommonConstants.BACK_OFFICE_STAFF_ROLE_ID));
            }

            if (getLoggedInUser().getLco()) builder = builder.and(staffUser.lcoId.eq(getLoggedInUser().getPartnerId()));
            else builder = builder.and(staffUser.lcoId.isNull());

            List<StaffUser> staffUserList = (List<StaffUser>) entityRepository.findAll(builder);
            return convertResponseModelIntoPojo(staffUserList);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Staff-User");
        List<StaffUserPojo> staffUserPojos = (entityRepository.findAll().stream().map(data -> staffUserMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
        createExcel(workbook, sheet, StaffUserPojo.class, staffUserPojos, getFields());
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<StaffUserPojo> staffUserPojos = entityRepository.findAll().stream().map(data -> staffUserMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, StaffUserPojo.class, staffUserPojos, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{StaffUserPojo.class.getDeclaredField("id"), StaffUserPojo.class.getDeclaredField("username"), StaffUserPojo.class.getDeclaredField("fullName"), StaffUserPojo.class.getDeclaredField("email"), StaffUserPojo.class.getDeclaredField("roleName"), StaffUserPojo.class.getDeclaredField("phone"), StaffUserPojo.class.getDeclaredField("regDate"), StaffUserPojo.class.getDeclaredField("status"), StaffUserPojo.class.getDeclaredField("partnerName")};
    }

    @Override
    public Page<StaffUser> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID)
                            return getStaffByNameOrUsernameOrEmailOrRoleName(searchModel.getFilterValue(), pageRequest);
                        else
                            return getStaffByNameOrUsernameOrEmailOrRoleNameByPartner(searchModel.getFilterValue(), pageRequest);
                    }
                } else throw new RuntimeException("Please Provide Search Column!");
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    public Page<StaffUser> getStaffByNameOrUsernameOrEmailOrRoleName(String s1, PageRequest pageRequest) {
        List<Integer> mvnoIds = new ArrayList<>();
        mvnoIds.add(1);
        // TODO: pass mvnoID manually 6/5/2025
        mvnoIds.add(getMvnoIdFromCurrentStaff(null));
        if (getLoggedInUser().getLco()) {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                return entityRepository.findAllByNameOrEmailOrRole(pageRequest, s1, s1, s1, s1, s1, mvnoIds, getLoggedInUser().getPartnerId());
            else
                return entityRepository.findAllByNameOrEmailOrRole(pageRequest, s1, s1, s1, s1, s1, mvnoIds, getBUIdsFromCurrentStaff(), getLoggedInUser().getPartnerId());
        } else {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                return entityRepository.findAllByNameOrEmailOrRole(pageRequest, s1, s1, s1, s1, s1, mvnoIds);
            else
                return entityRepository.findAllByNameOrEmailOrRole(pageRequest, s1, s1, s1, s1, s1, mvnoIds, getBUIdsFromCurrentStaff());
        }

    }

    public Page<StaffUser> getStaffByNameOrUsernameOrEmailOrRoleNameByPartner(String s1, PageRequest pageRequest) {
        List<Integer> mvnoIds = new ArrayList<>();
        mvnoIds.add(1);
        // TODO: pass mvnoID manually 6/5/2025
        mvnoIds.add(getMvnoIdFromCurrentStaff(null));
        if (getLoggedInUser().getLco()) {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                return entityRepository.findAllByNameOrEmailOrRoleByPartner(pageRequest, s1, s1, s1, s1, s1, getLoggedInUserPartnerId(), mvnoIds, getLoggedInUser().getPartnerId());
            else
                return entityRepository.findAllByNameOrEmailOrRoleByPartner(pageRequest, s1, s1, s1, s1, s1, getLoggedInUserPartnerId(), mvnoIds, getBUIdsFromCurrentStaff(), getLoggedInUser().getPartnerId());

        } else {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                return entityRepository.findAllByNameOrEmailOrRoleByPartner(pageRequest, s1, s1, s1, s1, s1, getLoggedInUserPartnerId(), mvnoIds);
            else
                return entityRepository.findAllByNameOrEmailOrRoleByPartner(pageRequest, s1, s1, s1, s1, s1, getLoggedInUserPartnerId(), mvnoIds, getBUIdsFromCurrentStaff());
        }
    }

    public List<AuditForResponseModel> getStaffListForAuditFor() {
        String SUBMODULE = MODULE + " [getStaffListForAuditFor()] ";
        List<AuditForResponseModel> responseList = new ArrayList<>();
        try {
            List<StaffUser> staffUserList = getAllActiveEntities();
            if (null != staffUserList && 0 < staffUserList.size()) {
                for (StaffUser customers : staffUserList) {
                    AuditForResponseModel responseModel = new AuditForResponseModel();
                    responseModel.setId(customers.getId());
                    responseModel.setName(customers.getFullName());
                    responseList.add(responseModel);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return responseList;
    }

    public List<StaffUser> getByServiceAreaId(Integer long1) {
        return entityRepository.getByServiceAreaId(long1);
    }

    public List<StaffUser> getAllStaffByServiceAreaId(Integer areaid) {
        QStaffUser qStaffUser = QStaffUser.staffUser;
        BooleanExpression booleanExpression = qStaffUser.isNotNull();
        booleanExpression = booleanExpression.and(qStaffUser.isDelete.eq(false));
        List<Integer> staffIdsall = new ArrayList<>();
        List<Integer> ids = entityRepository.findAllByServiceareaId(areaid);
        staffIdsall.addAll(ids);
        // booleanExpression = booleanExpression.and(qStaffUser.id.in(ids));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            QStaffUserBusinessUnitMapping qStaffUserBusinessUnitMapping = QStaffUserBusinessUnitMapping.staffUserBusinessUnitMapping;
            List<Integer> buids = getBUIdsFromCurrentStaff().stream().map(aLong -> aLong.intValue()).collect(Collectors.toList());
            BooleanExpression booleanExpression1 = qStaffUserBusinessUnitMapping.businessunitId.in(buids);
            List<StaffUserBusinessUnitMapping> staffUserBusinessUnitMappingsList = (List<StaffUserBusinessUnitMapping>) staffUserBusinessUnitMappingRepository.findAll(booleanExpression1);
            List<Integer> staffids = staffUserBusinessUnitMappingsList.stream().map(staffUserBusinessUnitMapping -> staffUserBusinessUnitMapping.getStaffId()).collect(Collectors.toList());
            staffIdsall.addAll(staffids);
            booleanExpression = booleanExpression.and(qStaffUser.id.in(staffIdsall));
        }
        // TODO: pass mvnoID manually 6/5/2025
        booleanExpression = booleanExpression.and(qStaffUser.mvnoId.eq(getMvnoIdFromCurrentStaff(null)));
        List<StaffUser> staffUserList = (List<StaffUser>) entityRepository.findAll(booleanExpression);
        List<StaffUser> staffUserList1 = new ArrayList<>();
        for (StaffUser list : staffUserList) {
            StaffUser staffUser = new StaffUser();
            staffUser.setId(list.getId());
            staffUser.setFullName(list.getFullName());
            staffUser.setPhone(list.getPhone());
            staffUserList1.add(staffUser);
        }
        return staffUserList1;
        //return staffUserList.stream().map(staffUser -> staffUser.getFullName()).collect(Collectors.toList());
    }

    public List<StaffUser> getByServiceAreaIdAndTeamId(Integer serviceAreaId, Long teamId) {
        QStaffUser qStaffUser = QStaffUser.staffUser;
        JPAQuery<StaffUserServiceAreaMapping> queryForStaffService = new JPAQuery<>(entityManager);
        JPAQuery<TeamUserMapping> queryForStaffTeam = new JPAQuery<>(entityManager);
        JPAQuery<StaffUser> queryForStaff = new JPAQuery<>(entityManager);
        QTeamUserMapping qTeamUserMapping = QTeamUserMapping.teamUserMapping;
        QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;


        BooleanExpression booleanExpression = qStaffUser.isDelete.eq(false).and(qStaffUser.isNotNull()).and(qStaffUser.status.eq(CommonConstants.ACTIVE_STATUS));
        List<StaffUser> staffUserList = queryForStaff.select(qStaffUser).from(qStaffUser).where(qStaffUser.id.in(queryForStaffService.select(qStaffUserServiceAreaMapping.staffId).from(qStaffUserServiceAreaMapping).where(qStaffUserServiceAreaMapping.serviceId.eq(serviceAreaId))).and(qStaffUser.id.in(queryForStaffTeam.select(qTeamUserMapping.staffId.intValue()).from(qTeamUserMapping).where(qTeamUserMapping.teamId.eq(teamId)))).and(booleanExpression)).fetch();
        return staffUserList;
    }

    public List<StaffUser> getByTeamId(Long teamId) {
        QStaffUser qStaffUser = QStaffUser.staffUser;
        JPAQuery<StaffUserServiceAreaMapping> queryForStaffService = new JPAQuery<>(entityManager);
        JPAQuery<TeamUserMapping> queryForStaffTeam = new JPAQuery<>(entityManager);
        JPAQuery<StaffUser> queryForStaff = new JPAQuery<>(entityManager);
        QTeamUserMapping qTeamUserMapping = QTeamUserMapping.teamUserMapping;

        BooleanExpression booleanExpression = qStaffUser.isDelete.eq(false).and(qStaffUser.isNotNull()).and(qStaffUser.status.eq(CommonConstants.ACTIVE_STATUS));
        List<StaffUser> staffUserList = queryForStaff.select(qStaffUser).from(qStaffUser).where(qStaffUser.id.in(queryForStaffTeam.select(qTeamUserMapping.staffId.intValue()).from(qTeamUserMapping).where(qTeamUserMapping.teamId.eq(teamId))).and(booleanExpression)).fetch();
        return staffUserList;
    }


//    public List<StaffUser> getByTeam(Long teamId) {
//        QStaffUser qStaffUser = QStaffUser.staffUser;
//        Teams teams = teamsService.getRepository().findById(teamId).get();
//        //JPAQuery<StaffUserServiceAreaMapping> queryForStaffService = new JPAQuery<>(entityManager);
//        JPAQuery<TeamUserMapping> queryForStaffTeam = new JPAQuery<>(entityManager);
//        JPAQuery<StaffUser> queryForStaff = new JPAQuery<>(entityManager);
//        QTeamUserMapping qTeamUserMapping = QTeamUserMapping.teamUserMapping;
//        QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
//
//
//        BooleanExpression booleanExpression = qStaffUser.isDelete.eq(false)
//                .and(qStaffUser.isNotNull()).and(qStaffUser.status.eq(CommonConstants.ACTIVE_STATUS));
//        List<StaffUser> staffUserList = queryForStaff.select(qStaffUser).from(qStaffUser)
//                .where(queryForStaffTeam.in())
//                .fetch();
//        return staffUserList;
//    }

    public StaffUser resetPassword(@Valid PasswordDto passwordDto) {
        String SUBMODULE = MODULE + " [resetPassword()] ";
        try {
            if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
                throw new IllegalArgumentException("Please enter valid password. New password and confirm password value must be same.");
            } else if (passwordDto.getUserName() != null) {
                List<StaffUser> staffUserList = this.getStaffUserFromUsername(passwordDto.getUserName());
                if (staffUserList != null && staffUserList.size() > 0) {
                    StaffUser staffUser = staffUserList.get(0);
                    if (staffUser != null) {
                        PasswordEncoder encoder = new BCryptPasswordEncoder();
                        staffUser.setNewpassword(encoder.encode(passwordDto.getNewPassword()));
                        staffUser.setPassword(staffUser.getNewpassword());
                        entityRepository.save(staffUser);
                        return staffUser;
                    }
                } else {
                    throw new IllegalArgumentException("Please enter valid username. No record found for this one.");
                }
            }
        } catch (Throwable e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        }
        return null;
    }

    @Override
    public StaffUser get(Integer id, Integer mvnoId) {
        String cacheKey = cacheKeys.STAFFUSER + id;
        StaffUser staffUser = null;

        try {
            // Try to get from cache
            staffUser = (StaffUser) cacheService.getFromCache(cacheKey, StaffUser.class);

            if (staffUser != null) {
                log.info("StaffUser from cache ::::::::::::::::" + staffUser.getId() + " ::::: Username :::::: " + staffUser.getUsername());
                // TODO: pass mvnoID manually 6/5/2025
//                Integer currentMvnoId = getMvnoIdFromCurrentStaff();
                if (mvnoId == null || mvnoId == 1 ||
                        staffUser.getMvnoId() == 1 || staffUser.getMvnoId().equals(mvnoId)) {
                    return staffUser;
                }
                return null;
            }

            // Fetch from DB if not found in cache
            staffUser = super.get(id,mvnoId);

            if (staffUser != null) {
                Integer currentMvnoId = getMvnoIdFromCurrentStaff();
                if (currentMvnoId == null || currentMvnoId == 1 ||
                        staffUser.getMvnoId() == 1 || staffUser.getMvnoId().equals(currentMvnoId)) {

                    log.info("StaffUser from Database query ::::::::::::::::" + staffUser.getId() + " ::::: Username :::::: " + staffUser.getUsername());
                    log.info("StaffUser stored in cache ::::::::::::::::");
                    cacheService.putInCacheWithExpire(cacheKey, staffUser);
                    return staffUser;
                }
            }
        } catch (Exception e) {
            log.error("Error while fetching StaffUser: ", e);
        }

        return null;
    }

    public StaffUser getStaffForUpdateAndDelete(Integer id,Integer mvnoId) {
        StaffUser staffUser = get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if (staffUser == null || !(mvnoId == 1 || mvnoId == staffUser.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return staffUser;
    }

    public void changestatus(String status, String username) {
        String newStatus = status;
        StaffUser staffUser = entityRepository.findStaffUserByUsername(username);
        if (staffUser != null) {
            if (!staffUser.getStatus().equals(newStatus)) {
                Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.STAFF_STATUS_CHANGE_TEMPLATE);
                if (optionalTemplate.isPresent()) {
                    if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                        StaffStatusChangeMessage statusMessage = new StaffStatusChangeMessage(RabbitMqConstants.STAFF_STATUS_CHANGE_TEMPLATE_HEADER, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, staffUser, newStatus);
                        statusMessage.setEmailConfigured(true);
                        statusMessage.setSmsConfigured(true);
                        Gson gson = new Gson();
                        gson.toJson(statusMessage);
                        kafkaMessageSender.send(new KafkaMessageData(statusMessage,StaffStatusChangeMessage.class.getSimpleName() ));
//                        messageSender.send(statusMessage, RabbitMqConstants.QUEUE_STAFF_SEND_STATUS);
                    }
                }
            }
        }
    }


    public GenericDataDTO makeGenericResponse(GenericDataDTO genericDataDTO, Page<WorkflowAudit> paginationList) {
        genericDataDTO.setDataList(paginationList.getContent());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    //Get All StaffUserIds By ServiceAreas
    public List<StaffUser> getStaffUserByServiceArea() {
        try {
            QStaffUser qStaffUser = QStaffUser.staffUser;
            QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression aBoolean = qStaffUser.isNotNull().and(qStaffUser.isDelete.eq(false));
            if (getLoggedInUserId() != 1) {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
//                aBoolean = aBoolean.and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId).from(qWareHouseServiceAreaMapping).where(qWareHouseServiceAreaMapping.serviceId.in(serviceIDs))).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff()))).and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId).from(qWareHouseParentServiceAreaMapping).where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.in(serviceIDs))).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff())));
                // TODO: pass mvnoID manually 6/5/2025
                aBoolean = aBoolean.and(qStaffUser.id.in(query.select(qStaffUserServiceAreaMapping.staffId).from(qStaffUserServiceAreaMapping).where(qStaffUserServiceAreaMapping.serviceId.in(serviceAreaIds))).and(qStaffUser.mvnoId.eq(getMvnoIdFromCurrentStaff(null))));
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                return IterableUtils.toList(entityRepository.findAll(aBoolean));
            } else {
                return entityRepository.findAll();
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<StaffUserPojo> getStaffUserByServiceAreaId(Integer serviceAreaId) {
        List<StaffUserPojo> staffUserPojos = new ArrayList<>();
        try {
            QStaffUser qStaffUser = QStaffUser.staffUser;
            QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression aBoolean = qStaffUser.isNotNull().and(qStaffUser.isDelete.eq(false));
            if (getLoggedInUserId() != 1) {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
//                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
//                aBoolean = aBoolean.and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId).from(qWareHouseServiceAreaMapping).where(qWareHouseServiceAreaMapping.serviceId.in(serviceIDs))).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff()))).and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId).from(qWareHouseParentServiceAreaMapping).where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.in(serviceIDs))).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff())));
                // TODO: pass mvnoID manually 6/5/2025
                aBoolean = aBoolean.and(qStaffUser.id.in(query.select(qStaffUserServiceAreaMapping.staffId).from(qStaffUserServiceAreaMapping).where(qStaffUserServiceAreaMapping.serviceId.eq(serviceAreaId))).and(qStaffUser.mvnoId.eq(getMvnoIdFromCurrentStaff(null))));
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                staffUserPojos.addAll(((List<StaffUser>) entityRepository.findAll(aBoolean)).stream().map(staffUser -> staffUserMapper.domainToDTO(staffUser, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
            } else {
                staffUserPojos.addAll(entityRepository.findAll().stream().map(staffUser -> staffUserMapper.domainToDTO(staffUser, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
        return staffUserPojos;
    }

    public List<StaffUserViewPojo> viewStaffUserByServiceArea() {
        List<StaffUser> staffUserList = getStaffUserByServiceArea();
        List<StaffUserViewPojo> staffUserViewPojoList = new ArrayList<>();
        for (StaffUser staffUser : staffUserList) {
            staffUserViewPojoList.add(dtoToViewdto(staffUser));

        }
        return staffUserViewPojoList;

    }

    public StaffUserViewPojo dtoToViewdto(StaffUser staffUser) {
        StaffUserViewPojo staffUserViewPojo = new StaffUserViewPojo();
        staffUserViewPojo.setId(staffUser.getId());
        staffUserViewPojo.setFirstname(staffUser.getFirstname());
        staffUserViewPojo.setLastname(staffUser.getLastname());
        staffUserViewPojo.setUsername(staffUser.getUsername());
        return staffUserViewPojo;

    }

    @Override
    public boolean duplicateVerifyAtSave(String username) {
        boolean flag = false;
        if (username != null) {
            username = username.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(username);
            else
                // TODO: pass mvnoID manually 6/5/2025
                count = entityRepository.duplicateVerifyAtSave(username, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public StaffUserPojo findByStaffId(Integer staffId) throws Exception {
        StaffUser staffUser = getRepository().findById(staffId).orElse(null);
        if (staffUser != null) {
            return convertStaffUserModelToStaffUserPojo(staffUser,staffUser.getMvnoId());
        }
        return null;
    }

    public List<StaffUser> getAllActiveEntitiesStaff() {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findByIsDeleteIsFalseOrderByIdDesc()
                .stream().filter(x -> x.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || x.getMvnoId() == null || x.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1).collect(Collectors.toList());
    }

    public List<StaffUserAllPojo> convertResponseModelIntoStaffUserAllPojo(List<StaffUser> staffUserList) throws Exception {
        String SUBMODULE = MODULE + " [convertResponseModelIntoStaffUserAllPojo()] ";
        List<StaffUserAllPojo> staffUserListRes = new ArrayList<>();
        try {
            if (staffUserList != null && staffUserList.size() > 0) {
                for (StaffUser staffUser : staffUserList) {
                    StaffUserAllPojo pojo = new StaffUserAllPojo();
                    pojo.setId(staffUser.getId());
                    pojo.setUsername(staffUser.getUsername());
                    staffUserListRes.add(pojo);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return staffUserListRes;


    }

    public List<StaffUser> findAllByParentStaffId(Integer parentStaffId) {
        return entityRepository.findAllByParentStaffId(parentStaffId);
    }

    //Shared Data from Common APIGW to CMS
    @Transactional
    public void saveStaffUserEntity(SaveStaffUserSharedDataMessage message) throws Exception {
        try {
            StaffUser staffUser = new StaffUser();
            staffUser.setId(message.getId());
            staffUser.setUsername(message.getUsername());
            staffUser.setPassword(message.getPassword());
            staffUser.setFirstname(message.getFirstname());
            staffUser.setLastname(message.getLastname());
            staffUser.setStatus(message.getStatus());
            staffUser.setEmail(message.getEmail());
            staffUser.setPhone(message.getPhone());
            staffUser.setPartnerid(message.getPartnerid());
            staffUser.setRoles(message.getRoles());
            staffUser.setIsDelete(message.getIsDelete());
            staffUser.setCreatedById(message.getCreatedById());
            staffUser.setLastModifiedById(message.getLastModifiedById());
            if(message.getTeamsList().size()>0){
                for (Teams item : message.getTeamsList()) {
                    TeamUserMapping teamUserMapping = new TeamUserMapping();
                    teamUserMapping.setTeamId(item.getId());
                    teamUserMapping.setStaffId(message.getId().longValue());
                    teamUserMappingsRepocitory.save(teamUserMapping);
                }
            }
            if(!message.getLast_login_time().equalsIgnoreCase("null")) {
                staffUser.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
            } else {
                staffUser.setLast_login_time(null);
            }
            staffUser.setMvnoId(message.getMvnoId());
            staffUser.setBranchId(message.getBranchId());
            staffUser.setServiceAreaNameList(message.getServiceAreaNameList());
            staffUser.setBusinessUnitNameList(message.getBusinessUnitNameList());
            if(message.getParentStaffId()!=null){
                StaffUser parentStaff = entityRepository.findById(message.getParentStaffId()).orElse(null);
                if(parentStaff!=null){
                    staffUser.setStaffUserparent(parentStaff);
                }
            }

            if(message.getDepartmentId() != null){
                staffUser.setDepartment(message.getDepartmentId());
            }

            entityRepository.save(staffUser);
            logger.info("Staff User created successfully with name " + message.getUsername());
        } catch (CustomValidationException e) {
            logger.error("Unable to create staff user with name " + message.getUsername(), e.getMessage());
        }
    }

    @Transactional
    public void updatetaffUserEntity(UpdateStaffUserSharedDataMessage message) throws Exception {
        try {
            StaffUser staffUser = entityRepository.findById(message.getId()).orElse(null);
            if (staffUser != null) {
                staffUser.setId(message.getId());
                staffUser.setUsername(message.getUsername());
                staffUser.setPassword(message.getPassword());
                staffUser.setFirstname(message.getFirstname());
                staffUser.setLastname(message.getLastname());
                staffUser.setStatus(message.getStatus());
                staffUser.setEmail(message.getEmail());
                staffUser.setPhone(message.getPhone());
//                staffUser.setTeam(message.getTeam());
                List<TeamUserMapping> teamUserMappingList =  teamUserMappingsRepocitory.findAllByStaffId(Long.valueOf(message.getId()));
                if (teamUserMappingList.size() != 0) {
                    for (TeamUserMapping teamUserMapping : teamUserMappingList) {
                        teamUserMappingsRepocitory.deleteById(teamUserMapping.getId());
                    }
                }
                if(message.getTeamsList().size()>0){
                    for (Teams item : message.getTeamsList()) {
                        TeamUserMapping teamUserMapping = new TeamUserMapping();
                        teamUserMapping.setTeamId(item.getId());
                        teamUserMapping.setStaffId(message.getId().longValue());
                        teamUserMappingsRepocitory.save(teamUserMapping);
                    }
                }
                staffUser.setCreatedById(message.getCreatedById());
                staffUser.setLastModifiedById(message.getLastModifiedById());
                if (!message.getLast_login_time().equalsIgnoreCase("null")) {
                    staffUser.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
                } else {
                    staffUser.setLast_login_time(null);
                }
                staffUser.setPartnerid(message.getPartnerid());
                staffUser.setRoles(message.getRoles());
                staffUser.setIsDelete(message.getIsDelete());
                staffUser.setMvnoId(message.getMvnoId());
                staffUser.setBranchId(message.getBranchId());
                staffUser.setServiceAreaNameList(message.getServiceAreaNameList());
                staffUser.setBusinessUnitNameList(message.getBusinessUnitNameList());
                if(message.getParentStaffId()!=null){
                    StaffUser parentStaff = entityRepository.findById(message.getParentStaffId()).orElse(null);
                    if(parentStaff!=null){
                        staffUser.setStaffUserparent(parentStaff);
                    }
                }
                if(message.getDepartmentId() != null){
                    staffUser.setDepartment(message.getDepartmentId());
                }

                entityRepository.save(staffUser);
                logger.info("Staff User updated successfully with name " + message.getUsername());
            } else {
                StaffUser staffUser2 = new StaffUser();
                staffUser2.setId(message.getId());
                staffUser2.setUsername(message.getUsername());
                staffUser2.setPassword(message.getPassword());
                staffUser2.setFirstname(message.getFirstname());
                staffUser2.setLastname(message.getLastname());
                staffUser2.setStatus(message.getStatus());
                staffUser2.setEmail(message.getEmail());
                staffUser2.setPhone(message.getPhone());
                if(message.getTeamsList().size()>0){
                    for (Teams item : message.getTeamsList()) {
                        TeamUserMapping teamUserMapping = new TeamUserMapping();
                        teamUserMapping.setTeamId(item.getId());
                        teamUserMapping.setStaffId(message.getId().longValue());
                        teamUserMappingsRepocitory.save(teamUserMapping);
                    }
                }
                if ( !message.getLast_login_time().trim().equalsIgnoreCase("null") && message.getLast_login_time() != null) {
                    staffUser2.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
                } else {
                    staffUser2.setLast_login_time(null);
                }
                staffUser2.setPartnerid(message.getPartnerid());
                staffUser2.setRoles(message.getRoles());
                staffUser2.setCreatedById(message.getCreatedById());
                staffUser2.setLastModifiedById(message.getLastModifiedById());
                staffUser2.setIsDelete(message.getIsDelete());
                staffUser2.setMvnoId(message.getMvnoId());
                staffUser2.setBranchId(message.getBranchId());
                staffUser2.setServiceAreaNameList(message.getServiceAreaNameList());
                staffUser2.setBusinessUnitNameList(message.getBusinessUnitNameList());
                if(message.getParentStaffId()!=null){
                    StaffUser parentStaff = entityRepository.findById(message.getParentStaffId()).orElse(null);
                    if(parentStaff!=null){
                        staffUser.setStaffUserparent(parentStaff);
                    }
                }
                if(message.getDepartmentId() != null){
                    staffUser2.setDepartment(message.getDepartmentId());
                }
                entityRepository.save(staffUser2);
                logger.info("Staff User updated successfully with name " + message.getUsername());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update staff user with name " + message.getUsername(), e.getMessage());
        }
    }

    public Boolean  changeStaffStatusByMvno(List<Integer> mvnoIds, String status) {
        try{
            List<Long> mvnoIdsLong = mvnoIds.stream().map(Long::valueOf).collect(Collectors.toList());
            List<Long> list = entityRepository.findAllStaffIdsByMvnoIds(mvnoIdsLong);
            if(!list.isEmpty()){
                customRepository.updateMvnoStatusForStaff(list,status);
                List<Integer> longList = list.stream().map(aLong -> aLong.intValue()).collect(Collectors.toList());
                MvnoStatusMessage mvnoStatusMessage = new MvnoStatusMessage(longList,status,true);
//                messageSender.send(mvnoStatusMessage, RabbitMqConstants.QUEUE_SEND_STAFF_STATUS_DUNNING_MESSAGE);
                kafkaMessageSender.send(new KafkaMessageData(mvnoStatusMessage,MvnoStatusMessage.class.getSimpleName(),"STAFF_STATUS_DUNNING"));
                return true;

            }else {
                return false;
            }
        }catch (Exception e){
            throw new CustomValidationException(417,"Error occured during deactivating staff"+e.getMessage(),null);
        }

    }

    public Boolean  changeStaffStatusActiveByMvno(Integer mvnoId, String status) {
        try{
            List<Integer> list = entityRepository.findStaffidByMvnoDeativationFlag(mvnoId);
            if(!list.isEmpty()){
                customRepository.updateMvnoStatusForStaff(list,status);
                return true;

            }else {
                return false;
            }
        }catch (Exception e){
            throw new CustomValidationException(417,"Error occured during deactivating staff"+e.getMessage(),null);
        }

    }

    public String generateToken(String username , String password){
        StaffUser staffUser = entityRepository.findStaffUserByUsername(username);
        Long mvnoId = staffUser.getMvnoId().longValue();
        String MVNO_NAME = mvnoRepository.findMvnoNameById(mvnoId);
        List<GrantedAuthority> role_name=new ArrayList<>();
        role_name.add(new SimpleGrantedAuthority("ADMIN"));
        LoggedInUser user = new LoggedInUser(username, password, true, true, true, true, role_name, MVNO_NAME, MVNO_NAME, LocalDateTime.now(), staffUser.getId(), staffUser.getPartnerid(), "ADMIN", null, staffUser.getMvnoId(), null, staffUser.getId(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),MVNO_NAME,null,null,null);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return GenerateTokenUsingLoggedInUser(user);
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
                .setExpiration(new Date(System.currentTimeMillis() + 864_000_000))
                .signWith(hmacKey)
                .compact();
        token =  "Bearer " + " " +token;
        return token;
    }

    public List<Integer>getStaffIds(Integer loggedInUserId){
        try{
            List<Integer> staffIds = entityRepository.findAllByParentStaffIds(loggedInUserId);
            staffIds.add(loggedInUserId);
            return  staffIds;
        }catch (Exception e){
            ApplicationLogger.logger.error("something went wrong while fetching staffids to display leadmaster");
        }
        return null;
    }



    public StaffLedgerDetailsDto getWalletDetail(Integer id, Integer mvnoId) {
        StaffLedgerDetailsDto dto=new StaffLedgerDetailsDto();
        try {
             dto = staffLedgerDetailsRepository.getStaffLedgerSummary(id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while fetching StaffUser Wallet Detail: ", e);
        }
        return dto;
    }

    public List<StaffUser> getByTeamIdAndServiceAreaId(Long teamId,Long serviceAreaId) {
        List<StaffUser> staffUserList=staffUserRepository.getDistinctStaffByServiceAreaAndTeamId(Arrays.asList(serviceAreaId),teamId);
        return staffUserList;
    }

}
