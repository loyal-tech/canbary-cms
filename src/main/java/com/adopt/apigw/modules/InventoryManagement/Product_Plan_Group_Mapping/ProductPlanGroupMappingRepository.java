package com.adopt.apigw.modules.InventoryManagement.Product_Plan_Group_Mapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductPlanGroupMappingRepository extends JpaRepository<ProductPlanGroupMapping,Long>, QuerydslPredicateExecutor<ProductPlanGroupMapping> {

}
