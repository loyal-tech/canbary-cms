package com.adopt.apigw.modules.Cas.Domain;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CasMasterRepository extends JpaRepository<CasMaster, Long>, QuerydslPredicateExecutor<CasMaster> {

}