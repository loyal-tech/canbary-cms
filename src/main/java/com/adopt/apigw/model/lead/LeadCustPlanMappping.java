package com.adopt.apigw.model.lead;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.adopt.apigw.pojo.LeadCustPlanMapppingPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffInclude;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLLEADCUSTPACKAGEREL")
public class LeadCustPlanMappping {

	@Id
	@Column(name = "cust_plan_mappping_id", nullable = false)
	private Integer id;

	@Column(name = "plan_id")
	private Integer planId;

	private Integer custid;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;

	@Column(name = "start_date")
	private LocalDateTime startDate;

	@Column(name = "end_date")
	private LocalDateTime endDate;

	@Column(name = "expiry_date")
	private LocalDateTime expiryDate;

	@Column(name = "start_date_string")
	private String startDateString;
	
	@Column(name = "end_date_string")
	private String endDateString;
	
	@Column(name = "expiry_date_string")
	private String expiryDateString;

	private String status;

	@Column(name = "qospolicy_id")
	private Long qospolicyId;

	private String uploadqos;

	private String downloadqos;

	private String uploadts;

	private String downloadts;

	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "custPlanMappping")
	private List<LeadCustQuotaDtls> custQuotaDtlsList = new ArrayList<>();

	private String service;

	@Column(name = "is_delete")
	private Boolean isDelete;

	@Column(name = "is_trial_plan")
	private Boolean isTrialPlan;

	@Column(name = "offer_price")
	private Double offerPrice;
	
	@Column(name = "tax_amount")
	private Double taxAmount;
	
	private Long creditdocid;
	
	@Column(name = "wallet_bal_used")
	private Double walletBalUsed;
	
	@Column(name = "purchase_type")
	private String purchaseType;
	
	@Column(name = "online_purchase_id")
	private Long onlinePurchaseId;
	
	@Column(name = "purchase_from")
	private String purchaseFrom;
	
	private Long debitdocid;

	private Double validity;

	@Column(name = "plan_name")
	private String planName;

	private Double discount;
	
	private Integer plangroupid;

	@Column(name = "plan_validity_days")
	private Integer planValidityDays;

	@Column(name = "is_invoice_to_org")
	private Boolean isInvoiceToOrg;

	@Column(name = "bill_to")
	private String billTo;

	@Column(name = "new_amount")
	private Double newAmount;

	@Column(name = "renewal_id")
	private Integer renewalId;

	@Column(name = "cust_ref_id")
	private Integer custRefId;
	
	public LeadCustPlanMappping(Integer id) {
		this.id = id;
	}
	
	public LeadCustPlanMappping(LeadCustPlanMapppingPojo leadCustPlanMappingPojo) {
		this.id = leadCustPlanMappingPojo.getId();
		this.planId = leadCustPlanMappingPojo.getPlanId();
		this.custid = leadCustPlanMappingPojo.getCustid();
		if (leadCustPlanMappingPojo.getLeadMasterId() != null) {
			this.leadMaster = new LeadMaster(leadCustPlanMappingPojo.getLeadMasterId());
		}
		this.startDate = leadCustPlanMappingPojo.getStartDate();
		this.endDate = leadCustPlanMappingPojo.getEndDate();
		this.expiryDate = leadCustPlanMappingPojo.getExpiryDate();
		this.startDateString = leadCustPlanMappingPojo.getStartDateString();
		this.endDateString = leadCustPlanMappingPojo.getEndDateString();
		this.expiryDateString = leadCustPlanMappingPojo.getExpiryDateString();
		this.status = leadCustPlanMappingPojo.getStatus();
		this.qospolicyId = leadCustPlanMappingPojo.getQospolicyId();
		this.uploadqos = leadCustPlanMappingPojo.getUploadqos();
		this.downloadqos = leadCustPlanMappingPojo.getDownloadqos();
		this.uploadts = leadCustPlanMappingPojo.getUploadts();
		this.downloadts = leadCustPlanMappingPojo.getDownloadts();
		this.service = leadCustPlanMappingPojo.getService();
		this.isDelete = leadCustPlanMappingPojo.getIsDelete();
		this.offerPrice = leadCustPlanMappingPojo.getOfferPrice();
		this.taxAmount = leadCustPlanMappingPojo.getTaxAmount();
		this.creditdocid = leadCustPlanMappingPojo.getCreditdocid();
		this.walletBalUsed = leadCustPlanMappingPojo.getWalletBalUsed();
		this.purchaseType = leadCustPlanMappingPojo.getPurchaseType();
		this.onlinePurchaseId = leadCustPlanMappingPojo.getOnlinePurchaseId();
		this.purchaseFrom = leadCustPlanMappingPojo.getPurchaseFrom();
		this.debitdocid = leadCustPlanMappingPojo.getDebitdocid();
		this.validity = leadCustPlanMappingPojo.getValidity();
		this.planName = leadCustPlanMappingPojo.getPlanName();
		this.discount = leadCustPlanMappingPojo.getDiscount();
		this.plangroupid = leadCustPlanMappingPojo.getPlangroupid();
		this.planValidityDays = leadCustPlanMappingPojo.getPlanValidityDays();
		this.isInvoiceToOrg = leadCustPlanMappingPojo.getIsInvoiceToOrg();
		this.billTo = leadCustPlanMappingPojo.getBillTo();
		this.newAmount = leadCustPlanMappingPojo.getNewAmount();
		this.renewalId = leadCustPlanMappingPojo.getRenewalId();
		this.custRefId = leadCustPlanMappingPojo.getCustRefId();
		if (leadCustPlanMappingPojo.getQuotaList()!= null && leadCustPlanMappingPojo.getQuotaList().size() > 0) {
			List<LeadCustQuotaDtls> custQuotaDtlsList = new ArrayList<LeadCustQuotaDtls>();
			for (LeadCustQuotaDtlsPojo custQuotaDtlsPojo : leadCustPlanMappingPojo.getQuotaList()) {
				custQuotaDtlsList.add(new LeadCustQuotaDtls(custQuotaDtlsPojo));
			}
			this.custQuotaDtlsList = custQuotaDtlsList;
		}
		this.service = leadCustPlanMappingPojo.getService();
		this.isDelete = leadCustPlanMappingPojo.getIsDelete();
		this.offerPrice = leadCustPlanMappingPojo.getOfferPrice();
		this.taxAmount = leadCustPlanMappingPojo.getTaxAmount();
		this.creditdocid = leadCustPlanMappingPojo.getCreditdocid();
		this.walletBalUsed = leadCustPlanMappingPojo.getWalletBalUsed();
		this.purchaseType = leadCustPlanMappingPojo.getPurchaseType();
		this.onlinePurchaseId = leadCustPlanMappingPojo.getOnlinePurchaseId();
		this.purchaseFrom = leadCustPlanMappingPojo.getPurchaseFrom();
		this.debitdocid = leadCustPlanMappingPojo.getDebitdocid();
		this.validity = leadCustPlanMappingPojo.getValidity();
		this.planName = leadCustPlanMappingPojo.getPlanName();
		this.validity = leadCustPlanMappingPojo.getValidity();
		this.planName = leadCustPlanMappingPojo.getPlanName();
		this.discount = leadCustPlanMappingPojo.getDiscount();
		this.plangroupid = leadCustPlanMappingPojo.getPlangroupid();
		this.planValidityDays = leadCustPlanMappingPojo.getPlanValidityDays();
		this.isInvoiceToOrg = leadCustPlanMappingPojo.getIsInvoiceToOrg();
		this.billTo = leadCustPlanMappingPojo.getBillTo();
		this.newAmount = leadCustPlanMappingPojo.getNewAmount();
		this.renewalId = leadCustPlanMappingPojo.getRenewalId();
		this.custRefId = leadCustPlanMappingPojo.getCustRefId();
		this.isTrialPlan = leadCustPlanMappingPojo.getIsTrialPlan();
	}
}
