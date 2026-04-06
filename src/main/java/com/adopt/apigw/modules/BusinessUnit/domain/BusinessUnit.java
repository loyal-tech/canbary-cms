package com.adopt.apigw.modules.BusinessUnit.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.postpaid.PlanCasMapping;
import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.modules.InvestmentCode.Domain.InvestmentCode;
import com.adopt.apigw.modules.InvestmentCodeBUmapping.IcNameBuMapping;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblmbusinessunit")
@EntityListeners(AuditableListener.class)
public class BusinessUnit extends Auditable implements IBaseData<Long>{
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "businessunitid")
    private Long id;

    private String buname;

    private String bucode;

    private String status;

    @Column(name = "plan_binding_type",length = 50)
    private String planBindingType;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @ManyToMany(fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblmicnamebumapping",joinColumns = {@JoinColumn(name = "businessunitid")}, inverseJoinColumns = {@JoinColumn(name = "investmentcode_id")} )
    private List<InvestmentCode> investmentCodeid=new ArrayList<>();

    /*@CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    @DiffIgnore
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "LASTMODIFIEDDATE")
    @DiffIgnore
    private LocalDateTime updatedate;

    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
    @DiffIgnore
    private Integer createdById;

    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
    @DiffIgnore
    private Integer lastModifiedById;
    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    //@DiffIgnore
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    //@DiffIgnore
    private String lastModifiedByName;*/

    //    @JsonManagedReference
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessUnit")
//    private List<PlanService> planServiceList = new ArrayList<>();
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }
}
