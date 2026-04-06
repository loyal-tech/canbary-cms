package com.adopt.apigw.rabbitMq.message;

import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;

import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadDocDetailsDTOMessage {

	private Long docId;

	private String docType;

	private String docSubType;

	private String remark;

	private String mode;

	private String docStatus;

	private String filename;

	private String uniquename;

	private Boolean isDelete = false;

	private String documentNumber;

	private Long leadMasterId;

	private String startDate;

	private String endDate;
}
