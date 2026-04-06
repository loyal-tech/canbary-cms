package com.adopt.apigw.modules.Region.Mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.Branch.mapper.BranchMapper;
import com.adopt.apigw.modules.Branch.model.BranchDTO;
import com.adopt.apigw.modules.Branch.repository.BranchRepository;
import com.adopt.apigw.modules.Branch.service.BranchService;
import com.adopt.apigw.modules.Region.domain.Region;
import com.adopt.apigw.modules.Region.model.RegionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class RegionMapper implements IBaseMapper<RegionDTO , Region> {

    @Autowired
    private BranchService branchService;

    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private BranchRepository branchRepository;

    @Override
    @Mapping(source = "dtoData.branchid", target = "branchidList")
    public abstract Region dtoToDomain(RegionDTO dtoData, CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "data.branchidList", target = "branchid")
    public abstract RegionDTO domainToDTO(Region data, CycleAvoidingMappingContext context);

    Integer fromBranchToId(Branch entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    Branch fromIdToBranch(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Branch entity;
        try {
//            BranchDTO entityDTO = branchService.getEntityById(entityId.longValue());
            entity =branchRepository.findById(entityId.longValue()).get();
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }


}
