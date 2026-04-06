package com.adopt.apigw.repository.postpaid;


import com.adopt.apigw.model.postpaid.CreditDocument;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.XsltManagement;

import java.util.List;


//@JaversSpringDataAuditable
@Repository
public interface XsltManagementRepository extends JpaRepository<XsltManagement, Integer>, QuerydslPredicateExecutor<XsltManagement> {

    List<XsltManagement> findByStatus(String status);

    @Query(value = "select * from tbltemplatemanagement t where t.is_delete=false AND t.MVNOID in :mvnoIds AND t.lcoid IS NULL"
            ,nativeQuery = true
            ,countQuery = "select count(*) from tbltemplatemanagement t where t.is_delete=false AND t.MVNOID in :mvnoIds AND t.lcoid IS NULL")
    Page<XsltManagement> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tbltemplatemanagement t where t.is_delete=false AND t.MVNOID in :mvnoIds AND t.lcoid=:lcoId"
            ,nativeQuery = true
            ,countQuery = "select count(*) from tbltemplatemanagement t where t.is_delete=false AND t.MVNOID in :mvnoIds AND t.lcoid=:lcoId")
    Page<XsltManagement> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds,@Param("lcoId") Integer lcoId);

    @Query(value = "select * from tbltemplatemanagement t where t.is_delete=false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds) AND t.lcoid IS NULL) AND t.lcoid IS NULL"
            ,nativeQuery = true
            ,countQuery = "select count(*) from tbltemplatemanagement t where t.is_delete=false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND t.lcoid IS NULL")
    Page<XsltManagement> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select * from tbltemplatemanagement t where t.is_delete=false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND t.lcoid=:lcoId"
            ,nativeQuery = true
            ,countQuery = "select count(*) from tbltemplatemanagement t where t.is_delete=false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND t.lcoid=:lcoId")
    Page<XsltManagement> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds,@Param("lcoId") Integer lcoId);

    @Query("update XsltManagement t set t.isDelete=true where t.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);
    @Query(value = "select * from  tbltemplatemanagement t where t.is_delete=false AND t.lcoid IS NULL", nativeQuery = true)
    Page<XsltManagement> findAll(PageRequest pageRequest);

    @Query(value = "select * from  tbltemplatemanagement t where t.is_delete=false AND t.lcoid=:lcoId", nativeQuery = true)
    Page<XsltManagement> findAll(Pageable pageRequest,@Param("lcoId") Integer lcoId);


    @Query(value = "select * from tbltemplatemanagement t \n" +
            "where (t.templatename like '%' :s1 '%' or t.templatetype like '%' :s2 '%' or t.status like '%' :s3 '%')\n" +
            "and t.is_delete = 0 AND t.MVNOID in :mvnoIds",nativeQuery = true
            ,countQuery = "select count(*) from tbltemplatemanagement t \n" +
            "where (t.templatename like '%' :s1 '%' or t.templatetype like '%' :s2 '%' or t.status like '%' :s3 '%')\n" +
            "and t.is_delete = 0 AND t.MVNOID in :mvnoIds")
    Page<XsltManagement> findAllByCustom(Pageable pageable,@Param("s1") String s1,@Param("s2") String s2,@Param("s3") String s3, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tbltemplatemanagement t \n" +
            "where (t.templatename like '%' :s1 '%' or t.templatetype like '%' :s2 '%' or t.status like '%' :s3 '%')\n" +
            "and t.is_delete = 0 AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))",nativeQuery = true
            ,countQuery = "select count(*) from tbltemplatemanagement t \n" +
            "where (t.templatename like '%' :s1 '%' or t.templatetype like '%' :s2 '%' or t.status like '%' :s3 '%')\n" +
            "and t.is_delete = 0 AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))")
    Page<XsltManagement> findAllByCustom(Pageable pageable,@Param("s1") String s1,@Param("s2") String s2,@Param("s3") String s3, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select * from tbltemplatemanagement t \n" +
            "where (t.templatename like '%' :s1 '%' or t.templatetype like '%' :s2 '%' or t.status like '%' :s3 '%')\n" +
            "and t.is_delete = 0",nativeQuery = true
            ,countQuery = "select count(*) from tbltemplatemanagement t \n" +
            "where (t.templatename like '%' :s1 '%' or t.templatetype like '%' :s2 '%' or t.status like '%' :s3 '%')\n" +
            "and t.is_delete = 0")
    Page<XsltManagement> findAllByCustom(Pageable pageable,@Param("s1") String s1,@Param("s2") String s2,@Param("s3") String s3);

    @Query(value = "select count(*) from tbltemplatemanagement t where t.templatename=:name and t.is_delete=false and t.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tbltemplatemanagement t where t.templatename=:name and t.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId")Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tbltemplatemanagement t where t.templatename=:name and t.templateid =:id and t.is_delete=false and t.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tbltemplatemanagement t where t.templatename=:name and t.templateid =:id and t.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id, @Param("mvnoId")Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tbltemplatemanagement t where t.templatename=:name and t.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tbltemplatemanagement t where t.templatename=:name and t.templateid =:id and t.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id);
}
