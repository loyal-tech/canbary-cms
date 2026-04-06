package com.adopt.apigw.modules.CustomerDBR.repository;

import com.adopt.apigw.modules.CustomerDBR.domain.CustomerDBR;
import com.adopt.apigw.modules.CustomerDBR.model.CustomDailyRevenue;
import com.adopt.apigw.modules.CustomerDBR.model.CustomMonthlyRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomMonthlyRevenueRepository extends JpaRepository<CustomMonthlyRevenue, Long>, QuerydslPredicateExecutor<CustomMonthlyRevenue> {

    @Query(value = " select * from tblcustomerdbr t " + "where start_date between :startdate and :endate", nativeQuery = true)
    List<CustomerDBR> getdbrdeatails(@Param(value = "startdate") LocalDate startdate,
                                     @Param(value = "endate") LocalDate endate);
}
