package com.adopt.apigw.modules.subscriber.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.payments.domain.Payment;
import com.adopt.apigw.modules.payments.model.PaymentDTO;
import com.adopt.apigw.modules.subscriber.Domain.ServiceAudit;
import com.adopt.apigw.modules.subscriber.model.ServiceAuditDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ServiceAuditMapper extends IBaseMapper<ServiceAuditDTO, ServiceAudit>{
}
