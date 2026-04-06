package com.adopt.apigw.modules.fieldMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepo extends JpaRepository<Fields, Long> , QuerydslPredicateExecutor<Fields> {
    Fields findByFieldname(String name);

}
