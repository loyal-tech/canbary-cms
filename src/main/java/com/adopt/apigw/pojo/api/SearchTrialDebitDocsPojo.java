package com.adopt.apigw.pojo.api;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class SearchTrialDebitDocsPojo {
	
	private Integer billrunid;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate billfromdate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate billtodate;
	
	private String custname;
	
	private String custmobile;
	
	private String docnumber;
	
	private Integer customerid;

	public Integer getBillrunid() {
		return billrunid;
	}

	public void setBillrunid(Integer billrunid) {
		this.billrunid = billrunid;
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

	public Integer getCustomerid() {
		return customerid;
	}

	public void setCustomerid(Integer customerid) {
		this.customerid = customerid;
	}
	
}
