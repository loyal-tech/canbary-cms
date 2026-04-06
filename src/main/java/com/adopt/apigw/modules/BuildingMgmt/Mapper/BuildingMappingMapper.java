package com.adopt.apigw.modules.BuildingMgmt.Mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.BuildingMgmt.DTO.BuildingManagementDTO;
import com.adopt.apigw.modules.BuildingMgmt.DTO.BuildingMappingDTO;
import com.adopt.apigw.modules.BuildingMgmt.Domain.BuildingManagement;
import com.adopt.apigw.modules.BuildingMgmt.Domain.BuildingMapping;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.stream.Collectors;



@Mapper
public abstract class BuildingMappingMapper implements IBaseMapper<BuildingMappingDTO, BuildingMapping> {

    @Override
    @Mappings({
            @Mapping(source = "buildingManagement.buildingMgmtId", target = "buildingMgmtId") // Map entity ID to DTO
    })
    public abstract BuildingMappingDTO domainToDTO(BuildingMapping entity, @Context CycleAvoidingMappingContext context);
//    @Override
//    @Mappings({
//            @Mapping(target = "buildingManagement.id", source = "buildingMgmtId"),  // Fix: Ensuring correct list mapping
//            //@Mapping(target = "displayName", source = "name")  // Example for another field
//    })
//    public abstract BuildingMappingDTO domainToDTO(BuildingMapping domain, @Context CycleAvoidingMappingContext context);

    @Override
    @Mappings({
            @Mapping(target = "buildingManagement", expression = "java(mapBuildingManagement(dto))") // Custom method to set the reference properly
    })
    public abstract BuildingMapping dtoToDomain(BuildingMappingDTO dto, @Context CycleAvoidingMappingContext context);

    public List<BuildingMappingDTO> domainToDtoList(List<BuildingMapping> entities, @Context CycleAvoidingMappingContext context) {
        return entities.stream().map(entity -> domainToDTO(entity, context)).collect(Collectors.toList());
    }

    public List<BuildingMapping> dtoToDomainList(List<BuildingMappingDTO> dtos, @Context CycleAvoidingMappingContext context) {
        return dtos.stream().map(dto -> dtoToDomain(dto, context)).collect(Collectors.toList());
    }

    protected BuildingManagement mapBuildingManagement(BuildingMappingDTO dto) {
        if (dto.getBuildingMgmtId() == null) {
            return null;
        }
        BuildingManagement buildingManagement = new BuildingManagement();
        buildingManagement.setBuildingMgmtId(dto.getBuildingMgmtId());
        return buildingManagement;
    }
}



