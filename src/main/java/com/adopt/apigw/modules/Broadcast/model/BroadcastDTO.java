package com.adopt.apigw.modules.Broadcast.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BroadcastDTO extends Auditable implements IBaseDto {
    private Long id;
    private String type;
    private String priority;
    private Long templateid;
    private Integer customer_id;
    private String emailsubject;
    private String custstatus;
    private String custcondition;
    private Integer planid;
    private Integer serviceareaid;
    private Integer networkdeviceid;
    private Integer slotid;
    private String expiry_condition;
    private LocalDate expirydate1;
    private LocalDate expirydate2;
    private Integer expirywithin;

    @JsonManagedReference
    private List<BroadcastPortsDTO> broadcastPortsList=new ArrayList<>();

    private String body;
    private String status;
    

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
