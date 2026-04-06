package com.adopt.apigw.mapper.postpaid;

import com.adopt.apigw.modules.role.repository.RoleRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.service.common.StaffUserService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.Teams.domain.Teams;
import com.adopt.apigw.modules.Teams.mapper.TeamsMapper;
import com.adopt.apigw.modules.Teams.model.TeamsDTO;
import com.adopt.apigw.modules.Teams.service.TeamsService;
import com.adopt.apigw.modules.role.domain.Role;
import com.adopt.apigw.modules.role.model.RoleDTO;
import com.adopt.apigw.modules.role.service.RoleService;
import com.adopt.apigw.pojo.api.StaffUserPojo;
import com.adopt.apigw.service.postpaid.PartnerService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = TeamsMapper.class)
public abstract class StaffUserMapper implements IBaseMapper<StaffUserPojo, StaffUser> {

    private String MODULE = " [StaffUserMapper] ";

    @Autowired
    private PartnerService partnerService;
    @Autowired
    private TeamsService teamsService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TeamsMapper teamsMapper;
    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private StaffUserRepository staffUserRepository;


    @Override
    @Mapping(source = "staffUser.roles", target = "roleIds")
    @Mapping(source = "staffUser.createdate", target = "regDate", dateFormat = "dd/MM/yyyy HH:mm a")
    @Mapping(source = "staffUser.updatedate", target = "updatedatestring", dateFormat = "dd/MM/yyyy HH:mm a")
    @Mapping(source = "staffUser.staffUserparent.id", target = "parentStaffId")
    //@Mapping(source = "staffUser.team", target = "teamIds")
    @Mapping(target = "displayId", source = "staffUser.id")
    @Mapping(target = "displayName", source = "staffUser.username")
    public abstract StaffUserPojo domainToDTO(StaffUser staffUser, CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "dtoData.roleIds", target = "roles")
    @Mapping(source = "dtoData.parentStaffId", target = "staffUserparent")
    //@Mapping(source = "dtoData.teamIds", target = "team")
    public abstract StaffUser dtoToDomain(StaffUserPojo dtoData, CycleAvoidingMappingContext context);

    public abstract java.util.Set<Role> mapRoleIdsToRole(List<Integer> value);

    public abstract java.util.List<Integer> mapRolesToRoleIds(Set<Role> value);

    Integer fromRoleToId(Role entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    Role fromIdToRole(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Role entity;
        try {
//            RoleDTO entityDTO = roleService.getEntityById(entityId.longValue());
            entity = roleRepository.findById(entityId.longValue()).get();
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    StaffUser fromParentStaffIdToStaffUserparent(Integer entityId){
        if (entityId == null) {
            return null;
        }
        StaffUser entity = new StaffUser();
        try{
            entity = staffUserRepository.findById(entityId).get();
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

  /*  Long fromTeamToId(Teams entity) {
        return entity == null ? null : entity.getId();
    }

    Teams fromIdToTeam(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Teams entity;
        try {
            TeamsDTO entityDTO = teamsService.getEntityById(entityId.longValue());
            entity = teamsMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }*/

    @AfterMapping
    void afterMapping(@MappingTarget StaffUserPojo staffUserPojo, StaffUser staffUser) {
        try {

            if (null != staffUser.getPartnerid()) {
                Partner partner = partnerService.get(staffUser.getPartnerid(), staffUserPojo.getMvnoId());
                staffUserPojo.setPartnerName(null != partner && null != partner.getName() ? partner.getName() : "-");
            } else {
                staffUserPojo.setPartnerName("-");
            }

            if (null != staffUser.getRoles() && 0 < staffUser.getRoles().size()) {
                staffUserPojo.setRoleName(staffUser.getRoles().stream().map(Role::getRolename).collect(Collectors.toList()));
            } else {
                staffUserPojo.setRoleName(new ArrayList<>());
            }

         /*   if (null != staffUser.getTeam() && 0 < staffUser.getTeam().size()) {
                staffUserPojo.setTeamNameList(staffUser.getTeam().stream().map(Teams::getName).collect(Collectors.toList()));
            } else
                staffUserPojo.setTeamNameList(Arrays.asList("-"));*/

        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
}
