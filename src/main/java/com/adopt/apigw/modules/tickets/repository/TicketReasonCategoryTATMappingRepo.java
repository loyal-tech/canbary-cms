package com.adopt.apigw.modules.tickets.repository;

import com.adopt.apigw.modules.tickets.domain.TicketReasonCategoryTATMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketReasonCategoryTATMappingRepo  extends JpaRepository<TicketReasonCategoryTATMapping, Long>, QuerydslPredicateExecutor<TicketReasonCategoryTATMapping> {

    TicketReasonCategoryTATMapping findByTicketReasonCategoryIdAndOrderNumber(Long ticketReasonCategoryId, Long orderNumber);
}
