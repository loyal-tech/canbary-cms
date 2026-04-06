package com.adopt.apigw.modules.InventoryManagement.ItemStatusMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemStatusMappingRepo extends JpaRepository<ItemStatusMapping,Long>, QuerydslPredicateExecutor<ItemStatusMapping> {

     ItemStatusMapping findTopByOrderByItemIdDesc();

      @Query(value = "select * from tbltitemstatusmapping t where itemid=:id and t.item_status='Allocated'",nativeQuery = true)
      List<ItemStatusMapping> findByStatus(@Param("id")Long id);


    @Query(value = "select * from tbltitemstatusmapping t where itemid=:id and t.item_status='UnAllocated'",nativeQuery = true)
    List<ItemStatusMapping> findByItemStatus(@Param("id")Long id);

     List<ItemStatusMapping> findByItemId(Long id);

     List<ItemStatusMapping> findByCustomerId(Long custId);

    List<ItemStatusMapping> findByCustomerIdIn(List<Long> custIds);
}
