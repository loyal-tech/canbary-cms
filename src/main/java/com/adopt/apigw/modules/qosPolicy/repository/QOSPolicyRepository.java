package com.adopt.apigw.modules.qosPolicy.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
public interface QOSPolicyRepository extends JpaRepository<QOSPolicy, Long >, QuerydslPredicateExecutor<QOSPolicy> {

    Page<QOSPolicy> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoId(String name, Pageable pageable,Integer mvnoId);

    @Query("SELECT t from QOSPolicy t WHERE t.isDeleted = false")
    Page<QOSPolicy> findAll(Pageable pageable);

    @Query("SELECT t from QOSPolicy t WHERE t.isDeleted = false and MVNOID in :mvnoIds")
    Page<QOSPolicy> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query("SELECT t from QOSPolicy t WHERE t.isDeleted = false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))")
    Page<QOSPolicy> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tbl_qos_policy m where m.name=:name and m.is_deleted=false and MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tbl_qos_policy m where m.name=:name and m.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tbl_qos_policy m where m.name=:name and m.id=:id and  m.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tbl_qos_policy m where m.name=:name and m.id=:id and  m.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tbl_qos_policy m where m.name=:name and m.is_deleted=false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name);

    @Query(value = "select count(*) from tbl_qos_policy m where m.name=:name and m.id=:id and  m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    @Query(value = "select count(*) from TBLMPOSTPAIDPLAN where qospolicy_id =:id",nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);

    @Query("select count(*) from TimeBasePolicyDetails t where t.qqsid =:id and t.timeBasePolicy.isDeleted = false")
    Integer findCountForTimeBasePolicy(@Param("id")Long id);

    Optional<QOSPolicy> findById(Long id);
    Optional<QOSPolicy> findAllByNameAndMvnoIdIn(String name,List<Integer> mvnoId);


    @Query("SELECT t from QOSPolicy t WHERE t.isDeleted = false and t.mvnoId =:mvnoId")
    List<QOSPolicy> findAllQosPolicyByMvnoId(@Param("mvnoId")Integer mvnoId);

    @Query("SELECT q.name FROM QOSPolicy q WHERE q.isDeleted = false AND q.mvnoId IN :mvnoIds")
    List<String> findNamesByIsDeletedFalseAndMvnoIdIn(@Param("mvnoIds") List<Integer> mvnoIds);

    Optional<QOSPolicy> findAllByIsDeletedFalseAndName(String mvnoId);

    List<QOSPolicy> findAllByIsDeletedFalseAndNameInAndMvnoIdIn(List<String> names, List<Integer> mvno);


}
