package com.adopt.apigw.modules.partnerdocDetails.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.customerDocDetails.domain.CustomerDocDetails;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.modules.partnerdocDetails.domain.PartnerdocDetails;
import com.adopt.apigw.modules.partnerdocDetails.model.PartnerdocDTO;
import com.adopt.apigw.repository.postpaid.PartnerRepository;
import com.adopt.apigw.service.postpaid.PartnerService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class PartnerDocDetailsMapper implements IBaseMapper<PartnerdocDTO, PartnerdocDetails> {

    @Autowired
    private PartnerService partnerService;
    @Autowired
    private PartnerRepository partnerRepository;

    @Override
    @Mapping(target = "partner", source = "partnerId")
    public abstract PartnerdocDetails dtoToDomain(PartnerdocDTO dto, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "partner", target = "partnerId")
    public abstract PartnerdocDTO domainToDTO(PartnerdocDetails domain, @Context CycleAvoidingMappingContext context);

    Integer fromPartnerTopartnerId(Partner entity) {
        return entity == null ? null : entity.getId();
    }

    Partner partnerIdToPartner(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Partner entity;
        try {
            entity = partnerRepository.findById(entityId).get();
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
}
