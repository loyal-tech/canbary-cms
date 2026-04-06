package com.adopt.apigw.modules.PriceGroup.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBookPlanDetail;
import com.adopt.apigw.modules.PriceGroup.model.PriceBookPlanDetailDTO;

@Mapper
public interface PriceBookPlanDtlMapper extends IBaseMapper<PriceBookPlanDetailDTO,PriceBookPlanDetail> {
}
