package com.adopt.apigw.modules.BusinessVerticals.Mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
//import com.adopt.apigw.modules.BusinessUnit.domain.BusinessRegion;
//import com.adopt.apigw.modules.BusinessUnit.mapper.BusinessRegionMapper;
//import com.adopt.apigw.modules.BusinessUnit.model.BusinessRegionDTO;
//import com.adopt.apigw.modules.BusinessUnit.service.BusinessRegionService;
import com.adopt.apigw.modules.BusinessVerticals.DTO.BusinessVerticalsDTO;
import com.adopt.apigw.modules.BusinessVerticals.domain.BusinessVerticals;
import com.adopt.apigw.modules.Region.Mapper.RegionMapper;
import com.adopt.apigw.modules.Region.domain.Region;
import com.adopt.apigw.modules.Region.model.RegionDTO;
import com.adopt.apigw.modules.Region.repository.RegionRepository;
import com.adopt.apigw.modules.Region.service.RegionService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class BusinessVerticalsMpper implements IBaseMapper<BusinessVerticalsDTO, BusinessVerticals>
{
    @Autowired
    private RegionService regionService;
    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private RegionMapper regionMapper;

    @Override
    @Mapping(source = "dtoData.region_id", target = "buregionidList")
    public abstract BusinessVerticals dtoToDomain(BusinessVerticalsDTO dtoData, CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "data.buregionidList", target = "region_id")
    public abstract BusinessVerticalsDTO domainToDTO(BusinessVerticals data, CycleAvoidingMappingContext context);

    Integer fromRegionToId(Region entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    Region fromIdToRegion(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Region entity;
        try {
             entity = regionRepository.findById(entityId.longValue()).get();
//            entity = regionMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
}
