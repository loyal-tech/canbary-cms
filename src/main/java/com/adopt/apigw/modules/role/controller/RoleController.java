package com.adopt.apigw.modules.role.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.modules.role.domain.Role;
import com.adopt.apigw.modules.role.mapper.RoleMapper;
import com.adopt.apigw.repository.common.StaffRolRelRepo;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.role.model.RoleDTO;
import com.adopt.apigw.modules.role.service.RoleService;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.RoleMessage;

import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.ROLE)
public class RoleController extends ExBaseAbstractController<RoleDTO> {

    @Autowired
    AuditLogService auditLogService;
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private MessageSender messageSender;

    @Autowired
    private StaffRolRelRepo staffRoleRelRepo;
    @Autowired
    CreateDataSharedService createDataSharedService;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    public RoleController(RoleService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[RoleController]";
    }
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, @RequestParam Integer mvnoId) {
        return super.getAll(requestDTO, req,mvnoId);
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = super.getEntityById(id, req,mvnoId);
        RoleDTO role = (RoleDTO) genericDataDTO.getData();
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
                AclConstants.OPERATION_ROLE_VIEW, req.getRemoteAddr(), null, role.getId().longValue(), role.getRolename());
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_ADD + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody RoleDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        // TODO: pass mvnoID manually 6/5/2025
        if(getMvnoIdFromCurrentStaff(null) != null) {
            // TODO: pass mvnoID manually 6/5/2025
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff(null));
        }

        if(getLoggedInUser().getLco())
            entityDTO.setLcoId(getLoggedInUser().getPartnerId());
        else
            entityDTO.setLcoId(null);

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        boolean flag = roleService.duplicateVerifyAtSave(entityDTO.getRolename());
        if (flag) {
            genericDataDTO = super.save(entityDTO, result, authentication, req,mvnoId);
            RoleDTO role = (RoleDTO) genericDataDTO.getData();
            //send message
            RoleMessage roleMessage = new RoleMessage();
            if(role != null) {
                roleMessage.setId(role.getId());
                roleMessage.setRolename(role.getRolename());
                roleMessage.setStatus(role.getStatus());
                roleMessage.setIsDelete(role.getIsDelete());
                roleMessage.setSysRole(role.getSysRole());
                roleMessage.setMvnoId(role.getMvnoId());
                roleMessage.setAclEntryDTOList(role.getAclEntryPojoList());
                kafkaMessageSender.send(new KafkaMessageData(roleMessage,RoleMessage.class.getSimpleName() ));
//                messageSender.send(roleMessage, RabbitMqConstants.QUEUE_ROLE);
                Role roleEntity = roleService.convertRolePojoToRoleModel(role);
                createDataSharedService.sendEntitySaveDataForAllMicroService(roleEntity);
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
                        AclConstants.OPERATION_ROLE_VIEW, req.getRemoteAddr(), null, role.getId(), role.getRolename());
            }

        } else {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(MessageConstants.ROLE_NAME_EXITS);
            logger.error("Unable to Create  Role With name "+entityDTO.getRolename()+"    :  request: { From : {}, Request Url : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        }

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody RoleDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) != null) {
                // TODO: pass mvnoID manually 6/5/2025
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff(null));
            }

            if(getLoggedInUser().getLco())
                entityDTO.setLcoId(getLoggedInUser().getPartnerId());
            else
                entityDTO.setLcoId(null);

            boolean flag = roleService.duplicateVerifyAtEdit(entityDTO.getRolename(), entityDTO.getId());
            if (flag) {
                Integer staffRoleCount = staffRoleRelRepo.findByRoleId(entityDTO.getId()).size();
                if(staffRoleCount == 0 || (staffRoleCount > 0 && entityDTO.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS))) {
                    genericDataDTO = super.update(entityDTO, result, authentication, req,mvnoId);
                    if (genericDataDTO.getResponseCode() != 200) {
                        return genericDataDTO;
                    }
                    RoleDTO role = (RoleDTO) genericDataDTO.getData();
                    //send message
                    RoleMessage roleMessage = new RoleMessage();
                    roleMessage.setId(role.getId());
                    roleMessage.setRolename(role.getRolename());
                    roleMessage.setStatus(role.getStatus());
                    roleMessage.setIsDelete(role.getIsDelete());
                    roleMessage.setSysRole(role.getSysRole());
                    roleMessage.setMvnoId(role.getMvnoId());
                    roleMessage.setAclEntryDTOList(role.getAclEntryPojoList());
//                    messageSender.send(roleMessage, RabbitMqConstants.QUEUE_ROLE);
                    kafkaMessageSender.send(new KafkaMessageData(roleMessage,RoleMessage.class.getSimpleName() ));
                    Role roleEntity = roleService.convertRolePojoToRoleModel(role);
                    createDataSharedService.updateEntityDataForAllMicroService(roleEntity);
                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
                            AclConstants.OPERATION_ROLE_EDIT, req.getRemoteAddr(), null, entityDTO.getId().longValue(), entityDTO.getRolename());
                } else {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage(MessageConstants.ROLE_IN_USE);
                }
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.ROLE_NAME_EXITS);
                logger.error("Unable to Update Role With "+roleService.getEntityById(entityDTO.getId(),entityDTO.getMvnoId())+" to "+entityDTO.getRolename()+"   Role With name "+entityDTO.getRolename()+"    :  request: { From : {}, Request Url : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        
        
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody RoleDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = super.delete(entityDTO, authentication, req);
        RoleDTO role = (RoleDTO) genericDataDTO.getData();
        //send message
        RoleMessage roleMessage = new RoleMessage();
        roleMessage.setId(role.getId());
        roleMessage.setRolename(role.getRolename());
        roleMessage.setStatus(role.getStatus());
        roleMessage.setIsDelete(true);
        roleMessage.setSysRole(role.getSysRole());
        roleMessage.setMvnoId(role.getMvnoId());
        roleMessage.setAclEntryDTOList(role.getAclEntryPojoList());
//        messageSender.send(roleMessage, RabbitMqConstants.QUEUE_ROLE);
        kafkaMessageSender.send(new KafkaMessageData(roleMessage,RoleMessage.class.getSimpleName() ));
        Role roleEntity = roleService.convertRolePojoToRoleModel(role);
        createDataSharedService.deleteEntityDataForAllMicroService(roleEntity);
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
                AclConstants.OPERATION_ROLE_DELETE, req.getRemoteAddr(), null, entityDTO.getId().longValue(), entityDTO.getRolename());
        return genericDataDTO;

    }

    @Override
    public GenericDataDTO getAllWithoutPagination(@RequestParam Integer mvnoId) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<RoleDTO> list = roleService.getAllEntities(mvnoId);
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());

        }

        return genericDataDTO;
    }

