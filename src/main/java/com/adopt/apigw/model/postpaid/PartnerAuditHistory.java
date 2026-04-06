package com.adopt.apigw.model.postpaid;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "tblpartneraudithistory")
public class PartnerAuditHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partnerauditid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "partnerid", nullable = false, length = 40)
    private Integer partnerId;

    @Column(name = "partnername", nullable = false, length = 40)
    private String partnerName;

    @Column(name= "new_customer_count")
    private Long newCustomerCount;

    @Column(name= "renew_customer_count")
    private Long renewCustomerCount;

    @Column(name= "total_customer_count")
    private Long totalCustomerCount;

    @Column(name = "createdate", nullable = false, length = 40, updatable = false)
    private LocalDateTime createdate;

    @Column(name = "lastauditdate", nullable = false, length = 40, updatable = false)
    private LocalDateTime lastAuditdate;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "is_active", columnDefinition = "Boolean default true", nullable = false)
    private Boolean isActive = true;
}
