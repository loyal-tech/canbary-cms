package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadCustPlanMapppingPojoMessage {

	private Integer id;

	private Integer planId;

	private Integer custid;

	private Long leadMasterId;

	private String startDate;

	private String endDate;

	private String expiryDate;

	private String startDateString;

	private String endDateString;

	private String expiryDateString;

	private String status;

	private Long qospolicyId;

	private String uploadqos;

	private String downloadqos;

	private String uploadts;

	private String downloadts;

	private String service;

	private Boolean isDelete = false;
	
	private Boolean isTrialPlan = false;

	private Double offerPrice;

	private Double taxAmount;

	private Long creditdocid;

	private Double walletBalUsed = 0.0;;

	private String purchaseType;

	private Long onlinePurchaseId;

	private String purchaseFrom;

	private Long debitdocid;

	private Double validity;

	private String planName;

	private Double discount;

	private Integer plangroupid;

	private Integer planValidityDays;

	private Boolean isInvoiceToOrg = false;

	private String billTo = "CUSTOMER";;

	private Double newAmount = 0d;;

	private Integer renewalId;

	private Integer custRefId;
}
