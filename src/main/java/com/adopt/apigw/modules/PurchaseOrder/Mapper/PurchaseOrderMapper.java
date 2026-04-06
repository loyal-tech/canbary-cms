package com.adopt.apigw.modules.PurchaseOrder.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Matrix.domain.Matrix;
import com.adopt.apigw.modules.Matrix.model.MatrixDTO;
import com.adopt.apigw.modules.PurchaseOrder.DTO.PurchaseOrderDTO;
import com.adopt.apigw.modules.PurchaseOrder.Domain.PurchaseOrder;
import org.mapstruct.Mapper;

@Mapper
public interface PurchaseOrderMapper extends IBaseMapper<PurchaseOrderDTO, PurchaseOrder> {
}
