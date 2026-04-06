package com.adopt.apigw.repository;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.lead.LeadCustChargeDetails;

@Repository
//@JaversSpringDataAuditable
public interface LeadCustChargeDetailsRepository extends JpaRepository<LeadCustChargeDetails, Integer>{

	@Query(name = "select * from tblleadcustchargedtls where lead_master_id=:leadId")
	List<LeadCustChargeDetails> findByLeadMasterId(@Param("leadId") Long leadId);
}
