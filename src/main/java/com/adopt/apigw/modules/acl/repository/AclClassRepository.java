package com.adopt.apigw.modules.acl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adopt.apigw.modules.acl.domain.AclClass;

public interface AclClassRepository extends JpaRepository<AclClass, Long> {
}