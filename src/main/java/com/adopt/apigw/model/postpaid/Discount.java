package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "TBLMDISCOUNT")
@EntityListeners(AuditableListener.class)
public class Discount extends Auditable {
	
	
	/*
	 CREATE TABLE TBLMDISCOUNT
  (
    DISCOUNTID  serial,
    NAME        VARCHAR(255) NOT NULL,
    DESCRIPTION VARCHAR(255),
    STATUS      CHAR(1) DEFAULT 'Y' NOT NULL,
    CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    MVNOID bigint UNSIGNED,
    PRIMARY KEY (DISCOUNTID),
    FOREIGN KEY (MVNOID) REFERENCES TBLMMVNO (MVNOID)
  );
	 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISCOUNTID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false, length = 40)
    private String desc;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @JsonManagedReference
    @OneToMany(mappedBy = "discount", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<DiscountMapping> discMappingList = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "discount", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<DiscountPlanMapping> planMappingList = new ArrayList<>();

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name="mvnoName")
    private String mvnoName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMvnoId() {
        return mvnoId;
    }

    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }

    public List<DiscountMapping> getDiscMappingList() {
        return discMappingList;
    }

    public void setDiscMappingList(List<DiscountMapping> discMappingList) {
        this.discMappingList = discMappingList;
    }

    public List<DiscountPlanMapping> getPlanMappingList() {
        return planMappingList;
    }

    public void setPlanMappingList(List<DiscountPlanMapping> planMappingList) {
        this.planMappingList = planMappingList;
    }


}
