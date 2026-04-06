package com.adopt.apigw.modules.ippool.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.ippool.domain.IPPoolDtls;
import com.adopt.apigw.modules.ippool.model.IPPoolDtlsDTO;

@Mapper
public interface IPPoolDtlsMapper extends IBaseMapper<IPPoolDtlsDTO, IPPoolDtls> {
}
