package com.adopt.apigw.modules.Alert.emailSchedular.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Alert.emailSchedular.SchedularDTO.SchedulerDTO;
import com.adopt.apigw.modules.Alert.emailSchedular.domain.Scheduler;

@Mapper
public interface SchedularMapper extends IBaseMapper<SchedulerDTO, Scheduler> {
}
