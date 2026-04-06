package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.BranchService.model.BranchServiceMappingEntity;
import com.adopt.apigw.modules.DunningRuleBranchMapping.domain.DunningRuleBranchMapping;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "tbldunningrules")
@EntityListeners(AuditableListener.class)
public class DunningRule extends Auditable{
	
	
	/*
	 create table tbldunningrules
(
	druleid serial primary key,
	name varchar(100),
	fromemail varchar(200),
	bccemail varchar(200),
	comm_email varchar(2),
	comm_sms varchar(2),
	internal_pay_email varchar(200),
	esc_staff_email varchar(200),
	coll_agency_email varchar(200),
	creditclass varchar(50),
	rulestatus varchar(1),
    created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP	
);
	 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "druleid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "name", nullable = false, length = 40)
    private String name;


    @Column(name = "ccemail", length = 40)
    private String ccemail;

    @Column(name = "mobile", length = 40)
    private String mobile;


    @Column(name = "creditclass", nullable = false, length = 40)
    private String creditclass;

    @Column(name = "rulestatus", nullable = false, length = 40)
    private String status;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "drule", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    @ToString.Exclude
    private List<DunningRuleAction> actionList = new ArrayList<DunningRuleAction>();

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "cust_type", nullable = false, length = 40)
    private String customerType;

    @Column(name = "dunning_type" , length = 100)
    private String dunningType;

    @Column(name = "dunning_sub_type" , length = 100)
    private String dunningSubType;

    @Column(name = "dunning_sub_sector",length = 40)
    private String dunningSubSector;

    @Column(name = "dunning_sector",length = 40)
    private String dunningSector;

    @Column(name = "lcoid")
    private Integer lcoId;

    @Column(name = "customer_pay_type" ,  length = 100)
    private String customerPayType;

    @Column(name = "dunning_for" , length = 100)
    private String dunningFor;

    @Transient
    private String mvnoName;

    @Column(name = "is_generate_payment_link", columnDefinition = "Boolean default false", nullable = false)
    private  Boolean isGeneratepaymentLink=false;

    public DunningRule() {
    }

    public DunningRule(Integer id) {
        this.id = id;
    }

    public DunningRule(String name, LocalDateTime createdate, LocalDateTime updatedate, String status) {
        super();
        this.name = name;
        this.status = status;
    }
}
