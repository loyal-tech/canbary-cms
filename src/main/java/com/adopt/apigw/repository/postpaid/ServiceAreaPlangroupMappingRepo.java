package com.adopt.apigw.repository.postpaid;

import com.adopt.apigw.model.postpaid.PlanGroup;
import com.adopt.apigw.model.postpaid.ServiceAreaPlanGroupMapping;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface ServiceAreaPlangroupMappingRepo extends JpaRepository<ServiceAreaPlanGroupMapping, Integer>, QuerydslPredicateExecutor<ServiceAreaPlanGroupMapping> {

    List<ServiceAreaPlanGroupMapping> findByServiceArea(ServiceArea serviceAreaId);

    List<ServiceAreaPlanGroupMapping> findAllByPlanGroup(PlanGroup planGroup);

    List<ServiceAreaPlanGroupMapping> findByPlanGroupAndServiceAreaIn(PlanGroup planGroup,List<ServiceArea> serviceAreaList );
    List<ServiceAreaPlanGroupMapping> findAllByServiceArea_IdIn(List<Long> serviceArea_id);
    @Query(" SELECT DISTINCT spgm.planGroup.planGroupId FROM ServiceAreaPlanGroupMapping spgm WHERE spgm.serviceArea.id IN :serviceAreaIds")
    List<Integer> findPlanGroupIdsByServiceAreaIds(@Param("serviceAreaIds") List<Long> serviceAreaIds);

}
