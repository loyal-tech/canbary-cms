package com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.domain.ReportProblem;

@Repository
public interface ReportProblemRepository extends JpaRepository<ReportProblem, Long> , QuerydslPredicateExecutor<ReportProblem> {


    @Query(value = "select * from tblreportproblem where PHNO =:phno",nativeQuery = true)
    List<ReportProblem> findAllReportProblemByPhno (@Param(value = "phno") Long phno);

    @Query(value = "select * from tblreportproblem where phno =:phno",nativeQuery = true)
    Page<ReportProblem> findAllReportByPhno(Long phno, Pageable pageable);

}
