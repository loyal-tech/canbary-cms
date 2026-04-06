package com.adopt.apigw.core.mapper;

import java.util.List;

import org.mapstruct.Context;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

public interface IBaseMapper<DTO, DATA> {
    DTO domainToDTO(DATA data, @Context CycleAvoidingMappingContext context);
    DATA dtoToDomain(DTO dtoData, @Context CycleAvoidingMappingContext context);
    List<DTO> domainToDTO(List<DATA> data, @Context CycleAvoidingMappingContext context);

    List<DATA> dtoToDomain(List<DTO> data, @Context CycleAvoidingMappingContext context);

//    @Mappings({
//            @Mapping(target = "mvnoId", ignore = true)
//    })
    DATA updateDTOToDomain(DTO dto, @MappingTarget DATA data, @Context CycleAvoidingMappingContext context);
}

