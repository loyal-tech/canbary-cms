package com.adopt.apigw.modules.Teams.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "tblteams")
@EntityListeners(AuditableListener.class)
public class Teams extends Auditable implements IBaseData<Long> {

    @Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(name = "team_name")
    private String name;

    @Column(name = "team_status")
    private String status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tblteamusermapping", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns = @JoinColumn(name = "staffid"))
    @ToString.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<StaffUser> staffUser = new HashSet<>();

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "partnerid")
    private Partner partner;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @ManyToOne
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "parentteamid")
    private Teams parentTeams;

    @Transient
    private String cafStatus;

    @Column(name = "lcoid")
    private Integer lcoId;

    @Column(name = "teamtype")
    private String teamType;


    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    public Teams getParentTeams() {
        if (parentTeams == null) {
            return null;
        } else {
            return parentTeams;
        }
    }

    @Override
    public String toString() {
        return "Teams [id=" + id + "]";
    }
}
