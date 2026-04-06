package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.utils.CommonConstants;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustQuotaDtlsPojo extends Auditable {

    public CustQuotaDtlsPojo() {
    }

    public CustQuotaDtlsPojo(CustPlanMapppingPojo planMapppingPojo, Integer planId, String quotaType, Double totalQuota, Double usedQuota, String quotaUnit, Double timeTotalQuota, Double timeQuotaUsed, String timeQuotaUnit, Double totalQuotaKB, Double usedQuotaKB, Double timeUsedQuotaSec, Double timeTotalQuotaSec, CustomersPojo customers,String planName, String plangroup) {
        this.planId = planId;
        this.quotaType = quotaType;
        this.totalQuota = totalQuota;
        this.usedQuota = usedQuota;
        this.quotaUnit = quotaUnit;
        this.timeTotalQuota = timeTotalQuota;
        this.timeQuotaUsed = timeQuotaUsed;
        this.timeQuotaUnit = timeQuotaUnit;
        this.custPlanMappping = planMapppingPojo;
        this.totalQuotaKB = totalQuotaKB;
        this.usedQuotaKB = usedQuotaKB;
        this.timeUsedQuotaSec = timeUsedQuotaSec;
        this.timeTotalQuotaSec = timeTotalQuotaSec;
        this.customer = customers;
        this.planName = planName;
        this.planGroup = plangroup;
        this.skipQuotaUpdate=planMapppingPojo.getSkipQuotaUpdate();
        if(customers.getParentQuotaType() != null)
            this.custQuotaType = customers.getParentQuotaType();
        else
            this.custQuotaType = CommonConstants.CUST_QUOTA_TYPE.INDIVIDUAL;
    }

    public CustQuotaDtlsPojo(CustPlanMapppingPojo planMapppingPojo, Integer planId, String quotaType, Double didtotalquota, Double didusedquota, Double intercomtotalquota, Double intercomusedquota, CustomersPojo customers, String didQuotaUnit, String intercomQuotaUnit) {
        this.planId = planId;
        this.didtotalquota = didtotalquota;
        this.quotaType = quotaType;
        this.didusedquota = didusedquota;
        this.intercomtotalquota = intercomtotalquota;
        this.intercomusedquota = intercomusedquota;
        this.custPlanMappping = planMapppingPojo;
        this.customer = customers;
        this.didQuotaUnit = didQuotaUnit;
        this.intercomQuotaUnit = intercomQuotaUnit;
        if(customers.getParentQuotaType() != null)
            this.custQuotaType = customers.getParentQuotaType();
        else
            this.custQuotaType = CommonConstants.CUST_QUOTA_TYPE.INDIVIDUAL;
    }

    private String planGroup;

    private Integer id;

    private Integer planId;

    private String quotaType;

    private String custQuotaType;

    private Double totalQuota;

    private Double usedQuota;

    private String quotaUnit;

    private Double timeTotalQuota;

    private Double timeQuotaUsed;

    private String timeQuotaUnit;

    private Boolean isDelete = false;

    private Double totalQuotaKB;

    private Double usedQuotaKB;

    private Double timeUsedQuotaSec;

    private Double timeTotalQuotaSec;

    private Double didtotalquota;

    private Double didusedquota;

    private Double intercomtotalquota;

    private Double intercomusedquota;

    private String didQuotaUnit;

    private String intercomQuotaUnit;
    
    private String planName;

    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CustPlanMapppingPojo custPlanMappping;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CustomersPojo customer;

    private Integer cprId;

    private Double currentSessionUsageTime;

    private Double currentSessionUsageVolume;

    private LocalDateTime lastQuotaReset;

    private String parentQuotaType;

    private boolean isChunkAvailable;

    private Double reservedQuotaInPer;

    private Double totalReservedQuota;
    private String upstreamprofileuid;

    private String downstreamprofileuid;
    private String usageQuotaType;
    public Boolean skipQuotaUpdate;
}
