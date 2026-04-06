package com.adopt.apigw.modules.Notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.Broadcast.domain.BroadcastPorts;
import com.adopt.apigw.modules.Notification.domain.Notification;
import com.adopt.apigw.modules.Notification.domain.NotificationConfig;

import java.util.List;

@Repository
public interface NotificationConfigRepository extends JpaRepository<NotificationConfig,Long> {
}
