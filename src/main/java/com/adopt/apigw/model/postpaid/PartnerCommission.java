package com.adopt.apigw.model.postpaid;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.adopt.apigw.model.common.Customers;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Table(name = "TBLPARTNERCOMMREL")
public class PartnerCommission {
	
	
	/*
create table TBLPARTNERCOMMREL
(
	PARNTERCOMMRELID SERIAL PRIMARY KEY,
	CUSTOMERID BIGINT UNSIGNED,
	PARTNERID BIGINT UNSIGNED,
	COMM_TYPE VARCHAR(100),	
	COMM_REL_VALUE NUMERIC(2),
	COMM_VALUE NUMERIC(20,4),
	CREATEDATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	BILLDATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PROCESS_STATUS VARCHAR(50),
	FOREIGN KEY (PARTNERID) REFERENCES TBLPARTNERS(PARTNERID),
	FOREIGN KEY (CUSTOMERID) REFERENCES TBLCUSTOMERS(CUSTID)
);
	 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARNTERCOMMRELID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "CUSTOMERID", nullable = false, length = 40)
    private Integer customerid;

    @Column(name = "PARTNERID", nullable = false, length = 40)
    private Integer partnerid;

    @Column(name = "COMM_TYPE", nullable = false, length = 40)
    private String commtype;

    @Column(name = "COMM_REL_VALUE", nullable = false, length = 40)
    private Double commrelval;

    @Column(name = "COMM_VALUE", nullable = false, length = 40)
    private Double commval;

    @CreationTimestamp
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @Column(name = "BILLDATE", nullable = false, length = 40)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime billdate;

    @Column(name = "PROCESS_STATUS", nullable = false, length = 40)
    private String status;

}
