package com.adopt.apigw.modules.SubBusinessVertical.Mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.BusinessVerticals.DTO.BusinessVerticalsDTO;
import com.adopt.apigw.modules.BusinessVerticals.Mapper.BusinessVerticalsMpper;
import com.adopt.apigw.modules.BusinessVerticals.Respository.BusinessVerticalsRepository;
import com.adopt.apigw.modules.BusinessVerticals.Service.BusinessVerticalsService;
import com.adopt.apigw.modules.BusinessVerticals.domain.BusinessVerticals;
import com.adopt.apigw.modules.SubBusinessVertical.Domain.SubBusinessVertical;
import com.adopt.apigw.modules.SubBusinessVertical.Model.SubBusinessVerticalDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class SubBusinessVerticalMapper implements IBaseMapper<SubBusinessVerticalDTO, SubBusinessVertical> {

    @Autowired
    BusinessVerticalsService businessVerticalsService;
    @Autowired
    BusinessVerticalsRepository businessVerticalsRepository;

    @Autowired
    BusinessVerticalsMpper businessVerticalsMpper;

   // @Mapping(source = "buVerticalsId", target = "businessVerticals")
    public abstract SubBusinessVertical dtoToDomain(SubBusinessVerticalDTO dtoData,@Context CycleAvoidingMappingContext context);

    //@Mapping(source = "businessVerticals", target = "buVerticalsId")
    public abstract SubBusinessVerticalDTO domainToDTO(SubBusinessVertical data,@Context CycleAvoidingMappingContext context);

    Integer fromBusinessVerticalToId(BusinessVerticals entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    BusinessVerticals fromIdToBusinessVertical(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        BusinessVerticals entity;
        try {
//            BusinessVerticalsDTO entityDTO = businessVerticalsService.getEntityById(entityId.longValue());
            entity = businessVerticalsRepository.findById(entityId.longValue()).get();
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

}
