package com.adopt.apigw.pojo.api;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreditDocumentSearchPojo {

    private Integer id;
    private String customerName;
    private Double amount;
    private String documentno;
    private String invoiceNumber;
    private String paydetails2;
    private Double tdsamount;
    private Double abbsAmount;
    private String referenceno;
    private String paymode;
    private String type;
    private LocalDate paymentdate;
    private String status;
    private Integer approverid;
    private Integer nextTeamHierarchyMappingId;
    private  String filename;

    private String createbyname;

    private String remarks;

    private Integer custId;

    private boolean batchAssigned;

    private String mvnoName;
    private  String currency;

    private Integer parentId;

    public CreditDocumentSearchPojo(Integer id, String customerName, Double amount, String documentno, String invoiceNumber, String paydetails2, Double tdsamount, Double abbsAmount, String referenceno, String paymode, String type, LocalDate paymentdate, String status, Integer approverid, Integer nextTeamHierarchyMappingId,String createbyname,String remarks,String filename,Integer custId,boolean batchAssigned, String mvnoName,String currency,Integer parentId) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
        this.documentno = documentno;
        this.invoiceNumber = invoiceNumber;
        this.paydetails2 = paydetails2;
        this.tdsamount = tdsamount;
        this.abbsAmount = abbsAmount;
        this.referenceno = referenceno;
        this.paymode = paymode;
        this.type = type;
        this.paymentdate = paymentdate;
        this.status = status;
        this.approverid = approverid;
        this.nextTeamHierarchyMappingId = nextTeamHierarchyMappingId;
        this.createbyname = createbyname;
        this.remarks = remarks;
        this.filename=filename;
        this.custId = custId;
        this.batchAssigned = batchAssigned;
        this.mvnoName = mvnoName;
        this.currency = currency;
        this.parentId= parentId;
    }

}
