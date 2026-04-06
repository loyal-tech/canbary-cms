package com.adopt.apigw.modules.PriceGroup.repository;

import com.adopt.apigw.model.postpaid.Partner;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.adopt.apigw.modules.PriceGroup.domain.PriceBook;

import java.util.List;

@JaversSpringDataAuditable
public interface PriceBookRepository extends JpaRepository<PriceBook, Long>, QuerydslPredicateExecutor<PriceBook> {


    @Query("select t from PriceBook t where t.status='Active' AND t.isDeleted = false")
    List<PriceBook> getAllByStatus();

    @Query("SELECT t from PriceBook t where t.isDeleted = false ")
    Page<PriceBook> findAll(Pageable pageable);

    @Query("SELECT t from PriceBook t where t.isDeleted = false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))")
    Page<PriceBook> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);


    @Query("SELECT t from PriceBook t where t.isDeleted = false and MVNOID in :mvnoIds")
    Page<PriceBook> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    Page<PriceBook> findAllByBooknameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);

    Page<PriceBook> findAllByBooknameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(@Param("name") String name, Pageable pageable, @Param("mvnoIds") List mvnoIds);


    @Query(value = "select count(*) from tblpricebook m where m.bookname=:name and m.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblpricebook t where t.bookname=:name and t.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblpricebook t where t.bookname=:name and t.bookid=:id and t.is_delete=false and t.MVNOID in :mvnoIds AND t.BUID in :buIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds, @Param("buIds") List buIds);

    @Query(nativeQuery = true, value = "select * from tblpricebook t \n" +
            "where t.bookname like '%' :s1 '%' and t.is_delete = 0 AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", countQuery = "select count(*) from tblpricebook t \n" +
            "where t.bookname like '%' :s1 '%' and t.is_delete = 0 AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))")
    Page<PriceBook> findAllByNameAndIsDeleteIsFalse(@Param("s1") String s1, Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);


    @Query(value = "select count(*) from tblpricebook m where m.bookname=:name and m.bookid=:id and  m.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblpricebook m where m.bookname=:name and m.bookid=:id and  m.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit2(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblpricebook m where m.bookname=:name and m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblpricebook m where m.bookname=:name and m.bookid=:id and  m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    @Query(value = "select count(*) from tblpartners t where t.pricebookid =:id and t.is_delete =false", nativeQuery = true)
    Integer countPartnerByPriceBook(@Param("id") Integer id);

    @Query(value = "select count(*) as tab from tblpartners t1  where t1.pricebookid =:id " ,nativeQuery = true)
            Integer deleteVerify(@Param("id")Integer id);
}
