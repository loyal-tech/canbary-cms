//package com.adopt.apigw.modules.tickets.repository;
//
//import com.adopt.apigw.modules.tickets.domain.CaseReason;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.querydsl.QuerydslPredicateExecutor;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface CaseReasonRepository extends JpaRepository<CaseReason, Long>, QuerydslPredicateExecutor<CaseReason> {
//    CaseReason findCaseReasonByName(String name);
//
//    CaseReason findCaseReasonByNameAndMvnoIdIn(String name, List mvnoIds);
//
//    @Query(value = "SELECT * from tblcasereasons t WHERE t.is_delete = false"
//            , nativeQuery = true
//            , countQuery = "SELECT count(*) from tblcasereasons t WHERE t.is_delete = false")
//    Page<CaseReason> findAll(Pageable pageable);
//
//    @Query(value = "SELECT * from tblcasereasons t WHERE t.is_delete = false and t.MVNOID in :mvnoIds"
//            , nativeQuery = true
//            , countQuery = "SELECT count(*) from tblcasereasons t WHERE t.is_delete = false and t.MVNOID in :mvnoIds")
//    Page<CaseReason> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);
//
//    @Query(value = "SELECT * from tblcasereasons t WHERE t.is_delete = false and t.MVNOID in :mvnoIds and t.BUID in :buIds"
//            , nativeQuery = true
//            , countQuery = "SELECT count(*) from tblcasereasons t WHERE t.is_delete = false and t.MVNOID in :mvnoIds and t.BUID in :buIds")
//    Page<CaseReason> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds , @Param("buIds") List buIds);
//
//
//
//    @Query(nativeQuery = true, value = "SELECT * FROM tblcasereasons t where (t.name like '%' :s1 '%' or t.tat_consideration like '%' :s2 '%')" +
//            " and t.is_delete = 0", countQuery = "SELECT count(*) FROM tblcasereasons t where (t.name like '%' :s1 '%' or t.tat_consideration like '%' :s2 '%')" +
//            " and t.is_delete = 0")
//    Page<CaseReason> findAllByNameContainingIgnoreCaseOrTatConsiderationContainingIgnoreCaseAndIsDeleteIsFalse(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable);
//
//    @Query(nativeQuery = true, value = "SELECT * FROM tblcasereasons t where (t.name like '%' :s1 '%' or t.tat_consideration like '%' :s2 '%') and MVNOID in :mvnoIds" +
//            " and t.is_delete = 0", countQuery = "SELECT count(*) FROM tblcasereasons t where (t.name like '%' :s1 '%' or t.tat_consideration like '%' :s2 '%') and MVNOID in :mvnoIds" +
//            " and t.is_delete = 0")
//    Page<CaseReason> findAllByNameContainingIgnoreCaseOrTatConsiderationContainingIgnoreCaseAndIsDeleteIsFalse(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("mvnoIds")List mvnoIds);
//
//    @Query(value = "select count(*) from tblcasereasons where name=:name and is_delete=false and MVNOID in :mvnoIds and BUID in :buIds", nativeQuery = true)
//    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds , @Param("buIds") List buIds);
//
//    @Query(value = "select count(*) from tblcasereasons where name=:name and is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
//    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);
//
//
//    @Query(value = "select count(*) from tblcasereasons where name=:name and reasonId =:id and is_delete=false and MVNOID in :mvnoIds and BUID in :buIds", nativeQuery = true)
//    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("mvnoIds")List mvnoIds,@Param("buIds") List buIds);
//
//
//    @Query(value = "select count(*) from tblcasereasons where name=:name and reasonId =:id and is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
//    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("mvnoIds")List mvnoIds);
//
//    @Query(value = "select count(*) from tblcasereasons where name=:name and is_delete=false", nativeQuery = true)
//    Integer duplicateVerifyAtSave(@Param("name") String name);
//
//    @Query(value = "select count(*) from tblcasereasons where name=:name and reasonId =:id and is_delete=false", nativeQuery = true)
//    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id);
//
//
//
//    @Query(nativeQuery = true, value = "SELECT * FROM tblcasereasons t where (t.name like '%' :s1 '%' or t.tat_consideration like '%' :s2 '%') and MVNOID in :mvnoIds and BUID in :buIds" +
//            " and t.is_delete = 0", countQuery = "SELECT count(*) FROM tblcasereasons t where (t.name like '%' :s1 '%' or t.tat_consideration like '%' :s2 '%') and MVNOID in :mvnoIds and BUID in :buIds" +
//            " and t.is_delete = 0")
//    Page<CaseReason> findAllByNameContainingIgnoreCaseOrTatConsiderationContainingIgnoreCaseAndIsDeleteIsFalse(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("mvnoIds")List mvnoIds , @Param("buIds") List buIds);
//
//
//    @Query(value = "select CREATEDBYSTAFFID from tblcasereasons where name=:name and is_delete=false and MVNOID=:mvnoId", nativeQuery = true)
//	Integer getCreatedBy(@Param("name")String name,  @Param("mvnoId")Integer mvnoId);
//}
