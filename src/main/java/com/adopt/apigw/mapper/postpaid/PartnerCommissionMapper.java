package com.adopt.apigw.mapper.postpaid;

import com.adopt.apigw.repository.radius.CustomersRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.PartnerCommission;
import com.adopt.apigw.pojo.api.PartnerCommissionPojo;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.spring.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class PartnerCommissionMapper implements IBaseMapper<PartnerCommissionPojo, PartnerCommission> {

    @Override
    public abstract PartnerCommissionPojo domainToDTO(PartnerCommission partnerCommission, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract PartnerCommission dtoToDomain(PartnerCommissionPojo dtoData, @Context CycleAvoidingMappingContext context);
    @Autowired
    private CustomersRepository customersRepository;

    @AfterMapping
    void afterMap(@MappingTarget PartnerCommissionPojo partnerCommissionPojo, PartnerCommission data) {
        CustomersService customersService = SpringContext.getBean(CustomersService.class);
        Customers customers =  customersRepository.findById(data.getCustomerid()).get();
        partnerCommissionPojo.setCustomerName(customers.getFullName());
    }
}
