package com.adopt.apigw.modules.Branch.mapper;

import java.util.Collections;
import java.util.stream.Collectors;

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
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.Branch.model.BranchDTO;
import com.adopt.apigw.modules.Branch.service.BranchService;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.Teams.domain.Teams;
import com.adopt.apigw.modules.Teams.model.TeamsDTO;

@Mapper
public abstract class BranchMapper implements IBaseMapper<BranchDTO, Branch> {
	
	@Autowired
    private BranchService branchService;
	
	@Autowired
    private ServiceAreaService serviceAreaService;
	
	@Override
    @Mapping(source = "branch.serviceAreaNameList", target = "serviceAreaIdsList")
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "name")
    public abstract BranchDTO domainToDTO(Branch branch, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "dtoData.serviceAreaIdsList", target = "serviceAreaNameList")
    public abstract Branch dtoToDomain(BranchDTO dtoData, @Context CycleAvoidingMappingContext context);
    
    Long fromServiceAreaToId(ServiceArea entity) {
        return entity == null ? null : entity.getId();
    }
    
    ServiceArea fromIdToServiceArea(Long entityId) {
        if (entityId == null) {
            return null;
        }
        ServiceArea entity;
        try {
            entity = serviceAreaService.getByID(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
    
    @AfterMapping
    void afterMapping(@MappingTarget BranchDTO branchDTO, Branch branch) {
        try {
            if (null != branch.getServiceAreaNameList() && 0 < branch.getServiceAreaNameList().size()) {
            	branchDTO.setServiceAreaNameList(branch.getServiceAreaNameList().stream().map(ServiceArea::getName).collect(Collectors.toList()));
            } else {
            	branchDTO.setServiceAreaNameList(Collections.singletonList("-"));
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error("Branch Mapper" + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

}
