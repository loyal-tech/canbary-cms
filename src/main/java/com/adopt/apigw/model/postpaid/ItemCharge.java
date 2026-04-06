package com.adopt.apigw.model.postpaid;

import java.util.Date;

public class ItemCharge {

	Integer cstchargeid;

	String type;

	String planid;

	String planname;

	String chargeid;

	String chargename;

	String chargedescription;

	double price;

	String chargetype;

	int billingcycle;

	double tax;

	Date startDate;

	Date endDate;
	
	double discount;
	
	String taxid;

	String customerid;
	
	String partenrid;
	
	String comm_type;
	
	String comm_rel_value;
	
	double comm_value;
	
	String saccode;

	String validity;

	String  unitOfValidity;

	double customerDiscount;

	Date planStartDate;

	Date planExpireDate;

	String planValidityDays;

	String customerLevelDiscount;

	private String billTo;

	private boolean isInvoiceToOrg;

	private Double newAmount;

	private Long qty;

	private String custpackageid;

	private String productName;


	private Date chargeStartDate;

	private String custRefName;

	private Boolean isRoyaltyApply;


	public Boolean getRoyaltyApply() {
		return isRoyaltyApply;
	}


	public void setRoyaltyApply(Boolean royaltyApply) {
		isRoyaltyApply = royaltyApply;
	}


	public Date getChargeStartDate() {
		return chargeStartDate;
	}

	public void setChargeStartDate(Date chargeStartDate) {
		this.chargeStartDate = chargeStartDate;
	}

	public String getType() {return type;}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getCstchargeid() {
		return cstchargeid;
	}

	public void setCstchargeid(Integer cstchargeid) {
		this.cstchargeid = cstchargeid;
	}

	public Long getQty() {
		return qty;
	}

	public void setQty(Long qty) {
		this.qty = qty;
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

	public double getComm_value() {
		return comm_value;
	}

	public void setComm_value(double comm_value) {
		this.comm_value = comm_value;
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

	/**
	 * @return the planid
	 */
	public String getPlanid() {

		return planid;
	}

	/**
	 * @param planid
	 *            the planid to set
	 */
	public void setPlanid(String planid) {

		this.planid = planid;
	}

	/**
	 * @return the planname
	 */
	public String getPlanname() {

		return planname;
	}

	/**
	 * @param planname
	 *            the planname to set
	 */
	public void setPlanname(String planname) {

		this.planname = planname;
	}

	/**
	 * @return the chargeid
	 */
	public String getChargeid() {

		return chargeid;
	}

	/**
	 * @param chargeid
	 *            the chargeid to set
	 */
	public void setChargeid(String chargeid) {

		this.chargeid = chargeid;
	}

	/**
	 * @return the chargename
	 */
	public String getChargename() {

		return chargename;
	}

	/**
	 * @param chargename
	 *            the chargename to set
	 */
	public void setChargename(String chargename) {

		this.chargename = chargename;
	}

	/**
	 * @return the chargedescription
	 */
	public String getChargedescription() {

		return chargedescription;
	}

	/**
	 * @param chargedescription
	 *            the chargedescription to set
	 */
	public void setChargedescription(String chargedescription) {

		this.chargedescription = chargedescription;
	}

	/**
	 * @return the price
	 */
	public Double getPrice() {

		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(Double price) {

		this.price = price;
	}

	/**
	 * @return the chargetype
	 */
	public String getChargetype() {

		return chargetype;
	}

	/**
	 * @param chargetype
	 *            the chargetype to set
	 */
	public void setChargetype(String chargetype) {

		this.chargetype = chargetype;
	}

	/**
	 * @return the billingcycle
	 */
	public int getBillingcycle() {

		return billingcycle;
	}

	/**
	 * @param billingcycle
	 *            the billingcycle to set
	 */
	public void setBillingcycle(int billingcycle) {

		this.billingcycle = billingcycle;
	}

	/**
	 * @return the tax
	 */
	public double getTax() {

		return tax;
	}

	/**
	 * @param tax
	 *            the tax to set
	 */
	public void setTax(double tax) {

		this.tax = tax;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}


	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}




	public String getTaxid() {
		return taxid;
	}

	public void setTaxid(String taxid) {
		this.taxid = taxid;
	}


	public String getSaccode() {
		return saccode;
	}

	public void setSaccode(String saccode) {
		this.saccode = saccode;
	}

	public double getCustomerDiscount() {
		return customerDiscount;
	}

	public void setCustomerDiscount(double customerDiscount) {
		this.customerDiscount = customerDiscount;
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

	public String getCustpackageid() {
		return custpackageid;
	}

	public void setCustpackageid(String custpackageid) {
		this.custpackageid = custpackageid;
	}

	public String getCustRefName() {
		return custRefName;
	}

	public void setCustRefName(String custRefName) {
		this.custRefName = custRefName;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ItemCharge [planid=" + planid + ", planname=" + planname + ", chargeid=" + chargeid + ", chargename="
				+ chargename + ", chargedescription=" + chargedescription + ", price=" + price + ", chargetype="
				+ chargetype + ", billingcycle=" + billingcycle + ", tax=" + tax + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", discount=" + discount + ", taxid=" + taxid + ", customerid=" + customerid
				+ ", partenrid=" + partenrid + ", comm_type=" + comm_type + ", comm_rel_value=" + comm_rel_value
				+ ", comm_value=" + comm_value + ", saccode=" + saccode + "]";
	}


}
