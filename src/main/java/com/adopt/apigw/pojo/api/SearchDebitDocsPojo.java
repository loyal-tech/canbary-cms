package com.adopt.apigw.pojo.api;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class SearchDebitDocsPojo {

	private Integer billrunid;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate billfromdate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate billtodate;
	
	private String custname;
	
	private String custmobile;
	
	private String docnumber;
	
	private Integer customerid;

	private Double adjustedAmount;

	private  String type;
	private Long branchId;
	private Long businessunit;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate initiateDate;

	private Integer serviceId;

	private Integer serviceAreaId;

	private Integer staffId;
	private Integer partnerId;
	private List<String> status;
	private  String invoiceCancelRemarks;



	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public Long getBusinessunit() {
		return businessunit;
	}

	public void setBusinessunit(Long businessunit) {
		this.businessunit = businessunit;
	}

	public LocalDate getInitiateDate() {
		return initiateDate;
	}

	public void setInitiateDate(LocalDate initiateDate) {
		this.initiateDate = initiateDate;
	}




	public Double getAdjustedAmount() {
		return adjustedAmount;
	}

	public void setAdjustedAmount(Double adjustedAmount) {
		this.adjustedAmount = adjustedAmount;
	}



	public Integer getBillrunid() {
		return billrunid;
	}

	public void setBillrunid(Integer billrunid) {
		this.billrunid = billrunid;
	}

	public LocalDate getBillfromdate() {
		return billfromdate;
	}

	public void setBillfromdate(LocalDate billfromdate) {
		this.billfromdate = billfromdate;
	}

	public LocalDate getBilltodate() {
		return billtodate;
	}

	public void setBilltodate(LocalDate billtodate) {
		this.billtodate = billtodate;
	}

	public String getCustname() {
		return custname;
	}

	public void setCustname(String custname) {
		this.custname = custname;
	}

	public String getCustmobile() {
		return custmobile;
	}

	public void setCustmobile(String custmobile) {
		this.custmobile = custmobile;
	}

	public String getDocnumber() {
		return docnumber;
	}

	public void setDocnumber(String docnumber) {
		this.docnumber = docnumber;
	}

	public Integer getCustomerid() {
		return customerid;
	}

	public void setCustomerid(Integer customerid) {
		this.customerid = customerid;
	}

	public String getType() {return type;}

	public void setType(String type) {this.type = type;}


	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}

	private Integer planId;

	public Integer getPlanId() {
		return planId;
	}

	public void setPlanId(Integer planId) {
		this.planId = planId;
	}
}
