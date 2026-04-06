package com.adopt.apigw.modules.CreditTransactionMapping;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tblttransactioncreditmapping")
@Data
public class CreditTransactionMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "creditdocumentid")
    private Integer creditdocumentid;

    @Column(name = "debitdocumentid")
    private Integer debitdocumentid;

    @Column(name = "transactionno")
    private String transactionno;

    @Column(name = "adjustedamount")
    private Double adjustedamount;

    @Column(name = "custid")
    private Integer custid;



}
