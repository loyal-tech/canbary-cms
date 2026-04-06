package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "TBLDISCOUNTAUDIT")
public class discountAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "custpackgeid", nullable = false, length = 40)
    private Integer custpackgeid;
    @Column(name = "staffid", nullable = false, length = 40)
    private Integer staffid;
    @Column(name = "olddiscount", length = 40)
    private Double oldDiscount;
    @Column(name = "newdiscount", length = 40)
    private Double newDiscount;
    @Column(name = "updateddate", nullable = false)
    private LocalDateTime updateddate;
    @Column(name = "staffname", nullable = false)
    private String staffname;
    @Column(name = "remarks")
    private String remarks;

    @Column(name = "olddiscounttype", length = 40)
    private String oldDiscountType;
    @Column(name = "newdiscounttype", length = 40)
    private String newDiscountType;

    @Column(name = "olddiscountexpirydate", length = 40)
    private LocalDate oldDiscountExpiryDate;
    @Column(name = "newdiscountexpirydate", length = 40)
    private LocalDate newDiscountExpiryDate;
}
