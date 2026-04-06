package com.adopt.apigw.modules.SubscriberUpdates.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.adopt.apigw.modules.SubscriberUpdates.domain.SubscriberUpdate;

import java.time.LocalDate;
import java.util.List;

public interface SubscriberUpdateRepository extends JpaRepository<SubscriberUpdate, Long> {
    List<SubscriberUpdate> getAllByCustomers_IdOrderByCreatedateDesc(Integer custId);

    List<SubscriberUpdate> getAllByCustomers_Id(Integer custId);

    @Query(value = "select * from tblsubscriberupdates t where date(t.createdate) between :startDate AND :endDate AND t.custid=:custId AND t.operation=:operation", nativeQuery = true)
    List<SubscriberUpdate> findAllByCreatedateBetweenAndCustomers_IdAndOperation(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("custId") Integer custId, @Param("operation") String operation);

    @Query(value = "select * from tblsubscriberupdates t where date(t.createdate) between :startDate AND :endDate AND t.custid=:custId", nativeQuery = true)
    List<SubscriberUpdate> findByStartDateEndDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("custId") Integer custId);

    @Query(value = "select * from tblsubscriberupdates t where t.custid=:custId AND t.operation=:operation", nativeQuery = true)
    List<SubscriberUpdate> findByOperation(@Param("custId") Integer custId, @Param("operation") String operation);
}
