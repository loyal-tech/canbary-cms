package com.adopt.apigw.model.lead;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.adopt.apigw.rabbitMq.message.LeadCustChargeDetailsPojoMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadCustChargeDetailsPojo {

	private Integer id;

	private Integer planid;

	private Integer chargeid;

	private String chargeName;

	private String chargetype;

	private Double validity;

	private Double price;

	private Double actualprice;

	private Long leadMasterId;

	private String remarks;

	private Date charge_date;

	private String chargeDateString;

	private Date startdate;

	private String startdateString;

	private Date enddate;

	private String enddateString;

	private Double taxamount;

	private Boolean is_reversed;

	private LocalDateTime rev_date;

	private String revdateString;

	private Double rev_amt;

	private String rev_remarks;

	private Boolean isUsed;

	private Long purchaseEntityId;

	private Long ippooldtlsid;

	private Long debitdocid;

	private String createDateString;

	private String updateDateString;

	private String type;

	private Integer planValidity;

	private String unitsOfValidity;

	private Integer taxId;

	private Integer custPlanMapppingId;

	private LocalDate lastBillDate;

	private LocalDate nextBillDate;

	private Integer billingCycle;
	
	public LeadCustChargeDetailsPojo(LeadCustChargeDetails leadCustChargeDetails) {
		this.id = leadCustChargeDetails.getId();
		this.planid = leadCustChargeDetails.getPlanid();
		this.chargeid = leadCustChargeDetails.getChargeid();
		this.chargeName = leadCustChargeDetails.getChargeName();
		this.chargetype = leadCustChargeDetails.getChargetype();
		this.validity = leadCustChargeDetails.getValidity();
		this.price = leadCustChargeDetails.getPrice();
		this.actualprice = leadCustChargeDetails.getActualprice();
		if(leadCustChargeDetails.getLeadMaster() != null)
			this.leadMasterId = leadCustChargeDetails.getLeadMaster().getId();
		this.remarks = leadCustChargeDetails.getRemarks();
		this.charge_date = leadCustChargeDetails.getCharge_date();
		this.chargeDateString = leadCustChargeDetails.getChargeDateString();
		this.startdate = leadCustChargeDetails.getStartdate();
		this.startdateString = leadCustChargeDetails.getStartdateString();
		this.enddate = leadCustChargeDetails.getEnddate();
		this.enddateString = leadCustChargeDetails.getEnddateString();
		this.taxamount = leadCustChargeDetails.getTaxamount();
		this.is_reversed = leadCustChargeDetails.getIs_reversed();
		this.rev_date = leadCustChargeDetails.getRev_date();
		this.revdateString = leadCustChargeDetails.getRevdateString();
		this.rev_amt = leadCustChargeDetails.getRev_amt();
		this.rev_remarks = leadCustChargeDetails.getRev_remarks();
		this.isUsed = leadCustChargeDetails.getIsUsed();
		this.purchaseEntityId = leadCustChargeDetails.getPurchaseEntityId();
		this.ippooldtlsid = leadCustChargeDetails.getIppooldtlsid();
		this.debitdocid = leadCustChargeDetails.getDebitdocid();
		this.createDateString = leadCustChargeDetails.getCreateDateString();
		this.updateDateString = leadCustChargeDetails.getUpdateDateString();
		this.type = leadCustChargeDetails.getType();
		this.planValidity = leadCustChargeDetails.getPlanValidity();
		this.unitsOfValidity = leadCustChargeDetails.getUnitsOfValidity();
		this.taxId = leadCustChargeDetails.getTaxId();
		this.custPlanMapppingId = leadCustChargeDetails.getCustPlanMapppingId();
		this.lastBillDate = leadCustChargeDetails.getLastBillDate();
		this.nextBillDate = leadCustChargeDetails.getNextBillDate();
		this.billingCycle = leadCustChargeDetails.getBillingCycle();
	}
	
	public LeadCustChargeDetailsPojo(LeadCustChargeDetailsPojoMessage leadCustChargeDetails) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		this.id = leadCustChargeDetails.getId();
		this.planid = leadCustChargeDetails.getPlanid();
		this.chargeid = leadCustChargeDetails.getChargeid();
		this.chargeName = leadCustChargeDetails.getChargeName();
		this.chargetype = leadCustChargeDetails.getChargetype();
		this.validity = leadCustChargeDetails.getValidity();
		this.price = leadCustChargeDetails.getPrice();
		this.actualprice = leadCustChargeDetails.getActualprice();
		this.leadMasterId = leadCustChargeDetails.getLeadMasterId();
		this.remarks = leadCustChargeDetails.getRemarks();
		this.chargeDateString = leadCustChargeDetails.getChargeDateString();
		this.startdateString = leadCustChargeDetails.getStartdateString();
		this.enddateString = leadCustChargeDetails.getEnddateString();
		this.taxamount = leadCustChargeDetails.getTaxamount();
		this.is_reversed = leadCustChargeDetails.getIs_reversed();
		this.rev_date = leadCustChargeDetails.getRev_date();
		this.revdateString = leadCustChargeDetails.getRevdateString();
		this.rev_amt = leadCustChargeDetails.getRev_amt();
		this.rev_remarks = leadCustChargeDetails.getRev_remarks();
		this.isUsed = leadCustChargeDetails.getIsUsed();
		this.purchaseEntityId = leadCustChargeDetails.getPurchaseEntityId();
		this.ippooldtlsid = leadCustChargeDetails.getIppooldtlsid();
		this.debitdocid = leadCustChargeDetails.getDebitdocid();
		this.createDateString = leadCustChargeDetails.getCreateDateString();
		this.updateDateString = leadCustChargeDetails.getUpdateDateString();
		this.type = leadCustChargeDetails.getType();
		this.planValidity = leadCustChargeDetails.getPlanValidity();
		this.unitsOfValidity = leadCustChargeDetails.getUnitsOfValidity();
		this.taxId = leadCustChargeDetails.getTaxId();
		this.custPlanMapppingId = leadCustChargeDetails.getCustPlanMapppingId();
		if (leadCustChargeDetails.getLastBillDate() != null) {
			this.lastBillDate = LocalDate.parse(leadCustChargeDetails.getLastBillDate(),formatter);
		}
		if (leadCustChargeDetails.getNextBillDate() != null) {
			this.lastBillDate = LocalDate.parse(leadCustChargeDetails.getNextBillDate(),formatter);
		}
		this.billingCycle = leadCustChargeDetails.getBillingCycle();
	}
}
