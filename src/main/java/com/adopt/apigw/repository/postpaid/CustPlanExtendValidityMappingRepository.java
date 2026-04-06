package com.adopt.apigw.repository.postpaid;

import com.adopt.apigw.model.postpaid.CustPlanExtendValidityMapping;
import feign.Param;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface CustPlanExtendValidityMappingRepository extends JpaRepository<CustPlanExtendValidityMapping, Integer>, QuerydslPredicateExecutor<CustPlanExtendValidityMapping> {


    List<CustPlanExtendValidityMapping> findAllByCustPlanMapppingId(Integer custPlanId);
    List<CustPlanExtendValidityMapping> findAllByCustServiceMappingId(Integer custServiceMappingId);


}
