package com.adopt.apigw.modules.InventoryManagement.itemConditionMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemConditionMappingRepository extends JpaRepository<ItemConditionsMapping, Long>, QuerydslPredicateExecutor<ItemConditionsMapping> {
    @Query(value = "select count(*) from tbltitemconditions where id =:id and is_deleted=false ", nativeQuery = true)
    Integer deleteVerify(@Param("id") Integer id);

    @Query(value = "select * from adoptconvergebss.tbltitemconditions where item_id =:id and filename is not NULL order by id desc;", nativeQuery = true)
    List<ItemConditionsMapping> getItemConditionByItemId(@Param("id") Long id);

}