package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import org.javers.core.metamodel.annotation.DiffIgnore;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "TBLCHARGES")
@EntityListeners(AuditableListener.class)
@NoArgsConstructor
public class Charge extends Auditable {
	
	
	/*
create table TBLCHARGES
(
	CHARGEID serial,
	CHARGENAME varchar(255),
	DESCRIPTION VARCHAR(255),
	CHARGETYPE VARCHAR(32),
	PRICE NUMERIC(16,4),
	TAXID BIGINT UNSIGNED NOT NULL, 
	DISCOUNTID BIGINT UNSIGNED NOT NULL, 
	CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (TAXID) REFERENCES TBLMTAX(TAXID),
    FOREIGN KEY (DISCOUNTID) REFERENCES TBLMDISCOUNT(DISCOUNTID)
);
	 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHARGEID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "CHARGENAME", nullable = false, length = 40)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false, length = 40)
    private String desc;

    @Column(name = "CHARGETYPE", nullable = false, length = 40)
    private String chargetype;

    @Column(name = "PRICE", nullable = false, length = 40)
    private double price;

    @Column(name = "actual_price", length = 40)
    private double actualprice;

    @JoinColumn(name = "TAXID")
    @OneToOne(cascade = CascadeType.ALL)
    private Tax tax;

    @DiffIgnore
    @Column(name = "DISCOUNTID", nullable = false, length = 40)
    private Integer discountid;

    @Column(name = "dbr", nullable = false, length = 40)
    private double dbr;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    private String chargecategory;
    private String saccode;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tblmservicechargemapping", joinColumns = {@JoinColumn(name = "chargeid")}, inverseJoinColumns = {@JoinColumn(name = "servicesid")})
    private List<Services> serviceList = new ArrayList<>();

    //private Double taxamount;
    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "service", nullable = false, length = 40)
    private String service;

    @Column(name = "status",nullable = false,length = 40)
    private String status;

    @DiffIgnore
    @Column(name = "LEDGER_ID",length = 40)
    private String ledgerId;

    @Column(name = "royalty_payable")
    private Boolean royalty_payable=false;


    @Column(name = "taxamount")
    private Double taxamount;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "pushable_ledger_id")
    private String pushableLedgerId;

    @Column(name = "isinventorycharge")
    private Boolean isinventorycharge;

    @DiffIgnore
    @Column(name = "productid")
    private Long productId;

    @Column(name = "inventorychargetype")
    private String inventoryChargeType;

    @Column(name ="mvnoName")
    private String mvnoName;

    @Column(name ="currency")
    private String currency;

    public Charge(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Charge(Charge charge) {
        this.id = charge.getId();
        this.name = charge.getName();
        this.desc = charge.getDesc();
        this.chargetype = charge.getChargetype();
        this.price = charge.getPrice();
        this.actualprice = charge.getActualprice();
        this.tax = new Tax(charge.getTax().getId());
        this.discountid = charge.getDiscountid();
        this.dbr = charge.getDbr();
        this.isDelete = charge.getIsDelete();
        this.chargecategory = charge.getChargecategory();
        this.saccode = charge.getSaccode();
//        this.serviceList = serviceList;
        this.mvnoId = charge.getMvnoId();
        this.buId = charge.getBuId();
        this.service = charge.getService();
        this.status = charge.getStatus();
        this.ledgerId = charge.getLedgerId();
        this.royalty_payable = charge.getRoyalty_payable();
        this.taxamount = charge.getTaxamount();
        this.businessType = charge.getBusinessType();
        this.pushableLedgerId = charge.getPushableLedgerId();
    }
}
