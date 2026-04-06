package com.adopt.apigw.model.common;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "tblenterpriseaudit")
public class EnterpriseETRAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id", nullable = false)
    Long id;

    @Column(name = "cust_id")
    Long custId;

    @Column(name = "cust_user_name")
    String custUserName;

    @Column(name = "staff_id")
    Long staffId;

    @Column(name = "staff_person_name")
    String staffPersonName;

    @Column(name="notification_sent_date")
    LocalDate notificationSentDate;

    @Column(name="notification_sent_time")
    LocalTime notificationSentTime;

    @Column(name="notification_message")
    String notificationMessage;

    @Column(name="notification_mode")
    String notificationMode;

    @Column(name="message_mode")
    String messageMode;

    @Column(name="notification_status")
    String notificationStatus;
}
