package com.adopt.apigw.modules.Communication.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Communication.domain.Communication;
import com.adopt.apigw.modules.Communication.dto.CommunicationDTO;

@Mapper
public interface CommunicationMapper extends IBaseMapper<CommunicationDTO, Communication> {

}
