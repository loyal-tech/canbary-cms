package com.adopt.apigw.modules.NetworkDevices.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingDto;
import com.adopt.apigw.modules.InventoryManagement.product.*;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.Pincode.model.PincodeDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDevices;
import com.adopt.apigw.modules.NetworkDevices.model.NetworkDeviceDTO;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class NetworkDeviceMapper implements IBaseMapper<NetworkDeviceDTO, NetworkDevices> {

    @Autowired
    ProductServiceImpl productService;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    private ProductRepository productRepository;

    @Mapping(source = "inwardId", target = "inwardId")
    @Mapping(source = "product", target = "productId")
    @Override
    public abstract NetworkDeviceDTO domainToDTO(NetworkDevices networkDevices, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "inwardId", target = "inwardId")
    @Mapping(source = "productId", target = "product")
    @Override
    public abstract NetworkDevices dtoToDomain(NetworkDeviceDTO dtoData, @Context CycleAvoidingMappingContext context);
    Integer fromProductToProductId(Product entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    Product fromProductIdToProduct(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Product entity;
        try {
//            ProductDto entityDTO = productService.getEntityById(entityId.longValue());
            entity = productRepository.findById(entityId.longValue()).get();
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

//    Integer fromProductToProductId(Product entity) {
//        return entity == null ? null : entity.getId().intValue();
//    }
//
//    Product fromProductIdToProduct(Integer entityId) {
//        if (entityId == null) {
//            return null;
//        }
//        Product entity;
//        try {
//            ProductDto entityDTO = productService.getEntityById(entityId.longValue());
//            entity = productMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
//            entity.setId(entityId.longValue());
//        } catch (Exception e) {
//            e.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }

}
