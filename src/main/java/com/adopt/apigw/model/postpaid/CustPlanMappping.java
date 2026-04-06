package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.ChangePlanDTOs.CustPlanMappingRevenue;
import com.adopt.apigw.modules.linkacceptance.model.LinkAcceptanceDTO;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;
import com.adopt.apigw.pojo.api.PostpaidPlanPojo;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "TBLCUSTPACKAGEREL")
@EntityListeners(AuditableListener.class)
public class CustPlanMappping extends Auditable {
	

	/*
CREATE TABLE TBLCUSTPACKAGEREL
(
	custpackageid SERIAL PRIMARY KEY,
	custid BIGINT UNSIGNED NOT NULL,
	planid BIGINT UNSIGNED NOT NULL,
	startdate timestamp not null,
	enddate timestamp not null,
	expirydate timestamp not null,
	status char(1),
	FOREIGN KEY(custid) REFERENCES tblcustomers(custid),
	FOREIGN KEY(planid) REFERENCES TBLMPOSTPAIDPLAN(postpaidplanid)
);
 
	 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custpackageid", nullable = false, length = 40)
    private Integer id;

//    @DiffIgnore
    @Column(name = "planid", length = 40)
    private Integer planId;

    @Column(name = "vasid", length = 40)
    private Integer vasId;

//    @DiffIgnore
    @Transient
    private PostpaidPlanPojo postpaidPlanPojo;

    @Column(nullable = false, length = 40)
    private String service;

    @Column(name = "startdate", nullable = false, length = 40)
    private LocalDateTime startDate;

    @Column(name = "enddate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;

    @DiffIgnore
    @Column(name = "expirydate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime expiryDate;

    @Column(name = "status", nullable = false, length = 150)
    private String status;

    @DiffIgnore
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "custid")
    @LazyCollection(LazyCollectionOption.FALSE)
    private Customers customer;

    @Transient
//    @DiffIgnore
    private PostpaidPlan postpaidPlan;

    @DiffIgnore
    @OneToOne
    @JoinColumn(name = "qospolicyid")
    private QOSPolicy qospolicy;

    @DiffIgnore
    private String uploadqos;

    @DiffIgnore
    private String downloadqos;

    @DiffIgnore
    private String uploadts;

    @DiffIgnore
    private String downloadts;

    @DiffIgnore
    private Double offerPrice;
    @DiffIgnore
    private Double taxAmount;
    @DiffIgnore
    private Double walletBalUsed = 0.0;
    @DiffIgnore
    private String purchaseType;
    @DiffIgnore
    private Long onlinePurchaseId;
    @DiffIgnore
    private String purchaseFrom;
    @DiffIgnore
    @Column(name = "billable_cust_id", nullable = true)
    private Integer billableCustomerId;



    @DiffIgnore
    @Column(columnDefinition = "Boolean default false",name = "isinvoicestop")
    private Boolean isinvoicestop = false;
    @DiffIgnore
    @Column(columnDefinition = "Boolean default false",name = "istrialplan")
    private Boolean istrialplan = false;

    @DiffIgnore
    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "custPlanMappping", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("id desc")
    private List<CustQuotaDetails> quotaList = new ArrayList<>();

//    @DiffIgnore
//    @JsonManagedReference
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "custPlanMappping", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
//    @OrderBy("id desc")
//    private List<CustQuotaDetails> quotaList = new ArrayList<>();

    @DiffIgnore
    private Long debitdocid;

    @Column(name = "is_delete",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
    
    @Transient
    @DiffIgnore
    private Double validity;

    @Column(name = "discount")
    private Double discount;

    @DiffIgnore
    @Column(name = "plan_validity_days")
    private Integer planValidityDays;

    @OneToOne
    @DiffIgnore
    @JoinColumn(name = "plangroupid")
    private PlanGroup planGroup;

    @DiffIgnore
    @Column(name = "is_invoice_to_org")
    private Boolean isInvoiceToOrg;

    @DiffIgnore
    @Column(name = "bill_to")
    private String billTo;

    @Column(name = "new_amount")
    private Double newAmount;

    @DiffIgnore
    @Column(name = "renewal_id")
    private Integer renewalId;

    @DiffIgnore
    @Column(name = "cust_ref_id")
    private Integer custRefId;

    @DiffIgnore
    @Column(name = "next_staff")
    private Integer nextStaff;

    @DiffIgnore
    @Column(name = "cust_ref_name")
    private String custRefName;

    @DiffIgnore
    @Column(name = "dbr")
    private Double dbr;

    @Column(name = "cust_plan_status")
    private String custPlanStatus;

    @DiffIgnore
    @Column(name = "next_team_hir_mapping")
    private Integer nextTeamHierarchyMappingId;

    @DiffIgnore
    @Column(name = "is_invoice_created")
    private Boolean isInvoiceCreated;

    @Column(name = "old_discount")
    private Double oldDiscount;

    @DiffIgnore
    @Column(name = "grace_days")
    private Integer graceDays = 0;

    @DiffIgnore
    @Column(name = "stop_service_date", length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate stopServiceDate;

    @DiffIgnore
    @Column(name = "custservicemappingid", nullable = false)
    private Integer custServiceMappingId;

    @Column(name = "remarks")
    private String remarks;

    @DiffIgnore
    @Column(name = "ezybill_service_id")
    private String ezyBillServiceId;
    @DiffIgnore
    @Column(name = "ezbill_package_id")
    private String ezBillPackageId;
    @DiffIgnore
    @Column(name = "cas_id")
    private String casId;

    @DiffIgnore
    @Column(name = "grace_date_time")
    private LocalDateTime graceDateTime;

    @DiffIgnore
    @Column(name = "invoice_type")
    private String invoiceType;
    @DiffIgnore
    private Long traildebitdocid;
    @DiffIgnore
    @Column(name = "promise_to_pay_remarks")
    private String promise_to_pay_remarks;
    @DiffIgnore
    @Column(name = "promisetopay_renew_count")
    private Long promisetopay_renew_count;

    @DiffIgnore
    @Column(name = "is_trial_validity", length = 4)
    private Double isTrialValidityDays;

    @DiffIgnore
    @Column(name = "trial_plan_validity_count")
    private Integer trialPlanValidityCount =0;

    @DiffIgnore
    @Column(name = "start_servicedate", nullable = false, length = 40)
    private LocalDateTime startServiceDate;

    @DiffIgnore
    @Column(name = "service_stop_date", nullable = false, length = 40)
    private LocalDateTime serviceHoldDate;


    @Transient
    @DiffIgnore
    private LinkAcceptanceDTO linkAcceptanceDTO;

    @DiffIgnore
    @Column(name = "promise_to_pay_startdate", length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime promise_to_pay_startdate;;

    @DiffIgnore
    @Column(name = "promise_to_pay_enddate", length = 40)
    private LocalDateTime promise_to_pay_enddate;

    @DiffIgnore
    @Column(name = "total_hold_days")
    private Integer totalHoldDays;

    @DiffIgnore
    @Column(name = "s_discount_type")
    private String discountType="One-time";

    @DiffIgnore
    @Column(name = "discount_expiry_date")
    private LocalDate discountExpiryDate;

    @DiffIgnore
    @Column(name = "downtime_expiry_date")
    private LocalDate downTimeExpiryDate;

    @DiffIgnore
    @Column(name = "downtime_start_date")
    private LocalDate downTimeStartDate;

    @DiffIgnore
    @Column(name = "cprid_promise")
    private Integer cprIdForPromiseToPay;

    @DiffIgnore
    @Column(name = "is_hold")
    private Boolean isHold;

    @DiffIgnore
    @Column(name = "is_void")
    private Boolean isVoid;

    @Column(name = "extend_validity_remarks")
    private String extendValidityremarks;

    @DiffIgnore
    @Column(name = "service_hold_by")
    private String serviceHoldBy;

    @DiffIgnore
    @Column(name = "service_start_by")
    private String serviceStartBy;

    @DiffIgnore
    @Column(name = "service_hold_remarks")
    private String serviceHoldRemarks;

    @DiffIgnore
    @Column(name = "service_start_remarks")
    private String serviceStartRemarks;

    @DiffIgnore
    @Column(name = "is_contains_cust_invoice")
    private Boolean isContainsCustomerInvoice;

    @DiffIgnore
    @Column(name = "cust_cpr")
    private Integer customerCpr;

    public CustPlanMappping() {
    }

    public CustPlanMappping(CustPlanMappping custPlanMappping) {
        this.expiryDate = custPlanMappping.getExpiryDate();
    }

    private Long creditdocid;
    @DiffIgnore
    @Column(name="service_id")
    private Integer serviceId;

    @Column(name = "serial_number")
    private String serialNumber;
    @Transient
    private  boolean isServiceThroughLead;
    @Transient
    private  boolean trailPlanFromToday;
    @Transient
    private  boolean trailPlanFromTrailDay;

    @Column(name = "voucherid")
    private Long voucherId;

    @Column(name = "is_planexpiry_notified",nullable = false)
    private Boolean isPlanExpiryNotified;

    @Transient
    public Boolean skipQuotaUpdate;

    @Column(name = "renewal_for_booster")
    private Boolean renewalForBooster;

    public CustPlanMappping(Integer id, String serialNumber) {
        this.id = id;
        this.serialNumber = serialNumber;
    }

    @Override
    public String toString() {
        return "CustPlanMappping{}";
    }

    public CustPlanMappping(Integer id,Integer planId)
    {
        this.id=id;
        this.planId=planId;
    }

    public CustPlanMappping(Integer id, LocalDateTime endDate, LocalDateTime expiryDate, Long debitdocid)
    {
        this.id=id;
        this.endDate=endDate;
        this.expiryDate=expiryDate;
        this.debitdocid=debitdocid;
    }

    public CustPlanMappping(Integer id, LocalDateTime startDate, LocalDateTime serviceHoldDate,Integer customerCpr,Integer custServiceMappingId,Integer createdById)
    {
        this.id=id;
        this.startDate=startDate;
        this.serviceHoldDate=serviceHoldDate;
        this.createdById=createdById;
        this.custServiceMappingId=custServiceMappingId;
        this.customerCpr=customerCpr;
    }
}
