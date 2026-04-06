package com.adopt.apigw.modules.Alert.smsScheduler.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_communication_smsjob")
public class SmsScheduler implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, length = 40)
    private Long id;

    @Column(name="destination", nullable = false, length = 40)
    private String destination;

    @Column(name="source", nullable = false, length = 40)
    private String source;

    @Column(name="message", nullable = false, length = 40)
    private String message;

    @Column(name="schedule_time", nullable = false, length = 40)
    private LocalDateTime scheduleTime;

    @Column(name="status", nullable = false, length = 40)
    private Boolean status;

    @Column(name="job_id", nullable = false, length = 40)
    private String jobId;

    @Column(name="job_grp", nullable = false, length = 40)
    private String jobGroup;

    @Column(nullable = true)
    private Timestamp sendedAt;

    @Column(name = "isSended",nullable = true)
    private Boolean isSended;

    private String templateId;

    @Column(columnDefinition = "text",nullable = true)
    private String error;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
