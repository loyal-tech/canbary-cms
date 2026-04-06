package com.adopt.apigw.modules.TicketTatMatrix.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import com.adopt.apigw.modules.TicketTatMatrix.Model.TicketTatMatrixDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TicketTatMatrixMapper extends IBaseMapper<TicketTatMatrixDTO, TicketTatMatrix> {
}
