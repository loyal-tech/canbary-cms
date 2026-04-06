package com.adopt.apigw.modules.TimeBasePolicy.repository;

import com.adopt.apigw.modules.TimeBasePolicy.domain.TimeBasePolicyDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerTimeBasePolicyDetailsRepository extends JpaRepository<TimeBasePolicyDetails, Long> {
    @Query(value = "select * from tbltimebasepolicydetails", nativeQuery = true)
    List<TimeBasePolicyDetails> findAll();

    @Query(value = "select t.* from tbltimebasepolicydetails t left join tblmpostpaidplan t2 on t2.timebasepolicyid = t.policy_id where t2.POSTPAIDPLANID =:id", nativeQuery = true)
    List<TimeBasePolicyDetails> findBytimebasepolicyid(@Param("id") Integer id);

}
