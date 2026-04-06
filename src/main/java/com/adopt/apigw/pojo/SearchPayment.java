package com.adopt.apigw.pojo;

import java.time.LocalDate;
import java.util.List;

import com.querydsl.core.types.Expression;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.model.postpaid.Partner;

@Getter
@Setter
public class SearchPayment {


    private String referenceno;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate payfromdate;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate paytodate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate recordfromdate;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordtodate;

	private String idlist;
	private String emailreceipt;
	private String remarks;

	private String paystatus;

	private Customers customer;
	private String paymode;
	
	private String customerid;
	
	private List<CreditDocument> paymentlist;

	private Partner partner;
	
	private String type;

	private Integer nextApprover;

	private Integer nextStaffId;

	private String mobileNumber;

	private String invoiceNumber;

	private String chequeNo;
	private String paydetails1;//bankName

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate chequedate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate paymentdate;

	private String userName;

	private Integer staff;

	private Integer approveId;

	private String branchname;

    private	List<Long> buID;

	private String creditDocumentNumber;

	private String receiptNo;

	private String destinationBank;
	private String partnerName;
	private Long serviceAreaId;



	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getDestinationBank() {
		return destinationBank;
	}
	public void setDestinationBank(String destinationBank) {
		this.destinationBank = destinationBank;
	}
	public String getCreditDocumentNumber() {
		return creditDocumentNumber;
	}

	public void setCreditDocumentNumber(String creditDocumentNumber) {
		this.creditDocumentNumber = creditDocumentNumber;
	}

	public Integer getNextStaffId() {
		return nextStaffId;
	}

	public void setNextStaffId(Integer nextStaffId) {
		this.nextStaffId = nextStaffId;
	}


	public Integer getNextApprover() {
		return nextApprover;
	}

	public void setNextApprover(Integer nextApprover) {
		this.nextApprover = nextApprover;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LocalDate getRecordfromdate() {
		return recordfromdate;
	}

	public void setRecordfromdate(LocalDate recordfromdate) {
		this.recordfromdate = recordfromdate;
	}

	public LocalDate getRecordtodate() {
		return recordtodate;
	}

	public void setRecordtodate(LocalDate recordtodate) {
		this.recordtodate = recordtodate;
	}


	public String getReferenceno() {
		return referenceno;
	}

	public void setReferenceno(String referenceno) {
		this.referenceno = referenceno;
	}

	public LocalDate getPayfromdate() {
		return payfromdate;
	}

	public void setPayfromdate(LocalDate payfromdate) {
		this.payfromdate = payfromdate;
	}

	public LocalDate getPaytodate() {
		return paytodate;
	}

	public void setPaytodate(LocalDate paytodate) {
		this.paytodate = paytodate;
	}

	public String getPaystatus() {
		return paystatus;
	}

	public void setPaystatus(String paystatus) {
		this.paystatus = paystatus;
	}

	public Customers getCustomer() {
		return customer;
	}

	public void setCustomer(Customers customer) {
		this.customer = customer;
	}

	public String getPaymode() {
		return paymode;
	}

	public void setPaymode(String paymode) {
		this.paymode = paymode;
	}

	public List<CreditDocument> getPaymentlist() {
		return paymentlist;
	}

	public void setPaymentlist(List<CreditDocument> paymentlist) {
		this.paymentlist = paymentlist;
	}
	
	public String getEmailreceipt() {
		return emailreceipt;
	}
	public void setEmailreceipt(String emailreceipt) {
		this.emailreceipt = emailreceipt;
	}
	public String getIdlist() {
		return idlist;
	}
	public void setIdlist(String idlist) {
		this.idlist = idlist;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}


}
