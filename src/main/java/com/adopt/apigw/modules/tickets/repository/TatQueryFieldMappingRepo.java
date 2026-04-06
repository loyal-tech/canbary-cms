package com.adopt.apigw.modules.tickets.repository;

import com.adopt.apigw.modules.tickets.domain.TatQueryFieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TatQueryFieldMappingRepo extends JpaRepository<TatQueryFieldMapping, Long>, QuerydslPredicateExecutor<TatQueryFieldMapping> {
}
