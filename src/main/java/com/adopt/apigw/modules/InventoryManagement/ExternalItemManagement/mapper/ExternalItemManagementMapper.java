package com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.domain.ExternalItemManagement;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.model.ExternalItemManagementDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class ExternalItemManagementMapper implements IBaseMapper<ExternalItemManagementDTO, ExternalItemManagement> {
}
