package com.adopt.apigw.modules.ServiceArea.repository;

import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceAreaPincodeRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ServiceAreaPincodeRelRepository  extends JpaRepository<ServiceAreaPincodeRel, Long>, QuerydslPredicateExecutor<ServiceAreaPincodeRel> {
}
