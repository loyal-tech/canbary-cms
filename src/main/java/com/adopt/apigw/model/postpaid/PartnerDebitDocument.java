package com.adopt.apigw.model.postpaid;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data


@ToString
@Table(name = "tblpartnerdebitdocument")
public class PartnerDebitDocument {
	
	
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
);	 

CREATE TABLE tblpartnercreditdoc (
  creditdocumentid serial primary key,
  creditdocumentnumber varchar(200),
  partnerid bigint unsigned DEFAULT NULL,
  billdate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  createdate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  startdate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  enddate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  duedate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  latepaymentdate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  subtotal decimal(20,4) DEFAULT '0.0000',
  tax decimal(20,4) DEFAULT '0.0000',
  discount decimal(20,4) DEFAULT '0.0000',
  totalamount decimal(20,4) DEFAULT '0.0000',
  previousbalance decimal(20,4) DEFAULT '0.0000',
  latepaymentfee decimal(20,4) DEFAULT '0.0000',
  currentpayment decimal(20,4) DEFAULT '0.0000',
  currentdebit decimal(20,4) DEFAULT '0.0000',
  currentcredit decimal(20,4) DEFAULT '0.0000',
  totaldue decimal(20,4) DEFAULT '0.0000',
  totalamountinwords varchar(200) DEFAULT NULL,
  totaldueinwords varchar(200) DEFAULT NULL,
  partnerbillrunid bigint unsigned DEFAULT NULL,
  billrunstatus varchar(200) DEFAULT NULL,
  xmldocument longtext ,
  email varchar(200) DEFAULT NULL,
  phone varchar(200) DEFAULT NULL,
  FOREIGN KEY(PARTNERID) REFERENCES TBLPARTNERS(PARTNERID),
  FOREIGN KEY(PARTNERBILLRUNID) REFERENCES tblmpartnerbillrun(PARTNERBILLRUNID)
);


*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "creditdocumentid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "creditdocumentnumber", nullable = false, length = 40)
    private String docnumber;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "partnerid")
    private Partner partner;

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

    @Column(name = "partnerbillrunid", nullable = false, length = 40)
    private Integer billrunid;

    @Column(name = "billrunstatus", nullable = false, length = 40)
    private String billrunstatus;

    @Column(name = "xmldocument", nullable = false)
    private String document;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    @Column(name = "adjustedamount", nullable = false)
    private Double adjustedamount;

    @Column(name = "remark", nullable = false)
    private String remark;
}
