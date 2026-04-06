package com.adopt.apigw.modules.role.service;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveRoleSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateRoleSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.acl.domain.CustomACLEntry;
import com.adopt.apigw.modules.acl.model.CustomACLEntryDTO;
import com.adopt.apigw.modules.role.domain.QRole;
import com.adopt.apigw.modules.role.domain.Role;
import com.adopt.apigw.modules.role.domain.RoleACLEntry;
import com.adopt.apigw.modules.role.mapper.RoleMapper;
import com.adopt.apigw.modules.role.model.RoleACLEntryDTO;
import com.adopt.apigw.modules.role.model.RoleDTO;
import com.adopt.apigw.modules.role.repository.RoleAclRepository;
import com.adopt.apigw.modules.role.repository.RoleRepository;
import com.adopt.apigw.rabbitMq.message.CommonRoleMessage;
import com.adopt.apigw.service.radius.CustomACLService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService extends ExBaseAbstractService<RoleDTO, Role, Long> {
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private RoleRepository entityRepository;

    @Autowired
    RoleAclRepository roleAclRepository;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private CustomACLService customACLService;
    @Autowired
    CreateDataSharedService createDataSharedService;

    public RoleService(RoleRepository repository, RoleMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[RoleService]";
    }

    @Override
    public List<RoleDTO> getAllEntities(Integer mvnoId) throws Exception {
        List<RoleDTO> roleDTOS = new ArrayList<>();
        //if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
        roleDTOS = convertResponseModelIntoPojo(entityRepository.findByStatus(CommonConstants.ACTIVE_STATUS));
        //} else {
        //    roleDTOS = convertResponseModelIntoPojo(entityRepository.findByStatusAndIdIn(CommonConstants.ACTIVE_STATUS, CommonUtils.getPartnerStaffRoleIdList().stream().map(Long::valueOf).collect(Collectors.toList())));
        //}
        if(getLoggedInUser().getLco())
            // TODO: pass mvnoID manually 6/5/2025
            return roleDTOS.stream().filter(data->data.getLcoId()!=null && data.getLcoId()==getLoggedInUser().getPartnerId()).filter(roleDTO -> roleDTO.getMvnoId() == 1 || mvnoId == 1 || roleDTO.getMvnoId() == mvnoId).collect(Collectors.toList());
        else
            // TODO: pass mvnoID manually 6/5/2025
            return roleDTOS.stream().filter(data->data.getLcoId()==null).filter(roleDTO -> roleDTO.getMvnoId() == 1 || mvnoId == 1 || roleDTO.getMvnoId() == mvnoId).collect(Collectors.toList());
    }

    public List<RoleDTO> getAllByIdIn(List<Long> idList,Integer mvnoId) throws Exception {
        List<RoleDTO> roleDTOS = new ArrayList<>();
        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
            roleDTOS = convertResponseModelIntoPojo(entityRepository.findAllById(idList));
        } else {
            roleDTOS = convertResponseModelIntoPojo(entityRepository.findAllById(UtilsCommon.getPartnerStaffRoleIdList().stream().map(Long::valueOf).collect(Collectors.toList())));
        }
        // TODO: pass mvnoID manually 6/5/2025
        return roleDTOS.stream().filter(roleDTO -> roleDTO.getMvnoId() == 1 || mvnoId == 1 || roleDTO.getMvnoId() == mvnoId).collect(Collectors.toList());
    }
    public List<RoleDTO> getAllByIdInForCWSC(List<Long> idList, Integer mvnoId, Integer partnerId) throws Exception {
        List<RoleDTO> roleDTOS = new ArrayList<>();
        if (partnerId.equals(CommonConstants.DEFAULT_PARTNER_ID)) {
            roleDTOS = convertResponseModelIntoPojo(entityRepository.findAllById(idList));
        } else {
            roleDTOS = convertResponseModelIntoPojo(entityRepository.findAllById(UtilsCommon.getPartnerStaffRoleIdList().stream().map(Long::valueOf).collect(Collectors.toList())));
        }
        return roleDTOS.stream().filter(roleDTO -> roleDTO.getMvnoId() == 1 || mvnoId == 1 || roleDTO.getMvnoId() == mvnoId).collect(Collectors.toList());
    }



    public GenericDataDTO getListByPagination(PageRequest pageRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Role> paginationList = getRepository().findAll(pageRequest);
        if (null != paginationList && 0 < paginationList.getSize()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    @Override
    public RoleDTO getEntityById(Long aLong,Integer mvnoId) throws Exception {
        return convertRoleModelToRolePojo(entityRepository.findById(aLong).get());
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_ADD + "\")")
    @Override
    public RoleDTO saveEntity(RoleDTO pojo) {
        Role obj = convertRolePojoToRoleModel(pojo);
//        obj = saveRole(obj);
        pojo = convertRoleModelToRolePojo(obj);
        return pojo;
    }




    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_EDIT + "\")")
    @Override
    public RoleDTO updateEntity(RoleDTO entity) throws Exception {
        return saveEntity(entity);
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_DELETE + "\")")
    @Override
    public void deleteEntity(RoleDTO entity) throws Exception {
        // TODO: pass mvnoID manually 6/5/2025
        if(entity == null || !(getMvnoIdFromCurrentStaff(null) == 1 || getMvnoIdFromCurrentStaff(null).intValue() == entity.getMvnoId()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        entity.setIsDelete(true);
        entityRepository.save(convertRolePojoToRoleModel(entity));
    }

    public Role convertRolePojoToRoleModel(RoleDTO roleDTO) {
        Role role = null;
        if (roleDTO != null) {
            role = new Role();
            if (roleDTO.getId() != null) {
                role.setId(roleDTO.getId());
            }
            role.setRolename(roleDTO.getRolename());
            role.setStatus(roleDTO.getStatus());
            role.setCreatedate(roleDTO.getCreatedate());
//            role.setAclEntry(null);
            role.setIsDelete(roleDTO.getDelete());
            role.setSysRole(roleDTO.getSysRole());
            if (roleDTO.getMvnoId() != null) {
                role.setMvnoId(roleDTO.getMvnoId());
            }
            if (roleDTO.getAclEntryPojoList() != null && roleDTO.getAclEntryPojoList().size() > 0) {
                List<CustomACLEntry> aclEntryList = new ArrayList<>();
                CustomACLEntry aclEntry = null;
                for (CustomACLEntryDTO aclEntryPojo : roleDTO.getAclEntryPojoList()) {
                    aclEntry = new CustomACLEntry();
                    if (aclEntryPojo.getId() != null) {
                        aclEntry.setId(aclEntryPojo.getId());
                    }
                    aclEntry.setClassid(aclEntryPojo.getClassid());
                    aclEntry.setPermit(aclEntryPojo.getPermit());
                    if (aclEntryPojo.getRoleId() != null) {
                        //aclEntry.setRole(entityRepository.getOne(aclEntryPojo.getRoleId()));
                    }
                    aclEntryList.add(aclEntry);
                }
                role.setLcoId(roleDTO.getLcoId());
//                role.setAclEntry(aclEntryList);
            }
        }
        return role;
    }

    public RoleDTO convertRoleModelToRolePojo(Role role) {
        RoleDTO pojo = null;
        if (role != null) {
            pojo = new RoleDTO();
            pojo.setId(role.getId());
            pojo.setRolename(role.getRolename());
            pojo.setStatus(role.getStatus());
            pojo.setCreatedate(role.getCreatedate());
            pojo.setDelete(role.getIsDelete());
            pojo.setSysRole(role.getSysRole());
            pojo.setCreatedate(role.getCreatedate());
            pojo.setCreatedById(role.getCreatedById());
            pojo.setCreatedByName(role.getCreatedByName());
            pojo.setLastModifiedById(role.getLastModifiedById());
            pojo.setUpdatedate(role.getUpdatedate());
            pojo.setLastModifiedByName(role.getLastModifiedByName());
            if (role.getMvnoId() != null) {
                pojo.setMvnoId(role.getMvnoId());
            }
//            if (role.getAclEntry() != null && role.getAclEntry().size() > 0) {
//                List<CustomACLEntryDTO> custAclList = new ArrayList<CustomACLEntryDTO>();
//                CustomACLEntryDTO customACLEntryDTO = null;
////                for (CustomACLEntry customACLEntry : role.getAclEntry()) {
//                    customACLEntryDTO = new CustomACLEntryDTO();
//                    if (customACLEntry.getId() != null) {
//                        customACLEntryDTO.setId(customACLEntry.getId());
//                    }
//                    customACLEntryDTO.setClassid(customACLEntry.getClassid());
//                    customACLEntryDTO.setPermit(customACLEntry.getPermit());
//                    if (customACLEntry.getRole() != null) {
//                        customACLEntryDTO.setRoleId(customACLEntry.getRole().getId().intValue());
//                    }
//                    custAclList.add(customACLEntryDTO);
//                }
//                pojo.setLcoId(role.getLcoId());
//                pojo.setAclEntryPojoList(custAclList);
//            }
        }
        return pojo;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
    public List<RoleDTO> convertResponseModelIntoPojo(List<Role> roleList) {
        List<RoleDTO> pojoListRes = new ArrayList<>();
        if (roleList != null && roleList.size() > 0) {
            for (Role role : roleList) {
                if (role.getIsDelete() == false) pojoListRes.add(convertRoleModelToRolePojo(role));
                else continue;
            }
        }
        return pojoListRes;
    }

    public void validateRequest(RoleDTO pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
            if (pojo.getId() != null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
            }
        }
        if (!(pojo.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS) || pojo.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Role");
        createExcel(workbook, sheet, RoleDTO.class, null,mvnoId);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, RoleDTO.class, null,mvnoId);
    }

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    public int getLoggedInUserPartnerId() {
        int partnerId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
            }
        } catch (Exception e) {
            partnerId = -1;
        }
        return partnerId;
    }

    public GenericDataDTO getRoleByName(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getRoleByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QRole qRole = QRole.role;
            BooleanExpression booleanExpression = qRole.isNotNull().and(qRole.isDelete.eq(false)).and(qRole.rolename.likeIgnoreCase("%" + name + "%"));
            if(getLoggedInUser().getLco())
                booleanExpression=booleanExpression.and(qRole.lcoId.eq(getLoggedInUser().getPartnerId()));
            else
                booleanExpression=booleanExpression.and(qRole.lcoId.isNull());

            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qRole.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
               // booleanExpression = booleanExpression.or(qRole.mvnoId.eq(1));
            }
            Page<Role> roleList = entityRepository.findAll(booleanExpression, pageRequest);
            if (0 < roleList.getSize()) {
                makeGenericResponse(genericDataDTO, roleList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public GenericDataDTO makeGenericResponse(GenericDataDTO genericDataDTO, Page<Role> paginationList) {
        genericDataDTO.setDataList(paginationList.getContent().stream().map(this::convertRoleModelToRolePojo).collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "rolename", sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getRoleByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }
    
	@Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        if (name != null) {
        	name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
                // TODO: pass mvnoID manually 6/5/2025
            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }
    
    
    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
        boolean flag = false;
        if (name != null) {
        	name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
                // TODO: pass mvnoID manually 6/5/2025
            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                    // TODO: pass mvnoID manually 6/5/2025
                else countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Role> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        QRole qRole=QRole.role;
        BooleanExpression expression=qRole.isNotNull();
        if(getLoggedInUser().getLco())
            expression=expression.and(qRole.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            expression=expression.and(qRole.lcoId.isNull());
            expression=expression.and(qRole.isDelete.eq(false));

        // TODO: pass mvnoID manually 6/5/2025
        if(getMvnoIdFromCurrentStaff(null) == 1)
            paginationList = entityRepository.findAll(expression,pageRequest);
        else {
            // TODO: pass mvnoID manually 6/5/2025
            expression=expression.and(qRole.mvnoId.in(1,getMvnoIdFromCurrentStaff(null)));
            paginationList = entityRepository.findAll(expression,pageRequest);
        }
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    // Shared Data From Common APIGW to CMS
    public void saveRoleEntity(SaveRoleSharedDataMessage message) throws Exception {
        try {
            Role role = new Role();
            List<CustomACLEntry> customACLEntryList = new ArrayList<>();
            role.setId(message.getId());
            role.setRolename(message.getRolename());
            role.setStatus(message.getStatus());
            role.setSysRole(message.getSysRole());
            role.setCreatedById(message.getCreatedById());
            role.setLastModifiedById(message.getLastModifiedById());
            for (CustomACLEntry item : message.getAclEntry()) {
                CustomACLEntry customACLEntry = new CustomACLEntry();
                customACLEntry.setId(item.getId());
                customACLEntry.setClassid(item.getClassid());
//                customACLEntry.setRole(item.getRole());
                customACLEntry.setPermit(item.getPermit());
                customACLEntryList.add(customACLEntry);
            }
//            role.setAclEntry(customACLEntryList);
            role.setIsDelete(message.getIsDelete());
            role.setMvnoId(message.getMvnoId());
            role.setLcoId(message.getLcoId());
            entityRepository.save(role);
            logger.info("Role created successfully with name " + message.getRolename());
        } catch (CustomValidationException e) {
            logger.error("Unable to create role with name " + message.getRolename(), e.getMessage());
        }
    }

    public void updateRoleEntity(UpdateRoleSharedDataMessage message) throws Exception {
        try {
            Role role = entityRepository.findById(message.getId()).orElse(null);
            if (role != null) {
                List<CustomACLEntry> customACLEntryList = new ArrayList<>();
                role.setId(message.getId());
                role.setRolename(message.getRolename());
                role.setStatus(message.getStatus());
                role.setSysRole(message.getSysRole());
                role.setCreatedById(message.getCreatedById());
                role.setLastModifiedById(message.getLastModifiedById());
                for (CustomACLEntry item : message.getAclEntry()) {
                    CustomACLEntry customACLEntry = new CustomACLEntry();
                    customACLEntry.setId(item.getId());
                    customACLEntry.setClassid(item.getClassid());
//                    if (message.getIsDelete().equals(false)) {
//                        customACLEntry.setRole(item.getRole());
//                    } else if (message.getIsDelete().equals(true)) {
//                        customACLEntry.setRole(null);
//                    }
                    customACLEntry.setPermit(item.getPermit());
                    customACLEntryList.add(customACLEntry);
                }
//                role.setAclEntry(customACLEntryList);
                role.setIsDelete(message.getIsDelete());
                role.setMvnoId(message.getMvnoId());
                role.setLcoId(message.getLcoId());
                entityRepository.save(role);
                logger.info("Role updated successfully with name " + message.getRolename());
            } else {
                Role role1 = new Role();
                List<CustomACLEntry> customACLEntryList = new ArrayList<>();
                role1.setId(message.getId());
                role1.setRolename(message.getRolename());
                role1.setStatus(message.getStatus());
                role1.setSysRole(message.getSysRole());
                role1.setCreatedById(message.getCreatedById());
                role1.setLastModifiedById(message.getLastModifiedById());
                for (CustomACLEntry item : message.getAclEntry()) {
                    CustomACLEntry customACLEntry = new CustomACLEntry();
                    customACLEntry.setId(item.getId());
                    customACLEntry.setClassid(item.getClassid());
                    if (message.getIsDelete().equals(false)) {
//                        customACLEntry.setRole(item.getRole());
                    } else if (message.getIsDelete().equals(true)) {
//                        customACLEntry.setRole(null);
                    }
                    customACLEntry.setPermit(item.getPermit());
                    customACLEntryList.add(customACLEntry);
                }
//                role1.setAclEntry(customACLEntryList);
                role1.setIsDelete(message.getIsDelete());
                role1.setMvnoId(message.getMvnoId());
                role1.setLcoId(message.getLcoId());
                entityRepository.save(role1);
                logger.info("Role updated successfully with name " + message.getRolename());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update role with name " + message.getRolename(), e.getMessage());
        }
    }

    public void saveRole(CommonRoleMessage message) {
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

            Role roles= null;
            Optional<Role> role = entityRepository.findById(message.getId());
            if (role.isPresent()){
                roles = role.get();
                List<RoleACLEntry> roleACLEntries = roleAclRepository.findAllByRoleId(role.get().getId());
                roleAclRepository.deleteAll(roleACLEntries);
            }else {
                roles = new Role();
                roles.setCreatedate(LocalDateTime.now());

            }
            roles.setId(message.getId());
            roles.setSysRole(message.getSysRole());
            roles.setRolename(message.getRolename());
            roles.setUpdatedate(LocalDateTime.now());
            List<RoleACLEntry> aclEntryList = new ArrayList<>();
            if (message.getAclMenus()!=null && message.getAclMenus().size()>0) {
                for (RoleACLEntryDTO item : message.getAclMenus()) {
                    RoleACLEntry roleACLEntry = new RoleACLEntry(roles,item.getCode(), item.getMenuid(),item.getId());
                    aclEntryList.add(roleACLEntry);
                }
            }
//            roles.setRoleAclEntry(aclEntryList);
            roles.setIsDelete(message.getIsDelete());
            roles.setLcoId(message.getLcoId());
            roles.setMvnoId(message.getMvnoId());
            roles.setCreatedById(message.getCreatedById());
            roles.setCreatedByName(message.getCreatedByName());
            roles.setLastModifiedByName(message.getLastModifiedByName());
            roles.setLastModifiedById(message.getLastModifiedById());
            roles.setStatus(message.getStatus());
            entityRepository.save(roles);
            if(!CollectionUtils.isEmpty(aclEntryList))
                roleAclRepository.saveAll(aclEntryList);
//            customACLService.reloadCache();
            customACLService.updateCache(roles.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRole(CommonRoleMessage message) {
        try{
            Optional<Role> role = entityRepository.findById(message.getId());
            if (role.isPresent()){
                role.get().setIsDelete(true);
                List<RoleACLEntry> roleACLEntries = roleAclRepository.findAllByRoleId(role.get().getId());
                roleAclRepository.deleteAll(roleACLEntries);
                entityRepository.save(role.get());
//               customACLService.reloadCache();
                customACLService.updateCache(role.get().getId());

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
