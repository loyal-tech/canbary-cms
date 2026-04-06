package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.pojo.api.RecordPaymentPojo;
import com.adopt.apigw.rabbitMq.message.CreditDocMessage;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTCREDITDOC")
@EntityListeners(AuditableListener.class)
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CreditDocument.class)
public class CreditDocument extends Auditable {
	
/*
CREATE TABLE TBLTCREDITDOC
(
	CREDITDOCID serial primary key,
	CUSTID bigint unsigned not null,
	PAYMENTDATE TIMESTAMP,
	PAYMODE	varchar(50),
	PAYDETAILS1	varchar(200),
	PAYDETAILS2	varchar(200),
	PAYDETAILS3	varchar(200),
	PAYDETAILS4	varchar(200),
	AMOUNT	numeric (20,4),
	STATUS 	varchar(50), 
	APPROVEDBYSTAFFID	NUMERIC(20),
	REMARKS	VARCHAR(150),	
	CREATEDATE	TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CREATEDBYSTAFFID	NUMERIC(20),
	LASTMODIFIEDBYSTAFFID	NUMERIC(20),
	LASTMODIFIEDDATE	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (CUSTID) REFERENCES tblcustomers(custid)
);
 */

    @Id
    @DiffIgnore
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CREDITDOCID", nullable = false, length = 40)
    private Integer id;

    @DiffIgnore
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CUSTID")
    private Customers customer;

    @Column(name = "PAYMODE", nullable = false, length = 40)
    private String paymode;

    @DiffIgnore
    @Column(name = "paymentdate", nullable = false, length = 40)
    private LocalDate paymentdate;

    @DiffIgnore
    @Column(name = "chequedate", length = 40)
    private LocalDate chequedate;

    @DiffIgnore
    @Column(name = "PAYDETAILS1", nullable = false, length = 40)
    private String paydetails1; //Bank

    @DiffIgnore
    @Column(name = "PAYDETAILS2", nullable = false, length = 40)
    private String paydetails2; //Branch

    @DiffIgnore
    @Column(name = "PAYDETAILS3", nullable = false, length = 40)
    private String paydetails3; //ChequeNo

    @DiffIgnore
    @Column(name = "PAYDETAILS4", nullable = false, length = 40)
    private String paydetails4; //PaymentReferenceNo

    @Column(name = "amount", nullable = false, length = 40)
    private Double amount = 0.0;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @DiffIgnore
    @Column(name = "APPROVEDBYSTAFFID", nullable = false, length = 40)
    private Integer approverid;

    @Column(name = "remarks", length = 40)
    private String remarks;

    @DiffIgnore
    @Column(name = "referenceno", nullable = false, length = 40)
    private String referenceno;

    @DiffIgnore
    @Column(name = "xmldocument", nullable = false, length = 40)
    private String xmldocument;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    @DiffIgnore
    @Column(name = "tdsflag", columnDefinition = "Boolean default false", nullable = false)
    private Boolean tdsflag;

    @DiffIgnore
    private Double tdsamount;

    @DiffIgnore
    @Column(name = "is_reversed", columnDefinition = "Boolean default false", nullable = false)
    private Boolean is_reversed;

    private LocalDate resevrsed_date;
    private Integer resverse_debitdoc_id;
    private Boolean tds_received;
    private LocalDate tds_received_date;

    private Integer tds_credit_doc_id;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buID;

    @DiffIgnore
    @Column(name = "lcoid", length = 40, updatable = false)
    private Integer lcoid;

    @DiffIgnore
    @Column(name = "invoiceid", nullable = false, length = 40)
    private Integer invoiceId;

    @Column(name = "paytype")
    private String paytype;

    @DiffIgnore
    @Column(name = "type", length = 25)
    private String type;

    @DiffIgnore
    @Column(name = "next_team_hir_mapping")
    private Integer nextTeamHierarchyMappingId;

    @DiffIgnore
    @Column(name = "receipt_number")
    private String reciptNo;

    @DiffIgnore
    @Column(name = "paymentreferenceno",  length = 40)
    private String paymentreferenceno;

//    @Transient
//    private Integer nextStaffId;

    //
    @DiffIgnore
    @ManyToMany(mappedBy = "creditDocumentList")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnoreProperties("creditDocumentList")
    @ToString.Exclude
    private List<DebitDocument> debitDocumentList;

    @DiffIgnore
    @Column(name = "adjustedamount", nullable = false)
    private Double adjustedAmount;

    @DiffIgnore
    @Column(name = "bankid")
    private Long bankManagement;

    @DiffIgnore
    @Column(nullable = false, name = "destinationBank")
    private Long destinationBank;

    private String filename;
    private String uniquename;
    private Double barteramount;
    @DiffIgnore
    @Column(name = "tds_amount")
    private Double tdsAmount;
    @DiffIgnore
    @Column(name = "abbs_amount")
    private Double abbsAmount;
    @DiffIgnore
    @Column(name = "branch")
    private String branchname;
    @DiffIgnore
    @Column(name = "onlinesource")
    private String onlinesource;

    @Column(name = "creditdocumentno", nullable = false, length = 40)
    private String creditdocumentno;
    @DiffIgnore
    @Column(name = "ledger_id")
    private String ledgerId;
    @DiffIgnore
    @Column(name = "batchassigned")
    private Boolean batchAssigned;

    @Transient
    private String mvnoName;

    public String getOnlinesource() {
        return onlinesource;
    }

    public void setOnlinesource(String onlinesource) {
        this.onlinesource = onlinesource;
    }



    @Transient
    double remainingAmount;

    @Transient
    String invoiceNumber;

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


    public Long getBankManagement() {
        return bankManagement;
    }

    public void setBankManagement(Long bankManagement) {
        this.bankManagement = bankManagement;
    }

    public Long getDestinationBank() {
        return destinationBank;
    }

    public void setDestinationBank(Long destinationBank) {
        this.destinationBank = destinationBank;
    }

    public CreditDocument(RecordPaymentPojo pojo) {
        this.bankManagement = getBankManagement();
    }


    public CreditDocument(double amount, double adjustedAmount, String paymode, Integer id, double remainingAmount,String referenceno,String creditdocumentno) {
        this.amount = amount;
        this.adjustedAmount = adjustedAmount;
        this.paymode = paymode;
        this.id = id;
        this.remainingAmount = remainingAmount;
        this.referenceno=referenceno;
        this.creditdocumentno=creditdocumentno;

    }



    public CreditDocument(CreditDocMessage creditDocument, Customers customers) {
        this.id = creditDocument.getId();
        this.customer = customers;
        this.paymode = creditDocument.getPaymode();
        this.paymentdate = LocalDate.parse(creditDocument.getPaymentdate());
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
        this.resverse_debitdoc_id = creditDocument.getResverse_debitdoc_id();
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
        this.reciptNo = creditDocument.getReciptNo();
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
        this.setCreatedById(creditDocument.getLoggedInuserid());
        this.setCreatedByName(creditDocument.getCreatedByName());
//        this.xmldocument=creditDocument.getXmldocument();
        if (creditDocument.getChequedate()!=null) {
            this.chequedate = LocalDate.parse(creditDocument.getChequedate());
        }
    }

}
