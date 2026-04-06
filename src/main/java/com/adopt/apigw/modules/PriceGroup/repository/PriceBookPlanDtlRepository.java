package com.adopt.apigw.modules.PriceGroup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adopt.apigw.modules.PriceGroup.domain.PriceBookPlanDetail;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface PriceBookPlanDtlRepository extends JpaRepository<PriceBookPlanDetail,Long>, QuerydslPredicateExecutor<PriceBookPlanDetail> {

}
