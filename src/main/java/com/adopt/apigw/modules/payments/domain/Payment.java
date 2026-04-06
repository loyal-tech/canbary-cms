package com.adopt.apigw.modules.payments.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.adopt.apigw.modules.payments.model.PaymentMode;
import com.adopt.apigw.modules.payments.model.PaymentStatus;

import lombok.Data;

@Entity
@Table(name = "tbl_payu_payments")
@Data
public class Payment {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String email;
    @Column
    private String name;
    @Column
    private String phone;
    @Column
    private String productInfo;
    @Column
    private Double amount;
    @Column
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Column
    @Temporal(TemporalType.DATE)
    private Date paymentDate;
    @Column
    private String txnId;
    @Column
    private String mihpayId;
    @Column
    @Enumerated(EnumType.STRING)
    private PaymentMode mode;
    private String command;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

}
