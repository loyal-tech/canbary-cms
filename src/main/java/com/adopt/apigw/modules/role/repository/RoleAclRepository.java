package com.adopt.apigw.modules.role.repository;

import com.adopt.apigw.modules.role.domain.RoleACLEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleAclRepository extends JpaRepository<RoleACLEntry,Long> {
    @Query("select rce from RoleACLEntry rce where rce.roleId = :id")
    List<RoleACLEntry> findAllByRoleId( @Param("id") Long id);
}
