package com.adopt.apigw.modules.tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adopt.apigw.modules.tickets.domain.CaseUpdateDetails;

public interface CaseUpdateDetailsRepository extends JpaRepository<CaseUpdateDetails, Long> {
}
