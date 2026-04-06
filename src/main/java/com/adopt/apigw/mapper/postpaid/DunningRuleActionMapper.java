package com.adopt.apigw.mapper.postpaid;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.DunningRuleAction;
import com.adopt.apigw.pojo.api.DunningRuleActionPojo;

@Mapper
public interface DunningRuleActionMapper extends IBaseMapper<DunningRuleActionPojo, DunningRuleAction> {
}
