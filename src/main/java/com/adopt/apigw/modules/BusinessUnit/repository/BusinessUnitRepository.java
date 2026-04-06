package com.adopt.apigw.modules.BusinessUnit.repository;

import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BusinessUnitRepository extends JpaRepository<BusinessUnit, Long>, QuerydslPredicateExecutor<BusinessUnit> {

    @Query(value = "SELECT * from tblmbusinessunit t WHERE t.is_deleted = false"
            , nativeQuery = true
            , countQuery = "SELECT count(*) from tblmbusinessunit t WHERE t.is_deleted = false")
    Page<BusinessUnit> findAll(Pageable pageable);

    @Query(value = "select * from tblmbusinessunit t where t.is_deleted = false and MVNOID in :mvnoIds", nativeQuery = true)
    Page<BusinessUnit> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmbusinessunit m where m.buname=:buname and m.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("buname") String buname, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmbusinessunit m where m.buname=:buname and m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("buname") String buname);

    @Query(value = "select count(*) from tblmbusinessunit m where m.bucode=:bucode and m.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSaveUcode(@Param("bucode") String bucode, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmbusinessunit m where m.bucode=:bucode and m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSaveUcode(@Param("bucode") String bucode);

    @Query(value = "select count(*) from tblmbusinessunit where buname=:buname and businessunitid =:id and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("buname") String buname, @Param("id") Long id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmbusinessunit where buname=:buname and businessunitid =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("buname") String buname, @Param("id") Long id);

    @Query(value = "select count(*) from tblmbusinessunit where bucode=:bucode and businessunitid =:id and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyUcodeAtEdit(@Param("bucode") String bucode, @Param("id") Long id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmbusinessunit where bucode=:bucode and businessunitid =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyUcodeAtEdit(@Param("bucode") String bucode, @Param("id") Long id);


    Page<BusinessUnit> findAllBybunameContainingIgnoreCaseAndIsDeletedIsFalse(String buname, Pageable pageable);

    Page<BusinessUnit> findAllBybunameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String buname, Pageable pageable, List mvnoIds);

    //    @Query(value = "select count(*) as tab from TBLMSERVICES t1  where t1.businessunitid =:id " ,nativeQuery = true)
//    Integer deleteVerify(@Param("id")Integer id);
    @Query(value = "select count(*) as tab from tblstaffbusinessunitrel t1  where t1.businessunitid =:id ", nativeQuery = true)
    Integer deleteVerify(@Param("id") Integer id);

    @Query(value = "select count(*) as tab from tbltsubbusinessunit t1  where t1.businessunitid =:id and is_deleted=false", nativeQuery = true)
    Integer deleteVerifyForSubBusinessunit(@Param("id") Integer id);

    List<BusinessUnit> findAllByIdIn(List<Long> buids);


//    BusinessUnit findBybunameContainingIgnoreCaseAndIsDeletedIsFalse(String buname);

    @Query("SELECT b.planBindingType FROM BusinessUnit b WHERE b.id = :id")
    String findPlanBindingTypeById(@Param("id") Long id);

}
