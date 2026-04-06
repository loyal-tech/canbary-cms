package com.adopt.apigw.modules.Branch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.Branch.domain.Branch;

@Repository
public interface BranchRepository  extends JpaRepository<Branch, Long>, QuerydslPredicateExecutor<Branch> {
	
 	@Query(value = "SELECT * from tblmbranch t WHERE t.is_deleted = false"
            , nativeQuery = true
            , countQuery = "SELECT count(*) from tblmbranch t WHERE t.is_deleted = false")
    Page<Branch> findAll(Pageable pageable);

    @Query(value = "select * from tblmbranch t where t.is_deleted = false and MVNOID in :mvnoIds", nativeQuery = true)
    Page<Branch> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmbranch m where m.name=:name and m.is_deleted=false and MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmbranch m where m.name=:name and m.is_deleted=false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name);

    @Query(value = "select count(*) from tblmbranch where name=:name and branchid =:id and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmbranch where name=:name and branchid =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id);

    Page<Branch> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);

    Page<Branch> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);

    @Query(value = "select count(*) as tab from tblstaffuser t1  where t1.branchid =:id " ,nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);

    @Query(value = "select count(*) as tab from tbltregionbranchmapping t1  where t1.branchid =:id " ,nativeQuery = true)
    Integer deleteVerifyForRegion(@Param("id")Integer id);

    Branch findByIdAndIsDeletedIsFalse(Long id);

    List<Branch> findAllByIdIn(List<Long> result);

    List<Branch> findAllByStatusAndIsDeletedFalseAndIdIn(String Status,List<Long> result);

    List<Branch> findAllById(Long id);

   Branch findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(String name);

    @Query(value = "select t.name from tblmbranch t where t.branchid in :branchids",nativeQuery = true)
    List<String> getAllBranchNamesByBranchIds(@Param("branchids") List<Long> branchids);

    Optional<Branch>getAllByNameEqualsAndMvnoIdIn(String name,List<Integer> mvnoid);

    @Query(value = "select t.name from tblmbranch t where t.branchid =:branchid",nativeQuery = true)
    String findNameById(@Param("branchid") Long id);

    @Query(value = "select t.branchid from tblmbranch t where t.MVNOID = :mvnoId and t.branchid in " +
            "(select t2.branchid from tblmbranchservicearearel t2 where t2.servicearea_id = :ServiceAreaId) order by t.branchid desc limit 1;", nativeQuery = true)
    Long getLatestBranchIdByServiceAreaId(Integer mvnoId, Long ServiceAreaId);
}
