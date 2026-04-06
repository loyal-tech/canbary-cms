package com.adopt.apigw.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.lead.LeadSource;

@Repository
//@JaversSpringDataAuditable
public interface LeadSourceRepository extends JpaRepository<LeadSource, Long>{

}
