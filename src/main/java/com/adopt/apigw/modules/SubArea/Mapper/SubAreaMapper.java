package com.adopt.apigw.modules.SubArea.Mapper;


import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.SubArea.DTO.SubAreaDTO;
import com.adopt.apigw.modules.SubArea.Domain.SubArea;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import javax.ws.rs.core.Context;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface  SubAreaMapper extends IBaseMapper<SubAreaDTO, SubArea> {

    @Override
    @Mapping(source = "subAreaDTO.areaId", target = "area", qualifiedByName = "mapAreaById")
    SubArea dtoToDomain(SubAreaDTO subAreaDTO, @Context CycleAvoidingMappingContext context);

    @Named("mapAreaById")
    default Area mapAreaById(Long areaId) {
        return (areaId != null) ? new Area(areaId) : null;
    }

    @Override
    default List<SubAreaDTO> domainToDTO(List<SubArea> data, @Context CycleAvoidingMappingContext context) {
        return data.stream().map(this::mapSubAreaToDTO).collect(Collectors.toList());
    }


    @Mapping(source = "area", target = "areaId", qualifiedByName = "mapArea")
    SubAreaDTO mapSubAreaToDTO(SubArea subArea);

    @Named("mapArea")
    default Long mapArea(Area area) {
        return (area != null) ? area.getId() : null;
    }
}

