package com.adopt.apigw.modules.tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adopt.apigw.modules.tickets.domain.CaseUpdate;

public interface CaseUpdateRepository extends JpaRepository<CaseUpdate, Long> {
}
