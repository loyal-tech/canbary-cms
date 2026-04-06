package com.adopt.apigw.pojo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.adopt.apigw.model.postpaid.BillRun;

public class SearchBillRun {

	private Integer billrunid;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate billfromdate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate billtodate;
	private String billrunstatus;
	
	private List<BillRun> billrunlist;

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

	public List<BillRun> getBillrunlist() {
		return billrunlist;
	}

	public void setBillrunlist(List<BillRun> billrunlist) {
		this.billrunlist = billrunlist;
	}
	
	
}
