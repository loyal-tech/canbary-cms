package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Customers;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "TBLTCUSTLEDGERDETAILS")
public class CustomerLedgerDtls {
	
/*
CREATE TABLE TBLTCUSTLEDGERDETAILS
(
	CUSTLEDGERDTLSID SERIAL PRIMARY KEY,
	CREATEDATE	TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	TRANSTYPE VARCHAR(10), 
	TRANSCATEGORY VARCHAR(10),
	AMOUNT NUMERIC(20,4),
	CUSTID BIGINT UNSIGNED,
	CUSTNAME varchar(100),
	PAYMENTMODE varchar(100),
	BANK varchar(100),
	BRANCH varchar(100),
	PAYMENTREFNO bigint unsigned,
	CREDITDOCID bigint unsigned,
	DEBITDOCID bigint unsigned,
	DESCRIPTION VARCHAR(300),
    FOREIGN KEY(CREDITDOCID) REFERENCES TBLTCREDITDOC(CREDITDOCID),
	FOREIGN KEY(DEBITDOCID) REFERENCES tbltdebitdocument(DEBITDOCUMENTID),
  	FOREIGN KEY (CUSTID) REFERENCES TBLCUSTOMERS(CUSTID)
);
 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTLEDGERDTLSID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "TRANSTYPE", nullable = false, length = 40)
    private String transtype;

    @Column(name = "PAYMENTMODE", nullable = false, length = 40)
    private String paymentMode;

    @Column(name = "BANK", length = 40)
    private String bank;

    @Column(name = "BRANCH", length = 40)
    private String branch;

    @Column(name = "PAYMENTREFNO", length = 40)
    private String paymentRefNo;

    @Column(name = "TRANSCATEGORY", nullable = false, length = 40)
    private String transcategory;

    @Column(name = "amount", nullable = false, length = 40)
    private Double amount;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CUSTID")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customers customer;

    @Column(name = "CREDITDOCID", nullable = false, length = 40)
    private Integer creditdocid;

    @Column(name = "DEBITDOCID", nullable = false, length = 40)
    private Integer debitdocid;

    @Column(name = "DESCRIPTION", nullable = false, length = 40)
    private String description;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime CREATE_DATE;

    @Transient
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime END_DATE;

    @Transient
    private Integer refNo;

    @Transient
    private Double balAmount;

    @Column(name = "is_delete", nullable = false, columnDefinition = "boolean default false")
    private Boolean isDelete=false;

    @Column(name = "is_void")
    private Boolean isVoid=false;
    @Transient
    Integer custId;

}
