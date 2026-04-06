package com.adopt.apigw.modules.Teams.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Teams.domain.Hierarchy;
import com.adopt.apigw.modules.Teams.domain.QueryFieldMapping;
import com.adopt.apigw.modules.Teams.model.HierarchyDTO;
import com.adopt.apigw.modules.Teams.model.QueryFieldDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class QueryFieldMapper implements IBaseMapper<QueryFieldDTO, QueryFieldMapping> {


//    @Mapping(source = "teamHierarchyMapping.teamId", target = "teamId")
//    public abstract QueryFieldDTO domainToDTO(QueryFieldMapping data, @Context CycleAvoidingMappingContext context);
//
//
//    @Mapping(source = "teamId", target = "teamHierarchyMapping.teamId")
//    public abstract QueryFieldMapping dtoToDomain(QueryFieldDTO dtoData, @Context CycleAvoidingMappingContext context);


}
