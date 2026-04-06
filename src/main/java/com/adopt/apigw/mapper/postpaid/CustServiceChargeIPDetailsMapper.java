package com.adopt.apigw.mapper.postpaid;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.CustServiceChargeIPDetails;
import com.adopt.apigw.model.postpaid.PostpaidPlanCharge;
import com.adopt.apigw.pojo.api.CustServiceChargeIPDetailsPojo;
import com.adopt.apigw.pojo.api.PostpaidPlanChargePojo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class CustServiceChargeIPDetailsMapper implements IBaseMapper<CustServiceChargeIPDetailsPojo, CustServiceChargeIPDetails> {

    @Override
    public abstract CustServiceChargeIPDetails dtoToDomain(CustServiceChargeIPDetailsPojo pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CustServiceChargeIPDetailsPojo domainToDTO(CustServiceChargeIPDetails domain, @Context CycleAvoidingMappingContext context);
}
