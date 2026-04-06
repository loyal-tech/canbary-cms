package com.adopt.apigw.modules.VoucherConfiguration.repository;


import com.adopt.apigw.modules.VoucherConfiguration.domain.VoucherConfiguration;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
@JaversSpringDataAuditable
public interface VoucherConfigurationRepository extends JpaRepository<VoucherConfiguration, Long>, QuerydslPredicateExecutor<VoucherConfiguration> {

}
