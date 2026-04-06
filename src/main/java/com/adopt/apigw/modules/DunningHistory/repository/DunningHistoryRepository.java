package com.adopt.apigw.modules.DunningHistory.repository;

import com.adopt.apigw.modules.DunningHistory.domain.DunningHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DunningHistoryRepository extends JpaRepository<DunningHistory, Long>, QuerydslPredicateExecutor<DunningHistory> {

    @Query(value = "select * from tbldunninghistory where custid IS Not NULL",nativeQuery = true)
    List<DunningHistory> findAllCustomerDunningHistory();

    @Query(value = "select * from tbldunninghistory where partnerid IS Not NULL",nativeQuery = true)
    List<DunningHistory> findAllPartnerDunningHistory();

    @Query(value = "select COUNT(*) from tbldunninghistory where (event_name =:eventName OR event_name IS NULL) AND (action =:action OR action IS NULL) AND (staffid =:staffid OR staffid IS NULL) AND (custid =:custid OR custid IS NULL) AND (partnerid =:partnerid OR partnerid IS NULL)",nativeQuery = true)
    Integer CountDunningHappen(@Param("eventName")String eventName, @Param("action") String action, @Param("staffid") Long staffid,@Param("custid") Integer custid,@Param("partnerid") Long partnerid);

    @Query(value = "select * from tbldunninghistory where (event_name =:eventName OR event_name IS NULL) AND (action =:action OR action IS NULL) AND (staffid =:staffid OR staffid IS NULL) AND (custid =:custid OR custid IS NULL) AND (partnerid =:partnerid OR partnerid IS NULL)",nativeQuery = true)
    List<DunningHistory> CountDunningHappenWithAllHistory(@Param("eventName")String eventName, @Param("action") String action, @Param("staffid") Long staffid,@Param("custid") Integer custid,@Param("partnerid") Long partnerid);




}
