package com.adopt.apigw.repository.postpaid;


import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.model.postpaid.PostpaidPlanCharge;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface PostpaidPlanChargeRepo extends JpaRepository<PostpaidPlanCharge, Integer>, QuerydslPredicateExecutor<PostpaidPlanCharge> {

    @Query(value = "select * from TBLMPOSTPAIDPLANCHARGEREL where lower(name) like '%' :search  '%' order by POSTPAIDPLANCHARGERELID", countQuery = "select count(*) from TBLMPOSTPAIDPLANCHARGEREL where lower(name) like '%' :search '%'", nativeQuery = true)
    Page<PostpaidPlanCharge> searchEntity(@Param("search") String searchText, Pageable pageable);

    @Query(value = "select t3.CHARGEID from tblmpostpaidplan t inner join tblmpostpaidplanchargerel t2 on t.POSTPAIDPLANID =t2.POSTPAIDPLANID \n" + "inner join tblcharges t3  on t3.CHARGEID =t2.CHARGEID \n" + "where t.POSTPAIDPLANID =:planId", nativeQuery = true)
    Integer getchargeByPlan(@Param("planId") Integer planId);

    @Query(value = "select t3.PRICE from tblmpostpaidplan t inner join tblmpostpaidplanchargerel t2 on t.POSTPAIDPLANID =t2.POSTPAIDPLANID \n" + "inner join tblcharges t3  on t3.CHARGEID =t2.CHARGEID \n" + "where t.POSTPAIDPLANID =:planId", nativeQuery = true)
    Integer getchargePriceByPlanId(@Param("planId") Integer planId);

    @Query(value = "select t3.CHARGEID from tblmpostpaidplan t inner join tblmpostpaidplanchargerel t2 on t.POSTPAIDPLANID =t2.POSTPAIDPLANID \n" + "inner join tblcharges t3  on t3.CHARGEID =t2.CHARGEID \n" + "where t.POSTPAIDPLANID =:planId", nativeQuery = true)
    List<Integer> getchargelistByPlan(@Param("planId") Integer planId);


    @Query(value = "select t.chargeprice from TBLMPOSTPAIDPLANCHARGEREL t where t.POSTPAIDPLANID =:planId and t.CHARGEID=:chargeId", nativeQuery = true)
    List<Double> getChargeListByChargeIdAndPlanId(@Param("planId") Integer planId,@Param("chargeId") Integer chargeId);

    @Query(value = "select t from PostpaidPlanCharge t where t.plan.id =:planId")
    List<PostpaidPlanCharge> findAllByPlan(@Param("planId") Integer planId);

//    List<PostpaidPlanCharge> findAll(BooleanExpression eq);

//    List<PostpaidPlanCharge> findAllByPlanId();

}
