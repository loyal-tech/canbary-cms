package com.adopt.apigw.model.postpaid;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "TBLMCUSTLEDGER")
@EntityListeners(AuditableListener.class)
public class CustomerLedger extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTLEDGERID", nullable = false, length = 40)
    private Integer id;


    @Column(name = "TOTALDUE", nullable = false, length = 40)
    private Double totaldue = 0.0;

    @Column(name = "TOTALPAID", nullable = false, length = 40)
    private Double totalpaid = 0.0;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CUSTID")
    @ToString.Exclude
    private Customers customer;

	@Override
	public String toString() {
		return "CustomerLedger []";
	}
    
  
}
