package com.adopt.apigw.modules.DunningRuleBranchMapping.repository;

import com.adopt.apigw.modules.DunningRuleBranchMapping.domain.DunningRuleBranchMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DunningRuleBranchMappingRepository extends JpaRepository<DunningRuleBranchMapping , Integer> {


    @Query(value = "select DISTINCT t.partner_id from tbltdunningrulebranchmapping t where t.dunning_rule_id =:dunningRuleId",nativeQuery = true)
    List<Long> findAllPartnerIdByDunningId(@Param("dunningRuleId") Integer dunningRuleId);

    @Query(value = "select  DISTINCT t.branch_id from tbltdunningrulebranchmapping t where t.dunning_rule_id =:dunningRuleId",nativeQuery = true)
    List<Long> findAllBranchIdByDunningId(@Param("dunningRuleId") Integer dunningRuleId);

    @Query(value = "select DISTINCT t.service_area_id from tbltdunningrulebranchmapping t where t.dunning_rule_id =:dunningRuleId",nativeQuery = true)
    List<Long> findAllServiceAreaByDunningId(@Param("dunningRuleId") Integer dunningRuleId);

    @Query(value = "select  DISTINCT * from tbltdunningrulebranchmapping t where t.dunning_rule_id =:dunningRuleId",nativeQuery = true)
    List<DunningRuleBranchMapping> findAllByByDunningId(@Param("dunningRuleId") Integer dunningRuleId);
}
