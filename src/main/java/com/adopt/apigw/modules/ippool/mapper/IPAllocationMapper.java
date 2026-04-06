package com.adopt.apigw.modules.ippool.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.ippool.domain.IPAllocation;
import com.adopt.apigw.modules.ippool.model.IPAllocationDTO;

@Mapper
public interface IPAllocationMapper extends IBaseMapper<IPAllocationDTO, IPAllocation> {
}
