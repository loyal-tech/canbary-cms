package com.adopt.apigw.modules.acl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adopt.apigw.modules.acl.domain.CustomACLEntry;

import java.util.List;

public interface CustomACLEntryRepository extends JpaRepository<CustomACLEntry, Long> {

//    public List<CustomACLEntry> findAllByRole_IdIn(List<Long> id);
}
