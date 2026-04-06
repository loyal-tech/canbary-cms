package com.adopt.apigw.repository.postpaid;

import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.CustomerChargeHistory;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

//@JaversSpringDataAuditable
@Repository
public interface CustomerChargeHistoryRepo extends JpaRepository<CustomerChargeHistory, Integer>, QuerydslPredicateExecutor<CustomerChargeHistory> {

    @Query(value = "select * from tbltcustchargehistory where cust_id= :customerId AND plan_id= :planId AND cust_plan_mapping_id= :custPackageId", nativeQuery = true)
    CustomerChargeHistory findByPlandIdAndCustIdAndCprId(@Param("customerId") Long customerId,@Param("planId") Long planId, @Param("custPackageId") Long custPackageId);


    @Query(value = " SELECT * from tbltcustchargehistory  WHERE  cust_plan_mapping_id= :cust_plan_mapping_id", nativeQuery = true)
    List<CustomerChargeHistory> findAllChargesByCprId(@Param("cust_plan_mapping_id") Integer custPlanMappingId);

    @Query(value = " SELECT * from tbltcustchargehistory  WHERE  cust_id= :custid AND next_charge_billdate= :nextbilldate", nativeQuery = true)
    List<CustomerChargeHistory> findAllChargesByCustIdAndNextBillDate(@Param("custid") Integer custId,@Param("nextbilldate") LocalDate nextBillDate);

    @Query(value = "select * from tbltcustchargehistory where cust_id= :customerId AND charge_id= :chargeId", nativeQuery = true)
    CustomerChargeHistory findByCustIdAndChargeId(@Param("customerId") Integer customerId, @Param("chargeId") Integer chargeId);

    boolean existsByCustomerIdAndChargeId(Integer custId, Integer chargeId);

    boolean existsByCustPlanMapppingId(Integer custpackrelId);

    List<CustomerChargeHistory>  findAllByCustomerId(Integer custId);

    List<CustomerChargeHistory> findByCustPlanMapppingIdIn(Set<Integer> cprIds);

    Set<CustomerChargeHistory> findByCustPlanMapppingIdIn(List<Integer> cprIds);

    @Query(value = "select t.history_id from tbltcustchargehistory t where t.plan_id =:planId and t.charge_id =:chargeId",nativeQuery = true)
    List<Integer> getChargeHistoryIdsByPlanIdandchargeId(@Param("planId") Integer planId, @Param("chargeId") Integer chargeId);



}
