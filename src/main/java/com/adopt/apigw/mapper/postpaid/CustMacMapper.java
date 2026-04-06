package com.adopt.apigw.mapper.postpaid;

import com.adopt.apigw.repository.radius.CustomersRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustMacMappping;
import com.adopt.apigw.model.postpaid.CustMacMapppingPojo;
import com.adopt.apigw.service.common.CustomersService;

@Mapper
public abstract class CustMacMapper implements IBaseMapper<CustMacMapppingPojo, CustMacMappping> {

    @Autowired
    private CustomersService customersService;
    @Autowired
    private CustomersRepository customersRepository;

    @Override
    @Mapping(target = "customer", source = "custid")
    public abstract CustMacMappping dtoToDomain(CustMacMapppingPojo pojo, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(target = "custid", source = "customer")
    public abstract CustMacMapppingPojo domainToDTO(CustMacMappping domain, @Context CycleAvoidingMappingContext context);

    Integer fromCustomerToId(Customers entity) {
        return entity == null ? null : entity.getId();
    }

    Customers fromIdToCustomer(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Customers entity;
        try {
            entity =  customersRepository.findById(entityId).get();
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

}
