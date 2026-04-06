package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tblcustquotadtls")
@EntityListeners(AuditableListener.class)
public class CustQuotaDetails extends Auditable {
	
    /*
	create table tblcustquotadtls
	(
		quotadtlsid serial primary key,
		custid BIGINT UNSIGNED NOT NULL,
		planid BIGINT UNSIGNED NOT NULL,
		quotatype varchar(50),
		totalquota numeric(20,4),
		usedquota numeric(20,4),
		created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
		lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP,
		foreign key (custid) references tblcustomers(custid),
		foreign key (planid) references TBLMPOSTPAIDPLAN(POSTPAIDPLANID)
	);
	 
*/

    public CustQuotaDetails() {
    }

    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quotadtlsid", nullable = false, length = 40)
    private Integer id;

    @ToString.Exclude
    @OneToOne
    @DiffIgnore
    @JoinColumn(name = "planid")
    private PostpaidPlan postpaidPlan;

    @Column(name = "quotatype")
    private String quotaType;

    @Column(name = "totalquota")
    private Double totalQuota = 0.0;

    @Column(name = "usedquota")
    private Double usedQuota = 0.0;

    @Column(name = "quotaunit")
    private String quotaUnit;

    @Column(name = "timetotalquota")
    private Double timeTotalQuota = 0.0;

    @Column(name = "timequotaused")
    private Double timeQuotaUsed = 0.0;

    @Column(name = "timequotaunit")
    private String timeQuotaUnit;
    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
    @Column(name = "totalquotakb")
    private Double totalQuotaKB = 0.0;

    @Column(name = "usedquotakb")
    private Double usedQuotaKB = 0.0;

    @Column(name = "timeusedquotasec")
    private Double timeUsedQuotaSec = 0.0;

    @Column(name = "timetotalquotasec")
    private Double timeTotalQuotaSec = 0.0;

    @JsonBackReference
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.MERGE)
    @JoinColumn(name = "custpackageid")
//    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CustPlanMappping custPlanMappping;

    @DiffIgnore
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "custid")
    private Customers customer;

    @DiffIgnore
    private Double didtotalquota;
    @DiffIgnore
    private Double didusedquota;
    @DiffIgnore
    private Double intercomtotalquota;
    @DiffIgnore
    private Double intercomusedquota;
    @DiffIgnore
    private String didQuotaUnit;
    @DiffIgnore
    private String intercomQuotaUnit;

    @DiffIgnore
    @Column(name = "currentsessionusagetime")
    private Double currentSessionUsageTime = 0.0;

    @DiffIgnore
    @Column(name = "currentsessionusagevolume")
    private Double currentSessionUsageVolume = 0.0;

    @DiffIgnore
    @Column(name = "last_quota_reset", nullable = false, updatable = true)
    private LocalDateTime lastQuotaReset;
    @DiffIgnore
    @Column(name = "parnet_quota_type")
    private String parentQuotaType;

    @Column(name = "is_chunk_available")
    private Boolean isChunkAvailable;

    @DiffIgnore
    @Column(name = "reserved_quota_in_per")
    private Double reservedQuotaInPer;


    @DiffIgnore
    @Column(name = "total_reserved_quota")
    private Double totalReservedQuota;
    @Column(name = "upstreamprofileuid")
    private String upstreamprofileuid;
    @Column(name = "downstreamprofileuid")
    private String downstreamprofileuid;
    @Column(name = "usage_quota_type")
    private String usageQuotaType;

    @Column(name = "skip_quota_update")
    private Boolean skipQuotaUpdate;


    public CustQuotaDetails(CustQuotaDetails custQuotaDetails) {
        this.id = custQuotaDetails.getId();
        this.postpaidPlan = custQuotaDetails.getPostpaidPlan();
        this.quotaType = custQuotaDetails.getQuotaType();
        this.totalQuota = custQuotaDetails.getTotalQuota();
        this.usedQuota = custQuotaDetails.getUsedQuota();
        super.setCreatedByName(custQuotaDetails.getCreatedByName());
        super.setCreatedate(custQuotaDetails.getCreatedate());
        super.setCreatedById(custQuotaDetails.getCreatedById());
        super.setLastModifiedById(custQuotaDetails.getLastModifiedById());
        super.setLastModifiedByName(custQuotaDetails.getLastModifiedByName());
        super.setUpdatedate(custQuotaDetails.getUpdatedate());
        this.quotaUnit = custQuotaDetails.getQuotaUnit();
        this.timeTotalQuota = custQuotaDetails.getTimeTotalQuota();
        this.timeQuotaUsed = custQuotaDetails.getTimeQuotaUsed();
        this.timeQuotaUnit = custQuotaDetails.getTimeQuotaUnit();
        this.totalQuotaKB = custQuotaDetails.getTotalQuotaKB();
        this.usedQuotaKB = custQuotaDetails.getUsedQuotaKB();
        this.timeUsedQuotaSec = custQuotaDetails.getTimeUsedQuotaSec();
        this.timeTotalQuotaSec = custQuotaDetails.getTimeTotalQuotaSec();
        this.custPlanMappping = custQuotaDetails.getCustPlanMappping();
        this.parentQuotaType = custQuotaDetails.getParentQuotaType();
        this.isChunkAvailable = custQuotaDetails.getIsChunkAvailable();
        this.reservedQuotaInPer = custQuotaDetails.getReservedQuotaInPer();
        this.totalReservedQuota = custQuotaDetails.getTotalReservedQuota();
        this.usageQuotaType = custQuotaDetails.getUsageQuotaType();
        this.customer = custQuotaDetails.getCustomer();
    }
}
