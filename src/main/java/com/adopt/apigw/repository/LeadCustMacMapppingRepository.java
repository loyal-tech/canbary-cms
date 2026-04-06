package com.adopt.apigw.repository;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.lead.LeadCustMacMappping;

@Repository
//@JaversSpringDataAuditable
public interface LeadCustMacMapppingRepository extends JpaRepository<LeadCustMacMappping, Integer>{

	@Query(name = "select * from tblleadcustmacmapping where lead_master_id=:leadId")
	List<LeadCustMacMappping> findByLeadMasterId(@Param("leadId") Long leadId);
}
