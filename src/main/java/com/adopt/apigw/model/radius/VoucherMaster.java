package com.adopt.apigw.model.radius;

import lombok.Data;
import lombok.ToString;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.adopt.apigw.model.postpaid.PostpaidPlan;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tblvouchermaster")
public class VoucherMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vcid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "vcname", nullable = false, length = 40)
    private String vcName;

    public Integer getPlid() {
        return plid;
    }

    public void setPlid(Integer plid) {
        this.plid = plid;
    }

    @Column(name = "vcqty", nullable = false, length = 40)
    private Integer vcQty;

    @Column(name = "link_type",nullable = false )
    @Enumerated(EnumType.STRING)
    private VoucherLinkType linkType;
    @Column(name = "voucher_amount")
    private Double voucherAmount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plid", nullable = true, insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private PostpaidPlan plan;

    @Column(name = "plid", nullable = false, length = 40)
    private Integer plid;

    @Column(name = "numericval", nullable = true, length = 40)
    private String numeric;
    @Column(name = "uppercase", nullable = true, length = 40)
    private String uppercase;
    @Column(name = "lowercase", nullable = true, length = 40)
    private String lowercase;
    @Column(name = "voucherlength", nullable = false, length = 40)
    private Integer voucherlength;
    @Column(name = "vouchervalidity", nullable = false, length = 40)
    private Integer vouchervalidity;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

    public VoucherMaster(Integer id, String vcName, Integer vcQty, PostpaidPlan plan, String numeric, String uppercase, String lowercase, Integer voucherlength, Integer vouchervalidity) {
        this.id = id;
        this.vcName = vcName;
        this.vcQty = vcQty;
        this.plan = plan;
        this.numeric = numeric;
        this.uppercase = uppercase;
        this.lowercase = lowercase;
        this.voucherlength = voucherlength;
        this.vouchervalidity = vouchervalidity;
    }

    public VoucherMaster() {
    }
}
