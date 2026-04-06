package com.adopt.apigw.repository;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.lead.LeadDocDetails;

@Repository
//@JaversSpringDataAuditable
public interface LeadDocDetailsRepository extends JpaRepository<LeadDocDetails, Long>{

	@Query(name = "select * from TBLMLEADDOCDETAILS where lead_master_id=:leadMasterId",nativeQuery = true)
	List<LeadDocDetails> findByLeadMasterId(@Param("leadMasterId") Long leadMaterId);
}
