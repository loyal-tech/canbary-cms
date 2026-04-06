package com.adopt.apigw.pojo.api;

import java.time.LocalDate;

public class SearchPartnerCreditDocsPojo {

	private Integer billrunid;
	
	private LocalDate billfromdate;
	
	private LocalDate billtodate;
	
	private String partnername;
	
	private String partnermobile;
	
	private String docnumber;

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

	public String getPartnername() {
		return partnername;
	}

	public void setPartnername(String partnername) {
		this.partnername = partnername;
	}

	public String getPartnermobile() {
		return partnermobile;
	}

	public void setPartnermobile(String partnermobile) {
		this.partnermobile = partnermobile;
	}

	public String getDocnumber() {
		return docnumber;
	}

	public void setDocnumber(String docnumber) {
		this.docnumber = docnumber;
	}
}
