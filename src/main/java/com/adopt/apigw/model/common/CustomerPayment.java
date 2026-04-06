package com.adopt.apigw.model.common;

import com.adopt.apigw.pojo.api.CustomerPaymentDto;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tbltpayment")
public class CustomerPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long id;

    @Column(name = "orderid", nullable = false, length = 40)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    @Column (name="custid", nullable =false)
    private Integer custId;

    @Column(name = "payment", nullable = false, length = 40)
    private Double payment;

    @Column(name = "status")
    private String status;

    @Column(name = "pgtransactionid")
    private String pgTransactionId;

    @Column(name = "linkid")
    private String linkId;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "plan_id")
    private Integer planId;

    @Column(name = "is_from_captive")
    private Boolean isFromCaptive = false;

    @Column(name="merchant_name")
    private String merchantName;

    @Column(name="transaction_date")
    private LocalDateTime transactionDate;

    @Column(name="customer_user_name")
    private String customerUsername;

    @Column(name="mvnoid")
    private Integer mvnoid;

   @Column(name="buid")
   private Integer buid;

   @Column(name="creditdocid")
   private Integer creditDocumentId;

    @Column(name="paymentlink")
    private String paymentLink;

    @Column(name="checksum")
    private String checksum;

    @Column (name="partnerid")
    private Integer partnerId;

    @Column(name="partner_payment_id")
    private Integer partnerPaymentId;

    @Column(name = "account_number")
    private String accountNumber;

    public CustomerPayment() {
    }

    public CustomerPayment(CustomerPaymentDto paymentDto) {
        this.custId = paymentDto.getCustId();
        this.payment = paymentDto.getPayment();
        this.status = paymentDto.getStatus();
        if(paymentDto.getCustomerUsername()!=null){
            this.customerUsername= paymentDto.getCustomerUsername();
        }if(paymentDto.getMerchantName()!=null){
            this.merchantName=paymentDto.getMerchantName();
        }
        if(paymentDto.getAccountNumber()!=null){
            this.accountNumber=paymentDto.getAccountNumber();
        }

        if(paymentDto.getIsFromCaptive() != null){
            this.isFromCaptive = paymentDto.getIsFromCaptive();
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
