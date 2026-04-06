package com.adopt.apigw.modules.customerDocDetails.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.time.LocalDate;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity
@Table(name = "tblcustdocdetails")
@EntityListeners(AuditableListener.class)
public class CustomerDocDetails extends Auditable implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long docId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cust_id")
    private Customers customer;

    private String docType;
    private String docSubType;
    private String mode;
    private String remark;
    private String docStatus;
    private String filename;
    private String uniquename;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    
    @Column(name = "STARTDATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "ENDDATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(name = "next_team_hir_mapping")
    private Integer nextTeamHierarchyMappingId;

    @Column(name = "next_staff")
    private Integer nextStaff;

    @Column(name = "documentnumber")
    private String documentNumber;
    @Transient
    private Integer parentStaffId;
    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return docId;
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
