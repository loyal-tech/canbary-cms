package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.common.VasPlanCharge;
import com.adopt.apigw.pojo.api.VasPlanChargePojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VasPlanChargeRepository extends JpaRepository<VasPlanCharge,Integer>, QuerydslPredicateExecutor<VasPlanCharge> {

    List<VasPlanCharge> findByVasPlanId(Integer vasPlanId);
    boolean existsByVasPlan_Id(Integer vasPlanId);

}
