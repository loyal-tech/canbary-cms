package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "tbltshiftlocation")
public class ShiftLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "service_area_id")
    private Long updateAddressServiceAreaId;

    @Column(name = "partner_id", nullable = false)
    private Integer shiftPartnerId;

    @Column(name = "transferable_commission")
    private  Double transferableCommission;

    @Column(name = "charge_id")
    private  Integer chargeId;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "discount")
    private Double discount;
    @Column(name = "billable_customer_id")
    private Integer billableCustomerId;

    @Column(name = "payment_owner")
    private String paymentOwner;

    @Column(name = "payment_owner_id")
    private Integer paymentOwnerId;

    @Column(name = "requested_by_id")
    private Integer requestedById;

    @Column(name = "requested_date")
    private LocalDateTime requestedDate;

    @Column(name = "requested_by_name")
    private String requestedByName;

    @Column(name = "transferable_balance")
    private Double transferableBalance;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    private String latitude;

    private String longitude;
}
