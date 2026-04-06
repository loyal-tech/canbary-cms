package com.adopt.apigw.modules.TicketFollowupDetail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adopt.apigw.modules.TicketFollowupDetail.domain.TicketFollowupDetail;

public interface TicketFollowupDetailRepository extends JpaRepository<TicketFollowupDetail, Long> {

	List<TicketFollowupDetail> getAllByCaseId(Long caseId);

}
