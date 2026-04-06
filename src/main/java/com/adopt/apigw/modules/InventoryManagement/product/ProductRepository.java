package com.adopt.apigw.modules.InventoryManagement.product;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product> {

	Product findByName(String productDto);
	Page<Product> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);
	Page<Product> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);
	@Query(value = "select count(*) from tbltproduct m where m.name=:name and m.is_deleted=false",nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name")String name);

	@Query(value = "select count(*) from tbltproduct m where m.name=:name and m.is_deleted=false and mvno_id in :mvnoIds",nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);
	@Query(value = "select count(*) from tbltproduct m where m.rms_product_id=:productId and m.is_deleted=false",nativeQuery = true)
	Integer duplicateProductIdVerifyAtSave(@Param("productId")String productId);

	@Query(value = "select count(*) from tbltproduct m where m.rms_product_id=:productId and m.is_deleted=false and mvno_id in :mvnoIds",nativeQuery = true)
	Integer duplicateProductIdVerifyAtSave(@Param("productId")String productId, @Param("mvnoIds") List mvnoIds);

	@Query(value = "select count(*) from tbltinward t where t.product_id =:id and t.is_deleted=false" ,nativeQuery = true)
	Integer deleteVerify(@Param("id")Integer id);

	@Query(value = "select count(*) from tbltproduct t where t.product_id =:id and t.name =:name and t.is_deleted =false", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id);

	// Find duplicate pop name at edit with mvnoId
	@Query(value = "select count(*) from tbltproduct t where t.product_id =:id and  t.name =:name and t.is_deleted =false and mvno_id in :mvnoIds", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoids);
	@Query(value = "select count(*) from tbltproduct t where t.product_id =:id and t.rms_product_id =:productId and t.is_deleted =false", nativeQuery = true)
	Integer duplicateProductIdVerifyAtEdit(@Param("productId")String productId, @Param("id") Integer id);

	// Find duplicate pop name at edit with mvnoId
	@Query(value = "select count(*) from tbltproduct t where t.product_id =:id and  t.name =:productId and t.is_deleted =false and mvno_id in :mvnoIds", nativeQuery = true)
	Integer duplicateProductIdVerifyAtEdit(@Param("productId")String productId, @Param("id") Integer id, @Param("mvnoIds") List mvnoids);

	List<Product> findAllByIdIn(List<Long> id);

	@Query(value = "select count(*) from tbltproduct t where t.case_id =:id and t.is_deleted =false", nativeQuery = true)
	Integer countAllByByCasId(Integer id);

	List<Product> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name);


	@Query(value = "select count(*) as tab from tbltproduct t  where t.vendorid =:vendorid" ,nativeQuery = true)
	Integer deleteVerifyVendor(@Param("vendorid")Integer vendorId);
}
