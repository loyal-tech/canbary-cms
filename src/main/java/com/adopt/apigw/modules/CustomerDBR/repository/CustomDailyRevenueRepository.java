package com.adopt.apigw.modules.CustomerDBR.repository;

import com.adopt.apigw.modules.CustomerDBR.model.CustomDailyRevenue;
import com.adopt.apigw.pojo.AggregateCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomDailyRevenueRepository extends JpaRepository<CustomDailyRevenue, Long>, QuerydslPredicateExecutor<CustomDailyRevenue> {
    @Query(value = "select * from tbldailyrevenue where date between :startDate and :endDate",nativeQuery = true)
    List<CustomDailyRevenue> getAllDbrBetweenStartDateAndEndDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT mvnoid AS mvnoId, buid AS buId, service_area_id AS serviceAreaId FROM tbldailyrevenue GROUP BY mvnoid, buid, service_area_id",nativeQuery = true)
    List<AggregateCount> getAllByAggregateByDate();
}
