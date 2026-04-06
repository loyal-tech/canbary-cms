package com.adopt.apigw.modules.Alert.emailSchedular.SchedularDTO;

import com.adopt.apigw.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
@Data
public class SchedulerDTO implements IBaseDto {

    public SchedulerDTO() {
    }

    public SchedulerDTO(Boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public SchedulerDTO(String email,String subject,LocalDateTime scheduleTime,Boolean status,String jobId, String jobGroup,String message) {
        this.email=email;
        this.subject=subject;
        this.scheduleTime=scheduleTime;
        this.status = status;
        this.jobId=jobId;
        this.jobGroup=jobGroup;
        this.message = message;
    }

    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String subject;

    @NotNull
    private String body;

    @NotNull
    private LocalDateTime scheduleTime;

    private Boolean status;

    private String jobId;

    @NotNull
    private String jobGroup;

    private ZoneId scheduleTimeZone;

    private String message;

    private Timestamp sendedAt;

    private Boolean isSended;

    private String error;
    
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
