package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.repository.radius.RadiusProfileRepository;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.model.radius.RadiusProfile;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;
import com.adopt.apigw.modules.qosPolicy.mapper.QOSPolicyMapper;
import com.adopt.apigw.modules.qosPolicy.model.QOSPolicyDTO;
import com.adopt.apigw.modules.qosPolicy.service.QOSPolicyService;
import com.adopt.apigw.pojo.api.PostpaidPlanPojo;
import com.adopt.apigw.service.radius.RadiusProfileService;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ChargeMapper.class})
public abstract class PostpaidPlanMapper {


    @Mapping(source = "qospolicy", target = "qospolicyid")
    @Mapping(source = "qospolicy.name", target = "qospolicyName")
    @Mapping(source = "radiusprofile", target = "radiusprofileIds")
    @Mapping(source = "createdate", target = "createDateString", dateFormat = "dd/MM/yyyy HH:mm a", defaultValue = "-")
    @Mapping(source = "updatedate", target = "updateDateString", dateFormat = "dd/MM/yyyy HH:mm a", defaultValue = "-")
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayPostpaidName", source = "name")
    //@Mapping(source = "planQosMappingEntities",target = "planQosMappingEntityList")
    public abstract PostpaidPlanPojo domainToDTO(PostpaidPlan data, @Context CycleAvoidingMappingContext context) throws NoSuchFieldException;

    public abstract List<PostpaidPlanPojo> domainToDTO(List<PostpaidPlan> data, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "qospolicyid", target = "qospolicy")
    @Mapping(source = "radiusprofileIds", target = "radiusprofile")
    public abstract PostpaidPlan dtoToDomain(PostpaidPlanPojo dtoData, @Context CycleAvoidingMappingContext context) throws NoSuchFieldException;

    @Autowired
    private QOSPolicyService qosService;

    @Autowired
    private RadiusProfileService radiusProfileService;
    @Autowired
    private RadiusProfileRepository radiusProfileRepository;

    //QOSPolicyService qosService; = SpringContext.getBean(QOSPolicyService.class);

    private QOSPolicyMapper qosMapper = Mappers.getMapper(QOSPolicyMapper.class);

    private static String MODULE = " [PostPaidPlanMapper] ";

    //RadiusProfileIds(Integer) to RadiusProfile Mapping
    public abstract java.util.List<RadiusProfile> mapRadiusProfileIdsToRadiusProfile(List<java.lang.Integer> value);

    public abstract java.util.List<Integer> mapRadiusProfileToRadiusProfileIds(List<RadiusProfile> value);

    Integer fromRadiusprofile(RadiusProfile entity) {
        return entity == null ? null : entity.getId();
    }

    RadiusProfile fromRadiusprofileIds(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        return radiusProfileRepository.findById(entityId).get();
    }


    Long fromQospolicy(QOSPolicy entity) {
        return entity == null ? null : entity.getId();
    }

    QOSPolicy fromQospolicyid(Long entityId) {
        if (entityId == null) {
            return null;
        }
        QOSPolicy entity = null;
        try {
            QOSPolicyDTO entityDTO = qosService.getById(entityId, false);
            entity = qosMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

}
