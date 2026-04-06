package com.adopt.apigw.repository.postpaid;

import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.model.postpaid.ServiceChargeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceChargeMappingRepository extends JpaRepository<ServiceChargeMapping, Long>, QuerydslPredicateExecutor<ServiceChargeMapping> {
}
