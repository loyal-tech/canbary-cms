package com.adopt.apigw.repository.radius;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.adopt.apigw.model.radius.Plan;


//@JaversSpringDataAuditable
@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer>, QuerydslPredicateExecutor<Plan> {
    @Query(value = "select * from tblmplanmaster where lower(name) like '%' || :search || '%'  order by id desc",
            countQuery = "select count(*) from tblmplanmaster where lower(name) like '%' || :search || '%' ",
            nativeQuery = true)
    Page<Plan> findPlans(@Param("search") String searchText, Pageable pageable);


}
