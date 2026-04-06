package com.adopt.apigw.modules.tickets.repository;

import com.adopt.apigw.modules.ResolutionReasons.model.RootCauseResolutionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RootCauseReasonRepository extends JpaRepository<RootCauseResolutionMapping, Long> {
}
