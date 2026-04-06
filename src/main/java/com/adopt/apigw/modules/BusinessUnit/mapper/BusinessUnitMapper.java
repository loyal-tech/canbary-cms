package com.adopt.apigw.modules.BusinessUnit.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.Branch.model.BranchDTO;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.BusinessUnit.model.BusinessUnitDTO;
import com.adopt.apigw.modules.InvestmentCode.DTO.InvestmentCodeDto;
import com.adopt.apigw.modules.InvestmentCode.Domain.InvestmentCode;
import com.adopt.apigw.modules.InvestmentCode.mapper.InvestmentCodeMapper;
import com.adopt.apigw.modules.InvestmentCode.repository.InvestmentCodeRepository;
import com.adopt.apigw.modules.InvestmentCode.service.InvestmentCodeService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class BusinessUnitMapper implements IBaseMapper<BusinessUnitDTO, BusinessUnit> {

    @Autowired
    private InvestmentCodeService investmentCodeService;
    @Autowired
    InvestmentCodeRepository investmentCodeRepository;

    @Autowired
    private InvestmentCodeMapper investmentCodeMapper;

    @Override
//    @Mapping(source ="dtoData.investmentcode_id",target="investmentCodeList")
    public  abstract BusinessUnit dtoToDomain(BusinessUnitDTO dtoData, CycleAvoidingMappingContext context);

    @Override
    @Mapping(target = "displayId", source = "data.id")
    @Mapping(target = "displayName", source = "data.buname")
//    @Mapping(source = "data.investmentCodeList", target = "investmentcode_id")
    public abstract BusinessUnitDTO domainToDTO(BusinessUnit data,CycleAvoidingMappingContext context);

    Long fromIcNameToId(InvestmentCode entity){return entity == null ? null : entity.getId();}

    InvestmentCode fromIdToIcName(Long entityId) {
        if (entityId == null) {
            return null;
        }
        InvestmentCode entity;
        try {
             entity = investmentCodeRepository.findById(entityId).get();
//            entity = investmentCodeMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
}
