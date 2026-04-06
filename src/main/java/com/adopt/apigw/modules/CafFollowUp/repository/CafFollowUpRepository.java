package com.adopt.apigw.modules.CafFollowUp.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.CafFollowUp.domain.CafFollowUp;

@Repository
public interface CafFollowUpRepository extends JpaRepository<CafFollowUp, Long>,QuerydslPredicateExecutor<CafFollowUp>{

	
	@Query(name = "select * from tbltcaffollowup where customer_id=:customersId",nativeQuery = true)
	Page<CafFollowUp> findByCustomersId(@Param("customersId") Integer customersId,Pageable pageable);
	
	@Query(name = "select * from tbltcaffollowup where is_missed=:isMissed AND is_send=:isSend AND status=:status",nativeQuery = true)
	Page<CafFollowUp> findByIsMissedAndIsSendAndStatus(@Param("isMissed") boolean isMissed,@Param("isSend") boolean isSend,@Param("status") String status,Pageable pageable);
	
	@Query(name = "select * from tbltcaffollowup where follow_up_datetime>:fromTime and follow_up_datetime<=:toTime and is_missed = false and is_send = false and send_reminder_notification=false")
	Page<CafFollowUp> findByFollowUpDatetimeBetween(@Param("fromTime") LocalDateTime fromTime,@Param("toTime") LocalDateTime toTime,Pageable pageable);
	
	CafFollowUp findTopByOrderByIdDesc();

	@Query("SELECT s.followUpName FROM CafFollowUp s WHERE s.id = :followUpId")
	String findNameById(@Param("followUpId") Long followUpId);



}
