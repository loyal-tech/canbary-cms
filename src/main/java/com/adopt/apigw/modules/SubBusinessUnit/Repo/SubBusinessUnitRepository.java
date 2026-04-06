package com.adopt.apigw.modules.SubBusinessUnit.Repo;

import com.adopt.apigw.model.postpaid.State;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.SubBusinessUnit.Domain.SubBusinessUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubBusinessUnitRepository extends JpaRepository<SubBusinessUnit,Long>, QuerydslPredicateExecutor<SubBusinessUnit> {
//    Integer duplicateVerifyAtSaveWithName(String subBuName);

    @Query(value = "select count(*) from tbltsubbusinessunit m where m.subbuname=:subbuname and m.is_deleted=false",nativeQuery = true)
    Integer duplicateVerifyAtSaveWithName(@Param("subbuname")String subbuname);

    @Query(value = "select count(*) from tbltsubbusinessunit m where m.subbuname=:subbuname and m.is_deleted=false and MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSaveWithName(@Param("subbuname")String subbuname, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tbltsubbusinessunit where subbuname=:subbuname and sub_bu_id =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("subbuname") String subbuname, @Param("id") Long id);

    @Query(value = "select count(*) from tbltsubbusinessunit where subbuname=:subbuname and sub_bu_id =:id and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("subbuname") String subbuname, @Param("id") Long id, @Param("mvnoIds")List mvnoIds);

//    @Query(value = "select * from tbltsubbusinessunit t where t.is_deleted=false", nativeQuery = true)
//    Page<SubBusinessUnit> findAll(Pageable pageable);
}
