package com.adopt.apigw.modules.TimeBasePolicy.domain;

import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblmtimebasepolicy")
@EntityListeners(AuditableListener.class)
public class TimeBasePolicy extends Auditable implements IBaseData2<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id", nullable = false, length = 40)
    private Long id;

    @Column(name = "policy_name", nullable = false, length = 40)
    private String name;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name="mvnoName")
    private String mvnoName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "timeBasePolicy",orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TimeBasePolicyDetails> timeBasePolicyDetailsList = new ArrayList<>();

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
        return isDeleted;
    }
}
