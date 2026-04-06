package com.adopt.apigw.repository;

import com.adopt.apigw.model.common.CustomerNotes;
import com.adopt.apigw.model.postpaid.DepartmentPlanMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentPlanMappingRepository extends JpaRepository<DepartmentPlanMapping, Integer> {
    @Query("SELECT DISTINCT dpm.planId.id FROM DepartmentPlanMapping dpm WHERE dpm.department.id = :departmentId")
    List<Integer> findDistinctPlanIdsByDepartmentId(@Param("departmentId") Integer departmentId);
}