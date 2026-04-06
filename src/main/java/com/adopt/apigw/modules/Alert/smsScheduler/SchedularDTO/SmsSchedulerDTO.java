package com.adopt.apigw.modules.Alert.smsScheduler.SchedularDTO;

import com.adopt.apigw.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class SmsSchedulerDTO implements IBaseDto {

    public SmsSchedulerDTO() {
    }

    public SmsSchedulerDTO(Boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public SmsSchedulerDTO(String destination, String source, LocalDateTime scheduleTime, Boolean status, String jobId, String jobGroup, String message,String templateId) {
        this.destination=destination;
        this.source=source;
        this.scheduleTime=scheduleTime;
        this.status = status;
        this.jobId=jobId;
        this.jobGroup=jobGroup;
        this.message = message;
        this.templateId = templateId;
    }

    private Long id;

    private String destination;

    private String source;

    private String message;

    private LocalDateTime scheduleTime;

    private Boolean status;

    private String jobId;

    private String jobGroup;

    private ZoneId scheduleTimeZone;

    private Timestamp sendedAt;

    private Boolean isSended;

    private String error;

    private String templateId;
    
    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}

}
