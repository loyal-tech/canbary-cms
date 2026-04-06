package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.common.StaffUserServiceAreaMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository
public interface StaffUserServiceAreaMappingRepository extends JpaRepository<StaffUserServiceAreaMapping, Long>, QuerydslPredicateExecutor<StaffUserServiceAreaMapping> {

    List<StaffUserServiceAreaMapping> findByStaffIdIn(List<Integer> staffId);
    List<StaffUserServiceAreaMapping> findByStaffId(Integer staffId);

    List<StaffUserServiceAreaMapping>findAllByStaffIdIn(List<Integer> staffId);

    List<StaffUserServiceAreaMapping> findAllByServiceId(Integer staffId);

    List<StaffUserServiceAreaMapping> findAllByServiceIdIn(List<Integer> serviceAreaId);

    List<StaffUserServiceAreaMapping> findAllByStaffId (Integer staffId);
    @Query(value = "select t.serviceId from StaffUserServiceAreaMapping t where t.staffId=:staffId")
    List<Integer> findServiceAreaByStaffId (Integer staffId);


}
