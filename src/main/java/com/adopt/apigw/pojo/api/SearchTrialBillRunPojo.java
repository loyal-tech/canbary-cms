package com.adopt.apigw.pojo.api;

public class SearchTrialBillRunPojo {
	
	private Integer billrunid;
	
	private String billfromdate;
	
	private String billtodate;
	
	private String billrunstatus;

	public Integer getBillrunid() {
		return billrunid;
	}

	public void setBillrunid(Integer billrunid) {
		this.billrunid = billrunid;
	}

	public String getBillfromdate() {
		return billfromdate;
	}

	public void setBillfromdate(String billfromdate) {
		this.billfromdate = billfromdate;
	}

	public String getBilltodate() {
		return billtodate;
	}

	public void setBilltodate(String billtodate) {
		this.billtodate = billtodate;
	}

	public String getBillrunstatus() {
		return billrunstatus;
	}

	public void setBillrunstatus(String billrunstatus) {
		this.billrunstatus = billrunstatus;
	}

}
