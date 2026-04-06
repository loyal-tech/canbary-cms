package com.adopt.apigw.repository.postpaid;

import com.adopt.apigw.model.postpaid.PlanCasMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanCasMappingRepository extends JpaRepository<PlanCasMapping, Long>, QuerydslPredicateExecutor<PlanCasMapping> {
    List<PlanCasMapping> findAllByPlanId(Long planId);

}
