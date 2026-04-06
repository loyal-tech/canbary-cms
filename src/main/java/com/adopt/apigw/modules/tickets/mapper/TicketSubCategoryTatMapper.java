package com.adopt.apigw.modules.tickets.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.tickets.domain.TicketSubCategoryTatMapping;
import com.adopt.apigw.modules.tickets.model.TicketReasonCategoryDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TicketSubCategoryTatMapper  extends IBaseMapper<TicketReasonCategoryDTO, TicketSubCategoryTatMapping> {
}
