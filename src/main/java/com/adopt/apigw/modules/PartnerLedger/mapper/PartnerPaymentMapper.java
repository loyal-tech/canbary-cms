package com.adopt.apigw.modules.PartnerLedger.mapper;

import com.adopt.apigw.repository.postpaid.PartnerRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerPayment;
import com.adopt.apigw.modules.PartnerLedger.model.PartnerPaymentDTO;
import com.adopt.apigw.service.postpaid.PartnerService;

@Mapper
public abstract class PartnerPaymentMapper implements IBaseMapper<PartnerPaymentDTO, PartnerPayment> {

    @Mapping(source = "partner",target = "partnerId")
    public abstract PartnerPaymentDTO domainToDTO(PartnerPayment data, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "partnerId",target = "partner")
    public abstract PartnerPayment dtoToDomain(PartnerPaymentDTO dto,@Context CycleAvoidingMappingContext context);

    @Autowired
    private PartnerService partnerService;
    @Autowired
    private PartnerRepository partnerRepository;

    PartnerPayment fromId(final Long id){
        if(id==null){
            return null;
        }
        final PartnerPayment partnerPayment=new PartnerPayment();
        partnerPayment.setId(id);
        return  partnerPayment;
    }

    Integer fromPartner(Partner entity){return entity==null?null:entity.getId();}

    Partner fromPartnerId(Integer entityId){
        if(entityId==null){
            return null;
        }
        Partner entity=null;
        try{
            entity=partnerRepository.findById(entityId).get();
        }catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return  entity;
    }
}
