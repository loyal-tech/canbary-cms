package com.adopt.apigw.modules.InventoryManagement.productCategory;

import com.adopt.apigw.core.mapper.IBaseMapper;
import org.mapstruct.Mapper;

@Mapper
public interface ProductCategoryMapper  extends IBaseMapper<ProductCategoryDto, ProductCategory> {
}
