package com.adopt.apigw.modules.InventoryManagement.inventoryMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryMappingRepo extends JpaRepository<InventoryMapping, Long>, QuerydslPredicateExecutor<InventoryMapping> {

//    List<InventoryMapping> findAllByCustomerAndStatusAndQtyIsGreaterThan(Customers customers, String status, Long qty);
//    List<InventoryMapping> findAllByCustomerAndStatus(Customers customers, String status);
}
