package com.adopt.apigw.model.postpaid;

import java.util.Date;

public class PostpaidItemCharge {

	Integer cstchargeid;

	String type;

	String planid;

	String planname;

	String chargeid;

	String chargename;

	String chargedescription;

	Double price;

	String chargetype;

	int billingcycle;

	Double tax;

	Date startDate;

	Date endDate;
	
	Double discount;
	
	String taxid;

	String customerid;
	
	String partenrid;
	
	String comm_type;
	
	String comm_rel_value;
	
	double comm_value;

	String validity;

	String  unitOfValidity;

	Double customerDiscount;

	Date planStartDate;

	Date planExpireDate;

	String planValidityDays;

	String customerLevelDiscount;

	private String billTo;

	private boolean isInvoiceToOrg;

	private Double newAmount;

	private Date nextChargeBillDate;

	private Date lastChargeBillDate;

	private Integer historyId;

	private Integer custpackageId;

	private  Integer customerId;

	private Date chargeStartDate;

	private Boolean isFirstChargeApply;

	private Integer custRefId;

	private String custRefName;

	private Long chargeBillDay;

	private Boolean isRoyaltyApply;


	public Boolean getRoyaltyApply() {
		return isRoyaltyApply;
	}


	public void setRoyaltyApply(Boolean royaltyApply) {
		isRoyaltyApply = royaltyApply;
	}

	public Integer getCstchargeid() {
		return cstchargeid;
	}

	public void setCstchargeid(Integer cstchargeid) {
		this.cstchargeid = cstchargeid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPlanid() {
		return planid;
	}

	public void setPlanid(String planid) {
		this.planid = planid;
	}

	public String getPlanname() {
		return planname;
	}

	public void setPlanname(String planname) {
		this.planname = planname;
	}

	public String getChargeid() {
		return chargeid;
	}

	public void setChargeid(String chargeid) {
		this.chargeid = chargeid;
	}

	public String getChargename() {
		return chargename;
	}

	public void setChargename(String chargename) {
		this.chargename = chargename;
	}

	public String getChargedescription() {
		return chargedescription;
	}

	public void setChargedescription(String chargedescription) {
		this.chargedescription = chargedescription;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getChargetype() {
		return chargetype;
	}

	public void setChargetype(String chargetype) {
		this.chargetype = chargetype;
	}

	public int getBillingcycle() {
		return billingcycle;
	}

	public void setBillingcycle(int billingcycle) {
		this.billingcycle = billingcycle;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public String getTaxid() {
		return taxid;
	}

	public void setTaxid(String taxid) {
		this.taxid = taxid;
	}

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}

	public String getPartenrid() {
		return partenrid;
	}

	public void setPartenrid(String partenrid) {
		this.partenrid = partenrid;
	}

	public String getComm_type() {
		return comm_type;
	}

	public void setComm_type(String comm_type) {
		this.comm_type = comm_type;
	}

	public String getComm_rel_value() {
		return comm_rel_value;
	}

	public void setComm_rel_value(String comm_rel_value) {
		this.comm_rel_value = comm_rel_value;
	}

	public double getComm_value() {
		return comm_value;
	}

	public void setComm_value(double comm_value) {
		this.comm_value = comm_value;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getUnitOfValidity() {
		return unitOfValidity;
	}

	public void setUnitOfValidity(String unitOfValidity) {
		this.unitOfValidity = unitOfValidity;
	}

	public Double getCustomerDiscount() {
		return customerDiscount;
	}

	public void setCustomerDiscount(Double customerDiscount) {
		this.customerDiscount = customerDiscount;
	}

	public Date getPlanStartDate() {
		return planStartDate;
	}

	public void setPlanStartDate(Date planStartDate) {
		this.planStartDate = planStartDate;
	}

	public Date getPlanExpireDate() {
		return planExpireDate;
	}

	public void setPlanExpireDate(Date planExpireDate) {
		this.planExpireDate = planExpireDate;
	}

	public String getPlanValidityDays() {
		return planValidityDays;
	}

	public void setPlanValidityDays(String planValidityDays) {
		this.planValidityDays = planValidityDays;
	}

	public String getCustomerLevelDiscount() {
		return customerLevelDiscount;
	}

	public void setCustomerLevelDiscount(String customerLevelDiscount) {
		this.customerLevelDiscount = customerLevelDiscount;
	}

	public String getBillTo() {
		return billTo;
	}

	public void setBillTo(String billTo) {
		this.billTo = billTo;
	}

	public boolean isInvoiceToOrg() {
		return isInvoiceToOrg;
	}

	public void setInvoiceToOrg(boolean invoiceToOrg) {
		isInvoiceToOrg = invoiceToOrg;
	}

	public Double getNewAmount() {
		return newAmount;
	}

	public void setNewAmount(Double newAmount) {
		this.newAmount = newAmount;
	}

	public Date getNextChargeBillDate() {
		return nextChargeBillDate;
	}

	public void setNextChargeBillDate(Date nextChargeBillDate) {
		this.nextChargeBillDate = nextChargeBillDate;
	}

	public Date getLastChargeBillDate() {
		return lastChargeBillDate;
	}

	public void setLastChargeBillDate(Date lastChargeBillDate) {
		this.lastChargeBillDate = lastChargeBillDate;
	}

	public Integer getHistoryId() {
		return historyId;
	}

	public void setHistoryId(Integer historyId) {
		this.historyId = historyId;
	}

	public Integer getCustpackageId() {
		return custpackageId;
	}

	public void setCustpackageId(Integer custpackageId) {
		this.custpackageId = custpackageId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Date getChargeStartDate() {
		return chargeStartDate;
	}

	public void setChargeStartDate(Date chargeStartDate) {
		this.chargeStartDate = chargeStartDate;
	}

	public Boolean getFirstChargeApply() {
		return isFirstChargeApply;
	}

	public void setFirstChargeApply(Boolean firstChargeApply) {
		isFirstChargeApply = firstChargeApply;
	}

	public Integer getCustRefId() {
		return custRefId;
	}

	public void setCustRefId(Integer custRefId) {
		this.custRefId = custRefId;
	}

	public String getCustRefName() {
		return custRefName;
	}

	public void setCustRefName(String custRefName) {
		this.custRefName = custRefName;
	}

	public Long getChargeBillDay() {
		return chargeBillDay;
	}

	public void setChargeBillDay(Long chargeBillDay) {
		this.chargeBillDay = chargeBillDay;
	}
}
