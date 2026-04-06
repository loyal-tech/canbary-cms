package com.adopt.apigw.pojo.api;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ShiftLocationDTO {
	
	@NotNull
	private Long updateAddressServiceAreaId;
	
	@NotNull
	private Boolean isPaymentAddresSame = false;
	
	@NotNull
	private Boolean isPermanentAddress = false;
	
	@NotNull
	private Integer shiftPartnerid;
	
	@NotNull
    private CustomerAddressPojo addressDetails;

	private CustChargeOverrideDTO custChargeOverrideDTO;

	private Long popid;

	private Long oltid;

	private Integer requestedById;

	private Long branchID;

	private Boolean isInvoiceCleared;

	private Double transferableBalance;

	private Double transferableCommission;
}
