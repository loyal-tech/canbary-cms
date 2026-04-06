package com.adopt.apigw.modules.VoucherBatch.repository;


import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.VoucherBatch.domain.BSSVoucherBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BSSVoucherBatchRepository extends JpaRepository<BSSVoucherBatch, Long>, QuerydslPredicateExecutor<BSSVoucherBatch> {
    @Query("select t.plan from BSSVoucherBatch t where t.voucherBatchId =:voucherBatchId")
    PostpaidPlan findByPlanByVoucherBatch(@Param("voucherBatchId") Long voucherBatchId);

    @Query(value = "select * from TBLMVOUCHERBATCH where voucherbatchid =:voucherbatchid",nativeQuery = true)
    BSSVoucherBatch findVoucherBatchByBatchId(@Param("voucherbatchid") Long voucherbatchid);



}
