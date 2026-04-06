package com.adopt.apigw.modules.servicePlan.repository;

import com.adopt.apigw.model.postpaid.ServiceChargeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceChargemappingRepo extends JpaRepository<ServiceChargeMapping, Long >, QuerydslPredicateExecutor<ServiceChargeMapping>
{

}
