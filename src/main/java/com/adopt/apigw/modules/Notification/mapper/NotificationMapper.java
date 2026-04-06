package com.adopt.apigw.modules.Notification.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Notification.domain.Notification;
import com.adopt.apigw.modules.Notification.model.NotificationDTO;

@Mapper
public interface NotificationMapper extends IBaseMapper<NotificationDTO, Notification> {
}
