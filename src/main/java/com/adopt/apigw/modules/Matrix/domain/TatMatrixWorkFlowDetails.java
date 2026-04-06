package com.adopt.apigw.modules.Matrix.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tblmtatmatrixworkflowdetails")
public class TatMatrixWorkFlowDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "order_no")
    private Long orderNo;

    @Column(name = "level")
    private String level;

    @Column(name = "staff_id")
    private Integer staffId;

    @Column(name = "work_flow_id")
    private Long workFlowId;

    @Column(name = "tat_matrix_id")
    private Long tatMatrixId;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(name = "mtime")
    String mtime;

    @Column(name = "munit")
    String munit;

    @Column(name = "action")
    String action;

    @Column(name = "is_active", columnDefinition = "Boolean default false")
    private Boolean isActive = true;

    @Column(name = "current_team_heirarchy_mapping_id")
    private Integer currentTeamHeirarchyMappingId;

    @Column(name = "entity_id")
    private Integer entityId;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "event_id")
    private Integer eventId;

    @Column(name = "notification_type")
    private String notificationType;

    @Column(name = "team_id", nullable = true)
    private Long teamId;

    @Transient
    private LocalDateTime nextFollowUpDate;

    @Column(name = "is_overdue_reminder", nullable = true)
    private Boolean isOverDueReminder;

    @Column(name = "ticket_hold_time_init")
    private LocalDateTime ticketHoldTimeInit;

    @Column(name = "ticket_hold_time_end")
    private LocalDateTime ticketHoldTimeEnd;



//    @Column(name = "previous_level_staff_id")
//    private Integer previousLevelstaffId;
//
//    @Column(name = "previous_level_parent_staff_id")
//    private Integer previousLevelParentstaffId;
//
//    @Column(name = "level_change_date_time")
//    private LocalDateTime levelChangeDateTime;




    public TatMatrixWorkFlowDetails(Long orderNo, String level, Integer staffId, Long workFlowId, Long tatMatrixId, Integer parentId, LocalDateTime startDateTime, String mtime, String munit, String action, Boolean isActive, Integer currentTeamHeirarchyMappingId, Integer entityId, String eventName, Integer eventId, String notificationType, Long teamId, Boolean isOverDueReminder) {
        this.orderNo = orderNo;
        this.level = level;
        this.staffId = staffId;
        this.workFlowId = workFlowId;
        this.tatMatrixId = tatMatrixId;
        this.parentId = parentId;
        this.startDateTime = startDateTime;
        this.mtime = mtime;
        this.munit = munit;
        this.action = action;
        this.isActive = isActive;
        this.currentTeamHeirarchyMappingId = currentTeamHeirarchyMappingId;
        this.entityId = entityId;
        this.eventName = eventName;
        this.eventId = eventId;
        this.notificationType = notificationType;
        this.teamId = teamId;
        this.isOverDueReminder = isOverDueReminder;
    }
}
