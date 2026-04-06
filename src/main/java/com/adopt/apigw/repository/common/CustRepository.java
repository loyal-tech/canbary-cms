package com.adopt.apigw.repository.common;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.common.Customers;

//@JaversSpringDataAuditable
@Repository
public interface CustRepository extends PagingAndSortingRepository<Customers, Long> {

}
