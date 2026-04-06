package com.adopt.apigw.modules.ServiceParameters.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.ServiceParameters.domain.ServiceParameter;
import com.adopt.apigw.modules.ServiceParameters.model.ServiceParametersDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ServiceParametersMapper extends IBaseMapper<ServiceParametersDTO, ServiceParameter> {
}
