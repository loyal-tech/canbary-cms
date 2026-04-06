package com.adopt.apigw.model.postpaid;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

@Data
@Entity
@Table(name = "tblmplangroupmapping")
public class PlanGroupMapping extends Auditable{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plangroupmappingid")
    private Integer planGroupMappingId;
	
	@ManyToOne
    @JoinColumn(name = "POSTPAIDPLANID", referencedColumnName = "POSTPAIDPLANID")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PostpaidPlan plan;
	
	@Column(nullable = false, length = 40)
    private String service;

	@JsonBackReference
	@ManyToOne
    @JoinColumn(name = "plangroupid", referencedColumnName = "plangroupid")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PlanGroup planGroup;
	
	@Column(name = "is_deleted")
    private Boolean isDelete = false;;

    @DiffIgnore
	@Column(name= "MVNOID")
	private Integer mvnoId;
	
	@Transient
    private Double validity;

    @Column(name= "newofferprice")
    private Double newofferprice;

    @Transient
    @DiffIgnore
    private Long planId;

    @Transient
    @DiffIgnore
    private Integer planGroupId;


    @Override
    public String toString() {
        return "PlanGroupMapping{" +
                "planGroupMappingId=" + planGroupMappingId +
                ", service='" + service + '\'' +
                ", isDelete=" + isDelete +
                ", mvnoId=" + mvnoId +
                ", validity=" + validity +
                ", newofferprice=" + newofferprice +
                '}';
    }

    public PlanGroupMapping() {
    }

    public PlanGroupMapping(PlanGroupMapping planGroupMapping){
        this.planGroupMappingId = planGroupMapping.getPlanGroupMappingId();
        this.service = planGroupMapping.getService();
        this.isDelete = planGroupMapping.getIsDelete();
        this.mvnoId = planGroupMapping.getMvnoId();
        this.validity = planGroupMapping.getValidity();
        this.newofferprice = planGroupMapping.getNewofferprice();
        if(planGroupMapping.getPlanId()!=null) {
            this.planId = planGroupMapping.getPlanId();
            this.planGroupId = planGroupMapping.getPlanGroupId();
        }else {
            this.planId = planGroupMapping.getPlan().getId().longValue();
            this.planGroupId = planGroupMapping.getPlanGroup().getPlanGroupId();
        }
    }

    public Long getPlanId() {
        if(plan!=null){
            this.planId = Long.valueOf(this.plan.getId());
        }
        return planId;
    }
}
