package com.adopt.apigw.model.postpaid;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@ToString
@Table(name = "tblcustchargeinstallments")
public class CustChargeInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "installment_id")
    private Long id;

    @Column(name = "cust_id", nullable = false, length = 40)
    private Integer customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_charge_details_id", nullable = false)
    private CustChargeDetails custChargeDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_charge_history_id", nullable = false)
    private CustomerChargeHistory custChargeHistory;

    @Column(name = "installment_frequency", length = 20)
    private String installmentFrequency; // MONTHLY, QUARTERLY, ANNUALLY

    @Column(name = "installment_no")
    private Integer installmentNo;

    @Column(name = "total_installments")
    private Integer totalInstallments;

    @Column(name = "amount_per_installment", precision = 20, scale = 4)
    private BigDecimal amountPerInstallment;

    @Column(name = "installment_start_date")
    private LocalDate installmentStartDate;

    @Column(name = "next_installment_date")
    private LocalDate nextInstallmentDate;

    @Column(name = "last_installment_date")
    private LocalDate lastInstallmentDate;

    @Column(name = "installment_enabled")
    private Boolean installmentEnabled = false;

}

