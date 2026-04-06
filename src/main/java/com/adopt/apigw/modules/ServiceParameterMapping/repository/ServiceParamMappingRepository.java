package com.adopt.apigw.modules.ServiceParameterMapping.repository;

import com.adopt.apigw.modules.ServiceParameterMapping.domain.ServiceParamMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
public interface ServiceParamMappingRepository extends JpaRepository<ServiceParamMapping,Long> , QuerydslPredicateExecutor<ServiceParamMapping> {
    List<ServiceParamMapping> findByServiceid(Long serviceId);

    ServiceParamMapping findByServiceidAndServiceParamId(Long serviceId, Long serviceParamId);
}
