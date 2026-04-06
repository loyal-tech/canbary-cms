package com.adopt.apigw.pojo.api;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DebitDocSearchPojo {

    private Integer custid;
    private String customerName;
    private String paymentStatus;
    private Double adjustedAmount;
    private Integer nextStaff;
    private Integer nextTeamHierarchyMappingId;
    private String billrunstatus;
    private LocalDateTime createdate;
    private Double totalamount;
    private String docnumber;
    private LocalDateTime billdate;

    private String createdByName;

    private String custType;

    private String billableToName;

    private Integer billrunid;

    private String amountinwords;

    private Double discount;

    private LocalDateTime latepaymentdate;

    private LocalDateTime startdate;

    private LocalDateTime endate;

    private Integer id;

    private Double tax;

    private String status;

    private String custRefName;
    private String remarks;
    private String currency;


    //for invoiceSearch in all screens
    public DebitDocSearchPojo(Integer custid, String customerName, String paymentStatus, Double adjustedAmount, Integer nextStaff, Integer nextTeamHierarchyMappingId, String billrunstatus, LocalDateTime createdate, Double totalamount, String docnumber, LocalDateTime billdate,String createdByName,String custType,String billableToName,Integer id,String status,String custRefName , String remarks, String currency) {
        this.custid = custid;
        this.customerName = customerName;
        this.paymentStatus = paymentStatus;
        this.adjustedAmount = adjustedAmount;
        this.nextStaff = nextStaff;
        this.nextTeamHierarchyMappingId = nextTeamHierarchyMappingId;
        this.billrunstatus = billrunstatus;
        this.createdate = createdate;
        this.totalamount = totalamount;
        this.docnumber = docnumber;
        this.billdate = billdate;
        this.createdByName = createdByName;
        this.custType = custType;
        this.billableToName = billableToName;
        this.id = id;
        this.status = status;
        this.custRefName = custRefName;
        this.remarks=remarks;
        this.currency = currency;
    }

    public DebitDocSearchPojo(String customerName, String billrunstatus, LocalDateTime createdate, Double totalamount, String docnumber, LocalDateTime billdate,Integer billrunid,String amountinwords,Double discount,LocalDateTime latepaymentdate,LocalDateTime startdate,LocalDateTime endate,Double tax) {
        this.customerName = customerName;
        this.billrunstatus = billrunstatus;
        this.createdate = createdate;
        this.totalamount = totalamount;
        this.docnumber = docnumber;
        this.billdate = billdate;
        this.billrunid = billrunid;
        this.amountinwords = amountinwords;
        this.discount = discount;
        this.latepaymentdate = latepaymentdate;
        this.startdate = startdate;
        this.endate = endate;
        this.tax = tax;
    }




}
