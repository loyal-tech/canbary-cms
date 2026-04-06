package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.common.CustMilestoneDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustMilestoneDetailsRepository extends JpaRepository<CustMilestoneDetails, Long> {

    List<CustMilestoneDetails> findAllByCustomers_id(Long customerId);
    List<CustMilestoneDetails> findAllByLeadMaster_id(Long leadId);
}
