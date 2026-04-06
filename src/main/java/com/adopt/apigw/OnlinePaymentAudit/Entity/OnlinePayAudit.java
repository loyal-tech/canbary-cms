//package com.adopt.apigw.OnlinePaymentAudit.Entity;
//
//import com.adopt.apigw.model.common.Auditable;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "tblmonlinepayaudit")
//public class OnlinePayAudit extends Auditable {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name="reference_number",nullable = false)
//    private String referenceNumber;
//
//    @Column(name="transaction_amount",nullable = false)
//    private String transactionAmount;
//
//    @Column(name="merchant_name")
//    private String merchantName;
//
//    @Column(name = "payment_mode")
//    private String paymentMode;
//
//    @Column(name="payment_status", nullable = false)
//    private String paymentStatus;
//
//    @Column(name="transaction_date", nullable = false)
//    private LocalDateTime transactionDate;
//
//    @Column(name="customer_id",nullable = false)
//    private Integer customerId;
//
//    @Column(name="customer_user_name", nullable = false)
//    private String customerUserName;
//
//    @Column(name="plan_id",nullable = false)
//    private Integer planId;
//
//    @Column(name="plan_price",nullable = false)
//    private Long planPrice;
//
//    @Column(name="mvnoid",nullable = false)
//    private Integer mvnoid;
//
//    @Column(name="buid")
//    private Integer buid;
//
//
//
//
//
//
//
//
//}
