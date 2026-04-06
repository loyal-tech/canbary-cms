package com.adopt.apigw.repository;

import com.adopt.apigw.model.lead.LeadSource;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.lead.LeadMaster;

import java.util.List;

@Repository
//@JaversSpringDataAuditable
public interface LeadMasterRepository extends JpaRepository<LeadMaster, Long>, QuerydslPredicateExecutor<LeadMaster> {
    @Query(value = "select   t.leadSource.leadSourceName  from LeadMaster t where  t.id=:id ")
    String getLeadSourceNameFromLeadId(Long id);

}
