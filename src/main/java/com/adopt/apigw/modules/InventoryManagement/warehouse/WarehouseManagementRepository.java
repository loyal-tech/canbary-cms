package com.adopt.apigw.modules.InventoryManagement.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WarehouseManagementRepository extends JpaRepository<WareHouse, Long>, QuerydslPredicateExecutor<WareHouse> {
	//Page<WareHouse> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);
	//Page<WareHouse> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);
//	Page<WareHouse> findAll(Pageable pageable, List mvnoIds);

//	@Query(value = "select count(*) from tbltwarehousemanagement c where c.name=:name and c.is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
//	Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

//	@Query(value = "select count(*) from tbltwarehousemanagement c where c.name=:name and c.warehouse_id =:id and c.is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
//	Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);
//
//	@Query(value = "select count(*) from tbltwarehousemanagement c where c.name=:name and c.is_deleted=false", nativeQuery = true)
//	Integer duplicateVerifyAtSave(@Param("name") String name);
//
//	@Query(value = "select count(*) from tbltwarehousemanagement c where c.name=:name and c.warehouse_id =:id and c.is_deleted=false", nativeQuery = true)
//	Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

	@Query(nativeQuery = true, value = "select t.warehouse_id from adoptconvergebss.tbltwarehousemanagement t where t.name =:name and is_deleted = false")
	Integer findId(@Param("name") String name);
}
