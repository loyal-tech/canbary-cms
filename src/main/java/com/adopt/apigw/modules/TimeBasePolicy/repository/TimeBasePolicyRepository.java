package com.adopt.apigw.modules.TimeBasePolicy.repository;

import com.adopt.apigw.modules.TimeBasePolicy.domain.TimeBasePolicy;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
public interface TimeBasePolicyRepository extends JpaRepository<TimeBasePolicy, Long> , QuerydslPredicateExecutor<TimeBasePolicy> {
    // Find All Time Base Policy with Pagination
    @Query(value = "select * from tblmtimebasepolicy t where t.is_deleted = false"
            , countQuery = "select count(*) from tblmtimebasepolicy t where t.is_deleted = false"
            , nativeQuery = true)
    Page<TimeBasePolicy> findAll(Pageable pageable);

    // Find All Time Base Policy with Pagination and MVNOID
    @Query(value = "select * from tblmtimebasepolicy t where t.is_deleted = false and MVNOID in :mvnoIds", nativeQuery = true)
    Page<TimeBasePolicy> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    // Find All Time Base Policy with Pagination and MVNOID and BUID
    @Query(value = "select * from tblmtimebasepolicy t where t.is_deleted = false and (MVNOID = 1 or (MVNOID = :mvnoIds and BUID in :buIds))", nativeQuery = true)
    Page<TimeBasePolicy> findAll(Pageable pageable, @Param("mvnoIds") Integer mvnoId, @Param("buIds") List buIds);

    //Duplicate Verify At Save with Policy Name and MVNOID
    @Query(value = "select count(*) from tblmtimebasepolicy t where t.policy_name=:policyname and t.is_deleted=false and MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("policyname")String policyname, @Param("mvnoIds") List mvnoIds);

    //Duplicate Verify At Save with Policy Name
    @Query(value = "select count(*) from tblmtimebasepolicy t where t.policy_name=:policyname and t.is_deleted=false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("policyname")String policyname);

    //Duplicate Verify At Save with Policy Name with MVNOID with BUID
    @Query(value = "select count(*) from tblmtimebasepolicy t where t.policy_name=:policyname and t.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoIds and BUID in :buIds))",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("policyname")String policyname, @Param("mvnoIds") Integer mvnoIds, @Param("buIds") List buIds);
    Page<TimeBasePolicy> findAllBynameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);
    Page<TimeBasePolicy> findAllBynameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);

    //Duplicate Verify At Edit with Policy Name, Policy Id, and MVNOID
    @Query(value = "select count(*) from tblmtimebasepolicy t where t.policy_name=:policyname and t.policy_id=:policyid and t.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("policyname") String policyname, @Param("policyid") Integer policyid, @Param("mvnoIds") List mvnoIds);

    //Duplicate Verify At Edit with Policy Name, Policy Id, BUID, and MVNOID
    @Query(value = "select count(*) from tblmtimebasepolicy t where t.policy_name=:policyname and t.policy_id=:policyid and t.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoIds and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("policyname") String policyname, @Param("policyid") Integer policyid, @Param("mvnoIds") Integer mvnoId, @Param("buIds") List buIds);

    //Duplicate Verify At Edit with Policy Name, and Policy Id
    @Query(value = "select count(*) from tblmtimebasepolicy t where t.policy_name=:policyname and t.policy_id=:policyid and t.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("policyname") String policyname, @Param("policyid") Integer policyid);

    @Query(value = "select count(*) as tab from tblmpostpaidplan t1 where t1.timebasepolicyid =:id and t1.is_delete =false", nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);

    List<TimeBasePolicy> findAllById(Long timebasepolicyid);
    Optional<TimeBasePolicy> findAllByNameEqualsIgnoreCaseAndMvnoIdIn(String name, List<Integer> mvnoIds);
}
