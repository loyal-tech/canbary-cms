package com.adopt.apigw.modules.InventoryManagement.inward;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.mapper.ServiceAreaMapper;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import org.mapstruct.Context;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.adopt.apigw.core.mapper.IBaseMapper;
import org.mapstruct.Mapper;

@Mapper
public abstract class InwardMapper implements IBaseMapper<InwardDto,Inward> {

    @Autowired
    private ServiceAreaService serviceAreaService;
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Mapping(source = "serviceArea", target = "serviceAreaId")
    @Mapping(source = "createdByName", target = "createdBy")
    @Override
    public abstract InwardDto domainToDTO(Inward data, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "serviceAreaId", target = "serviceArea")
    @Override
    public abstract Inward dtoToDomain(InwardDto dtoData, @Context CycleAvoidingMappingContext context);

    Long fromServiceAreaToId(ServiceArea entity) {
        return entity == null ? null : entity.getId();
    }

    ServiceArea fromServiceAreaIdToServiceArea(Long entityId) {
        if (entityId == null) {
            return null;
        }
        ServiceArea entity;
        try {
            ServiceAreaDTO entityDTO = serviceAreaService.getEntityById(entityId, false);
            entity = serviceAreaMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
}
