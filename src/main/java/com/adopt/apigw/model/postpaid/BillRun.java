package com.adopt.apigw.model.postpaid;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "TBLMBILLRUN")
public class BillRun {
	
	
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
	 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billrunid", nullable = false, length = 40)
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
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "type", nullable = false, length = 40)
    private String type;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    
    @Column(name = "lcoid")
    private Integer lcoId;
}
