package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.Tax;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface TaxRepository extends JpaRepository<Tax, Integer> {

    @Query(value = "select * from TBLMTAX where lower(name) like '%' :search  '%' order by TAXID AND MVNOID in :MVNOIDS",
            countQuery = "select count(*) from TBLMTAX where lower(name) like '%' :search '%' AND MVNOID in :MVNOIDS",
            nativeQuery = true)
    Page<Tax> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOIDS") List MVNOIDS);

    @Query(value = "select * from TBLMTAX where lower(name) like '%' :search  '%' order by TAXID AND MVNOID in :MVNOIDS AND BUID in :buIds",
            countQuery = "select count(*) from TBLMTAX where lower(name) like '%' :search '%' AND MVNOID in :MVNOIDS AND BUID in :buIds",
            nativeQuery = true)
    Page<Tax> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOIDS") List MVNOIDS, @Param("buIds") List buIds);

    @Query(value = "select * from TBLMTAX where lower(name) like '%' :search  '%' order by TAXID",
            countQuery = "select count(*) from TBLMTAX where lower(name) like '%' :search '%'",
            nativeQuery = true)
    Page<Tax> searchEntity(@Param("search") String searchText, Pageable pageable);

    List<Tax> findByStatusAndIsDeleteIsFalse(String status);

    @Query("select t from Tax t where t.isDelete=false")
    List<Tax> findAll();

    @Query(nativeQuery = true,value = "select * from tblmtax t1 where t1.is_delete = false"
            ,countQuery = "select count(*) from tblmtax t1 where t1.is_delete = false")
    Page<Tax> findAll(Pageable pageable);

    @Query(nativeQuery = true,value = "select * from tblmtax t1 where t1.is_delete = false AND t1.MVNOID in :MVNOIDS"
            ,countQuery = "select count(*) from tblmtax t1 where t1.is_delete = false AND t1.MVNOID in :MVNOIDS")
    Page<Tax> findAll(Pageable pageable, @Param("MVNOIDS") List MVNOIDS);

    @Query(nativeQuery = true,value = "select * from tblmtax t1 where t1.is_delete = false AND (t1.MVNOID = 1 or (t1.MVNOID = :mvnoId and t1.BUID in :buIds))"
            ,countQuery = "select count(*) from tblmtax t1 where t1.is_delete = false AND (t1.MVNOID = 1 or (t1.MVNOID = :mvnoId and t1.BUID in :buIds))")
    Page<Tax> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query("update Tax t set t.isDelete=true where t.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    @Query(value = "select sum(tbl.tab) from(\n" +
            "select count(*) as tab from tblmpostpaidplan t2 where t2.TAXID =:id and t2.is_delete =false \n" +
            "union all\n" +
            "select count(*) as tab from tblcharges t3 where t3.TAXID =:id and t3.is_delete =false \n" +
            ")tbl",nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);

    @Query(nativeQuery = true, value = "select * from tblmtax t1 where (t1.NAME like '%' :s1 '%') and t1.TAXTYPE=:s2 and t1.is_delete = false "
            , countQuery = "select count(*) from tblmtax t1 where (t1.NAME like '%' :s1 '%') and t1.TAXTYPE=:s2 and t1.is_delete = false")
    Page<Tax> findAllByNameContainingIgnoreCaseAndTaxtypeContainingIgnoreCaseAndIsDeleteIsFalse(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from tblmtax t1\n" +
            "where (t1.NAME like '%' :s1 '%') and t1.TAXTYPE=:s2\n" +
            "and t1.is_delete = false AND  MVNOID in :mvnoIds"
            , countQuery = "select count(*) from tblmtax t1\n" +
            "where (t1.NAME like '%' :s1 '%') and t1.TAXTYPE=:s2\n" +
            "and t1.is_delete = false AND  MVNOID in :mvnoIds")
    Page<Tax> findAllByNameContainingIgnoreCaseAndTaxtypeContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable,@Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true, value = "select * from tblmtax t1\n" +
            "where (t1.NAME like '%' :s1 '%') and t1.TAXTYPE=:s2\n" +
            "and t1.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and t1.BUID in :buIds))"
            , countQuery = "select count(*) from tblmtax t1\n" +
            "where (t1.NAME like '%' :s1 '%') and t1.TAXTYPE=:s2\n" +
            "and t1.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and t1.BUID in :buIds))")
    Page<Tax> findAllByNameContainingIgnoreCaseAndTaxtypeContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable,@Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(nativeQuery = true, value = "select * from tblmtax t1\n" +
    		"where (t1.NAME like '%' :s1 '%') \n" +
    		"and t1.is_delete = false"
    		, countQuery = "select count(*) from tblmtax t1\n" +
    				"where (t1.NAME like '%' :s1 '%') \n" +
    		"and t1.is_delete = false")
    Page<Tax> findAllByNameContainingIgnoreCaseAndIsDeleteIsFalse(@Param("s1") String s1, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from tblmtax t1\n" +
            "where (t1.NAME like '%' :s1 '%') \n" +
            "and t1.is_delete = false AND  MVNOID in :mvnoIds"
            , countQuery = "select count(*) from tblmtax t1\n" +
            "where (t1.NAME like '%' :s1 '%') \n" +
            "and t1.is_delete = false AND  MVNOID in :mvnoIds")
    Page<Tax> findAllByNameContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(@Param("s1") String s1, Pageable pageable,@Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true, value = "select * from tblmtax t1\n" +
            "where (t1.NAME like '%' :s1 '%') \n" +
            "and t1.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and t1.BUID in :buIds))"
            , countQuery = "select count(*) from tblmtax t1\n" +
            "where (t1.NAME like '%' :s1 '%') \n" +
            "and t1.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and t1.BUID in :buIds))")
    Page<Tax> findAllByNameContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(@Param("s1") String s1, Pageable pageable,@Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmtax c where c.NAME=:name and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmtax c where c.NAME=:name and c.TAXID =:id and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    @Query(value = "select count(*) from tblmtax c where c.NAME=:name and c.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmtax c where c.NAME=:name and c.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmtax c where c.NAME=:name and c.TAXID =:id and c.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmtax c where c.NAME=:name and c.TAXID =:id and c.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);
}
