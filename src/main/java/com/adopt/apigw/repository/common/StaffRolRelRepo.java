package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.common.StaffRoleRel;
import com.adopt.apigw.modules.role.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRolRelRepo extends JpaRepository<StaffRoleRel, Long>, QuerydslPredicateExecutor<Role> {

    List<StaffRoleRel> findByRoleId(Long roleId);
    @Query(value = "select t.roleId from StaffRoleRel t where t.staffId=:staffId")
    List<Long> findRoleIdByStaffId(Long staffId);

}
