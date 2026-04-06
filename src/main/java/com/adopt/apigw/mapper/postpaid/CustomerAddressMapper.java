package com.adopt.apigw.mapper.postpaid;


import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustomerAddress;
import com.adopt.apigw.pojo.api.CustomerAddressPojo;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class CustomerAddressMapper implements IBaseMapper<CustomerAddressPojo, CustomerAddress> {

    @Autowired
    private CustomersService customersService;
    @Autowired
    private CustomersRepository customersRepository;

    @Override
    @Mapping(target = "customer", source = "customerId")
    public abstract CustomerAddress dtoToDomain(CustomerAddressPojo dto, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "customer", target = "customerId")
    public abstract CustomerAddressPojo domainToDTO(CustomerAddress domain, @Context CycleAvoidingMappingContext context);


    Integer fromCustomerTocustomerId(Customers entity) {
        return entity == null ? null : entity.getId();
    }

    Customers fromcustomerIdToCustomer(Integer entityId) {
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
