package com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.domain.Productplanmapping;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.dto.Productplanmappingdto;
import org.mapstruct.Mapper;

@Mapper
public interface Productplanmappingmapper extends IBaseMapper<Productplanmappingdto,Productplanmapping> {
}
