package com.adopt.apigw.schedulerAudit;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tblmscheduleraudit")
public class SchedulerAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "scheduler_name")
    private String schedulerName;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status")
    private String status;

    @Column(name = "total_count")
    private Integer totalCount;

    @Column(name = "description")
    private String description;
}
