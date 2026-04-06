package com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnMapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnDomain.ReturnDto;
import com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnModel.Return;
import org.mapstruct.Mapper;

@Mapper
public interface ReturnMapper extends IBaseMapper<ReturnDto, Return> {
}