//   // @Deprecated
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(Integer page, Integer pageSize, Integer sortOrder, String sortBy, GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }
    
  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
  @PostMapping("/searchrole")
  public GenericDataDTO search(@RequestBody PaginationRequestDTO requestDTO , HttpServletRequest req, @RequestParam Integer mvnoId) {
            GenericSearchDTO genericSearchDTO = new GenericSearchDTO();
            genericSearchDTO.setFilter(requestDTO.getFilters());
            return super.search(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortOrder(), requestDTO.getSortBy(), genericSearchDTO , req,mvnoId);
  }
    
    


//    @GetMapping("/role")
//    public GenericDataDTO getAllActiveRole() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            List<RoleDTO> roleList = roleService.convertResponseModelIntoPojo(roleService.getAllActiveEntities());
//            genericDataDTO = new GenericDataDTO();
//            genericDataDTO.setDataList(roleList);
//            genericDataDTO.setTotalRecords(roleList.size());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }

//    @PostMapping("/save")
//    public GenericDataDTO createRole(@Valid @RequestBody RoleDTO pojo) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            roleService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
//            pojo = roleService.saveEntity(pojo);
//            genericDataDTO = new GenericDataDTO();
//            genericDataDTO.setData(pojo);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }

//    @GetMapping("/role/{id}")
//    public GenericDataDTO getRole(@PathVariable Integer id) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            RoleDTO pojo = roleService.convertRoleModelToRolePojo(roleService.get(id));
//            genericDataDTO = new GenericDataDTO();
//            genericDataDTO.setData(pojo);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//            ;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }
////
//    @PostMapping("/update")
//    public GenericDataDTO updateRole(@Valid @RequestBody RoleDTO pojo) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            roleService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
//            pojo = roleService.updateEntity(pojo);
//            genericDataDTO.setData(pojo);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }
//
//    @PostMapping("/delete")
//    public GenericDataDTO deleteRole(@RequestBody RoleDTO pojo) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            roleService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
//            roleService.deleteEntity(pojo);
//            genericDataDTO.setData(pojo);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }
//
//
//    @GetMapping(value = "/role/excel")
//    public void roleExcel(HttpServletResponse response) throws Exception {
//        RoleService service = SpringContext.getBean(RoleService.class);
//        exportToExcel(service, response);
//    }
//
//    @GetMapping(value = "/role/pdf")
//    public void rolePDF(HttpServletResponse response) throws Exception {
//        RoleService service = SpringContext.getBean(RoleService.class);
//        exportToPDF(service, response);
//    }
//
    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }
}
