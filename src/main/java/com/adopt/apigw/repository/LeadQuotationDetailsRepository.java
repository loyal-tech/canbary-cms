package com.adopt.apigw.repository;

import com.adopt.apigw.model.lead.LeadQuotationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadQuotationDetailsRepository extends JpaRepository<LeadQuotationDetails, Long>, QuerydslPredicateExecutor<LeadQuotationDetails> {
    LeadQuotationDetails findByQuotationDetailId(Long quotationId);
}
