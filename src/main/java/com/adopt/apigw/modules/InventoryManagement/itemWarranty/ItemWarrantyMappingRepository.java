package com.adopt.apigw.modules.InventoryManagement.itemWarranty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemWarrantyMappingRepository extends JpaRepository<ItemWarrantyMapping, Long>, QuerydslPredicateExecutor<ItemWarrantyMapping> {
    @Query(value = "select count(*) from tbltitemwarranty where id =:id and is_deleted=false " ,nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);

    List<ItemWarrantyMapping> findByItemId(Long itemid);
}
