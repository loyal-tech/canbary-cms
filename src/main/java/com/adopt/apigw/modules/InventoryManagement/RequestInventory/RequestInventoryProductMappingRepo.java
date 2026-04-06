package com.adopt.apigw.modules.InventoryManagement.RequestInventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestInventoryProductMappingRepo extends JpaRepository<RequestInvenotryProductMapping,Long>, QuerydslPredicateExecutor<RequestInvenotryProductMapping> {

    List<RequestInvenotryProductMapping> findAllByInventoryRequestId(Long requestInventoryId);

}
