package com.adopt.apigw.repository.postpaid;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.DunningRuleAction;

@Repository
public interface DunningRuleActionRepository extends JpaRepository<DunningRuleAction, Integer> {

}
