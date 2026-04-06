package com.adopt.apigw.modules.tickets.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.tickets.domain.TicketReasonCategory;
import com.adopt.apigw.modules.tickets.model.TicketReasonCategoryDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class TicketReasonCategoryMapper implements IBaseMapper<TicketReasonCategoryDTO, TicketReasonCategory> {
}
