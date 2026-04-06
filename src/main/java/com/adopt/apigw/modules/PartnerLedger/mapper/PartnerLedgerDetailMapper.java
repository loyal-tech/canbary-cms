package com.adopt.apigw.modules.PartnerLedger.mapper;

import com.adopt.apigw.repository.postpaid.PartnerRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedgerDetails;
import com.adopt.apigw.modules.PartnerLedger.model.PartnerLedgerDetailsDTO;
import com.adopt.apigw.service.postpaid.PartnerService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper
public abstract class PartnerLedgerDetailMapper implements IBaseMapper<PartnerLedgerDetailsDTO, PartnerLedgerDetails> {

    @Mapping(source = "partner",target = "partnerId")
    @Mapping(source = "data.createDate", target = "createDate")
    public abstract PartnerLedgerDetailsDTO domainToDTO(PartnerLedgerDetails data, @Context CycleAvoidingMappingContext context);
    @Mapping(source = "partnerId",target = "partner")
    public abstract PartnerLedgerDetails dtoToDomain(PartnerLedgerDetailsDTO dto,@Context CycleAvoidingMappingContext context);

    @Autowired
    private PartnerService partnerService;
    @Autowired
    PartnerRepository partnerRepository;

    PartnerLedgerDetails fromId(final Long id){
        if(id==null){
            return null;
        }
        final PartnerLedgerDetails partnerLedgerDetails=new PartnerLedgerDetails();
        partnerLedgerDetails.setId(id);
        return  partnerLedgerDetails;
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

    LocalDate fromCreateDateTimeToCreateDate(LocalDateTime entity) {
        if (entity == null) {
            return null;
        } else {
            return entity.toLocalDate();
        }
    }

    LocalDateTime fromCreateDateToCreateDateTime(LocalDate entity) {
        if (entity == null) {
            return null;
        } else {
            return entity.atStartOfDay();
        }
    }

}
