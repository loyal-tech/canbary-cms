package com.adopt.apigw.modules.ServiceParameterMapping.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.ServiceParameterMapping.domain.ServiceParamMapping;
import com.adopt.apigw.modules.ServiceParameterMapping.model.ServiceParamMappingDTO;
import com.adopt.apigw.modules.ServiceParameters.domain.ServiceParameter;
import com.adopt.apigw.modules.ServiceParameters.mapper.ServiceParametersMapper;
import com.adopt.apigw.modules.ServiceParameters.model.ServiceParametersDTO;
import com.adopt.apigw.modules.ServiceParameters.service.ServiceParametersService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class ServiceParamMappingMapper implements IBaseMapper<ServiceParamMappingDTO, ServiceParamMapping> {

    @Autowired
    ServiceParametersService serviceParametersService;

    @Autowired
    ServiceParametersMapper serviceParametersMapper;

//    @Override
//    @Mapping(source = "dtoData.serviceparamid", target = "serviceParameter")
//    public abstract ServiceParamMapping dtoToDomain(ServiceParamMappingDTO dtoData, CycleAvoidingMappingContext context);
//
//
//    @Override
//    @Mapping(source = "data.serviceParameter", target = "serviceparamid")
//    public abstract ServiceParamMappingDTO domainToDTO(ServiceParamMapping data, CycleAvoidingMappingContext context);
//
//    Integer fromServiceParameterToId(ServiceParameter entity) {
//        return entity == null ? null : entity.getId().intValue();
//    }
//
//    ServiceParameter fromIdToServiceParameter(Integer entityId) {
//        if (entityId == null) {
//            return null;
//        }
//        ServiceParameter entity;
//        try {
//            ServiceParametersDTO entityDTO = serviceParametersService.getEntityById(entityId.longValue());
//            entity = serviceParametersMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
//            entity.setId(entityId.longValue());
//        } catch (Exception e) {
//            e.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }

}
