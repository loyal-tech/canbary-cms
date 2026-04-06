package com.adopt.apigw.pojo.api;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class CustChargeOverrideDTO {
	
	private Integer custid;
	
	private List<CustChargeDetailsPojo> custChargeDetailsPojoList;

	private Integer billableCustomerId=null;

	private String paymentOwner;

	private Integer paymentOwnerId;

	private Boolean isRenew;

	private Integer parentId;

	private Integer taxInPer;

	private Boolean isMvnoCharge;

	private List<Integer> debitDocDetailIds;

	private LocalDate ispFromDate;

	private LocalDate ispToDate;

}
