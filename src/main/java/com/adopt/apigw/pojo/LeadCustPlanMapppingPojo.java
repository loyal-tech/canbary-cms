package com.adopt.apigw.pojo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.adopt.apigw.model.lead.LeadCustPlanMappping;
import com.adopt.apigw.model.lead.LeadCustQuotaDtls;
import com.adopt.apigw.model.lead.LeadCustQuotaDtlsPojo;
import com.adopt.apigw.rabbitMq.message.LeadCustPlanMapppingPojoMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadCustPlanMapppingPojo {

	private Integer id;

	private Integer planId;

	private Integer custid;

	private Long leadMasterId;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime startDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime endDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime expiryDate;

	private String startDateString;

	private String endDateString;

	private String expiryDateString;

	private String status;

	private Long qospolicyId;

	private String uploadqos;

	private String downloadqos;

	private String uploadts;

	private String downloadts;

	private List<LeadCustQuotaDtlsPojo> quotaList = new ArrayList<>();

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

	public LeadCustPlanMapppingPojo(LeadCustPlanMappping leadCustPlanMapping) {
		this.id = leadCustPlanMapping.getId();
		this.planId = leadCustPlanMapping.getPlanId();
		this.custid = leadCustPlanMapping.getCustid();
		if (leadCustPlanMapping.getLeadMaster() != null) {
			this.leadMasterId = leadCustPlanMapping.getLeadMaster().getId();
		}
		this.startDate = leadCustPlanMapping.getStartDate();
		this.endDate = leadCustPlanMapping.getEndDate();
		this.expiryDate = leadCustPlanMapping.getExpiryDate();
		this.startDateString = leadCustPlanMapping.getStartDateString();
		this.endDateString = leadCustPlanMapping.getEndDateString();
		this.expiryDateString = leadCustPlanMapping.getExpiryDateString();
		this.status = leadCustPlanMapping.getStatus();
		this.qospolicyId = leadCustPlanMapping.getQospolicyId();
		this.uploadqos = leadCustPlanMapping.getUploadqos();
		this.downloadqos = leadCustPlanMapping.getDownloadqos();
		this.uploadts = leadCustPlanMapping.getUploadts();
		this.downloadts = leadCustPlanMapping.getDownloadts();
		this.service = leadCustPlanMapping.getService();
		this.isDelete = leadCustPlanMapping.getIsDelete();
		this.offerPrice = leadCustPlanMapping.getOfferPrice();
		this.taxAmount = leadCustPlanMapping.getTaxAmount();
		this.creditdocid = leadCustPlanMapping.getCreditdocid();
		this.walletBalUsed = leadCustPlanMapping.getWalletBalUsed();
		this.purchaseType = leadCustPlanMapping.getPurchaseType();
		this.onlinePurchaseId = leadCustPlanMapping.getOnlinePurchaseId();
		this.purchaseFrom = leadCustPlanMapping.getPurchaseFrom();
		this.debitdocid = leadCustPlanMapping.getDebitdocid();
		this.validity = leadCustPlanMapping.getValidity();
		this.planName = leadCustPlanMapping.getPlanName();
		this.discount = leadCustPlanMapping.getDiscount();
		this.plangroupid = leadCustPlanMapping.getPlangroupid();
		this.planValidityDays = leadCustPlanMapping.getPlanValidityDays();
		this.isInvoiceToOrg = leadCustPlanMapping.getIsInvoiceToOrg();
		this.billTo = leadCustPlanMapping.getBillTo();
		this.newAmount = leadCustPlanMapping.getNewAmount();
		this.renewalId = leadCustPlanMapping.getRenewalId();
		this.custRefId = leadCustPlanMapping.getCustRefId();
		if (leadCustPlanMapping.getCustQuotaDtlsList() != null
				&& leadCustPlanMapping.getCustQuotaDtlsList().size() > 0) {
			List<LeadCustQuotaDtlsPojo> custQuotaDtlsPojoList = new ArrayList<LeadCustQuotaDtlsPojo>();
			for (LeadCustQuotaDtls custQuotaDtls : leadCustPlanMapping.getCustQuotaDtlsList()) {
				custQuotaDtlsPojoList.add(new LeadCustQuotaDtlsPojo(custQuotaDtls));
			}
			this.quotaList = custQuotaDtlsPojoList;
		}
		this.service = leadCustPlanMapping.getService();
		this.isDelete = leadCustPlanMapping.getIsDelete();
		this.offerPrice = leadCustPlanMapping.getOfferPrice();
		this.taxAmount = leadCustPlanMapping.getTaxAmount();
		this.creditdocid = leadCustPlanMapping.getCreditdocid();
		this.walletBalUsed = leadCustPlanMapping.getWalletBalUsed();
		this.purchaseType = leadCustPlanMapping.getPurchaseType();
		this.onlinePurchaseId = leadCustPlanMapping.getOnlinePurchaseId();
		this.purchaseFrom = leadCustPlanMapping.getPurchaseFrom();
		this.debitdocid = leadCustPlanMapping.getDebitdocid();
		this.validity = leadCustPlanMapping.getValidity();
		this.planName = leadCustPlanMapping.getPlanName();
		this.validity = leadCustPlanMapping.getValidity();
		this.planName = leadCustPlanMapping.getPlanName();
		this.discount = leadCustPlanMapping.getDiscount();
		this.plangroupid = leadCustPlanMapping.getPlangroupid();
		this.planValidityDays = leadCustPlanMapping.getPlanValidityDays();
		this.isInvoiceToOrg = leadCustPlanMapping.getIsInvoiceToOrg();
		this.billTo = leadCustPlanMapping.getBillTo();
		this.newAmount = leadCustPlanMapping.getNewAmount();
		this.renewalId = leadCustPlanMapping.getRenewalId();
		this.custRefId = leadCustPlanMapping.getCustRefId();
		this.isTrialPlan = leadCustPlanMapping.getIsTrialPlan();
	}

	public LeadCustPlanMapppingPojo(LeadCustPlanMapppingPojoMessage leadCustPlanMapping) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		this.id = leadCustPlanMapping.getId();
		this.planId = leadCustPlanMapping.getPlanId();
		this.custid = leadCustPlanMapping.getCustid();
		this.leadMasterId = leadCustPlanMapping.getLeadMasterId();
		if (leadCustPlanMapping.getStartDate() != null) {
			this.startDate = LocalDateTime.parse(leadCustPlanMapping.getStartDate(),formatter);
		}
		if (leadCustPlanMapping.getEndDate() != null) {
			this.endDate = LocalDateTime.parse(leadCustPlanMapping.getEndDate(),formatter);
		}
		if (leadCustPlanMapping.getExpiryDate() != null) {
			this.expiryDate = LocalDateTime.parse(leadCustPlanMapping.getExpiryDate(),formatter);
		}
		this.startDateString = leadCustPlanMapping.getStartDateString();
		this.endDateString = leadCustPlanMapping.getEndDateString();
		this.expiryDateString = leadCustPlanMapping.getExpiryDateString();
		this.status = leadCustPlanMapping.getStatus();
		this.qospolicyId = leadCustPlanMapping.getQospolicyId();
		this.uploadqos = leadCustPlanMapping.getUploadqos();
		this.downloadqos = leadCustPlanMapping.getDownloadqos();
		this.uploadts = leadCustPlanMapping.getUploadts();
		this.downloadts = leadCustPlanMapping.getDownloadts();
		this.service = leadCustPlanMapping.getService();
		this.isDelete = leadCustPlanMapping.getIsDelete();
		this.offerPrice = leadCustPlanMapping.getOfferPrice();
		this.taxAmount = leadCustPlanMapping.getTaxAmount();
		this.creditdocid = leadCustPlanMapping.getCreditdocid();
		this.walletBalUsed = leadCustPlanMapping.getWalletBalUsed();
		this.purchaseType = leadCustPlanMapping.getPurchaseType();
		this.onlinePurchaseId = leadCustPlanMapping.getOnlinePurchaseId();
		this.purchaseFrom = leadCustPlanMapping.getPurchaseFrom();
		this.debitdocid = leadCustPlanMapping.getDebitdocid();
		this.validity = leadCustPlanMapping.getValidity();
		this.planName = leadCustPlanMapping.getPlanName();
		this.discount = leadCustPlanMapping.getDiscount();
		this.plangroupid = leadCustPlanMapping.getPlangroupid();
		this.planValidityDays = leadCustPlanMapping.getPlanValidityDays();
		this.isInvoiceToOrg = leadCustPlanMapping.getIsInvoiceToOrg();
		this.billTo = leadCustPlanMapping.getBillTo();
		this.newAmount = leadCustPlanMapping.getNewAmount();
		this.renewalId = leadCustPlanMapping.getRenewalId();
		this.custRefId = leadCustPlanMapping.getCustRefId();
		this.service = leadCustPlanMapping.getService();
		this.isDelete = leadCustPlanMapping.getIsDelete();
		this.offerPrice = leadCustPlanMapping.getOfferPrice();
		this.taxAmount = leadCustPlanMapping.getTaxAmount();
		this.creditdocid = leadCustPlanMapping.getCreditdocid();
		this.walletBalUsed = leadCustPlanMapping.getWalletBalUsed();
		this.purchaseType = leadCustPlanMapping.getPurchaseType();
		this.onlinePurchaseId = leadCustPlanMapping.getOnlinePurchaseId();
		this.purchaseFrom = leadCustPlanMapping.getPurchaseFrom();
		this.debitdocid = leadCustPlanMapping.getDebitdocid();
		this.validity = leadCustPlanMapping.getValidity();
		this.planName = leadCustPlanMapping.getPlanName();
		this.validity = leadCustPlanMapping.getValidity();
		this.planName = leadCustPlanMapping.getPlanName();
		this.discount = leadCustPlanMapping.getDiscount();
		this.plangroupid = leadCustPlanMapping.getPlangroupid();
		this.planValidityDays = leadCustPlanMapping.getPlanValidityDays();
		this.isInvoiceToOrg = leadCustPlanMapping.getIsInvoiceToOrg();
		this.billTo = leadCustPlanMapping.getBillTo();
		this.newAmount = leadCustPlanMapping.getNewAmount();
		this.renewalId = leadCustPlanMapping.getRenewalId();
		this.custRefId = leadCustPlanMapping.getCustRefId();
		this.isTrialPlan = leadCustPlanMapping.getIsTrialPlan();
	}
}
