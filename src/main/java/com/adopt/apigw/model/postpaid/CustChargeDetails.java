package com.adopt.apigw.model.postpaid;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
@Table(name = "tblcustchargedtls")
@EntityListeners(AuditableListener.class)
public class CustChargeDetails extends Auditable {

/*
create table tblcustchargedtls
(
   cstchargeid serial primary key,
   custid BIGINT UNSIGNED NOT NULL,
   planid BIGINT UNSIGNED NOT NULL,
   chargeid BIGINT UNSIGNED NOT NULL,
   chargetype varchar(2),
   amount numeric(20,4),
   CREATEDATE   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
   CREATEDBYSTAFFID NUMERIC(20),
   LASTMODIFIEDBYSTAFFID  NUMERIC(20),
   LASTMODIFIEDDATE	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   foreign key (planid) references TBLMPOSTPAIDPLAN(POSTPAIDPLANID),
   foreign key (custid) references tblcustomers(custid),
   foreign key (chargeid) references TBLCHARGES(CHARGEID)
);

*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cstchargeid", nullable = false, length = 40)
    private Integer id;


    public CustChargeDetails() {
    }

    @DiffIgnore
    @Column(name = "validity", length = 4)
    private Double validity;
    @DiffIgnore
    @Column(name = "planid", length = 40)
    private Integer planid;
    @DiffIgnore
    @Column(name = "chargeid", length = 40)
    private Integer chargeid;

    @Column(name = "chargetype", nullable = false, length = 40)
    private String chargetype;
    @Column(name = "price", nullable = false, length = 40)
    private Double price;
    @DiffIgnore
    @Column(name = "actual_price", nullable = false, length = 40)
    private Double actualprice;

    @Column(name = "remarks")
    private String remarks;
    @DiffIgnore
    @Column(name = "charge_date")
    private LocalDateTime charge_date;
    @DiffIgnore
    @Column(name = "startdate")
    private LocalDateTime startdate;
    @DiffIgnore
    @Column(name = "enddate")
    private LocalDateTime enddate;
    @DiffIgnore
    @Column(name = "taxamount")
    private Double taxamount;
    @DiffIgnore
    @Column(name = "is_reversed")
    private Boolean is_reversed;

    @Column(name = " rev_date")
    private LocalDateTime rev_date;
    @DiffIgnore
    @Column(name = " rev_amt")
    private Double rev_amt;
    @DiffIgnore
    @Column(name = " rev_remarks")
    private String rev_remarks;
    @DiffIgnore
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "custid")
    private Customers customer;

    private Boolean isUsed;
    @DiffIgnore
    @Column(name = "purchase_entity_id")
    private Long purchaseEntityId;
    @DiffIgnore
    @Column(name = "debitdocid")
    private Long debitdocid;
    @DiffIgnore
    private Long ippooldtlsid;
    @DiffIgnore
    @Column(name = "billable_cust_id")
    private Integer billableCustomerId;

    @Transient
    @DiffIgnore
    List<PostpaidPlanCharge> listOfChargeByPlan;

    @Transient
    private String charge_name;

    public CustChargeDetails(CustChargeDetails custChargeDetails) {
        this.id = custChargeDetails.getChargeid();
        this.remarks = custChargeDetails.getRemarks();
        this.is_reversed = custChargeDetails.getIs_reversed();
        this.rev_amt = custChargeDetails.getRev_amt();
        this.rev_date = custChargeDetails.getRev_date();
        this.rev_remarks = custChargeDetails.getRev_remarks();
    }
    
    @Column(name = "type", nullable = false, length = 40)
    private String type;
    @DiffIgnore
    @Column(name = "planvalidity", length = 4)
    private Integer planValidity;
    @DiffIgnore
    @Column(name = "unitsofvalidity", length = 40)
    private String unitsOfValidity;
    @DiffIgnore
    @Column(name = "taxid",length = 40)
    private Integer taxId;
    @DiffIgnore
    @Column(name = "custpackageid", nullable = false, length = 40)
    private Integer custPlanMapppingId;
    @DiffIgnore
    @Column(name = "lastbilldate", length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastBillDate;
    @DiffIgnore
    @Column(name = "nextbilldate", length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextBillDate;
    @DiffIgnore
    @Column(name = "billingcycle")
    private Integer billingCycle;
    
    @Column(columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;
    @DiffIgnore
    @Column(name = "dbr", length = 40)
    private Double dbr = 0.0;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "is_invoice_to_org")
    private Boolean isInvoiceToOrg;

    @Column(name = "bill_to")
    private String billTo;

    @Column(name = "new_amount")
    private Double newAmount;

    @Column(name = "static_ip_address")
    private String staticIPAdrress ;

    @Column(name="connection_no")
    private String connection_no;

    @Transient
    @DiffIgnore
    private String nextBillDateStr;

    @Transient
    @DiffIgnore
    private String lastBillDateStr;

    @Column(name = "installment_frequency", length = 20)
    private String installmentFrequency; // MONTHLY, QUARTERLY, ANNUALLY

    @Column(name = "installment_no")
    private Integer installmentNo;

    @Column(name = "total_installments")
    private Integer totalInstallments;

    @Column(name = "amount_per_installment", precision = 20, scale = 4)
    private BigDecimal amountPerInstallment;

    @Column(name = "installment_start_date")
    private LocalDate installmentStartDate;

    @Column(name = "next_installment_date")
    private LocalDate nextInstallmentDate;

    @Column(name = "last_installment_date")
    private LocalDate lastInstallmentDate;

    @Column(name = "installment_enabled")
    private Boolean installmentEnabled;

    @Transient
    @DiffIgnore
    private Double finalPrice;

    @Transient
    @DiffIgnore
    private Double discountPrice;

    @Transient
    @DiffIgnore
    private Double discountValue;

}
