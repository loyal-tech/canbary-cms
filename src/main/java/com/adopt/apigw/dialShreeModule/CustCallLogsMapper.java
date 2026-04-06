package com.adopt.apigw.dialShreeModule;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustCallLogsMapper {

    @Mapping(target = "dynamicData", source = "dynamicDataDTO")
    CustCallLogs dtoToDomain(CustCallLogsDTO dto,  @Context CycleAvoidingMappingContext context);
}
