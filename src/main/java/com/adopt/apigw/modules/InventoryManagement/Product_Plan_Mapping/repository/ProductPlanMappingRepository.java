package com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.repository;

import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.domain.Productplanmapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductPlanMappingRepository extends JpaRepository<Productplanmapping,Long>, QuerydslPredicateExecutor<Productplanmapping> {

    @Query(value = "select * from tbl_product_plan_mapping where plan_id =:id",nativeQuery = true)
    List<Productplanmapping> getallfromplanid(@Param("id") Long id);

    @Query(value = "select * from tbl_product_plan_mapping where id =:id",nativeQuery = true)
    List<Productplanmapping> findAllById(@Param("id") Long id);

    Productplanmapping findTopByOrderByIdDesc();

    List<Productplanmapping> findAllByPlanId(Long id);

    Productplanmapping findByPlanIdAndProductCategoryIdAndProductId(Long planId, Long productCategoryId, Long productId);
}
