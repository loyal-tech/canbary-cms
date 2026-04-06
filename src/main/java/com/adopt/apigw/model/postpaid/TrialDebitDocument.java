package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.PurchaseOrder.Domain.PurchaseOrder;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "TBLTTRIALDEBITDOCUMENT")
public class TrialDebitDocument {
	
	
	/*
create table TBLTDEBITDOCUMENT
(
	debitdocumentid serial,
	debitdocumentnumber varchar(200),
	subscriberid BIGINT UNSIGNED,
	billdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP, 
	createdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	startdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	enddate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	duedate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	latepaymentdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	subtotal NUMERIC(20,4) default 0,
	tax NUMERIC(20,4) default 0,
	discount NUMERIC(20,4) default 0,
	totalamount NUMERIC(20,4) default 0,
	previousbalance NUMERIC(20,4) default 0,
	latepaymentfee NUMERIC(20,4) default 0,
	currentpayment NUMERIC(20,4) default 0,
	currentdebit NUMERIC(20,4) default 0,
	currentcredit NUMERIC(20,4) default 0,
	totaldue NUMERIC(20,4) default 0,
	totalamountinwords varchar(200),
	totaldueinwords varchar(200),
	billrunid BIGINT UNSIGNED,
	billrunstatus varchar(200),
	xmldocument LONGTEXT,
	PRIMARY KEY (debitdocumentid ),
	FOREIGN KEY (subscriberid) REFERENCES tblcustomers (custid),
	FOREIGN KEY (billrunid) REFERENCES TBLMBILLRUN (billrunid)
);	 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trialdebitdocumentid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "trialdebitdocumentnumber", nullable = false, length = 40)
    private String docnumber;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscriberid")
    private Customers customer;

    @Column(name = "billdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime billdate;

    @Column(name = "createdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdate;

    @Column(name = "startdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime startdate;

    @Column(name = "enddate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime endate;

    @Column(name = "duedate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime duedate;

    @Column(name = "latepaymentdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime latepaymentdate;

    @Column(name = "subtotal", nullable = false, length = 40)
    private double subtotal;

    @Column(name = "tax", nullable = false, length = 40)
    private double tax;

    @Column(name = "discount", nullable = false, length = 40)
    private double discount;

    @Column(name = "totalamount", nullable = false, length = 40)
    private double totalamount;

    @Column(name = "previousbalance", nullable = false, length = 40)
    private double previousbalance;

    @Column(name = "latepaymentfee", nullable = false, length = 40)
    private double latepaymentfee;

    @Column(name = "currentpayment", nullable = false, length = 40)
    private double currentpayment;

    @Column(name = "currentdebit", nullable = false, length = 40)
    private double currentdebit;

    @Column(name = "currentcredit", nullable = false, length = 40)
    private double currentcredit;

    @Column(name = "totaldue", nullable = false, length = 40)
    private double totaldue;

    @Column(name = "totalamountinwords", nullable = false, length = 40)
    private String amountinwords;

    @Column(name = "totaldueinwords", nullable = false, length = 40)
    private String dueinwords;

    @Column(name = "trialbillrunid", nullable = false, length = 40)
    private Integer billrunid;

    @Column(name = "billrunstatus", nullable = false, length = 40)
    private String billrunstatus;

    @Column(name = "xmldocument", nullable = false)
    private String document;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    private String lastModifiedByName;

    @JoinColumn(name = "custpackrelid")
    private Integer custpackrelid;

    @OneToOne
    @JoinColumn(name = "purchaseorder_id")
    private PurchaseOrder purchaseorder;

    @Column(name = "billable_to_name")
    private String billableToName;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, targetEntity = TrialDebitDocumentDetail.class)
    @JoinColumn(name = "trialdebitdocumentid")
    private List<TrialDebitDocumentDetail> trialDebitDocumentDetails;

    @Column(name = "adjustedamount")
    private Double adjustedAmount;

}
