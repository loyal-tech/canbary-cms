package com.adopt.apigw.modules.Teams.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Teams.domain.Hierarchy;
import com.adopt.apigw.modules.Teams.model.HierarchyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public abstract class HierarchyMapper implements IBaseMapper<HierarchyDTO, Hierarchy> {


//    @Override
//    @Mappings({
//            @Mapping(target = "mvnoId", ignore = true)
//    })
//    public Hierarchy updateDTOToDomain(HierarchyDTO hierarchyDTO, Hierarchy hierarchy, CycleAvoidingMappingContext context) {
//        return hierarchy;
//    }
}
