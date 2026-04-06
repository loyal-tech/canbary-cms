package com.adopt.apigw.modules.tickets.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.mapper.ServiceAreaMapper;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.tickets.domain.CaseReasonConfig;
import com.adopt.apigw.modules.tickets.model.CaseReasonConfigPojo;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.service.common.StaffUserService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CaseReasonConfigMapper implements IBaseMapper<CaseReasonConfigPojo, CaseReasonConfig> {

    private String MODULE = " [CaseReasonConfigMapper] ";

//    @Mapping(source = "data.caseReason", target = "reasonid")
    @Mapping(source = "data.serviceArea", target = "serviceareaid")
    @Mapping(source = "data.staffUser", target = "staffid")
    public abstract CaseReasonConfigPojo domainToDTO(CaseReasonConfig data, @Context CycleAvoidingMappingContext context);

//    @Mapping(source = "dtoData.reasonid", target = "caseReason")
    @Mapping(source = "dtoData.serviceareaid", target = "serviceArea")
    @Mapping(source = "dtoData.staffid", target = "staffUser")
    public abstract CaseReasonConfig dtoToDomain(CaseReasonConfigPojo dtoData, @Context CycleAvoidingMappingContext context);


    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private StaffUserRepository staffUserRepository;

    Integer fromStaffUser(StaffUser entity) {
        return entity == null ? null : entity.getId();
    }

    StaffUser fromStaffUserId(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        StaffUser entity = null;
        try {
            entity = staffUserRepository.findById(entityId).get();
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @Autowired
    private ServiceAreaService ServiceAreaService;
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    Long fromServiceArea(ServiceArea entity) {
        return entity == null ? null : entity.getId();
    }

    ServiceArea fromServiceAreaId(Long entityId) {
        if (entityId == null) {
            return null;
        }
        ServiceArea entity = null;
        try {
            ServiceAreaDTO dto = ServiceAreaService.getEntityById(entityId, false);
            entity = serviceAreaMapper.dtoToDomain(dto, new CycleAvoidingMappingContext());
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @AfterMapping
    void afterMapping(@MappingTarget CaseReasonConfigPojo caseReasonConfigPojo, CaseReasonConfig caseReasonConfig) {
        try {
            if (null != caseReasonConfig.getServiceArea()) {
                ServiceArea serviceArea = caseReasonConfig.getServiceArea();
                caseReasonConfigPojo.setSericeAreaName(null != serviceArea && null != serviceArea.getName() ? serviceArea.getName() : "-");
            } else {
                caseReasonConfigPojo.setSericeAreaName("-");
            }
            if (null != caseReasonConfig.getStaffUser()) {
                StaffUser staffUser = caseReasonConfig.getStaffUser();
                caseReasonConfigPojo.setStaffUserName(null != staffUser && null != staffUser.getFullName() ? staffUser.getFullName() : "-");
            } else {
                caseReasonConfigPojo.setStaffUserName("-");
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
}
