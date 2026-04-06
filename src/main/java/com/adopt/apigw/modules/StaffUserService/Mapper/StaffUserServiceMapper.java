package com.adopt.apigw.modules.StaffUserService.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.StaffUserService.domain.StaffUserServiceMapping1;
import com.adopt.apigw.modules.StaffUserService.model.StaffUserServiceDTO;
import org.mapstruct.Mapper;

@Mapper
public interface StaffUserServiceMapper extends IBaseMapper<StaffUserServiceDTO, StaffUserServiceMapping1> {

}
