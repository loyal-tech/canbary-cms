package com.adopt.apigw.modules.Alert.smsScheduler.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Alert.smsScheduler.SchedularDTO.SmsSchedulerDTO;
import com.adopt.apigw.modules.Alert.smsScheduler.domain.SmsScheduler;

@Mapper
public interface SmsSchedularMapper extends IBaseMapper<SmsSchedulerDTO, SmsScheduler> {
}
