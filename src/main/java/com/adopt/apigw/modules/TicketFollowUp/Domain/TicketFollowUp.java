package com.adopt.apigw.modules.TicketFollowUp.Domain;
//
//import com.adopt.apigw.core.data.IBaseData;
//import com.adopt.apigw.model.common.StaffUser;
//import com.adopt.apigw.modules.tickets.domain.Case;
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import lombok.Data;
//import org.hibernate.annotations.CreationTimestamp;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//
//
//@Data
//@Entity
//@Table(name = "tbltticketfollowup")
public class TicketFollowUp  {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "ticket_follow_up_id", nullable = false)
//    private Long id;
//
//    @Column(name = "follow_up_name")
//    private String followUpName;
//
//    @Column(name = "follow_up_datetime")
//    private LocalDateTime followUpDatetime;
//
//    @Column(name = "remarks")
//    private String remarks;
//
//    @Column(name = "status")
//    private String status;
//
//    @Column(name = "is_missed ",columnDefinition = "Boolean default false", nullable = false)
//    private Boolean isMissed;
//
//    @JsonBackReference
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "ticket_id")
//    private Case ticket;
//
//    @JsonBackReference
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "assignee_id")
//    private StaffUser staffUser;
//
//    @Column(name = "created_by")
//    private String createdBy;
//
//    @CreationTimestamp
//    @Column(name = "created_on")
//    private LocalDateTime createdOn;
//
//    @Column(name = "is_send ",columnDefinition = "Boolean default false", nullable = false)
//    private Boolean isSend;
//
//    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
//    private Integer mvnoId;
//
//    @Column(name = "send_reminder_notification ")
//    private Boolean sendReminderNotification;
//
//    @Override
//    public Long getPrimaryKey() {
//        return id;
//    }
//
//    @Override
//    public void setDeleteFlag(boolean deleteFlag) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public boolean getDeleteFlag() {
//        // TODO Auto-generated method stub
//        return false;
//    }
}
