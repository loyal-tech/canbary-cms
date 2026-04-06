package com.adopt.apigw.modules.BankManagement.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.BankManagement.domain.BankManagement;
import com.adopt.apigw.modules.BankManagement.model.BankManagementDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface BankManagementMapper extends IBaseMapper<BankManagementDTO , BankManagement> {

    @Override
    @Mapping(target = "displayId", source = "bankManagement.id")
    @Mapping(target = "displayName", source = "bankManagement.bankname")
    BankManagementDTO domainToDTO(BankManagement bankManagement, CycleAvoidingMappingContext context);

    @Override
    List<BankManagementDTO> domainToDTO(List<BankManagement> bankManagementList, @Context CycleAvoidingMappingContext context);

}
