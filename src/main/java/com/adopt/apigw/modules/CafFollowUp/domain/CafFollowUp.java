package com.adopt.apigw.modules.CafFollowUp.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

@Data
@Entity
@Table(name = "tbltcaffollowup")
public class CafFollowUp implements IBaseData<Long>{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "caf_follow_up_id", nullable = false)
	private Long id;

	@Column(name = "follow_up_name")
	private String followUpName;
	
	@Column(name = "follow_up_datetime")
	private LocalDateTime followUpDatetime;
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "is_missed ",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isMissed;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "customer_id")
	private Customers customers;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "assignee_id")
	private StaffUser staffUser;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@CreationTimestamp
	@Column(name = "created_on")
	private LocalDateTime createdOn;
	
	@Column(name = "is_send ",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isSend;
	  
	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
	
	@Column(name = "send_reminder_notification ")
	private Boolean sendReminderNotification;

	@Override
	public Long getPrimaryKey() {
		return id;
	}

	@Override
	public void setDeleteFlag(boolean deleteFlag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getDeleteFlag() {
		// TODO Auto-generated method stub
		return false;
	}
}
