package com.adopt.apigw.modules.Matrix.repository;

import com.adopt.apigw.modules.Matrix.domain.Matrix;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatrixRepository extends JpaRepository<Matrix,Long> , QuerydslPredicateExecutor<Matrix> {

    @Query(value = "select count(*) from tblmmatrix t where t.name=:matrixname and t.is_deleted=false and MVNOID in :mvnoIds and is_deleted='false'", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("matrixname") String matrixname, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmmatrix t where t.name=:matrixname and t.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("matrixname") String matrixname);

    @Query(value = "select count(*) from tblmmatrix t where t.name=:matrixname and t.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoIds and BUID in :buIds)) and is_deleted='false'",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("matrixname")String matrixname, @Param("mvnoIds") Integer mvnoIds, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmmatrix t where t.name=:matrixname and t.id=:matrixid and t.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("matrixname") String policyname, @Param("matrixid") Integer policyid);


    @Query(value = "select count(*) from tblmmatrix t where t.name=:matrixname and t.id=:matrixid and t.is_deleted=false and MVNOID in :mvnoIds and is_deleted='false'", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("matrixname") String policyname, @Param("matrixid") Integer policyid, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmmatrix t where t.name=:matrixname and t.id=:matrixid and t.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoIds and BUID in :buIds)) and is_deleted='false'", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("matrixname") String policyname, @Param("matrixid") Integer policyid, @Param("mvnoIds") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) as tab from tbltteamhierarchymapping t1 where t1.tat_id =:id and t1.is_deleted =false", nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);

    @Query(value = "select * from tblmmatrix t where t.is_deleted = false and t.lcoid IS NULL", nativeQuery = true)
    Page<Matrix> findAll(Pageable pageable);

//    @Query(value = "select * from tblmmatrix t where t.is_deleted = false and t.lcoid IS NULL", nativeQuery = true)
//    Page<Matrix> findAll(Pageable pageable);

    @Query(value = "select * from tblmmatrix t where t.is_deleted = false and t.lcoid=:lcoId", nativeQuery = true)
    Page<Matrix> findAll(Pageable pageable,@Param("lcoId") Integer lcoId);

    @Query(value = "select * from tblmmatrix t where t.is_deleted = false and MVNOID in :mvnoIds and t.lcoid IS NULL", nativeQuery = true)
    Page<Matrix> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tblmmatrix t where t.is_deleted = false and MVNOID in :mvnoIds and t.lcoid=:lcoId", nativeQuery = true)
    Page<Matrix> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds,@Param("lcoId") Integer lcoId);

    @Query(value = "select * from tblmmatrix t where t.is_deleted = false and (MVNOID = 1 or (MVNOID = :mvnoIds and BUID in :buIds) and t.lcoid IS NULL)", nativeQuery = true)
    Page<Matrix> findAll(Pageable pageable, @Param("mvnoIds") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select * from tblmmatrix t where t.is_deleted = false and (MVNOID = 1 or (MVNOID = :mvnoIds and BUID in :buIds) and t.lcoid=:lcoId)", nativeQuery = true)
    Page<Matrix> findAll(Pageable pageable, @Param("mvnoIds") Integer mvnoId, @Param("buIds") List buIds,@Param("lcoId") Integer lcoId);

    @Query(value = "select * from tblmmatrix t where status = 'Active' and lcoid IS NULL and is_deleted='false'",nativeQuery = true)
    List<Matrix> findbystatus();

    @Query(value = "select * from tblmmatrix t where status = 'Active' and lcoid=:lcoId and is_deleted='false' ",nativeQuery = true)
    List<Matrix> findbystatus(@Param("lcoId") Integer lcoId);

//    Page<Matrix> findAllBynameContainingIgnoreCaseAndis_deletedIsFalse(String name, Pageable pageable);
//    Page<Matrix> findAllBynameContainingIgnoreCaseAndis_deletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);

    @Query(value = "select * from tblmmatrix t where status = 'Active' and  MVNOID in :mvnoIds and lcoid IS NULL and is_deleted='false'",nativeQuery = true)
    List<Matrix> findAllBystatus(@Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tblmmatrix t where status = 'Active' and  MVNOID in :mvnoIds and lcoid=:lcoId and is_deleted='false'",nativeQuery = true)
    List<Matrix> findAllBystatus(@Param("mvnoIds") List mvnoIds,@Param("lcoId") Integer lcoId);

    @Query(value = "select * from tblmmatrix t where status = 'Active' and (MVNOID = 1 or (MVNOID = :mvnoIds and BUID in :buIds) and lcoid IS NULL and is_deleted='false')",nativeQuery = true)
    List<Matrix> findAllBystatus(@Param("mvnoIds") Integer mvnoIds, @Param("buIds") List buIds);

    @Query(value = "select * from tblmmatrix t where status = 'Active' and (MVNOID = 1 or (MVNOID = :mvnoIds and BUID in :buIds) and lcoid=:lcoId and is_deleted='false')",nativeQuery = true)
    List<Matrix> findAllBystatus(@Param("mvnoIds") Integer mvnoIds, @Param("buIds") List buIds,@Param("lcoId") Integer lcoId);
}
