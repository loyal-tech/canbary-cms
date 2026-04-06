package com.adopt.apigw.modules.PriceGroup.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblpricebook")
@EntityListeners(AuditableListener.class)
public class PriceBook extends Auditable implements IBaseData2<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookid")
    @DiffIgnore
    private Long id;
    private String bookname;
    private LocalDateTime validfrom;
    private LocalDateTime validto;
    private String status;
    private String description;
    private String commission_on;

    @Column(name = "is_all_selected")
    private Boolean isAllPlanSelected=false;

    @Column(name = "is_all_plangroup_selected")
    private Boolean isAllPlanGroupSelected = false;


    @Column(name = "revenueshare")
    private Integer revenueSharePercentage;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "priceBook",orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PriceBookPlanDetail> priceBookPlanDetailList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "priceBook",orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    private List<ServiceCommission> serviceCommissionList = new ArrayList<>();

    @Column(name="is_deleted",columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @Transient
    private Integer noPartnerAssociate;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    
    @Column(name = "agr_percentage", nullable = false, length = 10)
    private String agrPercentage;
    
    @Column(name = "tds_percentage", nullable = false, length = 10)
    private String tdsPercentage;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @OneToMany(mappedBy = "priceBook", targetEntity = PriceBookSlabDetails.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PriceBookSlabDetails> priceBookSlabDetailsList = new ArrayList<>();

    @Column(name = "revenue_type")
    private String revenueType;

    @Column(name = "partner_id")
    private Integer partnerId;

    @Column(name = "plan_group")
    private String planGroup;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
}
