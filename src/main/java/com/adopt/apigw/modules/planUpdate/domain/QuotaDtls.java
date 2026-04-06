package com.adopt.apigw.modules.planUpdate.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblcustquotadtls")
@EntityListeners(AuditableListener.class)
public class QuotaDtls extends Auditable implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quotadtlsid")
    private Long quotaDtlsId;
    @ManyToOne
    @JoinColumn(name = "custid")
    private Customers customers;
    @OneToOne
    @JoinColumn(name = "planid")
    private PostpaidPlan postpaidPlan;
    @Column(name = "quotatype")
    private String quotaType;
    @Column(name = "totalquota")
    private Double totalQuota;
    @Column(name = "usedquota")
    private Double usedQuota;
    @Column(name = "quotaunit")
    private String quotaUnit;
    @Column(name = "timetotalquota")
    private String timeTotalQuota;
    @Column(name = "timequotaused")
    private String timeQuotaUsed;
    @Column(name = "timequotaunit")
    private String timeQuotaUnit;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return quotaDtlsId;
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
