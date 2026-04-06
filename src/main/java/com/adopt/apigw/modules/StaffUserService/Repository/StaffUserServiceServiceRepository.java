package com.adopt.apigw.modules.StaffUserService.Repository;

import com.adopt.apigw.modules.StaffUserService.domain.StaffUserServiceMapping1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StaffUserServiceServiceRepository extends JpaRepository<StaffUserServiceMapping1,Long>, QuerydslPredicateExecutor<StaffUserServiceMapping1> {


    @Query(value = "select count(*) from TBLTSTAFFUSERRECEIPTMAPPING where prefix=:name and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from TBLTSTAFFUSERRECEIPTMAPPING where prefix=:name and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);
}
