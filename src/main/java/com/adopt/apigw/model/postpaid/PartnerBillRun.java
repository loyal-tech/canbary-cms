package com.adopt.apigw.model.postpaid;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data


@ToString
@Table(name = "tblmpartnerbillrun")
public class PartnerBillRun {
	
	
	/*
create table TBLMBILLRUN
(
	billrunid serial,
	billruncreatedate TIMESTAMP  DEFAULT CURRENT_TIMESTAMP, 
	billrundate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	billruncount NUMERIC(10), 
	amount NUMERIC(20,4),
	status varchar(15),
	billruncompletedate TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (billrunid )
);


CREATE TABLE tblmpartnerbillrun (
  partnerbillrunid SERIAL primary key,
  billruncreatedate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  billrundate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  billruncount decimal(10,0) DEFAULT NULL,
  amount decimal(20,4) DEFAULT NULL,
  status varchar(15) DEFAULT NULL,
  billruncompletedate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  SUCCESSCOUNT decimal(20,0) DEFAULT NULL,
  failcount decimal(20,0) DEFAULT NULL
);

	 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partnerbillrunid", nullable = false, length = 40)
    private Integer id;

    @CreationTimestamp
    @Column(name = "billruncreatedate", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @Column(name = "billrundate", nullable = false, updatable = false)
    private LocalDateTime rundate;

    @Column(name = "billruncount", nullable = false, length = 40)
    private Integer billruncount;

    @Column(name = "amount", nullable = false, length = 40)
    private Double amount;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @Column(name = "billruncompletedate", nullable = false, updatable = false)
    private LocalDateTime billrunfinishdate;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;
}
