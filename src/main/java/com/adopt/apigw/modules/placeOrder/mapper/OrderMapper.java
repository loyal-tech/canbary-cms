package com.adopt.apigw.modules.placeOrder.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.placeOrder.domain.Order;
import com.adopt.apigw.modules.placeOrder.model.OrderDTO;

@Mapper
public interface OrderMapper extends IBaseMapper<OrderDTO, Order> {
}
