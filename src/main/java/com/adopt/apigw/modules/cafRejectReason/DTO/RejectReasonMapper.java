package com.adopt.apigw.modules.cafRejectReason.DTO;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.cafRejectReason.Entity.RejectReason;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class RejectReasonMapper implements IBaseMapper<RejectReasonDto, RejectReason>  {

    @Override
    @Mapping(source = "dtoData.rejectSubReasonDtoList", target = "rejectSubReasonList")
    public abstract RejectReason dtoToDomain(RejectReasonDto dtoData, CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "data.rejectSubReasonList", target = "rejectSubReasonDtoList")
    public abstract RejectReasonDto domainToDTO(RejectReason data, CycleAvoidingMappingContext context);

    }


