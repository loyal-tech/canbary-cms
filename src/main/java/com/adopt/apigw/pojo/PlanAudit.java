package com.adopt.apigw.pojo;

import com.adopt.apigw.model.postpaid.PostpaidPlan;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name = "tbltplanaudit")
@Data
public class PlanAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @DiffIgnore
    private Long id;

    @DiffIgnore
    @Column(name = "event_name")
    private String eventName;

    @Column(name = "entity_name")
    private String entitytName;

    @DiffIgnore
    @Column(name = "entity_id")
    private Long entityId;

    @DiffIgnore
    @Column(name = "action_by_id")
    private Integer actionById;

//    @DiffIgnore
//    @Column(name = "entity_name")
//    private String entityName;

    @Column(name = "action_by_name")
    private String actionByName;

    @Column(name = "action")
    private String action;

    @DiffIgnore
    @Column(name = "action_time")
    private LocalDateTime actionDateTime;

    @DiffIgnore
    @Column(name = "remark")
    private String remark;

    @Column(name = "details")
    private String details;

    @Column(name = "filename")
    private String fileName;

    public PlanAudit(PostpaidPlan plan, String username, String action, Integer staffId, String details, String fileName) {
        this.action = action;
        this.entitytName=plan.getName();
        this.actionByName = username;
        this.actionDateTime = LocalDateTime.now();
        this.entityId = Long.valueOf(plan.getId());
        this.eventName = new StringBuilder("PLAN_Change "+action).toString();
        this.remark=new StringBuilder("PLAN_Change "+action+"D Succesfully").toString();
        this.actionById = staffId;
        this.details=details;
        this.fileName = fileName;

    }

}
