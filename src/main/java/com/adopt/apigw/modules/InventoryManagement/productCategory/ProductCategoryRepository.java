package com.adopt.apigw.modules.InventoryManagement.productCategory;

import com.adopt.apigw.modules.InventoryManagement.product.Product;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long>, QuerydslPredicateExecutor<ProductCategory> {

    @Query(value = "select count(*) from tblmproductcategory m where m.name=:name and m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmproductcategory m where m.name=:name and m.is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmproductcategory where name=:name and product_id =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id);

    @Query(value = "select count(*) from tblmproductcategory where name=:name and product_id =:id and is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*)  from tbltproduct t where t.pc_id =:id and t.is_deleted =false", nativeQuery = true)
    Integer deleteVerify(@Param("id") Integer id);

    Page<ProductCategory> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);

    Page<ProductCategory> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);

    @Query(value = "select * from tblmproductcategory where `type` = 'CustomerBind' and status = 'Active'\n",nativeQuery = true)
    List<ProductCategory> getall();

    @Query(value = "select * from tblmproductcategory where status = 'Active' and is_deleted = false\n",nativeQuery = true)
    List<ProductCategory> getAllActiveProductCategories();

     List<ProductCategory> findAllByIdIn(Set<Long> productIds);

}
