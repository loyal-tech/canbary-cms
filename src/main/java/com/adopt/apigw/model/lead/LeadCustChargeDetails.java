package com.adopt.apigw.model.lead;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffInclude;

@Entity
@Data
@ToString
@Table(name = "tblleadcustchargedtls")
public class LeadCustChargeDetails {

	@Id
	@Column(name = "cstchargeid", nullable = false, length = 40)
	private Integer id;

	private Integer planid;

	private Integer chargeid;

	@Column(name = "charge_name")
	private String chargeName;

	private String chargetype;

	private Double validity;

	private Double price;

	private Double actualprice;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;

	private String remarks;

	private Date charge_date;

	@Column(name = "charge_date_string")
	private String chargeDateString;

	private Date startdate;

	@Column(name = "startdate_string")
	private String startdateString;

	private Date enddate;

	@Column(name = "enddate_string")
	private String enddateString;

	private Double taxamount;

	private Boolean is_reversed;

	private LocalDateTime rev_date;

	@Column(name = "revdate_string")
	private String revdateString;

	private Double rev_amt;

	private String rev_remarks;

	@Column(name = "is_used")
	private Boolean isUsed;

	@Column(name = "purchase_entity_id")
	private Long purchaseEntityId;

	private Long ippooldtlsid;

	private Long debitdocid;

	@Column(name = "create_date_string")
	private String createDateString;

	@Column(name = "update_date_string")
	private String updateDateString;

	private String type;

	@Column(name = "plan_validity")
	private Integer planValidity;

	@Column(name = "units_of_validity")
	private String unitsOfValidity;

	@Column(name = "tax_id")
	private Integer taxId;

	@Column(name = "cust_plan_mappping_id")
	private Integer custPlanMapppingId;

	@Column(name = "last_bill_date")
	private LocalDate lastBillDate;

	@Column(name = "next_bill_date")
	private LocalDate nextBillDate;

	@Column(name = "billing_cycle")
	private Integer billingCycle;
	
	public LeadCustChargeDetails(LeadCustChargeDetailsPojo leadCustChargeDetailsPojo) {
		this.id = leadCustChargeDetailsPojo.getId();
		this.planid = leadCustChargeDetailsPojo.getPlanid();
		this.chargeid = leadCustChargeDetailsPojo.getChargeid();
		this.chargeName = leadCustChargeDetailsPojo.getChargeName();
		this.chargetype = leadCustChargeDetailsPojo.getChargetype();
		this.validity = leadCustChargeDetailsPojo.getValidity();
		this.price = leadCustChargeDetailsPojo.getPrice();
		this.actualprice = leadCustChargeDetailsPojo.getActualprice();
		if(leadCustChargeDetailsPojo.getLeadMasterId() != null)
			this.leadMaster = new LeadMaster(leadCustChargeDetailsPojo.getLeadMasterId());
		this.remarks = leadCustChargeDetailsPojo.getRemarks();
		this.charge_date = leadCustChargeDetailsPojo.getCharge_date();
		this.chargeDateString = leadCustChargeDetailsPojo.getChargeDateString();
		this.startdate = leadCustChargeDetailsPojo.getStartdate();
		this.startdateString = leadCustChargeDetailsPojo.getStartdateString();
		this.enddate = leadCustChargeDetailsPojo.getEnddate();
		this.enddateString = leadCustChargeDetailsPojo.getEnddateString();
		this.taxamount = leadCustChargeDetailsPojo.getTaxamount();
		this.is_reversed = leadCustChargeDetailsPojo.getIs_reversed();
		this.rev_date = leadCustChargeDetailsPojo.getRev_date();
		this.revdateString = leadCustChargeDetailsPojo.getRevdateString();
		this.rev_amt = leadCustChargeDetailsPojo.getRev_amt();
		this.rev_remarks = leadCustChargeDetailsPojo.getRev_remarks();
		this.isUsed = leadCustChargeDetailsPojo.getIsUsed();
		this.purchaseEntityId = leadCustChargeDetailsPojo.getPurchaseEntityId();
		this.ippooldtlsid = leadCustChargeDetailsPojo.getIppooldtlsid();
		this.debitdocid = leadCustChargeDetailsPojo.getDebitdocid();
		this.createDateString = leadCustChargeDetailsPojo.getCreateDateString();
		this.updateDateString = leadCustChargeDetailsPojo.getUpdateDateString();
		this.type = leadCustChargeDetailsPojo.getType();
		this.planValidity = leadCustChargeDetailsPojo.getPlanValidity();
		this.unitsOfValidity = leadCustChargeDetailsPojo.getUnitsOfValidity();
		this.taxId = leadCustChargeDetailsPojo.getTaxId();
		this.custPlanMapppingId = leadCustChargeDetailsPojo.getCustPlanMapppingId();
		this.lastBillDate = leadCustChargeDetailsPojo.getLastBillDate();
		this.nextBillDate = leadCustChargeDetailsPojo.getNextBillDate();
		this.billingCycle = leadCustChargeDetailsPojo.getBillingCycle();
	}
}
