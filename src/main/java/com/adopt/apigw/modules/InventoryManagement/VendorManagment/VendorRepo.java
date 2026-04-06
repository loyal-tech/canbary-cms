package com.adopt.apigw.modules.InventoryManagement.VendorManagment;
import com.adopt.apigw.modules.InventoryManagement.item.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepo extends JpaRepository<Vendor,Long>, QuerydslPredicateExecutor<Vendor> {


    @Query(value = "select count(*) from tblmvendor m where m.name =:name and m.is_deleted =false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name);

    @Query(value = "select count(*) from tblmvendor m where m.name =:name and m.is_deleted =false and mvno_id in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);
    @Query(value = "select count(*) from tblmvendor m where m.name =:name and m.id =:id  and m.is_deleted =false",nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id);

    @Query(value = "select count(*) from tblmvendor m where m.name =:name and m.id =:id  and m.is_deleted =false and mvno_id in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);
    Page<Vendor> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);
    Page<Vendor> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);
    @Query(value = "select name from tblmvendor m where m.id =:id  and m.is_deleted =false", nativeQuery = true)
    String findNameById(@Param("id") Long id);
}
