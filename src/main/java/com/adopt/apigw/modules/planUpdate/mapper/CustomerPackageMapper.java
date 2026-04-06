package com.adopt.apigw.modules.planUpdate.mapper;

import com.adopt.apigw.model.postpaid.PostpaidPlan;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.planUpdate.domain.CustomerPackage;
import com.adopt.apigw.modules.planUpdate.model.CustomerPackageDTO;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;

@Mapper
public interface CustomerPackageMapper extends IBaseMapper<CustomerPackageDTO, CustomerPackage> {

    @Override
    @Mapping(source = "customers", target = "customersId")
    @Mapping(source = "plan", target = "planId")
    @Mapping(source = "qospolicy", target = "qospolicyId")
    CustomerPackageDTO domainToDTO(CustomerPackage data, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(target = "customers", source = "customersId")
    @Mapping(target = "qospolicy", source = "qospolicyId")
    @Mapping(target = "plan", source = "planId")
    CustomerPackage dtoToDomain(CustomerPackageDTO dtoData, @Context CycleAvoidingMappingContext context);

    default Long fromCustomers(Customers entity){
        return entity == null ? null : Long.valueOf(entity.getId());
    }

    default Customers fromCustomersIds(Integer entityId){
        final Customers customers = new Customers();
        customers.setId(entityId);
        return customers;
    }

    default Long fromPlan(PostpaidPlan entity){
        return entity == null ? null : Long.valueOf(entity.getId());
    }

    default PostpaidPlan fromPlanIds(Integer entityId){
        final PostpaidPlan plan = new PostpaidPlan();
        plan.setId(entityId);
        return plan;
    }

    default Long fromQosPolicy(QOSPolicy entity){
        return entity == null ? null : entity.getId();
    }

    default QOSPolicy fromPlanIds(Long entityId){
        final QOSPolicy qosPolicy = new QOSPolicy();
        qosPolicy.setId(entityId);
        return qosPolicy;
    }
}
