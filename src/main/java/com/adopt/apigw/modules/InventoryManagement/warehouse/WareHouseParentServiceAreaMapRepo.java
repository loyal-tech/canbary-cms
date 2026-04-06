package com.adopt.apigw.modules.InventoryManagement.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WareHouseParentServiceAreaMapRepo extends JpaRepository<WareHouseParentServiceAreaMapping, Long>, QuerydslPredicateExecutor<WareHouseParentServiceAreaMapping> {
}
