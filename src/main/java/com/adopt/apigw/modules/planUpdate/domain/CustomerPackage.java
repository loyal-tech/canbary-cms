package com.adopt.apigw.modules.planUpdate.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.PlanGroup;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.model.radius.Plan;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "tblcustpackagerel")
public class CustomerPackage extends Auditable<Integer> implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custpackageid")
    private Long custPackageId;
    @ManyToOne
    @JoinColumn(name = "custid")
    private Customers customers;
    @ManyToOne(optional = true)
    @JoinColumn(name = "planid", nullable = true)
    private PostpaidPlan plan;
    @Column(name = "startdate")
    private LocalDate startDate;
    @Column(name = "enddate")
    private LocalDate endDate;
    @Column(name = "expirydate")
    private LocalDate expiryDate;
    private String status;
    @OneToOne
    @JoinColumn(name = "qospolicyid")
    private QOSPolicy qospolicy;
    private String uploadqos;
    private String downloadqos;
    private String uploadts;
    private String downloadts;
    
    @Column(name = "is_delete")
    private Boolean isDelete;
    
    @Column(name = "discount")
    private Integer discount;




    @Column(columnDefinition = "Boolean default false",name = "isinvoicestop")
    private Boolean isinvoicestop = false;

    @Column(columnDefinition = "Boolean default false",name = "istrialplan")
    private Boolean istrialplan = false;

    @Column(name = "dbr")
    private Double dbr=0.0;

    @Column(name = "is_invoice_to_org")
    private boolean isInvoiceToOrg;
    
    @Column(name = "bill_to")
    private String billTo;

    @Column(name = "next_approver")
    private Integer nextApprover;

    @Column(name = "debitdocid")
    private Integer debitdocid;

    @Column(name = "next_staff")
    private Integer nextStaff;

//    @Column(name = "staff_approver_status")
//    private String staffapproverstatus;

    private String service;

    private Integer traildebitdocid;

    @Column(name = "cust_plan_status")
    private String custPlanStatus;


    @Column(name = "is_trial_validity", length = 4)
    private Double isTrialValidityDays;

    @Column(name = "trial_plan_validity_count")
    private Integer trialPlanValidityCount = 0;
    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return custPackageId;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDelete;
    }
}
