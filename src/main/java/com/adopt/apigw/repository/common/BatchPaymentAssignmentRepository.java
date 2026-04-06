package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.common.BatchPayment;
import com.adopt.apigw.model.common.BatchPaymentAssignment;
import com.adopt.apigw.model.common.StaffUser;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@JaversSpringDataAuditable
public interface BatchPaymentAssignmentRepository extends JpaRepository<BatchPaymentAssignment, Long>, QuerydslPredicateExecutor<BatchPaymentAssignment> {

    @Query(value = "select count(*) from tbltbatchpaymentassignment t where  t.staff_id = :id", nativeQuery = true)
    public Long findMinimumApprovalReuqestByStaff(@Param("id") Integer id);

    @Query(value = "select * from tbltbatchpaymentassignment t where  t.batch_id = :batchId and t.staff_id= :staffId", nativeQuery = true)
    public List<BatchPaymentAssignment> findByBatchPaymentAndStaffUser(@Param("batchId")  Long batchId,@Param("staffId") Integer staffId);

    @Query(value = "select * from tbltbatchpaymentassignment t where  t.staff_id = :staffId", nativeQuery = true)
    public List<BatchPaymentAssignment> findByStaffId(@Param("staffId") Long staffId);

    @Query(value = "select * from tbltbatchpaymentassignment t where t.batch_id= :batchId and t.next_staff_id = :staffId", nativeQuery = true)
    public List<BatchPaymentAssignment> findPreviousAssigne(@Param("batchId") Long batchId,@Param("staffId") Integer staffId);

    @Query(value = "select * from tbltbatchpaymentassignment t where t.batch_id= :batchId and t.staff_id = :staffId", nativeQuery = true)
    public BatchPaymentAssignment findNextAssignee(@Param("batchId") Long batchId,@Param("staffId") Integer staffId);

    Optional<BatchPaymentAssignment> findTopByBatchPaymentAndStaffUserOrderByAssignedDateDesc(
            BatchPayment batchPayment, StaffUser staffUser
    );
}

