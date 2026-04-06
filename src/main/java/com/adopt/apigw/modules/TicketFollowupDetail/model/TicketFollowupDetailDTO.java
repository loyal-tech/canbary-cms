package com.adopt.apigw.modules.TicketFollowupDetail.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.adopt.apigw.core.dto.IBaseDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class TicketFollowupDetailDTO implements IBaseDto{

    private Long id;
    
    private String remark;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime remarkDate;
    
    private Boolean isDelete = false;
    
    private Long caseId;
    
    private Integer staffId;
    
    private Integer custId;
  
    private String caseTitle;
    
    private String staffUserName;
    
    private String customersName;

    private Integer mvnoId;

    private String caseNumber ;

	@Override
	public Long getIdentityKey() {
		return id;
	}

	@Override
	public Integer getMvnoId() {
		return null;
	}
    
}
