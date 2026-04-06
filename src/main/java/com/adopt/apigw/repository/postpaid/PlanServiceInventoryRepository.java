package com.adopt.apigw.repository.postpaid;

import com.adopt.apigw.model.postpaid.PlanServiceInventoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PlanServiceInventoryRepository extends JpaRepository<PlanServiceInventoryMapping,Long>, QuerydslPredicateExecutor<PlanServiceInventoryMapping> {
}
