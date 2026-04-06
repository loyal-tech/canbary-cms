package com.adopt.apigw.modules.tickets.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.tickets.domain.CaseDocDetails;
import com.adopt.apigw.modules.tickets.model.CaseDocDetailsDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class CaseDocDetailsMapper implements IBaseMapper<CaseDocDetailsDTO, CaseDocDetails> {
}
