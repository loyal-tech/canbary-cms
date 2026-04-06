package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.Discount;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer> {

    @Query(value = "select * from TBLMDISCOUNT where lower(name) like '%' :search  '%' order by DISCOUNTID AND  MVNOID= :MVNOID OR MVNOID IS NULL",
            countQuery = "select count(*) from TBLMDISCOUNT where lower(name) like '%' :search '%' AND  MVNOID= :MVNOID OR MVNOID IS NULL",
            nativeQuery = true)
    Page<Discount> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOID") Integer mvnoId);

    List<Discount> findByStatus(String status);

    @Query("select t from Discount t where t.isDelete=false")
    List<Discount> findAll();

    @Query("update Discount b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    @Query(value = "select * from tblmdiscount t where t.is_delete = 0", nativeQuery = true
            , countQuery = "select count(*) from tblmdiscount t where t.is_delete = 0")
    Page<Discount> findAll(Pageable pageable);

    @Query(value = "select * from tblmdiscount t where t.is_delete = 0 and MVNOID in :mvnoIds", nativeQuery = true
            , countQuery = "select count(*) from tblmdiscount t where t.is_delete = 0 and MVNOID in :mvnoIds")
    Page<Discount> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true, value = "select * from tblmdiscount t \n" +
            "where t.NAME like '%' :s1 '%' and t.is_delete = 0", countQuery = "select count(*) from tblmdiscount t \n" +
            "where t.NAME like '%' :s1 '%' and t.is_delete = 0")
    Page<Discount> findAllByNameAndIsDeleteIsFalse(@Param("s1") String s1, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from tblmdiscount t \n" +
            "where t.NAME like '%' :s1 '%' and t.is_delete = 0 AND  MVNOID in :mvnoIds", countQuery = "select count(*) from tblmdiscount t \n" +
            "where t.NAME like '%' :s1 '%' and t.is_delete = 0 AND  MVNOID in :mvnoIds")
    Page<Discount> findAllByNameAndIsDeleteIsFalse(@Param("s1") String s1, Pageable pageable,@Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true, value = "select * from tblmdiscount t \n" +
            "where t.NAME like '%' :s1 '%' and t.is_delete = 0 AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", countQuery = "select count(*) from tblmdiscount t \n" +
            "where t.NAME like '%' :s1 '%' and t.is_delete = 0 AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))")
    Page<Discount> findAllByNameAndIsDeleteIsFalse(@Param("s1") String s1, Pageable pageable,@Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmdiscount t where t.NAME=:name and t.is_delete=false and t.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmdiscount t where t.NAME=:name and t.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId")Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmdiscount t where t.NAME=:name and t.DISCOUNTID =:id and t.is_delete=false and t.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmdiscount t where t.NAME=:name and t.DISCOUNTID =:id and t.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id, @Param("mvnoId")Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmdiscount t where t.NAME=:name and t.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmdiscount t where t.NAME=:name and t.DISCOUNTID =:id and t.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id);


}
