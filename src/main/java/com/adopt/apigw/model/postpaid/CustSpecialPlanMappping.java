package com.adopt.apigw.model.postpaid;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.lead.LeadMaster;
import com.adopt.apigw.pojo.api.CustSpecialPlanMapppingPojo;
import com.adopt.apigw.spring.security.AuditableListener;

import lombok.Getter;
import lombok.Setter;
import org.javers.core.metamodel.annotation.DiffIgnore;

@Entity
@Setter
@Getter
@Table(name = "TBLMCUSTSPECIALPLANREL")
@EntityListeners(AuditableListener.class)
public class CustSpecialPlanMappping extends Auditable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custspecialplanid", nullable = false, length = 40)
    private Integer id;
	
    @OneToOne
    @JoinColumn(name = "specialplanid")
    private PostpaidPlan specialPlan;
	
    @OneToOne
    @JoinColumn(name = "normalplanid")
    private PostpaidPlan normalPlan;

    @OneToOne
    @JoinColumn(name = "specialplangroupid")
    private PlanGroup specialPlanGroup;

    @OneToOne
    @JoinColumn(name = "normalplangroupid")
    private PlanGroup normalPlanGroup;

    @ManyToOne
    @JoinColumn(name = "custid")
    private Customers customer;

	@Column(name = "service",nullable = false, length = 40)
    private String service;
	
    @ManyToOne(fetch = FetchType.EAGER)
    @DiffIgnore
    @JoinColumn(name = "CUSTSPPLANID", referencedColumnName = "CUSTSPPLANID", nullable = false)
    private CustSpecialPlanRelMappping custSpecialPlanRelMappping;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @ManyToOne
    @JoinColumn(name = "leadcustid")
    private LeadMaster leadMaster;

    @Column(name="mvnoName")
    private String mvnoName;


    public CustSpecialPlanMappping(CustSpecialPlanMappping custSpecialPlanMappping){
        this.id = custSpecialPlanMappping.id;
        this.specialPlan = custSpecialPlanMappping.specialPlan;
        this.normalPlan = custSpecialPlanMappping.normalPlan;
        this.specialPlanGroup = custSpecialPlanMappping.specialPlanGroup;
        this.normalPlanGroup = custSpecialPlanMappping.normalPlanGroup;
        this.customer = custSpecialPlanMappping.customer;
        this.service = custSpecialPlanMappping.service;
        this.custSpecialPlanRelMappping = custSpecialPlanMappping.custSpecialPlanRelMappping;
        this.mvnoId = custSpecialPlanMappping.mvnoId;
        this.buId = custSpecialPlanMappping.buId;
        this.leadMaster = custSpecialPlanMappping.leadMaster;
    }
    public CustSpecialPlanMappping(CustSpecialPlanMapppingPojo custSpecialPlanMappping){
        this.id = custSpecialPlanMappping.getId();
        this.specialPlan = custSpecialPlanMappping.getSpecialPlan();
        this.normalPlan = custSpecialPlanMappping.getNormalPlan();
        this.specialPlanGroup = custSpecialPlanMappping.getSpecialPlanGroup();
        this.normalPlanGroup = custSpecialPlanMappping.getNormalPlanGroup();
        this.service = custSpecialPlanMappping.getService();
        this.mvnoId = custSpecialPlanMappping.getMvnoId();
        this.buId = custSpecialPlanMappping.getBuId();
    }

    public CustSpecialPlanMappping() {

    }
}
