package com.adopt.apigw.modules.tickets.repository;

import com.adopt.apigw.modules.tickets.domain.CaseFeedbackRel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseFeedbackRelRepository extends JpaRepository<CaseFeedbackRel, Long> {
}
