package com.adopt.apigw.modules.Teams.mapper;

import java.util.Collections;
import java.util.stream.Collectors;

import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.PartnerRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
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
import com.adopt.apigw.modules.Teams.model.TeamsDTO;
import com.adopt.apigw.modules.Teams.service.TeamsService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.postpaid.PartnerService;


@Mapper
public abstract class TeamsMapper implements IBaseMapper<TeamsDTO, Teams> {

    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private PartnerService partnerService;
    
    @Autowired
    private TeamsService teamsService;
    @Autowired
    private StaffUserRepository staffUserRepository;
    @Autowired
    private PartnerRepository partnerRepository;

    @Override
    @Mapping(source = "teams.staffUser", target = "staffUserIds")
    @Mapping(source = "teams.partner", target = "partnerid")
    @Mapping(source = "teams.parentTeams", target = "parentteamid")
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "name")
    public abstract TeamsDTO domainToDTO(Teams teams, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "dtoData.staffUserIds", target = "staffUser")
    @Mapping(source = "dtoData.partnerid", target = "partner")
    @Mapping(source = "dtoData.parentteamid", target = "parentTeams")
    public abstract Teams dtoToDomain(TeamsDTO dtoData, @Context CycleAvoidingMappingContext context);
    
    Long fromParentTeamsToId(Teams parentTeams) {
        return parentTeams == null ? null : parentTeams.getId();
    }
    
    Teams fromIdToParentTeams(Long entityId) {
        if (entityId == null) {
            return null;
        }
        Teams entity;
        try {
            entity = teamsService.getById(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    Integer fromStaffToId(StaffUser entity) {
        return entity == null ? null : entity.getId();
    }

    StaffUser fromIdToStaff(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        StaffUser entity;
        try {
            entity = staffUserRepository.findById(entityId).get();
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    Integer frommPartnerToId(Partner entity) {
        return entity == null ? null : entity.getId();
    }

    Partner fromIdToPartner(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Partner entity;
        try {
            entity = partnerRepository.findById(entityId).get();
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @AfterMapping
    void afterMapping(@MappingTarget TeamsDTO teamsDTO, Teams teams) {
        try {
            if (null != teams.getStaffUser() && 0 < teams.getStaffUser().size()) {
                teamsDTO.setStaffNameList(teams.getStaffUser().stream().map(StaffUser::getFullName).collect(Collectors.toList()));
            } else {
                teamsDTO.setStaffNameList(Collections.singletonList("-"));
            }
            if (null != teams.getPartner()) {
                teamsDTO.setPartnername(teams.getPartner().getName());
            } else {
                teamsDTO.setPartnername("-");
            }
            if(teams.getParentTeams() != null) {
            	teamsDTO.setParentteamid(teams.getParentTeams().getId());
            	teamsDTO.setParentTeamName(teams.getParentTeams().getName());
            }else {
            	teamsDTO.setParentTeamName("-");
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error("Teams Mapper" + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
}
