package com.adopt.apigw.modules.ServiceParameters.repository;

import com.adopt.apigw.modules.ServiceParameters.domain.ServiceParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServcieParametersRepository extends JpaRepository<ServiceParameter,Long>, QuerydslPredicateExecutor<ServiceParameter> {
    ServiceParameter findAllById(Long Id);
}
