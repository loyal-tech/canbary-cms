package com.adopt.apigw.modules.tickets.repository;

import com.adopt.apigw.modules.tickets.domain.Case;
import com.adopt.apigw.modules.tickets.domain.CaseDocDetails;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseDocDetailsRepository extends JpaRepository<CaseDocDetails,Long>,  QuerydslPredicateExecutor<CaseDocDetails> {

    List<CaseDocDetails> findAllByTicketId(Long ticketId);

}
