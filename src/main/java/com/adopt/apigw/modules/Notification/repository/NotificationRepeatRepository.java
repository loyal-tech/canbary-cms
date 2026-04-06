package com.adopt.apigw.modules.Notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adopt.apigw.modules.Notification.domain.NotificationRepeat;

import java.util.List;

public interface NotificationRepeatRepository extends JpaRepository<NotificationRepeat, Long> {
    List<NotificationRepeat> findBySubscriberidAndNotificationidAndPackrelid(Long subscriberid, Long packrelid, Long notificationid);
}
