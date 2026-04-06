package com.adopt.apigw.modules.Notification.domain;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblnotification_repeat")
public class NotificationRepeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long subscriberid;
    private Long packrelid;
    private Long notificationid;

    public NotificationRepeat(Long subscriberid, Long packrelid, Long notificationid) {
        this.subscriberid = subscriberid;
        this.packrelid = packrelid;
        this.notificationid = notificationid;
    }

    public NotificationRepeat() {

    }
}
