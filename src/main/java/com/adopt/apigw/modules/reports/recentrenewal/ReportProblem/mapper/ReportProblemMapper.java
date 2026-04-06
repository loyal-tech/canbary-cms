package com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.domain.ReportProblem;
import com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.model.ReportProblemDTO;

@Mapper
public interface ReportProblemMapper extends IBaseMapper<ReportProblemDTO, ReportProblem> {
    public abstract ReportProblem dtoToDomain(ReportProblemDTO dtoData, @Context CycleAvoidingMappingContext context);



}




