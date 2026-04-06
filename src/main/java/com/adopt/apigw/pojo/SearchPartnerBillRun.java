package com.adopt.apigw.pojo;

import java.time.LocalDate;
import java.util.List;

import com.adopt.apigw.model.postpaid.PartnerBillRun;

public class SearchPartnerBillRun {

	private Integer billrunid;
	private LocalDate billfromdate;
	private LocalDate billtodate;
	private String billrunstatus;
	
	private List<PartnerBillRun> billrunlist;

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

	public String getBillrunstatus() {
		return billrunstatus;
	}

	public void setBillrunstatus(String billrunstatus) {
		this.billrunstatus = billrunstatus;
	}

	public List<PartnerBillRun> getBillrunlist() {
		return billrunlist;
	}

	public void setBillrunlist(List<PartnerBillRun> billrunlist) {
		this.billrunlist = billrunlist;
	}
	
	
}
