package com.adopt.apigw.model.radius;

import lombok.Data;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@ToString
@Table(name = "tblvoucherbatch")
public class VoucherBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vbid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "vouchercode", nullable = false, length = 40)
    private String voucherCode;

    @DiffIgnore
    @Column(name = "vcid", nullable = false, length = 40)
    private Integer vcId;

    @DiffIgnore
    @Column(name = "planid", nullable = false, length = 40)
    private Integer planId;

    @Column(name = "validity", nullable = false, updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate validity;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

    @DiffIgnore
    @Column(name = "buid")
    private Long buId;



    public VoucherBatch() {
    }

    public VoucherBatch(String voucherCode, Integer vcId, Integer planId, LocalDate validity) {
        this.voucherCode = voucherCode;
        this.vcId = vcId;
        this.planId = planId;
        this.validity = validity;
    }

    public void setValidity(LocalDate validity) {
        this.validity = validity;
    }

    public VoucherBatch(Integer id, String voucherCode, Integer vcId, Integer planId, LocalDate validity) {
        this.id = id;
        this.voucherCode = voucherCode;
        this.vcId = vcId;
        this.planId = planId;
        this.validity = validity;
    }

}
