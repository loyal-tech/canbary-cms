package com.adopt.apigw.modules.CafFollowUp.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Data
@Table(name = "tbltcaffollowupaudit")
public class CafFollowUpAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "caf_follo_up_audit_id", nullable = false)
	private Long id;

	@Column(name = "audit_name")
	private String auditName;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "staff_name")
	private String staffName;
	
	@CreationTimestamp
	@Column(name = "created_on")
	private LocalDateTime createdOn;
	
	@Column(name = "customer_id")
	private Integer customerId;
}
