package com.adopt.apigw.model.postpaid;

import javax.persistence.*;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "tbltcustchargehistory")
@EntityListeners(AuditableListener.class)
public class CustomerChargeHistory extends Auditable<CustomerChargeHistory> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id", nullable = false, length = 40)
    private Integer id;

    @Column(name = "cust_id", nullable = false, length = 40)
    private Integer customerId;
    @Column(name = "plan_id", nullable = false, length = 40)
    private Integer planId;
    @Column(name = "charge_id", nullable = false, length = 40)
    private Integer chargeId;
    @Column(name = "tax_id", nullable = false, length = 40)
    private Integer taxId;
    @Column(name = "charge_amount", nullable = false, length = 40)
    private Double chargeAmount;
    @Column(name = "tax_amount", nullable = false, length = 40)
    private Double taxAmount;
    @Column(name = "discount", nullable = false, length = 40)
    private Double discount = 0.0;
    @Column(name = "cust_plan_mapping_id", nullable = false, length = 40)
    private Integer custPlanMapppingId;
    @Column(name = "plan_group_id", length = 40)
    private Integer planGroupId;
    @Column(name = "plan_name", length = 40)
    private String planName;

    @Column(name = "charge_name", length = 40)
    private String chargeName;

    @Column(name = "charge_desc", length = 40)
    private String charge_desc;

    @Column(name = "charge_type", length = 40)
    private String chargeType;

    @Column(name = "billing_cycle", length = 40)
    private Integer billingCycle;

    @Column(name = "saccode", length = 40)
    private String saccode;

    @Column(name = "next_charge_billdate", length = 40,nullable = true)
    private LocalDate nextBillDate;

    @Column(name = "last_charge_billdate", length = 40,nullable = true)
    private LocalDate lastBillDate;

    @Column(name = "charge_bill_day", length = 20,nullable = true)
    private Integer customerBillDay;

    @Column(name = "is_first_charge_apply", length = 40,nullable = true)
    private Boolean isFirstChargeApply;

    @Column(name = "is_royalty_apply", length = 40,nullable = true)
    private Boolean isRoyaltyApply=false;

    @Transient
    private String nextBillDateString;

    @Transient
    private String lastBillDateString;

}
