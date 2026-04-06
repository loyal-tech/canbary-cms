package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.DiscountMapping;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface DiscountMappingRepository extends JpaRepository<DiscountMapping, Integer> , QuerydslPredicateExecutor<DiscountMapping> {
    List<DiscountMapping> findByDiscountId(Integer discountId);

    @Query(value = "SELECT dm.* " +
                   "FROM TBLMDISCOUNTFIELDMAPPING dm " +
                   "INNER JOIN TBLMDISCOUNT d ON d.DISCOUNTID = dm.DISCOUNTID " +
                   "INNER JOIN TBLMDISCOUNTPOSTPAIDPLANREL rel ON rel.DISCOUNTID = d.DISCOUNTID " +
                   "WHERE d.IS_DELETE = 0 AND LOWER(dm.DISCOUNTTYPE) = 'percentage' " +
                   "AND rel.POSTPAIDPLANID = :planId " +
                   "AND (dm.VALIDFROM IS NULL OR dm.VALIDFROM <= CURRENT_DATE) " +
                   "AND (dm.VALIDUPTO IS NULL OR dm.VALIDUPTO >= CURRENT_DATE)",
            nativeQuery = true)
    List<DiscountMapping> findMappingsByPlanId(@Param("planId") Integer planId);
}
