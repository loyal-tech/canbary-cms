package com.adopt.apigw.pojo.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.adopt.apigw.constants.Constants;
import org.springframework.format.annotation.DateTimeFormat;

import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Transient;

@Data
public class CustChargeDetailsPojo extends Auditable {

    private Integer id;

    public CustChargeDetailsPojo() {
    }

    private Integer planid;

    @Transient
    private ChargePojo chargePojo;

    private Integer chargeid;

    private String chargeName;

    private String chargetype;

    private Double validity = 0.0;

    private Double price = 0.0;

    private Double actualprice = 0.0;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private CustomersPojo customer;

    private String remarks;

    private Date charge_date;

    private String chargeDateString;

    private Date startdate;

    private String startdateString;

    private Date enddate;

    private Date expiry;

    private String enddateString;

    private Double taxamount;

    private Boolean is_reversed = false;

    private LocalDateTime rev_date;

    private String revdateString;

    private Double rev_amt;

    private String rev_remarks;

    private Boolean isUsed;

    private Long purchaseEntityId;

    private Long ippooldtlsid;

    private Long debitdocid;

    private String createDateString;
    
    private String updateDateString;
    
    private String type;
    
    private Integer planValidity;
    
    private String unitsOfValidity;
    
    private Integer taxId;
    
    private Integer custPlanMapppingId;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastBillDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextBillDate;
        
    private Integer billingCycle;
    
    private Boolean isDeleted =false;

    private Double dbr;

    private Integer custServiceMappingId;

    private Double discount;

    private Boolean isInvoiceToOrg=false;

    private String billTo="CUSTOMER";

    private Double newAmount=0d;

    private String staticIPAdrress ;

    private String connection_no;

    private Boolean isRenew;

    private Integer taxInPer;

    private String installmentFrequency;

    private Integer installmentNo;

    private Integer totalInstallments;

    private BigDecimal amountPerInstallment;

    private LocalDate installmentStartDate;

}
