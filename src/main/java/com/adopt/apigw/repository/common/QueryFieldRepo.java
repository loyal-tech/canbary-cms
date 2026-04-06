package com.adopt.apigw.repository.common;

import com.adopt.apigw.modules.Teams.domain.QueryFieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryFieldRepo extends JpaRepository<QueryFieldMapping, Integer>, QuerydslPredicateExecutor<QueryFieldMapping> {
}
