package com.adopt.apigw.modules.tickets.repository;

import com.adopt.apigw.modules.ResolutionReasons.domain.ResolutionReasons;
import com.adopt.apigw.modules.tickets.domain.ResoSubCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResoSubCategoryMappingRepo extends JpaRepository<ResoSubCategoryMapping, Long>, QuerydslPredicateExecutor<ResoSubCategoryMapping> {

}
