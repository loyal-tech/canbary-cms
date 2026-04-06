package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.postpaid.TrialDebitDocumentDetail;

import java.time.LocalDateTime;
import java.util.List;

public class TrialDebitDocumentPojo extends ParentPojo {

    private Integer id;

    private String docnumber;

    private CustomersPojo customerPojo;

    private LocalDateTime billdate;

    private LocalDateTime createdate;

    private LocalDateTime startdate;

    private LocalDateTime endate;

    private LocalDateTime duedate;

    private LocalDateTime latepaymentdate;

    private double subtotal;

    private double tax;

    private double discount;

    private double totalamount;

    private double previousbalance;

    private double latepaymentfee;

    private double currentpayment;

    private double currentdebit;

    private double currentcredit;

    private double totaldue;

    private String amountinwords;

    private String dueinwords;

    private Integer billrunid;

    private String billrunstatus;

    private String document;

    private Boolean isDelete;

    private Integer custid;

    private String customerName;

    private String custType;
    private String paymentStatus;

    private String billableToName;

    private List<TrialDebitDocumentDetail> debitDocDetails;


    public List<TrialDebitDocumentDetail> getDebitDocDetails() {
        return debitDocDetails;
    }

    public void setDebitDocDetails(List<TrialDebitDocumentDetail> debitDocDetails) {
        this.debitDocDetails = debitDocDetails;
    }

    public String getBillableToName() {
        return billableToName;
    }

    public void setBillableToName(String billableToName) {
        this.billableToName = billableToName;
    }

    public void setPurchaseorder_id(Long purchaseorder_id) {
        this.purchaseorder_id = purchaseorder_id;
    }

    private Long purchaseorder_id;

//	public List<TrialDebitDocumentDetail> getDebitDocDetails() {
//		return debitDocDetails;
//	}
//
//	public void setDebitDocDetails(List<TrialDebitDocumentDetail> debitDocDetails) {
//		this.debitDocDetails = debitDocDetails;
//	}

    public Long getPurchaseorder_id() {
        return purchaseorder_id;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDocnumber() {
        return docnumber;
    }

    public void setDocnumber(String docnumber) {
        this.docnumber = docnumber;
    }

    public CustomersPojo getCustomerPojo() {
        return customerPojo;
    }

    public void setCustomerPojo(CustomersPojo customerPojo) {
        this.customerPojo = customerPojo;
    }

    public LocalDateTime getBilldate() {
        return billdate;
    }

    public void setBilldate(LocalDateTime billdate) {
        this.billdate = billdate;
    }

    public LocalDateTime getCreatedate() {
        return createdate;
    }

    public void setCreatedate(LocalDateTime createdate) {
        this.createdate = createdate;
    }

    public LocalDateTime getStartdate() {
        return startdate;
    }

    public void setStartdate(LocalDateTime startdate) {
        this.startdate = startdate;
    }

    public LocalDateTime getEndate() {
        return endate;
    }

    public void setEndate(LocalDateTime endate) {
        this.endate = endate;
    }

    public LocalDateTime getDuedate() {
        return duedate;
    }

    public void setDuedate(LocalDateTime duedate) {
        this.duedate = duedate;
    }

    public LocalDateTime getLatepaymentdate() {
        return latepaymentdate;
    }

    public void setLatepaymentdate(LocalDateTime latepaymentdate) {
        this.latepaymentdate = latepaymentdate;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(double totalamount) {
        this.totalamount = totalamount;
    }

    public double getPreviousbalance() {
        return previousbalance;
    }

    public void setPreviousbalance(double previousbalance) {
        this.previousbalance = previousbalance;
    }

    public double getLatepaymentfee() {
        return latepaymentfee;
    }

    public void setLatepaymentfee(double latepaymentfee) {
        this.latepaymentfee = latepaymentfee;
    }

    public double getCurrentpayment() {
        return currentpayment;
    }

    public void setCurrentpayment(double currentpayment) {
        this.currentpayment = currentpayment;
    }

    public double getCurrentdebit() {
        return currentdebit;
    }

    public void setCurrentdebit(double currentdebit) {
        this.currentdebit = currentdebit;
    }

    public double getCurrentcredit() {
        return currentcredit;
    }

    public void setCurrentcredit(double currentcredit) {
        this.currentcredit = currentcredit;
    }

    public double getTotaldue() {
        return totaldue;
    }

    public void setTotaldue(double totaldue) {
        this.totaldue = totaldue;
    }

    public String getAmountinwords() {
        return amountinwords;
    }

    public void setAmountinwords(String amountinwords) {
        this.amountinwords = amountinwords;
    }

    public String getDueinwords() {
        return dueinwords;
    }

    public void setDueinwords(String dueinwords) {
        this.dueinwords = dueinwords;
    }

    public Integer getBillrunid() {
        return billrunid;
    }

    public void setBillrunid(Integer billrunid) {
        this.billrunid = billrunid;
    }

    public String getBillrunstatus() {
        return billrunstatus;
    }

    public void setBillrunstatus(String billrunstatus) {
        this.billrunstatus = billrunstatus;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Integer getCustid() {
        return custid;
    }

    public void setCustid(Integer custid) {
        this.custid = custid;
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType;
    }
}
