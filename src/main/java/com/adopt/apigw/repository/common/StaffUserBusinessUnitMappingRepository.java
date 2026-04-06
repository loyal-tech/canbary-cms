package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.common.StaffUserBusinessUnitMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffUserBusinessUnitMappingRepository extends JpaRepository<StaffUserBusinessUnitMapping, Long>, QuerydslPredicateExecutor<StaffUserBusinessUnitMapping> {

    //List<StaffUserBusinessUnitMapping> findByStaffId(List<Integer> staffId);

    @Query(value = "select t.businessunitId from StaffUserBusinessUnitMapping t where t.staffId=:staffId")
    List<Long> findBuidByStaffId(Integer staffId);
}
