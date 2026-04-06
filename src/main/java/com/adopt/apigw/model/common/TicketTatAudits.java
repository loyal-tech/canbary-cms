package com.adopt.apigw.model.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblcasetatauditdetails")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketTatAudits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id", nullable = false, length = 40)
    private Integer id;

    @Column(name = "case_id", nullable = false)
    private Integer caseId;

    @Column(name = "case_status", nullable = false)
    private String caseStatus;


    @Column(name = "tat_action", nullable = false)
    private String tatAction;

    @Column(name = "tat_time", nullable = false)
    private Integer tatTime;

    @Column(name = "tat_unit", nullable = false)
    private String tatUnit;

    @Column(name = "sla_time", nullable = false)
    private Integer slaTime;

    @Column(name = "sla_unit", nullable = false)
    private String slaUnit;

    @Column(name = "tat_start_time", nullable = false)
    private String tatStartTime;


    @Column(name = "tat_message")
    private String tatMessage;

    @Column(name = "assign_staff_id", nullable = false)
    private Integer assignStaffId;

    @Column(name = "assign_staff_parent_id",nullable = false)
    private Integer assignStaffParentId;

    @Column(name = "case_level", nullable = false)
    private String caseLevel;

    @Column(name="notification_for",nullable = false)
    private String notificationFor;

    @Column(name="is_tat_breached",nullable = false)
    private String isTatBreached;


    @Column(name="is_sla_breached", nullable = false)
    private String isSlaBreached;

    @Column(name = "message_status",nullable = false)
    private String messageStatus;

    @Column(name = "message_mode",nullable = false)
    private String messageMode;

    @Transient
    private String staffName;

    @Transient
    private String parentStaffName;




}
