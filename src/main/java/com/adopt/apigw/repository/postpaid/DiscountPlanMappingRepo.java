package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.DiscountPlanMapping;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface DiscountPlanMappingRepo extends JpaRepository<DiscountPlanMapping, Integer> {

    List<DiscountPlanMapping> findByDiscountId(Integer discountId);


}
