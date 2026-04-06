package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CreditDebitDocMapping;
import com.adopt.apigw.model.postpaid.CreditDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDocMessage {

    private Integer id;
    private Integer customer;

    private String paymode;
    private String paymentdate;

    private String paydetails1; //Bank

    private String paydetails2; //Branch
    private String paydetails3; //ChequeNo
    private String paydetails4; //PaymentReferenceNo

    private Double amount = 0.0;

    private String status;

    private Integer approverid;

    private String remarks;

    private String referenceno;

    private Boolean isDelete;

    private Boolean tdsflag;

    private Double tdsamount;

    private Boolean is_reversed;

    private LocalDate resevrsed_date;
    private Integer resverse_debitdoc_id;
    private Boolean tds_received;
    private LocalDate tds_received_date;

    private Integer tds_credit_doc_id;

    private Integer mvnoId;

    private Long buID;

    private Integer lcoid;

    private Integer invoiceId;

    private String paytype;

    private String type;

    private Integer nextTeamHierarchyMappingId;

    private String reciptNo;


    private Double adjustedAmount;

    private Long bankManagement;

    private Long destinationBank;

    private String filename;
    private String uniquename;
    private Double barteramount;

    private Double tdsAmount;
    private Double abbsAmount;

    private String branchname;

    private String onlinesource;
    private String creditdocumentno;
    private List<CreditDebitDocMapping> creditDebitDocMappingList ;
    private String chequedate;
    private String createdate;
    private String lastmodifiedate;
    private String ledgerId;

    private Integer loggedInuserid;
    private Double walletBalance;
    private String createdByName;

    public CreditDocMessage(CreditDocument creditDocument, List<CreditDebitDocMapping> creditDebitDocMappings) {
        this.id = creditDocument.getId();
        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter1 =  DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        this.customer = creditDocument.getCustomer().getId();
        this.paymode = creditDocument.getPaymode();
        this.paydetails1 = creditDocument.getPaydetails1();
        this.paydetails2 = creditDocument.getPaydetails2();
        this.paydetails3 = creditDocument.getPaydetails3();
        this.paydetails4 = creditDocument.getPaydetails4();
        this.amount = creditDocument.getAmount();
        this.status = creditDocument.getStatus();
        this.remarks = creditDocument.getRemarks();
        this.referenceno = creditDocument.getReferenceno();
        this.isDelete = creditDocument.getIsDelete();
        this.tdsflag = creditDocument.getTdsflag();
        this.tdsamount = creditDocument.getTdsamount();
        this.is_reversed = creditDocument.getIs_reversed();
        this.resevrsed_date = creditDocument.getResevrsed_date();
        this.paymentdate = creditDocument.getPaymentdate().format(formatter);
        this.resverse_debitdoc_id = resverse_debitdoc_id;
        this.tds_received = creditDocument.getTds_received();
        this.tds_received_date = creditDocument.getTds_received_date();
        this.tds_credit_doc_id = creditDocument.getTds_credit_doc_id();
        this.mvnoId = creditDocument.getMvnoId();
        this.buID = creditDocument.getBuID();
        this.lcoid = creditDocument.getLcoid();
        this.invoiceId = creditDocument.getInvoiceId();
        this.paytype = creditDocument.getPaytype();
        this.type = creditDocument.getType();
        this.nextTeamHierarchyMappingId = creditDocument.getNextTeamHierarchyMappingId();
        if(creditDocument.getCreditdocumentno()!=null) {
            this.reciptNo = creditDocument.getCreditdocumentno();
        }else {
            this.reciptNo = "-";
        }
        this.adjustedAmount = creditDocument.getAdjustedAmount();
        this.bankManagement = creditDocument.getBankManagement();
        this.destinationBank = creditDocument.getDestinationBank();
        this.filename = creditDocument.getFilename();
        this.uniquename = creditDocument.getUniquename();
        this.barteramount = creditDocument.getBarteramount();
        this.abbsAmount = creditDocument.getAbbsAmount();
        this.branchname = creditDocument.getBranchname();
        this.onlinesource = creditDocument.getOnlinesource();
        this.creditdocumentno = creditDocument.getCreditdocumentno();
        this.paymentdate = creditDocument.getPaymentdate().format(formatter);
        this.creditDebitDocMappingList=creditDebitDocMappings;
        this.approverid=creditDocument.getApproverid();
        if(creditDocument.getChequedate()!=null){
            this.chequedate=creditDocument.getChequedate().format(formatter);
                    }
        this.createdate=creditDocument.getCreatedate().format(formatter1);
        this.ledgerId=creditDocument.getLedgerId();
        this.walletBalance=creditDocument.getCustomer().getWalletbalance();
        this.createdByName=creditDocument.getCreatedByName();

    }
}
