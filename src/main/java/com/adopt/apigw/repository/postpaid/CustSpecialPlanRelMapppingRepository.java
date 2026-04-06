package com.adopt.apigw.repository.postpaid;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.CustSpecialPlanRelMappping;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface CustSpecialPlanRelMapppingRepository extends JpaRepository<CustSpecialPlanRelMappping, Long>, QuerydslPredicateExecutor<CustSpecialPlanRelMappping>{
	
//	@Query(nativeQuery = true, value = "select * from TBLMCUSTSPECIALPLANRELMAPPING \n" +
//            "where mapping_name like '%' :s1 '%' and  MVNOID = :MVNOID OR MVNOID IS NULL OR MVNOID = 1", countQuery = "select count(*) from TBLMCUSTSPECIALPLANRELMAPPING \n" +
//            "where mapping_name like '%' :s1 '%' and  MVNOID = :MVNOID OR MVNOID IS NULL OR MVNOID = 1")
	
	@Query(value = "select * from TBLMCUSTSPECIALPLANRELMAPPING t where t.mapping_name= :s1 AND t.MVNOID= :MVNOID OR t.MVNOID IS NULL OR t.MVNOID = 1", nativeQuery = true
    , countQuery = "select count(*) from TBLMCUSTSPECIALPLANRELMAPPING t where t.mapping_name= :s1 AND t.MVNOID= :MVNOID OR t.MVNOID IS NULL OR t.MVNOID = 1")
	Page<CustSpecialPlanRelMappping> findAllByName(@Param("s1") String s1,@Param("MVNOID")Integer MVNOID,Pageable pageable);
	
	@Query(value = "select * from TBLMCUSTSPECIALPLANRELMAPPING t where t.mapping_name= :s1", nativeQuery = true
		    , countQuery = "select count(*) from TBLMCUSTSPECIALPLANRELMAPPING t where t.mapping_name= :s1")
	Page<CustSpecialPlanRelMappping> findAllByName(@Param("s1")String s1,Pageable pageable);

	@Query(value = "select count(*) from TBLMCUSTSPECIALPLANRELMAPPING c where c.mapping_name=:name and MVNOID in :mvnoIds", nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

	@Query(value = "select count(*) from TBLMCUSTSPECIALPLANRELMAPPING c where c.mapping_name=:name and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

	@Query(value = "select count(*) from TBLMCUSTSPECIALPLANRELMAPPING c where c.mapping_name=:name and c.CUSTSPPLANID =:id and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

	@Query(value = "select count(*) from TBLMCUSTSPECIALPLANRELMAPPING c where c.mapping_name=:name and c.CUSTSPPLANID =:id and MVNOID in :mvnoIds", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

	@Query(value = "select count(*) from TBLMCUSTSPECIALPLANRELMAPPING c where c.mapping_name=:name", nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name") String name);

	@Query(value = "select count(*) from TBLMCUSTSPECIALPLANRELMAPPING c where c.mapping_name=:name and c.CUSTSPPLANID =:id", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);
}
