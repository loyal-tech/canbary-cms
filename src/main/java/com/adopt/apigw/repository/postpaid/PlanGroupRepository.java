package com.adopt.apigw.repository.postpaid;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.PlanGroup;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface PlanGroupRepository extends JpaRepository<PlanGroup, Integer>, QuerydslPredicateExecutor<PlanGroup> {
	
	@Query(value = "select count(*) from tblmplangroup t where t.plangroupname=:name and t.is_deleted=false and t.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmplangroup t where t.plangroupname=:name and t.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmplangroup t where t.plangroupname=:name and t.plangroupid =:id and t.is_deleted=false and t.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmplangroup t where t.plangroupname=:name and t.plangroupid =:id and t.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmplangroup t where t.plangroupname=:name and t.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmplangroup t where t.plangroupname=:name and t.plangroupid =:id and t.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id);

    @Query(value = "select CREATEDBYSTAFFID from tblmplangroup where plangroupname=:name and is_deleted=false and MVNOID=:mvnoId and BUID in :buIds", nativeQuery = true)
	Integer getCreatedBy(@Param("name")String name,  @Param("mvnoId")Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select CREATEDBYSTAFFID from tblmplangroup where plangroupname=:name and is_deleted=false and MVNOID=:mvnoId", nativeQuery = true)
    Integer getCreatedBy(@Param("name")String name,  @Param("mvnoId")Integer mvnoId);
    
    @Query(value = "select * from tblmplangroup t \n" +
            "where " + "(t.plangroupname like '%' :s1 '%' \n" +
            "or t.plantype like '%' :s2 '%') \n" +
            "and t.is_deleted = false", nativeQuery = true
            , countQuery = "select count(*) from tblmplangroup t \n" +
            "where " + "(t.plangroupname like '%' :s1 '%' \n" +
            "or t.plantype like '%' :s2 '%') \n" +
            "and t.is_deleted = false")
    Page<PlanGroup> findByPanGroupNameOrPlanTypeContainingIgnoreCaseAndIsDeleteIsFalse(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable);

    @Query(value = "select * from tblmplangroup t \n" +
            "where " + "(t.plangroupname like '%' :s1 '%' \n" +
            "or t.plantype like '%' :s2 '%') \n" +
            "and t.is_deleted = false AND t.MVNOID in :mvnoIds", nativeQuery = true
            , countQuery = "select count(*) from tblmplangroup t \n" +
            "where " + "(t.plangroupname like '%' :s1 '%' \n" +
            "or t.plantype like '%' :s2 '%') \n" +
            "and t.is_deleted = false AND t.MVNOID in :mvnoIds")
    Page<PlanGroup> findByPanGroupNameOrPlanTypeContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("mvnoIds")  List mvnoIds);
    @Query(value = "select * from tblmplangroup t \n" +
            "where " + "(t.plangroupname like '%' :s1 '%' \n" +
            "or t.plantype like '%' :s2 '%') \n" +
            "and t.is_deleted = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true
            , countQuery = "select count(*) from tblmplangroup t \n" +
            "where " + "(t.plangroupname like '%' :s1 '%' \n" +
            "or t.plantype like '%' :s2 '%') \n" +
            "and t.is_deleted = false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))")
    Page<PlanGroup> findByPanGroupNameOrPlanTypeContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("mvnoId")  Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select * from tblmplangroup t where t.plangroupname=:name and t.is_deleted=false", nativeQuery = true)
    PlanGroup findByName(@Param("name") String name);
    PlanGroup findByPlanGroupNameEqualsAndMvnoIdInAndIsDelete( String name,List<Integer>mvnoId,Boolean deleted );
}
