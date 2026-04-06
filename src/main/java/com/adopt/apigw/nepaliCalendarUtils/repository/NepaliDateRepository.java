package com.adopt.apigw.nepaliCalendarUtils.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.nepaliCalendarUtils.domain.NepaliDate;

@Repository
public interface NepaliDateRepository extends JpaRepository<NepaliDate, Integer>, QuerydslPredicateExecutor<NepaliDate> {
		Optional<NepaliDate> findByYear(String year);
}
