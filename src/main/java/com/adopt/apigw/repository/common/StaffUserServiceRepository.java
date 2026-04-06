package com.adopt.apigw.repository.common;

import com.adopt.apigw.modules.StaffUserService.domain.StaffUserServiceMapping1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffUserServiceRepository extends JpaRepository<StaffUserServiceMapping1,Long>, QuerydslPredicateExecutor<StaffUserServiceMapping1> {

@Query(value = "select * from TBLTSTAFFUSERRECEIPTMAPPING t where t.staffmapping_id = :staffId ", nativeQuery = true)
    List<StaffUserServiceMapping1> findByStaffId(@Param("staffId") Integer id);
}
