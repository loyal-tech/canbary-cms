package com.adopt.apigw.pojo.api;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustomerCafAssignmentPojo {
	
	private Integer custcafId;

	private Integer staffId;

	private Integer nextStaffId;

	private String status;
		
	private String remark;
	
	private String flag;

	private Integer credDocId;

	private Integer planId;

	private Integer custPackageId;

	private Double newDiscount;

	private LocalDateTime assignedDate;

	private Integer planGroupId;

	private Integer addressId;

	private Integer custPlanMappingId;

	private Integer partnerPaymentId;

	private Integer custDocDetailsId;
}
