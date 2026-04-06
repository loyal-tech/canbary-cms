package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.pojo.PaymentListPojo;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class RecordPaymentPojo {

    private String referenceno;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate chequedate;

    private String chequedatestr;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentdate = LocalDate.now();

    private String paymentdatestr;

    private String chequeno;

    private String bank;

    private Integer customerid;

    private String paymode;

    private Double amount;

    private String paymentreferenceno;

    private String remark;

    private String branch;

    private List<Integer> invoiceId; //add list of invoiceid here

    private String type;

    private String paytype;

    private Integer mvnoId;

    private Long buId;
    private String bankManagement;
    private Long destinationBank;

    private Integer nextApprover;

    private Integer nextStaffId;
    private String reciptNo;
    private String filename;
    private String uniquename;
    private Double barteramount;
    private Double tdsAmount;
    private Double abbsAmount;
    private Integer creditDocId;
    private String onlinesource;

    private List<PaymentListPojo> paymentListPojos;

    private Integer loggedInuserid;

    private Boolean isAdjusted;

    public List<PaymentListPojo> getPaymentListPojos() {
        return paymentListPojos;
    }

    public void setPaymentListPojos(List<PaymentListPojo> paymentListPojos) {
        this.paymentListPojos = paymentListPojos;
    }

    public String getOnlinesource() {
        return onlinesource;
    }

    public void setOnlinesource(String onlinesource) {
        this.onlinesource = onlinesource;
    }



    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }
    private String file;
    public String getBatchname() {
        return batchname;
    }
    public void setBatchname(String batchname) {
        this.batchname = batchname;
    }
    private String batchname;



    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUniquename() {
        return uniquename;
    }

    public void setUniquename(String uniquename) {
        this.uniquename = uniquename;
    }

    public Double getBarteramount() {
        return barteramount;
    }

    public void setBarteramount(Double barteramount) {
        this.barteramount = barteramount;
    }

    public String getBankManagement() {
        return bankManagement;
    }

    public void setBankManagement(String bankManagement) {
        this.bankManagement = bankManagement;
    }

    public Long getDestinationBank() {
        return destinationBank;
    }

    public void setDestinationBank(Long destinationBank) {
        this.destinationBank = destinationBank;
    }

    public RecordPaymentPojo(CreditDocument creditDocument){
        if (creditDocument.getReferenceno()!=null){
            this.referenceno = creditDocument.getReferenceno();
        }
        if (creditDocument.getChequedate()!=null) {
            this.chequedate = creditDocument.getChequedate();
        }
        if (creditDocument.getPaymentdate()!=null){
            this.paymentdate = creditDocument.getPaymentdate();
        }
        if (creditDocument.getPaydetails3()!=null){
            this.chequeno = creditDocument.getPaydetails3();
        }
        this.customerid = creditDocument.getCustomer().getId();
        this.paymode = creditDocument.getPaymode();
        this.amount = creditDocument.getAmount();
        this.paymentreferenceno = creditDocument.getPaymentreferenceno();
        this.remark = creditDocument.getRemarks();
        this.branch = creditDocument.getBranchname();
        this.type = creditDocument.getType();
        this.paytype = creditDocument.getPaytype();
        this.mvnoId = creditDocument.getMvnoId();
        this.buId = creditDocument.getBuID();
        this.nextApprover = creditDocument.getApproverid();
        this.nextStaffId = creditDocument.getApproverid();
        this.reciptNo = creditDocument.getReciptNo();
        this.abbsAmount = creditDocument.getAbbsAmount();
        this.creditDocId = creditDocument.getId();
        this.onlinesource = creditDocument.getOnlinesource();
    }

}
