package com.adopt.apigw.modules.BuildingMgmt.Repository;

import com.adopt.apigw.modules.BuildingMgmt.Domain.BuildingManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingMgmtRepository extends JpaRepository<BuildingManagement,Long>, QuerydslPredicateExecutor<BuildingManagement> {
}
