package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreditDocumentPojo extends Auditable {

    private Integer id;

    private String paymode;

    private LocalDate paymentdate;

    private LocalDate chequedate;

    private String paydetails1;

    private String paydetails2;

    private String paydetails3;

    private String paydetails4;

    private Double amount;

    private String status;

    private Integer approverid;

    private String remarks;

    private String referenceno;
    private String xmldocument;
    private Integer custId;

    private String reciptNo;

    private Boolean isDelete = false;

    private String chequeNo;
    private String bankName;
    private Long destinationBank;
    private String branch;
    private Boolean tdsflag = false;
    private Double tdsamount;
    private Boolean is_reversed = false;
    private LocalDate resevrsed_date;
    private Integer resverse_debitdoc_id;
    private Boolean tds_received = false;
    private LocalDate tds_received_date;
    private Integer tds_credit_doc_id;

    private Double adjustedAmount;

    private String customerName;

    private Long serviceAreaId;

    private Integer invoiceId;

    private String invoiceNumber;

    private String type;

    private String paytype;

    private Boolean batchAssigned;

    private Integer nextTeamHierarchyMappingId;

    private String staff;

    private String documentno;

    private List<Long> buId;

    private String creditdocumentno;

    private String paymentreferenceno;

    public Integer getNextTeamHierarchyMappingId() {
        return nextTeamHierarchyMappingId;
    }

    public void setNextTeamHierarchyMappingId(Integer nextTeamHierarchyMappingId) {
        this.nextTeamHierarchyMappingId = nextTeamHierarchyMappingId;
    }


    private Integer mvnoId;

    private Integer lcoId;
    private Double tdsAmount;
    private Double abbsAmount;


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

    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }

    public Integer getMvnoId() {
        return mvnoId;
    }

    public String getPaymode() {
        return paymode;
    }

    public void setPaymode(String paymode) {
        this.paymode = paymode;
    }

    public LocalDate getPaymentdate() {
        return paymentdate;
    }

    public void setPaymentdate(LocalDate paymentdate) {
        this.paymentdate = paymentdate;
    }

    public String getPaydetails1() {
        return paydetails1;
    }

    public void setPaydetails1(String paydetails1) {
        this.paydetails1 = paydetails1;
    }

    public String getPaydetails2() {
        return paydetails2;
    }

    public void setPaydetails2(String paydetails2) {
        this.paydetails2 = paydetails2;
    }

    public String getPaydetails3() {
        return paydetails3;
    }

    public void setPaydetails3(String paydetails3) {
        this.paydetails3 = paydetails3;
    }

    public String getPaydetails4() {
        return paydetails4;
    }

    public void setPaydetails4(String paydetails4) {
        this.paydetails4 = paydetails4;
    }

    public double getAmount() {
        if(amount != null)
            return amount;
        else
            return 0;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getApproverid() {
        return approverid;
    }

    public void setApproverid(Integer approverid) {
        this.approverid = approverid;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public void setLcoId(Integer lcoId) {
        this.lcoId = lcoId;
    }

    public Integer getLcoId() {
        return lcoId;
    }

    public String getPaymentreferenceno() {
        return paymentreferenceno;
    }

    public void setPaymentreferenceno(String paymentreferenceno) {
        this.paymentreferenceno = paymentreferenceno;
    }


    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private CustomersPojo customer;

}
