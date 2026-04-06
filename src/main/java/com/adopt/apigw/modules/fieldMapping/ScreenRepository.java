package com.adopt.apigw.modules.fieldMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface ScreenRepository extends JpaRepository<Screens,Long>, QuerydslPredicateExecutor<Screens> {

    List<Screens> findIdByScreenname(String name);
}
