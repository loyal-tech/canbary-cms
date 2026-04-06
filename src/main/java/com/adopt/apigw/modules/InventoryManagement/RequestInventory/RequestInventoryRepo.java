package com.adopt.apigw.modules.InventoryManagement.RequestInventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestInventoryRepo extends JpaRepository<RequestInventory,Long>, QuerydslPredicateExecutor<RequestInventory> {

    RequestInventory findTopByOrderByIdDesc();

    List<RequestInventory> findAllByCreatedById(Integer id);


}
