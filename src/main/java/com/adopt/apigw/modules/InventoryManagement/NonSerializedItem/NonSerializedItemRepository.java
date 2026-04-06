package com.adopt.apigw.modules.InventoryManagement.NonSerializedItem;

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
public interface NonSerializedItemRepository extends JpaRepository<NonSerializedItem, Long>, QuerydslPredicateExecutor<NonSerializedItem> {

	Page<NonSerializedItem> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);

	Page<NonSerializedItem> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);

	@Query(value = "select count(*) from tblmnonserializeditem m where m.name=:name and m.is_deleted=false",nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name")String name);

	@Query(value = "select count(*) from tblmnonserializeditem m where m.name=:name and m.is_deleted=false and mvno_id in :mvnoIds",nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);

	@Query(value = "select count(*) from tblmnonserializeditem where id =:id and is_deleted=false " ,nativeQuery = true)
	Integer deleteVerify(@Param("id")Integer id);

	@Query(value = "select count(*) from tblmnonserializeditem t where t.id =:id and t.name =:name and t.is_deleted =false", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id);

	@Query(value = "select count(*) from tblnonserializeditem t where t.id =:id and  t.name =:name and t.is_deleted =false and mvno_id in :mvnoIds", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoids);

	List<NonSerializedItem> findAllByCurrentInwardIdAndProductId(Long inwardId, Long productId);

	@Query(value = "select * from tblmnonserializeditem t where t.current_inward_id in :id and t.is_deleted =false", nativeQuery = true)
	List<NonSerializedItem> findByCurrentId(@Param("id") List<Long> id);

	@Query(value = "select * from tblmnonserializeditem t where t.external_item_id in :id and t.is_deleted =false", nativeQuery = true)
	List<NonSerializedItem> findByCurrentExternalItemId(@Param("id") List<Long> id);

	@Query(value = "select * from tblmnonserializeditem t where t.warranty='InWarranty'",nativeQuery = true)
	List<NonSerializedItem> findBywarranty();

	@Query(value = "select * from tblmnonserializeditem t where id =:id",nativeQuery = true)
	List<NonSerializedItem> getall(@Param("id") Long id);

 	List<NonSerializedItem> findAllByIdIn(List<Long> id);
	NonSerializedItem findTopByOrderByIdDesc();

}
