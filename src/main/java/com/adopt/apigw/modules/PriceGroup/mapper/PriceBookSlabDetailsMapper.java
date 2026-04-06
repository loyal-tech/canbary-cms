package com.adopt.apigw.modules.PriceGroup.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBookSlabDetails;
import com.adopt.apigw.modules.PriceGroup.model.PriceBookSlabDetailsDTO;
import org.mapstruct.Mapper;

@Mapper
public interface PriceBookSlabDetailsMapper extends IBaseMapper<PriceBookSlabDetailsDTO, PriceBookSlabDetails> {
}
