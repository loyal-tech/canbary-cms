package com.adopt.apigw.modules.InventoryManagement.product;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.Charge;
import com.adopt.apigw.modules.tickets.domain.Case;
import com.adopt.apigw.modules.tickets.model.CaseDTO;
import com.adopt.apigw.repository.postpaid.ChargeRepository;
import com.adopt.apigw.service.postpaid.ChargeService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract  class ProductMapper implements IBaseMapper<ProductDto, Product> {


//    @Autowired
//    ChargeRepository chargeRepository;
//
//    @Override
////    @Mapping(target = "charge", source = "oldProductChargeId")
////    @Mapping(target= "charge", source = "newProductChargeId")
//    @Mapping( target = "charge", expression = "java(dto.oldProductChargeId + \" \" + product.newProductChargeId")
//    public abstract Product dtoToDomain(ProductDto dto, @Context CycleAvoidingMappingContext context);
//
//    @Override
////    @Mapping(target = "oldProductChargeId", source = "charge")
////    @Mapping(target= "newProductChargeId", source = "charge")
//    @Mapping( source = "charge", expression = "java(domain.oldProductChargeId + \" \" + domain.newProductChargeId")
//    public abstract ProductDto domainToDTO(Product domain, @Context CycleAvoidingMappingContext context);
//
//    Integer fromChargeToChargeId(Charge charge) {
//        return null != charge ? charge.getId() : null;
//    }
//
//    Charge fromChargeIdToCharge(Integer id) {
//        if (null == id) return null;
//        Charge entity = null;
//        try {
//            entity = chargeRepository.findById(id).orElse(null);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return entity;
//    }
}
