package com.adopt.apigw.modules.TicketFollowUp.Repository;
//
//import com.adopt.apigw.modules.CafFollowUp.domain.CafFollowUp;
////import com.adopt.apigw.modules.TicketFollowUp.Domain.TicketFollowUp;
//import com.adopt.apigw.modules.tickets.domain.Case;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.querydsl.QuerydslPredicateExecutor;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//
//@Repository
public interface TicketFollowUpRepository  {
//
//
////    @Query(name = "select * from tbltticketfollowup where case_id=:caseId",nativeQuery = true)
//    Page<TicketFollowUp> findByTicket(@Param("caseId") Case aCase, Pageable pageable);
//
//    @Query(name = "select * from tbltticketfollowup where is_missed=:isMissed AND is_send=:isSend AND status=:status",nativeQuery = true)
//    Page<TicketFollowUp> findByIsMissedAndIsSendAndStatus(@Param("isMissed") boolean isMissed,@Param("isSend") boolean isSend,@Param("status") String status,Pageable pageable);
//
//    @Query(name = "select * from tbltticketfollowup where follow_up_datetime>:fromTime and follow_up_datetime<=:toTime and is_missed = false and is_send = false and send_reminder_notification=false")
//    Page<TicketFollowUp> findByFollowUpDatetimeBetween(@Param("fromTime") LocalDateTime fromTime, @Param("toTime") LocalDateTime toTime, Pageable pageable);
//
//    TicketFollowUp findTopByOrderByIdDesc();
}
