package com.adopt.apigw.modules.InvestmentCode.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.InvestmentCode.DTO.InvestmentCodeDto;
import com.adopt.apigw.modules.InvestmentCode.Domain.InvestmentCode;
import com.adopt.apigw.modules.InvestmentCode.service.InvestmentCodeService;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class InvestmentCodeMapper implements IBaseMapper<InvestmentCodeDto, InvestmentCode> {

    String MODULE = " [InvestmentCodeMapper] ";
}
