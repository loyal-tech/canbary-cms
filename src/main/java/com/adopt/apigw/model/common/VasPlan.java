package com.adopt.apigw.model.common;


import com.adopt.apigw.model.postpaid.PostpaidPlanCharge;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "tblmvasplan")
public class VasPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "vas_name")
    private String name;

    @Column(name = "pausedayslimit")
    private Integer pauseDaysLimit;

    @Column(name = "pausetimelimit")
    private Integer pauseTimeLimit;

    @Column(name = "tatid")
    private Integer tatId;

    @Column(name = "inventory_replace_afteryears")
    private Integer inventoryReplaceAfterYears;

    @Column(name = "inventory_paid_months")
    private Integer inventoryPaidMonths;

    @Column(name = "inventory_count")
    private Integer inventoryCount;

    @Column(name = "shiftlocation_years")
    private Integer shiftLocationYears;

    @Column(name = "shiftlocation_months")
    private Integer shiftLocationMonths;

    @Column(name = "shiftlocation_count")
    private Integer shiftLocationCount;

    @Column(name = "paymenttype")
    private String paymentType;

    @Column(name = "vasamount")
    private Integer vasAmount;

    @Column(name = "mvnoid")
    private Integer mvnoId;

    @Column(name = "isdelete")
    private Boolean isdelete = false;

    @Column(name = "validity", length = 4)
    private Integer validity;

    @Column(name = "unitsofvalidity", nullable = false, length = 40, columnDefinition = "varchar(100) default 'Days'")
    private String unitsOfValidity;

    @OneToMany(mappedBy = "vasPlan", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    private List<VasPlanCharge> chargeList = new ArrayList<>();

    @Column(name = "isdefault",nullable = false)
    private Boolean isdefault;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mvnoid", referencedColumnName = "MVNOID", insertable = false, updatable = false)
    @JsonIgnore
    private Mvno mvno;

    @Transient
    public String getMvnoName() {
        return mvno != null ? mvno.getName() : null;
    }

}
