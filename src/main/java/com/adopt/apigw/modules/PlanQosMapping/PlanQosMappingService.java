package com.adopt.apigw.modules.PlanQosMapping;

import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanQosMappingService{
 @Autowired
 PlanQosMappingRepo planQosMappingRepo;

 @Autowired
    PostpaidPlanRepo postpaidPlanRepo;


    public List<PlanQosMappingEntity> getPlanQosMappingByPlanId( Long planId) {
        List<PlanQosMappingEntity> planQosMappingEntities = planQosMappingRepo.findAllByPlanId(Long.valueOf(planId));
        if (planQosMappingEntities.size() >0 || planQosMappingEntities != null) {
            planQosMappingEntities.stream().forEach(planQosMapping -> {
                if (planQosMapping.getId() != null) {
                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(planQosMapping.getPostpaidPlan().getId().intValue()).get();
                    planQosMapping.setPostpaidPlan(postpaidPlan);
                }
                if (planQosMapping.getQosPolicy() != null) {
                    PlanQosMappingEntity planQosMappingEntity = planQosMappingRepo.findById(planQosMapping.getId()).get();
                    planQosMapping.setId(planQosMappingEntity.getId());
                }
                if (planQosMapping.getPostpaidPlan() != null) {
                    PlanQosMappingEntity planQosMappingEntity = planQosMappingRepo.findById(Long.valueOf(planQosMapping.getId())).get();
                    planQosMapping.setPostpaidPlan(planQosMappingEntity.getPostpaidPlan());
                }
            });
        }
        return planQosMappingEntities;
    }



}
