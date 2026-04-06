package com.adopt.apigw.model.postpaid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanGroupMappingChargeRelRepo extends JpaRepository<PlanGroupMappingChargeRel,Long>, QuerydslPredicateExecutor<PlanGroupMappingChargeRel> {

    List<PlanGroupMappingChargeRel> findAllByPlanGroupMappingIn(List<PlanGroupMapping> planGroupMappingList);

    List<PlanGroupMappingChargeRel>  findAllByPlanGroupMapping(PlanGroupMapping id);
    List<PlanGroupMappingChargeRel> findAllByPlanId(Integer planId);
}
