package com.adopt.apigw.modules.mvnoDocDetails.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

import javax.persistence.*;

import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity
@Table(name = "tblmvnodocdetails")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class MvnoDocDetails extends Auditable implements IBaseData<Long> {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long docId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MVNOID")
    private Mvno mvno;

    @Column(name = "doc_type")
    private String docType;
    @Column(name = "doc_sub_type")
    private String docSubType;
    @Column(name = "mode")
    private String mode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "doc_status")
    private String docStatus;
    @Column(name = "filename")
    private String filename;
    @Column(name = "uniquename")
    private String uniquename;
    @Column(columnDefinition = "Boolean default false", name = "is_delete", nullable = false)
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
