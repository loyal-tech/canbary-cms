package com.adopt.apigw.modules.qosPolicy.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.postpaid.Charge;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicyGatewayMapping;
import com.adopt.apigw.modules.qosPolicy.model.QOSPolicyDTO;
import com.adopt.apigw.modules.qosPolicy.repository.QOSGatewayMappingRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public abstract class QOSPolicyMapper implements IBaseMapper<QOSPolicyDTO, QOSPolicy> {

    @Override
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "name")
    public abstract QOSPolicyDTO domainToDTO(QOSPolicy data, @Context CycleAvoidingMappingContext context);

}
