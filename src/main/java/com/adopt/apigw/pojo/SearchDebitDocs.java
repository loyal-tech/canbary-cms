package com.adopt.apigw.pojo;

import java.time.LocalDate;
import java.util.List;

import com.adopt.apigw.model.postpaid.DebitDocument;

public class SearchDebitDocs {

	private Integer billrunid;
	private LocalDate billfromdate;
	private LocalDate billtodate;
	private String custname;
	private String custmobile;
	private String docnumber;
	private  String type;

	private List<DebitDocument> debitdoclist;

	public Integer getBillrunid() {
		return billrunid;
	}

	public void setBillrunid(Integer billrunid) {
		this.billrunid = billrunid;
	}

	public LocalDate getBillfromdate() {
		return billfromdate;
	}

	public void setBillfromdate(LocalDate billfomdate) {
		this.billfromdate = billfomdate;
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

	public List<DebitDocument> getDebitdoclist() {
		return debitdoclist;
	}

	public void setDebitdoclist(List<DebitDocument> debitdoclist) {
		this.debitdoclist = debitdoclist;
	}

	public String getType() {return type;}

	public void setType(String type) {this.type = type;}
	
}
