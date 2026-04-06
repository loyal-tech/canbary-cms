package com.adopt.apigw.modules.tickets.repository;


import com.adopt.apigw.modules.tickets.domain.TicketReasonCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketReasonCategoryRepo  extends JpaRepository<TicketReasonCategory, Long>, QuerydslPredicateExecutor<TicketReasonCategory> {

    @Query(value = "select count(*) from tblmticketreasoncategory c where c.category_name=:name and c.is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmticketreasoncategory c where c.category_name=:name and c.is_deleted=false and (mvno_id = 1 or (mvno_id = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmticketreasoncategory c where c.category_name=:name and c.id =:id and c.is_deleted=false and (mvno_id = 1 or (mvno_id = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmticketreasoncategory c where c.category_name=:name and c.id =:id and c.is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmticketreasoncategory c where c.category_name=:name and c.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmticketreasoncategory c where c.category_name=:name and c.id =:id and c.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    List<TicketReasonCategory> findAllByCategoryNameContainingIgnoreCase( String categoryName);
}
