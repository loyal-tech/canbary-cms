package com.adopt.apigw.modules.tickets.repository;

import com.adopt.apigw.modules.tickets.domain.TicketAssignStaffMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketAssignStaffMappingRepo extends JpaRepository<TicketAssignStaffMapping, Long>, QuerydslPredicateExecutor<TicketAssignStaffMapping> {

}
