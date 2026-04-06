package com.adopt.apigw.repository.common;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.common.LeasedLineCustomers;
import com.adopt.apigw.model.postpaid.Tax;

@JaversSpringDataAuditable
@Repository
public interface LeasedLineCustomersRepository extends JpaRepository<LeasedLineCustomers, Integer> {
	
	@Query(value = "select * from TBLLEASEDLINECUSTOMERS where lower(name) like '%' :search  '%' order by llcustid AND MVNOID= :MVNOID OR MVNOID IS NULL",
            countQuery = "select count(*) from TBLLEASEDLINECUSTOMERS where lower(name) like '%' :search '%' AND MVNOID= :MVNOID OR MVNOID IS NULL",
            nativeQuery = true)
    Page<LeasedLineCustomers> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOID") Integer MVNOID);
	
    @Query("select l from LeasedLineCustomers l where l.isDelete=false")
    List<LeasedLineCustomers> findAll();
    
    @Query(nativeQuery = true,value = "select * from TBLLEASEDLINECUSTOMERS t1 where t1.is_delete = false AND t1.MVNOID in :mvnoIds"
            ,countQuery = "select count(*) from TBLLEASEDLINECUSTOMERS t1 where t1.is_delete = false AND t1.MVNOID in :mvnoIds")
    Page<LeasedLineCustomers> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true,value = "select * from TBLLEASEDLINECUSTOMERS t1 where t1.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))"
            ,countQuery = "select count(*) from TBLLEASEDLINECUSTOMERS t1 where t1.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))")
    Page<LeasedLineCustomers> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(nativeQuery = true,value = "select * from TBLLEASEDLINECUSTOMERS t1 where t1.is_delete = false"
            ,countQuery = "select count(*) from TBLLEASEDLINECUSTOMERS t1 where t1.is_delete = false")
    Page<LeasedLineCustomers> findAll(Pageable pageable);

    @Query("update LeasedLineCustomers t set t.isDelete=true where t.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);
    
    @Query(value = "select count(*) from TBLLEASEDLINECUSTOMERS c where c.NAME=:name and c.is_delete=false AND  c.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from TBLLEASEDLINECUSTOMERS c where c.NAME=:name and c.is_delete=false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from TBLLEASEDLINECUSTOMERS c where c.NAME=:name and c.llcustid =:id and c.is_delete=false AND c.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from TBLLEASEDLINECUSTOMERS c where c.NAME=:name and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from TBLLEASEDLINECUSTOMERS c where c.NAME=:name and c.llcustid =:id and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    @Query(value = "select count(*) from TBLLEASEDLINECUSTOMERS c where c.NAME=:name and c.llcustid =:id and c.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(nativeQuery = true, value = "select * from TBLLEASEDLINECUSTOMERS c\n" +
    		"where (c.NAME like '%' :s1 '%') \n" +
    		"and c.is_delete = false"
    		, countQuery = "select count(*) from TBLLEASEDLINECUSTOMERS c\n" +
    				"where (c.NAME like '%' :s1 '%') \n" +
    		"and c.is_delete = false")
    Page<LeasedLineCustomers> findAllByNameContainingIgnoreCaseAndIsDeleteIsFalse(@Param("s1") String s1, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from TBLLEASEDLINECUSTOMERS c\n" +
            "where (c.NAME like '%' :s1 '%') \n" +
            "and c.is_delete = false AND  c.MVNOID in :mvnoIds"
            , countQuery = "select count(*) from TBLLEASEDLINECUSTOMERS c\n" +
            "where (c.NAME like '%' :s1 '%') \n" +
            "and c.is_delete = false AND  c.MVNOID in :mvnoIds")
    Page<LeasedLineCustomers> findAllByNameContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(@Param("s1") String s1, Pageable pageable,@Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true, value = "select * from TBLLEASEDLINECUSTOMERS c\n" +
            "where (c.NAME like '%' :s1 '%') \n" +
            "and c.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))"
            , countQuery = "select count(*) from TBLLEASEDLINECUSTOMERS c\n" +
            "where (c.NAME like '%' :s1 '%') \n" +
            "and c.is_delete = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))")
    Page<LeasedLineCustomers> findAllByNameContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(@Param("s1") String s1, Pageable pageable,@Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

}
