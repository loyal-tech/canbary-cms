package com.adopt.apigw.modules.InventoryManagement.RequestInventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestInventoryHistoryRepo extends JpaRepository<RequestInventoryHistory,Long>, QuerydslPredicateExecutor<RequestInventoryHistory> {
}
