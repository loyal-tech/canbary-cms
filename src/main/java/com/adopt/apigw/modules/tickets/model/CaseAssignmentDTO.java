package com.adopt.apigw.modules.tickets.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CaseAssignmentDTO implements IBaseDto {

    private Long assignmentId;
    private Long casesId;
    private Integer staffUserId;
    private LocalDate assignedDate;
    private Integer mvnoId;
    public CaseAssignmentDTO(Long casesId, Integer staffUserId, LocalDate assignedDate) {
        this.casesId = casesId;
        this.staffUserId = staffUserId;
        this.assignedDate = assignedDate;
    }

    public CaseAssignmentDTO() {
    }

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return assignmentId;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}
}
