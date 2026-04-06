package com.adopt.apigw.modules.InventoryManagement.ItemGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemAssemblyProductMappingRepo extends JpaRepository<ItemAssemblyProductMapping,Long>, QuerydslPredicateExecutor<ItemAssemblyProductMapping> {
    List<ItemAssemblyProductMapping> findAllByItemAssemblyId(Long id);

}
