package com.adopt.apigw.modules.PlanQosMapping;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.BusinessVerticals.DTO.BusinessVerticalsDTO;
import com.adopt.apigw.modules.BusinessVerticals.domain.BusinessVerticals;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;
import com.adopt.apigw.modules.qosPolicy.mapper.QOSPolicyMapper;
import com.adopt.apigw.modules.qosPolicy.model.QOSPolicyDTO;
import com.adopt.apigw.modules.qosPolicy.repository.QOSPolicyRepository;
import com.adopt.apigw.modules.qosPolicy.service.QOSPolicyService;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import io.swagger.models.auth.In;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public abstract class PlanQosMappingMapper implements IBaseMapper<PlanQosMappingPojo , PlanQosMappingEntity> {

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private QOSPolicyService qosPolicyService;
    @Autowired
    private QOSPolicyRepository qosPolicyRepository;

    @Autowired
    private QOSPolicyMapper qosPolicyMapper;

    @Mapping(source = "planid" , target = "postpaidPlan")
    @Mapping(source = "qosid" , target = "qosPolicy")
    public abstract PlanQosMappingEntity  dtoToDomain(PlanQosMappingPojo dto, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "postpaidPlan.id" , target = "planid")
    @Mapping(source = "qosPolicy.id" , target = "qosid")
    @Mapping(source = "qosPolicy.name" , target = "qosPolicyName")
    public abstract PlanQosMappingPojo domainToDTO(PlanQosMappingEntity data, @Context CycleAvoidingMappingContext context);

    PostpaidPlan fromIdToPostpaidPlan(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        PostpaidPlan entity;
        try {
            entity =  postpaidPlanService.findById(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    QOSPolicy fromIdToQosPolicy(Integer entityId){
        if (entityId == null) {
            return null;
        }
        QOSPolicy entity;

        try {
            entity =qosPolicyRepository.findById(entityId.longValue()).get();
//            entity =  qosPolicyMapper.dtoToDomain( qosPolicyDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }


}
