package com.adopt.apigw.modules.ResolutionReasons.repository;

import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.BankManagement.domain.BankManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.adopt.apigw.modules.ResolutionReasons.domain.ResolutionReasons;

import java.util.List;

public interface ResolutionReasonsRepository extends JpaRepository<ResolutionReasons,Long>, QuerydslPredicateExecutor<ResolutionReasons> {
	@Query(value = "select count(*) from tblcaseresolutions where res_name=:name and is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblcaseresolutions where res_name=:name and res_id =:id and is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblcaseresolutions where res_name=:name and res_id =:id and is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("mvnoId") Integer mvnoId ,@Param("buIds") List buIds );

    @Query(value = "select count(*) from tblcaseresolutions where res_name=:name and is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId ,@Param("buIds") List buIds );

    @Query(value = "select count(*) from tblcaseresolutions where res_name=:name and is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblcaseresolutions where res_name=:name and res_id =:id and is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id);

    @Query(value = "SELECT t from ResolutionReasons t WHERE t.isDeleted = false")
    Page<ResolutionReasons> findAll(Pageable pageable);

    @Query(value = "select t from ResolutionReasons t where t.isDeleted=false and t.mvnoId in :mvnoIds")
    Page<ResolutionReasons> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select t from ResolutionReasons t where t.isDeleted=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))")
    Page<ResolutionReasons> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds );

    @Query(value = "select m from ResolutionReasons m WHERE m.status like 'Active' and m.isDeleted=false ")
    List<ResolutionReasons> findAllByStatus();

    @Query(value = "select m from ResolutionReasons m WHERE m.status like 'Active' and m.isDeleted=false and m.mvnoId in :mvnoIds")
    List<ResolutionReasons> findAllByStatus(@Param("mvnoIds") List mvnoIds);

    @Query(value = "select m from ResolutionReasons m WHERE m.status like 'Active' and m.isDeleted=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))")
    List<ResolutionReasons> findAllByStatus(@Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);


}
