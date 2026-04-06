package com.adopt.apigw.model.common;

import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tbltworkflowaudit")
public class WorkflowAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @DiffIgnore
    private Long id;
    @DiffIgnore
    private Integer eventId;
    @DiffIgnore
    private String eventName;
    @DiffIgnore
    private Integer entityId;
    @DiffIgnore
    private String entityName;
    @DiffIgnore
    private Integer actionByStaffId;

    private String actionByName;
    private String action;
    @DiffIgnore
    private LocalDateTime actionDateTime;
    @DiffIgnore
    private String remark;
    @DiffIgnore
    @Column(name = "cust_id")
    private Integer custId;

    @Column(name = "approval_status")
    private String approvalStatus;


}
