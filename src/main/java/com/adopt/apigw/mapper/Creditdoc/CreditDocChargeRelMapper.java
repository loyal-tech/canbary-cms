package com.adopt.apigw.mapper.Creditdoc;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.creditdoc.CreditDocChargeRel;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.pojo.CreditDoc.CreditDocChargeRelDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class CreditDocChargeRelMapper implements IBaseMapper<CreditDocChargeRelDTO, CreditDocChargeRel> {

//    @Override
//    @Mapping(target = "charge", source = "chargeName")
//    public abstract CreditDocChargeRel dtoToDomain(CreditDocChargeRelDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(target = "chargeName", source = "charge")
    public abstract CreditDocChargeRelDTO domainToDTO(CreditDocChargeRel domain, @Context CycleAvoidingMappingContext context);

    String fromChargeToChargeName(Charge charge) {
        return charge == null ? null : charge.getName();
    }
}
