package com.adopt.apigw.modules.CafFollowUp.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.adopt.apigw.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CafFollowUpDTO implements IBaseDto {

	private Long id;

	private String followUpName;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime followUpDatetime;

	private String remarks;

	private String status;

	private Boolean isMissed = false;

	private Boolean isSend = false;

	private Integer customersId;

	private String customersName;

	private Integer createdBy;

	private Integer staffUserId;
	
	private String staffUserName;

    private Integer mvnoId;

	@Override
	public Long getIdentityKey() {
		return id;
	}

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}


}
