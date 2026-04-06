package com.adopt.apigw.modules.InventoryManagement.productOwner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOwnerRepository extends JpaRepository<ProductOwner, Long>, QuerydslPredicateExecutor<ProductOwner> {
    @Query(value = "select * from tbltproductowner m where m.product_id=:productId and m.owner_id=:ownerId and owner_type =:ownerType",nativeQuery = true)
    ProductOwner findByProductIdOwnerIdAndOwnerType(Long productId, Long ownerId, String ownerType);
}
