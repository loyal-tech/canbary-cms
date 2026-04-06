package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

@Data
public class PurchasedHistoryDTO {
    private Long id;
    private String invoiceNo;
    private Integer custId;

    private String purchesdate;
    private Double amount;
    //    private LocalDateTime createdate;
//    private LocalDateTime endate;
//    private LocalDateTime duedate;
//    private Double subtotal;
//    private Double tax;
//    private Double discount;
//    private Double totalamount;
//    private Double previousbalance;
//    private Double latepaymentfee;
//    private Double currentpayment;
//    private Double currentdebit;
//    private Double currentcredit;
//    private Double totaldue;
//    private String amountinwords;
//    private String dueinwords;
//    private Integer billrunid;
//    private String billrunstatus;
//    private String document;
    private Boolean isDelete = false;
    private Integer planId;
    private String planName;
    private String service;
    private String partnerName;
    private String purchasedBy;
}
